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
/*
Programme Name	:ETCHOCompanyRegistrationView
Module    Name  :ETrans.
Task			:Company Registration
Sub Task		:To display all the details of the company.
Author Name		:Raghavender.G
Date Started    :
Date Completed  :
Date Modified   :Sept 12,2001.
Description     :
Method's Summary:
*/
%>   
<%@ page import	=	"javax.naming.InitialContext,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,					
					com.foursoft.etrans.setup.company.bean.HORegistrationJspBean,
					com.foursoft.etrans.common.bean.Address,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCHOCompanyRegistrationView.jsp ";
%>

<%
/*
* This file is invoked from ETCHOSuperEnterID.jsp
* It displays all the details of the company.
* @version 	1.00 19 01 2001.
*/
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList countryIds	= null;
	ArrayList currencyIds	= null;
	int len = 0;
	try
 	{
  		if(loginbean.getTerminalId()==null)
  		{
%>
  			<jsp:forward page="../ESupplyLogin.jsp"/>
<%
			return;
  		}
 
/* Creating home and remote references for the "ETransHOSuperSessionBean" and invoke getCountryId(),getCurrencyId()
   methods of this bean through the remote references.
*/	
   	InitialContext initial = new InitialContext();
    SetUpSessionHome home = ( SetUpSessionHome )initial.lookup("SetUpSessionBean");
	SetUpSession remote   = (SetUpSession)home.create();
    String operation       = null;
  	operation = request.getParameter("Operation");
    //countryIds = remote.getCountryIds("",loginbean.getTerminalId(),operation);//added by rk
	//currencyIds=remote.getCurrencyIds("");
		 /*if(countryIds !=null)
	       	len = countryIds.size();*/
  
    String companyMasterId = null;
    String companyName     = null;
    int  companyAddressId  = 0;
    String dateFormat      = null;
    String timeZone        = null;
    String currencyId       = null;
    String dayLightSavings = "N";
    int    addressId       = 0;
    String addressLine1    = null;
    String addressLine2    = null;
    String city            = null;
    String state           = null;
    String zipCode         = null;
    String countryId       = null;
    String phoneNo         = null;
    String fax             = null;
    String emailId         = null;
	String iataCode		   = null;
	String contactName	   = null;
//@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005  
	String contactLastName	   = null;
	String companyEIN	   = null;
//@@ 01-05-2005
	String designation	   = null;
	String opEmailId	   = null;
    
    String value           = null;
	String readOnly 	   = "";
	String submitValue     = null;
	String actionValue     = null;
	

  	//operation = request.getParameter("Operation");
	if (operation.equals("View"))
	{
		readOnly 	   = "readOnly";
	    submitValue     = "Continue";
		actionValue     = "ETCHOCompanyRegistrationEnterId.jsp";
	}
	else if(operation.equals("Modify"))
	{
			readOnly 	   = "";
			submitValue     = "Submit";
			actionValue     = "ETCHOCompanyRegistrationProcess.jsp";
	}
	else if(operation.equals("Delete"))
	{
			session.setAttribute("first","Second");
			readOnly 	   = "readOnly";
			submitValue     = "Delete";
			actionValue     = "ETCHOCompanyRegistrationProcess.jsp";
	}
	 	
	  	String companyId = request.getParameter("id").toUpperCase();
  	 // Context context = new InitialContext();
	  SetUpSessionHome slh = ( SetUpSessionHome )initial.lookup("SetUpSessionBean");
  	  com.qms.setup.ejb.sls.SetUpSession sl =
      (com.qms.setup.ejb.sls.SetUpSession)slh.create();
	  HORegistrationJspBean HORegistrationObj = sl.getHORegistrationDetails(companyId);
      if( HORegistrationObj != null)
      {
	      companyMasterId  = HORegistrationObj.getCompanyId();
	      companyName      = HORegistrationObj.getCompanyName();
  	      companyAddressId = HORegistrationObj.getAddressId();
		  dateFormat       = HORegistrationObj.getDateFormat();
		  iataCode  	   = HORegistrationObj.getIataCode();
	      contactName      = HORegistrationObj.getContactName();
//@@ Added By Ravi Kumar to Resolve the Issues SPETI-4168 & 4169 on 01-05-2005  
	      contactLastName      = HORegistrationObj.getContactLastName();
	      companyEIN      = HORegistrationObj.getCompanyEIN();
//@@ 01-05-2005  
  	      designation 	   = HORegistrationObj.getDesignation();
		  opEmailId		   = HORegistrationObj.getOpEmailId();
	      timeZone         = HORegistrationObj.getTimeZone();
	      currencyId        = HORegistrationObj.getHCurrency();
  	      dayLightSavings  = HORegistrationObj.getDayLightSavings();
	      Address address  = HORegistrationObj.getAddress();
	      addressId        = address.getAddressId();
	      addressLine1     = address.getAddressLine1();
  	      addressLine2     = address.getAddressLine2();
	      city             = address.getCity();
	      state            = address.getState();
	      zipCode          = address.getZipCode();
  	      countryId        = address.getCountryId();
	      phoneNo          = address.getPhoneNo();
	      emailId          = address.getEmailId();
	      fax              = address.getFax();
			if(iataCode == null)
			 iataCode ="";
			else if( iataCode.equalsIgnoreCase("null"))
			 iataCode = "";	
			if(designation == null)
			 designation = "";
			else if( designation.equalsIgnoreCase("null"))
			 designation = "";	 	 
			if(addressLine2 == null)
			{
				addressLine2 = "";
			}
			if(state == null)
			{
				state = "";
			}
			if(zipCode == null)
			{
				zipCode = "";
			}
			if(phoneNo == null)
			{
				phoneNo = "";
			}
			if(emailId == null)
			{
				emailId = "";
			}
			if(fax == null)
			{
				fax = "";
			}
            

%>
<html>
<head>
<title> HOCompany Registration </title>
</style>
<script language="JavaScript">
var country_arr  = new Array();   
 var currency_arr = new Array();

function showCountryLOV()
{
	var Url='ETCLOVCountryAndCurrencyIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=No';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
/*function setCurrency()
{
<%
 //if(operation.equals("Modify"))
// {
	//for (int i=0;i<len;i++)
	//{
%>
	country_arr[<%//=i%>]   = "<%//=(String)countryIds.get(i)%>";
	currency_arr[<%//=i%>]  = "<%//=(String)currencyIds.get(i)%>";
<%
	//}
%>
  	var count;
	for( count=0 ; count <  <%//=len%> ; count++)
	{
	    var sub = country_arr[count].substring(country_arr[count].indexOf("[")+1,country_arr[count].indexOf("]"))
		var sub1 = currency_arr[count].substring(currency_arr[count].indexOf("[")+1,currency_arr[count].indexOf("]"))
	  	if ( document.forms[0].countryId.value==sub)
	     { 
		   
			document.forms[0].currencyId.value=sub1; 
			return true;
		}
	}
	return false;
<%
// }
// %>	
 return true;
}*/

var req;
function request(url) {
        
        if (window.XMLHttpRequest) 
		{
            req = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req = new ActiveXObject("Microsoft.XMLHTTP");
        } 
		else 
		{
            return false;
        }

         if (!req) {
            return false;
        }
        req.open("GET", url, true);
		req.onreadystatechange = callback;
		req.send(null);

    }

    function callback() {
    if (req.readyState == 4) {
        if (req.status == 200) 
		{
			var response = req.responseText.toString();
			if(response.indexOf('QMS0')!=-1)
			{
				document.forms[0].currencyId.value = '';
			}
			else if(response.indexOf('QMS-1')!=-1 )
			{
				alert("An Error has Occurred While Fetching Currency Id. \nPlease Select Country Id from LOV.");
				return false;
			}
			else if(response.indexOf('null')!=-1)
			{
				alert("Please Enter a Valid Country Id or Select from the LOV");
				return false;
			}
			else if(response.indexOf('Session Expired')!=-1)
			{
				alert("Your Session has Expired!\nPossible Reasons:\n1. The idle time duration of your session might have exceeded. (or)\n2. Some other user might have logged in with same User Credentials.");
				return false;
			}
			else
				document.forms[0].currencyId.value = response;
        }
    }
}
function placeFocus() 
{
	var operation='<%= operation %>';
   if(operation==('View')|| operation==('Delete') )
	{
		document.forms[0].submit.focus();
		 
	}
	else
	{
		
  	document.forms[0].addressLine1.focus();
   }
}
	
function getKeyCode()
 {
  if(event.keyCode!==13)
    {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
    }
  return true;
 }

function getSpecialCode()
 {
  if(event.keyCode!==13)
    {
     if(event.keyCode==34 || event.keyCode==39 ||event.keyCode==59||event.keyCode==96||event.keyCode==126)
	 event.returnValue =false;
    }
  return true;
 } 

function upperCase(input)
 {
  input.value=input.value.toUpperCase();
 }

function Mandatory()
{
	for(i=0;i<document.forms[0].elements.length;i++)
    {
		
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
	   
    }				
    addressLine1	    =  document.forms[0].addressLine1.value; 
   	city				=  document.forms[0].city.value;
	countryId			=  document.forms[0].countryId.value;
	currencyId			=  document.forms[0].currencyId.value;
	opEmailId			=  document.forms[0].opEmailId.value;
	contactName			=  document.forms[0].contactName.value;	
	
	if(contactName.length ==0)
	{
		alert("Please Enter Contact Person's First Name");
		document.forms[0].contactName.focus();
		return false;
	}
     	
  if(addressLine1.length==0)
	{
		alert("Please enter Address");
		document.forms[0].addressLine1.focus();
		return false;
	}
   
    if(city.length==0)
	{
		alert("Please enter City ");
		document.forms[0].city.focus();
		return false;
	}
		 
	if (countryId.length ==0 )
	{
	    alert("Please enter Country Id");
		document.forms[0].countryId.focus();
		return false;
	}
	else if(countryId.length <2)
	{		
	    alert("Please enter two characters for Country Id");
		document.forms[0].countryId.focus();
		return false;
	}	
	 if(currencyId.length==0)
	{
		alert("Please Select the Country Id from the LOV.");
		document.forms[0].countryId.focus();
		return false;
	}  
  	/*else
		setCurrency(document.forms[0].countryId);
	if(!setCurrency(document.forms[0].countryId))
	{
		alert("Please enter correct Country Id ");
		document.forms[0].countryId.focus();
		return false
	}*/
	 if(opEmailId.length==0)
	{
		alert("Please enter Operations EmailId ");
		document.forms[0].opEmailId.focus();
		return false;
	}
	document.forms[0].submit.disabled='true';
	return true;	
}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body onLoad = "placeFocus()" >
     <form method="POST"  onSubmit="return Mandatory()"  action="<%=actionValue%>" name="companyreg">
	
	<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top"> 
    <td colspan="2" bgcolor="ffffff">
      
      <table width="800" border="0" cellspacing="1" cellpadding="4" >
        <tr class='formlabel'> 
          <td><table width="790" border="0" ><tr class='formlabel'><td>&nbsp;Company 
             - <%=operation%></td><td align=right><%=loginbean.generateUniqueId("ETCHOCompanyRegistrationView.jsp",operation)%></td></tr></table> </td>
        </tr></table>
        
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata' valign="top"><td colspan="4">&nbsp;</td></tr>
                <tr class='formdata'> 
                  <td colspan="2" >Company Name:<font color="#FF0000">*</font>
				 <br>
                       <input type='text' class='text' name="companyName" value ="<%=companyName%>" <%=readOnly%>  size="40" maxlength="50" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(companyName)">
                 </td>
                  <td colspan="2"  width="340" >Abbr 
                    Name:<font color="#FF0000">*</font><br>
                                      <input type='text' class='text' name="companyId"  value = "<%=companyMasterId%>" <%=readOnly%>  size="5" maxlength="3" onBlur="upperCase(companyId)" onkeyPress="return getKeyCode(companyId)">
                    </td>
                </tr>
				  <tr class='formdata'> 
                  <td colspan="2">IATA Code:
				  <br>
                       <input type='text' class='text' name="iataCode" size="20" maxlength="16" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(this)" value ="<%=iataCode==null?"":iataCode%>" <%=readOnly%> >
                 </td>
<!-- @@ Added By Ravi Kumar to Resolve the Issue SPETI-4168 on 30-04-2005 -->
				  <td colspan="2">EIN #:
				  <br>
                       <input type='text' class='text' name="companyEIN" size="20" maxlength="16" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(this)" value ="<%="null".equalsIgnoreCase(checkNULL(companyEIN))?"":checkNULL(companyEIN)%>" <%=readOnly%>>
                 </td>
<!-- 30-04-2005-->

                </tr>
			  <tr class='formdata'> 
<!-- @@ Modified By Ravi Kumar to Resolve the Issue SPETI-4168 on 30-04-2005 -->
				  <td colspan="2" >Contact Person Name (First Name/Last Name):<font color="#FF0000">*</font><br>
<!--                            <input type='text' class='text' name="contactName" size="50"  maxlength="50" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(companyName)" value ="<%=contactName%>"  <%=readOnly%> >  -->
						<input type='text' class='text' name='contactName' value="<%=checkNULL(contactName)%>" size='23' maxlength='30'  onkeyPress="return getSpecialCode(this)" onBlur = "upperCase(this)" <%=readOnly%>>

						<input type='text' class='text' name='contactLastName' value="<%="null".equalsIgnoreCase(checkNULL(contactLastName))?"":checkNULL(contactLastName)%>" size='24' maxlength='30'  onkeyPress="return getSpecialCode(this)" onBlur = "upperCase(this)" <%=readOnly%>></td>
                   </td>
<!-- @@ 30-04-2005 -->
						<td colspan="2" width="340" >
						
						Designation:<br>
						 <input type='text' class='text' name="designation" size="50"  maxlength="50" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(this)" value ="<%=designation%>"  <%=readOnly%> >
						 
                    </td>
                </tr>
          </table>
      <table border="0" width="800" cellpadding="4" cellspacing="1">
                <tr valign="top" class='formdata'> 
                  <td colspan="2"  rowspan="2">Address:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="addressLine1" size="50" maxlength="65"  value ="<%=addressLine1%>"  <%=readOnly%> onBlur="upperCase(addressLine1)" onKeypress="return getSpecialCode(addressLine1)">
                    <br>
                    <input type='text' class='text' name="addressLine2" size="50" maxlength="65"  value ="<%=addressLine2%>"  <%=readOnly%> onBlur="upperCase(addressLine2)" onKeypress="return getSpecialCode(addressLine2)">
                    </td>
                  <td colspan="2" width="339">Zip 
                    or Postal Code:<br>
                    <input type='text' class='text' name="zipCode" size="15" maxlength="10" value="<%=zipCode%>"  <%=readOnly%>  onBlur="upperCase(zipCode)" onkeyPress="return getSpecialCode(zipCode)">
                    </td>
                </tr>
                <tr class='formdata'> 
                  <td colspan="2" width="339">Contact 
                    No:<br>
                    <input type='text' class='text' name="phoneNo" size="20" maxlength="15" value="<%=phoneNo%>"  <%=readOnly%>  onkeyPress="return getSpecialCode(phoneNo)" onBlur="upperCase(this)">
                   </td>
                </tr>
                <tr valign="top" class='formdata'> 
                  <td colspan="2" >City:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="city" size="30" maxlength="30" value="<%=city%>"  <%=readOnly%>  onBlur="upperCase(city)" onkeyPress="return getSpecialCode(city)">
                    </td>
                  <td colspan="2"  width="339">Fax 
                    No:<br>
                    <input type='text' class='text' name="fax" size="20" maxlength="15" value="<%=fax%>"  <%=readOnly%> onBlur="upperCase(this)" onkeyPress="return getSpecialCode(fax)">
                   </td>
                </tr>
                <tr valign="top" class='formdata'> 
                  <td colspan="2" >State 
                    or Province:<br>
                    <input type='text' class='text' name="state" size="30" maxlength="30"  value="<%=state%>"  onBlur="upperCase(state)" <%=readOnly%> onkeyPress="return getSpecialCode(state)">
                    </td>
                  <td colspan="2"  width="339">Country Id :<font color="#FF0000">*</font>&nbsp;&nbsp;&nbsp;&nbsp;Currency:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="countryId" size="5" maxlength="2" value="<%=countryId%>" <%=readOnly%>  onBlur="upperCase(countryId);<%="Modify".equalsIgnoreCase(operation)?"request('ETCCurrency.jsp?countryId='+this.value)":""%>" onkeyPress="return getKeyCode(countryId)">
<%					
		if(operation.equals("Modify"))
		{
%>                          
					<input type="button" class='input' value="..." name="bt_country" onClick="showCountryLOV()">
<%
		}
%>
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='text' class='text' name="currencyId" size="5" maxlength="3" value="<%=currencyId%>" readOnly>
					</td>
                </tr>
              <tr valign="top" class='formdata'> 
                  <td colspan="2" >Operations Email Id:<font color="#FF0000">*</font>
                    <br>
                    <input type='text' class='text' name="opEmailId" size="50" maxlength="50" value="<%=opEmailId%>" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(this)" <%=readOnly%>>
                   </td>
                  <td colspan="2" width="339">Email Id:<br>
                    <input type='text' class='text' name="emailId" size="50" maxlength="50"  value="<%=emailId%>"  onBlur="upperCase(emailId)" onkeyPress="return getSpecialCode(emailId)" <%=readOnly%>>
                   </td>
                </tr>

              </table>
 
                        <table border="0" width="800" cellpadding="4" cellspacing="1" >
                <tr class='denotes'> 
                  <td  valign="top" >
				  <font color="#FF0000">*</font>Denotes 
                    Mandatory
					</td>
                  <td   width="340" valign="top" align="right">
					 <input type= hidden name=Operation value="<%=operation%>" >
					 <input type="hidden" name=addressId value=<%=companyAddressId %> >
					  <input type="hidden" name="dayLightSavings" value="Y" >
<%					
		if(operation.equals("View") || operation.equals("Delete"))
		{
%>                          
					<input type="submit" value="<%=submitValue%>" name="submit" class='input'>
					
<%
		}
		

	else
		  {
	%>
                      <input type="submit" value="<%=submitValue%>" name="submit" class='input'>
					  <input type="reset" value="Reset" name="reset" onClick="placeFocus()"  class='input' >
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
	}
	else
	{
			 session.setAttribute("Operation",operation); 
			 String errorMessage = "Record does not exist for this CompanyId  : "+companyId;
			 session.setAttribute("ErrorCode","RNF");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCHOCompanyRegistrationEnterId.jsp");
%>
     
      <jsp:forward page="../ESupplyErrorPage.jsp" />
<%		
	}	
  }
  catch( Exception nme )
  {
       //Logger.error(FILE_NAME,"Exception in HOCompanyRegistrationView.jsp : ", nme.toString());
       logger.error(FILE_NAME+"Exception in HOCompanyRegistrationView.jsp : "+ nme.toString());
  }
%> <!-- @@ Added by Ravi Kumar to Resolve AES/SED Related Issues on 02-05-2005-->
<%!
	private String checkNULL(String val)
	{
		return val == null ?"" : val;
	}
%>
<!-- 02-05-2005-->