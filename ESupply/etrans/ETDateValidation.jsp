<%@ page import="com.foursoft.esupply.common.util.ESupplyDateUtility"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
	ESupplyDateUtility	eSupplyDateUtility  = new ESupplyDateUtility();
	//Srivegi Modified on 20050421 (FSET-2488 & SPETI-6310)
	String		dateFormat = "";
	if(loginbean.getUserPreferences()==null)
	{dateFormat = "MM/DD/YY";}
    else
	{dateFormat				=	loginbean.getUserPreferences().getDateFormat();}
	//@@ 20050421 (FSET-2488 & SPETI-6310)
	String		currentDate				=	eSupplyDateUtility.getCurrentDateString(dateFormat);
	String format = request.getParameter("format");	
	//String currentDate = request.getParameter("currentDate");	
	String lessDate		= request.getParameter("lessDate");
%>

<script language="javascript">

/* This is JavaScript file for validating Date input values for an user interface */


function y2k1(num) {

	var strNumber = ""+num;
	var number = parseInt(strNumber, 10);
	
	//alert("year = '"+number+"'");

	if(number == 0) {
		number =  2000;
	}

	if (number >= 1  && number <= 99 )
	{
		if(number >=1 & number <= 9) {
			number =  '200' + number;
		}
		if(number >= 10 & number <= 99) {
			number =  '20' + number;
		} 
	}
	//alert("year = "+number);
	return number 
}
	
function padout1(number) 
{
	if (number >= '01' && number<= '09' )
	{
		return number;
	}
	else if (number >= 1 && number<= 9 )
		number =  '0' + number ;

	return number 

}

function LeapYear(intYear) 
{
if (((intYear % 4 == 0) && (intYear % 100 != 0))|| ((intYear % 100 == 0) && (intYear % 400 == 0)))
		return true;
	return false;
}


// @@ Suneetha Added on 20050525
function datesDiff(dt1,dt2)
{
	var	startdate =	dt1;
	// stdate one is entered
		var		stday =	startdate.substr(0,2);
		var		stmon =	startdate.substr(3,2);
		var		styear = startdate.substr(6,4);
	// end date
		enddate	= dt2;
		var		endday   = enddate.substr(0,2);
		var		endmon =enddate.substr(3,2);
		var		endyear  = enddate.substr(6,4);
	return (Date.UTC(styear,stmon-1,stday) - Date.UTC(endyear,endmon-1,endday) )/86400000;//returns difference in seconds
}
// @@ 20050525



function timeCheck(timeobj)
{
	if(timeobj.value.length > 0)
	{
		time = timeobj.value
		var i = timeobj.value.indexOf(":");
		if(i == 1)
		{
		  timeobj.value = '0'+timeobj.value;
		}
		var k = timeobj.value.indexOf(":");
		var j =	(timeobj.value.length - k-1);
		time = time.toString()
		
		if(j ==1)
			timeobj.value = timeobj.value + '0';
		var m = (timeobj.value.length - k-1);
		var delimiter  = timeobj.value.substring(3,2);
		
		if(k==2 && m==2 ) 
		{ 
			var delimiter  = timeobj.value.substring(3,2);
			if(delimiter == ":")
			{	
				var hour = timeobj.value.substring(0,2)

				var minute = timeobj.value.substring(3,5)

					
				if(hour > 23 || minute > 59)
				{
					alert("Please enter valid Time ")
					timeobj.focus();	
				}
			}
			else
			{
				alert("Invalid Time delimiter")
				timeobj.focus();
				
			}		
		}
		else if(k != 2 || m != 2) 
		{
			alert("Invalid Time format")
			timeobj.focus();
		}
	}
}


//Added by JS
function dtCheck(dtobj,dateFiledName)
{
	edt = dtobj.value;
	if(edt.length == 0)
	{
		alert(dateFiledName + " Can not Be Empty .Please Enter ");
		return false;
	}				
}



////////////////y2k bug fix////////////////
var fyear=null;
var tyear=null;
////////////////y2k bug fix////////////////

 


function dtCheck(dtobj)
{
	edt = dtobj.value;
	//alert("dtobj name = "+dtobj.name+"\ndtobj value = "+dtobj.value);
	var dateformat = '<%=format%>';
	if(dtobj.value>0)
	{
		if(dateformat == 'DD/MM/YY' || dateformat == 'DD/MM/YYYY')
		{
			var es=edt.indexOf("/");
			var eh=edt.indexOf("-");
			if(es==-1 && eh==-1)
			{
				day = edt.substr(0,2);
				mon = edt.substr(2,2);
				year= edt.substr(4,edt.length);
				var abc=day+"/"+mon+"/"+year;
				dtobj.value=abc;
				edt=abc
			}	
			else
			{
				edt= dtobj.value;
			}
		}
		else if(dateformat == 'DD-MM-YY' || dateformat == 'DD-MM-YYYY')
		{
			var es=edt.indexOf("/");
			var eh=edt.indexOf("-");
			if(es==-1 && eh==-1)
			{
				day = edt.substr(0,2);
				mon = edt.substr(2,2);
				year= edt.substr(4,edt.length);
				var abc=day+"-"+mon+"-"+year;
				dtobj.value=abc;
				edt=abc
			}
			else
			{
				edt= dtobj.value;
			}
		}
	
		else if(dateformat == 'MM/DD/YY' || dateformat == 'MM/DD/YYYY')
		{
			var es=edt.indexOf("/");
			var eh=edt.indexOf("-");
			if(es==-1 && eh==-1)
			{
				mon = edt.substr(0,2);
				day = edt.substr(2,2);
				year= edt.substr(4,edt.length);
				var abc=mon+"/"+day+"/"+year;
				dtobj.value=abc;
				edt=abc
			}
			else
			{
				edt= dtobj.value;
			}
		}
		else if(dateformat == 'MM-DD-YY' || dateformat == 'MM-DD-YYYY')
		{
			var es=edt.indexOf("/");
			var eh=edt.indexOf("-");
			if(es==-1 && eh==-1)
			{
				mon = edt.substr(0,2);
				day = edt.substr(2,2);
				year= edt.substr(4,edt.length);
				var abc=mon+"-"+day+"-"+year;
				dtobj.value=abc;
				edt=abc
			}
			else
			{
				edt= dtobj.value;
			}
		}
	}
	if(edt=="") {
		return false;
	}				
	if(edt.length>10) {
		alert("The Date format is invalid. Please enter the date in '<%=format%>'  format");
dtobj.value="";//added by silpa.p on 6-06-11 for date check
		dtobj.focus();
		flag=false;
		return false;
	}
	seps = "/";
	seph = "-";
	if( (edt.indexOf(seps)== -1) && (edt.indexOf(seph)== -1)) {
		alert("The Date format is invalid. Please enter the date in '<%=format%>'  format");
dtobj.value="";//added by silpa.p on 6-06-11 for date check
		dtobj.focus();
		return false;	
	}
	
	fpos = edt.indexOf(seps);
	
	if(fpos==-1)
		fpos = edt.indexOf(seph);

	spos = edt.indexOf(seps,fpos+1);
	
	if(spos==-1)
		spos = edt.indexOf(seph,fpos+1);
			
	if(dateformat == 'DD/MM/YY' || dateformat == 'DD-MM-YY' || dateformat == 'DD/MM/YYYY' || dateformat == 'DD-MM-YYYY')
	{
		day = edt.substr(0,fpos);
		mon = edt.substr(fpos+1,(spos-fpos-1));

	}
	if(dateformat == 'MM/DD/YY' || dateformat == 'MM-DD-YY' || dateformat == 'MM/DD/YYYY' || dateformat == 'MM-DD-YYYY')
	{
		mon = edt.substr(0,fpos);
		day = edt.substr(fpos+1,(spos-fpos-1));
	}
	
	year = edt.substr(spos+1);	
	
	if(year.length==0) {
		alert("Please enter the 'year' of the date.");
		dtobj.focus();
		return false;
	}
	
	if(year.length==1 || year.length==3) {
		alert("The 'year' in the date is not valid.");
		dtobj.focus();
		return false;
	}
	
	if(day.length>2 || mon.length>2 || year.length>4) {
		alert("This is not a valid date.");
		dtobj.focus();
		return false;
	}

	if(isNaN(day) || isNaN(mon) || isNaN(year)) {
		alert("This is not a valid date.");
		dtobj.focus();
		return false;
	}
	
	if(isNaN(day)) {
		alert("Please enter only numeric value(s) for the 'day'.");
		dtobj.focus();
		return false;
	}
	
	if(isNaN(mon)) {
		alert("Please enter only numeric value(s) for the 'month'.");
		dtobj.focus();
		return false;
	}
	
	if(isNaN(year)) {
		alert("Please enter only numeric value(s) for the 'year'.");
		dtobj.focus();
		return false;
	}


	year = y2k1(year);

	if (mon>12 || mon <1) {
		alert("The 'month' in the date is not valid.");
		dtobj.focus();
		return false;
	}
	
	if ((mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) && (day > 31 || day < 1)) {
		alert("The 'day' in the date is not valid.");
		dtobj.focus();
		return false;
	}
	
	if ((mon == 4 || mon == 6 || mon == 9 || mon == 11) && (day > 30 || day < 1)) {
		alert("The 'day' in the date is not valid.");
		dtobj.focus();
		return false;
	}

	if (mon == 2)  {
		if (day < 1) {
			alert("The 'day' in the date is not valid.");
			dtobj.focus();
			return false;
		}
		if (LeapYear(year) == true)  {
			if (day > 29)  {
				alert("There cannot be more than 29 days in February month in a leap year.");
				dtobj.focus();
				return false;
			}
		} else  {
			if (day > 28)  {
				alert("There cannot be more than 28 days in February month in a year other than a leap year.");
				dtobj.focus();
				return false;
			}
		}
	 }

	day = padout1(day);
	mon = padout1(mon);

	var datedelimiter = "/"
	if(dateformat.indexOf("-") > -1)
		datedelimiter = "-"
	
	var tempYear = year.toString();
	
	if(dateformat == 'DD/MM/YY' || dateformat == 'DD-MM-YY')
	{
		s = day + datedelimiter +mon+ datedelimiter + tempYear.substr(2) ;
	}
	if(dateformat == 'DD/MM/YYYY' || dateformat == 'DD-MM-YYYY')
	{
		s = day + datedelimiter +mon+ datedelimiter + year ; 
	}
	
	if(dateformat == 'MM/DD/YY' || dateformat == 'MM-DD-YY') 
	{
		s = mon+ datedelimiter +day+ datedelimiter + tempYear.substr(2) ;
	}
	if(dateformat == 'MM/DD/YYYY' || dateformat == 'MM-DD-YYYY')
	{
		s = mon+ datedelimiter +day+ datedelimiter + year ;	
	}

	dtobj.value=s ;
	
	return true;

	//	alert(dtobj.value  +   "is a valid date") ;
	
} // end dtCheck


///////
function chkFutureDate(obj,type)
{	
	var startdate = obj.value;		
	//var enddate=currentDate;
	var enddate = "<%=currentDate%>"
	
	var seps = "/";
	var seph = "-";
	
	var fpos = startdate.indexOf(seps);
	
	if(fpos==-1)
		fpos = startdate.indexOf(seph);

	spos = startdate.indexOf(seps,fpos+1);
	
	if(spos==-1)
		spos = startdate.indexOf(seph,fpos+1);
	
	var dateformat = '<%=format%>'
		
	if(dateformat == 'DD/MM/YY' || dateformat == 'DD-MM-YY' || dateformat == 'DD/MM/YYYY' || dateformat == 'DD-MM-YYYY')
	{
		stday = startdate.substr(0,fpos);
		stmon = startdate.substr(fpos+1,(spos-fpos-1));
		
		day = enddate.substr(0,fpos);
		month = enddate.substr(fpos+1,(spos-fpos-1));

	}
	if(dateformat == 'MM/DD/YY' || dateformat == 'MM-DD-YY' || dateformat == 'MM/DD/YYYY' || dateformat == 'MM-DD-YYYY')
	{
		stmon = startdate.substr(0,fpos);
		stday = startdate.substr(fpos+1,(spos-fpos-1));
		
		month = enddate.substr(0,fpos);
		day   = enddate.substr(fpos+1,(spos-fpos-1));
	}
	
	var styear = startdate.substr(spos+1);	
	var year = enddate.substr(spos+1);

	////////////////y2k bug fix////////////////
	if(styear.length == 2)
	{
		if(styear <25)
			styear = "20" +styear;
		else
			styear = "19" +styear;
	}
	
	if(year.length == 2)
	{
		if(year <25)
			year = "20" +year;
		else
			year = "19" +year;
	}
	////////////////y2k bug fix////////////////
	
	//for past date

	/*old
	if(styear.length == 2)
		styear = "20" +styear
		
	
	if(year.length == 2)
		year = "20" +year
		*/
	//for future date
	
		if(type=="gt")
		{
			daysdiff =	Date.UTC(styear,stmon-1,stday)-Date.UTC(year,month-1,day) ;
			daysleft = daysdiff/1000/60/60/24;
			if(daysleft>=0)
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
	
	//for past date

	if(type=="lt")
	{			
		daysdiff =  Date.UTC(year,month-1,day) - Date.UTC(styear,stmon-1,stday);
		daysleft = daysdiff/1000/60/60/24;
		
//		if(daysleft <=0)  modified by rakesh for (DHL) on 20050108
		if(daysleft >=0)  
		{	
			return true;
		}
		else
		{
			return false;
		}
	}
	return true;
}

function chkFromToDate(fromDate,toDate)
{
	var startdate = fromDate;
	var enddate = toDate;

  //("fromdate::"+fromDate)
  //("toDate:::"+toDate)
	
	var seps = "/";
	var seph = "-";
	
	var fpos = startdate.indexOf(seps);
	
	if(fpos==-1)
		fpos = startdate.indexOf(seph);

	spos = startdate.indexOf(seps,fpos+1);
	
	if(spos==-1)
		spos = startdate.indexOf(seph,fpos+1);

	var dateformat = '<%=format%>'
	
	//alert('startdate = '+startdate);
	//alert('enddate = '+enddate);

		
	if(dateformat == 'DD/MM/YY' || dateformat == 'DD-MM-YY' || dateformat == 'DD/MM/YYYY' || dateformat == 'DD-MM-YYYY')
	{
		stday = startdate.substr(0,fpos);
		stmon = startdate.substr(fpos+1,(spos-fpos-1));
		
		day = enddate.substr(0,fpos);
		month = enddate.substr(fpos+1,(spos-fpos-1));
		

	}

	if(dateformat == 'DD/MON/YY' || dateformat == 'DD-MON-YY' || dateformat == 'DD/MON/YYYY' || dateformat == 'DD-MON-YYYY')
	{
		stday = startdate.substr(0,fpos);
		
		stmon = getNumMonth(startdate.substr(fpos+1,(spos-fpos-1)));
		
		
		
		day = enddate.substr(0,fpos);
		month = getNumMonth(enddate.substr(fpos+1,(spos-fpos-1)));
	
	}


	if(dateformat == 'MM/DD/YY' || dateformat == 'MM-DD-YY' || dateformat == 'MM/DD/YYYY' || dateformat == 'MM-DD-YYYY')
	{
		stmon = startdate.substr(0,fpos);
		stday = startdate.substr(fpos+1,(spos-fpos-1));
		
		month = enddate.substr(0,fpos);
		day   = enddate.substr(fpos+1,(spos-fpos-1));
	}
	
	var styear = startdate.substr(spos+1);	
	var endyear = enddate.substr(spos+1);

	//alert('styear = '+styear);
	//alert('endyear = '+endyear);
	////////////////y2k bug fix////////////////	
	if (dateformat == 'DD/MM/YY' || dateformat == 'DD-MM-YY' || dateformat == 'MM/DD/YY' || dateformat == 'MM-DD-YY'||dateformat == 'DD/MON/YY' || dateformat == 'DD-MON-YY' )
	{
	/*
		if (styear.length > 2)
		{
			if(styear.substr(2,2) <25)
				styear = "20" +styear.substr(2,2);
			else
				styear = "19" +styear.substr(2,2);
		}
		else
		{
			if(styear <25)
				styear = "20" +styear;
			else
				styear = "19" +styear;
		}
		//alert(styear);
		if (endyear.length > 2)
		{
			if(endyear.substr(2,2) <25)
				endyear = "20" +endyear.substr(2,2);
			else
				endyear = "19" +endyear.substr(2,2);
		}
		else
		{
			if(endyear <25)
				endyear = "20" +endyear;
			else
				endyear = "19" +endyear;
		}
		//alert(endyear);
		*/

		/*if (styear.length > 2)
		{
			if(styear.substr(2,2) <25)
				styear = "20" +styear.substr(2,2);
			else
				styear = "19" +styear.substr(2,2);
		}
		else*/
		//
	//	{
		if (styear.length ==2)
		{
			if(styear <25)
				styear = "20" +styear;
			else
				styear = "19" +styear;
		}
	//	}
		//alert(styear);
	//	if (endyear.length > 2)
	//	{
	//		if(endyear.substr(2,2) <25)
	//			endyear = "20" +endyear.substr(2,2);
	//		else
	//			endyear = "19" +endyear.substr(2,2);
	//	}
	//	else
	//	if{
		if (endyear.length == 2)
		{
			if(endyear <25)
				endyear = "20" +endyear;
			else
				endyear = "19" +endyear;
		}

		//}
		//alert(endyear);
	} 
////////////////y2k bug fix////////////////
	daysdiff =  Date.UTC(endyear,month-1,day) - Date.UTC(styear,stmon-1,stday);
	daysleft = daysdiff/1000/60/60/24;

	if(daysleft>=0)
	{	
		return true;
	}
	else
	{
		return false;
	}
	return true;
}



//////////////////////////////// timeDifference(startdate) /////////////////////////


function y2k(number)    { return (number < 1000) ? number + 1900 : number; }
function padout(number) { return (number < 10) ? '0' + number : number; }

var today = new Date();
var day = today.getDate(),  month = today.getMonth(), year = today.getYear(), whichOne = 0,index=0,noOfrow = 0;

////////////////y2k bug fix////////////////
var yearTemp=null;
////////////////y2k bug fix////////////////

function restart() 
{	
	var formatTemp = '<%=format%>'	
	 yearTemp = year.toString() //var removed
		
	dySeperator =  formatTemp.indexOf("-");
	finalSeparator = '/' 
	if(dySeperator > -1)
		finalSeparator ='-'

	var d = '';
	if(formatTemp == 'DD/MM/YY' || formatTemp == 'DD-MM-YY')
	{
		d=''+  padout(day)+ finalSeparator + padout(month - 0 + 1) + finalSeparator + yearTemp.substr(2);
	}
	
	if(formatTemp == 'DD/MM/YYYY' || formatTemp == 'DD-MM-YYYY')
	{
		d=''+  padout(day)+ finalSeparator + padout(month - 0 + 1) + finalSeparator + year;
	}
	
	if(formatTemp == 'MM/DD/YY' || formatTemp == 'MM-DD-YY')
	{
		d=''+  padout(month - 0 + 1)+ finalSeparator +padout(day)  + finalSeparator + yearTemp.substr(2);
	}
	
	if(formatTemp == 'MM/DD/YYYY' || formatTemp == 'MM-DD-YYYY')
	{
		d=''+  padout(month - 0 + 1)+ finalSeparator +padout(day)  + finalSeparator + year;
	}	
	if(formatTemp == 'DD-MON-YYYY')
	{
		d=''+  padout(month - 0 + 1)+ finalSeparator +padout(day)  + finalSeparator + year;
	}
	
<%//@@ Avinash replaced on 20050208
/*
	if(whichOne=="scheduleDate")
	{
		
		if(index==0)
		{
			document.forms[0].scheduleDate.value=d;
		}else
		{
			document.forms[0].scheduleDate[index].value=d;
		}
	}else if(whichOne=="actualtime")
	{
	  if(noOfrow==0 || noOfrow == 1)
		{
			document.forms[0].actualtime.value=d;
		}else
		{
			document.forms[0].actualtime[index].value=d;
		} 
	}else if(whichOne=="actualTime")
	{
	  if(noOfrow==0 || noOfrow == 1)
		{
			document.forms[0].actualTime.value=d;
		}else
		{
			document.forms[0].actualTime[index].value=d;
		} 
	}
	else if(whichOne=="etd")
	{
	  if(noOfrow==0 || noOfrow == 1)
		{
			document.forms[0].etd.value=d;
		}else
		{
			document.forms[0].etd[index].value=d;
		} 
	}
	else if(whichOne=="eta")
	{
	  if(noOfrow==0 || noOfrow == 1)
		{
			document.forms[0].eta.value=d;
		}else
		{
			document.forms[0].eta[index].value=d;
		} 
	}
	else

	{		
		var  element = document.forms[0].elements;

		if(formatTemp == 'DD/MM/YY' || formatTemp == 'DD-MM-YY')
		{
			element[whichOne].value = '' + padout(day) + finalSeparator + padout(month - 0 + 1) + finalSeparator + yearTemp.substr(2);
		}
		if(formatTemp == 'DD/MM/YYYY' || formatTemp == 'DD-MM-YYYY')
		{
			element[whichOne].value = '' + padout(day) + finalSeparator + padout(month - 0 + 1) + finalSeparator + year;
		}

		if(formatTemp == 'MM/DD/YY' || formatTemp == 'MM-DD-YY')
		{
			element[whichOne].value = '' + padout(month - 0 + 1) + finalSeparator + padout(day) + finalSeparator + yearTemp.substr(2);
		}
		if(formatTemp == 'MM/DD/YYYY' || formatTemp == 'MM-DD-YYYY')
		{
			element[whichOne].value = '' + padout(month - 0 + 1) + finalSeparator + padout(day) + finalSeparator + year;
		}
		if(formatTemp == 'DD-MON-YYYY')
		{
			element[whichOne].value=''+  padout(month - 0 + 1)+ finalSeparator +padout(day)  + finalSeparator + year;
		}

	}

	*/%>

	var  element = document.getElementsByName(whichOne)[isNaN(index) ? 0 : index];

	if(formatTemp == 'DD/MM/YY' || formatTemp == 'DD-MM-YY')
	{
		element.value = '' + padout(day) + finalSeparator + padout(month - 0 + 1) + finalSeparator + yearTemp.substr(2);
	}
	if(formatTemp == 'DD/MM/YYYY' || formatTemp == 'DD-MM-YYYY')
	{
		element.value = '' + padout(day) + finalSeparator + padout(month - 0 + 1) + finalSeparator + year;
	}

	if(formatTemp == 'MM/DD/YY' || formatTemp == 'MM-DD-YY')
	{
		element.value = '' + padout(month - 0 + 1) + finalSeparator + padout(day) + finalSeparator + yearTemp.substr(2);
	}
	if(formatTemp == 'MM/DD/YYYY' || formatTemp == 'MM-DD-YYYY')
	{
		element.value = '' + padout(month - 0 + 1) + finalSeparator + padout(day) + finalSeparator + year;
	}
	if(formatTemp == 'DD-MON-YYYY')
	{
		element.value=''+  padout(month - 0 + 1)+ finalSeparator +padout(day)  + finalSeparator + year;
	}
	
	//@added by silpa on 29-04-2011
	if(whichOne=="effDate")
	    element.focus();//ended

	<%//@@ 20050208%>
		////////////////y2k bug fix on////////////////


if((whichOne=="fromDate")&&(yearTemp!=null))
	 fyear=yearTemp;
	 if((whichOne=="toDate")&&(yearTemp!=null))
	 tyear=yearTemp;

    if((whichOne=="contractStartDate")&&(yearTemp!=null)) 
		fyear=yearTemp;
	 if((whichOne=="contractValidDate")&&(yearTemp!=null))
		 tyear=yearTemp;



if((whichOne=="manufacturingDate")&&(yearTemp!=null)) 
	 fyear=yearTemp;
	 if((whichOne=="purchaseDate")&&(yearTemp!=null))
	 tyear=yearTemp;

if((whichOne=="routeValidFrom")&&(yearTemp!=null))  
	 fyear=yearTemp;
	 if((whichOne=="routeValidTill")&&(yearTemp!=null))
	 tyear=yearTemp;



////////////////y2k bug fix////////////////
	mywindow.close();
}

//Finally
//Added by JS
function newWindow(str,noOfrows,ind,contextPath) 
{
	whichOne = str;
	index=ind;
  noOfrow= noOfrows;
  var	width		=	350;
	var	height		=	270;
	var	top			=	(screen.availHeight - height) / 2;
	var	left		=	(screen.availWidth  - width)  / 2;
  day = today.getDate(), month = today.getMonth(), year = y2k(today.getYear());
	var myOptions =	'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';						

   if(contextPath!=null)
	{
	    mywindow=open(contextPath+'html/ETSCalendar.html','myname', myOptions);
      mywindow.location.href = contextPath+'html/ETSCalendar.html';
     

	}
	else
	{
		 mywindow=open('html/ETSCalendar.html','myname', myOptions);
		 mywindow.location.href = 'html/ETSCalendar.html';
	}
	
		if (mywindow.opener == null)
			mywindow.opener = self;
}



function nullChecker(form)
{	
	 var fvalue = form.fromdate.value;
	 var tvalue = form.todate.value;
	 var formatTemp = '<%=format%>'	
	//The below is commented by S.V. Srinivas becoz this variables are no ware used
	 //var BBDate = document.forms[0].hiddenBBDate.value;
	 //var FYDate = document.forms[0].hiddenFYDate.value;
     //var CRDate = document.forms[0].hiddenCRDate.value;
  
	 var flag=true;
	
     flag=dtCheck(form.fromdate);
	 if( flag==false)
	 {
  	   alert("The 'Date' field cannot be left blank.");
	   return false;
     }

	 flag1=dtCheck(form.todate);
	 if(flag1==false)
	 {
	     alert("The 'Date' field cannot be left blank.");
		 return false;
     }
	 /*
	 if(!chkFromToDate(fvalue,tvalue))
	 {
		 alert("The 'From' date should be less than or equal to the 'To' date.");
		 return false;
     }
		*/
	if(formatTemp == 'DD/MM/YY' || formatTemp == 'DD-MM-YY' || formatTemp == 'MM/DD/YY' || formatTemp == 'MM-DD-YY' ||formatTemp == 'DD/MON/YY' || formatTemp == 'DD-MON-YY')
	{ 
     ////////////////y2k bug fix////////////////
	
	if(fyear!=null){
	fvalue = fvalue.substring(0,fvalue.length-2)+fyear;
	}
	if(tyear!=null){
	tvalue = tvalue.substring(0,tvalue.length-2)+tyear;
	}
	
	////////////////y2k bug fix////////////////
	 if(!chkFromToDate(fvalue,tvalue))
  	 {
    	 alert("The 'From' date should be less than or equal to the 'To' date.");
       return false;
     }
  }   
  else
  {
      // ("in else loop") 
     if(!chkFromToDate(fvalue,tvalue))
  	 {
    	 alert("The 'From' date should be less than or equal to the 'To' date.");
       return false;
     }
  }
	 if(!chkFutureDate(form.todate,"lt"))
	 {
		alert("The 'To' date should be less than the Current Date '<%=currentDate%>'. " );
		return false;
	 }
	 return true;
}

////////////////////////// showReport( flag ) ///////////////////////////

var windowFeatures 	= 	"directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no,scrollbars=yes,resizable=yes";

function showReport(flag, formObj)
{
	if(flag==true) {
		
		var millisecs	=	(new Date()).getTime();

		var repWin = open("ESupplyReportPage.jsp", "REPORTWINDOW_"+millisecs, windowFeatures);
		window.repWin;

		//while(repWin.frames.length < 2) {}
		
		//alert("newly opened window's name = '"+windowName+"'\n"+
		//		"opened window's frames[1]'s name = '"+repWin.frames[1].name+"'");
		
		//repWin.frames[1].name	=	"REPORTFRAME_"+millisecs;

		//alert("new window name = "+repWin.frames[1].name);

		//formObj.target	=	repWin.frames[1].name;
		
		repWin.opener = self;
			repWin.focus();
				
		return true;
	} else {
		return false;	 
	}
}

//new on 10-6-04 by Annapurna
/* This is JavaScript function for validating Date input values for an user interface */
function lessDate(prevdate1,prevtime1,nextdate2,nexttime2)
{
		var	flag		=	true;
		var date1		=	prevdate1;
		var time1		=	prevtime1;
		if(time1=='')
			time1='00:00';
		var date2		=	nextdate2;
		var time2		=	nexttime2;
		if(time2=='')
			time2='00:00';	
		var dateFormat = '<%=format%>'
		
			time1	=	time1.split(":");
			time2	=   time2.split(":");
		
			var H1  = time1[0];
			var MI1	= time1[1];

			var H2	= time2[0];
			var MI2	= time2[1];

		if(dateFormat == "DD/MM/YY" || dateFormat =="DD/MM/YYYY" || dateFormat =="MM/DD/YY" || dateFormat =="MM/DD/YYYY" )
		{
				date1	=	date1.split("/");
				date2	=	date2.split("/");
		}
		if(dateFormat =="DD-MM-YY" || dateFormat =="DD-MM-YYYY" || dateFormat =="MM-DD-YY" || dateFormat =="MM-DD-YYY" )
		{
				date1	=	date1.split("-");
				date2	=	date2.split("-");
		}
		if(dateFormat.substring(0,2)=="DD")
		{
			var D1	= date1[0];
			var M1	= date1[1];
			var Y1	= date1[2];

			var D2	= date2[0];
			var M2	= date2[1];
			var Y2	= date2[2];
		}
		if(dateFormat.substring(0,2)=="MM")
		{
			var D1	= date1[1];
			var M1	= date1[0];
			var Y1	= date1[2];

			var D2	= date2[1];
			var M2	= date2[0];
			var Y2	= date2[2];
		}
	var dateObj1 = new Date(year2k(Y1)+"/"+M1+"/"+D1);
	dateObj1.setHours(H1);
	dateObj1.setMinutes(MI1);
	var dateObj2 = new Date(year2k(Y2)+"/"+M2+"/"+D2);
	dateObj2.setHours(H2);
	dateObj2.setMinutes(MI2);
	if(dateObj2 > dateObj1)
		flag =true;
	else
		flag =false;


	return flag;
}

function year2k(number) { return (number < 2000) ? number + 2000 : number; }

/*
<%
if(lessDate!=null)
{
%>
function lessDate(prevdate1,prevtime1,nextdate2,nexttime2)
{

	var	flag		=	true;
	if(Operation!='View')
	{
		var date1		=	prevdate1;
		var time1		=	prevtime1;
		if(time1=='')
			time1='00:00';
		var date2		=	nextdate2;
		var time2		=	nexttime2;
		if(time2=='')
			time2='00:00';	
		var dateFormat	= 'DD-MM-YY';

		if(dateFormat == "DD/MM/YY" || dateFormat =="DD/MM/YYYY" || dateFormat =="MM/DD/YY" || dateFormat =="MM/DD/YYYY" )
		{
				date1	=	date1.split("/");
				date2	=	date2.split("/");
		}
		if(dateFormat =="DD-MM-YY" || dateFormat =="DD-MM-YYYY" || dateFormat =="MM-DD-YY" || dateFormat =="MM-DD-YYY" )
		{
				date1	=	date1.split("-");
				date2	=	date2.split("-");
		}
		if(dateFormat.substring(0,2)=="DD")
		{
			var D1	= date1[0];
			var M1	= date1[1];
			var Y1	= date1[2];

			var D2	= date2[0];
			var M2	= date2[1];
			var Y2	= date2[2];
		}
		if(dateFormat.substring(0,2)=="MM")
		{
			var D1	= date1[1];
			var M1	= date1[0];
			var Y1	= date1[2];

			var D2	= date2[1];
			var M2	= date2[0];
			var Y2	= date2[2];
		}
		var dateObj1 = new Date(D1+"/"+M1+"/"+Y1);
		var dateObj2 = new Date(D2+"/"+M2+"/"+Y2);
		if(dateObj1 == dateObj2)
			flag = true;
		if(dateObj2 < dateObj1)
			flag =false;
	}
	return flag;
}
<%
}
%>

*/
</script>
