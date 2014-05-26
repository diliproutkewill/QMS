<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSSellChargesDetails.jsp
Product Name	: QMS
Module Name		: Charge Basis Master
Task		    : Adding/View/Modify/Delete chargebasis
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete" chargebasis information
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
	private static final String FILE_NAME="QMSSellChargesDetails.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);
  
	String operation		=	request.getParameter("Operation");
	String terminalId		=	loginbean.getTerminalId();
	String chargeId			=	"";
	String rateBreak		=	"";
	String rateType			=	"";
	String overAllMargin	=	"";
	String marginType		=	"";
	String marginBasis		=	"";
	String base				=	"";
	String min				=	"";
	String max				=	"";
	String flat				=	"";
	String baseMargin		=	"";
	String minMargin		=	"";
	String maxMargin		=	"";
	String flatMargin		=	"";
	String[] chargeSlab		=	new String[14];
	String[] marginValue	=	new String[14];
	String[] chargeRate		=	new String[14];
	String[] chargeFlatRate	=	new String[14];
	String   readOnly		=	"";
	String   disabled		=	"";
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	String      nextNavigation		=	null;
	String      dummyBuyChargeFlag	=	"";
	BuychargesHDRDOB buychargesHDRDOB	=	null;
	BuychargesDtlDOB buychargesDtlDOB	=	null;
	ArrayList		 dtlList			=	null;
	String		dataTerminalId		= (String)request.getAttribute("terminalId");
	//Logger.info(FILE_NAME,"--------------->");
	try{
		if(dataTerminalId==null || dataTerminalId.equals(""))
        { dataTerminalId=loginbean.getTerminalId();}
		if(operation.equals("View") || operation.equals("Delete"))
		{
			readOnly		=	"readOnly";
			disabled		=	"disabled";
		}
		buychargesHDRDOB	=	(BuychargesHDRDOB)request.getAttribute("SellChargesHDRDtls");
		if(buychargesHDRDOB==null)
		{
			throw new Exception("No data found");
		}else
		{
			rateBreak	=	buychargesHDRDOB.getRateBreak();
			rateType	=	buychargesHDRDOB.getRateType();
			chargeId		=	buychargesHDRDOB.getChargeId();
			overAllMargin	=	buychargesHDRDOB.getOverallMargin();
			marginType		=	buychargesHDRDOB.getMarginType();
			marginBasis			=	buychargesHDRDOB.getMarginBasis();
			dtlList				=	buychargesHDRDOB.getBuyChargeDtlList();
			dummyBuyChargeFlag	=   buychargesHDRDOB.getDummyBuychargesFlag();
			if(dtlList!=null && dtlList.size()>0)
			{
				int dtlListSize=dtlList.size();
				int cnt = 0;
				for(int i=0;i<dtlListSize;i++)
				{
					buychargesDtlDOB	=	(BuychargesDtlDOB)dtlList.get(i);
					if(buychargesDtlDOB.getChargeSlab().equals("BASE"))
						{	base		=	new Double(buychargesDtlDOB.getChargeRate()).toString();
							baseMargin	=	buychargesDtlDOB.getMarginValue();}	
					else if(buychargesDtlDOB.getChargeSlab().equals("MIN"))
						{	min			=	new Double(buychargesDtlDOB.getChargeRate()).toString();
							minMargin	=	buychargesDtlDOB.getMarginValue();}
					else if(buychargesDtlDOB.getChargeSlab().equals("MAX"))
						{	max			=	new Double(buychargesDtlDOB.getChargeRate()).toString();
							maxMargin	=	buychargesDtlDOB.getMarginValue();}
					else if(buychargesDtlDOB.getChargeSlab().equals("Flat") || "Percent".equals(buychargesDtlDOB.getChargeSlab()) || "Absolute".equals(buychargesDtlDOB.getChargeSlab()))
						{	flat		=	new Double(buychargesDtlDOB.getChargeRate()).toString();
							flatMargin	=	buychargesDtlDOB.getMarginValue();}
					else
						{
							chargeSlab[cnt]		=	buychargesDtlDOB.getChargeSlab();
							marginValue[cnt]		=	buychargesDtlDOB.getMarginValue()+"";
							//if("F".equals(buychargesDtlDOB.getChargeRate_indicator()))
							if("S".equals(buychargesDtlDOB.getChargeRate_indicator()))
								{	chargeFlatRate[cnt++]	=	buychargesDtlDOB.getChargeRate()+"";}
							else
								{	chargeRate[cnt++]	=	buychargesDtlDOB.getChargeRate()+"";}
						}
					
				}
			}
		}
%>
<html>
<head>
<title>SellCharges <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="html/eSupply.js"></script>
<script >
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
						//ADDED BY SUBRAHMANYAM FOR THE ISSUE 143489
					
					if(margintype.value == 'P' && overallmargin.value =='Y')
					{
						data	=	data	+"<table width='100%' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
									"<tr class='formdata'><td width='25%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' "+
									"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='hidden' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' value='0.0' onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='hidden' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' value='0.0'  onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='hidden' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' value='0.0'  onBlur='checkNumbers1(this);'></td></tr></table>";
					}
					else{//ADDED BY SUBRAHMANYAM FOR THE ISSUE 143489

					data	=	data	+"<table width='100%' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
									"<tr class='formdata'><td width='25%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' "+
									"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
									"<td width='25%' height='25' align='center' valign='center'>"+
									"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td></tr></table>";
					}                
						if(document.getElementById("spanMargin")!=null)
						{
							setSpanContent(data,"spanMargin");
					//		document.getElementById("spanMargin").innerHTML=data;
					//		document.getElementById("spanMargin").style.display='block';
						}
						if(document.getElementById("spanValueBreaks")!=null)
						{
							setSpanContent("","spanValueBreaks");
					//		document.getElementById("spanValueBreaks").innerHTML="";
					//		document.getElementById("spanSlabs").style.display='block';
						}
					}else if((marginbasis.value=='V'))
					{
						data=data+"<table width='100%' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
								  "<tr class='formdata'><td height='25' valign='center' colspan='11'><b>Value Breaks</b></td></tr>"+
								  "<tr class='formdata'><td height='25' valign='center' align='center'>"+
								  "<input type='text' class='text' name='marginSlab' size=4 maxLength='6' "+
								  "onKeyPress=''return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>";
								 for(var i=1;i<10;i++)
								 {
									 data=data+"<td height='25' valign='center' valign='center' align='center'><input type='text' class='text' name='marginSlab' size=4 maxLength='6' "+
								  "onKeyPress=''return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>";
								 }
								  data=data+"</tr><tr class='formdata'>";
								 for(var i=0;i<10;i++)
								 {
									 data=data+"<td height='25' valign='center' align='center'><input type='text' class='text' name='marginRate' size=4 maxLength='6' "+
								  "onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>";
								 }
								  data=data+"</tr></table>";

						if(document.getElementById("spanValueBreaks")!=null)
						{
							setSpanContent(data,"spanValueBreaks");
					//		document.getElementById("spanValueBreaks").innerHTML=data;
					//		document.getElementById("spanSlabs").style.display='block';
						}
						if(document.getElementById("spanMargin")!=null)
						{
							setSpanContent("","spanMargin");
					//		document.getElementById("spanMargin").innerHTML="";
					//		document.getElementById("spanSlabs").style.display='block';
						}
					}
				
			}else if(ratebreak.value=='Absolute' || ratebreak.value=='Percent')
			{
					data	=	data	+"<input type='text' class='text' name='marginRate'  size=5 maxLength='6' "+
										"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>>";
					if(document.getElementById("spanMargin")!=null)
					{
							setSpanContent(data,"spanMargin");
					//		document.getElementById("spanMargin").innerHTML="";
					//		document.getElementById("spanSlabs").style.display='block';
					}
			}else if(ratebreak.value=='Slab' || ratebreak.value=='Slab%')
			{
				if((overallmargin.value=='Y') && ratetype.value!='Both')
				{
					if((margintype.value=='P'))
					{
						data	=	data	+"<table width='100%' border='0' cellspacing='1' bgColor='#FFFFFF'>"+
											"<tr class='formdata'><td height='25' valign='center'>&nbsp;&nbsp;"+
											"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' "+
											"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'  <%=readOnly%>></td></tr></table>";
					}else
					{
						data	=	data	+"<table width='100%' border='0' cellspacing='1' bgColor='#FFFFFF'>"+
											"<tr class='formdata'><td width='7%' height='25' align='center' valign='center'>"+
											"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' "+
											"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>"+
											"<td width='7%' height='25' align='center' valign='center'>"+
											"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>"+
											"<td width='7%' height='25' align='center' valign='center'>"+
											"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>"+
											"<td height='25' valign='center' colspan='11'>Slab Margin "+
											"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td></tr></table>";
					}
				}else if((overallmargin.value=='N') || (ratetype.value=='Both'))
				{
						var width = 7;
						var addValue = 0;
						data	=	data	+"<table width='100%' border='0' cellspacing='1' cellpadding='' bgColor='#FFFFFF'>"+
											"<tr class='formdata'><td width='"+width+"%' height='25' align='center' valign='center'>"+
											"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' "+
											"onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>"+
											"<td width='"+width+"%' height='25' align='center' valign='center'>"+
											"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>"+
											"<td width='"+width+"%' height='25' align='center' valign='center'>"+
											"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>";
						for(i=0;i<11;i++)
						{
							data = data+"<td width='"+width+"%' height='25' align='center' valign='center'>"+
										"<input type='text' class='text' name='marginRate'  size=4 maxLength='6' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);' <%=readOnly%>></td>";
						}
						data = data+"</tr></table>"
				}
					if(document.getElementById("spanMargin")!=null)
					{
							setSpanContent(data,"spanMargin");
					//		document.getElementById("spanMargin").innerHTML="";
					//		document.getElementById("spanSlabs").style.display='block';
					}
			}
		}

	}

	function setSpanContent(spanContent,spanLocation)
	{
		if(document.getElementById(spanLocation)!=null)
		{
			document.getElementById(spanLocation).innerHTML=spanContent;
			document.getElementById(spanLocation).style.display='block';
		}
	}
	function showCurrencies(currencyId,index)
	{
		var operation	 = document.forms[0].operation.value;
		var termid		 = '<%=loginbean.getTerminalId()%>';
		//var searchStr	 =	document.getElementById(currencyId).value;
		Url='etrans/ETCCurrencyConversionAddLOV.jsp?searchString=&index='+index+'&Operation='+operation+'&teminalId='+termid+'&name='+currencyId+'&fromWhere=buycharges&selection=single';
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=400,height=360,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
	}
	function loadMarginDetails()
	{
		var overAllMargin	= document.sellChargeform.overallmargin;
		var marginType		= document.sellChargeform.margintype;
		var marginBasis		= document.sellChargeform.marginbasis;
		var marginRate		= document.forms[0].marginRate;
		var marginSlab		= document.forms[0].marginSlab;
<%
		if("Flat".equals(rateBreak) || "Flat%".equals(rateBreak))
		{
%>		
			if(marginBasis.value=='N')
			{
				marginRate[0].value='<%=baseMargin%>';
				marginRate[1].value='<%=minMargin%>';
				marginRate[2].value='<%=maxMargin%>';
				marginRate[3].value='<%=flatMargin%>';
			}else if(marginBasis.value=='V')
			{
<%
				for(int i=0;i<10;i++)
				{
%>
					marginSlab[<%=i%>].value = '<%=(chargeSlab[i]!=null)?chargeSlab[i]:""%>';
					marginRate[<%=i%>].value = '<%=(marginValue[i]!=null)?marginValue[i]:""%>';
<%
				}
%>
			}
			
<%
		}else if("Slab".equals(rateBreak) || "Slab%".equals(rateBreak))
		{
			if("Both".equals(rateType) || "N".equals(overAllMargin))
			{
%>
				marginRate[0].value  = '<%=baseMargin%>';
				marginRate[1].value  = '<%=minMargin%>';
				marginRate[2].value  = '<%=maxMargin%>';
<%
				for(int i=3;i<14;i++)
				{
%>
					marginRate[<%=i%>].value = '<%=(marginValue[i-3]!=null)?marginValue[i-3]:""%>';
<%				
				}
			}else if("A".equals(marginType))
			{
%>
				marginRate[0].value  = '<%=baseMargin%>';
				marginRate[1].value  = '<%=minMargin%>';
				marginRate[2].value  = '<%=maxMargin%>';
				marginRate[3].value  = '<%=(marginValue[0]!=null)?marginValue[0]:""%>';
<%			}else if("P".equals(marginType))
			{
%>
				marginRate.value  = '<%=baseMargin%>';
<%			
			}
		}else if("Absolute".equals(rateBreak) || "Percent".equals(rateBreak))
		{
%>
				marginRate.value='<%=flatMargin%>';
<%
		}
%>
	}
<% //if("Modify".equals(operation) //@@Modified by Kameswari for the WPBN issue-154398
	if("Modify".equals(operation)||"Accept".equals(operation))
	{
%>
		function validataSellChargeHdr()
		{
				var index = 1;
					if(document.getElementById("chargeBasisId"+(index)).value.length ==0)
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
						/*if(document.getElementById("chargeRate1@1").value=='' || parseFloat(document.getElementById("chargeRate1@1").value)==0.0)
						{	
								alert('Please Enter BASE Rate value for ChargeId '+chargeIdValue);
								document.getElementById("chargeRate1@1").focus();
								return false;
						}*/
						if(document.getElementById("chargeRate2").value=='' || parseFloat(document.getElementById("chargeRate2").value)==0.0)
						{	
								alert('Please Enter MIN Rate value for ChargeId '+chargeIdValue);
								document.getElementById("chargeRate2").focus();
								return false;
						}
						/*if(document.getElementById("chargeRate1@3").value=='' || parseFloat(document.getElementById("chargeRate1@3").value)==0.0)
						{	
								alert('Please Enter MAX Rate value for ChargeId '+chargeIdValue);
								document.getElementById("chargeRate1@3").focus();
								return false;
						}*/
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
							/*if(document.forms[0].chargeRate[i].value == 0)
							{
								alert('Please Enter BASE Rate Value ChargeId '+chargeIdValue);
								document.forms[0].chargeRate[i].focus();
								return false;
							}*/
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
							/*if(document.forms[0].chargeRate[i].value == 0)
							{
								alert('Please Enter MAX Rate Value ChargeId '+chargeIdValue);
								document.forms[0].chargeRate[i].focus();
								return false;
							}*/
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
      //ADDED BY SUBRAHMANYAM FOR THE ISSUE 143489
	
		if(marginType == 'P' && overAllMargin == 'Y')
		{
			document.sellChargeform.marginRate[1].value=document.sellChargeform.marginRate[0].value;
			document.sellChargeform.marginRate[2].value=document.sellChargeform.marginRate[0].value;
			document.sellChargeform.marginRate[3].value=document.sellChargeform.marginRate[0].value;
			if(document.getElementById("chargeRate3").value==0)
					document.sellChargeform.marginRate[2].value=0.0
		}
	//ENDED BY SUBRAHMANYAM FOR THE ISSUE 143489
			if(rateBreak=='Absolute' || rateBreak=='Percent')
			{
				if(document.sellChargeform.marginRate.value==0 || document.sellChargeform.marginRate.value=='')
				{
						alert("Enter Base marginValue");
						document.sellChargeform.marginRate.focus();
						return false;			
				}
			}else if(rateBreak == 'Flat' || rateBreak=='Flat%')
			{
				marginBasis		= document.getElementById("marginbasis").value;
				if((marginBasis=='N'))
				{
					//@@Commented and Modified by Kameswari for the WPBN issue-136615
					/*if((document.sellChargeform.marginRate[0].value==0 || document.sellChargeform.marginRate[0].value=='') && document.getElementById("chargeRate1").value.length>0)
					{
						alert("Enter Base marginValue");
						document.sellChargeform.marginRate[0].focus();
						return false;
					}
					if((document.sellChargeform.marginRate[1].value==0 || document.sellChargeform.marginRate[1].value==''))
					{
						alert("Enter Min marginValue");
						document.sellChargeform.marginRate[1].focus();
						return false;
					}
					if((document.sellChargeform.marginRate[2].value==0 || document.sellChargeform.marginRate[2].value=='') && document.getElementById("chargeRate3").value.length>0)
					{
						alert("Enter Max marginValue");
						document.sellChargeform.marginRate[2].focus();
						return false;
					}
					if(document.sellChargeform.marginRate[3].value==0 || document.sellChargeform.marginRate[3].value=='')
					{
						alert("Enter Flat marginValue");
						document.sellChargeform.marginRate[3].focus();
						return false;
					}*/
					
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
								if(document.forms[0].marginRate[i].value == 0 || document.forms[0].marginRate[i].value=='')
								{
										alert('Please Enter marginValue Value at column 1');
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
								if(document.forms[0].marginRate[i].value == 0 || document.forms[0].marginRate[i].value=='')
								{
										alert('Please Enter marginValue Value at column 2');
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
								if(document.forms[0].marginRate[i].value == 0)
								{
										alert('Please Enter marginValue Value at column '+(i+1));
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
						if(document.forms[0].marginRate.value==0 || document.forms[0].marginRate.value=='')
						{
							alert("Please Enter marginValue");
							document.forms[0].marginRate.focus();
							return false;
						}
					}else
					{
						if(document.sellChargeform.marginRate[0].value.length==0 && document.sellChargeform.chargeRate[0].value.length>0)
						{
							alert("Enter Base marginValue");
							document.sellChargeform.marginRate[0].focus();
							return false;
						}
						if(document.sellChargeform.marginRate[1].value.length==0)
						{
							alert("Enter Min marginValue");
							document.sellChargeform.marginRate[1].focus();
							return false;
						}
						if(document.sellChargeform.marginRate[2].value.length==0 && document.sellChargeform.chargeRate[2].value.length>0)
						{
							alert("Enter Max marginValue");
							document.sellChargeform.marginRate[2].focus();
							return false;
						}
						if(document.sellChargeform.marginRate[3].value.length==0)
						{
							alert("Enter Slab marginValue");
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
									alert("Enter marginRate");
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
										alert("Enter marginRate");
										document.sellChargeform.marginRate[i].focus();
										return false;
									}
								}else
								{
									if(document.sellChargeform.chargeRate[i].value.length>0 ||   document.sellChargeform.chargeFlatRate[i-3].value.length>0)
									{
										alert("Enter marginRate");
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
<%
	}
%>
		function Manditory()
		{

			var flag = true;
<%
		 //if("Modify".equals(operation) //@@Modified by Kameswari for the WPBN issue-154398
	if("Modify".equals(operation)||"Accept".equals(operation))
			{
%>
				if(!validataSellChargeHdr())
					flag = false;
				if(flag && !validateMarginType())
					flag = false;
				if(flag && !validateSlabs())
					flag = false;
				if(flag && !validateMargins())
					flag = false;
<%
			}
%>
			if(flag)
			{
				if(document.sellChargeform.submitB!=null)
				{	document.sellChargeform.submitB.disabled = true;}
				if(document.sellChargeform.resetB!=null)
				{	document.sellChargeform.resetB.disabled = true;}		
			}
			return flag;
		}
		function showChargeBasis(chargeBasisId,index)
		{
				var operation	 = document.forms[0].operation.value;
				var termid		 = '<%=loginbean.getTerminalId()%>';
				var	searchStr	 = document.getElementById(chargeBasisId).value; Url='etrans/QMSLOVChargeBasisIds.jsp?searchString=&shipmentMode=&index='+index+'&Operation='+operation+'&teminalId='+termid+'&name='+chargeBasisId+'&fromWhere=sellcharges&selection=single';
				var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
				var Options='scrollbar=yes,width=400,height=360,resizable=no';
				var Features=Bars+' '+Options;
				var Win=open(Url,'Doc',Features);
		}
function viewContinue()//added by rk
{
	document.forms[0].action='QMSSellChargesController?Operation=View';
	document.forms[0].submit();
	return true;
}
</script>
</head>
<body onload='setMarginSpan();loadMarginDetails();'>
<form name="sellChargeform" method="post" action="QMSSellChargesController" onSubmit ='return Manditory()'>
<table width="940" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="940" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td>SellCharge - <%=operation%> </td>
						<td align=right>QS1050622 
						</td></tr></table>
			</td>
		  </tr>
		</table>
		<table width="940" border="0" cellspacing="1" >
			<tr class="formdata"><td colspan="13" >&nbsp;</td></tr>
		 </table>
		<table id="chargesTable" width="940" border="0" cellspacing="1" cellpadding="4">
		  <tr class='formheader'>
		  <td></td>
			<td width="100" align=center><b>Charge:<font color="#FF0000">*</font></b></td>
			<td width="190" align=center><b>DescriptionId:<font color="#FF0000">*</font></b></td>
			<td width="100" align=center><b>ChargeBasis:<font color="#FF0000">*</font></b></td>
			<td width="150" align=center><b>Description:</b></td>
			<td width="100" align=center><b>Currency:<font color="#FF0000">*</font></b></td>
			<td width="100" align=center><b>Rate Break:<font color="#FF0000">*</font></b></td>
			<td width="100" align=center><b>Rate Type:<font color="#FF0000">*</font></b></td>
			<td width="100" align=center><b>Weight Class:<font color="#FF0000">*</font></b></td>
		  </tr>
		  <tr class='formdata'>
			<td><input type='hidden' name='buychargeid' value='<%=buychargesHDRDOB.getBuychargeId()%>'></td>
			<td width="100" align=center><input type='text' class='text' name='chargeId' id='chargeId1' size='7' value='<%=buychargesHDRDOB.getChargeId()%>' readOnly></td>
			<td width="190" align=center><input type='text' class='text' name='chargeDescriptionId' id='chargeDescriptionId1' size='25' value='<%=buychargesHDRDOB.getChargeDescId()%>' readOnly></td>
			<td width="100" align=center><input type='text' class='text' name='chargeBasisId' id='chargeBasisId1' size='7' value='<%=buychargesHDRDOB.getChargeBasisId()%>' readOnly><input type='button' class ='input' name='chargeBasisIdPoPLov' value='...' onClick='showChargeBasis("chargeBasisId","1")' disabled></td>
			<td width="150" align=center><input type='text' class='text' name='chargeBasisDescription' id='chargeBasisDescription1'  size='15' value='<%=buychargesHDRDOB.getChargeBasisDesc()%>' readOnly></td>
			<td width="100" align=center><input type='text' class='text' name='currencyId' id='currencyId1'  size='7' value = '<%=buychargesHDRDOB.getCurrencyId()%>' onBlur='toUpper(this);' readOnly>
			<!-- <input type='button' class ='input' name='currencyIdPoPLov' value='...' onClick='showCurrencies("currencyId","1")' <%=disabled%>> -->	
			</td>
			<td width="100" align=center><select  class='select' name='rateBreak' id='rateBreak1' >
				<option value='<%=buychargesHDRDOB.getRateBreak()%>'><%=buychargesHDRDOB.getRateBreak()%></option></td>
			<td width="100" align=center><select  class='select' name='rateType' id='rateType1'>
				<option value='<%=buychargesHDRDOB.getRateType()%>'><%=buychargesHDRDOB.getRateType()%></option></td>
			<td width="100" align=center><select  class='select' name='weightClass' id='weightClass1'>
				<option value='<%=buychargesHDRDOB.getWeightClass()%>'><%=(buychargesHDRDOB.getWeightClass()!=null && (buychargesHDRDOB.getWeightClass().equals("G"))?"General":"WeightScale")%></option></td>
		  </tr>
			<tr class="formdata"><td colspan="13" >&nbsp;</td></tr>
		</table>
<%
			dtlList			=	buychargesHDRDOB.getBuyChargeDtlList();
			if(dtlList!=null && dtlList.size()>0)
			{
%>
				<table width='940' border='0' cellspacing='1' cellpadding='4'>
					<tr class='formlabel'  colspan='13' valign='Top'>&nbsp Margin Calculations</tr>
					<tr class='formdata'  colspan='' valign='Top'>
					<td width=''>Overall Margin:<select name='overallmargin' class='select' onChange='setMarginSpan()'>
<%
		 //if("Modify".equals(operation) //@@Modified by Kameswari for the WPBN issue-154398
	if("Modify".equals(operation)||"Accept".equals(operation))
			{
%>
						<option value=''></option><option value='N' <%=("N".equals(overAllMargin)?"Selected":"")%>>No</option>
<%
				if(!"Both".equals(buychargesHDRDOB.getRateType()))
				{
%>				
						<option value='Y' <%=("Y".equals(overAllMargin)?"Selected":"")%>>Yes</option>

<%				
				}
%>
					    </select></td>
					<td>Margin Type<select name='margintype' class='select' onChange='setMarginSpan()'><option value=''></option>
						<option value='A' <%=("A".equals(marginType)?"Selected":"")%>>Absolute</option>
						<option value='P' <%=("P".equals(marginType)?"Selected":"")%>>Percent</option></select></td>
<%
				if("Flat".equals(buychargesHDRDOB.getRateBreak()) || "Flat%".equals(buychargesHDRDOB.getRateBreak()))
				{
%>
					<td>Margin Basis<select name='marginbasis' class='select' onChange='setMarginSpan()'><option value=''></option>
					    <option value='N' <%=("N".equals(marginBasis)?"Selected":"")%>>Normal</option>
					    <!-- <option value='V' <%=("V".equals(marginBasis)?"Selected":"")%>>Value</option></select></td> -->
<%			
				}
			}else if("View".equals(operation) || "Delete".equals(operation))
				{
%>
					<option value='<%=overAllMargin%>'><%=(overAllMargin.equals("Y"))?"Yes":"No"%></option></select></td>
					<td>Margin Type<select name='margintype' class='select' onChange='setMarginSpan()'>
					<option value='<%=marginType%>'><%=(marginType.equals("A"))?"Absolute":"Percent"%></option></select></td>
<%
					if("Flat".equals(buychargesHDRDOB.getRateBreak()) || "Flat%".equals(buychargesHDRDOB.getRateBreak()))
					{
%>
						<td>Margin Basis<select name='marginbasis' class='select' onChange='setMarginSpan()'>
						<option value='<%=marginBasis%>'><%=(marginBasis.equals("N"))?"Normal":"Value"%></option>
<%
					}
				}
				if("Percent".equals(buychargesHDRDOB.getRateBreak()) || "Absolute".equals(buychargesHDRDOB.getRateBreak()))
				{
					buychargesDtlDOB	=	(BuychargesDtlDOB)dtlList.get(0);
%>
						</tr></table>
						<table width='940' border='0' cellspacing='1' cellpadding='4'>
						<tr class='formdata'  colspan='' valign='Top'><td rowspan=2 width='10%'><b>ChargeId: <br><%=buychargesHDRDOB.getChargeId()%></b></td></td>
						<td width='10%' ><b>FLAT</b><input type=hidden name='chargeSlab' value='AbsRPersent'></td><td></td></tr>
						<tr class='formdata' colspan='' valign='Top'>
						<td width='80%'><input type='text' class='text' id='chargeRate1' name='chargeRate' value='<%=buychargesDtlDOB.getChargeRate()%>' size=5 readOnly ></td>
						<td></td></tr><tr class='formdata'><td width='10%'><b>Margin</b></td><td colspan='1'>
						<span id='spanMargin'></span></td></tr></table>
<%
				}else if("Flat".equals(buychargesHDRDOB.getRateBreak()) || "Flat%".equals(buychargesHDRDOB.getRateBreak()))
				{
%>
<!-- 					<table width='940' border='0' cellspacing='1' cellpadding='4'>
						<tr class='formlabel'  colspan='13' valign='Top'>Margin Calculations</tr> -->
						</tr></table>
						<table width='940' border='0' cellspacing='1' cellpadding=''>
						<tr class='formdata' colspan='' valign='Top'><td rowspan=2 width='10%'><b>ChargeId: <br><%=chargeId%></b></td></td>
						<td width='7%' height='25' align='center' valign='center'><b>BASE</b><input type=hidden name='chargeSlab' value='BASE'></td>
						<td width='7%' height='25' align='center' valign='center'><b>MIN</b><input type=hidden name='chargeSlab' value='MIN'></td>
						<td width='7%' height='25' align='center' valign='center'><b>MAX</b><input type=hidden name='chargeSlab' value='MAX'></td>
						<td width='7%' height='25' align='center' valign='center'><b>FLAT</b><input type=hidden name='chargeSlab' value='Flat'></td><td rowspan=3 width='62%' height='75'><span id='spanValueBreaks'></span></td></tr>
						<tr class='formdata' colspan='' valign='Top'>
						<td width='7%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' id='chargeRate1' value='<%=base%>' size=4 readOnly></td>
						<td width='7%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' id='chargeRate2' value='<%=min%>' size=4 readOnly></td>
						<td width='7%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' id='chargeRate3' value='<%=max%>' size=4 readOnly></td>
						<td width='7%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' id='chargeRate4' value='<%=flat%>' size=4 readOnly></td></tr><tr class='formdata'><td width='10%'><b>Margin</b></td><td colspan='4' height='25'>
						<span id='spanMargin'></span></td></tr></table>
<%
				}else if("Slab".equals(buychargesHDRDOB.getRateBreak()) || "Slab%".equals(buychargesHDRDOB.getRateBreak()))
				{					
%>
<!-- 					<table width='940' border='0' cellspacing='0' cellpadding='4'>
						<tr class='formlabel'  colspan='13' valign='Top'>Margin Calculations</tr> -->
						</tr></table>
						<table width='940' border='0' cellspacing='1' cellpadding=''>
						<tr class='formdata' colspan='13' valign='Top'><td rowspan=2 width='10%'><b>ChargeId: <br><%=chargeId%></b></td></td>
						<td width='6%' height='25' align='center' valign='center'><b>BASE</b><input type=hidden name='chargeSlab' value='BASE'></td>
						<td width='6%' height='25' align='center' valign='center'><b>MIN</b><input type=hidden name='chargeSlab' value='MIN'></td>
						<td width='6%' height='25' align='center' valign='center'><b>MAX</b><input type=hidden name='chargeSlab' value='MAX'></td>
						
<%
						for(int j=0;j<11;j++)
						{
%>
						<td width='6%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeSlab' value='<%=(chargeSlab[j]!=null)?chargeSlab[j]:""%>' size=4 readOnly></td>
<%
						}
%>
						</tr>
						<tr class='formdata' colspan='' valign='Top'>
						<td width='6%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' value='<%=base%>' size=4 readOnly></td>
						<td width='6%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' value='<%=min%>' size=4  readOnly></td>
						<td width='6%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' value='<%=max%>' size=4  readOnly></td>
<%						for(int j=0;j<11;j++)
						{
							if("Both".equals(rateType))
							{
%>
								<td width='4%' height='25' align='center' valign='center'><input type='text' class='textHighlight' name='chargeFlatRate' id='chargeFlatRate1@<%=(j+1)%>' value='<%=(chargeFlatRate[j]!=null)?chargeFlatRate[j]:""%>' size=1 readOnly><input type='text' class='text' name='chargeRate' id='chargeRate1@<%=(j+1)%>' value='<%=(chargeRate[j]!=null)?chargeRate[j]:""%>' size=3 readOnly></td>
<%
							}else{
%>
								<td width='6%' height='25' align='center' valign='center'><input type='text' class='text' name='chargeRate' value='<%=(chargeRate[j]!=null)?chargeRate[j]:""%>' size=4 readOnly></td>
<%
							}
						}
%>
						</tr><tr class='formdata'><td width='10%'><b>Margin</b></td><td colspan='14' height='25'>
						<span id='spanMargin'></span></td></tr></table>
<%					
				}
			}else
			{
				throw new Exception();
			}
%>		  
		<table border="0" width="940" cellpadding="4" cellspacing="1">
          <tr class='denotes' bgcolor="#FFFFFF">
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory
            </td>
            <td valign="top" align="right">
                 
<%
				if(!"View".equals(operation))
				{
%>					<input type="submit" value="Submit" name="submitB" class="input" onClick=''>
					<input type='reset' value='Reset' class='input' name ='resetB'>
<%
				}else if("View".equals(operation)){
%> 					<input type="button" value="Continue" name="continue" class="input" onClick='return viewContinue()'>
<%}%>
				<input type='hidden' name='Operation' value='<%=operation%>'>
				<input type='hidden' name='subOperation' value='<%=operation%>'>
				<input type="hidden" name="terminalId" id="terminalId" value="<%=dataTerminalId%>">
				<input type="hidden" name="dummyBuychargesflag" value='<%=(dummyBuyChargeFlag!=null)?dummyBuyChargeFlag:""%>'>
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>
</form>
</body>
</html>
<%	}catch(Exception e)
	{
		//Logger.info(FILE_NAME,"exeption ----->"+e);
    logger.info(FILE_NAME+"exeption ----->"+e);
		errorMessage	= "Exception While Loading the data:";
		nextNavigation	= "QMSSellChargesController?Operation="+operation+"&subOperation=";				
		errorMessageObject = new ErrorMessage(errorMessage,nextNavigation); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
%>
					<jsp:forward page="QMSESupplyErrorPage.jsp" />
<%

	}
%>
