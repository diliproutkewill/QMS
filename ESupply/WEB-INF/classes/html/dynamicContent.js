//Major modification done on Friday,11  July 2003 15:26:35  to implement element sliding
//Note : Functions and variables used in this file should not be used anywhere else.
function importXML(xmlFile)	//this method takes input of the XML file from the user and loads all the tags into a XMLDoc object
{
	//alert(" in JS File")
	if (document.implementation && document.implementation.createDocument)	//for netscape users
	{
		xmlDoc = document.implementation.createDocument("", "", null);
		xmlDoc.onload = initializeDynamicContentRows;
	}
	else if (window.ActiveXObject)	//for IE users
	{
		xmlDoc = new ActiveXObject("Microsoft.XMLDOM");
		xmlDoc.onreadystatechange = function () {if (xmlDoc.readyState == 4) initializeDynamicContentRows()};
    
 	}
	else	//for others
	{
		alert('Your browser can\'t handle this script');
		return;
	}
	xmlDoc.load(xmlFile);
	
	//if(document.all && xmlDoc.xml.length==0)
		//alert("Invalid XML File..1 "+xmlFile);
}
var dynamicContentRow;	//variable which stores the dynamic content of the XML
/*
	The most complex and the important method in this XML dynamic row functionality
	Limitations/Pre-requisites
		For LABEL,the "labelText" child node is the terminating point
		For TEXTAREA,the "value" child node is the terminating point
		For SELECT,the "option" child node is the terminating point,for other attributes
		The minimum requirement for a field to have the onKeyDown enabled is the "NAME" attribute,every field should have a name
		The element sliding up/down is by default enabled,if some element doesn't require it then they have to have attribute 'enableSliding" set to "false"
		For Select and Textareas the Sliding is possible through "CTRL" + UP/DOWN cusrsor keys
*/

function getDynamicContent(tagName,fieldIdCounter,parentTableId)	 //reads the XML data and creates the form elements
{
	//alert("hi::"+fieldIdCounter);
	var dynamicElements = xmlDoc.getElementsByTagName(tagName).item(0);
	var x = dynamicElements.getElementsByTagName("ChildElement");	 //get all the elements collection of the tag name specified
	var row = document.createElement('TR');	//create a new TR
	var elementType;	 //Stores the type of element like "Input" ,"Select", "Label", "TextArea"...etc
	var alignment;	//alignment of the TD
	var vAlignment;	//vertical alignment of the TD
	var isMerged				= false;	//indicates whether the element is merged with the previous element
	var startingElement		= false;	//indicates whether the element is the starting element of the TD
	var myElement				= "";	//Actual content of the TD is stored in this variable
	var fieldName				= "";	//Stores the name of the field,which is needed to generate ID for that field
	var enableSliding			= true;	//checks for enableSliding attribute presence
	try
	{
		if(x.length==0)	 //If the length of the elements collection is zero,it indicates the invalidity of the XML file or tha TAG name
		{
			alert("Invalid XML Tag "+tagName+"\nCheck XML File");
			return null;	//Return the control,no dynamic row will be created
		}
	}
	catch(e){alert("get dynamic contect ..in exception"); return null;}

	for (i=0;i<x.length;i++)	//Elements Collection Loop
	{ 
		var idCounterString = "";	//Variable which stores the ID part of the field
		elementType		= getAttributeValue(x[i],"elementType",null);	 //gets the "elementType" attribute
		isMerged		= getAttributeValue(x[i],"merged",false);	//get the "merged" attribute from the element
		startingElement	= getAttributeValue(x[i],"startingElement",false);	//gets the "startingElement" attribute
		className		= getAttributeValue(x[i],"class",null);	//gets the "class" attribute
		alignment		= getAttributeValue(x[i],"align","center");	//gets the "align" attribute
		vAlignment		= getAttributeValue(x[i],"valign","top");	//gets the "align" attribute
		enableSliding	= getAttributeValue(x[i],"enableSliding",true);	//gets the "enableSliding" attribute

		if(enableSliding!="false")
			enableSliding = true;
		else
			enableSliding = false;
		
		if(startingElement)	//if this is starting element,reset the myElement variable
			myElement = "";

		if(!isMerged)	//if the previous element is not merged then add the TD to the TR and reset the myElement
		{
			if(myTableCell!=null)
				row.appendChild(myTableCell);
			myElement = "";
		}
		var myTableCell = document.createElement('TD');	//Create a new TD
		if(className!=null)	//set className attribute only if specified
			myTableCell.className = className;
		myTableCell.align	 = alignment;	//set the alignment
		myTableCell.vAlign = vAlignment;	//set the vertical alignment
		var keyDownAttached = false;	//tracks whether the "onKeyDown" event is attached to the element or not

		if(x[i].childNodes.length>0)	//if the element has child nodes then only proceed further
		{
			myElement += "<"+elementType+" parentTableId='"+parentTableId+"' ";	//start creating a new element
			var tagClosed = false;	//useful to track whether the tag is closed for the elements like "SELECT","TEXTAREA"
			for(j=0;j<x[i].childNodes.length;j++)	//Loop for child nodes of the element
			{
				if(x[i].childNodes[j].nodeName.toUpperCase()=="NAME")	//if the child node attribute is "NAME",the track it as its useful for ID generation
					idCounterString = " id = "+x[i].childNodes[j].firstChild.nodeValue+""+fieldIdCounter+" ";	//append NAME+Counter and set it to ID
				if(idCounterString.length>0)
				{
					myElement += idCounterString;
					idCounterString = "";
				}
				if(x[i].childNodes[j].nodeType!= 1)	//skip the for loop if the nodeType is not "1"
					continue;
				if(x[i].childNodes[j].firstChild==null && elementType!="select" && elementType!="textarea")	//if the first child is null and element type is not "SELECT",the skip the loop
					continue;
				if(elementType=="label" && x[i].childNodes[j].nodeName=="labelText")	//if element type is "LABEL" and the nodename is "labelText" then the label tag is done
				{
					myElement += ">"+x[i].childNodes[j].firstChild.nodeValue+"</label>";
					break;
				}
				if(elementType=="textarea" && x[i].childNodes[j].nodeName=="value")	//for TEXTAREA the encounter of "VALUE" node terminates the element
				{
					if(!keyDownAttached && enableSliding)	//if key down event is not attached earlier then attach it with default event,for element movement
						myElement += " onKeyDown=\'trapDynamicContentRowKeyDown(event);\' ";
					if(x[i].childNodes[j].firstChild==null)
						myElement += "></textarea>";	//Close the tag
					else
						myElement += ">"+x[i].childNodes[j].firstChild.nodeValue+"</textarea>";	//Close the tag
					break;
				}
				if(elementType=="select" && x[i].childNodes[j].nodeName=="option")	 //if the element type is "SELECT" and the childNode name is options then enter the loop
				{
					if(!tagClosed)	//if the select tag is not closed earlier then close it now
					{
						tagClosed = true;	
						if(!keyDownAttached && enableSliding)	//check for key down and attach the default one and close the tag
						{
							myElement += " onKeyDown=\'trapDynamicContentRowKeyDown(event);\'> ";
						}
						else
						{
							myElement += ">";
						}
					}
					var internalText = "";	//used to store the options internal attributes and append to the select tag
					for(var i1=0;i1<x[i].childNodes[j].attributes.length;i1++)
					{
						internalText += x[i].childNodes[j].attributes[i1].name+"='"+x[i].childNodes[j].attributes[i1].value+"'";
					}
					if(x[i].childNodes[j].firstChild!=null)
						myElement += "<option "+internalText+">"+x[i].childNodes[j].firstChild.nodeValue+"</option> ";
					else
						myElement += "<option "+internalText+"></option> ";	//if empty child node then create a empty option
				}
				else
				{
					if(x[i].childNodes[j].nodeName.toUpperCase().indexOf("ONKEYDOWN")!=-1)	 //check for onKeyDown ,if present attach the user defined and default one
					{
						keyDownAttached = true;						
						if(x[i].childNodes[j].firstChild!=null)
						{
							if(enableSliding)
								myElement += x[i].childNodes[j].nodeName+"=\""+x[i].childNodes[j].firstChild.nodeValue+",trapDynamicContentRowKeyDown(event);\" ";
							else
								myElement += x[i].childNodes[j].nodeName+"=\""+x[i].childNodes[j].firstChild.nodeValue+"\" ";
						}
						else
						{
							if(enableSliding)
								myElement += x[i].childNodes[j].nodeName+"=\"trapDynamicContentRowKeyDown(event);\" ";
						}
					}
					else
					{
						//append any attribute specified in the XML file
						if(x[i].childNodes[j].firstChild!=null)
							myElement += x[i].childNodes[j].nodeName+"='"+x[i].childNodes[j].firstChild.nodeValue+"' ";
						else
							myElement += x[i].childNodes[j].nodeName+"='' ";
					}					
					if(x[i].childNodes[j].nodeName.toUpperCase()=="NAME")	//get the name of the element
						fieldName = x[i].childNodes[j].firstChild.nodeValue;
				}
			}
			if(elementType=="select")	//again check the select tag and attach the keyDownEvent and close the tag
			{
				if(!tagClosed)
				{
					tagClosed = true;
					if(!keyDownAttached && enableSliding)
					{
						myElement += " onKeyDown=\'trapDynamicContentRowKeyDown(event);\'> ";
					}
					else
					{
						myElement += ">";
					}
				}
				myElement += "</select>";
			}
			else if(elementType=="textarea")	//to be implemented
			{
				//do nothing
			}
			else if(elementType!="label")	//for all other elements
			{
				if(!keyDownAttached && enableSliding)
				{
					myElement += " onKeyDown=\'trapDynamicContentRowKeyDown(event);\'> ";
				}
				else
				{
					myElement += ">";
				}
			}
		}
		else	//if no childs are there,then just create a empty TD
		{
			myElement = "&nbsp;";
		}
		//if(elementType=="select")
			//alert(myElement+" elementType "+elementType);
		myTableCell.innerHTML += myElement;	 //append to the HTML of the TD with the myElement create
		if(!isMerged && !startingElement)	 //if the element is not merged and not starting element then add TD to the TR
			row.appendChild(myTableCell);
	}
	if(isMerged)	 // if the last element is merged then append it here
	{
		if(myTableCell!=null)
			row.appendChild(myTableCell);
	}
	dynamicContentRow = row;	//assign to the global variable
	//alert(dynamicContentRow.innerHTML);
}
function getAttributeValue(x,attribName,defaultValue)	 //check for the node with the attribute,if present return value,else the default Value sent in
{
	for(var i=0;i<x.attributes.length;i++)
	{
		if(attribName==x.attributes[i].name)
			return x.attributes[i].value;
	}
	return defaultValue;
}
function createDynamicContentRow(tableId,str11)	//creates dynamicROW,simple method
{
	var myTable				= document.getElementById(tableId);
	
	//alert("in js myTable:"+tableId);

	if(myTable==null)
		return;
	var myTableBody			= document.createElement("TBODY");
	var idcounter				= myTable.getAttribute("idcounter");
	var idcountertemp			= idcounter;//Added by Anil.k	
	var xmlTagName			= myTable.getAttribute("xmlTagName");
	var defaultElement		= myTable.getAttribute("defaultElement");
	var functionName			= myTable.getAttribute("functionName");
	var deleteButtonValue	= myTable.getAttribute("deleteButton");
	var addButtonValue		= myTable.getAttribute("addButton");
	var maxRows		        = myTable.getAttribute("maxRows");  
	//alert(" maxRows "+maxRows);

//  alert("maxRows:"+maxRows)
	//new additions
	if(maxRows==null)
		myTable.setAttribute("maxRows",25)


	var onBeforeCreateFunction = myTable.getAttribute("onBeforeCreate");
	if(onBeforeCreateFunction!=null)
	{
		var functionsList = this[onBeforeCreateFunction];
		if(functionsList!=null)
		{
			if(!functionsList(idcounter))
				return false;
		}
	}


	deleteButtonValue		= (deleteButtonValue==null)?"<<":deleteButtonValue;
	addButtonValue			= (addButtonValue==null)?">>":addButtonValue;

//if(( maxRows != null || maxRows.length > 0 ) && myTable.rows.length > maxRows)
if( maxRows != null  && myTable.rows.length > maxRows)
{
  alert("Cannot add more than "+maxRows+" rows");
  return;
}

//	<table border='0' width="800" cellspacing='1' cellpadding='2' nowrap id=consoleCostTable idcounter="1" 
//	defaultElement="charge" xmlTagName="BBConsoleCostDetails" functionName="defaultFunction" 
//	deleteFunctionName="defaultDeleteFunction">

	//alert("idcounter:"+idcounter+":\nxmlTagName:"+xmlTagName+":\ndefaultElement:"+defaultElement+":\nfunctionName:"+functionName+":\ndeleteButtonValue:"+deleteButtonValue+":\naddButtonValue:"+addButtonValue);

	myTableBody.setAttribute("id",tableId+"tbody"+idcounter);
	getDynamicContent(xmlTagName,idcounter,tableId);
	if(dynamicContentRow==null)
		return;
	if(document.all && dynamicContentRow.innerHTML=="")
		return;
	var myTableRow = dynamicContentRow;
	var myTableCell=document.createElement("TD");
	var idcounter1=1;
	myTableCell.setAttribute("align","center");
	myTableCell.className = "formdata";
	//alert("idcounter22 "+idcounter);
	//if(idcounter>1)
	if("QuoteChargeGroups"==tableId && idcounter>=1)
		myTableCell.innerHTML = "<input id="+tableId+"delbut"+idcounter+" name="+tableId+"delbut type=button value='"+deleteButtonValue+"' onClick=deleteDynamicContentRow('"+tableId+"','"+myTableBody.getAttribute("id")+"'); class=input>";
	if("QuoteChargeGroups"!=tableId && idcounter>1)
		myTableCell.innerHTML = "<input id="+tableId+"delbut"+idcounter+" name="+tableId+"delbut type=button value='"+deleteButtonValue+"' onClick=deleteDynamicContentRow('"+tableId+"','"+myTableBody.getAttribute("id")+"'); class=input>";
	else
		myTableCell.appendChild(document.createTextNode('   '));
    
	myTableCell.appendChild(document.createTextNode(' '));
	myTableCell.setAttribute("align","right");
	myTableRow.insertBefore(myTableCell,myTableRow.firstChild);

	var myTableCell = document.createElement('TD');
	myTableCell.className = "formdata";
	myTableCell.innerHTML = "<input name=addbut id="+tableId+"addbut"+idcounter+" type=button value='"+addButtonValue+"' onClick=createDynamicContentRow('"+tableId+"','"+myTableBody.getAttribute("id")+"'); class=input>";
	myTableRow.appendChild(myTableCell);
	
	myTableBody.appendChild(myTableRow);

 
	myTable.appendChild(myTableBody);

// alert("myTableBodybb:\n"+myTableBody.innerHTML);
 
	idcounter++;
	myTable.setAttribute("idcounter",idcounter);
	var prevElement;
	if(idcounter>1)
	{
		for(var i=1;i<=idcounter;i++)
		{
			if(document.getElementById(tableId+'addbut'+i)!=null)
			{  //modified by phani sekhar for wpbn 16760 
				if("chargegroupmaster"==tableId)
				{
				document.getElementById(tableId+'addbut'+i).style.visibility="visible"; 
				}
				else
				document.getElementById(tableId+'addbut'+i).style.visibility="hidden"; 
				prevElement=i;
			}
		}
		document.getElementById(tableId+'addbut'+prevElement).style.visibility="visible"; 
	}
if("chargegroupmaster"==tableId)//added by phani sekhar for wpbn 16760
	{
		if (str11!=null && idcounter>1 )
			{
		



			var rwCnt = parseInt(str11.substring(22));
			var tempId=0;
			var temp= idcounter-rwCnt;			
			var flag=true;
            var i=idcounter-1;
		    var  x=0;
			if(temp>2)
			{
				while(flag)
				{
					
				
							for(x=i-1;;x--)
					         {
								 
								 if(document.getElementById('chargeid'+x)!=null)
								 {
									 if(x==rwCnt)
									 {
                                        flag=false;
										document.getElementById('chargeid'+i).value="";
										document.getElementById('chargedescid'+i).value="";
										break;

									 }
									 document.getElementById('chargeid'+i).value=document.getElementById('chargeid'+x).value;
									 document.getElementById('chargedescid'+i).value=document.getElementById('chargedescid'+x).value;
									 i=x;
									 break;
								 }
							 }
                

				}
				

			}
		
	
	
		}

}//end 16760		
	//alert(" functionName "+functionName);
	if(functionName!=null)
	{
		var functionsList = this[functionName];
		//alert(" functionsList "+functionsList);
		if(functionsList!=null)
			functionsList(idcounter-1);
	}
	//alert(" before try ");
	try
	{	
		/*if(document.getElementById(defaultElement+(idcounter-1))!=null&&!document.getElementById(defaultElement+(idcounter-1)).disabled)
			document.getElementById(defaultElement+(idcounter-1)).focus();*/
			//Added by Anil.k
		if(document.getElementById(defaultElement+(idcountertemp-1))!=null&&!document.getElementById(defaultElement+(idcountertemp-1)).disabled)
			document.getElementById(defaultElement+(idcountertemp)).focus();//modified by silpa.p on 7-06-11 
	}
	catch(e){alert("create dyn..in exception");}

  //alert(myTable.innerHTML);
  //alert("table created");
}
function deleteDynamicContentRow(tableId,str,currentIndex)
{
	var myTable		= document.getElementById(tableId)
	var functionName = myTable.getAttribute("deleteFunctionName");
	//alert("str="+str);
	var idcounter		= myTable.getAttribute("idcounter");

	//new additions
	var onBeforeDeleteFunction = myTable.getAttribute("onBeforeDelete");
//@@ Added by subrahmanyam for pbn id: 221432 on 21-Oct-10
	if(("QuoteChargeGroups"==tableId  &&  myTable.rows.length >2) || "QuoteChargeGroups"!=tableId ){
	if(onBeforeDeleteFunction!=null)

	{
		var functionsList = this[onBeforeDeleteFunction];
		if(functionsList!=null)
		{
			if(!functionsList(currentIndex))
				return false;
		}
	}

	if(document.all)
	{
		var tbody=document.getElementById(str);
		tbody.removeNode(true);
	}
	else
	{
		var tbody=document.getElementById(str);
		myTable.removeChild(tbody);
	}
	var prevElement;
	for(var i=1;i<=idcounter;i++)
	{
		if(document.getElementById(tableId+'addbut'+i)!=null)
			prevElement=i;
	}
	if(document.getElementById(tableId+'addbut'+prevElement)!=null)
		document.getElementById(tableId+'addbut'+prevElement).style.visibility="visible"; 
	if(functionName!=null)
	{
		var functionsList = this[functionName];
		if(functionsList!=null)
			functionsList();
	}
	}
	//@@ Added by subrahmanyam for pbn id: 221432 on 21-Oct-10
	else if("QuoteChargeGroups"==tableId  &&  myTable.rows.length <=2)
		alert('This Row Is Not Removed,Atleast one  Charge Group Will Exists')
	//--idcounter;
	//myTable.setAttribute("idcounter",idcounter);
	//}
}
/*function controlDynamicContentControlButtons(tableId,disableFlag,idcounter,hideFlag)
{
	if(disableFlag==null)
		disableFlag = false;
	var myTable		= document.getElementById(tableId);
	if(idcounter==null)
		idcounter		= myTable.getAttribute("idcounter");
	for(var i=1;i<=idcounter;i++)
	{
		if(document.getElementById(tableId+'delbut'+i)!=null)
			document.getElementById(tableId+'delbut'+i).disabled = disableFlag;
		if(document.getElementById(tableId+'addbut'+i)!=null)
			document.getElementById(tableId+'addbut'+i).disabled = disableFlag;
		if(hideFlag!=null)
		{
			if(document.getElementById(tableId+'delbut'+i)!=null)
				document.getElementById(tableId+'delbut'+i).style.visibility = 'hidden';
			if(document.getElementById(tableId+'addbut'+i)!=null)
				document.getElementById(tableId+'addbut'+i).style.visibility = 'hidden';
		}
	}
}*/
function trapDynamicContentRowKeyDown(e)	//default onKeyDown event
{
// NEED TO SOLVE COMBO BOX PROBLEM IN 'IE'
	var ie		= (document.all)?true:false;
	var ns	= (ie)?false:true;
//alert("ie:"+ie+":-ns:"+ns);
	try
	{
		var whichCode		= (ns) ? e.which : event.keyCode;
//alert("ie:"+ie+":-ns:"+ns);
		var altPressed		= (ns) ? e.altKey : event.altKey;
		var shiftPressed	= (ns) ? e.shiftKey : event.shiftKey;
		var ctrlPressed		= (ns) ? e.ctrlKey : event.ctrlKey;
		var srcElement		= (ns) ? e.target : event.srcElement;
		var isInputField	= true;
		var currentIndex	= srcElement.id.substring(srcElement.name.length);
//alert("!ctrlPressed:"+!ctrlPressed);
		if(!ctrlPressed)
    {
//alert("!ct");
			return;
    }

//alert("KEY "+String.fromCharCode(whichCode)+" Key "+whichCode+" altPressed "+altPressed+" shiftPressed "+shiftPressed+" ctrlPressed "+ctrlPressed+" srcElement "+srcElement.id);      
		if(whichCode!="38" && whichCode!="40")
		{
			return;
		}
		if(srcElement.type=="select-one" || srcElement.type=="select-multiple" || srcElement.type=="textarea")
			isInputField = false;
//alert("ctrlPressed "+ctrlPressed+" isInputField "+isInputField);
		if(!isInputField && !ctrlPressed)
			return;
		if(whichCode=="38")	//move up
		{
			var prevElement = findPrevDynamicContentElement(srcElement);
//alert("prevElement:"+prevElement);            
			if(prevElement!=null)
				prevElement.focus();
		}
		else //if(whichCode=="40")
		{
//alert("if(whichCode==40)"+srcElement);
//alert("if(whichCode==40)"+document.getElementById(srcElement.getAttribute('parentTableId')).getAttribute('idcounter'));      
			var nextElement = findNextDynamicContentElement(srcElement,document.getElementById(srcElement.getAttribute('parentTableId')).getAttribute("idcounter"));
//alert("nextElement:"+nextElement);      
			if(nextElement!=null)
				nextElement.focus();
		}
		if(!isInputField)
			event.returnValue = false;
	}
	catch(e){alert("trap key.. in exception")}
}
function findPrevDynamicContentElement(srcElement)
{
	var prevElement	= null;
	var currentIndex	= srcElement.id.substring(srcElement.name.length);
	var elementName	= srcElement.name;
	if(currentIndex>=0)
	{
		while(currentIndex>=0)
		{
			var tmpElement = document.getElementById(elementName+(currentIndex-1));
			if(tmpElement!=null)
			{
				prevElement = tmpElement;
				break;
			}
			currentIndex--;
		}
	}
	return prevElement;
}
function findNextDynamicContentElement(srcElement,totalElementsCount)
{
	var nextElement	= null;
	var currentIndex	= srcElement.id.substring(srcElement.name.length);
	var elementName	= srcElement.name;
	var currentCounter = totalElementsCount;
	if(totalElementsCount>0)
	{
		while(currentIndex<=totalElementsCount)
		{
			var tmpElement = document.getElementById(elementName+(currentIndex*1+1));
			if(tmpElement!=null)
			{
				nextElement = tmpElement;
				break;
			}
			currentIndex++;
		}
	}
	return nextElement;
}
/*function transformDynamicContentField(fieldObj)
{
	if(fieldObj==null)
		return;
	if(fieldObj.getAttribute("type")=="select-one" || fieldObj.getAttribute("type")=="select-multiple")
		fieldObj.outerHTML = "<input selectedIndex=0 options=null type=hidden name='"+fieldObj.name+"' id='"+fieldObj.id+"' value='"+fieldObj.value+"'>"+fieldObj.options[fieldObj.selectedIndex].innerText;
	else
		fieldObj.outerHTML = "<input type=hidden name='"+fieldObj.name+"' id='"+fieldObj.id+"' value='"+fieldObj.value+"'>"+fieldObj.value;
	//alert("<input type=hidden name='"+fieldObj.name+"' id='"+fieldObj.id+"' value='"+fieldObj.value+"'>"+fieldObj.value);
}*/
//end of dynamic code script

//sample for loading options list into a select field by giving the function name in the table object
/*
function loadList(tmpIdCounter)
{
	var selectObject = document.getElementById("select1"+tmpIdCounter)
	selectObject.add(new Option("Sowmi","Sowmi"));
	selectObject.add(new Option("Vinesh","Vinesh"));
	selectObject.add(new Option("Sowmi1","Sowmi1"));
}
*/

function removeDynamicContentTableRows(tableId)
{
	var myTable		= document.getElementById(tableId);
	var idcounter		= myTable.getAttribute("idcounter");
	//alert("idcounter"+idcounter);
    for(var i=1;i<=idcounter;i++)
		{    
    
      if(document.getElementById(tableId+"tbody"+i)!=null)
      {
          var tbody=document.getElementById(tableId+"tbody"+i);
		  
          if(document.all){
           //alert("if");
			   tbody.removeNode(true);
		  }
          else{
			 // alert("else");
            myTable.removeChild(tbody);
		  }
      }
	  
		}
    myTable.setAttribute("idcounter","1");
  
/*
			if(document.getElementById(tableId+"tbody"+i)!=null)
			{
      alert(index+":document.getElementById(tableId+tbody+i):"+document.getElementById(tableId+"tbody"+i));
				document.getElementById(tableId+"tbody"+i).removeNode(true);
        //document.getElementById(tableId+"tbody"+i).deleteRow(index);
			}
  */

}

