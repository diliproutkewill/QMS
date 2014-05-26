/* this is JavaScript file for validating the input values for an user interface*/
function chkFromToDate(fromdate,todate)
{
	startdate = fromdate;
	enddate = todate;

//stdate   is from date 

	stday = startdate.substr(0,2);
	stmon = startdate.substr(3,2);
	styear = startdate.substr(6,4);

//end date is  to  date

	endday = enddate.substr(0,2);
	endmon = enddate.substr(3,2);
	endyear = enddate.substr(6,4);

    daysdiff =  Date.UTC(endyear,endmon-1,endday) -Date.UTC(styear,stmon-1,stday);
	daysleft = daysdiff/1000/60/60/24;

	if(daysleft>=0)
		return true;	
	else
		return false;
 return true;
}
function appendYear(num) {
	var strNumber = ""+num;
	var number = parseInt(strNumber, 10);
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
	return number 
}
	 	
function dtCheckToAppend(dtobj)
{

	edt = dtobj.value;
	if(edt=="")
	{
		return false;
	}				
	if(edt.length>10)
	{		
		flag=false;
		return false;
	}
	seps = "/";
	seph = "-";
	if( (edt.indexOf(seps)== -1) && (edt.indexOf(seph)== -1))
	{		
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
	if(year.length==3)
	{		
		return false;
	}
	year = appendYear(year);
	day = appendZero(day);
	mon = appendZero(mon);
	s = day+"/"+mon+"/"+year ;
	dtobj.value=s ;

}
function appendZero(number)  {

	if (number >= '01' && number<= '09' ) {
		return number;
	}
	else if (number >= 1 && number<= 9 ) {
		number =  '0' + number ;
	}

	return number 

}
var reason = '';
function isValidDate(myDate,sep)
{
  len=myDate.length;
  if(len > 0)
  {
       if (myDate.length <= 10)
       {
          fIdx = myDate.indexOf(sep);
           sIdx = myDate.indexOf(sep,fIdx+1);
           if ( fIdx!=-1 && sIdx!=-1 )
           {
                var date  = myDate.substring(0,fIdx);
                var month = myDate.substring(fIdx+1,sIdx);
                var year  = myDate.substring(sIdx+1);
                if(date.length==0 || month.length==0 || year.length==0)
                  return false;
                var test = new Date(year,month-1,date);
                if(year=='00')
                  year=eval(year)+2000;
                if ((month-1 == test.getMonth()) && (date == test.getDate()))
                {
                    reason = '';
                    return true;
                }
                else
                return false;
            }
            else
            return false;
       }
       else
         return false;
   }
}

function tellMeIfInvalid(myDate,obj,label)
{
    var name1      =    obj.name.substr(0,1).toUpperCase();
    var name2      =    obj.name.substr(1).toLowerCase();
    var newName =    name1+name2;

    if(myDate.length > 0 || myDate != "" || myDate != " ")
    {
        if (!(isValidDate(myDate,'/')))
        {
          alert("Please enter correct "+label);
          obj.focus();
    	  obj.select();
          return false;
        }
        else
          return true;
    }
}

function isValidTime(input,label)
{
    timeStr=input.value
    var flag= true;
    if(timeStr.length!=0)
    {
        var timePat = /^(\d{1,2}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;

        var matchArray = timeStr.match(timePat);
        if (matchArray == null)
            flag= false;

        else if (matchArray != null)
        {
            hour = matchArray[1];
            minute = matchArray[2];
            second = matchArray[4];
            ampm = matchArray[6];

            if (second=="") { second = null; }
            if (ampm=="") { ampm = null }

            if (hour < 0  || hour > 23)
                flag= false
            else if (minute<0 || minute > 59)
                flag= false
            else if (second != null && (second < 0 || second > 59))
                flag= false
            else
             flag= true
        }
        else
             flag= true
     }
    if(!flag)
    {
          alert("Please enter correct "+label );
          input.focus();
     	  input.select();
          return flag;
    }
    return flag;
}
function checkDates(dateObj ,dateLabel ,timeObj ,timeLabel)
{
	if(dateObj.value.length>0 || timeObj.value.length>0)
		return (isValidTime(timeObj,timeLabel) && 	 
		tellMeIfInvalid(dateObj.value,dateObj,dateLabel) )
}
function checkDates(dateObj ,dateLabel )
{
	if(dateObj.value.length>0 )
		return 	tellMeIfInvalid(dateObj.value,dateObj,dateLabel) 
}
function y2k(number)    
{ 
	return( number < 1000) ? number + 1900 : number;
}
	

	function newWindow(str)
   {
	    whichOne = str;
		var today = new Date();
	    day = today.getDate(), month = today.getMonth(), year = y2k(today.getYear());
	    mywindow=open('EAccountsCalender.html','myname','resizable=no,width=350,height=270');
	    mywindow.location.href = 'EAccountsCalender.html';
	    if (mywindow.opener == null) 
			mywindow.opener = self;
	}	