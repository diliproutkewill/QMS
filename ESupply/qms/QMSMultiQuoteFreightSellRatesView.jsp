																													 																															<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.									  
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSMultiQuoteFreightSellRatesView.jsp
Product Name		: QMS
Module Name			: Multi Quote
Task				: Detailed View
Date started		: 08 Jan 2011
Date modified		:
Author    			: Rakesh K
Related Document	: 

--%>

<%@page import = "java.util.ArrayList,
					java.util.Set,
					java.util.LinkedHashSet,
					java.util.StringTokenizer,
					java.util.Iterator,
				  java.sql.Timestamp,
				  java.util.Hashtable,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.operations.multiquote.dob.MultiQuoteMasterDOB,
				  com.qms.operations.multiquote.dob.MultiQuoteFinalDOB,
				  com.qms.operations.multiquote.dob.MultiQuoteFreightLegSellRates,
				  com.qms.operations.multiquote.dob.MultiQuoteFreightRSRCSRDOB"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSMultiQuoteFreightSellRatesView.jsp"; %>
 <%!
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

MultiQuoteFinalDOB					finalDOB			=	null;
MultiQuoteMasterDOB					masterDOB			=	null;
ArrayList						freightRates		=	null;
ArrayList						sellRates			=	null;
ArrayList						frtTiedCustInfo		=	null;
String[]						slabWeightBreaks	=	null;
String[]						listWeightBreaks	=	null;
MultiQuoteFreightLegSellRates		legRateDetails		=	null;
MultiQuoteFreightLegSellRates		tiedCustLegDOB		=	null;
MultiQuoteFreightRSRCSRDOB			sellRateDOB			=	null;
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

Boolean[]							noRatesFlag			=	null;
boolean							displayFlag			=	false;
int                             tempcount           =  0;
String                          quoteName          = null; //@@Added by VLAKSHMI for the WPBN issue-167677
String                          quoteStatus         = null;
String                     completeFlag         = null;
String[]                   originLocation       = null;
String					   originLoc			=	null;
String					   destLoc				=	null;
String                     backButton           = null;
String[]				   spotRateFlag		= request.getParameterValues("spotRateFlag");//Added by Anil.k for Spot Rates
String						spotRateFlag1  = "";
Hashtable spotRateDetails  = new Hashtable();
ArrayList weightBreakSlabs = new ArrayList();
LinkedHashSet srWeightBreak= new LinkedHashSet();
ArrayList rateDescription  = new ArrayList();
ArrayList chargeRateIndicator = new ArrayList();
ArrayList surChargeId	   = new ArrayList();
ArrayList checkedFlagValue = new ArrayList();
LinkedHashSet currencyId   = null;
String	  carrierId		   = null;
String	  serviceLevel	   = null;
String    uom			   = null;
String    densityRatio	   = null;
String	  frequency		   = null;
String	  transitTime	   = null;//Ended by Anil.k for Spot Rates

try
{
	quoteStatus = request.getParameter("quoteStatus");
	completeFlag = request.getParameter("completeFlag");
	backButton = (String)request.getAttribute("BackButton");
	eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());

	java.text.DecimalFormat df	=new java.text.DecimalFormat("##0.00");

	operation			=	request.getParameter("Operation");
	quoteName			=	request.getParameter("quoteName"); // @@Added by VLAKSHMI for the WPBN issue-167677
	if("View".equalsIgnoreCase(operation) && "BackButton".equalsIgnoreCase(backButton))
	{
    finalDOB		=	(MultiQuoteFinalDOB)session.getAttribute("viewFinalDOB");
	}else{
	finalDOB			=	(MultiQuoteFinalDOB)request.getAttribute("finalDOB");
	System.out.println("107-------finalDOB--------"+finalDOB);
	if(finalDOB==null)
		finalDOB		=	(MultiQuoteFinalDOB)session.getAttribute("finalDOB");
	System.out.println("110-------finalDOB--------"+finalDOB);
	}
	
	masterDOB			=	finalDOB.getMasterDOB();
	freightRates		=	finalDOB.getLegDetails();
	originLocation		=	masterDOB.getOriginLocation();
	
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
<TITLE>MultiQuote - Freight Sell Rates</TITLE>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">

<script src="<%=request.getContextPath()%>/html/TableSorting.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/html/jquery.js"></script>

<script type="text/javascript">
		$(document).ready(function(){
				$("#tab0").css("background-color","D3D3D3");
			<%	for(int i =0; i<originLocation.length ;i++){%>
				$("#tab<%=i%>").click(function(){
					$("#<%=i%>container").fadeIn(0);
					$("#tab<%=i%>").css("background-color","D3D3D3");
					<%for(int k=0;k<originLocation.length ;k++ ){
						if(i==k)
						 continue;
					%>
						$("#<%=k%>container").fadeOut(0);
						$("#tab<%=k%>").css("background-color","#ffcc00");
					<%}%>
				});
			<%} %>
			});

var noOfLegs	=	<%=freightRatesSize%>;

function changeToUpper(field)
{
	//alert(field.name);
	field.value = field.value.toUpperCase();
}


function addNewChargeRow(obj,trid,laneno)
	{
	var tableObj = document.getElementById("chargeDetails"+laneno);
	var rowid = document.getElementById("chargesLaneCounter"+laneno).value;
	rowid= Number(rowid)+1;
    document.getElementById("chargesLaneCounter"+laneno).value = rowid;
	var rowobj   =	(tableObj).insertRow((document.getElementById(trid).sectionRowIndex)+1);
	rowobj.setAttribute("className","formdata");
	rowobj.setAttribute("id","tr"+laneno+rowid,0);
    var cellobj  =	rowobj.insertCell(0);
	cellobj.innerHTML  = "<input class=input type=button disabled value='<<' onclick=deleteChargeRow(this,'tr"+laneno+rowid+"',"+laneno+")>";
	cellobj  =	rowobj.insertCell(1);
    cellobj.setAttribute("align","center");
    cellobj.innerHTML  = "<input class=text maxLength=16 name=chargeGroupIds"+laneno+" id=chargeGroupIds"+laneno+rowid+"									size=28  readonly >  "+
							"<input class=input type=button name=chargeGroupIdBt disabled value=...   id=chargeGroupIdBt"+laneno+rowid+"    onclick=popUpWindow(this,"+laneno+","+rowid+") >  "+
							"<input class=input type=button name=chargeGroupIdDet value='VeiwDetails' onclick=popUpWindow(this,"+laneno+","+rowid+")  disabled>";
    cellobj  =	rowobj.insertCell(2);
    cellobj.innerHTML  = "<input class=input type=button value= '>>' disabled onclick=addNewChargeRow(this,'tr"+laneno+rowid+"',"+laneno+")>";
	}

function deleteChargeRow(obj,trid,laneno)
{
   
	var tableobj =   document.getElementById("chargeDetails"+laneno);
	var rownum   =   document.getElementById(trid).sectionRowIndex;
	
	 if(Number(tableobj.rows.length)== 1){
		 alert(" You Can't Delete this row ")
	 }else{
	 tableobj.deleteRow(rownum);
		 }
}
	
function popUpWindow(input,laneno,rowno)
{
	var terminalId    ='<%=loginbean.getTerminalId()%>';
	var accessLevel	  =	'<%=loginbean.getAccessType()%>';
	var accsLvl		  = ('HO_TERMINAL' == accessLevel?'H':accessLevel);
	var shipmentMode  = '';
	var Url	=	'';
	var Bars = '';
	var Features = '';
	var Options	= '';
	var btnId;
	var searchString;
	Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=no';
	Options='width=400,height=300,resizable=no';

	if(input.name=='chargeGroupIdBt')
	{
		
		searchString=document.getElementById("chargeGroupIds"+laneno+rowno).value;
		Url='etrans/QMSMultiQuoteLOVChargeGroupIds.jsp?Operation=Modify&searchString='+searchString+'&name=chargeGroupIds'+laneno+rowno+'&terminalId='+terminalId+'&shipmentMode='+<%=masterDOB.getShipmentMode()%>+'&accessLevel='+accsLvl;
	}else if(input.name=='chargeGroupIdDet')
	{
		btnId = input.id.substring(input.name.length);
		searchString=document.getElementById("chargeGroupIds"+laneno+rowno).value;
		if(document.getElementById("chargeGroupIds"+laneno+rowno).value=='')
		{
			alert("Please enter Charge Group");
			document.getElementById("chargeGroupIds"+laneno+rowno).focus();
			return false;
		}
		Url='etrans/QMSChargesGroupingMaster.jsp?Operation=View&chargeGroupId='+searchString+"&fromWhere=Quote";
		Options='width=800,height=600,resizable=no';
	}
	//alert(Url)
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features); 
}
   



function validate()
{

	for(i=0;i<noOfLegs;i++)
	{//alert()
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
{		//Modified by Rakesh on 06-01-2010			
		 if(obj.checked){
			document.getElementById("hid"+index).value	=	obj.value;
		   }else{
            document.getElementById("hid"+index).value	=	"";		   
		   }

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

	         for(var k=0;k<rate;k++)
		   {	
				 
 			  if(radio[j].checked)
			    {		  
					if(document.getElementById('rateVal'+index1+k).value!='-' && document.getElementById('rateVal'+index1+k).value!=null)
				   document.getElementById('checkedBox'+index1+k).disabled=false;
			

					  }


				 else{
						
						if(index1==i+j)  //Modified by Rakesh on 06-01-2011
					 {
						// modified by VLAKSHMI for issue 155535 on 27/01/09
							 if(document.getElementById('wtBreakType'+i+j).value=='LIST' && document.getElementById('consoleType'+i+j).value=='FCL')
							 {
								 //@@ Commented by subrahmanyam for the ENhancement 180164 on 31/08/09
									 if(document.getElementById('rateVal'+i+j+k).value!='-' && document.getElementById('rateVal'+i+j+k).value!=null)
									 {
									 document.getElementById('checkedBox'+i+j+k).checked=false;
									 document.getElementById('checkedBox'+i+j+k).disabled=true;	//Modified by Rakesh on 06-01-2010
									 }
								
								



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

			  			 if(checkedBox[x].getAttribute("disabled"))
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
{//alert(noOfLegs)
	var radio = '';

	for(var i=0;i<noOfLegs;i++)
	{
		radio = document.getElementsByName('leg'+i);

		for(var j=0;j<radio.length;j++)
		{
			if(radio[j].checked)
			{
				//Commented and Modified by Rakesh on 04-01-2010			 
				//document.getElementById("hid"+i).value	=	radio[j].value;
				 document.getElementById("hid"+i+j).value	=	radio[j].value;

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

<style type="text/css">
			.tabs{
				float:left;
				background-color:#ffcc00;
				font-family:bold, Helvetica, sans-serif;
				font-size:10px;
				color:000000;
				text-align:left;
				padding:10px;
				cursor:hand;
			}


<%	for(int a =0; a<originLocation.length; a++){%>
			
			.container<%=a%>{
				//alert(<%=a%>);
				position:absolute;
				width:550px;
				height:auto;
				background-color:D3D3D3;
				font-family:bold, Helvetica, sans-serif;
				font-size:12px;
				color:000000;
				padding:10px;
				line-height:1.6;
				text-align:justify;
				<% if( a == 0){ %>
					display:block;
				<% } else { %>
					display:none;
				<%}%>
				left: 0px;
				top: 0px;
			}
		<% } %>
</style>

</HEAD>

<BODY onLoad='initTables()'>
       <form method="post" name="sellRates" action="QMSMultiQuoteController" >

	<table border="0" cellPadding="4" cellSpacing="1" width="138.1%" bgcolor='DCDCDC'>
		<tbody>
			<tr color='#FFFFFF'>
				<td>
				<table border="0" cellPadding="4" cellSpacing="1" width="100%">
					<tbody>
					 <!-- @@Modified by VLAKSHMI for the WPBN issue-167677 -->
						<tr class="formlabel">
						<%if("Modify".equalsIgnoreCase(operation) || "View".equalsIgnoreCase(operation)) {%>
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


%>
               <div>	
					<%
				         noRatesFlag = new Boolean[originLocation.length];
						for(int i =0; i<freightRatesSize;i++){
							originLoc = masterDOB.getOriginPort()[i];
							destLoc	  = masterDOB.getDestPort()[i];
							
					%>
						<div class="tabs" id="tab<%=i%>" style="border:1px solid white" ><font size='1.0%'><b><%=originLoc%>-<%=destLoc%></b></font></div>
				   <% } %>
			   </div>	    

 <div style="background-color:#999999; clear:both;  margin-top:0px; position:relative;">
  <%  for(int i=0;i<freightRatesSize;i++)//@@noOfLegs
	{
		//logger.info("freightRatesSize"+freightRatesSize+i)
		noRatesFlag[i]			=	false;
		legRateDetails		=	(MultiQuoteFreightLegSellRates)freightRates.get(i);

		slabWeightBreaks	=	legRateDetails.getSlabWeightBreaks();

		listWeightBreaks	=	legRateDetails.getListWeightBreaks();

		sellRates			=	legRateDetails.getRates();

		//tiedCustLegDOB		=	(MultiQuoteFreightLegSellRates)frtTiedCustInfo.get(i);
             //logger.info("freightRatesSize::"+freightRatesSize+i);
		boolean headerSet		=	false;
		boolean flatHeaderSet	=	false;
		boolean slabHeaderSet	=	false;
		boolean listHeaderSet	=	false;
		boolean commonHeader	=	false;
		String[] flag = (String[])finalDOB.getMasterDOB().getSpotRatesFlag();
		if(!"Y".equalsIgnoreCase(flag[i])){//Added by Anil.k for Spot Rates
		if(!legRateDetails.isSpotRatesFlag() && tiedCustLegDOB==null)
		{

			if(sellRates!=null)
				sellRatesSize	=	sellRates.size();
			else
				sellRatesSize	=	0;

			if(sellRatesSize > 0)
				noRatesFlag[i]	= true;
%>

 <div class="container<%=i%>" id="<%=i%>container" bgcolor='DCDCDC' style=" clear:both;  margin-top:0px; position:relative;">

<%System.out.println("sellRatesSize..."+sellRatesSize);
			int  counter =	0;  //Modified by Rakesh on 06-01-2010

			for(int j=0;j<sellRatesSize;j++)
			{

				sellRateDOB		=	(MultiQuoteFreightRSRCSRDOB)sellRates.get(j);
				int tempfrtCount				=	0;
				String[] rateDesc= sellRateDOB.getRateDescriptions();
				effectiveFrom	=   sellRateDOB.getEffDate();
				checkedFlag		=	sellRateDOB.getSelectedFlag();
	%>

<%
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
					<table border="0" cellPadding="4" cellSpacing="1" width="209.5%" bgcolor='#FFFFFF'>
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
				{				
					commonHeader=false;
%>
					<table border="0" cellPadding="4" cellSpacing="1" width="209.5%"  bgcolor='#FFFFFF'>
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

					<table border="0" cellPadding="4" cellSpacing="1" width="209.5%" bgcolor='#FFFFFF' >
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
					<table border="0" cellPadding="4" cellSpacing="1" width="209.5%" bgcolor='#FFFFFF'>
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
							<%}
							else{%>
				<TH ><font size='0.25%'>Transit<br>Days</TH></font>
								<%}%>
<%

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
								<TH><font size='0.25%'>Internal Notes</TH></font>
								<TH><font size='0.25%'>External Notes</TH></font>
							</tr>
<%} else {

									%>

		<table id="table<%=tableCount%>" border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='DCDCDC'>
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
						
<%//}
					}
					else if("Slab".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && !slabHeaderSet)
					{
						slabHeaderSet=true;
						String[] slabWtBreaks= sellRateDOB.getSlabWeightBreaks();
						for(int k=0;k<slabWtBreaks.length;k++)
						{
								if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]) || "-".equalsIgnoreCase(rateDesc[k])){
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
								<TH><font size='0.25%'>RSR/<br>CSR/BR</TH></font>
								<TH><font size='0.25%'>Defined<br>By</TH></font>
								<TH><font size='0.25%'>Currency<br>Id</TH></font>
								<TH><font size='0.25%'>Effective<br>From</TH></font>
								<TH><font size='0.25%'>Valid<br>Upto</TH></font>
								<TH><font size='0.25%'>Internal Notes</TH></font>
								<TH><font size='0.25%'>External Notes</TH></font>
							</tr>

							<%	}
					tableCount++;
				} //else{
					if("List".equalsIgnoreCase(sellRateDOB.getWeightBreakType()) && listHeaderSet && "FCL".equalsIgnoreCase(sellRateDOB.getConsoleType()))
					{  
						tempcount           =   0;
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
								<td><input type="checkbox" value="<%=j%>" name="leg<%=i%>"  <%="Y".equalsIgnoreCase(checkedFlag)?"checked":""%> onchange="setSelectedIndex(this,'<%=i%><%=j%>','<%=i%><%=j%>','<%=containerCount%>');" multiple disabled>
								<input type="hidden" id="wtBreakType<%=i%><%=j%>" name="wtBreakType" value='<%=sellRateDOB.getWeightBreakType()%>'>
								<input type="hidden" id="consoleType<%=i%><%=j%>" name="consoleType" value='<%=sellRateDOB.getConsoleType()%>'>
								<input type="hidden" id="checkCount<%=i%><%=j%>" name="checkCount" value='<%=containerCount%>'>
								</td>
								<% tempcount++;} else { %>
								<!-- Modified by subrahmanyam for the pbn id: 186812 on 22/oct/09, checkIndex was replaced in the place of k -->
								<td></td> <%}  if("Y".equalsIgnoreCase(checkedFlag)) { disabled=false;
								%>
								<td><input type="checkBox"  value="<%=listWeightBreaks[k]%>"   name="checkedBox<%=i%><%=j%><%=checkIndex%>" id="checkedBox<%=i%><%=j%><%=checkIndex%>"  <%="Y".equalsIgnoreCase(checkedFlag)?"checked":""%> onClick="setSelectedIndex1(this,'<%=i%><%=j%><%=checkIndex%>','<%=listWeightBreaks[k]%>');" disabled>
								
								<%}else{%>

<td><input type="checkBox"  value="<%=listWeightBreaks[k]%>"   name="checkedBox<%=i%><%=j%><%=checkIndex%>" id="checkedBox<%=i%><%=j%><%=checkIndex%>" disabled <%="Y".equalsIgnoreCase(checked[k])?"checked":""%> onClick="setSelectedIndex1(this,'<%=i%><%=j%><%=checkIndex%>','<%=listWeightBreaks[k]%>');" disabled>
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
								 <%
									if(j==counter ){
									if(k==0){
									counter+=1;	%>

								<td><font size='0.25%'>
								<input class="text" id="Notes<%=j%>" name="notes<%=j%>" readonly size="5"  maxLength="1000" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" title="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" readOnly></td></font>
								<td><font size='0.25%'><input class="text" id="extNotes<%=j%>" readonly name="extNotes<%=j%>" size="5"  maxLength="1000" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" title="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" readOnly></td></font>
								 <%}}else{%>
								  <td><font size='0.25%'>
								<input type='hidden' id="Notes<%=j%>" name="notes<%=j%>" size="5"  maxLength="1000" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" title="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" readOnly></td></font>
								<td><font size='0.25%'><input type='hidden' id="extNotes<%=j%>" name="extNotes<%=j%>" size="5"  maxLength="1000" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" title="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" readOnly></td></font>
								  <%}%> 
							</tr>
							<%

								//}
									}//End of ##1
								}// End of ##2
								%>
								<!--@@ Commented by subrahmanyam for the Enhancement 180164 on 31/08/09  -->
								 <input type="hidden" id="rateVal<%=i%><%=j%><%=k%>" name="rateVal" value='<%=rate[k]%>'>
								<!--@@ Added by subrahmanyam for the Enhancement 180164 on 31/08/09  -->
<!-- 									<%if(k>=4){%>
                  <input type="hidden" id="rateVal<%=i%><%=j%><%=k-4%>" name="rateVal" value='<%=rate[k-4]%>'>
                  <%}else{%>
                  <input type="hidden" id="rateVal<%=i%><%=j%><%=k%>" name="rateVal" value='<%=rate[k]%>'>
                  <%}%> -->
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

					//for(int k=0;k<ratesSize;k++)
					//{
									
										for(int x=1;x<surchargeDesc.size();x++){//##3
												String surcharge	=	 (String	)surchargeDesc.get(x);
												int tempCount	=	0;
												int frtCount				=	14-containerCount;
												int surChargeCount = 0;%>
						<tr valign="top" class='formdata' align="center">
						<td /><td/>
						<td ><font size='0.25%'><%=toTitleCase(surcharge)%></font></td>
						<%		int count3=0;
								for(int sd=0;sd<ratesSize;sd++)
								{//##4
										if(!"A FREIGHT RATE".equalsIgnoreCase(rateDesc[sd]) && surcharge.equalsIgnoreCase(rateDesc[sd]) )//&&listWeightBreaks[sd].startsWith(listWeightBreaks[k]) )
										{		  count3+=1;
%>
												<td ><font size='0.25%'><%=listWeightBreaks[sd]%></font></td>


	<%									}
								}//## 4
							for(int td=0;td<frtCount;td++){//## td
							%>
								<td/>
							<% while(count3!=3){%>
								 <td/>
								 <%  count3++;
							 }
							} //End of ##td%>
						</tr>
							<tr valign="top" class='formdata' align="center">
						<td /><td/><td/>
								<%	
									int count2=0;
									for(int sd=0;sd<ratesSize;sd++)
									{ //##5
										if(!"A FREIGHT RATE".equalsIgnoreCase(rateDesc[sd]) && surcharge.equalsIgnoreCase(rateDesc[sd]) )//&&listWeightBreaks[sd].startsWith(listWeightBreaks[sd]) )
										{		count2+=1;
%>
												<td ><font size='0.25%'><%=rate[sd]%></font></td>


	<%									}
								}//end of ##5
								System.out.println("count 2 --------"+count2);
							for(int td=0;td<frtCount;td++){
							%>
								<td/>
							<% while(count2!=3){%>
								 <td/>
							<%	count2++;  }
								
							}%>
				<%			}//end of ##3
				//	}

	} else {  //logger.info("sellRateDOBsellRateDOB::"+sellRateDOB.getConsoleType()+j); %>


						 <tr valign="top" class='formdata' align="center">
								<td><input type="checkbox" value="<%=j%>" name="leg<%=i%>" <%="Y".equalsIgnoreCase(checkedFlag)?"checked":""%> onchange="setSelectedIndex(this,'<%=i%><%=j%>');" multiple  disabled>
								<input type="hidden" id="wtBreakType<%=i%><%=j%>" name="wtBreakType" value='<%=sellRateDOB.getWeightBreakType()%>'>
								<input type="hidden" id="consoleType<%=i%><%=j%>" name="consoleType" value='<%=sellRateDOB.getConsoleType()%>'>
								</td>
								<td><font size='0.25%'><%=sellRateDOB.getOrigin()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getDestination()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCarrierId()%></td></font>
								<!--@@Modified by kameswari for the WPBN issue-31330-->
								<!--td><%//=sellRateDOB.getServiceLevelId()%></td-->
								<td nowrap><font size='0.25%'><%=sellRateDOB.getServiceLevelDesc()%></td></font>
								<!-- @@Kameswari -->
								<td><font size='0.25%'><%=sellRateDOB.getFrequency()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getTransitTime()%></td></font>
<%
								rate	=	sellRateDOB.getChargeRates();
								for(int k=0;k<rate.length;k++)
								{
									if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]) || "-".equalsIgnoreCase(rateDesc[k])){
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
								<td><font size='0.25%'><input class="text" id="Notes<%=j%>" name="notes<%=j%>" readonly size="5" maxLength="1000" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" title="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" readOnly></td></font>
								<td><font size='0.25%'><input class="text" id="extNotes<%=j%>" readonly name="extNotes<%=j%>" size="5"  maxLength="1000" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" title="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" readOnly></td></font>

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
	for(int x=1;x<surchargeDesc.size();x++){
	String surcharge	=	 (String	)surchargeDesc.get(x);
	int tempCount	=	0;	
	int frtCount				=	0;
	int surChargeCount = 0;
	%>
	<tr valign="top" class='formdata' align="center">
	<td />
	<%System.out.println("1033-----"+surcharge);%>
	<td ><font size='0.25%'><%=surcharge != null && !"".equals(surcharge)?toTitleCase(surcharge.substring(0,surcharge.length()-3)):surcharge%></font></td>
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
								<td nowrap><font size='0.5%'><%=flatwtBreaks[k].length()>=5?flatwtBreaks[k].substring(5):flatwtBreaks[k]%></td></font>
<%}
						}
	}
	else if("Slab".equalsIgnoreCase(sellRateDOB.getWeightBreakType())){
	for(int k=0;k<slabWtBreaks.length;k++)
	{
		if("A FREIGHT RATE".equalsIgnoreCase(rateDesc[k]) || "-".equalsIgnoreCase(rateDesc[k]))
		frtCount ++;
	}
						for(int k=0;k<slabWtBreaks.length;k++)
						{
							if(surcharge.equalsIgnoreCase(rateDesc[k])){
								surChargeCount++;
%>
								<td nowrap><font size='0.5%'><%=toTitleCase(slabWtBreaks[k].length()>=5?slabWtBreaks[k].substring(5):slabWtBreaks[k])%></td></font>
<%}
						}
	}
			tempCount = 14+frtCount-(surChargeCount+2);
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
									<td  nowrap><font size='0.25%'><%=(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k]) && !"".equals(rate[k]))?df.format(Double.parseDouble(rate[k])):"-"%></td>
<%
									}
		}	
		for(int td=0;td<tempCount;td++)
				{
%>
	<td/>
	<%}
%>
		</tr>
<%}
%>

<%}%>
         <input type="hidden" id="hid<%=i%><%=j%>" name="hid<%=i%><%=j%>">

	<%		//}
			}//sellRatesSize
			%>
									<%
				if(!noRatesFlag[i])
				{
					
					displayFlag	=	true;

%>
			<tr>
			<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
					<tbody>
					 <tr color="#FFFFFF">
						<td align="center">
						<font face="Verdana" size="2" color='red'>
						<b>No Rates Are Defined for the Leg <%=originLoc%>-<%=destLoc%>.</b></font>
						</td>
					</tr>
				</tbody>

				</table>
				</tr>
<%
				}
if(!"Freight".equalsIgnoreCase(masterDOB.getQuoteWith())) { 
%>

<tr class ='formheader'><td  colspan = '17'>ApplicableChargeGroups</td></tr>
<tr> <td colspan = '10'>
<table class ='tabledata' border="0" id='chargeDetails<%=i%>'cellPadding="4" cellSpacing="1" width="165%" bgcolor='DCDCDC'>
<%if("View".equalsIgnoreCase(operation) ){
         legRateDetails    =   (MultiQuoteFreightLegSellRates)freightRates.get(i);
		 int chargeslen = legRateDetails.getChargeGroupIds().length;
		for(int j=0;j<chargeslen;j++)
		{%>
	   <tr id='tr<%=i%><%=j%>' class ='formdata' >
	   <td ><input class="input" type="button" disabled value="<<" onclick="deleteChargeRow(this,'tr<%=i%><%=j%>',<%=i%>)"></td>
	   <td align='center'><input class="text" maxLength="16" readonly name="chargeGroupIds<%=i%>" id='chargeGroupIds<%=i%><%=j%>' size="28"  value="<%=legRateDetails.getChargeGroupIds()[j]!= null ?legRateDetails.getChargeGroupIds()[j]:""%>" onblur = "return this.upper();">
	   <input class="input" type="button" name="chargeGroupIdBt" disabled id="chargeGroupIdBt<%=i%><%=j%>" value="..." >
	   <input class="input" type="button" disabled name="chargeGroupIdDet" value="VeiwDetails" onclick="popUpWindow(this,<%=i%>,<%=j%>)"></td>
	   <td><input class="input" type="button" value=">>" disabled onclick="addNewChargeRow(this,'tr<%=i%><%=j%>',<%=i%>)"></td>
	   </tr>
<%}
	 

}else{%>
<tr id='tr<%=i%>0' class ='formdata' >
<td ><input class="input" type="button" disabled value="<<" onclick="deleteChargeRow(this,'tr<%=i%>0',<%=i%>)"></td>
<td align='center'><input class="text" maxLength="16" readonly name="chargeGroupIds<%=i%>" id='chargeGroupIds<%=i%>0' size="28"  value=""  onblur = "changeToUpper(this);">
<input class="input" type="button" disabled name="chargeGroupIdBt" id="chargeGroupIdBt<%=i%>0" value="..." onclick="popUpWindow(this,<%=i%>,0)">
<input class="input" type="button"  name="chargeGroupIdDet" value="VeiwDetails"  disabled onclick="popUpWindow(this,<%=i%>,0)"></td>
<td><input class="input" type="button" value=">>" disabled onclick="addNewChargeRow(this,'tr<%=i%>0',<%=i%>)"></td>
</tr>
<%}%>
</table>
<%}%>
</td>
</tr>
<tr>
<td><input class="input" name="submit" type="submit" id='submit' value="<< Back" onClick="setIndexForChecked(this)">
</td>
<td><input class="input" name="submit" type="submit" value="Next >>"  onClick="setIndexForChecked(this);  return setIndexForChecked1(this,'<%=sellRatesSize%>','<%=rate.length%>')" >
</td></tr>
			</tbody></table>

 </div>


			<!--	<input type="hidden" id="hid<%=i%>" name="hid<%=i%>">-->
<%		}}else{
%>
<div class="container<%=i%>" id="<%=i%>container" bgcolor='DCDCDC' style=" clear:both;  margin-top:0px; position:relative;">
<table width="209%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF' id='spotRates'>
 <tr class="formdata"><td valign ='bottom' nowrap  colspan="9">
<font size='0.25%'><b>Spot Rates: </b><%=legRateDetails.getOrigin()+"-"+legRateDetails.getDestination()%></font></td></tr>
<tr></tr>

<tr class="formdata"><td valign ='bottom' colspan="9"><font size='0.25%'><b>Weight Break: </b><%=masterDOB.getMultiquoteweightBrake()%></font><input type="hidden" name="spotRateType<%=i%>" value="<%=masterDOB.getMultiquoteweightBrake()%>"></td></tr>

 <tr class='formheader'>
 <th><font size='0.25%'><b>Origin</b></font></th>
 <th><font size='0.25%'><b>Destination</b></font></th>
 <th><font size='0.25%'><b>Carrier Id</b></font></th>
 <th><font size='0.25%'><b>UOM</b></font></th>
 <th><font size='0.25%'><b>Density Ratio</b></font></th>
 <th><font size='0.25%'><b>Service Level</b></font></th>
 <th><font size='0.25%'><b>Frequency</b></font></th>
 <th><font size='0.25%'><b>Currency</b></font></th>
 <th><font size='0.25%'><b>Transit Time</b></font></th></tr>
<%
	 spotRateDetails = legRateDetails.getSpotRateDetails();
	weightBreakSlabs= legRateDetails.getWeightBreaks();
	checkedFlagValue= legRateDetails.getCheckedFlag();
	int spotSize	= spotRateDetails.size();	
	String[] srdesc	= null;
	String temp		= "";	
	String[] surid	= null;
	String temp2	= "";
	srWeightBreak	= legRateDetails.getWeightBreak();
	surChargeId		= legRateDetails.getSurchargeId();
	rateDescription = legRateDetails.getSpotRateDescription();
	chargeRateIndicator = legRateDetails.getChargeRateIndicator();	
	for(int t=0;t<rateDescription.size();t++)
	{		
		if(!temp2.equalsIgnoreCase((String)rateDescription.get(t)))
		{			
			temp = temp+","+((String)rateDescription.get(t)).split("-")[0];
			temp2 = (String)rateDescription.get(t);			
		}
	}	
	srdesc = temp.split(",");
	temp = "";
	temp2= "";
	for(int t=0;t<surChargeId.size();t++)
	{		
		if(surChargeId.get(t)!=null && !temp2.equalsIgnoreCase((String)surChargeId.get(t)))
		{			
			temp = temp+","+(String)surChargeId.get(t);
			temp2 = (String)surChargeId.get(t);
		}
	}
	surid = temp.split(",");		
	//double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(0));
    //System.out.println(rate1[0]+"&&&");	
	currencyId		= new LinkedHashSet();
	currencyId		= legRateDetails.getSurCurrency()!=null?legRateDetails.getSurCurrency():null;
	carrierId		= legRateDetails.getCarrier()!=null?legRateDetails.getCarrier():"";
	serviceLevel	= legRateDetails.getServiceLevel()!=null?legRateDetails.getServiceLevel():"";
	uom				= legRateDetails.getUom()!=null?legRateDetails.getUom():"";
	densityRatio	= legRateDetails.getDensityRatio()!=null?legRateDetails.getDensityRatio():"";
	frequency		= legRateDetails.getFrequency()!=null?legRateDetails.getFrequency():"";
	transitTime		= legRateDetails.getTransitTime()!=null?legRateDetails.getTransitTime():""; %>
 
 <tr class="formdata">
 <td align="center"><%=masterDOB.getOriginPort()[i]%></td>
 <td align="center"><%=masterDOB.getDestPort()[i]%></td> 
 <td align="center"><input  type="hidden" name="carrierId<%=i%>" value='<%=carrierId%>'> <%=carrierId%> </td>
 <td align="center"><input  type="hidden" name="uom<%=i%>"  value='<%=uom%>'> <%=uom%> </td>
 <td align="center"><INPUT  TYPE="hidden" name="densityRatio<%=i%>" value='<%=densityRatio%>'> <%=densityRatio%> </td>
 <td align="center"><INPUT  TYPE="hidden" value='<%=serviceLevel%>' name="serviceLevel<%=i%>" > <%=serviceLevel%> </td>
 <td align="center"><INPUT  TYPE="hidden" value='<%=frequency%>' name="frequency<%=i%>" > <%=frequency%> </td>
 <td align="center"><input  type="hidden" value='<%=legRateDetails.getCurrency().split(",")[0]%>' name="currencyId<%=i%>" > <%=legRateDetails.getCurrency().split(",")[0]%> </td>
 <td align="center"><input  type="hidden" name="transitTime<%=i%>" value='<%=transitTime%>'> <%=transitTime%> </td>
 </tr>
 <tr></tr>
 <tr class='formdata'><td colspan="9">
 <%if("Flat".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())){%>
	<table width='50%' cellpadding='4' cellspacing='0' border='0'>
		  <tr class='formdata'>	
		  <td nowrap></td>
			<%for(int k=0;k<spotSize;k++){
				if("A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k))){%> 
				<td align='center' valign="bottom"><%=weightBreakSlabs.get(k)%><input type='hidden' name='flatWeightBreak<%=i%>' id='flatWeightBreak<%=i%><%=k%>'  value='<%=weightBreakSlabs.get(k)%>'></td>
			<%}}%>
			</tr>
			<tr class='formdata' >
			 <td nowrap><b>Freight Rate</b></td>
			 <%for(int k=0;k<spotSize;k++){
					double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(k));
				if("A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k))){%> 
				<td align='center' ><input class='text' type='text' size='6' class='text' name='flatRate<%=i%>' id='flatRate<%=i%><%=k%>' onKeyPress='return getDotNumberCode(this)' value='<%=rate1[2]%>'><input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>' ></td>
			 <%}}%>
			</tr>
	</table>
 <%}else if("Slab".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())){%>
 <table width='50%' cellpadding='4' cellspacing='0' border='0'>
		  <tr class='formdata'>
			<td valign="bottom">Slab</td>				 
				<%for(int k=0;k<spotSize;k++){
					if("A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k))){%> 
				<td align='center' valign="bottom"><input class='text' maxLength='8' name='slabWeightBreak<%=i%>' id='slabWeightBreak<%=i%><%=k%>' size='3'  value='<%=weightBreakSlabs.get(k)%>'></td>
				<%}}%>
			</tr>
			<tr class='formdata' >
				<td nowrap>Freight Rate</td>
			<%for(int k=0;k<spotSize;k++){
					double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(k));
				if("A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k))){%>
			<td align='center' ><input class='text' maxLength='8' name='slabRate<%=i%>' id='slabRate<%=i%><%=k%>' size='3' onKeyPress='return getDotNumberCode(this)' value='<%=rate1[2]%>'><input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>' ></td>
			<%}}%>
			</tr>
	</table>
 <%}else if("List".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())){%>	
	<table width='50%' cellpadding='4' cellspacing='0' >
		<tr class='formdata'>		
			<td valign="bottom"><div id='listType'><%="AIR".equalsIgnoreCase(shipmentMode)?"ULD Types":"Container Types"%></div></td>
			<%for(int k=0;k<spotSize;k++){
				if("A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k))){%>					
				<td align='center'><input class='text' maxLength='8' name='listWeightBreak<%=i%>' id='listWeightBreak<%=i%><%=k%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=weightBreakSlabs.get(k)%>' readOnly></td>
			<%}}%>
		</tr>
		<tr class='formdata'>
			<td valign="bottom">Rate</td>
			<%for(int k=0;k<spotSize;k++){
					double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(k));
				if("A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k))){%>
				<td align='center'valign="bottom" ><input class='text' maxLength='8' name='listRate<%=i%>' id='listRate<%=i%><%=k%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=rate1[2]%>'><input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>' ></td>
			<%}}%>
		</tr>
	</table>
	<%}%>
 </td></tr>
  <%if(surid!=null){%>
 <tr class='formheader'>
 <td valign="bottom" colspan="9"><b>SurCharges</b></td></tr>
 <%}%>
 <% Iterator srWB = surid!=null?srWeightBreak.iterator():null;
	Iterator cur  = surid!=null?currencyId.iterator():null;
	String wtBreakValue;
	String rateBreak= null;
	String rateType = null;
	String curr;
	if(surid!=null){
	for(int sur=0;sur<surid.length-1;sur++){
		if(srWB.hasNext()){
		wtBreakValue = ((String)srWB.next()).substring(surid[sur+1].length()+1);
		curr		 = (String)cur.next();
		System.out.println("curr="+curr);%>
 <tr class="formdata"><td colspan="9">
		<fieldset>
		<legend><b><%=srdesc[sur+2]%></b></legend>
 <table width='40%' cellpadding='4' cellspacing='0'  border='0'>

 <tr class="formdata"><td colspan="9">
 Lane <%=sur+1%></td></tr>
 <tr class="formdata"><td colspan="9">
 <table width='40%' cellpadding='4' cellspacing='0'  border='0'><tr class="formdata" id="tr<%=sur%><%=i%>">
 <tr align="center">&nbsp;SurCharge Id &nbsp;&nbsp;&nbsp &nbsp;SurCharge Desc&nbsp;&nbsp;&nbsp;&nbsp; RateBreak&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;RateType&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;WeightBreaks</tr><!--modified by silpa.p on 13-05-11-->
 <td colspan="2" align="center"><input type='text' class='text' size='15' name='surchargeIds<%=sur%><%=i%>' id ='sr<%=sur%><%=i%>' value='<%=surid[sur+1]%>' onBlur='' readonly></td>
  <% rateBreak = surid[sur+1].substring(3,4).toUpperCase();
     if("A".equals(rateBreak))
             rateBreak = "Absolute";
	 else if("F".equals(rateBreak))
             rateBreak = "Flat";
	 else if("L".equals(rateBreak))
             rateBreak = "List";
     else if("P".equals(rateBreak))
             rateBreak = "percent";
	 else if("S".equals(rateBreak))
             rateBreak = "Slab";
    rateType  = surid[sur+1].substring(4,5).toUpperCase();
	if("A".equals(rateType))
             rateType = "Absolute";
	 else if("F".equals(rateType))
             rateType = "Flat";
	 else if("L".equals(rateType))
             rateType = "List";
     else if("P".equals(rateType))
             rateType = "percent";
	 else if("S".equals(rateType))
             rateType = "Slab";
	 %>
 <td colspan="2" align="center"><input type='text' class='text' size='15' name='surchargeDesc<%=sur%><%=i%>' id ='srd<%=sur%><%=i%>' value='<%=srdesc[sur+2]%>' readonly></td>
  <td><input type='text' class='text' name='ratebreak<%=sur%><%=i%>' value= <%=rateBreak%> id = 'ratebreak<%=sur%><%=i%>'
 size='15' maxlength="100" readonly></td>
  <td><input type='text' class='text' name='ratetype<%=sur%><%=i%>' value= <%=rateType%> id = 'ratetype<%=sur%><%=i%>'
 size='15' maxlength="100" readonly></td>
 <td colspan="2" align="center"><input type='text' class='text' size='15' name='surchargeWeightBreaks<%=sur%><%=i%>' id ='srw<%=sur%><%=i%>' value='<%=wtBreakValue%>' readonly></td>
 </tr></table></td></tr>

 <tr class="formdata">
 <td valign="bottom" >Currency</td>
 <td valign="bottom"><input  type="text" class='text' size='6' value='<%=curr.split(",")[1]%>' id="currencyId<%=sur%><%=i%>" name="currencyId<%=sur%><%=i%>" readonly >
 </td>
 <td colspan="7">
 <%if("Slab".equalsIgnoreCase(wtBreakValue)){%>
 <table width='50%' cellpadding='4' cellspacing='0'  border='0'>
		<tr class='formdata'>
			<td rowspan="2" nowrap valign="bottom" colspan="7"><b>Rate</b></td>	
			<%for(int k=0;k<spotSize;k++){
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {%>
				<td><input type='hidden'  name='srslabbreaks<%=sur%><%=i%>' id='srslabbreaks<%=sur%><%=i%><%=k%>'  value='<%=((String)weightBreakSlabs.get(k)).substring(surid[sur+1].length())%>' ><%=((String)weightBreakSlabs.get(k)).substring(surid[sur+1].length())%> </td>
			<% }}%>
			<!-- <td ><b>Min</b><input type="hidden" name='srslabbreaks<%=sur%><%=i%>' id='srslabbreaks<%=sur%><%=i%>0' value="MIN"></td>
			<%for(int k=0;k<11;k++){%>
				<td><input type=text  class='text' name='srslabbreaks<%=sur%><%=i%>' id='srslabbreaks<%=sur%><%=i%><%=k%>'  value=""  size='4'  > </td>
			<%}%> -->
		</tr>
		<tr class="formdata">
			<%for(int k=0;k<spotSize;k++){
					double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(k));
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {%>
				<td><input type='text' class='text' size='4' name='srslabvalues<%=sur%><%=i%>' id='srslabvalues<%=sur%><%=i%><%=k%>' value='<%=rate1[2]%>'> <input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>' ></td>
			<% }}%>
			<!-- <%for(int k=0;k<11;k++){%>
				<td>
				 <input type=text  class='text' name='srslabvalues<%=sur%><%=i%>' id='srslabvalues<%=sur%><%=i%><%=k%>'  value="" size='4' >
				</td>
			<%}%> -->
		</tr>
	 </table>
 <%}else if("Both".equalsIgnoreCase(wtBreakValue)){%> 
 <table width='50%' cellpadding='4' cellspacing='0'  border='0'>
		<tr class='formdata'>
			<td rowspan="2" nowrap valign="bottom" colspan="7"><b>Rate</b></td>
			<%for(int k=0;k<spotSize;k++){
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {%>
				<td><input type='hidden'  name='srbothbreaks<%=sur%><%=i%>' id='srbothbreaks<%=sur%><%=i%><%=k%>'  value='<%=((String)weightBreakSlabs.get(k)).substring(surid[sur+1].length())%>' ><%=((String)weightBreakSlabs.get(k)).substring(surid[sur+1].length())%> </td>
			<% }}%>
			<!-- <td align="center">
				 <input type=text  class='text' name='srMin' id='srMin' value='MIN'  size='3' id='0'  readonly>
				 <input type='hidden'  class='text' name='srbothbreaks<%=sur%><%=i%>' id='srbothbreaks<%=sur%><%=i%>0' value='MIN'>
			</td>			
			<%for(int k=1;k<12;k++){%>
              <td align="center"><input type=text  class='text' name='srbothbreaks<%=sur%><%=i%>' id='srbothbreaks<%=sur%><%=i%><%=k%>'  value=""  size="3" id='0'  maxlength='10'></td>
			<%}%> -->
		</tr>
		<tr class='formdata'>
			<!-- <%for(int k=0;k<spotSize;k++){
					double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(0));
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && srdesc[sur+2].equalsIgnoreCase(((String)rateDescription.get(k)).split("-")[0])) {%>
				<td><input type='text' class='text' size='6' name='srslabvalues<%=sur%><%=i%>' id='srslabvalues<%=sur%><%=i%><%=k%>' value='<%=rate1[2]%>'> </td>
			<% }}%> -->
			<%for(int k=0;k<spotSize;k++){
				double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(k));
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {
				if("Min".equalsIgnoreCase(((String)weightBreakSlabs.get(k)).substring(surid[sur+1].length()))){%>
                <td><input type=text  class='text' name='srslabvalue<%=sur%><%=i%>'  size=3 id='srslabvalue<%=sur%><%=i%><%=k%>'  value='<%=rate1[2]%>'><input type="hidden"  class='text' name='srflatvalue<%=sur%><%=i%>'  value='<%=rate1[2]%>'  id='srflatvalue<%=sur%><%=i%><%=k%>'><input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>' ></td>
			<%}else{%>
           	  <td><input type=text  class='textHighlight' name='srslabvalue<%=sur%><%=i%>'  value='<%="slab".equalsIgnoreCase((String)chargeRateIndicator.get(k))?rate1[2]:""%>'  size='1' id='srslabvalue<%=sur%><%=i%><%=k%>' onpaste='return false;' autocomplete='off' maxlength='10'><input type=text  class='text' name='srflatvalue<%=sur%><%=i%>'  value='<%="flat".equalsIgnoreCase((String)chargeRateIndicator.get(k))?rate1[2]:""%>'  size="2" id='srflatvalue<%=sur%><%=i%><%=k%>' onpaste='return false;' autocomplete='off'><input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>' ></td>
			<%}}}%>
		</tr>
	 </table>
 <%}else if("List".equalsIgnoreCase(wtBreakValue)){%>
	<table width='50%' cellpadding='4' cellspacing='0'  border='0'>
		<tr class='formdata'>
			<td rowspan="2" nowrap valign="bottom" colspan="7"><b>Rate</b></td>				
			<%for(int k=0;k<spotSize;k++){
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {%>
				<td><input type='hidden'  name='srList<%=sur%><%=i%>' id='srList<%=sur%><%=i%><%=k%>'  value='<%=((String)weightBreakSlabs.get(k)).substring(0,4)%>' ><%=((String)weightBreakSlabs.get(k)).substring(0,4)%> </td>
			<% }}%>
		</tr>
		<tr class="formdata">
			<%for(int k=0;k<spotSize;k++){
					double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(k));
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {%>
				<td><input type='text' class='text' size='6' name='srListValue<%=sur%><%=i%>' id='srListValue<%=sur%><%=i%><%=k%>' value='<%=rate1[2]%>'><input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>' > </td>
			<% }}%>
		</tr>
	 </table>
 <%}else{%>
 <table width='50%' cellpadding='4' cellspacing='0'  border='0'>
		<tr class='formdata'>
			<td rowspan="2" nowrap valign="bottom" colspan="7"><b>Rate</b></td>				
			<%for(int k=0;k<spotSize;k++){
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {%>
				<td><input type='hidden'  name='srelse<%=sur%><%=i%>' id='srelse<%=sur%><%=i%><%=k%>'  value='<%=((String)weightBreakSlabs.get(k)).substring(surid[sur+1].length())%>' ><%=((String)weightBreakSlabs.get(k)).substring(surid[sur+1].length())%> </td>
			<% }}%>
		</tr>
		<tr class="formdata">
			<%for(int k=0;k<spotSize;k++){
					double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(k));
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {%>
				<td><input type='text' class='text' size='6' name='srelseValue<%=sur%><%=i%>' id='srelseValue<%=sur%><%=i%><%=k%>' value='<%=rate1[2]%>'> <input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>' ></td>
			<% }}%>
		</tr>
	 </table>
 <%}%>
	 </td></tr></table></fieldset> </td></tr>

<%}}}%>
 
 <%		System.out.println((masterDOB.getQuoteWith()));
if(!"Freight".equalsIgnoreCase(masterDOB.getQuoteWith())) {  // Added by Anil.k%>
<tr class ='formheader'><td  colspan = '10'>ApplicableChargeGroups</td></tr>
<tr> <td colspan = '10'>
<table class ='tabledata' border="0" id='chargeDetails<%=i%>'cellPadding="4" cellSpacing="1" width="100%" bgcolor='DCDCDC'>
<%if("VIEW".equalsIgnoreCase(operation)){
         legRateDetails    =   (MultiQuoteFreightLegSellRates)freightRates.get(i);
		 int chargeslen = legRateDetails.getChargeGroupIds().length;
		for(int j=0;j<chargeslen;j++)
		{%>
	   <tr id='trr<%=i%><%=j%>' class ='formdata' >
	   <td ></td>
	   <td align='center'><input class="text" maxLength="16" name="chargeGroupIds<%=i%>" id='chargeGroupIds<%=i%><%=j%>' size="28"  value="<%=legRateDetails.getChargeGroupIds()[j]!= null ?legRateDetails.getChargeGroupIds()[j]:""%>" onblur = "return this.upper();">	   
	   <input class="input" type="button" name="chargeGroupIdDet" value="VeiwDetails" onclick="popUpWindow(this,<%=i%>,<%=j%>)"></td>
	   <td></td>
	   </tr>
<%}
	 

}%>
</table>
</td></tr>
<%}%>
 <tr>
<td><input class="input" name="submit" type="submit" id='submit' value="<< Back" onClick="setIndexForChecked(this)">
<input class="input" name="submit" type="submit" value="Next >>"  onClick="setIndexForChecked(this); " >
</td>
</tr>
 </table>
 </div>
 <%}}%>
 </div>


      


		
						
						


<div>	
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
						<% for(int i =0; i<originLocation.length;i++){%>
                        <input type="hidden" name="chargesLaneCounter<%=i%>" id="chargesLaneCounter<%=i%>" value='0'>
						<%}%>
						<%if(!displayFlag){%>
						<%}%>
						</td>
					</tr>
				</tbody>
			</table>
			</div>
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