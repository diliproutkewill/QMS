<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSCostingDetails.jsp
Product Name	: QMS
Module Name		: Costing
Task		    : Adding
Date started	:
Date Completed	:
Date modified	:
Author    		:
Description		: The application "Adding
Actor           :
Related Document: CR_DHLQMS_1008
--%>
<%@ page import="java.util.ArrayList,
				org.apache.log4j.Logger,
				java.util.StringTokenizer,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue,
				com.qms.operations.costing.dob.CostingChargeDetailsDOB,
				com.qms.operations.costing.dob.CostingLegDetailsDOB,
				com.qms.operations.costing.dob.CostingMasterDOB,
				com.qms.operations.costing.dob.CostingRateInfoDOB,
				com.qms.operations.costing.dob.CostingHDRDOB"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSCostingDetails.jsp";
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
  logger  = Logger.getLogger(FILE_NAME);
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	String      nextNavigation		=	"";
	CostingMasterDOB costingMasterDOB=  null;
	CostingChargeDetailsDOB detailsDOB= null;
	CostingLegDetailsDOB  legDetails=   null;
	CostingRateInfoDOB   rateDetailsDOB=   null;
	String operation	            =	request.getParameter("Operation");
	String subOperation 	        =	request.getParameter("subOperation");
	String terminalId	            =	loginbean.getTerminalId();
    ArrayList  originList           =   new ArrayList();
	ArrayList  destinationList      =   new ArrayList();
	ArrayList  costingLegDetailsList=   new ArrayList();
    ArrayList  rateDetails          =   new ArrayList();
	ArrayList frieghtChargeDetails  =   new ArrayList();
	ArrayList list_exNotes          =  null;
	ArrayList  detailsList          = new ArrayList();
	String       currency           =   "";
	String       basis              =   "";

	String origin		=	null;
	String destination	=	null;
	String customerId	=	null;
	String quoteId		=	null;

	String noOfPieces	=	null;
	String uom			=	null;
	String actualWt		=	null;
	String volume		=   null;
	String volumeUom	=	null;
	String invCurrency	=	null;
	String invValue		=	null;
	String baseCurreny	=	null;

	String[] primaryUnitArray=null;
	String[] secondaryUnitArray=null;
	String[] rateValue = null;
	String disabled		=	"";

	double originTotal	=	0;
	double destTotal	=	0;
	double frtTotal		=	0;
	double total		=	0;
	java.text.DecimalFormat   deciFormat     = null;
	String rateIndicator = "";
	boolean listRateFlag = false;
	String errorMsg = null;
	String percentFlag = "";
	String custContactIds	=	"";
    int countDetail = 0;
	int s =0;
	String breakPoint = null;
	String salesPersonEmail = null;//added by subrahamanyam for 146444 on 10/02/09
	try{

		    deciFormat						 = new java.text.DecimalFormat("##0.00");
			java.text.DecimalFormat   df     = new java.text.DecimalFormat("0.000000");
			java.text.DecimalFormat df1			  =	new java.text.DecimalFormat("##,###,##0.00");

			list_exNotes = new ArrayList();

		   CostingHDRDOB	costingHDRDOB = (CostingHDRDOB)session.getAttribute("costingHDRDOB");
           costingMasterDOB			=	(CostingMasterDOB)session.getAttribute("costingMasterDOB");
		   if((String)request.getParameter("countDetail")!=null)
			countDetail            = Integer.parseInt((String)request.getParameter("countDetail"));


		   errorMsg      = (String)request.getAttribute("errorMsg");
		   //Logger.info("",""+costingMasterDOB);
		   if(costingMasterDOB!=null)
		   {
               originList=(ArrayList)costingMasterDOB.getOriginList();
               destinationList=(ArrayList)costingMasterDOB.getDestinationList();
			   costingLegDetailsList= (ArrayList)costingMasterDOB.getCostingLegDetailsList();
		       list_exNotes         =  (ArrayList)costingMasterDOB.getExternalNotes();
			   logger.debug("exchangeRatesList :: "+costingMasterDOB.getExchangeRatesList());

			  // Logger.info("",""+originList);
			   //Logger.info("",""+destinationList);
			 //  Logger.info("",""+costingLegDetailsList);
			  // Logger.info("",""+list_exNotes);
		   }

			session.setAttribute("costingHDRDOB",costingHDRDOB);
			session.setAttribute("costingMasterDOB",costingMasterDOB);

		   if(costingHDRDOB!=null)
			{
				origin	   = costingHDRDOB.getOrigin();
				destination= costingHDRDOB.getDestination();
				customerId = costingHDRDOB.getCustomerid();
				quoteId	   = costingHDRDOB.getQuoteid();

				noOfPieces = costingHDRDOB.getNoOfPieces()+"";
				uom		   = costingHDRDOB.getUom();
				actualWt   = costingHDRDOB.getActualWeight()+"";
				volume	   = costingHDRDOB.getVolume()+"";
				volumeUom  = costingHDRDOB.getVolumeUom();
/*				invCurrency= costingHDRDOB.getInvCurrency();
				invValue   = costingHDRDOB.getInvValue();*/
				baseCurreny= costingHDRDOB.getBaseCurrency();
				//Logger.info(FILE_NAME,"baseCurreny"+baseCurreny);
				salesPersonEmail =costingHDRDOB.getSalesPersonEmail();//added by subrahmanyam for 146444 on 10/02/09
			}


%>

<html>
<head>
<title>Export Air Freight Costing</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
	var countDetail=0;
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
			if(input.value.length == index+3)
			return false;
		}
	  }
	}
	return true;
}

function showCurrency(currencyId)
{
		var termid		 = '<%=loginbean.getTerminalId()%>';
	var searchStr	 =	document.getElementById(currencyId).value;
	/*Url='etrans/ETCCurrencyConversionAddLOV.jsp?searchString=&index=&Operation=<%=operation%>&teminalId='+termid+'&name='+currencyId+'&fromWhere=buycharges&selection=single';*/
	Url='etrans/ETCCurrencyConversionAddLOV.jsp?searchString='+searchStr+'&index=&Operation=<%=operation%>&teminalId='+termid+'&name='+currencyId+'&fromWhere=buycharges&selection=single';
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=400,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}

function showPackCal()
{
		var termid		 = '<%=loginbean.getTerminalId()%>';
	//var searchStr	 =	document.getElementById(currencyId).value;
	Url='etrans/QMSCostingPackCalLOV.jsp';
	var Bars = 'directories=0, border=0,location=0,menubar=0,status=0,titlebar=0,scrollbars=1';//@@Modified by kameswari for the issue-54668
	var Options='width=830,height=360,resizable=yes';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}

function submitToServer(input)
{
	var name = input.name;
    var countDetail = <%=countDetail%>;

	countDetail++;

	if(document.forms[0].emailChkBox !=null)
	{
		if(!document.forms[0].emailChkBox.checked)
			document.forms[0].email.value = "";
		if(!document.forms[0].faxChkBox.checked)
			document.forms[0].fax.value = "";
		if(!document.forms[0].printChkBox.checked)
			document.forms[0].print.value = "";
	}

	 if(document.forms[0].basecurrency.value.length==0)
		{
			alert("Please Enter Base Currency");
			document.forms[0].basecurrency.focus();
			return false;
		}else
		{

			if(name=='details')
			{

				document.forms[0].action='QMSCostingController?Operation=Add&subOperation=details&countDetail='+ <%=countDetail+1%>;
				document.forms[0].submit();
			}else if(name=='calculate')
			{
				//document.forms[0].action='QMSCostingController?Operation=Add&subOperation=details';
				document.forms[0].action='QMSCostingController?Operation=Add&subOperation=calculate&countDetail='+ <%=countDetail+1%>;
				document.forms[0].submit();
			}else if(name == 'finalsubmit')
			{
				var flag = confirm("Do You Want To Send This Costing to the Customer?");
				if(flag== true)
					document.forms[0].action='QMSCostingController?Operation=Add&subOperation=sendmailsave';
				else
					document.forms[0].action='QMSCostingController?Operation=Add&subOperation=save';

				document.forms[0].submit();
			}
		}




}

function setTotal()
{
		var total = 0.0;

		if(document.forms[0].originTotal !=null && document.forms[0].originTotal.value.length>0)
		{
				total = total+ parseFloat(document.forms[0].originTotal.value);
		}
		if(document.forms[0].frtTotal !=null && document.forms[0].frtTotal.value.length>0)
		{
				total = total+ parseFloat(document.forms[0].frtTotal.value);
		}
		if(document.forms[0].destTotal !=null && document.forms[0].destTotal.value.length>0)
		{
				total = total+ parseFloat(document.forms[0].destTotal.value);
		}

		if(document.forms[0].total!=null &&  total>0)
		{
				document.forms[0].total.value = total;
				roundNew(document.forms[0].total,2);
		}

}
function roundNew(obj,X)
{
	var number = obj.value;
	X = (!X ? 2 : X);
	obj.value=Math.round(number*Math.pow(10,X))/Math.pow(10,X);
}
function disableSubmit(input)
{
	if(input.checked==true)//@@Added by Kameswari
	{
		if(document.forms[0].finalsubmit!=null)
		{

					document.forms[0].finalsubmit.disabled = true;

		}
	}
	//@@Added by Kameswari
	else
	{
		if(document.forms[0].finalsubmit!=null)
		{

					document.forms[0].finalsubmit.disabled = false;

		}

	}
	//@@Kameswari
}


function convertWeight(input)
		{
	            var claValue =  document.forms[0].actualweight.value;
				if(input.value=='LB' )
				    document.forms[0].actualweight.value=parseFloat(claValue)*2.20462;
				 else if(input.value=='KG')
				    document.forms[0].actualweight.value=parseFloat(claValue)/2.20462;

				 roundNew(document.forms[0].actualweight,2);
		}
function convertVolume(input)
		{
	            var claValue =  document.forms[0].volume.value;
				if(input.value=='CFT' && claValue.length>0)
				    document.forms[0].volume.value=parseFloat(claValue)*35.3146;
				 else if(input.value=='CBM' && claValue.length>0)
				    document.forms[0].volume.value=parseFloat(claValue)/35.3146;

				 roundNew(document.forms[0].volume,2);
		}

function checkNumbers1(input)
{
    if(trim(input.value).length>0)
    {
        if(isNaN(trim(input.value)))
        {
            //alert("Please do not enter characters for "+label);
			input.value='';
            input.focus();
            return false;
        }
    }
    return true
}
function trim(input)
 {
	while (input.substring(0,1) == ' ')
		input = input.substring(1, input.length);

	while (input.substring(input.length-1,input.length) == ' ')
		input = input.substring(0, input.length-1);

   return input;
 }

function selectAll()
		{
            var originSize = 0;
			var frtSize    = 0;
			var destSize   = 0;

	        if(document.forms[0].originSize!=null)
				 originSize = document.forms[0].originSize.value;

			if(document.forms[0].frtSize!=null)
				frtSize    = document.forms[0].frtSize.value;
			if(document.forms[0].destSize!=null)
				destSize   = document.forms[0].destSize.value;

			var selectCheck = document.forms[0].selectl.checked;

			for(i=0;i<originSize;i++)
			{
				if(selectCheck)
					document.getElementById("origincheck"+i).checked = true;
				else
					document.getElementById("origincheck"+i).checked = false;
			}
			/*for(i=0;i<frtSize;i++)
			{
				if(selectCheck)
					document.getElementById("frtcheck"+i).checked = true;
				else
					document.getElementById("frtcheck"+i).checked = false;
			}*/
			for(i=0;i<destSize;i++)
			{
				if(selectCheck)
					document.getElementById("destcheck"+i).checked = true;
				else
					document.getElementById("destcheck"+i).checked = false;
			}
		}
function openContactLOV()
{
//modified by phani sekhar for wpbn 167768
	//Url='<%=request.getContextPath()%>/etrans/ContactLOV.jsp?custId='+document.forms[0].customerId.value+'&flag=Costing&quoteId=<%=quoteId%>';
	Url='<%=request.getContextPath()%>/etrans/AttentionToLOV.jsp?custId='+document.forms[0].customerId.value+'&flag=Costing&quoteId=<%=quoteId%>'+'&attentionCustomerId='+document.forms[0].attentionCustomerId.value+'&attentionSlno='+document.forms[0].attentionSlno.value+'&attentionEmailId='+document.forms[0].attentionEmailId.value+'&attentionFaxNo='+document.forms[0].attentionFaxNo.value+'&attentionContactNo='+document.forms[0].attentionContactNo.value;
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=400,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}

function openNewContactLOV()
{
	Url='etrans/NewContactDetails.jsp?custId='+document.forms[0].customerId.value;
	Options='width=1024,height=450,resizable=yes';
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
function setValues(values)
{
  document.getElementById('contactPersons').length=0
  for(var i=0;i<values.length;i++)
  {
	document.getElementById('contactPersons').options[i] = new Option(values[i],values[i],true,true);
  }
}
//@@ Added by subrahmanyam for the Enhancement 146444 on 10/02/09
function checkEmail()
{
	var str = document.forms[0].opEmailId.value;
	var i=0,j=-1;
	var str1;
	var flag=false;
	while(1)
	{
		if(document.forms[0].opEmailId.value=="")
		{
			return true;
		}
		else
		{
			j=str.indexOf(";",j);
			if(j==-1)
			{
				break;
			}
			str1=str.substring(i,j);
	    	if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(str1)))
			{
		//@@Commented by subrahmanyam for 146444 on 20-apr-09
		//alert(str1 + " is an Invalid E-mail Address!);
//@@Added by subrahmanyam for 146444 on 20-apr-09
		alert(str1 + " is an Invalid E-mail Address!  \n Multiple E-mails Should be Separated by SemiColon only");
				document.forms[0].opEmailId.focus();
				flag=true;
				break;
			}

			i=j+1;
			j=j+1;
			continue;
		}
	}
	str1=str.substring(i);
	if(str1!=''&& !flag)
	if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(str1)))
	{
//@@Commented by subrahmanyam for 146444 on 20-apr-09
		//alert(str1 + " is an Invalid E-mail Address!);
//@@Added by subrahmanyam for 146444 on 20-apr-09
		alert(str1 + " is an Invalid E-mail Address!  \n Multiple E-mails Should be Separated by SemiColon only");
		document.forms[0].opEmailId.focus();
		return false;
	}

	return true;
}
//@@ Endedd by subrahmanyam for the Enhancement 146444 on 10/02/09
//@@ added by phani sekhar for wpbn  167678
function setMailValues(values1)
{
  document.getElementById('userModifiedMailIds').length=0
	  var mStrTemp="";
  for(var i=0;i<values1.length;i++)
  {
		if(mStrTemp.length!=0)
		mStrTemp=mStrTemp+","+values1[i];
		else
		mStrTemp	=	values1[i];

  }
  document.getElementById('userModifiedMailIds').value =mStrTemp;
}
//end 16768

</script>
</head>
<body onLoad="setTotal()">
<form method='post' action ='QMSCostingController' >
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="100%" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="100%" >
					<tr valign="top" class="formlabel">
						<td> <%=costingMasterDOB.getShipmentMode()%> Freight Costing </td>
						<td align=right>QS1050622
						</td></tr></table>
			</td>
		  </tr>
		</table>
<%
		if(errorMsg!=null && errorMsg.trim().length()>0)
		{
%>
		<table border='0' width="100%" cellpadding="0" cellspacing="0">
			<tr class='formdata'>
				<font color='#ff0000'><%=errorMsg%></font>
			</tr>
		</table>
<%
		}
%>
		<table border='0' width="100%" cellpadding="4" cellspacing="1">
			<tr class='formdata'>

					<td>Origin Location:<%=(origin!=null)?origin:""%></td>
					<td>Destination Location:<%=(destination!=null)?destination:""%></td>
					<td>Quote ID:<%=(quoteId!=null)?quoteId:""%></td>
			</tr>
			<tr class='formheader'>
				<td colspan='3'>Customer Details</td>
			</tr>
			<tr class='formdata'>
					<td>Customer ID:<br>
					<input type='text' name = 'customerId' value='<%=(customerId!=null)?customerId:""%>' class='text' readOnly size='15'>
					</td>
					<td>Attention To:
					<input class="input" type="button" value="..." name="contactPersonLov" onclick="openContactLOV()">&nbsp;
					<input class="input" type="button" value="NEW" row="0" onclick="openNewContactLOV()"><br>
				   <select class="select"  name="contactPersons" size="4" multiple style="width:200px;margin:0px 0 5px 0;">
<%
				if(costingMasterDOB.getContactPersonNames()!=null)
				{
				  for(int i=0;i<costingMasterDOB.getContactPersonNames().length;i++)
				  {
					 custContactIds = custContactIds+costingMasterDOB.getContactPersonIds()[i]+",";
%>					<option value='<%=costingMasterDOB.getContactPersonNames()[i]%>' selected>
						<%=costingMasterDOB.getContactPersonNames()[i]%>
					</option>
<%
				  }
				}
%>
				 </select><br>
			</td>
			<td>Notes for Customer:<br>
			<textarea name='notes' class="text" cols="50" rows='10' style="height:60"><%=costingHDRDOB.getNotes()!=null?costingHDRDOB.getNotes():""%></textarea>
			</td>

			</tr>
		</table>
        <table border="0" width="100%" cellpadding="4" cellspacing="1">
					<tr valign="top" class="formheader">
						<td colspan='5'> Shipment Details </td>
					</tr>
		<!--/table>
		<table border='0' width="100%" cellpadding="0" cellspacing="1"-->
			<tr class='formdata'>
					<td colspan='2'>Incoterms<br>
					<select class="select"  name="incoterms" size="1" >
					<!--modified by silpa.p on 16-06-11-->
					<option value="CFR" <%="CFR".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>CFR</option>
					<option value="CIF" <%="CIF".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>CIF</option>
					<option value="CIP" <%="CIP".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>CIP</option>
					<option value="CPT" <%="CPT".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>CPT</option>
					<option value="DAP" <%="DAP".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>DAP</option>
				    <option value="DAT" <%="DAT".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>DAT</option>
					<option value="DDP" <%="DDP".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>DDP</option>
					<option value="EXW" <%="EXW".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>EXW</option>
					<option value="FAS" <%="FAS".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>FAS</option>
					<option value="FCA" <%="FCA".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>FCA</option>
					<option value="FOB" <%="FOB".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>FOB</option>
					
				<!--@@Commented by Anusha for the WPBN ISSUE:380917 -->
					<!--<option value="DAF" <%="DAF".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>DAF</option>
					<option value="DEQ" <%="DEQ".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>DEQ</option>
					<option value="DES" <%="DES".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>DES</option>
					<option value="DDU" <%="DDU".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>DDU</option>
					<option value="DDU CC" <%="DDU CC".equalsIgnoreCase(costingHDRDOB.getIncoterms())?"selected":""%>>DDU CC</option>-->
				<!--@@Commented by Anusha for the WPBN ISSUE:380917 -->
				</select>
					<!--input type='text' class='text' name="incoterms" id="incoterms" readOnly value ='<%//=(costingHDRDOB.getIncoterms()!=null)?costingHDRDOB.getIncoterms():""%>' size="6"-->
					</td>
					<td colspan='2'>Commodity Type<br><input type='text' class='text' name="commoditytype" id="commoditytype" readOnly value ='<%=(costingHDRDOB.getCommodityType()!=null)?costingHDRDOB.getCommodityType():""%>' onblur="this.value=this.value.toUpperCase()" size="6"  >&nbsp;<input type='checkbox'  name='commodityTypeCheck'  <%=costingMasterDOB.getCommodityTypeCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getCommodityTypeCheck())?"checked":""):"checked" %>></td>
<!--@@ Added by subrahmanyam for the Enhancement 146444 on 10/02/09  -->
					<td colspan='2'> SalesPerson E-Mail Id<br><input type='text' class='text' name="opEmailId"		value ='<%=salesPersonEmail!=null?salesPersonEmail:""%>' size=40 onBlur='checkEmail()'></td>
<!--@@ Endded by subrahmanyam for the Enhancement 146444 on 10/02/09  -->
			</tr>
<%
				int frtListSize = costingLegDetailsList.size();
			   if(frtListSize>0)
				{
%>
					<tr class='formdata'>
					<td></td>
					<td >TransitTime</td>
					<td >Frequency</td><td >Carrier</td><td >Service Level</td></tr>
<%
						for(int m=0;m<costingLegDetailsList.size();m++)
					    {
						    legDetails = (CostingLegDetailsDOB)costingLegDetailsList.get(m);
%>
							<tr class='formdata'>
							<td><%=(legDetails.getOrigin())%>-<%=(legDetails.getDestination())%></td>
							<td ><input type='text' class='text' name="transittime" id="transittime" value ='<%=(legDetails.getTransitTime())%>' readonly size="6"  >&nbsp;<input type='checkbox' name='transittimeCheck' <%=costingMasterDOB.getTransittimeCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getTransittimeCheck())?"checked":""):"checked" %> ></td> <!-- Added by Rakesh on 24-01-2011 for CR:231219 -->
							<td ><input type='text' class='text' name="frequency" id="frequency" readonly value ='<%=(legDetails.getFrequency())%>' size="6"  >&nbsp;<input type='checkbox' name='frequencyCheck' <%=costingMasterDOB.getFrequencyCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getFrequencyCheck())?"checked":""):"checked" %>></td>
							
							<!-- Commented by Rakesh on 04-02-2011 -->
<!-- 
							<td ><input type='text' class='text' name="carrier" id="carrier" readonly value ='<%=(costingHDRDOB.getCarrier()!=null?costingHDRDOB.getCarrier():"")%>' size="20"  >&nbsp;<input type='checkbox' name='carrierCheck' <%=costingMasterDOB.getCarrierCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getCarrierCheck())?"checked":""):"checked" %>></td>
							<td ><input type='text' class='text' name="serviceLevel" id="serviceLevel" readonly value ='<%=(costingHDRDOB.getServiceLevel()!=null?costingHDRDOB.getServiceLevel():"")%>' size="6"  >&nbsp;<input type='checkbox' name='serviceLevelCheck' <%=costingMasterDOB.getServiceLevelCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getServiceLevelCheck())?"checked":""):"checked" %>></td>
 -->						<!-- Commente  Ended by Rakesh on 04-02-2011 -->
							<td ><input type='text' class='text' name="carrier" id="carrier" readonly value ='<%=(legDetails.getCarrier()!=null?legDetails.getCarrier():"")%>' size="20"  >&nbsp;<input type='checkbox' name='carrierCheck' <%=costingMasterDOB.getCarrierCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getCarrierCheck())?"checked":""):"checked" %>></td>
							<td ><input type='text' class='text' name="serviceLevel" id="serviceLevel" readonly value ='<%=(legDetails.getServiceLevel()!=null?legDetails.getServiceLevel():"")%>' size="6"  >&nbsp;<input type='checkbox' name='serviceLevelCheck' <%=costingMasterDOB.getServiceLevelCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getServiceLevelCheck())?"checked":""):"checked" %>></td>


							</tr><!-- Added by Rakesh on 24-01-2011 for CR:231219 -->
<%
						}
				}
%>

			<tr class='formdata'>
					<td  width='15%'>No.of pieces<br><input type='text' class='text' name="noofpieces" id="noofpieces" value ='<%=(noOfPieces!=null)?noOfPieces:""%>' onblur="this.value=this.value.toUpperCase()" onKeyPress='return getDotNumberCode(this)' size="6" onchange ='disableSubmit(this)' >&nbsp;<input type='checkbox' name='noOfPiecesCheck' <%=costingMasterDOB.getNoOfPiecesCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getNoOfPiecesCheck())?"checked":""):"checked" %>></td>
					<!--td>UOM<br><select name='uom' onchange ='convertWeight(this);disableSubmit(this)' class='select'>
									<option value='KG' <%//=(uom!=null && "KG".equals(uom))?"selected":""%> >KG</option>
									<option value='LB' <%//=(uom!=null && "LB".equals(uom))?"selected":""%> >LB</option>
							   </select></td-->
					<td  width='15%'>Actual Weight<br><input type='text' class='text' name="actualweight" id="actualweight" value ='<%=(actualWt!=null)?actualWt:""%>' onblur="this.value=this.value.toUpperCase()" size="6" onKeyPress='return getDotNumberCode(this)' onchange ='disableSubmit(this)' >&nbsp;<input type='checkbox' name='actualWeightCheck' <%=costingMasterDOB.getActualWeightCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getActualWeightCheck())?"checked":""):"checked" %>></td>
					<td width='15%'>UOM<br>
					<select name='uom' onchange ='convertWeight(this);disableSubmit(this)' class='select'>
									<option value='KG' <%=(uom!=null && "KG".equals(uom))?"selected":""%> >KG</option>
									<option value='LB' <%=(uom!=null && "LB".equals(uom))?"selected":""%> >LB</option>
							   </select>&nbsp;<input type='checkbox' name='uomCheck' <%=costingMasterDOB.getUomCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getUomCheck())?"checked":""):"checked" %>>
					</td>
					<!--td>UOM<br><select name='uom' onchange ='convertWeight(this);disableSubmit(this)' class='select'>
									<option value='KG' <%//=(uom!=null && "KG".equals(uom))?"selected":""%> >KG</option>
									<option value='LB' <%//=(uom!=null && "LB".equals(uom))?"selected":""%> >LB</option>
							   </select></td-->
					<!--td>VolumeUOM<br><select name='volumeUom' onchange ='convertVolume(this);disableSubmit(this)' class='select'>
									<option value='CBM' <%//=(uom!=null && "CBM".equals(volumeUom))?"selected":""%> >CBM</option>
									<option value='CFT' <%//=(uom!=null && "CFT".equals(volumeUom))?"selected":""%> >CFT</option>
							   </select></td-->

					<td width='25%' nowrap>Volume(Length*width*Height)<br>
					<input type='text' class='text' name="volume" id="volume" value ='<%=(volume!=null)?volume:""%>' onblur="this.value=this.value.toUpperCase()" size="6" onKeyPress='return getDotNumberCode(this)' onchange ='disableSubmit(this)' >
					<input type="button" class='input' value="Calculation" name="cal" onClick='showPackCal()'>&nbsp;<input type='checkbox' name='volumeCheck' <%=costingMasterDOB.getVolumeCheck()!=null?("Y".equalsIgnoreCase(costingMasterDOB.getVolumeCheck())?"checked":""):"checked" %>>
					</td>

					<!-- <input type='hidden' name='volumeUom' value='CBM'> -->
					<td >Volume UOM<br>
						<select name='volumeUom' onchange ='convertVolume(this);disableSubmit(this)' class='select'>
						 <option value='CBM' <%=(uom!=null && "CBM".equals(volumeUom))?"selected":""%> >CBM</option>
						  <option value='CFT' <%=(uom!=null && "CFT".equals(volumeUom))?"selected":""%> >CFT</option>
						</select>
					</td>
					<!--td>

					<input type="button" class='input' value="Calculation" name="cal" onClick='showPackCal()'>
					</td-->

			</tr>
			<tr class='formdata'>
					<!-- <td>Invoice Currency<br><input type='text' class='text' name="invcurrency" id="invcurrency" value ='' onblur="this.value=this.value.toUpperCase()" size="6"  >
					<input type="button" class='input' value="..." name="invcurrencylov" onClick=showCurrency('invcurrency')></td>
					<td>Invoice Value<br><input type='text' class='text' name="invvalue" id="invvalue" value ='' onblur="this.value=this.value.toUpperCase()" size="6"  ></td> -->
					<td colspan='5'>Base Currency<font  color=#ff0000>*</font><br><input type='text' class='text' name="basecurrency" id="basecurrency" value ='<%=(baseCurreny!=null)?baseCurreny:loginbean.getCurrencyId()%>' onblur="this.value=this.value.toUpperCase()" size="6"  onchange ='disableSubmit(this)' >
					<input type="button" class='input' value="..." name="basecurrencylov" onClick=showCurrency('basecurrency')></td>

			</tr>

		</table>
		<table border="0" width="100%" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
<!-- @@Added by subrahmanyam for 146444 on 20-apr-09 -->
		  <td align="left"><font  color=#ff0000>Note: </font>Multiple E-Mails Should be Separated With SemiColon(;) In The SalesPerson E-Mail Id Field.</td>
<!-- @@Ended by subrahmanyam for 146444 on 20-apr-09 -->
            <td valign="top" align="right">
                <input type="button" value="Details" name="details" class="input" onclick='submitToServer(this)'>
				<!-- <input type="reset" value="Reset" name="reset" class="input"> -->
				<input type='hidden' name='Operation' value='<%=operation%>'>
				<input type='hidden' name='subOperation' value='details'>
				<input type="hidden" name="contactIds" value="<%=custContactIds!=null?custContactIds:""%>">
        <!--added by phani sekhar for wpbn 167678 -->
         <!-- Added by Phani for the WPBN issue-167678 -->
			   <input type="hidden" name="userModifiedMailIds" value="">
			   <input type="hidden" name="attentionCustomerId" value="">
			   <input type="hidden" name="attentionSlno" value="">
			   <input type="hidden" name="attentionEmailId" value="">
			   <input type="hidden" name="attentionFaxNo" value="">
				 <input type="hidden" name="attentionContactNo" value="">
			   <!--ends 167678 -->
		     </td>
          </tr>
        </table>
<%
			if(subOperation!=null && ("details".equals(subOperation)||"calculate".equals(subOperation)) && (errorMsg==null || ( errorMsg!=null && errorMsg.trim().length()<=0)))
			{
%>
               <table>
				  <tr class='formheader'>
					Total Cost Based On Below Quote(Base Currency) :<input type=text size=10  name='total'  value='' readOnly class='text'  >
				  </tr>
				  <tr class='formheader'>
					<td>Select <input type='checkbox' name="selectl" onClick="disableSubmit(this);selectAll()" ></td>
					<td>Charge Name</td>
					<td>Break Point</td>
					<td>Currency</td>
					<td>Rate</td>
				    <td>Basis</td>
					<td>Primary</td>
					<td>Secondary</td>
					<td>Value</td>
				    <td>Exchange Rate</td>
				    <td>Density Ratio</td>
					<td colspan="7">Notes</td>
				  </tr>
        <%
	        int originListSize	=	originList.size();
			if(originListSize>0)
			{
					for(int m=0;m<originListSize;m++)
					 {
						 detailsDOB = (CostingChargeDetailsDOB)originList.get(m);//Modified by kameswari for Surcharge Enhancements
						 /*detailsList =(ArrayList)originList.get(m);
						 detailsDOB = (CostingChargeDetailsDOB)detailsList.get(0);*/

						 rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();
						if(!"List".equalsIgnoreCase(detailsDOB.getWeightBreak()))
						{
						 if("checked".equals(detailsDOB.getChecked()))
						 {
							if(detailsDOB.getRateValue()==null || "".equals(detailsDOB.getRateValue()))
							 {

								disabled	=	"disabled";
							 }
						 }

						 if(!detailsDOB.getWeightBreak().equalsIgnoreCase("Percent") && !detailsDOB.getWeightBreak().equalsIgnoreCase("Absolute"))
							 {

							 //Logger.info(FILE_NAME,"checke::"+detailsDOB.getChecked());
				%>
						  <tr class='formdata'>
							<td><input type="checkbox" name='origincheck<%=m%>' id='origincheck<%=m%>' value="" <%=("checked".equals(detailsDOB.getChecked()))?"checked":""%> onClick='disableSubmit(this)'></td>
							<td><%//=detailsDOB.getChargeDescId()==null?"":detailsDOB.getChargeDescId().trim()%>
								<%=detailsDOB.getInternalName()!=null?detailsDOB.getInternalName():""%>
							</td>
							<td>
<%
							if((detailsDOB.getPrimaryBasis().equalsIgnoreCase("KG")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("LB")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("CBM")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("shipment")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("piece") || detailsDOB.getPrimaryBasis().equalsIgnoreCase("CFT")))
							 {
%>
								<%=(detailsDOB.getBrkPoint()!=null)?detailsDOB.getBrkPoint():""%>
<%
							 }
							 else
							 {
%>
<!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<!--  <input type='text' size=4  name='originBreakPoint<%=m%>'  value='<%=(detailsDOB.getBrkPoint()!=null)?detailsDOB.getBrkPoint():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'> -->
<!--@@Addeed by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<input type='text' size=4  name='originBreakPoint<%=m%>' style="background-color:BROWN;" value='<%=(detailsDOB.getBrkPoint()!=null)?detailsDOB.getBrkPoint():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
<%
							 }
							 if(detailsDOB.getWeightBreak()!=null && detailsDOB.getWeightBreak().endsWith("%") && !"Per Shipment".equalsIgnoreCase(detailsDOB.getChargeBasis()))
									 percentFlag = "%";
								 else
									 percentFlag = "";
%>
							 </td>
							<td><%=detailsDOB.getCurrency()%></td>
							<td><%=(detailsDOB.getRate()!=null)?df1.format(new Double(detailsDOB.getRate()))+percentFlag:""%></td>
							<td nowrap><%=(detailsDOB.getChargeBasis()!=null)?detailsDOB.getChargeBasis():""%></td>
							<td><input type=text size=8  name='orginP<%=m%>'  value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>' readOnly class='text' ></td>
							<td><% if(detailsDOB.getSecondaryBasis()!=null && !"".equals(detailsDOB.getSecondaryBasis())) {
							%>
<!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<!-- <input type=text size=8  name='orginS<%=m%>'  value='<%=(detailsDOB.getSecUnitValue()!=null)?detailsDOB.getSecUnitValue():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td> -->
<!--@@Addeed by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<input type=text size=8  name='orginS<%=m%>' style="background-color:YELLOW;" value='<%=(detailsDOB.getSecUnitValue()!=null)?detailsDOB.getSecUnitValue():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<% } %></td>
							<td><input type=text size=8  name='originV<%=m%>'  value='<%=(detailsDOB.getRateValue()!=null)?detailsDOB.getRateValue():""%>' readOnly  class='text' ></td>
							<td><%=(detailsDOB.getConvFactor()>0)?(df.format(detailsDOB.getConvFactor())):"1.00"%></td>
							<td><%=detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio()%></td>
							<td colspan="4"><input type=text size=8  name="Notes"  value='' readOnly class='text' ></td>
						  </tr>
						  <%
							 }
							}
							else
							 {
%>
							  <tr class='formdata'>
								<td><input type="checkbox" name='origincheck<%=m%>' id='origincheck<%=m%>' value="" <%=("checked".equals(detailsDOB.getChecked()))?"checked":""%> onClick='disableSubmit(this)'></td>
							  <td colspan='15'><%//=detailsDOB.getChargeDescId()==null?"":detailsDOB.getChargeDescId().trim()%>
								<%=detailsDOB.getInternalName()!=null?detailsDOB.getInternalName():""%>
							</td>
							  </tr>
<%
								primaryUnitArray	= detailsDOB.getPrimaryUnitArray();
								secondaryUnitArray	= detailsDOB.getSecUnitArray();
								rateValue			= detailsDOB.getSecUnitArray();
							 }
							for(int j=0;j<rateDetails.size();j++)
							 {
								rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);
								rateIndicator = "";
								if(detailsDOB.getWeightBreak().equalsIgnoreCase("List"))
								{
%>
								  <tr class='formdata'>
								  <td></td>
									<td></td>
									<td><%=rateDetailsDOB.getWeightBreakSlab()%></td>
									<td><%=detailsDOB.getCurrency()%></td>
									<td><%=df1.format(new Double(rateDetailsDOB.getRate()))%></td>
									<td nowrap><!-- <%//=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?"Per Shipment ":detailsDOB.getChargeBasisDesc()%> --><%="Per Container"%></td>
<!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
									<!-- <td><input type=text size=8  name='orginP<%=m%>'  value='<%=(primaryUnitArray!=null && (primaryUnitArray[j]!=null))?primaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'></td>
									<td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
									<td><input type=text size=8  name='orginP<%=m%>' style="background-color:YELLOW;" value='<%=(primaryUnitArray!=null && (primaryUnitArray[j]!=null))?primaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'></td>
									<td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
<%
									if(detailsDOB.getSecondaryBasis()!=null && !"".equals(detailsDOB.getSecondaryBasis()))
									{
%><!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
										<!-- <input type=text size=8  name='orginS<%=m%>'  value='<%=(secondaryUnitArray!=null && (secondaryUnitArray[j]!=null))?secondaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
										<input type=text size=8  name='orginS<%=m%>' style="background-color:YELLOW;" value='<%=(secondaryUnitArray!=null && (secondaryUnitArray[j]!=null))?secondaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
<%
									}
%>									</td>
									<td><input type=text size=8  name='originV<%=m%>'  value='<%=(rateDetailsDOB.getRateValue()!=null && (rateDetailsDOB.getRateValue()!=null))?rateDetailsDOB.getRateValue():""%>' readOnly  class='text' ></td>
									<td><%=(detailsDOB.getConvFactor()>0)?(df.format(detailsDOB.getConvFactor())):"1.00"%></td>
									<td><%=detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio()%></td>
									<td colspan="4"><input type=text size=8  name="Notes"  value='<%//=(legDetails.getNotes()!=null)?legDetails.getNotes():""%>' readOnly class='text' ></td>
									<td colspan="4"></td>
								  </tr>
<%
									if("checked".equals(detailsDOB.getChecked()) && (rateDetailsDOB.getRateValue()!=null && !"".equals(rateDetailsDOB.getRateValue())))
									 {

										   listRateFlag = true;

											//@@Commented and Modified by Kameswari for the WPBN issue-133207
											/*originTotal = originTotal+Double.parseDouble(rateDetailsDOB.getRateValue());*/
											originTotal = originTotal+(Double.parseDouble(rateDetailsDOB.getRateValue())*detailsDOB.getConvFactor());
									 }
								}
								else
								{
							    if((detailsDOB.getWeightBreak().equalsIgnoreCase("Slab") ||   	detailsDOB.getWeightBreak().equalsIgnoreCase("slab%") ) &&
								!(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Max") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Base") ))
								 {
									if(detailsDOB.getRateType().equalsIgnoreCase("Flat"))
									  rateIndicator = "F";
								    else if(detailsDOB.getRateType().equalsIgnoreCase("slab"))
									  rateIndicator = "S";
									else if(detailsDOB.getRateType().equalsIgnoreCase("both"))
									 {
										if(rateDetailsDOB.getRateIndicator()!=null && rateDetailsDOB.getRateIndicator().startsWith("F"))
											rateIndicator	= "F";
										else
											rateIndicator	= "S";
									 }
								//rateIndicator =(rateDetailsDOB.getRateIndicator()!=null)?rateDetailsDOB.getRateIndicator():"";

								 }else
								 {
									rateIndicator = "";
								 }

								 if((detailsDOB.getWeightBreak().endsWith("%") || detailsDOB.getWeightBreak().equalsIgnoreCase("Percent"))&& !(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Max") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Base") ))
									 percentFlag = "%";
								 else
									 percentFlag = "";

						  if(detailsDOB.getWeightBreak().equalsIgnoreCase("Percent") || detailsDOB.getWeightBreak().equalsIgnoreCase("Absolute"))
							 {

							  //Logger.info("sdfjasdlf1231",detailsDOB.getWeightBreak());
						  %>

						  	<tr class='formdata'>
							<td><input type="checkbox" name='origincheck<%=m%>' id='origincheck<%=m%>'  value="" <%=("checked".equals(detailsDOB.getChecked()))?"checked":""%> onClick='disableSubmit(this)'></td>

							<td><%//=detailsDOB.getChargeDescId()==null?"":detailsDOB.getChargeDescId().trim()%>
								<%=detailsDOB.getInternalName()!=null?detailsDOB.getInternalName():""%>
							</td>
							<td><%=rateDetailsDOB.getWeightBreakSlab()%></td>
							<td><%=detailsDOB.getCurrency()%></td>
							<td><%=df1.format(new Double(rateDetailsDOB.getRate()))%><%=percentFlag%></td>
							<td ><%//=rateDetailsDOB.getWeightBreakSlab()%>
							<%=detailsDOB.getChargeBasisDesc()%></td>
<%
							if((detailsDOB.getPrimaryBasis().equalsIgnoreCase("KG")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("LB")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("CBM")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("shipment")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("piece") ||  detailsDOB.getPrimaryBasis().equalsIgnoreCase("CFT")))
							 {
%>
							<td><input type=text size=8  name='orginP<%=m%>'  value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>' readOnly class='text' ></td>
<%
							 }else
								 {
%><!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
								<!-- <td><input type=text size=8  name='orginP<%=m%>'  value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)'></td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
								<td><input type=text size=8  name='orginP<%=m%>' style="background-color:YELLOW;" value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)'></td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
<%
								 }
%><!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<!-- <td><input type=text size=8  name='orginS<%=m%>'  value='<%=(detailsDOB.getSecUnitValue()!=null)?detailsDOB.getSecUnitValue():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<td><input type=text size=8  name='orginS<%=m%>' style="background-color:YELLOW;" value='<%=(detailsDOB.getSecUnitValue()!=null)?detailsDOB.getSecUnitValue():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<td><input type=text size=8  name='originV<%=m%>'  value='<%=(detailsDOB.getRateValue()!=null)?detailsDOB.getRateValue():""%>' readOnly  class='text' ></td>
							<td><%=(detailsDOB.getConvFactor()>0)?(df.format(detailsDOB.getConvFactor())):"1.00"%></td>
							<td></td>
							<td></td>
							<td colspan="4"></td>
						  </tr>
			  <%
							 }
							else
							 {
			  %>
							 <tr class='formdata'>
						  <td></td>
							<td></td>
							<td><%=rateDetailsDOB.getWeightBreakSlab()%></td>
							<td><%=detailsDOB.getCurrency()%></td>
							<td><%=df1.format(new Double(rateDetailsDOB.getRate()))+""+rateIndicator+percentFlag%></td>
							<td nowrap><%=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") || rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Base") || rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Max") || ("Both".equalsIgnoreCase(detailsDOB.getRateType()) && "F".equalsIgnoreCase(rateIndicator))?"Per Shipment ":detailsDOB.getChargeBasisDesc()%></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td colspan="4"></td>
						  </tr>
		 <%
							 }
								/*if("checked".equals(detailsDOB.getChecked()) && detailsDOB.getRateValue()!=null && !"".equals(detailsDOB.getRateValue()))
								{
										logger.info("detailsDOB.getRateValue():"+detailsDOB.getRateValue());
										originTotal = originTotal+Double.parseDouble(detailsDOB.getRateValue());
								}*/
							}
						 }
						if(!detailsDOB.getWeightBreak().equalsIgnoreCase("List"))
						{
							if("checked".equals(detailsDOB.getChecked()) && detailsDOB.getRateValue()!=null && !"".equals(detailsDOB.getRateValue()))
							{
									//originTotal = originTotal+Double.parseDouble(detailsDOB.getRateValue());
									//@@Commented and Modified by Kameswari for the WPBN issue-133207
									originTotal = originTotal+(Double.parseDouble(detailsDOB.getRateValue())*detailsDOB.getConvFactor());
							}
						}

						if("checked".equals(detailsDOB.getChecked()))
						{
							if(!listRateFlag && detailsDOB.getWeightBreak().equalsIgnoreCase("List"))
							{
								disabled	=	"disabled";
							}
						}
					   }


%>
			     <tr class='formheader'>
			     <td colspan="8" align="center">&nbsp;&nbsp;&nbsp;&nbsp;SubTotal Origin Charges(Base Currency)</td>
				 <td colspan="8"><input type=text size=10  name="originTotal"  value='<%=(originTotal>0)?(deciFormat.format(originTotal)+""):""%>' readOnly  class='text'> <input type='hidden' name='originSize' value ='<%=originListSize%>'></td>
				 </tr>

<%			}

			  //Logger.info(FILE_NAME,"disabled"+disabled);
               int count =0;
			   listRateFlag = false;
			   frtListSize = costingLegDetailsList.size();
			   if(frtListSize>0)
				{
						for(int m=0;m<costingLegDetailsList.size();m++)
					    {
						 legDetails = (CostingLegDetailsDOB)costingLegDetailsList.get(m);

						 frieghtChargeDetails      =  (ArrayList)legDetails.getCostingChargeDetailList();

						 for(int n=0;n<frieghtChargeDetails.size();n++)
						 {
							 String temp ="";
                           %>

							 	<tr class='formdata' width='100%'>
								<td colspan='15' align='center'><b><%=legDetails.getOrigin()%>-<%=legDetails.getDestination()%></b></td>
							</tr>
							<% // detailsDOB = (CostingChargeDetailsDOB)frieghtChargeDetails.get(n);//Modified by kameswari for Surcharge Enhancements
							  detailsList = (ArrayList)frieghtChargeDetails.get(n);
							  for(int k=0;k<detailsList.size();k++)
							 {
                               detailsDOB =(CostingChargeDetailsDOB)detailsList.get(k);
							  rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();

						    if(!(("SEA".equalsIgnoreCase(costingMasterDOB.getShipmentMode())&&detailsDOB.getWeightBreak().equalsIgnoreCase("list"))|| ("Multi-Mode".equalsIgnoreCase(costingMasterDOB.getShipmentMode())&&detailsDOB.getWeightBreak().equalsIgnoreCase("list"))||("AIR".equalsIgnoreCase(costingMasterDOB.getShipmentMode())&&detailsDOB.getWeightBreak().equalsIgnoreCase("list")&&"FREIGHT RATE".contains(detailsDOB.getRateDescription().toUpperCase()) )||("TRUCK".equalsIgnoreCase(costingMasterDOB.getShipmentMode())&&detailsDOB.getWeightBreak().equalsIgnoreCase("list")&&"FREIGHT RATE".contains(detailsDOB.getRateDescription().toUpperCase()))))
								 {

								if("checked".equals(detailsDOB.getChecked()))
								{
									if(detailsDOB.getRateValue()==null || "".equals(detailsDOB.getRateValue()))
									{

										disabled	=	"disabled";
									}
								}




						%>
							<tr class='formdata'>
							<td><input type="checkbox" name='frtcheck<%=count%>' id='frtcheck<%=count%>' value= '<%=("checked".equals(detailsDOB.getChecked()))?"checked":""%>' onClick='this.checked=true' checked ></td>
							<%if(detailsDOB.getRateDescription()!= null && ("A Freight Rate".equalsIgnoreCase(detailsDOB.getRateDescription()) || "Freight Rate".equalsIgnoreCase(detailsDOB.getRateDescription()))){%>
							<td nowrap><%=toTitleCase(detailsDOB.getRateDescription())%></td>
							<%}else{%>
							<!--<td nowrap><%=detailsDOB.getRateDescription()!=null?toTitleCase(detailsDOB.getRateDescription().substring(0,detailsDOB.getRateDescription().length()-3)):""%></td>-->
							<td nowrap><%=detailsDOB.getRateDescription().indexOf("-")!=-1?toTitleCase(detailsDOB.getRateDescription().substring(0,detailsDOB.getRateDescription().length()-3)):toTitleCase(detailsDOB.getRateDescription())%></td><!--modified by silpa.p on 9-06-11-->
                            <%}%>
						
							<td><% 
							System.out.println(detailsDOB.getPrimaryBasis()+"--------"+detailsDOB.getRateDescription())	;
							if((detailsDOB.getPrimaryBasis().equalsIgnoreCase("KG")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("LB")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("CBM")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("shipment")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("piece") || detailsDOB.getPrimaryBasis().equalsIgnoreCase("CFT")))
							 {
							  %>
								<%=(detailsDOB.getBrkPoint()!=null)?detailsDOB.getBrkPoint():""%>
							  <%
							 }
							 else
							 {
							 %>
<!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							 <!-- <input type=text size=4  name='frtBreakPoint<%=count%>'  value='<%=(detailsDOB.getBrkPoint()!=null)?detailsDOB.getBrkPoint():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							 <input type=text size=4  name='frtBreakPoint<%=count%>' style="background-color:YELLOW;" value='<%=(detailsDOB.getBrkPoint()!=null)?detailsDOB.getBrkPoint():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							 <%
							 }
							 %>
							 </td>
							<td><%=detailsDOB.getCurrency()%></td>
							<td><%=(detailsDOB.getRate()!=null)?detailsDOB.getRate():""%></td>
							<td nowrap><%=(detailsDOB.getChargeBasis()!=null)?detailsDOB.getChargeBasis():""%></td>
					<%
						if("C.A.F%".equalsIgnoreCase(detailsDOB.getRateDescription())|| "Currency Adjustment Factor%-PF".equalsIgnoreCase(detailsDOB.getRateDescription()) || detailsDOB.getBrkPoint().endsWith("Percent"))
						 {%>
<!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
                            <!--  <td><input type=text size=8  name='cafP<%=count%>'   value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>'  class='text' ></td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							 <td><input type=text size=8  name='cafP<%=count%>' style="background-color:YELLOW;"  value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>'  class='text' ></td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
					<%}
//						else if("SURCHARGE".equalsIgnoreCase(detailsDOB.getRateDescription()))
							else if("SURCHARGE".contains(detailsDOB.getRateDescription().toUpperCase()))
						 {%>
<!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
                             <!-- <td><input type=text size=8  name='surchargeP<%=count%>'   value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>'  class='text' ></td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							 <td><input type=text size=8  name='surchargeP<%=count%>'  style="background-color:YELLOW;" value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>'  class='text' ></td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
					<%}
					else
					 {%>
							<td><input type=text size=8  name='frtP<%=count%>'  value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""
							 %>' readOnly class='text' ></td>
							 <%}%>
							 <td></td>
							<td><input type=text size=8  name='frtV<%=count%>'  value='<%=(detailsDOB.getRateValue()!=null)?detailsDOB.getRateValue():""%>' readOnly class='text' ></td>


							<td><%=(detailsDOB.getConvFactor()>0)?df.format(detailsDOB.getConvFactor()):"1.00"%></td>
							<td><input type=text size=8  name=""  value='<%=detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio()%>' readOnly class='text' ></td>
							<td colspan="4"><input type=text size=8  name="Notes"  value='<%=(legDetails.getNotes()!=null)?legDetails.getNotes():""%>' readOnly class='text' ></td>
						  </tr>
							<%
								 if(detailsDOB.getRateValue()!=null && !"".equals(detailsDOB.getRateValue()))
								 {

										//frtTotal = frtTotal+Double.parseDouble(detailsDOB.getRateValue());
										//@@Commented and Modified by Kameswari for the WPBN issue-133207
										frtTotal = frtTotal+(Double.parseDouble(detailsDOB.getRateValue())*detailsDOB.getConvFactor());
								 }

							 }
							else
							 {
							%>
						  <!--<tr class='formdata' width='100%'>
								<td colspan='15' align='center'><b><%=legDetails.getOrigin()%>-<%=legDetails.getDestination()%></b></td>
							</tr>-->
							<%}%>
						  <!--<tr class='formdata'>
							<td><input type="checkbox" name='frtcheck<%=count%>' id='frtcheck<%=count%>' value="" <%=("checked".equals(detailsDOB.getChecked()))?"checked":""%> onClick='this.checked=true' checked></td>-->

						  <!--</tr>-->
						  <%

							if(detailsDOB.getWeightBreak().equalsIgnoreCase("List"))
							 {
									primaryUnitArray = detailsDOB.getPrimaryUnitArray();
									rateValue		 = detailsDOB.getSecUnitArray();
							 }
                           int c =0;
  						   int tempAbs = 0; //@@ Added by subrahmanyam for 181430
							String tempDesc= "";
						  for(int j=0;j<rateDetails.size();j++)
							 {
							 if(j>0)
							 {
							   rateDetailsDOB = (CostingRateInfoDOB)rateDetails.get(j-1);
							  temp	=	rateDetailsDOB.getRateDescription();
							  }
							   rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);


								rateIndicator = "";
								if(rateDetailsDOB!=null)
								 {
							//logger.info("rateDetailsDOB.getRateDescription()"+rateDetailsDOB.getRateDescription());
							/*logger.info("detailsDOB.getRateDescription()"+detailsDOB.getRateDescription());*/	if(rateDetailsDOB.getRateDescription().equalsIgnoreCase(detailsDOB.getRateDescription()))
							 {
								//   rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);
								rateIndicator = "";

										  if((detailsDOB.getWeightBreak().equalsIgnoreCase("Slab") || detailsDOB.getWeightBreak().equalsIgnoreCase("slab%") ) &&
								!(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Max") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Base") ))
								 {
									if(detailsDOB.getRateType().equalsIgnoreCase("Flat"))
									  rateIndicator = "F";
								    else if(detailsDOB.getRateType().equalsIgnoreCase("slab"))
									  rateIndicator = "S";
									else if(detailsDOB.getRateType().equalsIgnoreCase("both"))
									 {
										if(rateDetailsDOB.getRateIndicator()!=null && rateDetailsDOB.getRateIndicator().startsWith("F"))
											rateIndicator	= "F";
										else
											rateIndicator	= "S";
//										rateIndicator =(rateDetailsDOB.getRateIndicator()!=null)?rateDetailsDOB.getRateIndicator():"";
									 }

								 }else
										rateIndicator = "";


								 if((detailsDOB.getWeightBreak().endsWith("%") || detailsDOB.getWeightBreak().equalsIgnoreCase("Percent"))&& !(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Max") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Base") ))
									percentFlag = "%";
								 else
									 percentFlag   = "";

						  if(detailsDOB.getWeightBreak().equalsIgnoreCase("List"))
							 {
										  %>
						  <tr class='formdata'><!--@@Commented by kameswari for Surcharge Enhancements-->
<!--@@Modified by subrahmanyam for 213788-->
						  <%
							if("SEA".equalsIgnoreCase(costingMasterDOB.getShipmentMode()) || "Multi-Mode".equalsIgnoreCase(costingMasterDOB.getShipmentMode())||("AIR".equalsIgnoreCase(costingMasterDOB.getShipmentMode())&&"FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription())))
								 {
							  if(j==0)
								 {%>
						  <td><input type="checkbox" name='frtcheck<%=count%>' id='frtcheck<%=count%>' value= '<%=("checked".equals(detailsDOB.getChecked()))?"checked":""%>'onClick='this.checked=true' checked></td>

						  <td><%=detailsDOB.getRateDescription()%></td>
						  <%}
						  else
								 {

								 if(temp.equalsIgnoreCase(detailsDOB.getRateDescription()))
									 {	 %>
									 <td></td>
									 <td></td>
									 <%
									 }else
										 {
                                   if(!("FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()) ||
									  "A FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()) ||
									   "CAF%".equalsIgnoreCase(detailsDOB.getRateDescription()))){//added by silpa.p on 29-06-11 for caf validation System.out.println("detailsDOB.getRateDescription()-----------------"+detailsDOB.getRateDescription());%>
										  <td><input type="checkbox" name='frtcheck<%=count%>' id='frtcheck<%=count%>' value= '<%=("checked".equals(detailsDOB.getChecked()))?"checked":""%>'onClick='this.checked=true' checked></td>
										  <td><%=detailsDOB.getRateDescription().length()>3? detailsDOB.getRateDescription().substring(0,detailsDOB.getRateDescription().length()-3):detailsDOB.getRateDescription()%></td>
								 <%}else{%>
										  <td><input type="checkbox" name='frtcheck<%=count%>' id='frtcheck<%=count%>' value= '<%=("checked".equals(detailsDOB.getChecked()))?"checked":""%>'onClick='this.checked=true' checked></td>
										  <td><%=detailsDOB.getRateDescription()%></td>
								   <%}}
									 }
								 }
								 else
								 {%>
								 <td>&nbsp;</td>
                                <td>&nbsp;</td>
								 <%}%>


						<% //System.out.println(rateDetailsDOB.getWeightBreakSlab());
							if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("BAFM3")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("CAF%")
                                  ||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("PSSM3"))
                               {%>
				    <%if(tempAbs == 1 && tempDesc.equalsIgnoreCase(detailsDOB.getRateDescription())){%><!-- Modified by subrahmanyam for 181430 -->
				     <!-- <td>OR</td> --><td><%=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("CAF%")?"Percent":"Flat"%></td>
					  <%tempAbs=0;}else{ if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("CAF%")){%>
						  <td>Percent</td>
						  <%}else{%>
					   <!-- <td>ABSOLUTE</td> --><td>Flat</td>
					 <%}}
					 }
					  else if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("BAFMIN")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("CAFMIN")
                               ||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("PSSMIN")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("FSMIN")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SSMIN"))
                               {tempAbs =1;%>
					<td>MIN</td>
                     <%tempDesc=detailsDOB.getRateDescription();}
					   else
						   if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("FSBASIC")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SSBASIC"))
					   {%>
					   <td>BASIC</td>
					   <%}
						   else if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("FSKG")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SSKG"))
                               {%>
					  <td>FLAT</td>
					  <%}
						   else if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("CSF"))
						{%>
						<td>ABSOLUTE</td>
					  <%}
					  else if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SURCHARGE"))
                       {%>
					   <td>PERCENT</td>
					   <%}
					  else if (
						  rateDetailsDOB.getWeightBreakSlab().endsWith("CAF")||rateDetailsDOB.getWeightBreakSlab().endsWith("BAF")
                               ||rateDetailsDOB.getWeightBreakSlab().endsWith("CSF")||rateDetailsDOB.getWeightBreakSlab().endsWith("PSS")||rateDetailsDOB.getWeightBreakSlab().endsWith("caf")
                               ||rateDetailsDOB.getWeightBreakSlab().endsWith("baf")||rateDetailsDOB.getWeightBreakSlab().endsWith("csf")||rateDetailsDOB.getWeightBreakSlab().endsWith("pss"))
                               {
								   s= rateDetailsDOB.getWeightBreakSlab().length()-3;
                                   breakPoint= rateDetailsDOB.getWeightBreakSlab().substring(0,s);
								   %>
						<td><%=breakPoint%></td>
					  <%}
						else
						{%>
					  <td><% if(!("FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()) ||
									  "A FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()))){%>
								  <%=rateDetailsDOB.getWeightBreakSlab().substring(0,rateDetailsDOB.getWeightBreakSlab().length()-5)%>	
									  <%}else{%>
					  <%=rateDetailsDOB.getWeightBreakSlab()%></td>
					 <%}}%>
							<td><%=(detailsDOB.getCurrency()!=null)?detailsDOB.getCurrency():""%></td>
							<td><%=df1.format(new Double(rateDetailsDOB.getRate()))%></td>
							<!-- <%//=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?"Per Shipment ":detailsDOB.getChargeBasisDesc()%> --><%if("1".equalsIgnoreCase(detailsDOB.getShipmentMode()))
								 {
								if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("FSKG ")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SSKG"))
									 {%>
								<td nowrap>Per Kilogram</td>
								<%}
								else if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("FSMIN")
									||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SSMIN")
									||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("FSBASIC")
									||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SSBASIC"))
									 {%>

								<td nowrap>Per Shipment</td>
								<%
									 }
								else
								 {%>
								<td nowrap>Per ULD</td>
						      <%}
							 }
							else if(rateDetailsDOB.getWeightBreakSlab().endsWith("CAF%")||rateDetailsDOB.getWeightBreakSlab().endsWith("caf")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SURCHARGE") ||rateDetailsDOB.getWeightBreakSlab().endsWith("CAFPF") ||rateDetailsDOB.getWeightBreakSlab().endsWith("CAFLF") ||rateDetailsDOB.getWeightBreakSlab().endsWith("CAFLP") )//@@ CAFLF condition added by govind for the issue 262475
								 {%>
								 <td nowrap>Percent of Freight</td>
							<%
								 }
							else
								 {
						%>
						 <td nowrap>Per Container</td>
						 <%}
                          if(!("2".equalsIgnoreCase(detailsDOB.getShipmentMode())))
								 {
							  if("FREIGHT RATE".equalsIgnoreCase(detailsDOB.getRateDescription()))
									 {%>
<!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
									 <!-- <td><input type=text size=8  name='frtP<%=count%>'   value='<%=(primaryUnitArray!=null && (primaryUnitArray[j]!=null))?primaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'></td>
									<td></td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
									<td><input type=text size=8  name='frtP<%=count%>'  style="background-color:YELLOW;" value='<%=(primaryUnitArray!=null && (primaryUnitArray[j]!=null))?primaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'></td>
									<td></td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<td><input type=text size=8  name='frtV<%=count%>'  value='<%=(rateDetailsDOB.getRateValue()!=null && (rateDetailsDOB.getRateValue()!=null))?rateDetailsDOB.getRateValue():""%>' readOnly  class='text' ></td> <td><%=(detailsDOB.getConvFactor()>0)?(df.format(detailsDOB.getConvFactor())):"1.00"%></td>
							<td><%=detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio()%></td>
							<td colspan="4"><input type=text size=8  name="Notes"  value='<%=(legDetails.getNotes()!=null)?legDetails.getNotes():""%>' readOnly class='text' ></td>

						 	<% }

					else
					 {%>
							<!--<td><input type=text size=8  name='frtP<%=count%>'  value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""
							 %>' readOnly class='text' ></td>
							 <td></td>
							<td><input type=text size=8  name='frtV<%=count%>'  value='<%=(detailsDOB.getRateValue()!=null && (detailsDOB.getRateValue()!=null))?detailsDOB.getRateValue():""%>' readOnly  class='text' ></td>-->
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td colspan='4'>&nbsp;</td>
							<%}

								 }
								 else
								 {%>
<!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<!-- <td><input type=text size=8   name='frtP<%=count%>'  value='<%=(primaryUnitArray!=null && (primaryUnitArray[j]!=null))?primaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'></td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<td><input type=text size=8  style="background-color:YELLOW;" name='frtP<%=count%>'  value='<%=(primaryUnitArray!=null && (primaryUnitArray[j]!=null))?primaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'></td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<td></td>
							<td><input type=text size=8  name='frtV<%=count%>'  value='<%=(rateDetailsDOB.getRateValue()!=null && (rateDetailsDOB.getRateValue()!=null))?rateDetailsDOB.getRateValue():""%>' readOnly  class='text' ></td>
							<td><%=(detailsDOB.getConvFactor()>0)?(df.format(detailsDOB.getConvFactor())):"1.00"%></td>
							<td><%=detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio()%></td>
							<td colspan="4"><input type=text size=8  name="Notes"  value='<%=(legDetails.getNotes()!=null)?legDetails.getNotes():""%>' readOnly class='text' ></td>
							<%}%>


							<!--<td colspan="3"></td>-->
						  </tr>
			  <%

								 if("checked".equals(detailsDOB.getChecked()) && (rateDetailsDOB.getRateValue()!=null && !"".equals(rateDetailsDOB.getRateValue())))
								 {

								        listRateFlag = true;

										//frtTotal = frtTotal+Double.parseDouble(rateDetailsDOB.getRateValue());
										//@@Commented and Modified by Kameswari for the WPBN issue-133207
										frtTotal = frtTotal+(Double.parseDouble(rateDetailsDOB.getRateValue())*detailsDOB.getConvFactor());
								 }



							 }
							else
							 {
			  %>
							<tr class='formdata'>
						  <td></td>
							<td></td>
							<% //System.out.println("1372-----"+rateDetailsDOB.getWeightBreakSlab());
				  if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("BAFM3")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("CAF%")
                                  ||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("PSSM3"))
                               {%>
				<%if(tempAbs == 1 && tempDesc.equalsIgnoreCase(detailsDOB.getRateDescription())){%>
				     <!-- <td>OR</td> --><td><%=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("CAF%")?"Percent":"Flat"%></td>
					 <%tempAbs=0;}else{ if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("CAF%")){%>
						  <td>Percent</td>
						  <%}else{%>
					   <!-- <td>ABSOLUTE</td> --><td>Flat</td>
					 <%}}}
					  else if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("BAFMIN")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("CAFMIN")
                               ||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("PSSMIN")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("FSMIN")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SSMIN"))
                               {tempAbs =1;%>
					<td>MIN</td>
                     <%tempDesc=detailsDOB.getRateDescription();}
					   else
						   if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("FSBASIC")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SSBASIC"))
					   {%>
					   <td>BASIC</td>
					   <%}
						   else if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("FSKG")||rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SSKG"))
                               {%>
					  <td>FLAT</td>
					  <%}
						   else if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("CSF"))
						{%>
						<td>ABSOLUTE</td>
					  <%}
					  else if(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("SURCHARGE"))
                       {%>
					   <td>PERCENT</td>
					   <%}
					  else if (
						  rateDetailsDOB.getWeightBreakSlab().endsWith("CAF")||rateDetailsDOB.getWeightBreakSlab().endsWith("BAF")
                               ||rateDetailsDOB.getWeightBreakSlab().endsWith("CSF")||rateDetailsDOB.getWeightBreakSlab().endsWith("PSS")||rateDetailsDOB.getWeightBreakSlab().endsWith("caf")
                               ||rateDetailsDOB.getWeightBreakSlab().endsWith("baf")||rateDetailsDOB.getWeightBreakSlab().endsWith("csf")||rateDetailsDOB.getWeightBreakSlab().endsWith("pss"))
                               {
								   s= rateDetailsDOB.getWeightBreakSlab().length()-3;
                                   breakPoint= rateDetailsDOB.getWeightBreakSlab().substring(0,s);
								   %>
						<td><%=breakPoint%></td>
					  <%}
						else
						{%>
					  <td>
					  <%if(detailsDOB.getRateDescription()!=null && !("A Freight Rate".equalsIgnoreCase(detailsDOB.getRateDescription()) || "Freight Rate".equalsIgnoreCase(detailsDOB.getRateDescription()) ) ){%>
					  
					  <%=rateDetailsDOB.getWeightBreakSlab().length()>5?rateDetailsDOB.getWeightBreakSlab().substring(5):rateDetailsDOB.getWeightBreakSlab()%></td>
					 <%}else{%>
					 <%=rateDetailsDOB.getWeightBreakSlab()%></td>
					 <%}}%>
							<td><%=detailsDOB.getCurrency()%></td>
							<td><%=df1.format(new Double(rateDetailsDOB.getRate()))%><%=rateIndicator+percentFlag%></td>

					<%
								  if("FSBASIC".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"FSMIN".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"SSBASIC".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"BAFMIN".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"SSMIN".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"PSSMIN".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()) || rateDetailsDOB.getWeightBreakSlab().endsWith("MIN"))//@@ Modifed by govind for yhe issue 264638
					  {
					  %>
					   <td>Per Shipment</td>
					   <%}
					  else if("FSKG".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"SSKG".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()) )
					  {%>
					  <td>Per Kilogram</td>
					   <%}
					  else if("BAFM3".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"PSSM3".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())|| "BAFFFFLAT".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()) ||  "PSSFFFLAT".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()) ||
						  (("2".equalsIgnoreCase(detailsDOB.getShipmentMode())||"4".equalsIgnoreCase(detailsDOB.getShipmentMode())) &&                                        
						  "FLAT".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())))
						{%>
						<!--  <td>per Cubic Meter</td> --><td>Per Weight Measurement</td>
						 <%}
					  else if("CAF%".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab())||"SURCHARGE".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()) || rateDetailsDOB.getWeightBreakSlab().startsWith("CAF") )
						{%>
						<td>Percent of Freight</td>
						 <%}
					  else if("CSF".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()) || rateDetailsDOB.getWeightBreakSlab().startsWith("CSF"))
					  {%>
						<td>Per Shipment</td>
					 <%}
					  else if(rateDetailsDOB.getWeightBreakSlab().endsWith("CAF"))
		 			 {%>
                           <td>Percent of Freight</td>
					   <%}
					  else if ("CAFMIN".equalsIgnoreCase(rateDetailsDOB.getWeightBreakSlab()))
					 {%>
                        <td>Per Shipment</td>
					 <%
						}
					  else
					 {
					 %>
					<td nowrap><%=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")|| ("Both".equalsIgnoreCase(detailsDOB.getRateType()) && "F".equalsIgnoreCase(rateIndicator))?"Per Shipment ":detailsDOB.getChargeBasisDesc()%></td>
					 <%}%>


							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td colspan="4"></td>
						  </tr>
		 <%                   }
							c++;
							 }

						   }
						 }
					 }


								//Logger.info(FILE_NAME,"listRateFlag"+listRateFlag);
							 	if("checked".equals(detailsDOB.getChecked()))
								{
									if(!listRateFlag && detailsDOB.getWeightBreak().equalsIgnoreCase("List"))
									{
										disabled	=	"disabled";
									}
								}

							  count++;
						 }
						 }



   %>
			     <tr class='formheader'>
			     <td colspan="8" align="center">&nbsp;&nbsp;&nbsp;&nbsp;SubTotal Freight Charges(Base Currency)</td>

				 <td colspan="8"><input type=text size=10  name="frtTotal"   value='<%=(frtTotal>0)?(deciFormat.format(frtTotal)+""):""%>' readOnly class='text' ><input type='hidden' name='frtSize' value ='<%=count%>'></td>


 <%
	 }
	//Logger.info(FILE_NAME,"disabled"+disabled);
				int destSize = destinationList.size();
				listRateFlag = false;
				//Logger.info("","66666666666");
				// Logger.info("",""+destSize);
				if(destSize>0)
				{
					   for(int m=0;m<destSize;m++)
					   {


						 detailsDOB = (CostingChargeDetailsDOB)destinationList.get(m);    //Modified by kameswari for Surcharge Enhancements
						/* detailsList = (ArrayList)destinationList.get(m);
                        detailsDOB = (CostingChargeDetailsDOB)detailsList.get(0);   */
						 rateDetails=(ArrayList)detailsDOB.getCostingRateInfoDOB();


						if(!"List".equalsIgnoreCase(detailsDOB.getWeightBreak()))
						{
						 if("checked".equals(detailsDOB.getChecked()))
						 {
							if(detailsDOB.getRateValue()==null || "".equals(detailsDOB.getRateValue()))
							 {
								disabled	=	"disabled";
							 }
						 }

						 if(!detailsDOB.getWeightBreak().equalsIgnoreCase("Percent") && !detailsDOB.getWeightBreak().equalsIgnoreCase("Absolute"))
							 {
				  %>
						  <tr class='formdata'>
							<td><input type="checkbox" name='destcheck<%=m%>' id='destcheck<%=m%>' value="" <%=("checked".equals(detailsDOB.getChecked()))?"checked":""%> onClick='disableSubmit(this)' ></td>
							<td><%//=detailsDOB.getChargeDescId()==null?"":detailsDOB.getChargeDescId().trim()%>
								<%=detailsDOB.getInternalName()!=null?detailsDOB.getInternalName():""%>
							</td>
							<td>
							<% if((detailsDOB.getPrimaryBasis().equalsIgnoreCase("KG")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("LB")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("CBM")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("shipment")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("piece") || detailsDOB.getPrimaryBasis().equalsIgnoreCase("CFT")))
							 {
%>
									<%=(detailsDOB.getBrkPoint()!=null)?detailsDOB.getBrkPoint():""%>
<%
							 }
							 else
							 {
%><!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							 <!-- <input type=text size=4  name='destBreakPoint<%=m%>'  value='<%=(detailsDOB.getBrkPoint()!=null)?detailsDOB.getBrkPoint():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							 <input type=text size=4  name='destBreakPoint<%=m%>' style="background-color:YELLOW;" value='<%=(detailsDOB.getBrkPoint()!=null)?detailsDOB.getBrkPoint():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
<%
							 }
							if(detailsDOB.getWeightBreak()!=null && detailsDOB.getWeightBreak().endsWith("%") && !"Per Shipment".equalsIgnoreCase(detailsDOB.getChargeBasis()))
								 percentFlag = "%";
							 else
								 percentFlag = "";
%>
							 </td>
							<td><%=detailsDOB.getCurrency()%></td>
							<td><%=(detailsDOB.getRate()!=null)?detailsDOB.getRate()+percentFlag:""%></td>
							<td nowrap><%=(detailsDOB.getChargeBasis()!=null)?detailsDOB.getChargeBasis():""%></td>
							<td><input type=text size=8  name='destP<%=m%>'  value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>' readOnly class='text' ></td>
							<td><% if(detailsDOB.getSecondaryBasis()!=null && !"".equals(detailsDOB.getSecondaryBasis().trim())) {
							%>
<!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<!-- <input type=text size=8  name='destS<%=m%>'   value='<%=(detailsDOB.getSecUnitValue()!=null)?detailsDOB.getSecUnitValue():""%>'  class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<input type=text size=8  name='destS<%=m%>'  style="background-color:YELLOW;" value='<%=(detailsDOB.getSecUnitValue()!=null)?detailsDOB.getSecUnitValue():""%>'  class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<% } %></td>
							<td><input type=text size=8  name='destV<%=m%>'  value='<%=(detailsDOB.getRateValue()!=null)?detailsDOB.getRateValue():""%>' readOnly class='text' ></td>
							<td><%=(detailsDOB.getConvFactor()>0)?(df.format(detailsDOB.getConvFactor())):"1.00"%></td>
							<td><%=detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio()%></td>
							<td colspan="4"><input type=text size=8  name="Notes"  value='' readOnly class='text' ></td>
						  </tr>
						  <%
							 }
							}
							else
							 {
%>
							  <tr class='formdata'>
								<td><input type="checkbox" name='destcheck<%=m%>' id='destcheck<%=m%>' value="" <%=("checked".equals(detailsDOB.getChecked()))?"checked":""%> onClick='disableSubmit(this)'></td>
							  <td colspan='15'><%//=detailsDOB.getChargeDescId()==null?"":detailsDOB.getChargeDescId().trim()%>
								<%=detailsDOB.getInternalName()!=null?detailsDOB.getInternalName():""%>
							</td>
							  </tr>
<%
								primaryUnitArray	= detailsDOB.getPrimaryUnitArray();
								secondaryUnitArray	= detailsDOB.getSecUnitArray();
								rateValue			= detailsDOB.getSecUnitArray();
							 }
							for(int j=0;j<rateDetails.size();j++)
							 {

							   rateDetailsDOB=(CostingRateInfoDOB)rateDetails.get(j);

								rateIndicator = "";
								if(detailsDOB.getWeightBreak().equalsIgnoreCase("List"))
								{
%>
								  <tr class='formdata'>
								  <td></td>
									<td></td>
									<td><%=rateDetailsDOB.getWeightBreakSlab()%></td>
									<td><%=detailsDOB.getCurrency()%></td>
									<td><%=df1.format(new Double(rateDetailsDOB.getRate()))%></td>
									<td nowrap><!-- <%//=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min")?"Per Shipment ":detailsDOB.getChargeBasisDesc()%> --><%="Per Container"%></td>
<!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
									<!-- <td><input type=text size=8  name='destP<%=m%>'   value='<%=(primaryUnitArray!=null && (primaryUnitArray[j]!=null))?primaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'></td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
									<td><input type=text size=8  name='destP<%=m%>' style="background-color:YELLOW;"  value='<%=(primaryUnitArray!=null && (primaryUnitArray[j]!=null))?primaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'></td>
									<td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
<%
									if(detailsDOB.getSecondaryBasis()!=null && !"".equals(detailsDOB.getSecondaryBasis()))
									{
%><!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
										<!-- <input type=text size=8  name='destS<%=m%>'  value='<%=(secondaryUnitArray!=null && (secondaryUnitArray[j]!=null))?secondaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
										<input type=text size=8  name='destS<%=m%>' style="background-color:YELLOW;"  value='<%=(secondaryUnitArray!=null && (secondaryUnitArray[j]!=null))?secondaryUnitArray[j]:""%>' class='text' onchange ='disableSubmit(this)' onBlur='checkNumbers1(this);'>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
<%
									}
%>									</td>
									<td><input type=text size=8  name='destV<%=m%>'  value='<%=(rateDetailsDOB.getRateValue()!=null && (rateDetailsDOB.getRateValue()!=null))?rateDetailsDOB.getRateValue():""%>' readOnly  class='text' ></td>
									<td><%=(detailsDOB.getConvFactor()>0)?(df.format(detailsDOB.getConvFactor())):"1.00"%></td>
									<td><%=detailsDOB.getDensityRatio()==null?"":detailsDOB.getDensityRatio()%></td>
									<td colspan="4"><input type=text size=8  name="Notes"  value='<%//=(legDetails.getNotes()!=null)?legDetails.getNotes():""%>' readOnly class='text' ></td>
									<td colspan="4"></td>
								  </tr>
<%
									if("checked".equals(detailsDOB.getChecked()) && (rateDetailsDOB.getRateValue()!=null && !"".equals(rateDetailsDOB.getRateValue())))
									 {

											listRateFlag = true;
											/*destTotal = destTotal+Double.parseDouble(rateDetailsDOB.getRateValue());*/
											//@@Commented and Modified by Kameswari for the WPBN issue-133207
											destTotal = destTotal+(Double.parseDouble(rateDetailsDOB.getRateValue())*detailsDOB.getConvFactor());
									 }
								}
								else
								{
							   	if((detailsDOB.getWeightBreak().equalsIgnoreCase("Slab") || detailsDOB.getWeightBreak().equalsIgnoreCase("slab%") ) && !(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Max") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Base") ))
								 {
									if(detailsDOB.getRateType().equalsIgnoreCase("Flat"))
									  rateIndicator = "F";
									else if(detailsDOB.getRateType().equalsIgnoreCase("slab"))
									  rateIndicator = "S";
									else if(detailsDOB.getRateType().equalsIgnoreCase("both"))
									 {
										if(rateDetailsDOB.getRateIndicator()!=null && rateDetailsDOB.getRateIndicator().startsWith("F"))
											rateIndicator	= "F";
										else
											rateIndicator	= "S";
//										rateIndicator =(rateDetailsDOB.getRateIndicator()!=null)?rateDetailsDOB.getRateIndicator():"";
									 }


								 }else
									 rateIndicator = "";


								 if((detailsDOB.getWeightBreak().endsWith("%") || detailsDOB.getWeightBreak().equalsIgnoreCase("Percent"))&& !(rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Max") ||
								  rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Base") ))
									percentFlag = "%";
								 else
									 percentFlag = "";

							   if(detailsDOB.getWeightBreak().equalsIgnoreCase("Percent") || detailsDOB.getWeightBreak().equalsIgnoreCase("Absolute"))
							   {
						  %>
						  <tr class='formdata'>
							<td><input type="checkbox" name='destcheck<%=m%>' id = 'destcheck<%=m%>' value="" <%=("checked".equals(detailsDOB.getChecked()))?"checked":""%> onClick='disableSubmit(this)' ></td>
							<td><%//=detailsDOB.getChargeDescId()==null?"":detailsDOB.getChargeDescId().trim()%><%=detailsDOB.getInternalName()!=null?detailsDOB.getInternalName():""%></td>
							<td><%=rateDetailsDOB.getWeightBreakSlab()%></td>
							<td><%=detailsDOB.getCurrency()%></td>
							<td><%=df1.format(new Double(rateDetailsDOB.getRate()))%><%=percentFlag%></td>
							<td><%//=rateDetailsDOB.getWeightBreakSlab()%><%=detailsDOB.getChargeBasisDesc()%></td>
<%
							if((detailsDOB.getPrimaryBasis().equalsIgnoreCase("KG")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("LB")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("CBM")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("shipment")||detailsDOB.getPrimaryBasis().equalsIgnoreCase("piece") || detailsDOB.getPrimaryBasis().equalsIgnoreCase("CFT")))
							 {
%>
								<td><input type=text size=8  name='destP<%=m%>'  value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>' readOnly class='text'  ></td>
<%
							 }else
								   {
%><!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
								<!-- <td><input type=text size=8  name='destP<%=m%>'  value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
								<td><input type=text size=8  name='destP<%=m%>' style="background-color:YELLOW;" value='<%=(detailsDOB.getPrimaryUnitValue()!=null)?detailsDOB.getPrimaryUnitValue():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
<%							}
%><!--@@Commented by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<!-- <td><input type=text size=8  name='destS<%=m%>'  value='<%=(detailsDOB.getSecUnitValue()!=null)?detailsDOB.getSecUnitValue():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td> -->
<!--@@Added by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<td><input type=text size=8  name='destS<%=m%>' style="background-color:YELLOW;" value='<%=(detailsDOB.getSecUnitValue()!=null)?detailsDOB.getSecUnitValue():""%>' class='text' onchange ='disableSubmit(this)' onKeyPress='return getDotNumberCode(this)' onBlur='checkNumbers1(this);'></td>
<!--@@Ended by subrahmanyam for the WPBN issue:146446 on 23/12/2008  -->
							<td><input type=text size=8  name='destV<%=m%>'  value='<%=(detailsDOB.getRateValue()!=null)?detailsDOB.getRateValue():""%>' readOnly  class='text' ></td>
							<td><%=(detailsDOB.getConvFactor()>0)?(df.format(detailsDOB.getConvFactor())):"1.00"%></td>
							<td><%=detailsDOB.getDensityRatio()!=null?detailsDOB.getDensityRatio():""%></td>
							<td></td>
							<td colspan="4"></td>
						  </tr>
					<%		}
							else
							 {
					 %>
						   <tr class='formdata'>
						  <td></td>
							<td></td>
							<td><%=rateDetailsDOB.getWeightBreakSlab()%></td>
							<td><%=detailsDOB.getCurrency()%></td>
							<td><%=df1.format(new Double(rateDetailsDOB.getRate()))%><%=rateIndicator+percentFlag%></td>
							<td nowrap><%=rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Min") || rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Base") || rateDetailsDOB.getWeightBreakSlab().equalsIgnoreCase("Max")|| ("Both".equalsIgnoreCase(detailsDOB.getRateType()) && "F".equalsIgnoreCase(rateIndicator))?"Per Shipment ":detailsDOB.getChargeBasisDesc()%></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td colspan="4"></td>
						  </tr>
					 <%	     }
								 /*if("checked".equals(detailsDOB.getChecked()) && (detailsDOB.getRateValue()!=null && !"".equals(detailsDOB.getRateValue())))
								 {
										destTotal = destTotal+Double.parseDouble(detailsDOB.getRateValue());
								 }*/
							}
						}
								if(!detailsDOB.getWeightBreak().equalsIgnoreCase("List"))
								{
									if("checked".equals(detailsDOB.getChecked()) && (detailsDOB.getRateValue()!=null && !"".equals(detailsDOB.getRateValue())))
									 {
											//destTotal = destTotal+Double.parseDouble(detailsDOB.getRateValue());
											//@@Commented and Modified by Kameswari for the WPBN issue-133207
											destTotal = destTotal+(Double.parseDouble(detailsDOB.getRateValue())*detailsDOB.getConvFactor());
									 }
								}
								if("checked".equals(detailsDOB.getChecked()))
								{
									if(!listRateFlag && detailsDOB.getWeightBreak().equalsIgnoreCase("List"))
									{
										disabled	=	"disabled";
									}
								}
						   }

            %>
                 <tr class='formheader'>
			     <td colspan="8" align="center">&nbsp;&nbsp;&nbsp;&nbsp;SubTotal DestinationCharges(Base Currency)</td>
				 <td colspan="8"><input type=text size=10  name="destTotal"  value='<%=(destTotal>0)?deciFormat.format(destTotal)+"":""%>' readOnly  class='text'><input type='hidden' name='destSize' value ='<%=destSize%>'></td>
				 </tr>
<%
				}
			//	 System.out.println("111111111111    "+Integer.parseInt(costingHDRDOB.getLaneNo()));
				 int laneNo=Integer.parseInt(costingHDRDOB.getLaneNo());
				if(list_exNotes!=null && list_exNotes.size()>0)
				{
					int exNotesSize = list_exNotes.size();
%>
					<tr class='formheader' align='center'>
						<td colspan="16" align="center">&nbsp;&nbsp;&nbsp;&nbsp;External Notes</td>
					<tr>
					<%
						for(int i=0;i<exNotesSize;i++)
						{
							//if(laneNo==i){
%>
							<tr align = 'center' class='formdata'>
								<td colspan="16" align="center"><%=list_exNotes.get(i)%></td>
								<input type='hidden' name='extNotes' value="<%=list_exNotes.get(i)%>">
							</tr>

<%					//	}
							}

				}
				// Logger.info(FILE_NAME,"disabled"+disabled);
%>
			<!-- Added by Rakesh on 24-01-2011 for CR:231219	 -->
			<tr class='formheader' align='center'>
				<td colspan="16" align="center">&nbsp;&nbsp;&nbsp;&nbsp;Additional Notes</td>
			</tr>
		   <tr >
		 <td class='formdata' align='center' colspan="16"><textarea name='costingNotes' class="text" cols="50" rows='10' style="height:60"><%=costingMasterDOB.getCostingNotes()!=null?costingMasterDOB.getCostingNotes():""%></textarea></td>
		   </tr>  <!-- Ended by Rakesh on 24-01-2011 for CR:231219	 -->
		  <tr align='center' class='formheader'>
				<td colspan="16">Send Options</td>
			</tr>
			<tr>
				<td class='formdata' align='center' colspan="16">
					<INPUT TYPE="checkbox" NAME="emailChkBox" <%="Y".equalsIgnoreCase(costingMasterDOB.getEmailFlag())?"checked":""%>>&nbsp;Email
					<INPUT TYPE="hidden" name="email" value="on">
					<INPUT TYPE="checkbox" NAME="faxChkBox" <%="Y".equalsIgnoreCase(costingMasterDOB.getFaxFlag())?"checked":""%>>&nbsp;Fax
					<INPUT TYPE="hidden" name="fax" value="on">
					<INPUT TYPE="checkbox" NAME="printChkBox" <%="Y".equalsIgnoreCase(costingMasterDOB.getPrintFlag())?"checked":""%>>&nbsp;Print
					<INPUT TYPE="hidden" name="print" value="on">
					 <input type='hidden' name="fromPage" id="fromPage" value ='<%=request.getParameter("fromPage")%>' >
				 <input type='hidden' name="isFetchNext" id="isFetchNext" value ='Y' >
					
				</td>
			</tr>
         </table>
		<table border="0" width="100%" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" align="right">
                <input type="button" value="Calculate" name="calculate" class="input" onclick='submitToServer(this)'>
				<!-- <input type="reset" value="Reset" name="reset" class="input"> -->
				<input type="button" value="Submit" name="finalsubmit" class="input" <%=disabled%> onclick='submitToServer(this)'>
		     </td>

          </tr>
        </table>

<%
	   }
%>
		</td>
    </tr>
</table>
</form>
</body>
</html>
<%
	}catch(Exception e)
	{

		//Logger.error(FILE_NAME,"Error in JSP "+e);
    logger.error(FILE_NAME+"Error in JSP "+e);
		e.printStackTrace();
		errorMessage = "An Error Has Occurred While Displaying Costing Details. Please Try Again.";
		errorMessageObject = new ErrorMessage(errorMessage,nextNavigation);
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		keyValueList.add(new KeyValue("Operation",operation));
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);
%>
					<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	}
%>
