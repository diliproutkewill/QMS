//alert("esupply");
function toUpperCase()
{

	if( document.forms.length > 0 ) 
	{
		var field = document.forms[0];
		for(i = 0; i < field.length; i++) 
		{
			if( (field.elements[i].type == "text") )
			{
        	 txt=document.forms[0].elements[i].value.toUpperCase();
			 txt=txt.replace(/'/g,""); 			
			
	   		 document.forms[0].elements[i].value =txt;
      	  }
		}
	}
}

function getSpecialCode(){
  if(event.keyCode!=13){
	if((event.keyCode==36 || event.keyCode==34 || event.keyCode==37 || event.keyCode==38 || event.keyCode==39 || event.keyCode==59 || event.keyCode==96 || event.keyCode==126))
		return false;
  }
  return true;			
}

function checkMandatory(input,label)
{
    if(trim(input.value).length == 0)
    {
        alert("Please enter "+label);
        input.focus();
		input.select();
        return false;
    }
    if(!specialCharFilter(input,label))
     return false;
 return false;
}

function specialCharFilter(input,label)
    {
        s = trim(input.value);
        //filteredValues = "''~!@#$%^&*()_-+=|\:;<>,./?";
		filteredValues = "$?'`;"+'"';	
        var i;
        var returnString = "";
        var flag = 0;
        for (i = 0; i < s.length; i++)
        {
            var c = s.charAt(i);
            if ( filteredValues.indexOf(c) == -1 )
                    returnString += c.toUpperCase();
            else
            {
                alert("Please do not enter special characters for "+label);
                input.focus();
				input.select();
                return false;
            }
        }
        return true;
  }
 function charFilter(input,label)
    {
        s = trim(input.value.toUpperCase());
        //filteredValues = "''~!@#$%^&*()_-+=|\:;<>,./?";
		filteredValues = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";	
        var i;
        var returnString = "";
        var flag = 0;
        for (i = 0; i < s.length; i++)
        {
            var c = s.charAt(i);
            if ( filteredValues.indexOf(c) != -1 )
                    returnString += c.toUpperCase();
            else
            {
                alert("Please do not enter special characters for "+label);
                input.focus();
				input.select();
                return false;
            }
        }
        return true;
  }
  function numberFilter(input,label)
  {
        s = trim(input.value.toUpperCase());
        //filteredValues = "''~!@#$%^&*()_-+=|\:;<>,./?";
		filteredValues = "1234567890";	
        var i;
        var returnString = "";
        var flag = 0;
        for (i = 0; i < s.length; i++)
        {
            var c = s.charAt(i);
            if ( filteredValues.indexOf(c) != -1 )
                    returnString += c.toUpperCase();
            else
            {
                alert("Enter only numbers for "+label);
                input.focus();
				input.select();
                return false;
            }
        }
		input.value = trim(input.value);
        return true;
}
function specialCharFilter1(input,label)
    {
        s = trim(input);
        //filteredValues = "''~!@#$%^&*()_-+=|\:;<>,./?";
		filteredValues = "-()$?'`;"+'"';	
        var i;
        var returnString = "";
        var flag = 0;
        for (i = 0; i < s.length; i++)
        {
            var c = s.charAt(i);
            if ( filteredValues.indexOf(c) == -1 )
                    returnString += c.toUpperCase();
            else
            {
                alert("Please do not enter special characters for "+label);                
                return false;
            }
        }
        return true;
  }

  function numberFilter1(input,label)
  {
        s = trim(input);
        //filteredValues = "''~!@#$%^&*()_-+=|\:;<>,./?";
		filteredValues = "1234567890";	
        var i;
        var returnString = "";
        var flag = 0;
        for (i = 0; i < s.length; i++)
        {
            var c = s.charAt(i);
            if ( filteredValues.indexOf(c) != -1 )
                    returnString += c.toUpperCase();
            else
            {
                alert("Enter only numbers for "+label);              
                return false;
            }
        }
		input.value = trim(input.value);
        return true;
}
  function numberFilter2(input)
  {
        s = trim(input.value);
        //filteredValues = "''~!@#$%^&*()_-+=|\:;<>,./?";
		filteredValues = "1234567890";	
        var i;
        var returnString = "";
        var flag = 0;
        for (i = 0; i < s.length; i++)
        {
            var c = s.charAt(i);
            if ( filteredValues.indexOf(c) != -1 )
                    returnString += c.toUpperCase();
            else
            {
                 return false;
            }
        }
		input.value = trim(input.value);
        return true;
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

function checkNumbers(input,label)
{
    if(trim(input.value).length>0)
    {
        if(isNaN(trim(input.value)))
        {
            alert("Please do not enter characters for "+label);
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
function removeDecimal(input)
{
  var newValue='';
 
  var first = input.value.substring(0,input.value.indexOf("."));
  var last = input.value.substring(input.value.indexOf(".")+1);
  var lastTwo = input.value.substring(input.value.indexOf(".")+1);
  var find  = input.value.indexOf(".");
  
   if(lastTwo.length >2)
      lastTwo = lastTwo.substring(0,2);
  
   if(find!=-1 )
   {
        newValue = first+"."+lastTwo;
		input.value=newValue; 
	 }
 }

function roundToPennies(n)
{
 pennies = n * 100;

 pennies = Math.round(pennies);

 strPennies = "" + pennies;
 len = strPennies.length;

 alert( strPennies.substring(0, len - 2) + "." + strPennies.substring(len - 2, len));
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




function roundToNear(input)
{
          var val = input.value;
	      var index = val.indexOf(".");
		  var valueBeforeDecimal =val.substring(0,index);
		  var valueAfterDecimal  =val.substring(index+1);
		  if( index != -1 )
		  {
			if(valueAfterDecimal.length != 0 )
			{
			  if(parseInt(valueAfterDecimal) > 0 )
		      {
			    firstNumber  = val.substring(index+1,index+2);
			    secondNumber = val.substring(index+2);
			    if(parseInt(secondNumber) < 5 )
			    {  
			      input.value = valueBeforeDecimal+"."+firstNumber+"0";
			    }
			    else if(parseInt(secondNumber) >5)
			    {  
				  input.value = parseInt(valueBeforeDecimal)+"."+parseInt(parseInt(firstNumber)+1)+"0";
		        }
		        else
				{
				  input.value = valueBeforeDecimal+"."+firstNumber+"0";
				}
			  }
		      else
			  {
			    input.value = valueBeforeDecimal+".00";
			  }
		    }
	      }
          else
		  {
		        input.value = val+".00";
		  }
 }


function weightRounding( obj)
{     
		var val = obj.value;
		
		if( val >= 0)
		{  
			if(isNaN(val))
				val=0;
			var value8= val;
			result8 =Math.floor(value8)+".";
			var cents8=100*(value8-Math.floor(value8))+0.5;
			result8 += Math.floor(cents8/10);
			result8 += Math.floor(cents8%10);
			val = result8;
			obj.value = parseFloat(val);
		}
}
function toDouble(num){

num = Math.round(num * 100);
num = parseFloat(num / 100);
{
if (num == parseInt(num))
{
 num= (num + ".00");
}
else if ((num * 10) == parseInt(num * 10))
num =(num + "0");
}
alert(num);
}

function showLOV(url)
{    

		var	width		=	360;
		var	height		=	270;  // Changed According to new  UI Version.
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;
		
		var Bars		= 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options		= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		var Features	= Bars+' '+Options;

		if(Win !=null)
		{
			Win.close();
			Win = open(url,'Doc',Features);
		}		
		else
			Win = window.open(url,'Doc',Features);

		if (!Win.opener) 
			Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;
}
function checkNumbersAndRoundToTwoDecimal(input,label)
{
    if(trim(input.value).length>0)
    {
        if(isNaN(trim(input.value)))
        {
            alert("Please do not enter characters for "+label);
            input.focus();
            return false;
        }
    }
	roundToTwoDecimal(input);
    return true
}
function roundToTwoDecimal( obj)
{     
		var val = obj.value;
		
		if( val >= 0)
		{  
			if(isNaN(val))
				val=0;
			var value8= val;
			result8 =Math.floor(value8)+".";
			var cents8=100*(value8-Math.floor(value8))+0.5;
			if(cents8>100)
			{
				result8	=	parseInt(result8)+1;
				result8	=	result8+".";
				cents8	=	00;
			}
				result8 += Math.floor(cents8/10);
				result8 += Math.floor(cents8%10);
			val = result8;
			obj.value = parseFloat(val);
		}
}
function toUpper(input)
{		
		input.value	=	input.value.toUpperCase();
}
function roundValue(obj)
{
		var val = obj.value;		
		
		if(isNaN(val))
			val=0;

		if( val >= 0)
		{  
			if(isNaN(val))
				val=0;
			var value8= val;
			result8 =Math.floor(value8)+".";
			var cents8=100*(value8-Math.floor(value8))+0.5;
			if(cents8 >=100)
			{
				result8=(Math.floor(value8)+2)+".00"
			}
			else
			{
			result8 += Math.floor(cents8/10);
			result8 += Math.floor(cents8%10);
			}			
			obj.value = result8;
		}
}