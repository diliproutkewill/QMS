<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSBuyChargesMasterEnterId.jsp
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
	private static final String FILE_NAME="QMSBuyChargesMasterDetails.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String operation		=	request.getParameter("Operation");
	String terminalId		=	loginbean.getTerminalId();
	String chargeId			=	"";
	String base				=	"";
	String min				=	"";
	String max				=	"";
	String flat				=	"";
	String[] chargeSlab		=	new String[14];
	String[] chargeRate		=	new String[14];
	String[] chargeFlatRate	=	new String[14];
	String   readOnly		=	"";
	String   disabled		=	"";
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	String      nextNavigation		=	null;
	BuychargesHDRDOB buychargesHDRDOB	=	null;
	BuychargesDtlDOB buychargesDtlDOB	=	null;
	ArrayList		 dtlList			=	null;
	//Logger.info(FILE_NAME,"--------------->");
	try{
		if(operation.equals("View") || operation.equals("Delete"))
		{
			readOnly		=	"readOnly";
			disabled		=	"disabled";
		}
		buychargesHDRDOB	=	(BuychargesHDRDOB)request.getAttribute("BuyChargesHDRDtls");
		if(buychargesHDRDOB==null)
		{
			throw new Exception("No data found");
		}
%>
<html>
<head>
<title>BuyCharges <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="html/eSupply.js"></script>
<script >
function viewContinue()//added by rk
{
	//document.forms[0].action='etrans/QMSBuyChargesMasterEnterId.jsp?Operation=View';
	document.forms[0].action='QMSBuyChargesController?Operation=View&subOperation=';//@@Modified by Kameswari for the WPBN issue-103279
	document.forms[0].submit();

	return true;
}
</script>
</head>
<body>
<form name="buychargesenter" method="post" action="QMSBuyChargesController" onSubmit ='return mandatory()'>
<table width="940" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="940" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="790" >
					<tr valign="top" class="formlabel">
						<td> BuyCharge - <%=operation%> </td>
						<td align=right >QS1050522
						</td></tr></table>
			</td>
		  </tr>
		</table>
		<table id="chargesTable" width="940" border="0" cellspacing="1" cellpadding="4">
		  <tr class='formheader'>
		  <td></td>
			<td width="190" align=center><b>Charge:*</b></td>
			<td width="150" align=center><b>Description:</b></td>
			<td width="150" align=center><b>ChargeBasis:*</b></td>
			<td width="150" align=center><b>Description:</b></td>
			<td width="100" align=center><b>Currency:*</b></td>
			<td width="100" align=center><b>Rate Break:*</b></td>
			<td width="100" align=center><b>Rate Type:*</b></td>
			<td width="100" align=center><b>Weight Class:*</b></td>
			<td colspan=3 width=50><b>DensityRatio:<b></td>
		  </tr>
		  <tr class='formdata'>
			<td></td>
			<td width="190" align=center><%=buychargesHDRDOB.getChargeId()%><input type='hidden' name='chargeId' id='chargeId1' value='<%=buychargesHDRDOB.getChargeId()%>'></td>
			<td width="100" align=center><%=buychargesHDRDOB.getChargeDescId()%><input type='hidden' name='chargeDescription' id='chargeDescription1' value='<%=buychargesHDRDOB.getChargeDescId()%>'></td>
			<td width="150" align=center><%=buychargesHDRDOB.getChargeBasisId()%><input type='hidden' name='chargeBasisId' id='chargeBasisId1' value='<%=buychargesHDRDOB.getChargeBasisId()%>'></td>
			<td width="100" align=center><%=buychargesHDRDOB.getChargeBasisDesc()%><input type='hidden' name='chargeBasisDescription' id='chargeBasisDescription1' value='<%=buychargesHDRDOB.getChargeBasisDesc()%>'></td>
			<td width="100" align=center><input type='text' class='text' name='chargeCurrencyId' id='chargeCurrencyId1' <%=readOnly%> size='4' value = '<%=buychargesHDRDOB.getCurrencyId()%>' <%=readOnly%>>
			</td>
			<td width="100" align=center><select  class='select' name='rateBreak' id='rateBreak1' >
				<option value='<%=buychargesHDRDOB.getRateBreak()%>'><%=buychargesHDRDOB.getRateBreak()%></option></td>
			<td width="100" align=center><select  class='select' name='chargeRateType' id='chargeRateType1'>
				<option value='<%=buychargesHDRDOB.getRateType()%>'><%=buychargesHDRDOB.getRateType()%></option></td>
			<td width="100" align=center><select  class='select' name='chargeRateType' id='chargeRateType1'>
				<option value='<%=buychargesHDRDOB.getWeightClass()%>'><%=(buychargesHDRDOB.getWeightClass()!=null && (buychargesHDRDOB.getWeightClass().equals("G"))?"General":"WeightScale")%></option></td>
			<td colspan=3 width=150><%=(buychargesHDRDOB.getDensityGrpCode()!=null)?buychargesHDRDOB.getDensityGrpCode():""%></td>
		  </tr>
<%
			dtlList	=	buychargesHDRDOB.getBuyChargeDtlList();
			chargeId	=	buychargesHDRDOB.getChargeId();
			if(dtlList!=null && dtlList.size()>0)
			{
				if("Percent".equals(buychargesHDRDOB.getRateBreak()) || "Absolute".equals(buychargesHDRDOB.getRateBreak()))
				{
					buychargesDtlDOB	=	(BuychargesDtlDOB)dtlList.get(0);
%>
					<table width='940' border='0' cellspacing='1' cellpadding='4'>
						<tr class='formdata'  colspan='13' valign='Top'><td rowspan=2 width='10%'><b>ChargeId: <br><%=buychargesHDRDOB.getChargeId()%></b></td></td>
						<td width='10%' ><b>FLAT</b><input type=hidden name='chargeSlab' value='AbsRPersent'></td><td></td></tr>
						<tr class='formdata' colspan='13' valign='Top'>
						<td width='80%'><input type='text' class='text' id='chargeRate1' name='chargeRate' value='<%=buychargesDtlDOB.getChargeRate()%>' size=5 <%=readOnly%> ></td>
						<td></td></tr></table>
<%
				}else if("Flat".equals(buychargesHDRDOB.getRateBreak()) || "Flat%".equals(buychargesHDRDOB.getRateBreak()))
				{
%>
					<table width='940' border='0' cellspacing='1' cellpadding='4'>
						<tr class='formdata' colspan='13' valign='Top'><td rowspan=2 width='10%'><b>ChargeId: <br><%=chargeId%></b></td></td>
						<td width='10%'><b>BASE</b><input type=hidden name='chargeSlab' value='BASE'></td>
						<td width='10%'><b>MIN</b><input type=hidden name='chargeSlab' value='MIN'></td>
						<td width='10%'><b>MAX</b><input type=hidden name='chargeSlab' value='MAX'></td>
						<td width='70%'><b>FLAT</b><input type=hidden name='chargeSlab' value='Flat'></td></tr>
						<tr class='formdata' colspan='13' valign='Top'>
<%
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
						}
%>
						<td width='10%'><input type='text' class='text' name='chargeRate' id='chargeRate1@1' value='<%=base%>' size=4 <%=readOnly%>></td>
						<td width='10%'><input type='text' class='text' name='chargeRate' id='chargeRate1@2' value='<%=min%>' size=4 <%=readOnly%>></td>
						<td width='10%'><input type='text' class='text' name='chargeRate' id='chargeRate1@3' value='<%=max%>' size=4 <%=readOnly%>></td>
						<td width='50%'><input type='text' class='text' name='chargeRate' id='chargeRate1@4' value='<%=flat%>' size=4 <%=readOnly%>></td></tr></table>
<%
				}else if("Slab".equals(buychargesHDRDOB.getRateBreak()) || "Slab%".equals(buychargesHDRDOB.getRateBreak()))
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
					
%>
					<table width='940' border='0' cellspacing='0' cellpadding='4'>
						<tr class='formdata' colspan='13' valign='Top'><td rowspan=2 width='10%'><b>ChargeId: <br><%=chargeId%></b></td></td>
						<td width='6%'><b>BASE</b><input type=hidden name='chargeSlab' value='BASE'></td>
						<td width='6%'><b>MIN</b><input type=hidden name='chargeSlab' value='MIN'></td>
						<td width='6%'><b>MAX</b><input type=hidden name='chargeSlab' value='MAX'></td>
						
<%
						for(int j=0;j<11;j++)
						{
%>
						<td width='6%'><input type='text' class='text' name='chargeSlab' value='<%=(chargeSlab[j]!=null)?chargeSlab[j]:""%>' size=4 <%=readOnly%>></td>
<%
						}
%>
						<td></td></tr>
						<tr class='formdata' colspan='13' valign='Top'>
						<td width='6%'><input type='text' class='text' name='chargeRate' value='<%=base%>' size=4 <%=readOnly%>></td>
						<td width='6%'><input type='text' class='text' name='chargeRate' value='<%=min%>' size=4  <%=readOnly%>></td>
						<td width='6%'><input type='text' class='text' name='chargeRate' value='<%=max%>' size=4  <%=readOnly%>></td>
<%						String rateType	=	buychargesHDRDOB.getRateType();
						for(int j=0;j<11;j++)
						{
							if("Both".equals(rateType))
							{
%>
								<td width='4%'><input type='text' class='textHighlight' name='chargeFlatRate' id='chargeFlatRate1@<%=(j+1)%>' value='<%=(chargeFlatRate[j]!=null)?chargeFlatRate[j]:""%>' size=1 <%=readOnly%>><input type='text' class='text' name='chargeRate' id='chargeRate1@<%=(j+1)%>' value='<%=(chargeRate[j]!=null)?chargeRate[j]:""%>' size=3 <%=readOnly%>></td>
<%
							}else{
%>
								<td width='6%'><input type='text' class='text' name='chargeRate' value='<%=(chargeRate[j]!=null)?chargeRate[j]:""%>' size=4 <%=readOnly%>></td>
<%
							}
						}
%>
						<td></td></tr></table>
<%					
				}
			}else
			{
				throw new Exception();
			}
%>		  
		</span>
		</table>
		<table border="0" width="940" cellpadding="4" cellspacing="1">
          <tr class='denotes' bgcolor="#FFFFFF">
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory
            </td>
            <td valign="top" align="right">
			<%if("buyView".equalsIgnoreCase(request.getParameter("subOperation"))){%>
              <input type="button" value="Close" name="Close" class="input" onClick='window.close()'>
			  <%}else{%>
			  <input type="button" value="Continue" name="Continue" class="input" onClick='return viewContinue()'>
			  <%}%>
				<input type='hidden' name='Operation' value='<%=operation%>' >
				<input type='hidden' name='subOperation' value='enter'>
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
		if(operation!=null && operation.equals("View"))
		{
			errorMessage	= "Exception While Loading the data:";
			nextNavigation	= "QMSBuyChargesController?Operation="+operation+"&SubOperation=";				
		}
		else
		{
			errorMessage	= "Exception While Modifying the data:";
			nextNavigation	= "etrans/QMSBuyChargesMasterEnterId.jsp?Operation="+operation+"&SubOperation=";
		}
		errorMessageObject = new ErrorMessage(errorMessage,nextNavigation); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
%>
					<jsp:forward page="/ESupply/QMSESupplyErrorPage.jsp" />
<%
	}
%>
