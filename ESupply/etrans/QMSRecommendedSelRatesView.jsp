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
						java.util.*,
						java.util.Iterator,
						java.text.DecimalFormat,
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.qms.operations.sellrates.java.QMSBoundryDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSRecommendedSelRatesView.jsp";
%>

<%
    logger  = Logger.getLogger(FILE_NAME);	
		DecimalFormat decimals    = new java.text.DecimalFormat("##,###,##0.00");
		ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
		ArrayList			keyValueList			=   new ArrayList();
		ErrorMessage		errorMessageObject		=   null;
		ArrayList			listValues				=	null;
		ArrayList			finalList				=	null;
		ArrayList			headerlist				=	null;
		QMSSellRatesDOB		sellRatesDob			=	null;
		QMSSellRatesDOB		sellRatDob				=	null;
		String				shipmentMode			=	null;
		String				weigthBrake				=	null;
		String				rateType				=	null;
		String				overallMargin			=	null;
		String				currencyId				=	null;
		String				serviceLevel			=	null;
		String				operation				=	null;
		String				marginType				=	null;
		String				keyHash					=	null;
		String				sMode					=	null;
		String[]			weighttBkValues			=	null;
		String				consoleTpe					= null;
		int					noofRecords				=	0;
		int					noofPages				=	0;
		int count=0,temp=0;
		HashMap				mapValue				=	null;
		int                 noOfSegments            =   Integer.parseInt(loginbean.getUserPreferences().getSegmentSize());
		try
		{
			operation				=	request.getParameter("Operation");
			listValues				=	(ArrayList)session.getAttribute("HeaderValues");
			mapValue				=	(HashMap)session.getAttribute("HashList");
			if(mapValue==null)
				mapValue	=	new HashMap();
			//System.out.println("listValueslistValueslistValueslistValueslistValueslistValues :: "+listValues);
			//session.removeAttribute("DisplysellRatesValues");
			if(listValues!=null)
			{
				sellRatesDob		=	(QMSSellRatesDOB)listValues.get(0);
				finalList			=	(ArrayList)listValues.get(1);
				if(finalList!=null)
				{
					headerlist				=	(ArrayList)finalList.get(0);
					Integer  no_ofRecords   =   (Integer)finalList.get(1);
					Integer  no_ofPages		=	(Integer)finalList.get(2);
					noofRecords				=	no_ofRecords.intValue();
					noofPages				=	no_ofPages.intValue();
					//System.out.println("noofRecordsnoofRecordsnoofRecordsnoofRecords :: "+noofRecords);
					//System.out.println("noofPagesnoofPagesnoofPagesnoofPagesnoofPages:: "+noofPages);
				}
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
				consoleTpe  = sellRatesDob.getConsoleType();
				System.out.println("getWeightBreakgetWeightBreakgetWeightBreak : "+sellRatesDob.getWeightBreak());
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
<script>
function functionCall(operation,pageNo)
{
	var stMode		=	'<%=sellRatesDob.getShipmentMode()%>';	
	var currId		=	'<%=sellRatesDob.getCurrencyId()%>';
	var wtBreak		=	'<%=sellRatesDob.getWeightBreak()%>';
	var rtType		=	'<%=sellRatesDob.getRateType()%>';
	var wtClass		=	'<%=sellRatesDob.getWeightClass()%>';
	var orign		=	'<%=sellRatesDob.getOrigin()%>';
	var desination	=	'<%=sellRatesDob.getDestination()%>';
	var serviceLevel=	'<%=sellRatesDob.getServiceLevel()%>';
	var carrier		=	'<%=sellRatesDob.getCarrier_id()%>';
	var consType	=	'<%=sellRatesDob.getConsoleType()%>';
	//added by phani sekhar for wpbn 171213 on 20090609
	var orgRegion   =	'<%=sellRatesDob.getOriginRegions()%>';
	var destRegion	=  '<%=sellRatesDob.getDestRegions()%>'; 
	var	labelVal	=	"";
	if(stMode=="2")
		labelVal	=	"consoleType";
	else if(stMode=="4")
		labelVal	=	"consoleTypes";
		//modified by phani sekhar for wpbn 171213 on 20090606	
	document.forms[0].action='QMSSellRateController?&pageNo='+pageNo+'&Operation='+operation+'&subOperation=View'+'&shipmentMode='+stMode+'&baseCurrency='+currId+'&weightBreak='+wtBreak+'&rateType='+rtType+'&weightClass='+wtClass+'&origin='+orign+'&destination='+desination+'&serviceLevelId='+serviceLevel+'&carriers='+carrier+'&'+labelVal+'='+consType+'&originRegion='+orgRegion+'&destinationRegion='+destRegion;
	document.forms[0].submit();
}
</script>
<body>
<form method="post" action="QMSSellRateController" >
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td >
       <table width="100%" cellpadding="4" cellspacing="0">
			  <tr valign="top" class='formlabel'>
			    <td colspan="6" >Recommended Sell Rates Master - View </td>
				 <td align="right">QS1060232</td>
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
		</tr>
		</table>
		<table width="100%" cellpadding="4" cellspacing="1">
<%
	if(headerlist!=null && headerlist.size() > 0)
	{
%>      
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
			<td>Frequency</td>
<%
			sellRatDob				=	(QMSSellRatesDOB)headerlist.get(0);

			weighttBkValues			=	sellRatDob.getWeightBreaks();
			int	wtBreakLength				=	weighttBkValues.length;
					//System.out.println("wtBreakLengthwtBreakLengthwtBreakLengthwtBreakLengthwtBreakLength:: "+wtBreakLength);
					
					String[]	    rateDescs         =   sellRatDob.getRateDescription();  
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
		
%>		
				<!-- Modified by Mohan for Issue No.219976 on 2-11-2010 -->
				<td>Internal Notes</td>
				<td>External Notes</td>
          </tr>
<%
		int	headerlistSize				=	headerlist.size();
		for(int i=0;i<headerlistSize;i++)
		{
			sellRatDob				=	(QMSSellRatesDOB)headerlist.get(i);
						weighttBkValues			=	sellRatDob.getWeightBreaks();
					wtBreakLength				=	weighttBkValues.length;

%>
			  <tr class='formdata'> 
				<td><%=sellRatDob.getOrigin()!=null?sellRatDob.getOrigin():""%></td>
				<td><%=sellRatDob.getOriginCountry()!=null?sellRatDob.getOriginCountry():""%></td>
				<td><%=sellRatDob.getDestination()!=null?sellRatDob.getDestination():""%></td>
				<td><%=sellRatDob.getDestinationCountry()!=null?sellRatDob.getDestinationCountry():""%></td>
				<td><%=sellRatDob.getCarrier_id()!=null?sellRatDob.getCarrier_id():""%></td>
				<td><%=sellRatDob.getServiceLevel()!=null?sellRatDob.getServiceLevel():""%></td>
				<td><%=sellRatDob.getTransitTime()!=null?sellRatDob.getTransitTime():""%></td>
				<td><%=sellRatDob.getFrequency()!=null?sellRatDob.getFrequency():""%></td>
<%
			double[] chargesValue	=	sellRatDob.getChargeRatesValues();
			int chargesLength		=	chargesValue.length;
		
			for(int k=0;k<count;k++)
			{
%>
						<td><%=decimals.format(chargesValue[k])%></td>
<%
			}
%>
				<td align="right"><%=sellRatDob.getNotes()!=null?sellRatDob.getNotes():""%></td>
				<!-- //Modified by Mohan for Issue No.219976 on 2-11-2010 -->
				<td align="right"><%=sellRatDob.getExtNotes()!=null?sellRatDob.getExtNotes():""%></td>
			  </tr>
			 
<%
							String[] ratedesc =sellRatDob.getRateDescription();
						Set rdesc	= new LinkedHashSet();
						ArrayList surchargeDesc	= new ArrayList();
						for(String s: ratedesc)
							rdesc.add(s);
						Iterator it	= rdesc.iterator();
						while(it.hasNext())
								surchargeDesc.add(it.next());

						// Added By Kishore Podili for SurCharge Currency 

						String[] schCurrencyArray  =sellRatDob.getSurChargeCurency();
					/*		for(String st:schCurrencyArray)
						System.out.println("schCurrencyArray---------"+st);
						Set schCurrencySet	= new LinkedHashSet();
						ArrayList schCurrencyList	= new ArrayList();
						for(String s: schCurrencyArray)
							schCurrencySet.add(s);
						 it	= schCurrencySet.iterator();
						while(it.hasNext())
								schCurrencyList.add(it.next());
								Commented by govind for unable to view/Modify  the RSR when we are having same currecies*/

						if(count<wtBreakLength)
			         	{
						  if((wtBreakLength<count*2+8)||"slab".equalsIgnoreCase(weigthBrake)||"flat".equalsIgnoreCase(weigthBrake))
					       {
							
	  							  for (int r=1;r<surchargeDesc.size();r++){
								 String diffDesc	= (String)surchargeDesc.get(r);
								 String schCurr = schCurrencyArray[r-1];//(String)schCurrencyList.get(r);
								  int desCount	=	0;
						 %>
						  <tr class="formdata">
						  <td>&nbsp</td>
				    <td colspan=2><%=diffDesc.substring(0,(diffDesc.length()-3))%></td> <!-- Added By Kishore for Sur Charge Desc -->
					<td> Currency </td>
				  <%for(int m=count;m<wtBreakLength;m++)
				  {
					 if(weighttBkValues[m]!=null&&weighttBkValues[m]!="" && (diffDesc.equalsIgnoreCase(ratedesc[m]) ))
					  {
						 desCount++;
						 // Added by Kishore on 16Feb2011 to display slabs as min,slab and flat etc
					   if("2".equals(shipmentMode)&&"FCL".equals(consoleTpe))
						  {
								weighttBkValues[m] = weighttBkValues[m].length() > 4?weighttBkValues[m].substring(0,4):weighttBkValues[m];
						  }
					   else	
							weighttBkValues[m] = weighttBkValues[m].length() > 5?weighttBkValues[m].substring(5,weighttBkValues[m].length()):weighttBkValues[m];
						 
						 %>
						 <td><%=weighttBkValues[m]%></td>
<%			
			     
					  }
					  
				} 
				 // System.out.println("count..."+count);
				  				  //System.out.println("desCount..."+desCount);
					 // temp	=	count*2+8-wtBreakLength;
	 					  temp	=	8-(desCount+2)+count;
				  				  //System.out.println("temp..."+temp);
					  for(int s=0;s<temp;s++)
					 {
%>
			  <td>&nbsp;</td>
			 <%}%>
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
			   <td colspan=2>&nbsp;</td>
		
                   <%for(int n=count;n<chargesLength;n++)
				   {
					   if(n== count){%><td><%=schCurrencyArray[n]%></td><%}
				if(diffDesc.equalsIgnoreCase(ratedesc[n])){
					%>
				  <td><%=chargesValue[n]%>
                </td>
				
		<%	  }
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
					 
				   for(int m=count;m<count*2+8;m++)
				  {
					  if(weighttBkValues[m]!=null&&weighttBkValues[m]!="")
					  {
							// Added by Kishore on 16Feb2011 to display slabs as min,slab and flat etc
					   if("2".equals(shipmentMode)&&"FCL".equals(consoleTpe))
						  {
								weighttBkValues[m] = weighttBkValues[m].length() > 4?weighttBkValues[m].substring(0,4):weighttBkValues[m];
						  }
					   else	
							weighttBkValues[m] = weighttBkValues[m].length() > 5?weighttBkValues[m].substring(5,weighttBkValues[m].length()):weighttBkValues[m];


						  %>
				     <td><%=weighttBkValues[m]%></td>
<%			        }
			     } 
		%>		  
					  
					
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
                   <%for(int n=count;n<count*2+8;n++)
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
				   for(int m=count*2+8;m<wtBreakLength;m++)
				  {
					   if(weighttBkValues[m]!=null&&weighttBkValues[m]!="")
					  {
						   temp++;%>
						<td><%=weighttBkValues[m]%></td>
<%			         }
			     } 
				  
			  for(int s=temp;s<count+8;s++)
					{
					%>		  <td>&nbsp;</td>
								 <%}%>
              </tr>
			   <tr class="formdata">
			   <td>&nbsp;</td>
                   <%for(int n=count*2+8;n<chargesLength;n++)
				  { 
					%>
				  
				  <td><%=chargesValue[n]%>
                </td>
				<%
				  }
				  for(int s=temp;s<count+8;s++)
					{%>
				 <td>&nbsp;</td>
			  <%}%>
			   </tr>
			   <%}
				}
			}%>
			<tr>
				  <td valign="middle" colspan="10" >
<%
					int  currentPageNo	=	0;
					if(sellRatesDob.getPageNo()!=null)
						currentPageNo = Integer.parseInt(sellRatesDob.getPageNo());

					if(currentPageNo != 1)
					{
%>
						<a href="#" onClick="functionCall('<%=operation%>','<%=currentPageNo-1%>')"><img src="images/Toolbar_backward.gif"  alt="Previous Page"></a>
 <%					}        
					
					int multiplier = 1;
					int startNo = 0;
					int endNo = 0;

					if(currentPageNo > noOfSegments)
					{
						multiplier = currentPageNo / noOfSegments;
						if(currentPageNo % noOfSegments != 0)
							startNo = noOfSegments * multiplier;
						else
							startNo = noOfSegments * (multiplier - 1);
					}
					if(noofPages > startNo)
					{
						if(noofPages > startNo + noOfSegments)
							endNo = startNo + noOfSegments;
						else
							endNo = startNo + (noofPages - startNo);
					} else
					{
						endNo = noofPages;
					}
					for(int i = startNo; i < endNo; i++)
						if(currentPageNo == i + 1){%>
							<font size="3"><B><%=(i + 1)%></B></font>&nbsp;
						<%}else{%>
							<a href="#" onClick="functionCall('<%=operation%>','<%=(i + 1)%>')"><%=(i + 1)%></a>&nbsp;
<%	                       }
				if(currentPageNo != noofPages)
				  {
%>
						<a href="#" onClick="functionCall('<%=operation%>','<%=currentPageNo+1%>')"><img src="images/Toolbar_forward.gif" alt="Next Page"></a>
 <%					
				  }
%>
                </td>
			</tr>
		 </table>
<%
	}
	else
	{
%>
		<table width="100%"   border="0" cellpadding="4" cellspacing="1">
			<tr class='formdata'> 
				<td colspan="6" align="center">
					<b>No Rates Are Defined for the Specified Details.<b>
				</td>
			</tr>
			 </table>
<%
	}
%>
	<table width="100%"   border="0" cellpadding="4" cellspacing="1">
		<tr><td colspan=10 align="right">
			<input type="hidden" name="Operation" value='<%=operation%>'>
			<input type="hidden" name="subOperation" value='Enter'>
			<input type="submit" name="Submit" value="Continue" class='input'>
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
			//Logger.error(FILE_NAME,"Error in QMSRecommendedSelRatesView.jsp "+e);
      logger.error(FILE_NAME+"Error in QMSRecommendedSelRatesView.jsp "+e);
		
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