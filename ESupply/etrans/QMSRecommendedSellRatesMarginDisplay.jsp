<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSRecommendedSellRatesMarginDisplay.jsp
	Product Name	: QMS
	Module Name		: Recommended Sell Rate
	Task		    : View Recommended Sell Rate
	Date started	: 26-07-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: Madhusudhan Reddy .Y
	Description		: The application "Modify" Recommended Sell Rate
	Actor           :
	Related Document: CR_DHLQMS_1004

--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.qms.operations.sellrates.java.QMSBoundryDOB,
						com.qms.operations.sellrates.ejb.sls.QMSSellRatesSessionHome,
						com.qms.operations.sellrates.ejb.sls.QMSSellRatesSession,
						com.foursoft.esupply.common.java.LookUpBean,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSRecommendedSellRatesMarginDisplay.jsp";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);	
		ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
		ArrayList			keyValueList			=   new ArrayList();
		ErrorMessage		errorMessageObject		=   null;
		ArrayList			listValues				=	null;
		
		QMSSellRatesDOB		sellRatesDob			=	null;
		QMSSellRatesDOB		sellRtDob				=	null;

		QMSSellRatesSessionHome   sellRatesHome     =   null;
		QMSSellRatesSession       sellRatesRemote   =   null;

		String				shipmentMode			=	null;
		String				weigthBrake				=	null;
		String				currencyId				=	null;
		String				serviceLevel			=	null;
		String				rateType				=	null;
		String				buyrateId				=	null;
		String				carier					=	null;
		String				distination				=	null;
		String				orign					=	null;
		String				operation				=	null;
		try
		{
			sellRatesDob			=	new QMSSellRatesDOB();
			operation				=	request.getParameter("oper");
			sellRatesDob.setShipmentMode(request.getParameter("shipmentMode"));
			sellRatesDob.setWeightBreak(request.getParameter("weightBreak"));
			sellRatesDob.setCurrencyId(request.getParameter("currency"));
			sellRatesDob.setServiceLevel(request.getParameter("service"));
			sellRatesDob.setRateType(request.getParameter("rateType"));
			sellRatesDob.setBuyRateId(request.getParameter("buyrateId"));
			sellRatesDob.setCarrier_id(request.getParameter("carier"));
			sellRatesDob.setDestination(request.getParameter("distination"));
			sellRatesDob.setOrigin(request.getParameter("orign"));
			sellRatesDob.setFrequency(request.getParameter("frequency"));
			sellRatesDob.setTerminalId(request.getParameter("terminalId"));

			sellRatesHome			=		(QMSSellRatesSessionHome)LookUpBean.getEJBHome("QMSSellRatesSessionBean");
			sellRatesRemote			=		(QMSSellRatesSession)sellRatesHome.create();

			listValues				=		sellRatesRemote.getMarginValues(sellRatesDob,operation);
			
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>QMSRecommendedSellRatesMarginDisplay</title>
<%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<body>

<form method="post" action="QMSSellRateController" >
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td >
    <table width="100%" cellpadding="4" cellspacing="0">
			<tr valign="top" class='formlabel'>
			   <td colspan="12" width="903">Recommended Sell Rates Master -Modify </td>
			   <td align="right">SP310145</td>
			</tr>
	</table>
		<table width="100%" cellpadding="4" cellspacing="1">
          <tr class='formheader'> 
			<td>Origin</td>
			<td>Destination</td>
			<td>Carrier</td>
            <td>Service Level</td>
			<%if("1".equals(shipmentMode)||"4".equals(shipmentMode)){%>
			  <td>Approximate <br>Transit Time</td>
			  <%}else{%>
					<td>Approximate <br>Transit Days</td>
				<%}%>
			<td>Weight Break</td>
			<td>Overall Margin</td>
			<td>Margin Type</td>
			<td>Weight Break Slab</td>
			<td>Charge Rate</td>
			<td>Margin </td>
		</tr>
	<%
			if(listValues!=null)
			{
				int listSize	=	listValues.size();
				for(int i=0;i<listSize;i++)
				{

					sellRtDob	=	(QMSSellRatesDOB)listValues.get(i);
	%>
		  <tr class='formdata'>
			<td><%=sellRtDob.getOrigin()%></td>
			<td><%=sellRtDob.getDestination()%></td>
			<td><%=sellRtDob.getCarrier_id()%></td>
			<td><%=sellRtDob.getServiceLevel()%></td>
			<td><%=sellRtDob.getTransitTime()%></td>
			<td><%=sellRtDob.getWeightBreak()%></td>
			<td><%=sellRtDob.getOverAllMargin()%></td>
			<td><%=sellRtDob.getMarginType()%></td>
			<td><%=sellRtDob.getWeightBreakSlab()%></td>
			<td><%=sellRtDob.getMinimumRate()%></td>
			<td><%=sellRtDob.getMarginPer()%></td>
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
			//Logger.error(FILE_NAME,"Error in QMSRecommendedSellRatesMarginDisplay.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSRecommendedSellRatesMarginDisplay.jsp "+e);
		
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