
	
	var req;
	var isIE;
	var names;
	var target;
	var targetName;
	var countryName;
	var rot_method;
	var rot_value=new Array();
	var refID;
	var copy='';
	var ref_value=new Array();
    var autorow
	var ids; //Stores all data from servlet
	var index=-1;	// tracks the list of values index
	var clearflag=1; //used to clear AJAX LOV when ever user clicks on page and not on LOV
	var count=0;
	var argcount=0;
	var input; //for checking the type from houseMoreInfo
	var flag;

	function showTableAutoComplete(url,targ,targName,countryIdCode,keyEvent,top,left)
	{
		flag=true;
		input=targ;
  		var EventObj;

        if(document.all){
            //thisbrowser="ie"
			 EventObj=event;  
			 isIE=true;
        }
	        if(!document.all && document.getElementById){
            //thisbrowser="NN6";
			EventObj=keyEvent;
        }
			if(EventObj.keyCode == 40){//DOWN ARROW
				if(ids!=null && names!=null &&  names.options.length >0){
					if(names.selectedIndex !=names.options.length-1)
  					   names.options[names.selectedIndex+1].selected = true;
				}
		}else if(EventObj.keyCode == 38){// UP ARROW
			if(ids!=null && names!=null &&  names.options.length >0){
					if(names.selectedIndex !=0)
						names.options[names.selectedIndex-1].selected = true;
				}
		}else if(EventObj.keyCode == 13){//ENTER
			setIdAutoComplete();
		}else{

			 if(EventObj.keyCode == 8 || EventObj.keyCode == 46){
			document.getElementById(targName).value="";//Added By Abhinay for Party Ajax Call Mode CR
			}
	
						
			target = document.getElementById(targ);
			targetName = document.getElementById(targName);
			if(countryIdCode && countryIdCode!='')
			countryName = document.getElementById(countryIdCode);
			autorow = document.getElementById("menu-popup");
			setAutoRowAutoComplete();
			req=null;
			initRequestAutoComplete();
			req.open("GET", url, false);
			req.onreadystatechange = callbackAutoComplete;
			req.send(null);
		}
		
	}
	function setAutoRowAutoComplete()
	{
		autorow.style.top = getElementYAutoComplete(target) + "px";
                //@ Added By Abhi For AJAX Party Type CR
		left=getElementXAutoComplete(target);
		autorow.style.left = getElementXAutoComplete(target) + "px";
		if(document.body.clientWidth<(left+250)){
			autorow.style.left=left-(250-target.offsetWidth);
		}
		
	}
	function getElementYAutoComplete(temp)
	{
		  objTop = 0;
		  objLeft = 0;
		   while (temp.offsetParent)
		  {
			objTop += temp.offsetTop;
			objLeft += temp.offsetLeft;
			temp = temp.offsetParent
		  }
		  objTop+=20;
		return objTop;
	}

	function getElementXAutoComplete(temp)
	{
		  objTop = 0;
		  objLeft = 0;
		    while (temp.offsetParent)
		  {
			objTop += temp.offsetTop;
			objLeft += temp.offsetLeft;
			temp = temp.offsetParent
		  }
		  objTop+=temp.offsetHeight;
		return objLeft;
	}
	function initRequestAutoComplete() 
	{
		
		if (window.XMLHttpRequest) 
		{
			req = new XMLHttpRequest();
		}
		else if (window.ActiveXObject) 
		{
			isIE = true;
			req = new ActiveXObject("Microsoft.XMLHTTP");
		}
	}

	function callbackAutoComplete() 
	{   
		if (req.readyState == 4) 
		{  
			if (req.status == 200){
				clearTableAutoComplete();
				parseMessagesAutoComplete();
			}else// if (req.status == 204)
			{
				clearTableAutoComplete();
			}

			
		}
	}    
function trimStringAutoComplete(str) {
  while (str.charAt(0) == ' ' || str.charAt(0)=='\n')
    str = str.substring(1);
  while (str.charAt(str.length - 1) == ' ' || str.charAt(str.length - 1)=='\n')
    str = str.substring(0, str.length - 1);
  
  return str;
}

	function parseMessagesAutoComplete() 
	{
		var id;
  		if (!names) 
			names = document.getElementById("names");
		ids = req.responseXML.getElementsByTagName("ids")[0];	
		index=0;
	/*	if(ids==null)
		{
			if(flag==true){
		/alert("Please Enter Customer Id ");
			flag=false;
			}
			//if(document.forms[0].jspName && document.forms[0].jspName.value=="PIOrCostEntry"){
				target.value="";
				targetName.value="";
			//}
			 clearTableAutoComplete();
			 autorow.style.visibility='hidden';
			return;
		}*/ //@@Commented by kiran.v on 03/02/2012 for Wpbn Issue-
	if(index!==ids.childNodes.length)
		{
	clearTableAutoComplete();
	autorow.style.visibility='visible';
	var countIds=0;
	count=0;
	while(index<ids.childNodes.length ) 
			{
				 id = ids.childNodes[index];
				appendIdAutoComplete(id.childNodes[0].nodeValue);
				count++;
				index++;
				countIds++;
				
			}
                //@ Added By Abhi For AJAX Party Type CR
		names.style.width="250px";			
		}
	else		
		setAutoRowAutoComplete();

	}

	function dontClearAutoComplete()
	{
		clearflag=0;
	}
	function clearTableAutoComplete() 
	{
		if(clearflag==1)
		{
		if (names) {
		  for (loop = names.childNodes.length -1; loop >= 0 ; loop--) {
			names.remove(loop);
		  }
		}
		autorow!=null?autorow.style.visibility='hidden':""; //modified by kiran b for issueId:[11:32:13 AM] Kiran Gara: 266324
		}
		clearflag=1;		
	}

	function appendIdAutoComplete(id) //Adds item to list
	{
		
			names.options[names.options.length]=new Option(id.substring(0,id.indexOf(';')),id.substring(id.indexOf(';')+1));
	//		names.options[names.options.length]=new Option(id);
	}

	function keyOnSelectAutoComplete(selCode) 
	{

		var selKeyCode;
		if(isIE==true)
			selKeyCode=event.keyCode;
		else
			selKeyCode=selCode;
		if(selKeyCode==13)
			setTimeout('setId()',100);
		else if(selKeyCode==27)
		{
			clearTableAutoComplete();
			target.focus();
		}
	}



	function setIdAutoComplete()
	{	
		
		if(ids!=null && names.selectedIndex!=-1)
		{
			//target.value=names.options[names.selectedIndex].text;
			//target.value=names.options[names.selectedIndex].value;
			var temp = names.options[names.selectedIndex].text;
			target.value = temp.substring(0,temp.indexOf(';'));
			targetName.value =  names.options[names.selectedIndex].value;
			if(input == 'shipperIdPartyName'){
			  document.forms[0].shipperIdPartyName.focus();
			}

			//Added By Venkateswarlu Karnati for issue:239456 Start
						target.focus();
		}			

		clearTableAutoComplete();
	
	}
    


	 document.onmousedown = function(e)
  { 
     if(!e) { 
     e = window.event;
   }
    var element = (typeof e.srcElement == 'undefined') ? e.target : e.srcElement;
    // @@ Modified by khaja For known issue on 1/13/2011.
   if(typeof element.type=='undefined'/* && 'menu-popup'!=element.getAttribute("id")*/){ 
    if(event.button==1 || event.button==2 || event.button==3 || event.button==4) {
      clearTableAutoComplete();
    }      
   } 
  }
	// For Tab out for issue - E2E_179
	function keyDownFunction(e){

	  if(!e) {
		  e = window.event;
		}
		
		if(e.keyCode==9)
		   clearTableAutoComplete();
	}