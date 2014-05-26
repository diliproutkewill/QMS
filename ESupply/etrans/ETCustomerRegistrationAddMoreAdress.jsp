
	<%--
				Program Name		: ETCustomerRegistrationAdd.jsp
				Module name		: HO Setup
				Task		        : Adding Customer
				Sub task			: Add ,Modify,Delete processes
				Author Name		: A.Hemanth Kumar
				Date Started		: September 08, 2001
				Date completed	: September 11, 2001
				Date Modified	: December 10,2001 by Rizwan.
				Corrected the problem which was not showing CorporateCustomerId when modified.
				Description      :
				This file is used to add a new Customer. This jsp takes all the data entered in the fields and passes to
				CustomerRegistrationProcess.jsp
				This file interacts with CustomerRegSession Bean and then calls the method 'getCountryIds()' which inturn
				retrieves all the ContryIds corresponding to the Country Names.

	--%>
	<%@		page import = "javax.ejb.CreateException,
			javax.naming.Context,
			javax.naming.InitialContext,
			java.util.ArrayList,
			com.foursoft.etrans.common.util.java.OperationsImpl,
			com.qms.setup.ejb.sls.SetUpSession,
            com.qms.setup.ejb.sls.SetUpSessionHome,
			com.foursoft.etrans.setup.customer.java.CustomerModel,
			com.foursoft.etrans.common.bean.Address,
			com.foursoft.esupply.common.java.ErrorMessage,
			com.foursoft.esupply.common.java.KeyValue,
			com.foursoft.esupply.common.util.ESupplyDateUtility,
			java.sql.Timestamp" %>

			<%	// @@ 20050308 for Bulk Invoicing	%>
			

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

	<%
	    //@@ Srivegi added on 20050224 (AES-SED)
        String contactPersonLastName			=	"";
		String knownShipper1					=	"";
		String einssn					                =	"";
		//@@ 20050224
	    
		// @@ Suneetha added on 20050305 for Bulk Invoicing
		Timestamp  invoiceFrequencyValidDateTemp = null;
		String  invoiceFrequencyValidDate = "";
		String  invoiceFrequencyFlag	  = "";	
		String  invoiceInfo				  = "";
		String  bulkInvoiceRequired		= "";	

		ESupplyDateUtility	eSupplyDateUtility	=	new ESupplyDateUtility();
		String		dateFormat		=	loginbean.getUserPreferences().getDateFormat();	

		// @@ 20050308 for Bulk Invoicing
		String Customer				=	"";   	// String to store Customer
		String customerType			=	"";    	// String to store customerType
		String companyName			=	"";    	// String to store companyName
		String contactName			=	"";    	// String to store contactName
		String designation			=	"";    	// String to store designation
		String registered			=	"";	   	// String to store Registered		
		String notes				=	"";    	// String to store notes
		String abbrName				=	"";    	// String to store abbreviatedName
		String countryIds[]			=	null;  	//String array to store countryIds
		String currency1[]			=	null;  	// String array to store currencies
		String scode				=	"";   	// String to store Salesman code
		int    creditdays			=	0;   	 // integer to store credit days
		double creditlimit			=	0.0;  	// a double  store credit limit
		String currency				=	"";  	// String to store currency
		String currency3			=	"";     // String to store currency Id
		String customerId			=	"";     // String to store customer Id
		String  addressLine1		=	"";     // String to store addressLine1
		String  addressLine2		=	"";     // Strng to store addressLine2
		String  city				=	"";     // String to store city
		String	corpCustomerId		=	"";
		String	opEmailId			=	"";
		String	custType			=	"";

		String  state				=	"";    // String to store state
		String  zipCode				=	"";    // String to store zipCode
		String  countryId			=	"";    // String to store country Id
		String  phoneNo				=	"";    // String to store phone No
		String  emailId				=	"";    // String to store email Id
		String  fax					=	"";    // String to store fax No

		String 	operation			=	"";    // String to store type of operation
		String 	value				=	"";    // String to store value of navigation button
		String 	readOnly			=	"";    // String to store readonly String
		String  disabled			=	"";    // String to store disabled String
		String 	actionValue			=	"";    // String to store action Value
		String 	terminalId			=	"";    // String to store termianl Id
		String 	customerIdReadOnly	=	"";    // String to store customerId read only value
		String 	terminalIdReadOnly	=	"";    // String to store terminalId read only value
		String 	abbrNameReadOnly	=	"";    // String to store abbreviated name read only value
		String 	registrationLevel	=	"";    // String to store customer Registration level i.e., terminal or customer

		String 	creditFlag			=	"";
		String 	bbFlag				=	"";
		String 	masterCloseFlag		=	"";
		String	label				=	"";
		String	submitValue			=	"";
        String serviceLevelId       =   null;  // It Contains Customer Prefered ServiceLevel By K.N.V.Prasada Reddy

		ArrayList currencyid			= null;    // a Vector to store currency Ids
		OperationsImpl operationsImpl	= new OperationsImpl();
		ArrayList listOfValues			= null;   	
		ErrorMessage errorMessageObject = null;
		ArrayList	 keyValueList	    = null;
		ArrayList	 custAddList		= new ArrayList();
		ArrayList	 custMoreAddList	= new ArrayList();

		ArrayList		allDetailsList		= null;   
		CustomerModel	customerModelDetails= null;


		String		readOnlyView		= ""; //This is used for if operation is view than i have seting to fieds as readonly
		boolean		statusView			= true;
		try{		

				operation            = request.getParameter("Operation");
				registrationLevel    = request.getParameter("registrationLevel");
				if(registrationLevel.equalsIgnoreCase("C")){
					label="Corporate ";
				}else{
					label="Terminal ";
				}
				// checking for the type of operation
				Customer = request.getParameter("Customer");

				if(operation.equalsIgnoreCase("Add")){
					customerType = "R" ;	
				}			
				if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("Delete") || operation.equalsIgnoreCase("Upgrade") )
						customerId   = request.getParameter("corpCustomerId");

						terminalId   =	loginbean.getTerminalId();
						actionValue  = 	"ETCustomerRegistrationProcess.jsp?Operation="+operation;		
				if(!operation.equalsIgnoreCase("Add")){
					// checking for the type of customer
					if(Customer.equalsIgnoreCase("NCCS")){
						if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") ){
							customerType = request.getParameter("customerType");
							if(customerType.equalsIgnoreCase("Registered"))
								customerType = "R" ;
							if(customerType.equalsIgnoreCase("UnRegistered") )
									customerType = "U";
					}
					if(operation.equalsIgnoreCase("Upgrade") )
						customerType = "U";

				}else if(Customer.equalsIgnoreCase("CCS")){
					if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") ){
							customerType = "R";
					}
				}
			}
		if(operation.equalsIgnoreCase("Add")){
				customerIdReadOnly	="";
				terminalIdReadOnly	="readOnly";
				customerIdReadOnly  ="";
				abbrNameReadOnly	="";
		}else if(operation.equalsIgnoreCase("Modify")&&registrationLevel.equalsIgnoreCase("T")){
				customerIdReadOnly  ="";
				abbrNameReadOnly	="readOnly";
		}else{
				customerIdReadOnly	="readOnly";
				terminalIdReadOnly	="readOnly";
				customerIdReadOnly  ="readOnly";
				abbrNameReadOnly	="readOnly";
		}
		
		Context 							initialContext 	= null;   // variable to store initial context
		SetUpSessionHome 	customerHome 	= null;   // variable to store Home object
		SetUpSession		customerRemote 	= null;    // variable to store remote reference
		CustomerModel						customerModel	= null;    // instance of CustomerDetail class assigned null value
		Address								addressModel	= null;	
		SetUpSessionHome 			utilHome 		= null;
		SetUpSession 				utilRemote 		= null;

		
		initialContext 	=	new InitialContext();
		customerHome 	=	(SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
		customerRemote 	=	(SetUpSession)customerHome.create();
		utilHome		=	(SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
		utilRemote		=	(SetUpSession)utilHome.create();

		allDetailsList=customerRemote.getAllCustomerDetails(loginbean.getTerminalId());
	

		// checking for the type of operation
		if(operation.equalsIgnoreCase("Add") || operation.equalsIgnoreCase("Modify")){
			countryIds 		= operationsImpl.getCountryIds();
			session.setAttribute("CountryIds",countryIds);
			currencyid	= utilRemote.getCurrencyIds("");
			currency	= loginbean.getCurrencyId();//"USD";
			//currency	="";

		}
		if(operation.equalsIgnoreCase("View") ){
				readOnly		= "readonly";
				actionValue		= "ETCustomerRegistrationEnterId.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
				submitValue		= "Continue";
				disabled 		= "disabled";
		}else if(operation.equalsIgnoreCase("Upgrade") ){
				readOnly	    = "readonly";
				actionValue	    = "ETCustomerRegistrationProcess.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
				submitValue		= "Upgrade";
				disabled 	    = "disabled";

		}
		else if(operation.equalsIgnoreCase("Delete") )
		{
				readOnly		= "readonly";
				disabled 		= "disabled";
				actionValue		= "ETCustomerRegistrationProcess.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
				submitValue		= "Delete";
		}
		else
		{
				readOnly		= "";
				disabled		="";
				actionValue			= "ETCustomerRegistrationProcess.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
				submitValue				= "Submit";
		}
		
	
	if(operation.equalsIgnoreCase("Delete")){
		listOfValues = customerRemote.getCustomerDetail(customerId,loginbean,"R",registrationLevel,operation);
	}else if(operation.equalsIgnoreCase("Upgrade")){
		listOfValues = customerRemote.getCustomerDetail(customerId,loginbean,"U",registrationLevel,operation);
	}else if(operation.equalsIgnoreCase("View")){ 
		listOfValues = customerRemote.getCustomerDetail(customerId,loginbean,customerType,registrationLevel,operation);
	}else if(operation.equalsIgnoreCase("Modify") ){
			listOfValues = customerRemote.getCustomerDetail(customerId,loginbean,customerType,registrationLevel,operation);
	} 

	if(!operation.equalsIgnoreCase("Add")){       // verifying whether CustomerDetail object is null or not
		if(listOfValues!=null && !listOfValues.isEmpty()){ 	
			customerModel =(CustomerModel)listOfValues.get(0);
			addressModel  =(Address)listOfValues.get(1);
			custAddList			=	(ArrayList)listOfValues.get(2);
			custMoreAddList		=	(ArrayList)listOfValues.get(3);
			if(customerModel != null){

			// storing the values in to the variables

				customerId 	  = customerModel.getCustomerId();	if(customerId==null) customerId=" ";
				terminalId	  = customerModel.getTerminalId();
				companyName   = customerModel.getCompanyName();
				contactName   = customerModel.getContactName();
				designation   = customerModel.getDesignation();
				registered	  = customerModel.getRegistered();	
				notes	      = customerModel.getNotes();
				abbrName  	  = customerModel.getAbbrName();
				scode         = customerModel.getSCode();
				creditdays    = customerModel.getCreditDays();
				creditlimit   = customerModel.getCreditLimit();
				currency      = customerModel.getCurrency();
				custType	  = customerModel.getTypeOfCustomer(); 
                //@@ Srivegi added on 20050224 (AES-SED)
                contactPersonLastName			=	customerModel.getContactLastName()!=null?customerModel.getContactLastName():"";
				knownShipper1					=	customerModel.getcustType()!=null?customerModel.getcustType():"";
				einssn					        =	customerModel.getEINSSNNo()!=null?customerModel.getEINSSNNo():"";
                //@@ 20050224

				// @@ Suneetha added on 20050305 for Bulk Invoicing
				invoiceFrequencyValidDateTemp = customerModel.getInvoiceFrequencyValidDate();
				invoiceFrequencyFlag	  = customerModel.getInvoiceFrequencyFlag()!=null?customerModel.getInvoiceFrequencyFlag():"";
				invoiceInfo				  = customerModel.getInvoiceInfo()!=null?customerModel.getInvoiceInfo():"";
				bulkInvoiceRequired		  = customerModel.getBulkInvoiceRequired()!=null?customerModel.getBulkInvoiceRequired():"";	
				eSupplyDateUtility.setPattern(dateFormat);
				invoiceFrequencyValidDate=eSupplyDateUtility.getDisplayString(invoiceFrequencyValidDateTemp);
				// @@ 20050308 for Bulk Invoicing

				if(custType==null)
					custType="Customer";

				corpCustomerId= customerModel.getCorpCustomerId();
				opEmailId	  = customerModel.getOpEmailId();

                serviceLevelId =  customerModel.getServiceLevelId();// By Prasada Reddy
        
				creditFlag	  =	customerModel.getCreditFlag();

				bbFlag  	  =	customerModel.getBBFlag();

				masterCloseFlag=customerModel.getMasterCloseFlag();

				addressLine1  		= addressModel.getAddressLine1();
				addressLine2  		= addressModel.getAddressLine2();
				city  				= addressModel.getCity();
				state  				= addressModel.getState();
				zipCode  			= addressModel.getZipCode();
				countryId  			= addressModel.getCountryId();
				phoneNo  			= addressModel.getPhoneNo();
				emailId  			= addressModel.getEmailId();
				fax  				= addressModel.getFax();
			}
	}else{

				errorMessageObject = new ErrorMessage("Record not found for "+label+" CustomerId : "+ customerId,"ETCustomerRegistrationEnterId.jsp?Customer="+Customer+"&registrationLevel="+registrationLevel); 

				keyValueList = new ArrayList();

				keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
				keyValueList.add(new KeyValue("Operation",operation)); 	
				errorMessageObject.setKeyValueList(keyValueList);
				request.setAttribute("ErrorMessage",errorMessageObject);


%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%		}
	}
%>



<html>
<head>
<title>Customer Registraion </title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>

<script language="javascript" src="html/eSupplyDateValidation.js"></script>
<script language="javascript" src="html/eSupply.js"></script>
<script language="javascript">
<!-- This code is Used for old script 	-->
var Operation	=	'<%=operation%>';
var customerType	=	'<%=customerType%>';
var registrationLevel='<%=registrationLevel%>';
arrayCountryIds      = new Array();
var currency_arr     = new Array();


var abbrName1		=	new Array();
var companyName1	=	new Array();
var addressLine11	=	new Array();
var addressLine22	=	new Array();
var city11			=	new Array();
var state11			=	new Array();
var zipCode11		=	new Array();
var opEmailId11		=	new Array();
//@@ Srivegi added on 20050224 (AES-SED)
var lastName	=	new Array();
var knownCust			=	new Array();
var einssn			=	new Array();
//@@ 20050224


<%
		if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Add") ){
%>
function loadValues(){
<%
	if( countryIds!=null ){
		int len2 = countryIds.length;
		for( int i=0; i<len2; i++ ){
			int idx = countryIds[i].indexOf('(');     //braces change from angular to square
			int idy	= countryIds[i].indexOf(')');
			String cId = countryIds[i].substring(idx+2,idy-1);
%>
			arrayCountryIds[<%=i%>] = '<%=cId%>';
<%
		}
	}

%>
	}

<%
if(currency1 != null){
	for(int i =0; i < currency1.length;i++){
%>
		currency_arr[<%=i%>] = '<%=currency1[i]%>'
<%
		}
	}
}
%>



<!-- This function filters the single quotes. -->
function stringFilter(input){
	s = input.value;
	input.value = s.toUpperCase();
}

function getValidFaxCode() //new
{
	if(event.keyCode!=13)
	{
		if(event.keyCode == 43 || event.keyCode == 45)
		{
			return true;
		}
		
		else if((event.keyCode != 46 && event.keyCode <48) || event.keyCode >57 )
		{
			alert("Fax Number should contain only Numbers, + , - ");
			return false;
		}
		else
		{	
			return true;
		}

	}
	
}


function upper(e){
	e.value	= e.value.toUpperCase();	
}

<!--	This function calls 'ETransLOVCountryIds.jsp' to get the CountryIds. -->


<!--	This function calls 'ETransLOVCountryIds.jsp' to get the CountryIds. -->
function getXMLUsed()
{
	return "Y";
}
function showCountryIds1(row){
	var URL 	= 'ETCLOVCountryIds.jsp?searchString='+document.getElementById("countryIdd"+row).value.toUpperCase()+'&custAddMor=CustAddMore&row='+row+'&whereClause=custMultAdd';
	var Bars 	= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options = 'scrollbars = yes,width = 360,height = 360,resizable = yes';
	var Features = Bars +' '+ Options;
	var Win 	 = open(URL,'Doc',Features);
}

function showCountryIds(){


		var URL 	= 'ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
		var Bars 	= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
		var Options = 'scrollbars = yes,width = 360,height = 360,resizable = yes';
		var Features = Bars +' '+ Options;
		var Win 	 = open(URL,'Doc',Features);
}
function showCurrencyLOV(){
		var Url      = 'ETCLOVCurrencyIds.jsp?fromWhat=two&searchString='+document.forms[0].currencyId.value.toUpperCase()+'&Query=WithOut'
		var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
		var Features =  Bars+''+Options;
		var Win      =  open(Url,'Doc',Features);
}
function showServiceLevelLOV()
	{   
					
		if(document.forms[0].serviceLevelId.value=="")
			searchString='';
		else
		  searchString=document.forms[0].serviceLevelId.value.toUpperCase();	
	//	input=" WHERE SERVICELEVELID LIKE '"+searchString+"'";
		var Url		=	"ETCLOVServiceLevelIds.jsp?searchString="+searchString+"&shipmentMode=All";	
		
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=360,height=360,resizable=no';
		var Features=Bars+' '+Options;
    	var Win=open(Url,'Doc',Features);
	}
function placeFocus(){
		<% 
			if(operation.equalsIgnoreCase("Add")){	
		%>	document.forms[0].abbrName.focus();
		<% 
			}
			if(operation.equalsIgnoreCase("Modify")){
		%>
				document.forms[0].companyName.focus();
		<%	
			}
			if(operation.equals("View") || operation.equals("Delete") ){
		%>
				document.forms[0].submit.focus();
		<%
			}
			if(operation.equalsIgnoreCase("Upgrade"))	{	
		%>
			document.forms[0].opEmailId.readOnly=false;
			document.forms[0].opEmailId.focus();
		<%
			}

			if( (customerType.equals("R") && !operation.equals("Add")) || (customerType.equals("U") && operation.equals("Upgrade"))){

			if(creditFlag!=null)
			if(creditFlag.equals("Y")){
		%>
			document.forms[0].creditFlag.checked=true;
			document.forms[0].creditFlag.value="Y";
		<%	
			}
			if(bbFlag!=null)
				if(bbFlag.equals("Y")){
		%>			
			document.forms[0].bbFlag.checked=true;
			document.forms[0].bbFlag.value="Y";
		<%		}
			if(masterCloseFlag!=null)
			if(masterCloseFlag.equals("Y")){
		%>			
					document.forms[0].masterCloseFlag.checked=true;
					document.forms[0].masterCloseFlag.value="Y";
		<%		
				}
			}
		%>
	}
function decCheck(input)
{
	  if(input.value.length==0)
		 input.value=0.0
		var32 = input.value;
		if(var32.length>0)
		{
			if( var32 >= 0)
			{
			if(isNaN(var32))
				var32=0;
				var value1= var32;
				result =Math.floor(value1)+".";
				var cents=100*(value1-Math.floor(value1))+0.5;
				result += Math.floor(cents/10);
				result += Math.floor(cents%10);
				var32=result
			}
			}
				input.value = var32;
}

function getDotNumberCode(input)    // Numbers + Dot
{
		if(event.keyCode!=13){
			
			if(event.keyCode == 46 ){
				if(input.value.indexOf(".") == -1)
					return true;
				else
					return false;
			}
		if((event.keyCode < 46 || event.keyCode==47 || event.keyCode > 57) )
				return false;	
		else{
				var index = input.value.indexOf(".");
				if( index != -1 ){
					if(input.value.length == index+3)
						return false;
				}
			}
		}
		return true;	
}
function checkEmail()
{
	var str = document.forms[0].opEmailId.value;
	var i=0,j=-1;
	var str1;
	var flag=false;
	while(1)
	{
		if(document.forms[0].opEmailId.value=="")
		{
			alert(" opEmailId can't be empty");	
			document.forms[0].opEmailId.focus();
			return false;
		}
		else
		{
			j=str.indexOf(";",j);
			if(j==-1)
			{
				break;
			}
			str1=str.substring(i,j);
			//alert(str1);
			if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(str1)))
			{
				alert(str1 + " is an Invalid E-mail Address!");	
				document.forms[0].opEmailId.focus();
				flag=true;
				break;
			}
			
			i=j+1;
			j=j+1;
			continue;
		}
	}
	str1=str.substring(i);
	//alert("last "+str1);
	if(str1!=''&& !flag)
	if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(str1)))
	{
		alert(str1 + " is an Invalid E-mail Address!");
		document.forms[0].opEmailId.focus();
		return false;
	}
	
return true;
		
}
function checkCurrencyId(field){
		var flag 	= false;
		currencyId 	= new Array();
<%
		if(currencyid != null){
			for(int i = 0; i < currencyid.size();i++){
%>
				currencyId[<%=i%>] = '<%=currencyid.get(i).toString()%>'
<%
			}
		}
%>
		for(i = 0; i < currencyId.length;i++){
			currencyId[i]     = currencyId[i].substring(currencyId[i].indexOf('[')+1,currencyId[i].indexOf(']'))
		}
		field.value  = field.value.toUpperCase();
		var currency = field.value;
		if(currency.length > 0){
			for(j=0;j<currencyId.length;j++){
				if(currencyId[j]==currency){
					flag	=	true;
					break;
				}
			}//end of for loop

		}//end of currency length  > 0

		return flag;
}

// @@ Suneetha Added on 20050305 for Bulk Invoicing
function setCustomerInvoiceInfo() 
{
	if(document.forms[0].bulkInvoiceRequired.checked == true){
		if(document.forms[0].invoiceFrequencyValidDate.value==""){
			alert("Select Invoice Frequncy Valid From Date by Clicking 'Select Invoice Frequency' Button");
			document.forms[0].custInvoiceInfoButton.focus();
			return false;
		}
	}
	else{
		document.forms[0].invoiceFrequencyValidDate.value="";
		document.forms[0].invoiceFrequencyFlag.value="";
		document.forms[0].invoiceInfo.value="";
	}
	return true;
}
// @@ 20050305 for Bulk Invoicing

function Mandatory(){
			<%
					if(operation.equals("Add") || operation.equals("Modify")){
			%>	
					// @@ Suneetha Added on 20050305 for Bulk Invoicing
					var result = setCustomerInvoiceInfo();
					if(result==false)
						return false;
					// @@ 20050305 for Bulk Invoicing
						if(document.forms.length > 0){
							for( i=0;i<document.forms[0].elements.length;i++){
								if((document.forms[0].elements[i].type=="text") || (document.forms[0].elements[i].type=="textarea")){
									if(	document.forms[0].elements[i].name != "emailId" && document.forms[0].elements[i].name!="typeOfCustomer" && 
											document.forms[0].elements[i].name != "opEmailId" )
											document.forms[0].elements[i].value=document.forms[0].elements[i].value.toUpperCase();
									 }
									if(document.forms[0].elements[i].type=="checkbox"){
										if(document.forms[0].elements[i].name!="creditFlag"){
											if(document.forms[0].elements[i].checked==true){
												if(document.forms[0].opEmailId.value=="" || document.forms[0].opEmailId.value==" "){
																											alert("Please Enter Operations EmailId");
														document.forms[0].opEmailId.focus();
														return false;
													}
												}
											}
										}
							}
						}//end of form length  > 0 if condituion
		


						terminalId  = document.forms[0].terminalId.value;
						customerId    = document.forms[0].customerId.value;
						abbrName    = document.forms[0].abbrName.value;
						companyName = document.forms[0].companyName.value;
						addressLine1= document.forms[0].addressLine1.value;
						city        = document.forms[0].city.value;
						countryId   = document.forms[0].countryId.value;
						currency    = document.forms[0].currencyId.value;
                        

						if(terminalId.length==0){
							alert("Please enter TerminalId");
							document.forms[0].terminalId.focus();
							return false;
						}

						if(customerId.length == 0 && registrationLevel=='C')
						{
							alert("Please enter <%=label%> Customer Id .");
							document.forms[0].customerId.focus();
							return false;
						}

						if(abbrName.length < 4 && registrationLevel=='T')
						{
							alert("Abbreviated Name should be of 4 characters.");
							document.forms[0].abbrName.focus();
							return false;
						}

						if(companyName.length==0)
						{
							alert("Please enter Company Name");
							document.forms[0].companyName.focus();
							return false;
						}
						if(addressLine1.length==0)
						{
							alert("Please enter Address");
							document.forms[0].addressLine1.focus();
							return false;
						}

						if(parseFloat(document.forms[0].creditlimit.value) > 0 && currency.length==0)
						{
							alert("Please enter Currency");
							document.forms[0].currencyId.focus();
							return false;
						}
						if(document.forms[0].currencyId.value.length > 0  && !checkCurrencyId(document.forms[0].currencyId))
						{

							alert("Please enter correct Currency.");
							document.forms[0].currencyId.value='';
							document.forms[0].currencyId.focus();
							return false;

						}
						if(city.length==0)
						{
							alert("Please enter City");
							document.forms[0].city.focus();
							return false;
						}
						if(countryId.length==0)
						{
							alert("Please enter CountryId");
							document.forms[0].countryId.focus();
							return false;
						}
						else
						{

								loadValues();
								var flag=0;
								document.forms[0].countryId.value=countryId.toUpperCase();
							if(countryId.length > 0 )
							{
								for( i=0; i<arrayCountryIds.length; i++ )
								{

									if( countryId.toUpperCase()==arrayCountryIds[i])
									{
										flag = 1;
										break;
									}
								}
								if(flag==0)
								{
									alert("Please enter correct Country.");
									countryId = "";
									document.forms[0].countryId.focus();
								}
							}

							if(flag==0)
								return false;
						}//nd of else condition

						if(document.forms[0].opEmailId.value.length > 200 )
						{
							alert("Operation EmailId should be less than 200 characters.");
							document.forms[0].opEmailId.focus();
							return false;
						}
						if(document.forms[0].emailId.value.length > 200 )
						{
							alert("EmailId should be less than 200 characters.");
							document.forms[0].emailId.focus();
							return false;
						}
						return checkEmail(); 
<%
			}
			else
			{	
%>
						document.forms[0].submit.disabled='true';
						return true;
<%			}
%>
	}






<%
//code added
	allDetailsList.trimToSize();	
	int listSize=allDetailsList.size();
    if(listSize != 0)
	{
      for( int i=0;i<listSize;i++ )
	  {
		customerModelDetails=(CustomerModel)allDetailsList.get(i);
%>
		abbrName1[<%=i%>]		=	'<%=customerModelDetails.getAbbrName()%>';   
		companyName1[<%=i%>]	=	'<%=customerModelDetails.getCompanyName()%>';
		addressLine11[<%=i%>]	=	'<%=customerModelDetails.getNotes()%>';
		addressLine22[<%=i%>]	=	'<%=customerModelDetails.getRegistered()%>';
		city11[<%=i%>]			=	'<%=customerModelDetails.getCorpCustomerId()%>';
		state11[<%=i%>]			=	'<%=customerModelDetails.getSCode()%>';
		zipCode11[<%=i%>]		=	'<%=customerModelDetails.getContactName()%>';
		opEmailId11[<%=i%>]		=	'<%=customerModelDetails.getOpEmailId()%>';
        //@@ Srivegi added on 20050224 (AES-SED)
        lastName[<%=i%>]	=	'<%=customerModelDetails.getContactLastName()%>';
		einssn[<%=i%>]			=	'<%=customerModelDetails.getEINSSNNo()%>';
		knownCust[<%=i%>]			=	'<%=customerModelDetails.getcustType()%>';
		//@@ 20050224
<%

	  }
	}
	
%>

//to validate abbr name
function verifyAbbrName(input)
{
	flag1=false;
	s = input.value;

	if(s.length!=0)
	{
		for(var j=0;j<abbrName1.length;j++)
		{
			if(s==abbrName1[j])
			{
				flag1=true;
			   //break;
			}
			else
			{
				flag1=false;
			}
			if(flag1==true)
			{
				var j=confirm("Do you want to create with same abbr name  "+s);
				if(j==true)
				return true;
			else
				return false;
			}
		}
	}
}



//to validate company name

function verifyCompanyName(input)
{
	flag1=false;
	s = input.value;

	if(s.length!=0)
	{
		for(var j=0;j<companyName1.length;j++)
		{
			if(s==companyName1[j])
			{
				flag1=true;
			   //break;
			}
			else
			{
				flag1=false;
			}
			if(flag1==true)
			{
				var j=confirm("Do you want to create with same company name  "+s);
				if(j==true)
				return true;
			else
				return false;
			}
		}
	}
}

//for validating addressLine1
function verifyAddressLine11(input)
{
	flag1=false;
	s = input.value;

	if(s.length!=0)
	{
		for(var j=0;j<addressLine11.length;j++)
		{
			if(s==addressLine11[j])
			{
				flag1=true;
			   //break;
			}
			else
			{
				flag1=false;
			}
			if(flag1==true)
			{
				var j=confirm("Do you want to create with same address  "+s);
				if(j==true)
				return true;
			else
				return false;
			}
		}
	}
}


//for validating address line2 
function verifyAddressLine22(input)
{
	flag1=false;
	s = input.value;

	if(s.length!=0)
	{
		for(var j=0;j<addressLine22.length;j++)
		{
			if(s==addressLine22[j])
			{
				flag1=true;
			   //break;
			}
			else
			{
				flag1=false;
			}
			if(flag1==true)
			{
				var j=confirm("Do you want to create with same address  "+s);
				if(j==true)
				return true;
			else
				return false;
			}
		}
	}
}


//for validating city 
function verifyCity22(input)
{
	flag1=false;
	s = input.value;

	if(s.length!=0)
	{
		for(var j=0;j<city11.length;j++)
		{
			
			if(s==city11[j])
			{
				flag1=true;
			   //break;
			}
			else
			{
				flag1=false;
			}
			if(flag1==true)
			{
				var j=confirm("Do you want to create with same city  "+s);
				if(j==true)
				return true;
			else
				return false;
			}
		}
	}
}

//for validating state 
function verifyState22(input)
{
	flag1=false;
	s = input.value;

	if(s.length!=0)
	{
		for(var j=0;j<state11.length;j++)
		{
			if(s==state11[j])
			{
				flag1=true;
			   //break;
			}
			else
			{
				flag1=false;
			}
			if(flag1==true)
			{
				var j=confirm("Do you want to create with same state or province  "+s);
				if(j==true)
				return true;
			else
				return false;
			}
		}
	}
}


//for validating zipCode
function verifyZipCode22(input)
{
	flag1=false;
	if(s!='')
	s = input.value;

	if(s.length!=0)
	{
		for(var j=0;j<zipCode11.length;j++)
		{
			if(s==zipCode11[j])
			{
				flag1=true;
			   //break;
			}
			else
			{
				flag1=false;
			}
			if(flag1==true)
			{
				var j=confirm("Do you want to create with same zipcode  "+s);
				if(j==true)
				return true;
			else
				return false;
			}
		}
	}
}



//for validating email Id
function verifyOperationsEmail11(input)
{
	flag1=false;
	s = input.value;

	if(s.length!=0)
	{
		for(var j=0;j<opEmailId11.length;j++)
		{
			if(s==opEmailId11[j])
			{
				flag1=true;
			   //break;
			}
			else
			{
				flag1=false;
			}
			if(flag1==true)
			{
				var j=confirm("Do you want to create with same emailId  "+s);
				if(j==true)
				return true;
			else
				return false;
			}
		}
	}
}








	function checkSpecialKeyCode()
	{

		if(event.keyCode == 96 || event.keyCode == 59 || event.keyCode == 39 || event.keyCode == 34)
		{
		return false;
	}

	return true;
}

function checkEmailSpecialKeyCode(){	

	if(event.keyCode == 96 ||  event.keyCode == 39 || event.keyCode == 34)
	{
		return false;
	}

	return true;
}



function getKeyCode()
{
		if(event.keyCode!==13){
			if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||(event.keyCode > 122 && event.keyCode <127) && event.keyCode==38)
				event.returnValue =false;
		}
		return true;
}
function setOnLoadCustomerType()
{
<% 
		if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Add") ){
%>
			var custType = '<%=custType%>';
			document.forms[0].typeOfCustomer.value=custType;

<%
			}
%>
}

function getCustomerIds(){

		var whereClause	=	"";
		var custType	=	'Corporate';
		//	whereClause		=	" CUSTOMERTYPE='"+custType+"'";
		var customerType=	"R";
		//	var myUrl 		= 	'ETransLOVCustomerIds.jsp?CustomerType='+customerType+'&searchString='+document.forms[0].corpCustomerId.value.toUpperCase()+'&whereClause='+whereClause+'&whereToSet=corpCustomerId';  

		var myUrl 		= 	'ETransLOVCustomerIds.jsp?customerType=Corporate&searchString='+document.forms[0].corpCustomerId.value.toUpperCase()+'&whereToSet=corpCustomerId&registrationLevel=C&registered='+customerType+'&operation=View&terminalId=<%=loginbean.getTerminalId()%>';  
		var myBars 		= 	'directories=no,location=left,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions 	=	'scrollbars=yes,width=400,height=360,resizable=no';
		var myFeatures 	= 	myBars+','+myOptions;
		var newWin 		= 	window.open(myUrl,'myDoc',myFeatures);

		if (!newWin.opener)
			newWin.opener = self;
		if (newWin.focus != null)
			newWin.focus();
		return false;
}

function setFlag(Obj)
{
	if(Obj.checked==true)
		Obj.value='Y';
	else
		Obj.value='N';

}
// @@ Suneetha added on 20050305 for Bulk Invoicing
function setBulkInvoiceRequiredFlag(Obj){
	document.forms[0].custInvoiceInfoButton.disabled= !Obj.checked;
}

function selectFrequency()
{
	var Url      =	'ETCustomerInvoiceInfo.jsp?'+
					'invoiceFrequencyValidDate='+document.forms[0].invoiceFrequencyValidDate.value+
					'&invoiceFrequencyFlag='+document.forms[0].invoiceFrequencyFlag.value+
					'&invoiceInfo='+document.forms[0].invoiceInfo.value;

	var Bars     =	'directories=no,location=no,menubar=no,status=no,titlebar=no';
	var Options  =	'scrollbars=yes,width=500,height=587,resizable=no';
	var Features =  Bars+''+Options;
	var Win      =  open(Url,'Doc',Features);
}
// @@ 20050305 for Bulk Invoicing

<!-- End of The  old script 	-->

var myTable = document.getElementById("etCustomer");
function checkFunctionAndExecute(){


		if(Mandatory()){
			cust.style.display='none';
			cust2.style.display='none';
			cust1.style.display='block';
			if("<%=operation%>"=="Add")
			{
				createRow(1);
			}
			else if("<%=operation%>"=="View" || "<%=operation%>"=="Modify")
			{
				setValues();
			}
		}//if(Mandatory()
		return true;
}

function setValues()
{
	var myTable = document.getElementById("etCustomer");
	<%				
		if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") )
		{
			if(operation.equalsIgnoreCase("View"))
			{
				readOnlyView = "readOnly";
			}
			else
			{
				readOnlyView = "";
			}
			if(listOfValues!=null && !listOfValues.isEmpty())
			{ 	
				custAddList			=	(ArrayList)listOfValues.get(2);
				custMoreAddList		=	(ArrayList)listOfValues.get(3);
			}
			int i=0;
			for( i=0;((i<custAddList.size())&&(i<custMoreAddList.size()));i++)
			{
				CustomerModel	tempCustMObj	=	(CustomerModel)custAddList.get(i);
				Address			tempAddObj		=	(Address)custMoreAddList.get(i);
			%>
				var rowCount="<%=i+1%>";
				createMRow(rowCount);
				document.getElementById("contactPersond"+rowCount).value			= "<%=tempCustMObj.getContactName() != null ? tempCustMObj.getContactName() :"" %>" ;
				document.getElementById("addressIddm"+rowCount).value				="<%=tempAddObj.getAddressId()%>";
				document.getElementById("designationd"+rowCount).value				= "<%=tempCustMObj.getDesignation() != null ? tempCustMObj.getDesignation() :"" %>"      ;
				for(var ad=0;ad<document.getElementById("add_Flagd"+rowCount).length;ad++)
				{
					if(document.getElementById("add_Flagd"+rowCount).options[ad].value=="P" && "<%=tempCustMObj.getAddressType()%>"=="P")
						document.getElementById("add_Flagd"+rowCount).options[ad].selected=true;
					if(document.getElementById("add_Flagd"+rowCount).options[ad].value=="B" && "<%=tempCustMObj.getAddressType()%>"=="B")
						document.getElementById("add_Flagd"+rowCount).options[ad].selected=true;
					if(document.getElementById("add_Flagd"+rowCount).options[ad].value=="D" && "<%=tempCustMObj.getAddressType()%>"=="D")
						document.getElementById("add_Flagd"+rowCount).options[ad].selected=true;
				}
				if("<%=operation%>"=="View")
				{
					document.getElementById("add_Flagd"+rowCount).disabled=true;
				}
				for(var ad=0;ad<document.getElementById("del_Flagd"+rowCount).length;ad++)
				{
					if(document.getElementById("del_Flagd"+rowCount).options[ad].value=="N" && "<%=tempCustMObj.getDelFlag()%>"=="N")
						document.getElementById("del_Flagd"+rowCount).options[ad].selected=true;
					if(document.getElementById("del_Flagd"+rowCount).options[ad].value=="Y" && "<%=tempCustMObj.getDelFlag()%>"=="Y")
						document.getElementById("del_Flagd"+rowCount).options[ad].selected=true;
				}
				if("<%=operation%>"=="View")
				{
					document.getElementById("del_Flagd"+rowCount).disabled=true;
				}
				document.getElementById("addressLine1d"+rowCount).value			= "<%=tempAddObj.getAddressLine1() != null ? tempAddObj.getAddressLine1() :"" %>";
				document.getElementById("addressLine2d"+rowCount).value			="<%=tempAddObj.getAddressLine2() != null ? tempAddObj.getAddressLine2() :"" %>"       ;
				document.getElementById("cityd"+rowCount).value							=  "<%=tempAddObj.getCity() != null ? tempAddObj.getCity() :"" %>"            ;
				document.getElementById("zipOrPostalCoded"+rowCount).value	="<%=tempAddObj.getZipCode() != null ? tempAddObj.getZipCode() :"" %>"                         ;
				document.getElementById("countryIdd"+rowCount).value				= "<%=tempAddObj.getCountryId() != null ? tempAddObj.getCountryId() :"" %>"                    ;
				document.getElementById("stated"+rowCount).value							=  "<%=tempAddObj.getState() != null ? tempAddObj.getState() :"" %>"     
				document.getElementById("contactNod"+rowCount).value				= "<%=tempAddObj.getPhoneNo() != null ? tempAddObj.getPhoneNo() :"" %>"      
				document.getElementById("emailIdd"+rowCount).value					="<%=tempAddObj.getEmailId() != null ? tempAddObj.getEmailId() :"" %>"    ;
				if(rowCount>1)
					document.getElementById("etCustomerdelbut"+rowCount).style.visibility="hidden";
			<%
				}
				if(operation.equals("Modify") && i==0)
				{
					%>
						createRow(1);
					<%
				}
			}
			if(operation.equals("View"))
			{
				%>
				document.getElementById("etCustomeraddbut"+(myTable.getAttribute("idcounter")*1-1)).style.visibility="hidden";
				<%
			}
			else
			{
				%>
				//createRow(myTable.getAttribute("idcounter")*1);
				<%
			}
	%>		
}
function createRow(counter)
{
	counter =counter*1;
	if(counter>1 && "<%=operation%>"!="View")
	{
		if(!validateBeforeCreation(counter))
			return false;
	}
	var myTableRow;
	var myTableCell1;
	var myTableCell2;
	var myTableCell3;
	var myTableCell4;
	var myTableCell5;

	var myTable				= document.getElementById("etCustomer");
	var myTableBody			= document.createElement("TBODY");
	var tbodyid="etCustomerTbody"+counter;
	myTableBody.setAttribute("id",tbodyid);
	myTableBody.setAttribute("class","body");

	myTableRow		= document.createElement('TR');
	myTableRow.setAttribute("class","formdata");

	myTableCell1		= document.createElement('TD');
	myTableCell1.setAttribute("colSpan","5");
	myTableCell1.setAttribute("align","left");
	myTableCell1.innerHTML="Address&nbsp;"+counter;
	myTableRow.appendChild(myTableCell1);
	myTableBody.appendChild(myTableRow);

	myTableRow=document.createElement('TR');
	myTableRow.setAttribute("class","formdata");

	myTableCell1=document.createElement('TD');
	myTableCell1.setAttribute("width","5%");
	myTableCell1.setAttribute("align","left");
	myTableCell1.setAttribute("valign","top");
	var delbutname="etCustomerdelbut"+counter;
	if(counter>1)
	{
		myTableCell1.innerHTML="<input id="+delbutname+" name="+delbutname+" type=button value='<<' onClick=deleteRow("+counter+"); class=input>";
	}
	else
	{
		myTableCell1.innerHTML="&nbsp;";
	}

	myTableCell2=document.createElement('TD');
	myTableCell2.setAttribute("width","30%");
	myTableCell2.innerHTML="Contact Person : <br> <input type='text' class='text' maxlength='30' size='20' name='contactPersond' id='contactPersond"+counter+"' value= ''  onKeyPress='return checkSpecialKeyCode()' onBlur = 'upper(this)' <%=readOnlyView%> ><input type='hidden' maxlength='20' size='20' name='addressIddm' id='addressIddm"+counter+"'  value= ''   >";
	myTableCell3=document.createElement('TD');
	myTableCell3.setAttribute("width","30%");
	myTableCell3.innerHTML="Designation :<br><input type='text' class='text' maxlength='50' size='20' name='designationd'  id='designationd"+counter+"'  value=  ''  onKeyPress='return checkSpecialKeyCode()' onBlur = 'upper(this)' <%=readOnlyView%> >";

	myTableCell4=document.createElement('TD');
	myTableCell4.setAttribute("align","left");
	myTableCell4.setAttribute("width","30%");
	var data="";
	data="AddressType :<font color='#FF0000'>*</font><br><select style='width:130px' name='add_Flagd'     id='add_Flagd"+counter+"'  class='select'><option value='P' selected>PickUP Address</option><option value='B' >Billing Address</option><option value='D' >Delivery Address</option></select>";
	<%
		if(operation.equals("Add")  )
		{
	%>
		data=data+"<input type=hidden  name='del_Flagd'  id='del_Flagd"+counter+"'   value='N'>";
	<%	
		}
		else if(operation.equals("View") || operation.equals("Modify"))
		{
	%>
		data=data+"<br>Delete : <select size='1' name='del_Flagd'  id='del_Flagd"+counter+"'   class='select'><option value='N' selected>NO</option><option value='Y' >YES</option></select>";
	<%	
		}
	%>
	myTableCell4.innerHTML=data;
	
	myTableCell5=document.createElement('TD');
	myTableCell5.setAttribute("width","5%");
	myTableCell5.setAttribute("align","left");
	myTableCell5.innerHTML="&nbsp;"

	myTableRow.appendChild(myTableCell1);
	myTableRow.appendChild(myTableCell2);
	myTableRow.appendChild(myTableCell3);
	myTableRow.appendChild(myTableCell4);
	myTableRow.appendChild(myTableCell5);
	myTableBody.appendChild(myTableRow);

	myTableRow=document.createElement('TR');
	myTableRow.setAttribute("class","formdata");

	myTableCell1=document.createElement('TD');
	myTableCell1.setAttribute("width","5%");
	myTableCell1.innerHTML="&nbsp;";

	myTableCell2=document.createElement('TD');
	myTableCell2.setAttribute("width","30%");
	myTableCell2.innerHTML="Address :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=75 size='20' name='addressLine1d'  id='addressLine1d"+counter+"'  value='' onBlur = 'upper(this)' <%=readOnlyView%> ><br><input type='text' class='text' maxlength=75 size='20' name='addressLine2d'  id='addressLine2d"+counter+"' value='' onBlur = 'upper(this)' <%=readOnlyView%> >";

	myTableCell3=document.createElement('TD');
	myTableCell3.setAttribute("width","30%");
	myTableCell3.innerHTML="City :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=30 size='20' name='cityd'  id='cityd"+counter+"' value='' onBlur = 'upper(this)' <%=readOnlyView%> >";

	myTableCell4=document.createElement('TD');
	myTableCell4.setAttribute("width","30%");
	data="";
	data="ZipCode&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;<input type='text' class='text' maxlength=10 size='5' name='zipOrPostalCoded'  id='zipOrPostalCoded"+counter+"'  <%=readOnlyView%> value='' onBlur = 'upper(this)'  ><br>CountryId :<font color='#FF0000'>*</font>&nbsp;&nbsp;<input type='text' class='text' maxlength=2 size='5' name='countryIdd'  id='countryIdd"+counter+"'  value='' onBlur = 'upper(this)' <%=readOnly%> onKeyPress='return getKeyCode()' >&nbsp;";
	<%   
		if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Add") )
		{
	%> 
		data=data+"<input type='button' class='input' value='...' name='lov_CountryId"+counter+"'  id='lov_CountryId"+counter+"' onClick='showCountryIds1("+counter+")' >";
	<%
		}
	%> 		
	myTableCell4.innerHTML=data;
	
	myTableCell5=document.createElement('TD');
	myTableCell5.setAttribute("width","5%");
	myTableCell5.innerHTML="&nbsp;"

	myTableRow.appendChild(myTableCell1);
	myTableRow.appendChild(myTableCell2);
	myTableRow.appendChild(myTableCell3);
	myTableRow.appendChild(myTableCell4);
	myTableRow.appendChild(myTableCell5);
	myTableBody.appendChild(myTableRow);

	myTableRow=document.createElement('TR');
	myTableRow.setAttribute("class","formdata");

	myTableCell1=document.createElement('TD');
	myTableCell1.setAttribute("align","center");
	myTableCell1.setAttribute("width","5%");
	myTableCell1.innerHTML="&nbsp;";

	myTableCell2=document.createElement('TD');
	myTableCell2.setAttribute("width","30%");
	myTableCell2.innerHTML="State or Province :<br> <input type='text' class='text' maxlength=30 size='20' name='stated'  id='stated"+counter+"'  <%=readOnlyView%> value='' onBlur = 'upper(this)' >";

	myTableCell3=document.createElement('TD');
	myTableCell3.setAttribute("width","30%");
	myTableCell3.innerHTML="Contact No :<br> <input type='text' class='text' maxlength=20 size='20' name='contactNod'  id='contactNod"+counter+"'  <%=readOnlyView%> value='' onBlur = 'upper(this)' >";

	myTableCell4=document.createElement('TD');
	myTableCell4.setAttribute("width","30%");		
	myTableCell4.innerHTML="Email :<br> <input type='text' class='text' maxlength=250 size='20' name='emailIdd'  id='emailIdd"+counter+"'  <%=readOnlyView%> value='' onBlur = 'upper(this)' >";
	
	myTableCell5=document.createElement('TD');
	myTableCell5.setAttribute("width","5%");
	myTableCell5.setAttribute("align","left");
	myTableCell5.setAttribute("vAlign","bottom");
	var addbutname="etCustomeraddbut"+counter;
	myTableCell5.innerHTML="<input id="+addbutname+" name='"+addbutname+"'  id='"+addbutname+"'  type=button value='>>' onClick=createRow("+(counter+1)+"); class=input>"

	myTableRow.appendChild(myTableCell1);
	myTableRow.appendChild(myTableCell2);
	myTableRow.appendChild(myTableCell3);
	myTableRow.appendChild(myTableCell4);
	myTableRow.appendChild(myTableCell5);
	myTableBody.appendChild(myTableRow);
	myTable.appendChild(myTableBody);


	counter=counter+1;
	myTable.setAttribute("idcounter",counter);
	var prevElement=1;
	for(var i=1;i<=counter;i++)
	{
		if(document.getElementById("etCustomeraddbut"+i)!=null)
		{
			document.getElementById("etCustomeraddbut"+i).style.visibility="hidden"; 
			prevElement=i;
		}
	}
	document.getElementById("etCustomeraddbut"+prevElement).style.visibility="visible"; 	
	if(counter>1 && "<%=operation%>"!="View" )
	{
		if(!defaultFunction(prevElement))
			return false;
	}
	document.getElementById("contactPersond"+prevElement).focus();
}
function validateBeforeCreation(currentRow)
{
	currentRow=currentRow*1;
	if(currentRow>1)
	{
		var prevRow=currentRow*1-1;
		if(document.getElementById("contactPersond"+prevRow)!=null && document.getElementById("del_Flagd"+prevRow).value=="N")
		{
			if(document.getElementById("addressLine1d"+prevRow).value=="")
			{
				alert("Please Enter Address");
				document.getElementById("addressLine1d"+prevRow).focus();
				return false;
			}
			if(document.getElementById("cityd"+prevRow).value=="")
			{
				alert("Please Enter City");
				document.getElementById("cityd"+prevRow).focus();
				return false;
			}
			if(document.getElementById("countryIdd"+prevRow).value=="" || document.getElementById("countryIdd"+prevRow).value.length<=0)
			{
				alert("Please Enter Country");
				document.getElementById("countryIdd"+prevRow).focus();
				return false;
			}
			else
			{
				loadValues();
				var flag=0;
				var countryId=document.getElementById("countryIdd"+prevRow).value;
				document.getElementById("countryIdd"+prevRow).value=countryId.toUpperCase();
					if(countryId.length > 0 )
					{
						for( i=0; i<arrayCountryIds.length; i++ )
						{
							if( countryId.toUpperCase()==arrayCountryIds[i])
							{
								flag = 1;
								break;
							}
						}
						if(flag==0)
						{
							alert("Please enter correct Country.");
							document.getElementById("countryIdd"+prevRow).focus();
						}
					}
				if(flag==0)
					return false;
			}//nd of else condition
		}
		if(!findDuplicates())
			return false;
		
	}
	return true;
}
function deleteRow(counter)
{		
	if(!validateBeforeDeletion(counter))
		return false;
	var myTableBody	= document.getElementById("etCustomerTbody"+counter);
	myTableBody.removeNode(true);
	var myTable	= document.getElementById("etCustomer");
	var idCounter=myTable.getAttribute("idcounter")*1-1;
	if(counter>1)
	{
		for(var i=idCounter;i>=1;i--)
		{
			if(document.getElementById("etCustomeraddbut"+i)!=null)
			{
				document.getElementById("etCustomeraddbut"+i).style.visibility="visible";
				break;
			}
		}
	}
	if(!defaultDeleteFunction(counter))
		return false;
	return true;
}

function defaultFunction(currentRow)
{
	return true;
}
function defaultDeleteFunction(currentRow)
{
	return true;
}
function validateBeforeDeletion(currentRow)
{
	return true;
}
function checkTheRowsSubmit()
{
	var myTableObj=document.getElementById("etCustomer");
	for(var i=1;i<myTableObj.getAttribute("idcounter")*1;i++)
	{
			if(document.getElementById("contactPersond"+i)==null || document.getElementById("del_Flagd"+i).value=="Y" )
				continue;
				if(document.getElementById("addressLine1d"+i).value=="")
				{
					alert("Please Enter Address");
					document.getElementById("addressLine1d"+i).focus();
					return false;
				}
				if(document.getElementById("cityd"+i).value=="")
				{
					alert("Please Enter City");
					document.getElementById("cityd"+i).focus();
					return false;
				}
				if(document.getElementById("countryIdd"+i).value=="" || document.getElementById("countryIdd"+i).value.length<=0)
				{
					alert("Please Enter Country");
					document.getElementById("countryIdd"+i).focus();
					return false;
				}
				if(document.getElementById("countryIdd"+i).value!="" || document.getElementById("countryIdd"+i).value.length>0)
				{
					loadValues();
					var flag=0;
					var countryId=document.getElementById("countryIdd"+i).value;
					document.getElementById("countryIdd"+i).value=countryId.toUpperCase();
						if(countryId.length > 0 )
						{
							for( rr=0; rr<arrayCountryIds.length;rr++ )
							{
								if( countryId.toUpperCase()==arrayCountryIds[rr])
								{
									flag = 1;
								}
							}
							if(flag==0)
							{
								alert("Please enter correct Country.");
								document.getElementById("countryIdd"+i).focus();
								return false;
							}
						}
				}
	}
	if(!findDuplicates())
		return false;
	return true;
}
function findDuplicates()
   {   
		var myTable = document.getElementById("etCustomer");
		for(var i=1;i<parseInt((myTable.getAttribute("idcounter")*1));i++)
	   {
			if(document.getElementById("contactPersond"+i)==null  || document.getElementById("del_Flagd"+i).value=="Y")		
				continue;
			  for(k=i+1;k<parseInt((myTable.getAttribute("idcounter")*1));k++)
				{
					if(document.getElementById("contactPersond"+k)==null)
						continue;
					if(document.getElementById("addressLine1d"+i).value==document.getElementById("addressLine1d"+k).value && document.getElementById("cityd"+i).value==document.getElementById("cityd"+k).value && document.getElementById("countryIdd"+i).value==document.getElementById("countryIdd"+k).value && document.getElementById("add_Flagd"+i).options[document.getElementById("add_Flagd"+i).selectedIndex].value==document.getElementById("add_Flagd"+k).options[document.getElementById("add_Flagd"+k).selectedIndex].value)
					{
						alert("Duplicate Addresses Not Allowed");
						document.getElementById("addressLine1d"+k).focus();
						return false;
					}
				}
	   }
      return true;
   }


   
function createMRow(counter)
{
	counter =counter*1;
	var myTableRow;
	var myTableCell1;
	var myTableCell2;
	var myTableCell3;
	var myTableCell4;
	var myTableCell5;

	var myTable				= document.getElementById("etCustomer");
	var myTableBody			= document.createElement("TBODY");
	var tbodyid="etCustomerTbody"+counter;
	myTableBody.setAttribute("id",tbodyid);
	myTableBody.setAttribute("class","body");

	myTableRow		= document.createElement('TR');
	myTableRow.setAttribute("class","formdata");

	myTableCell1		= document.createElement('TD');
	myTableCell1.setAttribute("colSpan","5");
	myTableCell1.setAttribute("align","left");
	myTableCell1.innerHTML="Address&nbsp;"+counter;
	myTableRow.appendChild(myTableCell1);
	myTableBody.appendChild(myTableRow);

	myTableRow=document.createElement('TR');
	myTableRow.setAttribute("class","formdata");

	myTableCell1=document.createElement('TD');
	myTableCell1.setAttribute("width","5%");
	myTableCell1.setAttribute("align","left");
	myTableCell1.setAttribute("valign","top");
	var delbutname="etCustomerdelbut"+counter;
	if(counter>1)
	{
		myTableCell1.innerHTML="<input id="+delbutname+" name="+delbutname+" type=button value='<<' onClick=deleteRow("+counter+"); class=input>";
	}
	else
	{
		myTableCell1.innerHTML="&nbsp;";
	}

	myTableCell2=document.createElement('TD');
	myTableCell2.setAttribute("width","30%");
	myTableCell2.innerHTML="Contact Person : <br> <input type='text' class='text' maxlength='30' size='20' name='contactPersondm' id='contactPersond"+counter+"' value= ''  onKeyPress='return checkSpecialKeyCode()' onBlur = 'upper(this)' <%=readOnlyView%> ><input type='hidden' maxlength='20' size='20' name='addressIddm' id='addressIddm"+counter+"'  value= ''   >";
	myTableCell3=document.createElement('TD');
	myTableCell3.setAttribute("width","30%");
	myTableCell3.innerHTML="Designation :<br><input type='text' class='text' maxlength='30' size='20' name='designationdm'  id='designationd"+counter+"'  value=  ''  onKeyPress='return checkSpecialKeyCode()' onBlur = 'upper(this)' <%=readOnlyView%> >";

	myTableCell4=document.createElement('TD');
	myTableCell4.setAttribute("align","left");
	myTableCell4.setAttribute("width","30%");
	var data="";
	data="AddressType :<font color='#FF0000'>*</font><br><select style='width:130px' name='add_Flagdm'     id='add_Flagd"+counter+"'  class='select'><option value='P' selected>PickUP Address</option><option value='B' >Billing Address</option><option value='D' >Delivery Address</option></select>";
	<%
		if(operation.equals("Add")  )
		{
	%>
		data=data+"<input type=hidden  name='del_Flagdm'  id='del_Flagd"+counter+"'   value='N'>";
	<%	
		}
		else if(operation.equals("View") || operation.equals("Modify"))
		{
	%>
		data=data+"<br>Delete : <select size='1' name='del_Flagdm'  id='del_Flagd"+counter+"'   class='select'><option value='N' selected>NO</option><option value='Y' >YES</option></select>";
	<%	
		}
	%>
	myTableCell4.innerHTML=data;
	
	myTableCell5=document.createElement('TD');
	myTableCell5.setAttribute("width","5%");
	myTableCell5.setAttribute("align","left");
	myTableCell5.innerHTML="&nbsp;"

	myTableRow.appendChild(myTableCell1);
	myTableRow.appendChild(myTableCell2);
	myTableRow.appendChild(myTableCell3);
	myTableRow.appendChild(myTableCell4);
	myTableRow.appendChild(myTableCell5);
	myTableBody.appendChild(myTableRow);

	myTableRow=document.createElement('TR');
	myTableRow.setAttribute("class","formdata");

	myTableCell1=document.createElement('TD');
	myTableCell1.setAttribute("width","5%");
	myTableCell1.innerHTML="&nbsp;";

	myTableCell2=document.createElement('TD');
	myTableCell2.setAttribute("width","30%");
	myTableCell2.innerHTML="Address :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=75 size='20' name='addressLine1dm'  id='addressLine1d"+counter+"'  value='' onBlur = 'upper(this)' <%=readOnlyView%> ><br><input type='text' class='text' maxlength=75 size='20' name='addressLine2dm'  id='addressLine2d"+counter+"' value='' onBlur = 'upper(this)' <%=readOnlyView%> >";

	myTableCell3=document.createElement('TD');
	myTableCell3.setAttribute("width","30%");
	myTableCell3.innerHTML="City :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=30 size='20' name='citydm'  id='cityd"+counter+"' value='' onBlur = 'upper(this)' <%=readOnlyView%> >";

	myTableCell4=document.createElement('TD');
	myTableCell4.setAttribute("width","30%");
	data="";
	data="ZipCode&nbsp;&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;&nbsp;<input type='text' class='text' maxlength=10 size='5' name='zipOrPostalCodedm'  id='zipOrPostalCoded"+counter+"'  <%=readOnlyView%> value='' onBlur = 'upper(this)'  ><br>CountryId :<font color='#FF0000'>*</font>&nbsp;&nbsp;<input type='text' class='text' maxlength=2 size='5' name='countryIddm'  id='countryIdd"+counter+"'  value='' onBlur = 'upper(this)' <%=readOnly%> onKeyPress='return getKeyCode()' >&nbsp;";
	<%   
		if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Add") )
		{
	%> 
		data=data+"<input type='button' class='input' value='...' name='lov_CountryId"+counter+"'  id='lov_CountryId"+counter+"' onClick='showCountryIds1("+counter+")' >";
	<%
		}
	%> 		
	myTableCell4.innerHTML=data;
	
	myTableCell5=document.createElement('TD');
	myTableCell5.setAttribute("width","5%");
	myTableCell5.innerHTML="&nbsp;"

	myTableRow.appendChild(myTableCell1);
	myTableRow.appendChild(myTableCell2);
	myTableRow.appendChild(myTableCell3);
	myTableRow.appendChild(myTableCell4);
	myTableRow.appendChild(myTableCell5);
	myTableBody.appendChild(myTableRow);

	myTableRow=document.createElement('TR');
	myTableRow.setAttribute("class","formdata");

	myTableCell1=document.createElement('TD');
	myTableCell1.setAttribute("align","center");
	myTableCell1.setAttribute("width","5%");
	myTableCell1.innerHTML="&nbsp;";

	myTableCell2=document.createElement('TD');
	myTableCell2.setAttribute("width","30%");
	myTableCell2.innerHTML="State or Province :<br> <input type='text' class='text' maxlength=30 size='20' name='statedm'  id='stated"+counter+"'  <%=readOnlyView%> value='' onBlur = 'upper(this)' >";

	myTableCell3=document.createElement('TD');
	myTableCell3.setAttribute("width","30%");
	myTableCell3.innerHTML="Contact No :<br> <input type='text' class='text' maxlength=20 size='20' name='contactNodm'  id='contactNod"+counter+"'  <%=readOnlyView%> value='' onBlur = 'upper(this)' >";

	myTableCell4=document.createElement('TD');
	myTableCell4.setAttribute("width","30%");		
	myTableCell4.innerHTML="Email :<br> <input type='text' class='text' maxlength=250 size='20' name='emailIddm'  id='emailIdd"+counter+"'  <%=readOnlyView%> value='' onBlur = 'upper(this)' >";
	
	myTableCell5=document.createElement('TD');
	myTableCell5.setAttribute("width","5%");
	myTableCell5.setAttribute("align","left");
	myTableCell5.setAttribute("vAlign","bottom");
	var addbutname="etCustomeraddbut"+counter;
	myTableCell5.innerHTML="<input id="+addbutname+" name='"+addbutname+"'  id='"+addbutname+"'  type=button value='>>' onClick=createRow("+(counter+1)+"); class=input>"

	myTableRow.appendChild(myTableCell1);
	myTableRow.appendChild(myTableCell2);
	myTableRow.appendChild(myTableCell3);
	myTableRow.appendChild(myTableCell4);
	myTableRow.appendChild(myTableCell5);
	myTableBody.appendChild(myTableRow);
	myTable.appendChild(myTableBody);


	counter=counter+1;
	myTable.setAttribute("idcounter",counter);
	var prevElement=1;
	for(var i=1;i<=counter;i++)
	{
		if(document.getElementById("etCustomeraddbut"+i)!=null)
		{
			document.getElementById("etCustomeraddbut"+i).style.visibility="hidden"; 
			prevElement=i;
		}
	}
	document.getElementById("etCustomeraddbut"+prevElement).style.visibility="visible"; 	
	document.getElementById("contactPersond"+prevElement).focus();
}

//@@ Srivegi added on 20050224 (AES-SED)
function chrnum(input)
		{
			s = input.value;
			filteredValues = "'~!@#$%^&*()_+=|\:;<>,./?";
			var i;
			var returnString = "";
			var flag = 0;
			for (i = 0; i < s.length; i++)
			{
				var c = s.charAt(i);
				if(filteredValues.indexOf(c) == -1)
						returnString += c.toUpperCase();
				else
					flag = 1;
			}
			if( flag==1 )
			{
				alert("Special Characters not allowed");
				var field = document.forms[0];
				for(i = 0; i < field.length; i++)
					if( field.elements[i] == input )
						document.forms[0].elements[i].focus();
			}

			input.value = returnString;
			if(flag==1) return false
			if(flag==0) return true
		}
//@@ 20050224 (AES-SED)
</script>

</head>
<body <%= ( (operation.equals("View") ) ||  (operation.equals("Delete")) ) ? "":"onload='placeFocus();'"%> >
<a name="top"></a> 
<form  method="post"  onSubmit=" return Mandatory();" action="<%=actionValue%>">  
<table width="800" border="0" cellspacing="0" cellpadding="0">
<tr>
<td bgcolor="#FFFFFF" valign="top" >




<span id='cust' style='display:block'>
			<table border='0' width='800' cellpadding='4' cellspacing='1' bgcolor='#ffffff'>
			<tr valign='top' class='formlabel'>    	  
				<td colspan='3'><table width='760' border='0' ><tr class='formlabel'><td><%=label%>Customer Registration - <%=operation%> Module</td>
				<td align=right><%=loginbean.generateUniqueId("ETCustomerRegistrationAddMoreAdress.jsp",operation)%></td>
			</tr>
		</table>
	</td>
	</tr>
	<tr valign='top' class='formdata'>
		<td colspan='2'>&nbsp;</td>
	</tr>
	<tr valign='top' class='formdata'>
		<td  width='50%'>TerminalId:<font color='#FF0000'><sup>*</sup></font><br><input type='text' class='text' name='terminalId' value = '<%=terminalId%>'  size='16' maxlength='16' readOnly></td>
	<% 
		if(registrationLevel.equalsIgnoreCase("T"))//terminal level customer
		{
	%>
		<td width='50%'>Abbreviated Name:<font color='#FF0000'>*</font><br><input type='text' class='text' name='abbrName' value = '<%=abbrName%>'  <%=abbrNameReadOnly%>   size='6' maxlength='4' onKeyPress='return getKeyCode()' onBlur = 'stringFilter(abbrName);' onChange='verifyAbbrName(this)'><input type='hidden' name='customerId' value = '<%=customerId%>' >       
	<%	
		}else // Corporate level customer
		{
	%>
		<td  width='50%'> <%=label%> Customer Id :<font color='#FF0000'>*</font><br><input type='text' class='text' name='customerId' value = '<%=customerId%>' <%=customerIdReadOnly%>   size='16' maxlength='16' onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(customerId);' onChange='verifyAbbrName(this)'><input type='hidden' name='abbrName' value = '<%=abbrName%>' >
	<%
		}
	%>	
		</td>
	</tr>
	<tr valign='top' class='formdata'>
		<td  width='50%'>Company Name:<font color='#FF0000'><sup>*</sup></font><br><input type='text' class='text' name='companyName' value = '<%=companyName%>' <%=customerIdReadOnly%>  size='50' maxlength='50'  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(companyName);' onChange='verifyCompanyName(this)' >
		</td>
		<td width='50%'>
	<% 
		if(registrationLevel.equalsIgnoreCase("T"))//terminal level customer
		{
	%>
		 Corporate Customer Id:<br><input type='text' class='text' name='corpCustomerId'  size='16' maxlength='16' value='<%=corpCustomerId!=null ? corpCustomerId : "" %>' onKeyPress='return checkSpecialKeyCode()' <%=readOnly%> onBlur = 'stringFilter(corpCustomerId)' ><input type='hidden' name='typeOfCustomer' value='Customer'>
	<%
		if(operation.equalsIgnoreCase("Add")||operation.equalsIgnoreCase("Modify"))
			{
		%>
			<input type='button' value='...' name='probutton' onClick='getCustomerIds()' class='input'>
		<%
			}
		}else // Corporate level customer
		{
	%>
		<input type='hidden' name='typeOfCustomer' value='Corporate'><input type='hidden' name='corpCustomerId' >
	<%
		}
	%>
		</td>
	</tr>
	<tr valign='top' class='formdata'>
	<% //@@ Added by Srivegi on 20040224 (AES-SED) %>
		<td  >Contact Person(First Name/Last Name):<br>
		<input type='text' class='text' name='contactPerFistName' value='<%=contactName != null ? contactName : "" %>' size='10' maxlength='30'  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(contactPerFistName)' <%=readOnly%>>
          <input type='text' class='text' name='contactPerLastName' value='<%=contactPersonLastName != null ? contactPersonLastName : "" %>' size='10' maxlength='30'  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(contactPerLastName)' <%=readOnly%>></td>
    <%//@@ 20040224 (AES-SED)%>
		<!--<input type='text' class='text' name='contactName'  value = '<%=contactName != null ? contactName : "" %>'  size='35' maxlength='30'  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(contactName)' <%=readOnly%> >
		<input type='text' class='text' name='contactName'  value = '<%=contactName != null ? contactName : "" %>'  size='35' maxlength='30'  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(contactName)' <%=readOnly%> ></td>-->
		<td  width='340'>Designation:<br><input type='text' class='text' name='designation' value= '<%=designation != null ? designation : "" %>'    size='30' maxlength ='30'    onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(designation)' <%=readOnly%> ></td>
	</tr>
	 
	<% //@@ Added by Srivegi on 20040224 (AES-SED) %>	
	<tr valign='top' class='formdata'>
	      <td width='181'>EIN/SSN # :<br>
          <input type='text' class='text' name='einssn' value='<%=einssn%>' size='12' <%=readOnly%> onBlur='stringFilter(einssn);chrnum(einssn);' onKeyPress='' maxlength='12'></td>
		<%	// @@ Suneetha added on 20050305 for Bulk Invoicing %>
		  <td></td>
		<%  // @@ 20050305 for Bulk Invoicing %>
		  </tr>
	
    <tr valign='top' class='formdata'>
	      <td width='181'>Customer :<br>
		  <!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5294 on 200505010.-->
          <select name='knownShipper' class='select' value="Known" <%=disabled%>>
		<% // @@ Senthil Replaced on 20050412 for SPETI-5407 %>
		<%//  <option value='U' >Known</option> %>
		<%//  <option value='K' >Unknown</option></td> %>
		
		  <option value='K' <%= "K".equals(knownShipper1)?"selected":""%> >Known</option>
		  <option value='U' <%= "U".equals(knownShipper1)?"selected":""%> >Unknown</option></td>
		
		  <% // @@ 20050412 %>
		<%	// @@ Suneetha added on 20050305 for Bulk Invoicing %>
		  <td></td>
		<%  // @@ 20050305 for Bulk Invoicing %>
		  </tr>
   <%//@@ 20040224 (AES-SED)%>

   <tr valign='top' class='formdata'>
		<td >Credit Days:<br><input type='text' class='text' name='creditdays'  size='5'   maxlength='4' value='<%=creditdays%>' <%=readOnly%>   onkeyPress='return getDotNumberCode(this)' ><input type='hidden' name='scode' size='16' maxlength='16'  value=' '></td>
		<td width='50%'>Credit Limit :<br><input type='text' class='text' name='creditlimit' size='13' onBlur='decCheck(this)' onkeyPress='return getDotNumberCode(this)' maxlength='13' value='<%=creditlimit%>' <%=readOnly%> >&nbsp;<input type='text' class='text' name='currencyId' size='5' maxlength='3' onBlur = 'stringFilter(this)' onKeyPress='return getKeyCode()' value='<%=currency!=null?currency:"" %>' <%=readOnly%>>
	<%
		if(operation.equalsIgnoreCase("Add") || operation.equalsIgnoreCase("Modify"))
		{
	%>	
		&nbsp;<input type='button' value='...' name='creditcurrency' onClick='showCurrencyLOV()' class='input'>
	<%
		}
	%>
		</td>
	</tr>
</table>
					<table border='0' width='800' cellpadding='4' cellspacing='1' bgcolor='#ffffff'>
				<%	// @@ Suneetha added on 20050305 for Bulk Invoicing %>
					<tr valign='top' class='formdata'> 
					<td colspan='2'> 
					<!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5294 on 200505010.-->
					<input type='checkbox' name='bulkInvoiceRequired' value='Y' <%=disabled%> onClick='setBulkInvoiceRequiredFlag(this)' <%="Y".equals(bulkInvoiceRequired)?"checked":""%>>
					Consolidated Invoicing  </td>
					<td><!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5294 on 200505010.-->
					<input type='button' value='Select Invoicing Frequency' name='custInvoiceInfoButton' onClick='selectFrequency()' <%=disabled%> class='input' <%="Y".equals(bulkInvoiceRequired)?"":"disabled"%>>
					</td></tr>
				<%  // @@ 20050305 for Bulk Invoicing %>
					<tr valign='top' class='formdata'> 
					<td colspan='2' rowspan='2' width='50%'>Address:<font color='#FF0000'>*</font><br>
					<input type='text' class='text' name='addressLine1' value = '<%=addressLine1 != null ? addressLine1 :"" %>' size='50' maxlength='75'  <%=readOnly%>    onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(addressLine1);' onChange='verifyAddressLine11(this)' ><BR>
					<input type='text' class='text' name='addressLine2' value = '<%=addressLine2 != null ? addressLine2 :"" %>' size='50' maxlength='75'  <%=readOnly%>  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(addressLine2);verifyAddressLine22(this)' >
					</td>
					<td colspan='2' width='339'>Zip or Postal Code:<br>
					<input type='text' class='text' name='zipCode'  value = '<%=zipCode != null ? zipCode :"" %>' size='20' maxlength='15'  <%=readOnly%>   onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(zipCode)' >
					</td>
					</tr>
					<tr class='formdata'> 
					<td colspan='2'  width='339'>Contact No:<br>
					<input type='text' class='text' name='phoneNo' value = '<%=phoneNo != null ? phoneNo :"" %>' size='20' maxlength='15'  <%=readOnly%>  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(phoneNo)' >
					</td>
					</tr>
					<tr valign='top' class='formdata'> 
					<td colspan='2'>City:<font color='#FF0000'>*</font><br>
                    <% //@@ Srivegi Modified on 20050520 for UAT %>  
					<input type='text' class='text' name='city' value = '<%=city != null ? city :"" %>' size='30' maxlength='30'  <%=readOnly%> onKeyPress='return checkSpecialKeyCode()' <%= "Add".equals(operation) ? "onBlur = 'stringFilter(city);verifyCity22(this)'" : "onBlur = 'stringFilter(city)'" %>>
					</td>
                    <% //@@ 20050520 for UAT %> 
					<td colspan='2' width='339'>Fax No:<br>
					<input type='text' class='text' name='fax' value = '<%=fax != null ? fax :"" %>' size='20' maxlength='15'  <%=readOnly%> onKeyPress='return getValidFaxCode();' onBlur = 'stringFilter(fax)' >
					</td>
					</tr>
					<tr valign='top' class='formdata'> 
					<td colspan='2' >State or Province:<br>
					<input type='text' class='text' name='state' value = '<%=state != null ? state :"" %>' size='30' maxlength='30'  <%=readOnly%>    onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(state);verifyState22(this)' >
					</td>
					<td colspan='2' width='339'> Country Id:<font color='#FF0000'>*</font><br>
					<input type='text' class='text' name='countryId' value = '<%=countryId != null ? countryId :"" %>' size='4' maxlength='2'  <%=readOnly%>  onKeyPress='return getKeyCode()' 	  onBlur='stringFilter(countryId)' >
          
<%           
			if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Add") )
			{
%> 
					&nbsp;<input type='button' class='input' value='...' name='lov_CountryId'  onClick='showCountryIds()' >
<%
			}
%> 
          </td>
					</tr>
					<tr valign='top' class='formdata'> 
					<td colspan='2' >Operations EmailId:<font color='red'>*</font><br>
					<textarea  class='select' cols='35' rows='4' name='opEmailId' onKeyPress='return checkEmailSpecialKeyCode()'  <%=readOnly%> onBlur='verifyOperationsEmail11(this)'><%=opEmailId != null ? opEmailId :"" %></textarea>
					</td>
					<td colspan='2' width='50%'>EmailId: <br>
					<textarea class='select' cols='35' rows='4' name='emailId' onKeyPress='return checkEmailSpecialKeyCode()'  <%=readOnly%> ><%=emailId != null ? emailId :"" %></textarea>
					</td>
					</tr>
<%   
		if(customerType.equalsIgnoreCase("U"))
		{
			if(operation.equalsIgnoreCase("Upgrade"))
			{
      %> 
					<tr valign='top' class='formdata'> 
					<td colspan='2'> 
					<input type='checkbox' name='creditFlag' value='N' onClick='setFlag(this)' >
					Apply Credit Limit </td>
					<td colspan='2' rowspan='2'>Notes<br>
					<textarea name='notes' class='select' cols='35' rows='4' onKeyPress='return checkSpecialKeyCode()' <%=readOnly%>> <%=notes !=null ? notes : "" %></textarea>
					</td>
					</tr>
					<tr valign='top' class='formdata'> 
					<td colspan='2'>Mail Notifications:<br>
					<input type='checkbox' name='masterCloseFlag' value='N'  onClick='setFlag(this)' >
					Master Close 
					<input type='checkbox' name='bbFlag' value='N'  onClick='setFlag(this)'>
					BreakBulk<br><br>
                    Preferred Service Level<br><input type='text' class='text' size=8 name=serviceLevelId onKeyPress='return getKeyCode()'>
					&nbsp;<input class='input' type=button name=btnServiceLov value='...' onclick='showServiceLevelLOV()'>
					</td></tr>
<%
			}
			else if(!operation.equalsIgnoreCase("Upgrade"))
			{
%> 
					<tr valign='top' class='formdata'> 
					<td colspan='4'> 
					<input type='hidden' name='creditFlag' value='N' >
					<input type='hidden' name='masterCloseFlag' value='N'>
					<input type='hidden' name='bbFlag' value='N'>
					Notes<br>
					<textarea name='notes' class='select' onKeyPress='return checkSpecialKeyCode()' cols='35' rows='4' <%=readOnly%>><%=notes !=null ? notes : "" %></textarea>
					</td>
					</tr>
<%
			}
			}	
			else if(customerType.equalsIgnoreCase("R")||operation.equalsIgnoreCase("Delete"))
			{
%> 
					<tr valign='top' class='formdata'> 
					<td colspan='2'> 
					<input type='checkbox' name='creditFlag' value='N' onClick='setFlag(this)' <%=disabled%> >
					Apply Credit Limit </td>
					<td colspan='2' rowspan='2'>Notes<br>
					<textarea name='notes' class='select' cols='35' onKeyPress='return checkSpecialKeyCode()' rows='4' <%=readOnly%>><%=notes !=null ? notes : "" %></textarea>
					</td>
					</tr>
					<tr valign='top' class='formdata'> 
					<td colspan='2'>Mail Notifications:<br>
					<input type='checkbox' name='masterCloseFlag' value='N'  onClick='setFlag(this)' <%=disabled%>>
					Master Close 
					<input type='checkbox' name='bbFlag' value='N' <%=disabled%> onClick='setFlag(this)'>
					BreakBulk<br><br>
                    Preferred Service Level<br><input type='text' class='text' size=8 name=serviceLevelId onKeyPress='return getKeyCode()' <%=readOnly%> value=<%=serviceLevelId != null ? serviceLevelId :"" %>>
<%           
			if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Add") )
			{
%> 
					&nbsp;<input class='input' type=button name=btnServiceLov value='...' onclick='showServiceLevelLOV()'>
<%
			}
%>           
					</td></tr>
<%
			}
%> 
					<tr valign='top' class='denotes'> 
					<td colspan='2' ><font color='#FF0000'>*</font>Denotes Mandatory <br>
					<% // Changed By senthil prabhu.s for  1784 on 20052405 %>
					Note: Please separate each EmailId by a semi colon (;) </font></font>
					</td>
					<td colspan='2'  align='right'>
<%
		if(operation.equalsIgnoreCase("Add")){		
%>
						<input type='button' name='add_more_Address' value='<%=operation%> Address' class='input' onClick='checkFunctionAndExecute()'> &nbsp;&nbsp;&nbsp;

<%
		}
%>


<%
			if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Upgrade") ){
				if(customerType.equalsIgnoreCase("R")){
%>	
					<input type='button' name='add_more_Address' value='<%=operation%> Address' class='input' onClick='checkFunctionAndExecute()'> &nbsp;&nbsp;&nbsp;
<%
				}//end of if
			}//end of if	
%>
<%
			if(operation.equalsIgnoreCase("View")){
				// verifying whether CustomerDetail object is null or not
				if(listOfValues!=null && !listOfValues.isEmpty()){ 	
					//customerModel		=	(CustomerModel)listOfValues.get(0);
						custAddList			=	(ArrayList)listOfValues.get(2);
						custMoreAddList		=	(ArrayList)listOfValues.get(3);
				}					
				if((custAddList.size()>0) && (custMoreAddList.size()>0)){
%>
						<input type='button' name='add_more_Address' value='<%=operation%> Address' class='input' onClick='checkFunctionAndExecute()'> &nbsp;&nbsp;&nbsp;

<%
				}
			}
%>

					<input type='submit' value='<%=submitValue%>' name='submit' class='input'>
					</td>
					</tr>
					</table>
	</span>


<!-- 
	Used div instead of span cust
	End of div tag
-->

<!-- 
	Used div instead of span  cust1
	Start of div tag
-->
<span id='cust1' style='display:none'>
<table width='800' border='0' cellspacing='1' cellpadding='4' bgcolor='#ffffff' >
<tr class='formlabel'> 
<td colspan='5'>Customer Registration <%=operation%> - More Details </td>
</tr>
</table>
<table border='0' width="800" cellspacing='1' cellpadding='4' nowrap id="etCustomer" idcounter="1" 
	defaultElement="addressLine1d" xmlTagName="etCustomer" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" >		 
</table>	
<%
	if(operation.equalsIgnoreCase("View"))
	{
%>
	<table width='800' border='0' cellspacing='1' cellpadding='4' >
	<tr class='denotes'> 
		<td colspan='4' align='right'><input type='submit' value='Continue' name='submit' class='input'></td>
	</tr>
	</table>	
<%
	}
	else
	{
%>
	<table width='800' border='0' cellspacing='1' cellpadding='4' >
	<tr class='denotes'> 
		<td colspan='4' align='right'> <input type='submit' value='<%=submitValue%>' name='submit' class='input' onClick='return checkTheRowsSubmit()'></td>
	</tr>
	</table>	
<%
	}
%>
</span>
<!-- 
	Used div instead of span cust1
	End of div tag
-->


<span id='cust2' style='display:block;position:absolute;align:left;left:540;top:660;width=10;height=10;'>

<%  
	if(!(operation.equals("Add"))){
%> 
		<input type="hidden" name="customerAddressId" value="<%=customerModel.getCustomerAddressId()%>" >
		<input type="hidden" name="registered" value="<%=registered%>" >
<%
	}
%> 
<input type='hidden' name='prqCreateFlag' value='N' > 
<input type='hidden' name='prqModifyFlag' value='N'> 
<input type='hidden' name='prqDeleteFlag' value='N'> 
<input type='hidden' name='houseCreateFlag' value='N' > 
<input type='hidden' name='houseModifyFlag' value='N'> 
<input type='hidden' name='houseDeleteFlag' value='N'> 
<input type='hidden' name='masterCreateFlag' value='N'> 
<input type='hidden' name='masterModifyFlag' value='N'> 
<input type='hidden' name='masterDeleteFlag' value='N'> 
<input type='hidden' name='bbModifyFlag' value='N'> 
<input type='hidden' name='doCreateFlag' value='N'> 
<input type="hidden" name="registrationLevel" value="<%=registrationLevel%>">
<input type="hidden" name="Operation"  value="<%=operation%>">   
<% // @@ Suneetha Added on 20050307 for Bulk Invoicing %>
<input type="hidden" name="invoiceFrequencyValidDate" value="<%=invoiceFrequencyValidDate==null ? "" : invoiceFrequencyValidDate%>">          
<input type="hidden" name="invoiceFrequencyFlag" value="<%=invoiceFrequencyFlag==null ? "" : invoiceFrequencyFlag%>">           
<input type="hidden" name="invoiceInfo" value="<%=invoiceInfo==null ? "" : invoiceInfo%>">               
<% // @@ 20050307 for Bulk Invoicing %>
</div> &nbsp;
</td>
</tr>
</table>
</form>
</body>
</html>
<%
	}
	catch(Exception ee)	{
		ee.printStackTrace();

		errorMessageObject = new ErrorMessage("Error while loading the details with CustomerId : " + customerId,"ETCustomerRegistrationEnterId.jsp?Customer="+Customer+"&registrationLevel="+registrationLevel); 

		keyValueList = new ArrayList();

		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject);

%>
		<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	}//CATCH


%>
