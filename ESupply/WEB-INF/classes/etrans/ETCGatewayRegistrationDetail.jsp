<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
  /**
	Program Name		:	ETCGatewayRegistrationDetail.jsp
	Module Name			:   ETrans
	Task				:	Gateway Master
	Sub Task			:	Modify/View/Delte
	Author Name			:   Anand . A
	Date Started		:	September 11,2001
	Date Ended			:	September 11,2001
	Date Modified		:  
		
	Description			:	This File main Purpose is to show the Details of Gateway Master
	                        provided the Gateway Master Id entered in GatewayMasterEnterId 
  */
%>
<%@ page import	=	"javax.naming.InitialContext,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.common.bean.Address" %>

<jsp:useBean id="gatewayJSP" class="com.foursoft.etrans.setup.gateway.bean.GatewayJSPBean" scope="request"/>
<jsp:setProperty name="gatewayJSP" property="*" />
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCGatewayRegistrationDetail.jsp ";
%>

<%
   logger  = Logger.getLogger(FILE_NAME);	
   try
   {
    if(loginbean.getTerminalId() == null )
     {
%>
      <jsp:forward page="../ESupplyLogin.jsp"/>
<%
     }
   }
   catch(Exception e)
   { 
	  //Logger.error(FILE_NAME,"Error inGatewayRegistrationModifyProcess.jsp file : ", e.toString());
    logger.error(FILE_NAME+"Error inGatewayRegistrationModifyProcess.jsp file : "+ e.toString());
   } 
    ArrayList   terminalId       = null;  			// for storing all the Terminal Ids
    ArrayList   locationId       = null;  			// for storing all the Location Ids 
    String[]    locationName     = null;  			// for storing all the LocationName 
	ArrayList   countryIds       = null;            // for storing all the Country Ids 
    String      gatewayType      = null;            // for storing all the Gateway Types
    String      gatewayName      = null;            // for storing the Gateway Name
    String      contactName      = null;            // for storing the contact Name 
    double      addressId        = 0;               // for storing the Address Id 
    String      notes            = "";              // for storing the Notes   
    String      addressLine1     = null;            // for storing the Address Line1 
    String      addressLine2     = "";              // for storing the Address Line2 
    String      city             = null;            // for storing the City     
    String      state            = "";              // for storing the State  
    String      zipCode          = "";              // for storing the ZipCode
    String      countryId        = null;            // for storing the Country Id   
    String      phoneNo          = "";              // for storing the Phone No
    String      emailId          = "";              // for storing the Email Id
    String      fax              = "";              // for storing the Fax
    String      terminals[]      = null;            // for storing all the Terminals  
    String      gatewayId        = null;            // for storinng the Gateway Id 
    String      locId            = null;            // for storing the Location Id
    String      gatewayTypeAir   = null;            // for storing the GatewayTypeAir vlaue i.e. eitehr Yes or No
    String      gatewayTypeSea   = null;            // for storing the GatewayTypeSea vlaue i.e. eitehr Yes or No 
    String      gatewayTypeTruck = null;            // for storing the GatewayTypeTruck vlaue i.e. eitehr Yes or No
    boolean     b                = true;            
	String      companyName      = null;            // for storing the Company Name
    Address     address          = null;            // Address Object for storing Address Details  
    String      operation        = null;            // Operation to capture the Passed Parameter i.e., whether Add/Modify/View/Delete 
    String      actionValue      = null;           // for storing the ActionValue i.e., action tag of html form 
	String      readOnly         = null;            // for checking whether to make the fields readOnly or Not  
	String      dsbl             = null;            // for checking whether to make the fields disable or Not 
	String[]    gatewayIds       = null;            // for storing the Gateway Ids
	String 		timeZone		 = null;  
	ArrayList	terminalIdDB	 = null;
    
	try
	{
	  
	  operation = session.getAttribute("Operation").toString();
    
	  if(operation != null && operation.equalsIgnoreCase("View")) 
       {
         dsbl        = "disabled";
         actionValue = "ETCGatewayRegistrationEnterId.jsp?Operation="+operation; 
		 readOnly    = "readonly";
       }
      else if(operation != null && operation.equalsIgnoreCase("Delete"))  
       {
         dsbl        = "disabled";
         actionValue = "ETCGatewayRegistrationProcess.jsp"; 
	     readOnly    = "readonly"; 
       }
      else if(operation != null && operation.equalsIgnoreCase("Modify"))   
       {
	     dsbl        = "";
         actionValue = "ETCGatewayRegistrationProcess.jsp"; 
		 readOnly    = ""; 
       }
		
       // InitialContext of JNDI 
       InitialContext initialContext = new InitialContext();     
       // Getting Home Reference
       SetUpSessionHome gmsh=(SetUpSessionHome)initialContext.lookup("SetUpSessionBean"); 
       // Getting Remote Reference
       SetUpSession gms=(SetUpSession)gmsh.create();
	      
       // Getting Home Reference
       SetUpSessionHome gmsh1=(SetUpSessionHome)initialContext.lookup("SetUpSessionBean"); 
       // Getting Remote Reference
       SetUpSession gms1=(SetUpSession)gmsh1.create();
	
	   gatewayId = request.getParameter("gatewayId");
	   gatewayIds=gms.loadGatewayId();
	   boolean flag = true;
	   if(gatewayIds != null)
	   { // if begin
		for(int i = 0 ;i < gatewayIds.length ; i++)
		 {
			
		   if(gatewayIds[i] != null && gatewayIds[i].equals(gatewayId))
		   {
			 flag = false;
			 break;
		   }
		 }
	   } // if end
		//Logger.info(FILE_NAME,"Flaggg:	"+flag);
	   if(flag == true)
	   {	 
		 String errorMessage = "Record does not exist with GatewayId : "+gatewayId;
		 session.setAttribute("Operation", operation);
		 session.setAttribute("ErrorCode","RNF");
		 session.setAttribute("ErrorMessage",errorMessage);
		 session.setAttribute("NextNavigation","ETCGatewayRegistrationEnterId.jsp");  
%>
           <jsp:forward page="../ESupplyErrorPage.jsp"/> 
<%
       }	 				
       else
	   {

	//	String whereClause	   = "WHERE OPER_ADMIN_FLAG = 'O'  "; //AND SHIPMENTMODE  IN (1,3,5,7) 
	    gatewayJSP   = gms.viewGatewayDB(gatewayId);
     // terminalId   = gms1.getTerminalIds(whereClause);		//util
	   // terminals = gms1.getTerminalIds("","","1","O","");  //WHERE OPER_ADMIN_FLAG = 'O'  AND SHIPMENTMODE IN (1,3,5,7) ;
	    terminalId	= gms1.getTerminalIds("","","1","O","");	
	    terminalIdDB	= gms.getTerminalIds(gatewayId);	

        locationId   = gms1.getLocationIds("",operation,loginbean.getTerminalId(),"");		//util
        countryIds   = gms1.getCountryIds("",loginbean.getTerminalId(),operation);			//util
        locationName = new String[0];//gms.loadLocationName();
        gatewayType=gatewayJSP.getTypeofGateway();

	    companyName =  gatewayJSP.getCompanyName();
	    gatewayName=gatewayJSP.getGatewayName();
	    if(gatewayId != null)
	    { 
         if(gatewayId.length() > 3)
          locId=gatewayId.substring(0,3);
	    }
        contactName=gatewayJSP.getContactName();

        notes	=gatewayJSP.getNotes();
		timeZone=gatewayJSP.getTimeZone();
        if(notes==null)
         {
          notes="";
         } 
        address =(com.foursoft.etrans.common.bean.Address)gatewayJSP.getAddress(); 
        addressLine1=address.getAddressLine1();
        addressLine2=address.getAddressLine2();
        if(addressLine2 ==null || addressLine2.equals("NA"))
        {
         addressLine2="";
        } 

        city=address.getCity();
        state=address.getState();
        if(state == null || state.equals("NA"))
        {
         state="";
        }

        zipCode=address.getZipCode();
        if(zipCode == null || zipCode.equals("NA"))
        {
         zipCode="";
        }
  
        countryId=address.getCountryId();

        phoneNo=address.getPhoneNo();

        if(phoneNo == null || phoneNo.equals("NA"))
        {
          phoneNo="";
        }
        emailId=address.getEmailId();
        if(emailId == null || emailId.equals("NA"))
        {
          emailId="";
        } 
	    if(emailId == null )
        {
          emailId="";
        } 
        fax=address.getFax();

        if(fax == null || fax.equals("NA"))
        {
          fax="";
        } 
        terminals=gatewayJSP.getTerminals();
%>       
<html>

<title>Gateway Registration</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<head>
<script>
<%    locationId = new ArrayList(); %>
      locationName = new Array();
	  var countryId = new Array();
<%    countryId    = new String() ;  %>
      locId = "";
<%
    if(countryIds != null)
    {	
     for(int i=0;i<countryIds.size();i++)
     {
%>
      countryId[<%= i %>] = '<%= countryIds.get(i) %>'
<%
     }
    } 
%>
function setLocationName()
   { <!-- setLocationName() begin -->
     flag = true;
     locId = document.forms[0].locationId.value;
<%
     if(locationId != null)
      {	
       for(int i=0;i<locationId.size();i++)
       {
%>
        locationId[<%= i %>] = '<%= locationId.get(i) %>'
        locationName[<%= i %>] = '<%= locationName[i] %>'
<%	
       }
      } 
%>
      for(j=0;j<locationId.length;j++)
       {
        if(locationId[j]==locId)
         {
            document.forms[0].gatewayName.value=locationName[j];
            document.forms[0].gatewayId.value=locId+'G';
            flag=true;
            break;
         }
        else
		 {
            flag=false;
         }
       }
      if(flag==false)
       {
        alert("Please enter correct LocationId");
        document.forms[0].locationId.focus();
       } 
   } <!-- setLocationName() begin -->
		
		
function fillFields()
  {
   if( document.forms.length > 0 ) 
    {
	  var field = document.forms[0];
	  for(i = 0; i < field.length; i++) 
	  {
		if( (field.elements[i].type == "text") || (field.elements[i].type == "textarea") )		
		 {	       		       			
		   document.forms[0].elements[i].value = document.forms[0].elements[i].value.toUpperCase();
	     }
	  }
    }
           
           
      var flag=true;
      dest=0;
      if(document.forms[0].locationId.value.length==0)
       {
        flag=false;
        alert("Please select LocationId");
       }
      else if(document.forms[0].gatewayType.value == 0)
       {
        flag=false;
        alert("Please select Gateway Type");
       }
	  else if(document.forms[0].companyName.value.length == 0)
       {
        flag=false;
        alert("Please enter Company Name");
        document.forms[0].companyName.focus();
       }
      else if(document.forms[0].contactName.value.length==0)
       {
        flag=false;
        alert("Please enter Contact Name");
        document.forms[0].contactName.focus();
       } 
      else if(document.forms[0].addressLine1.value.length==0)
       {
        flag=false;
        alert("Please enter Address");
        document.forms[0].addressLine1.focus();
       }
      else if(document.forms[0].city.value.length==0)
       {
        flag=false;
        alert("Please enter City");
        document.forms[0].city.focus();
       } 
      else if(document.forms[0].countryId.value.length==0)
       {
        flag=false;
        alert("Please enter CountryId");
        document.forms[0].countryId.focus();
       }
      else if(document.forms[0].terminalSelected.length == 0)
       {
        flag=false;
        alert("Please select Terminals");
       }
      else if(checkCountryId() == false)
	   {
		flag=false;
       }   
      if(document.forms[0].notes.value.length > 400 )
	   {
		var temp = document.forms[0].notes.value.substring(0,398);
		document.forms[0].notes.value = temp;
	   }
           
       dest = document.forms[0].terminalSelected.options.length;
       destList = new Array();
       for(i=0;i<dest;i++)
       {
        destList[i] = document.forms[0].terminalSelected.options[i].value;
       }
       document.forms[0].terminalhide.value=destList.toString();
	   if (document.forms[0].emailId.value.length>0)
		if(!ValidateForm())
           return false;
       return flag; 
    }
function showLOV()
    {
      var Url      = 'ETCLOVTerminalIds.jsp?searchString='+document.forms[0].terminalId.value.toUpperCase();
      var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
      var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
      var Features = Bars+''+Options; 
      var Win     =  open(Url,'Doc',Features);
      return false;
    }
function setCountryId()
    {
      var Url      = 'ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
      var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
      var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
      var Features = Bars+''+Options; 
      var Win     =  open(Url,'Doc',Features);
      return false;
    }
function setTerminal()
    { <!-- setTerminal() begin -->
      	destList        = window.document.forms[0].terminalSelected;
      	srcList         = window.document.forms[0].terminalFromDB;
      	var len         = destList.length;
      	for(var i=0;i<srcList.length;i++)
       	{  <!-- for begin -->
        	if((srcList.options[i]!=null) && (srcList.options[i].selected))
         	{ <!-- if begin -->
          		var found=false;  
          		for(var count=0;count<len;count++)
          		{ <!-- for loop -->
           			if(destList.options[count] !=null)
            		{
             			if(srcList.options[i].text == destList.options[count].text)
              			{
               				found=true;
               				break;
              			}
            		}
          		} <!-- for loop -->
         		if(found!=true)
          		{
           			destList.options[len] = new Option(srcList.options[i].text,srcList.options[i].value);
           			len++;
          		}
         	} <!-- if end -->
        } <!-- for end -->
    } <!-- setTerminal() end -->
          
function removeTerminal()
   {
     	var destList = window.document.forms[0].terminalSelected;
     	var len = destList.length;
     	for(var i=(len-1);i>=0;i--)
      	{
       		if((destList.options[i] !=null) && (destList.options[i].selected==true))
        	{
          		destList.options[i]  = null;
        	}
      	} 
   }
          
function populateLocation()
    {
      	var Url      = 'ETCLOVTerminalIds.jsp';
      	var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
      	var Options  = 'scrollbars=yes,width=200,height=280,resizable=no';
      	var Features = Bars+''+Options; 
      	var Win     =  open(Url,'Doc',Features);
      	return false;
    }
       
function passTerminalId()
    {
            
    }
          
function setFields()
    {
            
	  var gatewayType = '<%= gatewayType%>'
      
	  document.forms[0].gatewayType.value = gatewayType;

	  if(gatewayType == '1' || gatewayType == '3' ||gatewayType == '5' ||gatewayType == '7')
	  {
		document.forms[0].Air.checked = true;
		document.forms[0].Air.disabled = true;
	  }

	  if(gatewayType == '2' || gatewayType == '3' ||gatewayType == '6' ||gatewayType == '7')
	  {
		document.forms[0].Sea.checked = true;
		document.forms[0].Sea.disabled = true;
	  }

	  if(gatewayType == '4' || gatewayType == '5' ||gatewayType == '6' ||gatewayType == '7')
	  {
		document.forms[0].Truck.checked = true;
		document.forms[0].Truck.disabled = true;
	  }

          
<%
     if(terminalId != null)
      {	
       for(int i=0;i<terminalIdDB.size();i++)
       {
%>
        document.GatewayMaster.terminalSelected.options[<%= i %>] = new Option('<%= terminalIdDB.get(i) %>','<%= terminalIdDB.get(i) %>');
<%
       }
      } 
%>

	var timeZone 		= '<%=timeZone%>';
	for(var i=0;i<document.forms[0].timeZone.length;i++)
	{
		if(	timeZone == document.forms[0].timeZone.options[i].value)
		{
			document.forms[0].timeZone.selectedIndex=i;
			break;
		}
	}


    }  <!-- setFields() end -->
function setAllCaps(input)
    {
      input.value=input.value.toUpperCase();
    }
       
function checkCountryId()
    {
     	document.forms[0].countryId.value=document.forms[0].countryId.value.toUpperCase();
      	var country = document.forms[0].countryId.value;
      	if(country.length!=0)
       	{
	        for(j=0;j<countryId.length;j++)
    	    {
        		if(countryId[j].substring(countryId[j].indexOf("[")+1,countryId[j].indexOf("]"))==country)
          		{
           			return true;
          		} 
            }
	    		alert("Please enter correct CountryId");
        		document.forms[0].countryId.focus();
        		document.forms[0].countryId.value="";
				return false;
       } 
   }
function getKeyCode()
   {
   		if(event.keyCode == 96 || event.keyCode == 59 || event.keyCode == 39 || event.keyCode == 34)
	 	{
	  		return false;
     	}
	  		return   true  	 
   }	
  
function setGatewayType(obj)
{
  var objName = obj.name;
  var objValue = obj.value;
  var gatewayTypeValue = document.forms[0].gatewayType.value;
  if(obj.checked)
	document.forms[0].gatewayType.value = parseInt(gatewayTypeValue) + parseInt(obj.value);
  else
	document.forms[0].gatewayType.value = parseInt(gatewayTypeValue) - parseInt(obj.value);

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
</script>
</head>
<body onLoad="setFields()" >
<%
  if(operation != null && operation.equalsIgnoreCase("Modify"))
  {
%>	
    <form name="GatewayMaster" method="post" action='<%=actionValue%>' onSubmit="return fillFields()">
<%
  }
  else if(operation != null && (operation.equalsIgnoreCase("Delete") || operation.equalsIgnoreCase("View") ))
  {
  }
%>  
<form  name =GatewayMaster method="post" action='<%=actionValue%>' >
<table width="800" border="0" cellspacing="0" cellpadding="0">
	<tr valign="top" bgcolor="#FFFFFF"> 
    <td>
<table border="0" width="800" cellpadding="4" cellspacing="1">
	<tr valign="top" class='formlabel'> 
    <td colspan="4"><table width="790" border="0" ><tr class='formlabel'><td>Gateway - <%=operation%> </td><td align=right><%=loginbean.generateUniqueId("ETCGatewayRegistrationDetail.jsp",operation)%></td></tr></table> </td>
    </tr></table>
	<table border="0" width="800" cellpadding="4" cellspacing="1">
	<tr valign="top" class='formdata'><td colspan="4">&nbsp;</td></tr>
    <tr valign="top" class='formdata'> 
    <td colspan="2" width="50%">Location Id :<font color="#FF0000">*</font><br>
    <input type='text' class='text' name="locationId" size="5" readonly maxlength="3" value='<%= locId %>'>
    <td colspan="2"  width="50%">Gateway Name:<br>
	<input type='text' class='text' name="gatewayName"  size="50" readonly value='<%= gatewayName %>'></td>
	</tr>
    <tr valign="top" class='formdata'> 
    <td colspan="2" width="50%">Gateway Id:<br>
	<input type='text' class='text' name="gatewayId" size="20" readonly value='<%= gatewayId %>'></td>
	<td colspan="2" width="50%">Gateway Type:<br>
                <input type="checkbox" name="Air" value="1" onClick="setGatewayType(this)">  <font face="Verdana" size="2">Air</font>
				  <input type="checkbox" name="Sea" value="2" onClick="setGatewayType(this)">  <font face="Verdana" size="2">Sea
                <input type="checkbox" name="Truck" value="4" onClick="setGatewayType(this)"> Truck</font>
					 <input type="hidden" name="gatewayType" value = "0">
	</td>
	</tr>
    <tr valign="top" class='formdata'> 
    <td colspan="2"  width="50%">Company Name:<font color="#FF0000">*</font><br>
	<input type='text' class='text' name="companyName" size="40" onBlur="setAllCaps(companyName)" maxlength="50" onKeyPress="return getKeyCode()" value='<%= companyName %>' <%=readOnly%>>
    <font color="#FF0000"></font><br><br>
        Time Zone:<br>
           <select size="1" name="timeZone" class='select'>
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
			<option value="Australia/Broken_Hill">(GMT+10:30) Central Summer Time(New South Wales)
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
	<td colspan="2" width="339">Notes:<br>
	<textarea rows="4" class='select' name="notes" cols="40" onKeyPress="return getKeyCode()" onBlur="setAllCaps(notes)" <%=readOnly%>><%=notes%></textarea></td>
    </tr>
</table>
    <br>
<table border="0" width="800" cellpadding="4" cellspacing="1">
	<tr valign="top" class='formdata'> 
	<td colspan="2" rowspan="2" width="50%">Address:<font color="#FF0000">*</font><br>
    <input type='text' class='text' name="addressLine1" size="50" onBlur="setAllCaps(addressLine1)" maxlength="65" onKeyPress="return getKeyCode()" <%=readOnly%> value='<%= addressLine1 %>'>
    <br>
    <input type='text' class='text' name="addressLine2" size="50" onBlur="setAllCaps(addressLine2)" maxlength="65" onKeyPress="return getKeyCode()" <%=readOnly%> value='<%= addressLine2 %>'></td>
	<td colspan="2" width="50%">Contact Name:<font color="#FF0000">*</font><br>
      <input type='text' class='text' name="contactName" size="40" onBlur="setAllCaps(contactName)" maxlength="50" onKeyPress="return getKeyCode()"  <%=readOnly%> value='<%= contactName %>'></td>
    </tr>
    <tr class='formdata'> 
	<td colspan="2" width="50%">Zip or Postal Code:<br>
      <input type='text' class='text' name="zipCode" size="15" maxlength="10" onBlur="setAllCaps(zipCode)" onKeyPress="return getKeyCode()" <%=readOnly%> value='<%= zipCode %>'></td>
	</tr>
    <tr valign="top" class='formdata'> 
    <td colspan="2" width="50%">City:<font color="#FF0000">*</font><br>
    <input type='text' class='text' name="city" size="30" onBlur="setAllCaps(city)" maxlength="30" onKeyPress="return getKeyCode()" <%=readOnly%> value='<%= city %>'></td>
	<td colspan="2" width="50%">Contact No:<br>
      <input type='text' class='text' name="phoneNo" size="20" maxlength="15" onKeyPress="return getKeyCode()" <%=readOnly%> value='<%= phoneNo %>'></td>
    </tr>
    <tr valign="top" class='formdata'> 
    <td colspan="2" width="50%">State or Province:<br>
    <input type='text' class='text' name="state" size="30" onBlur="setAllCaps(state)" maxlength="30" onKeyPress="return getKeyCode()" <%=readOnly%> value='<%= state %>'></td>
	<td colspan="2" width="50%">Fax No: <br>
      <input type='text' class='text' name="fax" size="20" maxlength="15" onKeyPress="return getKeyCode()" <%=readOnly%> value='<%= fax %>'></td>
    </tr>
    <tr valign="top" class='formdata'> 
    <td colspan="2" width="50%">Country:<font color="#FF0000">*</font><br>
    <input type='text' class='text' name="countryId" size="4" onBlur="setAllCaps(this)" maxlength="2"  onKeyPress="return getKeyCode()" <%=readOnly%> value='<%= address.getCountryId() %>'>
    <input type="button" class='input' value="..." name="LOVCountry" onClick="return setCountryId()"  <%=dsbl%>></td>
	<td colspan="2" width="50%">Email Id:<br><input type='text' class='text' name="emailId" size="20" maxlength="50" onBlur="setAllCaps(state)"  onKeyPress="return getKeyCode()" <%=readOnly%> value='<%= emailId %>'></td>
    </tr>
</table>
	
<table border="0" width="800" cellpadding="2" >
  <tr class='formheader'>	
	<td  width='14%'colspan="3">Terminals</td>    
	</tr>
    <tr class='formdata'> 
	<td  width="50%" align="right"> 
    <select size="10" name="terminalFromDB" multiple class='select'>
<%
	if(terminalId != null)
	{				  
      for(int j=0;j<terminalId.size();j++)
      {
%> 
         <option value=<%= terminalId.get(j) %>><%= terminalId.get(j) %></option>
<%				  
     }
   }  
%> 
	</select>
    </td>
    <td width="14%"  align="center"><br><br><br> 
    <input type="button"  class='input' value="  &gt;&gt;  " size="20" name="B3" onClick="setTerminal()"  <%=dsbl%>>
    <p align="center"> 
    <input type="button" class='input' value="  &lt;&lt;  " size="20" name="B3" onClick="removeTerminal()" <%=dsbl%>>
    </p></td>
    <td width="46%"> 
    <select size="10" name="terminalSelected" multiple class='select'></select></td>
	</tr>
    <input type="hidden" name="terminalhide">
    <input type="hidden" name="length' value='<%= terminalId.size() %>'&gt;&lt;tr&gt;&lt;td width="15%" valign="bottom" align="left">
    <tr class='denotes'> 
    <td> 
	<font color="#FF0000">*</font>Denotes mandatory</td>
	<td></td>
	<td align="right"> 
<%
  	if(operation != null && operation.equalsIgnoreCase("Modify"))
  	{
%>	<!-- Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5531 on 20050411.-->	  
    <input type="submit" value="Submit" name="submit" class='input' onClick='setFields()'>
	<input type="reset" name="Reset" value="Reset" class='input'>
    
<%
  	}
  	else if(operation != null && operation.equalsIgnoreCase("Delete"))
  	{
%>	
	<input type="submit" value="Delete" name="submit" class='input'>
<%
  	}				  	
  	else if(operation != null && operation.equalsIgnoreCase("View"))
  	{
%>	
    <input type="submit" value="Continue" name="submit" class='input'>   
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
<%
    } // else block          
   }
   catch(Exception e)
   {
	  //Logger.error(FILE_NAME,"Error in GatewayRegistrationDetail.jsp file : ", e.toString());
    logger.error(FILE_NAME+"Error in GatewayRegistrationDetail.jsp file : "+ e.toString());
	  e.printStackTrace();
	  session.setAttribute("Operation", operation);
	  session.setAttribute("ErrorCode","ERR");
	  session.setAttribute("ErrorMessage","Error while reteriving data with GatewayId : "+request.getParameter("gatewayId"));
	  session.setAttribute("NextNavigation","ETCGatewayRegistrationEnterId.jsp");
%>
      <jsp:forward page="../ESupplyErrorPage.jsp"/> 
<%
    }
%>	  
