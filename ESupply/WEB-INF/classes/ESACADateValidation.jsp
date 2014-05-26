<%
	String format = request.getParameter("format");
	String currentDate = request.getParameter("currentDate");
%>

<script language="javascript">

/* This is JavaScript file for validating Date input values for an user interface */

function y2k1(num) {

	var strNumber = ""+num;
	var number = parseInt(strNumber, 10);
	
	//("year = '"+number+"'");

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
	//("year = "+number);
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

function timeCheck(timeobj)
{
	
	time = timeobj.value
	time = time.toString()
		
	if(time.length==5)
	{ 
		var delimiter = time.substr(2,1)
		if(delimiter == ":")
		{	
			var hour = time.substr(0,2)
			var minute = time.substr(3,2)
				
			if(hour > 23 || minute > 59)
			{
				alert("Please enter valid Time ")
				timeobj.focus();
				return false;	
			}
		}
		else
		{
			alert("Invalid Time delimiter")
			timeobj.focus();
			return false;
		}		
	}
	else if(time.length != 0 && time.length != 5)
	{
		alert("Invalid Time format")
		timeobj.focus();
		return false;
	}
	return true;
}

function getNumMonth(mon)
{
	
	   var monthArray = new Array("JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC");
		
		if(isNaN(mon))
		{
		mon=mon.toUpperCase();
				
			for(var i=0; i<12; i++)
			{
			
				if(monthArray[i]==mon)
				{
				returnValue = i+1;
				break;
				}
				returnValue = i+23;
				
			}
		
		}
		
	return returnValue;
}


function getMon(mon)
{		
		var monthArray = new Array("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
		
		var returnValue;

		for(var i=0; i<12; i++)
		{
			if(mon==i)
			{
			returnValue = monthArray[i-1];
			break;
			}
		}
		return returnValue;
	
}
function dtCheck(dtobj)
{
	edt = dtobj.value;
	//alert("dtobj name = "+dtobj.name+"\ndtobj value = "+dtobj.value);
	
	if(edt=="") {
		return false;
	}	
	
	var dateformat = '<%=format%>'	
			
	if(edt.length>11) {
		alert("The Date format is invalid. Please enter the date in '"+dateformat+"' format");
		dtobj.focus();
		flag=false;
		return false;
	}
	seps = "/";
	seph = "-";
	if( (edt.indexOf(seps)== -1) && (edt.indexOf(seph)== -1)) {
		alert("The Date format is invalid. Please enter the date in '"+dateformat+"' format");
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
	
	if(dateformat == 'DD/MON/YYYY' || dateformat == 'DD-MON-YYYY'|| dateformat == 'DD/MON/YY' || dateformat == 'DD/MON/YY')
	{
	
	day = edt.substr(0,fpos);
	mon = getNumMonth(edt.substr(fpos+1,(spos-fpos-1)));
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
		alert("This is not a valid date");
		dtobj.focus();
		return false;
	}

	if(isNaN(day) || isNaN(mon) || isNaN(year)) {
		alert("This is not a valid date");
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

	if(dateformat == 'DD/MM/YY' || dateformat == 'DD-MM-YY' || dateformat == 'DD/MON/YY' || dateformat == 'DD-MON-YY')
	{
		if (tempYear.length > 2)
		{
			s = day + datedelimiter +mon+ datedelimiter + tempYear.substr(2) ;
		}
		else
		{    
			s = day + datedelimiter +mon+ datedelimiter + tempYear;
		}
	}
	if(dateformat == 'DD/MM/YYYY' || dateformat == 'DD-MM-YYYY')
	{
		s = day + datedelimiter +mon+ datedelimiter + year ; 
	}
	
	if(dateformat == 'MM/DD/YY' || dateformat == 'MM-DD-YY') 
	{
		if (tempYear.length > 2)
		{
			s = mon+ datedelimiter +day+ datedelimiter + tempYear.substr(2) ;
		}
		else
		{
			s = mon+ datedelimiter +day+ datedelimiter + tempYear ;
		}
	}

	if(dateformat == 'MM/DD/YYYY' || dateformat == 'MM-DD-YYYY')
	{
		s = mon+ datedelimiter +day+ datedelimiter + year ;	
	}
	
	
	if(dateformat == 'DD/MON/YY' || dateformat == 'DD-MON-YY')
	{
		if (tempYear.length > 2)
		{
			s = day + datedelimiter +getMon(mon)+ datedelimiter + tempYear.substr(2) ;
		}
		else
		{    
			s = day + datedelimiter +getMon(mon)+ datedelimiter + tempYear;
		}
	}
	if(dateformat == 'DD/MON/YYYY' || dateformat == 'DD-MON-YYYY')
	{
		s = day + datedelimiter +getMon(mon)+ datedelimiter + year ; 
	}

	dtobj.value=s ;
	
	return true;
	
} // end dtCheck


function chkFutureDate(obj,type)
{
	var startdate = obj.value;
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
	
	if(styear.length == 2)
		styear = "20" +styear
		
	var year = enddate.substr(spos+1);

	if(year.length == 2)
		year = "20" +year

	//for past date

	if(type=="lt")
	{
		daysdiff =  Date.UTC(year,month-1,day) - Date.UTC(styear,stmon-1,stday);
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
		
	if (dateformat == 'DD/MM/YY' || dateformat == 'DD-MM-YY' || dateformat == 'MM/DD/YY' || dateformat == 'MM-DD-YY'||dateformat == 'DD/MON/YY' || dateformat == 'DD-MON-YY' )
	{
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
	} 

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
var day = today.getDate(),  month = today.getMonth(), year = today.getYear(), whichOne = 0,index=0;

function restart() 
{
	var formatTemp = '<%=format%>'	
	var yearTemp = year.toString()
		
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
	
	if(formatTemp == 'DD/MON/YYYY' || formatTemp == 'DD-MON-YYYY')
	{
		d=''+  padout(day)+ finalSeparator + getMon(month - 0 + 1) + finalSeparator + year;
	}

	if(formatTemp == 'DD/MON/YY' || formatTemp == 'DD-MON-YY')
	{
		d=''+  padout(day)+ finalSeparator + getMon(month - 0 + 1) + finalSeparator + yearTemp.substr(2);
	}

	if(formatTemp == 'MM/DD/YY' || formatTemp == 'MM-DD-YY')
	{
		d=''+  padout(month - 0 + 1)+ finalSeparator +padout(day)  + finalSeparator + yearTemp.substr(2);
	}
	
	if(formatTemp == 'MM/DD/YYYY' || formatTemp == 'MM-DD-YYYY')
	{
		d=''+  padout(month - 0 + 1)+ finalSeparator +padout(day)  + finalSeparator + year;
	}	
	

	if(whichOne=="refDocDate")
	{
		if(number2==1)
		{
			document.forms[0].refDocDate.value=d;
		}else
		{
			document.forms[0].refDocDate[index].value=d;
		}
	}
	else if(whichOne=="chequeDate")
	{
		if(parseInt(number)-1==1)
		{
			document.forms[0].chequeDate.value=d;
		}else
		{
			document.forms[0].chequeDate[index].value=d;
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
		if(formatTemp == 'DD/MON/YY' || formatTemp == 'DD-MON-YY')
		{
			element[whichOne].value = '' + padout(day) + finalSeparator + getMon(month- 0 + 1) + finalSeparator + yearTemp.substr(2);
		}
		if(formatTemp == 'DD/MON/YYYY' || formatTemp == 'DD-MON-YYYY')
		{
			element[whichOne].value = '' + padout(day) + finalSeparator + getMon(month- 0 + 1) + finalSeparator + year;
		}
		if(formatTemp == 'MM/DD/YY' || formatTemp == 'MM-DD-YY')
		{
			element[whichOne].value = '' + padout(month - 0 + 1) + finalSeparator + padout(day) + finalSeparator + yearTemp.substr(2);
		}

		if(formatTemp == 'MM/DD/YYYY' || formatTemp == 'MM-DD-YYYY')
		{
			element[whichOne].value = '' + padout(month - 0 + 1) + finalSeparator + padout(day) + finalSeparator + year;
		}
		
	}
	mywindow.close();
}

function newWindow(str) {
	
	var	width		=	350;
	var	height		=	270;
	var	top			=	(screen.availHeight - height) / 2;
	var	left		=	(screen.availWidth  - width)  / 2;
	//var myOptions =	'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';

    whichOne = str;
    day = today.getDate(), month = today.getMonth(), year = y2k(today.getYear());
	//alert('in newWindow '+year);
	var myOptions =	'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';						
    
	mywindow=open('html/ETSCalendar.html','myname', myOptions);
 
    mywindow.location.href = 'html/ETSCalendar.html';

    if (mywindow.opener == null) 
		mywindow.opener = self;
}

function newWindow1(str,ind) 
{
		whichOne = str;
		index=ind;
		day = today.getDate(), month = today.getMonth(), year = y2k(today.getYear());
		mywindow=open('calendar.html','myname','resizable=no,width=350,height=250');
		mywindow.location.href = 'html/ETSCalendar.html';
		if (mywindow.opener == null)
			mywindow.opener = self;
}


function nullChecker(form)
{	
	var formatTemp = '<%=format%>'	
	
	var fvalue = form.fromDate.value;
	var tvalue = form.toDate.value;
	
	
	  var flag=true;
	
     flag=dtCheck(form.fromDate);
	 if( flag==false)
	 {
  	   alert("The 'Date' field cannot be left blank.");
	   return false;
     }

	 flag1=dtCheck(form.toDate);
	 if(flag1==false)
	 {
	    alert("The 'Date' field cannot be left blank.");
		 return false;
     }

	if(formatTemp == 'DD/MM/YY' || formatTemp == 'DD-MM-YY' || formatTemp == 'MM/DD/YY' || formatTemp == 'MM-DD-YY' ||formatTemp == 'DD/MON/YY' || formatTemp == 'DD-MON-YY')
  { 
     //("in if loop fromdate"+hiddenFromDate+"todate:::"+hiddenToDate) 
     
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

	if(!chkFutureDate(form.toDate,"lt"))
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
		
		//("newly opened window's name = '"+windowName+"'\n"+
		//		"opened window's frames[1]'s name = '"+repWin.frames[1].name+"'");
		
		//repWin.frames[1].name	=	"REPORTFRAME_"+millisecs;

		//("new window name = "+repWin.frames[1].name);

		//formObj.target	=	repWin.frames[1].name;
		
		repWin.opener = self;
			repWin.focus();
				
		return true;
	} else {
		return false;	
	}
}
</script>