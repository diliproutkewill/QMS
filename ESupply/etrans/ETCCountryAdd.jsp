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
/*	Programme Name : CountryAddJSP.
*	Module Name    : ETrans.
*	Task Name      : CountryMaster
*	Sub Task Name  : Adding the Country,s Name alongwith it's Id,currency and it's region of existance  like Europe,Asia etc..
*	Author		   : 
*	Date Started   :
*	Date Ended     : Sept 06, 2001.
*	Modified Date  : Sept 06, 2001(Ratan K.M.).
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList "%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
%>
<%
		 String FILE_NAME	=	"ETCCountryAdd.jsp";
     logger  = Logger.getLogger(FILE_NAME);
  try 			
  {

	 if(loginbean.getTerminalId() == null) //this is to verify whether we can get "terminalid" through the 
	 {											  //login bean object.		
%>
   <jsp:forward page="../ESupplyLogin.jsp" />
<%
	 }
	 else
	 {   
 %>
<html>
<head>
<title>Country Add </title>
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
<!--

function upperCase(input)
 {
  input.value=input.value.toUpperCase();
 }
function getKeyCode()
 {
  if(event.keyCode!=13)
    {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
    }
  return true;
 }
function getSpecialCode()
 {
	//  G.Srinivas Modified to avoid hyphen in country Names for QA-Issue No: SPETI-3903,3910,3911,3906.
	//alert(event.keyCode);
  if(event.keyCode!=13)
    {
      if((event.keyCode > 32 && event.keyCode < 40) || event.keyCode == 64   ||event.keyCode==96 || event.keyCode==126 || event.keyCode==45)
	 event.returnValue =false;
    }
  return true;
 }

//@@ Modifed By G.Srinivas on 20050523 for QA-Issue 1771-1774

function specialCharFilter(input,label) 
	{
		
		s = input.value;
		
		filteredValues = "''~!@#$%^&*()_-+=|\:;<>,.?"+'"';		
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++) 
		{  
			var c = s.charAt(i);
			if ( filteredValues.indexOf(c) == -1 ) 
					returnString += c.toUpperCase();
			else
			{
				alert("Please do not enter special characters "+label);
				input.value = "";
				input.focus();
				return false;
			}
		}
		return true;
	}
//@@ Modifed By G.Srinivas on 20050523 for QA-Issue 1771-1774 ends

function placeFocus() 
{
  document.forms[0].countryId.focus(); 
 }
function trim(inputString) {
  
   if (typeof inputString != "string") { return inputString; }
   var retValue = inputString;
   var ch = retValue.substring(0, 1);
   while (ch == " ") { // Check for spaces at the beginning of the string
      retValue = retValue.substring(1, retValue.length);
      ch = retValue.substring(0, 1);
   }
   ch = retValue.substring(retValue.length-1, retValue.length);
   while (ch == " ") { // Check for spaces at the end of the string
      retValue = retValue.substring(0, retValue.length-1);
      ch = retValue.substring(retValue.length-1, retValue.length);
   }
   while (retValue.indexOf("  ") != -1) { // Note that there are two spaces in the string - look for multiple spaces within the string
      retValue = retValue.substring(0, retValue.indexOf("  ")) + retValue.substring(retValue.indexOf("  ")+1, retValue.length); // Again, there are two spaces in each of the strings
   }
   return retValue; // Return the trimmed string back to the user
} // Ends the "trim" function

function Mandatory()
{
	for(i=0;i<document.forms[0].elements.length;i++)
    {
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		  document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }
	   
    countryId    =  trim(document.forms[0].countryId.value);
    countryName  =  document.forms[0].countryName.value;
    currencyId   =  document.forms[0].currencyId.value; 
    region       =  document.forms[0].region.selectedIndex;
    if(countryId.length ==0)
	{
	    alert(" Please enter Country Id");
		document.forms[0].countryId.focus();
		return false;
	}
	else if(countryId.length < 2)
	{
		 alert("Please enter two characters for Country Id")
         document.forms[0].countryId.value =  countryId
		 document.forms[0].countryId.focus();
		 return false;
	}	
    if(countryName.length == 0)
	{
		alert(" Please enter Country Name");
		document.forms[0].countryName.focus();
		return false;
	}
	if(currencyId.length== 0)
	{
		alert("Please enter Currency Id");
		document.forms[0].currencyId.focus();
		return false;
	}
	else if(currencyId.length <3)
	{ 
		alert("Please enter three characters for Currency Id");
		document.forms[0].currencyId.focus();
		return false;
	}
	if(region == 0)
	{
		alert("Please select a Region");
		document.forms[0].region.focus();
		return false;
	}
	document.forms[0].jbt_Test.disabled='true';				
  	return true;	
}	 
//-->
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body OnLoad="placeFocus()">
<form method="POST"  onSubmit="return Mandatory()" action="ETCCountryProcess.jsp" name="country" onLoad="placeFocus()" >
<table width="800" border="0" cellspacing="0" cellpadding="0" >
  <tr valign="top"> 
    <td  colspan="2" bgcolor="ffffff">
     
      
      <table width="800" border="0" cellspacing="1" cellpadding="4" >
        <tr class='formlabel'> 
			 <td ><table width="790" border="0" ><tr class='formlabel'><td>&nbsp;Country - Add </td><td align=right><%=loginbean.generateUniqueId("ETCCountryAdd.jsp","Add")%></td></tr></table></td>
        </tr>
        </table>
           
            
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
			  <% // @@ Murali Modified On 20050421 Regarding SPETI-5644 ;  %>
			  <!-- <tr class='formdata'><td colspan="4">&nbsp;</td></tr> -->
			  <tr class='formdata'><td colspan="5">&nbsp;</td></tr>
			  <% // @@ Murali Modified On 20050421 Regarding SPETI-5644 ;  %>

                <tr valign="top" class='formdata'> 
                  <td width="100" >Country 
                    Id:<font color="#FF0000">*</font><br>
                    
                    <input type='text' class='text' name="countryId" size="3"  maxlength="2" onBlur="upperCase(countryId)" onkeyPress="return getKeyCode(countryId)">
                    </td>
                  <td width="241">Country 
                    Name:<font color="#FF0000">*</font><br>
                    <!-- //@@ Modifed By G.Srinivas on 20050523 for QA-Issue 1771-1774 -->
                    <input type='text' class='text' name="countryName" size="33" maxlength="30" onBlur='upperCase(countryName);return specialCharFilter(this,"")' onkeyPress="return getSpecialCode(countryName)">
                    </td>
                  <td width="102">Currency Id:<font color="#FF0000">*</font><br>
                     
                    <input type="text" class='text' name="currencyId" size="5" maxlength="3" onBlur="upperCase(currencyId)" onkeyPress="return getKeyCode(currencyId)">
                    </td>
                  <td height="20"  width="321">Region:<font color="#FF0000">*<br>
                    </font>
                    <select size="1" name="region" class='select'>
                      <option value = " "> Select </option>
                      <option value = ASPA>ASPA</option> <!-- Asia Pacific -->
                      <option value = AMNO>AMNO </option> <!-- North America -->
                      <option value = AMLA>AMLA </option> <!-- Latin America -->
                      <option value = EURE>EURE</option> <!-- Europe -->
                      <option value = EMA>EMA</option> <!-- Emerging Markets -->
                      <!--@@ Added by subrahmanyam for the adding of regions as requested by KIM on 18-jan-10 -->
                      <!-- <option value = NAP>NAP</option>--> <!-- North Asia Pacific -->
                      <!--<option value = SAP>SAP</option> --> <!-- South Asia Pacific -->
					<!--@@ Ended by subrahmanyam for the adding of regions as requested by KIM on 18-jan-10 -->                      
                      <option value = OTHER>OTHER</option>
                      <!-- <option value = ANTARCTICA>ANTARCTICA</option> -->
                    </select>
                    </td>
					<td  height="20">Area:<br>
                    <select size="1" name="area" class='select'>
                      <% // @@ Murali Modified On 20050421 Regarding SPETI-5641 ;  %>
					  <!-- <option >&nbsp;</option> -->
					  <option >Select</option>
					  <% // @@ Murali Modified On 20050421 Regarding SPETI-5641 ;  %>
                      <option value='Area1'>Area 1</option>
                      <option value='Area2'>Area 2</option>
                      <option value='Area3'>Area 3</option>
                    </select>
                    </td>
                </tr>
              </table>
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
                <tr class='denotes'> 
                  <td valign="top" colspan="4"> 
                     <!--Modified By Vijay For SPETI 5635-->
					  <font color="#FF0000">*</font><font face="Verdana" size="1">Denotes Mandatory</font>
                  
                </tr>
                <tr class='denotes' > 
                  <td valign="top"  width="30" > <font face="Verdana" size="1">
                    Note :</font></td>
					<td colspan="2"><font face="Verdana" size="1">
                    1)Country Ids to conform to IATA rule No 1.3.1<br>
                    2)Currency Ids to conform to IATA rule No 5.7.1
                  </font>
                  </td>
					<td  align="right">
					 <input type=hidden name=Operation value="Add">
					 <input type="submit" value="Submit" name="jbt_Test"  class='input'>
					 <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'>
                     
                  </td>
                </tr>
              </table>
              
          </td>
        </tr>
      </table></form>
      
</body>
</html>
<%
  }
  	}
	catch(Exception e)
  	{
		 //Logger.error(FILE_NAME,"Error in CommodityAdd.jsp file : "+e.toString());
     logger.error(FILE_NAME+"Error in CommodityAdd.jsp file : "+e.toString());
		 
		 ArrayList  keyValueList = new ArrayList();
		 ErrorMessage errorMessageObject = new ErrorMessage("Error while accessing the page","ETCCoountryAdd.jsp");
		 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 errorMessageObject.setKeyValueList(keyValueList);
         request.setAttribute("ErrorMessage",errorMessageObject); 
	
%>
		 <jsp:forward page="../ESupplyErrorPage.jsp" /> 
<%
 	}	   
%>  

