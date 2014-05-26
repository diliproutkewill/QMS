<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSMultiQuoteChargesSelect.jsp
Product Name		: QMS
Module Name			: Quote
Task				: Adding/Modify
Date started		:
Date modified		:
Author    			: Govind
Related Document	: CR_DHLQMS_219979

--%>

<%@page import = "java.util.ArrayList,
				  java.util.Calendar,
				  java.util.Date,
				  java.sql.Timestamp,
				  org.apache.log4j.Logger,
				  java.util.StringTokenizer,
				  com.foursoft.esupply.common.java.KeyValue,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.operations.multiquote.dob.MultiQuoteMasterDOB,
				  com.qms.operations.multiquote.dob.MultiQuoteFinalDOB,
				  com.qms.operations.multiquote.dob.MultiQuoteHeader,
				  com.qms.operations.multiquote.dob.MultiQuoteCharges,
				  com.qms.operations.multiquote.dob.MultiQuoteChargeInfo,
				  com.qms.operations.multiquote.dob.MultiQuoteFreightLegSellRates,
				  com.qms.operations.multiquote.dob.MultiQuoteFreightRSRCSRDOB"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSMultiQuoteChargesSelect.jsp"; 

  	private   String toTitleCase(String str){
			StringBuffer sb = new StringBuffer();     
			str = str.toLowerCase();
			StringTokenizer strTitleCase = new StringTokenizer(str);
			while(strTitleCase.hasMoreTokens()){
				String s = strTitleCase.nextToken();
				sb.append(s.replaceFirst(s.substring(0,1),s.substring(0,1).toUpperCase()) + " ");
			}
			
			return sb.toString();
		}
  %>
<%
 long start = System.currentTimeMillis();
	logger  = Logger.getLogger(FILE_NAME);
	ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
	String		  operation				  =	request.getParameter("Operation");
	String        dateFormat			  =	loginbean.getUserPreferences().getDateFormat();
	MultiQuoteFinalDOB finalDOB				  =	null;
	MultiQuoteHeader	  headerDOB				  =	null;
	MultiQuoteMasterDOB masterDOB			  =	null;

	ErrorMessage  errorMessageObject	  = null;
	ArrayList	  keyValueList			  = new ArrayList();

	ArrayList     charges                 = null;//@@Info of all the Legs
	ArrayList     originCharges           = null;
	ArrayList     destCharges             = null;
	ArrayList     freightCharges          = null;

	ArrayList     originChargeInfo        = null;
	ArrayList     destChargeInfo          = null;
	ArrayList     freightChargeInfo       = null;
	ArrayList          rateList           = new ArrayList();
	ArrayList         originList           = new ArrayList();
	ArrayList          destList           = new ArrayList();
	ArrayList          rateList1           = new ArrayList();
	ArrayList         originList1           = new ArrayList();
	ArrayList          destList1           = new ArrayList();
	ArrayList         sellRates            = null;
	MultiQuoteFreightLegSellRates  legCharges  = null;
	MultiQuoteCharges  chargesDOB			  = null;
	MultiQuoteChargeInfo chargeInfo			  = null;
   MultiQuoteFreightRSRCSRDOB   freightDOB     = null;
   MultiQuoteFreightLegSellRates legRateDetails		   = null;
	String		  str[]				      = null;
	String		  str1[]				   = null;
	String        selectedIndexList       = null;
	Timestamp     quoteDate				  = null;
	Timestamp     effDate				  = null;
	Timestamp     validUpto				  = null;
	String		  quoteDateStr			  = null;
	String		  effDateStr			  = null;
	String		  validUptoStr			  = null;
	String		  buyRatePermission		  = null;
	String		  colspan				  = "";
	int		       serviceColspan		  = 0;
	String		  infoColspan			  = "";
	String		  fromWhere				  = null;

	int           chargesSize			  =	0;

	int           originChargesSize		  =	0;
	int           originChargesInfoSize	  =	0;
	int           destChargesSize		  =	0;
	int           destChargesInfoSize	  =	0;
	int           freightChargesSize	  =	0;
	int           freightChargesInfoSize  =	0;

	String[]	  internalNotes			  =	null;
	String[]	  externalNotes			  =	null;
	String		  color					  = "";

	String		  originMarginDisabled	  = "";
	String		  destMarginDisabled	  = "";
	String		  frtMarginDisabled		  = "";

	String		  originDiscDisabled	  = "";
	String		  destDiscDisabled		  = "";
	String		  frtDiscDisabled		  = "";

	String		  originCheckedFlag		  = "";
	String		  destCheckedFlag		  = "";

	String		  isMarginTestPerformed	  = null;

	int[]		  originIndices			  =	null;
	int[]		  destIndices			  =	null;
	int[]		  frtIndices			  =	null;
	String		  shipmentMode			  =	null;
	String        update                  = null;
         int n =0;
	int count1 = 0;
	String breakPoint = null;
	boolean		  checkedFlag			  =	false;
    int count = 0;
	java.text.DecimalFormat df			  =	new java.text.DecimalFormat("##,###,##0.00");
	java.text.DecimalFormat df1			  =	new java.text.DecimalFormat("#######0.00");
	java.text.DecimalFormat dfDecimal			  =	new java.text.DecimalFormat("#######0.00000");//@@ Added by subrahmanyam for 180161
//@@ Added by subrahmanyam for 154381 on 09/02/09
		String		MarginTest				  = request.getParameter("MarginTest");
		int mCount=0;
		Integer     MarginTestCount=new Integer(mCount);
		Integer     MarginTestCount1=null;
		String  defaultMargin=request.getParameter("defaultMargin");
		String[] OriginChargesSelectedIndices=null;
		String[] destChargesSelectedIndices=null;
		String   submit =request.getParameter("submit");
        String    quoteName            = null;//@@Added by VLAKSHMI for the WPBN issue-167677
		String                  quoteStatus         = null;	 // @@Added by VLAKSHMI for the WPBN issue-167677
        String                     completeFlag         = null;
		int frtsize       =    0;
		int totbreaks = 0;
		String			quoteId		= null;
//@@ Ended by subrahmanyam for the Enhancement 154381 on 09/02/09
	//Calendar	  calendar	=	Calendar.getInstance();

	//java.text.DateFormat	=	new java.text.DateFormat();
		ArrayList	chargeInfoList	= new ArrayList();

	try
	{
		eSupplyDateUtility.setPatternWithTime("DD-MON-YYYY");//in order to get the full month name
	}
	catch(Exception exp)
	{

    logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
	}
	try
	{
          selectedIndexList = (String)session.getAttribute("selectedIndexList");
		  session.removeAttribute("selectedIndexList");
	      quoteName			=	request.getParameter("quoteName");//@@Added by VLAKSHMI for the WPBN issue-167677
		  quoteStatus			=	request.getParameter("quoteStatus");//@@Added by VLAKSHMI for the WPBN issue-167677
		  	  completeFlag			=	request.getParameter("completeFlag");//@@Added by VLAKSHMI for the WPBN issue-167677
		isMarginTestPerformed = (String)request.getAttribute("isMarginTestPerformed");
		finalDOB		      =	(MultiQuoteFinalDOB)session.getAttribute("finalDOB");
		System.out.println("headerDOB-------"+headerDOB);
		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();
		System.out.println("headerDOB-------"+headerDOB);
		fromWhere			  =  (String)request.getAttribute("fromWhere");
		update			      =  request.getParameter("update");
		//System.out.println("Count---"+request.getParameter("count"));
    //  count                    =  Integer.parseInt((String)(request.getParameter("count")!= null?request.getParameter("count"):0));
		quoteDate			  =  headerDOB.getDateOfQuotation();
		effDate				  =  headerDOB.getEffDate();
		validUpto			  =  headerDOB.getValidUpto();

		str					  = eSupplyDateUtility.getDisplayStringArray(quoteDate);
		quoteDateStr		  = str[0];

		str					  = eSupplyDateUtility.getDisplayStringArray(effDate);
		effDateStr			  =	str[0];

		if(validUpto!=null)
		{
			str					  = eSupplyDateUtility.getDisplayStringArray(validUpto);
			validUptoStr		  = str[0];
		}

		buyRatePermission	  = masterDOB.getBuyRatesPermission();

		if("Y".equals(buyRatePermission))
		{
			colspan			  = "14";
			serviceColspan	  = 7;
			infoColspan		  =	"7";
		}
		else
		{
			colspan			  = "11";
			serviceColspan	  = 6;
			infoColspan		  =	"5";
		}

		charges				  = finalDOB.getLegDetails();

		chargesSize			  = charges.size();

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

if(!"charges".equalsIgnoreCase(masterDOB.getQuoteWith()))
		{
	    		System.out.println("charges-------"+charges);
		chargeInfoList = ((MultiQuoteFreightLegSellRates)charges.get(0)).getFreightChargesList();
		System.out.println("chargeInfoList-------"+chargeInfoList);
					chargeInfo	=	(MultiQuoteChargeInfo)chargeInfoList.get(0);
					totbreaks = chargeInfo.getMultiBreakPoints().length;

		}else
		{
			totbreaks = 0;
		}


%>
<!--  @@ added by subrahmanyam for the enhancement 180161 -->
<%!
	public String round(double sellRate)
	{

    java.text.DecimalFormat dfDecimal	 =	new java.text.DecimalFormat("#######0.00000");
	java.text.DecimalFormat dfDecimal2	 =	new java.text.DecimalFormat("#######0.00");
      String rateString ="";
      int k = 0;
      int l = 0;
      int m = 0;
      rateString = Double.toString(sellRate);
      k = rateString.length();
      l = rateString.indexOf(".");
      m = (k - l)-1;
      if(m>5){
//		 rateString = dfDecimal.format(sellRate);
		 rateString = rateString.substring(0,l+6);
		 return rateString;
		}
		else if(m==1 || l==-1)
		{

			rateString = dfDecimal2.format(sellRate);
			return rateString;
		}
		else
			return Double.toString(sellRate);


    }



%>
<!--  @@ Ended by subrahmanyam for the enhancement 180161 -->
<HTML>
<HEAD>
<TITLE> Quote </TITLE>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
<script>

var noOfLegs					=	'<%=chargesSize%>';
var internalNotesArray			=	new Array();
var externalNotesArray			=	new Array();
var bcFlag						=	'';
var selectedbreaks				=	'';
<%
	if(finalDOB.getInternalNotes()!=null)
	{
		internalNotes			=	finalDOB.getInternalNotes();
	//	System.out.println("internalNotes----------"+internalNotes.length);
		for(int i=0;i<internalNotes.length;i++)
		{
%>			internalNotesArray[internalNotesArray.length]	=	"<%=internalNotes[i]!=null?internalNotes[i].trim():""%>";
<%
		}
	}
	if(finalDOB.getExternalNotes()!=null)
	{
		externalNotes			=	finalDOB.getExternalNotes();
		for(int i=0;i<externalNotes.length;i++)
		{
%>			externalNotesArray[externalNotesArray.length]	=	"<%=externalNotes[i]!=null?externalNotes[i].trim():""%>";
<%
		}
	}
%>
function closeandContinue()
{

document.forms[0].action='QMSReportController?Operation=Escalated&fromSummary=Yes&nextOperation=Escalatedpagging&subOperation=html/excel';
document.forms[0].submit();
}
//@@ Added by govind for the CR
function calaculateSellRate(obj,info,bre,breaks,brValue,rsrValue){
	
		 var marginvalue = "";
		 var discountvalue ="";
		 var marginType		=	"";
		 var discountType	=	"";
		 var calSellRate	=	0.0;
		if(bre==2){//@@ if margin ADD
            marginvalue = round(obj.value);
			if(marginvalue != ''  ){
               obj.value = round(marginvalue);
			marginType	=	 document.getElementById("marginTypes"+info+"1"+breaks).value;
			if(marginType	==	'A')
			 calSellRate	= round(parseFloat(brValue) + parseFloat(marginvalue));
			else if(marginType	==	'P')
			 calSellRate	= round(parseFloat(brValue) + (( parseFloat(brValue) * parseFloat(marginvalue) )/100));
			}
			
		}
		if(bre==5){//@@ if discount (subtract)
       //    alert("330--rsr---"+rsrValue+"--br--"+brValue)
			discountvalue = round(obj.value);
			if(discountvalue != ''  ){
				 obj.value = round(discountvalue);
			discountType	=	 document.getElementById("discountTypes"+info+"4"+breaks).value;
			if(discountType	==	'A')
			 calSellRate	= round(parseFloat(rsrValue) - parseFloat(discountvalue));
			
			else if(discountType	==	'P')
			 calSellRate	= round(parseFloat(rsrValue) - (( parseFloat(rsrValue) * parseFloat(discountvalue) )/100));
			}
			
			
			}
document.getElementById("calsellrate"+info+6+breaks).value=calSellRate;
}

function setSelectedIndexValueOnLoad() 
		{
	document.forms[0].selectedIndexList.value ='';
	var totbreaks = document.getElementsByName("breakChk");
    var totbreakslength = '<%=totbreaks%>';
	for(var i=0;i<totbreakslength;i++)
			{
		     selectedbreaks =document.forms[0].selectedIndexList.value;
              if(document.getElementsByName("breakChk")[i].checked)
				{
                    selectedbreaks = selectedbreaks+i+',';
					document.forms[0].selectedIndexList.value = selectedbreaks;
				  }
			}
		
	//alert(document.forms[0].selectedIndexList.value)	
	<%if("checked".equals(request.getParameter("selectCarrier"))){%>
		document.forms[0].carrier.checked=true;
		document.forms[0].selectCarrier.value="checked";
	<%}if("checked".equals(request.getParameter("selectService"))){%>
		document.forms[0].serviceLevel.checked=true;
	    document.forms[0].selectService.value="checked";
	<%}if("checked".equals(request.getParameter("selectFrequency"))){%>
		document.forms[0].frequency.checked=true;
	    document.forms[0].selectFrequency.value="checked";
	<%}%>
}

//Added by Anil.k for Carrier, Service and Frequency
function selectCSF(obj)
{	
	if(obj.value=="C")
	{
		if(obj.checked)
			document.forms[0].selectCarrier.value="checked";
		else
			document.forms[0].selectCarrier.value="unchecked";
	}
	else if(obj.value=="S")
	{
		if(obj.checked)
			document.forms[0].selectService.value="checked";
		else
			document.forms[0].selectService.value="unchecked";
	}
	else if(obj.value=="F")
	{
		if(obj.checked)
			document.forms[0].selectFrequency.value="checked";
		else
			document.forms[0].selectFrequency.value="unchecked";
}
}//Ended by Anil.k for Carrier, Service and Frequency




function setSelectedIndexValue(obj,breakcount)
	{
	var selectedlist="";
    selectedbreaks =document.forms[0].selectedIndexList.value;
	if(obj.checked){
	selectedbreaks = selectedbreaks+breakcount+',';
	document.forms[0].selectedIndexList.value = selectedbreaks;
	}else
	{
	   selectedlist = document.forms[0].selectedIndexList.value;
	   document.forms[0].selectedIndexList.value =selectedlist.replace(breakcount+",","");
	}
	
	}



function setEnableDisableAbsPers(info,bre,breaksLen,sellbuyflag)
{
		if(bre==1 && sellbuyflag=='RSR'){
		var flag		=	 confirm('This Will Disable Discount Types  And Disount Value for this Charge. \nDo You Want to Continue?')
		if(flag)
		{

			document.getElementById("multiSelectedFlag"+info).value="BR";

			for(var i=0; i<breaksLen; i++)
			{
				document.getElementById("discountTypes"+info+"4"+i).disabled=true;
				document.getElementById("DiscountVal"+info+"5"+i).disabled=true;
				document.getElementById("MarginVal"+info+"2"+i).disabled=false;
			}
	   }
	}
}



//@@ Govind ends
function initialize()
{
	importXML('<%=request.getContextPath()%>/xml/QMSQuote.xml');
}
function initializeDynamicContentRows()
{
	initializeDynamicContentDetails();
}
function initializeDynamicContentDetails()
{
	var tableObj	= document.getElementById("QuoteNotes");
	var totalLanes	=  internalNotesArray.length==0? 1 :internalNotesArray.length;
	if(internalNotesArray.length==0&&externalNotesArray.length!=0)
	{
	 totalLanes = externalNotesArray.length;
	 }
	for(var i=0;i<totalLanes;i++)
	{
		createDynamicContentRow(tableObj.getAttribute("id"));
		//Commented by Mohan for issue on 12112010
		//if(externalNotesArray==null || externalNotesArray.length==0)
		//	return;

		var internalNotes	=	document.getElementsByName("internalNotes");
		var externalNotes	=	document.getElementsByName("externalNotes");

		if(internalNotesArray.length>0)
		{
			internalNotes[i].value	=	internalNotesArray[i];
			//externalNotes[i].value	=	externalNotesArray[i];		//Commented by Mohan for issue on 12112010
		}
		else
		{
			internalNotes[i].value  =  "";
			//externalNotes[i].value	=	externalNotesArray[i];		//Commented by Mohan for issue on 12112010
		}

		//Commented by Mohan for issue on 12112010
		if(externalNotesArray!=null && externalNotesArray.length>0 && externalNotesArray[i]!='undefined')
		{
			externalNotes[i].value	=	externalNotesArray[i];
		}
		else
		{
			externalNotes[i].value	=	"";
		}

	}
}

function  riverseCalOfCharges(obj,orgDestDiscountValue,orgDestMarginValue,orgDestDiscountType,orgDestMarginType,brValue,rsrValue,k)
{
	var marginType ="";
	var discountType ="";
	var marginVal =0.0;
	var discountVal = 0.0;
	var brVal =0.0;
	var rsrVal = 0.0;
	var callSellRate =0.00;
//alert("orgDestDiscountValue--"+orgDestDiscountValue+"orgDestMarginValue--"+orgDestMarginValue+"K---"+k)
	
		if(!document.getElementsByName(orgDestMarginValue)[k].disabled){//if margin given
             marginType = document.getElementsByName(orgDestMarginType)[k].value;
			 marginVal  = document.getElementsByName(orgDestMarginValue)[k].value;
			 brVal  = brValue;
			 if(marginType == "A")
			{
             marginVal = round(parseFloat(obj.value) - parseFloat(brVal));
             
			}else if(marginType == "P"){
				marginVal = round((100*(parseFloat(obj.value) - parseFloat(brVal)))/parseFloat(brVal));
			}
			document.getElementsByName(orgDestMarginValue)[k].value = marginVal;
		//	alert("Given Margin-----"+marginType+"***********"+marginVal+"---------------"+brVal)
		}else if(!document.getElementsByName(orgDestDiscountValue)[k].disabled){//if discount given
		
              discountType = document.getElementsByName(orgDestDiscountType)[k].value;
              discountVal  = document.getElementsByName(orgDestDiscountValue)[k].value;
			  rsrVal = rsrValue;
			   if(discountType == "A")
			{
                 discountVal  = round(  parseFloat(rsrVal) - parseFloat(obj.value) ) ;

			}else if(discountType == "P"){
			   discountVal =  round(((parseFloat(rsrVal) - parseFloat(obj.value))*100)/parseFloat(rsrVal)) ;
			}
			document.getElementsByName(orgDestDiscountValue)[k].value = discountVal;
			//alert("Given Discount---"+discountType+"********"+discountVal+"-------------"+rsrVal)
			}

	
                 
}





function reverseCallSellRate(obj,info,breaks,brValue,rsrValue)
{
	var calSellRate = round(obj.value!= ''?obj.value:0.00  );
	var marginvalue=0;
	var discountvalue=0;
	var marginTypes = '';
	var discountTypes = '';
	var sell_Buy_Flag = document.getElementById("multiSelectedFlag"+info).value;
  if( sell_Buy_Flag == "BR")
	{
       marginTypes = document.getElementById("marginTypes"+info+"1"+breaks).value;
	   if(marginTypes == "A")
		{
           marginvalue =  round(  parseFloat(calSellRate) - parseFloat(brValue)  ) ;
		}
		else if(marginTypes == "P")
		{
          marginvalue =  round(((parseFloat(calSellRate) - parseFloat(brValue))*100)/round(parseFloat(brValue))) ;
		}
		
		document.getElementById("MarginVal"+info+"2"+breaks).value = marginvalue ;   
	}
  else if(sell_Buy_Flag == "RSR")
	{
         discountTypes =  document.getElementById("discountTypes"+info+"4"+breaks).value;
		 if(discountTypes == "A")
		{
            discountvalue  = round(  parseFloat(calSellRate) - parseFloat(rsrValue) ) ;
		}
		else if(discountTypes == "P")
		{
      discountvalue =  round(((parseFloat(rsrValue) - parseFloat(calSellRate))*100)/parseFloat(rsrValue)) ;
		}
		document.getElementById("DiscountVal"+info+"5"+breaks).value = discountvalue ;
	}
}

function calSellRateOnAbsPerChange(obj,bre,info,breaks,brValue,rsrValue)
{
 var obj1 =''; 
 var marginvalue = '';
 var discountvalue ='';
 var calSellRate =0;
if(bre == 1)
	{
    marginvalue =round(document.getElementById("MarginVal"+info+"2"+breaks).value);
    if(obj.value == 'A')
	calSellRate	= round(parseFloat(brValue) + parseFloat(marginvalue));
	else if(obj.value	==	'P')
	calSellRate	= round(parseFloat(brValue) + (( parseFloat(brValue) * parseFloat(marginvalue) )/100));
     }
 if(bre == 4)
	{
   discountvalue = round(document.getElementById("DiscountVal"+info+"5"+breaks).value);
	if(obj.value	==	'A')
	 calSellRate	= round(parseFloat(rsrValue) - parseFloat(discountvalue));
	else if(obj.value	==	'P')
	 calSellRate	= round(parseFloat(rsrValue) - (( parseFloat(rsrValue) *parseFloat(discountvalue))/100));
	}
document.getElementById("calsellrate"+info+"6"+breaks).value=calSellRate;
}




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

		 if((event.keyCode < 45 || event.keyCode==47 || event.keyCode > 57) )
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

function chr(input)
{
	s = input.value;
	filteredValues = "'\"";
	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if ( filteredValues.indexOf(c) == -1 )
				returnString += c;
		else
			flag = 1;
	}
	if( flag==1 )
	{
		alert("Special Characters not allowed");
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
		{
			if( field.elements[i] == input )
			{
				document.forms[0].elements[i].focus();
				break;
			}
		}
	}

	input.value = returnString;
}
function selectAll(laneno)
{
	var originChk = document.getElementsByName("originChkBox"+laneno);
	var destChk	  = document.getElementsByName("destChkBox"+laneno);

	if(document.getElementById("select"+laneno).checked)
	{
		for(var i=0;i<originChk.length;i++)
		{
			originChk[i].checked=true;
			setValue(i,"origin",laneno);
		}
		
		for(var i=0;i<destChk.length;i++)
		{
			destChk[i].checked=true;
			setValue(i,"dest",laneno);
		}
	}
	else
	{
		for(var i=0;i<originChk.length;i++)
		{
			originChk[i].checked=false;
			setValue(i,"origin",laneno);
		}
		
		for(var i=0;i<destChk.length;i++)
		{
			destChk[i].checked=false;
			setValue(i,"dest",laneno);
		}
	}
}
function setAllValuesOnLoad()
{
	var isMarginTestPerformed = '<%=isMarginTestPerformed%>';
	var frtsize = '<%=finalDOB.getLegDetails().size()%>';
	var submitObj  = document.getElementsByName("submit");
	
   for(var fr=0;fr<frtsize;fr++){
	   var originChk = document.getElementsByName("originChkBox"+fr);
	var destChk	  = document.getElementsByName("destChkBox"+fr);
	for(var i=0;i<originChk.length;i++)
	{
		setValue(i,"origin",fr);
	}
	
	for(var i=0;i<destChk.length;i++)
	{
		setValue(i,"dest",fr);
	}

	if(isMarginTestPerformed!='Y')
	{
		for(var i=0;i<submitObj.length;i++)
		{
			if(submitObj[i].value=='Update') // Added by Gowtham
				submitObj[i].disabled = true;
		}
	}
   }
}

function setValue(index,chargeType,laneno)
{

	if(chargeType=='origin')
	{
	
		if(document.getElementsByName("originChkBox"+laneno)[index].checked)
		{
		  document.getElementsByName("originChargeSelectedFlag"+laneno)[index].value='Y';
		    document.getElementsByName("originChargeIndices"+laneno)[index].value=index;
	}
	else
	{
			document.getElementsByName("originChargeSelectedFlag"+laneno)[index].value='N';
			  document.getElementsByName("originChargeIndices"+laneno)[index].value=index;
		}

	}
	else if(chargeType=='dest')
	{ 
		if(document.getElementsByName("destChkBox"+laneno)[index].checked)
		{
			document.getElementsByName("destChargeIndices"+laneno)[index].value=index;
			document.getElementsByName("destChargeSelectedFlag"+laneno)[index].value='Y';
		}
		else
		{
			document.getElementsByName("destChargeSelectedFlag"+laneno)[index].value='N';
		    document.getElementsByName("destChargeIndices"+laneno)[index].value=index;
		}
   }
   

//ended by subrahmanyam for the enhanement 154381
	
}


function disableUnChecked()
{

	
    var subVal   = document.forms[0].subVal.value;//subrahmanyam
	var frtsize = '<%=finalDOB.getLegDetails().size()%>';
	
for(var fr=0;fr<frtsize;fr++){
	var originChk = document.getElementsByName("originChkBox"+fr);
	var destChk	  = document.getElementsByName("destChkBox"+fr);
	for(var i=0;i<originChk.length;i++)
	{
//commented by subrahmanyam for 154381 on 03/02/09
		if(originChk[i].checked==false && subVal=='Next>>'){
		  document.getElementsByName("originChargeIndices"+fr)[i].disabled=true;
		  document.getElementsByName("originChargeIndices"+fr)[i].value=i;//added by KAM FOR DUPLICATE CHARGES ISSUE:
		}
		else
		{
			
			 document.getElementsByName("originChargeIndices"+fr)[i].value=i;//added by KAM FOR DUPLICATE CHARGES ISSUE:
			document.getElementsByName("originChkBox"+fr)[i].checked=originChk[i].checked;//added by subrahmanyam for 154381
			
		}

		//alert(document.getElementsByName("originChargeIndices")[i].disabled);
	}
	for(var i=0;i<destChk.length;i++)
	{

		if(destChk[i].checked==false  && (subVal=='Next>>')){
			document.getElementsByName("destChargeIndices"+fr)[i].disabled=true;
			 document.getElementsByName("destChargeIndices"+fr)[i].value=i;
			}
		else
		{
			
			 document.getElementsByName("destChargeIndices"+fr)[i].value=i;
			document.getElementsByName("destChkBox"+fr)[i].checked=destChk[i].checked;
		}
	}
}
	
}
var permission = '<%=buyRatePermission%>';
function enableDisableMarginType(fromObj,type,index,label,toObj,rate,type1,chargeType,rowNo,legNo,buysellFlag,laneno)
{

//alert('fromObj,type,index,label,toObj,rate,type1,chargeType,rowNo,legNo,buysellFlag..'+fromObj+": "+type+": "+index+": "+label+": "+toObj+": "+rate+": "+type1+": "+chargeType+": "+rowNo+": "+legNo+": "+buysellFlag);

	var obj		=	document.getElementsByName(type+laneno+index);
	var objType	=	document.getElementsByName(type+"Type"+laneno+index);
var obj1		=	document.getElementsByName(toObj+laneno+index);
	var flag			=	true;
	var disabledFlag	=	false;
	bcFlag				=  buysellFlag;
 var b = false;
	for(var i=0;i<obj.length;i++)
	{
		if(obj[i].disabled==true)
			disabledFlag  = true;
	}

	if(!disabledFlag && permission=='Y')
		flag		=	 confirm('This Will Disable '+label+' and '+label+ ' Type for this Charge. \nDo You Want to Continue?')

	if(flag)
	{
		for(var i=0;i<obj.length;i++)
		{



			objType[i].disabled=true;
			obj[i].disabled='disabled';
			obj1[i].disabled='';
		}
	}

	else
	{
		if(fromObj.options.value=='P')
			fromObj.options.value = 'A';
		else
			fromObj.options.value='P'
	}

	if(flag&&(buysellFlag=='RSR'||buysellFlag=='SC'||buysellFlag=='S'))
		{
		    calcSellRate(rate,index,type1,chargeType,obj.length,legNo,laneno);
		}
		else if(buysellFlag=='BR'||buysellFlag=='SBR'||buysellFlag=='BC'||buysellFlag=='B')
	  {
		calcSellRate(rate,index,type,chargeType,rowNo,legNo,laneno);
		}


}

function enableDisableMargin(fromObj,preValue,type,index,label)
{
	
	var obj		=	document.getElementsByName(type+index);
	
		var temp = type.substr(0,type.length-1);
        var laneno = type.substr(type.length-1,type.length)
	var objType	=	document.getElementsByName(temp+"Type"+laneno+index);
	var flag			=	true;
	var disabledFlag	=	false;

	for(var i=0;i<obj.length;i++)
	{
		if(obj[i].disabled==true)
			disabledFlag  = true;
	}

	if(!disabledFlag && permission=='Y')
		flag		=	 confirm('This Will Disable '+label+' and '+label+ ' Type for this Charge. \nDo You Want to Continue?')

	if(flag)
	{
		for(var i=0;i<obj.length;i++)
		{
			objType[i].disabled=true;
			obj[i].disabled=true;
		}

	}
	else
	{
		fromObj.value=preValue;
	}
}

function calcSellRate(rate,index,type,chargeType,rowNo,legNo,laneno)
{
	
	var noOfLegs	=	<%=chargesSize%>;

	var object=  document.getElementsByName(chargeType+type+laneno+index);
   var object1=  document.getElementsByName(chargeType+type+laneno+legNo+index);
	var length = 1;

	var length1 = 1;
	var count = 0;
	if(object!=null)
	{
	  length = object.length;

	}
	if(object1!=null)
	{
		length1 = object1.length;

	}
	if(chargeType=='origin' || chargeType=='dest')
	{

	   if(rowNo==length)
		{
		for(var k=0;k<rowNo;k++)
			{

		if(type=='Margin')
		{
			var marginType = document.getElementsByName(chargeType+type+"Type"+laneno+index)[k].value;
			var margin	   = document.getElementsByName(chargeType+type+laneno+index)[k].value;
			if(isNaN(margin))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+laneno+index)[k].value ='';
				document.getElementsByName(chargeType+type+laneno+index)[k].focus();
				return false;
			}
			if(marginType=='A')
			{
				if(margin.length!=0)
				{

					document.getElementsByName(chargeType+"SellRate"+laneno+index)[k].value = round(parseFloat(rate[count++])+parseFloat(margin));
				}
			}
			else if(marginType=='P')
			{
				if(margin.length!=0)
				{

					document.getElementsByName(chargeType+"SellRate"+laneno+index)[k].value = round(parseFloat(rate[count])+(parseFloat(margin)*parseFloat(rate[count++])/100));
				}

			}

		}
		else if(type=='Discount')
		{
			var discountType = document.getElementsByName(chargeType+type+"Type"+laneno+index)[k].value;
			var discount	 = document.getElementsByName(chargeType+type+laneno+index)[k].value;

			if(isNaN(discount))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+laneno+index)[k].value ='';
				document.getElementsByName(chargeType+type+laneno+index)[k].focus();
				return false;
			}

			if(discountType=='A')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[k].value = round(parseFloat(rate[count++])-parseFloat(discount));
			}
			else if(discountType=='P')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[k].value = round(parseFloat(rate[count])-((parseFloat(discount)*parseFloat(rate[count++])/100)));
			}

		}
	   }
	}
	  else
		{
		if(type=='Margin')
		{
			var marginType = document.getElementsByName(chargeType+type+"Type"+laneno+index)[rowNo].value;
			var margin	   = document.getElementsByName(chargeType+type+laneno+index)[rowNo].value;
			if(isNaN(margin))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+laneno+index)[rowNo].value ='';
				document.getElementsByName(chargeType+type+laneno+index)[rowNo].focus();
				return false;
			}
			if(marginType=='A')
			{
				if(margin.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[rowNo].value = round(parseFloat(rate)+parseFloat(margin));
			}
			else if(marginType=='P')
			{
				if(margin.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[rowNo].value = round(parseFloat(rate)+(parseFloat(margin)*parseFloat(rate)/100));
			}

		}
		else if(type=='Discount')
		{
			var discountType = document.getElementsByName(chargeType+type+"Type"+laneno+index)[rowNo].value;
			var discount	 = document.getElementsByName(chargeType+type+laneno+index)[rowNo].value;

			if(isNaN(discount))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+laneno+index)[rowNo].value ='';
				document.getElementsByName(chargeType+type+laneno+index)[rowNo].focus();
				return false;
			}

			if(discountType=='A')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[rowNo].value = round(parseFloat(rate)-parseFloat(discount));
			}
			else if(discountType=='P')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[rowNo].value = round(parseFloat(rate)-((parseFloat(discount)*parseFloat(rate)/100)));
			}

		}
	  }
	}
	
}

//added by subrahmanyam for 180161
function calcSellRate1(rate,index,type,chargeType,rowNo,legNo,sellBuyFlag,laneno)
{

	var noOfLegs	=	<%=chargesSize%>;
	bcFlag = sellBuyFlag;
   var object=  document.getElementsByName(chargeType+type+laneno+index);
   var object1=  document.getElementsByName(chargeType+type+laneno+legNo+index);
   var length = 1;
   var length1 = 1;
	var count = 0;
	if(object!=null)
	{
	  length = object.length;

	}
	if(object1!=null)
	{
		length1 = object1.length;

	}
	if(chargeType=='origin' || chargeType=='dest')
	{
       if(rowNo==length)
		{
		for(var k=0;k<rowNo;k++)
			{
             
		if(type=='Margin')
		{
		
			var marginType = document.getElementsByName(chargeType+type+"Type"+laneno+index)[k].value;
			var margin	   = document.getElementsByName(chargeType+type+laneno+index)[k].value;
			if(isNaN(margin))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+laneno+index)[k].value ='';
				document.getElementsByName(chargeType+type+laneno+index)[k].focus();
				return false;
			}
			if(marginType=='A')
			{
				if(margin.length!=0)
				{

					document.getElementsByName(chargeType+"SellRate"+laneno+index)[k].value = round(parseFloat(rate[count++])+parseFloat(margin));
				}
			}
			else if(marginType=='P')
			{
				if(margin.length!=0)
				{

					document.getElementsByName(chargeType+"SellRate"+laneno+index)[k].value = round(parseFloat(rate[count])+(parseFloat(margin)*parseFloat(rate[count++])/100));
				}

			}

		}
		else if(type=='Discount')
		{
			var discountType = document.getElementsByName(chargeType+type+"Type"+laneno+index)[k].value;
			var discount	 = document.getElementsByName(chargeType+type+laneno+index)[k].value;

			if(isNaN(discount))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+laneno+index)[k].value ='';
				document.getElementsByName(chargeType+type+laneno+index)[k].focus();
				return false;
			}

			if(discountType=='A')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[k].value = round(parseFloat(rate[count++])-parseFloat(discount));
			}
			else if(discountType=='P')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[k].value = round(parseFloat(rate[count])-((parseFloat(discount)*parseFloat(rate[count++])/100)));
			}

		}
	   }
	}
	  else
		{
		if(type=='Margin')
		{
			var marginType = document.getElementsByName(chargeType+type+"Type"+laneno+index)[rowNo].value;
			var margin	   = document.getElementsByName(chargeType+type+laneno+index)[rowNo].value;
			if(isNaN(margin))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+laneno+index)[rowNo].value ='';
				document.getElementsByName(chargeType+type+laneno+index)[rowNo].focus();
				return false;
			}
			if(marginType=='A')
			{
				if(margin.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[rowNo].value = round(parseFloat(rate)+parseFloat(margin));
			}
			else if(marginType=='P')
			{
				if(margin.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[rowNo].value = round(parseFloat(rate)+(parseFloat(margin)*parseFloat(rate)/100));
			}

		}
		else if(type=='Discount')
		{
			var discountType = document.getElementsByName(chargeType+type+"Type"+laneno+index)[rowNo].value;
			var discount	 = document.getElementsByName(chargeType+type+laneno+index)[rowNo].value;

			if(isNaN(discount))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+laneno+index)[rowNo].value ='';
				document.getElementsByName(chargeType+type+laneno+index)[rowNo].focus();
				return false;
			}

			if(discountType=='A')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[rowNo].value = round(parseFloat(rate)-parseFloat(discount));
			}
			else if(discountType=='P')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+laneno+index)[rowNo].value = round(parseFloat(rate)-((parseFloat(discount)*parseFloat(rate)/100)));
			}

		}
	  }
	}
	
}
//added by subrahmanyam for 180161
function roundOrgDest(n)
{

				var s =n+'';
				var len = s.length;
				var indexlen = s.indexOf('.');
				var total = len - indexlen -1;

				if(total > 5 && indexlen !=-1)
				{
					var flag	=	false;
					if(n<0)
					{
					n		=	Math.abs(n);

					flag	=	true;
					}
					n = n - 0; // force number
					d = 2;
					n += Math.pow(10, - (d + 4)); // round first
					n += ''; // force string

					if(flag)
					return -(d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 4));
					else
					return d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 4);
				}
				else if(total == 1 || indexlen==-1)
				{
						var flag	=	false;
						if(n<0)
						{
						n		=	Math.abs(n);
						flag	=	true;
						}
						n = n - 0; // force number
						d = 2;
						var f = Math.pow(10, d);
						n += Math.pow(10, - (d + 1)); // round first
						n = Math.round(n * f) / f;
						n += Math.pow(10, - (d + 1)); // and again
						n += ''; // force string
						if(flag)
							return -(d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 1));
						else
							return d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 1);

				}
				else
					return n;


}

// ended by subrahmanyam for 180161
function round (n)
{


	//@@ Added by subrahmanyam for the Enhancement 180161 on 28/Aug/09
		if ("B"==bcFlag || "S"==bcFlag)
		{
				var s =n+'';
				var len = s.length;
				var indexlen = s.indexOf('.');
				var total = len - indexlen -1;
				if(total > 5 && indexlen !=-1)
				{
					var flag	=	false;
					if(n<0)
					{
					n		=	Math.abs(n);

					flag	=	true;
					}
					n = n - 0; // force number
					d = 2;
					n += Math.pow(10, - (d + 4)); // round first
					n += ''; // force string

					if(flag)
					return -(d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 4));
					else
					return d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 4);
				}
             else if(total == 1 || indexlen==-1)
            {
						var flag	=	false;
						if(n<0)
						{
						n		=	Math.abs(n);
						flag	=	true;
						}
						n = n - 0; // force number
						d = 2;
						var f = Math.pow(10, d);
						n += Math.pow(10, - (d + 1)); // round first
						n = Math.round(n * f) / f;
						n += Math.pow(10, - (d + 1)); // and again
						n += ''; // force string
						if(flag)
							return -(d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 1));
						else
							return d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 1);

				}
				else
					return n;

		}
		else
		{
			 var flag	=	false;
			if(n<0)
			{
			n		=	Math.abs(n);
			flag	=	true;
			}
			n = n - 0; // force number
			d = 2;
			var f = Math.pow(10, d);
			n += Math.pow(10, - (d + 1)); // round first
			n = Math.round(n * f) / f;
			n += Math.pow(10, - (d + 1)); // and again
			n += ''; // force string
			if(flag)
			return -(d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 1));
			else
			return d == 0 ? n.substring(0, n.indexOf('.')):n.substring(0, n.indexOf('.') + d + 1);
		}
// @@ Ended by subrahmanyam for the enhancement 180161

}
function checkMarginTest()
{
	var chrgQuote	=	"<%=masterDOB.getQuoteWith()%>";//Added by Anil.k	
	var submitValue= document.forms[0].subVal.value;
	var submitObj  = document.getElementsByName("submit");
//	alert(submitValue)
	if("<<Back"!= submitValue){
    if((document.forms[0].selectedIndexList.value).length != 0 || "Charges"==chrgQuote)//Modified by Anil.k	
	{
       if(submitValue=='Next>>')
	{
		var flag	=	validate();

		if(!flag)
			return false;

		else
		{

		var marginTest = document.getElementsByName('marginTestFailed');
		var flag	   = false;

		var confirmFlag	= false;
       //   alert(marginTest.length) 
		for(var i=0;i<marginTest.length;i++)
		{
			if(marginTest[i].value=='Y')
				flag = true;
		}

		if(flag)
		{
			/*confirmFlag = confirm('Some of the Charges\Freights have Failed the Margin Test.\n Are You Sure You Want to Continue?');
			if(confirmFlag)
			{
				document.forms[0].marginTestFlag.value = 'F';
				return true;
			}
			else
			{*/
			//confirmFlag = confirm('MarginTest Failed');
			alert("Margin Test Failed");
			
			for(var i=0;i<submitObj.length;i++)
				{
					if(submitObj[i].value=='Update')
						submitObj[i].disabled = true;
				}
				return false;
			
		}


	  }
	}
	}else
	{
		alert("Please select Freight Breaks for margin test" );
		return false;
		
	}
	}
return true;
}

function validate()
{
	var internal		=	document.getElementsByName("internalNotes");
	var external		=	document.getElementsByName("externalNotes");

	for(var i=0;i<internal.length;i++)
	{
		if(internal[i].value.length==0 && external[i].value.length==0 && i!=0)
		{
			alert('Please Enter Internal or External Notes at Row '+(i+1));
			internal[i].focus();
			return false;
		}
    //@@Added by Kameswari for the WPBN issue-58293
		else if(internal[i].value.length>1000 )//Modified by Mohan for Issue on 12112010
		{
			alert('Internal Notes length should not be more than 1000 characters');
			internal[i].focus();
			return false;
		}
		else if(external[i].value.length>1000)
		{
			alert('External Notes length should not be more than 1000 characters');
			external[i].focus();
			return false;
		}
    //@@WPBN issue-58293
	}
	return true;
}

function setSubmitValue(val)
{
	//alert(val)
	document.forms[0].subVal.value = val;

}
function roundAll()
{

	for(var i=0;i<document.forms[0].elements.length;i++)
	{
		//@@ Commented by subrahmanyam for the enhancement 180161
		/*
		if(document.forms[0].elements[i].type=='text' && !isNaN(document.forms[0].elements[i].value))
		{
			document.forms[0].elements[i].value=round(document.forms[0].elements[i].value);
		}
		*/
		//@@ Added by subrahmanyam for 180161
		if(document.forms[0].elements[i].id=='chargeSellrate' || document.forms[0].elements[i].id=='Destrates')
    {
    				document.forms[0].elements[i].value=roundOrgDest(document.forms[0].elements[i].value);
		}
		else{
		if(document.forms[0].elements[i].type=='text' && !isNaN(document.forms[0].elements[i].value))
		{
			document.forms[0].elements[i].value=round(document.forms[0].elements[i].value);
		}
		}

	}
}
function openDetailedView()
{
	Url		= "QMSQuoteController?Operation=View&subOperation=EnterId&QuoteId=<%=masterDOB.getQuoteId()%>";
	Options	= 'width=800,height=600,resizable=yes';
	Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
	Features=Bars+','+Options;
	Win=open(Url,'QuoteDetailedView',Features);
}

function setSelectedIndexValue(obj,breakcount)
	{
	var selectedlist="";
    selectedbreaks =document.forms[0].selectedIndexList.value;
	if(obj.checked){
	selectedbreaks = selectedbreaks+breakcount+',';
	document.forms[0].selectedIndexList.value = selectedbreaks;
	}else
	{
	   selectedlist = document.forms[0].selectedIndexList.value;
	   document.forms[0].selectedIndexList.value =selectedlist.replace(breakcount+",","");
	}
	
	}
	function setSelectedIndexValueOnLoad() 
		{
	document.forms[0].selectedIndexList.value ='';
	var totbreaks = document.getElementsByName("breakChk");
    var totbreakslength = '<%=totbreaks%>';
	for(var i=0;i<totbreakslength;i++)
			{
		     selectedbreaks =document.forms[0].selectedIndexList.value;
              if(document.getElementsByName("breakChk")[i].checked)
				{
                    selectedbreaks = selectedbreaks+i+',';
					document.forms[0].selectedIndexList.value = selectedbreaks;
				  }
			}
		
	//alert(document.forms[0].selectedIndexList.value)	
	<%if("checked".equals(request.getParameter("selectCarrier"))){%>
		document.forms[0].carrier.checked=true;
		document.forms[0].selectCarrier.value="checked";
	<%}if("checked".equals(request.getParameter("selectService"))){%>
		document.forms[0].serviceLevel.checked=true;
	    document.forms[0].selectService.value="checked";
	<%}if("checked".equals(request.getParameter("selectFrequency"))){%>
		document.forms[0].frequency.checked=true;
	    document.forms[0].selectFrequency.value="checked";
	<%}%>
}
//@@Added by Kameswari for the WPBN issue-146448

			//@@WPBN issue-146448-->
</script>
</HEAD>

<BODY onLoad="initialize();setAllValuesOnLoad();roundAll();setSelectedIndexValueOnLoad();">
<form method='post' action='QMSMultiQuoteController' onsubmit='disableUnChecked();return checkMarginTest();'>
<table width="100%" cellpadding="0" cellspacing="0">
 <tr>
	<td>
		<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
		  <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
		  <%if(!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith())){//Added by Anil.k
			  if("Modify".equalsIgnoreCase(operation)) {%>
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode!=null?shipmentMode:""%> FREIGHT MULTILANE/MUTLICARRIER QUOTE (<%=quoteName%>,<%=quoteStatus%>,<%=completeFlag%>)- <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td>
			<%chargeInfoList = ((MultiQuoteFreightLegSellRates)charges.get(0)).getFreightChargesList();
					chargeInfo	=	(MultiQuoteChargeInfo)chargeInfoList.get(0);
							for(String s : chargeInfo.getMultiBreakPoints()){%></td>
							<%}%>
			</tr></table></td>
			<%}else { %>
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> FREIGHT MULTILANE/MUTLICARRIER QUOTE - <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td>
			<%chargeInfoList = ((MultiQuoteFreightLegSellRates)charges.get(0)).getFreightChargesList();
					chargeInfo	=	(MultiQuoteChargeInfo)chargeInfoList.get(0);
							for(String s : chargeInfo.getMultiBreakPoints()){%></td>
							<%}%></tr></table></td>
             <!-- @@WPBN issue-167677 -->
			<%}%>
		  </tr></table>
<%
		//Logger.info(FILE_NAME,"errors"+request.getAttribute("errors"));
		if(request.getAttribute("errors")!=null && ((StringBuffer)request.getAttribute("errors")).length()>0)
		{
%>
				<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
							<tr color="#FFFFFF">
								<td><font face="Verdana" size="2" color='red'><b>This form has not been submitted because of the following error(s) : </b> <br><br>
									<%=((StringBuffer)request.getAttribute("errors")).toString()%></font></td>
							</tr>
				</table>

<%
		}
chargeInfoList = ((MultiQuoteFreightLegSellRates)charges.get(0)).getFreightChargesList();
					chargeInfo	=	(MultiQuoteChargeInfo)chargeInfoList.get(0);
%>
       <table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
              <td colspan="<%=colspan%>" align=center><%=headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService().toUpperCase():""%> </td>
               <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td></tr>
			<tr class="formdata">
              <td colspan="<%=colspan%>" align=center><%=headerDOB.getCustomerName()!=null?headerDOB.getCustomerName():""%></td>
			   <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td></tr>
			<tr class="formdata">
              <td colspan="<%=colspan%>" align=center>ATTENTION TO:
<%			StringBuffer   attentionTo            =   null;

			if(masterDOB.getCustContactNames()!=null)
			{
				attentionTo            =   new StringBuffer("");
				for(int i=0;i<masterDOB.getCustContactNames().length;i++)
				{
					attentionTo.append(masterDOB.getCustContactNames()[i]!=null?masterDOB.getCustContactNames()[i]:"");
					if(i!=(masterDOB.getCustContactNames().length-1))
						attentionTo.append(",");
				}
			}%>

				<%=attentionTo!=null?attentionTo.toString():""%>
			</td>
			<td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td></tr>
			<tr class="formdata">
              <td colspan="<%=colspan%>" align=center>DATE OF QUOTATION:<%=quoteDateStr!=null?quoteDateStr:""%> </td>
			   <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" align='right'>PREPARED BY:</td>
			  <td colspan="<%=infoColspan%>" align='left'><%=headerDOB.getPreparedBy()!=null?headerDOB.getPreparedBy():""%></td>
			   <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td>
            </tr>
			<tr class="formheader" cellpadding="4" >
              <td colspan="<%=colspan%>" align=center>SERVICE INFORMATION</td>
			  <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Agent: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getAgent()!=null?headerDOB.getAgent():""%></td>
			 <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td>
            </tr>
			<!-- <tr class="formdata">
             
            </tr> --><!-- Commented by Anil.k -->
	
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Commodity or Product: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getCommodity()!=null?headerDOB.getCommodity():""%></td>
			 <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td>
            </tr>
			<!-- <tr class="formdata">
            
            </tr> --><!-- Commented by Anil.k -->
			<!--@@Added by Kameswari for the WPBN issue-146448-->
			
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Notes: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getNotes()!=null?headerDOB.getNotes():""%></td>
			  <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Date Effective: </td>
			  <td colspan="<%=infoColspan%>" ><%=effDateStr!=null?effDateStr:""%></td>
			  <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Validity of Quote: </td>
			  <td colspan="<%=infoColspan%>" ><%=validUptoStr!=null?validUptoStr:"VALID TILL FURTHER NOTICE"%></td>
			  <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td>
            </tr>
			<!--@@WPBN issue-146448-->
<%
			if(headerDOB.getPaymentTerms()!=null && headerDOB.getPaymentTerms().trim().length()!=0)
			{
%>				<tr class="formdata">
				  <td colspan="<%=serviceColspan%>" >Payment Terms: </td>
				  <td colspan="<%=infoColspan%>" ><%=headerDOB.getPaymentTerms()%></td>
                   <td colspan ="<%=chargeInfo.getMultiBreakPoints().length%>"></td>
				</tr>
<%
			}
				int tempCount = chargeInfo.getMultiBreakPoints().length;
%>

             

<%              frtsize =finalDOB.getLegDetails().size();
			System.out.println("frtsize-------"+frtsize)	;
          for(int fr=0;fr<frtsize;fr++){%>
	  <%
                 
				 legRateDetails		=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
                 originCharges				  =  legRateDetails.getOriginChargesList();
				 originIndices				  =  legRateDetails.getSelectedOriginChargesListIndices();
                 OriginChargesSelectedIndices =  legRateDetails.getOriginChargesSelectedFlag();
				
                if(originCharges!=null)
					originChargesSize		= originCharges.size();
				else
					originChargesSize		= 0;
             	System.out.println("originCharges-------"+originCharges)	;  
				if(originChargesSize>0 )
				{ 
                     if(fr==0){
%>
					 <tr class="formheader" cellpadding="4" align='center'>
			  <td>Select<br>
			  <input type="checkbox" name='select<%=fr%>' onclick='selectAll(<%=fr%>)'></td>
			  <td colspan='4'>Charge Name</td>
			  <td>Defined By</td>
			  <td>Breakpoint</td>
			  <td>Currency</td>
<%			if("Y".equals(buyRatePermission))
			{
%>			  <td colspan='2'>Buy Rate</td>
			  <td>Margin Type</td>
			  <td>Margin</td>
<%			}
%>
			   <td>RSR</td>
			  <td>Discount Type</td>
			  <td>Discount</td>
			  <td>Sell Rate</td>
			  <td>Basis</td>
			  <td>Ratio</td>
               <td colspan ="<%=tempCount%>"></td>
            </tr>
					
<%}
				}

				for(int j=0;j<originChargesSize;j++)
				{ if(j==0 && originChargesSize > 0){%>
					<tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align='center'><%=legRateDetails.getOrigin()%>-ORIGIN CHARGES</td>
					   <td colspan ="<%=tempCount%>"></td>
					</tr>
					<%}checkedFlag				=	false;
					chargesDOB				= (MultiQuoteCharges)originCharges.get(j);
					originList = new ArrayList();
				    originList1 = new ArrayList();


					if("BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
					{
						originDiscDisabled	=	"disabled";
					    originMarginDisabled	=	"";

					}

		             else
					{
						originDiscDisabled	=	"";
						originMarginDisabled	=	"disabled";
					}
				  
					originChargeInfo		= chargesDOB.getChargeInfoList();
					originChargesInfoSize	= originChargeInfo.size();

					if(originIndices!=null )
					{
						for(int i=0;i<originIndices.length;i++)
						{
							
							checkedFlag				=	false;
                    	if(OriginChargesSelectedIndices!=null && OriginChargesSelectedIndices.length>0){
							if(originIndices[i]==j&&"Y".equalsIgnoreCase(OriginChargesSelectedIndices[i]))
							{
								checkedFlag = true;
								break;
							}
						}
						}
					}
				
					if(!checkedFlag && ("Y".equalsIgnoreCase(chargesDOB.getSelectedFlag()) || "T".equalsIgnoreCase(chargesDOB.getSelectedFlag())) && isMarginTestPerformed==null)
					{
						checkedFlag = true;

					}

					if(j==0 && originChargesInfoSize>0)
					{
%>
				<tr class="formdata" cellpadding="4" align='center'>
				  <td colspan="<%=colspan%>"><B><%//=legCharges.getOrigin()+"-"+legCharges.getDestination()%></B></td>
				   <td colspan ="<%=tempCount%>"></td>
				</tr>
<%
					}
%>

<%		            for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)originChargeInfo.get(k);

						originList.add(new Double(chargeInfo.getBuyRate()));
						originList1.add(new Double(chargeInfo.getRecOrConSellRrate()));
					}
	                for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)originChargeInfo.get(k);
                        color = (checkedFlag && chargeInfo.isMarginTestFailed())?"#FF0000":"#000000";
						if(checkedFlag)
							originCheckedFlag = "checked";
						else originCheckedFlag = "";




%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%if(k==0){%><input type='checkbox' name='originChkBox<%=fr%>' onclick="setValue(<%=j%>,'origin',<%=fr%>)" <%=originCheckedFlag%>>
						<input type='hidden' name='originChargeIndices<%=fr%>'/>
						<input type='hidden' name='originChargeSelectedFlag<%=fr%>'/><%}else{}%><!-- added by subrahmanyam for 154381 -->
					  </td>
					  <td colspan ='4'><font color="<%=color%>"><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getInternalName():chargesDOB.getChargeDescriptionId()):""%></font></td>
					  <td><font color="<%=color%>"><%=k==0?chargesDOB.getTerminalId():""%></font></td>
					  <td><font color="<%=color%>"><%=toTitleCase(chargeInfo.getBreakPoint())%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
<%					if("Y".equals(buyRatePermission))
					{
%>	<!-- @@ Commented by subrahmanyam for 180161 -->
				<!--  <td><font color="<%=color%>"><%=df1.format(new Double(chargeInfo.getBuyRate()))%></font></td> -->
				<!-- @@ Added by subrahmanyam for 180161 -->
						<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
						<td colspan='2'><font color="<%=color%>"><%=round(chargeInfo.getBuyRate())%></font></td>
						<%}else{%>
						<td colspan='2'><font color="<%=color%>"><%=df1.format(new Double(chargeInfo.getBuyRate()))%></font></td>
						<%}%>
						<input type='hidden' id ='BSFLAG' name='AbsPersRate' value='<%=chargesDOB.getSellBuyFlag()%>'/>
					  <td>
					  <select class="select"  name="originMarginType<%=fr%><%=j%>" size="1" onChange="enableDisableMarginType(this,'originDiscount',<%=j%>,'Discount','originMargin',<%=originList%>,'Margin','origin',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>',<%=fr%>);return calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','origin',<%=k%>,0,<%=fr%>)" >
					   <option value="A" <%="A".equals(chargeInfo.getMarginType())?"selected":""%>>Absolute</option>
					  <option value="P" <%="P".equals(chargeInfo.getMarginType())?"selected":""%>>Percent</option>
					  </select></td>

	
				<%if("Yes".equalsIgnoreCase(MarginTest) || "<<Back".equalsIgnoreCase(submit)||"Y".equalsIgnoreCase(chargesDOB.getSelectedFlag())){
					%>
						<td align='center'><input class="text" type="text" value="<%=chargeInfo.getMargin()%>" size=5 name='originMargin<%=fr%><%=j%>'  <%=originMarginDisabled%> onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","origin",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",<%=fr%>)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','originDiscount<%=fr%>',<%=j%>,'Discount')" maxlength='10'>

				<%}else{
					session.setAttribute("MarginTestCount",MarginTestCount);%>
					<td align='center'><input class="text" type="text" value="<%=chargeInfo.getMargin()<0?0:chargeInfo.getMargin()%>" size=5 name='originMargin<%=fr%><%=j%>'  <%=originMarginDisabled%> onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","origin",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",<%=fr%>)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','originDiscount<%=fr%>',<%=j%>,'Discount')" maxlength='10'>
					  </td>
				<%}%>
					  <%}
%>

					<td><font color="<%=color%>"><%=new Double(chargeInfo.getRecOrConSellRrate())%></font></td><!-- @@ added for 180164 -->
					<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
						{%>
					 <td>
					  <select class="select"  name="originDiscountType<%=fr%><%=j%>"  size="1" disabled>
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
					  <%}
						else
						{%>
						<td>
					  <select class="select"  name="originDiscountType<%=fr%><%=j%>"  size="1" id='noRound' onChange="enableDisableMarginType(this,'originMargin',<%=j%>,'Margin','originDiscount',<%=originList1%>,'Discount','origin',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>',<%=fr%>);calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','origin',<%=k%>,0,<%=fr%>)">
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
					  <%}%>
					  <td><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size=5 name='originDiscount<%=fr%><%=j%>' onblur="calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','origin',<%=k%>,0,<%=fr%>)" <%=originDiscDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getDiscount()%>','originMargin<%=fr%>',<%=j%>,'Margin')" maxlength='10'></td>
					  
					  <%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					  <td><input class="text" type="text" id='chargeSellrate' value="<%=round(chargeInfo.getSellRate())%>" size=5 name='originSellRate<%=fr%><%=j%>' onBlur = "riverseCalOfCharges(this,'originDiscount<%=fr%><%=j%>','originMargin<%=fr%><%=j%>','originDiscountType<%=fr%><%=j%>','originMarginType<%=fr%><%=j%>','<%=chargeInfo.getBuyRate()%>','<%=chargeInfo.getRecOrConSellRrate()%>','<%=k%>')"></td>
					  <%}else{%>
					  <td><input class="text" type="text" id='chargeSellrate' value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5  name='originSellRate<%=fr%><%=j%>' onBlur = "riverseCalOfCharges(this,'originMargin<%=fr%><%=j%>','originMargin<%=fr%><%=j%>','originDiscountType<%=fr%><%=j%>','originMarginType<%=fr%><%=j%>','<%=chargeInfo.getBuyRate()%>','<%=chargeInfo.getRecOrConSellRrate()%>','<%=k%>')"></td>
					  <%}%>
					  <td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>

					  <input type='hidden' name='marginTestFailed' value='<%=(checkedFlag && chargeInfo.isMarginTestFailed())?"Y":"N"%>'/>
                      <td colspan ="<%=tempCount%>"></td>
					</tr>
<%
					}
				}
	}//new
			%>
			<tr class="formheader" cellpadding="4" align='center'>
			  <td>Origin Port</td>
			  <td>DestPort</td>
			  <td>Incoterms</td>
			  <td>Carrier<br><input type="checkbox" name="carrier" onClick="selectCSF(this)" value="C"></td><!-- Added by Anil.k to display in 4th screen or not --><input type="hidden" name="selectCarrier" value="unchecked">
			  <td>Service Level<input type="checkbox" name="serviceLevel" onClick="selectCSF(this)" value="S"></td><!-- Added by Anil.k to display in 4th screen or not --><input type="hidden" name="selectService" value="unchecked">
			  <td>Frequency<br><input type="checkbox" name="frequency" onClick="selectCSF(this)" value="F"></td><!-- Added by Anil.k to display in 4th screen or not --><input type="hidden" name="selectFrequency" value="unchecked">
			  <td>Approx Transit Time</td>
			  <td>Freight Validity</td>
			  <!-- <td>Density Ratio </td> -->
			  <td>Currency</td>
			  <td>BR/RSR</td>
<%chargeInfoList = ((MultiQuoteFreightLegSellRates)charges.get(0)).getFreightChargesList();
					int infoSize	=	chargeInfoList.size();
						int brkCount	=	0;
							chargeInfo	=	(MultiQuoteChargeInfo)chargeInfoList.get(0);
							String checked_Flag = "";
							
							//for(String a:chargeInfo.getSelectedFlag().split(","))
							//	System.out.println("checked_flag--------"+chargeInfo.getChecked_Flag());
							for(String s : chargeInfo.getMultiBreakPoints()){
							if(chargeInfo.getChecked_Flag()!= null && !"-".equals(chargeInfo.getChecked_Flag().split(",")[brkCount]) && !"".equals(chargeInfo.getChecked_Flag().split(",")[brkCount]) && selectedIndexList== null)
								checked_Flag = "checked";	
					//	System.out.println("777777777777"+chargeInfo.getMultiRateDescriptions()[brkCount]+"len"+s.length());
						//System.out.println(masterDOB.getMultiquoteweightBrake());%>
						
		<td nowrap>
		
		<%if(!"List".equalsIgnoreCase(masterDOB.getMultiquoteweightBrake())){%>
		<%=	(!"A FREIGHT RATE".equalsIgnoreCase(chargeInfo.getMultiRateDescriptions()[brkCount]) && s.length()>5)?s.substring(0,3)+s.substring(5):s%>
		<%}else{%>
        <%=	(!"A FREIGHT RATE".equalsIgnoreCase(chargeInfo.getMultiRateDescriptions()[brkCount])) ?s.substring(0,s.length()-2):s%>
         <%}%>
			<br>
						
                              <%=chargeInfo.getBasis().split(",")[brkCount]%><br>
	                         <%count++;%>
							<input type='checkbox'  name='breakChk' onclick='setSelectedIndexValue(this,<%=brkCount%>)'
							<%=selectedIndexList!= null?(selectedIndexList.contains(Integer.toString(brkCount))?"checked":""):checked_Flag%> >

							</td>
					 <%brkCount++;
							checked_Flag = "";}
                 
							//System.out.println(brkCount);%>
                   
           <td>Density Ratio </td>
            </tr>
<%	chargeInfoList = ((MultiQuoteFreightLegSellRates)charges.get(0)).getFreightChargesList();
					 infoSize	=	chargeInfoList.size();
					for(int info=0;info<infoSize;info++)
					{
							chargeInfo	=	(MultiQuoteChargeInfo)chargeInfoList.get(info);
							//System.out.println("sellbuyflag.."+chargeInfo.getSellBuyFlag());
							//System.out.println("SelectedFlag--------"+chargeInfo.getSelectedFlag());
							//System.out.println("checked_flag--------"+chargeInfo.getChecked_Flag());
							String[]	tempData = new String[chargeInfo.getMultiBreakPoints().length];
							String readOnly="readOnly";
							//	String  disabled = "";
							String freightcolor ="";
							String rsrBrcolor ="";
							String marginDiscountdisabled	="";
							String  buyRate		=	"";
							String  sellRate		=	"";
							int breaksLen	= chargeInfo.getMultiBreakPoints().length;
							if("Y".equalsIgnoreCase(isMarginTestPerformed) && isMarginTestPerformed!= null )
							 freightcolor = (chargeInfo.isMarginTestFailed())?"red":"black";
							else
							freightcolor = "black";
                            System.out.println("isMarginTestFailed()----"+chargeInfo.isMarginTestFailed());
						
                           
%>						   
	    <input type='hidden' name='marginTestFailed' value='<%=( chargeInfo.isMarginTestFailed())?"Y":"N"%>'/>
						   <input type = 'hidden'   name='multiSelectedFlag<%=info%>' id ='multiSelectedFlag<%=info%>'
							value = '<%=chargeInfo.getSellBuyFlag()%>' />
							<% int rowSpan = "BR".equals(chargeInfo.getSellBuyFlag())?8:8;
							System.out.println(chargeInfo.getSellBuyFlag()+"%%%%%%%%");%>
						   <tr  class="formdata" cellpadding="4" valign='center' rowspan=8>
                           <td rowspan="<%=rowSpan%>" align='center'>
						   <font color='<%=freightcolor%>'><%=chargeInfo.getOriginPort()!=null?chargeInfo.getOriginPort():""%></font></td>
                           <td rowspan="<%=rowSpan%>"><font color='<%=freightcolor%>'><%=chargeInfo.getDestPort()!=null?chargeInfo.getDestPort():""%></font></td>
						   <td rowspan="<%=rowSpan%>"><font color='<%=freightcolor%>'><%=chargeInfo.getIncoTerms()!=null?chargeInfo.getIncoTerms():""%></font></td>
						   <td rowspan="<%=rowSpan%>"><font color='<%=freightcolor%>'><%=chargeInfo.getCarrier()!=null?chargeInfo.getCarrier():""%></font></td>
						   <td rowspan="<%=rowSpan%>"><font color='<%=freightcolor%>'><%=chargeInfo.getServiceLevel()!=null?chargeInfo.getServiceLevel():""%></font></td>
						   <td rowspan="<%=rowSpan%>"><font color='<%=freightcolor%>'><%=chargeInfo.getFrequency()!=null?chargeInfo.getFrequency():""%></font></td>
						   <td rowspan="<%=rowSpan%>"><font color='<%=freightcolor%>'><%=chargeInfo.getTransitTime()!=null?chargeInfo.getTransitTime():""%></font></td>
						   <td nowrap rowspan="<%=rowSpan%>"><font color='<%=freightcolor%>'><%=chargeInfo.getValidUpto()!=null?eSupplyDateUtility.getDisplayStringArray(chargeInfo.getValidUpto())[0]:""%></font></td>
						   <!-- <td rowspan="8"><font color='<%=freightcolor%>'><%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%></font></td> -->
						   <td rowspan="<%=rowSpan%>"><font color='<%=freightcolor%>'><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td> 
						   <%for (int bre=0;bre<7;bre++){
	                        %><tr  class="formdata" cellpadding="4" align='center'>
							<%if(bre==0){
								tempData = chargeInfo.getMultiBuyRates();
								%>
							<td><b><font size='0.50' color='<%=freightcolor%>'>BR</font></b></td>
							<%}else if(bre==1){
								tempData = chargeInfo.getMultiMarginTypes();
									%>
							<td><b><font size='0.50'>Margin Types</font></b></td>
							<%}else if(bre==2){
							  tempData	=	chargeInfo.getMultiMargins();
										%>
								<td><b><font size='0.50' >Margins</font></b></td>
							<%}else if(bre==3 &&  !"BR".equals(chargeInfo.getSellBuyFlag())){
							  tempData	=	chargeInfo.getMultiSellRates();
										%>
								<td><b><font size='0.50' color='<%=freightcolor%>'>RSR</font></b></td>
										<%}else if(bre==4 &&  !"BR".equals(chargeInfo.getSellBuyFlag())){
							  tempData	=	chargeInfo.getMultiMarginTypes();
										%>
								<td><b><font size='0.50'>DiscountTypes</font></b></td>
										<%}else if(bre==5 &&  !"BR".equals(chargeInfo.getSellBuyFlag())){
							  tempData	=	chargeInfo.getMultiDiscountMargin();
										%>
								<td><b><font size='0.50'>Discount</font></b></td>
										<%}else if(bre==6){
							  tempData	=	chargeInfo.getMultiCalSellRates();
										%>
								<td><b><font size='0.50'>Sell Rate</font></b></td>
										<%}else{%><td></td><%}%>
							 
                            <%
								for (int breaks=0;breaks<breaksLen;breaks++){
								String  disabled="";
								if(bre==1 || (bre==4 &&  !"BR".equals(chargeInfo.getSellBuyFlag()))){
									buyRate	=	chargeInfo.getMultiBuyRates()[breaks];
									sellRate	=	chargeInfo.getMultiSellRates()[breaks];

									if((bre==4 &&"BR".equalsIgnoreCase(chargeInfo.getSellBuyFlag())))
										disabled = "disabled";
									else
                                        disabled = "";
							%>
							<input type='hidden' name ='selectedIndexVlaue' id ='selectedIndexVlaue<%=breaks%>' value ='-1'>
								<td>
						<select size='1' class='select' name='<%=bre==1?"marginTypes":"discountTypes"%><%=info%>' 
						id = '<%=bre==1?"marginTypes":"discountTypes"%><%=info%><%=bre%><%=breaks%>'  
						onChange='setEnableDisableAbsPers(<%=info%>,<%=bre%>,<%=breaksLen%>,"<%=chargeInfo.getSellBuyFlag()%>");calSellRateOnAbsPerChange(this,<%=bre%>,<%=info%>,<%=breaks%>,<%=buyRate.equals("-")?0:buyRate%>,<%=sellRate.equals("-")?0:sellRate%>)'  <%=disabled%> >
						  <option value="A" <%=tempData!=null?( "A".equals(tempData[breaks]) || "-".equals(tempData[breaks]) )?"selected":"":"selected"%>>Absolute</option>
						  <option value="P" <%=tempData!=null?"P".equals(tempData[breaks])?"selected":"":"selected"%>>Percent</option>
							</select></td>

									<%}else if(bre==2 || (bre==5 &&  !"BR".equals(chargeInfo.getSellBuyFlag()))){
										if("RSR".equalsIgnoreCase(chargeInfo.getSellBuyFlag()) && bre==2)
												marginDiscountdisabled = "disabled";
										else if("BR".equalsIgnoreCase(chargeInfo.getSellBuyFlag()) && bre==5)
												marginDiscountdisabled = "disabled";

										if(tempData!=null && "-".equals(tempData[breaks]) ){%>
									<td><input class='text' size='2' type='text' name='<%=bre==2?"MarginVal":"DiscountVal"%><%=info%>'
									id='<%=bre==2?"MarginVal":"DiscountVal"%><%=info%><%=bre%><%=breaks%>'
										value='-' readOnly />  </td>
										<%}else{
									buyRate	=	chargeInfo.getMultiBuyRates()[breaks];
									sellRate	=	chargeInfo.getMultiSellRates()[breaks];

											%>
									<td><input class='text' size='5' type='text' name='<%=bre==2?"MarginVal":"DiscountVal"%><%=info%>'
									id='<%=bre==2?"MarginVal":"DiscountVal"%><%=info%><%=bre%><%=breaks%>'
							value='<%=tempData!=null?tempData[breaks]:"0.0"%>' <%=tempData!=null&&"-".equals(tempData[breaks])?readOnly:""%> onKeyPress  ='return getDotNumberCode(this);'onBlur='calaculateSellRate(this,<%=info%>,<%=bre%>,<%=breaks%>,<%=buyRate.equals("-")?0:buyRate%>,<%=sellRate.equals("-")?0:sellRate%>)' <%=marginDiscountdisabled%> /></td>

										<%}}else if(bre==6){
											buyRate	=	chargeInfo.getMultiBuyRates()[breaks];
									        sellRate	=	chargeInfo.getMultiSellRates()[breaks];
											if(tempData!=null && "-".equals(tempData[breaks]) ){%>
									<td><input class='text' size='2' type='text' name='calsellrate<%=info%>'  value='-' readonly ></td>
											<%}else{%>
									<td><input class='text' size='5' type='text' name='calsellrate<%=info%>' id='calsellrate<%=info%><%=bre%><%=breaks%>' onKeyPress  ='return getDotNumberCode(this);' value='<%=tempData!=null && tempData[breaks] != null?tempData[breaks]:"0.0"%>' onBlur ='reverseCallSellRate(this,<%=info%>,<%=breaks%>,<%=buyRate.equals("-")?0:buyRate%>,<%=sellRate.equals("-")?0:sellRate%>)' ></td>
										<%}}else{
											   if(bre== 0){%>
											<td><font color='<%=selectedIndexList!= null?(selectedIndexList.contains(Integer.toString(breaks))?(chargeInfo.getFreightBrekMarginTest()[breaks]?"#FF0000":""):""):""%> '><%=tempData[breaks]%></font></td>
											<%}else if(!"BR".equals(chargeInfo.getSellBuyFlag())){
													   System.out.println(chargeInfo.isMultiDiscountTestFailed());%>
                                             <td><font color='<%=selectedIndexList!= null?(selectedIndexList.contains(Integer.toString(breaks))?(chargeInfo.getFreightBrekMarginTest()[breaks]?"#FF0000":""):""):""%> '><%=tempData[breaks]%></font></td>

									<%} else {%><td></td><%}}%>
								
							<%
								marginDiscountdisabled="";
							
							buyRate	=	"";
							sellRate	= "";
								}if(bre==0){%>

					<td rowspan="<%=rowSpan%>"><font color='<%=freightcolor%>'><%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%></font></td>
							<%}}%>
	                  
                     <!--  <td colspan ="<%=tempCount%>">			 -->		   						  
						  
						   
</tr><%

					}

%>

<%

				for(int fr=0;fr<frtsize;fr++){%>
                <tr class="formheader" cellpadding="4" align='center'>
			 


                
              <% legRateDetails		=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
                 destCharges				  =  legRateDetails.getDestChargesList();
				 destIndices				  =  legRateDetails.getSelctedDestChargesListIndices();
                 destChargesSelectedIndices =  legRateDetails.getDestChargesSelectedFlag();
		//		destCharges		= finalDOB.getDestChargesList();
			//	destIndices		= finalDOB.getSelctedDestChargesListIndices();
		//		destChargesSelectedIndices=finalDOB.getDestChargesSelectedFlag();//@@ Added by subrahmanyam for 154381 on 14/02/09
				if(destCharges!=null)
					destChargesSize	= destCharges.size();
				else
					destChargesSize	= 0;

				if(destChargesSize>0)
				{
					  if(fr==0){
%>
                   <td>Select</td>
			  <td colspan = '4'>Charge Name</td>
			  <td>Defined By</td>
			  <td>Breakpoint</td>
			  <td>Currency</td>
<%			if("Y".equals(buyRatePermission))
			{
%>			  <td colspan='2'>Buy Rate</td>
			  <td>Margin Type</td>
			  <td>Margin</td>
<%			}
%>
			   <td>RSR</td>
			  <td>Discount Type</td>
			  <td>Discount</td>
			  <td>Sell Rate</td>
			  <td>Basis</td>
			  <td>Ratio</td>
			  <td colspan ="<%=tempCount%>"></td>
            </tr>
                  

<%}%>




					
<%
				}
				for(int j=0;j<destChargesSize;j++)
				{
					 if(j==0 && destChargesSize >0 ){%>
					<tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align='center'><%=legRateDetails.getDestination()%>-DESTINATION CHARGES</td>
					<td colspan ="<%=tempCount%>"></td>
					</tr>
					
				<%}	destList = new ArrayList();
					destList1 = new ArrayList();
					checkedFlag				=	false;
					chargesDOB				= (MultiQuoteCharges)destCharges.get(j);
					

                  if("BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
					{
						destDiscDisabled	=	"disabled";
					    destMarginDisabled	=	"";

					}

		             else
					{
						destDiscDisabled	=	"";
						destMarginDisabled	=	"disabled";
					}
                     //@@Enhancement
					destChargeInfo			= chargesDOB.getChargeInfoList();
					destChargesInfoSize		= destChargeInfo.size();

					if(destIndices!=null)
					{
						for(int i=0;i<destIndices.length;i++)
						{
							checkedFlag				=	false;

							if(destIndices[i]==j&&destChargesSelectedIndices!=null&&"Y".equalsIgnoreCase(destChargesSelectedIndices[i]))

							{
								checkedFlag = true;
								break;
							}
						}
					}
						if(!checkedFlag && ("Y".equalsIgnoreCase(chargesDOB.getSelectedFlag()) || "T".equalsIgnoreCase(chargesDOB.getSelectedFlag())) && isMarginTestPerformed==null)
					{
						checkedFlag = true;
					}

					
						


					 for(int k=0;k<destChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)destChargeInfo.get(k);

							  destList.add(new Double(chargeInfo.getBuyRate()));
							  destList1.add(new Double(chargeInfo.getRecOrConSellRrate()));
					}
					for(int k=0;k<destChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)destChargeInfo.get(k);
						System.out.println("checkedFlag"+checkedFlag); // Added by Gowtham
						color	   =  (checkedFlag && chargeInfo.isMarginTestFailed())?"#FF0000":"#000000";
						if(checkedFlag)
							destCheckedFlag = "checked";
						else destCheckedFlag = "";


%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%if(k==0){%><input type='checkbox' name='destChkBox<%=fr%>' onclick="setValue(<%=j%>,'dest',<%=fr%>)" <%=destCheckedFlag%>>
						<input type='hidden' name='destChargeIndices<%=fr%>'/>
						<input type='hidden' name='destChargeSelectedFlag<%=fr%>'/><%}else{}%>
					  </td>
					  <td colspan = '4'><font color="<%=color%>"><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getInternalName():chargesDOB.getChargeDescriptionId()):""%></font></td>
					  <td><font color="<%=color%>"><%=k==0?chargesDOB.getTerminalId():""%></font></td>
					  <td><font color="<%=color%>"><%=toTitleCase(chargeInfo.getBreakPoint())%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
<%					if("Y".equals(buyRatePermission))
					{
%>			  		<!-- @@ Commented by subrahmanyam for 180161 -->
				<!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td> -->
				<!-- @@Added by subrahmanyam for 180161 -->
						<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) ||"S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
						<td colspan='2'><font color="<%=color%>"><%=round(chargeInfo.getBuyRate())%></font></td>
						<%}else{%>
						<td colspan='2'><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td>
						<%}%>
						<input type='hidden' name='AbsPersRateDest' value='<%=chargesDOB.getSellBuyFlag()%>'/>
				<!-- @@Ended by subrahmanyam for 180161 -->
					<td>
					  <select class="select"  name="destMarginType<%=fr%><%=j%>" size="1"  onChange="enableDisableMarginType(this,'destDiscount',<%=j%>,'Discount','destMargin',<%=destList%>,'Margin','dest',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>',<%=fr%>);calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','dest',<%=k%>,0,<%=fr%>)" >
						  <option value="A" <%="A".equals(chargeInfo.getMarginType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getMarginType())?"selected":""%>>Percent</option>
					  </select>
					  </td>
					
						<%if("Yes".equalsIgnoreCase(MarginTest) || "<<Back".equalsIgnoreCase(submit)||"Y".equalsIgnoreCase(chargesDOB.getSelectedFlag()))
						{
						MarginTestCount1=(Integer)session.getAttribute("MarginTestCount");
					//	int mTCount4=MarginTestCount1.intValue();

								%>

								<td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getMargin()%>" size=5 name='destMargin<%=fr%><%=j%>' onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","dest",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",<%=fr%>)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','destDiscount<%=fr%>',<%=j%>,'Discount')" <%=destMarginDisabled%> maxlength='10'></font></td>

					  <%}else{
								session.setAttribute("MarginTestCount",MarginTestCount);%>
								<td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getMargin()<0?0:chargeInfo.getMargin()%>" size=5 name='destMargin<%=fr%><%=j%>' onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","dest",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",<%=fr%>)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','destDiscount<%=fr%>',<%=j%>,'Discount')" <%=destMarginDisabled%> maxlength='10'></font></td>
					  <%}%>
<!-- @@ Ended by subrahmanyam for the Enhancement 154381 on 03/02/09 -->
<%					}
%>						<td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td>
						<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						{%>
						<td>
						<select class="select"  name="destDiscountType<%=fr%><%=j%>" size="1" disabled >
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
					  <%}
						else
						{	%>
                          <td>
						<select class="select"  name="destDiscountType<%=fr%><%=j%>" size="1" onChange="enableDisableMarginType(this,'destMargin',<%=j%>,'Margin','destDiscount',<%=destList1%>,'Discount','dest',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>',<%=fr%>);calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','dest',<%=k%>,0,<%=fr%>)" >
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
						<%}%>
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size=5 name='destDiscount<%=fr%><%=j%>'
					  onblur="calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','dest',<%=k%>,0,<%=fr%>)" <%=destDiscDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getDiscount()%>','destMargin<%=fr%>',<%=j%>,'Margin')" maxlength='10'></font></td>
					  <!-- <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5 readOnly name='destSellRate<%=j%>'></font></td> -->
					  <!-- Added for 180161 -->
					  <%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					  <td><font color="<%=color%>"><input class="text" type="text" id='Destrates' value="<%=round(chargeInfo.getSellRate())%>" size=5  name='destSellRate<%=fr%><%=j%>'onBlur = "riverseCalOfCharges(this,'destMargin<%=fr%><%=j%>','destMargin<%=fr%><%=j%>','destDiscountType<%=fr%><%=j%>','destMarginType<%=fr%><%=j%>','<%=chargeInfo.getBuyRate()%>','<%=chargeInfo.getRecOrConSellRrate()%>','<%=k%>')"></font></td>
					  <%}else{%>
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5  name='destSellRate<%=fr%><%=j%>'
					  onBlur = "riverseCalOfCharges(this,'destMargin<%=fr%><%=j%>','destMargin<%=fr%><%=j%>','destDiscountType<%=fr%><%=j%>','destMarginType<%=fr%><%=j%>','<%=chargeInfo.getBuyRate()%>','<%=chargeInfo.getRecOrConSellRrate()%>',<%=k%>)"></font></td>
					  <%}%>
					  <!-- Ended for 180161 -->
					  <td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly></font></td>
					  <input type='hidden' name='marginTestFailed' value='<%=(checkedFlag && chargeInfo.isMarginTestFailed())?"Y":"N"%>'/>
					  <td colspan ="<%=tempCount%>"></td>
					</tr>
<%
					}
				}

	}//new for end
	}	else{ //Added by Anil.k
	 if("Modify".equalsIgnoreCase(operation)) {%>
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> FREIGHT MULTILANE/MULTICARRIER QUOTE (<%=quoteName%>,<%=quoteStatus%>,<%=completeFlag%>)- <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td>			
			</tr></table></td>
			<%}else { %>
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> FREIGHT MULTILANE/MULTICARRIER QUOTE - <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td>
			</tr></table></td>
             <!-- @@WPBN issue-167677 -->
			<%}%>
		  </tr></table>
<%
		//Logger.info(FILE_NAME,"errors"+request.getAttribute("errors"));
		if(request.getAttribute("errors")!=null && ((StringBuffer)request.getAttribute("errors")).length()>0)
		{
%>
				<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
							<tr color="#FFFFFF">
								<td><font face="Verdana" size="2" color='red'><b>This form has not been submitted because of the following error(s) : </b> <br><br>
									<%=((StringBuffer)request.getAttribute("errors")).toString()%></font></td>
							</tr>
				</table>

<%
		}
%>
       <table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
              <td colspan="<%=colspan%>" align=center><%=headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService().toUpperCase():""%> </td>
              </tr>
			<tr class="formdata">
              <td colspan="<%=colspan%>" align=center><%=headerDOB.getCustomerName()!=null?headerDOB.getCustomerName():""%></td>
			  </tr>
			<tr class="formdata">
              <td colspan="<%=colspan%>" align=center>ATTENTION TO:
<%			StringBuffer   attentionTo            =   null;

			if(masterDOB.getCustContactNames()!=null)
			{
				attentionTo            =   new StringBuffer("");
				for(int i=0;i<masterDOB.getCustContactNames().length;i++)
				{
					attentionTo.append(masterDOB.getCustContactNames()[i]!=null?masterDOB.getCustContactNames()[i]:"");
					if(i!=(masterDOB.getCustContactNames().length-1))
						attentionTo.append(",");
				}
			}%>

				<%=attentionTo!=null?attentionTo.toString():""%>
			</td>
			</tr>
			<tr class="formdata">
              <td colspan="<%=colspan%>" align=center>DATE OF QUOTATION:<%=quoteDateStr!=null?quoteDateStr:""%> </td>			   
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" align='right'>PREPARED BY:</td>
			  <td colspan="<%=infoColspan%>" align='left'><%=headerDOB.getPreparedBy()!=null?headerDOB.getPreparedBy():""%></td>
            </tr>
			<tr class="formheader" cellpadding="4" >
              <td  colspan="<%=colspan%>" align=center>SERVICE INFORMATION</td>
			  
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Agent: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getAgent()!=null?headerDOB.getAgent():""%></td>
			
            </tr>
			<!-- <tr class="formdata">
             
            </tr> -->
	
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Commodity or Product: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getCommodity()!=null?headerDOB.getCommodity():""%></td>
			 
            </tr>
			<tr class="formdata">
            
            </tr>
			<!--@@Added by Kameswari for the WPBN issue-146448-->
			
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Notes: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getNotes()!=null?headerDOB.getNotes():""%></td>
			  
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Date Effective: </td>
			  <td colspan="<%=infoColspan%>" ><%=effDateStr!=null?effDateStr:""%></td>
			 
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Validity of Quote: </td>
			  <td colspan="<%=infoColspan%>" ><%=validUptoStr!=null?validUptoStr:"VALID TILL FURTHER NOTICE"%></td>
			  
            </tr>
			<!--@@WPBN issue-146448-->
<%
			if(headerDOB.getPaymentTerms()!=null && headerDOB.getPaymentTerms().trim().length()!=0)
			{
%>				<tr class="formdata">
				  <td colspan="<%=serviceColspan%>" >Payment Terms: </td>
				  <td colspan="<%=infoColspan%>" ><%=headerDOB.getPaymentTerms()%></td>
                  
				</tr>
<%
			}
				
%>

             

<%              frtsize =finalDOB.getLegDetails().size();
			System.out.println("frtsize-------"+frtsize)	;
          for(int fr=0;fr<frtsize;fr++){%>
	  <%
                 
				 legRateDetails		=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
                 originCharges				  =  legRateDetails.getOriginChargesList();
				 originIndices				  =  legRateDetails.getSelectedOriginChargesListIndices();
                 OriginChargesSelectedIndices =  legRateDetails.getOriginChargesSelectedFlag();
				
                if(originCharges!=null)
					originChargesSize		= originCharges.size();
				else
					originChargesSize		= 0;
             	System.out.println("originCharges-------"+originCharges)	;  
				if(originChargesSize>0)
				{
%>
					 <tr class="formheader" cellpadding="4" align='center'>
			  <td>Select<br>
			  <input type="checkbox" name='select<%=fr%>' onclick='selectAll(<%=fr%>)'></td>
			  <td>Charge Name</td>
			  <td>Defined By</td>
			  <td>Breakpoint</td>
			  <td>Currency</td>
<%			if("Y".equals(buyRatePermission))
			{
%>			  <td>Buy Rate</td>
			  <td>Margin Type</td>
			  <td>Margin</td>
<%			}
%>
			   <td>RSR</td>
			  <td>Discount Type</td>
			  <td>Discount</td>
			  <td>Sell Rate</td>
			  <td>Basis</td>
			  <td>Ratio</td>              
            </tr>
					
					
					
					
					
					 
<%
				}

				for(int j=0;j<originChargesSize;j++)
				{
					if( j==0 && originChargesSize>0){%>
					<tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align='center'><%=legRateDetails.getOrigin()%>-ORIGIN CHARGES</td>
					 
					</tr>
					
				<%}	checkedFlag				=	false;
					chargesDOB				= (MultiQuoteCharges)originCharges.get(j);
					originList = new ArrayList();
				    originList1 = new ArrayList();


					if("BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
					{
						originDiscDisabled	=	"disabled";
					    originMarginDisabled	=	"";

					}

		             else
					{
						originDiscDisabled	=	"";
						originMarginDisabled	=	"disabled";
					}
				  
					originChargeInfo		= chargesDOB.getChargeInfoList();
					originChargesInfoSize	= originChargeInfo.size();

					if(originIndices!=null )
					{
						for(int i=0;i<originIndices.length;i++)
						{
							
							checkedFlag				=	false;
                    	if(OriginChargesSelectedIndices!=null && OriginChargesSelectedIndices.length>0){
							if(originIndices[i]==j&&"Y".equalsIgnoreCase(OriginChargesSelectedIndices[i]))
							{
								checkedFlag = true;
								break;
							}
						}
						}
					}
				
					if(!checkedFlag && ("Y".equalsIgnoreCase(chargesDOB.getSelectedFlag()) || "T".equalsIgnoreCase(chargesDOB.getSelectedFlag())) && isMarginTestPerformed==null)
					{
						checkedFlag = true;

					}

					if(j==0 && originChargesInfoSize>0)
					{
%>
				<tr class="formdata" cellpadding="4" align='center'>
				  <td ><B><%//=legCharges.getOrigin()+"-"+legCharges.getDestination()%></B></td>
				  
				</tr>
<%
					}
%>

<%		            for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)originChargeInfo.get(k);

						originList.add(new Double(chargeInfo.getBuyRate()));
						originList1.add(new Double(chargeInfo.getRecOrConSellRrate()));
					}
	                for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)originChargeInfo.get(k);
                        color = (checkedFlag && chargeInfo.isMarginTestFailed())?"#FF0000":"#000000";
						if(checkedFlag)
							originCheckedFlag = "checked";
						else originCheckedFlag = "";




%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%if(k==0){%><input type='checkbox' name='originChkBox<%=fr%>' onclick="setValue(<%=j%>,'origin',<%=fr%>)" <%=originCheckedFlag%>>
						<input type='hidden' name='originChargeIndices<%=fr%>'/>
						<input type='hidden' name='originChargeSelectedFlag<%=fr%>'/><%}else{}%><!-- added by subrahmanyam for 154381 -->
					  </td>
					  <td><font color="<%=color%>"><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getInternalName():chargesDOB.getChargeDescriptionId()):""%></font></td>
					  <td><font color="<%=color%>"><%=k==0?chargesDOB.getTerminalId():""%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getBreakPoint()%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
<%					if("Y".equals(buyRatePermission))
					{
%>	<!-- @@ Commented by subrahmanyam for 180161 -->
				<!--  <td><font color="<%=color%>"><%=df1.format(new Double(chargeInfo.getBuyRate()))%></font></td> -->
				<!-- @@ Added by subrahmanyam for 180161 -->
						<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
						<td><font color="<%=color%>"><%=round(chargeInfo.getBuyRate())%></font></td>
						<%}else{%>
						<td><font color="<%=color%>"><%=df1.format(new Double(chargeInfo.getBuyRate()))%></font></td>
						<%}%>
						<input type='hidden' id ='BSFLAG' name='AbsPersRate' value='<%=chargesDOB.getSellBuyFlag()%>'/>
					  <td>
					  <select class="select"  name="originMarginType<%=fr%><%=j%>" size="1" onChange="enableDisableMarginType(this,'originDiscount',<%=j%>,'Discount','originMargin',<%=originList%>,'Margin','origin',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>',<%=fr%>);return calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','origin',<%=k%>,0,<%=fr%>)" >
					   <option value="A" <%="A".equals(chargeInfo.getMarginType())?"selected":""%>>Absolute</option>
					  <option value="P" <%="P".equals(chargeInfo.getMarginType())?"selected":""%>>Percent</option>
					  </select></td>

	
				<%if("Yes".equalsIgnoreCase(MarginTest) || "<<Back".equalsIgnoreCase(submit)||"Y".equalsIgnoreCase(chargesDOB.getSelectedFlag())){
					%>
						<td align='center'><input class="text" type="text" value="<%=chargeInfo.getMargin()%>" size=5 name='originMargin<%=fr%><%=j%>'  <%=originMarginDisabled%> onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","origin",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",<%=fr%>)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','originDiscount<%=fr%>',<%=j%>,'Discount')" maxlength='10'>

				<%}else{
					session.setAttribute("MarginTestCount",MarginTestCount);%>
					<td align='center'><input class="text" type="text" value="<%=chargeInfo.getMargin()<0?0:chargeInfo.getMargin()%>" size=5 name='originMargin<%=fr%><%=j%>'  <%=originMarginDisabled%> onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","origin",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",<%=fr%>)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','originDiscount<%=fr%>',<%=j%>,'Discount')" maxlength='10'>
					  </td>
				<%}%>
					  <%}
%>

					<td><font color="<%=color%>"><%=new Double(chargeInfo.getRecOrConSellRrate())%></font></td><!-- @@ added for 180164 -->
					<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
						{%>
					 <td>
					  <select class="select"  name="originDiscountType<%=fr%><%=j%>"  size="1" disabled>
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
					  <%}
						else
						{%>
						<td>
					  <select class="select"  name="originDiscountType<%=fr%><%=j%>"  size="1" id='noRound' onChange="enableDisableMarginType(this,'originMargin',<%=j%>,'Margin','originDiscount',<%=originList1%>,'Discount','origin',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>',<%=fr%>);calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','origin',<%=k%>,0,<%=fr%>)">
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
					  <%}%>
					  <td><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size=5 name='originDiscount<%=fr%><%=j%>' onblur="calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','origin',<%=k%>,0,<%=fr%>)" <%=originDiscDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getDiscount()%>','originMargin<%=fr%>',<%=j%>,'Margin')" maxlength='10'></td>
					  
					  <%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					  <td><input class="text" type="text" id='chargeSellrate' value="<%=round(chargeInfo.getSellRate())%>" size=5 name='originSellRate<%=fr%><%=j%>' onBlur = "riverseCalOfCharges(this,'originMargin<%=fr%><%=j%>','originMargin<%=fr%><%=j%>','originDiscountType<%=fr%><%=j%>','originMarginType<%=fr%><%=j%>','<%=chargeInfo.getBuyRate()%>','<%=chargeInfo.getRecOrConSellRrate()%>','<%=k%>')"></td>
					  <%}else{%>
					  <td><input class="text" type="text" id='chargeSellrate' value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5  name='originSellRate<%=fr%><%=j%>'
					  onBlur = "riverseCalOfCharges(this,'originMargin<%=fr%><%=j%>','originMargin<%=fr%><%=j%>','originDiscountType<%=fr%><%=j%>','originMarginType<%=fr%><%=j%>','<%=chargeInfo.getBuyRate()%>','<%=chargeInfo.getRecOrConSellRrate()%>','<%=k%>')"></td>
					  <%}%>
					  <td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>

					  <input type='hidden' name='marginTestFailed' value='<%=(checkedFlag && chargeInfo.isMarginTestFailed())?"Y":"N"%>'/>                    
					</tr>
<%
					}
				}
	}//new
			%>			
<%

				for(int fr=0;fr<frtsize;fr++){%>
                <tr class="formheader" cellpadding="4" align='center'>
			 


                
              <% legRateDetails		=	(MultiQuoteFreightLegSellRates)finalDOB.getLegDetails().get(fr);
                 destCharges				  =  legRateDetails.getDestChargesList();
				 destIndices				  =  legRateDetails.getSelctedDestChargesListIndices();
                 destChargesSelectedIndices =  legRateDetails.getDestChargesSelectedFlag();
		//		destCharges		= finalDOB.getDestChargesList();
			//	destIndices		= finalDOB.getSelctedDestChargesListIndices();
		//		destChargesSelectedIndices=finalDOB.getDestChargesSelectedFlag();//@@ Added by subrahmanyam for 154381 on 14/02/09
				if(destCharges!=null)
					destChargesSize	= destCharges.size();
				else
					destChargesSize	= 0;

				if(destChargesSize>0)
				{
%>
                   <td>Select</td>
			  <td>Charge Name</td>
			  <td>Defined By</td>
			  <td>Breakpoint</td>
			  <td>Currency</td>
<%			if("Y".equals(buyRatePermission))
			{
%>			  <td>Buy Rate</td>
			  <td>Margin Type</td>
			  <td>Margin</td>
<%			}
%>
			   <td>RSR</td>
			  <td>Discount Type</td>
			  <td>Discount</td>
			  <td>Sell Rate</td>
			  <td>Basis</td>
			  <td>Ratio</td>			 
            </tr>
                  






					
<%
				}
				for(int j=0;j<destChargesSize;j++)
				{ if(j==0 && destChargesSize >0){%>
					<tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align='center'><%=legRateDetails.getDestination()%>-DESTINATION CHARGES</td>
					
					</tr>

			<%}		destList = new ArrayList();
					destList1 = new ArrayList();
					checkedFlag				=	false;
					chargesDOB				= (MultiQuoteCharges)destCharges.get(j);
					

                  if("BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
					{
						destDiscDisabled	=	"disabled";
					    destMarginDisabled	=	"";

					}

		             else
					{
						destDiscDisabled	=	"";
						destMarginDisabled	=	"disabled";
					}
                     //@@Enhancement
					destChargeInfo			= chargesDOB.getChargeInfoList();
					destChargesInfoSize		= destChargeInfo.size();

					if(destIndices!=null)
					{
						for(int i=0;i<destIndices.length;i++)
						{
							checkedFlag				=	false;

							if(destIndices[i]==j&&destChargesSelectedIndices!=null&&"Y".equalsIgnoreCase(destChargesSelectedIndices[i]))

							{
								checkedFlag = true;
								break;
							}
						}
					}
						if(!checkedFlag && ("Y".equalsIgnoreCase(chargesDOB.getSelectedFlag()) || "T".equalsIgnoreCase(chargesDOB.getSelectedFlag())) && isMarginTestPerformed==null)
					{
						checkedFlag = true;
					}

					if(j==0 && destChargesInfoSize>0)
					{
%>
						<tr class="formdata" cellpadding="4" align=center>
							<td ><B><%//=legCharges.getOrigin()+"-"+legCharges.getDestination()%></B></td>
						</tr>
<%
					}

					 for(int k=0;k<destChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)destChargeInfo.get(k);

							  destList.add(new Double(chargeInfo.getBuyRate()));
							  destList1.add(new Double(chargeInfo.getRecOrConSellRrate()));
					}
					for(int k=0;k<destChargesInfoSize;k++)
					{
						chargeInfo = (MultiQuoteChargeInfo)destChargeInfo.get(k);
						color	   =  (checkedFlag && chargeInfo.isMarginTestFailed())?"#FF0000":"#000000";
						if(checkedFlag)
							destCheckedFlag = "checked";
						else destCheckedFlag = "";


%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%if(k==0){%><input type='checkbox' name='destChkBox<%=fr%>' onclick="setValue(<%=j%>,'dest',<%=fr%>)" <%=destCheckedFlag%>>
						<input type='hidden' name='destChargeIndices<%=fr%>'/>
						<input type='hidden' name='destChargeSelectedFlag<%=fr%>'/><%}else{}%>
					  </td>
					  <td><font color="<%=color%>"><%=k==0?(("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))?chargesDOB.getInternalName():chargesDOB.getChargeDescriptionId()):""%></font></td>
					  <td><font color="<%=color%>"><%=k==0?chargesDOB.getTerminalId():""%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getBreakPoint()%></font></td>
					  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
<%					if("Y".equals(buyRatePermission))
					{
%>			  		<!-- @@ Commented by subrahmanyam for 180161 -->
				<!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td> -->
				<!-- @@Added by subrahmanyam for 180161 -->
						<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) ||"S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
						<td><font color="<%=color%>"><%=round(chargeInfo.getBuyRate())%></font></td>
						<%}else{%>
						<td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td>
						<%}%>
						<input type='hidden' name='AbsPersRateDest' value='<%=chargesDOB.getSellBuyFlag()%>'/>
				<!-- @@Ended by subrahmanyam for 180161 -->
					<td>
					  <select class="select"  name="destMarginType<%=fr%><%=j%>" size="1"  onChange="enableDisableMarginType(this,'destDiscount',<%=j%>,'Discount','destMargin',<%=destList%>,'Margin','dest',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>',<%=fr%>);calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','dest',<%=k%>,0,<%=fr%>)" >
						  <option value="A" <%="A".equals(chargeInfo.getMarginType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getMarginType())?"selected":""%>>Percent</option>
					  </select>
					  </td>
					
						<%if("Yes".equalsIgnoreCase(MarginTest) || "<<Back".equalsIgnoreCase(submit)||"Y".equalsIgnoreCase(chargesDOB.getSelectedFlag()))
						{
						MarginTestCount1=(Integer)session.getAttribute("MarginTestCount");
					//	int mTCount4=MarginTestCount1.intValue();

								%>

								<td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getMargin()%>" size=5 name='destMargin<%=fr%><%=j%>' onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","dest",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",<%=fr%>)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','destDiscount<%=fr%>',<%=j%>,'Discount')" <%=destMarginDisabled%> maxlength='10'></font></td>

					  <%}else{
								session.setAttribute("MarginTestCount",MarginTestCount);%>
								<td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getMargin()<0?0:chargeInfo.getMargin()%>" size=5 name='destMargin<%=fr%><%=j%>' onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","dest",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",<%=fr%>)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','destDiscount<%=fr%>',<%=j%>,'Discount')" <%=destMarginDisabled%> maxlength='10'></font></td>
					  <%}%>
<!-- @@ Ended by subrahmanyam for the Enhancement 154381 on 03/02/09 -->
<%					}
%>						<td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td>
						<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						{%>
						<td>
						<select class="select"  name="destDiscountType<%=fr%><%=j%>" size="1" disabled >
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
					  <%}
						else
						{	%>
                          <td>
						<select class="select"  name="destDiscountType<%=fr%><%=j%>" size="1" onChange="enableDisableMarginType(this,'destMargin',<%=j%>,'Margin','destDiscount',<%=destList1%>,'Discount','dest',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>',<%=fr%>);calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','dest',<%=k%>,0,<%=fr%>)" >
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
						<%}%>
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size=5 name='destDiscount<%=fr%><%=j%>'
					  onblur="calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','dest',<%=k%>,0,<%=fr%>)" <%=destDiscDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getDiscount()%>','destMargin<%=fr%>',<%=j%>,'Margin')" maxlength='10'></font></td>
					  <!-- <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5 readOnly name='destSellRate<%=j%>'></font></td> -->
					  <!-- Added for 180161 -->
					  <%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					  <td><font color="<%=color%>"><input class="text" type="text" id='Destrates' value="<%=round(chargeInfo.getSellRate())%>" size=5  name='destSellRate<%=fr%><%=j%>'onBlur = "riverseCalOfCharges(this,'destMargin<%=fr%><%=j%>','destMargin<%=fr%><%=j%>','destDiscountType<%=fr%><%=j%>','destMarginType<%=fr%><%=j%>','<%=chargeInfo.getBuyRate()%>','<%=chargeInfo.getRecOrConSellRrate()%>','<%=k%>')"></font></td>
					  <%}else{%>
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5  name='destSellRate<%=fr%><%=j%>' onBlur = "riverseCalOfCharges(this,'destMargin<%=fr%><%=j%>','destMargin<%=fr%><%=j%>','destDiscountType<%=fr%><%=j%>','destMarginType<%=fr%><%=j%>','<%=chargeInfo.getBuyRate()%>','<%=chargeInfo.getRecOrConSellRrate()%>','<%=k%>')"></font></td>
					  <%}%>
					  <!-- Ended for 180161 -->
					  <td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly></font></td>
					  <input type='hidden' name='marginTestFailed' value='<%=(checkedFlag && chargeInfo.isMarginTestFailed())?"Y":"N"%>'/>
					 
					</tr>
<%
					}
				}

	}

	
	}
%>
	</table>

		<table width="100%" cellpadding="4" cellspacing="1" nowrap id="QuoteNotes" idcounter="1" defaultElement="internalNotes" xmlTagName="QuoteNotes" bgcolor='#FFFFFF'>

			<tr class='formheader'>
				<td colspan="<%=colspan%>">Notes</td>
            </tr>

			<tr class='formheader' align="center">
			  <td></td>
			  <td>Internal Notes</td>
			   <td>External Notes</td>
			  <td></td>
            </tr>
		</table>

		<table width="100%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class='text'>
				  <td width="18%" align="right" colspan="<%=colspan%>">
				  
				  <input class="input" name="submit"  type="submit" value="Update" onclick='setSubmitValue("Next>>")'>  <!--Added by Gowtham  -->
          		   <input class="input" name="submit" type="submit" value="Margin Test" onclick='setSubmitValue(this.value)'> 
				 <input class="input" name="submit" type="submit" value="Close" onclick='closeandContinue()'>
				
				 <input type="hidden" name ="quoteId" value=<%=masterDOB.getQuoteId()%>>
				  <input type="hidden" name="isEscalated" value=<%=request.getParameter("isEscalated")%>> <!-- Added by Gowtham -->
				<!--   <input type="hidden" name="Operation" value="<%=operation%>"> -->
				  <input type="hidden" name="Operation" value="Modify">
				  <input type="hidden" name="subOperation" value="SELLCHARGES" >
				   <input type="hidden" name="update" value="update" >
				 <input type="hidden" name="subVal">
                  <input type='hidden'   name='selectedIndexList' value ='<%=selectedIndexList!= null?selectedIndexList:""%>' > 
				  <input type="hidden" name="marginTestFlag">
				  <input type="hidden" name="fromWhere" value="<%=(String)request.getAttribute("fromWhere")%>" >
				  <input type="hidden" name="quoteName" value="<%= quoteName!=null? quoteName:""%>">
				   <input type="hidden" name="quoteStatus" value="<%= quoteStatus%>">   <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
				   <input type="hidden" name="completeFlag" value="<%= completeFlag%>">
				  <input type="hidden" name="fromWhat" value="<%=request.getParameter("fromWhat")!=null?request.getParameter("fromWhat"):""%>" >
				  </td>
				   <input type="hidden" name="defaultMargin" value="<%=chargeInfo.getMargin()%>" ><!-- subrahmanyam for 154381 on 03/02/09 -->
            </tr>
	</table>

	</td>
 </tr>
</table>
</form>
</BODY>
</HTML>
<%

logger.info("Total TIme taken for rendering JSP (3rd screen) in milli seconds::  " + (System.currentTimeMillis()-start) + "  User Id::" + masterDOB.getUserId());
	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Error in JSP"+e);
    logger.error(FILE_NAME+"Error in JSP"+e);
		e.printStackTrace();
		errorMessageObject = new ErrorMessage("An Error Has Occurred. Please Try Again.","QMSQuoteController");
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		keyValueList.add(new KeyValue("Operation",operation));
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);

%>		<jsp:forward page="../ESupplyErrorPage.jsp"/>

<%
	}
%>