<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSRecommendedSellRatesView.jsp
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
						java.util.*,
						java.text.DecimalFormat,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.qms.operations.sellrates.java.QMSBoundryDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSRecommendedSellRatesView.jsp";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);	
		DecimalFormat decimals    = new java.text.DecimalFormat("##,###,##0.00");
		ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
		ArrayList			keyValueList			=   new ArrayList();
		ErrorMessage		errorMessageObject		=   null;

		ArrayList			fslListValues			=	null;

		ArrayList			listValues				=	null;
		ArrayList			listSellRatesDtl		=	null;
		ArrayList			weightBreakList			=	null;
		ArrayList			boundryList				=	null;
		QMSSellRatesDOB		sellRatesDob			=	null;
		QMSSellRatesDOB		sellRatesDob1			=	null;
		QMSBoundryDOB		boundryDob				=	null;

		String				shipmentMode			=	null;
		String				weigthBrake				=	null;
		String				rateType				=	null;
		String				overallMargin			=	null;
		String				currencyId				=	null;
		String				serviceLevel			=	null;
		String				operation				=	null;
		String				marginType				=	null;
		String				sMode					=	null;
		String[]			weighttBkValues			=	null;
		int count =0,temp=0;
		try
		{
			operation				=	request.getParameter("Operation");
			listValues				=	(ArrayList)session.getAttribute("DisplysellRatesValues");
			//System.out.println("listValueslistValueslistValueslistValueslistValueslistValues :: "+listValues);
			if(listValues!=null)
			{
				sellRatesDob		=	(QMSSellRatesDOB)listValues.get(0);
				fslListValues		=	(ArrayList)listValues.get(1);
				currencyId			=	sellRatesDob.getCurrencyId();
				shipmentMode		=	sellRatesDob.getShipmentMode();
				if("1".equals(shipmentMode))
					sMode="Air";
				else if("2".equals(shipmentMode))
					sMode="Sea";
				else if("4".equals(shipmentMode))
					sMode="Truck";
				weigthBrake		=	sellRatesDob.getWeightBreak();
				rateType		=	sellRatesDob.getRateType();
				overallMargin	=	sellRatesDob.getOverAllMargin();
				serviceLevel	=	sellRatesDob.getServiceLevel();
				marginType		=	sellRatesDob.getMarginType();
				//System.out.println("getWeightBreakgetWeightBreakgetWeightBreak : "+sellRatesDob.getWeightBreak());
				//System.out.println("getRateTypegetRateTypegetRateTypegetRateType : "+sellRatesDob.getRateType());

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
       <table width="100%" cellpadding="4" cellspacing="0">
			  <tr valign="top" class='formlabel'>
			    <td >Recommended Sell Rates Master - <%=operation%> </td>
				<td align="right">SP310035</td>
			  </tr>
		</table>
		<table width="100%" cellpadding="4" cellspacing="0">
			<tr valign="top" class='formdata'>
			    <td >Shipment Mode:<b><br>
                  <%=sMode!=null?sMode:""%></b>

				</td>
				<td>Weight Break:&nbsp;<b><br>
                  <%=weigthBrake!=null?weigthBrake:""%></b>
            </td>
			<td>Rate Type:&nbsp;<b><br>
              <%=rateType!=null?rateType:""%></b>
            </td>
			<td>Currency:&nbsp;<b><br>
              <%=currencyId!=null?currencyId:""%></b>
					</td>
			<td> 
              Overall Margin:<b><br>
               <%=overallMargin!=null?overallMargin:""%></b>
			</td>
			<td > 
              Margin Type:&nbsp;<b><br>
				<%=marginType!=null?marginType:""%></b>
              </td>
		</tr>
		</table>
		<table width="100%" cellpadding="4" cellspacing="1">

          <tr class='formheader'> 
			<td>Origin</td>
			<td>Origin Country</td>
			<td>Destination</td>
			  <td>Destination Country</td>
			  <td>Carrier</td>
            <td>Service Level</td>
			<%if("1".equals(shipmentMode)||"4".equals(shipmentMode)){%>
			  <td>Approximate <br>Transit Time</td>
			  <%}else{%>
					<td>Approximate <br>Transit Days</td>
				<%}%>
<%
					sellRatesDob1					=	(QMSSellRatesDOB)fslListValues.get(0);
		
					weighttBkValues					=	sellRatesDob1.getAllWeightBreaks();
				
					int	wtBreakLength				=	weighttBkValues.length;
					//System.out.println("wtBreakLengthwtBreakLengthwtBreakLengthwtBreakLengthwtBreakLength:: "+wtBreakLength);
					String[]    rateDescs         =   sellRatesDob1.getRateDescription();  
				
				for(int j=0;j<wtBreakLength;j++)
					{
  						if(("A FREIGHT RATE".equalsIgnoreCase(rateDescs[j])))
						{
							 
							 count++;
							 if(weighttBkValues[j]!=null&&weighttBkValues[j]!="")
							{
								%>
						
				<td ><%=weighttBkValues[j]%></td>
					 
<%					  	  }
                        }
					}
		
%>			<!-- Modified by Mohan for Issue No.219976 on 1-11-2010 -->
			<td>Internal Notes</td> 
			<td>External Notes</td>
          </tr>
<%
		if(fslListValues!=null)
		{
			int	fslListValuessize	=	fslListValues.size();
			for(int i=0;i<fslListValuessize;i++)
			{
				sellRatesDob1		=	(QMSSellRatesDOB)fslListValues.get(i);
						weighttBkValues					=	sellRatesDob1.getAllWeightBreaks();
						wtBreakLength				=	weighttBkValues.length;

%>
				  <tr class='formdata'> 
				  
					<td><%=sellRatesDob1.getOrigin()!=null?sellRatesDob1.getOrigin():""%></td>
					<td><%=sellRatesDob1.getOriginCountry()!=null?sellRatesDob1.getOriginCountry():""%></td>
					<td><%=sellRatesDob1.getDestination()!=null?sellRatesDob1.getDestination():""%></td>
					<td><%=sellRatesDob1.getDestinationCountry()!=null?sellRatesDob1.getDestinationCountry():""%></td>
					<td><%=sellRatesDob1.getCarrier_id()!=null?sellRatesDob1.getCarrier_id():""%></td>
					<td><%=sellRatesDob1.getServiceLevel()!=null?sellRatesDob1.getServiceLevel():""%></td>
					<td><%=sellRatesDob1.getTransitTime()!=null?sellRatesDob1.getTransitTime():""%></td>
<%
					//double[] chargesValue	=	sellRatesDob1.getChargeRatesValues();
					String[] chargesValue	=	sellRatesDob1.getChargeRates();
					String[] chargeInd		=	sellRatesDob1.getChargeInr();
					int chargesLength		=	chargesValue.length;
					//System.out.println("chargesLengthchargesLengthchargesLengthchargesLengthchargesLength:: "+chargesLength);
				if("SLAB".equalsIgnoreCase(weigthBrake) && "BOTH".equalsIgnoreCase(rateType))
				{
					for(int k=0;k<count;k++)
					{
						//System.out.println("chargesValue[k]chargesValue[k]chargesValue[k]chargesValue[k]:: "+chargesValue[k]);
						if("-".equals(chargesValue[k]))
						{
%>
							<td><%="-"%></td>
<%
						}
						else
						{
%>
						<td>
							<%=decimals.format(Double.parseDouble(chargesValue[k]))%><%="FLAT".equalsIgnoreCase(chargeInd[k])?"(F)":"(S)"%>
						</td>
<%
						}
					}
				}
				else
				{
					for(int k=0;k<count;k++)
					{
						if("-".equals(chargesValue[k]))
						{
%>
							<td><%="-"%></td>
<%
						}
						else
						{
%>
							<td><%=decimals.format(Double.parseDouble(chargesValue[k]))%></td>
<%
						}
					}
				}
%>
					<td ><%=sellRatesDob1.getNoteValue()!=null?sellRatesDob1.getNoteValue():""%></td>
					<!-- //Modified by Mohan for Issue No.219976 on 1-11-2010 -->
					<td ><%=sellRatesDob1.getExtNotes()!=null?sellRatesDob1.getExtNotes():""%></td>
				   </tr>
  <%
			  String[] ratedesc =sellRatesDob1.getRateDescription();
						Set rdesc	= new LinkedHashSet();
						ArrayList surchargeDesc	= new ArrayList();
						for(String s: ratedesc)
                      	if(s!=null && !"A FREIGHT RATE".equals(s))
							rdesc.add(s);
						Iterator it	= rdesc.iterator();
						while(it.hasNext())
								surchargeDesc.add(it.next());

                        //    <!-- Added By Kishore for SurCharge Currency-->

						String[] schCurrencyArray	  =sellRatesDob1.getSurChargeCurency();
						/*Set schCurrencyArraySet	= new LinkedHashSet();
						ArrayList schCurrencyList 	= new ArrayList();
						for(String s: schCurrencyArray)
							schCurrencyArraySet.add(s);
						 it	= schCurrencyArraySet.iterator();
						while(it.hasNext())
								schCurrencyList.add(it.next());*/

                                                // <!-- End OF Kishore for SurCharge Currency-->

						if(count<wtBreakLength)
			         	{
						  if((wtBreakLength<count*2+7)||"slab".equalsIgnoreCase(weigthBrake)||"flat".equalsIgnoreCase(weigthBrake)
							  || "LIST".equalsIgnoreCase(weigthBrake) ) {
	  							  for (int r=0;r<surchargeDesc.size();r++){
								 String diffDesc	= (String)surchargeDesc.get(r);
								// String schCurr = (String)schCurrencyList.get(r);
								  int desCount	=	0;
						 %>
						  <tr class="formdata">
						  <td>&nbsp</td>
				    <td colspan=2><%=diffDesc.substring(0,(diffDesc.length()-3))%></td> <!-- Added By Kishore to trim Surcharge Desc -->
					<td >Currency</td> <!-- Added By Kishore for SurCharge Currency-->
				  <%for(int m=count;m<wtBreakLength;m++)
				  {
					 if(weighttBkValues[m]!=null&&weighttBkValues[m]!="" && (diffDesc.equalsIgnoreCase(ratedesc[m]) ))
					  {
						 desCount++;
						 %>
						 <td><%=weighttBkValues[m]%></td>
<%			
			     
					  }
					  
				} 
				  
					 // temp	=	count*2+7-wtBreakLength;
 					  temp	=	7-(desCount+2)+count;// <!-- Added By Kishore for allignment -->
					  for(int s=0;s<temp;s++)
					 {
%>
			  <td>&nbsp;</td>
			 <%}%>
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
			   <td colspan=2>&nbsp;</td>
			<%//   <td><%=schCurr </td>%>
                   <%for(int n=count;n<chargesLength;n++)
				   {  if(n == count){%>
                     <td> <%=schCurrencyArray[n]%> </td>
						 <%}
					  if(chargesValue[n]!=null&&chargesValue[n]!="")
					  {
						  if(!"-".equals(chargesValue[n]) && diffDesc.equalsIgnoreCase(ratedesc[n]))
						  {%>
				  
				  <td><%=chargesValue[n]%>
                </td>
				<%}
					  /*else
						  {  */%>
						  <!-- <td>-</td> -->
				 <%     // }
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
					 
				   for(int m=count;m<count*2+7;m++)
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
                   <%for(int n=count;n<count*2+7;n++)
				  {	
	              %>

				  
				  <td><%=chargesValue[n]%>
                </td>
				<%
				  }
				 %>
			   </tr>
			   <tr class="formdata">
				    <td>&nbsp;</td>
				  <%
					 temp	=0;
				   for(int m=count*2+7;m<wtBreakLength;m++)
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
                   <%for(int n=count*2+7;n<chargesLength;n++)
				  { 
					%>
				  
				  <td><%=chargesValue[n]%>
                </td>
				<%
				  }
				  for(int s=temp;s<count+7;s++)
					{%>
				 <td>&nbsp;</td>
			  <%}%>
			   </tr>
			   <%}
				}
			}
		}%>
	</table>
	 <table width="100%" cellpadding="4" cellspacing="0">
		<tr><td align="right">
			<input type="hidden" name="Operation" value='<%=operation%>'>
			<input type="hidden" name="subOperation" value='Insert'>
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
			//Logger.error(FILE_NAME,"Error in QMSRecommendedSellRatesView.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSRecommendedSellRatesView.jsp "+e);
		
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