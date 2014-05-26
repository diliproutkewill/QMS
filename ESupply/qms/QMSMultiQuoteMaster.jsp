<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSMultiQuoteMaster.jsp
Product Name		: QMS
Module Name			: Quote
Task				: Adding/Modify
Date started		: 8th Aug 2005 	
Date modified		:  
Author    			: Govind
Related Document	: CR_DHLQMS_219970

--%>

<%@page import = "java.util.ArrayList,
				  java.util.Hashtable,
				  java.util.HashMap,
				  java.util.Iterator,
				  javax.ejb.ObjectNotFoundException,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.KeyValue,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.util.ESupplyDateUtility,
				  com.qms.operations.multiquote.dob.MultiQuoteMasterDOB,
				  com.qms.operations.multiquote.dob.MultiQuoteFreightLegSellRates,
				  com.qms.operations.multiquote.dob.MultiQuoteFinalDOB"
%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!	
  private static Logger logger = null;
  private static final String FILE_NAME	=	"QMSMultiQuoteMaster.jsp"; %>

<%
  logger  = Logger.getLogger(FILE_NAME);	
MultiQuoteMasterDOB			masterDOB			=	null;
MultiQuoteFinalDOB			finalDOB			=	null;
MultiQuoteFreightLegSellRates legDOB				=	null; 
ESupplyDateUtility		eSupplyDateUtility	=	new ESupplyDateUtility();
String					operation			=	request.getParameter("Operation");
String					dateFormat			=	loginbean.getUserPreferences().getDateFormat();
String					currentDate			=	eSupplyDateUtility.getCurrentDateString(dateFormat);
String					currentTime		=	eSupplyDateUtility.getCurrentTime();
String					effDate				=	null;
String					validTo				=	eSupplyDateUtility.getFortNightDateString(dateFormat);//Added by Anil.k on 04-JAN-2011
String					createdDate			=	null;
String[]				chargeGroups		=	null;
String[]				headerFooter		=	null;
String[]				content				=	null;
String[]				level				=	null;
String[]				align				=	null;
String[]				minRate				=	null;
String[]				flatRate			=	null;

Hashtable				spotRateDetails		=	null;
ArrayList				weightBreaks		=	null;
Iterator				weightBreaksItr		=	null;
int						weightBreaksSize	=	0;
int						noOfLegs			=	0;
ArrayList				legDetails			=	null;
Hashtable				accessList			=	null;
String					disabled			=	"";
String					modfyDisabled		=	"";//Added by Anil.k

boolean					isSpotRatesFlag		=	false;
String					spotRateType		=	"";
String					legShipmentMode		=	"";
String					legServiceLevel		=	null;
String					legUOM				=	null;
String					legDensityRatio		=	null;
String                  legCurrency         =   null;
String					leg					=	"";
String					readOnly			=	"";
String					addressId			=	"";
String					createdBy			=	"";

double[]				spotRate			=	null;
ArrayList               containerBreak		= new ArrayList();//@@Added by subrahmanyam for the wpbn id:196050 on 29/Jan/10

String					custContactIds		=	"";

String                  quoteStatus         = null;
String                  completeFlag         = null;
String					cargoAccPlace		="";
String                  back = null;
String					custDate			=	null;	//Added by Rakesh K on 23-02-2011 for Issue:236359
String					custTime			=	null;	//Added by Rakesh K on 28-02-2011 for Issue:
//Added to get Attention To values in Modify Case
String					attentionSlno		= "";
String					customerId			= "";
String					contactNo			= "";
String					emailId				= "";
String					faxNo				= "";
//Ended to get Attention To values in Modify Case
try
{

	quoteStatus = request.getParameter("quoteStatus");
	completeFlag = request.getParameter("completeFlag");

	eSupplyDateUtility.setPattern(dateFormat);
	
	if(session.getAttribute("finalDOB")!=null)
		finalDOB	=	(MultiQuoteFinalDOB)session.getAttribute("finalDOB");
	if(session.getAttribute("fromBack")!= null)
		back= (String)session.getAttribute("fromBack");
    //  System.out.println(finalDOB);
	//        System.out.println(finalDOB.getLegDetails());
	/*if(session.getAttribute("addressId")!=null)
       addressId    = (String)session.getAttribute("addressId");*/
	if(finalDOB!=null)
	{
		masterDOB	=	finalDOB.getMasterDOB();
		addressId	=	""+masterDOB.getCustomerAddressId();
	   legDetails  =	finalDOB.getLegDetails();
		}
	else
		masterDOB	=	new MultiQuoteMasterDOB();

	if(legDetails!=null)
		noOfLegs    =  legDetails.size();
	else
		noOfLegs    =  1;

	if(masterDOB.getEffDate()!=null)
		effDate				=	eSupplyDateUtility.getDisplayString(masterDOB.getEffDate());

	if(masterDOB.getValidTo()!=null)
		validTo				=	eSupplyDateUtility.getDisplayString(masterDOB.getValidTo());
	
	if(masterDOB.getCreatedDate()!=null)
		createdDate			=	eSupplyDateUtility.getDisplayString(masterDOB.getCreatedDate());

	//Added by Rakesh K on 23-02-2011 for Issue:236359
	if(masterDOB.getCustDate()!=null)
		custDate			=	eSupplyDateUtility.getDisplayString(masterDOB.getCustDate());

	if(masterDOB.getCustTime()!=null)
		custTime			=	masterDOB.getCustTime();
	
	// Ended by Rakesh K on 23-02-2011 for Issue:236359

	accessList  =  (Hashtable)session.getAttribute("accessList");

	if(accessList.get("10605")==null)
		disabled="disabled";
	if("Modify".equalsIgnoreCase(operation))
		readOnly = "readOnly";
    if(masterDOB.getQuoteStatus()!=null)
	{
        if("PEN".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "PENDING".equalsIgnoreCase(quoteStatus))
              quoteStatus="PENDING";
         else if("ACC".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "POSITIVE".equalsIgnoreCase(quoteStatus) )
             quoteStatus="POSITIVE";
      else if("REJ".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "REJECTED".equalsIgnoreCase(quoteStatus))
             quoteStatus="REJECTED";
      else if("GEN".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "GENERATED".equalsIgnoreCase(quoteStatus) )
            quoteStatus="GENERATED";
      else if("APP".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "APPROVED".equalsIgnoreCase(quoteStatus) )
             quoteStatus="APPROVED";
      else if("QUE".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "ESCELATED".equalsIgnoreCase(quoteStatus))
             quoteStatus="ESCALATED";
      else if("NAC".equalsIgnoreCase(masterDOB.getQuoteStatus()) || "NEGATIVE".equalsIgnoreCase(quoteStatus))
           quoteStatus="NEGATIVE";
	}

if(finalDOB!=null && finalDOB.getFlagsDOB()!=null&&finalDOB.getFlagsDOB().getCompleteFlag()!=null)//modified by VLAKSHMI for issue 169959 on 07/05/09
	{
	if("C".equalsIgnoreCase(finalDOB.getFlagsDOB().getCompleteFlag()))
        completeFlag="COMPLETE";
   else if("I".equalsIgnoreCase(finalDOB.getFlagsDOB().getCompleteFlag()))
       completeFlag="INCOMPLETE";
	}

%>
<html>
<head>
<jsp:include page="../etrans/ETDateValidation.jsp">
<jsp:param name="format" value="<%=dateFormat%>"/>
</jsp:include>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Quote - <%=operation%></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
<script language="javascript">
var chargeGroupsArray	=	new Array();
var headerFooterArray	=	new Array();
var contentArray		=	new Array();
var levelArray			=	new Array();
var alignArray			=	new Array();
var spotRateFlags		=	new Array();
var temprownnum         =	0;
var tempZoneId           =      "";  // Added By Kishore Podili For Multi Zone Codes
var quotewith           =  '<%=masterDOB.getQuoteWith()%>';

<%
	//Added by Anil.k
	if(finalDOB!=null && finalDOB.getLegDetails()!=null && finalDOB.getLegDetails().size()!=0)
	{
		legDOB			=	(MultiQuoteFreightLegSellRates)legDetails.get(0);
		chargeGroups	=	legDOB.getChargeGroupIds();
		if(chargeGroups!=null){
		for(int i=0;i<chargeGroups.length;i++)
		{
%>
			chargeGroupsArray[chargeGroupsArray.length]	=	'<%=chargeGroups[i]!=null?chargeGroups[i]:""%>';
<%	
		}}
	}//END
	/*
	if(masterDOB.getChargeGroupIds()!=null)
	{
		chargeGroups	=	masterDOB.getChargeGroupIds();
		for(int i=0;i<chargeGroups.length;i++)
		{
						/* chargeGroupsArray[chargeGroupsArray.length]	=	'<%=chargeGroups[i-1]!=null?chargeGroups[i-1]:""%>'; -->*/
						if(masterDOB!= null)
%>
document.forms[0].laneCounter.value = 1;

<%	
		/*}
	}*/

	if(masterDOB.getHeaderFooter()!=null)
	{
		headerFooter	=	masterDOB.getHeaderFooter();
		content			=	masterDOB.getContentOnQuote();
		level			=	masterDOB.getLevels();
		align			=	masterDOB.getAlign();

System.out.println("headerFooter.length--------"+headerFooter.length);
		for(int i=1;i<=headerFooter.length;i++)
		{//Modified by Anil.k
%>
			headerFooterArray[headerFooterArray.length]	=	'<%=headerFooter[i-1]!=null?headerFooter[i-1]:"H"%>';
			contentArray[contentArray.length]			=	'<%=content[i-1]!=null?content[i-1]:""%>';
			levelArray[levelArray.length]				=	'<%=level[i-1]!=null?level[i-1]:""%>';
			alignArray[alignArray.length]				=	'<%=align[i-1]!=null?align[i-1]:"L"%>';
<%	
		}//END
	}
	for(int i=0;i<noOfLegs;i++)
	{
		if(legDetails!=null)
		{
			legDOB			=	(MultiQuoteFreightLegSellRates)legDetails.get(i);
			if(legDOB.isSpotRatesFlag())
			{
				isSpotRatesFlag = true;//Added by Anil.k for Spot Rates
%>			
				spotRateFlags[<%=i%>]	=	'Y';
<%		}
			else
			{
				isSpotRatesFlag = false;//Added by Anil.k for Spot Rates
%>			
				spotRateFlags[<%=i%>]	=	'N';
<%		}
		}
	}
%>



function setMailValues(values1)
{
  document.getElementById('userModifiedMailIds').length=0
	  var mStrTemp="";
  for(var i=0;i<values1.length;i++)
  {
		if(mStrTemp.length!=0)
		mStrTemp=mStrTemp+","+values1[i-1];
		else
		mStrTemp	=	values1[i-1];
	
  }
  document.getElementById('userModifiedMailIds').value =mStrTemp;
}
// @@ Added by kiran.v	on 17/11/2011	   for Wpbn Issue		  280268
function setWeightBreak()
	{
			var shipmentMode= document.getElementById("shipmentMode").value	;
			//alert(shipmentMode);
			if(shipmentMode=='Sea')
				document.getElementById("WeightBreak").value='Flat';
			 else
				 	document.getElementById("WeightBreak").value='Slab';
	}
 // Ended by kiran.v
function addLaneRow(obj,trid)
	{
var tableObj = document.getElementById("LaneDetails");
var rowid = document.forms[0].laneCounter.value;
rowid= Number(rowid)+1;
document.forms[0].laneCounter.value= rowid;
var rowobj   =	(tableObj).insertRow(trid.sectionRowIndex+2);
rowobj.setAttribute("className","formdata");
rowobj.setAttribute("id","tr"+rowid,0);
var cellobj  =	rowobj.insertCell(0);
//Added by Rakesh K on 21-02-2011 for Issue236319
//cellobj.setAttribute("valign","bottom");
cellobj.setAttribute("width","5%");
cellobj.setAttribute("align","MIDDLE");
cellobj.innerHTML = "<input class='input' type='button' value='<<' onclick='deleteLaneRow(this,tr"+rowid+")'> ";
cellobj  =	rowobj.insertCell(1);
cellobj.setAttribute("valign","bottom");//Modified by Rakesh K on 21-02-2011 for Issue236319
cellobj.setAttribute("width","22%");
cellobj.setAttribute("nowrap");
cellobj.innerHTML = "Org Loc:<font color='#ff0000'>*</font>Org Port:<font color='#ff0000'>*</font><br>"+//Modified by Anil.k
					"<input  class=text maxLength=16 name='originLoc' id=originLoc"+rowid+" size=5 onBlur=changeToUpper(this);chrnum(this);checkAjaxOriginDestinationValid('Origin','originLoc"+rowid+"')					 <%=readOnly%>>  "+//Modified by Anil.k
					"<input class=input type=button value=... onclick=openLocationLov('Origin',"+rowid+") 					<%="'readOnly'".equalsIgnoreCase(readOnly)?"'disabled'":""%> > "+//modified by silpa.p on 5-04-11
				    "<input  class=text maxLength=16 name=originPort id=originPort"+rowid+" size=5 					onBlur=changeToUpper(this);chrnum(this);checkAjaxOriginDestinationPortValid('Origin','originPort"+rowid+"');checkAjaxRateCheckForTheLane('Origin','originPort"+rowid+"');  <%=readOnly%>>&nbsp;"+
					"<input class=input type=button value=... onclick=openOrgPortLov("+rowid+")					<%="'readOnly'".equalsIgnoreCase(readOnly)?"'disabled'":""%>> "; 
cellobj  =	rowobj.insertCell(2);
cellobj.setAttribute("valign","bottom");
cellobj.setAttribute("width","22%");//Modified by Rakesh K on 21-02-2011 for Issue236319
cellobj.setAttribute("nowrap");
cellobj.innerHTML = "Shipper(Zipcode/Zone)<br>"+
					"<input class='text' maxLength='36' name='shipperZipCode' id='shipperZipCode"+rowid+"' size='6' onBlur='chr1(this);return validateZipCode(this);' onchange= 'populateCAP("+rowid+")'  >&nbsp;<input class='input' type='button' value='...'  id='shipperZipCodeLOV"+rowid+"' onclick=popUpWindow1('shipperZipCode',"+rowid+")>&nbsp;<input class='text' maxLength='250' name='shipperZone'id='shipperZone"+rowid+"' size='3' onBlur=\" changeToUpper(this);chr(this);checkAjaxIsValidZoneCode('shipper','shipperZone"+rowid+"','originLoc"+rowid+"'); \"  onchange=populateCAP("+rowid+") >&nbsp;<input class='input' type='button' value='...' onclick=popUpWindow1('shipperZone',"+rowid+"); onBlur=populateCAP("+rowid+"); >";
cellobj  =	rowobj.insertCell(3);
cellobj.setAttribute("valign","bottom");
cellobj.setAttribute("width","22%"); //Modified by Rakesh K on 21-02-2011 for Issue236319
cellobj.setAttribute("nowrap");
cellobj.innerHTML = "Dest&nbsp;Loc:<font color='#ff0000'>*</font>Dest&nbsp;Port:"+						
					"<font color='#ff0000'>*</font><br><input class=text maxLength=16  name=destLoc id=destLoc"+rowid+"  size=5  onBlur=changeToUpper(this);chrnum(this);checkAjaxOriginDestinationValid('Destination','destLoc"+rowid+"')		 <%=readOnly%>> "+"<input class=input type=button value=... onclick=openLocationLov('Dest',"+rowid+")<%="'readOnly'".equalsIgnoreCase(readOnly)?"'disabled'":""%>>&nbsp;&nbsp;"+
					"<input class=text maxLength=16 name=destPort id=destPort"+rowid+" size=6 onBlur=changeToUpper(this);chrnum(this);checkAjaxOriginDestinationPortValid('Destination','destPort"+rowid+"');checkAjaxRateCheckForTheLane('Destination','destPort"+rowid+"'); <%=readOnly%> > "+ 
				    "<input class=input type=button value=... onclick=openDestPortLov("+rowid+")    <%="'readOnly'".equalsIgnoreCase(readOnly)?"'disabled'":""%>>  ";
cellobj  =	rowobj.insertCell(4);
cellobj.setAttribute("valign","bottom");
cellobj.setAttribute("width","25%"); //Modified by Rakesh K on 21-02-2011 for Issue236319
cellobj.setAttribute("nowrap");
cellobj.innerHTML ="Consignee(Zipcode/Zone)<br>"+
					"<input class=text maxLength=36 name=consigneeZipCode id=consigneeZipCode"+rowid+" size=6 onBlur=chr1(this);return validateZipCode(this) onchange=populateCAP("+rowid+")>  <input class=input type=button value=...  id='consigneeZipCodeLOV"+rowid+"'  onclick=popUpWindow1('consigneeZipCode',"+rowid+") onBlur=populateCAP("+rowid+")> <input class=text maxLength=250 name=consigneeZone id=consigneeZone"+rowid+" size=3  onBlur=\"changeToUpper(this);chr(this);checkAjaxIsValidZoneCode('consignee','consigneeZone"+rowid+"','destLoc"+rowid+"');\" onchange=populateCAP("+rowid+");  >&nbsp;<input class=input type=button value=... onclick=popUpWindow1('consigneeZone',"+rowid+"); onBlur=populateCAP("+rowid+"); >";
cellobj  =	rowobj.insertCell(5);
cellobj.setAttribute("noWrap","true");
cellobj.setAttribute("width","10%");//Modified by Rakesh K on 21-02-2011 for Issue236319
//Modified by Anil.k for Spot Rates
cellobj.innerHTML ="Spot Rates: <br>"+
					"<select class='select'  id='spotRateFlag"+rowid+"' name='spotRateFlag' size='1' <%=disabled%> onchange='displaySpotrateSurchargeCount(this,"+(rowid+1)+")'>"+
					"<option value='N' <%=!isSpotRatesFlag?"'selected'":""%>>NO</option>"+
					"<option value='Y' <%=isSpotRatesFlag?"'selected'":""%>>YES</option></select>"; 
cellobj  =	rowobj.insertCell(6);
cellobj.setAttribute("valign","bottom");
cellobj.innerHTML ="<input class='input' type='button' value='>>'onclick='addLaneRow(this,tr"+rowid+")'>";

rowid = document.forms[0].laneCounter.value;
rowid= Number(rowid)+1;
document.forms[0].laneCounter.value= rowid;
 rowobj   =	(tableObj).insertRow(trid.sectionRowIndex+3);
rowobj.setAttribute("className","formdata");
rowobj.setAttribute("id","tr"+rowid,0);
cellobj  =	rowobj.insertCell(0);
cellobj.setAttribute("colSpan","2");
cellobj.setAttribute("nowrap");
cellobj.innerHTML="Incoterms:<font color='#ff0000'>*</font>"+
					"<select class='select'  name='incoTerms' id='incoTerms"+rowid+"' size='1' onchange='populateCAP("+rowid+")'>"+
	                        "<option value='CFR' >CFR</option>"+
	                        "<option value='CIF' >CIF</option>"+
	                         "<option value='CIP' >CIP</option>"+
	                         "<option value='CPT' >CPT</option>"+
	                         "<option value='DAP' >DAP</option>"+
	                         "<option value='DAT' >DAT</option>"+
	                         "<option value='DDP' >DDP</option>"+
	                         "<option value='EXW' >EXW</option>"+
	                        "<option value='FAS' >FAS</option>"+
                           "<option value='FCA' >FCA</option>"+
	                        "<option value='FOB' >FOB</option>"+
					//@@Coommented by Anusha for the WPBN ISSUE:380917 //	
						/*	"<option value='DAF' >DAF</option>"+
						   "<option value='DDU' >DDU</option>"+
							"<option value='DDU CC' >DDU CC</option>"+
							"<option value='DES' >DES</option>"+
							"<option value='DEQ' >DEQ</option>"+ */
					 // @@Coommented by Anusha for the WPBN ISSUE:380917 //
				"</select>";
cellobj  =	rowobj.insertCell(1);
cellobj.setAttribute("colSpan","2");
cellobj.setAttribute("nowrap");
cellobj.innerHTML="	Named Place :<font color='#ff0000'>*</font>"+
				  "<select class='select' valign='bottom' name='cargoAcceptance'id='cargoAcceptance"+rowid+"' onchange='populateCAP("+rowid+")'  style='width:110px;margin:0px 0 5px 0;'>"+
				  "<option value=''></option>"+
				  "<option value='ZIPCODE' >Zip Code</option>"+
				  "<option value='ZONECODE' >Zone Code</option>"+
				  "<option value='DDAO' >DGF Terminal</option>"+
				 "<option value='PORT' >Port</option>"+
				"<option value='OTHER' >Other</option>"+
				"</select> "+
				"<textarea name='cargoAccPlace' id='cargoAccPlace"+rowid+"' class='text' onBlur='return check_Length(255,this)'  cols='12' rows='4' style='height:30' rows='5'></textarea>";
cellobj  =	rowobj.insertCell(2);
cellobj.setAttribute("colSpan","2");
cellobj.setAttribute("nowrap");
cellobj.setAttribute("width","10%");
cellobj.innerHTML="<span id='spotSurcharges"+rowid+"' style='DISPLAY:none' class='formdata'>"+
		          "SpotRate Surcharges Count:<INPUT TYPE='text' class='text' NAME='spotRateSurchargeCount' id='spotRateSurchargeCount"+rowid+"' size='5' onBlur = 'setSpotSurchargeCountvalue(this,"+rowid+")'></span><INPUT TYPE='hidden'  NAME='spotRateSurchargeCount1' id='spotRateSurchargeCount1"+rowid+"' value='0' >";
cellobj  =	rowobj.insertCell(3);
cellobj.setAttribute("nowrap");
cellobj.setAttribute("width","10%");
/*cellobj.innerHTML="Carrier Id:<font color='#ff0000'>*</font><br>"+
				  "<INPUT TYPE='text' class='text' NAME='CarriedIdLOV' size='5'>  "+
				  "<input class='input' type='button' value='...' onclick='' 'disabled'>";*/
/*cellobj  =	rowobj.insertCell(3);
cellobj.innerHTML="&nbsp;&nbsp;&nbsp";
cellobj  =	rowobj.insertCell(4);
cellobj.innerHTML="&nbsp;&nbsp;&nbsp"; */


}

function deleteLaneRow(obj,trid)
{
   	var tableobj =   document.getElementById('LaneDetails');
	var rownum   =   trid.sectionRowIndex;
		
	 if(Number(tableobj.rows.length)== 2){
		 alert("U can't Delete this Row")
	 }else{
	 tableobj.deleteRow(rownum);
     tableobj.deleteRow(rownum);
	 }
	
	
   

}




//@@WPBN issue-167678
function showListLov(shipMode,legNo)
{
	if(document.getElementById("spotRateType"+legNo).value=='LIST')
	{
		if(noOfLegs == 1)
			shipMode = document.forms[0].shipmentMode.value;
		else
		{
			if(shipMode == '1')
				shipMode = "Air";
			else if(shipMode == '2')
				shipMode = "Sea";
			else if(shipMode == '4')
				shipMode = "Truck";
		}
		myUrl= '<%=request.getContextPath()%>/etrans/ListMasterMultipleLOV.jsp?listValue=&shipmentMode='+shipMode+'&legNo='+legNo;
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;
		
		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
			newWin.opener = self;
		return true;
	}
}

function checkAjaxServiceLevelValid(obj)
	{
     var servicelevel = obj.value;
	 var url ='';
	 if(servicelevel != '')
		{
		 url ='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=servicelevelcheck&servicelevel='+servicelevel+'&shipmentMode='+ document.forms[0].shipmentMode.value;
	if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }
		//alert(url)
        req.open("GET",url,true);
		req.onreadystatechange = callbackServiceLevelValid;
        req.send(null);
		}
	}

function serviceLevelValid()
	{
	if(req.readyState == 4) {
        if (req.status == 200) 
		{	  
	var response = req.responseText.toString();	
		if(response.length>0)
			alert(response)
	}
	   }
	}


function checkAjaxcommodityValid(obj)
	{
		var comodityId = obj.value;
		var url ='';
		if(comodityId != '' && document.forms[0].shipmentMode.value != '')
		{
	url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=ComodityCheck&comodityId='+comodityId+'&shipmentMode='+ document.forms[0].shipmentMode.value;
		if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }
		
        req.open("GET",url,true);
		req.onreadystatechange = callbackComodityValid;
        req.send(null);
		}
	}

function callbackComodityValid()
	{
	if(req.readyState == 4) {
        if (req.status == 200) 
		{	  
	var response = req.responseText.toString();	
		if(response.length>0)
			alert(response)
	}
	   }
	}

function checkAjaxIndustryValid(obj){
    
	var industryId = obj.value;
	var url ='';
	 if(industryId != '')
	{
 url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=IndustryCheck&industryId='+industryId;
  if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }
		//alert(url)
        req.open("GET",url,true);
		req.onreadystatechange = callbackIndustryValid;
        req.send(null);
		}
}

function callbackIndustryValid()
	{
	if(req.readyState == 4) {
        if (req.status == 200) 
		{	  
	var response = req.responseText.toString();	
		if(response.length>0)
			alert(response)
	}
	   }
	}


function checkAjaxOriginDestinationValid(locationName,id)
	{
 var rownum = (locationName=='Origin')?id.substring(9):id.substring(7);

	if(locationName !='' && document.getElementById(id).value != '')
		{
   if((locationName=='Origin' && document.getElementById("destLoc"+rownum).value!= document.getElementById(id).value) ||
	(locationName=='Destination' && document.getElementById("originLoc"+rownum).value!= document.getElementById(id).value))
			{
           var url='';
		   if(locationName  == 'Origin')
	url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=OriginDestinationValid&originLoc='+document.getElementById(id).value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&Location=Origin';
		   else
    url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=OriginDestinationValid&destLoc='+document.getElementById(id).value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&Location=Destination';
		 if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }
		//alert(url)
        req.open("GET",url,true);
		req.onreadystatechange = callbackOriginDestinationValid;
		req.send(null);
		}else{
         alert("Origin And Destination Location  Are Same ")
           document.getElementById(id).value ='';
			document.getElementById(id).focus();
		}
		}else{
          document.getElementById(id).value ='';
		}
}
	function callbackOriginDestinationValid()
	{
       if (req.readyState == 4) {
        if (req.status == 200) 
		{	  
		var response = req.responseText.toString();	
		if(response.length>0)
			alert(response)
	}
	   }
	}

 function checkAjaxRateCheckForTheLane(locationName,id)
	{
	 
	var shipMode ='';
	var operation = '<%=operation%>';
	
	if(document.forms[0].shipmentMode.value=='Air')
		{
			shipMode = '1';
			
		}
		else if(document.forms[0].shipmentMode.value=='Sea')
		{
			shipMode = '2';
			
		}
		else
		{
			shipMode = '4';
			
		}
   var rownum = (locationName=='Origin')?id.substring(10):id.substring(8);//Port
     temprownnum  = rownum;
	
	 var spotRateValue = document.getElementById("spotRateFlag"+temprownnum).selectedIndex;//Added by Anil.k for Spot Rates	 
	
    if(locationName !='' && document.getElementById(id).value != '' && document.getElementById("originLoc"+rownum).value != '')
		{
		
	if(((locationName=='Origin' && document.getElementById("destPort"+rownum).value!='' && document.getElementById(id).value!= '') ||
   (locationName=='Destination' && document.getElementById("originPort"+rownum).value!= '' && document.getElementById(id).value!= ''))
		&& spotRateValue!=1)//Modified by Anil.k for Spot Rates
			{
           var url='';
		   if(locationName  == 'Origin'){
	url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=RateCheck&origin='+document.getElementById(id).value+'&shipmentmode='+shipMode+'&Location=Origin&destination='+document.getElementById("destPort"+rownum).value+'&weightBreak='+(document.forms[0].WeightBreak.value).toUpperCase()+'&dummytime='+((new Date()).getTime());
		   }
		   else{
    url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=RateCheck&destination='+document.getElementById(id).value+'&shipmentmode='+shipMode+'&Location=Destination&origin='+document.getElementById("originPort"+rownum).value+'&weightBreak='+(document.forms[0].WeightBreak.value).toUpperCase()+'&dummytime='+((new Date()).getTime());
		   }
		  // alert(url)
		 if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }

        req.open("GET",url,true);
		req.onreadystatechange = callbackOriginDestinationRateCheck;
		req.send(null);
		}else if("Copy"==operation && spotRateValue==1){
            document.getElementById("spotSurcharges"+temprownnum).style.display = 'block';
		}

		}else{
		   alert("Enter"+locationName+" Location Name")
          document.getElementById(id).value ='';
		}
}
	function callbackOriginDestinationRateCheck()
	{
	  var rownum = Number(temprownnum)+1;
	  var operation = '<%=operation%>';
       if (req.readyState == 4) {
        if (req.status == 200) 
		{	  
		var response = req.responseText.toString();	
		if(response.length>0){
			   //alert(response);
			   var confirmV = confirm(response+" Do you Want Spot Rates?");	//Added by Anil.k for Spot Rates
			   if(confirmV){
				  
				   document.getElementById("spotRateFlag"+temprownnum).selectedIndex = 1;
                    document.getElementById("spotSurcharges"+rownum).style.display = 'block';
			   }
			   else
			   {//Ended by Anil.k for Spot Rates
			 //  if( document.getElementById("spotRateFlag"+temprownnum).selectedIndex)
			//alert(document.getElementById("spotRateFlag"+temprownnum).selectedIndex+"-------"+operation+"888"+rownum)
			  if("Copy"==operation && document.getElementById("spotRateFlag"+temprownnum).selectedIndex!=1){
				 
                   document.getElementById("spotSurcharges"+rownum).style.display ='none';
			  }else{
               document.getElementById("destPort"+temprownnum).value='';
			   document.getElementById("originPort"+temprownnum).value='';
			   document.getElementById("destLoc"+temprownnum).value='';
			   document.getElementById("originLoc"+temprownnum).value='';
			  }
			   }//Added by Anil.k for Spot Rates

			}else if("Copy"==operation && document.getElementById("spotRateFlag"+temprownnum).selectedIndex!=1){
		  
             document.getElementById("spotSurcharges"+temprownnum).style.display ='none';
			}
	}
	   }
	}


function checkAjaxOriginDestinationPortValid(locationName,id)
	{
	
   var rownum = (locationName=='Origin')?id.substring(10):id.substring(8);
   
	if(locationName !='' && document.getElementById(id).value != '')
		{
	if((locationName=='Origin' && document.getElementById("destPort"+rownum).value!= document.getElementById(id).value) ||
	(locationName=='Destination' && document.getElementById("originPort"+rownum).value!= document.getElementById(id).value))
			{
           var url='';
		   if(locationName  == 'Origin'){
	url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=OriginDestinationportValid&originPort='+document.getElementById(id).value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&Location=Origin';
		   }
		   else{
    url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=OriginDestinationPortValid&destPort='+document.getElementById(id).value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&Location=Destination';
		   }
		 if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }

        req.open("GET",url,true);
		req.onreadystatechange = callbackOriginDestinationPortValid;
		req.send(null);
		}else{
			alert("Origin And Destination Port's Are Same ")
           document.getElementById(id).value ='';
			document.getElementById(id).focus();
		}

		}else{
          document.getElementById(id).value ='';
		}
}
	function callbackOriginDestinationPortValid()
	{
       if (req.readyState == 4) {
        if (req.status == 200) 
		{	  
		var response = req.responseText.toString();	
		if(response.length>0)
			alert(response)
	}
	   }
	}

function checkAjaxSalespersonValid(obj){
	var salesPersonId  = obj.value;
	var servicelevel  = (document.forms[0].serviceLevelId.value!='')?document.forms[0].serviceLevelId.value:"";

         if(salesPersonId !='') {
            var url=
		'<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=SalesPersonValid&salesPersonId='+salesPersonId+'&shipmentMode='+document.forms[0].shipmentMode.value+'&servicelevel='+servicelevel;
		 if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }
		//alert(url)
        req.open("GET",url,true);
		req.onreadystatechange = callbackSalesPerson;
		req.send(null);
		}else{
          document.forms[0].salesPersonCode.value ='';
		}
    }
	function callbackSalesPerson() {
    if (req.readyState == 4) {
        if (req.status == 200) 
		{	  
		var response = req.responseText.toString();	 
        if(response != 'Valid')
			alert(response)
		}
	}
}
        

function getCustAddressByAjax(obj) {
	    var custid = obj.value;
		if(custid != '' ){
	   var url ='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=CustAdd&customerId='+custid+'&dummytime='+((new Date()).getTime());;
		 if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        } 
        req.open("GET",url,true);
		req.onreadystatechange = callback;
		req.send(null);
		}else{
          document.forms[0].address.value ='';
		}
    }
function callback() {
    if (req.readyState == 4) {
        if (req.status == 200) 
		{
			var response = req.responseText.toString();
			var add  = response.split("$");
			var address ="";
			for(i=0;i<add.length;i++){
              if(i!=0)
				address += ((add[i]!='' && add[i]!= "undefined")?add[i]:'');
			    
			}
			
			document.forms[0].address.value = address;
			document.forms[0].addressId.value = add[0];
		
        }
    }
}

//Added on 21Feb2011 for getting Attention To values through Ajax Call
function getCustAttentionTo(obj) {
	    
		var len = document.forms[0].contactPersons.options.length;			
		 for (i=len-1; i>=0; i--) {
				document.forms[0].contactPersons.remove(i);  }
          document.forms[0].attentionSlno.value="";
		  document.forms[0].attentionCustomerId.value="";
	    var custid = obj.value;
		if(custid != '' ){
	   var url ='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=CustAtt&customerId='+custid+'&address='+document.forms[0].address.value+'&dummytime='+((new Date()).getTime());;
		 if (window.XMLHttpRequest) 
		{
            req1 = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req1 = new ActiveXObject("Microsoft.XMLHTTP");
        } 
        req1.open("GET",url,true);
		req1.onreadystatechange = callback1;
		req1.send(null);
		}else{
			var len = document.forms[0].contactPersons.options.length;			
		 for (i=len-1; i>=0; i--) {
				document.forms[0].contactPersons.remove(i);  }
		}
    }
function callback1() {
    if (req1.readyState == 4) {
        if (req1.status == 200) 
		{			
			var response = req1.responseText.toString();
			//var att  = response.split("$");		
			var att  = response.split(",");
			var attention ="";

			for(i=0;i<att.length-1;i++){
              /*if(i!=0)
				address += ((add[i]!='' && add[i]!= "undefined")?add[i]:'');*/
				var atten  = att[i].split("$");				
				//if(att[0]!='' && att[0] != "Customer is Invalid Or Does not exist.")
				//document.forms[0].contactPersons.options[i]=new Option(att[i],att[i],true,true);
				if(atten[0]!='' && atten[0] != "Customer is Invalid Or Does not exist."){
				document.forms[0].contactPersons.options[i]=new Option(atten[0],atten[0],true,true);	
				//Modified for Back Button With Freight
				if(document.forms[0].contactIds.value!="")
					document.forms[0].contactIds.value = document.forms[0].contactIds.value+","+(atten[5]!=""?atten[5]:"-");
				else
					document.forms[0].contactIds.value = atten[5]!=""?atten[5]:"-";

				if(document.forms[0].attentionCustomerId.value!="")
					document.forms[0].attentionCustomerId.value = document.forms[0].attentionCustomerId.value+","+(atten[4]!=""?atten[4]:"-");
				else
					document.forms[0].attentionCustomerId.value = atten[4]!=""?atten[4]:"-";

				if(document.forms[0].attentionSlno.value!=""){
					document.forms[0].attentionSlno.value = document.forms[0].attentionSlno.value+","+(atten[5]!=""?atten[5]:"-");
				}
				else
					{
					document.forms[0].attentionSlno.value = atten[5]!=""?atten[5]:"-";
					}

				if(document.forms[0].attentionEmailId.value!="")
					document.forms[0].attentionEmailId.value = document.forms[0].attentionEmailId.value+","+(atten[1]!=""?atten[1]:"-");
				else
					document.forms[0].attentionEmailId.value = atten[1]!=""?atten[1]:"-";

				if(document.forms[0].attentionFaxNo.value!="")
					document.forms[0].attentionFaxNo.value = document.forms[0].attentionFaxNo.value+","+(atten[3]!=""?atten[3]:"-");
				else
					document.forms[0].attentionFaxNo.value = atten[3]!=""?atten[3]:"-";

				if(document.forms[0].attentionContactNo.value!="")
					document.forms[0].attentionContactNo.value = document.forms[0].attentionContactNo.value+","+(atten[2]!=""?atten[2]:"-");
				else
					document.forms[0].attentionContactNo.value = atten[2]!=""?atten[2]:"-";
				//Ended Modification for Back Button With Freight
				}
			}			
			//document.forms[0].address.value = address;
			//document.forms[0].addressId.value = add[0];
		
        }
    }
}//Ended Ajax call for Attention To 
 //Added by Rakesh
function getSalesPerson(obj) {
	    var custid = obj.value;
		if(custid != '' ){
	   var url ='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=SalesPerson1&customerId='+custid+'&dummytime='+((new Date()).getTime());;
		 if (window.XMLHttpRequest) 
		{
            req2 = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req2 = new ActiveXObject("Microsoft.XMLHTTP");
        } 
        req2.open("GET",url,true);
		req2.onreadystatechange = callback3;
		req2.send(null);
		}else{
          document.forms[0].salesPersonCode.value ='';
		}
    }
function callback3() {
    if (req2.readyState == 4) {
        if (req2.status == 200) 
		{
			var response = req2.responseText.toString();
			if(response=="Customer is Invalid Or Does not exist.")
			{
			  alert(response);
			}
			else
			{
			   	 if(response.length>0)
				 document.forms[0].salesPersonCode.value=response;
				 else
        		 document.forms[0].salesPersonCode.value=document.forms[0].createdBy.value;

        }
    }
}
}

  //Ended by Rakesh
//@@ Added By govind for the Multilane and Multi carrier
function popUpWindow1(input,rownum)
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
	var consoltype = '';
	if( document.forms[0].shipmentMode.value.length !=0 && document.forms[0].shipmentMode.value == 'Sea')
		{
         consoltype = (document.forms[0].WeightBreak.value=='List')?'FCL':'LCL';
		}
	
	Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=no';
	Options='width=400,height=300,resizable=no';
	 if(input=='shipperZipCode')
	{
		if(document.getElementById('originLoc'+rownum).value=='')
		{
			alert("Please enter the Origin Location ");
			document.getElementById('originLoc'+rownum).focus();
			return false;
		}
		if(document.forms[0].shipmentMode.value.length==0)
		{
			alert("Please Select a shipmentMode.");
			document.forms[0].shipmentMode.focus();
			return false;
		}
		if(document.getElementById('shipperZipCode'+rownum).value.length==0)
		{
			if(document.getElementById('shipperZipCode'+rownum).disabled==false) {
			alert("Please enter the Shipper Zip Code");
			document.getElementById('shipperZipCode'+rownum).focus();
			return false;
			 }else{

				 alert('Zip Code as Cargo Acceptence Place is not possible for MultiZones');
				 document.getElementById('cargoAcceptance'+rownum).options.value='';
				 document.getElementById('cargoAccPlace'+rownum).value = '';
					return false;
			 
			 }
		}
		Url='etrans/QMSMultiQuoteLOVZoneCodes.jsp?searchString1='+document.getElementById('originLoc'+rownum).value+'&searchString2='+document.getElementById('shipperZipCode'+rownum).value+'&wheretoset=shipperZipCode&shipmentMode='+document.forms[0].shipmentMode.value+'&consoleType='+consoltype+'&rownum='+rownum+'&MultiQuote=Y';
	}else if(input=='consigneeZipCode')
	{
		if(document.getElementById('destLoc'+rownum).value=='')
		{
			alert("Please enter the destination Location");
			document.getElementById('destLoc'+rownum).focus();
			return false;
		}
		if(document.forms[0].shipmentMode.value.length==0)
		{
			alert("Please Select a shipmentMode Mode.");
			document.forms[0].shipmentMode.focus();
			return false;
		}
		if(document.getElementById('consigneeZipCode'+rownum).value.length==0)
		{
			if(document.getElementById('consigneeZipCode'+rownum).disabled==false) {
			alert("Please enter the Consignee Zip Code");
			document.getElementById('consigneeZipCode'+rownum).focus();
			return false;
			}else{
						 alert('Zip Code as Cargo Acceptence Place is not possible for MultiZones');
						 document.getElementById('cargoAcceptance'+rownum).options.value='';
						 document.getElementById('cargoAccPlace'+rownum).value = '';
					return false;
			
			}
		}		Url='etrans/QMSMultiQuoteLOVZoneCodes.jsp?searchString1='+document.getElementById('destLoc'+rownum).value+'&searchString2='+document.getElementById('consigneeZipCode'+rownum).value+'&wheretoset=consigneeZipCode&shipmentMode='+document.forms[0].shipmentMode.value+'&consoleType='+consoltype+'&rownum='+rownum+'&MultiQuote=Y';
		}else if(input=='shipperZone')
		{
		if(document.getElementById('originLoc'+rownum).value.length==0)
		{
			alert('Please Enter the OriginLocation Id');
			document.getElementById('originLoc'+rownum).focus();
			return false;
		}
		if(document.forms[0].shipmentMode.value.length==0)
		{
			alert("Please Select a shipmentMode Mode.");
			document.forms[0].shipmentMode.focus();
			return false;
		}
		var Options='scrollbar=yes,width=700,height=300,resizable=no';
	
		var searchString  = document.getElementById('originLoc'+rownum).value;
		var searchString2 = document.getElementById('shipperZipCode'+rownum).value;
		var searchString3 = document.getElementById('shipperZone'+rownum).value;//Added by RajKumari on 11/10/2008 for WPBN 143511...
		var Url='etrans/QMSMultiQuoteLOVZoneCodesMultiple.jsp?searchString1='+searchString+'&searchString2='+searchString2+'&searchString3='+searchString3+'&wheretoset=shipperZone&fromWhere=Quote&shipmentMode='+document.forms[0].shipmentMode.value+'&consoleType='+consoltype+'&rownum='+rownum+'&MultiQuote=Y';
	}
	else if(input=='consigneeZone')
	{
		if(document.getElementById('destLoc'+rownum).value.length==0)
		{
			alert('Please Enter the Destination Location Id');
			document.getElementById('destLoc'+rownum).focus();
			return false;
		}
		if(document.forms[0].shipmentMode.value.length==0)
		{
			alert("Please Select a Consignee Mode.");
			document.forms[0].shipmentMode.focus();
			return false;
		}
		var Options='scrollbar=yes,width=700,height=300,resizable=no';
	
		var searchString  = document.getElementById('destLoc'+rownum).value;
		var searchString2 = document.getElementById('consigneeZipCode'+rownum).value;
		var searchString3 = document.getElementById('consigneeZone'+rownum).value;//Added by RajKumari on 11/10/2008 for WPBN 143511...
		var Url='etrans/QMSMultiQuoteLOVZoneCodesMultiple.jsp?searchString1='+searchString+'&searchString2='+searchString2+'&searchString3='+searchString3+'&wheretoset=consigneeZone&fromWhere=Quote&shipmentMode='+document.forms[0].shipmentMode.value+'&consoleType='+consoltype+'&rownum='+rownum+'&MultiQuote=Y';
	}
	else if(input == "shipperZipCodeChg")//Added by Anil.k for ZipCodes Quote With Charges on 1Mar2011
	{
		if(document.getElementById('originLocCharge').value=='')
		{
			alert("Please enter the Origin Location ");
			document.getElementById('originLocCharge').focus();
			return false;
		}
		if(document.forms[0].shipmentMode.value.length==0)
		{
			alert("Please Select a shipmentMode.");
			document.forms[0].shipmentMode.focus();
			return false;
		}
		if(document.getElementById('shipperZipCodeChg').value=='')
		{
			alert("Please enter the Shipper Zip Code");
			document.getElementById('shipperZipCodeChg').focus();
			return false;
		}
		Url='etrans/QMSMultiQuoteLOVZoneCodes.jsp?searchString1='+document.getElementById('originLocCharge').value+'&searchString2='+document.getElementById('shipperZipCodeChg').value+'&wheretoset=shipperZipCode&shipmentMode='+document.forms[0].shipmentMode.value+'&consoleType='+consoltype+'&rownum=Chg'+'&MultiQuote=Y';
	}
	else if(input == "consigneeZipCodeChg")
	{
		if(document.getElementById('destLocCharge').value=='')
		{
			alert("Please enter the destination Location");
			document.getElementById('destLocCharge').focus();
			return false;
		}
		if(document.forms[0].shipmentMode.value.length==0)
		{
			alert("Please Select a shipmentMode Mode.");
			document.forms[0].shipmentMode.focus();
			return false;
		}
		if(document.getElementById('consigneeZipCodeChg').value=='')
		{
			alert("Please enter the Consignee Zip Code");
			document.getElementById('consigneeZipCodeChg').focus();
			return false;
		}		Url='etrans/QMSMultiQuoteLOVZoneCodes.jsp?searchString1='+document.getElementById('destLocCharge').value+'&searchString2='+document.getElementById('consigneeZipCodeChg').value+'&wheretoset=consigneeZipCode&shipmentMode='+document.forms[0].shipmentMode.value+'&consoleType='+consoltype+'&rownum=Chg'+'&MultiQuote=Y';
	}
	else if(input=='shipperZoneChg')
	{
		if(document.getElementById('originLocCharge').value.length==0)
		{
			alert('Please Enter the OriginLocation Id');
			document.getElementById('originLocCharge').focus();
			return false;
		}
		if(document.forms[0].shipmentMode.value.length==0)
		{
			alert("Please Select a shipmentMode Mode.");
			document.forms[0].shipmentMode.focus();
			return false;
		}
		var Options='scrollbar=yes,width=700,height=300,resizable=no';

		var searchString  = document.getElementById('originLocCharge').value;
		var searchString2 = document.getElementById('shipperZipCodeChg').value;
		var searchString3 = document.getElementById('shipperZoneChg').value;//Added by RajKumari on 11/10/2008 for WPBN 143511...
		var Url='etrans/QMSMultiQuoteLOVZoneCodesMultiple.jsp?searchString1='+searchString+'&searchString2='+searchString2+'&searchString3='+searchString3+'&wheretoset=shipperZone&fromWhere=Quote&shipmentMode='+document.forms[0].shipmentMode.value+'&consoleType='+consoltype+'&rownum=Chg'+'&MultiQuote=Y';
	}
	else if(input=='consigneeZoneChg')
	{
		if(document.getElementById('destLocCharge').value.length==0)
		{
			alert('Please Enter the Destination Location Id');
			document.getElementById('destLocCharge').focus();
			return false;
		}
		if(document.forms[0].shipmentMode.value.length==0)
		{
			alert("Please Select a Consignee Mode.");
			document.forms[0].shipmentMode.focus();
			return false;
		}
		var Options='scrollbar=yes,width=700,height=300,resizable=no';

		var searchString  = document.getElementById('destLocCharge').value;
		var searchString2 = document.getElementById('consigneeZipCodeChg').value;
		var searchString3 = document.getElementById('consigneeZoneChg').value;//Added by RajKumari on 11/10/2008 for WPBN 143511...
		var Url='etrans/QMSMultiQuoteLOVZoneCodesMultiple.jsp?searchString1='+searchString+'&searchString2='+searchString2+'&searchString3='+searchString3+'&wheretoset=consigneeZone&fromWhere=Quote&shipmentMode='+document.forms[0].shipmentMode.value+'&consoleType='+consoltype+'&rownum=Chg'+'&MultiQuote=Y';
	}
	//Ended by Anil.k on 1Mar2011

	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features); 

	}









function popUpWindow(input)
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
	Options='width=400,height=300,resizable=yes';
	
	if(input=='preQuoteId')
	{
		if(document.forms[0].customerId.value=='')
		{
			alert("Please enter the Customer Id.");
			document.forms[0].customerId.focus();
			return false;
		}
		Url='etrans/QMSQuoteIdsLOV.jsp?customerId='+document.forms[0].customerId.value+'&searchString='+document.forms[0].preQuoteId.value+'&whereToSet='+input;
	}
	else if(input=='customerLov')
	{
		Url='ETAdvancedLOVController?entity=Customer&formfield=customerId&operation=Add&mode=1&type=single&terminalId='+terminalId+'&fromWhere=Quote';
		Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
		Options='width=820,height=600,resizable=yes';
	}
	else if(input=='newCustomerLov')
	{
<%
		if(("OPER_TERMINAL").equalsIgnoreCase(loginbean.getAccessType()))
		{
%>
			Url='etrans/ETCustomerRegistrationAdd.jsp?Operation=Add&Customer=NCCS&registrationLevel=T&fromWhat=Quote';
			Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
			Options='width=820,height=700,resizable=no';
<%
		}
		else
		{
%>
			Url='etrans/ETCustomerRegistrationAdd.jsp?Operation=Add&Customer=CCS&registrationLevel=C&fromWhat=Quote';
			Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
			Options='width=820,height=700,resizable=no';
<%
		}
%>
			

	}
	else if(input=='addrLov')
	{
		if(document.forms[0].customerId.value=='')
		{
			alert("Please enter the Customer Id");
			document.forms[0].customerId.focus();
			return false;
		}
        Url='etrans/ETCustAddressLOV.jsp?searchString='+document.forms[0].customerId.value+'&flag=Quote&addrType=';
	}
	else if(input=='salesPerson')
	{
		Url='ESACUserIdsLOV.jsp?filterString='+document.forms[0].salesPersonCode.value+'&fromWhat=Quote&accessType=repOfficer&locationId=<%=loginbean.getTerminalId()%>';
	}
	else if(input=='industry')
	{
		Url='etrans/QMSIndustryIdLOV.jsp?Operation=Modify&searchString='+document.forms[0].industryId.value+'&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>';
	}
	else if(input=='commodity')
	{
		if(document.forms[0].commodityType.selectedIndex==0){
			alert("Please Select Commodity Type");
			document.forms[0].commodityType.focus();
			return false;
		}
	
    var commodityType = document.forms[0].commodityType[document.forms[0].commodityType.selectedIndex].value;
	commodityType     = commodityType.replace('&',';amp') ;
   Url='etrans/ETCLOVCommodityIds.jsp?searchString='+document.forms[0].commodityId.value+'&commodityType='+document.forms[0].commodityType[document.forms[0].commodityType.selectedIndex].value;
	}
	else if(input=='serviceLevel')
	{
		Url='etrans/ETCLOVServiceLevelIds.jsp?searchString='+document.forms[0].serviceLevelId.value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>';
	}
	else if(input=='quotingStation')
	{
		Url='etrans/ETCLOVTerminalIds.jsp?searchString='+document.forms[0].quotingStation.value+'&wheretoset='+input+'&shipmentMode='+document.forms[0].shipmentMode.value;
	}
	else if(input=='originLoc')
	{
		Url='etrans/ETCLOVLocationIds.jsp?searchString='+document.forms[0].originLoc.value+'&wheretoset='+input+'&from=Quote&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&shipmentMode='+document.forms[0].shipmentMode.value;//added by rk
	}
	else if(input=='destLoc')
	{
		Url='etrans/ETCLOVLocationIds.jsp?searchString='+document.forms[0].destLoc.value+'&wheretoset='+input+'&from=Quote&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&shipmentMode='+document.forms[0].shipmentMode.value;//added by rk
	}
	
	
	else if(input=='originPort')
	{
		if(document.forms[0].shipmentMode.value=='Sea')
			Url='etrans/sea/ETSLOVPortIds.jsp?searchStr='+document.forms[0].originPort.value+'&whereToSet='+input+'&fromWhere=Quote';
		else
			Url='etrans/ETCLOVLocationIds.jsp?searchString='+document.forms[0].originPort.value+'&wheretoset='+input+'&from=Quote&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&shipmentMode='+document.forms[0].shipmentMode.value;
	}
	else if(input=='destPort')
	{
		if(document.forms[0].shipmentMode.value=='Sea')
			Url='etrans/sea/ETSLOVPortIds.jsp?searchStr='+document.forms[0].destPort.value+'&whereToSet='+input;
		else
			Url='etrans/ETCLOVLocationIds.jsp?searchString='+document.forms[0].destPort.value+'&wheretoset='+input+'&from=Quote&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&shipmentMode='+document.forms[0].shipmentMode.value;
	}
	
	else if(input=='newAddrLov')
	{
		if(document.forms[0].customerId.value=='')
		{
			alert("Please enter the Customer Id");
			document.forms[0].customerId.focus();
			return false;
		}
		
		Url='etrans/NewAddress.jsp?custId='+document.forms[0].customerId.value;
		Options='width=750,height=400,resizable=no';
		
	}
	else if(input=='newContactPerson')
	{
		if(document.forms[0].customerId.value=='')
		{
			alert("Please enter the Customer Id");
			document.forms[0].customerId.focus();
			return false;
		}
		Url='etrans/NewContactDetails.jsp?custId='+document.forms[0].customerId.value;
		Options='width=810,height=450,resizable=no';

	}
	else if(input=='contactLov')
	{
		if(document.forms[0].customerId.value=='')
		{
			alert("Please enter the Customer Id");
			document.forms[0].customerId.focus();
			return false;
		}
		
		//commented and modified by phani sekhar for wpbn 167678
	//Url='etrans/ContactLOV.jsp?custId='+document.forms[0].customerId.value+'&flag=Quote&address='+document.forms[0].checkFlag.value;
	//	Options='width=400,height=300,resizable=no';
Url='etrans/AttentionToLOV.jsp?custId='+document.forms[0].customerId.value+'&flag=Quote&address='+document.forms[0].checkFlag.value+'&attentionCustomerId='+document.forms[0].attentionCustomerId.value+'&attentionSlno='+document.forms[0].attentionSlno.value+'&attentionEmailId='+document.forms[0].attentionEmailId.value+'&attentionFaxNo='+document.forms[0].attentionFaxNo.value+'&attentionContactNo='+document.forms[0].attentionContactNo.value;
		Options='width=700,height=400,resizable=yes';
	}else if(input.name=='contentLOV')
	{
		btnId = input.id.substring(input.name.length);
		var searchString=document.getElementById("content"+btnId).value;
		var shipMode	= '';
		var shipModeStr = '';

		if(document.forms[0].shipmentMode.value=='Air')
		{
			shipMode = '1';
			shipModeStr='1,3,5,7';
		}
		else if(document.forms[0].shipmentMode.value=='Sea')
		{
			shipMode = '2';
			shipModeStr='2,3,6,7';
		}
		else
		{
			shipMode = '4';
			shipModeStr='4,5,6,7';
		}
		//searchString  = document.forms[0].content.value;

		Url='QMSLOVContentIds.jsp?opeartion=QUOTE&searchString='+searchString+'&shipmentMode='+shipMode+'&shipModeStr='+shipModeStr+'&whereToSet=content'+btnId;
	}
	else if(input.name=='chargeGroupIdBt')//Added by Anil.k on 04-JAN-2011
	{
		btnId = input.id.substring(input.name.length);
		searchString=document.getElementById("chargeGroupId"+btnId).value;
		var originLocCharge = document.forms[0].originLocCharge.value;//Added by Anil.k for Enhancement 231214 on 25Jan2011		
		var destLocCharge	= document.forms[0].destLocCharge.value;//Added by Anil.k for Enhancement 231214 25Jan2011
		Url='etrans/QMSLOVChargeGroupIds.jsp?Operation=Modify&searchString='+searchString+'&name=chargeGroupId'+btnId+'&terminalId='+terminalId+'&shipmentMode='+document.forms[0].shipmentMode.value+'&accessLevel='+accsLvl+'&fromWhere=MultiQuote&originLocation='+originLocCharge+'&destLocation='+destLocCharge;//Modified by Anil.k for Enhancement 231214 on 25Jan2011
	}
	else if(input.name=='chargeGroupIdDet')
	{
		Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=yes';
		btnId = input.id.substring(input.name.length);
		searchString=document.getElementById("chargeGroupId"+btnId).value;
		if(document.getElementById("chargeGroupId"+btnId).value=='')
		{
			alert("Please enter Charge Group");
			document.getElementById("chargeGroupId"+btnId).focus();
			return false;
		}
		Url='etrans/QMSChargesGroupingMaster.jsp?Operation=View&chargeGroupId='+searchString+"&fromWhere=Quote";
		Options='width=800,height=600,resizable=no';
	}
	//END
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features); 
}

//@@Added by kameswari for the WPBN issue 30908
function pop(index)
{
	  
	   var currency=document.getElementById("currencyId"+index).value;
	 	myUrl= 'etrans/ETCCurrencyConversionAddLOV.jsp?searchString='+currency+'&currencyId=currencyId'+index+'&fromWhere=quote&Operation=Add';
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;

		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
		newWin.opener = self;
		return true;
       
}
//@@WPBN-30908

function openLink(url)
{
	if(document.forms[0].preQuoteId.value.length==0)
	{
		alert('Please Enter Quote Id');
		document.forms[0].preQuoteId.focus();
		return false;
	}
	Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
	Options='width=800,height=600,resizable=yes';
	Features=Bars+','+Options;
	Win=open(url,'View',Features);
}

var noOfLegs	  = '<%=noOfLegs%>';
var Url			  =	'';
var Bars		  = '';
var Features	  = '';
var Options		  = '';
var	shipmentMode  = '';
	

function showDensityLOV(index,shipMode)
{
		//alert(shipMode);
	if(noOfLegs=='1')
		shipmentMode = document.forms[0].shipmentMode.value;
	else if(shipMode==1)
		shipmentMode	= 'Air';
	else if(shipMode==2)
		shipmentMode	= 'Sea';
	else
		shipmentMode	= 'Truck';

	var searchString= document.getElementById("densityRatio"+index).value;
	Url	 =  'etrans/QMSDensityRatioLOV.jsp?searchString='+searchString+'&name=densityRatio'+index+'&shipmentMode='+shipmentMode+'&fromWhere=BR&uom='+document.getElementById("uom"+index).value;
	Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
	Options='width=400,height=300,resizable=no';
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features);
}

function showLegServiceLevel(index,shipMode)
{
	if(noOfLegs=='1')
		shipmentMode = document.forms[0].shipmentMode.value;
	else if(shipMode==1)
		shipmentMode	= 'Air';
	else if(shipMode==2)
		shipmentMode	= 'Sea';
	else
		shipmentMode	= 'Truck';
	
	var searchString= document.getElementById("serviceLevel"+index).value;

	Url='etrans/ETCLOVServiceLevelIds.jsp?searchString='+searchString+'&shipmentMode='+shipmentMode+'&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&wheretoset=serviceLevel'+index;
	Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
	Options='width=400,height=300,resizable=no';
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features);
}
//@@Added by kiran.v on 06/02/2012 for Wpbn Issue
function getClockTime()
{
   var now    = new Date();
   var hour   = now.getHours();
   var minute = now.getMinutes();
   if (hour   < 10) { hour   = "0" + hour;   }
   if (minute < 10) { minute = "0" + minute; }
   var timeString = hour +
                    ':' +
                    minute;     
   var operation = '<%=operation%>';
  var custTime='<%=custTime!=null?custTime:""%>';
 if((operation=='Add' || operation=='Copy' )  && custTime==''  )
	{
document.getElementById("custTime").value=timeString;

	}

}
//@@Ended by kiran.v

function initialize()
{
	importXML('<%=request.getContextPath()%>/xml/QMSQuote.xml');
}

function initializeDynamicContentRows()
{
	initializeDynamicChargeDetails();//Modified by Anil.k
	initializeDynamicContentDetails();
}

function initializeDynamicChargeDetails()
{
	var quoteWithValue = '<%=masterDOB.getQuoteWith()%>';
	var operation = '<%=operation%>';
	var notcharges  = 0;
	if(((operation == "Modify" || operation == "View" || operation == "Copy") && quoteWithValue == "Charges") || operation == "Add" || (operation == "Copy" && document.forms[0].Quotewith.selectedIndex == 1))
	{
	var tableObj	= document.getElementById("QuoteChargeGroups");
//if (operation == "Copy" && document.forms[0].Quotewith.selectedIndex == 1)
if (quoteWithValue == "Both" && document.forms[0].Quotewith.selectedIndex == 1  ){
	var totalLanes	=  1;
	 notcharges  = 1;
}else
	{
	var totalLanes	= chargeGroupsArray.length ==0 ? 1 : chargeGroupsArray.length;
}
	for(var i=0;i<totalLanes;i++)
	{
		//alert("in intilizze-----"+i)
	//Added By Kishore For Dyna Exception 
	if ( document.forms[0].Quotewith.selectedIndex == 1 ){
	
		createDynamicContentRow(tableObj.getAttribute("id"));
	}

		if(chargeGroupsArray.length==0)
			return;
	if(notcharges!=1){
		var chargeGroupId	=	document.getElementsByName("chargeGroupId"); 

	if ( document.forms[0].Quotewith.selectedIndex == 1 )
		chargeGroupId[i].value	=	chargeGroupsArray[i];//Modified by Anil.k
	}
}
}
}

function initializeDynamicContentDetails()
{
	var tableObj	= document.getElementById("QuoteContent");
	var totalLanes	= headerFooterArray.length ==0 ? 1 : headerFooterArray.length;
	
	for(var i=0;i<totalLanes;i++)
	{
		createDynamicContentRow(tableObj.getAttribute("id"));

		if(headerFooterArray.length==0)
			return;

		var headerFooter	=	document.getElementsByName("headerFooter");
		var content			=	document.getElementsByName("content");
		var level			=	document.getElementsByName("level");
		var align			=	document.getElementsByName("align");
//Modified by Anil.k
		headerFooter[i].value	=	headerFooterArray[i];
		content[i].value		=	contentArray[i];
		level[i].value			=	levelArray[i];
		align[i].value			=	alignArray[i];
	}
}

function changeToUpper(field)
{
	//alert(field.name);
	field.value = field.value.toUpperCase();
}

function getNumberCode(input)
{
	if(event.keyCode!=13)
	{
		if(input.value.lastIndexOf(".")>-1)
		{
			if(event.keyCode < 48  || event.keyCode > 57)
				return false;
		}
		else
		{
			if((event.keyCode < 48 && event.keyCode!=46)  || event.keyCode > 57)
				return false;
		}
	}
	return true;
}
function getNumberCodeNegative(input)
{
	if(event.keyCode!=13)
	{
		if(input.value.substring("-").length>0)
		{
			if(event.keyCode < 48 || event.keyCode > 57)
				return false;
		}
		else
		{
			if((event.keyCode < 48 && event.keyCode!=45) || event.keyCode > 57)
				return false;
		}
	}
	
	return true;
}

function chrnum(input)
{
	s = input.value;
	var filteredValues;

	if(input.name=='shipperZipCode'||input.name=='consigneeZipCode')
		filteredValues = "''~!@#$%^&*()+=|\:;<>,./?";
	else
		filteredValues = "''~!@#$%^&*()+=|\:;<>,./?";

	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if ( filteredValues.indexOf(c) == -1 )
				returnString += c.toUpperCase();
		else
			flag = 1;
	}
	if( flag==1 )
	{
		alert("Special Characters not allowed");
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
		{
			if( field.elements[i-1] == input )
			{
				document.forms[0].elements[i-1].focus();
				break;
			}
		}
	}

	input.value = returnString;
}
function validateZipCode(input)
{
	input.value = input.value.toUpperCase();

	if(isNaN(input.value))
	{
		/*if(input.value.indexOf("-")==-1)
		{
			alert("Please Use a Hyphen (-) Between Alpha and Numeric Part of the Zip Code");
			input.focus();
			return false;
		}*/
	}
}

function chr1(input)
{
	s = input.value;
	filteredValues = "''~!@#$%^&*()+=|\:;<>./?";
	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if ( filteredValues.indexOf(c) == -1 )
				returnString += c.toUpperCase();
		else
			flag = 1;
	}
	if( flag==1 )
	{
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
		{
			if( field.elements[i-1] == input )
			{
				//document.forms[0].elements[i-1].focus();
				break;
			}
		}
	}

	input.value = returnString;
}

function chr(input)
{
	s = input.value;
	filteredValues = "''~!@#$%^&*()+=|\:;<>./?";
	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if ( filteredValues.indexOf(c) == -1 )
				returnString += c.toUpperCase();
		else
			flag = 1;
	}
	if( flag==1 )
	{
		alert("Special Characters not allowed");
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
		{
			if( field.elements[i-1] == input )
			{
				document.forms[0].elements[i-1].focus();
				break;
			}
		}
	}

	input.value = returnString;
}
function chkForNumeric(input)
{
	if(isNaN(input.value))
	{
		alert('Please Enter Numeric Values Only');
		input.value='';
		input.focus();
		return false;
	}
}

function showListValues(obj,legNo)
{
	if(obj!=null)
	{
		for(var i=0;i<document.getElementsByName("listWeightBreak"+legNo).length;i++)
			document.getElementsByName("listWeightBreak"+legNo)[i-1].value = '';
		for(var i=0;i<obj.length;i++)
		{
			document.getElementsByName("listWeightBreak"+legNo)[i-1].value = obj[i-1].value;
		}
	}
}

function validate()
{
	var flag=false;
	if(document.forms[0].effDate.value=='')
	{
		alert("Please enter The Effective Date");
		document.forms[0].effDate.focus();
		return false;
	}
	else
	{
		if(!chkFutureDate(document.forms[0].effDate,"gt"))
		{
			alert("Please Enter the Effective Date Greater than or equal to the Current Date.");
			document.forms[0].effDate.focus();
			return false;
		}
	}
	/*if(document.forms[0].validTo.value =='')
	{
		alert("Please enter The validTo Date");
		document.forms[0].validTo.focus();
		return false;
	}*/ //modified by silpa.p on 6-06-11 for valid date empty
	if(document.forms[0].validTo.value!='')
	{
		if(!lessDate(document.forms[0].effDate.value,'',document.forms[0].validTo.value,''))
		{
			alert("Please Enter the Valid to Date Greater than the Effective Date.");
			document.forms[0].validTo.focus();
			return false;
		}
	}

	if(document.forms[0].customerId.value=='')
	{
		alert("Please enter the Customer Id.");
		document.forms[0].customerId.focus();
		return false;
	}
	if(document.forms[0].salesPersonCode.value=='')
	{
		alert("Please enter the Sales Person Code.");
		document.forms[0].salesPersonCode.focus();
		return false;
	}
	
	if(document.forms[0].address.value.length==0)
	{
		alert('Please Select the Customer Address');
		return false;
	}
	//@@ Added by kiran.v on 17/11/2011	 for Wpbn Issue	 280269
	if(document.forms[0].custDate.value=='')
	{
		alert('Please enter the Cust Req Date');
		return false;
	}
	if(document.forms[0].custTime.value=='')
	{
		alert('Please enter the Cust Req Time');
		return false;
	}
	//Added by Anil.k on 03-JAN-2011
	if((document.getElementById("Quotewith").selectedIndex) != 1)
	{		
	for(var i=0;i<document.getElementsByName("originLoc").length;i++)
	{
		
      if(document.getElementsByName("originLoc")[i].value != '' && document.getElementsByName("destLoc")[i].value!= '' && document.getElementsByName("cargoAccPlace")[i].value!= '')
		{
              //check for rate exisits
		}
		else {
               flag= true;
		}
	}
	if(flag){
      alert("Lane details are Incomplete ");
		return false;
	}
	}//Added by Anil.k on 03-JAN-2011
	else{		
		//Modified by Anil.k for the issue 235440
		if(document.getElementById("originLocCharge").value == '' || document.getElementById("destLocCharge").value== '')	
		{  
			 alert("Lane details are Incomplete ");
			 return false;
		 }		
		if(document.getElementById("chargecargoAccPlace").value == '') 
		{
			alert("Please Enter Named place ");
			return false;
		}

	for(var i=0;i<document.getElementsByName("chargeGroupId").length;i++)
	{

      if(document.getElementsByName("chargeGroupId")[i].value != '')
		{
              //check for rate exisits
		}
		else {
               flag= true;
		}
	}
	if(flag){
      alert("Applicable Charge Groups are Incomplete ");
		return false;
	}
	}
	document.getElementById("WeightBreak").disabled=false;
	document.getElementById("shipmentMode").disabled=false;
	document.getElementById("Quotewith").disabled=false;//END
  for(var i=0;i<document.forms[0].elements.length;i++)
	{
		if(document.forms[0].elements[i].type=='text')//Modified by Anil.k
		{
			document.forms[0].elements[i].value=document.forms[0].elements[i].value.toUpperCase();//Modified by Anil.k
		}
	}
	//Added by Anil.k on 19-01-11
	for(var i=0;i<document.getElementsByName("originLoc").length;i++)
	{
		for(var j=1;j<document.getElementsByName("originLoc").length && i!=j;j++)
		{		
			//@@Modified by kiran.v on 16/01/2012 for Wpbn Issue
			if(document.getElementsByName("originLoc")[i].value == document.getElementsByName("originLoc")[j].value  && document.getElementsByName("destLoc")[i].value == document.getElementsByName("destLoc")[j].value && document.getElementsByName("originPort")[i].value == document.getElementsByName("originPort")[j].value &&
			document.getElementsByName("destPort")[i].value == document.getElementsByName("destPort")[j].value && 
				(("N"==document.getElementsByName("spotRateFlag")[i].value) && ("N"==document.getElementsByName("spotRateFlag")[j].value)))
			{
              alert("Lane No "+(i+1)+" and "+(j+1)+" contain same origin and destination");
			  return false;
			}
		}
	}
	return true;
}

function setValues(values)
{
	
  document.getElementById('contactPersons').length=0
	for(var i=0;i<values.length;i++)
  {
	document.getElementById('contactPersons').options[i] = new Option(values[i],values[i],true,true);
   }
}
function setZoneCodeValues(obj,where,rownum)
{
	
	var temp	=	'';
	var str		=	'';
	var objRegExp =	'';
	//document.getElementById(where).value='';		  //Commented by Rakesh on 28-03-2011
	
	for(i=0;i<obj.length;i++)
	{
		str	=	obj[i].value.split("[");

		if(temp!='')
			temp = temp+","+str[1].substring(0,str[1].indexOf("]"));
		else
			temp = temp+str[1].substring(0,str[1].indexOf("]"));

		objRegExp =	str[1].substring(0,str[1].indexOf("]"))+",";
		temp= temp.replace(objRegExp,'');
	}

	document.getElementById(where+rownum).value=temp;
}
function enableDisableCAP(rownum)
{
	if(document.getElementById('cargoAcceptance'+rownum)!=null && document.getElementById('cargoAcceptance'+rownum).options.value!=null)
	{
	
	if(document.getElementById('cargoAcceptance'+rownum).options.value!='OTHER')
		document.getElementById('cargoAccPlace'+rownum).readOnly = true;
	else 
	{
	
		document.getElementById('cargoAccPlace'+rownum).readOnly = false;
	}
		}
}

function populatechargesCAP()
{
/*alert();
alert("1822--"+document.getElementById('chargeincoTerms').options.value)
alert("1823--"+document.getElementById('chargecargoAcceptance').options.value)
alert("1824--org--"+document.forms[0].originLocCharge.value)*/
var temp		=	'';
	if(document.getElementById('chargeincoTerms')!=null && document.getElementById('chargeincoTerms').options!=null)
    {
	if( document.getElementById('chargeincoTerms').options.value=='EXW' ||		
		document.getElementById('chargeincoTerms').options.value=='FCA' || document.getElementById('chargeincoTerms').options.value=='FAS' ||
	    document.getElementById('chargeincoTerms').options.value=='FOB')
	{
			
		
			
			if(document.getElementById('chargecargoAcceptance').options!=null)
		{
				if(document.getElementById('chargecargoAcceptance').options.value=='DDAO')
			{
				//	temp = document.getElementById('chargecargoAcceptance').value;
                                        temp = document.getElementById('chargeincoTerms').value;//modified by silpa.p on 14-05-11
					temp = temp +" DGF TERMINAL "+ document.forms[0].originLocCharge.value;
					
					document.getElementById('chargecargoAccPlace').value = temp;
			}
			else if(document.getElementById('chargecargoAcceptance').options.value=='PORT')
			{
					if(document.getElementById(originLocCharge).value.length==0)
				{
						alert('Please Origin Port');
						document.getElementById(originLocCharge).focus();
                        document.getElementById('chargecargoAcceptance').options.value='';
						document.getElementById('chargecargoAccPlace').value = '';
						return false;
				}
					temp = document.getElementById('chargeincoTerms').options.value;
					temp = temp + " "+document.getElementById('originLocCharge').value;
					document.getElementById('chargecargoAccPlace').value = temp;
			}
			else if(document.getElementById('chargecargoAcceptance').options.value=='ZIPCODE')
			{
					
					if(document.getElementById('shipperZipCodeChg').value.length==0)
					{
						if(document.getElementById('shipperZipCodeChg').disabled==false) {
						alert('Please Enter Shipper Zip Code');
						document.getElementById('shipperZipCodeChg').focus();
						document.getElementById('chargecargoAcceptance').options.value='';
						document.getElementById('chargecargoAccPlace').value = '';
						return false;
						}else{
									 alert('Zip Code as Cargo Acceptence Place is not possible for MultiZones');
									 document.getElementById('chargecargoAcceptance').options.value='';
									 document.getElementById('chargecargoAccPlace').value = '';
									return false;
					  }
					}
					temp = document.getElementById('chargeincoTerms').options.value;
					temp = temp + " "+document.getElementById('shipperZipCodeChg').value+" "+document.forms[0].originLocCharge.value;
					document.getElementById('chargecargoAccPlace').value = temp;
			}
			else if(document.getElementById('chargecargoAcceptance').options.value=='ZONECODE')
			{
					if(document.getElementById('shipperZoneChg').value.length==0)
					{
						alert('Please Enter Shipper Zone Code');
						document.getElementById('shipperZoneChg').focus();
						document.getElementById('chargecargoAcceptance').options.value='';
						document.getElementById('chargecargoAccPlace').value = '';
						return false;
					}
					temp = document.getElementById('chargeincoTerms').options.value;
					temp = temp + " "+document.getElementById('shipperZoneChg').value+" "+document.forms[0].originLocCharge.value;
					
					document.getElementById('chargecargoAccPlace').value = temp;
			}
			else if(document.getElementById('chargecargoAcceptance').options.value=='OTHER')
			{
					temp = document.getElementById('chargeincoTerms').options.value;
					document.getElementById('chargecargoAccPlace').value = temp;
			}
			else if(document.getElementById('chargecargoAcceptance').options.value=='')
			{
					document.getElementById('chargecargoAccPlace').value = '';
			}
		}
	}
	else
	{

		
		if(document.getElementById('chargecargoAcceptance').options!=null)
		{
		if(document.getElementById('chargecargoAcceptance').options.value=='DDAO')
			{
					temp = document.getElementById('chargeincoTerms').options.value;
					temp = temp +" DGF TERMINAL "+ document.forms[0].destLocCharge.value;
					document.getElementById('chargecargoAccPlace').value = temp;
			}
			else if(document.getElementById('chargecargoAcceptance').options.value=='PORT')
			{
					temp = document.getElementById('chargeincoTerms').options.value;
					temp = temp + " "+document.getElementById('destLocCharge').value;
					document.getElementById('chargecargoAccPlace').value = temp;
			}
			else if(document.getElementById('chargecargoAcceptance').options.value=='ZIPCODE')
			{
					if(document.getElementById('consigneeZipCodeChg').value.length==0)
					{

						if(document.getElementById('consigneeZipCodeChg').disabled==false) {
						alert('Please Enter Consignee Zip Code');
						document.getElementById('consigneeZipCodeChg').focus();
						document.getElementById('chargecargoAcceptance').options.value='';
						document.getElementById('chargecargoAccPlace').value = '';
						return false;
						}else{
									alert('Zip Code as Cargo Acceptence Place is not possible for MultiZones');
									document.getElementById('chargecargoAcceptance').options.value='';
									document.getElementById('chargecargoAccPlace').value = '';
									return false;
						}
					}
					temp = document.getElementById('chargeincoTerms').options.value;
					temp = temp + " "+document.getElementById('consigneeZipCodeChg').value+" "+document.forms[0].destLocCharge.value;
					document.getElementById('chargecargoAccPlace').value = temp;
			}
			else if(document.getElementById('chargecargoAcceptance').options.value=='ZONECODE')
			{
					if(document.getElementById('consigneeZoneChg').value.length==0)
					{
						alert('Please Enter Consignee Zone Code');
						document.getElementById('consigneeZoneChg').focus();
						document.getElementById('chargecargoAcceptance').options.value='';
						document.getElementById('chargecargoAccPlace').value = '';
						return false;
					}
					temp = document.getElementById('chargeincoTerms').options.value;
					temp = temp + " "+document.getElementById('consigneeZoneChg').value+" "+document.forms[0].destLocCharge.value;
					document.getElementById('chargecargoAccPlace').value = temp;
			}
			else if(document.getElementById('chargecargoAcceptance').options.value=='OTHER')
			{
					temp = document.getElementById('chargeincoTerms').options.value;
					document.getElementById('chargecargoAccPlace').value = temp;
			}
			else if(document.getElementById('chargecargoAcceptance').options.value=='')
			{
					document.getElementById('chargecargoAccPlace').value = '';
			}
	}
	}
	}


}

function populateCAP(rownum)
{
	
	var operation ='<%=operation%>';
	var rownum1= (rownum ==1?1:(rownum-1));
     
	enableDisableCAP(rownum);
	
	var temp		=	'';
	if(document.getElementById('incoTerms'+rownum)!=null && document.getElementById('incoTerms'+rownum).options!=null)
    {
	if( document.getElementById('incoTerms'+rownum).options.value=='EXW' ||		
		document.getElementById('incoTerms'+rownum).options.value=='FCA' || document.getElementById('incoTerms'+rownum).options.value=='FAS' ||
	    document.getElementById('incoTerms'+rownum).options.value=='FOB')
	{
			
		
			
			if(document.getElementById('cargoAcceptance'+rownum).options!=null)
		{
				if(document.getElementById('cargoAcceptance'+rownum).options.value=='DDAO')
			{
					
					temp = document.getElementById('incoTerms'+rownum).value;
					
					temp = temp +" DGF TERMINAL "+ document.forms[0].originLocationName.value;
					
					document.getElementById('cargoAccPlace'+rownum).value = temp;
			}
			else if(document.getElementById('cargoAcceptance'+rownum).options.value=='PORT')
			{
					if(document.getElementById('originPort'+rownum1).value.length==0)
				{
						alert('Please Origin Port');
						document.getElementById('originPort'+rownum1).focus();
                        document.getElementById('cargoAcceptance'+rownum).options.value='';
						document.getElementById('cargoAccPlace'+rownum).value = '';
						return false;
				}
					temp = document.getElementById('incoTerms'+rownum).options.value;//modified by silpa.p on 13-05-11
					temp = temp + " "+document.getElementById('originPort'+rownum1).value;
					document.getElementById('cargoAccPlace'+rownum).value = temp;
			}
			else if(document.getElementById('cargoAcceptance'+rownum).options.value=='ZIPCODE')
			{
					
					if(document.getElementById('shipperZipCode'+rownum1).value.length==0)
					{
						if(document.getElementById('shipperZipCode'+rownum1).disabled==false) {
						alert('Please Enter Shipper Zip Code');
						document.getElementById('shipperZipCode'+rownum1).focus();
						document.getElementById('cargoAcceptance'+rownum).options.value='';
						document.getElementById('cargoAccPlace'+rownum).value = '';
						return false;
						}else{
									 alert('Zip Code as Cargo Acceptence Place is not possible for MultiZones');
									 document.getElementById('cargoAcceptance'+rownum).options.value='';
									 document.getElementById('cargoAccPlace'+rownum).value = '';
									return false;
					  }
					}
					temp = document.getElementById('incoTerms'+rownum).options.value;
					temp = temp + " "+document.getElementById('shipperZipCode'+rownum1).value+" "+document.forms[0].originLocationName.value;
					document.getElementById('cargoAccPlace'+rownum).value = temp;
			}
			else if(document.getElementById('cargoAcceptance'+rownum).options.value=='ZONECODE')
			{
					if(document.getElementById('shipperZone'+rownum1).value.length==0)
					{
						alert('Please Enter Shipper Zone Code');
						document.getElementById('shipperZone'+rownum1).focus();
						document.getElementById('cargoAcceptance'+rownum).options.value='';
						document.getElementById('cargoAccPlace'+rownum).value = '';
						return false;
					}
					temp = document.getElementById('incoTerms'+rownum).options.value;
					temp = temp + " "+document.getElementById('shipperZone'+rownum1).value+" "+document.forms[0].originLocationName.value;
					
					document.getElementById('cargoAccPlace'+rownum).value = temp;
			}
			else if(document.getElementById('cargoAcceptance'+rownum).options.value=='OTHER')
			{
					temp = document.getElementById('incoTerms'+rownum).options.value;
					document.getElementById('cargoAccPlace'+rownum).value = temp;
			}
			else if(document.getElementById('cargoAcceptance'+rownum).options.value=='')
			{
					document.getElementById('cargoAccPlace'+rownum).value = '';
			}
		}
	}
	else
	{

		
		if(document.getElementById('cargoAcceptance'+rownum).options!=null)
		{
		if(document.getElementById('cargoAcceptance'+rownum).options.value=='DDAO')
			{
					temp = document.getElementById('incoTerms'+rownum).options.value;
					temp = temp +" DGF TERMINAL "+ document.forms[0].destLocationName.value;
					document.getElementById('cargoAccPlace'+rownum).value = temp;
			}
			else if(document.getElementById('cargoAcceptance'+rownum).options.value=='PORT')
			{
					temp = document.getElementById('incoTerms'+rownum).options.value;
					temp = temp + " "+document.getElementById('destPort'+rownum1).value;
					document.getElementById('cargoAccPlace'+rownum).value = temp;
			}
			else if(document.getElementById('cargoAcceptance'+rownum).options.value=='ZIPCODE')
			{
					if(document.getElementById('consigneeZipCode'+rownum1).value.length==0)
					{

						if(document.getElementById('consigneeZipCode'+rownum1).disabled==false) {
						alert('Please Enter Consignee Zip Code');
						document.getElementById('consigneeZipCode'+rownum1).focus();
						document.getElementById('cargoAcceptance'+rownum).options.value='';
						document.getElementById('cargoAccPlace'+rownum).value = '';
						return false;
						}else{
									alert('Zip Code as Cargo Acceptence Place is not possible for MultiZones');
									document.getElementById('cargoAcceptance'+rownum).options.value='';
									document.getElementById('cargoAccPlace'+rownum).value = '';
									return false;
						}
					}
					temp = document.getElementById('incoTerms'+rownum).options.value;
					temp = temp + " "+document.getElementById('consigneeZipCode'+rownum1).value+" "+document.forms[0].destLocationName.value;
					document.getElementById('cargoAccPlace'+rownum).value = temp;
			}
			else if(document.getElementById('cargoAcceptance'+rownum).options.value=='ZONECODE')
			{
					if(document.getElementById('consigneeZone'+rownum1).value.length==0)
					{
						alert('Please Enter Consignee Zone Code');
						document.getElementById('consigneeZone'+rownum1).focus();
						document.getElementById('cargoAcceptance'+rownum).options.value='';
						document.getElementById('cargoAccPlace'+rownum).value = '';
						return false;
					}
					temp = document.getElementById('incoTerms'+rownum).options.value;
					temp = temp + " "+document.getElementById('consigneeZone'+rownum1).value+" "+document.forms[0].destLocationName.value;
					document.getElementById('cargoAccPlace'+rownum).value = temp;
			}
			else if(document.getElementById('cargoAcceptance'+rownum).options.value=='OTHER')
			{
					temp = document.getElementById('incoTerms'+rownum).options.value;
					document.getElementById('cargoAccPlace'+rownum).value = temp;
			}
			else if(document.getElementById('cargoAcceptance'+rownum).options.value=='')
			{
					document.getElementById('cargoAccPlace'+rownum).value = '';
			}
	}
	}
	}
	var operation = '<%=operation%>';//Added by Anil.k		
	if(operation == "Modify")
	{
		document.forms[0].WeightBreak.disabled=true;
		document.forms[0].shipmentMode.disabled=true;
		document.forms[0].Quotewith.disabled=true;
	}
	if(operation == "Modify" || operation == "Copy" || operation == "Add")//Modified by Anil.k for the issue 235442
	{
		var index = document.forms[0].Quotewith.selectedIndex;		
		if(index==1)
		{
			document.getElementById("Charges").style.display = 'block';
			document.getElementById("Freight").style.display = 'none';		
		}
		else
		{
			document.getElementById("Freight").style.display = 'block';
			document.getElementById("Charges").style.display = 'none';		
		}
	}	//END
}

function testShipMode(shipMode)
{
	var operation = '<%=operation%>';
	
	if(operation=='Modify' && shipMode != 0)
		document.forms[0].shipmentMode.options.selectedIndex = (shipMode-1);
	else
	{	
		if(noOfLegs=='1')
		{
			var uom			= document.getElementById("uom0");
			var listType	= document.getElementById("listType0");
			uom.length=0;
			if(document.forms[0].shipmentMode.value=='Sea')
			{
				uom[0] = new Option("CBM","CBM");
				uom[1] = new Option("CFT","CFT");
				listType.innerHTML = "Container Types";
			}
			else
			{
				if(document.forms[0].shipmentMode.value=="Air")
					listType.innerHTML = "ULD Types";
				else
					listType.innerHTML = "Container Types";
				
				uom[0] = new Option("Kg","Kg");
				uom[1] = new Option("Lb","Lb");
			}
		}
		else
		{
			for(var i=0;i<noOfLegs;i++)
			{
				var uom			= document.getElementById("uom"+i);
				var listType	= document.getElementById("listType"+i);
				var shipMode	= document.getElementById("legShipmentMode"+i).value;
				uom.length=0;
				if(shipMode=='2')
				{
					uom[0] = new Option("CBM","CBM");
					uom[1] = new Option("CFT","CFT");
					listType.innerHTML = "Container Types";
				}
				else
				{
					if(shipMode=="1")
						listType.innerHTML = "ULD Types";
					else
						listType.innerHTML = "Container Types";

					uom[0] = new Option("Kg","Kg");
					uom[1] = new Option("Lb","Lb");
				}
			}
		}
		return true;
	}
	
}
function clearListValues()
{
	
	if(noOfLegs=='1')
	{
		for(var j=0;j<document.getElementsByName("listWeightBreak0").length;j++)
		{
			document.getElementsByName("listWeightBreak0")[j].value = '';
			document.getElementsByName("listRate0")[j].value = '';
		}
	}
}
function openOrgPortLov(rownum)
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	if(document.forms[0].shipmentMode.value=="Sea")
	{
		tabArray = 'PORT_ID';
		formArray = 'originPort'+rownum;
		Url		="qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=PORTS&search= where PORT_ID LIKE '"+document.getElementById('originPort'+rownum).value+"~'";	
		Options	='width=600,height=750,resizable=0';
		
	}
	else
	{
		tabArray = 'LOCATIONID';
		formArray = 'originPort'+rownum;
				Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.getElementById('originPort'+rownum).value+"~'";

		Options	='width=750,height=750,resizable=yes';
	}
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}

function openDestPortLov(rownum)
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";

	if(document.forms[0].shipmentMode.value=="Sea")
	{
		tabArray = 'PORT_ID';
		formArray = 'destPort'+rownum;
		Url		="qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=PORTS&search= where PORT_ID LIKE '"+document.getElementById('destPort'+rownum).value+"~'";	
		Options	='width=600,height=750,resizable=yes';	
		
	}
	else
	{
		tabArray = 'LOCATIONID';
		formArray = 'destPort'+rownum;
		Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.getElementById('destPort'+rownum).value+"~'";
		Options	='width=750,height=750,resizable=yes';
	}
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}

function openLocationLov(input,rownum)
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	var val="";
	
	if(input=="Origin")
	{
		tabArray = 'LOCATIONID';
		formArray = 'originLoc'+rownum;	
		val = document.getElementById('originLoc'+rownum).value;
	
		Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+val+"~'";
		
	}
	else if(input=="Dest")
	{
		tabArray = 'LOCATIONID';
		formArray = 'destLoc'+rownum;	
        val = document.getElementById('destLoc'+rownum).value;
		      Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+val+"~'";

		
	}
	
	

	
  Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=1';
	Options	='width=750,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}

function openSalesPersonLov()
{
	var tabArray = 'EMPID';
	var formArray = 'salesPersonCode';
	
	var lovWhere	=	"";
	
	Url		="qms/ListOfValues.jsp?lovid=REPORTING_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=SALESPERSON&search= where EMPID LIKE '"+document.forms[0].salesPersonCode.value+"~'";
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
	
 }

function openCustomerLov()
{
	var tabArray = 'CUSTOMERID';
	var formArray = 'customerId';
	var lovWhere	=	"";
	
	Url		="qms/ListOfValues.jsp?lovid=CUSTOMER_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=QUOTECUSTOMER&search= where CUSTOMERID LIKE '"+document.forms[0].customerId.value+"~' ORDER BY trim(COMPANYNAME)";

	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
	
 }
 function check_Length(maxLength,input)
{
	val = input.value;
	if(val.length > maxLength)
	alert("Character Limit reached("+maxLength+")");
	val = val.substring(0,maxLength);
	
	input.value = val;
}
function changeConsoleVisibility(flag)
{
	var console	 = document.getElementById(flag+'Console');
	
	if(document.getElementById(flag+'Mode').value=='2')
		console.style.display="block";
	else
	{
		console.style.display="none";
		document.getElementById(flag+'ConsoleType').options[0].selected = true;
	}
}
function salesPersonFlag()
{
	<%if("Y".equalsIgnoreCase(masterDOB.getSalesPersonFlag()))
	{%>
	      document.forms[0].salesPersonFlag.checked=true;
	<%}%>
	
}
//Added by Anil.k on 03-JAN-2011
function checkAjaxOriginDestinationValidForCharges(locationName,id)
	{	
 var rownum = (locationName=='Origin')?id.substring(9):id.substring(7);

	if(locationName !='' && document.getElementById(id).value != '')
		{
   if(locationName=='Origin' || locationName=='Destination')
			{
           var url='';
		   if(locationName  == 'Origin')
	url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=OriginDestinationValid&originLoc='+document.getElementById(id).value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&Location=Origin';
		   else
    url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=OriginDestinationValid&destLoc='+document.getElementById(id).value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&Location=Destination';
		 if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }		
        req.open("GET",url,true);
		req.onreadystatechange = callbackOriginDestinationValidForCharges;
		req.send(null);
		}
		}
}
function callbackOriginDestinationValidForCharges()
{
       if (req.readyState == 4) {
        if (req.status == 200) 
		{	  
		var response = req.responseText.toString();	
		if(response.length>0)
			alert(response)
	}
	   }
}

//Modified By Kishore
var laneCreated = false; 
function laneDetails()
{	
	var index = document.forms[0].Quotewith.selectedIndex;
		var operation = '<%=operation%>';
		var quoteWithValue = '<%=masterDOB.getQuoteWith()%>';
	//var indexValue = document.forms[0].Quotewith.options[index].value;	
	if(index==1)
	{
				
		document.getElementById("Charges").style.display = 'block';
		document.getElementById("Freight").style.display = 'none';
		document.getElementById("WeightBreak").disabled = true;

   if ( (index==1  || (quoteWithValue == "Both" && document.forms[0].Quotewith.selectedIndex == 1 )) && laneCreated== false  ){ //index==1  ||
		//alert("kishore --> 2") 
     initializeDynamicChargeDetails();
		laneCreated=  true;
   }
	}
	else
	{
		document.getElementById("Freight").style.display = 'block';
		document.getElementById("Charges").style.display = 'none';
		document.getElementById("WeightBreak").disabled = false;
	}
	var quoteWithValue = '<%=masterDOB.getQuoteWith()%>';
	var operation = '<%=operation%>';
	//alert((operation=="Copy")+"vv"+(quoteWithValue=="Freight"))
	if(operation=="Copy" && (quoteWithValue=="Freight" || quoteWithValue=="Both" ))
	{		
		document.forms[0].originLocCharge.value = '' ;
		document.getElementById("destLocCharge").value= '';
		document.getElementById("shipperZipCodeChg").value= '';
		document.getElementById("shipperZoneChg").value= '';
		document.getElementById("consigneeZipCodeChg").value= '';
		document.getElementById("consigneeZoneChg").value= '';
        document.getElementById("chargecargoAcceptance").value= '';
		document.getElementById("chargecargoAccPlace").value= '';
	}
}//END
//Added by Anil.k for CR 231214 on 25Jan2011
function appChrg()
{
	if(document.getElementsByName("chargeGroupId").length==1)
		document.forms[0].chargeGroupId.value="";
	else
	{
		for(var j=0;j<document.getElementsByName("chargeGroupId").length;j++)		
			document.getElementById("chargeGroupId"+(j+1)).value="";		
	}
}
function extend(obj)
{
var operation = '<%=operation%>';//added by silpa.p on 30-05-11

if(operation!='Modify'){
	//added by silpa.p on 30-05-11

	if(dtCheck(obj)){

	var date = document.forms[0].effDate.value;
	var dateValue = date.split("-");	
	var d = new Date(dateValue[2],dateValue[1]-1,dateValue[0]);
    d.setDate(d.getDate()+29);
	var d1=d.getDate();
	//added by silpa on28-04-2011
	var date1= (d1 < 10) ? '0' + d1 : d1;
	var m =d.getMonth()+1;
	var month = (m < 10) ? '0' + m : m;
var validDate = date1+"-"+(month)+"-"+d.getYear();
   //ended
   //Added by silpa.p on 6-06-11 for valid date change
if(validDate.length>0){
	  var confirmV = confirm(" Do you Want To Change The Valid To Date Default 30 days?");
	    if(confirmV){
	document.forms[0].validTo.value = validDate;	
	}
	else{

document.forms[0].validTo.value = "";	
}
}//ended
	}
	else{
		document.forms[0].validTo.value = "";
		document.forms[0].effDate.value = "";//added by silpa on 3-05-11
	}
}//ended
}//Ended by Anil.k for CR 231214 on 25Jan2011
 //Added by Rakesh for Issue:   
function IsValidTime(field)
	{
		timeStr=field.value
		if(timeStr.length!=0)
		{
	
			if((timeStr.length==5 && timeStr.indexOf(':')==-1)  || (timeStr.length==6 && timeStr.indexOf(':') !=-1))
				var	timePat	= /^(\d{1,3}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;	
			else
			var	timePat	= /^(\d{1,2}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;
			
				
			if((timeStr.length==4 && timeStr.indexOf(':')==-1) || (timeStr.length==5 && timeStr.indexOf(':')!=-1))
			{
				if(timeStr.length==4 && timeStr.indexOf(':')==-1)
				{
					timeStr = timeStr.substring(0,2)+':'+timeStr.substring(2,4);
					field.value=timeStr;

				}
			}
			else if((timeStr.length==5 && timeStr.indexOf(':')==-1) || (timeStr.length==6 && timeStr.indexOf(':')!=-1))
			{
				if(timeStr.length==5 && timeStr.indexOf(':')==-1)
				{
					timeStr = timeStr.substring(0,3)+':'+timeStr.substring(3,5);
					field.value=timeStr;

				}
			}
			var	matchArray = timeStr.match(timePat);
			if (matchArray == null)
			{
				alert("Please enter in HH:MM format");
				field.value="";
				field.focus();
			}
			else
			{
				hour = matchArray[1];
				minute = matchArray[2];
				second = matchArray[4];
				ampm = matchArray[6];
				if (second=="")	{ second = null; }
				if (ampm=="") {	ampm = null	}
				if (hour < 0  || hour >	23)	
        {
          alert("Please enter correct Hours");
		  field.value="";
          field.focus();
				}
				else if	(minute<0 || minute	> 59) {
					alert ("Please enter correct Minutes.");
					field.value="";
					field.focus();
				}
			}
		}
	}
 //Ended by Rakesh for Issue: 

// Added By Kishore Podili For Multi Zone Codes
function checkAjaxIsValidZoneCode(zoneName,id,locationId)
	{
	
    tempZoneId  = id;
	var zoneValue = document.getElementById(id).value ;
	
	//alert("kishore -->  "+id);
	var rownum = (zoneName=='shipper')?id.substring(11):id.substring(13);
   temprownnum = rownum;
  

	//alert(zoneValue.indexOf(','));
	
	if(zoneValue.indexOf(',')!=-1)
		disableZipElement(zoneName,rownum);
	else
		enableZipElement(zoneName,rownum);
	
   
   
   
	if(zoneName !='' && document.getElementById(id).value != '' && document.getElementById(locationId).value!='')
		{
           var url='';
		 
	url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=IsValidZoneCode&zoneCode='+document.getElementById(id).value+'&shipmentMode='+document.forms[0].shipmentMode.value+'&location='+document.getElementById(locationId).value+'&weightBreak='+document.forms[0].WeightBreak.value+'&dummytime='+((new Date()).getTime());
		   
		   //alert(url);
		 
		 if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        }

        req.open("GET",url,true);
		req.onreadystatechange = callbackIsValidZoneCode;
		req.send(null); 
		
		}else{
          document.getElementById(id).value ='';
		} 
}
	function callbackIsValidZoneCode()
	{
       if (req.readyState == 4) {
        if (req.status == 200) 
		{	  
		var response = req.responseText.toString();	
		if(response.length>0){
			alert(response)
				 document.getElementById(tempZoneId).value = "";
				 document.getElementById(tempZoneId).focus();
			
		}
		}
	   }
	}

	function disableZipElement(zoneName,rownum){
	//alert("Hi Kishore1---rownum "+rownum);
	var rownum1= (rownum ==1?1:(parseInt(rownum)+1));
	//alert("Hi Kishore1---rownum: "+rownum+"		rownum1: "+rownum1);
	document.getElementById('cargoAcceptance'+rownum1).options.value='';
	document.getElementById('cargoAccPlace'+rownum1).value = '';
	document.getElementById(zoneName+"ZipCode"+rownum).value="";
	document.getElementById(zoneName+"ZipCode"+rownum).readOnly=true;
	document.getElementById(zoneName+"ZipCodeLOV"+rownum).disabled=true;
	//alert("Hi Kishore2");
	}

	function enableZipElement(zoneName,rownum){
	//alert("Hi Kishore3");
	var rownum1= (rownum ==1?1:(parseInt(rownum)+1));
//	alert("Hi Kishore1---rownum: "+rownum+"		rownum1: "+rownum1);
	document.getElementById('cargoAcceptance'+rownum1).options.value='';
	document.getElementById('cargoAccPlace'+rownum1).value = '';
	document.getElementById(zoneName+"ZipCode"+rownum).readOnly=false;
	document.getElementById(zoneName+"ZipCodeLOV"+rownum).disabled=false;
	//alert("Hi Kishore4");
	}
// Added By Kishore Podili For Multi Zone Codes  


function displaySpotrateSurchargeCount(obj,rownum)
	{
	//alert(document.getElementById("spotRateSurchargeCount"+rownum).value);
	if(obj.value=="Y"){
		 document.getElementById("spotSurcharges"+rownum).style.display = 'block';
	
	}
	else{
		 document.getElementById("spotSurcharges"+rownum).style.display = 'none';
	
	}

	}
function setSpotSurchargeCountvalue(obj,spotratecoutnid)
	{
//	alert(spotratecoutnid)
	document.getElementById("spotRateSurchargeCount1"+spotratecoutnid).value = (obj.value!="" || obj.value!= null)?obj.value:"0";
	}

</script>

</head>

<body onLoad="initialize();populateCAP(1);salesPersonFlag();getClockTime();">
<form  method="post" name="QuoteMaster" action="QMSMultiQuoteController" onSubmit="return validate()">
	<table width="103%" cellpadding="4" cellspacing="0" bgcolor='#FFFFFF'>
	
	
   

	
		<tr class="formlabel">

		<%if("Modify".equalsIgnoreCase(operation) || ("Copy".equalsIgnoreCase(operation))) {%>
			<td >MULTI QUOTE(<%=masterDOB.getQuoteId()%>,<%=quoteStatus%>,<%=completeFlag%>) - <%=operation!=null?operation.toUpperCase():""%></td><td align='right'>QS1060201</td>
           <input type='hidden' name='laneCounter' value='<%=finalDOB.getLegDetails().size()*2%>'>
			<%}else { %>
			<td >MULTI QUOTE - <%=operation!=null?operation.toUpperCase():""%></td><td align='right'>QS1060201</td>
			<input type='hidden' name='laneCounter' value='2'>
			<%}%>
		</tr>
			
		</table>
		<table width="103%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
<%
		StringBuffer	errors	=	(StringBuffer)request.getAttribute("errors");
		if(errors!=null)
		{
			String	errorMessages	=	errors.toString();
%>
			<tr color="#FFFFFF">
				<td colspan="8">
					<font face="Verdana" size="2" color='red'><b>The form has not been submitted because of the following error(s):</b><br><br>
					<%=errorMessages%></font>
				</td>
			</tr>
<%		
		}
		if(request.getAttribute("error")!=null)
		{
%>
				<tr color="#FFFFFF">
					<td colspan="8"><font face="Verdana" size="2" color='red'><b>This form has not been submitted because of the following error : </b> <br><br>
					<%=(String)request.getAttribute("error")%></font></td>
				</tr>
<%
		}
%>
		<tr class="formdata">
			<td  width='38%' valign="top">Quote&nbsp;Type:&nbsp;<font color="#ff0000">*</font>
			                          &nbsp;Wt Brk:<font color="#ff0000">*</font>
									  &nbsp;Quote With:<font color="#ff0000">*</font><br>
						<!--   // @@ Modified  by kiran.v	on 17/11/2011	   for Wpbn Issue		  280268	  -->
				<select name="shipmentMode" size="1" class="select" onchange="setWeightBreak()" <%=disabled%>>
					<option value="Air"   <%=masterDOB.getShipmentMode()==1?"selected":""%>>Air</option>
					<option value="Sea"   <%=masterDOB.getShipmentMode()==2?"selected":""%>>Sea</option>
					<option value="Truck" <%=masterDOB.getShipmentMode()==4?"selected":""%>>Truck</option>
				</select>
                    <!--Weight Break Static for screen--> 
					 &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;&nbsp;
				<select name="WeightBreak" size="1" class="select"  <%=disabled%>>
	<option value="Slab"   <%=masterDOB.getMultiquoteweightBrake()!=null?masterDOB.getMultiquoteweightBrake().equalsIgnoreCase("slab")?"selected":"":""%>>Slab</option>
	<option value="Flat"   <%=masterDOB.getMultiquoteweightBrake()!=null?masterDOB.getMultiquoteweightBrake().equalsIgnoreCase("Flat")?"selected":"":""%> >Flat</option>
	<option value="List"   
	<%=masterDOB.getMultiquoteweightBrake()!=null?masterDOB.getMultiquoteweightBrake().equalsIgnoreCase("list")?"selected":"":""%> >List</option>
				</select>
				&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<select name="Quotewith" size="1" class="select" onchange="laneDetails()"><!-- Modified by Anil.k on 03-JAN-2011 -->
					<option value="Freight"  <%="Freight".equalsIgnoreCase(masterDOB.getQuoteWith())?"selected":""%>>Freight</option>
					<option value="Charges"  <%="Charges".equalsIgnoreCase(masterDOB.getQuoteWith())?"selected":""%>>Charges</option>
					<option value="Both"     <%="Both".equalsIgnoreCase(masterDOB.getQuoteWith())?"selected":""%>>Both   </option>
				</select>
			</td>
			<td align='center' width='3%' valign="top">  Flag<br>
				<select name="impFlag" size="1" class="select">
					<option value="U" <%=!masterDOB.isImpFlag()?"selected":""%>></option>
					<option value="I" <%=masterDOB.isImpFlag()?"selected":""%>>Imp</option>
				</select>
			</td>
			<td width ='15%'>Quote Id:
<%			
			
			if(!"Add".equalsIgnoreCase(operation))
			{
%>
			<a href="#" onclick='openLink("QMSQuoteController?Operation=View&subOperation=Report&QuoteId=<%if(masterDOB.getPreQuoteId()!=null&& masterDOB.getPreQuoteId().length()>1){%><%=masterDOB.getPreQuoteId()%><%}%>")'><font color="#0000FF"><b>Previous</b></font></a>
			<!-- in the above line masterDOB.getPreQuoteId()!=0  replaced with masterDOB.getPreQuoteId()!=null by subrahmanyam for the enhancement #146971 and #146970 on 03/12/08-->
<%
			}
%>
				<input class="text" maxLength="20" name="preQuoteId"  size="10" onBlur="changeToUpper(this);" onKeyPress="return getNumberCode(this)" value="<%if(masterDOB.getPreQuoteId()!=null&& masterDOB.getPreQuoteId().length()>1){%><%=masterDOB.getPreQuoteId()%><%}%>" readOnly>
				
			</td>
			<!--@@Added by kiran.v on 29/07/2011 for Wpbn Issue  260143-->
			<%if("Copy".equalsIgnoreCase(operation)){%>
			<td width ='15%' valign="top">Created&nbsp;Date:<br>
				<input class="text" maxLength="25" name="createdDate"  size="10" value="<%=currentDate%>" onBlur="changeToUpper(this);return dtCheck(this)" readonly ><br>
			
			</td>
			<td width ='15%' valign="top">Eff&nbsp;Date:<font color="#ff0000">*</font><br><!--modified by silpa.p on 13-05-11-->
				<input class="text" maxLength="25" name="effDate"  size="10" value="<%=currentDate%>" onBlur="extend(this); return dtCheck(this)"><br><!--added by silpa.p on 1-06-11-->
				<input class="input" type="button" value="..." onclick="newWindow('effDate','0','0','')">
			</td>
			<%}else{%>
			<td width ='15%' valign="top">Created&nbsp;Date:<br>
				<input class="text" maxLength="25" name="createdDate"  size="10" value="<%=createdDate!=null?createdDate:currentDate%>" onBlur="changeToUpper(this);return dtCheck(this)" readonly ><br>
			
			</td>
			<td width ='15%' valign="top">Eff&nbsp;Date:<font color="#ff0000">*</font><br><!--modified by silpa.p on 13-05-11-->
				<input class="text" maxLength="25" name="effDate"  size="10" value="<%=effDate!=null?effDate:currentDate%>" onBlur="extend(this); return dtCheck(this)"><br><!--added by silpa.p on 1-06-11-->
				<input class="input" type="button" value="..." onclick="newWindow('effDate','0','0','')">
			</td>
			<%}%>
			<!--@@Ended by kiran.v -->
			<td width ='15%' valign="top">Valid To:<font color="#ff0000">*</font><br><!-- mod by gov for CR -->
				<input class="text" maxLength="25" name="validTo"  size="10" value="<%=validTo!=null?validTo:""%>" onBlur="changeToUpper(this);return dtCheck(this)"><br>
				<input class="input" type="button" value="..." onclick="newWindow('validTo','0','0','')">
			</td>
			
			<%
			if("Copy".equalsIgnoreCase(operation))
			{
				createdBy	=loginbean.getEmpId();/*	loginbean.getUserId();*/
			}
			else
			{
				createdBy	=	masterDOB.getCreatedBy()!=null?masterDOB.getCreatedBy():loginbean.getEmpId();
			}
			
%>
			<!--@@Modified by Kameswari for the WPBN issue-6318-->
			<td width ='17%' valign="top">Created By:<br>
				<input class="text" maxLength="25" name="createdBy"  size="15" value="<%=createdBy%>" readOnly>
           <!--  <td width ='17%' valign="top">Cust Req Date<br>
				<input class="text" maxLength="25" name="custDate"  size="15" value="" ><br>
				<input class="input" type="button" value="..." onclick="newWindow('custDate','0','0','')"></td> -->
			</td>
			<!--@@WPBN issue-61318-->
		</tr>
        </table>
	   <table width="103%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata" >
		<!-- 	<td  width ='20%' rowspan='2' valign='top'>Customer&nbsp;Id :&nbsp;<font color="#ff0000">*</font> -->
			<td  width ='20%'  valign='top'>Customer&nbsp;Id :&nbsp;<font color="#ff0000">*</font>
				<br>
				<input class="text" maxLength="25" name="customerId"  size="12" value="<%=masterDOB.getCustomerId()!=null?masterDOB.getCustomerId():""%>" row="0" onBlur="changeToUpper(this);chrnum(this);getCustAddressByAjax(this);getCustAttentionTo(this);getSalesPerson(this);"  <%=readOnly%>>
				<!-- <input class="input" name="custLovBut"  type="button" value="..." row="0" onclick="popUpWindow('customerLov')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>> -->
				<input class="input" name="custLovBut"  type="button" value="..." row="0" onclick="openCustomerLov()" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
				<input class="input" type="button" value="NEW" row="0" onclick="popUpWindow('newCustomerLov')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>&nbsp;<br>
				<!-- Added by Rakesh K on 23-02-2011 for Issue:236359-->
				
				<!-- Ended by Rakesh for Issue:        on 28-02-2011 -->
				<!-- Ended by Rakesh K on 23-02-2011 for Issue:236359-->
			</td>
			<!-- <td width ='20%' rowspan='2' valign='top'> -->
			<td width ='20%' valign='top'>
				<p align="left">Address:
				<input class="input" type="button" value="..." name="addrLovBut" onclick="popUpWindow('addrLov')">&nbsp;
				<input class="input" type="button" value="NEW" row="0" onclick="popUpWindow('newAddrLov')"><br>
				<textarea class="text" cols="30" rows="4" name="address" onBlur="changeToUpper(this);" readOnly><%=masterDOB.getCustomerAddress()!=null?masterDOB.getCustomerAddress():""%></textarea>
				<input type="hidden" name="addressId" value='<%="0".equalsIgnoreCase(addressId)?"":addressId%>'>
				</p>
			</td>
			<!-- <td width ='20%' rowspan='2' valign='top'>Attention To: -->
			<td width ='20%'  valign='top'>Attention To:
			<input class="input" type="button" value="..." name="contactPersonLov" onclick="popUpWindow('contactLov')">&nbsp;
				<input class="input" type="button" value="NEW" row="0" onclick="popUpWindow('newContactPerson')">
				<select class="select"  name="contactPersons" size="4" multiple style="width:200px;margin:0px 0 5px 0;">
<%				 //Modified by Rakesh for Issue:         on 03-01-2010
				if(masterDOB.getCustContactNames()!=null && masterDOB.getCustContactNames().length>0)
				{
				  for(int i=0;i<masterDOB.getCustContactNames().length;i++)
				  {
					  //Logger.info(FILE_NAME,"masterDOB.getCustomerContacts()::"+masterDOB.getCustomerContacts()[i-1]);
					  //custContactIds = custContactIds+masterDOB.getCustomerContacts()[i-1]+",";
					  //Added to get Attention To values in Modify Case
						if(attentionSlno == "")
							attentionSlno= masterDOB.getCustomerContacts()[i];
						else
							attentionSlno= attentionSlno+","+masterDOB.getCustomerContacts()[i];
						if(customerId!="")
							customerId = customerId+","+masterDOB.getCustomerId();
						else
							customerId = masterDOB.getCustomerId();	
						if(i>0)
							contactNo = contactNo+","+(masterDOB.getCustomerContactNo()[i]!=null?masterDOB.getCustomerContactNo()[i]:"");
						else
							contactNo = masterDOB.getCustomerContactNo()[i]!=null?masterDOB.getCustomerContactNo()[i]:"";
						if(i>0)
							emailId = emailId+","+(masterDOB.getCustomerContactsEmailIds()[i]!=null?masterDOB.getCustomerContactsEmailIds()[i]:"");
						else
							emailId = masterDOB.getCustomerContactsEmailIds()[i]!=null?masterDOB.getCustomerContactsEmailIds()[i]:"";
						if(i>0)
							faxNo = faxNo+","+(masterDOB.getCustomerContactsFax()[i]!=null?masterDOB.getCustomerContactsFax()[i]:"");
						else
							faxNo = masterDOB.getCustomerContactsFax()[i]!=null?masterDOB.getCustomerContactsFax()[i]:"";
						System.out.println(emailId+"@@@"+contactNo+"###"+faxNo);
						//Ended to get Attention To values in Modify Case
%>
				<option value='<%=masterDOB.getCustContactNames()[i]%>'  selected><%=masterDOB.getCustContactNames()[i]%></option>   <!-- Modified by Rakesh for Issue:        on 03-01-2010 -->
<%
				  }
				}
%>
				</select><br>
				
			</td>
			
			<td  width ='10%' valign='top'>Sales Person:<font color="#ff0000">*</font><br>
				<input class="text" maxLength="25" name="salesPersonCode"  size="14" onBlur="changeToUpper(this);chrnum(this);checkAjaxSalespersonValid(this)" value="<%=masterDOB.getSalesPersonCode()!=null?masterDOB.getSalesPersonCode():createdBy%>"><!--<br>-->&nbsp;<input class="input" type="button" value="..." name="salesPerLov" onclick="openSalesPersonLov()"><br>
				<!-- onclick="popUpWindow('salesPerson')" -->
			</td>

        
			<!--@@Added by kameswari for WPBN issue-61306-->
			<td width ='10%' valign='top'>
				Email&nbsp;Copy&nbsp;to<br>Sales&nbsp;Person<input type='checkbox' name='salesPersonFlag' value='0'>
				</td>
					<!--@@WPBN issue-61306-->
			
    	</tr>
		</table>
		 <table width="103%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
		    <!-- Added by Rakesh for Issue:        on 28-02-2011 -->
			<td width='28%'>
			Cust Req Date 	<font color="#ff0000">*</font>
				<%if("Add".equalsIgnoreCase(operation)  || "Copy".equalsIgnoreCase(operation)){%>
				<input class="text" maxLength="25" name="custDate"  size="10" value="<%=custDate!=null?custDate:currentDate%>" onBlur="changeToUpper(this);return dtCheck(this)">
				<%}else{%>
				<input class="text" maxLength="25" name="custDate"  size="10" value="<%=custDate!=null?custDate:""%>" onBlur="changeToUpper(this);return dtCheck(this)">				
				<%}%>
				<input class="input" type="button" value="..." onclick="newWindow('custDate','0','0','')"><BR><BR>
				  <!-- Added by Rakesh for Issue:        on 28-02-2011 -->
		       
			</td>
			<td width='25%'>
			  Cust Req Time :&nbsp;<font color="#ff0000">*</font> <!-- onBlur="chrnum(this)" -->
			  <!--@@Added by kiran.v on 17/11/2011 for Wpbn Issue 280269-->
					<%if("Add".equalsIgnoreCase(operation) || "copy".equalsIgnoreCase(operation)){%>
				<input class="text" maxLength="6" name="custTime"  value="<%=custTime!=null?custTime:""%>"  size="10"  onblur='return IsValidTime(this)'>
				<%}	else{%>
				<input class="text" name="custTime"  onblur='return IsValidTime(this)' maxlength="6" size="10"  value="<%=custTime!=null?custTime:""%>">
				<%}%>
			</td>
		    <td  width='47%' colspan='3' valign="top"><!-- Cust Req Time:<br> onBlur="chrnum(this)" -->
				<!-- <input class="text" name="custTime"  onblur='return IsValidTime(this)' maxlength="6" size="10"  value=""> -->
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cargo Notes:
			<!-- </td>	 --><!-- Ended by Rakesh for Issue:        on 28-02-2011 -->

			<!-- <td width='30%' colspan='2' valign="top"><br> --> <!-- onBlur="chrnum(this)" -->
				<input class="text" name="overLengthCargoNotes"  maxlength="100" size='35'                value="<%=masterDOB.getOverLengthCargoNotes()!=null?masterDOB.getOverLengthCargoNotes():""%>"><!--size='26'-->
			</td>
		</tr>
		</table>
	   <table width="103%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formdata">
		<td width ='8%' valign="top">Service Level:<!--Service Level-->
				<input class="text" maxLength="16" name="serviceLevelId"  size="4" onBlur="changeToUpper(this);chrnum(this);
				checkAjaxServiceLevelValid(this)" value="<%=masterDOB.getServiceLevelId()!=null?masterDOB.getServiceLevelId():""%>">&nbsp;<input class="input" type="button" name="serviceLvlLov" value="..." onclick="popUpWindow('serviceLevel')">
			</td>
			<td  width ='11%' valign="bottom">Industry:<br><!--<br>-->
				<input class="text" maxLength="16" name="industryId"  size="10" onBlur="changeToUpper(this);chrnum(this);checkAjaxIndustryValid(this)" value="<%=masterDOB.getIndustryId()!=null?masterDOB.getIndustryId():""%>">&nbsp;<input class="input" type="button" name="industryLov" value="..." onclick="popUpWindow('industry')">
			</td>
			
			<td  width ='11%' valign="bottom">Commodity Type:<br>
	
						<select size="1" name="commodityType" class='select' style="width:300px;margin:0px 0 5px 0;">
						  <option selected>(Select)</option>
						  <option value='Edible,Animal & Vegetable Products' <%="Edible,Animal & Vegetable Products".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Edible,Animal & Vegetable Products</option>
						  <option value='AVI,Inedible Animal & Vegetable Products' <%="AVI,Inedible Animal & Vegetable Products".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>AVI,Inedible Animal & Vegetable Products</option>
						  <option value='Textiles,Fibres & Mfrs.' <%="Textiles,Fibres & Mfrs.".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Textiles,Fibres & Mfrs.</option>
						  <option value='Metals,Mfrs Excl M/C , Vehicles & Electrical Equipment' <%="Metals,Mfrs Excl M/C , Vehicles & Electrical Equipment".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Metals,Mfrs Excl M/C , Vehicles & Electrical Equipment</option>
						  <option value='Machinery,Vehicles & Electrical Equipment' <%="Machinery,Vehicles & Electrical Equipment".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Machinery,Vehicles & Electrical Equipment</option>
						  <option value='Non-Metallic Minerals & Mfrs.' <%="Non-Metallic Minerals & Mfrs.".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Non-Metallic Minerals & Mfrs.</option>
						  <option value='Chemicals & Related Products' <%="Chemicals & Related Products".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Chemicals & Related Products</option>
						  <option value='Paper,Reed,Rubber & Wood Mfrs.' <%="Paper,Reed,Rubber & Wood Mfrs.".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Paper,Reed,Rubber & Wood Mfrs.</option>
						  <option value='Scientific,Professional & Precision Instruments' <%="Scientific,Professional & Precision Instruments".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>>Scientific,Professional & Precision Instruments</option>
						  <option value='Miscellaneous' <%="Miscellaneous".equalsIgnoreCase(masterDOB.getCommodityType())?"selected":""%>> Miscellaneous</option>
						</select>
				</td>
				<!--@@MOdified by Kameswari for the WPBN issue-61318-->
			  <!-- </tr>
			   <tr  class="formdata">-->
				  <td  width ='8%' valign="top">  Commodity<br>/Product:
					<input class="text" maxLength="16" name="commodityId"  size="10" onBlur="changeToUpper(this);checkAjaxcommodityValid(this)" value="<%=masterDOB.getCommodityId()!=null?masterDOB.getCommodityId():""%>">&nbsp;
					<input class="input" type="button" value="..." onclick="popUpWindow('commodity')"></td>
				<td valign="bottom">
					Hazardous<br>
					<input type="checkbox" value="ON" name="hazardousInd" <%if(masterDOB.isHazardousInd()){%>checked<%}%>>
				</td>
				<td valign="bottom">
					UN # <br><input class="text" maxLength="16" name="unNo" size="7" onBlur="changeToUpper(this);chrnum(this)" value="<%=masterDOB.getUnNumber()!=null?masterDOB.getUnNumber():""%>">
				</td>
				<td valign="bottom">
					Class # <br><input class="text" maxLength="16" name="commodityClass"  id='class1' size="7" onBlur="changeToUpper(this);chrnum(this)" value="<%=masterDOB.getCommodityClass()!=null?masterDOB.getCommodityClass():""%>">
			   </td>
			<!--  </tr>
			 </table>
			</td>-->		
			<!--@@WPBN Issue-61318-->
		</tr>
       </table>
	   <table width="103%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
		<tr class="formheader">
			<td  colspan="9" valign="bottom">Lane Details</td>
		</tr>
		</table>
		<span id="Freight" style='DISPLAY:block'><!-- Added by Anil.k on 03-JAN-2011-->
        <table width="103%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF' id='lanedetails'>

		<%	//System.out.println("laneSize---------"+back);
			if("Modify".equalsIgnoreCase(operation))
			{
				modfyDisabled = "disabled";
			}
			//System.out.println(masterDOB.getQuoteWith()+"%%%");
			if((("Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation)) && !"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))   || ("BackButton".equalsIgnoreCase(back)&&!"Charges".equalsIgnoreCase(masterDOB.getQuoteWith()))) {
		
			int laneSize  = finalDOB.getLegDetails().size();
			System.out.println("laneSize---------"+laneSize);
			for(int i=1;i<=laneSize;i++){
				%>
		    <tr id ='tr<%=i>1?i+1:i%>' class="formdata">
            <td valign ='bottom'><input class="input" type="button" value="<<" <%=modfyDisabled%> onclick="deleteLaneRow(this,tr<%=i>1?i+1:i%>)"></td><!--Modified by Anil.k-->
			<td nowrap width="25%" valign="bottom">Org Loc:<font color="#ff0000">*</font>
			 Org Port:<font color="#ff0000">*</font><br>
			<input class="text" maxLength="16" name="originLoc" id='originLoc<%=i%>' size="5" onBlur="changeToUpper(this);chrnum(this);checkAjaxOriginDestinationValid('Origin','originLoc<%=i%>');" 
			value="<%=masterDOB.getOriginLocation()[i-1]!= null?masterDOB.getOriginLocation()[i-1]:""%>" <%=readOnly%>>
		   <input class="input" type="button" value="..." onclick="openLocationLov('Origin',<%=i%>)" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
		   <input class="text" maxLength="16" name="originPort" id='originPort<%=i%>' size="6" onBlur="changeToUpper(this);chrnum(this);checkAjaxOriginDestinationPortValid('Origin','originPort<%=i%>');checkAjaxRateCheckForTheLane('Origin','originPort<%=i%>');" value="<%=masterDOB.getOriginPort()[i-1]!= null?masterDOB.getOriginPort()[i-1]:""%>" <%=readOnly%>>&nbsp;
		   <input class="input" type="button" value="..." onclick="openOrgPortLov(<%=i%>)" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>></td>
          <td width="15%" nowrap valign="bottom">Shipper(Zipcode/Zone)<br>
			<input class="text" maxLength="36" name="shipperZipCode" id="shipperZipCode<%=i%>" size="6" onBlur="chr1(this);return validateZipCode(this);" onchange="populateCAP(<%=i%>)" value="<%=masterDOB.getShipperZipCode()[i-1]!= null?masterDOB.getShipperZipCode()[i-1]:""%>" >&nbsp;
			<input class="input" type="button" value="..." id="shipperZipCodeLOV<%=i%>" onclick="popUpWindow1('shipperZipCode',<%=i%>)">&nbsp;
			<input class="text" maxLength="250" name="shipperZone" id="shipperZone<%=i%>" size="3" onBlur="changeToUpper(this);chr(this);checkAjaxIsValidZoneCode('shipper','shipperZone<%=i%>','originLoc<%=i%>');" onchange="populateCAP(<%=i%>)" value="<%=masterDOB.getShipperZones()[i-1]!= null?masterDOB.getShipperZones()[i-1]:""%>">&nbsp;
			<input class="input" type="button" value="..." onclick="popUpWindow1('shipperZone',<%=i%>)" onBlur="populateCAP(<%=i%>)"></td>
			<td  width="25%" nowrap valign="bottom">&nbsp;&nbsp;Dest&nbsp;Loc:<font color="#ff0000">*</font>Dest&nbsp;Port:<font			color="#ff0000">*</font><br>
			<input class="text" maxLength="16" name="destLoc" id="destLoc<%=i%>" size="5"		 
			onBlur="changeToUpper(this);chrnum(this);checkAjaxOriginDestinationValid('Destination','destLoc<%=i%>')" value="<%=masterDOB.getDestLocation()[i-1]!= null?masterDOB.getDestLocation()[i-1]:""%>" <%=readOnly%>>
			<input class="input" type="button" value="..." onclick="openLocationLov('Dest',<%=i%>)" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
            <input class="text" maxLength="16" name="destPort" id='destPort<%=i%>' size="6" onBlur="changeToUpper(this);chrnum(this);checkAjaxOriginDestinationPortValid('Destination','destPort<%=i%>');checkAjaxRateCheckForTheLane('Destination','destPort<%=i%>');" value="<%=masterDOB.getDestPort()[i-1]!= null?masterDOB.getDestPort()[i-1]:""%>" <%=readOnly%>>
			<input class="input" type="button" value="..." onclick="openDestPortLov(<%=i%>)" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
			</td>
			<td  width="15%" nowrap valign="bottom">Consignee(Zipcode/Zone)<br>
				<input class="text" maxLength="36" name="consigneeZipCode" id ="consigneeZipCode<%=i%>" size="6" onBlur="chr1(this);return validateZipCode(this)" value="<%=masterDOB.getConsigneeZipCode()[i-1]!= null?masterDOB.getConsigneeZipCode()[i-1]:""%>" onchange="populateCAP(<%=i%>)">
				<input class="input" type="button" value="..."  id="consigneeZipCodeLOV<%=i%>" onclick="popUpWindow1('consigneeZipCode',1)" onBlur="populateCAP(<%=i%>)">
				<input class="text" maxLength="250" name="consigneeZone" id="consigneeZone<%=i%>" size="3" onBlur="changeToUpper(this);chr(this);checkAjaxIsValidZoneCode('consignee','consigneeZone<%=i%>','destLoc<%=i%>');" onchange="populateCAP(<%=i%>)" value="<%=masterDOB.getConsigneeZones()[i-1]!= null?masterDOB.getConsigneeZones()[i-1]:""%>">&nbsp;<input class="input" type="button" value="..." onclick="popUpWindow1('consigneeZone',<%=i%>)" onBlur="populateCAP(<%=i%>)">
			</td>
			<td nowrap > Spot Rates: <br><!-- Modified by Anil.k for Spot Rates -->
			<%if("Copy".equalsIgnoreCase(operation)){%>
			<select class="select"  id="spotRateFlag<%=i%>" name="spotRateFlag" size="1" onChange="checkAjaxRateCheckForTheLane('Destination','destPort<%=i%>');"  <%=disabled%>>
					<option value="N" <%="N".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i-1])?"selected":""%>>NO</option>
					<option value="Y" <%="Y".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i-1])?"selected":""%>>YES</option>
				</select> 
			<%}else{%>
		<select class="select"  id="spotRateFlag<%=i%>" name="spotRateFlag" size="1" onChange="checkAjaxRateCheckForTheLane('Destination','destPort<%=i%>');"  <%=disabled%>>
					<!-- <option value="N" <%=!isSpotRatesFlag?"selected":""%>>NO</option>
					<option value="Y" <%=isSpotRatesFlag?"selected":""%>>YES</option> -->
					<option value="N" <%="N".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i-1])?"selected":""%>>NO</option>
					<option value="Y" <%="Y".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i-1])?"selected":""%>>YES</option>
				</select> 
			<%}%>
		</td>
		<td valign ='bottom' colspan='2'><input class="input" type="button" value=">>" <%=modfyDisabled%> onclick="addLaneRow(this,tr<%=i>1?i+1:i%>)"></td><!-- Modified by Anil.k -->
		</tr>
		<tr id='tr<%=i>1?i+2:i+1%>'class="formdata">
        <td colspan='2' valign ='bottom'>Incoterms:<font color='#ff0000'>*</font>
					<select class='select'  name='incoTerms' id='incoTerms<%=i%>' size='1' onchange='populateCAP(<%=i%>)'>
					<option value='CFR' <%="CFR".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>CFR</option>
					<option value='CIF' <%="CIF".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>CIF</option>
					<option value='CIP' <%="CIP".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>CIP</option>
					<option value='CPT' <%="CPT".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>CPT</option>
					<option value='DAP' <%="DAP".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>DAP</option>
					<option value='DAT' <%="DAT".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>DAT</option>
					<option value='DDP' <%="DDP".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>DDP</option>
					<option value='EXW' <%="'EXW".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>EXW</option>
					<option value='FAS' <%="FAS".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>FAS</option>
					<option value='FCA' <%="FCA".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>FCA</option>
					<option value='FOB' <%="FOB".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>FOB</option>
		            
			<!--@@Coommented by Anusha for the WPBN ISSUE:380917 -->	
                <!--   <option value='DAF' <%="DAF".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>DAF</option>
					<option value='DEQ' <%="DEQ".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>DEQ</option>
                    <option value='DES' <%="DES".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>DES</option>
                    <option value='DDU' <%="DDU".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>DDU</option>
			         <option value='DDU CC' <%="DDU CC'".equalsIgnoreCase(masterDOB.getIncoTermsId()[i-1])?"selected":""%>>DDU CC</option>-->
			<!--@@Coommented by Anusha for the WPBN ISSUE:380917 -->	
					</select></td>
			<%System.out.println("masterDOB.getIncoTermsId()[i-1]"+"i--"+i+"--------"+masterDOB.getIncoTermsId()[i-1]);%>
					<td colspan='2'>Named Place :<font color="#ff0000">*</font>
					<select class='select' valign='bottom' name='cargoAcceptance'id='cargoAcceptance<%=i%>' onchange='populateCAP(<%=i%>)'  style='width:110px;margin:0px 0 5px 0;'>
					<option value=''></option>
					<option value='ZIPCODE' <%="ZIPCODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i-1])?"selected":""%>>Zip Code</option>
					<option value='ZONECODE' <%="ZONECODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i-1])?"selected":""%>>Zone Code</option>
					<option value='DDAO' <%="DDAO".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i-1])?"selected":""%>>DGF Terminal</option>
					<option value='PORT' <%="PORT".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i-1])?"selected":""%>>Port</option>
					<option value='OTHER' <%="OTHER".equalsIgnoreCase(masterDOB.getCargoAcceptance()[i-1])?"selected":""%>>Other</option>
					</select>
					
					<textarea name='cargoAccPlace' class="text" id='cargoAccPlace<%=i%>' onBlur="return check_Length(255,this);"  cols="12" rows='4' style="height:30"  rows='5' value ="<%=masterDOB.getCargoAccPlace()[i-1]!= null ?masterDOB.getCargoAccPlace()[i-1]:""%>"><%=masterDOB.getCargoAccPlace()[i-1]!= null ?masterDOB.getCargoAccPlace()[i-1]:""%></textarea>
					</td>
					<%if(("Copy".equalsIgnoreCase(operation) || "BackButton".equalsIgnoreCase(back) ) && "Y".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i-1]) ) {%>
                    <td colspan ='2'>
		            <span id='spotSurcharges<%=i%>' style='DISPLAY:block' class="formdata">
		             SpotRate Surcharges Count:
                    <INPUT TYPE="text" class="text" NAME="spotRateSurchargeCount" id="spotRateSurchargeCount<%=i%>" value = "<%="BackButton".equalsIgnoreCase(back) && masterDOB.getSpotrateSurchargeCount()[i-1]!=null?masterDOB.getSpotrateSurchargeCount()[i-1]:"0"%>" size="5" <%="Modify".equalsIgnoreCase(operation)?"readonly":""%> onBlur = 'setSpotSurchargeCountvalue(this,<%=i%>)'>
		             </span>
                     <INPUT TYPE='hidden'  NAME="spotRateSurchargeCount1" id="spotRateSurchargeCount1<%=i%>" value="<%=masterDOB.getSpotrateSurchargeCount()[i-1]!=null?masterDOB.getSpotrateSurchargeCount()[i-1]:"0"%>">
		             </td>
                    
					<td nowrap width='10%'><!-- Carrier Id:<font color="#ff0000">*</font><br>
              <INPUT TYPE="text" class="text" NAME="CarriedIdLOV" size='5'>
              <input class="input" type="button" value="..." onclick="" "disabled":""%> -->
			</td>
					<%}else if("Modify".equalsIgnoreCase(operation) && "Y".equalsIgnoreCase(masterDOB.getSpotRatesFlag()[i-1]) ){
			//	System.out.println("123456"+masterDOB.getSpotrateSurchargeCount()[i-1]);%>
					<td nowrap colspan ='2'>
					 <span id='spotSurcharges<%=i%>' style='DISPLAY:block' class="formdata">
		             SpotRate Surcharges Count:
                    <INPUT TYPE="text" class="text" NAME="spotRateSurchargeCount" id="spotRateSurchargeCount<%=i%>" value = "<%=masterDOB.getSpotrateSurchargeCount()[i-1]!=null?masterDOB.getSpotrateSurchargeCount()[i-1]:""%>" size="5" readonly  onBlur = 'setSpotSurchargeCountvalue(this,<%=i%>)'>
		             </span>
                  <INPUT TYPE='hidden'  NAME="spotRateSurchargeCount1" id="spotRateSurchargeCount1<%=i%>" value="<%=masterDOB.getSpotrateSurchargeCount()[i-1]!=null?masterDOB.getSpotrateSurchargeCount()[i-1]:"0"%>"></td>
		             </td>
					<td nowrap width='10%'><!-- Carrier Id:<font color="#ff0000">*</font><br>
              <INPUT TYPE="text" class="text" NAME="CarriedIdLOV" size='5'>
              <input class="input" type="button" value="..." onclick="" "disabled":""%> -->
			</td>
					
					<%}else{%>
					<td colspan ='2'>
			<INPUT TYPE='hidden'  NAME="spotRateSurchargeCount1" id="spotRateSurchargeCount1<%=i%>" value='0'></td>
					<td nowrap width='10%'><!-- Carrier Id:<font color="#ff0000">*</font><br>
              <INPUT TYPE="text" class="text" NAME="CarriedIdLOV" size='5'>
              <input class="input" type="button" value="..." onclick="" "disabled":""%> -->
			</td>
					
					<%}%>
		</tr> 
             
            <%}//for end%>
</table>
		<%}else{%>
		<tr id ='tr1'class="formdata">
			
			<td valign ='bottom'><input class="input" type="button" value="<<" onclick="deleteLaneRow(this,tr1)"></td>
			<td nowrap width="25%" valign="bottom">Org Loc:<font color="#ff0000">*</font>
			      Org Port:<font color="#ff0000">*</font><br>
				<input class="text" maxLength="16" name="originLoc" id='originLoc1' size="5" onBlur="changeToUpper(this);chrnum(this);checkAjaxOriginDestinationValid('Origin','originLoc1')" value="" <%=readOnly%>>
				<input class="input" type="button" value="..." onclick="openLocationLov('Origin',1)" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
				<input class="text" maxLength="16" name="originPort" id='originPort1' size="6" onBlur="changeToUpper(this);chrnum(this);checkAjaxOriginDestinationPortValid('Origin','originPort1');checkAjaxRateCheckForTheLane('Origin','originPort1');" value="" <%=readOnly%>>&nbsp;<input class="input" type="button" value="..." onclick="openOrgPortLov(1)" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
			</td>
		<td width="15%" nowrap valign="bottom">Shipper(Zipcode/Zone)<br>
				<input class="text" maxLength="36" name="shipperZipCode" id="shipperZipCode1" size="6" onBlur="chr1(this);return validateZipCode(this);" onchange="populateCAP(1)" value="" >&nbsp;<input class="input" type="button" value="..."  id="shipperZipCodeLOV1"  onclick="popUpWindow1('shipperZipCode',1)">&nbsp;<input class="text" maxLength="250" name="shipperZone" id="shipperZone1" size="3" onBlur="changeToUpper(this);chr(this);checkAjaxIsValidZoneCode('shipper','shipperZone1','originLoc1');" onchange="populateCAP(1)" value="">&nbsp;<input class="input" type="button" value="..." onclick="popUpWindow1('shipperZone',1)" onBlur="populateCAP(1)">
			</td>
			<td  width="30%" valign="bottom">Dest&nbsp;Loc:<font color="#ff0000">*</font>&nbsp;Dest&nbsp;Port:<font			color="#ff0000">*</font><br><!--modified by silpa.p on 5-04-11-->
					<input class="text" maxLength="16" name="destLoc" id="destLoc1" size="5"		 
					onBlur="changeToUpper(this);chrnum(this);checkAjaxOriginDestinationValid('Destination','destLoc1')" value="" <%=readOnly%>>
				<input class="input" type="button" value="..." onclick="openLocationLov('Dest',1)" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>&nbsp;
                <input class="text" maxLength="16" name="destPort" id='destPort1' size="6" onBlur="changeToUpper(this);chrnum(this);checkAjaxOriginDestinationPortValid('Destination','destPort1');checkAjaxRateCheckForTheLane('Destination','destPort1');" value="" <%=readOnly%>>
				<input class="input" type="button" value="..." onclick="openDestPortLov(1)" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
			</td>
			
			<td  width="15%" nowrap valign="bottom">Consignee(Zipcode/Zone)<br>
				<input class="text" maxLength="36" name="consigneeZipCode" id ="consigneeZipCode1" size="6" onBlur="chr1(this);return validateZipCode(this)" value="" onchange="populateCAP(1)">
				<input class="input" type="button" value="..." id="consigneeZipCodeLOV1"onclick="popUpWindow1('consigneeZipCode',1)" onBlur="populateCAP(1)">
				<input class="text" maxLength="250" name="consigneeZone" id="consigneeZone1" size="3" onBlur="changeToUpper(this);chr(this);checkAjaxIsValidZoneCode('consignee','consigneeZone1','destLoc1');" onchange="populateCAP(1)" value="">&nbsp;<input class="input" type="button" value="..." onclick="popUpWindow1('consigneeZone',1)" onBlur="populateCAP(1);">
			</td>
			
			<td nowrap > Spot Rates: <br><!-- Modified by Anil.k for Spot Rates -->
		<select class="select"  id="spotRateFlag1" name="spotRateFlag" size="1" onchange="displaySpotrateSurchargeCount(this,2)" <%=disabled%>>
					<option value="N" <%=!isSpotRatesFlag?"selected":""%>>NO</option>
					<option value="Y" <%=isSpotRatesFlag?"selected":""%>>YES</option>
				</select> 
		</td>
		<td valign ='bottom' colspan='2'><input class="input" type="button" value=">>" onclick="addLaneRow(this,tr1)"></td>
			
		</tr>
       
		<tr id='tr2'class="formdata">
		
		<td colspan='2' valign='bottom'>Incoterms:<font color="#ff0000">*</font>
				<select class="select"  name="incoTerms" id ="incoTerms1" size="1" onchange='populateCAP(1)'>
				   <option value="CFR" >CFR</option>
					<option value="CIF" >CIF</option>
					<option value="CIP" >CIP</option>
					<option value="CPT" >CPT</option>
					<option value="DAP" >DAP</option>
					<option value="DAT" >DAT</option>
					<option value="DDP" >DDP</option>
					<option value="EXW" >EXW</option>
					<option value="FAS" >FAS</option>
					<option value="FCA" >FCA</option>
					<option value="FOB" >FOB</option>
					
				<!--@@Coommented by Anusha for the WPBN ISSUE:380917 -->	
					<!--<option value="DAF" >DAF</option>
					<option value="DEQ" >DEQ</option>
					<option value="DES" >DES</option>
					<option value="DDU" >DDU</option>
					<option value="DDU CC" >DDU CC</option>-->
				<!--@@Coommented by Anusha for the WPBN ISSUE:380917 -->	
				</select>
		</td>	
			<td colspan='2'>Named Place :<font color="#ff0000">*</font>
				<select class='select' valign='bottom' name='cargoAcceptance'  id ='cargoAcceptance1' onchange='populateCAP(1)' style="width:110px;margin:0px 0 5px 0;">
					<option value=''></option>
					<option value='ZIPCODE'>Zip Code</option>
					<option value='ZONECODE' >Zone Code</option>
					<option value='DDAO' >DGF Terminal</option>
					<option value='PORT' >Port</option>
					<option value='OTHER' >Other</option>
				</select>
				<textarea name='cargoAccPlace' id='cargoAccPlace1' class="text" onBlur="return check_Length(255,this)"  cols="12" rows='4' style="height:30" rows='5'></textarea>
				
		</td>
		<td nowrap colspan ='2'>
		<span id="spotSurcharges2" style='DISPLAY:none' class="formdata">
		SpotRate Surcharges Count:
         <INPUT TYPE="text" class="text" NAME="spotRateSurchargeCount" id="spotRateSurchargeCount2"  value = "" size="5" onBlur = 'setSpotSurchargeCountvalue(this,"2")'>
		</span>
       <INPUT TYPE='hidden'  NAME="spotRateSurchargeCount1" id="spotRateSurchargeCount12" value='0'>
		</td>
			<td nowrap width='10%'><!-- Carrier Id:<font color="#ff0000">*</font><br>
              <INPUT TYPE="text" class="text" NAME="CarriedIdLOV" size='5'>
              <input class="input" type="button" value="..." onclick="" "disabled":""%> -->
			</td>
			
		</tr>
		</table>
		<%
}%>
<!-- Added by Anil.k on 03-JAN-2011 -->
</span>
<span id="Charges" style='DISPLAY:none' class="formdata">
 <table width="103%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF' id='LaneDetails'>
<tr id='tr' class="formdata">
		
		<td width="10%" valign="bottom">Origin Location:<font color="#ff0000">*</font>
		<input class="text" maxLength="16" name="originLocCharge" id='originLocCharge' size="12" onBlur="changeToUpper(this);chrnum(this);checkAjaxOriginDestinationValidForCharges('Origin','originLocCharge')" onChange="appChrg()" value="<%=finalDOB!= null?masterDOB.getOriginLocation()[0]:""%>" <%=readOnly%>>
		<input class="input" type="button" value="..." onclick="openLocationLov('Origin','Charge')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
		</td>
		<!--Added by Anil.k on 1Mar2011-->		
		<td width="10%" nowrap valign="bottom">Shipper(Zipcode/Zone)<br>
		<input class="text" maxLength="36" name="shipperZipCodeChg" id="shipperZipCodeChg" size="6" onBlur="chr1(this);return validateZipCode(this);" onchange="populatechargesCAP()" value="<%= masterDOB.getShipperZipCode()!=null?(masterDOB.getShipperZipCode()[0]!=null?masterDOB.getShipperZipCode()[0]:""):""%>" >&nbsp;<input class="input" type="button" value="..." onclick="popUpWindow1('shipperZipCodeChg','Chg')">&nbsp;<input class="text" maxLength="250" name="shipperZoneChg" id="shipperZoneChg" size="3" onBlur="changeToUpper(this);chr(this)" onchange="populatechargesCAP()" value="<%=masterDOB.getShipperZones()!=null?(masterDOB.getShipperZones()[0]!=null?masterDOB.getShipperZones()[0]:""):""%>">&nbsp;<input class="input" type="button" value="..." onclick="popUpWindow1('shipperZoneChg','Chg')" onBlur="populatechargesCAP()"></td>
		<!-- Ended by Anil.k on 1Mar2011-->
		<td  width="10%" valign="bottom">Destination&nbsp;Location:<font color="#ff0000">*</font><br>
		<input class="text" maxLength="16" name="destLocCharge" id="destLocCharge" size="12"		 
		onBlur="changeToUpper(this);chrnum(this);checkAjaxOriginDestinationValidForCharges('Destination','destLocCharge')" onChange="appChrg()" value="<%=finalDOB!= null?masterDOB.getDestLocation()[0]:""%>" <%=readOnly%>>
		<input class="input" type="button" value="..." onclick="openLocationLov('Dest','Charge')" <%="readOnly".equalsIgnoreCase(readOnly)?"disabled":""%>>
		</td>
		<!--Added by Anil.k on 1Mar2011-->
		<td  width="15%" nowrap valign="bottom">Consignee(Zipcode/Zone)<br>
		<input class="text" maxLength="36" name="consigneeZipCodeChg" id ="consigneeZipCodeChg" size="6" onBlur="chr1(this);return validateZipCode(this)" value="<%=masterDOB.getConsigneeZipCode()!=null?(masterDOB.getConsigneeZipCode()[0]!=null?masterDOB.getConsigneeZipCode()[0]:""):""%>" onchange="populatechargesCAP()">
		<input class="input" type="button" value="..." onclick="popUpWindow1('consigneeZipCodeChg',1)" onBlur="populatechargesCAP()">
		<input class="text" maxLength="250" name="consigneeZoneChg" id="consigneeZoneChg" size="3" onBlur="changeToUpper(this);chr(this)" onchange="populatechargesCAP()" value="<%=masterDOB.getConsigneeZones()!=null?(masterDOB.getConsigneeZones()[0]!=null?masterDOB.getConsigneeZones()[0]:""):""%>">&nbsp;<input class="input" type="button" value="..." onclick="popUpWindow1('consigneeZoneChg',1)" onBlur="populatechargesCAP()">
		</td>
		<!--Ended by Anil.k on 1Mar2011-->
</tr>
       <tr  class="formdata">
	   <%if("Modify".equalsIgnoreCase(operation) || "Copy".equalsIgnoreCase(operation)   || ("BackButton".equalsIgnoreCase(back))) {%>
		
          <td colspan='1' valign="bottom">Incoterms:<font color="#ff0000">*</font>
		  <select class="select"  name="chargeincoTerms" id ="chargeincoTerms" size="1" onchange='populatechargesCAP()'>
		            <option value='CFR' <%="CFR".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>CFR</option>
					<option value='CIF' <%="CIF".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>CIF</option>
					<option value='CIP' <%="CIP".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>CIP</option>
					<option value='CPT' <%="CPT".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>CPT</option>
					<option value='DAP' <%="DAP".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>DAP</option>
					<option value='DAT' <%="DAT".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>DAT</option>
					<option value='DDP' <%="DDP".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>DDP</option>
					 <option value='EXW' <%="EXW".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>EXW</option>
					 <option value='FAS' <%="FAS".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>FAS</option>
					<option value='FCA' <%="FCA".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>FCA</option>
					<option value='FOB' <%="FOB".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>FOB</option>
					
				<!--@@Coommented by Anusha for the WPBN ISSUE:380917 -->		
					<!--<option value='DAF' <%="DAF".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>DAF</option>
					<option value='DEQ' <%="DEQ".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>DEQ</option>
					<option value='DES' <%="DES".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>DES</option>
				    <option value='DDU' <%="DDU".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"selected":""%>>DDU</option>
					<option value="DDU CC" <%="'DDU CC'".equalsIgnoreCase(masterDOB.getIncoTermsId()[0])?"'selected'":""%>>DDU CC</option>-->
				<!--@@Coommented by Anusha for the WPBN ISSUE:380917 -->		
					</select></td>
					<td colspan='2'>Named Place :<font color="#ff0000">*</font>
					<select class='select' valign='bottom' name='chargecargoAcceptance'id='chargecargoAcceptance' onchange='populatechargesCAP()'  style='width:110px;margin:0px 0 5px 0;'>
					<option value=''></option>
					<option value='ZIPCODE' <%="ZIPCODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()[0])?"selected":""%>>Zip Code</option>
					<option value='ZONECODE' <%="ZONECODE".equalsIgnoreCase(masterDOB.getCargoAcceptance()[0])?"selected":""%>>Zone Code</option>
					<option value='DDAO' <%="DDAO".equalsIgnoreCase(masterDOB.getCargoAcceptance()[0])?"selected":""%>>DGF Terminal</option>
					<option value='OTHER' <%="OTHER".equalsIgnoreCase(masterDOB.getCargoAcceptance()[0])?"selected":""%>>Other</option>
					</select>
					<textarea name='chargecargoAccPlace' class="text" id='chargecargoAccPlace' onBlur="return check_Length(255,this);"  cols="16" rows='4' style="height:30"  rows='5' value ="<%=masterDOB.getCargoAccPlace()[0]!= null ?masterDOB.getCargoAccPlace()[0]:""%>"><%=masterDOB.getCargoAccPlace()[0]!= null ?masterDOB.getCargoAccPlace()[0]:""%></textarea>
		</td>	
		<%}else{%>
		   
               <td colspan='2' valign="bottom">Incoterms:<font color="#ff0000">*</font>
				<select class="select"  name="chargeincoTerms" id ="chargeincoTerms" size="1" onchange='populatechargesCAP()'>
					<option value="CFR" >CFR</option>
					<option value="CIF" >CIF</option>
					<option value="CIP" >CIP</option>
					<option value="CPT" >CPT</option>
					<option value="DAP" >DAP</option>
					<option value="DAT" >DAT</option>
					<option value="DDP" >DDP</option>
					<option value="EXW" >EXW</option>
					<option value="FAS" >FAS</option>
					<option value="FCA" >FCA</option>
					<option value="FOB" >FOB</option>
				<!--@@Coommented by Anusha for the WPBN ISSUE:380917 -->	
					<!--<option value="DAF" >DAF</option>
					<option value="DEQ" >DEQ</option>
					<option value="DES" >DES</option>
					<option value="DDU" >DDU</option>
					<option value="DDU CC" >DDU CC</option>-->
				<!--@@Coommented by Anusha for the WPBN ISSUE:380917 -->	
				</select>
		</td>	
			<td colspan='2'>Named Place :<font color="#ff0000">*</font>
				<select class='select' valign='bottom' name='chargecargoAcceptance'  id ='chargecargoAcceptance' onchange='populatechargesCAP()' style="width:100px;margin:0px 0 5px 0;">
					<option value=''></option>
					<option value='ZIPCODE'>Zip Code</option>
					<option value='ZONECODE' >Zone Code</option>
					<option value='DDAO' >DGF Terminal</option>
					<option value='OTHER' >Other</option>
				</select>
				<textarea name='chargecargoAccPlace' id='chargecargoAccPlace' class="text" onBlur="return check_Length(255,this)"  cols="10" rows='4' style="height:30"  rows='5'></textarea>
				
		</td>     

		<%}%>
	   </tr>
</table>
<table width="103%"" cellpadding="4" cellspacing="1" nowrap id="QuoteChargeGroups" idcounter="1" defaultElement="chargeGroupId" xmlTagName="QuoteChargeGroups" bgcolor='#FFFFFF'>
		
		<tr class='formheader'> 
			<td colspan="14" valign="bottom"> Applicable Charge Groups</td>
		</tr>

	</table>
</span>
<!-- END -->
<%for(int i=0;i<noOfLegs;i++)
{
		if(legDetails!=null)
		{
			legDOB					=	(MultiQuoteFreightLegSellRates)legDetails.get(i);

			legShipmentMode			=	""+legDOB.getShipmentMode();
				
			if(legDOB.isSpotRatesFlag())
			{
				/*isSpotRatesFlag			=	true;
				spotRateType			=	legDOB.getSpotRatesType();
				weightBreaks			=	legDOB.getWeightBreaks();
				weightBreaksSize		=	weightBreaks.size();
				if(legDOB.getSpotRateDetails()!=null)
					spotRateDetails		=	legDOB.getSpotRateDetails();

				legServiceLevel			=	legDOB.getServiceLevel();
				legUOM					=	legDOB.getUom();
				legCurrency             =   legDOB.getCurrency();
				legDensityRatio			=	legDOB.getDensityRatio();*/
				leg						=	"("+legDOB.getOrigin()+"-"+legDOB.getDestination()+")";

			}
			else
			{
				leg						=	"("+legDOB.getOrigin()+"-"+legDOB.getDestination()+")";
			}
		}
}
%>
	

		
	<table width="103%"" cellpadding="4" cellspacing="1" nowrap id="QuoteContent" idcounter="1" defaultElement="headerFooter" xmlTagName="QuoteContent" bgcolor='#FFFFFF'>

		<tr class='formheader'> 
			<td colspan="13" valign="bottom"> Content on Quote (Default Selected if Left Empty) </td>
		</tr>

		<tr class='formheader'> 
			<td></td>
			<td align="center" valign="bottom">Header/Footer</td>
			<td align="center" valign="bottom">Content</td>
			<td align="center" valign="bottom">Level</td>
			<td align="center" valign="bottom">Align</td>
			<td align="center" valign="bottom"></td>
		</tr>
	
	</table>
		
	<table width="103%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>

		<tr class="text">
			<td  align="left" valign="bottom"><font color="#ff0000">*</font>Denotes Mandatory</font></td>
			<td  align="right">
				<input type="hidden" name="Operation" value="<%=operation%>">
				<input type="hidden" name="originLocationName">
				<input type="hidden" name="destLocationName">
				<input type="hidden" name="portName">
				<input type="hidden" name="incompletescreen"  value ="<%=masterDOB.isIncomplete_screen()%>">
				<input type="hidden" name="subOperation" value="MASTER" >
				 <input class="input" name="submit" id='SaveExit' type="submit" value="Save & Exit"  > 
				<input class="input" name="submit" type="submit" value="Next >>" >
				<input type="hidden" name="contactIds" value="<%=custContactIds!=null?custContactIds:""%>"> <!-- //added  by rk -->
			    <input type="hidden" name="checkFlag" value=""> 
					 <!-- @@Added by VLAKSHMI for the WPBN issue-167677 -->
				 <input type="hidden" name="quoteName" value="<%= masterDOB.getQuoteId()!=null? masterDOB.getQuoteId():""%>">

					 <input type="hidden" name="quoteStatus" value="<%= quoteStatus%>">
         	 <!-- @@WPBN issue-167677 -->

			
				 <input type="hidden" name="completeFlag" value="<%= completeFlag%>">
				 	 <!-- @@WPBN issue-167677 -->

			   	 <!-- Added by Phani for the WPBN issue-167678 -->
			   <input type="hidden" name="userModifiedMailIds" value="">
			   <!-- Modified to get Attention To values in Modify Case -->
			   <input type="hidden" name="attentionCustomerId" value='<%=customerId!=""?customerId:""%>'>
			   <input type="hidden" name="attentionSlno" value='<%=attentionSlno!=""?attentionSlno:""%>'>
			   <input type="hidden" name="attentionEmailId" value='<%=emailId!=""?emailId:""%>'>
			   <input type="hidden" name="attentionFaxNo" value='<%=faxNo!=""?faxNo:""%>'>		
          <input type="hidden" name="attentionContactNo" value='<%=contactNo!=""?contactNo:""%>'>	
			   <!--ends 167678 -->
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
	//Logger.error(FILE_NAME,"Error in the jsp  :"+e.toString());
  logger.error(FILE_NAME+"Error in the jsp  :"+e.toString());
}
finally
{
	masterDOB			=	null;
	spotRateDetails		=	null;
	weightBreaks		=	null;
	weightBreaksItr		=	null;
}
%>