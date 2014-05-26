<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSQuoteChargesSelect.jsp
Product Name		: QMS
Module Name			: Quote
Task				: Adding/Modify
Date started		:
Date modified		:
Author    			: Yuvraj
Related Document	: CR_DHLQMS_1007

--%>

<%@page import = "java.util.ArrayList,
				  java.util.Calendar,
				  java.util.Date,
				  java.sql.Timestamp,
				  java.util.StringTokenizer,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.KeyValue,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.operations.quote.dob.QuoteMasterDOB,
				  com.qms.operations.quote.dob.QuoteFinalDOB,
				  com.qms.operations.quote.dob.QuoteHeader,
				  com.qms.operations.quote.dob.QuoteCharges,
				  com.qms.operations.quote.dob.QuoteChargeInfo,
				  com.qms.operations.quote.dob.QuoteFreightLegSellRates,
				  com.qms.operations.quote.dob.QuoteFreightRSRCSRDOB"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSQuoteChargesSelect.jsp";
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
		}%>
<%
 long start = System.currentTimeMillis();
	logger  = Logger.getLogger(FILE_NAME);
	ESupplyDateUtility  eSupplyDateUtility=	new ESupplyDateUtility();
	String		  operation				  =	request.getParameter("Operation");
	String        dateFormat			  =	loginbean.getUserPreferences().getDateFormat();
	QuoteFinalDOB finalDOB				  =	null;
	QuoteHeader	  headerDOB				  =	null;
	QuoteMasterDOB masterDOB			  =	null;

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
	QuoteFreightLegSellRates  legCharges  = null;
	QuoteCharges  chargesDOB			  = null;
	QuoteChargeInfo chargeInfo			  = null;
   QuoteFreightRSRCSRDOB   freightDOB     = null;

	String		  str[]				      = null;
	String		  str1[]				   = null;
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
//@@ Ended by subrahmanyam for the Enhancement 154381 on 09/02/09
	//Calendar	  calendar	=	Calendar.getInstance();

	//java.text.DateFormat	=	new java.text.DateFormat();


	try
	{
		eSupplyDateUtility.setPatternWithTime("DD-MONTH-YYYY");//in order to get the full month name
	}
	catch(Exception exp)
	{

    logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
	}
	try
	{

	      quoteName			=	request.getParameter("quoteName");//@@Added by VLAKSHMI for the WPBN issue-167677
		  quoteStatus			=	request.getParameter("quoteStatus");//@@Added by VLAKSHMI for the WPBN issue-167677
		  	  completeFlag			=	request.getParameter("completeFlag");//@@Added by VLAKSHMI for the WPBN issue-167677
		isMarginTestPerformed = (String)request.getAttribute("isMarginTestPerformed");
		finalDOB		      =	(QuoteFinalDOB)session.getAttribute("finalDOB");
		masterDOB			  =	 finalDOB.getMasterDOB();
		headerDOB		      =	 finalDOB.getHeaderDOB();
		fromWhere			  =  request.getParameter("fromWhere");
		update			      =  request.getParameter("update");
      count                    =  Integer.parseInt((String)request.getParameter("count"));
		if(fromWhere==null || "null".equals(fromWhere))
			fromWhere		  =	 (String)request.getAttribute("fromWhere");
//  System.out.println("fromHere----"+fromWhere);
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
<%
	if(finalDOB.getInternalNotes()!=null)
	{
		internalNotes			=	finalDOB.getInternalNotes();
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
function selectAll()
{
	var originChk = document.getElementsByName("originChkBox");
	var destChk	  = document.getElementsByName("destChkBox");

	if(document.forms[0].select.checked)
	{
		for(var i=0;i<originChk.length;i++)
		{
			originChk[i].checked=true;
			setValue(i,"origin");
		}
		for(var j=0;j<noOfLegs;j++)
		{
			var frtChk	  = document.getElementsByName("frtChkBox"+j);
			for(var i=0;i<frtChk.length;i++)
			{
				frtChk[i].checked=true;
				setValue(i,"frt"+j);
			}
		}
		for(var i=0;i<destChk.length;i++)
		{
			destChk[i].checked=true;
			setValue(i,"dest");
		}
		for(var j=0;j<noOfLegs;j++)
		{
			var frtChk	  = document.getElementsByName("surChkBox"+j);
			//alert(frtChk.length);
			for(var i=0;i<frtChk.length;i++)
			{
				frtChk[i].checked=true;
				setValue(i,"ftr"+j);
			}
		}
	}
	else
	{
		for(var i=0;i<originChk.length;i++)
		{
			originChk[i].checked=false;
			setValue(i,"origin");
		}
		/*for(var j=0;j<noOfLegs;j++)
		{
			var frtChk	  = document.getElementsByName("frtChkBox"+j);
			for(var i=0;i<frtChk.length;i++)
			{
				frtChk[i].checked=false;
				setValue(i,"frt"+j);
			}
		}*/
		for(var j=0;j<noOfLegs;j++)
		{
			var frtChk	  = document.getElementsByName("surChkBox"+j);
			//alert(frtChk.length);
			for(var i=0;i<frtChk.length;i++)
			{
				frtChk[i].checked=false;
				setValue(i,"ftr"+j);
			}
		}
		for(var i=0;i<destChk.length;i++)
		{
			destChk[i].checked=false;
			setValue(i,"dest");
		}
	}
}
function setAllValuesOnLoad()
{
	var isMarginTestPerformed = '<%=isMarginTestPerformed%>';
	var submitObj  = document.getElementsByName("submit");
	var originChk = document.getElementsByName("originChkBox");
	var destChk	  = document.getElementsByName("destChkBox");

	for(var i=0;i<originChk.length;i++)
	{
		setValue(i,"origin");
	}
	for(var j=0;j<noOfLegs;j++)
	{
		var frtChk	  = document.getElementsByName("frtChkBox"+j);
		for(var i=0;i<frtChk.length;i++)
		{
			setValue(i,"frt"+j);
		}
	}
	for(var i=0;i<destChk.length;i++)
	{
		setValue(i,"dest");
	}

	if(isMarginTestPerformed!='Y')
	{
		for(var i=0;i<submitObj.length;i++)
		{
			if(submitObj[i].value=='Next>>'||submitObj[i].value=='Update') // Added by Gowtham for QuoteEscalted Modify issue.
				submitObj[i].disabled = true;
		}
	}
}

function setValue(index,chargeType)
{
// commented by subrahmanyam for the enhancement 154381 on 03/02/09
	/* if(chargeType=='origin')
	{
		if(document.getElementsByName("originChkBox")[index].checked)
			document.getElementsByName("originChargeIndices")[index].value=index;
		else
			document.getElementsByName("originChargeIndices")[index].value='';
	}
	else if(chargeType=='dest')
	{
		if(document.getElementsByName("destChkBox")[index].checked)
			document.getElementsByName("destChargeIndices")[index].value=index;
		else
			document.getElementsByName("destChargeIndices")[index].value='';
	} */
//added by subrahmanyam for the enhancement 154381
	if(chargeType=='origin')
	{
		//alert(document.getElementsByName("originChkBox")[index].checked);
		if(document.getElementsByName("originChkBox")[index].checked)
		{
		  document.getElementsByName("originChargeSelectedFlag")[index].value='Y';
		    document.getElementsByName("originChargeIndices")[index].value=index;
	}
	else
	{
			document.getElementsByName("originChargeSelectedFlag")[index].value='N';
			  document.getElementsByName("originChargeIndices")[index].value=index;
		}

	}
	else if(chargeType=='dest')
	{
		if(document.getElementsByName("destChkBox")[index].checked)
		{
			document.getElementsByName("destChargeIndices")[index].value=index;
			document.getElementsByName("destChargeSelectedFlag")[index].value='Y';
		}
		else
		{
			document.getElementsByName("destChargeSelectedFlag")[index].value='N';
		    document.getElementsByName("destChargeIndices")[index].value=index;
		}
   }
//ended by subrahmanyam for the enhanement 154381
	else
	{
		for(var j=0;j<noOfLegs;j++)
		{
			if(chargeType=='frt'+j)
			{

				if(document.getElementsByName("frtChkBox"+j)[index].checked)
					document.getElementsByName("frtChargeIndices"+j)[index].value=index;
				else
					document.getElementsByName("frtChargeIndices"+j)[index].value='';
				if(document.getElementsByName("surChkBox"+j)[index]!=null)
				{
				if(document.getElementsByName("surChkBox"+j)[index].checked)
					document.getElementsByName("surChargeIndices"+j)[index].value=index;
				else
					document.getElementsByName("surChargeIndices"+j)[index].value='';
				}
			}

		}
	}
}


function disableUnChecked()
{

	var originChk = document.getElementsByName("originChkBox");
	var destChk	  = document.getElementsByName("destChkBox");

	var subVal   = document.forms[0].subVal.value;//subrahmanyam
	for(var i=0;i<originChk.length;i++)
	{

//commented by subrahmanyam for 154381 on 03/02/09
		if(originChk[i].checked==false && subVal=='Next>>'){
		  document.getElementsByName("originChargeIndices")[i].disabled=true;
		  document.getElementsByName("originChargeIndices")[i].value=i;//added by KAM FOR DUPLICATE CHARGES ISSUE:
		}
		else
		{
			//document.getElementsByName("originChargeIndices")[i].disabled=false;//COMMENTED by KAM FOR DUPLICATE CHARGES ISSUE:
			 document.getElementsByName("originChargeIndices")[i].value=i;//added by KAM FOR DUPLICATE CHARGES ISSUE:
			document.getElementsByName("originChkBox")[i].checked=originChk[i].checked;//added by subrahmanyam for 154381
			//document.getElementsByName("originMargin")[i].disabled='';
			//document.getElementsByName("originChargeIndices")[i].value=i;// for 154381
		}

		//alert(document.getElementsByName("originChargeIndices")[i].disabled);
	}
	for(var i=0;i<destChk.length;i++)
	{
	//Commented by subrahmanyam for 154381 on 03/02/09
		if(destChk[i].checked==false  && (subVal=='Next>>')){
			document.getElementsByName("destChargeIndices")[i].disabled=true;
			 document.getElementsByName("destChargeIndices")[i].value=i;//added by KAM FOR DUPLICATE CHARGES ISSUE:
			}
		else
		{
			//document.getElementsByName("destChargeIndices")[i].disabled=false;//COMMENTED by KAM FOR DUPLICATE CHARGES ISSUE:
			 document.getElementsByName("destChargeIndices")[i].value=i;//added by KAM FOR DUPLICATE CHARGES ISSUE:
			document.getElementsByName("destChkBox")[i].checked=destChk[i].checked;//Added by subrahmanyam for 154381 on 03/02/09
			//document.getElementsByName("destMargin")[i].disabled='';
			//document.getElementsByName("destChargeIndices")[i].value=i;// for 154381
		}
	}
	for(var j=0;j<noOfLegs;j++)
	{
		var frtChk	  = document.getElementsByName("frtChkBox"+j);
		var surChk    = document.getElementsByName("surchargeChkBox"+j);
		for(var i=0;i<frtChk.length;i++)
		{
			if(frtChk[i].checked==false)
				document.getElementsByName("frtChargeIndices"+j)[i].disabled=true;
			else
			    document.getElementsByName("frtChargeIndices"+j)[i].value=i;
		}
		if(surChk!=null)
		{
			for(var i=0;i<surChk.length;i++)
			{
				if(surChk[i].checked==false)
					document.getElementsByName("surChargeIndices"+j)[i].disabled=true;
				else
					document.getElementsByName("surChargeIndices"+j)[i].value=i;
			}
		}

	}
}
var permission = '<%=buyRatePermission%>';
function enableDisableMarginType(fromObj,type,index,label,toObj,rate,type1,chargeType,rowNo,legNo,buysellFlag)
{

//alert('fromObj,type,index,label,toObj,rate,type1,chargeType,rowNo,legNo,buysellFlag..'+fromObj+": "+type+": "+index+": "+label+": "+toObj+": "+rate+": "+type1+": "+chargeType+": "+rowNo+": "+legNo+": "+buysellFlag);

	var obj		=	document.getElementsByName(type+index);
	var objType	=	document.getElementsByName(type+"Type"+index);
var obj1		=	document.getElementsByName(toObj+index);
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
		    calcSellRate(rate,index,type1,chargeType,obj.length,legNo);
		}
		else if(buysellFlag=='BR'||buysellFlag=='SBR'||buysellFlag=='BC'||buysellFlag=='B')
	  {
		calcSellRate(rate,index,type,chargeType,rowNo,legNo);
		}


}

function enableDisableMargin(fromObj,preValue,type,index,label)
{
	var obj		=	document.getElementsByName(type+index);
	var objType	=	document.getElementsByName(type+"Type"+index);

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

function calcSellRate(rate,index,type,chargeType,rowNo,legNo,obj)
{
	obj.value=round(obj.value);
	var noOfLegs	=	<%=chargesSize%>;

	var object=  document.getElementsByName(chargeType+type+index);
   var object1=  document.getElementsByName(chargeType+type+legNo+index);
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
			var marginType = document.getElementsByName(chargeType+type+"Type"+index)[k].value;
			var margin	   = document.getElementsByName(chargeType+type+index)[k].value;
			if(isNaN(margin))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+index)[k].value ='';
				document.getElementsByName(chargeType+type+index)[k].focus();
				return false;
			}
			if(marginType=='A')
			{
				if(margin.length!=0)
				{

					document.getElementsByName(chargeType+"SellRate"+index)[k].value = round(parseFloat(rate[count++])+parseFloat(margin));
				}
			}
			else if(marginType=='P')
			{
				if(margin.length!=0)
				{

					document.getElementsByName(chargeType+"SellRate"+index)[k].value = round(parseFloat(rate[count])+(parseFloat(margin)*parseFloat(rate[count++])/100));
				}

			}

		}
		else if(type=='Discount')
		{
			var discountType = document.getElementsByName(chargeType+type+"Type"+index)[k].value;
			var discount	 = document.getElementsByName(chargeType+type+index)[k].value;

			if(isNaN(discount))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+index)[k].value ='';
				document.getElementsByName(chargeType+type+index)[k].focus();
				return false;
			}

			if(discountType=='A')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[k].value = round(parseFloat(rate[count++])-parseFloat(discount));
			}
			else if(discountType=='P')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[k].value = round(parseFloat(rate[count])-((parseFloat(discount)*parseFloat(rate[count++])/100)));
			}

		}
	   }
	}
	  else
		{
		if(type=='Margin')
		{
			var marginType = document.getElementsByName(chargeType+type+"Type"+index)[rowNo].value;
			var margin	   = document.getElementsByName(chargeType+type+index)[rowNo].value;
			if(isNaN(margin))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+index)[rowNo].value ='';
				document.getElementsByName(chargeType+type+index)[rowNo].focus();
				return false;
			}
			if(marginType=='A')
			{
				if(margin.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[rowNo].value = round(parseFloat(rate)+parseFloat(margin));
			}
			else if(marginType=='P')
			{
				if(margin.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[rowNo].value = round(parseFloat(rate)+(parseFloat(margin)*parseFloat(rate)/100));
			}

		}
		else if(type=='Discount')
		{
			var discountType = document.getElementsByName(chargeType+type+"Type"+index)[rowNo].value;
			var discount	 = document.getElementsByName(chargeType+type+index)[rowNo].value;

			if(isNaN(discount))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+index)[rowNo].value ='';
				document.getElementsByName(chargeType+type+index)[rowNo].focus();
				return false;
			}

			if(discountType=='A')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[rowNo].value = round(parseFloat(rate)-parseFloat(discount));
			}
			else if(discountType=='P')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[rowNo].value = round(parseFloat(rate)-((parseFloat(discount)*parseFloat(rate)/100)));
			}

		}
	  }
	}
	else
	{

			if(rowNo==length1)
		{
		for(var k=0;k<rowNo;k++)
			{

			if(type=='Margin')
			{
				var marginType = document.getElementsByName(chargeType+type+legNo+"Type"+index)[k].value;
				var margin	   = document.getElementsByName(chargeType+type+legNo+index)[k].value;

				if(isNaN(margin))
				{
					alert("Please Enter Numeric Values Only.");
					document.getElementsByName(chargeType+type+legNo+index)[k].value ='';
					document.getElementsByName(chargeType+type+legNo+index)[k].focus();
					return false;
				}

				if(marginType=='A')
				{
					if(margin.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[k].value = round(parseFloat(rate[count++])+parseFloat(margin));

				}
				else if(marginType=='P')
				{
					if(margin.length!=0)

						document.getElementsByName(chargeType+"SellRate"+legNo+index)[k].value = round(parseFloat(rate[count])+(parseFloat(margin)*parseFloat(rate[count++])/100));
				}
			}
			else if(type=='Discount')
			{
				var discountType = document.getElementsByName(chargeType+type+legNo+"Type"+index)[k].value;
				var discount	 = document.getElementsByName(chargeType+type+legNo+index)[k].value;

				if(isNaN(discount))
				{
					alert("Please Enter Numeric Values Only.");
					document.getElementsByName(chargeType+type+legNo+index)[k].value ='';
					document.getElementsByName(chargeType+type+legNo+index)[k].focus();
					return false;
				}

				if(discountType=='A')
				{
					if(discount.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[k].value = round(parseFloat(rate[count++])-parseFloat(discount));
				}
				else if(discountType=='P')
				{
					if(discount.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[k].value = round(parseFloat(rate[count])-((parseFloat(discount)*parseFloat(rate[count++])/100)));
				}

			}

		  }

		}
		else
		{

		if(type=='Margin')
			{
				var marginType = document.getElementsByName(chargeType+type+legNo+"Type"+index)[rowNo].value;
				var margin	   = document.getElementsByName(chargeType+type+legNo+index)[rowNo].value;

				if(isNaN(margin))
				{
					alert("Please Enter Numeric Values Only.");
					document.getElementsByName(chargeType+type+legNo+index)[rowNo].value ='';
					document.getElementsByName(chargeType+type+legNo+index)[rowNo].focus();
					return false;
				}

				if(marginType=='A')
				{
					if(margin.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[rowNo].value = round(parseFloat(rate)+parseFloat(margin));
				}
				else if(marginType=='P')
				{
					if(margin.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[rowNo].value = round(parseFloat(rate)+(parseFloat(margin)*parseFloat(rate)/100));
				}
			}
			else if(type=='Discount')
			{
				var discountType = document.getElementsByName(chargeType+type+legNo+"Type"+index)[rowNo].value;
				var discount	 = document.getElementsByName(chargeType+type+legNo+index)[rowNo].value;

				if(isNaN(discount))
				{
					alert("Please Enter Numeric Values Only.");
					document.getElementsByName(chargeType+type+legNo+index)[rowNo].value ='';
					document.getElementsByName(chargeType+type+legNo+index)[rowNo].focus();
					return false;
				}

				if(discountType=='A')
				{
					if(discount.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[rowNo].value = round(parseFloat(rate)-parseFloat(discount));
				}
				else if(discountType=='P')
				{
					if(discount.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[rowNo].value = round(parseFloat(rate)-((parseFloat(discount)*parseFloat(rate)/100)));
				}

			}
		}
		//}
	}
}

//added by subrahmanyam for 180161
function calcSellRate1(rate,index,type,chargeType,rowNo,legNo,sellBuyFlag,obj)
{
	obj.value=round(obj.value);
	var noOfLegs	=	<%=chargesSize%>;
	bcFlag = sellBuyFlag;
	var object=  document.getElementsByName(chargeType+type+index);
   var object1=  document.getElementsByName(chargeType+type+legNo+index);
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
			var marginType = document.getElementsByName(chargeType+type+"Type"+index)[k].value;
			var margin	   = document.getElementsByName(chargeType+type+index)[k].value;
			if(isNaN(margin))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+index)[k].value ='';
				document.getElementsByName(chargeType+type+index)[k].focus();
				return false;
			}
			if(marginType=='A')
			{
				if(margin.length!=0)
				{

					document.getElementsByName(chargeType+"SellRate"+index)[k].value = round(parseFloat(rate[count++])+parseFloat(margin));
				}
			}
			else if(marginType=='P')
			{
				if(margin.length!=0)
				{

					document.getElementsByName(chargeType+"SellRate"+index)[k].value = round(parseFloat(rate[count])+(parseFloat(margin)*parseFloat(rate[count++])/100));
				}

			}

		}
		else if(type=='Discount')
		{
			var discountType = document.getElementsByName(chargeType+type+"Type"+index)[k].value;
			var discount	 = document.getElementsByName(chargeType+type+index)[k].value;

			if(isNaN(discount))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+index)[k].value ='';
				document.getElementsByName(chargeType+type+index)[k].focus();
				return false;
			}

			if(discountType=='A')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[k].value = round(parseFloat(rate[count++])-parseFloat(discount));
			}
			else if(discountType=='P')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[k].value = round(parseFloat(rate[count])-((parseFloat(discount)*parseFloat(rate[count++])/100)));
			}

		}
	   }
	}
	  else
		{
		if(type=='Margin')
		{
			var marginType = document.getElementsByName(chargeType+type+"Type"+index)[rowNo].value;
			var margin	   = document.getElementsByName(chargeType+type+index)[rowNo].value;
			if(isNaN(margin))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+index)[rowNo].value ='';
				document.getElementsByName(chargeType+type+index)[rowNo].focus();
				return false;
			}
			if(marginType=='A')
			{
				if(margin.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[rowNo].value = round(parseFloat(rate)+parseFloat(margin));
			}
			else if(marginType=='P')
			{
				if(margin.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[rowNo].value = round(parseFloat(rate)+(parseFloat(margin)*parseFloat(rate)/100));
			}

		}
		else if(type=='Discount')
		{
			var discountType = document.getElementsByName(chargeType+type+"Type"+index)[rowNo].value;
			var discount	 = document.getElementsByName(chargeType+type+index)[rowNo].value;

			if(isNaN(discount))
			{
				alert("Please Enter Numeric Values Only.");
				document.getElementsByName(chargeType+type+index)[rowNo].value ='';
				document.getElementsByName(chargeType+type+index)[rowNo].focus();
				return false;
			}

			if(discountType=='A')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[rowNo].value = round(parseFloat(rate)-parseFloat(discount));
			}
			else if(discountType=='P')
			{
				if(discount.length!=0)
					document.getElementsByName(chargeType+"SellRate"+index)[rowNo].value = round(parseFloat(rate)-((parseFloat(discount)*parseFloat(rate)/100)));
			}

		}
	  }
	}
	else
	{

			if(rowNo==length1)
		{
		for(var k=0;k<rowNo;k++)
			{

			if(type=='Margin')
			{
				var marginType = document.getElementsByName(chargeType+type+legNo+"Type"+index)[k].value;
				var margin	   = document.getElementsByName(chargeType+type+legNo+index)[k].value;

				if(isNaN(margin))
				{
					alert("Please Enter Numeric Values Only.");
					document.getElementsByName(chargeType+type+legNo+index)[k].value ='';
					document.getElementsByName(chargeType+type+legNo+index)[k].focus();
					return false;
				}

				if(marginType=='A')
				{
					if(margin.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[k].value = round(parseFloat(rate[count++])+parseFloat(margin));

				}
				else if(marginType=='P')
				{
					if(margin.length!=0)

						document.getElementsByName(chargeType+"SellRate"+legNo+index)[k].value = round(parseFloat(rate[count])+(parseFloat(margin)*parseFloat(rate[count++])/100));
				}
			}
			else if(type=='Discount')
			{
				var discountType = document.getElementsByName(chargeType+type+legNo+"Type"+index)[k].value;
				var discount	 = document.getElementsByName(chargeType+type+legNo+index)[k].value;

				if(isNaN(discount))
				{
					alert("Please Enter Numeric Values Only.");
					document.getElementsByName(chargeType+type+legNo+index)[k].value ='';
					document.getElementsByName(chargeType+type+legNo+index)[k].focus();
					return false;
				}

				if(discountType=='A')
				{
					if(discount.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[k].value = round(parseFloat(rate[count++])-parseFloat(discount));
				}
				else if(discountType=='P')
				{
					if(discount.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[k].value = round(parseFloat(rate[count])-((parseFloat(discount)*parseFloat(rate[count++])/100)));
				}

			}

		  }

		}
		else
		{

		if(type=='Margin')
			{
				var marginType = document.getElementsByName(chargeType+type+legNo+"Type"+index)[rowNo].value;
				var margin	   = document.getElementsByName(chargeType+type+legNo+index)[rowNo].value;

				if(isNaN(margin))
				{
					alert("Please Enter Numeric Values Only.");
					document.getElementsByName(chargeType+type+legNo+index)[rowNo].value ='';
					document.getElementsByName(chargeType+type+legNo+index)[rowNo].focus();
					return false;
				}

				if(marginType=='A')
				{
					if(margin.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[rowNo].value = round(parseFloat(rate)+parseFloat(margin));
				}
				else if(marginType=='P')
				{
					if(margin.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[rowNo].value = round(parseFloat(rate)+(parseFloat(margin)*parseFloat(rate)/100));
				}
			}
			else if(type=='Discount')
			{
				var discountType = document.getElementsByName(chargeType+type+legNo+"Type"+index)[rowNo].value;
				var discount	 = document.getElementsByName(chargeType+type+legNo+index)[rowNo].value;

				if(isNaN(discount))
				{
					alert("Please Enter Numeric Values Only.");
					document.getElementsByName(chargeType+type+legNo+index)[rowNo].value ='';
					document.getElementsByName(chargeType+type+legNo+index)[rowNo].focus();
					return false;
				}

				if(discountType=='A')
				{
					if(discount.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[rowNo].value = round(parseFloat(rate)-parseFloat(discount));
				}
				else if(discountType=='P')
				{
					if(discount.length!=0)
						document.getElementsByName(chargeType+"SellRate"+legNo+index)[rowNo].value = round(parseFloat(rate)-((parseFloat(discount)*parseFloat(rate)/100)));
				}

			}
		}
		//}
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

	//alert(document.getElementById('BSFLAG').value)
	//commented by subrahmanyam for 180161 on 28/Aug/09
	/*
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
	*/
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

	var submitValue= document.forms[0].subVal.value;
	var submitObj  = document.getElementsByName("submit");

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

		for(var i=0;i<marginTest.length;i++)
		{
			if(marginTest[i].value=='Y')
				flag = true;
		}

		if(flag)
		{
			confirmFlag = confirm('Some of the Charges have Failed the Margin Test.\n Are You Sure You Want to Continue?');
			if(confirmFlag)
			{
				document.forms[0].marginTestFlag.value = 'F';
				return true;
			}
			else
			{
				for(var i=0;i<submitObj.length;i++)
				{
					if(submitObj[i].value=='Next>>')
						submitObj[i].disabled = true;
				}
				return false;
			}
		}


	  }
	}

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
//@@Added by Kameswari for the WPBN issue-146448
function setValues()
{

	<%for(int i=0;i<chargesSize;i++)
			{
		legCharges	   = (QuoteFreightLegSellRates)charges.get(i);


				freightCharges = legCharges.getFreightChargesList();
				chargesDOB			= (QuoteCharges)freightCharges.get(0);
				if(chargesDOB.getValidUpto()!=null)
				{
					str1					  =eSupplyDateUtility.getDisplayStringArray(chargesDOB.getValidUpto());
				}
				if(!legCharges.isSpotRatesFlag()){
		if("Y".equalsIgnoreCase(chargesDOB.getTransitTimeChecked())||"on".equalsIgnoreCase(chargesDOB.getTransitTimeChecked()))
	{%>
		document.getElementById('transitTimeCheck<%=i%>').checked	= true;
	<%}
	else
				{%>
document.getElementById('transitTimeCheck<%=i%>').checked	= false;
	<%}
	if("Y".equalsIgnoreCase(chargesDOB.getFrequencyChecked())||"on".equalsIgnoreCase(chargesDOB.getFrequencyChecked()))
	{%>
		document.getElementById('frequencyCheck<%=i%>').checked	= true;
	<%}
	else
				{%>
		document.getElementById('frequencyCheck<%=i%>').checked	= false;
	<%}
	if("Y".equalsIgnoreCase(chargesDOB.getCarrierChecked())||"on".equalsIgnoreCase(chargesDOB.getCarrierChecked()))
	{%>
		document.getElementById('carrierCheck<%=i%>').checked	= true;
	<%}
	else
				{%>
		document.getElementById('carrierCheck<%=i%>').checked	= false;
	<%}
	if(str1!=null)
				{
   	if("Y".equalsIgnoreCase(chargesDOB.getRateValidityChecked())||"on".equalsIgnoreCase(chargesDOB.getRateValidityChecked()))
	{%>
		document.getElementById('rateValidityCheck<%=i%>').checked	= true;
	<%}
	else
				{%>
document.getElementById('rateValidityCheck<%=i%>').checked	= false;
	<%}
				}
			}}%>
}
	
			
		function reverse(obj,buyrate,margin,margintype,discounttype,discountvalue,rsrValue,k)
		{
			//alert(rsrValue);
			//alert(buyrate);
			//alert(margin);
			//alert(margintype);
obj.value=round(obj.value);
if(!isNaN(obj.value))
{








var marginType ="";
	var discountType ="";
	var marginVal =0.0;
	var discountVal = 0.0;
	var brVal =0.0;
	
	var rsrVal = 0.0;
	//alert(document.getElementsByName(margintype)[k].value);
	//alert(document.getElementsByName(margin)[k].value);
	//var callSellRate =0.00;
	if(!document.getElementsByName(margin)[k].disabled){//if margin given
             marginType = document.getElementsByName(margintype)[k].value;
			 marginVal  = document.getElementsByName(margin)[k].value;
			

			 brVal  = buyrate;
			 if(marginType == "A")
			{
				 
             marginVal = round(parseFloat(obj.value) - parseFloat(brVal));
             
			}else if(marginType == "P"){
				marginVal = round((100*(parseFloat(obj.value) - parseFloat(brVal)))/parseFloat(brVal));
			}
			document.getElementsByName(margin)[k].value = marginVal;

	}
	else if(!document.getElementsByName(discountvalue)[k].disabled){//if discount given
		
              discountType = document.getElementsByName(discounttype)[k].value;
              discountVal  = document.getElementsByName(discountvalue)[k].value;
			  rsrVal = rsrValue;
			   if(discountType == "A")
			{
                 discountVal  = round(  parseFloat(rsrVal) - parseFloat(obj.value) ) ;

			}else if(discountType == "P"){
			   discountVal =  round(((parseFloat(rsrVal) - parseFloat(obj.value))*100)/parseFloat(rsrVal)) ;
			}
			document.getElementsByName(discountvalue)[k].value = discountVal;
			//alert("Given Discount---"+discountType+"********"+discountVal+"-------------"+rsrVal)
			}
		}
		else{
		alert("Please Enter Numeric Values Only.");

obj.value='';
obj.focus();
}
		}
			
			
function reverse1(obj,buyrate1,recommsell,originDiscountType,originDiscount,originMargin,originMarginType,k,j)
		{
			
			//alert(buyrate1);
			//alert(j);
			//sellrate=obj.value;
			//alert(sellrate);
			//alert(originMargin);
			//alert(originMarginType);
			
			//alert(recommsell);
			//alert(originMarginType);
			//alert(originMargin);
			//alert(document.getElementsByName(originDiscountType)[k].value);
			//alert(document.getElementsByName(originDiscount)[0].value);
			
			//alert(document.getElementsByName(originMarginType)[k].value);
			//alert("rocky"+document.getElementsByName(originMargin)[0].value);
obj.value=round(obj.value);
if(!isNaN(obj.value))
{


var brVal1 =0.0;
			var originDiscountType1 ="";
			var originDiscount1 = 0.0;
			var recommsel = 0.0;
			var originMarginType1="";
				var originMargin1=0.0;
//alert(document.getElementsByName(originMargin)[0].value);
if(!document.getElementsByName(originMargin)[k].disabled){
	
	
	//alert(originMargin);
            originMarginType1 = document.getElementsByName(originMarginType)[k].value;
			originMargin1  = document.getElementsByName(originMargin)[k].value;
			 brVal1  = buyrate1;
			 
			 if(originMarginType1 == "A")
			{
				 
				
             originMargin1 = round(parseFloat(obj.value) - parseFloat(brVal1));
			 
			
			}else if(originMarginType1== "P"){
				originMargin1 = round((100*(parseFloat(obj.value) - parseFloat(brVal1)))/parseFloat(brVal1));
			}
			document.getElementsByName(originMargin)[k].value = originMargin1;

	}

else 
	if(!document.getElementsByName(originDiscount)[k].disabled){//if discount given
			{
		      //alert("discount origin");
              originDiscountType1 = document.getElementsByName(originDiscountType)[k].value;
              originDiscount1  = document.getElementsByName(originDiscount)[k].value;
			  recommsel= recommsell;
			   if(originDiscountType1 == "A")
			{
                 originDiscount1  = round(  parseFloat(recommsel) - parseFloat(obj.value) ) ;

			}else if(originDiscountType1 == "P"){
			   originDiscount1 =  round(((parseFloat(recommsel) - parseFloat(obj.value))*100)/parseFloat(recommsel)) ;
			}
			document.getElementsByName(originDiscount)[k].value = originDiscount1;
			//alert("Given Discount---"+discountType+"********"+discountVal+"-------------"+rsrVal)
			}
		}
		}
		else{
			alert("Please Enter Numeric Values Only.");
			obj.value='';
obj.focus();
}
		}


function reverse2(obj,buyrate2,recomm,destinationtype,destinationmargin,destidistype,destidiscount,k,j)
		{
obj.value=round(obj.value);
if(!isNaN(obj.value))
{


			//alert(recomm);
			//alert(buyrate2);
			//alert(j);
			//alert(destinationmargin);
			var brVal =0.0;
			var destinationmargin1=0.0;
			var destinationtype1="";
			var destidiscount1=0.0;
			var destidistype1="";
				var recomm1 = 0.0;
//alert(document.getElementsByName(destinationtype)[k].value);
			//alert(document.getElementsByName(destinationmargin)[0].value);


			//alert(document.getElementsByName(destidiscount)[0].value);
			//alert(document.getElementsByName(destidistype)[k].value);
			
			
			if(!document.getElementsByName(destinationmargin)[k].disabled){
				///if margin given
            destinationtype1 = document.getElementsByName(destinationtype)[k].value;
			destinationmargin1 = document.getElementsByName(destinationmargin)[k].value;
			 brVal  = buyrate2;
			 if(destinationtype1 == "A")
			{
             destinationmargin1 = round(parseFloat(obj.value) - parseFloat(brVal));
             
			}else if(destinationtype1== "P"){
				destinationmargin1= round((100*(parseFloat(obj.value) - parseFloat(brVal)))/parseFloat(brVal));
			}
			document.getElementsByName(destinationmargin)[k].value = destinationmargin1;

	}

else if(!document.getElementsByName(destidiscount)[k].disabled){//if discount given
		
              destidistype1= document.getElementsByName(destidistype)[k].value;
              destidiscount1  = document.getElementsByName(destidiscount)[k].value;
			  recomm1= recomm;
			   if(destidistype1== "A")
			{
                 destidiscount1  = round(  parseFloat(recomm1) - parseFloat(obj.value) ) ;

			}else if(destidistype1 == "P"){
			   destidiscount =  round(((parseFloat(recomm1) - parseFloat(obj.value))*100)/parseFloat(recomm1)) ;
			}
			document.getElementsByName(destidiscount)[k].value = destidiscount1;
			//alert("Given Discount---"+discountType+"********"+discountVal+"-------------"+rsrVal)
			}
		}
		else
			{
			alert("Please Enter Numeric Values Only.");
obj.value='';
obj.focus();
}
		}


		






		

			//@@WPBN issue-146448-->
</script>
</HEAD>

<BODY onLoad="initialize();setAllValuesOnLoad();roundAll();setValues();">
<form method='post' action='QMSQuoteController' onsubmit='disableUnChecked();return checkMarginTest();'>
<table width="100%" cellpadding="0" cellspacing="0">
 <tr>
	<td>
		<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
		  <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
		  <%if("Modify".equalsIgnoreCase(operation)) {%>
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> FREIGHT QUOTE (<%=quoteName%>,<%=quoteStatus%>,<%=completeFlag%>)- <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td></tr></table></td>
			<%}else { %>
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td align='left'><%=shipmentMode%> FREIGHT QUOTE - <%=operation!=null?operation.toUpperCase():""%></td><td align=right><!-- Screen Id --></td></tr></table></td>
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
              <td colspan="<%=colspan%>" align=center><%=headerDOB.getOriginCountry()!=null?headerDOB.getOriginCountry().toUpperCase():""%> TO <%=headerDOB.getDestinationCountry()!=null?headerDOB.getDestinationCountry().toUpperCase():""%></td>
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
              <td colspan="<%=colspan%>" align=center>SERVICE INFORMATION</td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Agent: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getAgent()!=null?headerDOB.getAgent():""%></td>
            </tr>
			<tr class="formdata">
              <!-- @@Commented by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008 -->
			<!-- <td colspan="<%=serviceColspan%>" >Cargo Acceptance Place: </td> -->
<!-- @@ Added by subrahmanyam for the WPBN ISSUE: 150460 ON 23/12/2008 -->
			<%if("EXW".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FAS".equalsIgnoreCase(headerDOB.getIncoTerms())  || "FCA".equalsIgnoreCase(headerDOB.getIncoTerms()) || "FOB".equalsIgnoreCase(headerDOB.getIncoTerms()) )
			{%>
				<td colspan="<%=serviceColspan%>" >Place Of Acceptance: </td>
				<%}else{%>
				<td colspan="<%=serviceColspan%>" >Place Of Delivery: </td>
				<%}%>
<!-- @@ Ended by subrahmanyam for the WPBN ISSUE:150460 ON 23/12/2008 -->

			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getCargoAcceptancePlace()!=null?headerDOB.getCargoAcceptancePlace():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Origin Port: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getOriginPortName()!=null?headerDOB.getOriginPortName()+", ":""%><%=headerDOB.getOriginPortCountry()!=null?headerDOB.getOriginPortCountry().toUpperCase():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>">Destination Port: </td>
			  <td colspan="<%=infoColspan%>"><%=headerDOB.getDestPortName()!=null?headerDOB.getDestPortName()+", ":""%><%=headerDOB.getDestPortCountry()!=null?headerDOB.getDestPortCountry().toUpperCase():""%></td>
            </tr>
	<!--@@ Commented by subrahmanyam for the enhancement #148546 on  09/12/2008 -->
			<!-- <tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Routing: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getRouting()!=null?headerDOB.getRouting():""%></td>
            </tr> -->
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Commodity or Product: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getCommodity()!=null?headerDOB.getCommodity():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan%>" >Type of service quoted: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getTypeOfService()!=null?headerDOB.getTypeOfService():""%> </td>
            </tr>
			<!--@@Added by Kameswari for the WPBN issue-146448-->
			<%		for(int i=0;i<chargesSize;i++)
			{
				legCharges	   = (QuoteFreightLegSellRates)charges.get(i);


				freightCharges = legCharges.getFreightChargesList();
				chargesDOB			= (QuoteCharges)freightCharges.get(0);
				if(chargesDOB.getValidUpto()!=null)
				{
					str1					  =eSupplyDateUtility.getDisplayStringArray(chargesDOB.getValidUpto());
				}
						serviceColspan = serviceColspan-1;
if(!legCharges.isSpotRatesFlag()){
%>
			<tr class="formdata" cellspacing='0'>
            		 <td colspan="<%=serviceColspan%>" border=0 cellspacing='0' cellpadding=0>Frequency</td>
			 <td border='0' cellpadding='0' cellspacing='0' align='right'><input type="checkbox" align='right' name='frequencyCheck<%=i%>' id='frequencyCheck<%=i%>'></td>
			 <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getFrequency()!=null)?chargesDOB.getFrequency():""%></td>
            </tr>
			<tr class="formdata" cellspacing='0'>

			  <td colspan="<%=serviceColspan%>" border=0 cellspacing='0' >Carrier</td>
			 <td border='0' cellpadding='0' cellspacing='0' align='right'><input type="checkbox" align='right' name='carrierCheck<%=i%>' id='carrierCheck<%=i%>'></td>

			  <td colspan="<%=infoColspan%>"  ><%=(chargesDOB.getCarrier()!=null)?chargesDOB.getCarrier():""%></td>
            </tr>
			<tr class="formdata">
             <%if("Sea".equalsIgnoreCase(shipmentMode))
				{%>
		    <td colspan="<%=serviceColspan%>" border=0 cellspacing='0' cellpadding=0>Approximate&nbsp;Transit&nbsp;Days</td>
			<%}
			else
				{%>
                 <td colspan="<%=serviceColspan%>" border=0 cellspacing='0' cellpadding=0>Approximate&nbsp;Transit&nbsp;Time</td>
				<%}%>
			 <td border='0' cellpadding='0' cellspacing='0' align='right'><input type="checkbox" align='right' name='transitTimeCheck<%=i%>' id='transitTimeCheck<%=i%>'></td>

			   <td colspan="<%=infoColspan%>"><%=(chargesDOB.getTransitTime()!=null)?chargesDOB.getTransitTime():""%></td>
            </tr>
			
			<tr class="formdata">
				 <%if(str1!=null)
				{%>
              <td colspan="<%=serviceColspan%>" >Freight Rate Validity</td>

			  <td align='right'><input type="checkbox" name='rateValidityCheck<%=i%>' id='rateValidityCheck<%=i%>'></td>
			   <td colspan="<%=infoColspan%>"><%=(str1!=null)?str1[0]:""%></td>
			  <%}
			  else
				{%>
			  <td colspan="<%=serviceColspan+1%>" >Freight Rate Validity</td>
			  <td colspan="<%=infoColspan%>">&nbsp;</td>

			 <%}}%>
            </tr>
			<%
				}%>
			<tr class="formdata">
              <td colspan="<%=serviceColspan+1%>" >Incoterms: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getIncoTerms()!=null?headerDOB.getIncoTerms():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan+1%>" >Notes: </td>
			  <td colspan="<%=infoColspan%>" ><%=headerDOB.getNotes()!=null?headerDOB.getNotes():""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan+1%>" >Date Effective: </td>
			  <td colspan="<%=infoColspan%>" ><%=effDateStr!=null?effDateStr:""%></td>
            </tr>
			<tr class="formdata">
              <td colspan="<%=serviceColspan+1%>" >Validity of Quote: </td>
			  <td colspan="<%=infoColspan%>" ><%=validUptoStr!=null?validUptoStr:"VALID TILL FURTHER NOTICE"%></td>
            </tr>
			<!--@@WPBN issue-146448-->
<%
			if(headerDOB.getPaymentTerms()!=null && headerDOB.getPaymentTerms().trim().length()!=0)
			{
%>				<tr class="formdata">
				  <td colspan="<%=serviceColspan+1%>" >Payment Terms: </td>
				  <td colspan="<%=infoColspan%>" ><%=headerDOB.getPaymentTerms()%></td>
				</tr>
<%
			}
%>
			<tr class="formheader" cellpadding="4" align='center'>
			  <td>Select<br>
			  <input type="checkbox" name='select' onclick='selectAll()'></td>
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
				originCharges				= finalDOB.getOriginChargesList();
				originIndices				= finalDOB.getSelectedOriginChargesListIndices();
				OriginChargesSelectedIndices=finalDOB.getOriginChargesSelectedFlag();//added by subrahmanyam for 154381
				if(originCharges!=null)
					originChargesSize		= originCharges.size();
				else
					originChargesSize		= 0;

				if(originChargesSize>0)
				{
%>
					<tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align='center'>ORIGIN CHARGES</td>
					</tr>
<%
				}
%>

<%
				for(int j=0;j<originChargesSize;j++)
				{
					checkedFlag				=	false;
					chargesDOB				= (QuoteCharges)originCharges.get(j);
					originList = new ArrayList();
				    originList1 = new ArrayList();

					//@@Modified by Kameswari for enhancement
					/*if(chargesDOB.getSellChargeId()==null || "M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						originDiscDisabled	=	"disabled";
					else
						originDiscDisabled	=	"";

					if("D".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						originMarginDisabled	=	"disabled";
					else
						originMarginDisabled	=	"";*/

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
				   //@@Enhancement
					//logger.info("originMarginDisabled"+chargesDOB.getBuyChargeId());
					originChargeInfo		= chargesDOB.getChargeInfoList();
					originChargesInfoSize	= originChargeInfo.size();

					if(originIndices!=null)
					{
						for(int i=0;i<originIndices.length;i++)
						{
							checkedFlag				=	false;
							if(originIndices[i]==j&&"Y".equalsIgnoreCase(OriginChargesSelectedIndices[i]))
							{
								checkedFlag = true;
								break;
							}
						}
					}
					//logger.info("checkedFlag"+checkedFlag);
					//logger.info("chargesDOB.getSelectedFlag()"+chargesDOB.getSelectedFlag());
					if(!checkedFlag && ("Y".equalsIgnoreCase(chargesDOB.getSelectedFlag()) || "T".equalsIgnoreCase(chargesDOB.getSelectedFlag())) && isMarginTestPerformed==null)
					{
						checkedFlag = true;

					}

					if(j==0 && originChargesInfoSize>0)
					{
%>
				<tr class="formdata" cellpadding="4" align='center'>
				  <td colspan="<%=colspan%>"><B><%//=legCharges.getOrigin()+"-"+legCharges.getDestination()%></B></td>
				</tr>
<%
					}
%>

<%		            for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);

						originList.add(new Double(chargeInfo.getBuyRate()));
						originList1.add(new Double(chargeInfo.getRecOrConSellRrate()));
					}
	                for(int k=0;k<originChargesInfoSize;k++)
					{
						chargeInfo = (QuoteChargeInfo)originChargeInfo.get(k);
                        color = (checkedFlag && chargeInfo.isMarginTestFailed())?"#FF0000":"#000000";
						if(checkedFlag)
							originCheckedFlag = "checked";
						else originCheckedFlag = "";




%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%if(k==0){%><input type='checkbox' name='originChkBox' onclick="setValue(<%=j%>,'origin')" <%=originCheckedFlag%>>
						<input type='hidden' name='originChargeIndices'/>
						<input type='hidden' name='originChargeSelectedFlag'/><%}else{}%><!-- added by subrahmanyam for 154381 -->
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
					  <select class="select"  name="originMarginType<%=j%>" size="1" onChange="enableDisableMarginType(this,'originDiscount',<%=j%>,'Discount','originMargin',<%=originList%>,'Margin','origin',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>');return calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','origin',<%=k%>,0,this)" >
					   <option value="A" <%="A".equals(chargeInfo.getMarginType())?"selected":""%>>Absolute</option>
					  <option value="P" <%="P".equals(chargeInfo.getMarginType())?"selected":""%>>Percent</option>
					  </select></td>

		<!-- @@ Commented by subrahmanyam for the Enhancement 154381 on 03/02/09 -->
					<!-- <td align='center'><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getMargin()))%>" size=5 name='originMargin<%=j%>'  <%=originMarginDisabled%> onblur='calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","origin",<%=k%>,0,this)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','originDiscount',<%=j%>,'Discount')" maxlength='10'>
					  </td>
<!-- @@ Added by subrahmanyam for the Enhancement 154381 on 03/02/09 -->

				<%if("Yes".equalsIgnoreCase(MarginTest) || "<<Back".equalsIgnoreCase(submit)||"Y".equalsIgnoreCase(chargesDOB.getSelectedFlag())){

			//	int mTCount1=MarginTestCount1.intValue();
					%>
						<td align='center'><input class="text" type="text" value="<%=chargeInfo.getMargin()%>" size=5 name='originMargin<%=j%>'  <%=originMarginDisabled%> onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","origin",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",this)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','originDiscount',<%=j%>,'Discount')" maxlength='10'>

				<%}else{
					session.setAttribute("MarginTestCount",MarginTestCount);%>
					<td align='center'><input class="text" type="text" value="<%=chargeInfo.getMargin()<0?0:chargeInfo.getMargin()%>" size=5 name='originMargin<%=j%>'  <%=originMarginDisabled%> onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","origin",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",this)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','originDiscount',<%=j%>,'Discount')" maxlength='10'>
					  </td>
				<%}%>
<!-- @@ Ended by subrahmanyam for the Enhancement 154381 on 03/02/09 -->

					  <%}
%>
					<!-- <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td> -->
					<td><font color="<%=color%>"><%=new Double(chargeInfo.getRecOrConSellRrate())%></font></td><!-- @@ added for 180164 -->
					<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
						{%>
					 <td>
					  <select class="select"  name="originDiscountType<%=j%>"  size="1" disabled>
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
					  <%}
						else
						{%>
						<td>
					  <select class="select"  name="originDiscountType<%=j%>"  size="1" id='noRound' onChange="enableDisableMarginType(this,'originMargin',<%=j%>,'Margin','originDiscount',<%=originList1%>,'Discount','origin',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>');calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','origin',<%=k%>,0,this)">
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
					  <%}%>
					  <td><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size=5 name='originDiscount<%=j%>' onblur="calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','origin',<%=k%>,0,this)" <%=originDiscDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getDiscount()%>','originMargin',<%=j%>,'Margin')" maxlength='10'></td>
					  <!-- @@ Commented by subrahmanyam for 180164 -->
					  <!-- <td><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5 readOnly name='originSellRate<%=j%>'></td> -->
					  <!-- @@Added by subrahmanyam for 180164 -->

					  <!-- @@ Added by subrahmanyam for the enhancement 180161 on 31/08/09 -->
					  <!-- <td><input class="text" type="text"  value="<%=chargeInfo.getSellRate()%>" size=5 readOnly name='originSellRate<%=j%>'></td> -->
					  <!-- @@ Added by subrahmanyam for the Enhancement 180161 on 31/08/09 -->
					  <%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					  <td><input class="text" type="text" id='chargeSellrate' value="<%=round(chargeInfo.getSellRate())%>" size=5  name='originSellRate<%=j%>' onblur="reverse1(this,<%=df.format(new Double(chargeInfo.getBuyRate()))%>,<%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%>,'originDiscountType<%=j%>','originDiscount<%=j%>','originMargin<%=j%>',
					  'originMarginType<%=j%>',<%=k%>,<%=j%>)"></td>
					  <%}else{%>
					  		  <input type=hidden name="bp<%=j%>" value='<%=chargeInfo.getBreakPoint()%>'/>
					  <td><input class="text" type="text" id='chargeSellrate' value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5  name='originSellRate<%=j%>' onblur="reverse1(this,<%=df.format(new Double(chargeInfo.getBuyRate()))%>,<%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%>,'originDiscountType<%=j%>','originDiscount<%=j%>','originMargin<%=j%>',
					  'originMarginType<%=j%>',<%=k%>,<%=j%>)"></td>
					  <%}%>
					  <td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</td>
					  <input type='hidden' name='marginTestFailed' value='<%=(checkedFlag && chargeInfo.isMarginTestFailed())?"Y":"N"%>'/>
					</tr>
<%
					}
				}

			for(int i=0;i<chargesSize;i++)
			{
				legCharges	   = (QuoteFreightLegSellRates)charges.get(i);


				freightCharges = legCharges.getFreightChargesList();

				if(freightCharges!=null)
					freightChargesSize = freightCharges.size();
				else
					freightChargesSize	= 0;

				if(i==0 && freightChargesSize>0)
				{
%>
					<tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align=center>FREIGHT CHARGES</td>
					</tr>
<%
				}

				for(int j=0;j<freightChargesSize;j++)
				{
					chargesDOB			= (QuoteCharges)freightCharges.get(j);
					freightChargeInfo	= chargesDOB.getChargeInfoList();
					freightChargesInfoSize	= freightChargeInfo.size();

					//@@Modified by Kameswari for enhancement
                    rateList = new ArrayList();
				    rateList1 = new ArrayList();
					if("BR".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"SBR".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
					{
					  frtDiscDisabled	=	"disabled";
					frtMarginDisabled = "";
					}
					else  if("RSR".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"CSR".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
					{
						frtDiscDisabled	=	"";
                 	frtMarginDisabled = "disabled";
					}




					/*if(chargesDOB.getSellChargeId()==null )
						frtDiscDisabled	=	"disabled";
					else
						frtDiscDisabled	=	"";

					if(chargesDOB.getSellChargeId()!=null)
						frtMarginDisabled	=	"disabled";
					else
						frtMarginDisabled	=	"";*/
					//@@Enhancement

					if(j==0 && freightChargesInfoSize>0)
					{
%>
						<tr class="formdata" cellpadding="4" align='center'>
							<td colspan="<%=colspan%>"><B><%=legCharges.getOrigin()+"-"+legCharges.getDestination()%></B></td>
						</tr>
<%
					}
					int tempabs =0; // added by subrahmanyam for 181430 on 10-sep-09

					String tempDesc ="";
					for(int k=0;k<freightChargesInfoSize;k++)
					{


						chargeInfo = (QuoteChargeInfo)freightChargeInfo.get(k);
							  rateList.add(new Double(chargeInfo.getBuyRate()));
							  rateList1.add(new Double(chargeInfo.getRecOrConSellRrate()));
					}
						for(int k=0;k<freightChargesInfoSize;k++)
					{

						String temp="";
						if(k>0)
						{
							chargeInfo = (QuoteChargeInfo)freightChargeInfo.get(k-1);
							temp =chargeInfo.getRateDescription();

						}
						chargeInfo = (QuoteChargeInfo)freightChargeInfo.get(k);
						color	   =  chargeInfo.isMarginTestFailed()?"#FF0000":"#000000";
						//tempDesc	=


					%>
					<tr class="formdata" cellpadding="4" align=center>
					 <!--<td><%if(k==0){%><input type='checkbox' name='frtChkBox<%=i%>' checked onclick="this.checked=true;setValue(<%=j%>,'frt<%=i%>')"><%}else{}%>
						<%if(k==0){%><input type='hidden' name='frtChargeIndices<%=i%>'><%}else{}%>
					  </td>-->
					 <!-- <td><font color="<%=color%>"><%=k==0?chargesDOB.getChargeDescriptionId():""%></font></td>-->
                   	 <%
					 if(k==0)
						{
						 //
						 %><td><input type='checkbox' name='frtChkBox<%=i%>' checked onclick="this.checked=true;setValue(<%=j%>,'frt<%=i%>')">
						 <input type='hidden' name='frtChargeIndices<%=i%>'></td>
						 <td><font color="<%=color%>"><%=(chargeInfo.getRateDescription()!=null)?chargeInfo.getRateDescription():"FREIGHT RATE"%></font></td>
					 <%
						}else
						{
						 if(temp.equalsIgnoreCase(chargeInfo.getRateDescription()))
							{%>
					 <td>&nbsp;</td>
					<td>&nbsp;</td>
					 <%
						}
						 else{
							 //rateList.add(new Double(chargeInfo.getBuyRate()));
					%>
					<td>
						 <input type='checkbox' name='surChkBox<%=i%>' checked onclick="setValue(<%=j%>,'ftr<%=i%>')">
						 <input type='hidden' name='surChargeIndices<%=i%>'></td>
                       <%if(!"SBR".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					<td><font color="<%=color%>">
					<%if(!"Truck".equalsIgnoreCase(shipmentMode)){%>
					<%= toTitleCase((chargeInfo.getRateDescription()!=null)?chargeInfo.getRateDescription().substring(0,chargeInfo.getRateDescription().length()-3):"A FREIGHT RATE")%></font></td>
					 <%}else{%>
					 <%=chargeInfo.getRateDescription()%>
					 <%}
					}else{%>
                        <td><font color="<%=color%>"><%= toTitleCase((chargeInfo.getRateDescription()!=null)?chargeInfo.getRateDescription().substring(0,chargeInfo.getRateDescription().length()):"A FREIGHT RATE")%></font></td>
					<%}}
						}%>
					 <!--@@Modified by Kameswari for enhancements-->
					 <!--@@Modified by subrahmanyam for 181430-->
					  <td><font color="<%=color%>"><%=k==0?chargesDOB.getTerminalId():""%></font></td>
					  <%if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFM3")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")
                                  ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSM3"))
                              {
						  if(tempabs==1 &&  tempDesc.equalsIgnoreCase(chargeInfo.getRateDescription())){%>
				     <!-- <td>OR</td> --><td><%=chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")?"Percent":"Flat"%></td>
					 <%tempabs=0;}else{
						  if(chargeInfo.getBreakPoint().equalsIgnoreCase("CAF%")){%>
						  <td>Percent</td>
						  <%}else{%>
					   <!-- <td>ABSOLUTE</td> --><td>Flat</td>
					 <%}}%>
					 <%}
					  else  if(chargeInfo.getBreakPoint().equalsIgnoreCase("BAFMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("CAFMIN")
                               ||chargeInfo.getBreakPoint().equalsIgnoreCase("PSSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("FSMIN")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSMIN"))
                               {tempabs=1;%>
					<td>MIN</td>
                     <%tempDesc=chargeInfo.getRateDescription();}
					   else
						   if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSBASIC")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSBASIC"))
					   {%>
					   <td>BASIC</td>
					   <%}
						   else if(chargeInfo.getBreakPoint().equalsIgnoreCase("FSKG")||chargeInfo.getBreakPoint().equalsIgnoreCase("SSKG"))
                               {%>
					  <td>FLAT</td>
					  <%}
						   else if(chargeInfo.getBreakPoint().equalsIgnoreCase("CSF"))
						{%>
						<td>ABSOLUTE</td>
					  <%}
					  else if(chargeInfo.getBreakPoint().equalsIgnoreCase("SURCHARGE"))
                       {%>
					   <td>PERCENT</td>
					   <%}
					  else if (
						  chargeInfo.getBreakPoint().endsWith("CAF")||chargeInfo.getBreakPoint().endsWith("BAF")
                               ||chargeInfo.getBreakPoint().endsWith("CSF")||chargeInfo.getBreakPoint().endsWith("PSS")||chargeInfo.getBreakPoint().endsWith("caf")
                               ||chargeInfo.getBreakPoint().endsWith("baf")||chargeInfo.getBreakPoint().endsWith("csf")||chargeInfo.getBreakPoint().endsWith("pss"))
                               {
								   n= chargeInfo.getBreakPoint().length()-3;
                                   breakPoint= chargeInfo.getBreakPoint().substring(0,n);
								   %>
						<td><%=breakPoint%></td>
					  <%}
						else
						{%>
					  <td><font color="<%=color%>">
					  <%System.out.println("------"+masterDOB.getWeightBreak());
							if(!"List".equalsIgnoreCase(masterDOB.getWeightBreak()) ){%>
					  <%=chargeInfo.getBreakPoint().length()>5?toTitleCase(chargeInfo.getBreakPoint().substring(5)):toTitleCase(chargeInfo.getBreakPoint())%></font></td>
					 <%}else{%>
					  <%=chargeInfo.getBreakPoint().length()>5?chargeInfo.getBreakPoint().substring(0,4):chargeInfo.getBreakPoint()%></font></td>
					<%}}
						%>
					  <td><font color="<%=color%>"><%=chargeInfo.getCurrency()!=null?chargeInfo.getCurrency():""%></font></td>
<%					if("Y".equals(buyRatePermission))
					{
%>			  		  <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getBuyRate()))%></font></td>
					  <td>

					  <select class="select"  name="frtMargin<%=i%>Type<%=j%>" size="1" onChange="enableDisableMarginType(this,'frtDiscount<%=i%>',<%=j%>,'Discount','frtMargin<%=i%>',<%=rateList%>,'Margin','frt',<%=k%>,<%=i%>,'<%=chargesDOB.getSellBuyFlag()%>');calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','frt',<%=k%>,<%=i%>,this)" >
						  <option value="A" <%="A".equals(chargeInfo.getMarginType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getMarginType())?"selected":""%>>Percent</option>
					  </select></td>
					<%
						  if("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription()))
						{%>
					
					<%if("Yes".equalsIgnoreCase(MarginTest) || "<<Back".equalsIgnoreCase(submit)||"Y".equalsIgnoreCase(chargesDOB.getSelectedFlag())){%>

							<td align=center><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getMargin()%>" size=5 name='frtMargin<%=i%><%=j%>'  onblur="calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','frt',<%=k%>,<%=i%>,this)" <%=frtMarginDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','frtDiscount<%=i%>',<%=j%>,'Discount')" maxlength='10'></font></td>

					<%}else{%>
							<td align=center><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getMargin()<0?0:chargeInfo.getMargin()%>" size=5 name='frtMargin<%=i%><%=j%>'  onblur="calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','frt',<%=k%>,<%=i%>,this)" <%=frtMarginDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','frtDiscount<%=i%>',<%=j%>,'Discount')" maxlength='10'></font></td>
					<%}%>
<!-- @@ Ended by subrahmanyam for the Enhancement 154381 on 03/02/09 -->
					<%}
					  else
						{
             //if(count>0)//@@Commented and Modified by Kameswari for the WPBN issue-143250
            if(count>0||"Y".equalsIgnoreCase(chargesDOB.getSelectedFlag()))
							{%>
                         <!-- @@ Commented by subrahmanyam for the Enhancement 154381 on 03/02/09 -->
                         <!-- <td align=center><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getMargin()))%>" size=5 name='frtMargin<%=i%><%=j%>'  onblur="calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','frt',<%=k%>,<%=i%>,this)" <%=frtMarginDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','frtDiscount<%=i%>',<%=j%>,'Discount')"  maxlength='10'></font></td> -->
<!-- @@ Added by subrahmanyam for the Enhancement 154381 on 03/02/09 -->
<!-- THIS IS FOR BR/RSR  SURCHARGES -->
					<%if("Yes".equalsIgnoreCase(MarginTest) || "<<Back".equalsIgnoreCase(submit)||"Y".equalsIgnoreCase(chargesDOB.getSelectedFlag())){%>

								<td align=center><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getMargin()%>" size=5 name='frtMargin<%=i%><%=j%>'  onblur="calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','frt',<%=k%>,<%=i%>,this)" <%=frtMarginDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','frtDiscount<%=i%>',<%=j%>,'Discount')"  maxlength='10'></font></td>

						<%}else{%>
							<td align=center><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getMargin()<0?0:chargeInfo.getMargin()%>" size=5 name='frtMargin<%=i%><%=j%>'  onblur="calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','frt',<%=k%>,<%=i%>,this)" <%=frtMarginDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','frtDiscount<%=i%>',<%=j%>,'Discount')"  maxlength='10'></font></td>
						<%}%>
<!-- @@ Ended by subrahmanyam for the Enhancement 154381 on 03/02/09 -->
<%				         }
                          else
					      {
							  %>
                            <td align=center><font color="<%=color%>"><input class="text" type="text" value="0.0" size=5 name='frtMargin<%=i%><%=j%>'  onblur="calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','frt',<%=k%>,<%=i%>,this)" <%=frtMarginDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','frtDiscount<%=i%>',<%=j%>,'Discount')"  maxlength='10'></font></td>

							<%}
						}
					  }
%>
                       <td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td>
					 <% if("BR".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"SBR".equalsIgnoreCase(chargesDOB.getSellBuyFlag()))
					{%>
					  <td>

						<select class="select"  name="frtDiscount<%=i%>Type<%=j%>" size="1"  disabled>
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					</td>
					<%
					}
						else
						{%>
						 <td>

						<select class="select"  name="frtDiscount<%=i%>Type<%=j%>" size="1" onChange="enableDisableMarginType(this,'frtMargin<%=i%>',<%=j%>,'Margin','frtDiscount<%=i%>',<%=rateList1%>,'Discount','frt',<%=k%>,<%=i%>,'<%=chargesDOB.getSellBuyFlag()%>');calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','frt',<%=k%>,
						<%=i%>,this)" >
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					</td>
					<%}%>
					 <%
						  if("FREIGHT RATE".equalsIgnoreCase(chargeInfo.getRateDescription()))
						{%>
            <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size='5' name='frtDiscount<%=i%><%=j%>' onblur="calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','frt',<%=k%>,<%=i%>,this)" <%=frtDiscDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getDiscount()%>','frtMargin<%=i%>',<%=j%>,'Margin')" maxlength='10'></font></td>
					  <%}
					  else
						{
            //if(count>0)//@@Commented and Modified by Kameswari for the WPBN issue-143250
            if(count>0||"Y".equalsIgnoreCase(chargesDOB.getSelectedFlag()))
							{%>
              <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size='5' name='frtDiscount<%=i%><%=j%>' onblur="calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','frt',<%=k%>,<%=i%>,this)" <%=frtDiscDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getDiscount()%>','frtMargin<%=i%>',<%=j%>,'Margin')" maxlength='10'></font></td>
           <%				         }
                          else
					      {
							  %>
                           <td><font color="<%=color%>"><input class="text" type="text" value="0.0" size='5' name='frtDiscount<%=i%><%=j%>' onblur="calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','frt',<%=k%>,<%=i%>,this)" <%=frtDiscDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getDiscount()%>','frtMargin<%=i%>',<%=j%>,'Margin')" maxlength='10'></font></td>


							<%}
						  }

%>
<!-- commented for  180161 -->
<!-- <td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5 readOnly name='frtSellRate<%=i%><%=j%>'></font></td> -->
           <%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
			<td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=round(chargeInfo.getSellRate())%>" size=5  name='frtSellRate<%=i%><%=j%>' onblur="reverse(this,<%=df.format(new Double(chargeInfo.getBuyRate()))%>,'frtMargin<%=i%><%=j%>','frtMargin<%=i%>Type<%=j%>','frtDiscount<%=i%>Type<%=j%>',
			'frtDiscount<%=i%><%=j%>',<%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%>,<%=k%>)"></font></td>
			<%}else{%>
			<td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5  name='frtSellRate<%=i%><%=j%>' onblur="reverse(this,<%=df.format(new Double(chargeInfo.getBuyRate()))%>,'frtMargin<%=i%><%=j%>','frtMargin<%=i%>Type<%=j%>','frtDiscount<%=i%>Type<%=j%>',
			'frtDiscount<%=i%><%=j%>',<%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%>,<%=k%>)"></font></td>
			<%}%>
					    <td align='center'><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly>&nbsp;</font></td>
					  <input type='hidden' name='marginTestFailed' value='<%=chargeInfo.isMarginTestFailed()?"Y":"N"%>'/>
					</tr>
<%

					}
				}
			}


				destCharges		= finalDOB.getDestChargesList();
				destIndices		= finalDOB.getSelctedDestChargesListIndices();
				destChargesSelectedIndices=finalDOB.getDestChargesSelectedFlag();//@@ Added by subrahmanyam for 154381 on 14/02/09
				if(destCharges!=null)
					destChargesSize	= destCharges.size();
				else
					destChargesSize	= 0;

				if(destChargesSize>0)
				{
%>
					<tr class="formheader" cellpadding="4" >
					  <td colspan="<%=colspan%>" align='center'>DESTINATION CHARGES</td>
					</tr>
<%
				}
				for(int j=0;j<destChargesSize;j++)
				{
					destList = new ArrayList();
					destList1 = new ArrayList();
					checkedFlag				=	false;
					chargesDOB				= (QuoteCharges)destCharges.get(j);
					//@@Modified by Kameswari for enhancement
					/*if(chargesDOB.getSellChargeId()==null || "M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						destDiscDisabled	=	"disabled";
					else
						destDiscDisabled	=	"";

					if("D".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						destMarginDisabled	=	"disabled";
					else
						destMarginDisabled	=	"";*/

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
							<td colspan="<%=colspan%>"><B><%//=legCharges.getOrigin()+"-"+legCharges.getDestination()%></B></td>
						</tr>
<%
					}

					 for(int k=0;k<destChargesInfoSize;k++)
					{
						chargeInfo = (QuoteChargeInfo)destChargeInfo.get(k);

							  destList.add(new Double(chargeInfo.getBuyRate()));
							  destList1.add(new Double(chargeInfo.getRecOrConSellRrate()));
					}
					for(int k=0;k<destChargesInfoSize;k++)
					{
						chargeInfo = (QuoteChargeInfo)destChargeInfo.get(k);
						color	   =  (checkedFlag && chargeInfo.isMarginTestFailed())?"#FF0000":"#000000";
						if(checkedFlag)
							destCheckedFlag = "checked";
						else destCheckedFlag = "";


%>					  <tr class="formdata" cellpadding="4" align='center'>
					  <td><%if(k==0){%><input type='checkbox' name='destChkBox' onclick="setValue(<%=j%>,'dest')" <%=destCheckedFlag%>>
						<input type='hidden' name='destChargeIndices'/>
						<input type='hidden' name='destChargeSelectedFlag'/><%}else{}%>
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
					  <select class="select"  name="destMarginType<%=j%>" size="1"  onChange="enableDisableMarginType(this,'destDiscount',<%=j%>,'Discount','destMargin',<%=destList%>,'Margin','dest',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>');calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,'Margin','dest',<%=k%>,0,this)" >
						  <option value="A" <%="A".equals(chargeInfo.getMarginType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getMarginType())?"selected":""%>>Percent</option>
					  </select>
					  </td>
					  <!-- @@ Commented by subrahmanyam for the Enhancement 154381 on 03/02/09 -->
					  <!-- <td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getMargin()))%>" size=5 name='destMargin<%=j%>' onblur='calcSellRate(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","dest",<%=k%>,0,this)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','destDiscount',<%=j%>,'Discount')" <%=destMarginDisabled%> maxlength='10'></font></td> -->
<!-- @@ Added by subrahmanyam for the Enhancement 154381 on 03/02/09 -->
						<%if("Yes".equalsIgnoreCase(MarginTest) || "<<Back".equalsIgnoreCase(submit)||"Y".equalsIgnoreCase(chargesDOB.getSelectedFlag()))
						{
						MarginTestCount1=(Integer)session.getAttribute("MarginTestCount");
					//	int mTCount4=MarginTestCount1.intValue();

								%>

								<td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getMargin()%>" size=5 name='destMargin<%=j%>' onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","dest",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",this)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','destDiscount',<%=j%>,'Discount')" <%=destMarginDisabled%> maxlength='10'></font></td>

					  <%}else{
								session.setAttribute("MarginTestCount",MarginTestCount);%>
								<td align='center'><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getMargin()<0?0:chargeInfo.getMargin()%>" size=5 name='destMargin<%=j%>' onblur='calcSellRate1(<%=chargeInfo.getBuyRate()%>,<%=j%>,"Margin","dest",<%=k%>,0,"<%=chargesDOB.getSellBuyFlag()%>",this)' onchange="enableDisableMargin(this,'<%=chargeInfo.getMargin()%>','destDiscount',<%=j%>,'Discount')" <%=destMarginDisabled%> maxlength='10'></font></td>
					  <%}%>
<!-- @@ Ended by subrahmanyam for the Enhancement 154381 on 03/02/09 -->
<%					}
%>						<td><font color="<%=color%>"><%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%></font></td>
						<%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"BC".equalsIgnoreCase(chargesDOB.getSellBuyFlag())||"M".equalsIgnoreCase(chargesDOB.getMarginDiscountFlag()))
						{%>
						<td>
						<select class="select"  name="destDiscountType<%=j%>" size="1" disabled >
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
					  <%}
						else
						{	%>
                          <td>
						<select class="select"  name="destDiscountType<%=j%>" size="1" onChange="enableDisableMarginType(this,'destMargin',<%=j%>,'Margin','destDiscount',<%=destList1%>,'Discount','dest',<%=k%>,0,'<%=chargesDOB.getSellBuyFlag()%>');calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','dest',<%=k%>,0,this)" >
						  <option value="A" <%="A".equals(chargeInfo.getDiscountType())?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(chargeInfo.getDiscountType())?"selected":""%>>Percent</option>
						</select>
					  </td>
						<%}%>
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getDiscount()))%>" size=5 name='destDiscount<%=j%>'
					  onblur="calcSellRate(<%=chargeInfo.getRecOrConSellRrate()%>,<%=j%>,'Discount','dest',<%=k%>,0,this)" <%=destDiscDisabled%> onchange="enableDisableMargin(this,'<%=chargeInfo.getDiscount()%>','destMargin',<%=j%>,'Margin')" maxlength='10'></font></td>
					  <!-- <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5 readOnly name='destSellRate<%=j%>'></font></td> -->
					  <!-- Added for 180161 -->
					  <%if("B".equalsIgnoreCase(chargesDOB.getSellBuyFlag()) || "S".equalsIgnoreCase(chargesDOB.getSellBuyFlag())){%>
					  <td><font color="<%=color%>"><input class="text" type="text" id='Destrates' value="<%=round(chargeInfo.getSellRate())%>" size=5  name='destSellRate<%=j%>' onblur="reverse2(this,<%=df.format(new Double(chargeInfo.getBuyRate()))%>,<%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%>,'destMarginType<%=j%>','destMargin<%=j%>' ,'destDiscountType<%=j%>','destDiscount<%=j%>',<%=k%>,<%=j%>)"></font></td>
					  <%}else{%>
					  <input type=hidden name="dbpi<%=j%>" value='<%=chargeInfo.getBreakPoint()%>'/>
					  <input type=hidden name="destSellRatei<%=j%>" value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>">
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=df1.format(new Double(chargeInfo.getSellRate()))%>" size=5  name='destSellRate<%=j%>' onblur="reverse2(this,<%=df.format(new Double(chargeInfo.getBuyRate()))%>,<%=df.format(new Double(chargeInfo.getRecOrConSellRrate()))%>,'destMarginType<%=j%>','destMargin<%=j%>' ,'destDiscountType<%=j%>','destDiscount<%=j%>',<%=k%>,<%=j%>)"></font></td>
					  <%}%>
					  <!-- Ended for 180161 -->
					  <td><font color="<%=color%>"><%=chargeInfo.getBasis()!=null?chargeInfo.getBasis():""%></font></td>
					  <td><font color="<%=color%>"><input class="text" type="text" value="<%=chargeInfo.getRatio()!=null?chargeInfo.getRatio():""%>" size=5 readOnly></font></td>
					  <input type='hidden' name='marginTestFailed' value='<%=(checkedFlag && chargeInfo.isMarginTestFailed())?"Y":"N"%>'/>
					</tr>
<%
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
				  <!-- Added by Rakesh on 01-02-2011 for CR:231217 -->
				 <% if("Y".equals(request.getParameter("isEscalated"))) { %>
				 <%}  else {%>
				  <input class="input" name="submit" id='SaveExit' type="submit" value="Save & Exit" onclick='setSubmitValue(this.value)'>   <!-- Ended by Rakesh on 01-02-2011 for CR:231217-->
<%}
			if(fromWhere!=null)
			{
				if("Y".equals(request.getParameter("isEscalated")))
				{
%>
				<%}else{%>
				  <input class="input" name="submit" type="submit" value="<<Back" onclick='setSubmitValue(this.value)'>
<%
			} }
					if("Y".equals(request.getParameter("isEscalated"))) { %>
					<input class="input" name="submit"  type="submit" value="Update" onclick='setSubmitValue("Next>>")'>
<%}%>					
				  <input class="input" name="submit" type="submit" value="Margin Test" onclick='setSubmitValue(this.value)'>
<%
			if(finalDOB.getUpdatedReportDOB()!=null)
			{
%>
				<input class="input" name="submit" type="button" value="Detailed View" onclick='openDetailedView()'>
<%
			}		// Added by Gowtham for QuoteEscalted Modify Issue.
%>				<%if("Y".equals(request.getParameter("isEscalated"))) {%>
					 <input class="input" name="submit" type="button" value="Close" onclick='window.close()'>
					 <%} else {%>
				  <input class="input" name="submit" type="submit" value="Next>>" onclick='setSubmitValue(this.value)'>
				<% } if("self".equalsIgnoreCase(request.getParameter("radiobutton"))) { %>
					  <input type="hidden" name="Operation" value="Modify">
					  <%} else { %>
				  <input type="hidden" name="Operation" value="<%=operation%>">
			<%}%>
			 <input type="hidden" name="isEscalated" value=<%=request.getParameter("isEscalated")%>> <!-- Added by Gowtham -->
				  <input type="hidden" name="subOperation" value="SELLCHARGES" >
				   <input type="hidden" name="update" value="update" >
				  <input type="hidden" name="subVal">
				  <input type="hidden" name="marginTestFlag">
				  <input type="hidden" name="fromWhere" value="<%=fromWhere%>" >
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