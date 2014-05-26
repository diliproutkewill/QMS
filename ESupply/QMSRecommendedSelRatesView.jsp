<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSRecommendedSelRatesView.jsp
	Product Name	: QMS
	Module Name		: Recommended Sell Rate
	Task		    : View Recommended Sell Rate
	Date started	: 26-07-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: Madhusudhan Reddy .Y
	Description		: The application "Adding/View/Modify/Invalidate" Recommended Sell Rate
	Actor           :
	Related Document: CR_DHLQMS_1004

--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						java.util.HashMap,
						java.util.Set,
						java.util.Iterator,
						com.foursoft.esupply.common.util.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.qms.operations.sellrates.java.QMSBoundryDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>

<%!
	private static final String FILE_NAME="QMSRecommendedSelRatesView.jsp";
%>
<%
		ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
		ArrayList			keyValueList			=   new ArrayList();
		ErrorMessage		errorMessageObject		=   null;
		ArrayList			listValues				=	null;
		ArrayList			listSellRatesDtl		=	null;
		ArrayList			weightBreakList			=	null;
		ArrayList			boundryList				=	null;
		ArrayList			chargeDtlList			=	null;
		ArrayList			headerlist				=	null;
		QMSSellRatesDOB		sellRatesDob			=	null;
		QMSSellRatesDOB		sellRatDob				=	null;
		QMSBoundryDOB		boundryDob				=	null;

		String				shipmentMode			=	null;
		String				weigthBrake				=	null;
		String				rateType				=	null;
		String				overallMargin			=	null;
		String				currencyId				=	null;
		String				serviceLevel			=	null;
		String				operation				=	null;
		String				marginType				=	null;
		String				keyHash					=	null;

		Iterator			itr						=	null;
		HashMap				mapValues				=	null;

		try
		{
			operation				=	request.getParameter("Operation");
			listValues				=	(ArrayList)session.getAttribute("DisplysellRatesValues");
			System.out.println("listValueslistValueslistValueslistValueslistValueslistValues :: "+listValues);
			session.removeAttribute("DisplysellRatesValues");
			if(listValues!=null)
			{
					sellRatesDob		=	(QMSSellRatesDOB)listValues.get(0);
					currencyId		=	sellRatesDob.getCurrencyId();
					shipmentMode	=	sellRatesDob.getShipmentMode();
					weigthBrake		=	sellRatesDob.getWeightBreak();
					rateType		=	sellRatesDob.getRateType();
					overallMargin	=	sellRatesDob.getOverAllMargin();
					serviceLevel	=	sellRatesDob.getServiceLevel();
					marginType		=	sellRatesDob.getMarginType();

					headerlist				=	(ArrayList)listValues.get(1);

					weightBreakList			=	(ArrayList)headerlist.get(0);
					mapValues				=	(HashMap)headerlist.get(1);
					itr						=	(mapValues.keySet()).iterator();

					System.out.println("getWeightBreakgetWeightBreakgetWeightBreak : "+sellRatesDob.getWeightBreak());
					System.out.println("getRateTypegetRateTypegetRateTypegetRateType : "+sellRatesDob.getRateType());

			}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>QMSRecommendedSellRatesView</title>
<%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<body>

<form method="post" action="QMSSellRateController" >
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td >
       <table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="6" >Recommended Sell Rates Master - View </td>
			  </tr>
				<tr valign="top" class='formdata'>
			    <td >Shipment Mode:<b><br>
                  <%=shipmentMode%></b>

				</td>
				<td>Weight Break:&nbsp;<b><br>
                  <%=weigthBrake%></b>
            </td>
			<td>Rate Type:&nbsp;<b><br>
              <%=rateType%></b>
            </td>
			<td>Currency:&nbsp;<b><br>
              <%=currencyId%></b>
					</td>
			
		</tr>
		</table>
		<table width="100%" cellpadding="4" cellspacing="1">
<%
	if(sellRatesDob!=null)
	{
%>      
          <tr class='formheader'> 
			<td>Origin</td>
			<td>Origin Country</td>
			<td>Destination</td>
			<td>Destination Country</td>
			<td>Carrier</td>
            <td>Service Level</td>
			<td>Transit Time</td>
			<td>Frequency</td>
<%
			if(weightBreakList!=null)
			{
				String	weightBreakValue	=	null;
				int weightBreakSize		=	weightBreakList.size();
				for(int i=0;i<weightBreakSize;i++)
				{
					weightBreakValue	=	(String)weightBreakList.get(i);
%>
				<td><%=weightBreakValue%></td>
<%
				}
			}
%>

			<td>Notes</td>
          </tr>
<%
		while(itr.hasNext())
		{
				keyHash	=	(String)itr.next();
				QMSSellRatesDOB sellRatesDob1	= (QMSSellRatesDOB)mapValues.get(keyHash);
				chargeDtlList					=  sellRatesDob1.getBoundryList();
%>
			  <tr class='formdata'> 
				
				
				<td><%=sellRatesDob1.getOrigin()!=null?sellRatesDob1.getOrigin():""%></td>
				<td><%=sellRatesDob1.getOriginCountry()!=null?sellRatesDob1.getOriginCountry():""%></td>
				<td><%=sellRatesDob1.getDestination()!=null?sellRatesDob1.getDestination():""%></td>
				<td><%=sellRatesDob1.getDestinationCountry()!=null?sellRatesDob1.getDestinationCountry():""%></td>
				<td><%=sellRatesDob1.getCarrier_id()!=null?sellRatesDob1.getCarrier_id():""%></td>
				<td><%=sellRatesDob1.getServiceLevel()!=null?sellRatesDob1.getServiceLevel():""%></td>
				<td><%=sellRatesDob1.getTransitTime()!=null?sellRatesDob1.getTransitTime():""%></td>
				<td><%=sellRatesDob1.getFrequency()!=null?sellRatesDob1.getFrequency():""%></td>
<%

			int weightBreSize		=	weightBreakList.size();
			String	weightBreakValues	=	null;
			String  flagValue			=	"";
			String  minFlat				=	"";
			boolean	flag				=	false;
			for(int k=0;k<weightBreSize;k++)
			{
				flag				=	false;
				weightBreakValues	=	(String)weightBreakList.get(k);
				
				int chargeDtlSize	=	chargeDtlList.size();
				for(int j=0;j<chargeDtlSize;j++)
				{
					boundryDob	=	(QMSBoundryDOB)chargeDtlList.get(j);


					if(boundryDob.getWeightBreak().equals(weightBreakValues))
					{
						flag				=	true;
						break;
					}
				}
				if(flag)
				{
%>
						<td><%=flagValue%><%=boundryDob.getChargeRate()%></td>
<%
				}
				else
				{
%>
						<td><%=0.0%></td>				
<%
				}
			}
%>
				<td align="right"><%=sellRatesDob1.getNotes()!=null?sellRatesDob1.getNotes():""%></td>
			  </tr>
<%			
		}
	}
%>
		<tr><td colspan=10 align="right">
			<input type="hidden" name="Operation" value='<%=operation%>'>
			<input type="hidden" name="subOperation" value='Enter'>
			<input type="submit" name="Submit" value="Submit" class='input'>
          </td></tr>

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
			Logger.error(FILE_NAME,"Error in QMSRecommendedSelRatesView.jsp "+e);
		
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