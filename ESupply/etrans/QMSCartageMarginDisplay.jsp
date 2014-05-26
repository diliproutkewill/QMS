<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSCartageMarginDisplay.jsp
	Product Name	: QMS
	Module Name		: Cartage Sell Charge
	Task		    : View Cartage Sell Charge
	Date started	: 14-10-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: RamaKrishna .Y
	Description		: The application "Modify" Cartage Sell Charge
	Actor           :
	Related Document: CR_DHLQMS_1004

--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.charges.java.QMSCartageSellDtlDOB,
						com.qms.operations.charges.java.QMSCartageMasterDOB,
						com.qms.operations.charges.ejb.sls.ChargeMasterSessionHome,
						com.qms.operations.charges.ejb.sls.ChargeMasterSession,
						com.foursoft.esupply.common.java.LookUpBean,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSCartageMarginDisplay.jsp";
%>
<%  
    logger  = Logger.getLogger(FILE_NAME);	
		ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
		ArrayList			keyValueList			=   new ArrayList();
		ErrorMessage		errorMessageObject		=   null;
		ArrayList			listValues				=	null;
		
		QMSCartageSellDtlDOB	sellChargeDOB		=	new QMSCartageSellDtlDOB();
		QMSCartageMasterDOB     masterDOB           =   new QMSCartageMasterDOB();
		

		ChargeMasterSessionHome   home     =   null;
		ChargeMasterSession       remote   =   null;

		String operation    =  request.getParameter("Operation");		
		try
		{
			java.text.DecimalFormat df	= new java.text.DecimalFormat("##,##,##0.00");
			
			String rateType     =  request.getParameter("rateType");
			String weightBreak  =  request.getParameter("weightBreak");
			String chargeType   =  request.getParameter("chargeType");
			String chargeBasis  =  request.getParameter("chargeBasis");
			String cartageId    =  request.getParameter("cartageId");
			String locationId   =  request.getParameter("locationId");
			String zoneCode     =  request.getParameter("zoneCode");
			
			//System.out.println();
			masterDOB.setWeightBreak(weightBreak);
			masterDOB.setRateType(rateType);
			masterDOB.setZoneCode(zoneCode);
			masterDOB.setLocationId(locationId);
			masterDOB.setChargeBasis(chargeBasis);
			masterDOB.setCartageId(new Long(cartageId).longValue());
			masterDOB.setChargeBasis(chargeBasis);
			masterDOB.setChargeType(chargeType);

			home			=		(ChargeMasterSessionHome)LookUpBean.getEJBHome("ChargeMasterSession");
			remote			=		(ChargeMasterSession)home.create();
			listValues		=		remote.getSellCartageChargesFlat(masterDOB);
			
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>QMSCartageMarginDisplay</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<body>

<form method="post" action="QMSSellRateController" >
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td >
    <table width="100%" cellpadding="4" cellspacing="0">
			<tr valign="top" class='formlabel'>
			   <td colspan="12" width="903">CartageSellCharges Master -Modify </td>
			   <td align="right">SP310145</td>
			</tr>
	</table>
		<table width="100%" cellpadding="4" cellspacing="1">
          <tr class='formheader'> 
			<td>Location</td>
			<td>ZoneCode</td>
			<td>ChargeType</td>
			<td>OverAllMargin</td>
			<td>Margin Type</td>
			<td>Margin</td>
			<td>CHARGESLAB</td>
			<td>ChargeRate</td>	
			
		</tr>
	<%
			if(listValues!=null)
			{
				int listSize	=	listValues.size();
				for(int i=0;i<listSize;i++)
				{

					sellChargeDOB	=	(QMSCartageSellDtlDOB)listValues.get(i);
	%>
		  <tr class='formdata'>
			<td><%=locationId%></td>
			<td><%=zoneCode%></td>
			<td><%=sellChargeDOB.getChargeType()%></td>
			<td><%=sellChargeDOB.getOverallMargin()%></td>
			<td><%=sellChargeDOB.getMarginType()%></td>
			<td><%=sellChargeDOB.getMargin()%></td>
			<td><%=sellChargeDOB.getChargeSlab()%></td>
			<td><%=df.format(sellChargeDOB.getChargeRate())%></td>
		  </tr>
<%
				}
			}
%>
	<table width="100%" cellpadding="4" cellspacing="1">
		<tr>
			<td align="right" colspan="9"><input type="button" name="close_butt" value="Close" class='input' onClick="window.close();"></td>
		</tr>
</table>
  </table>
  </td></tr></table>
</form>

</body>
</html>
<%
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Error in QMSCartageMarginDisplay.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSCartageMarginDisplay.jsp "+e);
		
			errorMessageObject = new ErrorMessage(  "Error while retreiving the details  ","QMSSellRateController?Operation="+operation+"&subOperation=Enter"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
	    	
%>
			<jsp:forward page="ESupplyErrorPage.jsp" />
<%
		}
%>