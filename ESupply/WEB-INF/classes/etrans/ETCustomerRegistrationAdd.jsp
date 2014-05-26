
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
			com.foursoft.etrans.setup.customer.java.CustContactDtl,
			com.foursoft.etrans.common.bean.Address,
			com.foursoft.esupply.common.java.ErrorMessage,
			com.foursoft.esupply.common.java.KeyValue"
	%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

	<%
	//@@ Srivegi added on 20050224 (AES-SED)
        String contactPersonLastName			=	"";
		String knownShipper1					=	"";
		String einssn					                =	"";
   //@@
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
		String addressLine3			="";
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
		String  devision			=	"";
		String	paymentTerms		=	"";
        String serviceLevelId       =   null;  // It Contains Customer Prefered ServiceLevel By K.N.V.Prasada Reddy
		String salesPersonId			=	""; // Added by Gowtham to show SalesPerson on 21-Feb-2011

		ArrayList currencyid			= null;    // a Vector to store currency Ids
		OperationsImpl operationsImpl	= new OperationsImpl();
		ArrayList listOfValues			= null;   	
		ErrorMessage errorMessageObject = null;
		ArrayList	 keyValueList	    = null;
		ArrayList	 custAddList		= new ArrayList();
		ArrayList	 custMoreAddList	= new ArrayList();
		ArrayList    allDetailsList		= null;   
		CustomerModel	customerModelDetails= null;
		ArrayList	custDtl				= new ArrayList();
		CustContactDtl custDtls			=null;

		String		readOnlyView		= ""; //This is used for if operation is view than i have seting to fieds as readonly
		boolean		statusView			= true;
		ArrayList   terminalList        = null;//@@ Added by subrahmanyam for 167669 on 27/04/09
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
						customerId   = request.getParameter("customerId");

						terminalId   =	loginbean.getTerminalId();
						actionValue  = 	"ETCustomerRegistrationProcess.jsp?Operation="+operation;		
				if(!operation.equalsIgnoreCase("Add")){
					// checking for the type of customer
					if(Customer.equalsIgnoreCase("NCCS")){
						if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") ){
							customerType = request.getParameter("customerType");
							if(customerType.equalsIgnoreCase("Registered") )
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

		//allDetailsList=customerRemote.getAllCustomerDetails(loginbean.getTerminalId());

		utilHome		=	(SetUpSessionHome)initialContext.lookup("SetUpSessionBean");
		utilRemote		=	(SetUpSession)utilHome.create();
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
				actionValue		= "ETCustomerRegistrationEnterIdC.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
				if(registrationLevel!=null && registrationLevel.equals("C"))
					actionValue	=	"ETCustomerRegistrationEnterIdC.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;

				value			= "Continue";
				disabled 		= "disabled";
		}else if(operation.equalsIgnoreCase("Upgrade") ){
				readOnly	    = "readonly";
				actionValue	    = "ETCustomerRegistrationProcess.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
				value		    = "Upgrade";
				disabled 	    = "disabled";

		}else if(operation.equalsIgnoreCase("Delete") )
		{
				readOnly		= "readonly";
				disabled 		= "disabled";
				value           = "Delete";
				actionValue			= "ETCustomerRegistrationProcess.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
		
		}
		else
		{
				readOnly		= "";
				disabled		="";
				actionValue		= "ETCustomerRegistrationProcess.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
				value			= "Submit";
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
				paymentTerms  =	customerModel.getPaymentTerms();
				devision	  =	customerModel.getDivision();
				salesPersonId = customerModel.getSalesPersonCode(); // Added by Gowtham to show salesperson in modify case.

			
				custDtl=(ArrayList)customerModel.getContactDtl();

				//@@ Srivegi added on 20050224 (AES-SED)
                contactPersonLastName			=	customerModel.getContactLastName()!=null?customerModel.getContactLastName():"";
				knownShipper1					=	customerModel.getcustType()!=null?customerModel.getcustType():"";
				einssn					        =	customerModel.getEINSSNNo()!=null?customerModel.getEINSSNNo():"";
                //@@ 20050224
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
				addressLine3		= addressModel.getAddressLine3();
				city  				= addressModel.getCity();
				state  				= addressModel.getState();
				zipCode  			= addressModel.getZipCode();
				countryId  			= addressModel.getCountryId();
				phoneNo  			= addressModel.getPhoneNo();
				emailId  			= addressModel.getEmailId();
				fax  				= addressModel.getFax();
			}
	}else{

				String url				=	"ETCustomerRegistrationEnterIdC.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
				if(registrationLevel!=null && registrationLevel.equals("C"))
					url				=	"ETCustomerRegistrationEnterIdC.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
				
				errorMessageObject = new ErrorMessage("Record not found for "+label+" CustomerId : "+ customerId,url); 

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
<script language="javascript" src ="../html/dynamicContent.js"></script>
<script language="javascript" src="html/eSupply.js"></script>
<script language="javascript">
<!-- This code is Used for old script 	-->

var abbrName1		=	new Array();
var companyName1		=	new Array();
var addressLine11	=	new Array();
var addressLine22	=	new Array();
var addressLine33	=	new Array();
var city11			=	new Array();
var state11			=	new Array();
var zipCode11		=	new Array();
var opEmailId11		=	new Array();
//@@ Srivegi added on 20050224 (AES-SED)
var lastName	=	new Array();
var knownCust			=	new Array();
var einssn			=	new Array();
var custAddrType	=	new Array(<%=custDtl.size()%>);
var custContactPerson=	new Array(<%=custDtl.size()%>);
var custDesignation	=	new Array(<%=custDtl.size()%>);
var custDept		=	new Array(<%=custDtl.size()%>);
var custZipCode		=	new Array(<%=custDtl.size()%>);
var custContact		=	new Array(<%=custDtl.size()%>);
var custFax			=	new Array(<%=custDtl.size()%>);
var custEmail		=	new Array(<%=custDtl.size()%>);
var custContactId	=	new Array(<%=custDtl.size()%>);
//@@ 20050224
var Operation	=	'<%=operation%>';
var customerType	=	'<%=customerType%>';
var registrationLevel='<%=registrationLevel%>';
arrayCountryIds      = new Array();
var currency_arr     = new Array();
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
if(!operation.equalsIgnoreCase("Add"))
{
	if(custDtl!=null)
	{
		for(int i=0;i<custDtl.size();++i)
		{	
			custDtls=(CustContactDtl)custDtl.get(i);
%>
			custAddrType[<%=i%>]		= "<%=custDtls.getAddrType()%>";	
			custContactPerson[<%=i%>]	= "<%=custDtls.getContactPerson()%>";
			custDesignation[<%=i%>]		= "<%=custDtls.getDesignation()%>";
			custDept[<%=i%>]			= "<%=custDtls.getDept()%>";
			custZipCode[<%=i%>]			= "<%=custDtls.getZipCode()%>";
			custContact[<%=i%>]			= "<%=custDtls.getContact()%>";
			custFax[<%=i%>]				= "<%=custDtls.getFax()%>";
			custEmail[<%=i%>]			= "<%=custDtls.getEmail()%>";
			custContactId[<%=i%>]		= "<%=custDtls.getContactId()%>";
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
function upper(e){
	e.value	= e.value.toUpperCase();	
}

<!--	This function calls 'ETransLOVCountryIds.jsp' to get the CountryIds. -->

function showCountryIds1(row){


	if(number11 == 1){
		var URL 	= 'ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryIdd.value.toUpperCase()+'&custAddMor=CustAddMore&row='+row+'&whereClause=custMultAdd';
}else{
	for(var i=0;i<number11;i++){
		var URL 	= 'ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryIdd[i].value.toUpperCase()+'&custAddMor=CustAddMore&row='+row+'&whereClause=custMultAdd';
}
}

//	var URL 	= 'ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
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

// Added by Gowtham on 21-Feb-2011 for salesPerson LOV
function salesPersonLov()
{	
	var tabArray = 'EMPID';
	var formArray = 'salesPersonCode';
	//var lovWhere  = " where ORG_ID='<%//=sessOrgid%>' and STATUS='ACTIVE'";
	var lovWhere	=	"";
	//alert('<%=request.getContextPath()%>');
	//window.open("qms/ListOfValues.jsp?lovid=REPORTING_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"","lov","top=0,left=0,height=600,width=470,toolbar=0,location=0,directories=0,status=0,menubar=0,scrollbars=1,resizable=0,minimize=0");

	Url="<%=request.getContextPath()%>/qms/ListOfValues.jsp?lovid=REPORTING_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=SALESPERSON&search= where EMPID LIKE '"+document.forms[0].salesPersonCode.value+"~'";
	var Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	var Options	='width=800,height=750,resizable=yes';
	var Features=Bars+','+Options;
	var Win=open(Url,'Doc',Features);
		
 }

  // Added by Gowtham on 21-Feb-2011 for salesPerson LOV
function showCurrencyLOV(){
		var Url      = 'ETCLOVCurrencyIds.jsp?fromWhat=two&searchString='+document.forms[0].currencyId.value.toUpperCase()+'&Query=WithOut'
		var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
		var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
		var Features =  Bars+''+Options;
		var Win      =  open(Url,'Doc',Features);
}

function placeFocus(){
		<% 
			if(operation.equalsIgnoreCase("Add")){	
		%>	//document.forms[0].customerId.focus();
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
function checkEmail(input,label)
{
	var str = input.value;
	var i=0,j=-1;
	var str1;
	var flag=false;
	while(1)
	{
		if(input.value.length==0)
		{
			alert("Please Enter the "+label);	
			input.focus();
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
				input.focus();
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
		input.focus();
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
//&& document.forms[0].elements[i].name != "opEmailId"
function Mandatory(){

			<%
					if(operation.equals("Add") || operation.equals("Modify")){
			%>	
						if(document.forms.length > 0){
							for( i=0;i<document.forms[0].elements.length;i++){
								if((document.forms[0].elements[i].type=="text") || (document.forms[0].elements[i].type=="textarea")){
									if(	document.forms[0].elements[i].name != "emailId" && document.forms[0].elements[i].name != "opEmailId" && document.forms[0].elements[i].name != "Email" && document.forms[0].elements[i].name!="typeOfCustomer" )
											document.forms[0].elements[i].value=document.forms[0].elements[i].value.toUpperCase();
									 }
									if(document.forms[0].elements[i].type=="checkbox"){
										if(document.forms[0].elements[i].name!="creditFlag"){
											if(document.forms[0].elements[i].checked==true){
												if(document.forms[0].opEmailId.value=="" || document.forms[0].opEmailId.value==" "){
																											alert("Please Enter Primary EmailId");
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

						
						//return false;
						
						if(document.forms[0].opEmailId.value.length > 200 )
						{
							alert("Operation EmailId should be less than 200 characters.");
							document.forms[0].opEmailId.focus();
							return false;
						} 
						/*if(document.forms[0].emailId.value.length > 200 )
						{
							alert("EmailId should be less than 200 characters.");
							document.forms[0].emailId.focus();
							return false;
						}*/
						
						if(document.getElementsByName("contactPerson")[0].value.length==0 )
						{
							alert("Please enter Contact Person.");
							document.getElementsByName("contactPerson")[0].focus();
							return false;
						}
						var flag =	true;
						//alert(document.getElementsByName("contactPerson").length)
						for(var i=0;i<document.getElementsByName("contactPerson").length;i++)
						{
							if(document.getElementsByName("contactPerson")[i].value.length!=0)
							{
								if(document.getElementsByName("conFax")[i].value.length==0 && document.getElementsByName("Email")[i].value.length==0)
								{
									alert("Please enter Fax or Email Id at Row "+(i+1));
									flag =	false;
									document.getElementsByName("conFax")[i].focus();
								}
								else
								{
									if(document.getElementsByName("Email")[i].value.length!=0)
									{
										flag =	false;
										flag = checkEmail(document.getElementsByName("Email")[i],"Contact Person Email Id at Row "+(i+1));
									}
								}
							}
							if(!flag)
								return false;
						}
						return checkEmail(document.forms[0].opEmailId,"Operation Email Id");
						document.forms[0].submit.disabled='true';
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
function removeRow22(row)
{
		number11-- 
		field = parseInt(row); 
		createCusrAddAddrMore(number11,field);
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

<!-- End of The  old script 	-->

function checkFunctionAndExecute(){


		if(Mandatory()){
			cust.style.visibility='hidden';
			cust2.style.visibility='hidden';
			createCusrAddAddrMore(number11);

<%
		if(!operation.equalsIgnoreCase("View")){
%>
			document.forms[0].addressLine1d.focus();
<%
		}//except view this focus is executed
%>

		}//if(Mandatory()


}



// Default 


var number11=1

function createCusrAddAddrMored(number1, row){

			if(number1<=0)number1=1;
				var number = parseInt(number1)

				data = "";
				inter = "'";

					data += "<table border='0' width='800' cellpadding='4' cellspacing='1' bgcolor='#ffffff'>"
					+" <tr valign='top' class='formlabel'>"    	  
					+" <td colspan='3'><table width='760' border='0' ><tr class='formlabel'><td><%=label%>Customer Registration - <%=operation%> Module</td><td align=right><%=loginbean.generateUniqueId("ETCustomerRegistrationAdd.jsp",operation)%></td></tr></table></td>"
					+"</tr>"
					+"<tr valign='top' class='formdata'><td colspan='3'>&nbsp;</td></tr>"
					+"<tr valign='top' class='formdata'>"
//@@ Commented by subrahmanyam for 167669 on 27/04/09
					/*
					+"<td  width='50%'>TerminalId:<font color='#FF0000'><sup>*</sup></font><br>"
					+"<input type='text' class='text' name='terminalId' value = '<%=terminalId%>'  size='16' maxlength='16' readOnly>"
					+"</td>"
					*/
//@@Added by subrahmanyam for 167669 on 27/04/09
					<%if(registrationLevel.equalsIgnoreCase("T") && "Add".equalsIgnoreCase(operation)){
						terminalList=customerRemote.getTerminalList(terminalId);%>
					+"<td  width='50%'>TerminalId:<font color='#FF0000'><sup>*</sup></font><br>"
					+"<select size='1' name='terminalId' class='select' >"
					
					<%for(int i=0;i<terminalList.size();i++)
					  {
						if(terminalId.equalsIgnoreCase((String)terminalList.get(i))){%>
							+"<option value='<%=terminalList.get(i)%>' selected><%=terminalList.get(i)%></option>"
						<%}else{%>
							+"<option value='<%=terminalList.get(i)%>'><%=terminalList.get(i)%></option>"
					<%}}%>
					+"</select></td>"	
						<%}else{%>
							+"<td  width='50%'>TerminalId:<font color='#FF0000'><sup>*</sup></font><br>"
					+"<input type='text' class='text' name='terminalId' value = '<%=terminalId%>'  size='16' maxlength='16' readOnly>"
					+"</td>"
						<%}%>
//@@ Ended by subrahmanyam for 167669 on 27/04/09

<% 
				if(registrationLevel.equalsIgnoreCase("T"))//terminal level customer
				{
%>
					+"<td colspan='3' width='50%'>Abbreviated Name:<font color='#FF0000'>*</font><br>"
					+"<input type='text' class='text' name='abbrName' value = '<%=abbrName%>'  <%=abbrNameReadOnly%>   size='6' maxlength='4' onKeyPress='return getKeyCode()' onBlur = 'stringFilter(abbrName);'>"
					+"<input type='hidden' name='customerId' value = '<%=customerId%>' > "            			

<%	
				}else // Corporate level customer
				{
%>
					+ "<td colspan='3'> <%=label%> Customer Id :<font color='#FF0000'>*</font><br>"
					+"<input type='text' class='text' name='customerId' value = '<%=customerId%>' <%=customerIdReadOnly%>   size='16' maxlength='16' onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(customerId);' >"
					+"<input type='hidden' name='abbrName' value = '<%=abbrName%>' >"

<%
				}
%>			

					+"</td></tr>"
					+"<tr valign='top' class='formdata'>"
					+"<td  >Company Name:<font color='#FF0000'><sup>*</sup></font><br>"
					+"<input type='text' class='text' name='companyName' value = '<%=companyName%>' <%=customerIdReadOnly%>  size='50' maxlength='50'  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(companyName);' >"
					+"</td>"
					+"<td>Division:<br><input type='text' class='text' name='division' value='<%=devision!=null?devision:""%>' size='20' maxlength='25' <%=customerIdReadOnly%> ></td>"
					+"<td >"
<% 
				if(registrationLevel.equalsIgnoreCase("T"))//terminal level customer
				{
%>
					+" Corporate Customer Id:<br>"  		
					+" <input type='text' class='text' name='corpCustomerId'  size='16' maxlength='16' value='<%=corpCustomerId!=null ? corpCustomerId : "" %>' onKeyPress='return checkSpecialKeyCode()' <%=readOnly%>  onBlur = 'stringFilter(corpCustomerId);' >"
					+" <input type='hidden' name='typeOfCustomer' value='Customer'>"
<%
				if(operation.equalsIgnoreCase("Add")||operation.equalsIgnoreCase("Modify")){
%>

					+"<input type='button' value='...' name='probutton' onClick='getCustomerIds()' class='input'>"
<%
				}
				}else // Corporate level customer
				{
%>
					+"<input type='hidden' name='typeOfCustomer' value='Corporate'>"
					+"<input type='hidden' name='corpCustomerId' >"
<%
				}
%>
					+"</td>"
					+"</tr>"
					+"<tr valign='top' class='formdata'>"
					<% //@@ Added by Srivegi on 20040224 (AES-SED) %>
					<% //@@ Murali Modified on 20050422 Regarding UI %>
		+"<td  >Contact Person(First Name/Last Name):<br>"
		+"<input type='text' class='text' name='contactPerFistName' value='<%=contactName != null ? contactName : "" %>' size='10' maxlength='30'  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(contactPerFistName)' <%=readOnly%>>"
					 +" <input type='text' class='text' name='contactPerLastName' value='<%=contactPersonLastName != null ? contactPersonLastName : "" %>' size='10' maxlength='30'  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(contactPerLastName)' <%=readOnly%>></td>"
					 +"<td colspan='2' >SalesPerson:<br><input type='text' class='text' name='salesPersonCode' value= '<%=salesPersonId%>' <%=customerIdReadOnly%>  onKeyPress='return checkSpecialKeyCode()' onBlur='upper(this);chrnum(this)'> <input type='button'class='input' value='...' name='salesPerLOV' onClick = 'salesPersonLov()'> <br> </td></tr>" // Added by Gowtham for SalesPerson LOV
					 +"<tr valign='top' class='formdata'>"
					  //+"<td >EIN/SSN # :<br>"
					 +"<td >Registration No:<br>"
					 +"<input type='text' class='text' name='einssn' value='<%=einssn%>' size='12' <%=readOnly%> onBlur='stringFilter(einssn);chrnum(einssn);' onKeyPress='' maxlength='12'></td>"
						+"<td colspan='2'></td></tr>"
					 <% //@@ Murali Modified on 20050422 Regarding UI %>
                    <%//@@ 20040224 (AES-SED)%>
					/*+"<td  >Contact Person:<br>"
					+"<input type='text' class='text' name='contactName'  value = '<%=contactName != null ? contactName : "" %>'  size='35' maxlength='30'  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(contactName)' <%=readOnly%> >"
					+"</td>"*/
					<% //@@ Added by Srivegi on 20040224 (AES-SED) %>	
	
    +"<tr valign='top' class='formdata'>"
					  +"<td >Customer :<br>"
          +"<select name='knownShipper' class='select'>"
		  +"<option value='U'  >Known</option>"
		  +"<option value='K'  >Unknown</option></select></td>"
   <%//@@ 20040224 (AES-SED)%>
					+"<td colspan='2'>Designation:<br>"
					+"<input type='text' class='text' name='designation' value= '<%=designation != null ? designation : "" %>'    size='30' maxlength ='30'    onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(designation)' <%=readOnly%> >"
					+"</td>"
					+"</tr>"
					+"<tr valign='top' class='formdata'>"
					+"<td > Payment&nbsp;Terms:<br>"
					+"<input type='text' class='text' name='paymentTerms' size='20' maxlength='25' value='<%=paymentTerms!=null?paymentTerms:""%>' <%=readOnly%> ></td>"
					+"<td >Credit Days:<br>"
					+"<input type='text' class='text' name='creditdays'  size='5'   maxlength='4' value='<%=creditdays%>' <%=readOnly%>    onkeyPress='return getDotNumberCode(this)' >"
					+"<input type='hidden' name='scode' size='16' maxlength='16'  value=' '>"
					+"</td>"
					+"<td >Credit Limit :<br>"
					+"<input type='text' class='text' name='creditlimit' size='13' onBlur='decCheck(this)' onkeyPress='return getDotNumberCode(this)' maxlength='13' value='<%=creditlimit%>' <%=readOnly%> >"
					+"&nbsp;<input type='text' class='text' name='currencyId' size='5' maxlength='3' onBlur = 'stringFilter(this)' onKeyPress='return getKeyCode()' value='<%=currency!=null?currency:"" %>' <%=readOnly%>>"
<%
			if(operation.equalsIgnoreCase("Add") || operation.equalsIgnoreCase("Modify"))
			{
%>
					+"<!--&nbsp;<input type='button' value='...' name='creditcurrency' onClick='showCurrencyLOV()' class='input'>-->"
<%
			}
%>
					+"</td>"
					+"</tr>"
					+"</table>"
					+"<table border='0' width='800' cellpadding='4' cellspacing='1' bgcolor='#ffffff'>"
					+"<tr valign='top' class='formdata'> "
					<% //@@ Murali Modified on 20050422 Regarding UI %>
					// +"<td colspan='2' rowspan='2' width='50%'>Address:<font color='#FF0000'>*</font><br>"
					+"<td rowspan='2' width='50%'>Address:<font color='#FF0000'>*</font><br>"
					<% //@@ Murali Modified on 20050422 Regarding UI %>
					+"<input type='text' class='text' name='addressLine1' value = \"<%=addressLine1 != null ? addressLine1 :"" %>\" size='50' maxlength='75'  <%=readOnly%>    onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(addressLine1);' ><BR>"
					+"<input type='text' class='text' name='addressLine2' value = \"<%=addressLine2 != null ? addressLine2 :"" %>\" size='50' maxlength='75'  <%=readOnly%>  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(addressLine2);' ><BR>"
					+"<input type='text' class='text' name='addressLine3' value=\"<%=addressLine3!=null?addressLine3:""%>\" size='50' maxlength='75' onKeyPress='return checkSpecialKeyCode()' onBlur='stringFilter(addressLine3)'  <%=readOnly%>>"
					//@@Modified by Kameswari
					/*
					"<input type='text' class='text' name='addressLine1' value = '<%=addressLine1 != null ? addressLine1 :"" %>' size='50' maxlength='75'  <%=readOnly%>    onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(addressLine1);' ><BR>"
					+"<input type='text' class='text' name='addressLine2' value = '<%=addressLine2 != null ? addressLine2 :"" %>' size='50' maxlength='75'  <%=readOnly%>  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(addressLine2);' ><BR>"
					+"<input type='text' class='text' name='addressLine3' value='<%=addressLine3!=null?addressLine3:""%>' size='50' maxlength='75' onKeyPress='return checkSpecialKeyCode()' onBlur='stringFilter(addressLine3)'  <%=readOnly%>>"*/
					+"</td>"
					+"<td colspan='4' >Zip or Postal Code:<br>"
					+"<input type='text' class='text' name='zipCode'  value = '<%=zipCode != null ? zipCode :"" %>' size='20' maxlength='15'  <%=readOnly%>   onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(zipCode)' >"
					+"</td>"
					+"</tr>"
					+"<tr class='formdata'> "
					+"<td colspan='4'>Contact No:<br>"
					+"<input type='text' class='text' name='phoneNo' value = '<%=phoneNo != null ? phoneNo :"" %>' size='20' maxlength='15'  <%=readOnly%>  onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(phoneNo)' >"
					+"</td>"
					+"</tr>"
					+"<tr valign='top' class='formdata'> "
					<% //@@ Murali Modified on 20050422 Regarding UI %>
					//+"<td colspan='2'>City:<font color='#FF0000'>*</font><br>"
					+"<td >City:<font color='#FF0000'>*</font><br>"
					<% //@@ Murali Modified on 20050422 Regarding UI %>
					+"<input type='text' class='text' name='city' value = '<%=city != null ? city :"" %>' size='30' maxlength='30'  <%=readOnly%> onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(city);' >"
					+"</td>"
					+"<td colspan='4'>Fax No:<br>"
					+"<input type='text' class='text' name='fax' value = '<%=fax != null ? fax :"" %>' size='20' maxlength='15'  <%=readOnly%> onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(fax)' >"
					+"</td>"
					+"</tr>"
					+"<tr valign='top' class='formdata'> "
					<% //@@ Murali Modified on 20050422 Regarding UI %>
					//+"<td colspan='2' >State or Province:<br>"
					+"<td >State or Province:<br>"
					<% //@@ Murali Modified on 20050422 Regarding UI %>
					+"<input type='text' class='text' name='state' value = '<%=state != null ? state :"" %>' size='30' maxlength='30'  <%=readOnly%>    onKeyPress='return checkSpecialKeyCode()' onBlur = 'stringFilter(state);' >"
					+"</td>"
					+"<td colspan='4'> Country Id:<font color='#FF0000'>*</font><br>"
					+"<input type='text' class='text' name='countryId' value = '<%=countryId != null ? countryId :"" %>' size='4' maxlength='2'  <%=readOnly%>  onKeyPress='return getKeyCode()' 	  onBlur='stringFilter(countryId)' >"
          
<%           
			if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Add") )
			{
%> 
					+"&nbsp;<input type='button' class='input' value='...' name='lov_CountryId'  onClick='showCountryIds()' >"
<%
			}
%> 
          +"</td>"
					+"</tr>"
					+"<tr valign='top' class='formdata'> "
					<% //@@ Murali Modified on 20050422 Regarding UI %>
					//+"<td td width='50%' colspan='2' >Operations EmailId:<font color='red'>*</font><br>"
					+"<td >Primary EmailId:<font color='red'>*</font><br>"
					<% //@@ Murali Modified on 20050422 Regarding UI %>
					+"(Note:This is not available during quote creation.<br>Contact person details to be set up separately)<br><textarea  class='select' cols='35' rows='4' name='opEmailId' onKeyPress='return checkEmailSpecialKeyCode()'  <%=readOnly%> onBlur='' ><%=opEmailId != null ? opEmailId :"" %></textarea>"
					+"</td>"
					+"<td colspan='4'>"
					+"<input type='hidden' name='emailId' value=''>"
					+"</td>"
					/*+"<td colspan='4'>EmailId: <br>"
					+"<textarea class='select' cols='35' rows='4' name='emailId' onKeyPress='return checkEmailSpecialKeyCode()'  <%=readOnly%> ><%=emailId != null ? emailId :"" %></textarea>"
					+"</td>"*/
					+"</tr>"
<%   
		if(customerType.equalsIgnoreCase("U"))
		{
			if(operation.equalsIgnoreCase("Upgrade"))
			{
      %> 
					+"<tr valign='top' class='formdata'> "
					+"<td > "
					+"<input type='checkbox' name='creditFlag' value='N' onClick='setFlag(this)' >"
					+"Apply Credit Limit </td>"
					+"<td  colspan='4'>Notes<br>"
					+"<textarea name='notes' class='select' cols='35' rows='4' onKeyPress='return checkSpecialKeyCode()' <%=readOnly%>> <%=notes !=null ? notes : "" %></textarea>"
					+"</td>"
					+"</tr>"
					+"<tr valign='top' class='formdata'> "
					+"<td >Mail Notifications:<br>"
					+"<input type='checkbox' name='masterCloseFlag' value='N'  onClick='setFlag(this)' >"
					+"Master Close "
					+"<input type='checkbox' name='bbFlag' value='N'  onClick='setFlag(this)'>"
					+"BreakBulk<br><br>"
                    +"Prefered Service Level<br><input type='text' class='text' size=8 name=serviceLevelId onKeyPress='return getKeyCode()'>"
					+"&nbsp;<input class='input' type=button name=btnServiceLov value='...' onclick='showServiceLevelLOV()'>"
					+"</td></tr>"
<%
			}
			else if(!operation.equalsIgnoreCase("Upgrade"))
			{
%> 
					+"<tr valign='top' class='formdata'> "
					+"<td colspan='4'> "
					+"<input type='hidden' name='creditFlag' value='N' >"
					+"<input type='hidden' name='masterCloseFlag' value='N'>"
					+"<input type='hidden' name='bbFlag' value='N'>"
					+"Notes<br>"
					+"<textarea name='notes' class='select' onKeyPress='return checkSpecialKeyCode()' cols='35' rows='4' <%=readOnly%>><%=notes !=null ? notes : "" %></textarea>"
					+"</td>"
					+"</tr>"
<%
			}
			}	
			else if(customerType.equalsIgnoreCase("R")||operation.equalsIgnoreCase("Delete"))
			{
%> 
					+"<tr valign='top' class='formdata'> "
					+"<td > "
					+"<input type='checkbox' name='creditFlag' value='N' onClick='setFlag(this)' <%=disabled%> >"
					+"Apply Credit Limit </td>"
					+"<td rowspan='2' colspan='4'>Notes<br>"
					+"<textarea name='notes' class='select' cols='35' onKeyPress='return checkSpecialKeyCode()' rows='4' <%=readOnly%>><%=notes !=null ? notes : "" %></textarea>"
					+"</td>"
					+"</tr>"
					+"<tr valign='top' class='formdata'> "
					+"<td >Mail Notifications:<br>"
					+"<input type='checkbox' name='masterCloseFlag' value='N'  onClick='setFlag(this)' <%=disabled%>>"
					+"Master Close "
					+"<input type='checkbox' name='bbFlag' value='N' <%=disabled%> onClick='setFlag(this)'>"
					+"BreakBulk<br><br>"
                    // Changed By Senthil prabhu 1783 ON 20052005
                   
				+"</td></tr></table>"
					+"<table border='0' width='100%' bgcolor='#FFFFFF'  id='customerreg'  idcounter='1'" 
			        +"defaultElement='Type' xmlTagName='customerreg' functionName='defaultFunction'" +"deleteFunctionName='defaultDeleteFunction' onBeforeDelete='' onBeforeCreate='' maxRows=25>"
					+"<tr class='formheader'>"
					+"<td>&nbsp;</td>"
					+"<td>AddressType</td><td>Contact Person</td><td>Designation</td><td>Dept</td>"
					+"<td>ZipCode</td><td>Contact</td><td>Fax</td><td>E-Mail</td><td>&nbsp;</td>"
					+"</table>"
<%
			}
%> 
					+"<table border='0' width='100%' bgcolor='#FFFFFF'>"
					+"<tr bgcolor='#FFFFFF'>"
					+"<td class='denotes'><font color='#FF0000'>*</font>Denotes Mandatory <br>"
					// Changed By Senthil prabhu for  1783 ON 20052005
					+" Note: Please separate each EmailId by a semi colon (;) </font></font>"
					+"</td>"
					+"<td align='right'>"
<%
		if(operation.equalsIgnoreCase("Add")){		
%>
						+"<input type='button' name='add_more_Address' value='<%=operation%> Address' class='input' onClick='checkFunctionAndExecute()'> &nbsp;&nbsp;&nbsp;"

<%
		}
%>


<%
			if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Upgrade") ){
				if(customerType.equalsIgnoreCase("R")){
%>	
					+"<input type='button' name='add_more_Address' value='<%=operation%> Address' class='input' onClick='checkFunctionAndExecute()'> &nbsp;&nbsp;&nbsp;"
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
						+"<input type='button' name='add_more_Address' value='<%=operation%> Address' class='input' onClick='checkFunctionAndExecute()'> &nbsp;&nbsp;&nbsp;"

<%
				}
			}
%>

					+"<input type='submit' value='<%=value%>' name='submit' class='input'> &nbsp;&nbsp;&nbsp;"	
				// Added by Suneetha for IssueID -SPETI-3842, 3843 on 7th Feb 05
<%
				if(operation.equalsIgnoreCase("modify"))
				{
%>
					+"</td><td><input type='reset' value='Reset' name='reset' class='input'>"
				// @@ 7th Feb 05
					+"</td>"
<%
				}
%>
					+"</tr>"
					+"</table>"

			if (document.layers)
			{
				document.layers.cust.document.write(data);
				document.layers.cust.document.close();
			}
			else
			{
				if (document.all)
				{
					cust.innerHTML = data;
				}
			}

}






//end of default

//NeW Code Add More Address
function checkTheRowsRemove(input){
	if(number11<25)
	{

		removeRow22(input);
		k3=0;
		for(j=0;j<document.forms[0].elements.length;j++)
		{
			if(document.forms[0].elements[j].name == "add_Flagd")
			{
				if(k3 < number11-1 )

						document.forms[0].elements[j].value = add_Flagd[k3++]
				}
		}


	}else{
		window.alert("Only up to 25 entries.");		
	}
}
function checkTheRowsRemove(input)
{
	if(number11<25)
	{

		removeRow22(input);
		k3=0;
		for(j=0;j<document.forms[0].elements.length;j++)
		{
			if(document.forms[0].elements[j].name == "add_Flagd")
			{
				if(k3 < number11-1 )

					document.forms[0].elements[j].value = add_Flagd[k3++]
			}
		}


	}else{
		window.alert("Only up to 25 entries.");		
	}
}


function checkTheRowsSubmit(){			

<%
	if(operation.equals("Modify")){
%>
		if( number11==1 && document.forms[0].del_Flagd.value == "Y"){
			return true;
		}else if( number11 > 1 && document.forms[0].del_Flagd[number11-1].value == "Y"){
				return true;
		}
<%	
	}
%>
		if(number11==1 && ( document.forms[0].countryIdd.value.length==0)){
				alert("Please enter CountryId");
				document.forms[0].countryIdd.focus();
				return false;
		}else if(number11 > 1 && document.forms[0].countryIdd[number11-1].value.length==0){
				alert("Please Enter CountryId ")
				document.forms[0].countryIdd[number11-1].focus()
				return false;
		}else{

				loadValues();			
				var flag=0;
				if(number11 == 1 ){	
						document.forms[0].countryIdd.value=document.forms[0].countryIdd.value.toUpperCase();
						if(document.forms[0].countryIdd.value.length > 0 )
						{
							for( i=0; i<arrayCountryIds.length; i++ )
							{

								if( document.forms[0].countryIdd.value.toUpperCase()==arrayCountryIds[i])
								{
									flag = 1;
									break;
								}
							}
							if(flag==0)
							{
								alert("Please enter correct Country.");
								document.forms[0].countryIdd.value = "";
								document.forms[0].countryIdd.focus();
							}
						}//IF

						if(flag==0)
						return false;
						}else{

								document.forms[0].countryIdd[number11-1].value=document.forms[0].countryIdd[number11-1].value.toUpperCase();
							if(document.forms[0].countryIdd[number11-1].value.length > 0 )
							{
								for( i=0; i<arrayCountryIds.length; i++ )
								{

									if( document.forms[0].countryIdd[number11-1].value.toUpperCase()==arrayCountryIds[i])
									{
											flag = 1;
											break;
									}
								}
								if(flag==0)
								{
										alert("Please enter correct Country.");
										document.forms[0].countryIdd[number11-1].value = "";
										document.forms[0].countryIdd[number11-1].focus();
								}
							}

							if(flag==0)
							return false;


					}//ELSE
		}//nd of else condition



		if(number11==1 && ( document.forms[0].addressLine1d.value.length==0) )
		{

					alert("Please Enter Address")
					document.forms[0].addressLine1d.focus()
					return false;
		}else if(number11==1 && ( document.forms[0].cityd.value.length==0) ){
					alert("Please Enter City")
					document.forms[0].cityd.focus()		
					return false;
		}else if(number11==1 && ( document.forms[0].countryIdd.value.length==0) ){
				alert("Please Enter CountryId")
				document.forms[0].countryIdd.focus()		
				return false;
		}else if(number11 > 1 && (document.forms[0].addressLine1d[number11-1].value.length==0) ){
				alert("Please Enter Address ")
				document.forms[0].addressLine1d[number11-1].focus()
				return false;
		}else if(number11 > 1 && document.forms[0].cityd[number11-1].value.length==0){
				alert("Please Enter City ")
				document.forms[0].cityd[number11-1].focus()
				return false;
		}else if(number11 > 1 && document.forms[0].countryIdd[number11-1].value.length==0){
				alert("Please Enter CountryId ")
				document.forms[0].countryIdd[number11-1].focus()
				return false;
		}else{
				return true;
		}	

}
function checkTheRows(){

		if(number11<25)
		{



				if(checkTheRowsSubmit()){
					createCusrAddAddrMore(++number11,-1)
					k3=0;
					for(j=0;j<document.forms[0].elements.length;j++){
						if(document.forms[0].elements[j].name == "add_Flagd"){
							if(k3 < number11-1 )
								document.forms[0].elements[j].value = add_Flagd[k3++]
						}//IF
					}//FOR
					k13=0;
		
					for(j=0;j<document.forms[0].elements.length;j++){
						if(document.forms[0].elements[j].name == "del_Flagd"){
							if(k13 < number11-1 )
								document.forms[0].elements[j].value = del_Flagd[k13++]
						}//IF
					}//FOR
				}//IF
		}else{
				window.alert("Only up to 25 entries.");		
		}
}
		
		
		
		var no_Of_rows=1;

		k1 = 0
		k2 = 0
		k3 = 0
		k4 = 0
		k5 = 0
		k6 = 0
		k7 = 0
		k8 = 0
		k9 = 0
		k10 = 0
		k11 = 0
		k12 = 0
		k13 = 0


		contactPersond		=	new Array(25);
		designationd		=	new Array(25);
		add_Flagd			=	new Array(25);
		addressLine1d		=	new Array(25);
		addressLine2d		=	new Array(25);
		addressLine3d		=	new Array(25);
		cityd				=	new Array(25);
		zipOrPostalCoded	=	new	Array(25);
		faxNod				=	new Array(25);
		stated				=	new Array(25);
		countryIdd			=	new Array(25);
		contactNod			=	new Array(25);
		emailIdd			=	new Array(25);
		del_Flagd			=	new Array(25);


		contactPersondm		=	new Array(25);
		designationdm		=	new Array(25);
		add_Flagdm			=	new Array(25);
		addressLine1dm		=	new Array(25);
		addressLine2dm		=	new Array(25);
		citydm				=	new Array(25);
		zipOrPostalCodedm	=	new	Array(25);
		faxNodm				=	new Array(25);
		statedm				=	new Array(25);
		countryIddm			=	new Array(25);
		contactNodm			=	new Array(25);
		emailIddm			=	new Array(25);
		del_Flagdm			=	new Array(25);


		for(i=0;(i<addressLine1d.length && i<cityd.length && i<countryIdd.length);i++)
		{
				contactPersond[i]='';
				designationd[i]='';
				addressLine1d[i]='';
				addressLine2d[i]='';
				addressLine3d[i]='';
				cityd[i]='';
				zipOrPostalCoded[i]='';
				faxNod[i]='';
				stated[i]='';
				countryIdd[i]='';
				contactNod[i]='';
				emailIdd[i]='';
		}

var modifyFlag	= true;

function createCusrAddAddrMore(number1, row){
			if(number1<=0)number1=1;
				var number = parseInt(number1)
				var k1=0
				var k2=0
				var k3=0
				var k4=0
				var k5=0
				var k6=0
				var k7=0
				var k8=0
				var k9=0
				var k10=0
				var k11=0
				var k12=0
				var k13=0

				for(j=0;j<document.forms[0].elements.length;j++){
					if(document.forms[0].elements[j].name == "contactPersond"){
						contactPersond[k1] = document.forms[0].elements[j].value
						k1++
					}
					if(document.forms[0].elements[j].name == "designationd"){
						designationd[k2] = document.forms[0].elements[j].value
						k2++
					}
					if(document.forms[0].elements[j].name == "add_Flagd"){

						add_Flagd[k3] = document.forms[0].elements[j].value
						k3++
					}
					if(document.forms[0].elements[j].name == "addressLine1d"){
						addressLine1d[k4] = document.forms[0].elements[j].value
						k4++
					}
					if(document.forms[0].elements[j].name == "addressLine2d"){
						addressLine2d[k5] = document.forms[0].elements[j].value
						k5++
					}
					if(document.forms[0].elements[j].name == "addressLine3d"){
						addressLine3d[k6] = document.forms[0].elements[j].value
						k6++
					}	
					if(document.forms[0].elements[j].name == "cityd"){
						cityd[k7] = document.forms[0].elements[j].value
						k7++
					}
					if(document.forms[0].elements[j].name == "zipOrPostalCoded"){
						zipOrPostalCoded[k8] = document.forms[0].elements[j].value
						k8++
					}

					if(document.forms[0].elements[j].name == "stated"){
						stated[k9] = document.forms[0].elements[j].value
						k9++
					}
					if(document.forms[0].elements[j].name == "countryIdd"){
						countryIdd[k10] = document.forms[0].elements[j].value
						k10++
					}
					if(document.forms[0].elements[j].name == "contactNod"){
						contactNod[k11] = document.forms[0].elements[j].value
						k11++
					}	
					if(document.forms[0].elements[j].name == "emailIdd"){
						emailIdd[k12] = document.forms[0].elements[j].value
						k12++
					}
					if(document.forms[0].elements[j].name == "del_Flagd"){

						del_Flagd[k13] = document.forms[0].elements[j].value	
						k13++
					}


			}

			//JS  Modify

				var l1=0
				var l2=0
				var l3=0
				var l4=0
				var l5=0
				var l6=0
				var l7=0
				var l8=0
				var l9=0
				var l10=0
				var l11=0
				var l12=0
				var l13=0

			<%
				
					if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View")){

						if(listOfValues!=null && !listOfValues.isEmpty()){ 	
							//customerModel		=	(CustomerModel)listOfValues.get(0);
							custAddList			=	(ArrayList)listOfValues.get(2);
							custMoreAddList		=	(ArrayList)listOfValues.get(3);
						 }

						for(int i=0;((i<custAddList.size())&&(i<custMoreAddList.size()));i++){

							CustomerModel	tempCustMObj	=	(CustomerModel)custAddList.get(i);
							Address			tempAddObj		=	(Address)custMoreAddList.get(i);
				%>
				
				if(modifyFlag){
						
						contactPersondm[<%=i%>]		= "<%=tempCustMObj.getContactName() != null ? tempCustMObj.getContactName() :"" %>"
						designationdm[<%=i%>]		= "<%=tempCustMObj.getDesignation() != null ? tempCustMObj.getDesignation() :"" %>"
						add_Flagdm[<%=i%>]			= "<%=tempCustMObj.getAddressType()%>"
						//@@Modified by Kameswari
						/* addressLine1dm[<%=i%>]		= "<%=tempAddObj.getAddressLine1() != null ? tempAddObj.getAddressLine1() :"" %>"
						addressLine2dm[<%=i%>]		= "<%=tempAddObj.getAddressLine2() != null ? tempAddObj.getAddressLine2() :"" %>" */
						/*addressLine1dm[<%=i%>]		= "<%=tempAddObj.getAddressLine1() != null ? tempAddObj.getAddressLine1() :"" %>\"
						addressLine2dm[<%=i%>]		= "\<%=tempAddObj.getAddressLine2() != null ? tempAddObj.getAddressLine2() :"" %>\"*/
						citydm[<%=i%>]				= "<%=tempAddObj.getCity() != null ? tempAddObj.getCity() :"" %>"
						zipOrPostalCodedm[<%=i%>]	= "<%=tempAddObj.getZipCode() != null ? tempAddObj.getZipCode() :"" %>"
						statedm[<%=i%>]				= "<%=tempAddObj.getState() != null ? tempAddObj.getState() :"" %>"
						countryIddm[<%=i%>]			= "<%=tempAddObj.getCountryId() != null ? tempAddObj.getCountryId() :"" %>"
						contactNodm[<%=i%>]			= "<%=tempAddObj.getPhoneNo() != null ? tempAddObj.getPhoneNo() :"" %>"
						emailIddm[<%=i%>]			= "<%=tempAddObj.getEmailId() != null ? tempAddObj.getEmailId() :"" %>"
						del_Flagdm[<%=i%>]			= "<%=tempCustMObj.getDelFlag()%>"

				
			
				}else{
					for(j=0;j<document.forms[0].elements.length;j++){
						if(document.forms[0].elements[j].name == "contactPersondm"){
							contactPersondm[l1] = document.forms[0].elements[j].value
							l1++
						}
						if(document.forms[0].elements[j].name == "designationdm"){
							designationdm[l2] = document.forms[0].elements[j].value
							l2++
						}
						if(document.forms[0].elements[j].name == "add_Flagdm"){

							add_Flagdm[l3] = document.forms[0].elements[j].value
							l3++
						}
						if(document.forms[0].elements[j].name == "addressLine1dm"){
							addressLine1dm[l4] = document.forms[0].elements[j].value
							l4++
						}
						if(document.forms[0].elements[j].name == "addressLine2dm"){
							addressLine2dm[l5] = document.forms[0].elements[j].value
							l5++
						}
					    if(document.forms[0].elements[j].name == "citydm"){
							citydm[l6] = document.forms[0].elements[j].value
							l7++
						}
						if(document.forms[0].elements[j].name == "zipOrPostalCodedm"){
							zipOrPostalCodedm[l7] = document.forms[0].elements[j].value
							l8++
						}

						if(document.forms[0].elements[j].name == "statedm"){
							statedm[l9] = document.forms[0].elements[j].value
							l9++
						}
						if(document.forms[0].elements[j].name == "countryIddm"){
							countryIddm[l10] = document.forms[0].elements[j].value
							l10++
						}
						if(document.forms[0].elements[j].name == "contactNodm"){
							contactNodm[l11] = document.forms[0].elements[j].value
							l11++
						}	
						if(document.forms[0].elements[j].name == "emailIddm"){
							emailIddm[l12] = document.forms[0].elements[j].value
							l12++
						}
						if(document.forms[0].elements[j].name == "del_Flagdm"){

							del_Flagdm[l13] = document.forms[0].elements[j].value	
							l13++
						}


				}
 
 
		}//else

<%
			}//for loop
%>
	modifyFlag = false;
<%
		
		}//main if
%>

//JS
			if(row !=-1){
				for(var j=0; j < add_Flagd.length -1; j++){
					if( j  >= row){
						contactPersond[j]		= contactPersond[j+1];
						designationd[j]			= designationd[j+1];
						add_Flagd[j]			= add_Flagd[j+1];
						addressLine1d[j]		= addressLine1d[j+1];
						addressLine2d[j]		= addressLine2d[j+1];
						addressLine3d[j]		= addressLine3d[j+1];
						cityd[j]				= cityd[j+1];
						zipOrPostalCoded[j]		= zipOrPostalCoded[j+1];
						stated[j]				= stated[j+1];
						countryIdd[j]			= countryIdd[j+1];
						contactNod[j]			= contactNod[j+1];
						emailIdd[j]				= emailIdd[j+1];
						del_Flagd[j]			= del_Flagd[j+1];


					}
				}
			}


				data = "";
				inter = "'";
				data += "<table width='800' border='0' cellspacing='1' cellpadding='4' bgcolor='#ffffff' > "
				+"<tr class='formlabel'> "
				+"<td colspan='7'>Customer Registration <%=operation%> - More Details </td>"
				+"</tr>"
				+"</table>"

				if (number < 26 && number > -1)	{
					data +="<table width='800' bgcolor=FFFFFF  cellpadding=4 cellspacing=1  bgcolor='#ffffff' >"
<%				
//System.out.println(" statusView-->"+statusView);

		//if(statusView){
				if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") ){
					// verifying whether CustomerDetail object is null or not
					//	statusView = false;	
//System.out.println(" after  statusView-->"+statusView);

					if(operation.equalsIgnoreCase("View")){
						readOnlyView = "readOnly";
					}else{
						readOnlyView = "";
					}
				
					if(listOfValues!=null && !listOfValues.isEmpty()){ 	
						//customerModel		=	(CustomerModel)listOfValues.get(0);
						custAddList			=	(ArrayList)listOfValues.get(2);
						custMoreAddList		=	(ArrayList)listOfValues.get(3);
					}


					//	if(custAddList!=null)	
				for(int i=0;((i<custAddList.size())&&(i<custMoreAddList.size()));i++){

						CustomerModel	tempCustMObj	=	(CustomerModel)custAddList.get(i);
						Address			tempAddObj		=	(Address)custMoreAddList.get(i);

%>

						data = data + "<tr  class='formdata'> <td colspan=4 align='left'> ADDRESS </td></tr>"
						data = data + "<tr  class='formdata'>"
						data = data + "<td width='5%'>&nbsp</td>"
						data = data + " <td width='12%'>Contact Person : <br> <input type='text' class='text' maxlength='30' size='20' name='contactPersondm' value=  " +inter + contactPersondm[<%=i%>] + inter  +"  onKeyPress='return checkSpecialKeyCode()' onBlur = 'upper(this)' <%=readOnlyView%> ><input type='hidden' maxlength='20' size='20' name='addressIddm' value= '<%=tempAddObj.getAddressId()%>'   ></td>" 
						data = data + "<td width='12%'>Designation :<br><input type='text' class='text' maxlength='30' size='20' name='designationdm' value=  " +inter + designationdm[<%=i%>] + inter  +"  onKeyPress='return checkSpecialKeyCode()' onBlur = 'upper(this)' <%=readOnlyView%> ></td>"
						data = data +"<td width='12%'>AddressType :<font color='#FF0000'>*</font><br><select size='1' name='add_Flagdm'  class='select'>"


				if(add_Flagdm[<%=i%>] == "P"){		

					data = data +"<option value='P' selected>PickUP Address</option>"
					data = data +"<option value='B' >Billing Address</option>"
					data = data +"<option value='D' >Delivery Address</option></select>"

				}else if(add_Flagdm[<%=i%>] == "B"){

					data = data +"<option value='P' >PickUP Address</option>"
					data = data +"<option value='B' selected>Billing Address</option>"
					data = data +"<option value='D' >Delivery Address</option></select>"

				}else if(add_Flagdm[<%=i%>] == "D"){


					data = data +"<option value='P' >PickUP Address</option>"
					data = data +"<option value='B' >Billing Address</option>"
					data = data +"<option value='D' selected>Delivery Address</option></select>"

				}


					data = data + "&nbsp "		
					data = data +"Delete :<select size='1' name='del_Flagdm'  class='select'>"

				if(del_Flagdm[<%=i%>] == "N"){

							
					data = data +"<option value='Y'>YES</option>"
					data = data +"<option value='N' selected >NO</option>"

				}else{

					data = data +"<option value='Y' >YES</option>"
					data = data +"<option value='N' selected >NO</option>"

				}

				data = data +"</select></td>"
				data = data + "<tr  class='formdata'>"
				data = data + "<td width='5%'>&nbsp</td>"
				//@@Modified by Kameswari
				/*data = data + "<td width='12%'>Address :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=75 size='20' name='addressLine1dm' value=" + inter + addressLine1dm[<%=i%>] + inter + " onKeyPress = 'return checkSpecialKeyCode()'; onBlur = 'upper(this)' <%=readOnlyView%> ><br><input type='text' class='text' maxlength=75 size='20' name='addressLine2dm' value=" + inter + addressLine2dm[<%=i%>] + inter + " onKeyPress = 'return checkSpecialKeyCode()';onBlur = 'upper(this)' <%=readOnlyView%> ></td>"	*/
				data = data + "<td width='12%'>Address :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=75 size='20' name='addressLine1dm' value=\<%=tempAddObj.getAddressLine1() != null ? tempAddObj.getAddressLine1() :"" %>\  onKeyPress = 'return checkSpecialKeyCode()'; onBlur = 'upper(this)' <%=readOnlyView%> ><br><input type='text' class='text' maxlength=75 size='20' name='addressLine2dm' value=\<%=tempAddObj.getAddressLine2() != null ? tempAddObj.getAddressLine2() :"" %>\ onKeyPress = 'return checkSpecialKeyCode()';onBlur = 'upper(this)' <%=readOnlyView%> ></td>"	
				
				data = data + "<td width='12%'>City :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=30 size='20' name='citydm' value=" + inter + citydm[<%=i%>] + inter + " onKeyPress = 'return checkSpecialKeyCode()';onBlur = 'upper(this)' <%=readOnlyView%> ></td>"
				data = data + "<td width='12%'>ZipCode :<input type='text' class='text' maxlength=10 size='5' name='zipOrPostalCodedm'  value=" + inter + zipOrPostalCodedm[<%=i%>] + inter + " onBlur = 'upper(this)' <%=readOnlyView%> ><br>"
				data = data + "CountryId :<font color='#FF0000'>*</font> <input type='text' class='text' maxlength=2 size='5' name='countryIddm'  value=" + inter + countryIddm[<%=i%>] + inter + "  onBlur = 'upper(this)' <%=readOnly%> onKeyPress='return getKeyCode()' readOnly>&nbsp;"
				data = data +"</td>"
				data= data  + "<tr  class='formdata'>"
				+ "<td width='5%'>&nbsp</td>"
				+ "<td width='12%'>State or Province :<br> <input type='text' class='text' maxlength=30 size='20' name='statedm' value=" + inter + statedm[<%=i%>] + inter + "  onBlur = 'upper(this)' <%=readOnlyView%> ></td>"							
				+ "<td width='12%'>Contact No :<br> <input type='text' class='text' maxlength=20 size='20' name='contactNodm' value=" + inter + contactNodm[<%=i%>] + inter + "  onBlur = 'upper(this)' <%=readOnlyView%> ></td>"
				+ "<td width='12%'>Email :<br> <input type='text' class='text' maxlength=250 size='20' name='emailIddm' value=" + inter + emailIddm[<%=i%>] + inter + " onBlur = 'upper(this)' <%=readOnlyView%> ></td></tr>"
				
<%

		}

		}
	//}//if(statusView)
%>				
<%
		
		if(!operation.equalsIgnoreCase("View") ){
%>
		
		for (i=0; i < number; i++)
		{
				data = data + "<tr  class='formdata'> <td colspan=4 align='left'> ADDRESS "+(i+1)+"</td></tr>"

				data = data + "<tr  class='formdata'>"

			if(i==0){
				data = data + "<td width='5%'>&nbsp</td>"
			}else{
				data = data + "<td width='5%'><input type='Button' class='input'  value='<<' onClick='removeRow22("+i+");' ></td>"

			}

		data= data + " <td width='12%'>Contact Person : <br> <input type='text' class='text' maxlength='30' size='20' name='contactPersond' value= " +inter + contactPersond[i] + inter  +"  onKeyPress='return checkSpecialKeyCode()' onBlur = 'upper(this)' ></td>" 
		data= data + "<td width='12%'>Designation :<br><input type='text' class='text' maxlength='30' size='20' name='designationd' value=" + inter + designationd[i] + inter + " onKeyPress='return checkSpecialKeyCode()' onBlur = 'upper(this)' ></td>"
		data= data + "<td width='12%'>AddressType :<font color='#FF0000'>*</font><br><select size='1' name='add_Flagd'  class='select'>"
	
		
		if(add_Flagd[i] == "P"){	
		

			data = data +"<option value='P' selected>PickUP Address</option>"
			data = data +"<option value='B' >Billing Address</option>"
			data = data +"<option value='D' >Delivery Address</option></select>"

		}else if(add_Flagd[i] == "B"){

			data = data +"<option value='P' >PickUP Address</option>"
			data = data +"<option value='B' selected>Billing Address</option>"
			data = data +"<option value='D' >Delivery Address</option></select>"

		}else if(add_Flagd[i] == "D"){


			data = data +"<option value='P' >PickUP Address</option>"
			data = data +"<option value='B' >Billing Address</option>"
			data = data +"<option value='D' selected>Delivery Address</option></select>"

		}else{
			data = data +"<option value='P'selected>PickUP Address</option>"
			data = data +"<option value='B' >Billing Address</option>"
			data = data +"<option value='D' >Delivery Address</option></select>"

		}
		
			
		
		
		data= data + "&nbsp "
<%
			if(operation.equals("Modify")){
%>
				+"Delete :<select size='1' name='del_Flagd'  class='select'><option value='N' selected>NO</option><option value='Y' >YES</option></select></td>"
<%	
			}else{

%>

		+"</td>"
<%
			}
%>
		+ "<tr  class='formdata'>"
		+ "<td width='5%'>&nbsp</td>"
		+ "<td width='12%'>Address :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=75 size='20' name='addressLine1d' value=" + inter+ addressLine1d[i] +inter+ " onKeyPress='return checkSpecialKeyCode()'; onBlur = 'upper(this)'; ><br><input type='text' class='text' maxlength=75 size='20' name='addressLine2d' value=" + inter + addressLine2d[i] + inter + " onKeyPress='return checkSpecialKeyCode()';onBlur = 'upper(this)'; ><br><input type='text' class='text' maxlength=75 size='20' name='addressLine3d' value=" + inter + addressLine3d[i] + inter + " onKeyPress='return checkSpecialKeyCode()';onBlur = 'upper(this)' ;onKeyPress='return checkSpecialKeyCode()'></td>"	
			/*
		+ "<td width='12%'>Address :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=75 size='20' name='addressLine1d' value=" + inter + addressLine1d[i] + inter + "onKeyPress='return checkSpecialKeyCode()'; onBlur = 'upper(this)'; ><br><input type='text' class='text' maxlength=75 size='20' name='addressLine2d' value=" + inter + addressLine2d[i] + inter + " onKeyPress='return checkSpecialKeyCode()';onBlur = 'upper(this)'; ><br><input type='text' class='text' maxlength=75 size='20' name='addressLine3d' value=" + inter + addressLine3d[i] + inter + " onKeyPress='return checkSpecialKeyCode()';onBlur = 'upper(this)' ;onKeyPress='return checkSpecialKeyCode()'></td>"	*/
		+ "<td width='12%'>City :<font color='#FF0000'>*</font><br><input type='text' class='text' maxlength=30 size='20' name='cityd' value=" + inter + cityd[i] + inter + " onBlur = 'upper(this)' ></td>"
		+ "<td width='12%'>ZipCode :<input type='text' class='text' maxlength=10 size='5' name='zipOrPostalCoded' value=" + inter + zipOrPostalCoded[i] + inter + " onBlur = 'upper(this)'  ><br>"
		+ "CountryId :<font color='#FF0000'>*</font> <input type='text' class='text' maxlength=2 size='5' name='countryIdd' value=" + inter + countryIdd[i] + inter + " onBlur = 'upper(this)' <%=readOnly%> onKeyPress='return getKeyCode()' >&nbsp;"

<%        

			if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Add") ){

%> 

			+ "<input type='button' class='input' value='...' name='lov_CountryId'  onClick='showCountryIds1("+i+")' >"

<%

			}
%> 								

		+"</td>"
		+ "<tr  class='formdata'>"
		+ "<td width='5%'>&nbsp</td>"
		+ "<td width='12%'>State or Province :<br> <input type='text' class='text' maxlength=30 size='20' name='stated' value=" + inter + stated[i] + inter + " onBlur = 'upper(this)' ></td>"							
		+ "<td width='12%'>Contact No :<br> <input type='text' class='text' maxlength=20 size='20' name='contactNod' value=" + inter + contactNod[i] + inter + " onBlur = 'upper(this)' ></td>"
		+ "<td width='12%'>Email :<br> <input type='text' class='text' maxlength=250 size='20' name='emailIdd' value=" + inter + emailIdd[i] + inter + " onBlur = 'upper(this)' >"


			if(i != (number-1))
					data=data+""

			if(i == (number-1))	{
					data = "" + data + " <input type='Button' class='input'  value='>>' onClick='checkTheRows();'></td>"
			}
					data = "" + data + "</div></tr>"
	}			
<%
		}//if (View Logic)
		if(operation.equalsIgnoreCase("View")){
%>
				data = "" + data +"<tr class='denotes'><td colspan='4' align='right'> <input type='submit' value='Continue' name='submit' class='input'></td></tr></table>"
	
<%
		}else{
%>
				data = "" + data +"<tr class='denotes'><td colspan='4' align='right'> <input type='submit' value='<%=value%>' name='submit' class='input' onClick='return checkTheRowsSubmit()'></td></tr></table>"
<%
		}
%>
				
			if (document.layers)
			{
				document.layers.cust1.document.write(data);
				document.layers.cust1.document.close();
			}
			else
			{
				if (document.all)
				{
					cust1.innerHTML = data;
				}
			}
		}
		else
		{
				window.alert("Only up to 25 entries.");
		}
		if(number>1 && number<26)	    
		{
			document.forms[0].addressLine1d[number-1].focus()
		}

}
	function initialize()
	{

		importXML('../xml/customerreg.xml');
	}
	function validateBeforeDeletion()
	{
		return true;
	}
	function validateBeforeCreation()
	{
		return true;
	}
	function initializeDynamicContentRows()
	{
<%
		if(!operation.equalsIgnoreCase("Add"))
		{
%>
			var tableObj = document.getElementById("customerreg");
			for(var index=0; index < <%=custDtl.size()%> ; index++)
			{
				createDynamicContentRow(tableObj.getAttribute("id"));
			}
<%
			if(custDtl.size()!=0)
			{
				for(int index=0;index<custDtl.size();++index)
				{
%>
					//if(custAddrType[0]=="P")//@@commented by subrahmanyam for 167669 on 04May09
          if(custAddrType[<%=index%>]=="P")//@@Added by subrahmanyam for 167669 on 04May09
						document.getElementById("addrtype<%=index+1%>").options[0].selected=true;
					//else if(custAddrType[0]=="D")//@@commented by subrahmanyam for 167669 on 04May09
          else if(custAddrType[<%=index%>]=="D")//@@added by subrahmanyam for 167669 on 04May09
						document.getElementById("addrtype<%=index+1%>").options[1].selected=true;
					else
						document.getElementById("addrtype<%=index+1%>").options[2].selected=true;
					if(custContactPerson[<%=index%>]=="null")
						document.getElementById("contactPerson<%=index+1%>").value	="";
					else
					{
					     document.getElementById("contactPerson<%=index+1%>").value	=	custContactPerson[<%=index%>];
					}
					
					if(custDesignation[<%=index%>]=="null")
						document.getElementById("Designation<%=index+1%>").value	="";
					else
						document.getElementById("Designation<%=index+1%>").value	=	custDesignation[<%=index%>];
					if(custDept[<%=index%>]=="null")
						document.getElementById("Department<%=index+1%>").value		="";
					else
						document.getElementById("Department<%=index+1%>").value		=	custDept[<%=index%>];
					if(custZipCode[<%=index%>]=="null")
						document.getElementById("ZipCode<%=index+1%>").value		="";
					else
						document.getElementById("ZipCode<%=index+1%>").value		=	custZipCode[<%=index%>];
					if(custContact[<%=index%>]=="null")
						document.getElementById("Contact<%=index+1%>").value		="";
					else
						document.getElementById("Contact<%=index+1%>").value		=	custContact[<%=index%>];
					if(custFax[<%=index%>]=="null")
						document.getElementById("conFax<%=index+1%>").value			="";
					else
						document.getElementById("conFax<%=index+1%>").value			=	custFax[<%=index%>];
					if(custEmail[<%=index%>]=="null")
						document.getElementById("Email<%=index+1%>").value			="";
					else
						document.getElementById("Email<%=index+1%>").value			=	custEmail[<%=index%>];
					if(custContactId[<%=index%>]=="null")
						document.getElementById("ContactId<%=index+1%>").value		=	"";
					else
						document.getElementById("ContactId<%=index+1%>").value		=	custContactId[<%=index%>];
					 <%
					if(operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("Delete"))  
					{ 
						 
					     if(operation.equalsIgnoreCase("Delete"))
						  {
						 %>
                            document.forms[0].addrtype[<%=index+1%>].value=document.getElementById("addrtype<%=index+1%>").value;
						 <%
						  }
						 %>
						 document.getElementById("addrtype<%=index+1%>").disabled=true;
						 document.getElementById("contactPerson<%=index+1%>").readOnly	=true;
						 document.getElementById("Designation<%=index+1%>").readOnly	=true;
						 document.getElementById("Department<%=index+1%>").readOnly	=true;
						 document.getElementById("ZipCode<%=index+1%>").readOnly	=true;
						 document.getElementById("Contact<%=index+1%>").readOnly	=true;
						 document.getElementById("conFax<%=index+1%>").readOnly	=true;
						 document.getElementById("Email<%=index+1%>").readOnly	=true;
				         document.getElementById("customerregaddbut<%=index+1%>").disabled =true;
				       <%   if(index>0) { %>
						 document.getElementById("customerregdelbut<%=index+1%>").disabled =true;   
				        
				 <% } } %>
<%		
				}
			}
			else
			{
%>
               setValues();
 <%
			if(operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("Delete")) 
			{ %>
               
               document.forms[0].addrtype.disabled=true;
			   document.forms[0].contactPerson.readOnly	=true;
			   document.forms[0].Designation.readOnly	=true;
			   document.forms[0].Department.readOnly	=true;
			   document.forms[0].ZipCode.readOnly	=true;
			   document.forms[0].Contact.readOnly	=true;
			   document.forms[0].conFax.readOnly	=true;
			   document.forms[0].Email.readOnly	=true;
               document.forms[0].addbut.disabled =true;
   
<%             }
			}
		}
		else
		{
%>			
			setValues();
<%
		}
%>
	}
	function setValues()
	{
		var tableObj = document.getElementById("customerreg");
		if(false)
		{}
		else
		{
			if(tableObj.getAttribute("idcounter")==1)
						createDynamicContentRow(tableObj.getAttribute("id"));
		}

	}
	function defaultFunction(currentRow)
	{
	}
	function defaultDeleteFunction()
	{
	}
	function checkNumeric(input)
	{
		input.value = input.value.toUpperCase();

		if(isNaN(input.value))
		{
			alert('Please Enter Numeric Values Only.');
			input.value='';
			input.focus();
			return false;
		}
	}

	
</script>
</head>
<body onLoad ='onLoad=createCusrAddAddrMored(number11,1);placeFocus();initialize();'>
<form  method="post"  onSubmit=" return Mandatory(); " action="<%=actionValue%>">  
<table width="800" border="0" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
<tr>
<td bgcolor="#FFFFFF" valign="top" >




<!--	   
<span id='cust'  style='position:relative;'></span> &nbsp;
<span id='cust1' style='position:relative;'></span> &nbsp;  !-->
<span id='cust'  style='position:absolute;'></span> 
<span id='cust1' style='position:absolute;'></span> 


<div id='cust2' style='position:absolute;align:left;left:540;top:660;width=10;height=10;'>

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
<input type="hidden" name="fromWhat"  value="<%=request.getParameter("fromWhat")%>">
<%
 if(operation.equalsIgnoreCase("delete"))
{
  if(custDtl.size()!=0)
   {
	for(int index=0;index<custDtl.size();++index)
	{
%>
<input type="hidden" name="addrtype">
<%
				
     } 
   }
}
%>
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

		String url				=	"ETCustomerRegistrationEnterIdC.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
		if(registrationLevel!=null && registrationLevel.equals("C"))
				url				=	"ETCustomerRegistrationEnterIdC.jsp?Operation="+operation+"&Customer="+Customer+"&registrationLevel="+registrationLevel;
		
		errorMessageObject = new ErrorMessage("Error while loading the details with CustomerId : " + customerId,url); 

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
