<script language="javascript">
/* this is JavaScript file for validating the input values for an user interfaceasdf */

function validateForm(form)
{
	var value = form.chargeid.value;
	var acctid = form.acctid.value;
	var acctname = form.acctname.value;
	if(value=="")
	{
		alert("The 'Charge Id' field cannot be left blank.");
		form.chargeid.focus();
		return false;
	}
	else if(acctid=="")
	{
		alert("The 'Account Id' field cannot be left blank.");
		return false;
	}
	else if(isNaN(acctid))
	{
		alert("Please enter a numeric value for the 'Account Id'.");
		return false;
	}	
	else if(acctname=="")
	{
		alert("The 'Account Name' field cannot be left blank.");
		return false;
	}	
	
}
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
	if (intYear % 100 == 0) 
		if (intYear % 400 == 0) 
				return true; 
	else if ((intYear % 4) == 0)
			return true; 

	return false;
}

function dtCheck(dtobj)
{
	edt = dtobj.value;
	//alert("dtobj name = "+dtobj.name+"\ndtobj value = "+dtobj.value);
	
	if(edt=="") {
		return false;
	}				
	if(edt.length>10) {
		alert("The Date format is invalid. Please enter the date in 'DD/MM/YY[YY]' format");
		dtobj.focus();
		flag=false;
		return false;
	}
	seps = "/";
	seph = "-";
	if( (edt.indexOf(seps)== -1) && (edt.indexOf(seph)== -1)) {
		alert("The Date format is invalid. Please enter the date in 'DD/MM/YY[YY]' format");
		dtobj.focus();
		return false;	
	}
	
	fpos = edt.indexOf(seps);
	
	if(fpos==-1)
		fpos = edt.indexOf(seph);

	spos = edt.indexOf(seps,fpos+1);
	
	if(spos==-1)
		spos = edt.indexOf(seph,fpos+1);

	day = edt.substr(0,fpos);
	mon = edt.substr(fpos+1,(spos-fpos-1));
	year = edt.substr(spos+1);	
	
	if(year.length==0) {
		alert("Please enter the 'year' of the date.");
		dtobj.focus();
		return false;
	}
	
	if(year.length==3) {
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
				alert("There cannot be more than 29 days in a leap year.");
				dtobj.focus();
				return false;
			}
		} else  {
			if (day > 28)  {
				alert("There cannot be more than 28 days in a year other than a leap year.");
				dtobj.focus();
				return false;
			}
		}
	 }

	day = padout1(day);
	mon = padout1(mon);

	s = day+"/"+mon+"/"+year ;

	dtobj.value=s ;
	
	return true;

	//	alert(dtobj.value  +   "is a valid date") ;
	
} // end dtCheck


///////
function chkFutureDate(obj,type)
{

	var startdate = obj.value;

	//stdate , one is entered

	 var	stday = startdate.substr(0,2);
	 var 	stmon = startdate.substr(3,2);
	 var 	styear = startdate.substr(6,4);
	
	var CRDate = document.forms[0].hiddenCRDate.value;
	//end date is  current date

	enddate = CRDate;
	day = enddate.substr(0,2);
	month = enddate.substr(3,2);
	year = enddate.substr(6,4);

	// alert("end date "+ endday+ "/" +endmon+"/"+endyear);

	//for future date

	if(type=="gt")
	{
		daysdiff =  Date.UTC(styear,stmon-1,stday)-Date.UTC(year,month-1,day) ;
		daysleft = daysdiff/1000/60/60/24;

		// alert(" days diff"+daysleft);
	
		if(daysleft>=0)
		{
			// alert("valid future date");
			return true;	
		}
		else
		{
			// alert("Not a future  date");	
			return false;
		}
	}

	//for past date

	if(type=="lt")
	{
		daysdiff =  Date.UTC(year,month-1,day) -Date.UTC(styear,stmon-1,stday);
		daysleft = daysdiff/1000/60/60/24;

		// alert(" days diff"+daysleft);
	
		if(daysleft>=0)
		{	
			// alert("valid past date");
			return true;
		}

		else
		{
			// alert("To Date should not be greater than Current Date");	
			return false;
		}
	}

	return true;
}




function chkFromToDate(fromdate,CRDate)
{
	startdate = fromdate;
	enddate = CRDate;

	//stdate   is from date 

	stday = startdate.substr(0,2);
	stmon = startdate.substr(3,2);
	styear = startdate.substr(6,4);
	

	//end date is  to  date

	day = enddate.substr(0,2);
	month = enddate.substr(3,2);
	year = enddate.substr(6,4);

	//	alert("end date "+ endday+ "/" +endmon+"/"+endyear);
	
	daysdiff =  Date.UTC(year,month-1,day) -Date.UTC(styear,stmon-1,stday);
	daysleft = daysdiff/1000/60/60/24;

		//alert(" days diff"+daysleft);
	
	if(daysleft>=0)
	{
		//	alert("valid from & to dates ");
		return true;	
	}
	else
	{
		//	alert("To date should be greater than from date");	
		return false;
	}

	return true;
}


//////////////////////////////// timeDifference(startdate) /////////////////////////

function timeDifference(startdate) 
{
	
	//startdate , one is entered   is Voucher date Entered by the user
	var		voucherDay			=	startdate.substr(0,2);
	var		voucherMonth		=	startdate.substr(3,2);
	var		voucherYear			=	startdate.substr(6,4);
	
	var		milliSecsInOneDay	=	86400000; // These are the number of millseconds in a day

	// daysleft ie diff between current date and voucher date
	daysDiff			=	 Date.UTC(currentYear,currentMonth,currentDay) - Date.UTC(voucherYear,voucherMonth-1,voucherDay);
	daysLeft			=	 daysDiff / milliSecsInOneDay;

	// difference between voucher date and books beginning from
	bbDaysDiff		 =	    Date.UTC(voucherYear,voucherMonth-1,voucherDay) - Date.UTC(booksBeginingYear,booksBeginingMonth,booksBeginingDay);
	bbDaysLeft		 =		bbDaysDiff / milliSecsInOneDay;

	// if the books were Provosionally closed then we can allow
	// the user to enter the vouchers b/w booskbegining date and 
	// financial year to date.
	
	if(varBooksStatus	==	"P") {
		var		fyDaysleft		=	0 , fyDaysDiff	=	0 ;
		
		if(financialYear != 0  && financialDay != 0) {
			fyDaysDiff	=	Date.UTC( voucherYear, voucherMonth-1, voucherDay) - Date.UTC(financialYear,financialMonth,financialDay) ;
			fyDaysleft	=	fyDaysDiff / milliSecsInOneDay;
		
			//	if Finanacial Year To  Date exists then   daysleft  is  diff between Financial year to Date and voucher date
			daysDiff	=	 Date.UTC(financialYear,financialMonth,financialDay)  - Date.UTC(voucherYear,voucherMonth-1,voucherDay);
			daysLeft	=	 daysDiff / milliSecsInOneDay;
		}	
		
		//	fyDaysleft > 0 means the diff. b/w voucher date is bigger than financial year date
		if(fyDaysleft > 0 ) {
			alert(	"The current Book is provisionally closed.\nThe 'Transaction Date' should be between the "+
					"Book's starting date '"+ booksBeginingDay+"/"+(booksBeginingMonth+1)+"/"+booksBeginingYear +"' and the "+
					"Book's closing date '"+financialDay +"/"+(financialMonth+1)+"/"+financialYear +"'.");
			return false;
		}
	}
		 
	// check for the  days diff. b/w  booksbegining and voucher date
	
	if(bbDaysLeft < 0) {
		alert("The 'Transaction Date' cannot be less than the Book's starting date.")
		return false;
	}

	// If either one of the backdays or backdate limits are available, use them
	// If not, then restrict the User to the Current date and don't allow him to enter
	// any other date other than the current date.
		
	if(backDays <= 0 && cutOffYear <= 0  && cutOffday <= 0) {
			
		// If no limits for backdays or backdate are there,
		// restrict the User to the current date only
		
		if(daysLeft > 0) {
			alert(	"The 'Transaction Date' should be equal to the Current Date."+"\n\n"+
					"The limits of Back Days and the Cut-off Date for Back-dated Transactions "+"\n"+
					"are not specified in the Company Information for this Book. "+"\n"+
					"For entering Back-dated Transactions, one of them should be specified."
					);
			return false;
		}

	} else {
		
		var cutOffDays = 0;
		
		if(cutOffYear > 0 && cutOffday > 0) {
			cutOffDaysDiff	=	Date.UTC(currentYear,currentMonth,currentDay) -Date.UTC(cutOffYear,cutOffMonth,cutOffday);
			cutOffDays		=	cutOffDaysDiff / milliSecsInOneDay;
		}
		
		// If either of them are there check the limits
		// If the date is within the limit of current date

		if(daysLeft >= 0) {
			
			var checkForBackDays   = false;
			var checkForCutoffDate = false;
			
			if(cutOffDays > 0) {
				checkForCutoffDate = true;
			}
			if(backDays > 0) {
				checkForBackDays = true;
			}
			
			if(backDays > 0 && cutOffDays > 0) {
				if(backDays != cutOffDays) {
					// If both are not equal check for only one, which ever is less
					if(backDays < cutOffDays) {
						// ceck for backdays
						checkForBackDays = true;
						checkForCutoffDate = false;
					} else {
						// check for cut off date
						checkForBackDays = false;;
						checkForCutoffDate = true;	
					}
				} else {
					// If both are equal check for only back days
					checkForCutoffDate = true;
					checkForBackDays = false;
				}
			}
			
			// We should check for the back days or the back date
			// which ever is less.
			
			if(checkForBackDays==true) {
				if(daysLeft > backDays) {
					alert("The 'Transaction Date' is exceeding the Back Days ("+backDays+") limit.");
					return false;
				}
			}
			
			// Check the CuT-off date ony if a valid cut-ff-date is there
				
			if(checkForCutoffDate==true) {
				if(daysLeft > cutOffDays) {
					alert("The 'Transaction Date' is before the 'Cutoff Date' ("+ cutOffday+"/"+(cutOffMonth+1)+"/"+cutOffYear +").");
					return false;
				}
			}
		
		} else {
			alert("The 'Transaction Date' should be less than or equal to the Current Date.")
			return false;
		}
	}
	
	return true;
}


function y2k(number)    { return (number < 1000) ? number + 1900 : number; }
function padout(number) { return (number < 10) ? '0' + number : number; }

var today = new Date();
var day = today.getDate(), month = today.getMonth(), year = y2k(today.getYear()), whichOne = 0;

function restart() 
{
	var  element = document.forms[0].elements;
	element[whichOne].value = '' + padout(day) + '/' + padout(month - 0 + 1) + '/' + year;
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
	var myOptions =	'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';						
    
    mywindow=open('EACalender.html','myname', myOptions);
    
    mywindow.location.href = 'EACalender.html';

    if (mywindow.opener == null) 
		mywindow.opener = self;
}


function nullCheck( form )
{ 
  
	var fvalue = form.asondate.value;

	var BBDate = form.hiddenBBDate.value;
	var FYDate = form.hiddenFYDate.value;
	var CRDate = form.hiddenCRDate.value;

		
	var flag=true;
	
    flag=dtCheck(form.asondate);
	if( flag==false)
	{
	   alert("The 'Date' field cannot be left blank.");
	   return false;
    }
	
	 
		//for checking from dates validation
		//if (!chkFromToDate( BBDate, form.asondate.value))
		if (!chkFromToDate(BBDate,fvalue))
		{
			alert("The 'As On Date' should be greater than or equal to the Books Beginning Date '"+BBDate+"'." );
			return false;
		}
		if(FYDate!= null && FYDate.lenght > 0)
		{		
			if(!chkFutureDate(form.asondate,"lt"))
			{
				alert("The 'As On Date' should not be greater than the Current Date '"+CRDate+"'." );
				return false;
			}
		}	
	   
	
	if(FYDate !=null && FYDate.length>0)
	{
	
	
			if(!chkFromToDate(fvalue , FYDate))
			{	
				
				alert("The 'As On Date' should be less than or equal to the Books Closing Date '"+FYDate+"'." );
				return false;
								
			}

	}	
	else if (!chkFutureDate(form.asondate,"lt"))
	{
		
			alert("The 'As On Date' should be less than the Current Date '"+CRDate+"'.");
			return false;
	}	
    
		return true;
    
}

 

function nullChecker(form)
{	
	var fvalue = form.fromdate.value;
	var tvalue = form.todate.value;

	var BBDate = document.forms[0].hiddenBBDate.value;
	var FYDate = document.forms[0].hiddenFYDate.value;
    var CRDate = document.forms[0].hiddenCRDate.value;
  
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
	 
	 //if(!chkFromToDate(form.fromdate.value, form.todate.value))
	 if(!chkFromToDate(fvalue,tvalue))
	 {
			alert("The 'From' date should be less than or equal to the 'To' date.");
			return false;
     }
		
		//for checking from dates validation
		if (!chkFromToDate(BBDate,fvalue))
		{
			alert("The 'From' date should be greater than or equal to the Books Beginning Date '"+BBDate+"'.");		
			return false;	
		}	
		if(!chkFutureDate(form.fromdate,"lt"))
		{
			alert("The 'From' date should be less than the Current Date '" +CRDate+"'." );
			return false;
		}
		//for checking to dates validation
		if(FYDate !=null && FYDate.length>0)
		{
			if (!chkFromToDate(tvalue,FYDate))
			{
				alert("The 'To' date should be less than or equal to the Books Closing Date '"+FYDate+"'.");
				return false;	
			}
				
		}
		if(!chkFutureDate(form.todate,"lt"))
		{
			alert("The 'To' date should be less than the Current Date '" +CRDate+"'." );
			return false;
		}
		
		
		return true;
		
		/*
		else if(!chkFutureDate(form.todate,"lt"))
		{
			alert("The 'To' date should be less than the Current Date '" +CRDate+"'." );
			return false;
		}
		else
		{
			return true;
		}
		*/
}

   
function nullChecking(form)
{ 
   
   var BBDate = form.hiddenBBDate.value;
   var FYDate = form.hiddenFYDate.value;
   var CRDate = form.hiddenCRDate.value;
   
   var avalue = form.asondate.value;
   var evalue = form.elements.length;	
   
   
   
   flag=dtCheck(form.asondate);
   if( flag==false)
   {
	   alert("The 'Date' field cannot be left blank.");
	   return false;
   }

   for(j=0; j < evalue; j++)
		{
				if(form.elements[j].name == "marginFrom")
 				{
					if(form.elements[j].value.length == 0 )
					{
						alert("Please enter a numeric value for 'Margin From'." );
						return false;
					}	
				}
				if(form.elements[j].name == "marginTo")
				{
					if(form.elements[j].value.length == 0 )
					{
						alert("Please enter a numeric value for 'Margin To'.");  
						return false;
					}	

				}
				if(form.elements[j].name == "asondate")
 				{
					if(form.elements[j].value.length == 0 )
					{
						alert("Please enter a 'As On Date'." );
						return false;
					}	
				}
		}

	
	    //for checking from dates validation
		if (!chkFromToDate(BBDate,avalue))
		{
			alert("The 'As On Date' should be greater than or equal to the Books Beginning Date '"+BBDate+"'.");
			return false;	
		}	
		if(FYDate!= null && FYDate.lenght > 0)
		{	
			if(!chkFutureDate(form.asondate,"lt"))
			{
				alert("The 'As On Date' should be less than the Current Date '"+CRDate+"'." );
				return false;
			}
		}	
	   
	
	if(FYDate !=null && FYDate.length>0)
	{
	
	
			if(!chkFromToDate(avalue , FYDate))
			{	
				
				alert("The 'As On Date' should be less than or equal to the Books Closing Date '"+FYDate+"'." );
				return false;
								
			}

	}	
	else if (!chkFutureDate(form.asondate,"lt"))
	{
		
			alert("The 'As On Date' should be less than the Current Date '"+CRDate+"'." );
			return false;
	}	
    
		return true;
    
}

////////////////////////////// removeSpaces(string) /////////////////////////////////

function removeSpaces(strVoucherType) {
		var temp = "";
		var character="";
		for(var i=0; i < strVoucherType.length; i++) {
			character = strVoucherType.substring(i,i+1);
			// If its not a space use it.
			if(character != " ") {
				temp = temp + character;
			}
		}
		strVoucherType = temp;
		//alert("strVoucherType = '"+strVoucherType+"'");
		return strVoucherType;

} // end removeSpaces()

//********************** MONEY FUNCTIONS *********************

//////////////////////////////// outputMoney(number,obj) /////////////////////////
	
function outputMoney( number, obj)  {
	
	var number = number+"";

	if(number.length > 0) {

		if(!numbersOnly(number,obj)) {
			if(obj.type.toLowerCase() == "text") {
				obj.focus();
				obj.select();
			}
			return false;
		}	

		if(number!=null && number.length > 0) {
			
			var isNegative	=	false;
			
			if(number.substring(0,1) == "-") {
				isNegative = true;
				number = number.substring(1, number.length);
			}
			
			var lengthOfNum = number.length;
			
			if(lengthOfNum >= 12) {
				//Decimal position
				var desPosition	= number.indexOf(".") ;
		
				if(obj.type.toLowerCase() == "text") {
					if(desPosition>12||(desPosition==-1&&lengthOfNum>12)) {
						alert("Please enter up to 15 digits including decimal.");
						obj.focus();
						return false;
					}
				}
			}
			if(!isNaN(number) ) {
			    
			    str =  outputDollars(Math.floor(number-0) + '') + outputCents(number - 0);
				
				if(isNegative) {
					str	=	"-" + str;	
				}
		    	obj.value = str;
				return true;

			} else {
				
				obj.value = "0.00";
				
				if(obj.type.toLowerCase() == "text") {
					obj.focus();
					obj.select();
				}
				return false;
			}
		}
	} else {
		obj.value = "0.00";	
	}
	return true;
}

//////////////////////////////// outputDollars(number) /////////////////////////

function outputDollars(number) {
    if (number.length <= 3) {
        return (number == '' ? '0' : number);
	} else {
        var	mod	  =	 number.length%3;
        var output	  =	(mod == 0 ? '' : (number.substring(0,mod)));
        for (i=0 ; i < Math.floor(number.length/3) ; i++) 
		{
            if ((mod ==0) && (i ==0))
                output+= number.substring(mod+3*i,mod+3*i+3);
            else
                output+= ',' + number.substring(mod+3*i,mod+3*i+3);
        }
        return (output);
    }
}

//////////////////////////////// outputCents(amount) /////////////////////////

function outputCents(amount)  {
    amount = Math.round( ( (amount) - Math.floor(amount) ) *100);
    return (amount < 10 ? '.0' + amount : '.' + amount);
}

//////////////////////////////// removeOnlyCommas(val) /////////////////////////

function removeOnlyCommas(val) {

	var temp = "";
	for(var i=0; i < val.length; i++) {
		var char = val.substring(i,i+1);
		if(char != ",") {
			temp = temp + char;
		}
	}
	//alert("temp = "+temp);
	return temp;
}

//////////////////////////////// removeComma(val) /////////////////////////

function removeComma(val) {

	var filter		=	 ",";
	var result		=	 "";
	var ch			=	 "";
				
	// Filter Out all the commas
	for(var i=0; i < val.length;i++) {
		ch	=	 val.charAt(i);
		if(filter.indexOf(ch) == -1) {
			result = result + ch;
		} // end if
	} // end for
	
	val = result;
	
	return val;
	
}

//////////////////////////////// removeComma(val, obj /////////////////////////

function removeComma(val, obj) {

	if(val!=null && val.length > 0) {
	 	num	=	removeDollarCents(val);	
		if(!isNaN(num)) {
			
			var numb = parseFloat( num );
			
			if(numb > 0) {
				obj.value = num;
				if(obj.type="text") {
					obj.select();
				}	
			} else {
				obj.value = "";
			}
		} else  {
			//alert("Not a valid number");
			obj.value = "";
			obj.focus();
		}
	}
}

//////////////////////////////// removeDollarCents(str) /////////////////////////

function removeDollarCents(str) {

	//removes commas from the numeric value
	filter		=	 ",";
	result		=	 "";
	ch			=	 "";
	for(i=0;i<str.length;i++) {
		ch	=	 str.charAt(i);
		if(filter.indexOf(ch) == -1) {
			result = result+ch;
		}
	}	
	return result;
}

//////////////////////////////// numbersOnly(number,obj) /////////////////////////

function numbersOnly(number,obj) {
	len =  trim(number) ;
	if(isNaN(number)  == true  ) {
		if(number.indexOf(",")!=-1) {
			removeComma(number, obj);
		} 
	}
	return true;
}

//////////////////////////////// trim(strText) ////////////////////////////

function trim(strText) { 
	/*  removes empty spaces from wither side of a javascript string */
 
    while (strText.substring(0,1) == ' ') {
   	    strText = strText.substring(1, strText.length);
   	}

    while (strText.substring(strText.length-1,strText.length) == ' ') {
   	    strText = strText.substring(0, strText.length-1);
   	}

	return strText;
} 


//////////////// ********* create_date_object(datestring) ********* ///////////////

function create_date_object(datestr)
{
	// Date	: 10.01.2001
	// Author	: Amit Parekh
	// Caller	: any function that wants to create a date object from a date string
	//			  in the 'dd/mm/yy' or 'dd.mm.yy' or 'dd-mm-yy' or
	//			  'dd/mm/yyyy' or 'dd-mm-yyyy' or 'dd.mm.yyyy' format.

	/*	FUNCTIONALITY :
		This function takes a date string as an argument and returns a date object
		created using the values in that date string.
	*/

	// Sample Date -----------------------09/01/2001
	// String indexes of the sample date--0123456789

	var dd = parseInt(datestr.substr(0,2), 10);
	var mm = parseInt(datestr.substr(3,2), 10);
  
	var yearString	=	datestr.substr(6);
	var yyyy	=	0;
	
	//alert("yearString = "+yearString);
	
	// If date was in DD/MM/YY format
	if(yearString.length >= 1 && yearString.length <= 4) {
		
		yyyy = parseInt( yearString , 10 );
		
		if(yyyy >= 0 && yyyy <= 999) { 
			yyyy = 2000 + yyyy;
		} else if(yyyy > 999 && yyyy < 1970) {
			yyyy = 1970;
		}
	}

	var date_object = new Date(yyyy, mm-1, dd)
 
	return date_object;
   
} // end function create_date_object(datestring)

///////////////////////////////// writeApprovalDate() /////////////////////////////

function writeApprovalDate() {
	
	/* if the approved check box is checked  approval date will be appeared in approval date field
		by default it is sys date
	*/
	if(document.forms[0].approved.checked) {	
		document.forms[0].approved.value = "Y"
		document.forms[0].approvedDate.value = document.forms[0].hiddenCRDate.value;
		// "utilitybean.getCurrentDateInDDMMYYYY(";
	} else {
		document.forms[0].approved.value = "N"
		document.forms[0].approvedDate.value="";
	}
	
	//alert("app date = "+document.forms[0].hiddenCRDate.value+"\napprove value = "+document.forms[0].approved.value );

}


//////////////////////////////// collapseSpaces( obj ) ////////////////////////////

function collapseSpaces( obj ) {

	var str	=	obj.value;
	var newString	=	"";

	
	for(var i=0; i < str.length; i++) {
		
		char = str.substring( i, i+1);
		
		//var oneSpaceAttached = false;

		if(char == " ") {
			// Check next character
			
			var nextChar = str.substring( i+1, i+2);
			
			if(nextChar != " ") {
				newString += char;
			}
			
			//else {
			//	if(!oneSpaceAttached) {
			//		oneSpaceAttached = true;
			//	}
			//}
		} else {
			newString += char;
			//oneSpaceAttached = false;
		}

	} // end for
	
	obj.value = newString;
	
}

////////////////////////// removeAllSpaces( obj ) ////////////////////////////

function removeAllSpaces( obj ) {

	var str	=	obj.value;
	var newString	=	"";
	
	for(var i=0; i < str.length; i++) {
		char = str.substring( i, i+1);
		if(char != " ") {
			newString += char;
			//oneSpaceAttached = false;
		}
	} // end for
	obj.value = newString;
	
}


///////////////////// checkForSpecialCharacters() ////////////////////

function checkForSpecialCharacters(object, str, strObjectName) {

	//alert("in checker function");
	val = object.value;
	object.value = trim(val);
	spChar = false;
	
	for(var i=0; i < val.length; i++) {
		var char = val.substring(i, i+1);
		if(  char=="~"  || char=="!"  || char=="@"  || char=="#" || char=="$" || char=="%" ||
		     char=="^"  || char=="&" || char=="*"  || char=="_"  || char=="-" || char=="=" || char=="+" || 
		     char=="("  || char==")" || char=="{"  || char=="}"  || char=="[" || char=="]" || 
		     char=="\\" || char=="|" || 
		     char==":"  || char==";" || char=="\"" || char=="'" || 
		     char=="<"  || char==">" || char==","  || char=="?"  || char=="/"
		            ) {
			
			alert("Please donot enter Special Characters in '"+strObjectName+"'.");
		    spChar = true;
			break;
		} 
	} // end for
	if(spChar == true) {
		object.value = "";
		object.focus();
		return false;
	} else {
		return true;	
	}
} // end checkForSpecialCharacters()

///////////////////// getGenericTransactionTypeName( genType ) ////////////////////
	
function getGenericTransactionTypeName( genType ) {
	
	var genericType	=	"";
	
	if(genType == "SL") {
		genericType	=	"SALES";
	}
	if(genType == "JR") {
		genericType	=	"JOURNAL";
	}
	if(genType == "BP") {
		genericType	=	"BANK PAYMENT";
	}
	if(genType == "CP") {
		genericType	=	"CASH PAYMENT";
	}
	if(genType == "BR") {
		genericType	=	"BANK RECEIPT";
	}
	if(genType == "CR") {
		genericType	=	"CASH RECEIPT";
	}
	if(genType == "DN") {
		genericType	=	"DEBIT NOTE";
	}
	if(genType == "CN") {
		genericType	=	"CREDIT NOTE";
	}
	if(genType == "CO") {
		genericType	=	"CONTRA";
	}
	
	return genericType;
}

////////////////////////// submission( flag ) ///////////////////////////

function submission( flag ) {
	if(flag==false) {
		document.forms[0].submit.disabled = true;
	}
	if(flag==true) {
		document.forms[0].submit.disabled = false;
	}
	// submission( false );
	// submission( true );
}

////////////////////////// showReport( flag ) ///////////////////////////

var windowFeatures 	= 	"directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no,scrollbars=yes,resizable=yes";

function showReport(flag, formObj) {

	if(flag==true) {
		
		var millisecs	=	(new Date()).getTime();

		var repWin = open("ESupplyReportPage.jsp", "REPORTWINDOW_"+millisecs, windowFeatures);
		window.repWin;

		while(repWin.frames.length < 2) {}
		
		//alert("newly opened window's name = '"+windowName+"'\n"+
		//		"opened window's frames[1]'s name = '"+repWin.frames[1].name+"'");
		
		repWin.frames[1].name	=	"REPORTFRAME_"+millisecs;

		//alert("new window name = "+repWin.frames[1].name);

		formObj.target	=	repWin.frames[1].name;
		
		repWin.opener = self;
			repWin.focus();
				
		return true;
	} else {
		return false;	
	}
}

</script>