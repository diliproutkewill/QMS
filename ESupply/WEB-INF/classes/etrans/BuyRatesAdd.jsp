<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% QMS - v 1.x 
%
--%>
 <%@ page import = "org.apache.log4j.Logger,
					java.util.ArrayList,
					com.qms.operations.rates.dob.RateDOB,
					com.foursoft.esupply.common.bean.DateFormatter,

					"%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
%>

<%
    logger  = Logger.getLogger("BuyRatesAdd.jsp");
		String				effectiveFrom	=	"";	
		String				effectiveTo		=	"";	
		String				currency		=	""; 	
		String				carrier			=	""; 	
		String				weightBreak		=	"";
		String				rateType		=	"";
		String				consoleType		=	"";
		String				shipmentMode	=	"";
		String				UOM				=	"";
		boolean				flag			=	false;
		String[]			wtbrakValues	=	null;
		String[]			listValues		=	null;
		 String surchargeWeightBreaks[]			=	null;//Added by Govind for the CR-219973
    String surchargeDesc[]					=	null;//Added by Govind for the CR-219973
	String surchargeIds[]					=	null;//Added by Govind for the CR-219973
	String srchargeIds[]					=	null;//Added by Govind for the CR-219973
	String srwatbraks[]						=	null;//Added by Govind for the CR-219973
		int					size			=	0;

		DateFormatter		dateUtility		=	null;		
		RateDOB				rateDOB			=	null;
		

%>
<%
		rateDOB					=	(RateDOB)session.getAttribute("rateDOB");
		surchargeWeightBreaks  = (String[])session.getAttribute("surchargeWeightBreaks");//Govind
		surchargeIds		   = (String[])session.getAttribute("surchargeIds");//Govind
		surchargeDesc		   = (String[])session.getAttribute("surchargeDesc");//Govind
		dateUtility				=	 new DateFormatter();
		wtbrakValues			=	 new String[26];//Modified by Mohan for Issue No.219976 on 28-10-2010

		if(rateDOB!=null)
		{
			rateType			=	rateDOB.getRateType();		
			weightBreak			=	rateDOB.getWeightBreak();
			currency			=	rateDOB.getCurrency();
			carrier				=	rateDOB.getCarrierId();
			effectiveFrom		=   dateUtility.convertToString(rateDOB.getEffectiveFrom()).replaceAll("/","-");

			if(rateDOB.getValidUpto()!=null)
				effectiveTo			=	dateUtility.convertToString(rateDOB.getValidUpto()).replaceAll("/","-");

			consoleType			=	rateDOB.getConsoleType();
			shipmentMode		=	rateDOB.getShipmentMode();
			UOM					=	rateDOB.getUom();
			listValues			=	(String[])session.getAttribute("listValues");
			
		
			if(listValues!=null)
			{
				size	=	listValues.length;
			}

			flag				=	true;

			if("Slab".equalsIgnoreCase(weightBreak))
			{
				wtbrakValues =(String[]) session.getAttribute("wtbrak");
			}



			

		}
		//System.out.println(wtbrakValues[0]);
%>


<html>
  <head>
	<title>Customer Contracts - Add</title>
	<link rel="stylesheet" href="ESFoursoft_css.jsp">
	
<jsp:include page="ETDateValidation.jsp" >
	<jsp:param name="format" value="<%=loginbean.getUserPreferences().getDateFormat()%>"/>
</jsp:include>
<script language="javascript">

		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=360,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;

<%
	session.setAttribute("Operation","Add");
%>
function checkValidity(obj)
{
	if(obj.name=='effectiveFrom' && obj.value.length > 0 && !chkFutureDate(obj,"gt"))
	{
		alert('Please Enter Effective From Date greater than or equal to Current Date');
		obj.focus();
		return false;
	}
	else if(obj.name=='validUpto' && obj.value.length > 0 && document.forms[0].effectiveFrom.value.length > 0 && !chkFromToDate(document.forms[0].effectiveFrom.value, obj.value))
	{
		alert('Please Enter Valid Upto Date greater than or equal to Effective From Date');
		obj.focus();
		return false;
	}
	else return true;
}
//Added by Mohan for Issue No.219976 on 28-10-2010
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
//Modified by Mohan for Issue No.219976 on 28-10-2010
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
function showWeightBreaks()
{
	var data="";


	if(document.forms[0].weightBreak.value =="Slab")
	{
		//Modified by Mohan for Issue No.219976 on 28-10-2010
		data="Min <input type=text  class=text value='<%=wtbrakValues[0] != null ? wtbrakValues[0] : ""%>' name=wtbrak size=4 id=0 onkeypress='return getNumberCodeNegative(this)' maxlength=10 ><input type=text  class=text value='<%=wtbrakValues[1] != null ? wtbrakValues[1] : ""%>' name=wtbrak size=4 id=1 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[2] != null ? wtbrakValues[2] : ""%>' name=wtbrak size=4 id=2 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[3] != null ? wtbrakValues[3] : ""%>' name=wtbrak size=4 id=3 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[4] != null ? wtbrakValues[4] : ""%>' name=wtbrak size=4 id=4 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[5] != null ? wtbrakValues[5] : ""%>' name=wtbrak size=4 id=5 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[6] != null ? wtbrakValues[6] : ""%>' name=wtbrak size=4 id=6 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[7] != null ? wtbrakValues[7] : ""%>' name=wtbrak size=4 id=7 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[8] != null ? wtbrakValues[8] : ""%>' name=wtbrak size=4 id=8 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[9] != null ? wtbrakValues[9] : ""%>' name=wtbrak size=4 id=9 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[10]!= null ? wtbrakValues[10]: ""%>' name=wtbrak size=4 id=10 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[11]!= null ? wtbrakValues[11]: ""%>' name=wtbrak size=4 id=11 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[12]!= null ? wtbrakValues[12]: ""%>' name=wtbrak size=4 id=12 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[13]!= null ? wtbrakValues[13]: ""%>' name=wtbrak size=4 id=13 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[14]!= null ? wtbrakValues[14]: ""%>' name=wtbrak size=4 id=14 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[15]!= null ? wtbrakValues[15]: ""%>' name=wtbrak size=4 id=15 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[16]!= null ? wtbrakValues[16]: ""%>' name=wtbrak size=4 id=16 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[17]!= null ? wtbrakValues[17]: ""%>' name=wtbrak size=4 id=17 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[18]!= null ? wtbrakValues[18]: ""%>' name=wtbrak size=4 id=18 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[19]!= null ? wtbrakValues[19]: ""%>' name=wtbrak size=4 id=19 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[20]!= null ? wtbrakValues[20]: ""%>' name=wtbrak size=4 id=20 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[21]!= null ? wtbrakValues[21]: ""%>' name=wtbrak size=4 id=21 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[22]!= null ? wtbrakValues[22]: ""%>' name=wtbrak size=4 id=22 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[23]!= null ? wtbrakValues[23]: ""%>' name=wtbrak size=4 id=23 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=text  class=text value='<%=wtbrakValues[24]!= null ? wtbrakValues[24]: ""%>' name=wtbrak size=4 id=24 onkeypress='return getDotNumberCode(this)' maxlength=9 ><input type=hidden  name=wtbrak id=25 >";
		
	}	
	if( document.layers)
	{
		document.layers.cust.document.write(data);
		document.layers.cust.document.close();
	}
	else
	{
		if(document.all)
		{
			cust.innerHTML = data;
		}
	}
}
function showWtBreaks()
{
    <!--Added by Kishore Podili for the issue id:226791_Alignment on 17-Dec-2010 -->		
    document.getElementById("wtBreak").innerHTML="<select name='weightBreak' id ='weightBreak' class='select' size=1 onchange='showWeightBreaks();showConsole();chage();showListValues()'><option value='Flat'>Flat</option><option value='Slab'>Slab</option><option value='List'>List</option></select>";
}

function showConsole()
{

		var data="";
		var data1="";
		
	if(document.forms[0].shipmentMode.value=="Sea")
	{
		if(document.forms[0].weightBreak.value=='List')
		{
			data="<select name='consoleType' class='select' size=1 onChange='changeConsole();chage();showWeightBreaks();showListValues();'><option value='FCL'>FCL</option></select>"
		}else
		{
			data="<select name='consoleType' class='select' size=1 onChange='changeConsole();chage();showWeightBreaks();showListValues();'><option value='LCL'>LCL</option><option value='FCL'>FCL</option></select>"
		}
		data1="Console Type";
	     				
	}else if(document.forms[0].shipmentMode.value=="Truck")
	{
		 if(document.forms[0].weightBreak.value=='List')
		{
				data="<select name='consoleType' class='select' size=1 onChange='changeConsole();chage();showWeightBreaks();showListValues();'><option value='FTL'>FTL</option></select>";
		}else
		{
				data="<select name='consoleType' class='select' size=1 onChange='changeConsole();chage();showWeightBreaks();showListValues();'><option value='LTL'>LTL</option><option value='FTL'>FTL</option></select>";
		}

		data1="Console Type";
	}
   /* else if(document.forms[0].shipmentMode.value=="Air")
	{

		
		document.getElementById("wtBreak").innerHTML="<select name='weightBreak' class='select' size=1 onchange='showWeightBreaks();chage();showListValues()'><option value='Flat'>Flat</option><option value='Slab'>Slab</option><option value='List'>List</option></select>";
	    
	}*/
	
	if( document.layers)
	{
		document.layers.console.document.write(data);
		document.layers.console.document.close();
		document.layers.consoleLable.document.write(data1);
		document.layers.consoleLable.document.close();
	}
	else
	{
		if(document.all)
		{
			console.innerHTML = data;
			consoleLable.innerHTML = data1;
		}
	}
	
	
}
function changeUOM()
{
	if(document.forms[0].shipmentMode.value=="Sea")
	{
     document.getElementById("UOM").innerHTML='<select name="uom" class="select" size=1><option value="CBM" <%="CBM".equalsIgnoreCase(UOM)?"selected":""%>>CBM</option><option value="CFT" <%="CFT".equalsIgnoreCase(UOM)?"selected":""%>>CFT</option></select>';
	}
	else if(document.forms[0].shipmentMode.value=="Air")
	{
	 
	 document.getElementById("UOM").innerHTML='<select name="uom" class="select" size=1><option value="KG" <%="KG".equalsIgnoreCase(UOM)?"selected":""%>>KG</option><option value="LB" <%="LB".equalsIgnoreCase(UOM)?"selected":""%>>LB</option></select>'; 
	}
	else if(document.forms[0].shipmentMode.value=="Truck")
	{
		document.getElementById("UOM").innerHTML='<select name="uom" class="select" size=1><option value="KG" <%="KG".equalsIgnoreCase(UOM)?"selected":""%>>KG</option><option value="LB" <%="LB".equalsIgnoreCase(UOM)?"selected":""%>>LB</option><option value="CBM" <%="CBM".equalsIgnoreCase(UOM)?"selected":""%>>CBM</option><option value="CFT" <%="CFT".equalsIgnoreCase(UOM)?"selected":""%>>CFT</option></select>';
	}

}
function changeConsole()
{
   
   var console=document.forms[0].consoleType.options[document.forms[0].consoleType.selectedIndex].value;
   if(console=='LCL' || console=='LTL')
	{
document.getElementById("wtBreak").innerHTML="<select name='weightBreak' class='select' size=1 onchange='showWeightBreaks();showConsole();chage()'>						<option value='Flat'>Flat</option><option value='Slab'>Slab</option></select>";
	}
	else if(console=='FCL' || console=='FTL')
    {
	document.getElementById("wtBreak").innerHTML="<select name='weightBreak' class='select' size=1 onchange='showWeightBreaks();chage();'>						<option value='List'>List</option></select>";
	}
}
function pop()
{
		myUrl= 'etrans/ETCCurrencyConversionAddLOV.jsp?searchString='+document.forms[0].baseCurrency.value.toUpperCase();
		
		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
		newWin.opener = self;
		return true;

}
function showCarrierIds()
{
	
	var URL 		= 'etrans/ETransLOVCarrierIds.jsp?searchString='+document.forms[0].carrierId.value.toUpperCase()+'&shipmentMode='+document.forms[0].shipmentMode.value;	
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 360,height = 360,resizable = yes';
	var Features 	= Bars +' '+ Options;

	var Win 		= open(URL,'Doc',Features);

}
</script>



	<script language='JavaScript'>


function hidediv() 
{ 
		if(document.getElementById('weightBreak').value=="Slab")
			document.getElementById('hideShow').style.visibility = 'visible'; 
		else
			document.getElementById('hideShow').style.visibility = 'hidden'; 


} 


function  showIds(row, obj)
{
	var name	= obj.name;
	
	var contractType = document.forms[0].contractType.value;
	var customerId = document.forms[0].customerId.value;
	var shipmentMode = document.forms[0].shipmentMode.value;
	var filterId = '';
	var laneType   = '';
	var searchStr = '';
	var corporate	= document.forms[0].corporate.value;

	if(obj.name == 'PLOV')
	{
		searchStr = customerId;
	}

	var url		 = 'etrans/ETCustomerContractLOV.jsp?contractType='+contractType+'&lovName='+name+'&customerId='+customerId+'&row='+row+'&laneType='+laneType+'&filterId='+filterId+'&searchStr='+searchStr+'&shipmentMode='+shipmentMode+'&corporate='+corporate;
	var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
	var Options  = 'scrollbars=yes,width=525,height=340,resizable=no';
	var Features = Bars+''+Options; 
	var Win      = open(url,'Doc',Features);
	  
	if (!Win.opener) 
		Win.opener = self;

	if (Win.focus != null) 
		Win.focus();
}

function upper(obj)
{ 
  obj.value = obj.value.toUpperCase();
}
function specialCharFilter()
{
	if(event.keyCode == 33 || event.keyCode == 34 || event.keyCode == 39 || 
		event.keyCode == 59 || event.keyCode == 96 || event.keyCode == 126)
		return false;
	return true;
}

function chage()
{
	
 
 var selectedIndex=document.forms[0].weightBreak.selectedIndex;
 var selectedvalue=document.forms[0].weightBreak.options[selectedIndex].value;
  if(selectedvalue=='Flat')
 {
  document.getElementById("rateType").innerHTML="<select name='rateType' class='select' size=1><option value='Flat'>Flat</option></select>";
 }
 else if(selectedvalue=='Slab') 
 {
  document.getElementById("rateType").innerHTML=
  "<select name='rateType' class='select' size=1><option value='Slab'>Slab</option><option value='Flat'>Flat</option><option value='Both'>Both</option></select>";
 }
 else
 {
	if(document.forms[0].shipmentMode.value=="Air" )
		  document.getElementById("rateType").innerHTML="<select name='rateType' class='select' size=1><option value='Pivot'>Pivot</op tion></select>";
	else
		  document.getElementById("rateType").innerHTML="<select name='rateType' class='select' size=1><option value='Flat'>Flat</op tion></select>";

     showConsole();

  showListLov();
 }
}

function   showListLov()
{
       var list  =  document.forms[0].listValue;

	   var temp="";
   if(list)
	{
	   for(m=0;m<list.length;m++)
	   {
		   if(list[m].value!='overPivot')
      	     temp=list[m].value+","+temp;
	   }
	}     
		myUrl= 'etrans/ListMasterMultipleLOV.jsp?listValue='+temp+'&shipmentMode='+document.forms[0].shipmentMode.value;
		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
				newWin.opener = self;
			return true;
}

function tempChar()
{
	var1= document.getElementsByName("wtbrak")
	if(var1!=null)
	{
		
		for(i=0;i<var1.length;i++)
		{
			if(var1[0].value.length==0 || var1[1].value.length==0 )
			{
				alert(" 1'st and 2'nd value should have values      ")
					return false;
			}
			if(i==0)
			{
				if(parseDouble(var1[0].value)+parseDouble(var1[1].value)!=0)
				{
					alert(" Weight breaks 1 should be with -value and 2'nd should be with equlal + value")
						return false;
				}
				i++;
			}else{
				if(var1[i-1].value!='' && var1[i].value!='' && parseDouble(var1[i-1].value)>=parseDouble(var1[i].value))
				{
					alert("  Value should be greater than previous value    ")
						return false;
				}

			}
		}
	}
  
}
function checkMandatory()
{
	if(!chkFutureDate(document.forms[0].effectiveFrom,"gt"))
	{
		alert('Please Enter Effective From Date greater than or equal to Current Date');
		document.forms[0].effectiveFrom.focus();
		return false;
	}

	if(document.forms[0].effectiveTo.value.length!=0){//added by rk for 13065
		if(!chkFromToDate(document.forms[0].effectiveFrom.value, document.forms[0].effectiveTo.value))
		{
			alert("Please Enter  Valid Upto Date should be Greater than Effective Date.");
			document.forms[0].effectiveTo.focus();
			return false;
		}
	}
	if(document.forms[0].weightBreak.options[document.forms[0].weightBreak.selectedIndex].value=="Slab")
	{
			return tempChar();
	}
	if(document.forms[0].weightBreak.options[document.forms[0].weightBreak.selectedIndex].value=="List")
	{
		if(!selectallValues())
		{	return false;}
	}
   document.forms[0].action= "BuyRatesController";
	//document.forms[0].submit();

}
function showListValues(obj,obj1)
{

	var data="";


	if(document.forms[0].weightBreak.value =="List")                                      
	{
		data="<table border='0' width='950' cellspacing='1' cellpadding='4' ><tr><td>";
	  if(obj1)                                                                              
	  {                                                                                     
		data="<select name='listValue' size=10 multiple class='select'>";
		for( i=0;i<obj.length;i++)
		{
			  
			  data=data+"<option value='"+obj[i]+"'>"+obj[i]+"</option>";
		}                     
																							
	 }else{                                                                                	
		
		
		
		data="<select name='listValue' size=10 multiple  class='select'>";
		if(obj!=null)
		for( i=0;i<obj.length;i++)
		{
			  
			  data=data+"<option value='"+obj[i].value+"'>"+obj[i].value+"</option>";
		}
	 }
	  if(document.forms[0].shipmentMode.value=="Air")
		{
		  data=data+"<option value='overPivot'></option>"
		}
	   data=data+"</select><input type='button' name='listLOV' onClick= 'showListLov()' value='...'class='input'></td></tr></table>";

	}
	   document.getElementById("listValues").innerHTML=data;
}
function selectallValues()
 {

	
        obj = document.forms[0].listValue;
		var temp  ;
		if("Air" == document.forms[0].shipmentMode.value)
			temp =1;
		else
			temp=0;
		//alert(obj.options.length);
		if(obj==null)
		{
			if("Air" == document.forms[0].shipmentMode.value)
				alert("Please select atleast one ULD Type");
			else
				alert("Please select atleast one Container Type");
			return false;
		}
		else if( obj.options.length==temp)
		{
			if("Air" == document.forms[0].shipmentMode.value)
				alert("Please select atleast one ULD Type");
			else
				alert("Please select atleast one Container Type");
			return false;
		}
		for (var i=0; i<obj.options.length; i++) 
		{
			obj.options[i].selected = true;
		}return true;

 }

function showValues()
{
	var var1		= <%=(shipmentMode.equals("4")?"3":shipmentMode)%>-1;
	var weightBreak = '<%=weightBreak%>'
	var	rateType	= '<%=rateType%>';
	var size		= '<%=size%>';
	

	document.forms[0].shipmentMode.options[var1].selected=true;
	if(var1==1)
	{
		showConsole();
		changeUOM()
	}
	if(weightBreak=='Slab')
	{
		if(weightBreak	== 'Slab')
		{
		document.forms[0].weightBreak.options[1].selected=true;
		}
		else if(weightBreak	== 'Flat'){
		document.forms[0].weightBreak.options[0].selected=true;
		}
		else{
		document.forms[0].weightBreak.options[2].selected=true;
		}
		showWeightBreaks();
		chage();
		if(rateType	== 'Slab')
		{
		document.forms[0].rateType.options[0].selected=true;
		}
		else if(rateType	== 'Flat'){
		document.forms[0].rateType.options[1].selected=true;
		}
		else{
		document.forms[0].rateType.options[2].selected=true;
		}

	}
	if(weightBreak=='List')
	{
		document.forms[0].weightBreak.options[2].selected=true;

		//chage();
		var listSize;
		if(document.forms[0].shipmentMode.value=="Truck" || document.forms[0].shipmentMode.value=="Sea")
		{
			listSize	=	<%=size%>;
			 document.getElementById("rateType").innerHTML="<select name='rateType' class='select' size=1><option value='Flat'>Flat</op tion></select>";
		}
		else
		{
			listSize	=	<%=size-1%>;
			document.getElementById("rateType").innerHTML="<select name='rateType' class='select' size=1><option value='Pivot'>Pivot</op tion></select>";
		}
		var list  = new Array(listSize);
		if(document.forms[0].shipmentMode.value=="Truck" || document.forms[0].shipmentMode.value=="Sea")
		{
<%			for (int i=0;i<size;i++)
			{
%>

				list [<%=i%>]	=	'<%=listValues[i]%>';	

<%			}
%>
		}
		else
		{
<%			for (int i=0;i<size-1;i++)
			{
%>

				list [<%=i%>]	=	'<%=listValues[i]%>';	

<%			}
%>
		}

	showListValues(list,true);
	}

}
//Added by govind for the CR-219973
function addSurchargeRow(obj,trid)//function to add new row dynamically obj=current element obj,trid rowid
{   
	var tableid  = document.forms[0].tableide.value;
	var tableobj =  document.getElementById(tableid);
	var rowobj   =	tableobj.insertRow(trid.sectionRowIndex+1);
    var newtrid  =  Number(document.forms[0].maxsurchargetrid.value)+1;
	document.forms[0].maxsurchargetrid.value=newtrid;
	rowobj.setAttribute("className","formdata");
	rowobj.setAttribute("id","tr"+newtrid,0);
	var cellobj  =	rowobj.insertCell(0);
	cellobj.setAttribute("colspan","5");
	cellobj.setAttribute("align","center");
	cellobj.innerHTML = "<input type='button' class='input'  id ='RS-"+(newtrid)+"'  name='RemoveSurcharge+"+(newtrid)+"' value='<<' onclick='deleteSurchargeRow(tr"+newtrid+")' >&nbsp;&nbsp;"+
	    "&nbsp;&nbsp;&nbsp;&nbsp;"+
		"<input type='text' class='text' name='surchargeIds' id = 'sr"+newtrid+"' size='30' maxlength=100 value='' onBlur='upper(this)'>&nbsp;"+
		"<input type='text' class='text' name='surchargeDesc' id = 'srd"+newtrid+"' size='30' maxlength=100 value='' onBlur='upper(this)'>&nbsp;"+
		"<input type='text' class='text' name='surchargeWeightBreaks' id = 'srw"+newtrid+"' size='30' maxlength=100 value='' onBlur='upper(this)'>&nbsp;"+
    	"<input type='button' class='input'  name='SurchargeLOV' value='...' onclick='openSurchargeLov(tr"+newtrid+")'>"+
        "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
		"<input type='button' class='input' id ='AS-"+(newtrid)+"' name='AddSurcharge"+(newtrid)+"' value='>>' onclick='addSurchargeRow(this,tr"+newtrid+")')'>";
}
//Added by govind for the CR-219973
function deleteSurchargeRow(obj)
{
    var tableid  = document.forms[0].tableide.value;
	var tableobj =  document.getElementById(tableid);
	var rownum    =   obj.sectionRowIndex;
//var ele = document.getElementById(obj).getElementsByClassName("input");
	//	alert(obj.firstChild.nodeName)
/*
    var rownum   = rowid.substring(rowid.indexOf('-')+1,rowid.length);*/
	
	 if(Number(tableobj.rows.length)== 1)
		 alert("U can't Delete this Row")
		 else
	 tableobj.deleteRow(rownum);
	
	
   

}

//Added by govind for the CR-219973
function openSurchargeLov(trid)
{
	//Added By Kishore Podili For LOV issue
	var mode = document.forms[0].shipmentMode.value;
	var shipmentMode;
	if(mode ==  "Air")
		shipmentMode = 1
	else if (mode ==  "Sea")
		shipmentMode = 2
	else 
		shipmentMode = 4

	var rate_break = document.getElementById("weightBreak").value;
	if(rate_break == 'List')
		rate_break = '=\'List\''
	
	else 
		rate_break = '!=\'List\''

	//alert(document.getElementById("weightBreak").value);
	//alert("openSurchargeLov"+ document.getElementById( 'sr'+rownum).value());
	//alert(mode+"		"+shipmentMode)
	var rownum = (trid.id).substring(2);
	var tabArray = 'SURCHARGE_ID,SURCHARGE_DESC,WEIGHT_BREAKS';
	var formArray = 'sr'+rownum+',srd'+rownum+',srw'+rownum;
	var tempId =  'sr'+rownum
	var surChargeId = document.getElementById(tempId).value;
	//alert(surChargeId)
		if(surChargeId != null && surChargeId != ""){
      var  cond = "    and  SURCHARGE_ID  Like  \'"+surChargeId+'~\'';
         rate_break   = rate_break+ cond;
	}
	else{
             var  cond = "    and  SURCHARGE_ID  Like \'"+surChargeId+'~\'';
			          rate_break   = rate_break+ cond;
	}
	var lovWhere	=	"";
	//@@ Modified by subrahmanyam for the pbn id: 210495 on 12-Jul-10
	Url		="qms/ListOfValues.jsp?lovid=SURCHARGE_LOV&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.value+"&operation=SURCHARGE&search= where shipment_Mode= "+shipmentMode+  " and rate_break " +  rate_break +" order by SURCHARGE_ID asc"; 
        //End Of  By Kishore Podili For LOV issue
	Bars	='directories=0,location=0,menubar=no,status=no,titlebar=0,scrollbars=1';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
 }


 

</script>
</head>
  <body onLoad="showWtBreaks();chage();showWeightBreaks();<%if(flag){%>showValues();<%}%>;changeUOM();showConsole()">
	<form   onSubmit='return checkMandatory();'><!--validateForm()-->

	<input type = 'hidden' name='subOperation' value='Add'>
	<input type = 'hidden' name='Operation' value='Add'>
	<input type = 'hidden' name='rows' value='1'>
	<input type = 'hidden' name='surchargeIds' value="" >
	<input type = 'hidden' name='surchargeDesc' >
	<input type = 'hidden' name='surchargeWeightBreaks' >
	<input type = 'hidden' name='maxsurchargetrid' value='1'>
    <input type = 'hidden' name='tableide' value=''>
	 <table width="990" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		<td>
			<table width="990" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="790" border="0" ><tr class='formlabel'><td>Buy Rates - Add </td><td align=right>&nbsp;</td><td align=right>QS1060111</td></tr></table></td>
			  </tr>
			  </table>
<%
if(request.getAttribute("detailRateDob")!=null)
{
	//System.out.println("in ratedob validateiasdf"+(String)request.getAttribute("detailRateDob"));
%>
<table width="990"  cellspacing="1" cellpadding="4">
 <tr valign="top" class='formdata'>
  <td>
       <font  color=#ff0000><%=(String)request.getAttribute("detailRateDob")%></font>
  </td>
</tr>
</table>
<%}
%>
			  
        <table width="990"  cellspacing="1" cellpadding="4">
          <tr valign="top" class='formdata'>

			    <td colspan=5>&nbsp;
				</td>
				
			  </tr>
			 
			  <tr valign="top" class='formdata'>
			    
            <td width="174" >Shipment Mode:<br>
					<select name='shipmentMode' class='select' size=1 onchange="showWtBreaks();showConsole();changeUOM();chage();showListValues()">
						<option value='Air'>Air</option>
						<option value='Sea'>Sea</option>
						<option value='Truck'>Truck</option>
					</select>
				</td>
				
            <td width="229">Effective From (<%=loginbean.getUserPreferences().getDateFormat()%>):<font color=#ff0000>*</font><br>
				<input type='text' class='text' name='effectiveFrom' value="<%=effectiveFrom !=null ? effectiveFrom:""	%>" size='10'    maxlength=10 onBlur='dtCheck(this)'>
				<input type='button' class='input'  name='b1' value='...' onClick="newWindow('effectiveFrom','0','0','')">
			</td>
				
            <td width="209"> Effective To (<%=loginbean.getUserPreferences().getDateFormat()%>):<br>
				  <input type='text' class='text' name='effectiveTo' size=10 maxlength=10 onBlur='dtCheck(this)' value="<%=effectiveTo !=null ? effectiveTo:""	%>">
				  <input type='button' class='input'  name='b1' value='...' onClick="newWindow('effectiveTo','0','0','')">
			</td>
				
            <td width="169" >Currency:<font color=#ff0000>*</font><br>
			        <input type='text' class='text' name='baseCurrency' size='16' maxlength=10 value="<%=currency !=null ? currency:""	%>" onBlur='upper(this)' onKeyPress='return specialCharFilter()' >
					<input type='button' class='input'  name='b1' value='...' onclick="pop();">

			</td>
		  </tr>
		  <tr valign="top" class='formdata'>
			    
            <td width="174">Carrier Id:<font  color=#ff0000>*</FONT><br>
              <input type='text' class='text' name='carrierId' size='16' maxlength=10 value="<%=carrier !=null ? carrier:""	%>" onBlur='upper(this)' onKeyPress='return specialCharFilter()' >
		      <input type='button' class='input'  name='b1' value='...' onclick="showCarrierIds();">
			</td>
            <td width="229">UOM:<span id='consoleLable' style='position:relative;'></span> <br>
				<span id="UOM"></span>
				<!-- <select name='uom' class='select' size=1>
						<option value='KG'>KG</option>
						<option value='LB'>LB</option>
				</select> -->

				 <span id='console' style='position:relative;'></span> 

			</td>
            <td width="209"> Weight Break:<font  color=#ff0000>*</FONT><br>
				
				<span id="wtBreak"></span>
				<!-- <select name='weightBreak' class='select' size=1 onchange="showWeightBreaks();chage();">
						<option value='Flat'>Flat</option>
						<option value='Slab'>Slab</option>
						<option value='List'>List</option>
					</select> -->
			</td>
            <td width="169" >
				Rate Type:<font  color=#ff0000>*</FONT><br><span id="rateType"></span>
				</td>
			  </tr>
			</table>
			<table border="0" width="990"   cellspacing="1" cellpadding="4" ><tr class='formdata'><td>
	<span id='cust' style='position:relative;'></span><!--</td> </tr></table>  Govind
	<table border="0" width="800"   cellspacing="1" cellpadding="4" ><tr class='formdata'> <td>-->
	<span id='listValues' style='position:relative;'></span> </td></tr></table>

		 <!-- Govind- Start -->	 
		 <table border="0" width="990"   cellspacing="1" cellpadding="4">
		<tr  valign="top" class='formheader'>
		<td colspan ='5'>
		SurCharge :<font color="#FF0000">*</font></td>
		</tr></table>
		
		
		<%if((surchargeIds != null)&&(surchargeIds.length>1 && surchargeWeightBreaks.length>1 && surchargeDesc.length>1)){%>
<table border="0" width="990"   cellspacing="1" cellpadding="4" id='surcharge1'>
<%			  for(int sr=1;sr<surchargeIds.length;sr++){%>
        <tr  valign='top' id ='tr<%=sr%>' class='formdata'>
		  <td colspan = '5' align ='center'>
		 <input type='button' class='input' id ='RS-<%=sr%>'  name='RemoveSurcharge0' value='<<' onclick="deleteSurchargeRow(tr<%=sr%>);" >
		 &nbsp;&nbsp;&nbsp;
		<input type='text' class='text' name='surchargeIds' id ='sr<%=sr%>' size='30' maxlength=100 value='<%=surchargeIds[sr]%>' onBlur='upper(this)'  >
		<input type='text' class='text' name='surchargeDesc' id ='srd<%=sr%>' size='30' maxlength=100 value='<%=surchargeDesc[sr]%>' onBlur='upper(this)'  >
		<input type='text' class='text' name='surchargeWeightBreaks' id ='srw<%=sr%>' size='30' maxlength=100 value='<%=surchargeWeightBreaks[sr]%>' onBlur='upper(this)'  >
		 <input type='button' class='input'  name='Surcharge' value='...' onclick="openSurchargeLov(tr<%=sr%>)">
        &nbsp;&nbsp;&nbsp;&nbsp;
		 <input type='button' class='input' id='AS-<%=sr%>'  name='AddSurcharge0' value='>>' onclick="addSurchargeRow(this,tr<%=sr%>)"  > <!-- kish -->
		</td>
		<script>document.forms[0].maxsurchargetrid.value=<%=sr%>;
		         document.forms[0].tableide.value='surcharge1' </script>
		</tr>
  		<%}%>
		</table>
		 <%}else{%>
   <table border="0" width="990"   cellspacing="1" cellpadding="4" id='surcharge2'>
		<tr  valign='top' id ='tr1' class='formdata'>
		 <td colspan = '5' align ='center'>
		 <input type='button' class='input' id ='RS-0'  name='RemoveSurcharge0' value='<<' onclick="deleteSurchargeRow(tr1);" >
		 &nbsp;&nbsp;&nbsp;
		<input type='text' class='text' name='surchargeIds' id ='sr1'size='30' maxlength=100 value="" onBlur='upper(this)'  >
		<input type='text' class='text' name='surchargeDesc' id ='srd1'size='30' maxlength=100 value="" onBlur='upper(this)'  >
		<input type='text' class='text' name='surchargeWeightBreaks' id ='srw1'size='30' maxlength=100 value="" onBlur='upper(this)'  >
		 <input type='button' class='input'  name='Surcharge' value='...' onclick="openSurchargeLov(tr1)">
        &nbsp;&nbsp;&nbsp;&nbsp;
		 <input type='button' class='input' id='AS-0'  name='AddSurcharge0' value='>>' onclick=" addSurchargeRow(this,tr1)">
		</td><!-- Govind- End -->
		 </tr>
		 </table>
		 <script>document.forms[0].tableide.value='surcharge2' </script>
		<%}%>

	 <table border="0" width="990"   cellspacing="1" cellpadding="4">
	    <tr class='denotes'>
		  <td valign=top >
		    <font color="#FF0000">*</font>Denotes Mandatory
		  </td>
		  <td align="right" valign = top >
		    <input name=submit type='submit' value="Next >>" class='input'>
			<input name=Reset type=reset value=Reset class='input'>
	  </td>
		</tr>
	  </table>
	 </td>
		</tr>
		
	  </table>
	</form>
  </body>
</html>
