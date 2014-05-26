<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSQuoteFreightSellRates.jsp
Product Name		: QMS
Module Name			: Quote
Task				: Adding/Modify
Date started		: 30th Aug 2005
Date modified		:
Author    			: S Anil Kumar
Related Document	: CR_DHLQMS_1007

--%>

<%@page import = "java.util.ArrayList,
					java.util.Set,
					java.util.LinkedHashSet,
					java.util.Iterator,
					java.util.StringTokenizer,
				  java.sql.Timestamp,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.operations.quote.dob.QuoteMasterDOB,
				  com.qms.operations.quote.dob.QuoteFinalDOB,
				  com.qms.operations.quote.dob.QuoteFreightLegSellRates,
				  com.qms.operations.quote.dob.QuoteFreightRSRCSRDOB"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSQuoteFreightSellRates.jsp"; 
  private   String toTitleCase(String str){
			StringBuffer sb = new StringBuffer();     
			str = str.toLowerCase();
			str =str.replace('(','~');
			str =str.replace(')','#');
			StringTokenizer strTitleCase = new StringTokenizer(str);
			while(strTitleCase.hasMoreTokens()){
				String s = strTitleCase.nextToken();
				sb.append(s.replaceFirst(s.substring(0,1),s.substring(0,1).toUpperCase()) + " ");
			}
			str= sb.toString();
		  str =str.replace('~','(');
		  str =str.replace('#',')');
			return str;
		}
  %>

<%
long start = System.currentTimeMillis();
logger  = Logger.getLogger(FILE_NAME);

QuoteFinalDOB					finalDOB			=	null;
QuoteMasterDOB					masterDOB			=	null;
ArrayList						freightRates		=	null;
ArrayList						sellRates			=	null;
ArrayList						frtTiedCustInfo		=	null;
String[]						slabWeightBreaks	=	null;
String[]						listWeightBreaks	=	null;
QuoteFreightLegSellRates		legRateDetails		=	null;
QuoteFreightLegSellRates		tiedCustLegDOB		=	null;
QuoteFreightRSRCSRDOB			sellRateDOB			=	null;
String							shipmentMode		=	null;
String							weightBreak			=	null;
String[]						rate				=	null;
String[]                        checked             =   null; // added by VALSKHMI
String[]						str					=	null;
String							operation			=	null;
int								freightRatesSize	=	0;
int								sellRatesSize		=	0;
int								tableCount			=	0;
int								tiedCustInfoSize	=	0;

ESupplyDateUtility				eSupplyDateUtility	=	new ESupplyDateUtility();

Timestamp						effectiveFrom		=	null;
Timestamp						validUpto			=	null;

String							effectiveFromStr	=	null;
String							validUptoStr		=	null;

String							checkedFlag			=	"";
boolean            disabled            =  true;

boolean							noRatesFlag			=	false;
boolean							displayFlag			=	false;
int                             tempcount           =  0;
String                          quoteName          = null; //@@Added by VLAKSHMI for the WPBN issue-167677
String                          quoteStatus         = null;
String                     completeFlag         = null;
try
{
	quoteStatus = request.getParameter("quoteStatus");
	completeFlag = request.getParameter("completeFlag");
	eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());

	java.text.DecimalFormat df	=new java.text.DecimalFormat("##0.00");

	operation			=	request.getParameter("Operation");
	quoteName			=	request.getParameter("quoteName"); // @@Added by VLAKSHMI for the WPBN issue-167677
	finalDOB			=	(QuoteFinalDOB)request.getAttribute("finalDOB");
	if(finalDOB==null)
		finalDOB		=	(QuoteFinalDOB)session.getAttribute("finalDOB");
	masterDOB			=	finalDOB.getMasterDOB();
	freightRates		=	finalDOB.getLegDetails();
	//frtTiedCustInfo     =	finalDOB.getTiedCustomerInfoFreightList();

	freightRatesSize	=	freightRates.size();
	if(!finalDOB.isMultiModalQuote())
	{
		if(masterDOB.getShipmentMode()==1)
			shipmentMode	=	"AIR";
		else if(masterDOB.getShipmentMode()==2)
			shipmentMode	=	"SEA";
		else if(masterDOB.getShipmentMode()==4)
			shipmentMode	=	"TRUCK";
	}
	else
	{
		shipmentMode	=	"MULTI-MODAL";
	}

%>

<HTML>
<HEAD>
<TITLE>Quote - Freight Sell Rates</TITLE>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">

<!-- <script src="<%=request.getContextPath()%>/html/TableSorting.js"></script> -->
<script language="javascript">

var noOfLegs	=	<%=freightRatesSize%>;
function validate()
{

	for(i=0;i<noOfLegs;i++)
	{
		var radio	=	document.getElementsByName("leg"+i);
		if(radio.length > 0 && document.getElementById("hid"+i).value.length==0 && document.forms[0].btnName.value=='Next >>')
		{
			alert("Please select one sell rate in Leg "+(i+1));
			radio[0].focus();
			return false;
		}
	}
}


function setSelectedIndex(obj,index,index1,rate)
{
	document.getElementById("hid"+index).value	=	obj.value;
		for(var i=0;i<noOfLegs;i++)
	 	{
		radio = document.getElementsByName('leg'+i);
		 for(var j=0;j<radio.length;j++)
			{
//@@ Added by subrahmanyam for the enhancement 180164
			 var count=0;
			 var count1=0;
//			   for(var k=0;k<rate;k++) //@@ Commented by subrahmanyam for the issue: 180164
		if(document.getElementById('wtBreakType'+i+j).value=='LIST' && document.getElementById('consoleType'+i+j).value=='FCL'){
//			alert('checkCount'+i+j)
	//		alert(document.getElementById('checkCount'+i+j).value)
				rate = document.getElementById('checkCount'+i+j).value;
		}

	         for(var k=0;k<rate;k++)//@@ Added by subrahmanyam for the Issue: 180164
		   {
 			  if(radio[j].checked)
			    {
				  //@@ COmmented by subrahmanyam for the Enhancement 180164 on 31/08/09
				
	  					// modified by VLAKSHMI for issue 155535 on 27/01/09
					if(document.getElementById('rateVal'+index1+k).value!='-' && document.getElementById('rateVal'+index1+k).value!=null)
				   document.getElementById('checkedBox'+index1+k).disabled=false;
			

					//@@ Added by subrahmanyam for the Enhancement on 31/08/09
	/* 					if(count==0 || !(document.getElementById('wtBreakType'+i+j).value=='LIST' && document.getElementById('consoleType'+i+j).value=='FCL')){
							//alert("if checked true: count=0: "+index1+k)
					  if(document.getElementById('rateVal'+index1+k).value!='-' && document.getElementById('rateVal'+index1+k).value!=null)
				   document.getElementById('checkedBox'+index1+k).disabled=false;
						}
						else
						{count1=count*5;
						//alert("if checked true: count!=0: "+index1+count1)
							 if(document.getElementById('rateVal'+index1+count1).value!='-' && document.getElementById('rateVal'+index1+count1).value!=null)
							 //document.getElementById('checkedBox'+index1+count1).disabled=false;// commented by subrahmanyam for pbn id: 186812
							 document.getElementById('checkedBox'+index1+k).disabled=false; // added by subrahmanyam for pbn id: 186812

						}*/
						//@@ ended by subrahmanyam for the Enhancement 180164
					  }


				 else{

						if(index1!=i+j)
					 {
						// modified by VLAKSHMI for issue 155535 on 27/01/09
							 if(document.getElementById('wtBreakType'+i+j).value=='LIST' && document.getElementById('consoleType'+i+j).value=='FCL')
							 {
								 //@@ Commented by subrahmanyam for the ENhancement 180164 on 31/08/09
								
								 if(document.getElementById('rateVal'+i+j+k).value!='-' && document.getElementById('rateVal'+i+j+k).value!=null)
									 {
									 document.getElementById('checkedBox'+i+j+k).checked=false;
									 document.getElementById('checkedBox'+i+j+k).disabled=true;
									 }
								
								 //@@ Added by subrahmanyam for the ENhancement 180164 on 31/08/09
							/* 	if(count==0){
									  if(document.getElementById('rateVal'+i+j+k).value!='-' && document.getElementById('rateVal'+i+j+k).value!=null)
									 {
									 document.getElementById('checkedBox'+i+j+k).checked=false;
									 document.getElementById('checkedBox'+i+j+k).disabled=true;
									 }
								}
								else
								 {//count1=count*5;
									 if(document.getElementById('rateVal'+i+j+count1).value!='-' && document.getElementById('rateVal'+i+j+count1).value!=null)
									 {
										 // commented by subrahmanyam for the pbn id: 186812 on 22/oct/09
										 //document.getElementById('checkedBox'+i+j+count1).checked=false;
										 //document.getElementById('checkedBox'+i+j+count1).disabled=true;
										// Added by subrahmanyam for the pbn id: 186812 on 22/oct/09
									 document.getElementById('checkedBox'+i+j+k).checked=false;
									 document.getElementById('checkedBox'+i+j+k).disabled=true;
									 }
								 } */
								 //@@ ended by subrahmanyamfor the enhancement 180164 on 31/08/09



							 }
					 }
					  }
	            //}
					count=count+1;//@@ Added by subrahmanyam for the Enhancement 180164 on 31/08/09
				}
			}


}
}
	function setSelectedIndex1(obj,index,container)
{

	if(obj.checked)
	document.getElementById("con"+index).value	=	container;
	else
		document.getElementById("con"+index).value="";

}
		function setIndexForChecked1(obj,sellrate,rate)
	{

		var checkedBox = '';
		var radio = '';
		var count =0;


		var radiochecked=false;


for(var i=0;i<noOfLegs;i++)
	   	{

			radio = document.getElementsByName('leg'+i);


	 	    for(var j=0;j<radio.length;j++)
			{
			  if(radio[j].checked)
			    {
				 
				  if(document.getElementById('wtBreakType'+i+j).value!='LIST' && document.getElementById('consoleType'+i+j).value!='FCL' || document.getElementById('wtBreakType'+i+j).value=='LIST' && document.getElementById('consoleType'+i+j).value!='FCL')
					  count="NOT A LIST";

				          for(var k=0;k<rate;k++)




						  {

				          checkedBox = document.getElementsByName('checkedBox'+i+j+k)


						  }


					  radiochecked=true;
			 }
			  		  if(count!='NOT A LIST')
				{


					for(var k=0;k<rate;k++)

						  {

				          checkedBox = document.getElementsByName('checkedBox'+i+j+k)
		      for(var x=0;x<checkedBox.length;x++)


			          {

			  			 if(!checkedBox[x].getAttribute("disabled"))
							 {

								 if(checkedBox[x].checked){
							       document.getElementById("con"+i+j+k).value	=	checkedBox[x].value;
								   count++;
								  }

							 }

					  }




							  }
			}

		   }

		}
			 if(count==0 && count!='NOT A LIST')
					 {
			   alert("Please select the corresponding checkbox");
			   return false;
				}

	}
function setIndexForChecked(btn)
{
	var radio = '';

	for(var i=0;i<noOfLegs;i++)
	{
		radio = document.getElementsByName('leg'+i);

		for(var j=0;j<radio.length;j++)
		{
			if(radio[j].checked)
			{

				document.getElementById("hid"+i).value	=	radio[j].value;

			}
		}
	}

	setName(btn);
}
function setName(btn)
{
	document.forms[0].btnName.value=btn.value;
}

function initTables()
{
	tables = document.getElementsByTagName("table");

	for (var i = 0; i < tables.length; i++)
	{
		if(tables[i].id!=null && tables[i].id.length!=0)
		{
			maxNCol = tables[i].rows[tables[i].rows.length-1].cells.length;
			initTable(tables[i].id,"0,1,2,"+(maxNCol-1));
		}
 	}

}

</script>
</HEAD>

<BODY >
       <form method="post" name="sellRates" action="QMSQuoteController" onSubmit='return validate()'>

	<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
		<tbody>
			<tr color='#FFFFFF'>
				<td>
				<table border="0" cellPadding="4" cellSpacing="1" width="100%">
					<tbody>
					 <!-- @@Modified by VLAKSHMI for the WPBN issue-167677 -->
						<tr class="formlabel">
						<%if("Modify".equalsIgnoreCase(operation)) {%>
							<td colspan=""><p ><b><%=shipmentMode%> FREIGHT SELL RATES(<%=quoteName%>,<%=quoteStatus%>,<%=completeFlag%>) - <%=operation!=null?operation.toUpperCase():""%></b></td>
							<%}else { %>
<td colspan=""><p ><b><%=shipmentMode%> FREIGHT SELL RATES- <%=operation!=null?operation.toUpperCase():""%></b></td>
							<%}%>
						</tr>
						 <!-- @@WPBN issue-167677 -->
					</tbody>
				</table>
<%
		if(request.getAttribute("errors")!=null)
		{
%>
				<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
					<tbody>
							<tr color="#FFFFFF">
								<td><font face="Verdana" size="2" color='red'><b>This form has not been submitted because of the following error(s) : </b> <br><br>
									<%=(String)request.getAttribute("errors")%></font></td>
							</tr>
						</tbody>
				</table>

<%
		}

	for(int i=0;i<freightRatesSize;i++)//@@noOfLegs
	{
		//logger.info("freightRatesSize"+freightRatesSize+i)
		noRatesFlag			=	false;
		legRateDetails		=	(QuoteFreightLegSellRates)freightRates.get(i);

		slabWeightBreaks	=	legRateDetails.getSlabWeightBreaks();

		listWeightBreaks	=	legRateDetails.getListWeightBreaks();

		sellRates			=	legRateDetails.getRates();

		//tiedCustLegDOB		=	(QuoteFreightLegSellRates)frtTiedCustInfo.get(i);
             //logger.info("freightRatesSize::"+freightRatesSize+i);
		boolean headerSet		=	false;
		boolean flatHeaderSet	=	false;
		boolean slabHeaderSet	=	false;
		boolean listHeaderSet	=	false;
		boolean commonHeader	=	false;

		if(!legRateDetails.isSpotRatesFlag() && tiedCustLegDOB==null)
		{

			if(sellRates!=null)
				sellRatesSize	=	sellRates.size();
			else
				sellRatesSize	=	0;

			if(sellRatesSize > 0)
				noRatesFlag	= true;

//System.out.println("sellRatesSize.."+sellRatesSize);
			for(int j=0;j<sellRatesSize;j++)
			{

				sellRateDOB		=	(QuoteFreightRSRCSRDOB)sellRates.get(j);
				int tempfrtCount				=	0;
				String[] rateDesc= sellRateDOB.getRateDescriptions();
				effectiveFrom	=   sellRateDOB.getEffDate();
				checkedFlag		=	sellRateDOB.getSelectedFlag();

				if(effectiveFrom!=null)
				{
					str				=	eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
					effectiveFromStr= str[0];
				}
				else
				{
					effectiveFromStr	=	"";
				}

				validUpto		=   sellRateDOB.getValidUpTo();

				if(validUpto!=null)
				{
					str				= eSupplyDateUtility.getDisplayStringArray(validUpto);
					validUptoStr	= str[0];
				}
				else
				{
					validUptoStr	=	"";
				}

				if(!headerSet)
				{
					headerSet	=	true;
%>
					<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
						<tbody>
							<tr class="formdata" >
								<td>
									<font size='0.25%'><b>Select Freight Rates: </b><%=legRateDetails.getOrigin()+"-"+legRateDetails.getDestination()%></font>
								</td>
							</tr>
						</tbody>
					</table>
<%
				}

				if("Flat".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !flatHeaderSet)
				{				//	System.out.println("----flat rates------------");
//				for(String flat: sellRateDOB.getFlatWeightBreaks())
	//				System.out.println(flat);

					commonHeader=false;
%>
					<table border="0" cellPadding="4" cellSpacing="1" width="100%"  bgcolor='#FFFFFF'>
						<tbody>
							<tr class="formdata" >
							  <td width='10%'>
								<font size='0.25%'><b>Weight Break:</b> FLAT</font>
							  </td>
							</tr>
						</tbody>
					</table>
<%
				}
				else if("Slab".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !slabHeaderSet)
				{	
					commonHeader=false;
					if(listHeaderSet)
					{
%>						</tbody>
						</table>
<%					}
%>

					<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF' >
						<tbody>
							<tr class="formdata">
								<font size='0.25%'><td ><b>Weight Break:</b> SLAB</td></font>
							</tr>
						</tbody>
					</table>
<%
				}
				else if("List".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !listHeaderSet)
				{
					commonHeader=false;
					if(flatHeaderSet)
					{
%>						</tbody>
						</table>
<%					}
%>
					<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
						<tbody>
							<tr class="formdata">
								<font size='0.25%'><td><b>Weight Break:</b> LIST</td></font>
							</tr>
						</tbody>
					</table>
<%
				}
				if(!commonHeader)
				{
					commonHeader=true;
					if("List".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !listHeaderSet && "FCL".equalsIgnoreCase(sellRateDOB.getConsoleType()))
					{
%>

					<table id="table<%=tableCount%>" border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
						<tbody>
							<tr valign="top" class='formheader'align="center" >
				<TH ><font size='0.25%'>Select</TH></font>
				<TH ><font size='0.25%%'>Select</TH></font>
			    <TH ><font size='0.25%'>Org</TH></font>
	    		<TH ><font size='0.25%'>Dest</TH></font>
		     	<TH ><font size='0.25%'>Carrier</TH></font>
				<TH ><font size='0.25%'>Service<br>Level</TH></font>
			    <TH ><font size='0.25%'>Frequency</TH></font>
							<%if("AIR".equalsIgnoreCase(shipmentMode)||"TRUCK".equalsIgnoreCase(shipmentMode)){%>
				<TH ><font size='0.25%'>Transit<br>Time</TH></font>
							<%}	else{%>
				<TH ><font size='0.25%'>Transit<br>Days</TH></font>
							<%}

						listHeaderSet=true;
						//for(int k=0;k<listWeightBreaks.length;k++)
						//{<%=listWeightBreaks[k]
%>
								<TH><font size='0.25%'>Container<br>Type</TH></font>
								<TH><font size='0.25%'>Rates</TH></font>
<!-- 								<TH><font size='0.25%'>BAF</TH></font>
								<TH><font size='0.25%'>CAF</TH></font>
								<TH><font size='0.25%'>CSF</TH></font>
								<TH><font size='0.25%'>PSS</TH></font>
 --><%
						//}

%>
								<!--TH>Weight Class</TH-->
								<TH><font size='0.25%'>RSR/<br>CSR/BR</TH></font>
								<TH><font size='0.25%'>Defined<br>by</TH></font>
								<TH><font size='0.25%'>Currency<br>Id</TH></font>
								<TH><font size='0.25%'>Effective<br>From</TH></font>
								<TH><font size='0.25%'>Valid<br>Upto</TH></font>
								<TH><font size='0.25%'>Density Ratio</TH></font>
								<TH><font size='0.25%'>Internal Notes</TH></font>
								<TH><font size='0.25%'>External Notes</TH></font>
							</tr>
<%} else {%>

		<table id="table<%=tableCount%>" border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
						<tbody>
							<tr valign="top" class='formheader'align="center" >
								<TH ><font size='0.25%'>Select</TH></font>
								<TH ><font size='0.25%'>Org</TH></font>
								<TH ><font size='0.25%'>Dest</TH></font>
								<TH ><font size='0.25%'>Carrier</TH></font>
								<TH ><font size='0.25%'>Service<br>Level</TH></font>
								<TH ><font size='0.25%'>Frequency</TH></font>
								<%if("AIR".equalsIgnoreCase(shipmentMode)||"TRUCK".equalsIgnoreCase(shipmentMode)){%>
								<TH><font size='1%'>Transit<br>Time</TH></font>
							<%}
							else{%>
									<TH><font size='0.25'>Transit<br>Days</TH></font>
								<%}%>
<%
					if("Flat".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !flatHeaderSet)
					{
						flatHeaderSet=true;
							String[] flatwtBreaks= sellRateDOB.getFlatWeightBreaks();
						for(int k=0;k<flatwtBreaks.length;k++)
						{
							if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k])){

%>
								<TH nowrap><font size='0.5%'><%=flatwtBreaks[k]%></TH></font>
<%}
						}


%>
							<!-- 	<TH ><font size='0.25%'>MIN</TH></font>
								<TH ><font size='0.25%'>FLAT</TH></font> -->
								<!-- @@ Added by subrahmanyam for the Enhancement 180161 on 31/08/09 -->
								<%
									//if("AIR".equalsIgnoreCase(shipmentMode) || 1==legRateDetails.getShipmentMode()){
									%>
								<!-- <TH ><font size='0.25%'>FS<br>BASIC</TH></font>
								<TH ><font size='0.25%'>FS<br>MIN</TH></font>
								<TH ><font size='0.25%'>FS<br>KG</TH></font>
								<TH ><font size='0.25%'>SS<br>BASIC</TH></font>
								<TH ><font size='0.25%'>SS<br>MIN</TH></font>
								<TH ><font size='0.25%'>SS<br>KG</TH></font> -->
								<%
									//}else if("TRUCK".equalsIgnoreCase(shipmentMode) || 4==legRateDetails.getShipmentMode()){
									%><!-- Added by subrahmanyam for 192404 on 16-dec-09-->
								<!-- <TH ><font size='0.25%'>SURCHARGE</TH></font> -->
								<%//}
								//else{%>
							<!-- 	<TH ><font size='0.25%'>CAF<br>MIN</TH></font>
								<TH ><font size='0.25%'>CAF<br>%</TH></font>
								<TH ><font size='0.25%'>BAF<br>MIN</TH></font>
								<TH ><font size='0.25%'>BAF<br>M3</TH></font>
								<TH ><font size='0.25%'>PSS<br>MIN</TH></font>
								<TH ><font size='0.25%'>PSS<br>M3</TH></font>
								<TH ><font size='0.25%'>CSF</TH></font> -->
						<!-- @@ Ended by subrahmanyam for the Enhancement 180161 on 31/08/09 -->
<%//}
					}
					else if("Slab".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !slabHeaderSet)
					{
						slabHeaderSet=true;
						String[] slabWtBreaks= sellRateDOB.getSlabWeightBreaks();
						for(int k=0;k<slabWtBreaks.length;k++)
						{
								if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]) || "-".equalsIgnoreCase(rateDesc[k])){
//									System.out.println("slabWeightBreaks[k]..."+slabWeightBreaks[k]);
%>
								<TH nowrap><font size='0.5%'><%=slabWtBreaks[k]%></TH></font>
<%
								}
						}
					}else if("List".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !listHeaderSet && "FTL".equalsIgnoreCase(sellRateDOB.getConsoleType())){
						listHeaderSet=true;
						for(int k=0;k<listWeightBreaks.length;k++)
						{ 

%>
						<TH nowrap><font size='0.5%'><%=listWeightBreaks[k]%></TH></font>
					<%	}}
						%>

								<!--TH>Weight Class</TH-->
								<TH><font size='0.25%'>RSR<br>CSR/BR</TH></font>
								<TH><font size='0.25%'>Defined<br>By</TH></font>
								<TH><font size='0.25%'>Currency<br>Id</TH></font>
								<TH><font size='0.25%'>Effective<br>From</TH></font>
								<TH><font size='0.25%'>Valid<br>Upto</TH></font>
								<TH><font size='0.25%'>Density Ratio</TH></font>
								<TH><font size='0.25%'>Internal Notes</TH></font>
								<TH><font size='0.25%'>External Notes</TH></font>
							</tr>

							<%	}
					tableCount++;
				} //else{
					if("List".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && listHeaderSet && "FCL".equalsIgnoreCase(sellRateDOB.getConsoleType()))
					{   tempcount           =   0;
						rate	=	sellRateDOB.getChargeRates();
						listWeightBreaks	=	sellRateDOB.getListWeightBreaks();
						checked =   sellRateDOB.getCheckedFalg();
	                    disabled=true;
						int ratesSize=rate.length;
						int containerCount	=	0;
						int checkIndex =0;//added by subrahmanyam for the pbn id: 186812 on 22/oct/09
							for(int k=0;k<ratesSize;k++)
								{
									if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]))
											containerCount++;
								}

								for(int k=0;k<ratesSize;k++)
								{
								if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]))
									{//##2
					            if(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k])) //commented by subrahmanyam for 180161
								  // if(rate[k]!=null)//added by subrahmanyam for 180161
									{ //##1
%>
							<tr valign="top" class='formdata' align="center">
							<% if(tempcount==0) {%>
								<td><input type="radio" value="<%=j%>" name="leg<%=i%>"  <%="Y".equalsIgnoreCase(checkedFlag)?"checked":""%> onClick="setSelectedIndex(this,'<%=i%>','<%=i%><%=j%>','<%=containerCount%>');">
								<input type="hidden" id="wtBreakType<%=i%><%=j%>" name="wtBreakType" value='<%=sellRateDOB.getWeightBreakType()%>'>
								<input type="hidden" id="consoleType<%=i%><%=j%>" name="consoleType" value='<%=sellRateDOB.getConsoleType()%>'>
								<input type="hidden" id="checkCount<%=i%><%=j%>" name="checkCount" value='<%=containerCount%>'>
								</td>
								<% tempcount++;} else { %>
								<!-- Modified by subrahmanyam for the pbn id: 186812 on 22/oct/09, checkIndex was replaced in the place of k -->
								<td></td> <%}  if("Y".equalsIgnoreCase(checkedFlag)) { disabled=false;%>
								<td><input type="checkBox"  value="<%=listWeightBreaks[k]%>"   name="checkedBox<%=i%><%=j%><%=checkIndex%>" id="checkedBox<%=i%><%=j%><%=checkIndex%>"  <%="Y".equalsIgnoreCase(checked[k])?"checked":""%> onClick="setSelectedIndex1(this,'<%=i%><%=j%><%=checkIndex%>','<%=listWeightBreaks[k]%>');">
								
								<%}else{%>

<td><input type="checkBox"  value="<%=listWeightBreaks[k]%>"   name="checkedBox<%=i%><%=j%><%=checkIndex%>" id="checkedBox<%=i%><%=j%><%=checkIndex%>" disabled <%="Y".equalsIgnoreCase(checked[k])?"checked":""%> onClick="setSelectedIndex1(this,'<%=i%><%=j%><%=checkIndex%>','<%=listWeightBreaks[k]%>');">
<%}%>

								<input type="hidden" id="con<%=i%><%=j%><%=checkIndex%>" name="con<%=i%><%=j%>">


								</td>
								<td><font size='0.25%'><%=sellRateDOB.getOrigin()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getDestination()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCarrierId()%></td></font>
								<!--@@Modified by kameswari for the WPBN issue-31330-->
								<!--td><%//=sellRateDOB.getServiceLevelId()%></td-->
								<td><font size='0.25%'><%=sellRateDOB.getServiceLevelDesc()%></td></font>
								<!-- @@Kameswari -->
								<td><font size='0.25%'><%=sellRateDOB.getFrequency()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getTransitTime()%></td></font>
                                  <td><font size='0.25%'><%=listWeightBreaks[k]%></td></font>
									<td  nowrap><font size='0.25%'><%=(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k]))?df.format(Double.parseDouble(rate[k])):"-"%></td></font>
								<!--td><%//=sellRateDOB.getWeightClass()%></td-->
								<td><font size='0.25%'><%=sellRateDOB.getRsrOrCsrFlag()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCreatedTerminalId()!=null?sellRateDOB.getCreatedTerminalId():""%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCurrency()!=null?sellRateDOB.getCurrency():""%></td></font>
								<td nowrap><font size='0.25%'><%=effectiveFromStr!=null?effectiveFromStr:""%></td></font>
								<td nowrap><font size='0.25%'><%=validUptoStr!=null?validUptoStr:""%></td></font>
                                <td nowrap><font size='0.25%'><%=sellRateDOB.getDensityRatio()%></td></font>
						<%	if(k==0){%>
								
								<td><font size='0.25%'><input class="text" id="Notes<%=j%>" name="notes<%=j%>" size="8"  maxLength="1000" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" title="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" readOnly></td></font>
								<td><font size='0.25%'><input class="text" id="extNotes<%=j%>" name="extNotes<%=j%>" size="8"  maxLength="1000" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" title="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" readOnly></td></font>
								<%}else{%>
                                  <td>&nbsp;&nbsp;</td>
								  <td>&nbsp;&nbsp;</td>
                                 <%}%>
							</tr>
							<%
				}//End of ##1
								}// End of ##2
								%>
								<!--@@ Commented by subrahmanyam for the Enhancement 180164 on 31/08/09  -->
								 <input type="hidden" id="rateVal<%=i%><%=j%><%=k%>" name="rateVal" value='<%=rate[k]%>'>
								
			<%		checkIndex++;//added by subrahmanyam for the pbn id:186812 on 22/oct/09
					}

						Set rdesc	= new LinkedHashSet();
						ArrayList surchargeDesc	= new ArrayList();
						for(String s: rateDesc){
							if(!"-".equalsIgnoreCase(s))
							rdesc.add(s);
						}
						Iterator it	= rdesc.iterator();
						while(it.hasNext())
								surchargeDesc.add(it.next());
										for(int x=1;x<surchargeDesc.size();x++){//##3
												String surcharge	=	 (String	)surchargeDesc.get(x);
												int tempCount	=	0;
												int frtCount				=	0;
												int surChargeCount = 0;%>
						<tr valign="top" class='formdata' align="center">
						<td /><td/>
						<td > <font  size = '0.25%'><%=toTitleCase(surcharge.substring(0,surcharge.length()-3))%></td></font><!-- @@Added by Silpa.p on 6-06-11 for ratedesc font setting  -->
						<%	for(int sd=0;sd<ratesSize;sd++)
								{//##4
										if(!"A FREIGHT RATE".equalsIgnoreCase(rateDesc[sd]) && surcharge.equalsIgnoreCase(rateDesc[sd]) ) {
%>
												<td ><font size='0.25%'><%=listWeightBreaks[sd].substring(0,4)%></td></font><!--added by silpa.p on 9-06-11-->


	<%									}
								}//## 4
								//@@Modified by kiran.v on 27/07/2011 for Wpbn Issue- 260760
							for(int td=0;td<12;td++){//## td
							%>
								<td/>
							<%} //End of ##td%>
						</tr>
							<tr valign="top" class='formdata' align="center">
						<td /><td/><td/>
								<%	for(int sd=0;sd<ratesSize;sd++)
								{ //##5
										if(!"A FREIGHT RATE".equalsIgnoreCase(rateDesc[sd]) && surcharge.equalsIgnoreCase(rateDesc[sd]) ) {%>
												<td ><font size='0.25%'><%=rate[sd]%></td></font><!--added by silpa.p on 9-06-11-->


	<%									}
								}//end of ##5
								//@@Modified by kiran.v on 27/07/2011 for Wpbn Issue- 260760
							for(int td=0;td<12;td++){
							%>
								<td/>
							<%}%>
				<%			}//end of ##3

} else {  //logger.info("sellRateDOBsellRateDOB::"+sellRateDOB.getConsoleType()+j); %>


						 <tr valign="top" class='formdata' align="center">
								<td><input type="radio" value="<%=j%>" name="leg<%=i%>" <%="Y".equalsIgnoreCase(checkedFlag)?"checked":""%> onClick="setSelectedIndex(this,'<%=i%>');">
								<input type="hidden" id="wtBreakType<%=i%><%=j%>" name="wtBreakType" value='<%=sellRateDOB.getWeightBreakType()%>'>
								<input type="hidden" id="consoleType<%=i%><%=j%>" name="consoleType" value='<%=sellRateDOB.getConsoleType()%>'>
								</td>
								<td><font size='0.25%'><%=sellRateDOB.getOrigin()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getDestination()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCarrierId()%></td></font>
								<!--@@Modified by kameswari for the WPBN issue-31330-->
								<!--td><%//=sellRateDOB.getServiceLevelId()%></td-->
								<td><font size='0.25%'><%=sellRateDOB.getServiceLevelDesc()%></td></font>
								<!-- @@Kameswari -->
								<td><font size='0.25%'><%=sellRateDOB.getFrequency()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getTransitTime()%></td></font>
<%
								rate	=	sellRateDOB.getChargeRates();
								for(int k=0;k<rate.length;k++)
								{
									if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]) || "-".equalsIgnoreCase(rateDesc[k])){
//										System.out.println("rate[k]......"+rate[k]);
%>
									<td  nowrap><font size='0.25%'><%=(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k]))?df.format(Double.parseDouble(rate[k])):"-"%></td>
<%
								}}
%>
								<!--td><%//=sellRateDOB.getWeightClass()%></td-->
								<td><font size='0.25%'><%=sellRateDOB.getRsrOrCsrFlag()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCreatedTerminalId()!=null?sellRateDOB.getCreatedTerminalId():""%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCurrency()!=null?sellRateDOB.getCurrency():""%></td></font>
								<td nowrap><font size='0.25%'><%=effectiveFromStr!=null?effectiveFromStr:""%></td></font>
								<td nowrap><font size='0.25%'><%=validUptoStr!=null?validUptoStr:""%></td></font>
								 <td nowrap><font size='0.25%'><%=sellRateDOB.getDensityRatio()%></td></font>
								<td><font size='0.25%'><input class="text" id="Notes<%=j%>" name="notes<%=j%>" size="5" maxLength="1000" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" title="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" readOnly></td></font>
								<td><font size='0.25%'><input class="text" id="extNotes<%=j%>" name="extNotes<%=j%>" size="5"  maxLength="1000" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" title="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" readOnly></td></font>

							</tr>
<%						Set rdesc	= new LinkedHashSet();
						ArrayList surchargeDesc	= new ArrayList();
						for(String s: rateDesc){
							if(!"-".equalsIgnoreCase(s))
							rdesc.add(s);
						}
						Iterator it	= rdesc.iterator();
						while(it.hasNext())
								surchargeDesc.add(it.next());
%>

<%
	System.out.println("surchargeDesc..."+surchargeDesc);
	for(int x=1;x<surchargeDesc.size();x++){
	String surcharge	=	 (String	)surchargeDesc.get(x);
	int tempCount	=	0;	
	int frtCount				=	0;
	int surChargeCount = 0;
	%>
	<tr valign="top" class='formdata' align="center">
	<td />
	<td ><font  size = '0.25%'><%="truck".equalsIgnoreCase(shipmentMode)?surcharge:toTitleCase(surcharge.substring(0,surcharge.length()-3))%></td></font><!-- @@Added by Silpa.p on 6-06-11 for ratedesc font setting  -->
<%
							String[] flatwtBreaks= sellRateDOB.getFlatWeightBreaks();
							String[] slabWtBreaks= sellRateDOB.getSlabWeightBreaks();
	if("Flat".equalsIgnoreCase(sellRateDOB.getWeightBreakType())){
	for(int k=0;k<flatwtBreaks.length;k++)
	{
		if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]) || "-".equalsIgnoreCase(rateDesc[k]))
		frtCount ++;
	}
						for(int k=0;k<flatwtBreaks.length;k++)
						{
							if(surcharge.equalsIgnoreCase(rateDesc[k])){
								surChargeCount++;
%>
								<td nowrap><font size='0.5%'><%=(flatwtBreaks[k].length()>=5 && !"TRUCK".equalsIgnoreCase(shipmentMode))?toTitleCase(flatwtBreaks[k].substring(5)):
								toTitleCase(flatwtBreaks[k])%></td></font>
<%}
						}
	}
	else if("Slab".equalsIgnoreCase(sellRateDOB.getWeightBreakType())){
		for (String slab: slabWtBreaks)
			System.out.print(slab+',');
			System.out.println();
	for(int k=0;k<slabWtBreaks.length;k++)
	{
		if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]) || "-".equalsIgnoreCase(rateDesc[k]))
		frtCount ++;
	}
						for(int k=0;k<slabWtBreaks.length;k++)
						{
							if(surcharge.equalsIgnoreCase(rateDesc[k])){
								surChargeCount++;%>
								
								<td nowrap><font size='0.5%'>
								<%=(slabWtBreaks[k].length()>=5 && !"TRUCK".equalsIgnoreCase(shipmentMode))?toTitleCase(slabWtBreaks[k].substring(5)):toTitleCase(slabWtBreaks[k])%></td></font>
<%}
						}
	}
	//tempCount = 14+frtCount-(surChargeCount+2);
	//@@Modified by kiran.v on 27/07/2011 for Wpbn Issue- 260760
			tempCount = 15+frtCount-(surChargeCount+2);
			//System.out.println("----a-----tempCount------"+tempCount);
		//	System.out.println("----a-----frtCount------"+frtCount);
			//System.out.println("----a-----surChargeCount------"+surChargeCount);
			for(int td=0;td<tempCount;td++)
	{
%>
	<td/>
	<%}
%>
</tr>
<tr valign="top" class='formdata' align="center">
<td/><td/>

	<%							for(int k=0;k<rate.length;k++)
								{
									if(surcharge.equalsIgnoreCase(rateDesc[k]) ){
%>
									<td  nowrap><font size='0.25%'><%=(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k]))?df.format(Double.parseDouble(rate[k])):"-"%></td>
<%
									}
		}	
					//System.out.println("----b-----tempCount------"+tempCount);
					//System.out.println("----b-----frtCount------"+frtCount);
					//System.out.println("----b-----surChargeCount------"+surChargeCount);
		for(int td=0;td<tempCount;td++)
				{
%>
	<td/>
	<%}
%>
		</tr>
<%}
%>

<%}
			//}
			}//sellRatesSize
				if(!noRatesFlag)
				{
					displayFlag	=	true;
%>				<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
					<tbody>
					 <tr color="#FFFFFF">
						<td align="center">
						<font face="Verdana" size="2" color='red'>
						<b>No Rates Are Defined for the Leg <%=legRateDetails.getOrigin()!=null?legRateDetails.getOrigin():""%>-<%=legRateDetails.getDestination()!=null?legRateDetails.getDestination():""%>.</b></font>
						</td>
					</tr>
				</tbody>
				</table>
<%
				}
%>
				<input type="hidden" id="hid<%=i%>" name="hid<%=i%>">
<%		}
%>

<%
		}
%>


			<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
				<tbody>
					<tr class="text">
						<td></td>
						<td align="right" >
						<input type="hidden" name="Operation" value="<%=operation%>">
						 <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
						<input type="hidden" name="quoteName" value="<%= quoteName!=null? quoteName:""%>">
						 <input type="hidden" name="quoteStatus" value="<%= quoteStatus%>">
						  <input type="hidden" name="completeFlag" value="<%= completeFlag%>">
						 <!-- @@WPBN issue-167677 -->
						<input type="hidden" name="subOperation" value="SELLRATES">
						<input type="hidden" name="fromWhere" value="<%=(String)request.getAttribute("fromWhere")%>">
						<input type="hidden" name="btnName">
						<!--added  by silpa.p on 6-06-11 for spacing -->
						<!--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-->
						<input class="input" name="submit" type="submit" id='submit' value="<< Back" onClick="setIndexForChecked(this)"></td>
						<%if(!displayFlag){%>
						<td><input class="input" name="submit" type="submit" value="Next >>" onClick="setIndexForChecked(this);  return setIndexForChecked1(this,'<%=sellRatesSize%>','<%=rate.length%>')">
						<%}%>
						</td>
					</tr>
				</tbody>
			</table>
		</table>

			</td>
		</tr>
	</tbody>
</table>
</form>
</BODY>
</HTML>

<%
 logger.info("Total TIme taken for rendering JSP (2nd screen) in milli seconds::   " + (System.currentTimeMillis()-start) + " User Id::" + masterDOB.getUserId());
}
catch(Exception e)
{
	e.printStackTrace();
	//Logger.error(FILE_NAME,"Error in jsp"+e.toString());
  logger.error(FILE_NAME+"Error in jsp"+e.toString());
}
finally
{
	legRateDetails		=	null;

	slabWeightBreaks	=	null;

	listWeightBreaks	=	null;
	masterDOB			=	null;
	finalDOB			=	null;
	sellRateDOB			=	null;
}
%>