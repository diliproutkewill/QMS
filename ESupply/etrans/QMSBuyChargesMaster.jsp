<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSBuyChargesMaster.jsp
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
	private static final String FILE_NAME	=	"QMSBuyChargesMaster.jsp";
%>
<%	
  logger  = Logger.getLogger(FILE_NAME);	
	String operation					=	request.getParameter("Operation");
	String subOperation					=	request.getParameter("subOperation");
	String   readOnly		=	"";
	String   disabled		=	"";
	String chargeId			=	"";
	String chargeDesc		=	"";
	String chargeBasisId	=	"";
	String chargeBasisDesc	=	"";
	String currencyId		=	"";
	String rateBreak		=	"";
	String rateType			=	"";
	String weightClass		=	"";
	String base				=	"";
	String min				=	"";
	String max				=	"";
	String flat				=	"";
	String[] chargeSlab		=	new String[14];
	String[] chargeRate		=	new String[14];
	String[] chargeFlatRate	=	new String[14];
	BuychargesHDRDOB buychargesHDRDOB	=	null;
	BuychargesDtlDOB buychargesDtlDOB	=	null;
	ArrayList		 dtlList			=	null;
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	String      densityRatio        =   null;
	String      nextNavigation		=	"";
	String		dataTerminalId		= (String)request.getAttribute("terminalId");
	ArrayList   densityGroupList    = null;
     ArrayList   chargeBasisList    = null;
	try{
	
		if(dataTerminalId==null || dataTerminalId.equals(""))
        { dataTerminalId=loginbean.getTerminalId();}
          
		//@@Added by Kameswari for the WPBN issue-54554
		if(operation.equals("Add")||operation.equals("Modify"))
		{
		   densityGroupList    = (ArrayList)session.getAttribute("densityGroupList");
		   chargeBasisList     =  (ArrayList)session.getAttribute("chargeBasisList");
		}
		//@@WPBN issue-54554
		if(operation.equals("Modify"))
		{
				readOnly		=	"readOnly";
				disabled		=	"disabled";
				buychargesHDRDOB	=	(BuychargesHDRDOB)request.getAttribute("BuyChargesHDRDtls");
				if(buychargesHDRDOB!=null)
				{
					chargeId		=	buychargesHDRDOB.getChargeId();
					chargeDesc		=	buychargesHDRDOB.getChargeDescId();
					chargeBasisId	=	buychargesHDRDOB.getChargeBasisId();
					chargeBasisDesc	=	buychargesHDRDOB.getChargeBasisDesc();
					currencyId		=	buychargesHDRDOB.getCurrencyId();
					rateBreak		=	buychargesHDRDOB.getRateBreak();
					rateType		=	buychargesHDRDOB.getRateType();
					weightClass		=	buychargesHDRDOB.getWeightClass();
					densityRatio    =   buychargesHDRDOB.getDensityGrpCode();
					dtlList	=	buychargesHDRDOB.getBuyChargeDtlList();

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
									//if("F".equals(buychargesDtlDOB.getChargeRate_indicator()))//Hilighted text is for Slab
									if("S".equals(buychargesDtlDOB.getChargeRate_indicator()))
										{	chargeFlatRate[cnt++]	=	new Double(buychargesDtlDOB.getChargeRate()).toString();}
									else
										{	chargeRate[cnt++]	=	new Double(buychargesDtlDOB.getChargeRate()).toString();}
								}
							}
						}
					}else
					{
						throw new Exception("No data Found");
					}
				}else
				{
					throw new Exception("No data Found");
				}

		}
	
%>
<html>	    
<head>
<title>Buy Charges  - Add</title>
	<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script src ="html/eSupply.js"></script>
<SCRIPT LANGUAGE="JavaScript">
//dyn table script
var idcounter=1,counter=1;
//var primaryunit = null;
var primaryunit = new Array();

function validateChargeId()
{
	var index=idcounter-1;
	/*for(var j=1;j<index;j++)
	{
		if(document.getElementById("chargeId"+(j))!=null)
		{
			for(var k=j+1;k<=index;k++)
			{	
				if(document.getElementById("chargeId"+(k))!=null)
				{	
					if((document.getElementById("chargeId"+(j)).value == document.getElementById("chargeId"+k).value ))
					{
						if((document.getElementById("chargeBasisId"+(j)).value == document.getElementById("chargeBasisId"+k).value ))
							{	alert("[ChargeId,chargeBasisId]  should not be the same for different lanes.");
								return false;
							}
					}
				}
			}
		}*/
		for(var j=1;j<index;j++)
		{
			if(document.getElementById("chargeDescriptionId"+(j))!=null)
			{
				for(var k=j+1;k<=index;k++)
				{	
					if(document.getElementById("chargeDescriptionId"+(k))!=null)
					{
						if(document.getElementById("chargeDescriptionId"+(j)).value== document.getElementById("chargeDescriptionId"+(k)).value)
						{
								alert("chargeDescriptionId should not be the same for different lanes.");
								return false;
						}
					}
				}
			}
		}
 return true;
}
function validateSlabs()
{
	var index=idcounter-1;
	var chargeIdVal;
	var rateType;
	var rateBreak;
	var rateCaltype;
	var charFlatRateIndex=0;
	var charFlatRateIndexLen=0;
	var chargeRowLength = 0;
	var i=0;

	if(index > 1)
	{
      for(var j=1;j<=index;j++)
      {
		chargeIdVal = document.getElementById("chargeDescriptionId"+j);
        rateBreak = document.getElementById("rateBreak"+j);
	    rateType = document.getElementById("chargeRateType"+j);
        rateCaltype = document.getElementById("rateCalculation"+j);
		



		if(chargeIdVal!=null)
		 {
				chargeIdValue=chargeIdVal.value;
				chargeRowLength = chargeRowLength + parseInt(document.getElementById("chargelen"+j).value);

				if(rateBreak.value == 'Absolute' || rateBreak.value == 'Percent')
				{
				
					if(document.getElementById("chargeRate"+j).value=='' || parseFloat(document.getElementById("chargeRate"+j).value)==0.0)
					{	
							alert('Please Enter '+rateBreak.value+' value for ChargeDescriptionId: '+chargeIdValue);
							document.getElementById("chargeRate"+j).focus();
							return false;
					}
					i = chargeRowLength;
				}			
				else if(rateBreak.value == 'Flat' || rateBreak.value == 'Flat%')
				{
						/*if(document.getElementById("chargeRate"+j+"@"+1).value=='' || parseFloat(document.getElementById("chargeRate"+j+"@"+1).value)==0.0)
						{	
								alert('Please Enter BASE Rate value for ChargeId '+chargeIdValue);
								document.getElementById("chargeRate"+j+"@"+1).focus();
								return false;
						}*/
						if((document.getElementById("chargeRate"+j+"@"+1).value=='' || parseFloat(document.getElementById("chargeRate"+j+"@"+1).value)==0.0)
						&& (document.getElementById("chargeRate"+j+"@"+2).value=='' || parseFloat(document.getElementById("chargeRate"+j+"@"+2).value)==0.0))
						{	
						 alert('Please Enter BASE/MIN Rate value for ChargeDescriptionId: '+chargeIdValue);
								return false;
						
						}
						/*if(document.getElementById("chargeRate"+j+"@"+3).value=='' || parseFloat(document.getElementById("chargeRate"+j+"@"+3).value)==0.0)
						{	
								alert('Please Enter MAX Rate value for ChargeId '+chargeIdValue);
								document.getElementById("chargeRate"+j+"@"+3).focus();
								return false;
						}*/
						if(document.getElementById("chargeRate"+j+"@"+4).value=='' || parseFloat(document.getElementById("chargeRate"+j+"@"+4).value)==0.0)
						{	
								alert('Please Enter FLAT Rate value for ChargeDescriptionId: '+chargeIdValue);
								document.getElementById("chargeRate"+j+"@"+4).focus();
								return false;
						}
						i = chargeRowLength;
				}
				else if(rateBreak.value == 'Slab' || rateBreak.value == 'Slab%')
				{
							

							for(var k=0; k<14; k++)
							{
								//alert("K+I=="+(k+i)+"i="+i)

								if(k == 0)
								{
									if((document.forms[0].chargeRate[k+i].value == 0 && document.forms[0].chargeRate[k+i+1].value == 0))
									{
										alert('Please Enter BASE/MIN Rate Value ChargeId '+chargeIdValue);
										document.forms[0].chargeRate[k+i].focus();
										return false;
									}
								}else if(k == 1)
								{
										if((document.forms[0].chargeRate[k+i].value == 0 && document.forms[0].chargeRate[k+i-1].value == 0))
										{
											alert('Please Enter BASE/MIN Rate Value for ChargeDescriptionId: '+chargeIdValue);
											document.forms[0].chargeRate[k+i].focus();
											return false;
										}
								}else if(k == 2)
								{
										/*if(document.forms[0].chargeRate[k+i].value == 0)
										{
											alert('Please Enter MAX Rate Value ChargeId '+chargeIdValue);
											document.forms[0].chargeRate[k+i].focus();
											return false;
										}*/
								}
								else if(k == 3)
								{
									if(document.forms[0].chargeSlab[k+i].value == 0)
									{
										alert('Please Enter Negative Value For Slab at Column 4 for ChargeDescriptionId: '+chargeIdValue);
										document.forms[0].chargeSlab[k+i].focus();
										return false;
									}
									else
									{
										if(isNaN(document.forms[0].chargeSlab[k+i].value) || parseInt(document.forms[0].chargeSlab[k+i].value) >= 0)
										{
											alert('Please Enter Negative Value For Slab at Column 4 for ChargeDescriptionId: '+chargeIdValue);
											document.forms[0].chargeSlab[k+i].focus();
											return false;
										}
										if(document.forms[0].chargeRate[k+i].value == 0 || document.forms[0].chargeRate[k+i].value =='')
										{
											if(rateType.value=='Flat' || rateType.value=='Slab')
											{
												alert('Please Enter Rate Value at column 4 for ChargeDescriptionId: '+chargeIdValue);
												document.forms[0].chargeRate[k+i].focus();
												return false;
											}else
											{
												if(document.forms[0].chargeFlatRate[k+charFlatRateIndex-3].value==0)
												{
												alert('Please Enter FlatRate/SlabRate Value at column 4 for ChargeDescriptionId: '+chargeIdValue);
												document.forms[0].chargeFlatRate[k+charFlatRateIndex-3].focus();
												return false;
												}
											}
										}
									}
								}
								else if(k == 4)
								{
									if(document.forms[0].chargeSlab[k+i].value == 0)
									{
										alert('Please Enter Positive Value For Slab at Column 5 for ChargeDescriptionId: '+chargeIdValue);
										document.forms[0].chargeSlab[k+i].focus();
										return false;
									}
									else
									{
										if(isNaN(document.forms[0].chargeSlab[k+i].value) || parseInt(document.forms[0].chargeSlab[k+i].value) <= 0)
										{
											alert('Please Enter Positive Value For Slab at Column 5 for ChargeDescriptionId: '+chargeIdValue);
											document.forms[0].chargeSlab[k+i].focus();
											return false;
										}
										if(Math.abs(document.forms[0].chargeSlab[k+i].value)  != Math.abs(document.forms[0].chargeSlab[k+i-1].value))
										{
											 alert("Please Enter Equal Positive Amount for Slab at Column No. "+(k+1)+' for ChargeDescriptionId: '+chargeIdValue);
											 document.forms[0].chargeSlab[k+i].focus();
											 return false;				 
										}
										if(document.forms[0].chargeRate[k+i].value == 0)
										{
											if(rateType.value=='Flat' || rateType.value=='Slab')
											{
												alert('Please Enter Rate Value at column 5 for ChargeDescriptionId: '+chargeIdValue);
												document.forms[0].chargeRate[k+i].focus();
												return false;
											}else
											{
												if(document.forms[0].chargeFlatRate[k+charFlatRateIndex-3].value==0)
												{
												alert('Please Enter FlatRate/SlabRate Value at column 5 for ChargeDescriptionId: '+chargeIdValue);
												document.forms[0].chargeFlatRate[k+charFlatRateIndex-3].focus();
												return false;
												}
											}
										}
									}
								}
								else
								{
									if(document.forms[0].chargeSlab[k+i].value != 0 || document.forms[0].chargeSlab[k+i].value!='')
									{
										if(isNaN(document.forms[0].chargeSlab[k+i].value) || parseInt(document.forms[0].chargeSlab[k+i].value) <= 0)
										{
											alert('Please Enter Positive Value For Slab at Column '+(k+1)+' for ChargeDescriptionId: '+chargeIdValue);
											document.forms[0].chargeSlab[k+i].focus();
											return false;
										}
										if(Math.abs(document.forms[0].chargeSlab[k+i-1].value)  >= Math.abs(document.forms[0].chargeSlab[k+i].value))
										{
											 alert("Please Enter Slab at Column No. "+(k+1)+ 'should be greater than Slab at column no. '+k+' for ChargeDescriptionId: '+chargeIdValue);
											 document.forms[0].chargeSlab[k+i].focus();
											 return false;				 
										}
										if(document.forms[0].chargeRate[k+i].value == 0)
										{
											if(rateType.value=='Flat' || rateType.value=='Slab')
											{
												alert('Please Enter Rate Value at column '+(k+1)+' for ChargeDescriptionId: '+chargeIdValue);
												document.forms[0].chargeRate[k+i].focus();
												return false;
											}else
											{
												if(document.forms[0].chargeFlatRate[k+charFlatRateIndex-3].value==0)
												{
												alert('Please Enter FlatRate/SlabRate Value at column '+(k+1)+' for ChargeDescriptionId: '+chargeIdValue);
												document.forms[0].chargeFlatRate[k+charFlatRateIndex-3].focus();
												return false;
												}
											}
										}
									}else
									{
										if(rateType.value=='Both')
										{
											if(document.forms[0].chargeFlatRate[k+charFlatRateIndex-3]!=null)
											{														if((document.forms[0].chargeFlatRate[k+charFlatRateIndex-3].value!=0 || document.forms[0].chargeFlatRate[k+charFlatRateIndex-3].value!='') || (document.forms[0].chargeRate[k+i].value!=0 || document.forms[0].chargeRate[k+i].value!=''))
												{
														alert('Please Enter Positive Value For Slab at Column '+(k+1)+' for ChargeDescriptionId: '+chargeIdValue);
														document.forms[0].chargeSlab[k+i].focus();
														return false;
												}
											}
										}
										else if(document.forms[0].chargeRate[k+i].value!=0 || document.forms[0].chargeRate[k+i].value!='')
										{
													alert('Please Enter Positive Value For Slab at Column '+(k+1)+' for ChargeDescriptionId: '+chargeIdValue);
													document.forms[0].chargeSlab[k+i].focus();
													return false;
										}
									}

								}

							}
							i = chargeRowLength;

					if(rateType.value=='Both')
					{	charFlatRateIndexLen=charFlatRateIndexLen+11;
						charFlatRateIndex	=charFlatRateIndexLen;}
				}
			}
		}
	}
	else
	{
	  	chargeIdVal = document.getElementById("chargeDescriptionId"+1);
        rateBreak = document.getElementById("rateBreak"+1);
	    rateType = document.getElementById("chargeRateType"+1);
        rateCaltype = document.getElementById("rateCalculation"+1);
		chargeIdValue=chargeIdVal.value;	
			 if(rateBreak.value == 'Percent' || rateBreak.value == 'Absolute')
			 {	
				
				if(document.getElementById("chargeRate"+1).value=='' || parseFloat(document.getElementById("chargeRate"+1).value)==0.0)
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
					if((document.getElementById("chargeRate1@1").value=='' || parseFloat(document.getElementById("chargeRate1@1").value)==0.0)
						&& (document.getElementById("chargeRate1@2").value=='' || parseFloat(document.getElementById("chargeRate1@2").value)==0.0))
					{	
						 alert('Please Enter BASE/MIN Rate value for ChargeDescriptionId: '+chargeIdValue);
							return false;
						
					}
					/*if(document.getElementById("chargeRate1@3").value=='' || parseFloat(document.getElementById("chargeRate1@3").value)==0.0)
					{	
						    alert('Please Enter MAX Rate value for ChargeId '+chargeIdValue);
							document.getElementById("chargeRate1@3").focus();
							return false;
					}*/
					if(document.getElementById("chargeRate1@4").value=='' || parseFloat(document.getElementById("chargeRate1@4").value)==0.0)
					{	
						    alert('Please Enter FLAT Rate value for ChargeDescriptionId: '+chargeIdValue);
							document.getElementById("chargeRate1@4").focus();
							return false;
					}
			}
			else if(rateBreak.value == 'Slab' || rateBreak.value == 'Slab%')
			{
				for(var i=0; i<document.forms[0].chargeSlab.length; i++)
				{
					if(i == 0)
					{
						if((document.forms[0].chargeRate[i].value == 0 && document.forms[0].chargeRate[i+1].value == 0) )
						{
							alert('Please Enter BASE/MIN Rate Value ChargeId '+chargeIdValue);
							document.forms[0].chargeRate[i].focus();
							return false;
						}
					}
					else if(i == 1)
					{
						if((document.forms[0].chargeRate[i].value == 0 && document.forms[0].chargeRate[i-1].value == 0))
						{
							alert('Please Enter MIN Rate Value for ChargeDescriptionId: '+chargeIdValue);
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
							alert('Please Enter Negative Value For Slab at Column 4 for ChargeDescriptionId: '+chargeIdValue);
							document.forms[0].chargeSlab[i].focus();
							return false;
						}
						else
						{
							if(isNaN(document.forms[0].chargeSlab[i].value) || parseInt(document.forms[0].chargeSlab[i].value) >= 0)
							{
								alert('Please Enter Negative Value For Slab at Column 4 for ChargeDescriptionId: '+chargeIdValue);
								document.forms[0].chargeSlab[i].focus();
								return false;
							}
							if(document.forms[0].chargeRate[i].value == 0 )
							{
								if(rateType.value=='Flat' || rateType.value=='Slab')
								{
									alert('Please Enter Rate Value at column 4 for ChargeDescriptionId: '+chargeIdValue);
									document.forms[0].chargeRate[i].focus();
									return false;
								}else
								{
									if(document.forms[0].chargeFlatRate[i-3].value==0)
									{
									alert('Please Enter FlatRate/SlabRate Value at column 4 for ChargeDescriptionId: '+chargeIdValue);
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
							alert('Please Enter Positive Value For Slab at Column 5 for ChargeDescriptionId: '+chargeIdValue);
							document.forms[0].chargeSlab[i].focus();
							return false;
						}
						else
						{
							if(isNaN(document.forms[0].chargeSlab[i].value) || parseInt(document.forms[0].chargeSlab[i].value) <= 0)
							{
								alert('Please Enter Positive Value For Slab at Column 5 for ChargeDescriptionId: '+chargeIdValue);
								document.forms[0].chargeSlab[i].focus();
								return false;
							}
							if(Math.abs(document.forms[0].chargeSlab[3].value)  != Math.abs(document.forms[0].chargeSlab[i].value))
							{
								 alert("Please Enter Equal Possitive Amount for Slab at Column No. "+(i+1)+' for ChargeDescriptionId: '+chargeIdValue);
								 document.forms[0].chargeSlab[i].focus();
								 return false;				 
							}
							if(document.forms[0].chargeRate[i].value == 0)
							{
								if(rateType.value=='Flat' || rateType.value=='Slab')
								{
									alert('Please Enter Rate Value at column 5 for ChargeDescriptionId: '+chargeIdValue);
									document.forms[0].chargeRate[i].focus();
									return false;
								}else
								{
									if(document.forms[0].chargeFlatRate[i-3].value==0)
									{
									alert('Please Enter FlatRate/SlabRate Value at column 5 for ChargeDescriptionId: '+chargeIdValue);
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
								alert('Please Enter Positive Value For Slab at Column '+(i+1)+' for ChargeDescriptionId: '+chargeIdValue);
								document.forms[0].chargeSlab[i].focus();
								return false;
							}
							if(Math.abs(document.forms[0].chargeSlab[i].value)  <= Math.abs(document.forms[0].chargeSlab[i-1].value))
							{
								 alert("Please Enter Slab at Column No. "+(i+1)+" is should be greate than Slab at Column No. "+i+' for ChargeDescriptionId: '+chargeIdValue);
								 document.forms[0].chargeSlab[i].focus();
								 return false;				 
							}
							if(document.forms[0].chargeRate[i].value == 0)
							{
								if(rateType.value=='Flat' || rateType.value=='Slab')
								{
									alert('Please Enter Rate Value at column '+(i+1)+' for ChargeDescriptionId: '+chargeIdValue);
									document.forms[0].chargeRate[i].focus();
									return false;
								}else
								{
									if(document.forms[0].chargeFlatRate[i-3].value==0)
									{
									alert('Please Enter FlatRate/SlabRate Value at column '+(i+1)+' for ChargeDescriptionId: '+chargeIdValue);
									document.forms[0].chargeFlatRate[i-3].focus();
									return false;
									}
								}
							}
						}else
						{
								if(document.forms[0].chargeRate[i].value!=0 || document.forms[0].chargeRate[i].value!='')
								{
									if(rateType.value=='Both')
									{
										if((document.forms[0].chargeFlatRate[i-3]!=null && document.forms[0].chargeFlatRate[i-3].value!=0))
										{
											alert('Please Enter Positive Value For Slab at Column '+(i+1)+' for ChargeDescriptionId: '+chargeIdValue);
											document.forms[0].chargeSlab[i].focus();
											return false;
										}
									}else
									{
										alert('Please Enter Positive Value For Slab at Column '+(i+1)+' for ChargeDescriptionId: '+chargeIdValue);
										document.forms[0].chargeSlab[i].focus();
										return false;
									}
								}
						}
					}
				}
			}
		}
 document.forms[0].Submit.disabled='true';
 return true;
}



function validateForm()
{
	toUpperCase();
  if(!checkAll())
	 return false;
 
  if(!validateChargeId())
     return false;

  if(!validateSlabs())
	 return false;

  return true;

}

function checkAll()
{
	var flag=true;
	var index	=	1;
  	if(idcounter>1 && counter>1)
	{
		for(index=1; index < idcounter; index++)
		{
			if(document.getElementById("chargeId"+(index))!=null)
			{
				if(document.getElementById("chargeId"+(index)).value.length ==0)
				{
					alert("Enter ChargeId");
					document.getElementById("chargeId"+(index)).focus();
					flag=false;
					break;
				}
				if(document.getElementById("chargeDescriptionId"+(index)).value.length ==0)
				{
					alert("Enter chargeDescriptionId");
					document.getElementById("chargeDescriptionId"+(index)).focus();
					flag=false;
					break;
				}
				else if(document.getElementById("chargeBasisId"+(index)).value.length ==0)
				{
					alert("Enter chargeBasisId");
					document.getElementById("chargeBasisId"+(index)).focus();
					flag=false;
					break;
				 }
				else if(document.getElementById("chargeCurrencyId"+(index)).value.length ==0)
				{
					alert("Enter Charge Currency");
					document.getElementById("chargeCurrencyId"+(index)).focus();
					flag=false;
					break;
				}
				else if(document.getElementById("rateBreak"+(index)).value.length ==0)
				{
					alert("Select rateBreak");
					document.getElementById("rateBreak"+(index)).focus();
					flag=false;
					break;
				}
				else if(document.getElementById("chargeRateType"+(index)).value.length ==0)
				{
					alert("Select chargeRateType");
					document.getElementById("chargeRateType"+(index)).focus();
					flag=false;
					break;
				}else if(document.getElementById("rateCalculation"+(index)).value.length ==0)
				{
					alert("Select rateCalculation");
					document.getElementById("rateCalculation"+(index)).focus();
					flag=false;
					break;
				}else if(document.getElementById("chargeBasisId"+(index)).value.length >0)
				{ 
                       if(!(document.getElementById("primaryUnit"+(index)).value==null||document.getElementById("primaryUnit"+(index)).value==''))
					{
						if(document.getElementById("primaryUnit"+(index)).value=='KG' || document.getElementById("primaryUnit"+(index)).value=='CBM' || document.getElementById("primaryUnit"+(index)).value=='LB' ||
						document.getElementById("primaryUnit"+(index)).value=='CFT'||primaryunit[index]=='CFT'||primaryunit[index]=='LB'||primaryunit[index]=='KG'||primaryunit[index]=='CBM')
						{
							
							 if(document.getElementById("densityGrpCode"+(index)).value.length ==0)
								{
									alert("Select DensityGroupCode for the lane "+index+" As primaryUnit:"+document.getElementById("primaryUnit"+(index)).value);
									document.getElementById("densityGrpCode"+(index)).focus();
									flag=false;
									break;
								}
								//@@Added by kameswari for the WPBN issue-54554
								else
							    {
								  var c1=0,c2=0;
								  if(document.getElementById("primaryUnit"+(index)).value=='KG' || document.getElementById("primaryUnit"+(index)).value=='CBM'||primaryunit[index]=='KG'||primaryunit[index]=='CBM')
									{
								   <%for(int i=0;i<densityGroupList.size();i=i+2)
									{%>
									   if(document.getElementById("densityGrpCode"+(index)).value=='<%=densityGroupList.get(i)%>')
										{
                                          c1++;
										
										}
									   <%}%>
										   if(c1==0)
									       {
									             alert("Please enter valid Density Group Code at lane:"+index);
												 flag=false;
									   }
									}
									else
									{
									 <%for(int j=1;j<densityGroupList.size();j=j+2)
									{%>
									   if(document.getElementById("densityGrpCode"+(index)).value=='<%=densityGroupList.get(j)%>')
										{
                                          c2++;
										
										}
									   <%}%>
										   if(c2==0)
									       {
									             alert("Please enter valid Density Group Code at lane:"+index);
												 flag= false;
									   }
									}
                  
								}
						}else
						{
							 if(document.getElementById("densityGrpCode"+(index)).value.length >0)
								{
									alert("deselect DensityGroupCode for the lane "+index+" As primaryUnit:"+document.getElementById("primaryUnit"+(index)).value);
									document.getElementById("densityGrpCode"+(index)).focus();
									flag=false;
									break;
								}
						}
					}
					
             //@@WPBN issue-54554
				}
			}

			if(!checkRateBreakForPS(index))
				return false;
		}
	}else
	{
				if(document.getElementById("chargeId"+(index)).value.length ==0)
				{
					alert("Enter ChargeId");
					document.getElementById("chargeId"+(index)).focus();
					flag=false;
				}
				if(document.getElementById("chargeDescriptionId"+(index)).value.length ==0)
				{
					alert("Enter chargeDescriptionId");
					document.getElementById("chargeDescriptionId"+(index)).focus();
					flag=false;
				}
				else if(document.getElementById("chargeBasisId"+(index)).value.length ==0)
				{
					alert("Enter chargeBasisId");
					document.getElementById("chargeBasisId"+(index)).focus();
					flag=false;
				 }
				else if(document.getElementById("chargeCurrencyId"+(index)).value.length ==0)
				{
					alert("Enter Charge Currency");
					document.getElementById("chargeCurrencyId"+(index)).focus();
					flag=false;
				}
				else if(document.getElementById("rateBreak"+(index)).value.length ==0)
				{
					alert("Select rateBreak");
					document.getElementById("rateBreak"+(index)).focus();
					flag=false;
				}
				else if(document.getElementById("chargeRateType"+(index)).value.length ==0)
				{
					alert("Select chargeRateType");
					document.getElementById("chargeRateType"+(index)).focus();
					flag=false;
				}else if(document.getElementById("rateCalculation"+(index)).value.length ==0)
				{
					alert("Select rateCalculation");
					document.getElementById("rateCalculation"+(index)).focus();
					flag=false;
				}else if(document.getElementById("chargeBasisId"+(index)).value.length >0)
				{
						if(!(document.getElementById("primaryUnit"+(index)).value==null||document.getElementById("primaryUnit"+(index)).value==''))
					{
						if(document.getElementById("primaryUnit"+(index)).value=='KG' || document.getElementById("primaryUnit"+(index)).value=='CBM' || document.getElementById("primaryUnit"+(index)).value=='LB' ||
						document.getElementById("primaryUnit"+(index)).value=='CFT'||primaryunit[index]=='KG'||primaryunit[index]=='CBM'||primaryunit[index]=='CFT'||primaryunit[index]=='LB')
						{
							 if(document.getElementById("densityGrpCode"+(index)).value.length ==0)
								{
									alert("Select DensityGroupCode for the lane "+index+" As primaryUnit:"+document.getElementById("primaryUnit"+(index)).value);
									document.getElementById("densityGrpCode"+(index)).focus();
									flag=false;
								}
								else
							    {
                  //@@Added by Kameswari for the WPBN issue-54554
								  var c1=0,c2=0;
								  if(document.getElementById("primaryUnit"+(index)).value=='KG' || document.getElementById("primaryUnit"+(index)).value=='CBM'||primaryunit[index]=='KG'||primaryunit[index]=='CBM')
									{
								   <%for(int i=0;i<densityGroupList.size();i=i+2)
									{%>
									   if(document.getElementById("densityGrpCode"+(index)).value=='<%=densityGroupList.get(i)%>')
										{
                                          c1++;
										 
										}
									   <%}%>
										   if(c1==0)
									       {
									             alert("Please enter valid Density Group Code at lane:"+index);
												 flag=false;
												
									   }
									}
									else
									{
									 <%for(int j=1;j<densityGroupList.size();j=j+2)
									{%>
									   if(document.getElementById("densityGrpCode"+(index)).value=='<%=densityGroupList.get(j)%>')
										{
                                          c2++;
										 
										}
									   <%}%>
										   if(c2==0)
									       {
									             alert("Please enter valid Density Group Code at lane:"+index);
												 flag=false;
									   }
									}
								}
                //@@WPBN issue-54554

						}else
						{
							 if(document.getElementById("densityGrpCode"+(index)).value.length >0)
								{
									alert("deselect DensityGroupCode for the lane "+index+" As primaryUnit:"+document.getElementById("primaryUnit"+(index)).value);
									document.getElementById("densityGrpCode"+(index)).focus();
									flag=false;
								}
						}
					}
				}
			if(!checkRateBreakForPS(index))
				return false;
	}
	return  flag;
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
function showCharges(chargeId,index)
{
		var operation	 = document.forms[0].operation.value;
		var termid		 = '<%=loginbean.getTerminalId()%>';	
	var GroupId	=	 document.getElementById("chargeGroupId"+index).value; ////added by VLAKSHMI for CR #170761 on 20090626
   //chbged by VLAKSHMI for CR #170761 on 20090626
Url='etrans/QMSLOVChargeIds.jsp?searchString='+document.getElementsByName(chargeId)[0].value+'&shipmentMode=&index='+index+'&Operation='+operation+'&teminalId='+termid+'&name='+chargeId+'&chargeGroupId='+GroupId+'&fromWhere=buycharges&selection=single';
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=400,height=360,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
}
function showChargeBasis(chargeBasisId,index)
{
		var operation	 = document.forms[0].operation.value;
		var termid		 = '<%=loginbean.getTerminalId()%>';
		//var	searchStr	 = '';
		 var	searchStr	  =document.getElementById("chargeBasisId"+index).value;	
		 
		 /*Url='etrans/QMSLOVChargeBasisIds.jsp?searchString='+searchStr+'&shipmentMode=&index='+index+'&Operation='+operation+'&teminalId='+termid+'&name='+chargeBasisId+'&fromWhere=buycharges&selection=single';
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=400,height=360,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);*/
		var tabArray = '';
		var formArray = '';
		var lovWhere	=	"";

		formArray = 'chargeBasisId'+index+',chargeBasisDescription'+index+',primaryUnit'+index+',secondaryUnit'+index;		
		
		tabArray = 'CHARGEBASIS,BASIS_DESCRIPTION,PRIMARY_BASIS,SECONDARY_BASIS';
		Url		//="qms/ListOfValues.jsp?lovid=CHARGEBASIS_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=BUYCHARGEBASIS&search=";  Commented By Sunil for SearchString. 
		="qms/ListOfValues.jsp?lovid=CHARGEBASIS_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=BUYCHARGEBASIS&search= where CHARGEBASIS LIKE'"+searchStr+"~'";


		Options	='width=750,height=750,resizable=yes';
//@@ Commented by subrahmanyam for the WPBN ISSUE:146436 on 12/12/2008
		//Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
//@@ Added by subrahmanyam for the WPBN ISSUE:146436 on 12/12/2008
		Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=1';
		Features=Bars+','+Options;

		Win=open(Url,'Doc',Features);
}
function openChargeDescIdsLOV(input)
{
	var id			=	input.id;
	var Bname		=	input.name;
	var index		=	id.substring(Bname.length);
	var name		=	"chargeDescriptionId"+index;
	var searchStr	=	document.getElementById(name).value;
	var chargeId	=	document.getElementById("chargeId"+index).value;
	var chargeGroupId= document.getElementById("chargeGroupId"+index).value;//@@Added by subrahmanyam for pbn id: 195270 on 20-Jan-10
	//alert(chargeGroupId)
	var fromWhere	=	"buycharge";

		if(chargeId=='')
		{ alert("Please Select chargeId");return false;}
		//@@Modified by subrahmanyam for pbn id: 195270 on 20-Jan-10
		var Url			= "etrans/QMSLOVDescriptionIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&selection=single&fromWhere="+fromWhere+"&chargeId="+chargeId+"&chargeGroupId="+chargeGroupId;
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=400,height=360,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
}
 //added by VLAKSHMI for CR #170761 on 20090626
function openChargeGroupIdLOV(input)
	{
	//var Win  = null;
	var id			=	input.id;
	var Bname		=	input.name;
	var index		=	id.substring(Bname.length);
	var name        = "chargeGroupId"+index;
	var searchStr	=	 document.getElementById(name).value;
	var chargeId	=	document.getElementById("chargeId"+index).value;
	var chargeIdDesc = document.getElementById("chargeDescriptionId"+index).value;
	var termid		 = '<%=loginbean.getTerminalId()%>';

		var Url			= "etrans/QMSLOVChargeGroupIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&fromWhere=chargeGroupEnterId&terminalId="+termid+"&chargeId="+chargeId+"&chargeIdDesc="+chargeIdDesc;
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=400,height=360,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
	}
function opendensityLOV(input)
{
	var id			=	input.id;
	var Bname		=	input.name;
	var index		=	id.substring(Bname.length);
	var name		=	"densityGrpCode"+index;
	var searchStr	=	document.getElementById(name).value;
	
	//var chargeId	=	document.getElementById("densityGrpCode"+index).value;
	var fromWhere	=	"buycharge";
    //alert(document.getElementById("primaryUnit"+index).value)
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
function addElementToTD(maxLength,flag,type,id,elementName,size,value,cellWidth,alignment,disable,clickEvent,blurEvent,className,focusEvent)
{
	myElement=document.createElement("input"); 
	myElement.setAttribute("onClick","")	//only ns
	myElement.setAttribute("name",elementName);
	myElement.setAttribute("type",type); 
	myElement.setAttribute("id", id+idcounter); 
	myElement.setAttribute("size", size); 
	myElement.setAttribute("value", value);
	if(maxLength!=null && maxLength!="")
	{
		myElement.setAttribute("maxLength",maxLength);
	}

	if(className!=null && className!="")
		myElement.className=className;

	if(disable=="true")
		myElement.readOnly="true"; 
	if(type=="button" && disable=="true")
		myElement.disabled="true";

	var myform=document.getElementsByTagName("form").item(0);
	myform.appendChild(myElement);

	if(clickEvent!=null && clickEvent!="")
	{
		myElement.setAttribute("eventType","click");
		if(document.all)
			myElement.attachEvent("onclick",eventHandler1);
		else
			myElement.setAttribute("onClick", "eventHandler('"+myElement.getAttribute("id")+"');");
	}

	if(blurEvent!=null && blurEvent!="")
	{
		if(document.all)
			myElement.attachEvent("onblur",eventHandler1);
		
		else
			myElement.setAttribute("onBlur", "eventHandler('"+myElement.getAttribute("id")+"');");
			myElement.setAttribute("eventType","blur");
	}

	/*if(focusEvent!=null && focusEvent!="")
	{
		if(document.all)
			myElement.attachEvent("onfocus",eventHandler1);
		else
			myElement.setAttribute("onFocus", "eventHandler('"+myElement.getAttribute("id")+"');");
		myElement.setAttribute("focusEvent","yes");
	}*/

	if(flag=="false")
		return myElement;

	myTableCell=document.createElement("TD");
	myTableCell.appendChild(myElement);
	myTableCell.setAttribute("align", alignment);
	
//	alert(myElement.getAttribute("name"));   
	return myTableCell;
}
var temp = 1;
var rateBreakTypes;
var rateBreakTypesValues;
var chargeSlabType;
var rateCalculation;
var rateCalculationValue;

/*<%
		if(operation.equals("Modify"))
		{
				if(buychargesHDRDOB!=null)
				{
%>
						rateBreakTypes=new Array('<%=rateBreak%>');
						rateBreakTypesValues=new Array('<%=rateBreak%>');
						chargeSlabType=new Array('<%=rateType%>');
						rateCalculation=new Array('<%=(("G".equals(weightClass))?"General":"WeightScale")%>');
						rateCalculationValue=new Array('<%=weightClass%>');
					
<%				}
		}else
		{
%>
				rateBreakTypes=new Array('','Flat','Flat%','Slab','Slab%','Absolute','Percent');
				rateBreakTypesValues=new Array('','Flat','Flat%','Slab','Slab%','Absolute','Percent');
				chargeSlabType=new Array("","Slab","Flat","Both");
				rateCalculation=new Array("General","WeightScale");
				rateCalculationValue=new Array("G","W");
<%
		}
%>*/
      //@@Modified by Kameswari for the WPBN issue-154698 on 20/02/09
     rateBreakTypes=new Array('','Flat','Flat%','Slab','Slab%','Absolute','Percent');
				rateBreakTypesValues=new Array('','Flat','Flat%','Slab','Slab%','Absolute','Percent');
				chargeSlabType=new Array("","Slab","Flat","Both");
				rateCalculation=new Array("General","WeightScale");
				rateCalculationValue=new Array("G","W");
function createNewTD(width,align,className,colspan)
{
	var myTableCell=document.createElement("TD");
	if(width!=null && width!="")
		myTableCell.setAttribute("width",width);
	if(align!=null && align!="")
		myTableCell.setAttribute("align",align);
	if(className!=null && className!="")
		myTableCell.className = className;
	if(colspan!=null && colspan!="")
		myTableCell.colSpan = colspan;	
	return myTableCell;
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

function checkLane()
{
	var flag=true;
	var idcount		=	1;
	var cnt	=	0;
		if(counter>1 && idcounter>1)
		{	
			for(i=1;i<idcounter;i++)
			{
				if(document.getElementById("chargeId"+i)!=null)
					{	cnt++;}
			}
		}
		if(cnt>=15)
		{
			alert("Maximum 15 lanes you can enter");
			return false;
		}
	if(event.srcElement!=null)
	{	idcount	=	event.srcElement.getAttribute("id").substring(event.srcElement.getAttribute("name").length);}

	if(idcounter >1 && counter>1)
	 {										
		if(document.getElementById("chargeId"+(idcount)).value.length ==0)
			flag=false;
		if(document.getElementById("chargeDescriptionId"+(idcount)).value.length ==0)
			flag=false;
	   	if(document.getElementById("chargeBasisId"+(idcount)).value.length ==0)
			flag=false;
		if(document.getElementById("chargeCurrencyId"+(idcount)).value.length ==0)
			flag=false;
		if(document.getElementById("rateBreak"+(idcount)).value.length ==0)
			flag=false;
		if(document.getElementById("chargeRateType"+(idcount)).value.length ==0)
			flag=false;
		if(document.getElementById("rateCalculation"+(idcount)).value.length ==0)
			flag=false;
		
		if(flag==false)
				alert("Enter lane "+(counter-1)+" data");
	}
	return  flag;
}
function createForm(myTableStr)
{
  {
  if(checkLane())
	{
		var myTable=document.getElementById(myTableStr);
		myTableBody=document.createElement("tbody");
		myTableBody.setAttribute("id","tbody"+idcounter);

		myTableRow=document.createElement("TR");
		myTableRow.className="formdata";
		myTableRow.setAttribute("valign","bottom");

		myTableCell=createNewTD(150,'');
    if(idcounter>1)
			myTableCell.appendChild(addElementToTD("","false","button","tbody","tbody","","<<","","left","false","deleteRow();","","input"));
		else
			myTableCell.appendChild(document.createTextNode('   '));
		myTableRow.appendChild(myTableCell); 
		myTableCell.appendChild(document.createTextNode(' '));
		
		myTableCell=createNewTD(150,'');	
	//@@Added by Yuvraj for WPBN-DHLQMS-22524 
	<%
		if("Add".equalsIgnoreCase(operation))
		{
    ////added by VLAKSHMI for CR #170761 on 20090626
%>			myTableCell.appendChild(addElementToTD("20","false","input","chargeGroupId","chargeGroupId","6","","","center","false","","YUP","text","yes"));

	myTableCell.appendChild(document.createTextNode(''));
	<%
		} %>
			<% if("Add".equals(operation)) { %>	
				myTableCell.appendChild(addElementToTD("","false","button","chargeGroupIdPop","chargeGroupIdPop","5","...","","left","false","yup","","input"));
		
		myTableRow.appendChild(myTableCell);

		myTableCell=createNewTD(200,'');
		<%}%> ////end for CR #170761
<%
		if("Add".equalsIgnoreCase(operation))
		{
%>			myTableCell.appendChild(addElementToTD("10","false","input","chargeId","chargeId","6","","","center","false","","YUP","text","yes"));
<%
		}
		else
		{//@@Yuvraj	
%>
			myTableCell.appendChild(addElementToTD("10","false","input","chargeId","chargeId","6","","","center","true","","YUP","text","yes"));	
<%		}
%>

		myTableCell.appendChild(document.createTextNode(''));
		<% if("Add".equals(operation)) { %>	
				myTableCell.appendChild(addElementToTD("","false","button","chargeIdPop","chargeIdPop","5","...","","left","false","yup","","input"));
		<%}%>
		myTableRow.appendChild(myTableCell);

		myTableCell=createNewTD(200,'');

		<% if("Add".equals(operation)) { %>
			myTableCell.appendChild(addElementToTD("","false","input","chargeDescriptionId","chargeDescriptionId","20","","50","center","false","","yup","text","yes"));
			myTableCell.appendChild(document.createTextNode(''));	myTableCell.appendChild(addElementToTD("","false","button","chargeDescIdPop","chargeDescIdPop","5","...","","left","false","yup","","input"));

		<%}else{%>
			myTableCell.appendChild(addElementToTD("","false","input","chargeDescriptionId","chargeDescriptionId","20","","50","center","true","","yup","text","yes"));		
			myTableCell.appendChild(document.createTextNode(' '));			myTableCell.appendChild(addElementToTD("","false","button","chargeDescIdPop","chargeDescIdPop","5","...","","left","true","yup","","input"));
		<%}%>
		myTableRow.appendChild(myTableCell);

		myTableCell=createNewTD(150,'center');
	
		/*myTableCell.appendChild(addElementToTD("10","false","input","chargeBasisId","chargeBasisId","6","","","center","true","","YUP","text","yes"));*/
		
	
<% if("Add".equals(operation)) { %>	myTableCell.appendChild(addElementToTD("10","false","input","chargeBasisId","chargeBasisId","6","","","center","false","","YUP","text","yes"));
<%}
else
		{%>
	myTableCell.appendChild(addElementToTD("10","false","input","chargeBasisId","chargeBasisId","6","","","center","false","","YUP","text","yes"));
<%}%>	myTableCell.appendChild(addElementToTD("10","false","hidden","primaryUnit","primaryUnit","6","","","center","","","","",""));
		myTableCell.appendChild(document.createTextNode(''));
		
		myTableCell.appendChild(addElementToTD("10","false","hidden","secondaryUnit","primaryUnit","6","","","center","","","","",""));
		myTableCell.appendChild(document.createTextNode(''));	
myTableCell.appendChild(addElementToTD("","false","button","chargeBasisIdPop","chargeBasisIdPop","5","...","","left","false","yup","","input"));

	
		myTableRow.appendChild(myTableCell);
		myTableRow.appendChild(addElementToTD("","true","input","chargeBasisDescription","chargeBasisDescription","20","","100","center","true","","yup","text","yes"));

		myTableCell=createNewTD(130,'center');
		//@@ || condition added by Yuvraj for WPBN-DHLQMS-22605
		<% if("Add".equals(operation) || "Modify".equalsIgnoreCase(operation)) { %> myTableCell.appendChild(addElementToTD("3","false","input","chargeCurrencyId","chargeCurrencyId","4","<%=loginbean.getCurrencyId()%>","","center","false","","yup","text","yes"));
		<%}else {%>
		myTableCell.appendChild(addElementToTD("3","false","input","chargeCurrencyId","chargeCurrencyId","4","<%=loginbean.getCurrencyId()%>","","center","true","","yup","text","yes"));
		<%}%>
		//@@ || condition added by Yuvraj for WPBN-DHLQMS-22605
		<% if("Add".equals(operation) || "Modify".equalsIgnoreCase(operation)) { %>
		myTableCell.appendChild(document.createTextNode(''));		myTableCell.appendChild(addElementToTD("","false","button","chargeCurrencyIdPop","chargeCurrencyIdPop","5","...","","left","false","yup","","input"));
       <%}%>
		myTableRow.appendChild(myTableCell);

		myTableRow.appendChild(addNewSelectWithTD('rateBreak','rateBreak',rateBreakTypes,rateBreakTypesValues,'select'));		
		myTableCell=createNewTD(100,'center');
		myTableCell.appendChild(addNewSelectWithTD('chargeRateType','chargeRateType',chargeSlabType,'','select',true));
		myTableRow.appendChild(myTableCell);
		 myTableCell=createNewTD(100,'center');
		myTableCell.appendChild(addNewSelectWithTD('rateCalculation','rateCalculation',rateCalculation,rateCalculationValue,'select',true));
		myTableRow.appendChild(myTableCell);

		myTableCell=createNewTD(150,'');
		myTableCell.appendChild(document.createTextNode(''));	/*naresh*/
		
		<% if("Add".equals(operation) || "Modify".equalsIgnoreCase(operation)) { %>	myTableCell.appendChild(addElementToTD("25","false","input","densityGrpCode","densityGrpCode","15","","","left","false","","YUP","text","yes"));	
		<%}else{%>
		myTableCell.appendChild(addElementToTD("25","false","input","densityGrpCode","densityGrpCode","15","","","center","true","","YUP","text","yes"));	
		<%}%>

		myTableCell.appendChild(document.createTextNode(''));	
		myTableRow.appendChild(myTableCell);		
	 myTableCell.appendChild(addElementToTD("","false","button","denistyPop","densityPop","5","...","","left","false","yup","","input"));
		
		
		myTableRow.appendChild(myTableCell);


		myTableCell=createNewTD(100,'left');
        /*<% if("Add".equals(operation)) { %>*/ //@@Commented by Kameswari for the WPBN issue-154398 on 20/02/09
				myTableCell.appendChild(addElementToTD("","false","button","addbut","addbut","10",">>","15","center","false","createForm('chargesTable');","","input"));
        /*<% } %>*/
		myTableRow.appendChild(myTableCell);

		myTableCell=createNewTD(100,'left','',2);
		myTableRow.appendChild(myTableCell);

		myTableBody.appendChild(myTableRow);

		myTableRow=document.createElement("TR");
		myTableRow.className="formdata";

		myTableCell=createNewTD('1000','left',"formdata",12)
		myTableCell=createSpan(myTableCell,idcounter);
		myTableRow.appendChild(myTableCell);
		myTableBody.appendChild(myTableRow);
		myTable.appendChild(myTableBody);
		//alert("Done");
		setSpanContent(idcounter);
		//alert(document.getElementById("spanSlabs"+idcounter));

		idcounter++;
		counter++;
    /*<% if("Add".equals(operation)) { %>*/ //@@Commented by Kameswari for the WPBN issue-154398 on 20/02/09
		if(counter>1 && idcounter>1)
		{
			for(i=1;i<=idcounter;i++)
			{
				if(document.getElementById('addbut'+i)!=null)
				{
					document.getElementById('addbut'+i).style.visibility="hidden"; 
					prevElement=i;
				}
			}
			document.getElementById('addbut'+prevElement).style.visibility="visible"; 
		}
   /* <% } %>*/
		if(document.getElementById('chargesContentDiv').style.display=="block")
			document.getElementById('chargeId'+(idcounter-1)).focus();
	}
}
}
function setSpanContent(idcounter)
{
	//alert("Hi")
	if(document.getElementById("spanSlabs"+idcounter)!=null)
	{
		var rateBreakObj = document.getElementById("rateBreak"+idcounter);
		var rateTypeObj = document.getElementById("chargeRateType"+idcounter);
		if(rateTypeObj!=null && rateTypeObj!=null)
		{
			var spanContent = "";
			if(rateTypeObj.selectedIndex == -1)
						return false;
			
		if(rateBreakObj.options[rateBreakObj.selectedIndex].value=="Slab" || rateBreakObj.options[rateBreakObj.selectedIndex].value=="Slab%" )
        {
					chargeRateSpan.innerHTML = '';
					if(rateTypeObj.options[rateTypeObj.selectedIndex].value=="Both")
					{	spanContent = showBoth(idcounter);}
					else
					{	spanContent = showSlab(idcounter);}
        }
        else if(rateBreakObj.options[rateBreakObj.selectedIndex].value=="Flat" || rateBreakObj.options[rateBreakObj.selectedIndex].value=="Flat%")
        {
					chargeRateSpan.innerHTML = '';
					spanContent = showFlat(idcounter);
        }
        else if(rateBreakObj.options[rateBreakObj.selectedIndex].value=="Percent" || rateBreakObj.options[rateBreakObj.selectedIndex].value=="Absolute")
        {
					chargeRateSpan.innerHTML = '';
					spanContent = showAbsValue(idcounter);
        }
			document.getElementById("spanSlabs"+idcounter).innerHTML=spanContent;
			document.getElementById("spanSlabs"+idcounter).style.display='none';
		}
	}
}

function createSpan(myObj,spanIdCounter)
{
	var mySpan = document.createElement("<SPAN id=spanSlabs"+spanIdCounter+"></SPAN>");
	myObj.appendChild(mySpan);
	return myObj;
}

function addNewSelectWithTD(elementName,id,optionsList,optionsValues,className,tdFlag)
{
	var myTableCell=document.createElement("TD");
	myTableCell.setAttribute("align","center");
	myTableCell.setAttribute("width","100");
	myElement=document.createElement("select");
	myElement.setAttribute("name",elementName);
	myElement.setAttribute("id", id+idcounter); 
	for(i=0;i<optionsList.length;i++)
	{
		if(optionsValues!=null && optionsValues!="")
			myElement.appendChild(addNewOption(optionsList[i],optionsValues[i]));
		else
			myElement.appendChild(addNewOption(optionsList[i]));
	}
	myElement.className = className;

	if(document.all)
		myElement.attachEvent("onchange",eventHandler1);
	else
		myElement.setAttribute("onChange", "eventHandler('"+myElement.getAttribute("id")+"');");
	myElement.setAttribute("eventType","onChange");

	if(tdFlag)
		return myElement;
	myTableCell.appendChild(myElement);
	return myTableCell;
}
function addNewOption(optionStr,optionValue)
{
	var myOption=document.createElement("option");
	myOption.setAttribute("innerText",optionStr);
	if(optionValue!=null && optionValue!="")
		myOption.setAttribute("value",optionValue);
	else
		myOption.setAttribute("value",optionStr);
	myOption.text=optionStr;
	return myOption;
}
function eventHandler1()
{	

	var tmpId=event.srcElement.getAttribute("id").substr(6); 

	var evtObj = event.srcElement;
	var tmpCurrentId = event.srcElement.getAttribute("id").substr(event.srcElement.getAttribute("name").length);

	if(event.srcElement.getAttribute("eventType")=="click")
	{	
		if(event.srcElement.getAttribute("id").substr(0,6)=="addbut")
		{	createForm('chargesTable');}
		else if(event.srcElement.getAttribute("id").substr(0,11)=="chargeIdPop")
		{
			showCharges("chargeId"+tmpCurrentId,tmpCurrentId);
		}		
		else if(event.srcElement.getAttribute("id").substr(0,19)=="chargeCurrencyIdPop")
		{
			showCurrencies("chargeCurrencyId"+tmpCurrentId,tmpCurrentId);
		}else if((event.srcElement.getAttribute("id").substr(0,16)=="chargeBasisIdPop"))
		{
			showChargeBasis("chargeBasisId"+tmpCurrentId,tmpCurrentId);
		}
		else if((event.srcElement.getAttribute("name")=="chargeDescIdPop"))
		{
			openChargeDescIdsLOV(event.srcElement);
		}
     //added by VLAKSHMI for CR #170761 on 20090626
		else if((event.srcElement.getAttribute("name")=="chargeGroupIdPop"))
		{
			openChargeGroupIdLOV(event.srcElement);
		}
		else if((event.srcElement.getAttribute("name")=="densityPop"))/*naresh*/
		{
			opendensityLOV(event.srcElement);
		}else
		{ deleteRow(event.srcElement.getAttribute("id"));}
	}
	else if(event.srcElement.getAttribute("eventType")=="onChange")
	{
		
		var chargeidvalue = document.getElementById("chargeId"+tmpCurrentId);
		var rateBreak		= document.getElementById("rateBreak"+tmpCurrentId);
		var rateBreakvalue	= rateBreak.value;
		
		if(event.srcElement.getAttribute("name")=="rateBreak")
		{
			myObj = document.getElementById("spanSlabs"+tmpCurrentId);
            document.getElementById("chargeRateType"+tmpCurrentId).length = 0;
			document.getElementById("rateCalculation"+tmpCurrentId).length = 0;
			//setRateBreakForPS(tmpCurrentId);
			if(rateBreakvalue == '')
				spanContent = '';
			if(rateBreakvalue == 'Flat' || rateBreakvalue == 'Flat%')
			{
				spanContent = showFlat(tmpCurrentId);
				if(!validateChargeId())
				{
					 rateBreak.value = "";
					 return false;
				}
				if( chargeidvalue.value == '')
				{
					alert("Please Select ChargeId.");
					rateBreak.value = "";
					chargeidvalue.focus();
					return false;
				}
				AddToOptionList(document.getElementById("chargeRateType"+tmpCurrentId), "Flat", "Flat", true );
				AddToOptionList(document.getElementById("rateCalculation"+tmpCurrentId), "G","General",true);
			}
	        if(rateBreakvalue == 'Absolute' || rateBreakvalue == 'Percent')
			{
				spanContent = showAbsValue(tmpCurrentId);
				if(!validateChargeId())
				{
				  rateBreak.value = "";
				  return false;
				 }
				if( chargeidvalue.value == '')
				{
					alert("Please Select ChargeId.");
					rateBreak.value = "";
					chargeidvalue.focus();
					return false;
				}
				AddToOptionList(document.getElementById("chargeRateType"+tmpCurrentId), "Flat", "Flat", true );
				AddToOptionList(document.getElementById("rateCalculation"+tmpCurrentId), "G","General",true);
			}
			 if((rateBreakvalue == 'Slab' || rateBreakvalue == 'Slab%'))
			{
				spanContent = showSlab(tmpCurrentId);
				if(!validateChargeId())
				{
				  rateBreak.value = "";
				  return false;
				 }
				if( chargeidvalue.value == '')
				{
					alert("Please Select ChargeId.");
					rateBreak.value = "";
					chargeidvalue.focus();
					return false;
				}

				if(rateBreakvalue == 'Slab%')
				{
					AddToOptionList(document.getElementById("chargeRateType"+tmpCurrentId), "Slab", "Slab", false );
				}
				else
				{
					AddToOptionList(document.getElementById("chargeRateType"+tmpCurrentId), "Flat", "Flat", false );
					AddToOptionList(document.getElementById("chargeRateType"+tmpCurrentId), "Slab", "Slab", true );
					AddToOptionList(document.getElementById("chargeRateType"+tmpCurrentId), "Both", "Both", false );
				}
				AddToOptionList(document.getElementById("rateCalculation"+tmpCurrentId), "G","General",true);
				AddToOptionList(document.getElementById("rateCalculation"+tmpCurrentId), "W","WeightScale",false);
			}
			document.getElementById("spanSlabs"+tmpCurrentId).innerHTML=spanContent;
			//setSpanContent(tmpCurrentId);
			myObj.style.display = "block";
			
		}
		else if(event.srcElement.getAttribute("name")=="chargeRateType")
		{
			var chargeRateType	= document.getElementById("chargeRateType"+tmpCurrentId);
			myObj = document.getElementById("spanSlabs"+tmpCurrentId);
			var chargeRateTypeValue	=	chargeRateType.value;
			if((chargeRateTypeValue == 'Slab' || chargeRateTypeValue == 'Flat'))
			{
				spanContent = showSlab(tmpCurrentId);
				if(!validateChargeId())
				{
				  rateBreak.value = "";
				  return false;
				 }
				if(chargeidvalue.value == '')
				{
					alert("Please Select ChargeId.");
					rateBreak.value = "";
					chargeidvalue.focus();
					return false;
				}
				if(rateBreak.value=='')
				{
					alert("Please,select rateType");
					rateBreak.focus();
					return false;
				}
			}
			if((chargeRateTypeValue == 'Both'))
			{
				spanContent = showBoth(tmpCurrentId);
				if(!validateChargeId())
				{
				  rateBreak.value = "";
				  return false;
				 }
				if( chargeidvalue.value == '')
				{
					alert("Please Select ChargeId.");
					rateBreak.value = "";
					chargeidvalue.focus();
					return false;
				}
				if(rateBreak.value=='')
				{
					alert("Please,select rateType");
					rateBreak.focus();
					return false;
				}
			}
			document.getElementById("spanSlabs"+tmpCurrentId).innerHTML=spanContent;
			//setSpanContent(tmpCurrentId);
			myObj.style.display = "block";			

		}
		else
		{
				if(!validateChargeId())
				{
				  rateBreak.value = "";
				  return false;
				 }
				if( chargeidvalue.value == '')
				{
					alert("Please Select ChargeId.");
					rateBreak.value = "";
					chargeidvalue.focus();
					return false;
				}
				if(rateBreak.value=='')
				{
					alert("Please,select rateType");
					rateBreak.focus();
					return false;
				}
				setSpanContent(tmpCurrentId);
				myObj = document.getElementById("spanSlabs"+tmpCurrentId);
				myObj.style.display = "block";
		}
	}
	else if(event.srcElement.getAttribute("eventType")=="blur")
	{
		//write any code for onblur event...I.V.Sekhar Merrinti
    event.srcElement.value = event.srcElement.value.toUpperCase();
	
		if(event.srcElement.getAttribute("name")=="densityGrpCode")
		{
			checkNumbers(event.srcElement,"densityGrpCode");
		}
		if(event.srcElement.getAttribute("name")=="chargeBasisId")
		{
			
          <% if(chargeBasisList!=null)
			{
			  for(int i=0;i<chargeBasisList.size();i=i+2)
		 {
				 %>
					 
		  if(document.getElementById("chargeBasisId"+tmpCurrentId).value=='<%=chargeBasisList.get(i)%>')
		 {
			/*if(document.getElementById("primaryUnit")!=null)
			 {*/

			//primaryunit[temp] = '<%=chargeBasisList.get(i+1)%>';
      		primaryunit[tmpCurrentId] = '<%=chargeBasisList.get(i+1)%>'; //@@Modified by vlakshmi for the WPBN issue
			document.getElementById("primaryUnit"+tmpCurrentId).value =primaryunit[tmpCurrentId] ;//@@Added by Kameswari for internal issue on 15/10/08
			 temp++;
			// }
		
		 }
		 
	
		  <%}
			}%>
		 
		  }
	
	}
	return;
}

function checkRateBreakForPS(tmpCurrentId)
{

	var chargeidvalue	=	document.getElementById("chargeId"+tmpCurrentId);
	var rateBreak		=	document.getElementById("rateBreak"+tmpCurrentId);
	var rateType		=	document.getElementById("rateType"+tmpCurrentId);
	var primaryUnit		=	document.getElementById("primaryUnit"+tmpCurrentId);
	var secondaryUnit	=	document.getElementById("secondaryUnit"+tmpCurrentId);


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
	
	if(rateBreak.value=='Percent')
	{
		if(primaryUnit.value!=null&&primaryUnit.value!='')
    {
      
      if(primaryUnit.value.toUpperCase()=='SHIPMENT' || primaryUnit.value.toUpperCase()=='VALUE')
      {
        if(primaryUnit.value.toUpperCase()=='SHIPMENT' && secondaryUnit.value.length == 0)
        {
          alert('The Secondary Basis of the selected Charge Basis should be defined in order to select Rate Break Percent.\nPlease change the Rate Break in lane :'+tmpCurrentId);
          rateBreak.value='';
          return false;
        }
      }
      else
      {
        alert("The Primary Basis of the selected Charge Basis should be either 'Per Shipment' or 'Value' in order to select Rate Break Percent.\n Please change the Rate Break in lane :"+tmpCurrentId);
        rateBreak.value='';
        return false;
      }
    }
	}

	return true;
}
function showFlat(idcounter)
	{
		var chargeIdValue = ""
		var data = "";
		var chargeObj = document.getElementById("chargeDescriptionId"+idcounter);
		var chargeIdObj = document.getElementById("chargeId"+idcounter);

		if(chargeObj!=null && chargeObj.value!=0 && chargeObj.value!="")
		{
			chargeIdValue = chargeObj.value;
			data = "" +"<table width='900' border='0' cellspacing='1' cellpadding='4'>"+
						"<tr class='formdata' colspan='13' valign='Top'><td rowspan=2 width='10%'><b>ChargeId: <br>"+chargeIdObj.value+"</b></td></td>"+
						"<input type=hidden name='chargelen' id='chargelen"+idcounter+"' value='4'>"+
						"<td width='10%'><b>BASE</b><input type=hidden name='chargeSlab' value='BASE'></td>"+
						"<td width='10%'><b>MIN</b><input type=hidden name='chargeSlab' value='MIN'></td>"+
						"<td width='10%'><b>MAX</b><input type=hidden name='chargeSlab' value='MAX'></td>"+
						"<td width='70%'><b>FLAT</b><input type=hidden name='chargeSlab' value='Flat'></td></tr>"+
						"<tr class='formdata' colspan='13' valign='Top'>"+
						"<td width='10%'><input type='text' class='text' name='chargeRate' id='chargeRate"+idcounter+"@"+1+"' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
						"<td width='10%'><input type='text' class='text' name='chargeRate' id='chargeRate"+idcounter+"@"+2+"' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
						"<td width='10%'><input type='text' class='text' name='chargeRate' id='chargeRate"+idcounter+"@"+3+"' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
						"<td width='50%'><input type='text' class='text' name='chargeRate' id='chargeRate"+idcounter+"@"+4+"' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td></tr></table>";
		 }
		return data;
	}
 function showAbsValue(idcounter)
	{
		var chargeIdValue = ""
		var data = "";
		var chargeObj = document.getElementById("chargeDescriptionId"+idcounter);
		var chargeIdObj = document.getElementById("chargeId"+idcounter);
		var spaces		=	"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		if(chargeObj!=null && chargeObj.value!=0 && chargeObj.value!="")
		{
			chargeIdValue = chargeObj.value;

			data = data +"<table width='900' border='0' cellspacing='1' cellpadding='4'>"+
						"<tr class='formdata'  colspan='13' valign='Top'><td rowspan=2 width='10%'><b>ChargeId: <br>"+chargeIdObj.value+"</b></td></td>"+
						"<input type=hidden name='chargelen' id='chargelen"+idcounter+"' value='1'>"+
						"<td width='10%' ><b>FLAT</b><input type=hidden name='chargeSlab' value='AbsRPersent'></td></tr>"+
						"<tr class='formdata' colspan='13' valign='Top'>"+
						"<td width='80%'><input type='text' class='text' id='chargeRate"+idcounter+"' name='chargeRate' value='' size=3 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+ 
						"<td></td></tr></table>";
	  }
		return data;
	}
function showSlab(idcounter)
	{
		var chargeIdValue = ""
	var deptIdValue = ""
	var data				 = "";
	var chargeObj		 = document.getElementById("chargeDescriptionId"+idcounter);
	var chargeIdObj = document.getElementById("chargeId"+idcounter);
	var i=0
	if(chargeObj!=null && chargeObj.value!=0 && chargeObj.value!="")
	{
		chargeIdValue = chargeObj.value;

		data = data +"<table width='900' border='0' cellspacing='0' cellpadding='4'>"+
					"<tr class='formdata' colspan='13' valign='Top'><td rowspan=2 width='10%'><b>ChargeId: <br>"+chargeIdObj.value+"</b></td></td>"+
					"<input type=hidden name='chargelen' id='chargelen"+idcounter+"' value='14'>"+
					"<td width='6%'><b>BASE</b><input type=hidden name='chargeSlab' value='BASE'></td>"+
					"<td width='6%'><b>MIN</b><input type=hidden name='chargeSlab' value='MIN'></td>"+
					"<td width='6%'><b>MAX</b><input type=hidden name='chargeSlab' value='MAX'></td>"+
					"<td width='6%'><input type='text' class='text' name='chargeSlab' value='' size=4  maxLength='6' onBlur='checkNumbers1(this)' ></td>";
					for(i=0;i<10;i++)
					{
						data=data+"<td width='6%'><input type='text' class='text' name='chargeSlab' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>";
					}
					data	=	data+"</tr>"+
									"<tr class='formdata' colspan='13' valign='Top'>";
					for(i=0;i<14;i++)
					{
						data	=	data+"<td width='6%'><input type='text' class='text' name='chargeRate' value='' size=4 maxLength='10'  onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>";
					}
					data	=	data+"<td></td></tr></table>";
		}
    return data;
	}
function showBoth(idcounter)
{
	var chargeIdValue = ""
	var deptIdValue = ""
	var data		= "";
	var chargeObj		 = document.getElementById("chargeDescriptionId"+idcounter);
	var chargeIdObj = document.getElementById("chargeId"+idcounter);

	if(chargeObj!=null && chargeObj.value!=0 && chargeObj.value!="")
	{
		chargeIdValue = chargeObj.value;

		data = data +"<table width='900' border='0' cellspacing='0' cellpadding='4'>"+
					"<tr class='formdata' colspan='13' valign='Top'><td rowspan=2 width='8%'><b>ChargeId: <br>"+chargeIdObj.value+"</b></td></td>"+
					"<input type=hidden name='chargelen'  id='chargelen"+idcounter+"' value='14'>"+
					"<td width='5%'><b>BASE</b><input type=hidden name='chargeSlab' value='BASE'></td>"+
					"<td width='5%'><b>MIN</b><input type=hidden name='chargeSlab' value='MIN'></td>"+
					"<td width='5%'><b>MAX</b><input type=hidden name='chargeSlab' value='MAX'></td>"+
					"<td width='4%'><input type='text' class='text' name='chargeSlab' value='' size=4  onBlur='checkNumbers1(this)' ></td>";
					for(i=0;i<10;i++)
					{
						data=data+"<td width='6%'><input type='text' class='text' name='chargeSlab' value='' size=4 onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>";
					}
					data	=	data+"</tr>"+
								"<tr class='formdata' colspan='13' valign='Top'>"+
								"<td width='5%'><input type='text' class='text' name='chargeRate' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
								"<td width='5%'><input type='text' class='text' name='chargeRate' value='' size=4 maxLength='10' onKeyPress='return 	getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>"+
								"<td width='5%'><input type='text' class='text' name='chargeRate' value='' size=4 maxLength='10' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>";
					for(i=1;i<12;i++)
					{
						data=data+"<td width='4%'><input type='text' class='textHighlight' name='chargeFlatRate' id='chargeFlatRate"+idcounter+"@"+i+"' value='' size=1 maxLength='10' onKeyPress='clearDataOnFocus(this);return getDotNumberCode(this);' onpaste='return false;' autocomplete='off' ><input type='text' class='text' name='chargeRate' id='chargeRate"+idcounter+"@"+i+"' value='' size=3 maxLength='10'  onKeyPress='clearDataOnFocus(this);return getDotNumberCode(this);' onpaste='return false;' autocomplete='off'></td>";
					}
					data	=	data+"<td></td></tr></table>";
		}
    return data;
}

function AddToOptionList(OptionList, OptionValue, OptionText,defSel)
{
   // Add option to the bottom of the list
   OptionList[OptionList.length] = new Option(OptionText, OptionValue,"",defSel);
}

function clearDataOnFocus(input)
{
	var index	=	input.id.substring(input.name.length);
	if(input.name=='chargeFlatRate')
	{	document.getElementById("chargeRate"+index).value='';}
	else if(input.name=='chargeRate')
	{	document.getElementById("chargeFlatRate"+index).value='';}
}
function deleteRow(str)
{
	var myTable=document.getElementsByTagName("table").item(0);
	if(document.all)
	{
			var tbody=document.getElementById(str);
			tbody.removeNode(true);
	}
	else
	{
		var tbody=document.getElementById(str);
		myTable.removeChild(tbody);
	}
	//idcounter--;
	counter--;

	for(i=1;i<=idcounter;i++)
	{
		if(document.getElementById('addbut'+i)!=null)
			prevElement=i;
	}
	if(document.getElementById('addbut'+prevElement)!=null)
		document.getElementById('addbut'+prevElement).style.visibility="visible"; 
}
function assaignValues()
{
		document.getElementById("chargeId1").value='<%=chargeId%>';
		document.getElementById("chargeDescriptionId1").value='<%=chargeDesc%>';
		document.getElementById("chargeBasisId1").value='<%=chargeBasisId%>';
   
		document.getElementById("chargeBasisDescription1").value='<%=chargeBasisDesc%>';
		document.getElementById("chargeCurrencyId1").value='<%=currencyId%>';
		document.getElementById("rateBreak1").value='<%=rateBreak%>';
		document.getElementById("chargeRateType1").value='<%=rateType%>';
		document.getElementById("rateCalculation1").value='<%=weightClass%>';
		document.getElementById("densityGrpCode1").value='<%=densityRatio!=null?densityRatio:""%>';
		setSpanContent(1);
		myObj = document.getElementById("spanSlabs1");
		myObj.style.display = "block";
		if('Percent'=='<%=rateBreak%>' || 'Absolute'=='<%=rateBreak%>')
			{	
				document.getElementById("chargeRate1").value	=	'<%=flat%>';
			}
		else if('Flat'=='<%=rateBreak%>' || 'Flat%'=='<%=rateBreak%>')
			{	
				document.getElementById("chargeRate1@1").value	=	'<%=(base!=null)?base:""%>';
				document.getElementById("chargeRate1@2").value	=	'<%=min%>';
				document.getElementById("chargeRate1@3").value	=	'<%=(max!=null)?max:""%>';
				document.getElementById("chargeRate1@4").value	=	'<%=flat%>';
			}
		else if('Slab'=='<%=rateBreak%>' || 'Slab%'=='<%=rateBreak%>')
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
					if('Both'=='<%=rateType%>')
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

function initialize()
{
  createForm('chargesTable');
<%
	if(operation.equals("Modify"))
	{
%>
		assaignValues();
<%
	 }
 %>	
}
//end of dyn table script
</SCRIPT>
</head>	 
<body bgcolor="#FFFFFF" onLoad="initialize();">
<form method="post" name=webform id=form1 action="QMSBuyChargesController"  onSubmit="return validateForm()">
<div id="mainDiv" style="visibility:visible;position:absolute;">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr >
<td bgcolor="#FFFFFF">
<table width="100%" border="0" cellspacing="1" cellpadding="4">
	<tr class='formlabel' > 
	<td ><table width="100%" border="0" ><tr class='formlabel'><td>Buy Charges - <%=operation%></b></td><td align=right>QS1050521</td></tr></table></td></tr></table>
  <table width="100%" border="0" cellspacing="1" cellpadding="4">
  <tr class="formdata"><td colspan="13" >&nbsp;</td></tr>
  <tr class="formdata"> 
	<td colspan="13" >
	</td>
  </tr>
</table>
<div id=chargesContentDiv style="display='block';">
<table id="chargesTable" width="100%" border="0" cellspacing="1" cellpadding="4">
  <tr class='formheader'>
  <td></td>
 <% if("Add".equalsIgnoreCase(operation))
		{ //added by VLAKSHMI for CR #170761 on 20090626
%>		
  <td><b>Charge Group Id:</b></td>
 <%} %>
	<td><b>Charge:<font color="#FF0000">*</font></b></td>
	<td><b>Charge Description Id:<font color="#FF0000">*</font></b></td><!-- increase in 100 width -->
	<td><b>Charge Basis:<font color="#FF0000">*</font></b></td>
	<td><b>Description:</b></td>
	<td><b>Currency:<font color="#FF0000">*</font></b></td>
	<td><b>Rate Break:<font color="#FF0000">*</font></b></td>
	<td><b>Rate Type:<font color="#FF0000">*</font></b></td>
	<td><b>Weight Class:<font color="#FF0000">*</font></b></td>
	<td><b>Density Group Code:</b></td>  <!-- naresh -->
	<td colspan=3 width=150>&nbsp;</td>
  </tr>  
</table>
</div>
<span id='chargeRateSpan'></span>
<table width="100%" border="0" cellspacing="1" cellpadding="4">
          <tr  class='denotes'> 
			<td><font color="#FF0000">*</font>Denotes Mandatory<br>
			</td>
            <td align="right">

			<input type="hidden" name="Operation" id="operation" value="<%=operation%>">
			<input type="hidden" name="subOperation" id="subOperation" value="<%=operation%>">
			<input type="hidden" name="terminalId" id="terminalId" value="<%=dataTerminalId%>">
			<input id='submit1' type='submit' name='Submit' value='Submit' class='input'>
			<input type=reset value=Reset class='input'>		
            </td>
          </tr>
        </table>
</td></tr>
</table>  
</form>
</div>
</body>
</html>
<%	}catch(Exception e)
	{
		//Logger.error(FILE_NAME,"exeption ----->"+e);
    logger.error(FILE_NAME+"exeption ----->"+e);
		if(operation!=null && operation.equals("Add"))
		{
			errorMessage	= "Exception While Loading the page";
			nextNavigation	= "QMSBuyChargesController?Operation="+operation+"&subOperation=";				
		}
		else
		{
			errorMessage	= "Exception While Modifying the data";
			nextNavigation	= "etrans/QMSBuyChargesMasterEnterId.jsp?Operation="+operation+"&subOperation=";
		}
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
