
<%
/*	Programme Name : QMSZoneCodeMasterAdd.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : ZoneCodeMaster
*	Sub Task Name  : ZoneCodeMaster Add
*	Author		   : Rama Krishna.Y
*	Date Started   : 16-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>
<%@ page import="org.apache.log4j.Logger,
                 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 java.util.ArrayList"%>


<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%! 
  private static Logger logger = null;
  public final static String   FILE_NAME = "QMSZoneCodeMasterAdd.jsp";%>

<%
    logger  = Logger.getLogger(FILE_NAME);
    String       operation      =  request.getParameter("Operation");
	
    try 			
   {

		 String  locationId             =  request.getParameter("locationId");
		 String  terminalId     		=  request.getParameter("terminalId"); 		 
		 String  city					=  request.getParameter("city");
		 String  state					=  request.getParameter("state");
		 String  zipCode				=  request.getParameter("zipCode");
		 String  shipmentMode			=  request.getParameter("shipmentMode");
		 String  consoleType			=  request.getParameter("consoleType");
//		 String  port					=  request.getParameter("port");

%>
<html>
<head>
<title>ZoneCodeMaster</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script language="javascript" src="html/dynamicContent.js"></script>
<script language="javascript">
var locationFlag        = false;
var controlStationFlag  = false;
//var portFlag            = false;

function showLov(lovName)
{
	 var URL  = "";
	
	if(lovName.name=="locationLOV")
	{
		locationFlag = true;		URL="QMSLOVLocationIds.jsp?searchString="+document.forms[0].locationId.value+"&wheretoset="+document.forms[0].locationId.name+"&city="+document.forms[0].city.name;
	}
	
	if(lovName.name=="terminalLOV")
	{
		if(document.forms[0].locationId.value.length<=0)
		{
			alert(" Enter location Id");
			return false;
		}
		URL="etrans/ETCLOVTerminalIds.jsp?searchString="+document.forms[0].terminalId.value+"&locationId="+document.forms[0].locationId.value;
	}
	
	/*if(lovName.name=="portLOV")
	{
		portFlag = true;
		URL="etrans/sea/ETSLOVPortIds.jsp?searchStr="+document.forms[0].port.value+"&whereToSet="+document.forms[0].port.name;
	}*/
	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;	
	var Win=open(URL,'Doc',Features);
}
function mandatory()
{	 
		if(document.forms[0].locationId.value.length<=0)
		{
			alert(" enter location Id");
			document.forms[0].locationId.focus();
			return false;
		}
		
		if(document.forms[0].terminalId.value.length<=0)
		{
			alert(" enter Terminal Id ");
			document.forms[0].terminalId.focus();
			return false;
		}		
		/*if(document.forms[0].port.value.length>0)
	    {
               alert("Please select from  Port LOV");
			   document.forms[0].controlStationId.focus();
			   return false;
	    }*/
		//alert(validateFromZipToZip());
		if(!validateFromZipToZip())
			return false;
		else
			return true;
}
function initialize()
{
	importXML('html/DynamicContent.xml');	
}
function initializeDynamicContentRows() 
{       
		  
		   <%if("ALPHANUMERIC".equals(zipCode))
	       {%>
			   
				 var tableZipCode	= document.getElementById("zipCodeTable1");
				createDynamicContentRow(tableZipCode.getAttribute("id")); 
           <%}
		   else
	       {%>			   
				var tableZipCode	= document.getElementById("zipCodeTable");
				createDynamicContentRow(tableZipCode.getAttribute("id")); 
 	       <%}%>
	
	return; 
 }
 function reLoad()
 {
	 
	 document.forms[0].action="QMSZoneCodeMasterAdd.jsp?Operation=<%=operation%>";
	 document.forms[0].submit();
 }

function numericOnly(input) 
{
		s = input.value;
		filteredValues = "''~!@#$%^&*()_-+=|\:;<>,/?.";
		filteredValues1 = "1234567890";
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++) 
		{  
			var c = s.charAt(i);
			if ( filteredValues.indexOf(c) == -1 && filteredValues1.indexOf(c)!=-1 ) 
				returnString += c;
			else
				flag = 1;
		}
		if( flag==1 )
		{
			alert("Characters not allowed");
			var field = document.forms[0];
			for(i = 0; i < field.length; i++) 
			{
			   if( field.elements[i] == input )
			   {
					document.forms[0].elements[i].focus();
					break;
		       }
		   }		
		}

		input.value = returnString;
}

function validateFromZipToZip()
{
	var fromZipCodes	=	document.getElementsByName("fromZipCode");
	var toZipCodes		=	document.getElementsByName("toZipCode");
	var alphaNumeric	=	document.getElementsByName("alphaNumeric");
//@@Added by subrahmanyam for the wpbn id: 197818 on 18/Feb/10
	var zipCodeType		= document.forms[0].zipCode.value;
	var zone=document.getElementsByName("zone");
    var estimatedTime=document.getElementsByName("estimatedTime");
    var estimatedDistance=document.getElementsByName("estimatedDistance");
//@@Modified by subrahmanyam for the wpbn id: 197818 on 18/Feb/10
	for(i=0;i<fromZipCodes.length;i++)
	{
		if(fromZipCodes[i].value.length==0 || toZipCodes[i].value.length==0 || "ALPHANUMERIC"==zipCodeType ||
			zone[i].value.length==0 ||estimatedTime[i].value.length==0 || estimatedDistance[i].value.length==0 )
		{

//@@Added by subrahmanyam for the wpbn id: 197818 on 18/Feb/10			
			if("ALPHANUMERIC"==zipCodeType)
			{
				if(alphaNumeric[i].value=='')
				{
					alert("Please Enter the Alpha-Numeric Value  at row "+(i+1));
					alphaNumeric[i].focus();
					return false;
				}

			}
//@@Ended by subrahmanyam for the wpbn id: 197818 on 18/Feb/10
			if(fromZipCodes[i].value.length==0)
			{
				alert("Please Enter the From Zip Code at row "+(i+1));
				fromZipCodes[i].focus();
				return false;
			}	
			if(toZipCodes[i].value.length==0)
			{
				alert("Please Enter the To Zip Code at row "+(i+1));
				toZipCodes[i].focus();
				return false;
			}
//@@Added by subrahmanyam for the wpbn id: 197818 on 18/Feb/10
			if(zone[i].value.length==0)
			{
				alert("Please Enter the Zone Code at row "+(i+1));
				zone[i].focus();
				return false;
			}	
			 if(estimatedTime[i].value==0)
			{
				alert("Please Enter the Estimated Time at row "+(i+1));
				estimatedTime[i].focus();
				return false;
			}
			if(estimatedDistance[i].value.length==0)
			{
				alert("Please Enter the Estimated Distance at row "+(i+1));
				estimatedDistance[i].focus();
				return false;
			}	

//@@Ended by subrahmanyam for the wpbn id: 197818 on 18/Feb/10
		}
		else
		{
			for(j=(i+1);j<fromZipCodes.length;j++)
			{
			
				//@@Commented by Kameswari for the WPBN issue-59461
				/*if(alphaNumeric[i]!=null && alphaNumeric[j]!=null)
				{
						if((alphaNumeric[i].value==alphaNumeric[j].value) && parseInt(fromZipCodes[i].value)<=parseInt(fromZipCodes[j].value) && parseInt(fromZipCodes[j].value)<=parseInt(toZipCodes[i].value))
					{
						alert("The From Zip Code "+fromZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
					if((alphaNumeric[i].value==alphaNumeric[j].value) && parseInt(fromZipCodes[i].value)<=parseInt(toZipCodes[j].value) && parseInt(toZipCodes[j].value)<=parseInt(toZipCodes[i].value))
					{
						alert("The To Zip Code "+toZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
				}
				else
				{
					
					if(parseInt(fromZipCodes[i].value)<=parseInt(fromZipCodes[j].value) && parseInt(fromZipCodes[j].value)<=parseInt(toZipCodes[i].value))
					{
						alert("The From Zip Code "+fromZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
					if(parseInt(fromZipCodes[i].value)<=parseInt(toZipCodes[j].value) && parseInt(toZipCodes[j].value)<=parseInt(toZipCodes[i].value))
					{
						alert("The To Zip Code "+toZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
				}*/
				//@@WPBN issue-59461
				//@@Modified by Kameswari for the WPBN issue-59461
				if(alphaNumeric[i]!=null && alphaNumeric[j]!=null)
				{
					if((alphaNumeric[i].value==alphaNumeric[j].value) && Number(fromZipCodes[i].value)<=Number(fromZipCodes[j].value) && Number(fromZipCodes[j].value)<=Number(toZipCodes[i].value))
					{
						alert("The From Zip Code "+fromZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
					if((alphaNumeric[i].value==alphaNumeric[j].value) && Number(fromZipCodes[i].value)<=Number(toZipCodes[j].value) && Number(toZipCodes[j].value)<=Number(toZipCodes[i].value))
					{
						alert("The To Zip Code "+toZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
					//@@Added by Kameswari for the WPBN issue - 118982
					if((alphaNumeric[i].value==alphaNumeric[j].value)&&Number(fromZipCodes[i].value)<=Number(toZipCodes[j].value) &&Number(fromZipCodes[i].value)>=Number(fromZipCodes[j].value))
					{
						alert("The From Zip Code "+fromZipCodes[i].value+" given in row "+(i+1)+" falls in the range of the Zip Codes given in row "+(j+1));
						return false;
					}
					//@@WPBN issue -118982
				}
				else
				{
					if(Number(fromZipCodes[i].value)<=Number(fromZipCodes[j].value) && Number(fromZipCodes[j].value)<=Number(toZipCodes[i].value))
					{
						//alert('fromZipCodes'+Number(fromZipCodes[j].value));
						alert("The From Zip Code "+fromZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
					if(Number(fromZipCodes[i].value)<=Number(toZipCodes[j].value) && Number(toZipCodes[j].value)<=Number(toZipCodes[i].value))
					{
						alert("The To Zip Code "+toZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
					//@@Added by Kameswari for the WPBN issue - 118982
					if(Number(fromZipCodes[i].value)<=Number(toZipCodes[j].value) &&Number(fromZipCodes[i].value)>=Number(fromZipCodes[j].value))
					{
						alert("The From Zip Code "+fromZipCodes[i].value+" given in row "+(i+1)+" falls in the range of the Zip Codes given in row "+(j+1));
						return false;
					}
					//@@WPBN issue -118982
				}
				//@@Modified by Kameswari for the WPBN issue-59461
			}
			
		}
	}

	return true;
}

function validateRow()
{
  var fromZipCode=document.getElementsByName("fromZipCode");
  var toZipCode=document.getElementsByName("toZipCode");
  var zone=document.getElementsByName("zone");
  var estimatedTime=document.getElementsByName("estimatedTime");
  var estimatedDistance=document.getElementsByName("estimatedDistance");
	var zipCodeType		= document.forms[0].zipCode.value;//@@Added by subrahmanya for the wpbn id: 197818 on 18/Feb/10
	var alphaNumeric	=	document.getElementsByName("alphaNumeric");


  for(n=0;n<fromZipCode.length;n++)	  
  {
	  //@@Modified by subrahmanyam for the wpbn id: 197818 on 18/Feb/10
   if(fromZipCode[n].value.length==0 || toZipCode[n].value.length==0 || zone[n].value.length==0|| estimatedTime[n].value.length==0|| estimatedDistance[n].value.length==0 || ( "ALPHANUMERIC"==zipCodeType && alphaNumeric[n].value.length==0)){
   alert("The row is not completed,Please Enter  all the values");
   return false;
   }
  }
  
  return true;
}
function checkZipCodes(obj)
{
  
  var index=obj.id.substring(obj.name.length);
  var fromZipCode=document.getElementById("fromZipCode"+index);
  var toZipCode=document.getElementById("toZipCode"+index);
  
  if(parseFloat(fromZipCode.value)> parseFloat(toZipCode.value))
  {
		alert("From Zip Code should be less than To Zip Code");
		document.getElementById("toZipCode"+index).value="";
		document.getElementById("toZipCode"+index).focus();
  }
}
function changeFlag(input)
{
  if(input.name=='locationId')
    locationFlag=false;
  if(input.name=='controlStationId')
    controlStationFlag=false;
  //if(input.name=='port')
    //portFlag=false;
  
}
function toUpperCase(object)
{
	object.value=object.value.toUpperCase();
}
function checkTime(field)
{
	timeStr=field.value
	if(timeStr.length!=0)
	{
		var	timePat	= /^(\d{1,2}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;
		if(timeStr.length==4 && timeStr.indexOf(':')==-1)
		{
			timeStr = timeStr.substring(0,2)+':'+timeStr.substring(2,4);
			field.value=timeStr;


		}

		var	matchArray = timeStr.match(timePat);
		if (matchArray == null)
		{
			alert("Please enter in HH:MM format");
			field.focus();
		}
		else
		{
			hour = matchArray[1];
			minute = matchArray[2];
			second = matchArray[4];
			ampm = matchArray[6];
			if (second=="")	{ second = null; }
			if (ampm=="") {	ampm = null	}
			if (hour < 0  || hour >	99)	{
			alert("Please enter correct Hours");
			field.focus();
			}
			else if	(minute<0 || minute	> 59) {
				alert ("Please enter correct Minutes.");
				field.focus();
			}
		}
	}
}
function changeConsoleVisibility(flag)
{
	var console	 = document.getElementById('console');
	
	if(flag)
	{
		document.forms[0].locationId.value='';
		document.forms[0].city.value='';
		document.forms[0].terminalId.value='';
	}

	if(document.forms[0].shipmentMode.value=='2')
		console.style.display="block";
	else
		console.style.display="none";

}
function openLocationLov()
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	
	var indx = document.forms[0].shipmentMode.selectedIndex;

	formArray = 'locationId';		
	
	tabArray = 'LOCATIONID';
	Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.options[indx].text+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].locationId.value+"~'";


	Options	='width=750,height=750,resizable=yes';
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Features=Bars+','+Options;

	Win=open(Url,'Doc',Features);
}
/*
function checkTime(input) 
{
		s = input.value;
		filteredValues = "''~!@#$%^&*()_-+=|\.;<>,/?";
		filteredValues1 = "1234567890:";
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++) 
		{  
			var c = s.charAt(i);
			if ( filteredValues.indexOf(c) == -1 && filteredValues1.indexOf(c)!=-1 ) 
				returnString += c;
			else
				flag = 1;
		}
		if( flag==1 )
		{
			alert("Characters not allowed");
			var field = document.forms[0];
			for(i = 0; i < field.length; i++) 
			{
			   if( field.elements[i] == input )
			   {
					document.forms[0].elements[i].focus();
					break;
		       }
		   }		
		}

		input.value = returnString;
}*/
</script>

<body onLoad='initialize();changeConsoleVisibility(false)'>

<form  method="post" action="QMSZoneCodeMasterProcess.jsp" onSubmit="return mandatory()">

<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
<tr>
	<td>
	<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Zone Code Master - Add</td><td align=right>QS1040411</td></tr></table></td>
		  </tr></table>

		  <table width="100%" cellpadding="4" cellspacing="1" bgcolor="#FFFFFF">
              <tr class="formdata" vAlign="top">
                <td  colspan='2'>Shipment Mode&nbsp;&nbsp;&nbsp;:
					<select name='shipmentMode' class='select' onchange='changeConsoleVisibility(true)'>
						<option value='1' <%="1".equalsIgnoreCase(shipmentMode)?"selected":""%>>Air</option>
						<option value='2' <%="2".equalsIgnoreCase(shipmentMode)?"selected":""%>>Sea</option>
					</select>
				</td>
				<td>
					 <DIV id="console" style="DISPLAY:none">
						Console Type&nbsp;&nbsp;&nbsp;:
						<select name="consoleType" class='select'>
							<option value="LCL" <%="LCL".equalsIgnoreCase(consoleType)?"selected":""%>>LCL</option>
							<option value="FCL" <%="FCL".equalsIgnoreCase(consoleType)?"selected":""%>>FCL</option>
						</select>
					</DIV>
				</td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="2">Location
                  <sup><font color="red">*</font></sup>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:
                  <input class="text" maxLength="16" name="locationId"  size="10" value='<%=locationId!=null?locationId:""%>' onBlur='toUpperCase(this)' onKeyPress='changeFlag(this)'>
                  <input class="input" name="locationLOV" onclick="openLocationLov()"  type="button" value="..." ></font></td>
                <td>Terminal<sup><font color="red">*</font></sup></font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:
                 <input class="text" maxLength="10" name="terminalId"  size="10" value='<%=terminalId!=null?terminalId:""%>' onBlur='toUpperCase(this)'            onKeyPress='changeFlag(this)'>&nbsp;<input class="input" name="terminalLOV" onclick="showLov(terminalLOV)"  type="button" value="..." ></font></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="2">City &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;:
                  <input class="text" maxLength="20" name="city" onBlur='toUpperCase(this)'  size="20"  value='<%=city!=null?city:""%>'></td>
                <td>State &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: 
                  <input class="text" maxLength="20" name="state" onBlur='toUpperCase(this)'  size="20" value='<%=state!=null?state:""%>' >
                  &nbsp;</font></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="2">Zip Code Type &nbsp;&nbsp;:
                  <select name="zipCode"   onChange='reLoad()' class='select'>
				  <%if("ALPHANUMERIC".equals(zipCode)){%>
				  <option value="ALPHANUMERIC">Alpha-Numeric</option>
				  <option value="NUMERIC">Numeric</option>				  
				  <%}else{%>
				  <option value="NUMERIC">Numeric</option>
				  <option value="ALPHANUMERIC">Alpha-Numeric</option>
				  <%}%>
				  </select></td>
                 <td><!--Port<sup><font color="red">*</font></sup> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: 
                  <input class="text" maxLength="20" onBlur='toUpperCase(this)'  name="port" size="20" >
                  <input class="input" name="portLOV" onclick="showLov(portLOV)"  type="button" value="..." null></font>--></td> 
              </tr>
            </tbody>
          </table>
		  <%if(!"ALPHANUMERIC".equals(zipCode))
	       {%>
		  <table width="100%" border='0' cellspacing='0' cellpadding='2' nowrap id='zipCodeTable' idcounter="1" defaultElement="defaultElement" xmlTagName="DynamicNumeric" functionName="" deleteFunctionName="defaultDeleteFunction" onBeforeCreate="validateRow" bgcolor='#FFFFFF'>
			<tr class='formheader' align="center"> 
				<td class=formheader></td>
				<td class=formheader>From Zipcode:<font color="red">*</font></td>
				<td class=formheader>To Zipcode:<font color="red">*</font></td>
				<td class=formheader>Zone:<font color="red">*</font></td>
				<td class=formheader>Estimation Time:<font color="red">*</font><br>(HH:MM)</td>
				<td class=formheader>Estimated Distance:<font color="red">*</font></td>				
				<td class=formheader>&nbsp;</td>
			</tr> 	          
		</table>
		<%}else{%>
		 <table width="100%" border='0' cellspacing='0' cellpadding='2' nowrap id='zipCodeTable1' idcounter="1" defaultElement="defaultElement" xmlTagName="DynamicAlphaNumeric" onBeforeCreate="validateRow" functionName="" deleteFunctionName="defaultDeleteFunction" bgcolor='#FFFFFF'>
			<tr class='formheader' align="center"> 
				<td class=formheader></td>
				<td class=formheader>From Zipcode:</td>
				<td class=formheader>To Zipcode:</td>
				<td class=formheader>Zone:</td>
				<td class=formheader>Estimation Time:<font color="red">*</font><br>(HH:MM)</td>
				<td class=formheader>Estimated Distance:<font color="red">*</font></td>				
				<td class=formheader>&nbsp;</td>
			</tr> 	          
		</table>  
		<%}%>
          <table  border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
            <tbody>
              <tr class="text" vAlign="top">
                <td  colSpan="4"><font color="red">*</font>Denotes
                  Mandatory
				  <input type="hidden" name="Operation"  value="<%=operation%>">
				  </td>

                <td align="right"  colSpan="2">&nbsp;&nbsp;
                  <input class="input" name="submit1" ; BORDER-BOTTOM-STYLE: solid; BORDER-LEFT-STYLE: solid; BORDER-RIGHT-STYLE: solid; BORDER-TOP-STYLE: solid" type="submit" value="Submit"></font></td>
              </tr>
            </tbody>
          </table>
        </td>
      </tr>
    </tbody>
  </table>  
</form>
</body>
</html>
<%
			//}
	}
	catch(Exception e)
	{
		e.printStackTrace();
		//Logger.error(FILE_NAME,"Error in QMSZoneCodeMasterAdd.jsp"+e.toString());
    logger.error(FILE_NAME+"Error in QMSZoneCodeMasterAdd.jsp"+e.toString());
		ArrayList       keyValueList   =  new ArrayList();
		ErrorMessage    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSZoneCodeMasterAdd.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
    <jsp:forward page="QMSESupplyErrorPage.jsp" />
<%
	}
%>