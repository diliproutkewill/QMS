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
*	Programme Name	:ETCHOCompanyRegistrationAdd.jsp
*	Module    Name  :ETrans   
*	Task			:CompanyRegistration. 
*	Sub Task		:To Register the company alongwith it's headoffice.
*	Author Name		:Raghavender.G
*   Date Started    :
*   Date Completed  :
*   Date Modified   :Sept 12,2001.By Ratan K.M.
*	Description     :
*   Method's Summary:
*
* This file is invoked when user selects Add module in HO Registration. 
* User has to enter all the details about company like company name, contact person and company address.
*
* @version 	1.00 19 01 2001
* @author 	Raghavender.G
*/
%>
<%@ page import	=	"javax.naming.InitialContext,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>
					
<jsp:useBean id= "loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCHOCompanyRegistrationAdd.jsp ";
%>


<%
  logger  = Logger.getLogger(FILE_NAME);	
	/*ArrayList countryIds = null;
	ArrayList currencyIds=null;*/
	int len = 0;
try
{
	//Logger.info(FILE_NAME,"term..."+loginbean.getTerminalId());
	if(loginbean.getTerminalId()== null) 
	{
%>		
   <jsp:forward page="../ESupplyLogin.jsp" />
 <%
	}
  else
  {	
/*
	Creating InitialContext object to look up the "ETransHOSuperSessionBean".
*/	    	
    try
    {
             String operation  = request.getParameter("Operation");//added by rk
	         InitialContext initial = new InitialContext();
			 SetUpSessionHome 	ETransUtilitiesHome	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
	 	     SetUpSession 		ETransUtilitiesRemote	=	(SetUpSession)ETransUtilitiesHome.create();
             /*countryIds = ETransUtilitiesRemote.getCountryIds("",loginbean.getTerminalId(),operation);//added by rk
	         currencyIds=ETransUtilitiesRemote.getCurrencyIds("");*/
			 
			 /*if(countryIds!= null) 
	   	     	len = countryIds.size();*/
					
	}
  catch(Exception ee)
  {
   //Logger.error(FILE_NAME,"Exception in countryId of HOCompanyRegistrationAdd", ee.toString());
   logger.error(FILE_NAME+"Exception in countryId of HOCompanyRegistrationAdd"+ ee.toString());
  }
%>
<html>
<head>
<title>HOCompanyRegistrationAdd</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script language="JavaScript">
//@@Commented by Yuvraj.
/*function setCurrency()
{ 
 var country_arr  = new Array(<%=len%>);   
 var currency_arr = new Array(<%=len%>);
<%
	//for (int i=0;i<len;i++)
	//{
%>
	//country_arr[<%//=i%>]   = "<%//=(String)countryIds.get(i)%>";
	//currency_arr[<%//=i%>]  = "<%//=(String)currencyIds.get(i)%>";
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
}*/
//@@Added by Yuvraj for Currency Id AJAX Implementation.
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
//@@Yuvraj

function showCountryLOV()
{
	var Url='ETCLOVCountryAndCurrencyIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=No';
	var Features=Bars+' '+Options;
    var Win=open(Url,'Doc',Features);
}

function placeFocus() 
	{
	   document.forms[0].companyName.focus();
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

//@@ Added By Ravi Kumar to resolve the Issue SPETI-5645 on 16-05-05
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
//@@ SPETI-5645

function Mandatory()
{
	//upperCase();
	for(i=0;i<document.forms[0].elements.length;i++)
    {
	   if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
	   document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
	   
    }				
	
    companyName			=  document.forms[0].companyName.value;
    companyId			=  document.forms[0].companyId.value;
    addressLine1	    =  document.forms[0].addressLine1.value; 
   	city				=  document.forms[0].city.value;
	countryId			=  document.forms[0].countryId.value;
	currencyId			=  document.forms[0].currencyId.value;
	opEmailId			=  document.forms[0].opEmailId.value;
	contactName			=  document.forms[0].contactName.value;	

    if(companyName.length==0)
	{
        alert("Please enter Company Name ");
		document.forms[0].companyName.focus();
		return false;
	}	
	
    if(companyId.length ==0)
	{
		alert("Please enter Abbr Name");
		document.forms[0].companyId.focus();
		return false;
	}
	else if(companyId.length <3)
	{
		alert("Please enter three characters for Abbr Name");
		document.forms[0].companyId.focus();
		return false;
	}
	
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
	else if(countryId.length <2 )
	{
	    alert("Please enter two characters for Country Id");
		document.forms[0].countryId.focus();
		return false;
	}	
  /*else
		setCurrency(document.forms[0].countryId);
	if(!setCurrency(document.forms[0].countryId))
	{
		alert("Please enter correct Country Id ");
		document.forms[0].countryId.focus();
		return false;
	}*/
	if(currencyId.length==0)
	{
		alert("Please Select the Country Id from the LOV.");
		document.forms[0].countryId.focus();
		return false;
	}
	 if(opEmailId.length==0)
	{
		alert("Please enter Operations EmailId ");
		document.forms[0].opEmailId.focus();
		return false;
	}
//@@ Added By Ravi Kumar to Resolve the Issue SPETI-5645 on 16-05-05
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
//@@ SPETI-5645
	document.forms[0].submit.disabled='true';
	return true;	
}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>

	<body  onLoad = "placeFocus()" >
     <form method="POST"  onSubmit="return Mandatory()"  action="ETCHOCompanyRegistrationProcess.jsp" name="companyreg">
		
	<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" > 
    <td bgcolor="ffffff">
      
      <table width="800" border="0" cellspacing="1" cellpadding="4" >
        <tr class='formlabel'> 
          <td><table width="790" border="0" ><tr class='formlabel'><td>&nbsp;Company 
             - Add</td><td align=right><%=loginbean.generateUniqueId("ETCHOCompanyRegistrationAdd.jsp","Add")%></td></tr></table></td>
        </tr></table>
        
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="4">&nbsp;</td></tr>
                <tr class='formdata'> 
                  <td colspan="2">Company Name:<font color="#FF0000">*</font>
				  <br>
                       <input type='text' class='text' name="companyName" size="40" maxlength="50" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(companyName)">
                 </td>
                  <td colspan="2" width="340" >Abbr 
                    Name:<font color="#FF0000">*</font><br>
                                      <input type='text' class='text' name="companyId" size="5" maxlength="3" onBlur="upperCase(companyId)" onkeyPress="return getKeyCode(companyId)">
                    </td>
                </tr>
				  <tr class='formdata'> 
                  <td colspan="2">IATA Code:
				  <br>
                       <input type='text' class='text' name="iataCode" size="20" maxlength="16" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(this)">
                 </td>
<!-- @@ Added By Ravi Kumar to Resolve the Issue SPETI-4168 on 30-04-2005 -->
				  <td colspan="2">EIN #:
				  <br>
                       <input type='text' class='text' name="companyEIN" size="20" maxlength="16" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(this)" value ="" >
                 </td>
                </tr>
			  <tr class='formdata'> 
                  <td colspan="2" >Contact Person Name (First Name/Last Name):<font color="#FF0000">*</font><br>
<!--                            <input type='text' class='text' name="contactName" size="50"  maxlength="50" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(companyName)" value =""   >  -->
						<input type='text' class='text' name='contactName' value='' size='23' maxlength='30'  onkeyPress="return getSpecialCode(this)" onBlur = "upperCase(this)" >

						<input type='text' class='text' name='contactLastName' value='' size='24' maxlength='30'  onkeyPress="return getSpecialCode(this)" onBlur = "upperCase(this)" ></td>
                   </td>
<!-- @@ 30-04-2005 -->
						<td colspan="2"  width="340"  >
						
						Designation:<br>
						 <input type='text' class='text' name="designation" size="50"  maxlength="50" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(this)">
						
                    </td>
                </tr>
          </table>
      <table border="0" width="800" cellpadding="4" cellspacing="1">
                <tr valign="top" class='formdata'> 
                  <td colspan="2" rowspan="2">Address:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="addressLine1" size="50" maxlength="65" onBlur="upperCase(addressLine1)" onKeypress="return getSpecialCode(addressLine1)">
                    <br>
                    <input type='text' class='text' name="addressLine2" size="50" maxlength="65" onBlur="upperCase(addressLine2)" onKeypress="return getSpecialCode(addressLine2)">
                    </td>
                  <td colspan="2" width="339">Zip 
                    or Postal Code:<br>
                    <input type='text' class='text' name="zipCode" size="15" maxlength="10" onBlur="upperCase(zipCode)" onkeyPress="return getSpecialCode(zipCode)">
                    </td>
                </tr>
                <tr class='formdata'> 
                  <td colspan="2"  width="339">Contact 
                    No:<br>
                    <input type='text' class='text' name="phoneNo" size="20" maxlength="15" onkeyPress="return getSpecialCode(phoneNo)" onBlur="upperCase(this)">
                    </td>
                </tr>
                <tr valign="top" class='formdata'> 
                  <td colspan="2" >City:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="city" size="30" maxlength="30" onBlur="upperCase(city)" onkeyPress="return getSpecialCode(city)">
                    </td>
                  <td colspan="2" width="339">Fax 
                    No:<br>
                    <input type='text' class='text' name="fax" size="20" maxlength="15" onBlur="upperCase(this)"onkeyPress="return getSpecialCode(fax)">
                    </td>
                </tr>
                <tr valign="top" class='formdata'> 
                  <td colspan="2" >State 
                    or Province:<br>
                    <input type='text' class='text' name="state" size="30" maxlength="30" onBlur="upperCase(state)" onkeyPress="return getSpecialCode(state)">
                   </td>
                  <td colspan="2" >Country Id :<font color="#FF0000">*</font>&nbsp;&nbsp;&nbsp;&nbsp;Currency:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="countryId" size="5" maxlength="2" onBlur="upperCase(countryId);request('ETCCurrency.jsp?countryId='+this.value);" onkeyPress="return getKeyCode(countryId)">
                    <input type="button" class='input' value="..." name="bt_country" onClick="showCountryLOV()">
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type='text' class='text' name="currencyId" size="5" maxlength="3" readOnly>
					</td>
                </tr>
              <tr valign="top" class='formdata'> 
                  <td colspan="2" >Operations Email Id:<font color="#FF0000">*</font>
                    <br>
                    <input type='text' class='text' name="opEmailId" size="50" maxlength="50" onBlur="upperCase(this)" onkeyPress="return getSpecialCode(this)">
                    </td>
                  <td colspan="2" width="339">Email 
                    Id:<br>
                    <input type='text' class='text' name="emailId" size="50" maxlength="50" onBlur="upperCase(emailId)" onkeyPress="return getSpecialCode(emailId)" >
                   </td>
                </tr>

              </table>
                        <table border="0" width="800" cellpadding="4" cellspacing="1" >
                
              <!--  <tr class='formdata'> 
                  <td colspan="2">Time 
                    Zone:<br>
                    <select size="1" name="timeZone" class='select'>
                      <option>(GMT +05:30)Bombay,Calcutta, Madras, New Delhi</option>
                      <option>(GMT -12:00)Eniwetok,Kwajalein</option>
                      <option>(GMT -11:00)Midway Island,Samoa</option>
                      <option>(GMT -10:00)Hawaii</option>
                      <option>(GMT -09:00)Alaska</option>
                      <option>(GMT -08:00)Pascfic Time(US & Canada);Tijuana</option>
                      <option>(GMT -07:00)Arizona</option>
                      <option>(GMT -07:00)Mountain Time(US &Canada)</option>
                      <option>(GMT -06:00)Central Time(US & Canada)</option>
                      <option>(GMT -06:00)Mexico City,Tegucigalpa</option>
                      <option>(GMT -06:00)Saskatchewan</option>
                      <option>(GMT -05:00)Bogota, Lima,Quito</option>
                      <option>(GMT -05:00)Eastern Time(US & Canada)</option>
                      <option>(GMT -05:00)Indiana(East)</option>
                      <option>(GMT -04:00)Atlantic Time(Canada)</option>
                      <option>(GMT -04:00)Caracas,La Paz</option>
                      <option>(GMT -04:00)Santiago</option>
                      <option>(GMT -03:30)Newfoundland</option>
                      <option>(GMT -03:00)Brasilia</option>
                      <option>(GMT -03:00)Buenos Aires, Georgetown</option>
                      <option>(GMT -02:00)Mid-Atlantic</option>
                      <option>(GMT -01:00)Azores,Cape Verde Is.</option>
                      <option>(GMT)Casablance , Monrovia </option>
                      <option>(GMT)GreenWich Mean Time :Dublin,Edinburgh,Lisbon,London</option>
                      <option>(GMT +01:00)Amsterdam, Berlin,Bern,Rome,Stockholm,Vienna</option>
                      <option>(GMT +01:00)Belgrade,Bratislava,Budapest,Ljublijana,Prague</option>
                      <option>(GMT +01:00)Brussels,Copenhagen,Madrid,Paris,Vilnius</option>
                      <option>(GMT +01:00)Sarajevo,Skopje,Sofija,Warsaw,Zagreb</option>
                      <option>(GMT +02:000)Athens,istanbul,Minsk</option>
                      <option>(GMT +02:00)Bucharest</option>
                      <option>(GMT +02:00)Cairo</option>
                      <option>(GMT +02:00)Harare,Pretoria</option>
                      <option>(GMT +02:00)Helsinki,Riga,Tallinn</option>
                      <option>(GMT +02:00)Jerusalem</option>
                      <option>(GMT +03:00)Baghdad,Kuwait,Riyadh</option>
                      <option>(GMT +03:00)Moscow,ST.Petersburg,Volgograd</option>
                      <option>(GMT +03:00)Nairobi</option>
                      <option>(GMT +03:30)Tehran</option>
                      <option>(GMT +04:00)Abu Dhabi,Muscat</option>
                      <option>(GMT +04:00)Baku,Tbilisi</option>
                      <option>(GMT +04:30)Kabul</option>
                      <option>(GMT +05:00)Ekaterinburg</option>
                      <option>(GMT +05:00)Islamabad,Karachi,Tashkent</option>
                      <option>(GMT +06:00)Astana,Almaty,Dhaka</option>
                      <option>(GMT +06:00)Colombo</option>
                      <option>(GMT +07:00)Bangkok,Hanoi,Jakarta</option>
                      <option>(GMT +08:00)Beijing,Chongqing,Hong Kong,Urumqi</option>
                      <option>(GMT +08:00)Perth</option>
                      <option>(GMT +08:00)Singapore</option>
                      <option>(GMT +08:00)Taipei</option>
                      <option>(GMT +09:00)Osaka,Sappora,Tokyo</option>
                      <option>(GMT +09:00)Seoul</option>
                      <option>(GMT +09:00)Yakutsk</option>
                      <option>(GMT +09:30)Adelaide</option>
                      <option>(GMT +09:30)Darwin</option>
                      <option>(GMT +10:00)Brisbane</option>
                      <option>(GMT +10:00)Canberra,Melbourne,Sydney</option>
                      <option>(GMT +10:00)Guam,Port Moresby</option>
                      <option>(GMT +10:00)Hobart</option>
                      <option>(GMT +10:00)Vladivostok</option>
                      <option>(GMT +11:00)Magadan,Soloman Is.,New Caledonia</option>
                      <option>(GMT +12:00)Auckland,Wellington</option>
                      <option>(GMT +12:00)Fiji,Kamchatka,Marshall Is.</option>
                    </select>
                  </td>
                  <td colspan="2" width="340" >
					Date 
                    Format:<br>
                    <select size="1" name="dateFormat" class='select'>
                      <option selected>dd/mm/yy</option>
                      <option>mm/dd/yy</option>
                      
                    </select>
                    </td>
                </tr>   -->  
				</table>
                        <table border="0" width="800" cellpadding="4" cellspacing="1" >
                <tr class='denotes'> 
                  <td  valign="top" >
				  <font color="#FF0000">*</font>Denotes 
                    Mandatory
					</td>
                  <td   width="340" valign="top" align="right">
					 <input type= hidden name=Operation value="Add" >
					  <input type="hidden" name="dayLightSavings" value="Y" >
                      <input type="submit" value="Submit" name="submit" class='input'>
					  <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'>
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
} 
catch(Exception e)
{
	//Logger.error(FILE_NAME,"Exception in HOCompanyRegistrationAdd.jsp:", e.toString());
  logger.error(FILE_NAME+"Exception in HOCompanyRegistrationAdd.jsp:"+ e.toString());
}
%>