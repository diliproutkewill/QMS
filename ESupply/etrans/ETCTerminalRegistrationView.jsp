<%
/*	Program Name	: ETCTerminalRegistrationView.jsp
	Module name		: HO Setup
	Task			: Adding Terminal
	Sub task		: to provide the view of the terminal requested
	Author Name		: C.L.N Saravana
	Date Started	: Feb 25, 2002
	Date completed	: Feb 27, 2002
	Description     :
		This file gives the view of the terminal information requested for View,Modify,Delete operations.On submission
		this page redirects to other pages corresponding to the operations.
*/
%>
<%@ page import = "	java.util.Vector,
					javax.naming.InitialContext,
				 	org.apache.log4j.Logger,					
					com.foursoft.etrans.setup.terminal.bean.TerminalRegJspBean,
					com.foursoft.etrans.common.bean.Address,
					com.foursoft.etrans.common.util.java.OperationsImpl,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>
					
<jsp:useBean id="vecLocationInfo" class="java.util.Vector" scope="session" />
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCTerminalRegistrationView.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	String terminalId         = null;    // String to store terminal Id
	String[] locationId       = null;    // String array to store location Ids
	String contactName        = null;    // String to store contact name
	String desg               = null;    // String to store designation
	String abbrName           = null;    // String to store abbreviated name
	String agentInd           = null;    // String to store agent Ind
	String notes              = null;    // String to store notes
	int     addressid         = 0;     // integer to store address id
//	Address address           = null;    // variable of Address class
	String  addressLine1      = null;    //  String to store addressLine1
	String  addressLine2      = null;    // String to store addressLine2
	String  city              = null;    // String to store city
	String  state             = null;    // String to store state
	String  zipCode           = null;    // String to store zip code
	String  countryId         = null;    // String to store country Id
	String  phoneNo           = null;    // String to store phone no
	String  emailId           = null;    // String to store email Id
	String  fax               = null;    // String to store fax no
	String value              = null;    // String to store value
	String agentValue         = null;    // String to store agent value
	String iatacode           = null;    // String to store IATA code
	String accountcode        = null;    // String to store account code
	String regno              = null;    // String to store tax reg no
	String bankName	          = null;
	String branchName         = null;
	String bankAddress        = null;
	String accountNo		  = null;
	String bankCity			  = null;
	int    discrepancy		  = 0;
	double interestRate	      = 0.0;
	String invoiceCategory    = null;
	String terminalType       = null; 	
	String strReadonly		  = "";     // String to store read only status
	String strDisable	      = "";     // String to store disable status
	String actionValue        = "";     // String to store  URL
	String strDisable1	      = "";     // String to store disable1
	String emailStatus		  = null;
	String collectShipment	  = null;
	String timeZone			  = null;
	String opTerminalType	  = null;
	String shipmentMode		  = null;
	String terminal			  = null;
	//@@ Avinash commented on 20050206 (MULTI-UOM)
	//int	   weightScale		  = 0;
	//@@ 20050206 (MULTI-UOM)
	Vector cbtLocationId      = null;
	//@@ Srivegi Added on 20050419 (Invoice-PR)
	String stockedInvoiceIdsCheck     = null;
	//@@ 20050419 (Invoice-PR)
	String adminROTerminal		=	null;	
	//<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
	String frequency  	    = null;
	String carrier  	    = null;
	String transittime  	= null;
	String rateValidity  	= null;
	//<!-- Added By RajKumari on 11/28/2008 for 146448 ends -->
	String discountType = null;
	String marginType = null;
%>

<%
	String label	= "Locations ";
	String readonly = "readonly";
	
	opTerminalType	= request.getParameter("terminalType");
	terminal		= request.getParameter("terminalId");
	session.setAttribute("terminalId",terminal);
	session.setAttribute("opTerminalType",opTerminalType);
	//if( (opTerminalType!=null && !opTerminalType.equals("O")) || (loginbean.getAccessType()!=null && loginbean.getAccessType().equals("LICENSEE") ))
  if( (opTerminalType!=null && !opTerminalType.equals("O")))
	{
		label 	 = "Terminals";
		readonly = "";
	}
		
	//Logger.info(FILE_NAME,"terminalType : "+opTerminalType);
	
	try
	{
	OperationsImpl operationsImpl = new  OperationsImpl();
	operationsImpl.createDataSource();
		// checking for the terminal
		if(loginbean.getTerminalId() == null )
		{
%>
			<jsp:forward page="../ESupplyLogin.jsp"/>
<%
			return ;
		}
	}
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Error in ETTerminalRegistrationView.jsp file : ", e.toString());
    logger.error(FILE_NAME+"Error in ETTerminalRegistrationView.jsp file : "+ e.toString());
	}
	String locationName   = null;    // String to store location name
//	String countryId      = null;    // String to store country id
	String companyIds[]   = null;    // String array to store companyIds
	String countryIds[]   = null;    // String array to store country ids
	String operation      = null;    // String to store type of operation
	InitialContext initialContext = null;    // variable to store InitialContext
	SetUpSessionHome terminalHome = null;    // variable to store Home Object
	SetUpSession terminalRemote = null;    // variable to store remote Object
	OperationsImpl operationsImpl = new OperationsImpl();
	operationsImpl.createDataSource();
	
	String str_cbtassignLocations	=""; //this is for onload assigning values

	try
	{
			operation = request.getParameter("Operation");
			initialContext = new InitialContext();
			terminalHome = (SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
			terminalRemote = (SetUpSession)terminalHome.create();
			// checking the type of operation
			if(operation != null && operation.equals("Modify"))
			{
				companyIds = terminalRemote.getCompanyIds();
				countryIds = operationsImpl.getCountryIds();
				session.setAttribute("CountryIds",countryIds);
			}
			if( operation.equals("View") )
			{
				strReadonly = "readonly";
				strDisable	= "disabled";
				actionValue = "ETCTerminalRegistrationEnterId.jsp?Operation=View";
			}
			else if( operation.equals("Modify") )
			{
				strReadonly = " ";
				strDisable	= " ";
				strDisable1 = " ";
				actionValue = "ETCTerminalRegistrationProcess.jsp";
			}
			else if( operation.equals("Delete") )
			{
				strReadonly = "readonly";
				strDisable	= "disabled";
				strDisable1 = " ";
				actionValue = "ETCTerminalRegistrationProcess.jsp";
			}
			
			
			
		String terminalId1 = request.getParameter("terminalId");//String to store terminal id
		InitialContext context = new InitialContext();    // variable to store InitialContext
		SetUpSessionHome trh = ( SetUpSessionHome )context.lookup("SetUpSessionBean");    // variable to store Home Object
		SetUpSession trr     = (SetUpSession)trh.create();     // variable to store Session Object
		//Logger.info(FILE_NAME," terminalId1 : "+terminalId1);
		//Logger.info(FILE_NAME," opTerminalType : "+opTerminalType);
		////loginbean is added by VLAKSHMI for  issue 173655 on 20090629
		TerminalRegJspBean terminalReg     = trr.getTerminalRegDetails(terminalId1,opTerminalType,loginbean);     // creating TerminalRegJspBean Object
		// if terminalReg is not null
		if( terminalReg != null)
		{
			terminalId        = terminalReg.getTerminalId();
			locationId        = terminalReg.getLocationId();
			cbtLocationId     = terminalReg.getCBTLocationId();
			abbrName          = terminalReg.getCompanyId();
			agentInd	      = terminalReg.getAgentInd ()  !=null ? terminalReg.getAgentInd ()   : "(Select)";
			contactName		  = terminalReg.getContactName()!=null ? terminalReg.getContactName() : "";
			desg		  	  = terminalReg.getDesignation()!=null ? terminalReg.getDesignation() : "";
			notes		  	  = terminalReg.getNotes()      !=null ? terminalReg.getNotes()       : "";
			iatacode          = terminalReg.getIataCode()   != null  ? terminalReg.getIataCode()  : "";
			accountcode       = terminalReg.getAccountCode()!= null  ? terminalReg.getAccountCode() : "";
			regno             = terminalReg.getTaxRegNo()   != null  ? terminalReg.getTaxRegNo()    : "";
			bankName          = terminalReg.getBankName()   != null  ? terminalReg.getBankName() : "";
			branchName        = terminalReg.getBranchName() !=null   ?  terminalReg.getBranchName():"";
			accountNo		  = terminalReg.getAccountNumber()	!=null   ?  terminalReg.getAccountNumber():"";
			bankAddress       = terminalReg.getBankAddress()!=null   ?  terminalReg.getBankAddress():"";
			bankCity          = terminalReg.getBankCity()   !=null   ?  terminalReg.getBankCity():"";
			discrepancy       = terminalReg.getDiscrepancy();
			interestRate      = terminalReg.getInterestRate();
			invoiceCategory   = terminalReg.getInvoiceCategory()!=null?  terminalReg.getInvoiceCategory():"";
			terminalType      = terminalReg.getTerminalType()!=null? terminalReg.getTerminalType():"";
			emailStatus		  =	terminalReg.getEmailStatus();
			timeZone		  =	terminalReg.getTimeZone();
			collectShipment   = terminalReg.getCollectShipment();
			//opTerminalType	  =	terminalReg.getOperationTerminalType();
	  		shipmentMode	  = terminalReg.getShipmentMode();
			//@@ Avinash commented on 20050206 (MULTI-UOM)
			//weightScale		  = terminalReg.getWeightScale();
			//@@ 20050206 (MULTI-UOM)       			 
			addressid         = terminalReg.getContactAddressId();
			Address address   = terminalReg.getAddress();
			//@@ Srivegi Added on 20050419 (Invoice-PR)
		    stockedInvoiceIdsCheck         = terminalReg.getStockedInvoiceIdsCheck();
			//@@ 20050419 (Invoice-PR)
			adminROTerminal					=	terminalReg.getChildTerminalFlag();//Added By I.V.Sekhar
			//<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
			frequency	  = terminalReg.getFrequency();
			carrier	      = terminalReg.getCarrier();
			transittime	  = terminalReg.getTransitTime();
			rateValidity	  = terminalReg.getRateValidity();
			//<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
	     discountType = terminalReg.getDiscountType();
			marginType = terminalReg.getMarginType();
	
			//Logger.info(FILE_NAME,"Fields are :" + iatacode+"::::"+accountcode+"::::"+regno);
			//Logger.info(FILE_NAME,"Account Number is :" + terminalReg.getAccountNumber());
			if( address != null)
			{
				addressLine1    = address.getAddressLine1();
				addressLine2	= address.getAddressLine2()!=null ? address.getAddressLine2() : "";
				city	 		= address.getCity()!=null ? address.getCity() : "";
				state	 		= address.getState()!=null ? address.getState() : "";
				zipCode	 		= address.getZipCode()!=null ? address.getZipCode() : "";
				countryId       = address.getCountryId();
				phoneNo	 	    = address.getPhoneNo()!=null ? address.getPhoneNo() : "";
				emailId	 	    = address.getEmailId()!=null ? address.getEmailId() : "";
				fax	 		    = address.getFax()!=null ? address.getFax() : "";
			}

			vecLocationInfo = terminalRemote.getLocationInfo("","","");    // populating Vector with location information
			
			session.setAttribute("remote",trr);
%>
<head>
<title>Terminal Registration</title>
<script language="JavaScript">
var terminalType = '<%=opTerminalType%>';
var operation    = '<%=operation%>';
var shipmentMode = '<%=shipmentMode%>';
var terminalId	 = '<%=terminal%>';
//<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
var frequency	  = '<%=frequency%>';
var	carrier	      = '<%=carrier%>';
var	transittime	  = '<%=transittime%>';
var	rateValidity	  = '<%=rateValidity%>';
//<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
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
	entries = str.split("-");
	for(i=0;i<entries.length;i++)
	{
		if(entries[i] != "-" && entries[i]!="")
		{
			window.document.forms[0].locationId.options[index] = new Option(entries[i] ,entries[i] )
			index++;
		}
	}
	changeLocationName();
}
	arrayCountryIds  = new Array();
function loadValues()
	{
<%
		if( countryIds!=null )
		{
		int len = countryIds.length;
		for( int i=0; i<len; i++ )
		{
			String str = countryIds[i].trim();
			int idx = str.indexOf('(');
			String cId = countryIds[i].substring(idx+2,idx+4);
%>
			arrayCountryIds[<%=i%>] = '<%=cId%>';
<%
		}
		}
%>
		setTerminalType();
		placeFocus();
	}
function setTerminalType()
	{
		varAgentInd = '<%=agentInd%>';
		<%//@@ Avinash commented on 20050206 (MULTI-UOM)
		//var weightScale	=	'<= weightScale>';
		//@@ 20050206 (MULTI-UOM)%>
		if(varAgentInd == 'c')
			document.forms[0].agentInd.value = 'COMPANY';
		else if(varAgentInd == 'a')
			document.forms[0].agentInd.value = 'AGENT';
		else if(varAgentInd == 'j')
			document.forms[0].agentInd.value = 'JOINT VENTURE';
		<%//@@ Avinash commented on 20050206 (MULTI-UOM)	
		//document.forms[0].weightScale.value	=	weightScale;	
		//@@ 20050206 (MULTI-UOM)%>
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
		flag = true;
		var label = " Location Id(s)";
		if(terminalType=='A')
			label = " Terminal Id(s)";
			
		for(i = 0; i < field.length; i++)
		{
			var varType  = field.elements[i].type;
			var varValue = field.elements[i].value;
			var varName  = field.elements[i].name;

			if( varType.toString().charAt(0)=="s" &&  varValue=="(Select)" && ( varName=="locationId"))
			{
				errorMessage = "Please select"+label;
				flag = false;
				alert(errorMessage);
				field.elements[i].focus()
				return flag;
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
			
			
		if(document.forms[0].shipmentMode.value == 0)
	    {
		 alert("Please select atleast one Shipment Mode");
		 return false;
		}
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

	function placeFocus()
	{
		if( document.forms.length > 0 )
		{
			var field = document.forms[0];
			for(i = 0; i < field.length; i++)
			{
				if( field.elements[i].name == "contactName" )
				{
					document.forms[0].elements[i].focus();
					break;
				}
			}
		}

	  shipmentMode = '<%= shipmentMode%>'
      
	  document.forms[0].shipmentMode.value = shipmentMode;

	  if(shipmentMode == '1' || shipmentMode == '3' ||shipmentMode == '5' ||shipmentMode == '7')
	  {
		document.forms[0].terminalMode[0].checked = true;
		document.forms[0].terminalMode[0].disabled = true;
	  }

	  if(shipmentMode == '2' || shipmentMode == '3' ||shipmentMode == '6' ||shipmentMode == '7')
	  {
		document.forms[0].terminalMode[1].checked = true;
		document.forms[0].terminalMode[1].disabled = true;
	  }

	  if(shipmentMode == '4' || shipmentMode == '5' ||shipmentMode == '6' ||shipmentMode == '7')
	  {
		document.forms[0].terminalMode[2].checked = true;
		document.forms[0].terminalMode[2].disabled = true;
	  }

	  //<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
		frequency	  = '<%=frequency%>';
		carrier	      = '<%=carrier%>';
		transittime	  = '<%=transittime%>';
		rateValidity	  = '<%=rateValidity%>';
		if(frequency == 'on')
	    {
		document.forms[0].frequency.checked = true;
		
	    }
		if(carrier == 'on')
	    {
		document.forms[0].carrier.checked = true;
		
	    }
		if(transittime == 'on')
	    {
		document.forms[0].transittime.checked = true;
		
	    }
		if(rateValidity == 'on')
	    {
		document.forms[0].rateValidity.checked = true;
		
	    }
		//<!-- Added By RajKumari on 11/28/2008 for 146448 ends -->
	}

function passLocationIds()
{
	len = document.forms[0].locationId.options.length;
	locationList = new Array();
	for(i=0;i<len;i++)
	{
		locationList[i] = document.forms[0].locationId.options[i].value;
	}
	document.forms[0].locationIdHide.value = locationList.toString();
}

function showCountryIds()
{
		var Url      = 'ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
		var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
		var Features =  Bars+''+Options;
		var Win      =  open(Url,'Doc',Features);
}
function showLocationIds()
{
	var len=document.forms[0].locationId.length;
	var id=document.forms[0].companyId.value;
	str = "";
	var fetchTerminalType = '';
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
	if(document.forms[0].terminalMode[0].checked  && document.forms[0].terminalMode[1].checked && document.forms[0].terminalMode[2].checked)
	{
	   shipmentMode = " WHERE SHIPMENTMODE=7";
	}
    if(document.forms[0].terminalMode[0].checked  && document.forms[0].terminalMode[1].checked && !document.forms[0].terminalMode[2].checked) 
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
	//Commented by JS for terminalRegistration Add/Modify var Url      = 'ETCTerminalRegistrationLocationIdsLOV.jsp?terminalId='+terminalId+'&idLike='+id+'&adminType='+terminalType+'&terminalType='+terminalType+'&flag=Modify&terminalid='+'<%=terminalId%>'+'&loc='+str+'&shipmentMode='+shipmentMode ;
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
	var Url      = 'ETCTerminalRegistrationLocationIdsLOV.jsp?terminalId='+terminalId+'&idLike=&adminType='+terminalType+'&terminalType='+terminalType+'&flag=Modify&terminalid='+'<%=terminalId%>'+'&loc='+str+'&shipmentMode=&fetchTerminalType='+fetchTerminalType+'&countryId='+document.forms[0].countryId.value+'&companyId='+document.forms[0].companyId.value;
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
function changeLocationName()
{
	if(document.forms[0].locationId.options.length > 0)
	{
		tempLocationId = document.forms[0].locationId.options[document.forms[0].locationId.selectedIndex].value
		strLocationId = tempLocationId.toString();
		locationId = strLocationId;
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

function selectTerminalType()
{
	var terminalType = '<%=terminalType%>';
	
	if(terminalType=='S')
	  document.forms[0].terminalType.options.selectedIndex=0;
	else
	  document.forms[0].terminalType.options.selectedIndex=1; 	

	var timeZone 		= '<%=timeZone%>';
	for(var i=0;i<document.forms[0].timeZone.length;i++)
	{
		if(	timeZone == document.forms[0].timeZone.options[i].value)
		{
			document.forms[0].timeZone.selectedIndex=i;
			break;
		}
	}
<%
	if(opTerminalType==null || opTerminalType.equals("O"))
	{
%>	  
		var emailStatus 	= '<%=emailStatus%>';
		for(var i=0;i<document.forms[0].emailStatus.length;i++)
		{
			if(	emailStatus == document.forms[0].emailStatus.options[i].value)
			{
				document.forms[0].emailStatus.selectedIndex=i;
				break;
			}
		}
		var collectShipment = '<%=collectShipment%>';
		for(var i=0;i<document.forms[0].collectShipment.length;i++)
		{
			if(	collectShipment == document.forms[0].collectShipment.options[i].value)
			{
				document.forms[0].collectShipment.selectedIndex=i;
				break;
			}
		}
<%
	}
%>	
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
//-->
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
	var locationids ='';
	len = document.forms[0].locationId.options.length;
	locationids = document.forms[0].locationId.options[0].value;
	for(i=1;i<len;i++)
	{
		locationids = locationids +','+document.forms[0].locationId.options[i].value;
	}
	
	 len= window.document.forms[0].cbtlocationId.options.length;
	if (window.document.forms[0].cbtlocationId.options[0].value != '(Select)')
	{
		 strCBTTerminals = window.document.forms[0].cbtlocationId.options[0].value;
			for(var i=1;i<len;i++)
			{
				strCBTTerminals = strCBTTerminals + ','+ window.document.forms[0].cbtlocationId.options[i].value;
			}
	}
    var Url      = 'ETCCBTTerminalRegistrationLocationIdsLOV.jsp?loc='+locationids+'&cbtloc='+strCBTTerminals;
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
	str =  cbthf;
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
//Added By I.V.Sekhar
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
//end sekhar

 <%}%>
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body  onLoad="loadValues();changeLocationName();selectTerminalType()"  >
<table width="800" border="0" cellspacing="0" cellpadding="0">
<tr><td bgcolor="ffffff">
<table width="800" border="0" cellspacing="1" cellpadding="4">
  <tr class='formlabel'>
    <td ><table width="790" border="0" ><tr class='formlabel'><td>&nbsp;Terminal - <%=operation%></td><td align=right><%=loginbean.generateUniqueId("ETCTerminalRegistrationView.jsp",operation)%></td></tr></table></td>
  </tr></table>
  <table width="800" border="0" cellspacing="1" cellpadding="4">
  <tr class='formdata'><td>&nbsp;</tr></table>
  
<%
  if(operation!=null && operation.equals("Modify") )
  {
%>
      <form method="post" action="<%= actionValue %>" name="terminalreg" onSubmit="return checkForNull()" >
<%
  }
  else if(operation!=null && (operation.equals("View")|| operation.equals("Delete") ))
  {
%>
      <form method="post" action="<%= actionValue %>" name="terminalreg"  >
<%
  }
%>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class='formdata'>
            <td colspan="2" width="20%">Terminal
              Id:<br>
              <input type='text' class='text' name="terminalId" value ="<%=terminalId%>" readonly size="16" maxlength="16">
              </td>
            <td width="15%">Abbr
              Name:<font color="#FF0000">*</font><br>
              
              <input type='text' class='text' name="companyId" value ="<%=abbrName%>" readonly size=6>
              </td>
            <td width="18%">Terminal
              Type:<br>
             <input type='text' class='text' name="agentInd" readOnly size="15">
              </td>
            <td colspan="2" width=20%"><%=label%>:<font color="#FF0000">*</font><br>
			<!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-6626 on 20050504.-->
              <select size=1 name="locationId"  onClick=changeLocationName() class='select' <%//=strDisable%>>
<%
       if(locationId != null)
       {
			for(int i=0;i<locationId.length;i++)
			{
%>
				<option value="<%=locationId[i]%>" > <%=locationId[i]%> </option>
<%
			}
       }
%>
			</select> <input type="button" class='input' value="..." name="B4" <%=strDisable%> onClick=showLocationIds() >
<%
	if(opTerminalType!=null && !opTerminalType.equals("O"))
	{//Added by I.V.Sekhar to restrict assigning either Adimins or OTs ,cont be both.
%>
				<br><input type='radio' name='adminRterminals' onClick='deselectValues()' <%=("A".equals(adminROTerminal)?"Checked":"")%>>Admin&nbsp;<input type='radio' name='adminRterminals' onClick='deselectValues()' <%=("O".equals(adminROTerminal)?"Checked":"")%>>Operation
				<input type='hidden' name="adminROTerminal" value='<%=adminROTerminal%>'>
<%
	}
%>              
<%
	if(opTerminalType==null || opTerminalType.equals("O"))
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
			  <td ><input type=checkbox name =cbt  value ='Y' onClick='changeToCBT()'  <%if (cbtLocationId.size()>0) 				  {%> checked <%}%> <%if(operation!=null && !operation.equals("Modify")){%>disabled<%}%> >CBT<br>Terminals:<font color="#FF0000">*</font>
				  <input type=hidden name =cbtflag  value = <%if (cbtLocationId.size()>0){%>"Y"<%}else{%> "N" <%}%>>
				  <!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-6626 on 20050504.-->
				  <select size="1" name="cbtlocationId" class='select' <%=strDisable%>>
               <%if (cbtLocationId.size()>0) 
				  {
				    for(int i=0;i<cbtLocationId.size();i++)
			            {
							str_cbtassignLocations=str_cbtassignLocations+","+(String)cbtLocationId.get(i);
                %>
				           <option value="<%=(String)cbtLocationId.get(i)%>" > <%=(String)cbtLocationId.get(i)%> </option>
               <%
			            }

                   }
				   else
					   {%>
                        <option value="(Select)" selected>(Select)</option>
                      <%}%>
              </select>&nbsp;<input type="button" class='input' value="..." name="CBTB4"  onClick=showCBTLocationIds() <%if (cbtLocationId.size()>0 && (operation!=null && operation.equals("Modify"))){%>  <%}else{%> disabled<%}%>></td>  
<%}%>
<%
	if(opTerminalType==null || opTerminalType.equals("O"))
	{
	}
	else
	{
%>
	    <input type="hidden" name="cbtLocationIdHide" value="<%=str_cbtassignLocations %>">
<%
	}
%>		

          </tr>
        </table>
      <table border=0 cellPadding=4 cellSpacing=1  width=800>
        <tr vAlign=top class='formdata'>
          <td width="175" >IATA Code:
            <br><input type='text' class='text' maxlength="16" size="22" name="iatacode" value="<%=iatacode%>"  onblur=changeToUpper(this) onkeypress="return checkSpecialKeyCode()" <%=strReadonly%>>
            </td>
			<td width="156" >Account Code:
            <br><input type='text' class='text' maxlength="16" size="20" name="accountcode" value="<%=accountcode%>" onblur=changeToUpper(this) onkeypress="return checkSpecialKeyCode()" <%=strReadonly%>>
			<td width="165" >Tax Reg No:
            <br><input type='text' class='text' maxlength="16" size="20" name="taxregno" value="<%=regno%>" onblur=changeToUpper(this) onkeypress="return checkSpecialKeyCode()" <%=strReadonly%>>
            </td>
			<td width="217">Shipment Mode:</font> <br>
                <input type="checkbox" name="terminalMode" value="1" onClick="setShipmentMode(this)" <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%>>  Air</font>
				  <input type="checkbox" name="terminalMode" value="2" onClick="setShipmentMode(this)" <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%>>  Sea
                <input type="checkbox" name="terminalMode" value="4" onClick="setShipmentMode(this)" <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%>> Truck</font>
   			</td>   
          </tr>
		</table>

		<!-- Added By RajKumari on 11/28/2008 for 146448 starts -->
		<%if(opTerminalType!=null && (opTerminalType.equals("A")||opTerminalType.equals("H"))) {%>
	   <table border=0 cellPadding=4 cellSpacing=1  width=800>
	     <tr vAlign=top class='formdata'>
			<td width="800">Default Quote Detail:</font> <br>
			    &nbsp;&nbsp;
                <input type="checkbox" name="frequency" onClick="setDefaultQuoteDetail(this)" <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%>>  Frequency
			    &nbsp;&nbsp;
				<input type="checkbox" name="carrier" onClick="setDefaultQuoteDetail(this)" <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%>>  Carrier
				&nbsp;&nbsp
                <input type="checkbox" name="transittime" onClick="setDefaultQuoteDetail(this)" <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%>>  Approximate Transit Time
				&nbsp;&nbsp;
				<input type="checkbox" name="rateValidity"  onClick="setDefaultQuoteDetail(this)" <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%>>  Freight Rate Validity
   			</td>    
       <!-- Added By phani sekhar for wpbn 170758 on 20090626 -->
			<td>Margin Type:<br>
			 <select class="select"  name="marginType" <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%>>
						  <option value="A" <%="A".equals(marginType)?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(marginType)?"selected":""%>>Percent</option>
			</select>
			</td><td>Discount Type:<br>
			 <select class="select"  name="discountType" <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%> >
						  <option value="A" <%="A".equals(discountType)?"selected":""%>>Absolute</option>
						  <option value="P" <%="P".equals(discountType)?"selected":""%>>Percent</option>
			</select>
			</td><!-- ends 170758-->
		 </tr>
	   </table>
	   <% } %>
<!-- Added By RajKumari on 11/28/2008 for 146448 ends-->
	
  		
  		<table border=0 cellPadding=4 cellSpacing=1 width=800>
         <tr vAlign=top class='formdata'>
          <td colSpan=2 width="365">System/Non-System:<font color=#ff0000>*</font><br>
            <select name='terminalType' class='select' <%=strDisable%>>
				<option value='S'>System</option>
			<% if (opTerminalType.equalsIgnoreCase("O")) { %>
				<option value='N'>Non-System</option>
			<%}%>
			</select>
          </td>
		  <%//@@ Avinash commented on 20050206 (MULTI-UOM)%>
<!-- 		<td colSpan=2  width="365">WeightScale
             <font color=#ff0000></font><br>
            	<select name='weightScale' class='select'>
				<%// if ((operation.equalsIgnoreCase("modify"))|| (operation.equalsIgnoreCase("delete"))) { %>
					<option value='6000' >6000</option>
					<option value='7000'>7000</option>
				<%//}%>
				<% //if (operation.equalsIgnoreCase("view")) { %>
					<option value='<%//= weightScale%>' readonly><%//= weightScale%></option>
				<%//}%>
            	</select>
           </td>	 -->			  
		<%//@@ 20050206 (MULTI-UOM)%>
       <td colspan="2">Time Zone:<br>
	   <!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-6626 on 20050504.-->
           <select size="1" name="timeZone" class='select' <%=strDisable%>>
		   <% if ((operation.equalsIgnoreCase("modify"))|| (operation.equalsIgnoreCase("delete"))) { %>
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
			<%}%>
			<% if (operation.equalsIgnoreCase("view")) { %>
					<option value='<%=timeZone%>' readonly><%=timeZone%></option>
				<%}%>
          </select>
         </td>
       </tr>
<%
	if(opTerminalType==null || opTerminalType.equals("O"))
	{
%>
          <tr valign="top" class='formdata'>
		  <!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5530 on 20050430.-->
            <td colspan="2" width="365">EMail Status:<br>
	           <select size="1" name="emailStatus" class='select' <%=strDisable%>>
                 <option value='Y'>Yes</option>
                 <option value='N'>No</option>
			   </select>
			</td>		  
            <td colspan="2" width="365">Collect Shipment :<br>
			<!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5530 on 20050430.-->
	           <select size="1" name="collectShipment" class='select' <%=strDisable%>>
                 <option value='CCA'>CC Allowed</option>
                 <option value='CCN'>CC Not Allowed</option>
			   </select>
			</td>		  
             <td colspan="2"></td>
		   </tr>
<%
	}
%>
	 <% //@@ Srivegi Added on 20050419 (Invoice-PR)
    %>
	 <tr valign="top" class='formdata'>
           <td>
			<input type="radio" name="stockedInvoiceIds" value="N" <%if(stockedInvoiceIdsCheck!=null && stockedInvoiceIdsCheck.equals("N")){%> checked<%}%> <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%>>System Invoice Ids
			<input type="radio" name="stockedInvoiceIds" value="Y" <%if(stockedInvoiceIdsCheck!=null && stockedInvoiceIdsCheck.equals("Y")){%> checked<%}%> <%if(operation!=null && !operation.equals("Modify")){%> disabled<%}%>>Invoices from Stock
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
              <input type='text' class='text' name="contactName" value="<%=contactName%>" size="50" maxlength="50"  <%=strReadonly%> onBlur=changeToUpper(contactName) onKeypress="return checkSpecialKeyCode()">
              </td>
             <td colSpan=2 width="365">Company
            Name:<font color=#ff0000>*</font><br><input type='text' class='text' maxlength="50" size="50" name="notes" value="<%=notes%>" <%=strReadonly%> onblur=changeToUpper(notes) onkeypress="return checkSpecialKeyCode()" >
            </td>
          </tr>
        </table>
     
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class='formdata'>
            
            <td colspan="2" rowspan="2">Address:<font color="#FF0000">*</font><br>
              <input type='text' class='text' name="addressLine1" maxlength=65 value="<%=addressLine1%>" size="50" <%=strReadonly%> onBlur=changeToUpper(addressLine1) onKeypress="return checkSpecialKeyCode()">
              <br>
              <input type='text' class='text' name="addressLine2" maxlength=65 value="<%=addressLine2%>" size="50" <%=strReadonly%> onBlur=changeToUpper(addressLine2) onKeypress="return checkSpecialKeyCode()">
            </td>
			<td colspan="2" width="365">Designation:<br>
              <input type='text' class='text' name="designation" value="<%=desg%>" size="50" maxlength="50" <%=strReadonly%> onBlur=changeToUpper(designation) onKeypress="return checkSpecialKeyCode()">
             </td> 
	           
          </tr>
          
          <tr class='formdata'>
		   <td colspan="2" width="357">Zip
              or Postal Code:<br>
              <input type='text' class='text' name="zipCode" value="<%=zipCode%>" size="16" maxlength="10" <%=strReadonly%> onBlur=changeToUpper(zipCode) onKeypress="return checkSpecialKeyCode()">
            </td>
          </tr>
          
          <tr valign="top" class='formdata'>
            <td colspan="2">City:<font color="#FF0000">*</font><br>
              <input type='text' class='text' name="city" value="<%=city%>" size="30" maxlength=30 <%=strReadonly%> onBlur=changeToUpper(city) onKeypress="return checkSpecialKeyCode()">
              </td>
            <td colspan="2"  width="357">Contact
              No:<br>
              <input type='text' class='text' name="phoneNo" value="<%=phoneNo%>" size="20" maxlength="15" <%=strReadonly%> onBlur=changeToUpper(phoneNo) onKeypress="return checkSpecialKeyCode()">
              </td>
            
          </tr>
          <tr valign="top" class='formdata'>
            <td colspan="2">State
              or Province:<br>
              <input type='text' class='text' name="state"  value="<%=state%>" size="30" maxlength=30  <%=strReadonly%> onBlur=changeToUpper(state) onKeypress="return checkSpecialKeyCode()">
              </td>
           <td colspan="2"  width="357">Fax
              No:<br>
              <input type='text' class='text' name="fax"  value="<%=fax%>" size="20" maxlength=15 <%=strReadonly%> onBlur=changeToUpper(fax) onKeypress="return checkSpecialKeyCode()">
              </td> 
            
          </tr>
          <tr valign="top" class='formdata'>
            <td colspan="2">Country:<font color="#FF0000">*</font><br>
              <input type='text' class='text' name="countryId" value="<%=countryId%>" maxlength=2 size="4" <%=strReadonly%> onBlur="changeToUpper(countryId)" onKeypress="return checkSpecialKeyCode()">
              <input type="button" class='input' value="..." name="B4" <%=strDisable%> onClick=showCountryIds() >
              </td>
            <td colspan="2" width="357">Email
              Id: <br>
              <input type='text' class='text' name="emailId" value="<%=emailId%>" size="50" maxlength=50 <%=strReadonly%>  onBlur="changeToUpper(emailId)" onKeypress="return checkSpecialKeyCode()">
              </td>
          </tr>
        </table>
        <table width="800" border="0" cellspacing="1" cellpadding="4">
          <tr class="formheader"> 
            <td colspan="2">Bank Details</font></td>
          </tr>
          <tr class='formdata'> 
            <td  width="50%">Bank 
              Account Number :<br>
              <input type='text' class='text' name="accountNumber" value='<%=accountNo%>' size="30" maxlength="24" onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()" <%=strReadonly%> >
            </td>
            <td width="50%">Name of the Bank 
              :<br>
              <input type='text' class='text' name="bankName" value='<%=bankName%>' size="50" maxlength="60" onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()" <%=strReadonly%> >
              </td>
          </tr>
          <tr class='formdata'> 
            <td  width="50%">Branch 
              Name :<br>
              <input type='text' class='text' name="branchName" value='<%=branchName%>' size="50" maxlength="60" onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()" <%=strReadonly%> >
             </td>
            <td  width="50%">Type Of Invoice :<br>
			<input type='text' class='text' name="invoiceCategory" size="20" maxlength="16" value='<%=invoiceCategory%>' onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()" <%=strReadonly%> ></font></td>
          </tr>
          <tr class='formdata'> 
            <td  width="50%">Address 
              of the Bank :<br>
              <input type='text' class='text' name="bankAddress" size="50" maxlength="60" value='<%=bankAddress%>' onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()" <%=strReadonly%> >
             </td>
            <td  width="50%">City 
              :<br>
              <input type='text' class='text' name="bankCity" size="50" maxlength="60" value='<%=bankCity%>'onBlur="changeToUpper(this)" onKeypress="return checkSpecialKeyCode()" <%=strReadonly%> >
              </font></td>
          </tr>
          <tr class='formdata'> 
            <td  width="50%">Days 
              Allowed for Discrepancy :<br>
              <input type='text' class='text' name="discrepancy" size="5" maxlength="2" value='<%=discrepancy%>' onKeypress="return getNumberCode()" <%=strReadonly%> >
              </td>
            <td  width="50%">Over 
              Due Interest in % per Month :<br>
              <input type='text' class='text' name="intrestRate" size="5" maxlength="3" value='<%=interestRate%>' onKeypress="return getDotNumberCode(this)" <%=strReadonly%> >
              </td>
          </tr>
        </table>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes
                Mandatory
            </td>
            <td valign="top" ></td>
            <td valign="top" ></td>
            <td valign="top"  align="right">
				 <input type="hidden" name="locationIdHide" >
<%
			if(operation!=null && operation.equals("View") )
			{
%>
                <input type="Submit" value="Continue" name="jbt_Test" class='input'>
<%
        	}
			else if(operation!=null && operation.equals("Delete") )
			{
%>
               <input type="Submit" value="Delete" name="jbt_Test" class='input'>
<%
			}
			else if(operation!=null && operation.equals("Modify") )
			{
%><!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-6649 on 200500504.-->
                <input type="Submit" class='input' value="Submit" name="jbt_Test" onClick="loadValues();passLocationIds()"  >
				<%//Modified by Sreelakshmi KVA - 20050411 SPETI-5532 //%>
				<input type="Reset" class="input" value="Reset" name = "Reset" >
<%
			}
%>
				 <input type="hidden" name="shipmentMode" value="0">
				 <input type="hidden" name="opTerminalType" value='<%=opTerminalType%>'>
	 			 <input type="hidden" name= "addressId"value=<%=addressid%> >
				 <input type="hidden" name= "Operation" value=<%=operation%> >
				 <input type="hidden" name= "terminalId" value=<%=terminalId1!=null?terminalId1:""%> >
                     
                  </td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
      </table>
      </body>
</html>
	  <%
	}
	else
	{
			 String errorMessage = "Record does not exist with TerminalId : "+terminalId1;
			 session.setAttribute("ErrorCode","RNF");
			 session.setAttribute("Operation",operation);
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCTerminalRegistrationEnterId.jsp");
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
	}
	catch(Exception nme )
	{
			 //Logger.error(FILE_NAME,"Exception in ETTerminalRegistrationView.jsp :", nme.toString());
       logger.error(FILE_NAME+"Exception in ETTerminalRegistrationView.jsp :"+ nme.toString());
			 String errorMessage = "Error while retriving data";
			 session.setAttribute("ErrorCode","ERR");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCTerminalRegistrationEnterId.jsp");
%>
			<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
%>

