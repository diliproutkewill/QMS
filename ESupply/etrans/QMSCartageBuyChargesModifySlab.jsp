<%--
% 
% Copyright (c) 1999-2005 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% QMS - v 1.0 
%
--%>
<%@ page import = "java.util.ArrayList,
			java.util.Iterator,
			java.util.HashMap,
			java.sql.Timestamp,
			org.apache.log4j.Logger,
			com.foursoft.esupply.common.java.ErrorMessage,
			com.foursoft.esupply.common.java.KeyValue,
			com.qms.operations.charges.java.QMSCartageMasterDOB,
			com.qms.operations.charges.java.QMSCartageBuyDtlDOB,
			com.foursoft.esupply.common.util.ESupplyDateUtility"%>
			
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
      private static Logger logger = null;
      private static final  String  FILE_NAME = "QMSCartageBuyChargesModifySlab.jsp" ;
%>

<%
    logger  = Logger.getLogger(FILE_NAME);	
    ESupplyDateUtility  eSupplyDateUtility		= new ESupplyDateUtility();
    String              dateFormat				= loginbean.getUserPreferences().getDateFormat();
	String				terminalId				= loginbean.getTerminalId();
	String				operation				= null;
	String				subOperation			= null;
	String 				shipmentMode			= null;
	String				onLoadCalls				= "";
	ErrorMessage		errorMessageObject		= null;
	ArrayList			keyValueList			= new ArrayList();

	QMSCartageMasterDOB cartageMaster			= null;
	QMSCartageBuyDtlDOB delBuyChargesDtl		= null;
	
	QMSCartageBuyDtlDOB cartageBuyDtl			= null;

	HashMap				hMap					= null;
	HashMap				dHMap					= null;
	
	ArrayList			buyChargesList			= null;
	
	ArrayList			pickUpList				= null;
	ArrayList			delList					= null;
	ArrayList			slabList				= null;

	String				locationId				= null;
    String				zoneCode				= null;
	String[]			zoneCodes				= null;
    //String[]			vendorIds				= null;
	String				str[]					= null;
    String				currencyId				= null;
    String				chargeType				= null;
    String				chargeBasis				= null;
    String				delChargeBasis			= null;
    String				unitofMeasure			= null;
    String				weightBreak				= null;
    String				rateType				= null;
	
	String				consoleType				= null;

	int					counter					= 0;
	String				label					= "";

	Timestamp			effectiveFrom			= null;
	String				effectiveFromStr		= null;
    
	Timestamp			validUpto				= null;
    
	String				validUptoStr			= null;
	String				labelButton				=null;

	
	java.text.DecimalFormat df	=new java.text.DecimalFormat("##,##,##0.00");
	try
	{
		eSupplyDateUtility.setPatternWithTime(dateFormat);
	}
	catch(Exception exp)
	{
		//Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());
    logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
	}

	try
	{	
		operation				=	request.getParameter("Operation");
		subOperation			=	request.getParameter("subOperation");
		//shipmentMode			=	request.getParameter("shipmentMode");

		//Logger.info(FILE_NAME, " operation ", operation);

		if(subOperation!=null)
			onLoadCalls = ";displayMargin()";

		cartageMaster			=	(QMSCartageMasterDOB)session.getAttribute("cartageMaster");

		if(cartageMaster!=null)
		{
			zoneCode	    = cartageMaster.getZoneCode();
			locationId      = cartageMaster.getLocationId();
			currencyId      = cartageMaster.getCurrencyId();
			//vendorIds       = cartageMaster.getVendorIds();
			zoneCodes       = cartageMaster.getZoneCodes();
			//System.out.println(zoneCodes.length+"zoneCodes");
			weightBreak		= cartageMaster.getWeightBreak();
			rateType		= cartageMaster.getRateType();

			chargeType		= cartageMaster.getChargeType();
			shipmentMode	= cartageMaster.getShipmentMode();
			consoleType		= cartageMaster.getConsoleType();
			//chargeBasis		= cartageMaster.getChargeBasis();
			//unitofMeasure	= cartageMaster.getUom();
		}
		buyChargesList		= (ArrayList)request.getAttribute("buyChargesList");
        

		if(buyChargesList!=null && buyChargesList.size()>0)
		{
			pickUpList		= (ArrayList)buyChargesList.get(0);
			delList			= (ArrayList)buyChargesList.get(1);
			slabList		= (ArrayList)buyChargesList.get(2);
            session.setAttribute("slabList",slabList);
			if("Delivery".equals(chargeType))
			{
				label	= "DELIVERY CHARGES";
				counter = delList.size();
			}
			else if("Both".equals(chargeType))
			{
//@@ Commented & Added by subrahmanyam for the pbn issue 202272 on 08-04-010
			/*	label	= "PICKUP CHARGES";
				if(pickUpList.size() < delList.size())
					counter = pickUpList.size();
				else if(pickUpList.size()>0 && delList.size() == 0)
					counter = pickUpList.size();
				else if(delList.size() > 0 && pickUpList.size() == 0)
					counter =  delList.size();
				else
					counter =  delList.size();
					*/
			label	= "PICKUP CHARGES";
				 if(pickUpList.size()>0 && delList.size() == 0)
					counter = pickUpList.size();
				else if(delList.size() > 0 && pickUpList.size() == 0)
					counter =  delList.size();
				else if(pickUpList.size() < delList.size())
					counter = pickUpList.size();
				else
					counter =  delList.size();
//@@ Ended by subrahmanyam for the pbn issue 202272 on 08-04-010

			}
			else
			{	
				label	= "PICKUP CHARGES";
				counter = pickUpList.size();
			}
		}
		String readOnly ="";
		if(operation.equals("buyModify")){
			labelButton="Modify";
		}
		else if(operation.equals("buyView")){
			labelButton="Continue";
			readOnly   ="readOnly";
		}
%>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Cartage Sell Charges-Slab</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">

<script>
function Mandatory()
{
	var msgHeader		= '';
	var msgErrors		= '';
	var focusPosition	= new Array();
	
	if(document.forms[0].submitName.value=="Search")
	{
		msgHeader= '_____________________________________________________\n\n' +
		'This form has not been submitted because of the following error(s).\n' +
		'Please correct the error(s) and re-submit.\n' +
		'_____________________________________________________\n\n';
		if(document.forms[0].locationId.value.length == 0)
		{
			msgErrors += 'Location Id cannot be empty\n';
			focusPosition[6] = 'locationId';
		}
		if(document.forms[0].zoneCodes.value.length == 0)
		{
			msgErrors += 'Zone Code cannot be empty\n';
			focusPosition[8] = 'zoneCodes';
		}
		if(document.forms[0].baseCurrency.value.length == 0)
		{
			msgErrors += 'CurrencyId cannot be empty\n';
			focusPosition[10] = 'baseCurrency';
		}
		if(msgErrors.length > 0)
		{
			alert(msgHeader + msgErrors);
			for(loop =0 ;loop< focusPosition.length; loop++)
			{
				if(focusPosition[loop] != null && focusPosition[loop] != '')
				{
					document.forms[0].elements[focusPosition[loop]].focus();
					break;
				}
			}
			return false;
		}

		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type == 'text')
				document.forms[0].elements[i].value = document.forms[0].elements[i].value.toUpperCase();
		}
	}
	else if(document.forms[0].submitName.value=="Modify")
	{
		 var chkBox			=   document.getElementsByName("checkBoxValue");
		 var dChkBox		=	document.getElementsByName("dCheckBoxValue");

		 var marginValues	=	document.getElementsByName("marginValues");
		 var dMarginValues	=	document.getElementsByName("dMarginValues");

		 var flag			=	true;	

		 if(chkBox!=null && chkBox.length!=0)
		{
			flag = validateMargins(chkBox,marginValues,"slabBreak","slabValue","<%=label%>");
		}

		 if(flag && dChkBox!=null && dChkBox.length!=0)
		{
			return validateMargins(dChkBox,dMarginValues,"dSlabBreak","dSlabValue","DELIVERY CHARGES");
		}
		return flag;
	}
}

function validateMargins(chkBox,marginValues,slabBreak,slabValue,label)
{
	var checkedFlag		=	false;
	for(var i=0;i<chkBox.length;i++)
	{	
	  if(chkBox[i].value == "YES")
	  {
		checkedFlag	=	true;
	  }
  }
	if(!checkedFlag)
	 {
		alert('Please Select at least One Lane in '+label);
		return false;
	 }
	 else return true;
}

function setName(name)
{
	document.forms[0].submitName.value	= name;
	
	if(name=="Modify" || name=="Continue")
		document.forms[0].subOperation.value='modifySlab';
	else if(name=="Search")
		document.forms[0].subOperation.value='Details';
}
function toUpperCase(obj)
{
	obj.value = obj.value.toUpperCase();
}

function showCurrencyLOV()
{
	var searchString = document.forms[0].baseCurrency.value;
	myUrl= 'etrans/ETCCurrencyConversionAddLOV.jsp?searchString='+searchString+'&Operation=<%=operation%>';
	var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
	var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
	var myFeatures = myBars+','+myOptions;
	
	newWin = open(myUrl,'myDoc',myFeatures);
	if (newWin.opener == null) 
		newWin.opener = self;
	return true;

}
function showLocationLOV()
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=450,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = document.forms[0].locationId.value;
	var Url='etrans/ETCLOVLocationIds.jsp?from=cartage&wheretoset=locationId&searchString='+searchString+'&shipmentMode=7';
	var Win=open(Url,'Doc',Features);
}
function showZoneCodesLOV()
{
	if(document.forms[0].locationId.value.length==0)
	{
		alert('Please Enter the Location Id');
		document.forms[0].locationId.focus();
		return false;
	}
	else
	{
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=700,height=300,resizable=no';
		var Features=Bars+' '+Options;
		
		var searchString1 = document.forms[0].locationId.value;
		var Url='etrans/QMSLOVZoneCodesMultiple.jsp?wheretoset=zoneCodes&searchString1='+searchString1+'&shipmentMode='+document.forms[0].shipmentMode.value+'&consoleType='+document.forms[0].consoleType.value;
		var Win=open(Url,'Doc',Features);
	}
}
function setZoneCodeValues(obj,where)
{
	document.getElementById(where).length=0;
	for( i=0;i<obj.length;i++)
	{
		document.getElementById(where).options[i] = new Option(obj[i].value,obj[i].value,true,true);
	}
}
function changeRateOptions()
{
	var rateType	   = document.getElementById("rateType");
	
	if(document.forms[0].weightBreak.options.value=='Flat')
	{
		rateType.length=0;
		rateType[0] = new Option('Flat','Flat');

	}
	else
	{
		rateType.length=0;
		rateType[0] = new Option('Flat','Flat'<%="Flat".equals(rateType)?",true,true":""%>);
		rateType[1] = new Option('Slab','Slab'<%="Slab".equals(rateType)?",true,true":""%>);
		rateType[2] = new Option('Both','Both'<%="Both".equals(rateType)?",true,true":""%>);
	}
}

function changeUOMOptions()
{
	var uom			   = document.getElementById("uom");
	if(document.forms[0].chargeBasis.options.value=='Weight')
	{
		uom.length = 0;
		uom[0] = new Option('Kg','Kg'<%="Kg".equals(unitofMeasure)?",true,true":""%>);
		uom[1] = new Option('Lb','Lb'<%="Lb".equals(unitofMeasure)?",true,true":""%>);
	}
	else
	{
		uom.length = 0;
		uom[0] = new Option('CFT','CFT'<%="CFT".equals(unitofMeasure)?",true,true":""%>);
		uom[1] = new Option('CBM','CBM'<%="CBM".equals(unitofMeasure)?",true,true":""%>);
	}
}
function displayMargin()
{
	var percent    = document.getElementById("percent");
	var absolute   = document.getElementById("absolute");
	var notOverall = document.getElementById("notOverall");
	
  if(document.forms[0].overAllMargin!=null && document.forms[0].marginType!=null)
  {	
	if(document.forms[0].overAllMargin.options.value=='No')
	{
		percent.style.display="none";
		absolute.style.display="none";
		notOverall.style.display="block";
	}
	else if(document.forms[0].overAllMargin.options.value=='Yes' && document.forms[0].marginType.options.value=='Absolute')
	{
		percent.style.display="none";
		notOverall.style.display="none";
		absolute.style.display="block";
	}
	else
	{
		absolute.style.display="none";
		notOverall.style.display="none";
		percent.style.display="block";
	}
  }

	var obj	= document.getElementsByName('marginValues');

	for(var i=0;i<obj.length;i++)
	{
		obj[i].value = '';
	}
}
function selectAll()
{
	var checkBoxes = document.getElementsByName("chkBox");

	if(document.forms[0].select.checked)
	{
		for(var i=0;i<checkBoxes.length;i++)
		{		
			checkBoxes[i].checked=true;
			setValue(i);
		}
	}
	else
	{
		for(var i=0;i<checkBoxes.length;i++)
		{
			checkBoxes[i].checked=false;
			setValue(i);
		}
	}
}
function dSelectAll()
{
	var checkBoxes = document.getElementsByName("dChkBox");

	if(document.forms[0].dSelect.checked)
	{
		for(var i=0;i<checkBoxes.length;i++)
		{		
			checkBoxes[i].checked=true;
			setValueDelivery(i);
		}
	}
	else
	{
		for(var i=0;i<checkBoxes.length;i++)
		{
			checkBoxes[i].checked=false;
			setValueDelivery(i);
		}
	}
}
function setValue(index)
{
	if(document.getElementsByName("chkBox")[index].checked)
		document.getElementsByName("checkBoxValue")[index].value="YES";
	else
		document.getElementsByName("checkBoxValue")[index].value='';

}
function setValueDelivery(index)
{
	if(document.getElementsByName("dChkBox")[index].checked)
		document.getElementsByName("dCheckBoxValue")[index].value="YES";
	else
		document.getElementsByName("dCheckBoxValue")[index].value='';

}
function chrnum(input)
{
	s = input.value;
	filteredValues = "'~!@#$%^&*()_+=|\:;<>,/?";
	var i;
	var returnString = "";
	var flag = 0;
	for (i = 0; i < s.length; i++)
	{
		var c = s.charAt(i);
		if(filteredValues.indexOf(c) == -1)
				returnString += c.toUpperCase();
		else
			flag = 1;
	}
	if( flag==1 )
	{
		alert("Special Characters not allowed");
		var field = document.forms[0];
		for(i = 0; i < field.length; i++)
			if( field.elements[i] == input )
				document.forms[0].elements[i].focus();
	}

	input.value = returnString;
	if(flag==1) return false
	if(flag==0) return true
}
function getKeyCode(obj)
{
	if(isNaN(obj.value))
	{
		alert('Please Enter Numeric Values Only');
		obj.value = '';
		obj.focus();
		return false;
	}
}
function disableSubmit()
{
	var obj				= document.getElementsByName('Submit');
	
	for(var i=0;i<obj.length;i++)
	{
		if(obj[i].value=="Modify")
		{
			obj[i].disabled=true;
		}
	}
}
function viewContinue()//added by rk
{
	document.forms[0].subOperation.disabled=true;
	document.forms[0].action='<%=request.getContextPath()%>/QMSCartageController?Operation=buyView';
	document.forms[0].submit();
	return true;
	
}
	//@@Added by Kameswari for the WPBN issue-138502
function setBoundaries()
{

		
	<%if("Both".equalsIgnoreCase(chargeType) ||"Pickup".equalsIgnoreCase(chargeType))
	{
		for(int i=0;i<counter;i++)
		{
		int count = 0;
		%>
		var maxlobj ='lowerBound'+<%=i%>+<%=slabList.size()-1%>;
		 var maxval = 'slabValue'+<%=i%>+<%=slabList.size()-1%>;
       var maxuobj = 'upperBound'+<%=i%>+<%=slabList.size()-1%>;
             if( document.getElementById(maxlobj).value==""&& document.getElementById(maxval).value!='0.0')
			{
                document.getElementById(maxlobj).value='0';
				 document.getElementById(maxuobj).value='0';
			}
//@@ Added by subrahmanyam for Enhancement 170759
		var baselobj ='lowerBound'+<%=i%>+0;
	    var baseval = 'slabValue'+<%=i%>+0;
        var baseuobj = 'upperBound'+<%=i%>+0;
		 if( document.getElementById(baselobj).value==""&& document.getElementById(baseval).value!='0.0')
			{
                document.getElementById(baselobj).value='0';
				 document.getElementById(baseuobj).value='0';
			}
//@@ Ended by subrahmanyam for the enhancement 170759
		// for(int j=0;j<slabList.size()-1;j++)//@@ Commented by subrahmanyam for 170759
		<%for(int j=1;j<slabList.size()-1;j++)//@@ Added by subrahmanyam for 170759
			{
			 count = j;%>
			  
			var lobj = 'lowerBound'+<%=i%>+<%=j%>;
		   var sval = 'slabValue'+<%=i%>+<%=j%>;
		   var length = '<%=slabList.size()%>';
		  

		   if( document.getElementById(lobj).value==""&& document.getElementById(sval).value!='0.0')
			{
			   document.getElementById(lobj).value ='<%=slabList.get(j)%>';

            <% if(j<slabList.size()-2)
				{%>
			 for(var k=<%=j%>+1;k<length-1;k++)
			{
                var sVal = 'slabValue'+<%=i%>+k;  
			  var uobj = 'upperBound'+<%=i%>+<%=j%>;  
				if(k<length-2)
				{
				 if(document.getElementById(sVal).value!='0.0')
					{
					  document.getElementById(uobj).value ='<%=slabList.get(count+1)%>';
					 break;
				 }
				}
				 else
				{
				  document.getElementById(uobj).value ='100000';
				   break;
				 
				}
			  <%count++;%>
			  }
			  <%}
			  else
				{%>
                  document.getElementById(uobj).value ='100000';
				  <%
				}%>
		   }
				 <%}
		}
	}%>


<%if("Both".equalsIgnoreCase(chargeType) ||"Delivery".equalsIgnoreCase(chargeType))
	{
             for(int i=0;i<delList.size();i++)
		{
		   int count = 0;%>
			 var maxlobj ='dLowerBound'+<%=i%>+<%=slabList.size()-1%>;
		     var maxval = 'dSlabValue'+<%=i%>+<%=slabList.size()-1%>;
            var maxuobj = 'dUpperBound'+<%=i%>+<%=slabList.size()-1%>;
             if( document.getElementById(maxlobj).value==""&& document.getElementById(maxval).value!='0.0')
			{
                document.getElementById(maxlobj).value='0';
				 document.getElementById(maxuobj).value='0';
			}
//@@ Added by subrahmanyam for the enhancement 170759
			var baselobj ='dLowerBound'+<%=i%>+0;
		     var baseval = 'dSlabValue'+<%=i%>+0;
            var baseuobj = 'dUpperBound'+<%=i%>+0;
             if( document.getElementById(baselobj).value==""&& document.getElementById(baseval).value!='0.0')
			{
                document.getElementById(baselobj).value='0';
				 document.getElementById(baseuobj).value='0';
			}
//@@ Ended by subrahmanyam for the enhancement 170759

			//for(int j=0;j<slabList.size()-1;j++)//@@ Commented by subrahmanyam for the Enhancement 170759
		 <%for(int j=1;j<slabList.size()-1;j++)//@@ Added by subrahmanyam for the enhancement 170759
			{
			count = j;%>
		 var dsval = 'dSlabValue'+<%=i%>+<%=j%>;
		 var dlobj = 'dLowerBound'+<%=i%>+<%=j%>;	
		 var length = '<%=slabList.size()%>';
		 if( document.getElementById(dlobj).value==""&& document.getElementById(dsval).value!='0.0')
			{
		    
		   document.getElementById(dlobj).value ='<%=slabList.get(j)%>';
		    
			<% if(j<slabList.size()-2)
				{%>
               for(var k=<%=j%>+1;k<length-1;k++)
			{
                var dsVal = 'dSlabValue'+<%=i%>+k;  
			    var duobj = 'dUpperBound'+<%=i%>+<%=j%>;    
				if(k<length-2)
				{
					if(document.getElementById(dsVal).value!='0.0')
					{
					  document.getElementById(duobj).value ='<%=slabList.get(count+1)%>';
					
				   break;
			        }
				}
				else
			  	{
					
					  document.getElementById(duobj).value ='100000';
				   break;
					 
				}
			<%count++;%>
			    }
			<%}
			  else
				{%>
				
                  document.getElementById(duobj).value ='100000';
			
				  <%
				}%>
		   }
		   <%}
		}
	}%>
}
		//@@WPBN issue-138502
			  	

function clearValues(obj1,obj2)
{
	var ind = obj1.id.substring(1,obj1.id.length);

	document.getElementById(ind).value = obj1.id.substring(0,1).toUpperCase();

	if(obj2.value.length>0)
		obj2.value = '';
}
function clearDValues(obj1,obj2)//@@For Delivery Charges, when Charge Type is Both
{
	var ind = obj1.id.substring(2,obj1.id.length);


	document.getElementById("d"+ind).value = obj1.id.substring(1,2).toUpperCase();

	if(obj2.value.length>0)
		obj2.value = '';
}
function openLocationLov()
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";

	var operation	=	document.forms[0].Operation.value;

	formArray = 'locationId';

	if(operation.toUpperCase()=='BUYVIEW')
		operation	=	"LOCSETUPVIEW";
	else
		operation	=	"CARTAGELOC";
	
	tabArray = 'LOCATIONID';
	Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation="+operation+"&search= where LOCATIONID LIKE '"+document.forms[0].locationId.value+"~'";


	Options	='width=750,height=750,resizable=yes';
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Features=Bars+','+Options;

	Win=open(Url,'Doc',Features);
}
function changeConsoleVisibility()
{
	var console	 = document.getElementById('console');
	var weightBreak = document.getElementById('weightBreak');
	
	/*if(flag)
		document.forms[0].consoleType.options[0].selected = true;*/
	if(document.forms[0].shipmentMode.value=='2')
		console.style.display="block";
	else
	{
		console.style.display="none";
		document.forms[0].consoleType.options[0].selected = true;
		/*if(flag)
		{
			weightBreak.length=0;
			weightBreak[0] = new Option("Flat","Flat");
			weightBreak[1] = new Option("Slab","Slab");
			displaySlab();
		}
		document.getElementById("maxCharge").disabled=false;*/
	}
	changeWeightBreakOptions();
}
function changeWeightBreakOptions()
{
	var weightBreak = document.getElementById('weightBreak');
	var rateType	= document.getElementById('rateType');
	
	if(document.forms[0].consoleType.value=='FCL')
	{
		weightBreak.length=0;
		rateType.length=0;
		weightBreak[0] = new Option("List","List");
		rateType[0]= new Option("Flat","Flat");
	}
	else
	{
		weightBreak.length=0;		
		weightBreak[0] = new Option("Flat","Flat"<%="Flat".equalsIgnoreCase(weightBreak)?",true,true":""%>);
		weightBreak[1] = new Option("Slab","Slab"<%="Slab".equalsIgnoreCase(weightBreak)?",true,true":""%>);
	}
}
</script>
</head>

<body onload='changeRateOptions();changeConsoleVisibility();<%=onLoadCalls%>'>
	<form method='post' action='QMSCartageController' onsubmit='setBoundaries();return Mandatory();'>

	  <table width="100%" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
		
			<table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="4"><table width="100%" border="0" ><tr class='formlabel'><td>
				Cartage Buy Charges - <%="buyView".equalsIgnoreCase(operation)?"View":"Modify"%> </td></tr></table></td>
			  </tr>
			  </table>
<%
		if(request.getAttribute("Errors")!=null)
		{
%>
			<table width="100%"   border="0" cellpadding="4" cellspacing="0">
				<tr valign="top" bgcolor="#FFFFFF">
					<td width="33%"><font face="Verdana" size="2" color='red'><b>This form has not been submitted because of the following error(s) : </b> <br><br>
						<%=(String)request.getAttribute("Errors")%></font></td>
				</tr>
			</table>
			
<%
		}
%>
			  <table width="100%" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formdata'>
			   <td>Shipment Mode:<br>
				<select name="shipmentMode" class='select' onchange='changeConsoleVisibility()'>
					<option value="1" <%="1".equalsIgnoreCase(shipmentMode)?"selected":""%>>Air</option>
					<option value="2" <%="2".equalsIgnoreCase(shipmentMode)?"selected":""%>>Sea</option>
			  </td>
			  <td>
				<DIV id="console" style="DISPLAY:none">
					Console Type:</br>
					<select name="consoleType" class='select' onchange='changeWeightBreakOptions()'>
						<option value="LCL" <%="LCL".equalsIgnoreCase(consoleType)?"selected":""%>>LCL</option>
						<option value="FCL" <%="FCL".equalsIgnoreCase(consoleType)?"selected":""%>>FCL</option>
					</select>
				</DIV>
			  </td>
			    <td >Charge Type:<br>
                <select size="1" name="chargeType" class='select' onChange='disableSubmit()'>
					<option value="Pickup" <%="Pickup".equals(chargeType)?"selected":""%>>Pickup</option>
					<option value="Delivery" <%="Delivery".equals(chargeType)?"selected":""%>>Delivery</option>
					<option value="Both" <%="Both".equals(chargeType)?"selected":""%>>Both</option>
                </select>
			  </td>
			  <!--td>Charge Basis:<br>
				<select name="chargeBasis" class='select' onchange='changeUOMOptions();disableSubmit()'>
					<option value='Weight' <%="Weight".equals(chargeBasis)?"selected":""%>>Weight</option>
					<option value='Volume' <%="Volume".equals(chargeBasis)?"selected":""%>>Volume</option>
                </select>
			 </td>
			  <td>UOM: <br>
              <select name="uom" class='select' onchange='disableSubmit()'>
                <option selected value="Kg">KG</option>
				<option value="Lb">LBS</option>
              </select>
			 </td-->
           <td>Weight Break: <font color="#FF0000">*</font><br>
              <select name="weightBreak" class='select' onchange='changeRateOptions();disableSubmit()'>
                <option value="Flat" <%="Flat".equals(weightBreak)?"selected":""%>>Flat</option>
                <option value="Slab" <%="Slab".equals(weightBreak)?"selected":""%>>Slab</option>
			 </select>
		  </td>
		  <td colspan='2'>Rate Type: <font color="red">*</font><br>
              <select name="rateType" class='select' onchange='disableSubmit()'>
                <option selected value="Flat">Flat</option>
                <option value="Slab">Slab</option>
			 </select>
		 </td>
		</tr>           
			  
		   <tr valign="top" class='formdata'>
			<td colspan='2'>Location Id: <font color="red">*</font><br>
				<input type='text' class='text' name="locationId" size="10" onblur='toUpperCase(this)' value='<%=locationId!=null?locationId:""%>'>
				<input type="button" value="..." name="locationIdLOV" class='input' onclick='openLocationLov()'>
			</td>
			 <td>Zone Codes:<font color="#FF0000">*</font><br>
					<select size='5' name='zoneCodes' class='select' multiple>
<%
					if(zoneCodes!=null)
					{
						for(int i=0;i<zoneCodes.length;i++)
						{
%>							<option value='<%=zoneCodes[i]%>' selected><%=zoneCodes[i]%></option>
<%
						}
					}
%>
					<input type="button" value="..." name="zoneCodeLOV" class='input' onclick='showZoneCodesLOV()'>
				</td>
			<td colspan='4'>Currency:<font  color="red">*</font><br>
				<input type='text' class='text' name='baseCurrency' size='10' onblur='toUpperCase(this)' value='<%=currencyId!=null?currencyId:""%>'>
                <input type='button' value='...' name='creditcurrency' onClick='showCurrencyLOV()' class='input'>
            </td>
		  </tr>

			<tr valign="top" class='denotes'>
			   <td colspan="5" align='left'>
			   <font  color="red">*</font> Denotes Mandatory <br>
			  </td>
			   <td align='right'>
				<input name="submit1" type="Submit" value="Search" class='input' onclick="setName(this.value)">
			  </td>
				<input type="hidden" name="Operation" value="<%=operation%>">
				<input type="hidden" name="subOperation" value="Details">
				<input type="hidden" name="submitName">			   
			</tr>
		</table>
<%
	if(request.getAttribute("Errors")==null)
	{
%>
		<table width="100%" cellpadding="4" cellspacing="1">
<%	if(counter==0)
	{
%>
		<tr bgcolor="#FFFFFF"> 
			<td colspan="13" align="center">
			<font face="Verdana" size="2" color='red'>
			<b>No Charges Are Defined for the Specified Details.</b></font>
			</td>
		</tr>
<%
	}
	else if(("Both".equals(chargeType) && pickUpList.size()!=0) || (!"Both".equals(chargeType)))
	{
%>		 <tr valign="top" class='formheader' align="center">
<%
			if(!"buyView".equalsIgnoreCase(operation))
			{
%>            <td rowspan="2" width='5%'>Select<br><input type='checkbox' name='select' onclick='selectAll()'></td>
<%
			}
%>
            <td rowspan="2" width='10%'>Zone Code</td>
			<td rowspan="2" width='10%'>Effective From</td>
			<td rowspan="2" width='10%' nowrap>Valid Upto</td>
			<td rowspan="2" width='10%'>Charge Basis</td>
            <td colspan='<%=slabList.size()+1%>' align="center"><%=label%></td>			
		</tr>
         <tr valign="top" class='formheader' align="center">
<%
			 for(int i=0;i<slabList.size();i++)
			{
%>				<td><%=slabList.get(i)%></td>
<%			}
%>		<td rowspan="" width='10%' nowrap>Density Ratio</td>
		</tr>
<%		for(int i=0;i<counter;i++)
		{
			if("Delivery".equals(chargeType))
				cartageBuyDtl	=	(QMSCartageBuyDtlDOB)delList.get(i);
			else
				cartageBuyDtl	=	(QMSCartageBuyDtlDOB)pickUpList.get(i);
			
			hMap				=	 cartageBuyDtl.getSlabRates();
			//zoneCodes[i]		=	cartageBuyDtl.getZoneCode();
			effectiveFrom		=  cartageBuyDtl.getEffectiveFrom();
			validUpto			=  cartageBuyDtl.getValidUpto();
			chargeBasis			= cartageBuyDtl.getChargeBasis();
			str					= eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
			effectiveFromStr	= str[0];
			str					= null;

			str					= eSupplyDateUtility.getDisplayStringArray(validUpto);
			validUptoStr		= str[0];
			str					= null;
			
%>	
         <tr valign="top" class='formdata' align="center">
<%
			if(!"buyView".equalsIgnoreCase(operation))
			{
%>  
				<td><input type="checkbox" name="chkBox" onclick='setValue("<%=i%>")'></td>
<%
			}
%>
			<td nowrap>Zone <%=cartageBuyDtl.getZoneCode()%></td>
            <td><%=effectiveFromStr%></td>
            <td width='10%'><%=validUptoStr%></td>
            <td width='10%'><%=chargeBasis!=null?chargeBasis:""%></td>
			<input type='hidden' name='cartageId' value='<%=cartageBuyDtl.getCartageId()%>'>
			<input type='hidden' name='checkBoxValue'>
			<input type='hidden' name='effectiveFrom' value='<%=effectiveFromStr!=null?effectiveFromStr:""%>'>
			<input type='hidden' name='validUpto' value='<%=validUptoStr!=null?validUptoStr:""%>'>
			<input type='hidden' name='chargeBasis' value='<%=chargeBasis!=null?chargeBasis:""%>'>
			<input type='hidden' name='zoneCode' value='<%=cartageBuyDtl.getZoneCode()%>'>
			<!--  Add by VLAKSHMI for issue 172623 on 040609 -->
			<input type='hidden' name='chargeType<%=i%>' value='<%=cartageBuyDtl.getChargeType()%>'>
			<!-- -->
<%
			 for(int j=0;j<slabList.size();j++)
			{
				
                 
%>			    <td>
<%				if("Both".equalsIgnoreCase(rateType))
				{
					   //if(j==0)//@@ Commented by subrahmanyam for the Enhancement 170759 
					   if(j==0 || j==1)//@@ Added by subrahmanyam for the enhancement 170759
					   {
						   if( hMap.get(slabList.get(j))!=null  )//added by phani sekhar for 177473 on 20090723
					{
%>                    
						 <input type="text" name="slabValue<%=i%><%=j%>" value='<%=hMap.get(slabList.get(j))!=null?(String)hMap.get(slabList.get(j)):"0.0"%>' maxlength='8' size='5' class='text' <%=readOnly%>></td>

			<%}else{%>
				<input type="text" name="slabValue<%=i%><%=j%>" value='0.0' size='5' class='text' readOnly  maxlength='8'></td>
			<%}%>
						 <input type='hidden' name='indicator<%=i%><%=j%>' value=''>
<%					   }
					   else if(j==(slabList.size()-1))
					   { 
					if( hMap.get(slabList.get(j))!=null  )
					{
%>
						 <input type="text" name="slabValue<%=i%><%=j%>" value='<%=hMap.get(slabList.get(j))!=null?(String)hMap.get(slabList.get(j)):"0.0"%>' size='5' maxlength='8' class='text' <%=readOnly%>></td>
					<%}else{%>
							<input type="text" name="slabValue<%=i%><%=j%>" value='0.0' size='5' class='text' readOnly  maxlength='8'></td>
					<%}%>
						 <input type='hidden' name='indicator<%=i%><%=j%>' id='<%=i+""+j%>' value=''>
<%						}
						 else
						{   
%>                         
<%							if(hMap.get("Ind"+slabList.get(j))!=null && "F".equalsIgnoreCase((String)hMap.get("Ind"+slabList.get(j))))
							{
%>	<input type='text' class='text' name="slabValue<%=i%><%=j%>" size="2" value='' onkeypress='clearValues(this,document.getElementById("f<%=i+""+j%>"))' id='s<%=i+""+j%>' onpaste='clearValues(this,document.getElementById("f<%=i+""+j%>"))' maxlength='8' <%=readOnly%>><%if( hMap.get(slabList.get(j))!=null  ){%><input type='text' class='text' name="flatValue<%=i%><%=j%>" size="2" value='<%=hMap.get(slabList.get(j))!=null?(String)hMap.get(slabList.get(j)):"0.0"%>' id='f<%=i+""+j%>' onkeypress='clearValues(this,document.getElementById("s<%=i+""+j%>"))' onpaste='clearValues(this,document.getElementById("s<%=i+""+j%>"))' <%=readOnly%> maxlength='8'>			
						<%} else{%>
						<input type="text" name="slabValue<%=i%><%=j%>" value='0.0' size='5' class='text' readOnly  maxlength='8'></td>
						<%}%>
						<input type='hidden' name='indicator<%=i%><%=j%>' id='<%=i+""+j%>' value='F'>
					
<%							}
						else if(hMap.get("Ind"+slabList.get(j))!=null && "S".equalsIgnoreCase((String)hMap.get("Ind"+slabList.get(j))))
							{
if( hMap.get(slabList.get(j))!=null  )
{%><input type='text' class='text' name="slabValue<%=i%><%=j%>" size="2" value='<%=hMap.get(slabList.get(j))!=null?(String)hMap.get(slabList.get(j)):"0.0"%>' onkeypress='clearValues(this,document.getElementById("f<%=i+""+j%>"))' id='s<%=i+""+j%>' onpaste='clearValues(this,document.getElementById("f<%=i+""+j%>"))' <%=readOnly%> maxlength='8'><%} else {%><input type="text" name="slabValue<%=i%><%=j%>" value='0.0' size='5' class='text' readOnly  maxlength='8'></td><%	}%><input type='text' class='text' name="flatValue<%=i%><%=j%>" size="2" value='' id='f<%=i+""+j%>' onkeypress='clearValues(this,document.getElementById("s<%=i+""+j%>"))' onpaste='clearValues(this,document.getElementById("s<%=i+""+j%>"))' <%=readOnly%> maxlength='8'>
							<input type='hidden' name='indicator<%=i%><%=j%>' id='<%=i+""+j%>' value='S'>
					
<%							}
%>					  
<%					}
				}//end of rateType as BOTH	
				else
				{	
					if( hMap.get(slabList.get(j))!=null  )
					{
%>
                 
				<input type="text" name="slabValue<%=i%><%=j%>" value='<%=hMap.get(slabList.get(j))!=null?(String)hMap.get(slabList.get(j)):"0.0"%>' size='5' class='text' <%=readOnly%> maxlength='8' ></td>
<%	} else {%>
								<input type="text" name="slabValue<%=i%><%=j%>" value='0.0' size='5' class='text' readOnly  maxlength='8' ></td>

<%	}
	}
%>
				<input type='hidden' name='lowerBound<%=i%><%=j%>' value='<%=hMap.get("LB"+slabList.get(j))!=null?hMap.get("LB"+slabList.get(j)):""%>'>
				<input type='hidden' name='upperBound<%=i%><%=j%>' value='<%=hMap.get("UB"+slabList.get(j))!=null?hMap.get("UB"+slabList.get(j)):""%>'>
				<!-- <input type='hidden' name='lineNumber<%=i%><%=j%>' value='<%=hMap.get("LNO"+slabList.get(j))!=null?hMap.get("LNO"+slabList.get(j)):""%>'> -->
				 <input type='hidden' name='lineNumber<%=i%><%=j%>' > 
				<input type='hidden' name='slabBreak<%=i%><%=j%>' value='<%=slabList.get(j)%>'>
				
<%			}
%>				<td><%=cartageBuyDtl.getDensityRatio()!=null?cartageBuyDtl.getDensityRatio():""%></td>
				<input type='hidden' name='densityRatio<%=i%>'	value='<%=cartageBuyDtl.getDensityRatio()!=null?cartageBuyDtl.getDensityRatio():""%>'>
				</tr>
<%
		}
	}
		if("Both".equals(chargeType))
		{
			
			if(pickUpList.size()==0 && counter!=0)
			{
%>
		   <tr bgcolor="#FFFFFF"> 
			<td colspan="13" align="center">
			<font face="Verdana" size="2" color='red'>
			<b>No Pickup Charges Are Defined for the Specified Details.</b></font>
			</td>
		   </tr>
<%
			}
			else if(delList.size()==0 && counter!=0)
			{
%>
		   <tr bgcolor="#FFFFFF"> 
			<td colspan="13" align="center">
			<font face="Verdana" size="2" color='red'>
			<b>No Delivery Charges Are Defined for the Specified Details.</b></font>
			</td>
		   </tr>
<%
			}
			if(counter!=0 && delList.size()!=0)
			{
%>
			<tr class="formheader" align="center">
<%
			if(!"buyView".equalsIgnoreCase(operation))
			{
%>  
				<td rowspan="2" width='5%'>Select<br><input type='checkbox' name='dSelect' onclick='dSelectAll()'></td>
<%
			}
%>
            <td rowspan="2"  width='10%'>Zone Code</td>
            <td rowspan="2" width='10%'>Effective From</td>
            <td rowspan="2" width='10%' nowrap>Valid Upto</td>
			<td rowspan="2" width='10%'>Charge Basis</td>
            <td colspan="<%=slabList.size()+1%>">DELIVERY CHARGES</td>
           </tr>
           
		   <tr class="formheader" align="center">

<%  		for(int i=0;i<slabList.size();i++)
			{
%>				<td><%=slabList.get(i)%></td>
<%			}
%>			<td>Density Ratio</td>
		</tr>
<%
			for(int i=0;i<delList.size();i++)
			{
				delBuyChargesDtl	= (QMSCartageBuyDtlDOB)delList.get(i);
				dHMap				=  delBuyChargesDtl.getSlabRates();

				effectiveFrom		= delBuyChargesDtl.getEffectiveFrom();
				validUpto			= delBuyChargesDtl.getValidUpto();
				delChargeBasis		= delBuyChargesDtl.getChargeBasis();

				str					= eSupplyDateUtility.getDisplayStringArray(effectiveFrom);
				effectiveFromStr	= str[0];
				str					= null;

				str					= eSupplyDateUtility.getDisplayStringArray(validUpto);
				validUptoStr		= str[0];
				str					= null;

%>
        <tr class="formdata" align="center">
<%
			if(!"buyView".equalsIgnoreCase(operation))
			{
%>  
				<td><input type="checkbox" name="dChkBox" onclick='setValueDelivery("<%=i%>")'></td>
<%
			}
%>
            <td nowrap>Zone <%=delBuyChargesDtl.getZoneCode()%></td>
            <td><%=effectiveFromStr%></td>
            <td><%=validUptoStr%></td>
            <td><%=delChargeBasis!=null?delChargeBasis:""%></td>
			<input type='hidden' name='dCartageId' value='<%=delBuyChargesDtl.getCartageId()%>'>
			<input type='hidden' name='dCheckBoxValue'>
			<input type='hidden' name='dEffectiveFrom' value='<%=effectiveFromStr!=null?effectiveFromStr:""%>'>
			<input type='hidden' name='dValidUpto' value='<%=validUptoStr!=null?validUptoStr:""%>'>
			<input type='hidden' name='delChargeBasis' value='<%=delChargeBasis!=null?delChargeBasis:""%>'>
			<!--  Add by VLAKSHMI for issue 172623 on 040609 -->
			<input type='hidden' name='dZoneCode' value='<%=delBuyChargesDtl.getZoneCode()%>'>
			<input type='hidden' name='dChargeType<%=i%>' value='<%=delBuyChargesDtl.getChargeType()%>'>
			<!-- end -->
<%
				 for(int j=0;j<slabList.size();j++)
			{
%>			 <td>

			<%if("Both".equalsIgnoreCase(rateType)){
					 if(j==0 || j==1) //@@Added by subrahmanyam for the pbn id: 207172 on 02-Jun-10
					 {
						 	//modified by phani sekhar for wpbn 177473 on 20090723
						 if(dHMap.get(slabList.get(j))!=null){ 
			   %>
						 <input type="text" name="dSlabValue<%=i%><%=j%>" value='<%=dHMap.get(slabList.get(j))!=null?(String)dHMap.get(slabList.get(j)):"0.0"%>' size='5' class='text' <%=readOnly%> AUTOCOMPLETE="off" maxlength='8'></td>
				 <% }else {%>  
<input type="text" name="dSlabValue<%=i%><%=j%>" value='0.0' size='5' class='text' readOnly maxlength='8'></td>
						<%}%>
						 <input type='hidden' name='dIndicator<%=i%><%=j%>' value=''>
					<%}
					   else if(j==(slabList.size()-1))
					   {
						   if(dHMap.get(slabList.get(j))!=null){ 
				    %>
						 <input type="text" name="dSlabValue<%=i%><%=j%>" value='<%=dHMap.get(slabList.get(j))!=null?(String)dHMap.get(slabList.get(j)):"0.0"%>' size='5' class='text' <%=readOnly%> AUTOCOMPLETE="off" maxlength='8'></td>
						  <% }else {%>
<input type="text" name="dSlabValue<%=i%><%=j%>" value='0.0' size='5' class='text' readOnly maxlength='8'></td>
						<%}%>
						 <input type='hidden' name='dIndicator<%=i%><%=j%>' value=''>
					<%}else{%>
					  <%if(hMap.get("Ind"+slabList.get(j))!=null && "F".equalsIgnoreCase((String)dHMap.get("Ind"+slabList.get(j)))){%>
							<input type='text' class='text' name="dSlabValue<%=i%><%=j%>" size="2" value='' onkeypress='clearDValues(this,document.getElementById("df<%=i+""+j%>"))' id='ds<%=i+""+j%>' onpaste='clearDValues(this,document.getElementById("df<%=i+""+j%>"))' <%=readOnly%> AUTOCOMPLETE="off" maxlength='8'><%  if(dHMap.get(slabList.get(j))!=null){ %><input type='text' class='text' name="dFlatValue<%=i%><%=j%>" size="2" value='<%=dHMap.get(slabList.get(j))!=null?(String)dHMap.get(slabList.get(j)):"0.0"%>' id='df<%=i+""+j%>' onkeypress='clearDValues(this,document.getElementById("ds<%=i+""+j%>"))' onpaste='clearDValues(this,document.getElementById("ds<%=i+""+j%>"))' <%=readOnly%> AUTOCOMPLETE="off" maxlength='8'><% }else {%>
						<input type="text" name="dSlabValue<%=i%><%=j%>" value='0.0' size='5' class='text' readOnly maxlength='8'></td><%}%>
							<input type='hidden' name='dIndicator<%=i%><%=j%>' id='d<%=i+""+j%>' value='F'>
					
					  <%}else if(dHMap.get("Ind"+slabList.get(j))!=null && "S".equalsIgnoreCase((String)dHMap.get("Ind"+slabList.get(j)))){
						  if(dHMap.get(slabList.get(j))!=null){ 
						  %>
							<input type='text' class='text' name="dSlabValue<%=i%><%=j%>" size="2" value='<%=dHMap.get(slabList.get(j))!=null?(String)dHMap.get(slabList.get(j)):"0.0"%>' onkeypress='clearDValues(this,document.getElementById("df<%=i+""+j%>"))' id='ds<%=i+""+j%>' <%=readOnly%> AUTOCOMPLETE="off" onpaste='clearDValues(this,document.getElementById("df<%=i+""+j%>"))' maxlength='8'><% }else {%><input type="text" name="dSlabValue<%=i%><%=j%>" value='0.0' size='5' class='text' readOnly maxlength='8'></td><%}%><input type='text' class='text' name="dFlatValue<%=i%><%=j%>" size="2" value='' id='df<%=i+""+j%>' onkeypress='clearDValues(this,document.getElementById("ds<%=i+""+j%>"))' onpaste='clearDValues(this,document.getElementById("ds<%=i+""+j%>"))' <%=readOnly%> AUTOCOMPLETE="off" maxlength='8'>
							<input type='hidden' name='dIndicator<%=i%><%=j%>' id='d<%=i+""+j%>' value='S'>
					
					  <%}%>					  
					<%}
				}	else{	
					if(dHMap.get(slabList.get(j))!=null){
					%>
                 
				<input type="text" name="dSlabValue<%=i%><%=j%>" value='<%=dHMap.get(slabList.get(j))!=null?(String)dHMap.get(slabList.get(j)):"0.0"%>' size='5' class='text' <%=readOnly%> maxlength='8'></td>
	<%				}// ENDS 177473
				else {%>
			<input type="text" name="dSlabValue<%=i%><%=j%>" value='0.0' size='5' class='text' readOnly maxlength='8'></td>
				<%}	}%>

				<input type='hidden' name='dSlabBreak<%=i%><%=j%>'	value='<%=slabList.get(j)%>'>
				<input type='hidden' name='dLowerBound<%=i%><%=j%>' value='<%=dHMap.get("LB"+slabList.get(j))!=null?dHMap.get("LB"+slabList.get(j)):""%>'>
				<input type='hidden' name='dUpperBound<%=i%><%=j%>' value='<%=dHMap.get("UB"+slabList.get(j))!=null?dHMap.get("UB"+slabList.get(j)):""%>'>
				<!-- <input type='hidden' name='dLineNumber<%=i%><%=j%>'	value='<%=dHMap.get("LNO"+slabList.get(j))!=null?dHMap.get("LNO"+slabList.get(j)):""%>'> -->
				 <input type='hidden' name='dLineNumber<%=i%><%=j%>'> 
<%			}
%>           <td><%=delBuyChargesDtl.getDensityRatio()!=null?delBuyChargesDtl.getDensityRatio():""%></td>
             <input type='hidden' name='dDensityRatio<%=i%>'	value='<%=delBuyChargesDtl.getDensityRatio()!=null?delBuyChargesDtl.getDensityRatio():""%>'>
		</tr>
<%
			}
		  }
		}
	
	}
	
%>
<%	
    if(counter!=0)
	{
%>
 <table width='100%' cellpadding='4' cellspacing='1'>

         <tr valign="top" class='text'>
            <td colspan="8">* Denotes Mandatory</td>
            <td colspan="2" align='right'>
				<%if(!"buyView".equalsIgnoreCase(operation)){%>
				<input type="Reset" value="Reset" class='input'>
				<input type="submit" name="Submit1" value='<%=labelButton%>' class='input' onclick="setName(this.value)">
				
				<%}else{%>
				<input type="button" name="Submit1" value='Continue' class='input' onclick="viewContinue()">
				<%}%>
			</td>
		</tr>
	  </table>
	  <%}%>
	  </table>

	</form>
 </body>
</html>
<%	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"error in JSP:"+e);
    logger.error(FILE_NAME+"error in JSP:"+e);
		e.printStackTrace();
		errorMessageObject = new ErrorMessage("Unable to Access the Page! Please Try Again.","QMSCartageController"); 
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);

%>		<jsp:forward page="../ESupplyErrorPage.jsp"/>

<%
	}
%>