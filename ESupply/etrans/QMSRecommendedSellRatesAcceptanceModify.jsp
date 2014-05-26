<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSRecommendedSellRatesAcceptanceModify.jsp
	Product Name	: QMS
	Module Name		: Recommended Sell Rate
	Task		    : Adding/View/Modify/Invalidate/Acceptance Recommended Sell Rate
	Date started	: 21-10-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: Madhusudhan Reddy .Y
	Description		: The application "AcceptanceReport" Recommended Sell Rate
	Actor           :
	Related Document: CR_DHLQMS_1004

--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						java.util.Set,
						java.util.LinkedHashSet,
						java.util.Iterator,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSRecommendedSellRatesAcceptanceModify.jsp";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);	
		ArrayList			listValues				=	null;
		ArrayList			accList					=	null;
		ArrayList			dobList					=	null;
		ArrayList			keyValueList			=   new ArrayList();
		ErrorMessage		errorMessageObject		=   null;
		QMSSellRatesDOB		headerSellRatesDOB		=	null;
		QMSSellRatesDOB		dtlSellRatesDOB			=	null;

		String				origin					=	null;
		String				originCut				=	null;
		String				distenation				=	null;
		String				distenationCut			=	null;
		String				currencyId				=	null;
		String				serviceLevel			=	null;
		String				carrierStr				=	null;
		String				shipmentMode			=	null;
		String				weightBrk				=	null;
		String				rateTpe					=	null;
		String				weigtClas				=	null;
		String				overallMrg				=	null;
		String				marginTpe				=	null;
		String				marginBass				=	null;
		String				consoleTpe				=	null;
		String				operation				=	null;
		String				transitTime				=	null;
		String				frequency				=	null;
		String				overallMag				=	null;
		String				mrgType					=	null;
		String				buyrateId				=	null;
		String[]			weighttBkValues			=	null;
		String[]			chargesValue			=	null;
		String[]			marign					=	null;
		String[]			mValues					=	null;
		String[]            rateDescs               =   null;
		int					indexValue				=	0;
		String[]			marginValues			=	null;
		int count=0,temp=0,templist=0;
		try
		{
			operation				=	request.getParameter("Operation");
			//System.out.println("indexindexindexindexindexindexindexindexindex : "+request.getParameter("index"));
			//System.out.println("serchStrserchStrserchStrserchStrserchStrserchStr : "+request.getParameter("serchStr"));
			if(request.getParameter("index")!=null)
				indexValue	=	Integer.parseInt(request.getParameter("index"));

			listValues		=	(ArrayList)session.getAttribute("HeaderValues");
			if(listValues!=null && listValues.size()>0)
			{
				headerSellRatesDOB		=	(QMSSellRatesDOB)listValues.get(0);
				accList					=	(ArrayList)listValues.get(1);
				dobList					=	(ArrayList)accList.get(0);

				dtlSellRatesDOB			=	(QMSSellRatesDOB)dobList.get(indexValue);

				currencyId				=	dtlSellRatesDOB.getCurrencyId();
				origin					=	dtlSellRatesDOB.getOrigin();
				originCut				=	dtlSellRatesDOB.getOriginCountry();
				distenation				=	dtlSellRatesDOB.getDestination();
				distenationCut			=	dtlSellRatesDOB.getDestinationCountry();
				serviceLevel			=	dtlSellRatesDOB.getServiceLevel();
				carrierStr				=	dtlSellRatesDOB.getCarrier_id();
				shipmentMode			=	headerSellRatesDOB.getShipmentMode();
				weightBrk				=	dtlSellRatesDOB.getWeightBreak();
				rateTpe					=	dtlSellRatesDOB.getRateType();
				weigtClas				=	dtlSellRatesDOB.getWeightClass();
				overallMrg				=	dtlSellRatesDOB.getOverAllMargin();
				marginTpe				=	dtlSellRatesDOB.getMarginType();
				marginBass				=	dtlSellRatesDOB.getMarginBasis();
				consoleTpe				=	headerSellRatesDOB.getConsoleType();
				transitTime				=	dtlSellRatesDOB.getTransitTime();
				frequency				=	dtlSellRatesDOB.getFrequency();
				weighttBkValues			=	dtlSellRatesDOB.getAllWeightBreaks();
				rateDescs               =	dtlSellRatesDOB.getRateDescription();//@@Added by kameswari for Surcharge Enhancements
				chargesValue			=	dtlSellRatesDOB.getChargeRates();
				buyrateId				=	dtlSellRatesDOB.getBuyRateId();
				marginValues			=	dtlSellRatesDOB.getMarginValues();
				if("etValue".equals(request.getParameter("serchStr")))
				{
					if("FLAT".equals(weightBrk))
					{
						marign					=		request.getParameterValues("margenValue");
						overallMag				=		request.getParameter("overMargin");
						mrgType					=		request.getParameter("marginType");
						int	marignLength		=		marign.length;
						mValues					=		new String[marignLength];
						for(int i=0;i<marignLength;i++)
						{
							if(rateDescs!=null&&"A FREIGHT RATE".equalsIgnoreCase(rateDescs[i]))//@@Added by Kameswari for the internal issue on 08/08/09
						      {
							mValues[i]	=	marign[i];
							  }
						}
						dtlSellRatesDOB.setMarginValues(mValues);
						dtlSellRatesDOB.setOverAllMargin(request.getParameter("overMargin"));
						dtlSellRatesDOB.setMarginType(request.getParameter("marginType"));
						//System.out.println("marginValuesmarginValuesmarginValuesmarginValuesmarginValues : "+marginValues[0]);
					}
					else if("SLAB".equals(weightBrk))
					{
						
						overallMag				=		request.getParameter("overMargin");
						mrgType					=		request.getParameter("marginType");
						marign					=		request.getParameterValues("margenValue");
						
						if("N".equals(overallMag))
						{

							int weighttBkValuesSize	=	weighttBkValues.length;
							mValues			=		new String[weighttBkValuesSize];
							for(int j=0;j<weighttBkValuesSize;j++)
							{
								//mValues[j]	=	request.getParameter(weighttBkValues[j]);
								if(rateDescs!=null&&"A FREIGHT RATE".equalsIgnoreCase(rateDescs[j])) //@@Added by Kameswari for the internal issue on 08/08/09
						      {
								mValues[j]	=	marign[j];
							  }	
								//System.out.println("mValuesmValuesmValuesmValuesmValues SLAB : "+mValues[j]);
							}
							
						}
						else
						{
							marign					=		request.getParameterValues("margenValue");
							int	marignLength		=		marign.length;
							mValues			=		new String[marignLength];
							for(int i=0;i<marignLength;i++)
							{
							  if(rateDescs!=null&&"A FREIGHT RATE".equalsIgnoreCase(rateDescs[i]))//@@Added by Kameswari for the internal issue on 08/08/09
						      {
								mValues[i]	=	marign[i];
							  }
							}
						}
						dtlSellRatesDOB.setMarginValues(mValues);
						dtlSellRatesDOB.setOverAllMargin(request.getParameter("overMargin"));
						dtlSellRatesDOB.setMarginType(request.getParameter("marginType"));
						//System.out.println("marginValuesmarginValuesmarginValuesmarginValuesmarginValues in slab : "+marginValues[0]);
					}
					else if("LIST".equals(weightBrk))
					{
						
						overallMag				=		request.getParameter("overMargin");
						mrgType					=		request.getParameter("marginType");
						marign					=		request.getParameterValues("margenValue");
						if("N".equals(overallMag))
						{
							int weighttBkValuesSize	=	weighttBkValues.length;
							mValues			=		new String[weighttBkValuesSize];
							for(int j=0;j<weighttBkValuesSize;j++)
							{
								if(rateDescs!=null&&"A FREIGHT RATE".equalsIgnoreCase(rateDescs[j]))//@@Added by Kameswari for the internal issue on 08/08/09
						      {
								mValues[j]	=	marign[j];
							  }	
							}
							
						
						}
						else
						{
							marign					=		request.getParameterValues("margenValue");
							int	marignLength		=		marign.length;
							mValues			=		new String[marignLength];
							for(int i=0;i<marignLength;i++)
							{
								if(rateDescs!=null&&"A FREIGHT RATE".equalsIgnoreCase(rateDescs[i]))
						      {
								mValues[i]	=	marign[i];
							  }
							}

						}
						dtlSellRatesDOB.setMarginValues(mValues);
						dtlSellRatesDOB.setOverAllMargin(request.getParameter("overMargin"));
						dtlSellRatesDOB.setMarginType(request.getParameter("marginType"));
						//System.out.println("marginValuesmarginValuesmarginValuesmarginValuesmarginValues in list : "+marginValues[0]);
					}
				}
			}
						
%>
<html>
<head>
<title>Recommended Sell Rates Master</title>
<%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

</body>
</html>
<script>

function checkValidation(obj)
{
	var operation		=	'<%=operation%>';
	var flag=false;
	var obj		=	document.getElementsByName("margenValue");
	var objSize	=	obj.length;
	for( i=0;i<objSize;i++)
	{
		if(obj[i].value.length==0)
		{
			alert("Please Enter Margin.");
			obj[i].focus();
			return false;
		}
	}
<%
	if(dobList!=null && dobList.size()>0)
	{
		if("SLAB".equalsIgnoreCase(weightBrk) || "LIST".equalsIgnoreCase(weightBrk))
		{
			int	wtBreakLength				=	weighttBkValues.length;
			for(int i=0;i<wtBreakLength;i++)
			{			
%>
				if(document.forms[0].overMargin.value=="N")
				{
					var nameVal	='<%=weighttBkValues[i]%>';
					var name=document.getElementsByName(nameVal);
					for(n=0;n<name.length;n++)
					{
						if(name[n].value.length==0)
						{
							alert("Please Enter Margin.");
							name[n].focus();
							return false;
						}
						else
							break;
					}
				}
<%
			}
		}
	}
%>
	
document.forms[0].action="etrans/QMSRecommendedSellRatesAcceptanceModify.jsp?Operation="+operation+"&index="+'<%=request.getParameter("index")%>'+"&serchStr=etValue";
document.forms[0].submit();
return true;

}

/*
* This function used for Margins disply to respective conditions
*/
function showWeightBreaks()
{
    var data="";
<%	
	if(dobList!=null && dobList.size()>0)
	{
%>
		if(document.forms[0].overMargin.value=="Y" && document.forms[0].marginType.value=="P" && document.forms[0].marginBasis.value=="N" && document.forms[0].weightBreak.value=="FLAT" && document.forms[0].rateType.value=="FLAT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='margenValue' size='7' value='<%=marginValues[0]%>'></td><td colspan='3'>&nbsp;</td></tr>";
			<% if(marginValues.length>1)
			{%>
			 data=data+"<tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' value='<%=marginValues[marginValues.length-1]%>' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
			 <%}%>
		}
		else if((document.forms[0].overMargin.value=="Y" || document.forms[0].overMargin.value=="N") && (document.forms[0].marginType.value=="A" || document.forms[0].marginType.value=="P") && document.forms[0].marginBasis.value=="N" && document.forms[0].weightBreak.value=="FLAT" && document.forms[0].rateType.value=="FLAT")
		{
			 //@@Modify by kameswari.p on 25/07/2011 for Wpbn Issue 259371
			  data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='551'></td><td>Margin </td>";
			 <%	
				 int	 wtBreakLength1				=	weighttBkValues.length;
			 int count1 = 0;
				 for(int i=0;i<wtBreakLength1;i++)
				{
								
					if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[i])))
						{
				
					%>
			data= data+"<td width='70' align='left' colspan='2'><input type=text  class='text' name='margenValue' size='7' value='<%=marginValues[i]%>'></td>";
			<%}
			else
					{  count1++;%>
				
			<%	}
				}%>
				data= data+" </tr>";
			<%if(count1>0)
			{%>
			 data=data+"<tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' value='<%=marginValues[marginValues.length-1]%>' size='7' ></td><td colspan='6'>&nbsp;</td></tr></table>";
			 <%}%>
				//@@Ended by 	kameswari.p
		}
		else if(document.forms[0].overMargin.value=="Y"  && document.forms[0].marginType.value=="A"  && document.forms[0].weightBreak.value=="SLAB" && (document.forms[0].rateType.value=="FLAT" || document.forms[0].rateType.value=="SLAB"))
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='551'></td><td width='90' align='left' colspan='2'>Min Margin <br><input type=text  class='text' name='margenValue' size='7'  value='<%=marginValues[0]%>'></td><td width='108' align='left' colspan='4'>Slab Margin <br><input type=text  class='text' name='margenValue' size='7'  value='<%=marginValues[1]%>'></td></tr>";
			 <% if(marginValues.length>2)
			{%>
			data= data+"<tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' value='<%=marginValues[marginValues.length-1]%>'></td><td colspan='3'>&nbsp;</td></tr></table>";
			<%}%>
		}
		else if(document.forms[0].overMargin.value=="Y"  && document.forms[0].marginType.value=="P"  && document.forms[0].weightBreak.value=="SLAB" && (document.forms[0].rateType.value=="FLAT" || document.forms[0].rateType.value=="SLAB"))
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='551'></td><td width='90' align='left' colspan='2'>Margin <br><input type=text  class='text' name='margenValue' size='7'  value='<%=marginValues[0]%>'></td><td colspan='4'>&nbsp;</td></tr>";
			  <% if(marginValues.length>1)
			{%>
			 data=data+"<tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7' value='<%=marginValues[marginValues.length-1]%>' ></td><td colspan='3'>&nbsp;</td></tr></table>";
			 <%}%>
		}
		else if(document.forms[0].overMargin.value=="N"  && (document.forms[0].marginType.value=="A" || document.forms[0].marginType.value=="P")  && document.forms[0].weightBreak.value=="SLAB" && (document.forms[0].rateType.value=="FLAT" || document.forms[0].rateType.value=="SLAB" || document.forms[0].rateType.value=="BOTH"))
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' width='55%'>Margin </td>";
<%
			if(dobList!=null)
			{
	             templist                    =    0;
				int	wtBreakLength				=	weighttBkValues.length;
				for(int i=0;i<wtBreakLength;i++)
				{

					if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[i])))
						{
							 
							 if(weighttBkValues[i]!=null&&weighttBkValues[i]!="")
							{
								 templist++;
		%>
						
			
				data	= data + "<td  align='right'><input type=text  class='text' name='margenValue' value='<%=marginValues[i]%>' size='7' ></td>";
					 
<%					}
                  }
			   }
			}
 if(marginValues.length>templist)
			{%>				
			data	=	data + "<td width='5%'>&nbsp;</td></tr><tr class='formdata'><td align='right'>Surcharge&nbsp;Margin </td><td align='right'><input type=text  class='text' name='surchargeValue' value='<%=marginValues[marginValues.length-1]%>' size='7' ></td><td colspan='6'>&nbsp;</td></tr></table>";
			<%}%>
		}
		else if(document.forms[0].overMargin.value=="Y"  && document.forms[0].marginType.value=="A"  && document.forms[0].weightBreak.value=="LIST" &&  document.forms[0].rateType.value=="PIVOT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='305'></td><td width='90' align='left' >Pivot Margin</td><td width='108' align='left' colspan='2'><input type=text  class='text' name='margenValue' size='7'  value='<%=marginValues[1]%>'></td><td width='90' align='left' >Over Pivot Margin</td><td width='108' align='left' ><input type=text  class='text' name='margenValue' size='7'  value='<%=marginValues[0]%>'></td></tr>";
			  <% if(marginValues.length>2)
			{%>	
			 data=data+"<tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' value='<%=marginValues[marginValues.length-1]%>' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
			 <%}%>
		}
		else if(document.forms[0].overMargin.value=="Y"  && document.forms[0].marginType.value=="P"  && document.forms[0].weightBreak.value=="LIST" && document.forms[0].rateType.value=="PIVOT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='5' width='551'></td><td width='90' align='left' colspan='2'>Margin </td><td width='108' align='left' ><input type=text  class='text' name='margenValue' size='7'  value='<%=marginValues[0]%>'></td><td colspan='4'>&nbsp;</td></tr>";
			   <% if(marginValues.length>1)
			{%>	
			 data=data+"<tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' value='<%=marginValues[marginValues.length-1]%>' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
		<%}%>
		}
		else if(document.forms[0].overMargin.value=="N"  && (document.forms[0].marginType.value=="A" || document.forms[0].marginType.value=="P")  && document.forms[0].weightBreak.value=="LIST" &&  document.forms[0].rateType.value=="PIVOT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' width='55%' >Pivot Margin</td>";
<%
		
			if(dobList!=null)
			{
				 templist                    =   0;
				int	wtBreakLength				=	weighttBkValues.length;
				for(int i=0;i<wtBreakLength;i++)
				{
                  
				if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[i])))
						{
							 
							 if(weighttBkValues[i]!=null&&weighttBkValues[i]!="")
							{
								 templist++;
		%>
						
				data	= data + "<td  align='right'><input type=text  class='text' name='margenValue' value='<%=marginValues[i]%>' size='7' ></td>";
					 
<%					}
                  }
			   }
			}

         if(marginValues.length>templist)
			{%>				
			data	=	data + "<td width='5%'>&nbsp;</td></tr><tr class='formdata'><td align='right'>Surcharge&nbsp;Margin </td><td align='right'><input type=text  class='text' name='surchargeValue' value='<%=marginValues[marginValues.length-1]%>' value= size='5' ></td><td colspan='6'>&nbsp;</td></tr></table>";
			<%}%>
		}
	
		else if(document.forms[0].shipmentMode.value=="2" &&  document.forms[0].consoleType.value=="FCL" && document.forms[0].overMargin.value=="Y"  && (document.forms[0].marginType.value=="P" || document.forms[0].marginType.value=="A") && document.forms[0].weightBreak.value=="LIST" && document.forms[0].rateType.value=="FLAT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='margenValue' size='7'  value='<%=marginValues[0]%>'></td><td colspan='3'>&nbsp;</td></tr>";
			   <% if(marginValues.length>1)
			{%>	
 //@@ Commented and Added by subrahmanyam for the pbn id: 206737 on 27-May-10
			   /*
			   			 data=data+"<tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' value='<%=marginValues[1]%>' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
			   */
			 data=data+"<tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' value='<%=marginValues[marginValues.length-1]%>' size='7' ></td><td colspan='3'>&nbsp;</td></tr></table>";
			 <%}%>
		}
		else if(document.forms[0].shipmentMode.value=="2" &&  document.forms[0].consoleType.value=="FCL" && document.forms[0].overMargin.value=="N"  && (document.forms[0].marginType.value=="P" || document.forms[0].marginType.value=="A") && document.forms[0].weightBreak.value=="LIST" && document.forms[0].rateType.value=="FLAT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td width='55%' align='right'>Margin </td>";
<%
			if(dobList!=null)
			{
				int	wtBreakLength				=	weighttBkValues.length;
				  templist                    =   0;
				for(int i=0;i<wtBreakLength;i++)
				{			

				if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[i])))
						{
							 
							 if(weighttBkValues[i]!=null&&weighttBkValues[i]!="")
							{
								 templist++;
		%>
						
				data	= data + "<td  align='right'><input type=text  class='text' name='margenValue' value='<%=marginValues[i]%>' size='7' ></td>";
					 
<%					}
                  }
			   }
			}
           if(marginValues.length>templist)
			{%>	
			data	=	data + "<td width='5%'>&nbsp;</td></tr><tr class='formdata'><td align='right'>Surcharge&nbsp;Margin </td><td align='right'><input type=text  class='text' name='surchargeValue'  value='<%=marginValues[marginValues.length-1]%>' size='5' ></td><td colspan='6'>&nbsp;</td></tr></table>";
			<%}%>
		}
		else if(document.forms[0].shipmentMode.value=="4" &&  document.forms[0].consoleTypes.value=="FTL" && document.forms[0].overMargin.value=="Y"  && (document.forms[0].marginType.value=="P" || document.forms[0].marginType.value=="A") && document.forms[0].weightBreak.value=="LIST" && document.forms[0].rateType.value=="FLAT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='margenValue' size='7'  value='<%=marginValues[0]%>'></td><td colspan='3'>&nbsp;</td></tr>";
		   <% if(marginValues.length>1)
			{%>	
			 data=data+"<tr class='formdata'><td align='right' colspan='6' width='551'></td><td>Surcharge&nbsp;Margin </td><td width='108' align='left' colspan='2'><input type=text  class='text' name='surchargeValue' size='7'	value='<%=marginValues[marginValues.length-1]%>' ></td><td colspan='3'>&nbsp;</td></tr></table>";
			 <%}%>
		}
		else if(document.forms[0].shipmentMode.value=="4" &&  document.forms[0].consoleTypes.value=="FTL" && document.forms[0].overMargin.value=="N"  && (document.forms[0].marginType.value=="P" || document.forms[0].marginType.value=="A") && document.forms[0].weightBreak.value=="LIST" && document.forms[0].rateType.value=="FLAT")
		{
			 data="<table width='100%' cellpadding='4' cellspacing='1'><tr class='formdata'><td width='55%' align='right'>Margin </td>";
<%
			if(dobList!=null)
			{
				int	wtBreakLength				=	weighttBkValues.length;
				 templist                    = 0;
				for(int i=0;i<wtBreakLength;i++)
				{			

				if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[i])))
						{
							 
							 if(weighttBkValues[i]!=null&&weighttBkValues[i]!="")
							{
								 templist++;
		%>
						
				data	= data + "<td  align='right'><input type=text  class='text' name='margenValue' value='<%=marginValues[i]%>' size='7' ></td>";
					 
<%					}
                  }
			   }
			}
		if(marginValues.length>templist)
			{%>	
			data	=	data + "<td width='5%'>&nbsp;</td></tr><tr class='formdata'><td align='right'>Surcharge&nbsp;Margin </td><td align='right'><input type=text  class='text' name='surchargeValue' value='<%=marginValues[marginValues.length-1]%>' size='5' ></td><td colspan='6'>&nbsp;</td></tr></table>";
			<%}%>
		}
		else
		{
			alert("This combination not available please check onther combination.");
		}
		if( document.layers)
		{
			document.layers.cust.document.write(data);
			document.layers.cust.document.close();
		}
		else
        {
            if(document.all)
            {
               cust.innerHTML = data;
             }
         }
<%
	}
%>
}
function showHide()
{
	document.forms[0].shipmentMode.focus();
	var data="";
	var index	=	document.forms[0].shipmentMode.value;
	if(index=="2")
	{
		 data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleType" class="select" onChange="selectData();changeValue(this);disableSubmit()"><option  value="LCL" <%="LCL".equalsIgnoreCase(consoleTpe) ? "SELECTED" : "" %>>LCL</option><option  value="FCL" <%="FCL".equalsIgnoreCase(consoleTpe) ? "SELECTED" : "" %>>FCL</option></select>';
	}
	else if(index=="4")
	{
		data='Console&nbsp;Type:<font  color=#ff0000>*</font><br><select size="1" name="consoleTypes" class="select" onChange="selectData();changeValue(this);disableSubmit()"><option  value="LTL" <%="LTL".equalsIgnoreCase(consoleTpe) ? "SELECTED" : "" %>>LTL</option><option  value="FTL" <%="FTL".equalsIgnoreCase(consoleTpe) ? "SELECTED" : "" %>>FTL</option></select>';
	}
	if( document.layers)
	{
		document.layers.cust1.document.write(data);
		document.layers.cust1.document.close();
	}
	else
	{
		if(document.all)
		{
		   cust1.innerHTML = data;
		 }
	 }
}
function showRateType()
{
	var data="";
	var index	=	document.forms[0].weightBreak.value;
	var mode	=	document.forms[0].shipmentMode.value;

	if((index=="FLAT") && (mode=="1" || (mode=="2" && document.forms[0].consoleType.value=="LCL") ||  (mode=="4" && document.forms[0].consoleTypes.value=="LTL") ))
	{
		 data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="selectData();changeValue();disableSubmit();"><option  value="FLAT" <%="FLAT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Flat</option></select>';
	}
	else if((index=="SLAB") && (mode=="1" || (mode=="2" && document.forms[0].consoleType.value=="LCL") ||  (mode=="4" && document.forms[0].consoleTypes.value=="LTL")))
	{
		  data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="selectData();changeValue();disableSubmit();"><option  value="FLAT" <%="FLAT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Flat</option><option value="SLAB" <%="SLAB".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Slab</option><option value="BOTH" <%="BOTH".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Both</option></select>';
	}
	else if(index=="LIST" && mode=="1")
	{
		 data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="selectData();changeValue();disableSubmit();"><option value="PIVOT" <%="PIVOT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Pivot</option></select>';
	}
	else
	{
		data='Rate Type: <font color="#FF0000">*</font><br><select size="1" name="rateType" class="select" onChange="selectData();changeValue();disableSubmit();"><option  value="FLAT" <%="FLAT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Flat</option><option value="SLAB" <%="SLAB".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Slab</option><option value="BOTH" <%="BOTH".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Both</option><option value="PIVOT" <%="PIVOT".equalsIgnoreCase(rateTpe) ? "SELECTED" : "" %>>Pivot</option></select>';
	}
	if( document.layers)
	{
		document.layers.cust2.document.write(data);
		document.layers.cust2.document.close();
	}
	else
	{
		if(document.all)
		{
		   cust2.innerHTML = data;
		 }
	 }
}
function changeValue()
{
	if(document.forms[0].shipmentMode.value=="2")
	{
		if(document.forms[0].consoleType.value=="FCL")
		{
			document.forms[0].weightBreak.value="LIST"; 
			document.forms[0].rateType.value="FLAT";
		}
		else if(document.forms[0].consoleType.value=="LCL" && document.forms[0].weightBreak.value=="LIST")
		{
			document.forms[0].weightBreak.value="FLAT";
		}
	}
	else if(document.forms[0].shipmentMode.value=="4")
	{
		if(document.forms[0].consoleTypes.value=="FTL")
		{
			document.forms[0].weightBreak.value="LIST"; 
			document.forms[0].rateType.value="FLAT";
		}
		else if(document.forms[0].consoleTypes.value=="LTL" && document.forms[0].weightBreak.value=="LIST")
		{
			document.forms[0].weightBreak.value="FLAT";
		}
	}
}
function disableSubmit()
{
	if(document.forms[0].next!=null)
	{
		var obj				= document.forms[0].next.value;
		if(obj=="Next>>")
		{
			document.forms[0].next.disabled=true;
		}
	}
}
/*
function displayMargins(orign,distination,carier,service,buyrateId)
{
	var weightBreak		=	document.forms[0].weightBreak.value;
	var rateType		=	document.forms[0].rateType.value;
	var shipmentMode	=	document.forms[0].shipmentMode.value;
	var currency		=	document.forms[0].baseCurrency.value
	var	oper			=	'<%=operation%>';
	var URL 		= 'etrans/QMSRecommendedSellRatesMarginDisplay.jsp?shipmentMode='+shipmentMode+'&rateType='+rateType+'&weightBreak='+weightBreak+'&buyrateId='+buyrateId+'&service='+service+'&carier='+carier+'&distination='+distination+'&orign='+orign+'&currency='+currency+'&oper'+oper;	
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 900,height = 500,resizable = yes';
	var Features 	= Bars +' '+ Options;
	var Win 		= open(URL,'myDoc',Features);
}*/
function selectData()
{
	document.forms[0].shipmentMode.value='<%=shipmentMode%>';
	document.forms[0].weightBreak.value='<%=weightBrk%>';
	document.forms[0].rateType.value='<%=rateTpe%>';
	var index	=	document.forms[0].shipmentMode.value;
	if(index=="2")
	{
		document.forms[0].consoleType.value='<%=consoleTpe%>';
	}
	else if(index=="4")
	{
		document.forms[0].consoleTypes.value='<%=consoleTpe%>';
	}
	document.forms[0].weightClass.value='<%=weigtClas%>';
}
function closeWin()
{
	var serVaue='<%=request.getParameter("serchStr")%>';
	if(serVaue=="etValue")
		window.close();
}
</script>
</head>
<body bgcolor="#FFFFFF" onLoad="closeWin();showHide();showRateType();showWeightBreaks();">
<form   method="post">

  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
       <table width="100%" cellpadding="4" cellspacing="0">
			<tr valign="top" class='formlabel'>
			   <td colspan="12" width="903">Recommended Sell Rates Master - Modify </td>
			   <td align="right">QS1060221</td>
			</tr>
	</table>
<%
		if(request.getAttribute("Errors")!=null)
		{
%>
			<table width="100%"   border="0" cellpadding="4" cellspacing="1">
				<tr class='formdata' valign="top">
					<td width="33%"><font color='red'><b>This form has not been submitted because of the following error(s) : </b> <br><br>
						<%=((String)request.getAttribute("Errors"))%>
					</font></td>
				</tr>
			</table>
<%
		}
%>
		<table width="100%" cellpadding="4" cellspacing="1">
			<tr valign="top" class='formdata'>
			   <td>Shipment&nbsp;Mode:<br>
					<select name='shipmentMode' class='select' size=1 onChange='selectData();showHide();disableSubmit();' >
						<option value='1' <%="1".equalsIgnoreCase(shipmentMode) ? "SELECTED" : "" %>>Air</option>
						<option value='2'<%="2".equalsIgnoreCase(shipmentMode) ? "SELECTED" : "" %>>Sea</option>
						<option value='4'<%="4".equalsIgnoreCase(shipmentMode) ? "SELECTED" : "" %>>Truck</option>
					</select>
					</select>
				</td>
				
				<td>
				  <div id='cust1' style='position:relative;'></div>
				</td>
				
				<td>Weight Break: <font color="#FF0000">*</font><br>
					<select size="1" name="weightBreak" class='select' onChange="selectData();changeValue();disableSubmit();showRateType();">
						<option selected value="FLAT" <%="FLAT".equalsIgnoreCase(weightBrk) ? "SELECTED" : "" %>>Flat</option>
						<option  value="SLAB" <%="SLAB".equalsIgnoreCase(weightBrk) ? "SELECTED" : "" %>>Slab</option>
						<option  value="LIST" <%="LIST".equalsIgnoreCase(weightBrk) ? "SELECTED" : "" %>>List</option>
					</select>
				</td>
				<td>
					<div id='cust2' style='position:relative;'></div>
				</td>
				<td>Currency:<font  color=#ff0000>*</font><br>
					<input type='text' class='text' name='baseCurrency' value='<%=currencyId!=null?currencyId:""%>' size='10' maxlength='3' onblur='this.value=this.value.toUpperCase()' onChange='disableSubmit();' readOnly>
				</td>
				<td>Weight Class: <br>
					<select size="1" name="weightClass" class='select' onChange='selectData();disableSubmit();'>
						<option  value="G" <%="G".equalsIgnoreCase(weigtClas) ? "SELECTED" : "" %>>General</option>
						<option  value="W" <%="W".equalsIgnoreCase(weigtClas) ? "SELECTED" : "" %>>Weight Scale</option>
					</select>
				</td>
				</tr>
	</table>
		
	<table width="100%" cellpadding="4" cellspacing="1">

		<tr class='formheader'> 
			<td>Origin</td>
			<td>Origin<br>Country</td>
			<td>Destination</td>
			<td>Destination Country</td>
			<td>Carrier</td>
			<td>Service Level</td>
			<%if("1".equals(shipmentMode)||"4".equals(shipmentMode)){%>
				<td>Approximate Transit Time</td>
			<%}else{%>
				<td>Approximate Transit Days</td>
			<%}%>
			<td>Frequency</td>
<%
		
		int	wtBreakLength				=	weighttBkValues.length;
		//System.out.println("wtBreakLengthwtBreakLengthwtBreakLengthwtBreakLengthwtBreakLength:: "+wtBreakLength);
			for(int j=0;j<wtBreakLength;j++)
			{
			//	logger.info("rateDescs[j]"+rateDescs[j]);
					if("A FREIGHT RATE".equalsIgnoreCase(rateDescs[j]))
						{
							 count++;
							 if(weighttBkValues[j]!=null&&weighttBkValues[j]!="")
							{
								%>
						
				<td ><%=weighttBkValues[j]%></td>
					 
<%					  	  }
                        }
					}
		
%>		
		</tr>
		<tr class='formdata'> 
		<td><%=origin%></td>
		<td><%=originCut!=null?originCut:""%></td>
		<td><%=distenation%></td>
		<td><%=distenationCut!=null?distenationCut:""%></td>
		<td><%=carrierStr%></td>
		<td><%=serviceLevel%></td>
		<td><%=transitTime%></td>
		<td><%=frequency%></td>
<%
	int chargesLength		=	chargesValue.length;
	//System.out.println("chargesLengthchargesLengthchargesLengthchargesLengthchargesLength:: "+chargesLength);
	for(int k=0;k<count;k++)
	{
						//System.out.println("chargesValue[k]chargesValue[k]chargesValue[k]chargesValue[k]:: "+chargesValue[k]);
%>
		<td><%=chargesValue[k]%></td>

<%}

						Set rdesc	= new LinkedHashSet();
						ArrayList surchargeDesc	= new ArrayList();
						for(String s: rateDescs)
							rdesc.add(s);
						Iterator it	= rdesc.iterator();
						while(it.hasNext())
								surchargeDesc.add(it.next());

				if(count<wtBreakLength)
				{
				  if((wtBreakLength<count*2+11)||"slab".equalsIgnoreCase(weightBrk)||"flat".equalsIgnoreCase(weightBrk)
					||  "list".equalsIgnoreCase(weightBrk))
				   {
							  for (int r=1;r<surchargeDesc.size();r++){
								 String diffDesc	= (String)surchargeDesc.get(r);
								 int desCount	=	0;
						 %>
						  <tr class="formdata">
						  <td>&nbsp</td>
				    <td><b><%=diffDesc%></b></td>
				  <%for(int m=count;m<wtBreakLength;m++)
				  {
					 if(weighttBkValues[m]!=null&&weighttBkValues[m]!=""&& (diffDesc.equalsIgnoreCase(rateDescs[m]) ))
					  {desCount++; %>
				 <td><%=weighttBkValues[m]%></td>
<%			
			  }
		} 
//					  temp	=	count*2+7-wtBreakLength;
					  temp	=	8-(desCount+2)+count;
					  for(int s=0;s<temp;s++)
					 {
%>
			  <td>&nbsp;</td>
			 <%}
            %>
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
			   <td>&nbsp;</td>
                   <%for(int n=count;n<chargesLength;n++)
				   {
					  if(chargesValue[n]!=null&&chargesValue[n]!=""&& (diffDesc.equalsIgnoreCase(rateDescs[n]) ))
					  {
						  if(!"-".equals(chargesValue[n]))
						  {%>
				  
				  <td><%=chargesValue[n]%>
                </td>
				<%}
					  else
						  {  %>
						  <td>-</td>
				 <%      }
					 }
				  
				 } 
				 for(int s=0;s<temp;s++)
					{%>
				 <td>&nbsp;</td>
			  <%}%>
			   </tr>
			   <%	   }}else
							   {%>
				  <!--@@Added by kameswari for the CR-->
				  <tr class="formdata">
				    <td>&nbsp;</td>
				  <%
					 
				   for(int m=count;m<count*2+11;m++)
				  {
					  if(weighttBkValues[m]!=null&&weighttBkValues[m]!="")
					  {
						  %>
				     <td><%=weighttBkValues[m]%></td>
<%			        }
			     } 
		%>		  
					  
					
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
                   <%for(int n=count;n<count*2+11;n++)
				  {	
	                 if(chargesValue[n]!=null&&chargesValue[n]!="")
					  {
						  if(!"-".equals(chargesValue[n]))
						  {%>

				  
				  <td><%=chargesValue[n]%>
                </td>
				<%}
					  else
						  {  %>
						  <td>-</td>
				     <%  }
					 }
				  }
				 %>
			   </tr>
			   <tr class="formdata">
				    <td>&nbsp;</td>
				  <%
					 temp	=0;
				   for(int m=count*2+11;m<wtBreakLength;m++)
				  {
					   if(weighttBkValues[m]!=null&&weighttBkValues[m]!="")
					  {
						   temp++;%>
						<td><%=weighttBkValues[m]%></td>
<%			         }
			     } 
				  
			  for(int s=temp;s<count+7;s++)
					{
					%>		  <td>&nbsp;</td>
								 <%}%>
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
                   <%for(int n=count*2+11;n<chargesLength;n++)
				  { 
						if(chargesValue[n]!=null&&chargesValue[n]!="")
					  { 
							if(!"-".equals(chargesValue[n]))
						  {%>
				  
				  <td><%=chargesValue[n]%>
                </td>
				<%}
					  else
						  {  %>
						  <td>-</td>
				      <% }
					 }
				  }
				  for(int s=temp;s<count+7;s++)
					{%>
				 <td>&nbsp;</td>
			  <%}%>
			   </tr>
			   <%}
				}
		%>	
	
		
  </tr>

</table>
<table width="100%" cellpadding="4" cellspacing="1">
  <tr class=formheader> <td colspan=12 > Margin Calculations </td></tr>
  <tr class='formdata'> 
	<td>
		Overall Margin: <br>
		<select size="1" name="overMargin" class='select' onChange="showWeightBreaks()">
			<option value="Y" <%="Y".equalsIgnoreCase(overallMrg) ? "SELECTED" : "" %>>YES</option>
			<option value="N" <%="N".equalsIgnoreCase(overallMrg) ? "SELECTED" : "" %>>NO</option>
		</select>
	</td>
	<td > 
		Margin Type: <br>
		<select size="1" name="marginType" class='select' onChange="showWeightBreaks();">
			<option value="P" <%="P".equalsIgnoreCase(marginTpe) ? "SELECTED" : "" %>>PERCENTAGE</option>
			<option value="A" <%="A".equalsIgnoreCase(marginTpe) ? "SELECTED" : "" %>>ABSOLUTE</option>
		</select>
	</td>
	<input type="hidden" name="marginBasis" value="N">
   </tr>
</table>
	<div id='cust' style='position:relative;'></div>
<table width="100%" cellpadding="4" cellspacing="1">
	 <tr class='denotes'> 
		  <td align='right'> 
				<input type="button" name="next" value="Submit" class='input' onClick="return checkValidation()">
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
	}
	catch(Exception e)
	{
		e.printStackTrace();
		//Logger.error(FILE_NAME,"Error in QMSRecommendedSellRatesAcceptanceModify.jsp "+e);
    logger.error(FILE_NAME+"Error in QMSRecommendedSellRatesAcceptanceModify.jsp "+e);
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