<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSMultiQuoteFreightSellRates.jsp
Product Name		: QMS
Module Name			: Quote
Task				: Adding/Modify
Date started		: 
Date modified		:
Author    			: Govind
Related Document	: CR_DHLQMS_219979

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
  private static final String FILE_NAME	=	"QMSMultiQuoteFreightSellRates.jsp"; %>
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
int				int_count				=	0;
int                             slabBothCount       =   0;
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
String[]				   spotRateFlag		= null;//request.getParameterValues("spotRateFlag");//Added by Anil.k for Spot Rates
String						spotRateFlag1  = "";
Hashtable spotRateDetails  = new Hashtable();
ArrayList weightBreakSlabs = new ArrayList();
LinkedHashSet srWeightBreak= new LinkedHashSet();
ArrayList rateDescription  = new ArrayList();
ArrayList chargeRateIndicator = new ArrayList();
ArrayList surChargeId	   = new ArrayList();
LinkedHashSet currencyId   = null;
ArrayList checkedFlagValue = new ArrayList();
ArrayList marginType	   = new ArrayList();
ArrayList marginValue	   = new ArrayList();
String	  carrierId		   = null;
String	  serviceLevel	   = null;
String    uom			   = null;
String    densityRatio	   = null;
String	  frequency		   = null;
String	  transitTime	   = null;//Ended by Anil.k for Spot Rates
//Commmented by Kishore
//System.out.println(spotRateFlag[0]+"%%%%"+request.getParameterValues("spotRateFlag"));


try
{
	
	System.out.println(spotRateFlag1+"$$$$");
	quoteStatus = request.getParameter("quoteStatus");
	completeFlag = request.getParameter("completeFlag");
	eSupplyDateUtility.setPatternWithTime(loginbean.getUserPreferences().getDateFormat());

	java.text.DecimalFormat df	=new java.text.DecimalFormat("##0.00");

	operation			=	request.getParameter("Operation");
	quoteName			=	request.getParameter("quoteName"); // @@Added by VLAKSHMI for the WPBN issue-167677
	finalDOB			=	(MultiQuoteFinalDOB)request.getAttribute("finalDOB");
	if(finalDOB==null)
		finalDOB		=	(MultiQuoteFinalDOB)session.getAttribute("finalDOB");
	masterDOB			=	finalDOB.getMasterDOB();
	freightRates		=	finalDOB.getLegDetails();
	originLocation		=	masterDOB.getOriginLocation();
	backButton = (String)request.getAttribute("BackButton");
	if(finalDOB!=null && finalDOB.getFlagsDOB()!=null&&finalDOB.getFlagsDOB().getCompleteFlag()!=null)//modified by VLAKSHMI for issue 169959 on 07/05/09
	{
	if("C".equalsIgnoreCase(finalDOB.getFlagsDOB().getCompleteFlag()))
        completeFlag="COMPLETE";
   else if("I".equalsIgnoreCase(finalDOB.getFlagsDOB().getCompleteFlag()))
       completeFlag="INCOMPLETE";
	}
	System.out.println("completeFlag-----------"+completeFlag);

	// Added By Kishore Podili 
	if(spotRateFlag==null)
		spotRateFlag = masterDOB.getSpotRatesFlag();

	for(int i1=0;i1<spotRateFlag.length;i1++)//Added by Anil.k for Spot Rates
	{
		if(i1==0)
		spotRateFlag1 = spotRateFlag[i1];
	    else
		spotRateFlag1 = spotRateFlag1+","+spotRateFlag[i1];
	}//Ended by Anil.k for Spot Rates

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
	cellobj.innerHTML  = "<input class=input type=button value='<<' onclick=deleteChargeRow(this,'tr"+laneno+rowid+"',"+laneno+")>";
	cellobj  =	rowobj.insertCell(1);
    cellobj.setAttribute("align","center");
    cellobj.innerHTML  = "<input class=text maxLength=16 name=chargeGroupIds"+laneno+" id=chargeGroupIds"+laneno+rowid+"									size=28 onblur = 'changeToUpper(this)' >  "+
							"<input class=input type=button name=chargeGroupIdBt value=...   id=chargeGroupIdBt"+laneno+rowid+"    onclick=popUpWindow(this,"+laneno+","+rowid+") >  "+
							"<input class=input type=button name=chargeGroupIdDet value='VeiwDetails' onclick=popUpWindow(this,"+laneno+","+rowid+") >";
    cellobj  =	rowobj.insertCell(2);
    cellobj.innerHTML  = "<input class=input type=button value= '>>' onclick=addNewChargeRow(this,'tr"+laneno+rowid+"',"+laneno+")>";
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
	var shipmentMode  = '<%=masterDOB.getShipmentMode()%>';
	var Url	=	'';
	var Bars = '';
	var Features = '';
	var Options	= '';
	var btnId;
	var searchString;
	 if(shipmentMode == "1"){
        shipmentMode = "Air";
      }else if(shipmentMode == "2"){
       shipmentMode = "Sea";
      }else if(shipmentMode == "4"){
       shipmentMode = "Truck";
      }
	Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=no';
	Options='width=400,height=300,resizable=yes';

	if(input.name=='chargeGroupIdBt')
	{
		var originLocCharge = document.getElementById("originLocCharge"+laneno).value;//Added by Anil.k for Enhancement 231214 on 25Jan2011
		var destLocCharge	= document.getElementById("destLocCharge"+laneno).value;//Added by Anil.k for Enhancement 231214 25Jan2011		
		searchString=document.getElementById("chargeGroupIds"+laneno+rowno).value;
		Url='etrans/QMSMultiQuoteLOVChargeGroupIds.jsp?Operation=Modify&searchString='+searchString+'&name=chargeGroupIds'+laneno+rowno+'&terminalId='+terminalId+'&shipmentMode='+shipmentMode+'&accessLevel='+accsLvl+'&fromWhere=MultiQuote&originLocation='+originLocCharge+'&destLocation='+destLocCharge;//Modified by Anil.k for Enhancement 231214 25Jan2011
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
		Options='width=800,height=600,resizable=yes';
	}
	//alert(Url)
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features); 
}
   



function validate()
{
var str= '';
var res='';
var spra = '<%=spotRateFlag1%>';//Added by Anil.k for Spot Rates
var spotRatesValues = spra.split(",");//Added by Anil.k for Spot Rates
var type='<%=masterDOB.getMultiquoteweightBrake()%>';
	<% int k1=0; %>
	for(i=0;i<noOfLegs;i++)
	{
		if(spotRatesValues[i] == "N"){//Added by Anil.k for Spot Rates
		str ='lane'+i;
		var radio	=	document.getElementsByName("leg"+i);
		for(j=0;j<radio.length;j++){
		//	alert(document.getElementById("hid"+i+j).value.length)
		if(radio.length > 0 && document.getElementById("hid"+i+j).value.length!=0 && document.forms[0].btnName.value=='Next >>')
		{  
            str =''; 
			break;
			/*alert("Please select one sell rate in Leg "+(i+1));
			radio[0].focus();*/
		//	return false;
		}
		}
		if(str != '' || str != null)
		res = (res + str);
		}else{//Added by Anil.k for Spot Rates
		var wtBreak = '<%=masterDOB.getMultiquoteweightBrake()%>';		
		if(document.getElementById("carrierId"+i).value.length==0 && document.forms[0].btnName.value=='Next >>')
			{alert("Please Enter Carrier at lane"+i);return false;}
		
		if(document.getElementById("serviceLevel"+i).value.length==0 && document.forms[0].btnName.value=='Next >>')
			{alert("Please Enter Service Level at lane"+i);return false;}
		 if(document.getElementById("frequency"+i).value.length==0 && document.forms[0].btnName.value=='Next >>')
			{alert("Please Enter Frequency at lane"+i);return false;} 
		if(document.getElementById("currencyId"+i).value.length==0 && document.forms[0].btnName.value=='Next >>')
			{alert("Please Enter Currency at lane"+i);return false;}
		if(document.getElementById("transitTime"+i).value.length==0 && document.forms[0].btnName.value=='Next >>')
			{alert("Please Enter Transit Time at lane"+i);return false;}


		/*if(document.getElementById("freightFlagValue"+i).value.length==0 && document.forms[0].btnName.value=='Next >>')
			{alert("Please Enter FREIGHT charges  at lane"+i);return false;}*/

			//alert(document.getElementsByName("flatRate"+i).length);
//@ added by silpa for validations
			if(type == "Flat")
			{
				
for(j=0;j<(document.getElementsByName("flatRate"+i).length);j++)
			{
	
	
		if(document.getElementById("hiddenfaltrate"+j+i).value.length==0 && document.forms[0].btnName.value=='Next >>')
			{alert("Please Enter Freight charges  at lane"+i);return false;}
			}
			}
			
				else if(type == "Slab")
				{


	//alert(document.getElementsByName("slabWeightBreak"+i).length);
for(j=0;j<(document.getElementsByName("slabRate"+i).length);j++)
			{
	//alert(document.getElementById("slabrate"+j+i).value);
	
		if(document.getElementById("slabWeightBreak"+i+j).value!="" && document.getElementById("slabRate"+i+j).value.length==0 && document.forms[0].btnName.value=='Next >>')
			{alert("Please Enter FREIGHT RATES  at lane"+i);return false;}
			}
				}
		
				
else if(type == "List")
				{


	//alert(document.getElementsByName("listRate"+i).length);
for(j=0;j<(document.getElementsByName("listRate"+i).length);j++)
			{
	//alert(document.getElementById("listRate"+i+j).value);
	
		if(document.getElementById("listWeightBreak"+i+j).value!="" && document.getElementById("listRate"+i+j).value.length==0 && document.forms[0].btnName.value=='Next >>')
			{alert("Please Enter  RATES  at lane"+i);return false;}
			}
				
				  }//@ ended by silpa  for validations
		}//Ended by Anil.k for Spot Rates*/
	}
	
	if(res.length >0 && document.forms[0].btnName.value != "<< Back")
	{
		alert("Please Select Atleast one Rate at "+res);
		return false;
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
	if(document.getElementById('wtBreakType'+i+j).value=='LIST' && document.getElementById('consoleType'+i+j).value=='FCL'){
		rate = document.getElementById('checkCount'+i+j).value;
		}
           
	         for(var k=0;k<rate;k++)
		   {
 		//	alert("287--"+(radio[j].checked && document.getElementById("hid"+index).value != ""))
			  if(radio[j].checked && document.getElementById("hid"+index).value != "")
			    {
			
		if(document.getElementById('rateVal'+index1+k).value!='-' && document.getElementById('rateVal'+index1+k).value!=null){
			   document.getElementById('checkedBox'+index1+k).disabled = false;
               document.getElementById('con'+index1+k).value = document.getElementById('checkedBox'+index1+k).value;
		   document.getElementById('checkedBox'+index1+k).checked=true;
				}

				}             


				 else{
                     //   alert("301--"+index1+'-----checkedBox---'+i+j+"-------"+((""+index1)==(i+""+j)))
						if((""+index1)==(i+""+j))  //Modified by Rakesh on 06-01-2011
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
				 
				  if(document.getElementById('wtBreakType'+i+j).value!='LIST' && document.getElementById('consoleType'+i+j).value!='FCL' || document.getElementById('wtBreakType'+i+j).value=='LIST' && document.getElementById('consoleType'+i+j).value!='FCL'){
					  count="NOT A LIST";
					  
				  }
     
				          for(var k=0;k<rate;k++)
					  {

				          checkedBox = document.getElementsByName('checkedBox'+i+j+k)
                      
					}


					  radiochecked=true;
			 }
			  		  if(count!='NOT A LIST')
				{


					for(var k=0;k<rate;k++)	  {

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
			   alert("Please select Atleast One Rate / Corresponding Container if List ");
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
//Added by Anil.k for Spot Rates
function openSurchargeLov(trid)
{	
	var mode = '<%=masterDOB.getShipmentMode()%>';
	var shipmentMode;
	/*if(mode ==  "Air")
		shipmentMode = 1
	else if (mode ==  "Sea")
		shipmentMode = 2
	else 
		shipmentMode = 4*/
	shipmentMode=mode;

	var rate_break = '<%=masterDOB.getMultiquoteweightBrake()%>';
	if(rate_break == 'List')
		rate_break = '=\'List\''
	
	else 
		rate_break = '!=\'List\''
	
	var rownum = (trid).substring(2);//modified by silpa.p for surcharge lov id on 4-06-11
	var tabArray = 'SURCHARGE_ID,SURCHARGE_DESC,RATE_BREAK,RATE_TYPE,WEIGHT_BREAKS';
	var formArray = 'sr'+rownum+',srd'+rownum+',ratebreak'+rownum+',ratetype'+rownum+',srw'+rownum;
	var tempId =  'sr'+rownum
		//alert(tempId)
		var surchargedesc= 'srd'+rownum
		//alert(surchargedesc)
	var surChargeId = document.getElementById(tempId).value;
	var surchargedescripton=document.getElementById(surchargedesc).value
	
		if(surChargeId != null && surChargeId != ""){
      var  cond = "    and  SURCHARGE_ID  Like  \'"+surChargeId+'~\'';
         rate_break   = rate_break+ cond;
	}
	else{
             var  cond = "    and  SURCHARGE_ID  Like \'"+surChargeId+'~\'';
			          rate_break   = rate_break+ cond;
	}
	//@Added by silpa on 28-04-2011
	 if(surchargedescripton!=null && surchargedescripton !=""){

var  cond = "    and  UPPER(SURCHARGE_DESC)  Like  \'"+surchargedescripton.toUpperCase()+'~\'';
         rate_break   = rate_break+ cond;

	}
	else{

var  cond = "    and  SURCHARGE_DESC  Like \'"+surchargedescripton+'~\'';//modified by silpa.p13-05-11
			          rate_break   = rate_break+ cond;
	}//Ended

	
	var lovWhere	=	"";
	
	Url		="qms/ListOfValues.jsp?lovid=SURCHARGE_LOV&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+mode+"&operation=SURCHARGE&search= where shipment_Mode= "+shipmentMode+  " and rate_break " +  rate_break +" order by SURCHARGE_ID asc"; 
        
	Bars	='directories=0,location=0,menubar=no,status=no,titlebar=0,scrollbars=1';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);	
 }
 function breaks(obj,index,lane)
 { 	 
	 var wtBreak= obj.value.split(",");	 	 
	 if(wtBreak == "SLAB")
	 {
		document.getElementById("spotRatesSurSlab"+lane+index).style.display='block';
		document.getElementById("spotRatesSurBoth"+lane+index).style.display='none';
		//document.getElementById("srslabbreaks"+lane+index+"0").value=document.getElementById("surchargeIds"+lane+index).value+"MIN";	
	 }
	 else if(wtBreak == "Both")
	 {
		 document.getElementById("spotRatesSurBoth"+lane+index).style.display='block';
		 document.getElementById("spotRatesSurSlab"+lane+index).style.display='none';		 
		 //document.getElementById("srbothbreaks"+lane+index+"0").value = document.getElementById("surchargeIds"+lane+index).value+"MIN";	 
	 }
	 else if(wtBreak == "")
	 {
		 //added by silpa.p on 31-05-11
		 document.getElementById("surchargeIds"+lane+index).value="";
		 document.getElementById("ratebreak"+lane+index).value="";
		 document.getElementById("ratetype"+lane+index).value="";
	     document.getElementById("surchargeDesc"+lane+index).value="";//ended
		 document.getElementById("spotRatesSurBoth"+lane+index).style.display='none';
		 document.getElementById("spotRatesSurSlab"+lane+index).style.display='none';
		 document.getElementById("spotRatesSurElse"+lane+index).innerHTML = "";
	 }

	
	 else
	 {
		 //document.getElementById("spotRatesSurElse").style.display='block';
		 var surChg = document.getElementById("surchargeIds"+lane+index).value;		 
		 document.getElementById("spotRatesSurBoth"+lane+index).style.display='none';		
		 document.getElementById("spotRatesSurSlab"+lane+index).style.display='none';
		 data = "";		 
		 data += "<table width='50%' cellpadding='4' cellspacing='0'  border='0'><tr class='formdata'>";
		 var wtBrk = '<%=masterDOB.getMultiquoteweightBrake()%>';		 
		 if(wtBrk != "List")
		 {
			for(var i=0;i<wtBreak.length;i++)
			{	
				data += "<td><b>"+wtBreak[i]+"<b><br><input type='text' class='text' name='srelseValue"+lane+index+"' id='srelseValue"+index+i+"' size='5' value ='' maxlength='30'><input type='hidden' name='srelse"+lane+index+"' value='"+wtBreak[i]+"' id='srelse"+lane+index+i+"'></td>";			
			}
		 }
		 else
		 {
			if(document.getElementsByName("listValues")[index].length == 0)
			 {
				alert("Please Select Containers");
				document.getElementById("surchargeIds"+lane+index).value = ""; 
				document.getElementById("surchargeDesc"+lane+index).value = "";
				document.getElementById("surchargeWeightBreaks"+lane+index).value = "";
				return false;
			 }
			var listBreak = ''; 
			for(var i=0;i<document.getElementsByName("listValues")[index].length;i++)
			{	
				listBreak = document.getElementsByName("listValues")[index].options[i].value;;
				data += "<td>"+listBreak+"<br><input type='text' class='text' name='srListValue"+lane+index+"' id='srListValue"+lane+index+i+"' size='5' value ='' maxlength='30'><input type='hidden' name='srList"+lane+index+"' value='"+listBreak+"' id='srList"+lane+index+i+"'></td>";			
			}
		 }
		 data += "</tr></table>";
		 document.getElementById("spotRatesSurElse"+lane+index).innerHTML = data;
	 }	 
 }
 function getNumberCodeNegative(input)
{
	var res = true;//Modified by Mohan for Issue No.219976 on 28-10-2010	
	alert(input.value);
	if(event.keyCode!=13)
	{
		if(input.value.substring("-").length>0)
		{
			//if(event.keyCode < 48 || event.keyCode > 57)
			//	return false;
				res =getDotNumberCode(input);
		}
		else
		{
			if((event.keyCode < 48 && event.keyCode!=45) || event.keyCode > 57)
				//return false;
				res = false;
		}
	}
	
	return res;
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
function display()
{


	<%if((!"Modify".equalsIgnoreCase(operation) && !"BackButton".equalsIgnoreCase(backButton))
		||("MODIFY".equalsIgnoreCase(operation) && "INCOMPLETE".equalsIgnoreCase(completeFlag))) {
		for(int a =0; a<originLocation.length; a++){
		if(masterDOB.getSpotRatesFlag()!=null && "Y".equals(masterDOB.getSpotRatesFlag()[a])){%>
	var wtBreak = '<%=masterDOB.getMultiquoteweightBrake()%>';
	if(wtBreak == "Flat")
	{
		document.getElementById("spotRatesFlat"+<%=a%>).style.display='block';
		document.getElementById("spotRatesSlab"+<%=a%>).style.display='none';
		document.getElementById("spotRatesListLov"+<%=a%>).style.display='none';
	}
	else if(wtBreak == "Slab")
	{
		document.getElementById("spotRatesSlab"+<%=a%>).style.display='block';
		document.getElementById("spotRatesFlat"+<%=a%>).style.display='none';
		document.getElementById("spotRatesListLov"+<%=a%>).style.display='none';
	}
	else if(wtBreak == "List")
	{
		document.getElementById("spotRatesSlab"+<%=a%>).style.display='none';
		document.getElementById("spotRatesFlat"+<%=a%>).style.display='none';
		document.getElementById("spotRatesListLov"+<%=a%>).style.display='block';
	}
	<%}}}%>
}
function showListLov(shipMode,legNo)
{	
	var rateType = '<%=masterDOB.getMultiquoteweightBrake()%>';
	if(rateType=='List')
	{
		//if(noOfLegs == 1)
			shipMode = '<%=shipmentMode%>';
		/*else
		{*/
			if(shipMode == 'AIR')
				shipMode = "Air";
			else if(shipMode == 'SEA')
				shipMode = "Sea";
			else if(shipMode == 'TRUCK')
				shipMode = "Truck";
		//}
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
function showListValues(obj,legNo)
{	
	
	if(obj!=null)
	{		
	//	for(var i=0;i<document.getElementsByName("listWeightBreak"+legNo).length;i++)
		//	document.getElementsByName("listWeightBreak"+legNo)[i].value = '';			
		
		for(var i=0;i<obj.length;i++)
		{
			//document.getElementsByName("listWeightBreak"+legNo)[i].value = obj[i].value;
			//@@ commented by kiran.v on 12/03/2012 for Wpbn Issue-
			document.getElementById("listWeightBreak"+legNo+i).value=obj[i].value;
		  document.getElementsByName("listValues")[legNo].options[i] = new Option(obj[i].value,obj[i].value);
			//document.forms[0].listValues.options[i]=new Option(obj[i].value,obj[i].value);	//commented  by silpa.p on 14-06-11
			
		}
	}
}
function srchargeSlabBreakCheck(obj,p,srcno,count)//p= column no of slab srcharge,srcno=surcharge rownumber,count =lane no)
{	
    if((p==1) && (obj.name == "srslabbreaks"))
        return  getNumberCodeNegative(obj);
    else
		return  getNumberCode(obj);
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
function srChargeSlabBreakValidation(obj,p,srcno,count)
	{
	var srChrgValue = obj.value;
     if(p==1) 
		{
		 if(Number(srChrgValue)>0){
			 alert("First Value Should be -ve and Second Value must be +ve of First Value");
			 obj.value="";
			 obj.focus();
		 }else{
				document.getElementsByName("srslabbreaks"+count+srcno+2)[0].value=srChrgValue.substring(1);
			 }
		}
		else if(p==2)
		{
			document.getElementsByName("srslabbreaks"+count+srcno+1)[0].value ='-'+srChrgValue;
		}
	}
function showDensityLOV(index,shipMode)
{
		//alert(shipMode);
	shipmentMode=	'<%=shipmentMode%>';	
	/*if(noOfLegs=='1')
		shipmentMode = document.forms[0].shipmentMode.value;*/
	if(shipMode=="AIR")
		shipmentMode	= 'Air';
	else if(shipMode=="SEA")
		shipmentMode	= 'Sea';
	else if(shipMode=="TRUCK")
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
	if(shipMode=="AIR")
		shipmentMode	= 'Air';
	else if(shipMode=="SEA")
		shipmentMode	= 'Sea';
	else if(shipMode=="TRUCK")
		shipmentMode	= 'Truck';
	
	var searchString= document.getElementById("serviceLevel"+index).value;

	Url='etrans/ETCLOVServiceLevelIds.jsp?searchString='+searchString+'&shipmentMode='+shipmentMode+'&Operation=Add&terminalId=<%=loginbean.getTerminalId()%>&wheretoset=serviceLevel'+index;
	Bars = 'directories=no,location=no,menubar=no,status=Yes,titlebar=no,scrollbars=yes';
	Options='width=400,height=300,resizable=no';
	Features=Bars+','+Options;
	Win=open(Url,'Doc2',Features);
}
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
function pop1(index,index1)
{	
	   var currency=document.getElementById("currencyId"+index+index1).value;	   
	 	myUrl= 'etrans/ETCCurrencyConversionAddLOV.jsp?searchString='+currency+'&currencyId=currencyId'+index+index1+'&fromWhere=quote&Operation=Add';
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;

		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
		newWin.opener = self;
		return true;
       
}
function showCarrierIds(index)
{
	shipmentMode=	'<%=shipmentMode%>';	
	wheretoset = "carrierId"+index;
	var URL 		= 'etrans/ETransLOVCarrierIds.jsp?searchString='+document.getElementById("carrierId"+index).value.toUpperCase()+'&shipmentMode='+shipmentMode+'&wheretoset='+wheretoset;	
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 360,height = 360,resizable = yes';
	var Features 	= Bars +' '+ Options;

	var Win 		= open(URL,'Doc',Features);

}
 //Ended by Anil.k for Spot Rates

function value1(obj,name,i,j)
	{
		if(obj.value!=""){
obj.value=round(obj.value);
	//alert(name);
if(name == "flatRate")
		{
	//alert(1);
document.getElementById("hiddenfaltrate"+i+j).value=(obj.value);
	}
		}

	}
//@ added by silpa for frequence validation
function getCommaNumberCode(val)
	{
			 //alert("event.keyCode:::"+event.keyCode);
	   if(event.keyCode!=13)
		{	
		 if((event.keyCode <= 44 ) || (event.keyCode>44 && event.keyCode <49) || (event.keyCode >= 56) )
			{
			   return false;	
			}
		}
    
		if(!getDotNumberCode(val))
			return false;    
      
		appendWithComma(val);


		
		return true;
	}

	function appendWithComma(val)
	{
		if(val.value.length>0)
		{
			var temp1 = val.value.substr(0,(val.value.length));
			var temp2 = val.value.substr((val.value.length), val.value.length);
			if(val.value.length<15)
				val.value = temp1+','+temp2;
		}
	}

//@ ended by 


//@added by silpa for validations
function IsValidTime(field)
	{
		timeStr=field.value
		if(timeStr.length!=0)
		{
			
			/*
			var	timePat	= /^(\d{1,2}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;
			if(timeStr.length==4 && timeStr.indexOf(':')==-1)
			{
				timeStr = timeStr.substring(0,2)+':'+timeStr.substring(2,4);
				field.value=timeStr;


			}
			*/

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
			
	 if (hour < 0  || hour > 999)
      {
        alert("Please enter correct Hours");
        field.focus();
			}
			else if	(minute<0 || minute	> 59) {
				alert ("Please enter correct Minutes.");
				field.focus();
			}
			else if	(second	!= null	&& (second < 0 || second > 59))
			{
				alert ("Please enter correct Seconds.");
				field.focus();
			}
			}
		}
	}//@ended
//@@added by silpa for min values
function minvalues(srslabbreaks,k,obj)
	{
	//alert(k);
         	//obj.value=round(obj.value);	
	var srsbreak;
	//var srsbreak1;
//alert(srslabbreaks);
var n;

	//alert(document.getElementById(srslabbreaks+k).value);

	srsbreak=(document.getElementsByName(srslabbreaks)[k].value);
	if(isNaN(srsbreak))
			{
	alert("Please Enter Numeric Values Only.");

			}
			else if(srsbreak.substr(0,1)=="-")
		{
				
			if((k+1)%2==0)
			
				document.getElementsByName(srslabbreaks)[k+1].value=srsbreak.substr(1,n);
		}
		

	
	}
	
//@added by silpa for spot rates validation

	function getNumberCodeNegative(input)
{
	var res = true;
	if(event.keyCode!=13)
	{
		if(input.value.substring("-").length>0)
		{
			//if((event.keyCode < 48 && event.keyCode!=46)|| event.keyCode > 57  )
			//	return false;
			res =getDotNumberCode(input);
		}
		else
		{   
			if((event.keyCode < 48 && event.keyCode!=45) || event.keyCode > 57)
				res = false;
		}
	}
	
	return res;
}
//@ ended


//@ added by silpa
	
function minvalues1(srbothbreaks,k)
	{
	//alert(k);
	//obj.value=round(obj.value);
	var srsbothbreak;
	
var n1;

	//alert(document.getElementById(srbothbreaks+k).value);

	srsbothbreak=(document.getElementsByName(srbothbreaks)[k].value);
	if(isNaN(srsbothbreak)){
	

alert("Please Enter Numeric Values Only.");


		}
		else if(srsbothbreak.substr(0,1)=="-")
		{
				
			if((k+1)%2==0)
			
				document.getElementsByName(srbothbreaks)[k+1].value=srsbothbreak.substr(1,n1);

		}
			
		


	

	
		
	
	}	

	//@ ended



	//@added by silpa
function minvalues2(slabWeightBreak,k)
	{
	//alert(k);
	
	var slabWeightBreak1;
	//obj.value=round(obj.value);
var n2;

	//alert(document.getElementById(slabWeightBreak+k).value);

	slabWeightBreak1=(document.getElementsByName(slabWeightBreak)[k].value);
	//alert(slabWeightBreak1);
	if(isNaN(slabWeightBreak1)){
	

alert("Please Enter Numeric Values Only.");


		}
		
		
			else if(slabWeightBreak1.substr(0,1)=="-")
			{

				
			if((k+1)%2==1)
			
		document.getElementsByName(slabWeightBreak)[k+1].value=slabWeightBreak1.substr(1,n2);

		}
		}
			
		

//@ended
//Added By Govind for SlabBoth issue
function slabBothCheck(obj)
	{
         obj.value="";
	}
//	Commented by Govind for slabboth issue
/*function brvalues(obj,srflatvalue,s,i,k)
	{
		//alert(obj.value);
		if(obj.value!="")
		{
		
document.getElementById("srslabvalue"+s+i+k).value="";
		}
		else
			obj.value="";
}


//@@ended by silpa for min values on 18-03-2011


/*@ added by silpa for spot rate validations on 11-04-2011
function brvalues1(obj,srslabvalue,s,i,k) Commented by Govind for slabboth issue
	{
		//alert(srflatvalue);


//obj.value=round(obj.value);

//alert(document.getElementById("srflatvalue"+s+i+k).value);

		if(obj.value!="")
		{

document.getElementById("srflatvalue"+s+i+k).value="";
		}
		else
			document.getElementById("srslabvalue"+s+i+k).value="";
	}

//@ended*/



//@ added by silpa


function checkNumbers1(input)
	{
		if(trim(input.value).length>0)
		{
				//if(isNaN(trim(input.value)))
    	if(isNaN(trim(input.value))||trim(input.value)<0)
			{
				//alert("Please do not enter characters for "+label);
				input.value='';
				input.focus();
				return false;
			}
		}
		return true
	}

//@ ENDED

function trim(input)
 { 
	while (input.substring(0,1) == ' ') 
		input = input.substring(1, input.length);

	while (input.substring(input.length-1,input.length) == ' ')
		input = input.substring(0, input.length-1);

   return input;
 } 

//@added by silpa for  decimal validations
function round(n)
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


//@added by silpa for decimal caluculations
function decimal(obj)


	{
		//alert(obj.value);
if(obj.value!= "")
obj.value=round(obj.value);

	}

	//@ended


//added by silpa.p on 31-05-11
function breaksForSurchargeIds(obj,index,lane){


 var surChargeId= obj.value.split(",");	 
 

 if(surChargeId == "")
	 {
		document.getElementById("surchargeDesc"+lane+index).value="";
		document.getElementById("ratebreak"+lane+index).value="";
		document.getElementById("ratetype"+lane+index).value="";
		document.getElementById("surchargeWeightBreaks"+lane+index).value="";		 document.getElementById("spotRatesSurBoth"+lane+index).style.display='none';
		document.getElementById("spotRatesSurSlab"+lane+index).style.display='none';
		document.getElementById("spotRatesSurElse"+lane+index).innerHTML = "";
	 }


}//ended

//added by silpa.p on 31-05-11
function breaksForSurchargeDescription(obj,index,lane)
	{
var surchargeDesription= obj.value.split(",");	 



 if(surchargeDesription == "")
	 {
		document.getElementById("surchargeIds"+lane+index).value="";
		document.getElementById("ratebreak"+lane+index).value="";
		document.getElementById("ratetype"+lane+index).value="";
		document.getElementById("surchargeWeightBreaks"+lane+index).value="";		 
		 document.getElementById("spotRatesSurBoth"+lane+index).style.display='none';
		 document.getElementById("spotRatesSurSlab"+lane+index).style.display='none';
		 document.getElementById("spotRatesSurElse"+lane+index).innerHTML = "";
	 }

	}//ended



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

<BODY onLoad="display()">
       <form method="post" name="sellRates" action="QMSMultiQuoteController" onSubmit='return validate()'>

	<table border="0" cellPadding="4" cellSpacing="1" width="138.1%" bgcolor='DCDCDC'>
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


%>
               <div>	
					<%
				         noRatesFlag = new Boolean[originLocation.length];
						for(int i =0; i<freightRatesSize;i++){
							originLoc = masterDOB.getOriginPort()[i];
							destLoc	  = masterDOB.getDestPort()[i];
							
					%>
						<div class="tabs" id="tab<%=i%>" style="border:1px solid white" ><font size='1.0%'><b><%=originLoc%>-<%=destLoc%></b></font></div>
						<!-- Added by Anil.k for Enhancement 231214 on 25Jan2011 -->
						<input type="hidden" name="originLocCharge" id="originLocCharge<%=i%>" value='<%=originLoc%>'>
						<input type="hidden" name="destLocCharge" id="destLocCharge<%=i%>" value='<%=destLoc%>'>
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
					<table border="0" cellPadding="4" cellSpacing="1" width="233.5%" bgcolor='#FFFFFF'>
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
					<table border="0" cellPadding="4" cellSpacing="1" width="233.5%"  bgcolor='#FFFFFF'>
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
								<TH><font size='0.25%'>Density Ratio</TH></font>
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
								<TH nowrap><font size='0.25%'>Service Level</TH></font>
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
								<td><input type="checkbox" value="<%=j%>" name="leg<%=i%>"  <%="Y".equalsIgnoreCase(checkedFlag)?"checked":""%> onClick="setSelectedIndex(this,'<%=i%><%=j%>','<%=i%><%=j%>','<%=containerCount%>');" multiple>
								<input type="hidden" id="wtBreakType<%=i%><%=j%>" name="wtBreakType" value='<%=sellRateDOB.getWeightBreakType()%>'>
								<input type="hidden" id="consoleType<%=i%><%=j%>" name="consoleType" value='<%=sellRateDOB.getConsoleType()%>'>
								<input type="hidden" id="checkCount<%=i%><%=j%>" name="checkCount" value='<%=containerCount%>'>
								</td>
								<% tempcount++;} else { %>
								<!-- Modified by subrahmanyam for the pbn id: 186812 on 22/oct/09, checkIndex was replaced in the place of k -->
								<td></td> <%}  if("Y".equalsIgnoreCase(checkedFlag)) { disabled=false;%>
								<td><input type="checkBox"  value="<%=listWeightBreaks[k]%>"   name="checkedBox<%=i%><%=j%><%=checkIndex%>" id="checkedBox<%=i%><%=j%><%=checkIndex%>"  <%="Y".equalsIgnoreCase(checked[k])?"checked":("Copy".equalsIgnoreCase(operation) && "Y".equalsIgnoreCase(checkedFlag))?"checked":"" %> onClick="setSelectedIndex1(this,'<%=i%><%=j%><%=checkIndex%>','<%=listWeightBreaks[k]%>');">
								
								<%}else{%>

<td><input type="checkBox"  value='<%=listWeightBreaks[k]%>'   name="checkedBox<%=i%><%=j%><%=checkIndex%>" id="checkedBox<%=i%><%=j%><%=checkIndex%>" disabled <%="Y".equalsIgnoreCase(checked[k])?"checked":("Copy".equalsIgnoreCase(operation) && "Y".equalsIgnoreCase(checkedFlag))?"checked":""%> onClick="setSelectedIndex1(this,'<%=i%><%=j%><%=checkIndex%>','<%=listWeightBreaks[k]%>');" readonly>
<%}%>

								<input type="hidden" id="con<%=i%><%=j%><%=checkIndex%>" name="con<%=i%><%=j%>">


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
                                  <td><font size='0.25%'><%=listWeightBreaks[k]%></td></font>
									<td  nowrap><font size='0.25%'><%=(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k]))?df.format(Double.parseDouble(rate[k])):"-"%></td></font>
								<!--td><%//=sellRateDOB.getWeightClass()%></td-->
								<td><font size='0.25%'><%=sellRateDOB.getRsrOrCsrFlag()%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCreatedTerminalId()!=null?sellRateDOB.getCreatedTerminalId():""%></td></font>
								<td><font size='0.25%'><%=sellRateDOB.getCurrency()!=null?sellRateDOB.getCurrency():""%></td></font>
								<td nowrap><font size='0.25%'><%=effectiveFromStr!=null?effectiveFromStr:""%></td></font>
								<td nowrap><font size='0.25%'><%=validUptoStr!=null?validUptoStr:""%></td></font>
								<td nowrap><font size='0.25%'><%=sellRateDOB.getDensityRatio()%></td></font>
								 <%
									if(j==counter ){
									if(k==0){
									counter+=1;	%>

								<td><font size='0.25%'>
								<!-- Added by Rakesh for Issue:       on 18-03-2011 -->
								 <input type="hidden" id="note<%=i%><%=j%>" name="note<%=i%><%=j%>" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>">
								 <input type="hidden" id="extNote<%=i%><%=j%>" name="extNote<%=i%><%=j%>" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>">
								 <!-- Ended by Rakesh for Issue:       on 18-03-2011 -->
								<input class="text" id="Notes<%=j%>" name="notes<%=j%>" size="5"  maxLength="1000" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" title="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>" readOnly></td></font>
								<td><font size='0.25%'><input class="text" id="extNotes<%=j%>" name="extNotes<%=j%>" size="5"  maxLength="1000" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" title="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>" readOnly></td></font>
								 <%}}else{%>
								  <td><font size='0.25%'>
								  <!-- Added by Rakesh for Issue:       on 18-03-2011 -->
								   <input type="hidden" id="note<%=i%><%=j%>" name="note<%=i%><%=j%>" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>">
								 <input type="hidden" id="extNote<%=i%><%=j%>" name="extNote<%=i%><%=j%>" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>">
								 <!-- Ended by Rakesh for Issue:       on 18-03-2011 -->
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
												int frtCount				=	14-containerCount;
												int surChargeCount = 0;%>
						<tr valign="top" class='formdata' align="center">
						<td /><td/>
						<td nowrap><font size='0.25%'><%=toTitleCase(surcharge.substring(0,surcharge.length()-3))%></font></td>
						<%		int count3=0;
								for(int sd=0;sd<ratesSize;sd++)
								{//##4
										if(!"A FREIGHT RATE".equalsIgnoreCase(rateDesc[sd]) && surcharge.equalsIgnoreCase(rateDesc[sd]) )//&&listWeightBreaks[sd].startsWith(listWeightBreaks[k]) )
										{		  count3+=1;
%>
												<td ><font size='0.25%'><%=listWeightBreaks[sd].substring(0,4)%></font></td>


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
								<td><input type="checkbox" value="<%=j%>" name="leg<%=i%>" <%="Y".equalsIgnoreCase(checkedFlag)?"checked":""%> onClick="setSelectedIndex(this,'<%=i%><%=j%>');" multiple >
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
								<td nowrap><font size='0.25%'><%=sellRateDOB.getDensityRatio()%></td></font>
                                                                	<!-- Added by Rakesh for Issue:       on 18-03-2011 -->
								 <input type="hidden" id="note<%=i%><%=j%>" name="note<%=i%><%=j%>" value="<%=sellRateDOB.getNotes()!=null?sellRateDOB.getNotes():""%>">
								 <input type="hidden" id="extNote<%=i%><%=j%>" name="extNote<%=i%><%=j%>" value="<%=sellRateDOB.getExtNotes()!=null?sellRateDOB.getExtNotes():""%>">
								 <!-- Ended by Rakesh for Issue:       on 18-03-2011 -->
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
	for(int x=1;x<surchargeDesc.size();x++){
	String surcharge	=	 (String	)surchargeDesc.get(x);
	int tempCount	=	0;	
	int frtCount				=	0;
	int surChargeCount = 0;
	%>
	<tr valign="top" class='formdata' align="center">
	<td />
	<td ><font size='0.25%'><%=toTitleCase(surcharge.substring(0,surcharge.length()-3))%></font></td>
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
									<td  nowrap><font size='0.25%'><%=(rate[k]!=null&&!"-".equalsIgnoreCase(rate[k]))?df.format(Double.parseDouble(rate[k])):"-"%></td>
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
//System.out.println((masterDOB.getQuoteWith()));
if(!"Freight".equalsIgnoreCase(masterDOB.getQuoteWith())) {  // Added by Anil.k%>
<tr class ='formheader'><td  colspan = '10'>ApplicableChargeGroups</td></tr>
<tr> <td colspan = '10'>
<table class ='tabledata' border="0" id='chargeDetails<%=i%>'cellPadding="4" cellSpacing="1" width="100%" bgcolor='DCDCDC'>
<%
	if("MODIFY".equalsIgnoreCase(operation) || "COPY".equalsIgnoreCase(operation)|| "BackButton".equalsIgnoreCase(backButton)){
         legRateDetails    =   (MultiQuoteFreightLegSellRates)freightRates.get(i);
		 int chargeslen = legRateDetails.getChargeGroupIds()!= null?legRateDetails.getChargeGroupIds().length:1;
		for(int j=0;j<chargeslen;j++)
		{
		if(legRateDetails.getChargeGroupIds()!= null){%>
	   <tr id='tr<%=i%><%=j%>' class ='formdata' >
	   <td ><input class="input" type="button" value="<<" onclick="deleteChargeRow(this,'tr<%=i%><%=j%>',<%=i%>)"></td>
	   <td align='center'><input class="text" maxLength="16" name="chargeGroupIds<%=i%>" id='chargeGroupIds<%=i%><%=j%>' size="28"  value="<%=legRateDetails.getChargeGroupIds()!= null?legRateDetails.getChargeGroupIds()[j]!= null ?legRateDetails.getChargeGroupIds()[j]:"":""%>" onBlur="changeToUpper(this);"><!--onblur = "return this.upper();" modified by silpa.p on 25-05-11-->
	   <input class="input" type="button" name="chargeGroupIdBt" id="chargeGroupIdBt<%=i%><%=j%>" value="..." onclick="popUpWindow(this,<%=i%>,<%=j%>)">
	   <input class="input" type="button" name="chargeGroupIdDet" value="ViewDetails" onclick="popUpWindow(this,<%=i%>,<%=j%>)"></td>
	   <td><input class="input" type="button" value=">>" onclick="addNewChargeRow(this,'tr<%=i%><%=j%>',<%=i%>)"></td>
	   </tr>
<%}else{%>
         <tr id='tr<%=i%><%=j%>' class ='formdata' >
	   <td ><input class="input" type="button" value="<<" onclick="deleteChargeRow(this,'tr<%=i%><%=j%>',<%=i%>)"></td>
	   <td align='center'><input class="text" maxLength="16" name="chargeGroupIds<%=i%>" id='chargeGroupIds<%=i%><%=j%>' size="28"  value="" onBlur="changeToUpper(this);">
	   <!-- onblur = "return this.upper();"modified by silpa.p on 25-05-11-->
	   <input class="input" type="button" name="chargeGroupIdBt" id="chargeGroupIdBt<%=i%><%=j%>" value="..." onclick="popUpWindow(this,<%=i%>,<%=j%>)">
	   <input class="input" type="button" name="chargeGroupIdDet" value="ViewDetails" onclick="popUpWindow(this,<%=i%>,<%=j%>)"></td>
	   <td><input class="input" type="button" value=">>" onclick="addNewChargeRow(this,'tr<%=i%><%=j%>',<%=i%>)"></td>
	   </tr>
	   <%}
	   }
	 

}else{%>
<tr id='tr<%=i%>0' class ='formdata' >
<td ><input class="input" type="button" value="<<" onclick="deleteChargeRow(this,'tr<%=i%>0',<%=i%>)"></td>
<td align='center'><input class="text" maxLength="16" name="chargeGroupIds<%=i%>" id='chargeGroupIds<%=i%>0' size="28"  value=""  onblur = "changeToUpper(this);">
<input class="input" type="button" name="chargeGroupIdBt" id="chargeGroupIdBt<%=i%>0" value="..." onclick="popUpWindow(this,<%=i%>,0)">
<input class="input" type="button" name="chargeGroupIdDet" value="ViewDetails" onclick="popUpWindow(this,<%=i%>,0)"></td>
<td><input class="input" type="button" value=">>" onclick="addNewChargeRow(this,'tr<%=i%>0',<%=i%>)"></td>
</tr>
<%}%>
</table>
<%} //Added by Anil.k%>
</td>
</tr>
<tr>
<td><input class="input" name="submit" type="submit" id='submit' value="<< Back" onClick="setIndexForChecked(this)">
</td>
<td><input class="input" name="submit" type="submit" value="Next >>" onClick="setIndexForChecked(this);  return setIndexForChecked1(this,'<%=sellRatesSize%>','<%=rate.length%>')">
</td></tr>
			</tbody></table>

 </div>


			<!--	<input type="hidden" id="hid<%=i%>" name="hid<%=i%>">-->
<%		} }else{//Added by Anil.k for Spot Rates
%>
<div class="container<%=i%>" id="<%=i%>container" bgcolor='DCDCDC' style=" clear:both;  margin-top:0px; position:relative;">
<table width="209%" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF' id='spotRates'>
 <tr class="formdata"><td valign ='bottom' nowrap  colspan="9">
<b>Spot Rates: </b><%=legRateDetails.getOrigin()+"-"+legRateDetails.getDestination()%></td></tr>
<tr></tr>

<tr class="formdata"><td valign ='bottom' colspan="9"><b>Weight Break: </b><%=masterDOB.getMultiquoteweightBrake()%><input type="hidden" name="spotRateType<%=i%>" value="<%=masterDOB.getMultiquoteweightBrake()%>"></td></tr>

 <tr class='formheader'>
 <th><b>Origin</b></th>
 <th><b>Destination</b></th>
 <th><b>Carrier Id</b></th>
 <th><b>UOM</b></th>
 <th><b>Density Ratio</b></th>
 <th><b>Service Level</b></th>
 <th><b>Frequency</b></th>
 <th><b>Currency</b></th>
 <th><b>Transit Time</b></th></tr>

 <%/* if((!"Modify".equalsIgnoreCase(masterDOB.getOperation()) && !"BackButton".equalsIgnoreCase(backButton)) || ("Modify".equalsIgnoreCase(masterDOB.getOperation()) && "INCOMPLETE".equalsIgnoreCase(completeFlag)) )@@ Commented by govind for not getting data in modify case when save and exit is selected*/
 if((!"Modify".equalsIgnoreCase(masterDOB.getOperation()) && !"BackButton".equalsIgnoreCase(backButton))  || 
("Modify".equalsIgnoreCase(masterDOB.getOperation()) && "INCOMPLETE".equalsIgnoreCase(completeFlag)) &&!( masterDOB.isIncomplete_screen())   )		 {
	 System.out.println("IN 2057");%>
 <tr class="formdata">
 <td align="center"><%=masterDOB.getOriginPort()[i]%></td>
 <td align="center"><%=masterDOB.getDestPort()[i]%></td> 
 <td align="center"><input class="text" type="text" value="" size="7" name="carrierId<%=i%>" id="carrierId<%=i%>" onBlur="changeToUpper(this)">
					<input type='button' class='input'  id=='carrierLov<%=i%>' name='carrierLov<%=i%>' value='...' onclick="showCarrierIds(<%=i%>);"></td>
 <td align="center"><%if("AIR".equalsIgnoreCase(shipmentMode)){%>
					<select class="select"  id="uom<%=i%>" name="uom<%=i%>" size="1" >
						<option value="Kg" >Kg</option>
						<option value="Lb" >Lb</option>
					</select>
					<%} else if("SEA".equalsIgnoreCase(shipmentMode)){%>	
					<select class="select"  id="uom<%=i%>" name="uom<%=i%>" size="1" >
					<option value="CBM" >CBM</option>
					<option value="CFT" >CFT</option>
					</select>					
					<%} else if("TRUCK".equalsIgnoreCase(shipmentMode)){%>
					<select class="select"  id="uom<%=i%>" name="uom<%=i%>" size="1" ><option value="KG">KG</option>
					<option value="LB" >LB</option><option value="CBM" >CBM</option><option value="CFT">CFT</option></select>
					<%}%></td>
 <td align="center"><INPUT class="text" TYPE="text" value="" size="7" id="densityRatio<%=i%>" name="densityRatio<%=i%>" onBlur="changeToUpper(this)">
					<input class="input" type="button" name="densityRatioLOV<%=i%>" id="densityRatioLOV<%=i%>" value="..." onclick="showDensityLOV(<%=i%>,'<%=shipmentMode%>')"></td>
 <td align="center"><INPUT class="text" TYPE="text" value="" size="7" name="serviceLevel<%=i%>" onBlur="changeToUpper(this)">
					<input class="input" type="button" name="serviceLvlLov2" value="..." onclick="showLegServiceLevel(<%=i%>,'<%=shipmentMode%>')"></td>
					
					<td align="center" width="74">
					<%

	 if("SEA".equalsIgnoreCase(shipmentMode))
	{
%>	
			  <select size="1" name="frequency<%=i%>" class='select'>
				 <option  value="WEEKLY" >WEEKLY</option>
				 <option  value="MONTHLY" >Monthly</option>
				 <option  value="FORTNIGHTLY" > Fortnightly</option><!--Fort Night--><!--@@Modified by Kameswari for the WPBN issue-62417-->
				 <option  value="EVERY 10 DAYS" >Every 10 Days</option>
               </select>

<%  }else
	{
%>
 <INPUT class="text" TYPE="text" value="" size="7" name="frequency<%=i%>"
 onkeypress='return getCommaNumberCode(this);' onBlur="changeToUpper(this)">
 <%	}
%>
</td>

 <td align="center"><input class="text" type="text" value="" size="7" name="currencyId<%=i%>" onBlur="changeToUpper(this)">
					<input type='button' class='input'  name='currencyLov<%=i%>' id="currencyLov<%=i%>" value='...' onclick="pop(<%=i%>);"></td>
					<td align="center"><input class="text" type="text" value="" size="7" name="transitTime<%=i%>"
<%
 if(!"SEA".equalsIgnoreCase(shipmentMode))
	{
%>	

 onBlur="return IsValidTime(this)"<%}else{%>onkeypress=" return getDotNumberCode(this)"onBlur='checkNumbers1(this);'<%}%> >
 
 </td>

 </tr>
 <tr></tr>
 <tr class='formdata'><td colspan="9">
 <span id="spotRatesSlab<%=i%>" style='DISPLAY:none'>
		<table width='101%' cellpadding='4' cellspacing='0' border='0'>
		  <tr class='formdata'>
			<td valign="bottom">Slab</td><td valign="bottom">				
				<input class='text' type='text'   value='BASIC' name='slabWeightBreak<%=i%>' id='slabWeightBreak<%=i%>0' readonly size='7'>
				<input class='text' type='text'  value='MIN' name='slabWeightBreak<%=i%>' id='slabWeightBreak<%=i%>1' readonly size='7'>
				<%for(int k=2;k<12;k++){%> 
				<input class='text' maxLength='8' name='slabWeightBreak<%=i%>' id='slabWeightBreak<%=i%><%=k%>' size='7'  value="" onblur="minvalues2('slabWeightBreak<%=i%>',<%=k%>)" onkeypress='return getNumberCodeNegative(this)'>
				<%}%>
				</td>
			</tr>
			<tr class='formdata' >
				<td nowrap>Freight Rate</td>
				<td>
			<%for(int k=0;k<12;k++){%>
			<input class='text' maxLength='8' name='slabRate<%=i%>' id='slabRate<%=i%><%=k%>' size='7' 
			onKeyPress='return getDotNumberCode(this)' onblur='decimal(this)'
			value=''  > 
			<%}%>
			</td>
			</tr>
	</table>
 </span>
 <span id="spotRatesFlat<%=i%>" style='DISPLAY:none'>
	<table width='50%' cellpadding='4' cellspacing='0'  border='0'>
		<tr class='formdata'>
			<td rowspan="2" valign="bottom"><b>Freight Charges</b></td>
			<td align="center"><b>Basic</b>
			<input type='hidden' maxLength='8' name='flatWeightBreak<%=i%>' size='6' value='BASIC'></td>
			<td align="center"><b>Min</b>
			<input type='hidden' maxLength='8' name='flatWeightBreak<%=i%>' size='6' value='MIN'></td>
			<td align="center"><b>Flat</b>
			<input type='hidden' maxLength='8' name='flatWeightBreak<%=i%>' size='6' value='FLAT'></td>
			<td colspan="3"></td>
		</tr>
		<tr class="formdata">
			<td align="center"><input type="hidden" value="" name="hiddenfaltrate0<%=i%>" id="hiddenfaltrate0<%=i%>" >
			<input class="text" type="text" value="" name="flatRate<%=i%>" id="flatRate<%=i%>" size="7" onBlur="changeToUpper(this);value1(this,'flatRate', 0,'<%=i%>');" onkeypress='return getNumberCodeNegative(this)'></td>
			<td align="center"><input type="hidden" value="" name="hiddenfaltrate1<%=i%>" id="hiddenfaltrate1<%=i%>" >
                        <input class="text" type="text" value="" name="flatRate<%=i%>" id="flatRate<%=i%>" size="7" onBlur="changeToUpper(this);value1(this,'flatRate',1,'<%=i%>')"  onkeypress='return getNumberCodeNegative(this)'></td>		
			<td align="center"><input type="hidden" value="" name="hiddenfaltrate2<%=i%>" id="hiddenfaltrate2<%=i%>" >
                        <input class="text" type="text" value="" name="flatRate<%=i%>" id="flatRate<%=i%>" size="7" onBlur="changeToUpper(this);value1(this,'flatRate',2,'<%=i%>')"   onkeypress='return getNumberCodeNegative(this)'></td>
			<input type="hidden" value="" name="freightFlagValue<%=i%>" id="freightFlagValue<%=i%>">
			<td colspan="4"></td>
		</tr>
	</table>
 </span>
 <span id="spotRatesListLov<%=i%>" style='DISPLAY:none'>
 	<table width='15%' cellpadding='4' cellspacing='0' >
		<tr class='formdata'>			
			<td valign="bottom"><select name='listValues' size=5 multiple class='select'></select>
			<input class="input" type="button" value="..." name="listLov" onClick="showListLov('<%=shipmentMode%>',<%=i%>)"></td>			
		</tr>
	</table> 
	<table width='101%' cellpadding='1' cellspacing='0' >
		<tr class='formdata'>
		<%System.out.println(shipmentMode+"%%%");%>
			<td valign="bottom"><div id='listType'><%="AIR".equalsIgnoreCase(shipmentMode)?"ULD Types":"Container Types"%></div></td>
			<%int listValues=15;
				for(int k=0;k<listValues;k++){%>					
				<td align='center'><input class='text' maxLength='8' name='listWeightBreak<%=i%>' id='listWeightBreak<%=i%><%=k%>' size='6' 
				onKeyPress='return getNumberCode(this)' value="" readOnly></td>
			<%}%>
		</tr>
		<tr class='formdata'>
			<td valign="bottom">Rate</td>
			<%for(int k=0;k<listValues;k++){%>
				<td align='center'valign="bottom" ><input class='text' maxLength='5' name='listRate<%=i%>' id='listRate<%=i%><%=k%>' size='6' onKeyPress='return getNumberCode(this)' 
				onblur='decimal(this)' value=''></td>
			<%}%>
		</tr>
	</table>
 </span>
 </td></tr>
 
 <!-- First Surcharge -->
 <tr class='formheader'>
 <td valign="bottom" colspan="9"><b>SurCharges</b></td></tr>
 <% int spotratesurcount = legRateDetails.getSpotrateSurchargeCount() == 0 ?1:legRateDetails.getSpotrateSurchargeCount();
	 for(int sur=0;sur<spotratesurcount;sur++){%>
 <tr class="formdata"><td colspan="9">
 <fieldset>
<!--<legend>Surcharge</legend>--><!--modified by silpa.p on 13-05-11-->
 <table width='70%' cellpadding='4' cellspacing='0'  border='0'>
  <tr class="formdata"><td colspan="9">
 <table width='80%' cellpadding='4' cellspacing='0'  border='0'>
 <!-- <tr class='formheader'><span id ="spotratesurchargeheader1<%=sur%><%=i%>" style='DISPLAY:none'><td colspan = "9">Surcharge <td></tr>
  <tr class='formheader'><span id ="spotratesurchargeheader2<%=sur%><%=i%>" style='DISPLAY:none'><td colspan = "9"> <td></tr> -->
 <tr class="formdata" id="tr<%=sur%><%=i%>">
<tr align="center">&nbsp;SurCharge Id &nbsp;&nbsp;&nbsp &nbsp;SurCharge Desc&nbsp;&nbsp;&nbsp;&nbsp; RateBreak&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;RateType&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;WeightBreaks</tr>
 <td  align="center"><input type='text' class='text' name='surchargeIds<%=sur%><%=i%>' id ='sr<%=sur%><%=i%>'size='15' maxlength="100" value="" onBlur='changeToUpper(this); breaksForSurchargeIds(this,<%=i%>,<%=sur%>) '></td><!--added by silpa.p on 31-05-11-->
  <td align="center"><input type='text' class='text' name='surchargeDesc<%=sur%><%=i%>' id ='srd<%=sur%><%=i%>'size='15' maxlength="100" value="" onBlur='changeToUpper(this); breaksForSurchargeDescription(this,<%=i%>,<%=sur%>)'></td><!--added by silpa.p on 31-05-11-->
 <td><input type='text' class='text' name='ratebreak<%=sur%><%=i%>' id = 'ratebreak<%=sur%><%=i%>'
 size='15' maxlength="100" readonly></td>
  <td><input type='text' class='text' name='ratetype<%=sur%><%=i%>' id = 'ratetype<%=sur%><%=i%>'
 size='15' maxlength="100" readonly></td>
 <td colspan='3' align="center"><input type='text' class='text' name='surchargeWeightBreaks<%=sur%><%=i%>' id ='srw<%=sur%><%=i%>'size='15' maxlength="100" value="" onBlur='breaks(this,<%=i%>,<%=sur%>);changeToUpper(this)'  readonly><!--added by silpa.p on 31-05-11-->
		 <input type='button' class='input'  name='Surcharge' value='...' onclick="openSurchargeLov('tr<%=sur%><%=i%>')"></td>
		 <!--modified by silpa.p for surcharge lov id on 4-06-11-->

 </tr></table></td></tr>
 <tr class="formdata">
 <td valign="bottom" width='10%'>Currency</td>
 <td valign="bottom"><input class="text" type="text" value="" size="7" name="currencyId<%=sur%><%=i%>" onBlur="changeToUpper(this)">
	 <input type='button' class='input'  name='currencyLov<%=sur%><%=i%>' id="currencyLov<%=sur%><%=i%>" value='...' onclick="pop1(<%=sur%>,<%=i%>);"></td>
 <td colspan="7">
 <span id="spotRatesSurSlab<%=sur%><%=i%>" style='DISPLAY:none'>
	<table width='100%' cellpadding='4' cellspacing='0'  border='0'>
		<tr class='formdata'>
			<td rowspan="2" nowrap valign="bottom" colspan="7"><b>SurCharges</b></td>	
			<td ><b>Min</b><input type="hidden" name='srslabbreaks<%=sur%><%=i%>' id='srslabbreaks<%=sur%><%=i%>0' value="MIN"></td>
			<%for(int k=1;k<11;k++){%>
				<td><input type=text  class='text' name='srslabbreaks<%=sur%><%=i%>' id='srslabbreaks<%=sur%><%=i%><%=k%>'  value=""  size='4'  onblur="minvalues('srslabbreaks<%=sur%><%=i%>',<%=k%>)" onkeypress='return getNumberCodeNegative(this)'> </td>
			<%}%>
		</tr>
		<tr class="formdata">
			<%for(int k=0;k<11;k++){%>
				<td>
				 <input type=text  class='text' name= 'srslabvalues<%=sur%><%=i%>'  id= 'srslabvalues<%=sur%><%=i%><%=k%>'  value="" onblur='decimal(this)' size='4'    onkeypress='return getNumberCodeNegative(this)'>
				</td>
			<%}%>
		</tr>
	 </table>
 </span>
<span id="spotRatesSurBoth<%=sur%><%=i%>" style='DISPLAY:none'>
	<table width='50%' cellpadding='4' cellspacing='0'  border='0'>
		<tr class='formdata'>
			<td rowspan="2" nowrap valign="bottom" colspan="7"><b>SurCharges</b></td>	
			<td align="center">
				 <input type=text  class='text' name='srMin' id='srMin' value='MIN'  size='3' id='0'  readonly>
				 <input type='hidden'  class='text' name='srbothbreaks<%=sur%><%=i%>' id='srbothbreaks<%=sur%><%=i%>0' value='MIN'>
			</td>			
			<%for(int k=1;k<11;k++){%>
              <td align="center"><input type=text  class='text' name='srbothbreaks<%=sur%><%=i%>' id='srbothbreaks<%=sur%><%=i%><%=k%>'  value=""  size="3" id='0'  maxlength='10' onblur="minvalues1('srbothbreaks<%=sur%><%=i%>',<%=k%>)" onkeypress='return getNumberCodeNegative(this)'></td>
			<%}%>
		</tr>
		<tr class='formdata'>
			<%for(int k=0;k<11;k++){
				/*if(k==0){%>
               <!--  <td><input type=text  class='text' name='srslabvalue<%=sur%><%=i%>'  value=""  size=3 id='srslabvalue<%=sur%><%=i%><%=k%>'  ><input type="text"  class='text' name='srflatvalue<%=sur%><%=i%>'  value=""  id='srflatvalue<%=sur%><%=i%><%=k%>'></td> -->
			<%/*}else{*/%>
           	  <td><input type=text  class='textHighlight' name='srslabvalue<%=sur%><%=i%>'  value=""  size='1' id='srslabvalue<%=sur%><%=i%><%=k%>' onpaste='return false;' autocomplete='off' maxlength='10' onkeypress='slabBothCheck(srflatvalue<%=sur%><%=i%><%=k%>);return getNumberCodeNegative(this);'><input type=text  class='text' name='srflatvalue<%=sur%><%=i%>'  value=""  size="2" id='srflatvalue<%=sur%><%=i%><%=k%>' 
onpaste='return false;' autocomplete='off'  onkeypress='slabBothCheck(srslabvalue<%=sur%><%=i%><%=k%>);return getNumberCodeNegative(this);'></td>
			<%}%>
		</tr>
	 </table>
 </span>
 <div id="spotRatesSurElse<%=sur%><%=i%>"></div>
 </td>
 </tr></table>
 </fieldset>
 </td>
 </tr>
  
 <%}  }else{
			
	spotRateDetails = legRateDetails.getSpotRateDetails();
	weightBreakSlabs= legRateDetails.getWeightBreaks();
	marginType		= legRateDetails.getMarginType();
	marginValue		= legRateDetails.getMarginValue();
	checkedFlagValue= legRateDetails.getCheckedFlag();
	System.out.println(checkedFlagValue+"checkedFlagValue");
	int spotSize	= spotRateDetails.size();	
	String[] srdesc	= null;
	String temp		= "";	
	String[] surid	= null;
	String temp2	= "";
	srWeightBreak	= legRateDetails.getWeightBreak();
	//System.out.println("srWeightBreak--------"+srWeightBreak);
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
		//System.out.println("surChargeId.size()--------"+surChargeId.size());
	srdesc = temp.split(",");
	temp = "";
	temp2= "";
	if(surChargeId!=null){
	for(int t=0;t<surChargeId.size();t++)
	{		
		if(surChargeId.get(t)!=null && !temp2.equalsIgnoreCase((String)surChargeId.get(t)))
		{			
			temp = temp+","+(String)surChargeId.get(t);
			temp2 = (String)surChargeId.get(t);
		}
	}
	surid = temp.split(",");		
	System.out.println(temp+"temp");
	}
	//double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(0));
    //System.out.println(rate1[0]+"&&&");	
	currencyId		= new LinkedHashSet();
	currencyId		= legRateDetails.getSurCurrency()!=null?legRateDetails.getSurCurrency():null;
	//System.out.println("currencyId---"+currencyId);
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
 <td align="center"><input  type="hidden" value='<%=legRateDetails.getCurrency()!= null?legRateDetails.getCurrency().split(",")[0]:""%>' name="currencyId<%=i%>" > <%=legRateDetails.getCurrency()!= null?legRateDetails.getCurrency().split(",")[0]:""%> </td>
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
				<td align='center' ><input class='text' type='text' size='6' class='text' name='flatRate<%=i%>' id='flatRate<%=i%><%=k%>' onKeyPress='return getDotNumberCode(this)' value='<%=rate1[2]%>'>				
				<input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>' >
				<input type='hidden' name='marginType<%=i%>' id='marginType<%=i%><%=k%>' value='<%=marginType!=null?marginType.get(k):"A"%>' >
				<input type='hidden' name='marginValue<%=i%>' id='marginValue<%=i%><%=k%>' value='<%=marginValue!=null?marginValue.get(k):0.0%>' >
				</td>
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
			<td align='center' ><input class='text' maxLength='8' name='slabRate<%=i%>' id='slabRate<%=i%><%=k%>' size='3' onKeyPress='return getDotNumberCode(this)' value='<%=rate1[2]%>'>
			<input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>'>
		<input type='hidden' name='marginType<%=i%>' id='marginType<%=i%><%=k%>' value='<%=marginType!=null?marginType.get(k):"A"%>' >
			<input type='hidden' name='marginValue<%=i%>' id='marginValue<%=i%><%=k%>' value='<%=marginValue!=null?marginValue.get(k):0.0%>' > </td>
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
				<td align='center'valign="bottom" ><input class='text' maxLength='8' name='listRate<%=i%>' id='listRate<%=i%><%=k%>' size='6' onKeyPress='return getNumberCode(this)' value='<%=rate1[2]%>'>
				<input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>'>
				<input type='hidden' name='marginType<%=i%>' id='marginType<%=i%><%=k%>' value='<%=marginType!=null?marginType.get(k):"A"%>' >
				<input type='hidden' name='marginValue<%=i%>' id='marginValue<%=i%><%=k%>' value='<%=marginValue!=null?marginValue.get(k):0.0%>' > </td>
			<%}}%>
		</tr>
	</table>
	<%}%>
 </td></tr>
 <%System.out.println(surChargeId+"surchargeid");
	if(surid!=null){%>
 <tr class='formheader'>
 <td valign="bottom" colspan="9"><b>SurCharges</b></td></tr>
 <%}%>
 <%
   //System.out.println(surChargeId+"surChargeId---"+srWeightBreak);
	Iterator srWB = surid!=null?srWeightBreak.iterator():null;
	Iterator cur  = surid!=null?currencyId.iterator():null;
	String wtBreakValue;
	String rateBreak= null;
	String rateType = null;
	String curr;
	if(surid!=null){
		//System.out.println("surid.length-1-------"+(surid.length-1));
	for(int sur=0;sur<surid.length-1;sur++){
		if(srWB.hasNext()){
		wtBreakValue = ((String)srWB.next()).substring(surid[sur+1].length()+1);
		//System.out.println("wtBreakValue------*"+wtBreakValue);
		curr		 = (String)cur.next();
		//System.out.println("curr------*********"+curr);
		
	%>

 <tr class="formdata"><td colspan="9">
		<fieldset>
 <legend><b><%=toTitleCase(srdesc[sur+2])%><b></legend> 
 <table width='40%' cellpadding='4' cellspacing='0'  border='0'>
 <tr class="formdata"><td colspan="9">
 Lane <%=sur+1%></td></tr>
 <tr class="formdata"><td colspan="9">
 <table width='40%' cellpadding='4' cellspacing='0'  border='0'><tr class="formdata" id="tr<%=sur%><%=i%>">
  <tr align="center">&nbsp;SurCharge Id &nbsp;&nbsp;&nbsp &nbsp;SurCharge Desc&nbsp;&nbsp;&nbsp;&nbsp; RateBreak&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;RateType&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;WeightBreaks</tr><!-- modified by silpa.p on 13-05-11-->
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
 <td colspan="3"></td>
 </tr></table>

 <tr class="formdata">
 <td valign="bottom" >Currency</td>
 <td valign="bottom"><input  type="text" class='text' size='6' value='<%=((String[])curr.split(","))[1]%>' id="currencyId<%=sur%><%=i%>" name="currencyId<%=sur%><%=i%>" readonly >
 </td>
 <td colspan="7">
 <%
	 if("Slab".equalsIgnoreCase(wtBreakValue)){
	 //System.out.println("IN slab");
	 slabBothCount =0;%>
 <table width='50%' cellpadding='4' cellspacing='0'  border='0'>
		<tr class='formdata'>
			<td rowspan="2" nowrap valign="bottom" colspan="7"><b>Rate</b></td>	
			<%for(int k=0;k<spotSize;k++){
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {
					slabBothCount++;%>
				<td><input type='text' class='text' name='srslabbreaks<%=sur%><%=i%>' id='srslabbreaks<%=sur%><%=i%><%=slabBothCount%>'  size='4' value='<%=((String)weightBreakSlabs.get(k)).substring(surid[sur+1].length())%>' > </td>
			<% }}
					for(int ct=slabBothCount+1;ct<=11;ct++){%>
					<td><input type='text' class='text'  name='srslabbreaks<%=sur%><%=i%>' size='4' id='srslabbreaks<%=sur%><%=i%><%=slabBothCount%>'  value="" > </td>
					<%}
					System.out.println("slabBothCount--------"+slabBothCount);%>
			<!-- <td ><b>Min</b><input type="hidden" name='srslabbreaks<%=sur%><%=i%>' id='srslabbreaks<%=sur%><%=i%>0' value="MIN"></td>
			<%for(int k=0;k<11;k++){%>
				<td><input type=text  class='text' name='srslabbreaks<%=sur%><%=i%>' id='srslabbreaks<%=sur%><%=i%><%=k%>'  value=""  size='4'  > </td>
			<%}%> -->
		</tr>
		<tr class="formdata">
			<%slabBothCount=0;
			for(int k=0;k<spotSize;k++){
					double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(k));
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {
					slabBothCount++;%>
				<td><input type='text' class='text' size='4' name='srslabvalues<%=sur%><%=i%>' id='srslabvalues<%=sur%><%=i%><%=slabBothCount%>' value='<%=rate1[2]%>'><input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>'>
				<input type='hidden' name='marginType<%=i%>' id='marginType<%=i%><%=slabBothCount%>' value='<%=marginType!=null?marginType.get(k):"A"%>' >
				<input type='hidden' name='marginValue<%=i%>' id='marginValue<%=i%><%=slabBothCount%>' value='<%=marginValue!=null?marginValue.get(k):0.0%>' ></td>
			<% }}
				for(int ct=slabBothCount+1;ct<=11;ct++){%>
                 <td><input type='text' class='text' size='4' name='srslabvalues<%=sur%><%=i%>' id='srslabvalues<%=sur%><%=i%><%=slabBothCount%>' value=""><input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value="">
				<input type='hidden' name='marginType<%=i%>' id='marginType<%=i%><%=slabBothCount%>' value='A' >
				<input type='hidden' name='marginValue<%=i%>' id='marginValue<%=i%><%=slabBothCount%>' value='0.0' ></td>
				<%}%>
			<!-- <%for(int k=0;k<11;k++){%>
				<td>
				 <input type=text  class='text' name='srslabvalues<%=sur%><%=i%>' id='srslabvalues<%=sur%><%=i%><%=k%>'  value="" size='4' >
				</td>
			<%}%> -->
		</tr>
	 </table>
 <%}else if("Both".equalsIgnoreCase(wtBreakValue)){
			slabBothCount =0;%> 
 <table width='50%' cellpadding='4' cellspacing='0'  border='0'>
		<tr class='formdata'>
			<td rowspan="2" nowrap valign="bottom" colspan="7"><b>Rate</b></td>
			<%for(int k=0;k<spotSize;k++){
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {
					slabBothCount++;%>
				<td><input type='text' class='text'  name='srbothbreaks<%=sur%><%=i%>' id='srbothbreaks<%=sur%><%=i%><%=slabBothCount%>' size='3' value='<%=((String)weightBreakSlabs.get(k)).substring(surid[sur+1].length())%>' ></td>
			<% }}System.out.println("slabBothCount---------"+slabBothCount);
					for(int ct=slabBothCount;ct<11;ct++){%>
               <td><input type='text' class='text' name='srbothbreaks<%=sur%><%=i%>' id='srbothbreaks<%=sur%><%=i%><%=ct%>'  value="" size='3'>
			   </td>
					<%}%>
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
			<%slabBothCount=0;
				for(int k=0;k<spotSize;k++){
					
				double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(k));
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {
					slabBothCount++;
				if("Minu".equalsIgnoreCase(((String)weightBreakSlabs.get(k)).substring(surid[sur+1].length()))){%>
                <td><input type=text  class='text' name='srslabvalue<%=sur%><%=i%>'  size=3 id='srslabvalue<%=sur%><%=i%><%=slabBothCount%>'  value='<%=rate1[2]%>'><input type="hidden"  class='text' name='srflatvalue<%=sur%><%=i%>'  value='<%=rate1[2]%>'  id='srflatvalue<%=sur%><%=i%><%=slabBothCount%>'>
				<input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>'>
				<input type='hidden' name='marginType<%=i%>' id='marginType<%=i%><%=slabBothCount%>' value='<%=marginType!=null?marginType.get(k):"A"%>' >
				<input type='hidden' name='marginValue<%=i%>' id='marginValue<%=i%><%=slabBothCount%>' value='<%=marginValue!=null?marginValue.get(k):0.0%>' ></td>
			<%}else{%>
           	  <td><input type=text  class='textHighlight' name='srslabvalue<%=sur%><%=i%>'  value='<%="slab".equalsIgnoreCase((String)chargeRateIndicator.get(k))?rate1[2]:""%>'  size='1' id='srslabvalue<%=sur%><%=i%><%=slabBothCount%>' onpaste='return false;' autocomplete='off' maxlength='10' onkeypress='slabBothCheck(srflatvalue<%=sur%><%=i%><%=slabBothCount%>);return getNumberCodeNegative(this);'><input type=text  class='text' name='srflatvalue<%=sur%><%=i%>'  value='<%="flat".equalsIgnoreCase((String)chargeRateIndicator.get(k))?rate1[2]:""%>'  size="2" id='srflatvalue<%=sur%><%=i%><%=slabBothCount%>' onpaste='return false;' autocomplete='off'
			  onkeypress='slabBothCheck(srslabvalue<%=sur%><%=i%><%=slabBothCount%>);return getNumberCodeNegative(this);'>
			  <input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>'>
			  <input type='hidden' name='marginType<%=i%>' id='marginType<%=i%><%=slabBothCount%>' value='<%=marginType!=null?marginType.get(k):"A"%>' >
				<input type='hidden' name='marginValue<%=i%>' id='marginValue<%=i%><%=slabBothCount%>' value='<%=marginValue!=null?marginValue.get(k):0.0%>' ></td>
			<%}}}
				for(int ct=slabBothCount+1;ct<=11;ct++){%>
               <td><input type=text  class='textHighlight' name='srslabvalue<%=sur%><%=i%>'  value="" size='1' id='srslabvalue<%=sur%><%=i%><%=ct%>' onpaste='return false;' autocomplete='off' maxlength='10' onkeypress='slabBothCheck(srflatvalue<%=sur%><%=i%><%=ct%>);return getNumberCodeNegative(this);'><input type=text  class='text' name='srflatvalue<%=sur%><%=i%>'  value=""  size="2" id='srflatvalue<%=sur%><%=i%><%=ct%>' onpaste='return false;' autocomplete='off'  onkeypress='slabBothCheck(srslabvalue<%=sur%><%=i%><%=ct%>);return getNumberCodeNegative(this);'>
			  <input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value="">
			  <input type='hidden' name='marginType<%=i%>' id='marginType<%=i%><%=ct%>' value="" >
				<input type='hidden' name='marginValue<%=i%>' id='marginValue<%=i%><%=ct%>' value="" ></td>
			<%}%>
		</tr>
	 </table>
 <%}else if("List".equalsIgnoreCase(wtBreakValue)){%>
	<table width='50%' cellpadding='4' cellspacing='0'  border='0'>
		<tr class='formdata'>
			<td rowspan="2" nowrap valign="bottom" colspan="7"><b>Rate</b></td>				
			<%for(int k=0;k<spotSize;k++){
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {%>
				<td><input type='hidden'  name='srList<%=sur%><%=i%>' id='srList<%=sur%><%=i%><%=k%>'  value='<%=((String)weightBreakSlabs.get(k)).substring(0,((String)weightBreakSlabs.get(k)).length()-5)%>' ><%=((String)weightBreakSlabs.get(k)).substring(0,((String)weightBreakSlabs.get(k)).length()-5)%> </td>
			<% }}%>
		</tr>
		<tr class="formdata">
			<%for(int k=0;k<spotSize;k++){
					double[] rate1 = (double[])spotRateDetails.get(weightBreakSlabs.get(k));
				if(!"A FREIGHT RATE".equalsIgnoreCase((String)rateDescription.get(k)) && surid[sur+1].equalsIgnoreCase((String)surChargeId.get(k))) {%>
				<td><input type='text' class='text' size='6' name='srListValue<%=sur%><%=i%>' id='srListValue<%=sur%><%=i%><%=k%>' value='<%=rate1[2]%>'><input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>'>
				<input type='hidden' name='marginType<%=i%>' id='marginType<%=i%><%=k%>' value='<%=marginType!=null?marginType.get(k):"a"%>' >
				<input type='hidden' name='marginValue<%=i%>' id='marginValue<%=i%><%=k%>' value='<%=marginValue!=null?marginValue.get(k):0.0%>' ></td>
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
				<td><input type='text' class='text' size='6' name='srelseValue<%=sur%><%=i%>' id='srelseValue<%=sur%><%=i%><%=k%>' value='<%=rate1[2]%>'><input type='hidden' name='checkedFlag<%=i%>' id='checkedFlag<%=i%>' value='<%=checkedFlagValue!=null?checkedFlagValue.get(k):""%>'> 
				<input type='hidden' name='marginType<%=i%>' id='marginType<%=i%><%=k%>' value='<%=marginType!=null?marginType.get(k):"A"%>' >
				<input type='hidden' name='marginValue<%=i%>' id='marginValue<%=i%><%=k%>' value='<%=marginValue!=null?marginValue.get(k):0.0%>' ></td>
			<% }}%>
		</tr>
	 </table>
 <%}%>
	  </td>
 </tr>
 </td>
 </tr></table>
 </fieldset></tr></td>
 <%}} }}
if(!"Freight".equalsIgnoreCase(masterDOB.getQuoteWith())) {  // Added by Anil.k%>
<tr class ='formheader'><td  colspan = '10'>ApplicableChargeGroups</td></tr>
<tr> <td colspan = '10'>
<table class ='tabledata' border="0" id='chargeDetails<%=i%>'cellPadding="4" cellSpacing="1" width="100%" bgcolor='DCDCDC'>
<%if(("MODIFY".equalsIgnoreCase(operation) && !"INCOMPLETE".equalsIgnoreCase(completeFlag) ) || "COPY".equalsIgnoreCase(operation)|| "BackButton".equalsIgnoreCase(backButton)){
         legRateDetails    =   (MultiQuoteFreightLegSellRates)freightRates.get(i);
		 int chargeslen = legRateDetails.getChargeGroupIds()!= null?legRateDetails.getChargeGroupIds().length:0;
		for(int j=0;j<chargeslen;j++)
		{%>
	   <tr id='trr<%=i%><%=j%>' class ='formdata' >
	   <td ><input class="input" type="button" value="<<" onclick="deleteChargeRow(this,'trr<%=i%><%=j%>',<%=i%>)"></td>
	   <td align='center'><input class="text" maxLength="16" name="chargeGroupIds<%=i%>" id='chargeGroupIds<%=i%><%=j%>' size="28"  value="<%=legRateDetails.getChargeGroupIds()[j]!= null ?legRateDetails.getChargeGroupIds()[j]:""%>" onBlur="changeToUpper(this);"><!--onblur = "return this.upper();" modified by silpa.p on 25-05-11-->
	   <input class="input" type="button" name="chargeGroupIdBt" id="chargeGroupIdBt<%=i%><%=j%>" value="..." onclick="popUpWindow(this,<%=i%>,<%=j%>)">
	   <input class="input" type="button" name="chargeGroupIdDet" value="ViewDetails" onclick="popUpWindow(this,<%=i%>,<%=j%>)"></td>
	   <td><input class="input" type="button" value=">>" onclick="addNewChargeRow(this,'trr<%=i%><%=j%>',<%=i%>)"></td>
	   </tr>
<%}
	 

}else{%>
<tr id='trr<%=i%>0' class ='formdata' >
<td ><input class="input" type="button" value="<<" onclick="deleteChargeRow(this,'trr<%=i%>0',<%=i%>)"></td>
<td align='center'><input class="text" maxLength="16" name="chargeGroupIds<%=i%>" id='chargeGroupIds<%=i%>0' size="28"  value=""  onblur = "changeToUpper(this);"><!--onblur = "return this.upper();" modified by silpa.p on 25-05-11-->
<input class="input" type="button" name="chargeGroupIdBt" id="chargeGroupIdBt<%=i%>0" value="..." onclick="popUpWindow(this,<%=i%>,0)">
<input class="input" type="button" name="chargeGroupIdDet" value="ViewDetails" onclick="popUpWindow(this,<%=i%>,0)"></td>
<td><input class="input" type="button" value=">>" onclick="addNewChargeRow(this,'trr<%=i%>0',<%=i%>)"></td>
</tr>
<%}%>
</table>
<%} //Added by Anil.k%>
 </td>
 </tr>
 <tr>
<td><input class="input" name="submit" type="submit" id='submit' value="<< Back" onClick="setName(this);" >
</td>
<td><input class="input" name="submit" type="submit" value="Next >>" onClick="setName(this);">
</td></tr>
 </table>
 </div>
<%
		} }
%>
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
						<!-- <input class="input" name="submit" type="submit" id='submit' value="<< Back" onClick="setIndexForChecked(this)"> -->
						<%if(!displayFlag){%>
						<!-- <input class="input" name="submit" type="submit" value="Next >>" onClick="setIndexForChecked(this);  return setIndexForChecked1(this,'sellRatesSize','rate.length')"> -->
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