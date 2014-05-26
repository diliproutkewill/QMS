<%--
	Program Name		: ETCTerminalRegistrationAdd.jsp
	Module name			: HO Setup
	Task				: Adding Terminal
	Sub task			: to add a terminal
	Author Name			: C.L.N Saravana
	Date Started		: Feb 25, 2002
	Date completed		: Feb 27, 2002
	Description		:
	This file displays the form to fill in the details for  a terminal registration and on
	submission directs the control to TerminalRegistraionAddProcess.jsp   


--%>
<%@ page import = "	javax.naming.InitialContext,
					java.util.Vector,
				 	org.apache.log4j.Logger,					
					com.foursoft.etrans.common.util.java.OperationsImpl,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>
					
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCTerminalRegistrationAdd.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	OperationsImpl operationsImpl = new OperationsImpl();
	operationsImpl.createDataSource();
	String userTerminalType = loginbean.getUserTerminalType();
	//Logger.info(FILE_NAME,"userTerminalType in Add : "+userTerminalType);
	
	
	try
	{
		// checking for teminal Id
		if(loginbean.getTerminalId() == null )
		{
%>
			<jsp:forward page="../ESupplyLogin.jsp"/>
<%
		}
	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Error in TerminalRegistrationAdd.jsp file : "+e.toString());
    logger.error(FILE_NAME+"Error in TerminalRegistrationAdd.jsp file : "+e.toString());
	}
%>
<%
	String locationName   = null;    // String to store locationName
	String locationId     = null;    // String to store locationId
	String countryId      = null;    // String to store countryId
	String companyIds[]   = null;    // a String array to store companyIds
	String countryIds[]   = null;    // a String array to store country Ids
	Vector vecLocationInfo = null;   // a Vector to store location information
	InitialContext initialContext = null;    // variable to store InitialContext
	SetUpSessionHome terminalHome = null;    // variable to store Home object
	SetUpSession		terminalRemote = null;    // variable to store remote reference
	
	String label	= "Locations ";
	String readonly = "readonly";
	
	String terminalType	= request.getParameter("terminalType");
	String adminType	= request.getParameter("adminType");
	
	
	if(terminalType!=null && terminalType.equals("A"))
	{
		label 	 = "Terminals";
		readonly = "";
	}
		
	//Logger.info(FILE_NAME,"terminalType : "+terminalType);
	//Logger.info(FILE_NAME,"adminType 	 : "+adminType);	
	try
	{
			initialContext  = new InitialContext();
			terminalHome    = (SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
			terminalRemote  = (SetUpSession)terminalHome.create();
			boolean exists  = true;
			int		companyLen	=	0;		
			companyIds      = terminalRemote.getCompanyIds();
			//Logger.info(FILE_NAME,"cc.."+companyIds);
			if(companyIds != null)
			 companyLen = companyIds.length;
				//Logger.info(FILE_NAME,"companyLen.."+companyLen);		 
			if(companyLen == 0)
			{
				
					String errorMessage = "No Company is registerd.Please click continue to register a company. ";    // String to store error message
					session.setAttribute("ErrorCode","MSG");
					session.setAttribute("ErrorMessage",errorMessage);
					session.setAttribute("Operation","Add");
					session.setAttribute("NextNavigation","ETCHOCompanyRegistrationAdd.jsp?Operation=Add");
%>
					<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
			}



			if(terminalType.equals("A") && (adminType!=null && adminType.equals("H") ))
			{
				exists = terminalRemote.checkHOTerminal();
				if(exists)
				{
					String errorMessage = "Record already exists for HOAdmin Terminal ";    // String to store error message
					session.setAttribute("ErrorCode","ERR");
					session.setAttribute("ErrorMessage",errorMessage);
					session.setAttribute("Operation",errorMessage);
					session.setAttribute("NextNavigation","ETCOperationTerminalRegistrationEnterId.jsp?Operation=Add");
%>
					<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
				}
			
			}
			
			countryIds      = operationsImpl.getCountryIds();
			vecLocationInfo = terminalRemote.getLocationInfo("","","");
			session.setAttribute("LocationInfo",vecLocationInfo);
			session.setAttribute("CountryIds",countryIds);
	}
	catch( Exception e )
	{
			//Logger.error(FILE_NAME,"Error in  TerminalRegistrationAdd.jsp file : "+e.toString());
      logger.error(FILE_NAME+"Error in  TerminalRegistrationAdd.jsp file : "+e.toString());
			String errorMessage = "Error while retriving data";    // String to store error message
			session.setAttribute("ErrorCode","ERR");
			session.setAttribute("ErrorMessage",errorMessage);
			session.setAttribute("Operation",errorMessage);
			session.setAttribute("NextNavigation","ETCTerminalRegistrationAdd.jsp");
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
%>

<html>
<head>
<title>Terminal Registration</title>
<script language="JavaScript">
var terminalType = '<%=terminalType%>';
var adminType    = '<%=adminType%>';
var compId		 = null;
var cbthf        = null;
<!--
function assignLocations()
{
	
	var len1= window.document.forms[0].locationId.length;
	var index=0;
	for(var i=0;i<len1;i++)
	{
		window.document.forms[0].locationId.options.remove(index);
	}
	str = hf;
	if(str.length>0)
	{
		entries = str.split("-");
		for(i=0;i<entries.length;i++)
		{
			if(entries[i] != "-" && entries[i]!="")
			{
				window.document.forms[0].locationId.options[index] = new Option(entries[i] ,entries[i] )
				index++;
			}
		}
		len = document.forms[0].locationId.options.length;
		locationList = new Array();
		for(i=0;i<len;i++)
		{
			locationList[i] = document.forms[0].locationId.options[i].value;
		}
		if(document.forms[0].locationId.options.length > 0)
		{	
			var terId;
			if(terminalType=='O')
		  		terId = window.document.forms[0].companyId.value+document.forms[0].locationId.options[0].value
			else
				terId = document.forms[0].companyId.value+document.forms[0].myId.value;
		
			document.forms[0].terminalId.value = terId;
			document.forms[0].locationIdHide.value = locationList.toString();
		
			if(terminalType=='O')
				changeLocationName();
		}
		else
		{
			document.forms[0].terminalId.value = '';
			document.forms[0].locationName.value = '';
		}
	}
  }

	arrayLocationIds = new Array();
	arrayCountryIds  = new Array();
	arrayTerminalType = new Array();
	function loadValues()
	{
<%
		if( countryIds!=null )
		{
			int len2 = countryIds.length;
			for( int i=0; i<len2; i++ )
			{
				int idx = countryIds[i].indexOf('(');
				String cId = countryIds[i].substring(idx+2,idx+4);
%>
				arrayCountryIds[<%=i%>] = '<%=cId%>';
<%
			}
		}
%>
		placeFocus();
	}
	function isValidCountry( input )
	{
		if(input.value.length > 0)
		{
			for( i=0; i<arrayCountryIds.length; i++ )
			{
				if( input.value==arrayCountryIds[i])
				{
					return true;
				}
			}
			alert("Please enter correct CountryId");
			input.focus()
			input.value="";
			return false;
		}
	}
	function placeFocus()
	{
		if( document.forms.length > 0 )
		{
			var field = document.forms[0];
			for(i = 0; i < field.length; i++)
			{
				if( field.elements[i].type.toString().charAt(0) == "s" )
				{
					document.forms[0].elements[i].focus();
					break;
				}
			}
		}
		populateCompanyInfo()
	}

	
	function checkForNull()
	{
		if( document.forms.length > 0 )
		{
			var field = document.forms[0];
			for(i = 0; i < field.length; i++)
			{
				if( (field.elements[i].type == "text") || (field.elements[i].type == "textarea") )
				{
					if(field.elements[i].name != 'emailId')
					{
					document.forms[0].elements[i].value = document.forms[0].elements[i].value.toUpperCase();
					}
				}
			}
		}
		var errorMessage = "";
		var field = document.forms[0];
		var label = " Location Id(s)";
		if(terminalType=='A')
			label = " Terminal Id(s)";
			
			
		flag = true;
		for(i = 0; i < field.length; i++)
		{
			var varType  = field.elements[i].type;
			var varValue = field.elements[i].value;
			var varName  = field.elements[i].name;
						
			
			if( (terminalType=='H' || adminType=='H' ) && document.forms[0].agentInd.value!='COMPANY')
			{	
				alert("HOAdmin Terminal MUST be one of the Company Id's ");
				field.companyId.focus();
				return false;
			}
			
			if(terminalType!=null && terminalType=='O')
			{	
				if(field.locationId.options.length==1 && field.locationId.options[0].value=='(Select)')
				{
					errorMessage = "Please select"+label ;
					flag = false;
					alert(errorMessage);
					field.elements[i].focus()
					return flag;
				}
			}	
			
			if(terminalType!=null && terminalType=='A')
			{
				/*if(compId!=null && compId!=document.forms[0].companyId.value)
				{
					if(field.locationId.options.length>0)
					{
						for(var kk=0;kk<field.locationId.options.length;kk++)
						{
							field.locationId.options[kk] = null;
						}	
						field.locationId.options[0] = new Option('(Select)','(Select)');
					}		
										
					alert("Please select new terminals for : "+document.forms[0].companyId.value); 	
					document.forms[0].locationId.focus();
					return false;
				}*/
				if(field.myId.value.length==0)
				{
					alert("Please enter the Id ");
					field.myId.focus();
					return false;
				}
				if(field.myId.value.length!=4)
				{
					alert("Please enter 4 characters for Id ");
					field.myId.focus();
					field.myId.select();
					return false;
				}
			}	
			
			if(document.forms[0].shipmentMode.value == 0)
		    {
			 alert("Please select atleast one Shipment Mode");
			 return false;
			}

			if( ( varType=="text"||varType=="textarea" ) && ( varName=="notes") && ( varValue.length == 0 )  )
			{
				errorMessage = "Please enter CompanyName";
				flag = false;
				alert(errorMessage);
				field.elements[i].focus()
				return flag;
			}
			if( ( varType=="text"||varType=="textarea" ) && ( varName=="addressLine1" ) && ( varValue.length == 0 )  )
			{
				errorMessage = "Please enter Address";
				flag = false;
				alert(errorMessage);
				field.elements[i].focus()
				return flag;
			}
			if( ( varType=="text"||varType=="textarea" ) && ( varName=="city" ) && ( varValue.length == 0 )  )
			{
				errorMessage = "Please enter City";
				flag = false;
				alert(errorMessage);
				field.elements[i].focus()
				return flag;
			}
			if( ( varType=="text"||varType=="textarea" ) && ( varName=="countryId" ) && ( varValue.length == 0 )  )
			{
				errorMessage = "Please enter CountryId";
				flag = false;
				alert(errorMessage);
				field.elements[i].focus()
				return flag;
			}
		}
		if(isValidCountry(document.forms[0].countryId) == false )
		{
			flag = false;
		}
		if(document.forms[0].iatacode.value.length == 0)
		document.forms[0].iatacode.value = ' ';
		if(document.forms[0].accountcode.value.length == 0)
		document.forms[0].accountcode.value = ' ';
		if(document.forms[0].taxregno.value.length == 0)
		document.forms[0].taxregno.value = ' ';
        if (document.forms[0].emailId.value.length>0)
		if(!ValidateForm())
           return false;
		<%if (label.equals("Terminals")){%>
		if(document.forms[0].cbtflag.value=='Y')
		   {
		   if(document.forms[0].cbtlocationId.options[0].value=='(Select)')
			   {
			   alert("Please Select CBT Terminals");
               document.forms[0].CBTB4.focus();
			   return false;
			   }
		   }
        <%}%>
		return flag;
	}
	
 function createId(input)
 {
	input.value = input.value.toUpperCase();
	document.forms[0].terminalId.value =document.forms[0].companyId.value+input.value;
 }	

 function populateCompanyInfo()
 {
<%
	if( companyIds!=null )
	{
	  
	  if( (userTerminalType!=null && userTerminalType.equals("H")) || (loginbean.getAccessType()!=null && loginbean.getAccessType().equals("LICENSEE")))
	  {	
		for(int i=0; i<companyIds.length;i++)
		{
			String cId = companyIds[i].substring(0,companyIds[i].indexOf('#'));
			String tType = companyIds[i].substring(companyIds[i].indexOf('#')+1);
%>
			document.forms[0].companyId.options[<%=i%>] = new Option('<%=cId%>','<%=cId%>');
			arrayTerminalType[<%=i%>] = '<%=tType%>';
<%
		}
	  }
	  else
	  {
		for(int i=0; i<companyIds.length;i++)
		{
			String cId = companyIds[i].substring(0,companyIds[i].indexOf('#'));
			String tType = companyIds[i].substring(companyIds[i].indexOf('#')+1);
%>
			arrayTerminalType[<%=i%>] = '<%=tType%>';
<%
		}
%>
		document.forms[0].companyId.options[0] = new Option('<%=loginbean.getCompanyId()%>','<%=loginbean.getCompanyId()%>');
<%
	 }
%>		
		document.forms[0].companyId[0].selected = true;
		document.forms[0].terminalId.value =document.forms[0].companyId.options[0].value;
		selectTerminalType();
<%
	}
%>
	populateLocationInfo();
}
function selectTerminalType()
{
	idx = document.forms[0].companyId.selectedIndex;
	if(arrayTerminalType[idx] == 'C')
		document.forms[0].agentInd.value = 'COMPANY';
	else if(arrayTerminalType[idx] == 'A')
		document.forms[0].agentInd.value = 'AGENT';
	else if(arrayTerminalType[idx] == 'J')
		document.forms[0].agentInd.value = 'JOINT VENTURE';
	
	 if(terminalType!='O')
	 {
			var len1	= window.document.forms[0].locationId.length;
			var index	= 0;
			for(var i=0;i<len1;i++)
			{
				window.document.forms[0].locationId.options.remove(index);
			}
			document.forms[0].locationId.options[0] = new Option('(Select)','(Select)');
	 }		
		
}
function populateLocationInfo()
{
	for( i=0;i<arrayLocationIds.length;i++)
	{
		document.forms[0].locationIds.options[i] = new Option(arrayLocationIds[i],arrayLocationIds[i]);
	}
}

function changeLocationName()
{
		
 if(terminalType=='O')
 {		
	if(document.forms[0].locationId.options.length > 0)
	{
		tempLocationId = document.forms[0].locationId.options[document.forms[0].locationId.selectedIndex].value
		tempCompanyId =  document.forms[0].companyId.options[document.forms[0].companyId.selectedIndex].value
		strLocationId = tempLocationId.toString();
		locationId = strLocationId;
		strTerminalId = tempCompanyId.toString();
		document.forms[0].terminalId.value = strTerminalId+strLocationId;
<%
		if( vecLocationInfo!=null )
		{
			int size = vecLocationInfo.size();
			String strLocationId = null;
			for(int i=0;i<size;i++)
			{
				String temp = 	vecLocationInfo.elementAt(i).toString();
				int index1 = temp.indexOf("%");
				int index2 = temp.lastIndexOf("%");
				strLocationId = temp.substring(0,index1);
				locationName = temp.substring(index1+1,index2);
				locationName = locationName.replace('%','[')+"]";
%>
				if( locationId == '<%=strLocationId%>' )
				{
					document.forms[0].locationName.value='<%=locationName%>'
				}
<%
			}
		}
%>
	}
 }
 
}

function changeTerminalId()
{
	if(document.forms[0].locationId.options.length > 0)
	{
		tempLocationId	=	document.forms[0].locationId.options[document.forms[0].locationId.selectedIndex].value
		tempCompanyId	=	document.forms[0].companyId.options[document.forms[0].companyId.selectedIndex].value
		strLocationId	=	tempLocationId.toString();
		locationId		=	strLocationId;
		strTerminalId	=	tempCompanyId.toString();
		
		if(terminalType=='O')
		{
			if(strLocationId != "")
				document.forms[0].terminalId.value = strTerminalId+strLocationId;
		}
		else
		{
			if(strLocationId != "")
			{
				document.forms[0].terminalId.value = strTerminalId+document.forms[0].myId.value;
			}
			
		}
	 }
	else
	{
		document.forms[0].terminalId.value = document.forms[0].companyId.value+document.forms[0].myId.value;
	}
}
function showCountryIds()
{
		var Url      = 'ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId1.value.toUpperCase()+'&whereClause=CountryMaster';
		var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
		var Features =  Bars+''+Options;
		var Win      =  open(Url,'Doc',Features);
}
function showLocationIds()
{
	var len=document.forms[0].locationId.length;
	var id=document.forms[0].companyId.value;
	var countrylen=document.forms[0].countryId1.value;
	str = "";
	var fetchTerminalType = '';
	var shipmentTypeChecked = false;
    var shipmentMode		= "";

	for(var i=0;i<3;i++)
	{
	  if(document.forms[0].terminalMode[i].checked)
	  {
	     shipmentTypeChecked = true;
		 break;
	  }
	}

	if(countrylen=='')
	{
		alert("Please Select CountryId");
		document.forms[0].countryId1.focus();
		return false;
	}
    
	if(!document.forms[0].terminalMode[0].checked && !document.forms[0].terminalMode[1].checked && !document.forms[0].terminalMode[2].checked)
	{
		alert('Please Select the shipment mode');
		return false;
	}
    	
	
	if(document.forms[0].terminalMode[0].checked && document.forms[0].terminalMode[1].checked && document.forms[0].terminalMode[2].checked)
	{
	   shipmentMode = " WHERE SHIPMENTMODE=7";
	}
    if(document.forms[0].terminalMode[0].checked && document.forms[0].terminalMode[1].checked && !document.forms[0].terminalMode[2].checked) 
	{
	   shipmentMode = "WHERE SHIPMENTMODE IN (3,7)";
	} 
    if(document.forms[0].terminalMode[1].checked && document.forms[0].terminalMode[2].checked && !document.forms[0].terminalMode[0].checked)
	{
	   shipmentMode = "WHERE SHIPMENTMODE IN (6,7)";
	} 
    if(document.forms[0].terminalMode[0].checked && document.forms[0].terminalMode[2].checked &&
	  !document.forms[0].terminalMode[1].checked)
	{
	   shipmentMode = "WHERE SHIPMENTMODE IN (5,7)";
	}

    if(document.forms[0].terminalMode[0].checked && !document.forms[0].terminalMode[1].checked && !document.forms[0].terminalMode[2].checked)
	{
	  shipmentMode = " WHERE SHIPMENTMODE IN (1,3,5,7)";
	}
	if(document.forms[0].terminalMode[1].checked && !document.forms[0].terminalMode[0].checked &&
	   !document.forms[0].terminalMode[2].checked)
	{
	   shipmentMode = "WHERE SHIPMENTMODE IN (2,3,6,7)";
	}
    if(document.forms[0].terminalMode[2].checked && !document.forms[0].terminalMode[0].checked &&
	   !document.forms[0].terminalMode[1].checked)
    {
	  shipmentMode = "WHERE SHIPMENTMODE IN (4,5,6,7)";
	}

	
	
	for(i=0;i<len;i++)
	{
		if(document.forms[0].locationId.options[i])
		{
			if(document.forms[0].locationId.options[i].value != '(Select)')
			{
				str = str + "-" + document.forms[0].locationId.options[i].value ;
			}
		}
	}
	//Commented By JS for terminal Registraion Add/Modify  var  Url      = 'ETCTerminalRegistrationLocationIdsLOV.jsp?idLike='+id+'&adminType='+adminType+'&terminalType='+terminalType+'&flag=Add&terminalid=Nun&loc='+str+'&shipmentMode='+shipmentMode ;

	//Added by I.V.Sekhar to fetch Specific terminals
	if(document.forms[0].adminRterminals!=null && terminalType!='O')
	{
		if(!document.forms[0].adminRterminals[0].checked && !document.forms[0].adminRterminals[1].checked)
		{	alert("Please,select terminal type");
			document.forms[0].adminRterminals[0].focus();
			return false;
		}else
		{
			if(document.forms[0].adminRterminals[0].checked)
			{	fetchTerminalType="A";document.forms[0].adminROTerminal.value='A';}
			else if(document.forms[0].adminRterminals[1].checked)
			{	fetchTerminalType="O";document.forms[0].adminROTerminal.value='O';}
		}
	}
	var Url      = 'ETCTerminalRegistrationLocationIdsLOV.jsp?idLike=&adminType='+adminType+'&terminalType='+terminalType+'&flag=Add&terminalid=Nun&loc='+str+'&shipmentMode='+shipmentMode+'&fetchTerminalType='+fetchTerminalType+'&countryId='+document.forms[0].countryId1.value+'&companyId='+document.forms[0].companyId.value;
	var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
	var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
	var Features =  Bars+''+Options;
	var Win      =  open(Url,'Doc',Features);
}
function checkSpecialKeyCode()
{
	if(event.keyCode == 96 || event.keyCode == 59 || event.keyCode == 39 || event.keyCode == 34)
	{
		return false;
	}
	return true;
}
function changeToUpper(field)
{
	field.value = field.value.toUpperCase();
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
function getNumberCode()
{
	if(event.keyCode!=13)
	{	
     if((event.keyCode < 48  || event.keyCode > 57) )
 	   return false;	
	}
	return true;	
}

function checkLength(input,label)
{
 if(operation=="Add")
 {
    if(input.value.length < 4)
	{
		alert(label+" should be between (4-50) characters ");
		input.focus();
		return false;
	}
	if(!specialCharFilter(input,label))
	 return false;
 }
return true;
}

function getAlphaNumeric()
{
   if(event.keyCode!=13)
	{	
     if((event.keyCode < 48 ||event.keyCode > 57) && (event.keyCode < 65 || event.keyCode > 90) && (event.keyCode < 97 || event.keyCode > 122) )
 	   return false;	
	}
	return true;		
}


function setShipmentMode(obj)
{
  var objName = obj.name;
  var objValue = obj.value;
  var shipmentValue = document.forms[0].shipmentMode.value;
  if(obj.checked)
	document.forms[0].shipmentMode.value = parseInt(shipmentValue) + parseInt(obj.value);
  else
	document.forms[0].shipmentMode.value = parseInt(shipmentValue) - parseInt(obj.value);

}
//Added By RajKumari on 11/28/2008 for 146448 starts..
function setDefaultQuoteDetail(obj)
{

  var objName = obj.name;

  if(objName=='frequency')
  {
  if(obj.checked)
	  {
	
	document.forms[0].frequency.value = 'on';
	  }
  else
	  {
	    
	document.forms[0].frequency.value = 'off';
	  }
  }

  if(objName=='carrier')
  {
  if(obj.checked)
	document.forms[0].carrier.value = 'on';
  else
	document.forms[0].carrier.value = 'off';
  }

  if(objName=='transittime')
  {
  if(obj.checked)
	document.forms[0].transittime.value = 'on';
  else
	document.forms[0].transittime.value = 'off';
  }

  if(objName=='rateValidity')
  {
  if(obj.checked)
	document.forms[0].rateValidity.value = 'on';
  else
	document.forms[0].rateValidity.value = 'off';
  }

}
//Added By RajKumari on 11/28/2008 for 146448 ends....
function echeck(str) {

		var at="@"
		var dot="."
		var lat=str.indexOf(at)
		var lstr=str.length
		var ldot=str.indexOf(dot)
		if (str.indexOf(at)==-1){
		   alert("Invalid E-mail ID")
		   return false
		}

		if (str.indexOf(at)==-1 || str.indexOf(at)==0 || str.indexOf(at)==lstr){
		   alert("Invalid E-mail ID")
		   return false
		}

		if (str.indexOf(dot)==-1 || str.indexOf(dot)==0 || str.indexOf(dot)==lstr){
		    alert("Invalid E-mail ID")
		    return false
		}

		 if (str.indexOf(at,(lat+1))!=-1){
		    alert("Invalid E-mail ID")
		    return false
		 }

		 if (str.substring(lat-1,lat)==dot || str.substring(lat+1,lat+2)==dot){
		    alert("Invalid E-mail ID")
		    return false
		 }

		 if (str.indexOf(dot,(lat+2))==-1){
		    alert("Invalid E-mail ID")
		    return false
		 }
		
		 if (str.indexOf(" ")!=-1){
		    alert("Invalid E-mail ID")
		    return false
		 }

 		 return true					
	}

function ValidateForm(){
	var emailID=document.forms[0].emailId
	
	if ((emailID.value==null)||(emailID.value=="")){
		alert("Please Enter your Email ID")
		emailID.focus()
		return false
	}
	if (echeck(emailID.value)==false){
		emailID.value=""
		emailID.focus()
		return false
	}
	return true
 }
<%if (label.equals("Terminals")){%>
	function changeToCBT()
	{
   
	if(document.forms[0].locationId.options[0].value=='(Select)')
		{
		  alert("Please Select Terminals");
		  document.forms[0].cbt.checked = false;
          document.forms[0].cbtflag.value   = 'N';
		}
	else
		 if(document.forms[0].cbt.checked)
		        {
				document.forms[0].CBTB4.disabled=false;
				document.forms[0].cbtflag.value   = 'Y';
				}
			else
		        {
					   document.forms[0].CBTB4.disabled=true;	 
					   document.forms[0].cbtflag.value   = 'N';
					   var len1= window.document.forms[0].cbtlocationId.options.length;
					   var index=0;
						for(var i=0;i<len1;i++)
						{
							window.document.forms[0].cbtlocationId.options.remove(index);
						}
						window.document.forms[0].cbtlocationId.options[0] = new Option('(Select)' ,'(Select)' )
				 }

  window.open;
	}
	
function showCBTLocationIds()
{
	var strCBT   = document.forms[0].locationIdHide.value;
	var strCBTTerminals ='';
	
	var len= window.document.forms[0].cbtlocationId.options.length;
	if (window.document.forms[0].cbtlocationId.options[0].value != '(Select)')
	{
		 strCBTTerminals = window.document.forms[0].cbtlocationId.options[0].value;
			for(var i=1;i<len;i++)
			{
				strCBTTerminals = strCBTTerminals + ','+ window.document.forms[0].cbtlocationId.options[i].value;
			}
	}
    var Url      = 'ETCCBTTerminalRegistrationLocationIdsLOV.jsp?loc='+strCBT+'&cbtloc='+strCBTTerminals;
	var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
	var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
	var Features =  Bars+''+Options;
	var Win      =  open(Url,'Doc',Features);
	}
 function cbtassignLocations()
{
	
	var len1= window.document.forms[0].cbtlocationId.options.length;
	var index=0;
	window.document.forms[0].cbtLocationIdHide.value='';
	for(var i=0;i<len1;i++)
	{
		window.document.forms[0].cbtlocationId.options.remove(index);
	}
	str = cbthf;
	if(str.length>0)
	{
		entries = str.split("-");
		for(i=0;i<entries.length;i++)
		{
			if(entries[i] != "-" && entries[i]!="")
			{
				window.document.forms[0].cbtlocationId.options[index] = new Option(entries[i] ,entries[i] );
				window.document.forms[0].cbtLocationIdHide.value = window.document.forms[0].cbtLocationIdHide.value + ','+entries[i];
				index++;
			}
		}
		len = document.forms[0].cbtlocationId.options.length;
		
	}
}
function deselectValues()
{
			var locationId	=	document.forms[0].locationId;
			var flag		=	false;
			//alert(document.forms[0].adminROTerminal.value)
			if(document.forms[0].adminRterminals[0].checked && document.forms[0].adminROTerminal.value=='O')
			{	document.forms[0].adminROTerminal.value='A';flag=true;}
			else if(document.forms[0].adminRterminals[1].checked && document.forms[0].adminROTerminal.value=='A')
			{	document.forms[0].adminROTerminal.value='O';flag=true;}
			if(flag)
			{
				locationId.options.length = 0;
				locationId.options[0] = new Option('(Select)','(Select)');
			}

}
 <%}%>
//-->
function placeCountry()
{
 document.forms[0].countryId.value=document.forms[0].countryId1.value;
}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body  onLoad=loadValues()>
      <form method="POST" action="ETCTerminalRegistrationAddProcess.jsp" name="terminalreg" >
<table width="800" border="0" cellspacing="0" cellpadding="0">
<tr><td  bgcolor="ffffff">
<table width="800" border="0" cellspacing="1" cellpadding="4">
  <tr class='formlabel'>
    <td><table width="790" border="0" ><tr class='formlabel'><td>Terminal - Add</td><td align=right><%=loginbean.generateUniqueId("ETCTerminalRegistrationAdd.jsp","Add")%></td></tr></table></td>
  </tr></table>
<table width="800" border="0" cellspacing="1" cellpadding="4">
  <tr class='formdata' valign="top"><td > &nbsp;</tr></table>
        <table border="0" width="800" cellpadding="4" cellspacing="1" >
          <tr valign="top" class='formdata'>
<%
	if(terminalType==null || terminalType.equals("O"))
	{
%>
       <td colspan="2" width=20%>Terminal Id:<br>
		   <input type='text' class='text' name="terminalId" readonly size="16">
        </td>
<%
	}
	else
	{
%>
		<td colspan="2" width=20%>Terminal Id:<font color="#FF0000">*</font><br>
		   <input type='text' class='text' name="terminalId" size="10" readonly >
		   <input type='text' class='text' name="myId" size="5" maxlength=4 onBlur="createId(this)" onKeyPress="return getAlphaNumeric()">
        </td>
<%
	}
logger.info("hi");
%>			  
            <td width=15%>Abbr Name:<font color="#FF0000">*</font><br>
              
              <select name="companyId" class='select' onChange="changeTerminalId();selectTerminalType()">
              </select>
              </td>
            <td width=18%>Terminal Type:<br>
              <input type='text' class='text' name="agentInd" readOnly size="15">

              </td>
			   <td colspan="2">CountryId:<font color="#FF0000">*</font><br>
              <input type='text' class='text' name="countryId1" size="4" maxlength=2 onBlur="changeToUpper(this);placeCountry()" onKeypress="return checkSpecialKeyCode()">
              <input type="button" class='input' value="..." name="B4" onClick=showCountryIds() >
              </td>
            <td colspan="1" width="18%"><%=label%>:
			
			<font color="#FF0000">*</font><br>
              <select size="1" name="locationId" onChange="changeLocationName()" class='select'>
                <option value="(Select)" selected>(Select)</option>
              </select>&nbsp;<input type="button" class='input' value="..." name="B4"  onClick=showLocationIds() >
			   &nbsp;
<%
	if(terminalType!=null && !terminalType.equals("O"))
	{//Added by I.V.Sekhar to restrict assigning either Adimins or OTs ,cont be both.
%>
				<br><input type='radio' name='adminRterminals' onClick='deselectValues()'>Admin&nbsp;<input type='radio' name='adminRterminals' onClick='deselectValues()'>Operation
				<input type='hidden' name="adminROTerminal" value=''>
<%
	}
%>
<%
	if(terminalType==null || terminalType.equals("O"))
	{
%>			   
	   		<input type='text' class='text' name="locationName" size="30" readonly >
<%
	}
	else
	{
%>
 			<input type="hidden" name="locationName">
<%
	}
%>			 
              </td>
<%if (label.equals("Terminals")){%>
			  <td  width="19%"><input type=checkbox name =cbt  value ='Y' onClick='changeToCBT()'>CBT<br>Terminals:<font color="#FF0000">*</font><input type=hidden name =cbtflag  value ='N'>
			  <select size="1" name="cbtlocationId" class='select'>
                <option value="(Select)" selected>(Select)</option>
              </select>&nbsp;<input type="button" class='input' value="..." name="CBTB4"  onClick=showCBTLocationIds() disabled></td>  
<%}%>

          </tr>
        </table>
		
       <table border=0 cellPadding=4 cellSpacing=1  width=800>
        <tr vAlign=top class='formdata'>
          <td width="175" >IATA Code:
            <br><input type='text' class='text' maxlength="16" size="22" name="iatacode" onblur=changeToUpper(this) onkeypress="return checkSpecialKeyCode()" >
            </td>
			<td width="156" >Account Code:
            <br><input type='text' class='text' maxlength="16" size="20" name="accountcode" onblur=changeToUpper(this) onkeypress="return checkSpecialKeyCode()" >
			<td width="165" >Tax Reg No:
            <br><input type='text' class='text' maxlength="16" size="20" name="taxregno" onblur=changeToUpper(this) onkeypress="return checkSpecialKeyCode()" >
	           </td>
   			<td width="217">Shipment Mode:</font> <br>
                <input type="checkbox" name="terminalMode" value="1" onClick="setShipmentMode(this)">  Air</font>
				  <input type="checkbox" name="terminalMode" value="2" onClick="setShipmentMode(this)">  Sea
                <input type="checkbox" name="terminalMode" value="4" onClick="setShipmentMode(this)"> Truck</font>
   			</td>         
         </tr>
       </table>
<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
<%if(terminalType!=null &&( terminalType.equals("A")||terminalType.equals("H"))) {
	logger.info("hi");%>
	   <table border=0 cellPadding=4 cellSpacing=1  width=800>
	     <tr vAlign=top class='formdata'>
			<td width="650">Default Quote Detail:</font> <br>
			    &nbsp;&nbsp;
                <input type="checkbox" name="frequency"  onClick="setDefaultQuoteDetail(this)">  Frequency
			    &nbsp;&nbsp;
				<input type="checkbox" name="carrier" onClick="setDefaultQuoteDetail(this)">  Carrier
				&nbsp;&nbsp
                <input type="checkbox" name="transittime" onClick="setDefaultQuoteDetail(this)"> Approximate Transit Time
				&nbsp;&nbsp;
				<input type="checkbox" name="rateValidity"  onClick="setDefaultQuoteDetail(this)">  Freight Rate Validity
   			</td> <!-- Added By phani sekhar  for 170758 starts ON 20090626-->   
			<td>Margin Type:<br>
			 <select class="select"  name="marginType" >
						  <option value="A">Absolute</option>
						  <option value="P">Percent</option>
			</select>
			</td><td>Discount Type:<br>
			 <select class="select"  name="discountType" >
						  <option value="A">Absolute</option>
						  <option value="P">Percent</option>
			</select>
			</td>
      <!-- ends 170758 -->
		 </tr>
	   </table>
	   <% } %>
<!-- Added By RajKumari on 11/28/2008 for 146448 ends-->
		
      <table border=0 cellPadding=4 cellSpacing=1 width=800>
        <tr vAlign=top class='formdata'>
          <td colSpan=2  width="365">System/Non-System
             <font color=#ff0000>*</font><br>
            	<select class='select' name='terminalType'>
					<option value='S' selected>System</option>
					<% if (terminalType.equalsIgnoreCase("O")) { %>
						<option value='N'>Non-System</option>
					<%}%>
            	</select>
           </td>
		   <%//@@ Avinash commented on 20050206 (MULTI-UOM)
			/*<td colSpan=2  width="365">WeightScale
             <font color=#ff0000></font><br>
            	<select name='weightScale' class='select'>
					<option value='6000' selected>6000</option>
					<option value='7000'>7000</option>
            	</select>
           </td>
		   */
		   //@@ 20050206 (MULTI-UOM)%>
           <td colspan="2">Time Zone:<br>
           <select size="1" name="timeZone" class='select'>
            <option value="MIT">(GMT-11:00) West Samoa Time
			<option value="Pacific/Niue">(GMT-11:00) Niue Time
			<option value="Pacific/Pago_Pago">(GMT-11:00) Samoa Standard Time
			<option value="America/Adak">(GMT-10:00) Hawaii-Aleutian Daylight Time
			<option value="HST">(GMT-10:00) Hawaii Standard Time
			<option value="Pacific/Fakaofo">(GMT-10:00) Tokelau Time
			<option value="Pacific/Rarotonga">(GMT-10:00) Cook Is. Time
			<option value="Pacific/Tahiti">(GMT-10:00) Tahiti Time
			<option value="Pacific/Marquesas">(GMT-09:30) Marquesas Time
			<option value="AST">(GMT-09:00) Alaska Daylight Time
			<option value="Pacific/Gambier">(GMT-09:00) Gambier Time
			<option value="America/Los_Angeles" selected>(GMT-08:00) Pacific Daylight Time
			<option value="Pacific/Pitcairn">(GMT-08:00) Pitcairn Standard Time
			<option value="America/Dawson_Creek">(GMT-07:00) Mountain Standard Time
			<option value="America/Denver">(GMT-07:00) Mountain Daylight Time
			<option value="America/Belize">(GMT-06:00) Central Standard Time
			<option value="America/Chicago">(GMT-06:00) Central Daylight Time
			<option value="Pacific/Easter">(GMT-05:00) Easter Is. Summer Time
			<option value="Pacific/Galapagos">(GMT-06:00) Galapagos Time
			<option value="America/Bogota">(GMT-05:00) Colombia Time
			<option value="America/Cayman">(GMT-05:00) Eastern Standard Time
			<option value="America/Grand_Turk">(GMT-05:00) Eastern Daylight Time
			<option value="America/Guayaquil">(GMT-05:00) Ecuador Time
			<option value="America/Havana">(GMT-05:00) Central Daylight Time
			<option value="America/Lima">(GMT-05:00) Peru Time
			<option value="America/Porto_Acre">(GMT-05:00) Acre Time
			<option value="America/Rio_Branco">(GMT-05:00) GMT-05:00
			<option value="America/Anguilla">(GMT-04:00) Atlantic Standard Time
			<option value="America/Asuncion">(GMT-03:00) Paraguay Summer Time
			<option value="America/Caracas">(GMT-04:00) Venezuela Time
			<option value="America/Cuiaba">(GMT-03:00) Amazon Summer Time
			<option value="America/Guyana">(GMT-04:00) Guyana Time
			<option value="America/Halifax">(GMT-04:00) Atlantic Daylight Time
			<option value="America/La_Paz">(GMT-04:00) Bolivia Time
			<option value="America/Manaus">(GMT-04:00) Amazon Standard Time
			<option value="America/Santiago">(GMT-03:00) Chile Summer Time
			<option value="Atlantic/Stanley">(GMT-03:00) Falkland Is. Summer Time
			<option value="America/St_Johns">(GMT-03:30) Newfoundland Daylight Time
			<option value="AGT">(GMT-03:00) Argentine Time
			<option value="America/Cayenne">(GMT-03:00) French Guiana Time
			<option value="America/Fortaleza">(GMT-03:00) Brazil Time
			<option value="America/Godthab">(GMT-03:00) Western Greenland Summer Time
			<option value="America/Miquelon">(GMT-03:00) Pierre & Miquelon Daylight Time
			<option value="America/Montevideo">(GMT-03:00) Uruguay Time
			<option value="America/Paramaribo">(GMT-03:00) Suriname Time
			<option value="America/Sao_Paulo">(GMT-02:00) Brazil Summer Time
			<option value="America/Noronha">(GMT-02:00) Fernando de Noronha Time
			<option value="Atlantic/South_Georgia">(GMT-02:00) South Georgia Standard Time
			<option value="America/Scoresbysund">(GMT-01:00) Eastern Greenland Summer Time
			<option value="Atlantic/Azores">(GMT-01:00) Azores Summer Time
			<option value="Atlantic/Cape_Verde">(GMT-01:00) Cape Verde Time
			<option value="Atlantic/Jan_Mayen">(GMT-01:00) Eastern Greenland Time
			<option value="Africa/Abidjan">(GMT+00:00) Greenwich Mean Time
			<option value="Africa/Casablanca">(GMT+00:00) Western European Time
			<option value="Atlantic/Canary">(GMT+00:00) Western European Summer Time
			<option value="Europe/Dublin">(GMT+00:00) Irish Summer Time
			<option value="Europe/London">(GMT+00:00) British Summer Time
			<option value="UTC">(GMT+00:00) Coordinated Universal Time
			<option value="Africa/Algiers">(GMT+01:00) Central European Time
			<option value="Africa/Bangui">(GMT+01:00) Western African Time
			<option value="Africa/Windhoek">(GMT+02:00) Western African Summer Time
			<option value="ECT">(GMT+01:00) Central European Summer Time
			<option value="ART">(GMT+02:00) Eastern European Summer Time
			<option value="Africa/Blantyre">(GMT+02:00) Central African Time
			<option value="Africa/Johannesburg">(GMT+02:00) South Africa Standard Time
			<option value="Africa/Tripoli">(GMT+02:00) Eastern European Time
			<option value="Asia/Jerusalem">(GMT+02:00) Israel Daylight Time
			<option value="Africa/Addis_Ababa">(GMT+03:00) Eastern African Time
			<option value="Asia/Aden">(GMT+03:00) Arabia Standard Time
			<option value="Asia/Baghdad">(GMT+03:00) Arabia Daylight Time
			<option value="Europe/Moscow">(GMT+03:00) Moscow Daylight Time
			<option value="Asia/Tehran">(GMT+03:30) Iran Sumer Time
			<option value="Asia/Aqtau">(GMT+04:00) Aqtau Summer Time
			<option value="Asia/Baku">(GMT+04:00) Azerbaijan Summer Time
			<option value="Asia/Dubai">(GMT+04:00) Gulf Standard Time
			<option value="Asia/Tbilisi">(GMT+04:00) Georgia Summer Time
			<option value="Asia/Yerevan">(GMT+04:00) Armenia Summer Time
			<option value="Europe/Samara">(GMT+04:00) Samara Summer Time
			<option value="Indian/Mahe">(GMT+04:00) Seychelles Time
			<option value="Indian/Mauritius">(GMT+04:00) Mauritius Time
			<option value="Indian/Reunion">(GMT+04:00) Reunion Time
			<option value="Asia/Kabul">(GMT+04:30) Afghanistan Time
			<option value="Asia/Aqtobe">(GMT+05:00) Aqtobe Summer Time
			<option value="Asia/Ashgabat">(GMT+05:00) Turkmenistan Time
			<option value="Asia/Bishkek">(GMT+05:00) Kirgizstan Summer Time
			<option value="Asia/Dushanbe">(GMT+05:00) Tajikistan Time
			<option value="Asia/Karachi">(GMT+05:00) Pakistan Time
			<option value="Asia/Tashkent">(GMT+05:00) Uzbekistan Time
			<option value="Asia/Yekaterinburg">(GMT+05:00) Yekaterinburg Summer Time
			<option value="Indian/Chagos">(GMT+05:00) Indian Ocean Territory Time
			<option value="Indian/Kerguelen">(GMT+05:00) French Southern & Antarctic Lands Time
			<option value="Indian/Maldives">(GMT+05:00) Maldives Time
			<option value="Asia/Calcutta">(GMT+05:30) India Standard Time
			<option value="Asia/Katmandu">(GMT+05:45) Nepal Time
			<option value="Antarctica/Mawson">(GMT+06:00) Mawson Time
			<option value="Asia/Almaty">(GMT+06:00) Alma-Ata Summer Time
			<option value="Asia/Colombo">(GMT+06:00) Sri Lanka Time
			<option value="Asia/Dacca">(GMT+06:00) Bangladesh Time
			<option value="Asia/Novosibirsk">(GMT+06:00) Novosibirsk Summer Time
			<option value="Asia/Thimbu">(GMT+06:00) Bhutan Time
			<option value="Asia/Rangoon">(GMT+06:30) Myanmar Time
			<option value="Indian/Cocos">(GMT+06:30) Cocos Islands Time
			<option value="Asia/Bangkok">(GMT+07:00) Indochina Time
			<option value="Asia/Jakarta">(GMT+07:00) Java Time
			<option value="Asia/Krasnoyarsk">(GMT+07:00) Krasnoyarsk Summer Time
			<option value="Indian/Christmas">(GMT+07:00) Christmas Island Time
			<option value="Antarctica/Casey">(GMT+08:00) Western Standard Time (Australia)
			<option value="Asia/Brunei">(GMT+08:00) Brunei Time
			<option value="Asia/Hong_Kong">(GMT+08:00) Hong Kong Time
			<option value="Asia/Irkutsk">(GMT+08:00) Irkutsk Summer Time
			<option value="Asia/Kuala_Lumpur">(GMT+08:00) Malaysia Time
			<option value="Asia/Macao">(GMT+08:00) China Standard Time
			<option value="Asia/Manila">(GMT+08:00) Philippines Time
			<option value="Asia/Singapore">(GMT+08:00) Singapore Time
			<option value="Asia/Ujung_Pandang">(GMT+08:00) Borneo Time
			<option value="Asia/Ulaanbaatar">(GMT+08:00) Ulaanbaatar Time
			<option value="Asia/Jayapura">(GMT+09:00) Jayapura Time
			<option value="Asia/Pyongyang">(GMT+09:00) Korea Standard Time
			<option value="Asia/Tokyo">(GMT+09:00) Japan Standard Time
			<option value="Asia/Yakutsk">(GMT+09:00) Yaktsk Summer Time
			<option value="Pacific/Palau">(GMT+09:00) Palau Time
			<option value="ACT">(GMT+09:30) Central Standard Time (Northern Territory)
			<option value="Australia/Adelaide">(GMT+10:30) Central Summer Time (South Australia)
			<option value="Australia/Broken_Hill">(GMT+10:30) Central Summer Time (South Australia/New South Wales)
			<option value="AET">(GMT+11:00) Eastern Summer Time (New South Wales)
			<option value="Antarctica/DumontDUrville">(GMT+10:00) Dumont-d'Urville Time
			<option value="Asia/Vladivostok">(GMT+10:00) Vladivostok Summer Time
			<option value="Australia/Brisbane">(GMT+10:00) Eastern Standard Time (Queensland)
			<option value="Australia/Hobart">(GMT+11:00) Eastern Summer Time (Tasmania)
			<option value="Pacific/Guam">(GMT+10:00) Chamorro Standard Time
			<option value="Pacific/Port_Moresby">(GMT+10:00) Papua New Guinea Time
			<option value="Pacific/Truk">(GMT+10:00) Truk Time
			<option value="Australia/Lord_Howe">(GMT+11:30) Load Howe Summer Time
			<option value="Asia/Magadan">(GMT+11:00) Magadan Summer Time
			<option value="Pacific/Efate">(GMT+11:00) Vanuatu Time
			<option value="Pacific/Guadalcanal">(GMT+11:00) Solomon Is. Time
			<option value="Pacific/Kosrae">(GMT+11:00) Kosrae Time
			<option value="Pacific/Noumea">(GMT+11:00) New Caledonia Time
			<option value="Pacific/Ponape">(GMT+11:00) Ponape Time
			<option value="Pacific/Norfolk">(GMT+11:30) Norfolk Time
			<option value="Antarctica/McMurdo">(GMT+13:00) New Zealand Daylight Time
			<option value="Asia/Anadyr">(GMT+12:00) Anadyr Summer Time
			<option value="Asia/Kamchatka">(GMT+12:00) Petropavlovsk-Kamchatski Summer Time
			<option value="Pacific/Fiji">(GMT+12:00) Fiji Time
			<option value="Pacific/Funafuti">(GMT+12:00) Tuvalu Time
			<option value="Pacific/Majuro">(GMT+12:00) Marshall Islands Time
			<option value="Pacific/Nauru">(GMT+12:00) Nauru Time
			<option value="Pacific/Tarawa">(GMT+12:00) Gilbert Is. Time
			<option value="Pacific/Wake">(GMT+12:00) Wake Time
			<option value="Pacific/Wallis">(GMT+12:00) Wallis & Futuna Time
			<option value="Pacific/Chatham">(GMT+13:45) Chatham Daylight Time
			<option value="Pacific/Enderbury">(GMT+13:00) Phoenix Is. Time
			<option value="Pacific/Tongatapu">(GMT+13:00) Tonga Time
			<option value="Pacific/Kiritimati">(GMT+14:00) Line Is. Time
          </select>
         </td>
       </tr>
		 
<%
	if(terminalType==null || terminalType.equals("O"))
	{
%>
          <tr valign="top" class='formdata'>
            <td colspan="2" width="365">EMail Status:<br>
	           <select size="1" name="emailStatus" class='select'>
                 <option value='Y' selected>Yes</option>
                 <option value='N'>No</option>
			   </select>
			</td>		  
            <td colspan="2" width="365">Collect Shipment :<br>
	           <select size="1" name="collectShipment" class='select'>
                 <option value='CCA' selected>CC Allowed</option>
                 <option value='CCN'>CC Not Allowed</option>
			   </select>
			</td>		  
            <td colspan="2"></td>
			</tr>
<%
	}
%>
     <% //@@ Srivegi Added on 20050419 (Invoice-PR)%>
	 

	 <tr valign="top" class='formdata'>
           <td>
			<input type="radio" name="stockedInvoiceIds" value="N" checked>System Invoice Ids
			<input type="radio" name="stockedInvoiceIds" value="Y" >Invoices from Stock
   			</td>         
            <td colspan="132"></td>
	  </tr>
	 
      <%//@@ 20050419 (Invoice-PR)
	%>
        </table>
  
		 
       <table border="0" width="800" cellpadding="4" cellspacing="1" >
          <tr valign="top" class='formdata'>
            <td colspan="2" width="365">Contact
              Person:<br>
              <input type='text' class='text' name="contactName" size="50" maxlength="50" onBlur=changeToUpper(contactName) onKeypress="return checkSpecialKeyCode()">
              </td>
          <td colSpan=2  width="365">Company Name:<font color=#ff0000>*</font><br>
				<input type='text' class='text' maxlength="50" size="50" name="notes" onblur=changeToUpper(notes) onkeypress="return checkSpecialKeyCode()" >
           </td>
          </tr>
        </table>
        
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class='formdata'>
            <td colspan="2"  rowspan="2">Address:<font color="#FF0000">*</font><br>
              <input type='text' class='text' name="addressLine1" maxlength=65 size="50" onBlur=changeToUpper(addressLine1) onKeypress="return checkSpecialKeyCode()">
              	<br>
              <input type='text' class='text' name="addressLine2" maxlength=65 size="50" onBlur=changeToUpper(addressLine2) onKeypress="return checkSpecialKeyCode()">
            </td>
            <td colspan="2" width="365">Designation:<br>
              <input type='text' class='text' name="designation" size="50" maxlength="50" onBlur=changeToUpper(designation) onKeypress="return checkSpecialKeyCode()">
            </td>
          </tr>
          
          <tr class='formdata'>
            <td colspan="2"  width="357">Zip
              or Postal Code:<br>
              <input type='text' class='text' name="zipCode" size="15" maxlength=10 onBlur=changeToUpper(zipCode) onKeypress="return checkSpecialKeyCode()">
              </td>
		   </tr>
		  
          <tr valign="top" class='formdata'>
            <td colspan="2" >City:<font color="#FF0000">*</font><br>
              <input type='text' class='text' name="city" size="30" maxlength=30 onBlur=changeToUpper(city) onKeypress="return checkSpecialKeyCode()">
              </td>
            <td colspan="2"  width="357">Contact
              No:<br>
              <input type='text' class='text' name="phoneNo" size="20" maxlength=20 onBlur=changeToUpper(phoneNo) onKeypress="return checkSpecialKeyCode()">
              </td>
            
          </tr>
          <tr valign="top" class='formdata'>
            <td colspan="2">State
              or Province:<br>
              <input type='text' class='text' name="state" size="30" maxlength=30 onBlur=changeToUpper(state) onKeypress="return checkSpecialKeyCode()">
              </td>
			  <td colspan="2" width="357">Fax
              No:<br>
              <input type='text' class='text' name="fax" size="20" maxlength=20 onBlur=changeToUpper(fax) onKeypress="return checkSpecialKeyCode()">
              </td>
            
          </tr>
          <tr valign="top" class='formdata'>
            <td colspan="2">CountryId:<font color="#FF0000">*</font><br>
              <input type='text' class='text' name="countryId" size="4" maxlength=2 onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()" readOnly>
              
			  <td colspan="2" width="357">Email
              Id: <br>
              <input type='text' class='text' name="emailId" size="50" maxlength="50"  onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()">
              </td>
            
          </tr>
        </table>
             
        <table width="800" border="0" cellspacing="1" cellpadding="1">
          <tr class='formheader'> 
            <td colspan="2">Bank Details :</td>
          </tr>
          <tr class='formdata'> 
            <td  width="50%">Bank 
              Account Number :<br>
              <input type='text' class='text' name="accountNumber" size="30" maxlength="24" onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()">
             </td>
              <td width="50%" >Name of the Bank 
              :<br>
              <input type='text' class='text' name="bankName" size="50" maxlength="60" onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()">
              </td>
          </tr>
          <tr class='formdata'> 
            <td  width="50%">Branch 
              Name :<br>
              <input type='text' class='text' name="branchName" size="50" maxlength="60" onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()">
              </td>
            <td  width="50%">Type Of Invoice :<br>
			<input type='text' class='text' name="invoiceCategory" size="20" maxlength="16" onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()"></td>
          </tr>
          <tr class='formdata'> 
            <td  width="50%">Address 
              of the Bank :<br>
              <input type='text' class='text' name="bankAddress" size="50" maxlength="60" onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()">
              </font></td>
            <td  width="50%">City 
              :<br>
              <input type='text' class='text' name="bankCity" size="50" maxlength="60" onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()">
              </font></td>
          </tr>
          <tr class='formdata'> 
            <td  width="50%">Days 
              Allowed for Discrepancy :<br>
              <input type='text' class='text' name="discrepancy" size="5" maxlength="2" onKeypress="return getNumberCode()">
              </font></td>
            <td  width="50%">Over 
              Due Interest in % per Month :<br>
              <input type='text' class='text' name="intrestRate" size="5" maxlength="3" onKeypress="return getDotNumberCode(this)">
              </font></td>
          </tr>
        </table>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" ><font color="#FF0000">*</font>Denotes
                Mandatory
            </td>
            <td valign="top" ></td>
            <td valign="top" ></td>
            <td valign="top"  align="right">
			      <input type="hidden" name="terminalhide">
				 <input type="hidden" name="locationIdHide" >
				 <input type="hidden" name="cbtLocationIdHide" >
				 <input type="hidden" name="opTerminalType" value='<%=terminalType%>'>
				 <input type="hidden" name="adminType" value='<%=adminType%>'>
				 <input type="hidden" name="shipmentMode" value = "0">
				 
				 <input type="hidden" name="Operation" value='<%=request.getParameter("Operation")%>'>
                <input type="Submit" class='input' value="Submit" name="jbt_Test" onClick="return checkForNull()" >
				<%//Added by Sreelakshmi KVA - 20050411 SPETI-5533 //%>
				<input type="Reset" class="input" value="Reset" name = "Reset" >
				<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
				<input type="hidden" name="frequency" >
				<input type="hidden" name="carrier" >
				<input type="hidden" name="transittime">
				<input type="hidden" name="rateValidity">
                <!-- Added By RajKumari on 11/28/2008 for 146448 ends -->
            </td>
          </tr>
        </table>
               
            </td>
          </tr>
        </table>  
      </form>
    </td>
  </tr>
</table>
</body>
</html>
