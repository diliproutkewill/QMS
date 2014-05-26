<%@ page import="java.text.NumberFormat,
				java.util.*,
				java.util.Calendar,
				java.text.DateFormat,
				javax.naming.InitialContext,
				org.apache.log4j.Logger"%>
<jsp:useBean id="loginbean" 
			 class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" 
			 scope="session"/>					 
<html>
<head>
<title>Pack Calculations</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<%!
  private static Logger logger = null;
%>

<%
  
	String FILE_NAME 	= "QMSCostingPackCalLOV.jsp";
	logger  = Logger.getLogger(FILE_NAME);	
	
  try
  {
    
	if(true)
	{

%>
<body >
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">


	 k1 = 0
 	 k2 = 0
	 k3 = 0
	 k4 = 0
	 k5 = 0
	 k6 = 0
	 k7 = 0
	 k8 = 0
	 
	boxes           = new Array(25);
	boxType         = new Array(25);
	boxLength       = new Array(25);
	boxBreadth	    = new Array(25);
	boxHeight       = new Array(25);
	boxWeight       = new Array(25);
	boxVolume       = new Array(25);
	for(i=0;i<boxes.length;i++)
	{
			boxes[i]='';
			boxType[i]='';
			boxLength[i]='';
			boxBreadth[i]='';
			boxHeight[i]='';
			boxWeight[i]='';
			boxVolume[i]='';
	}
var number11=1

<%

%>
		


function mandatory(obj,mes)
{
	if(obj.value.length==0 || obj.value==0 || obj.value=='0' || obj.value=='0.0' || obj.value=='0.00')
	{
		alert('Please Enter '+mes);
		obj.focus();
		return false;
	}
	return true;
}
//rounds a value after decimal period by checking its value 
	function round (n)
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
// to floor a value and then round the value using the prior function roundweight
function fn(input)
{
		var value8= input.value;
		result8 =Math.floor(value8)+".";
		var cents8=100*(value8-Math.floor(value8))+0.5;
	    result8 += Math.floor(cents8/10);
        result8 += Math.floor(cents8%10);
		round(result8);
		input.value=result8;
	   
		return input.value;
}
function allCalculations(input)
{
	boxesCalculations(input);
	Wtcalculations();
	Volcalculations();
	

	
}
// prevents from accepting anything other than a single dot and 
// numbers  [accepts keycodes(ascii values) from 48(0) to 57(9)  ] 

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
	}
	return true;
}

function checkForm()
{ 
	var totalPieces 	= document.forms[0].totalBooked;
    var actualWeight    = document.forms[0].actualWeight;
	var totalVolume     = document.forms[0].totalVolume;
    
	
	
	if(totalPieces.value.length==0)
    {
	  alert("Please enter Pieces");
	  totalPieces.focus();
	  return false;
	}
    if(actualWeight.value.length==0)
    {
	  alert("Please enter actualWeight");
	  actualWeight.focus();
	  return false;
	}
	if(totalVolume.value.length==0)
    {
	  alert("Please enter totalVolume");
	  totalVolume.focus();
	  return false;
	}
    if(number11<25)
	{
		if(number11==1 && ( document.forms[0].boxes.value.length==0) )
		{

			alert("Please fill this row and then go to submit button")
			document.forms[0].boxes.focus()
		    return false;
		}
		else if(number11 > 1 && (document.forms[0].boxes[number11-1].value.length==0))
		{
			alert("First fill this row and then go to the submit button")
			document.forms[0].boxes[number11-1].focus()
            return false;
		}    
		
	}
	
	setValues();  
   
}


function Start(page) 
{
	var OpenWin = this.open(page, "CtrlWindow", "toolbar=no,menubar=no,location=no,scrollbars=yes,width=360,height=360,resizable=no");
	return true;
}




function changeToUpper(field)
{
	field.value = field.value.toUpperCase();
}
function Volcalculations()
{
	total			= 0;
	totalVolume		= 0;
	
    var weightScale	=	1;	
	var measurement =   document.forms[0].measurement.value;	
	var totVolume   =   document.forms[0].totVolume.value;	
	
	if(number11==1)
	{
	   	if((document.forms[0].boxes.value).length!=0)
    	{
    		total=parseInt( document.forms[0].boxes.value)
     	}
     	if((document.forms[0].boxes.value).length!=0 &&(document.forms[0].boxLength.value).length!=0 && (document.forms[0].boxBreadth.value).length!=0 && (document.forms[0].boxHeight.value).length!=0 )
    	{
    		Vol=0;
			Vol=((parseFloat( document.forms[0].boxLength.value)*parseFloat( document.forms[0].boxBreadth.value)*parseFloat( document.forms[0].boxHeight.value)*parseFloat( document.forms[0].boxes.value)))/weightScale;
       		
			totalVolume+=Vol;
        	document.forms[0].boxVolume.value 	=	Vol;
			document.forms[0].totalVolume.value =	totalVolume;
			

			
     	}
	}
	else
	{
	  if(number11 < 26)
	  {	
		for(i=0;i<number11;i++)
		{
	       	if((document.forms[0].boxes[i].value).length!=0)
    		{
    			 total+=parseInt( document.forms[0].boxes[i].value)
     		}
     		if((document.forms[0].boxes[i].value).length!=0 &&(document.forms[0].boxLength[i].value).length!=0 && (document.forms[0].boxBreadth[i].value).length!=0 && (document.forms[0].boxHeight[i].value).length!=0 )
    		{
    			Vol=0;
     			Vol =((parseFloat( document.forms[0].boxLength[i].value)*parseFloat( document.forms[0].boxBreadth[i].value)*parseFloat( document.forms[0].boxHeight[i].value)*parseFloat( document.forms[0].boxes[i].value)))/weightScale;
       			
       			totalVolume+=Vol;
				document.forms[0].boxVolume[i].value =Vol;
		        document.forms[0].totalVolume.value =totalVolume;
										
			}
        }
	  }
	}
    
	var conversion=document.forms[0].totalVolume.value;
    
	
	if(measurement=='CM' && totVolume=='CBM')
    document.forms[0].totalVolume.value=conversion/(100*100*100);
    else if(measurement=='CM' && totVolume=='CFT')
    document.forms[0].totalVolume.value=conversion/(30.48*30.48*30.48);
    else if(measurement=='IN' && totVolume=='CBM')
    document.forms[0].totalVolume.value=conversion/(39.36993*39.36993*39.36993);
    else if(measurement=='IN' && totVolume=='CFT')
    document.forms[0].totalVolume.value=conversion/(12*12*12);

	round(document.forms[0].totalVolume.value);

	document.forms[0].totalVolume.value	=	round(document.forms[0].totalVolume.value);
    


}

/*function round (n)
{
	n = n - 0; // force number
	d = 2;
	var f = Math.pow(10, d);
	n += Math.pow(10, - (d + 1)); // round first
	n = Math.round(n * f) / f;
	n += Math.pow(10, - (d + 1)); // and again
	n += ''; // force string
	return d == 0 ? n.substring(0, n.indexOf('.')) :
	n.substring(0, n.indexOf('.') + d + 1);
}*/

function Wtcalculations()
{
	TotalWeight=0;
		    
	if(number11==1)
	{
		if(document.forms[0].boxWeight.value.length != 0 )
		{
			Wt = 0;
			Wt = parseFloat(document.forms[0].boxWeight.value);
			document.forms[0].actualWeight.value = Wt;
			
		}
	}
	else
	{
		if(number11<26)
		{	Wt=0;
			for(i=0;i<number11;i++)
			{
				if(document.forms[0].boxWeight[i].value.length!=0 ) 
				{
				    Wt = Wt+parseFloat(document.forms[0].boxWeight[i].value);
					document.forms[0].actualWeight.value = Wt;
					
				}
			}
		}
	}
    var conversions=document.forms[0].ActWeigh.value;
	var claValue   =document.forms[0].actualWeight.value;
	if(conversions=='KG')
    document.forms[0].actualWeight.value=claValue;
	else if(conversions=='LB')
    document.forms[0].actualWeight.value=claValue*2.2406;

    round(document.forms[0].actualWeight.value); 
}	  

function boxesCalculations(obj)
{

var TotalReceived=0;

	if(number11==1)
	{
	      	
     	if(document.forms[0].boxes.value.length != 0)
    	{
    		box=0;
			box= parseFloat(document.forms[0].boxes.value);
       		TotalReceived += box;
        	document.forms[0].totalBooked.value = TotalReceived;

     	}
	}
	else
	{
		if(number11<26)
		{	
			for(i=0;i<number11;i++)
			{

				if((document.forms[0].boxes[i].value).length!=0 ) 
				{
					box=0;
					box = parseFloat( document.forms[0].boxes[i].value);
					TotalReceived+=box;
					document.forms[0].totalBooked.value = TotalReceived;
				}

			}
		}
	}
		

}	  

 

function test1()
{
	if(number11<25)
	{
		if(number11==1 && ( document.forms[0].boxes.value.length==0) )
		{

			alert("Please fill this row and then go to the next row")
			document.forms[0].boxes.focus()
		}
		else if(number11 > 1 && (document.forms[0].boxes[number11-1].value.length==0))
		{
			alert("First fill this row and then go to the next row")
			document.forms[0].boxes[number11-1].focus()

		}    
		else
			createAirForm(++number11,-1)
	}
	else
		window.alert("Only up to 25 entries.");		

}


function createAirForm(numb,row)
{
		if(numb<=0)numb=1;
		var number = parseInt(numb)
		var k1=0
		var k2=0
		var k3=0
		var k4=0
		var k5=0
		var k6=0
		var k7=0

		for(j=0; j < document.forms[0].elements.length; j++)
		{
			if(document.forms[0].elements[j].name == "boxes")
			{
				boxes[k1] = document.forms[0].elements[j].value
				k1++
			}
			if(document.forms[0].elements[j].name == "boxType")
			{
				boxType[k3] = document.forms[0].elements[j].value
				k3++
			}
			if(document.forms[0].elements[j].name == "boxLength")
			{
				boxLength[k2] = document.forms[0].elements[j].value
				k2++
			}
			if(document.forms[0].elements[j].name == "boxBreadth")
			{
				boxBreadth[k4] = document.forms[0].elements[j].value
				k4++
			}
			if(document.forms[0].elements[j].name == "boxHeight")
			{
				boxHeight[k5] = document.forms[0].elements[j].value
				k5++
			}
			if(document.forms[0].elements[j].name == "boxWeight")
			{
				boxWeight[k6] = document.forms[0].elements[j].value
				k6++
			}
			if(document.forms[0].elements[j].name == "boxVolume")
			{
				boxVolume[k7] = document.forms[0].elements[j].value
				k7++
			}
		}
		if(row !=-1)
		{
			for(var j=0; j < boxes.length -1; j++)
			{
				if( j  >= row)
				{
					boxes[j] = boxes[j+1];
					boxType[j] = boxType[j+1];
					boxLength[j] = boxLength[j+1];
					boxBreadth[j] = boxBreadth[j+1];
					boxHeight[j] = boxHeight[j+1];
					boxWeight[j] = boxWeight[j+1];
					boxVolume[j] = boxVolume[j+1];
				}
			}
		}

		 
		data = "";
		inter = "'";
		
		if (number < 26 && number > -1)
		{
			data +="<table width=830 bgcolor=FFFFFF  cellpadding=4 cellspacing=1 >"
         
		    for (i=0; i < number; i++)
			{
				     data = data + "<tr align=center class='formdata'>"
					if(i==0)
					 {
						 data = data + "<td width=70></td>"
					 }else
					{
						 data = data + "<td width=70><input type='Button' class='input'  value='<<' onClick='removeRow22("+i+");' ></td>"
					}
					 data= data + "<td width=75><input type='text' maxlength=5 size=4 name='boxes' value=" + inter + boxes[i]  + inter + " onBlur = 'allCalculations(this)' onKeyPress = 'return checkNumber(this)' class='text'></td>" 

					  + "<td width=75><input type='text' maxlength=10 size=4 name='boxType' value=" + inter + boxType[i]  + inter + " onBlur='changeToUpper(this)' class='text'></td>"
					  + "<td width=75><input type='text' maxlength=10 size=4 name='boxLength'  value=" + inter + boxLength[i]  + inter + " onBlur='this.value=round(this.value);allCalculations(this)' onKeyPress = 'return getDotNumberCode(this)' class='text'></td>"
					  + "<td width=75><input type='text' maxlength=10 size=4 name='boxBreadth' value=" + inter + boxBreadth[i]  + inter + " onBlur='this.value=round(this.value);allCalculations(this)' onKeyPress = 'return getDotNumberCode(this)' class='text'></td>"
					  + "<td width=75><input type='text' maxlength=10 size=4 name='boxHeight' value=" + inter + boxHeight[i]  + inter + " onBlur='this.value=round(this.value);allCalculations(this)' onKeyPress = 'return getDotNumberCode(this)' class='text'></td>"
			
					  + "<td width=75><input type='text' maxlength=10 size=4 name='boxWeight' value=" + inter + boxWeight[i]  + inter + " onBlur='this.value=round(this.value);allCalculations(this)' onKeyPress = 'return getDotNumberCode(this)' class='text'></td>"
									+ "<td width=75><input type='hidden' maxlength=10 size=4 name='boxVolume' value='0'></td>"					 
							
					
					 
  				    if(i != (number-1))
                      data=data+"<th></th>"
		
		          
  				  
					if(i == (number-1))
					{
						data = "" + data + "<td width=70> <input type='Button' class='input'  value='>>' onClick='test1()'></td>"
					}
						data = "" + data + "</div></td></tr>"
			}
            data = "" + data +"</table>"
			
			if (document.layers)
		    {
				document.layers.cust.document.write(data);
				document.layers.cust.document.close();
			}
			else
			{
				if (document.all)
				{
					cust.innerHTML = data;
				}
			}
		}
		else
		{
			window.alert("Only up to 25 entries.");
		}
        if(numb>1 && numb<26)	    
	    {
	   		document.forms[0].boxes[numb-1].focus();
		}
}
function removeRow22(row)
{
    number11-- 
    field = parseInt(row); 
    createAirForm(number11,field)
	boxesCalculations();
	Volcalculations();
	Wtcalculations();
	

	
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

		if((event.keyCode < 46 || event.keyCode==47 || event.keyCode >57))
			return false;
	}
	return true;
}


function changeToUpper(field)
{
	field.value = field.value.toUpperCase();
}




function checkNumber(obj)
{
	var i = event.keyCode;  
	var condition;
	if(obj.name == 'boxes')
		condition = (i>47)&&(i<58);
	else
		condition = (i>47)&&(i<58) || i == 46;
	if(!condition)
		event.keyCode = 0;
}
function getKeyCode()
{
	if(event.keyCode!==13)
	{
		if ((event.keyCode > 31 && event.keyCode < 48)||(event.keyCode > 90 && event.keyCode < 97)||
		(event.keyCode > 122 && event.keyCode <127||(event.keyCode > 57 && event.keyCode < 65)))
			event.returnValue =false;
	}
	return true;
}


function focus1()
{
	if(number11<=1)
	document.forms[0].boxes.focus();
	else
	document.forms[0].boxes[0].focus();

}
function initialize()
{

document.forms[0].Submit.disabled=false;
createAirForm(parseInt(number11),-1);
focus1();
}
function roundNew(obj,X)
{
	var number = obj.value;
	
	X = (!X ? 2 : X);
	obj.value=Math.round(Math.round(number*Math.pow(10,X))/Math.pow(10,X));
}
function setValues()
{
    
	window.opener.document.forms[0].noofpieces.value 	 = document.forms[0].totalBooked.value;
    window.opener.document.forms[0].actualweight.value   = document.forms[0].actualWeight.value;
    window.opener.document.forms[0].volume.value         = document.forms[0].totalVolume.value;
	
	window.opener.document.forms[0].uom.value			 = document.forms[0].ActWeigh.value;
	
	window.opener.document.forms[0].volumeUom.value	 = document.forms[0].totVolume.value;
    
	
	window.close();
    
}
function changeMeasures()
{
  
  var dLength=document.getElementsByName("boxLength");
  var dBreadth=document.getElementsByName("boxBreadth");
  var dHeight=document.getElementsByName("boxHeight");
 
  
  if(document.forms[0].measurement.value=='CM')
  { 
	if(dLength.length>1)
	 {
	  for(m=0;m<dLength.length;m++)
	  {
	    if(dLength[m].value.length>0)
	    {
		  dLength[m].value=round(parseFloat(dLength[m].value)*2.54);
		  //round(dLength[m]);
		  dBreadth[m].value=round(parseFloat(dBreadth[m].value)*2.54);
		  //round(dBreadth[m]);
		  dHeight[m].value=round(parseFloat(dHeight[m].value)*2.54);
		  //round(dHeight[m]);
	     }
	  }
	}
    else
	{
	     if(document.forms[0].boxLength.value.length>0 && document.forms[0].boxBreadth.value.length>0 && document.forms[0].boxHeight.value.length>0)
		 {
			 document.forms[0].boxLength.value=round(parseFloat(document.forms[0].boxLength.value)*2.54);
			 //round(document.forms[0].boxLength);
			 document.forms[0].boxBreadth.value=round(parseFloat(document.forms[0].boxBreadth.value)*2.54);
			 //round(document.forms[0].boxBreadth);
			 document.forms[0].boxHeight.value=round(parseFloat(document.forms[0].boxHeight.value)*2.54);
			 //round(document.forms[0].boxHeight);
	     }
	}
  }
  else if(document.forms[0].measurement.value=='IN')
  {
	 if(dLength.length>1)
	 {
	  for(m=0;m<dLength.length;m++)
	  {
	    if(dLength[m].value.length>0 && dBreadth[m].value.length >0 && dHeight[m].value.length>0)
	    {
		  dLength[m].value=round(parseFloat(dLength[m].value)*0.3937);
		  //round(dLength[m]);
		  dBreadth[m].value=round(parseFloat(dBreadth[m].value)*0.3937);
		  //round(dBreadth[m]);
		  dHeight[m].value=round(parseFloat(dHeight[m].value)*0.3937);
		  //round(dHeight[m]);
	     }
	  }
	}
    else
	{
	      if(document.forms[0].boxLength.value.length>0 && document.forms[0].boxBreadth.value.length>0 && document.forms[0].boxHeight.value.length>0)
		 {
			 document.forms[0].boxLength.value=round(parseFloat(document.forms[0].boxLength.value)*0.3937);
			 //round(document.forms[0].boxLength);
			 document.forms[0].boxBreadth.value=round(parseFloat(document.forms[0].boxBreadth.value)*0.3937);
			 //round(document.forms[0].boxBreadth);
			 document.forms[0].boxHeight.value=round(parseFloat(document.forms[0].boxHeight.value)*0.3937);
			 //round(document.forms[0].boxHeight); 
	     }
	} 
  }

}
</script>
<body  onLoad="initialize()">
<form onSubmit="return checkForm()">
  <table width="830" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td> 
        <table width="830" border="0" cellspacing="1" cellpadding="4">
          <tr class='formlabel' valign="top"> 
            <td width='90%'>Pack Calculation </td>
            <td>QS1050623</td>
		  </tr>
        <tr class='formheader' valign="top"> 
		
		</tr> 
		</table>
        <table width="830" cellpadding="4" cellspacing="1" >
		     
			 <tr width=100% align="" class='formdata'> 
	         <td width="50%" colspan=8>
			 <font face="Verdana" size="2">Measurement :
            	<select name='measurement' onChange = "changeMeasures();Volcalculations()" class="select">
				<option value='CM' selected>CM</option>
				<option value='IN'>IN</option>
				</select>
			     </font>
			</td>
		    <td width='50%' >&nbsp;&nbsp;
			 <!-- <font face="Verdana" size="2">WeightScale : 
            	<select name='weightScale' onChange = "Volcalculations()">
				<option value='6000' selected>6000</option>
				<option value='7000'>7000</option>
				<option value='10000' >10000</option>
				</select>
			     </font> -->
			</td>
			</tr>	
		</table>	 
		<table width="830" cellpadding="4" cellspacing="1" >
			 <tr width=14% align="center" class='formheader'> 
	            <td width="75" rowspan=2></td>
				<td width="80" rowspan=2><p align="center">No of Pieces<font color="#FF0000">*</font><br></td>

		            <td width="80" rowspan=2><p align="center">Type of Box<br></td>
			<td width="245" colspan="3"><p align="center">Per Piece</td>
				 
				<td width="75" rowspan=2><p align="center">Weight(KGs)<font color="#FF0000">*</font><br></td> 
					<td width="75" rowspan=2><p align="center">
					</td>
					
				<td width="70" rowspan=2>&nbsp; </td>  
        	</tr>
			<tr width=14% align="center" class='formheader'>


						<td width="75"><p align="center">Length <br></td>
						<td width="75"><p align="center">Breadth<br></td>
						<td width="75"><p align="center">Height<br></td>

				</tr>
    
		</table>
  		<span id='cust' style='position:relative;'></span> &nbsp;
         <table width="830" border="0" cellspacing="1" cellpadding="4">
           <tr valign="top" class='formdata'> 
	            <td width="198">Total Pieces : <br>
	              <input type="text"  class='text' name="totalBooked" size="16"  value='' onKeyPress = 'return getDotNumberCode(this)'>
	            </td>
	            <td width="223">Actual Weight : <br>
	              <input type="text"  class='text' name="actualWeight" size="16"  onKeyPress = 'return getDotNumberCode(this)' readOnly>
	            <select name='ActWeigh' onChange = "Wtcalculations()" class='select'>
				<option value='KG' selected>KG</option>
				<option value='LB'>LB</option>
				
				</select>
				</td>
			    <td width="223">Total Volume : <br>		
	              <input type="text" class='text'  name="totalVolume" maxlength=10 size="16" onblur='this.value=round(this.value)' onKeyPress = 'return getDotNumberCode(this)'>
		        <select name='totVolume' onChange = "Volcalculations()" class='select'>
				<option value='CBM' selected>CBM</option>
				<option value='CFT'>CFT</option>
				
				</select>   
 	            </td>
			 
          
          <tr align="right" valign="top"> 
            <td colspan="4" align="right"> 
              <input type = "hidden" name="bookingId" value = "">
              <input type = "hidden" name="bookingType" value= "">
              <input type = "hidden" name="Operation" value="Add">
			  <input type = "hidden" name="consolType" value="">
			  <input type="hidden" name="shipmentMode" value=''>
			  <input type="hidden" name="operationType" value=''>			  
		  	  <input type = "submit" name="Submit" value="Submit" class='input'>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
</body>
</html>

<%
}
else
{
		
 %>
	<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
}

} catch(Exception e) {
		
	    //Logger.error(FILE_NAME,"Exception in QmsAreaCode.jsp file : "+e.toString());
      logger.error(FILE_NAME+"Exception in QmsAreaCode.jsp file : "+e.toString());
        String errorMessage = "Error in Getting Data";
		session.setAttribute("ErrorCode","ERR");
		session.setAttribute("ErrorMessage",errorMessage);
		
 %>
	<jsp:forward page="../ESupplyErrorPage.jsp" />
 <%
  }
%>

