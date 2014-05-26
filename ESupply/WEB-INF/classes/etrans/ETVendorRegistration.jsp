<%--
%
% Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
% This Software is the proprietary information of FourSoft,Pvt Ltd.
% Use is subject to license terms
%
% esupply-V 1.x
%
File								:	ETVendorRegistration
		Sub-module name			:	Vendor Registration
		Module name					:	ETrans
		Purpose of the class		:	It presents The GUI to add,Modify,View and Delete the Vendor Details
		Author							:	Nageswara Rao.D
		Date								:	21/Jan/2003
		Modified history		:
--%>
<%@ page import	=	"org.apache.log4j.Logger,
								com.foursoft.esupply.common.java.ErrorMessage,
								com.foursoft.etrans.setup.vendorregistration.java.VendorRegistrationJava,
								com.foursoft.etrans.common.bean.Address,
								com.foursoft.esupply.common.java.KeyValue" %>
				
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>

<%
	String							FILE_NAME					  =	"ETActivityHouseAdd.jsp";	
  logger  = Logger.getLogger(FILE_NAME);	
	String							terminalId					=	null;
	String							operation						=	null;
	String							subOperation				=	null;
	String							moduleType					=	null;
	String							abbrName						=	"";
	String							shipmentMode				=	"";
	String							companyName				  =	"";
	String							contactName					=	"";
	String							carrierId						=	"";
	String							designation					=	"";
	String							address1						=	"";
	String							address2						=	"";
	String							zipCode							=	"";
	String							city								=	"";
	String							state								=	"";
	String							countryId						=	"";
	String							phoneNo						  =	"";
	String							faxNo							  =	"";
	String							notes								=	"";
	String							emailId							=	"";
	String							operationMailId			=	"";
	String							readOnly						=	"";
	String							disabled						=	"";
	String							readforModify				=  "";
	String							action							=	"";
	String							addressId						=	"";
	String							vendorId						=	"";
  String              vendRegFlag         = null;
	StringBuffer				errorMessage				= null; 
  //@@Code added by Ujwala as per jira SPETI-5856 onDate :25/05/2005
	String							operFlag				=	"";	
	String							checkFlag				=	"";
	//@@End by Ujwala

	VendorRegistrationJava	vendorRegistrationJava	=	null;
	Address							    addressObj			        =	null;
  
	terminalId													=	loginbean.getTerminalId();
	operation														=	request.getParameter("Operation");
	subOperation												=	request.getParameter("SubOperation");
//@@ Modified By Ravi Kumar to Resolve the Issue SPETI-5895 on 26-04-2005
	String vendAcctOperation        =	request.getParameter("vendAcctOperation")==null?"":request.getParameter("vendAcctOperation");	
//@@ 26-04-2005
	if(request.getParameter("Type")!=null)
		moduleType			=	request.getParameter("Type");
  else
		moduleType			=	"Accounts";
	if(moduleType!=null && moduleType.equals("Accounts"))
	{
			subOperation="Accounts";
			vendAcctOperation="Accounts";
    }
    
	if(session.getAttribute("vendorErrors")!=null)
		errorMessage	  =	(StringBuffer)session.getAttribute("vendorErrors");

	if(session.getAttribute("VendorDetails")!=null)
		{
			vendorRegistrationJava  =   (VendorRegistrationJava)session.getAttribute("VendorDetails");
			vendorId                =   vendorRegistrationJava.getVendorId();
			addressObj              =   vendorRegistrationJava.getAddressObj();
			abbrName                =   vendorRegistrationJava.getAbbrName();
			companyName             =   vendorRegistrationJava.getCompanyName();
			shipmentMode            =   vendorRegistrationJava.getShipmentMode();
			contactName             =   vendorRegistrationJava.getContactName();
			carrierId               =   vendorRegistrationJava.getCarrierId();
			designation             =   vendorRegistrationJava.getDesignation();
			notes                   =   vendorRegistrationJava.getNotes();
			operationMailId         =   vendorRegistrationJava.getOperationMailId();
      vendRegFlag             =   vendorRegistrationJava.getVendRegFlag();
			addressId               =   String.valueOf(addressObj.getAddressId());
			address1                =   addressObj.getAddressLine1();
			address2                =   addressObj.getAddressLine2();
			zipCode                 =   addressObj.getZipCode();
			city                    =   addressObj.getCity();
			state                   =   addressObj.getState();
			countryId               =   addressObj.getCountryId();
			phoneNo                 =   addressObj.getPhoneNo();
			faxNo                   =   addressObj.getFax();
			emailId                 =   addressObj.getEmailId();
      //@@Code added by Ujwala as per jira SPETI-5856 onDate :25/05/2005
	        operFlag                =   vendorRegistrationJava.getIndicator();
			//@@End - Ujwala
		}	
    //@@Code added by Ujwala as per jira SPETI-5856 onDate :25/05/2005
		if(operFlag.equalsIgnoreCase("Y"))
            checkFlag = "checked";
	//@@End - Ujwala
	if(operation.equals("Modify"))
	{
		readforModify		=	"readOnly";
		subOperation		=	"update";
	}
	if(operation.equals("View")||operation.equals("Delete"))
	{
		readOnly			  =	"readOnly";
		disabled				=	"disabled";
	
	}
	if(operation.equals("Delete"))
	{
		subOperation		=	"remove";
	}
	if(operation.equals("View"))
		action	=	"etrans/ETVendorRegistrationEnterId.jsp?Operation="+operation+"&subOperation="+subOperation+"&Type="+moduleType;
	else
		action	=	"ETVendorRegistrationController";
	
%>
<html>
<head>
	<title>Vendor Registration <%=operation%></title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link rel="stylesheet" href="ESFoursoft_css.jsp">
	<script>
	function validate(){
		var field = document.forms[0];    
		for(i = 0; i < field.length; i++){
		   if( (field.elements[i].type == "text")  && (field.elements[i].name!= "emailId") )
			   document.forms[0].elements[i].value = document.forms[0].elements[i].value.toUpperCase();
	    }

		var msgHeader     = '';
		var msgErrors     = '';
		var focusPosition = new Array();
		msgHeader= '_____________________________________________________\n\n' +
		'This form has not been submitted because of the following error(s).\n' +
		'Please correct the error(s) and re-submit.\n' +
		'_____________________________________________________\n\n';

		if(document.forms[0].abbrName.value == 0){
			msgErrors       += 'Abbrivated Name cannot be empty\n';
			focusPosition[0] = 'abbrName';
		}

		if(document.forms[0].companyName.value == 0){
			msgErrors       += 'Company Name cannot be empty\n';
			focusPosition[1] = 'companyName';
		}
	
		if(document.forms[0].address1.value == 0){
			msgErrors       += 'Address cannot be empty. \n';
			focusPosition[2] = 'address1';
		}
		if(document.forms[0].city.value == 0){
			msgErrors       += 'City cannot be empty. \n';
			focusPosition[3] = 'city';
		}
		if(document.forms[0].countryId.value == 0){
			msgErrors       += 'CountryId cannot be empty. \n';
			focusPosition[4] = 'countryId';
		}
		if(document.forms[0].operationEmailId.value == 0){
			msgErrors       += 'Operations EmailId cannot be empty. \n';
			focusPosition[5] = 'operationEmailId';
		}		
<%
	    if(vendAcctOperation.equals("Accounts")){
%>
			if(!document.forms[0].Air.checked && !document.forms[0].Sea.checked && !document.forms[0].Truck.checked && !document.forms[0].Others.checked){
<%
		}else{
%>
			if(!document.forms[0].Air.checked && !document.forms[0].Sea.checked && !document.forms[0].Truck.checked){
<%
		}
%>
				alert("Select a shipment mode");
				return false;
			}
<%
	    if(vendAcctOperation.equals("Accounts")){
%>
			if(document.forms[0].Truck.checked==false && document.forms[0].Others.checked==false)
			{
<%
		}else{
%>
			if(document.forms[0].Truck.checked==false)
			{
<%
		}	
%>
        // commented below IF Condition to make CARRIER AS Non-mandatory  
				/*if(document.forms[0].carrierId.value == 0){
					msgErrors       += 'CarrierId cannot be empty. \n';
					focusPosition[6] = 'carrierId';
				}*/	
			}            
		
		if(msgErrors.length > 0){
			alert(msgHeader + msgErrors);
			for(loop =0 ;loop< focusPosition.length; loop++){
				if(focusPosition[loop] != null && focusPosition[loop] != ''){
					document.forms[0].elements[focusPosition[loop]].focus();
					break;
				}
			}
			
			return false;
			
		}else{
			assignedMode();
			return checkEmail();
			//return true;
		}		
	}
function checkEmail()
{
	var str = document.forms[0].operationEmailId.value;
	var i=0,j=-1;
	var str1;
	var flag=false;
	while(1)
	{
		if(document.forms[0].operationEmailId.value=="")
		{
			alert(" opEmailId can't be empty");	
			document.forms[0].operationEmailId.focus();
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
				document.forms[0].operationEmailId.focus();
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
		document.forms[0].operationEmailId.focus();
		return false;
	}

	document.forms[0].Submit2.disabled='true';
}
	function showCountryIds(){
		var URL	      = 'etrans/ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
		var Bars 		  = 'directories = no, location = no, menubar = no, status = no, titlebar = no';
		var Options 	= 'scrollbars = yes,width = 360,height = 360,resizable = yes';
		var Features 	= Bars +' '+ Options;
		var Win 		  = open(URL,'Doc',Features);
	}
	function showCarrierIds(){
<%
	if(operation.equals("Add"))
	{
	
%>			
		if(!document.forms[0].Air.checked && !document.forms[0].Sea.checked)
	    {
			alert("Select a shipment mode");
			return false;
		}

		if(document.forms[0].Air.checked == true)
			shipmentMode='Air';
		if(document.forms[0].Sea.checked ==true)
			shipmentMode='Sea';
		if(document.forms[0].Sea.checked ==true && document.forms[0].Air.checked == true)
			shipmentMode='All';
      
<%		
	}
	else
	{
		
%>	
		 var modeOfShipment	=	'<%=shipmentMode%>';
		 if(modeOfShipment == "1")
			 shipmentMode = 'Air';
		 else if(modeOfShipment == "2")
			 shipmentMode = 'Sea';	
		 else if(modeOfShipment == "4")
			 shipmentMode = 'Truck';	

<%
   }
%>
		var URL 		  = 'etrans/ETransLOVCarrierIds.jsp?searchString='+document.forms[0].carrierId.value.toUpperCase()+'&shipmentMode='+shipmentMode+'&type=vendor';	
		var Bars 		  = 'directories = no, location = no, menubar = no, status = no, titlebar = no';
		var Options 	= 'scrollbars = yes,width = 360,height = 360,resizable = yes';
		var Features 	= Bars +' '+ Options;
		var Win 		  = open(URL,'Doc',Features);
	}
	function changeToUpper(field){
		field.value   = field.value.toUpperCase();
	}
	function getStringKeyCode(){  // This function  accept only alfa characters
		if(event.keyCode!==13){
			if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||(event.keyCode > 122 && event.keyCode <127))
				event.returnValue =false;
		}
		return true;
	}
	function getSpecialCode(){   // This function accept chars and numbers only
		if(event.keyCode!=13){
			if((event.keyCode==38 || event.keyCode==34 || event.keyCode==39 || event.keyCode==59 || event.keyCode==96 || event.keyCode==126))
				return false;
		}
		return true;
	}
	function clearSMode(mode){
		if (mode.name == "Truck" || mode.name == "Air" || mode.name == "Sea")
						document.forms[0].carrierId.value='';
		if(mode.name == "Truck"){
				document.forms[0].Air.checked			= false;
				document.forms[0].Sea.checked			= false;
				document.forms[0].carrierId.value		= '';
				document.forms[0].carrierId.disabled	= true;
				document.forms[0].carrierLov.disabled	= true;
			<%
				if(vendAcctOperation.equals("Accounts")){
			%>
					document.forms[0].Others.checked	= false;				
			<%
				}	
			%>
		}else if(mode.name == "Air" || mode.name == "Sea"){
				document.forms[0].Truck.checked			= false;
				document.forms[0].carrierId.disabled	= false;
				document.forms[0].carrierLov.disabled	= false;
			<%
				if(vendAcctOperation.equals("Accounts")){
			%>
					document.forms[0].Others.checked	= false;	
			<%
				}	
			%>
		}
		else
		{
				document.forms[0].Air.checked			= false;
				document.forms[0].Sea.checked			= false;
				document.forms[0].Truck.checked			= false;
				document.forms[0].carrierId.value		= '';
				document.forms[0].carrierId.disabled	= true;
				document.forms[0].carrierLov.disabled	= true;						

<%
				if(!operation.equals("Add")){
%>
					document.forms[0].carrierId.value   = '<%=carrierId%>';
<%
				}
%>
		}
	}
	
	function assignedMode(){
		var field	 = document.forms[0];
		

		if(field.Air.checked == true)
			field.shipmentMode.value  ='1';
		if(field.Sea.checked ==true)
			field.shipmentMode.value  ='2';
		if(field.Sea.checked ==true && field.Air.checked == true)
			field.shipmentMode.value  ='3';
 		if(field.Truck.checked ==true)
			field.shipmentMode.value  ='4';

		//alert(field.shipmentMode.value)
	<%
		if(vendAcctOperation.equals("Accounts")){
	%>
			if(field.Others.checked ==true)
			{
				field.shipmentMode.value  ='0';
				field.indicator.value = 'N';
				field.operVisFlag.checked = false;
				field.operVisFlag.disabled = true;
			}
			if((field.Air.checked == true) || (field.Sea.checked == true) || (field.Truck.checked == true))
				field.operVisFlag.disabled = false;
	<%
		}	
	%>
  }
	function checkVendRegFlag(obj){
		if(obj.checked==true)
			document.forms[0].indicator.value ='Y';
		else
			document.forms[0].indicator.value ='N';
		
	}
	function placeFocus(){
		document.forms[0].abbrName.focus();
	}

	function assignShipmentMode(){		
		var operation			=	'<%=operation%>';
		var vendFlag   = '<%=vendRegFlag%>';
		if(operation!='Add')
		{
		  document.forms[0].vendRegFlag.value = '<%=vendRegFlag%>';
		}
<%
		if(vendAcctOperation.equals("Accounts"))
		{
%>	// CHENGED BY SENTHILPRABHU  FOR SPETI-5840.
	// @@ suneetha Replaced on 20050429
		//	document.forms[0].indicator.value = 'Y';
	//@@Code changed(CommentRemoved) by Ujwala as per jira SPETI-5856 onDate :25/05/2005
		/*if(document.forms[0].operVisFlag.checked==true)
			document.forms[0].indicator.value = 'Y';
		else*/
		if(document.forms[0].operVisFlag.checked==true)
			document.forms[0].indicator.value = 'Y';
		else
			document.forms[0].indicator.value ='N';
//@@End - Ujwala
	// @@ 20050429
<%
		}
		else
		{
%>
		document.forms[0].indicator.value = 'Y';
		
<%
		}
	    if(vendAcctOperation.equals("Accounts")){
%>    
		if(vendFlag=='A' && operation=='Add')
		{
			document.forms[0].operVisFlag.disabled = false;
			document.forms[0].operVisFlag.checked  = false;
		}
		else if(vendFlag=='A' && operation!='Add')
		{
			document.forms[0].operVisFlag.disabled = true;
		}
		else if(vendFlag=='O' && operation!='Add')
		{
			document.forms[0].operVisFlag.disabled = true;
			document.forms[0].operVisFlag.checked  = true;
		}
<%
    }
	if(!operation.equals("Add"))
	{
%>		
		var shipmentMode	=	'<%=shipmentMode%>';
		document.forms[0].carrierId.readonly	  =	true;
		document.forms[0].carrierLov.disabled  =	true;
		
		if(shipmentMode=="1"){
			document.forms[0].Air.checked     = true;
	        document.forms[0].Air.disabled    = true;
			document.forms[0].Sea.disabled    = true;
			document.forms[0].Truck.disabled  = true;
			//@@ Sreelakshmi K.V.A. commented 20041227 SPETI-3564,SPETI-3565,SPETI-3566
			//document.forms[0].Others.checked  = false;
			//document.forms[0].Others.disabled = true;
		}
		if(shipmentMode=="2"){
			document.forms[0].Air.disabled    = true;
			document.forms[0].Sea.checked     = true;
	        document.forms[0].Sea.disabled    = true;
			document.forms[0].Air.checked     = false;
			document.forms[0].Truck.disabled  = true;
			//@@ Sreelakshmi K.V.A. commented 20041227 SPETI-3564,SPETI-3565,SPETI-3566
			//document.forms[0].Others.checked  = false;
			//document.forms[0].Others.disabled = true;
		}
		if(shipmentMode=="3"){
			document.forms[0].Air.checked     = true;
			document.forms[0].Sea.checked     = true;
			document.forms[0].Air.disabled    = true;
			document.forms[0].Sea.disabled    = true;
			document.forms[0].Truck.disabled  = true;
			//@@ Sreelakshmi K.V.A. commented 20041227 SPETI-3564,SPETI-3565,SPETI-3566
			//document.forms[0].Others.checked  = false;
			//document.forms[0].Others.disabled = true;
		}
		if(shipmentMode=="4"){
			document.forms[0].Air.disabled    = true;
			document.forms[0].Sea.disabled    = true;
			document.forms[0].Truck.checked   = true;        
			document.forms[0].Truck.disabled  = true;
			//@@ Sreelakshmi K.V.A. commented 20041227 SPETI-3564,SPETI-3565,SPETI-3566
			//document.forms[0].Others.checked  = false;
			//document.forms[0].Others.disabled = true;
		}
		if(shipmentMode=="0"){
			document.forms[0].Air.checked     = false;
			document.forms[0].Sea.checked     = false;
			document.forms[0].Truck.checked   = false;
			document.forms[0].Air.disabled    = true;
			document.forms[0].Sea.disabled    = true;
			document.forms[0].Truck.disabled  = true;
			//@@ Sreelakshmi K.V.A. commented 20041227 SPETI-3564,SPETI-3565,SPETI-3566
			//@@ Sreelakshmi K.V.A. Uncommented 20050420 SPETI-5878 SPETI-5879
			document.forms[0].Others.checked  = true;
			document.forms[0].Others.disabled = true;
			//@@ 20050420
		}
<%
	}
%>
}
	</script>
</head>
<body bgcolor="#FFFFFF" onLoad="placeFocus();assignShipmentMode()">
<form method="post"  action="<%=action%>"  onSubmit="return validate()" >
<input type=hidden name=indicator>
	<table width="800" border="0" cellspacing="0" cellpadding="0">
	<tr>
	<td bgcolor="#ffffff">
	
	<table width="800" border="0" cellspacing="1" cellpadding="4">
	    <%//Header corrected  by Sreelakshmi KVA - 20050411 SPETI-5575//%>
		<tr class='formlabel'> <td colspan="3"><table width="790" border="0" ><tr class='formlabel'><td>Vendor Registration - <%=operation%>  </td><td align=right><%=loginbean.generateUniqueId("ETVendorRegistration.jsp",operation)%></td>
		</tr></table></td></tr>
		<!--tr class='formdata'>	<td colspan="3">&nbsp;</td></tr-->
		</table>
<%
		if(errorMessage != null)
		{
%>
			<table width="800"   border="0" cellpadding="4" cellspacing="1">
			<tr class='formdata' valign="top">
			<td width="33%"><font color='red'><b>Following field(s) are incorrect : </b> <br><br>
				<%=errorMessage.toString()%>
			</font></td>
			</tr>
			</table>
	
<%
		}
%>
		<table width="800"   border="0" cellpadding="4" cellspacing="1">
		<tr class='formdata'><td colspan="4">&nbsp;</td></tr>
	 <tr class='formdata'> 
			<td  width="192">TerminalId:<font color="#FF0000">*</font><br><input type="text"  class=text name="terminalId" size="16" value=<%=terminalId%>  readOnly></td>
			<td  width="217">Abbreviated Name:<font color="#FF0000">*</font><br><input type="text"  class=text name="abbrName" size="10" maxlength='4'  onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)" value=<%=abbrName%> <%=readforModify%> <%=readOnly%>></td>
			<input type="hidden" name="shipmentMode" value='<%=shipmentMode%>'  class="select">
		<td  width=350>Shipment Mode:<font color="#FF0000">*</font><br>
				<input type="checkbox" name="Air" value="1"	<%=disabled%>  onClick='clearSMode(this);assignedMode()'  >Air 
				<input type="checkbox" name="Sea" value="2"  onClick="clearSMode(this);assignedMode()"  <%=disabled%>>Sea 
				<input type="checkbox" name="Truck" value="4"  onClick="clearSMode(this);assignedMode()" <%=disabled%>>Truck 

		<%
			if(vendAcctOperation.equals("Accounts")){
		%>				
				<input type="checkbox" name="Others" value="0" onclick="clearSMode(this);assignedMode()">Others			
		<%
			}
		%>		
		</td>			
	</tr>
		<tr class='formdata'> 
			<td colspan="2">Company Name:<font color="#FF0000">*</font> <br><input type="text" class=text name="companyName" size="30" maxlength='50' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)" value='<%=companyName%>' <%=readOnly%>></td>
			<td>Carrier Id: <!-- <font color="#FF0000">*</font> --><br>
				<input type="text" class="text" name="carrierId" size="16" maxlength='5' onBlur="changeToUpper(this)"   class=text value='<%=carrierId%>'  <%=readforModify%> <%=readOnly%>>
				<input type="button" name="carrierLov" value="..." class='input' onClick='showCarrierIds()' <%=disabled%>>
			</td>
		</tr>
		<tr class='formdata'> 
			<td colspan="2">Contact Person: <br><input type="text" class=text name="contactPerson" size="30" maxlength='30' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)" value='<%=contactName%>' <%=readOnly%>></td>
			<td>Designation: <br><input type="text" class=text name="designation" size="30" maxlength='30' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)" value='<%=designation%>'  <%=readOnly%>></td>
		</tr>	
		<tr class='formdata'> 
			<td rowspan="2" colspan="2">Address:<font color="#FF0000">*</font> <br>
				<input type="text" class=text name="address1" size="30" maxlength='75' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)"  value='<%=address1%>' <%=readOnly%>><br>
				<input type="text" class=text name="address2" size="30" maxlength='75' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)" value='<%=address2%>'  <%=readOnly%>>
			</td>
			<td>Zip or Postal Code: <br><input type="text" class=text name="zipCode" size="16" maxlength='10' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)" value='<%=zipCode%>' <%=readOnly%>></td>
		</tr>
		<tr class='formdata'> 
			<td>Contact No: <br><input type="text" class=text name="phoneNo" size="16" maxlength='20' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)" value='<%=phoneNo%>' <%=readOnly%>></td>
		</tr>
		<tr class='formdata'> 
			<td colspan="2">City:<font color="#FF0000">*</font> <br><input type="text" class=text name="city" size="16" maxlength='30' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)" value='<%=city%>' <%=readOnly%>></td>
			<td>Fax No: <br><input type="text" name="faxNo" size="16" class=text maxlength='20' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)" value='<%=faxNo%>'  <%=readOnly%>></td>
		</tr>
		<tr class='formdata'> 
			<td colspan="2">State or Province:<br><input type="text" class=text name="state" size="16" maxlength='30' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)" value='<%=state%>' <%=readOnly%>></td>
		<td>Country Id:<font color="#FF0000">*</font> <br>
				<input type="text" name="countryId" size="16" class=text maxlength='2' onKeyPress = "return getStringKeyCode()" onBlur="changeToUpper(this)"  value='<%=countryId%>'  <%=readOnly%>>&nbsp;
				<input type="button" name="countryLov" value="..." class='input' onClick="showCountryIds()" <%=disabled%>></td>
		</tr>
		<tr class='formdata'> 
			<td colspan="2">Operations EmailId:<font color="#FF0000">*</font> <br><textarea name="operationEmailId" cols="35" rows="5" class='select' maxlength='250' onKeyPress = "return getSpecialCode()"  <%=readOnly%>><%=operationMailId%></textarea></td>
			<td>EmailId: <br><textarea name="mailId" cols="35" rows="5"   class='select' maxlength='250' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)"  <%=readOnly%>><%=emailId%> </textarea></td>
		</tr>
		<tr class='formdata'> 
			<td colspan="2">Notes :<br><textarea name="notes" cols="35" rows="5"  class='select' maxlength='2000' onKeyPress = "return getSpecialCode()" onBlur="changeToUpper(this)"   <%=readOnly%>> <%=notes%></textarea></td>
<%
    if(vendAcctOperation.equals("Accounts")){
%>
		<td colspan="2"> 
        
        <input type=hidden name=vendRegFlag value='A'>
        <!-- Code chenged by ujwala as per jira SPETI-5856 onDate: 25/05/2005-->
<!--        <input type="checkbox" name="operVisFlag" value="" <%=checkFlag%> onClick="checkVendRegFlag(this)">Operation Visiblity-->
        <input type="checkbox" name="operVisFlag" value="" <%=checkFlag%> onClick="checkVendRegFlag(this)">Operation Visiblity
<!--End by Ujwala-->
    </td>   
<%
		}
else
{
%>
	<input type=hidden name=vendRegFlag value='O'>
    <td>&nbsp;</td>
<%
}
%>
		</tr>
		</table>
		<input type=hidden name=Operation value='<%=operation%>'>
<%		
		if(operation.equals("Add") && subOperation.equals("Accounts")){
%>
			<input type=hidden name=SubOperation value='Idcreate'>
<%
		}else{
%>
			<input type=hidden name=SubOperation value='<%=subOperation%>'>
<%
		}	
%>
		<input type=hidden name=moduleType value='<%=moduleType%>'>
		<input type=hidden name=addressId value='<%=addressId%>'>
		<input type=hidden name=vendorId value='<%=vendorId%>'>
<!-- Added By Ravi Kumar to Resolve the Issue SPETI-5895 on 26-04-2005 -->
		<input type=hidden name=vendAcctOperation value='<%=vendAcctOperation%>'>
<!-- 26-04-2005 -->
		<table width="800"   border="0" cellpadding="4" cellspacing="1">
		<tr class='denotes'> 
		<% //  Changed by senthil prabhu for 1785 On 20052405 %>
		<td colspan="2"><font color='red'>*</font>Denotes Mandatory Note: Please separate each EmailId by a semi colon (;) </td>	
		<%//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-4571,4573 on 20050309.
		if(operation.equals("View"))
		{
		%>
				<input type="submit" name="Submit2" value="Continue" class='input'>
		<%
		}
		else if(operation.equals("Delete"))
		{
		%>
				<input type="submit" name="Submit2" value="Delete" class='input'>
		<%
		}
		else
		{//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-4570 on 20050309.
		%>
				<input type="submit" name="Submit2" value="Submit" class='input' onClick='assignShipmentMode()'>
				<% // @@ added by Suneetha for IssueID - SPETI-3851  on 7th Feb 05 %>
				<input type='reset' size=10  name=reset value='Reset'  class='input'>
				<% // @@ %>
		<%
		}
		%>
		</td>
	</tr>
</table>
</td>
</tr>
</table>
</form>
</body>
</html>
