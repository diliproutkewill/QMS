
<%
/*	Programme Name : QMSSalesPersonRegistrationAdd.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : SalesPersonRegistration
*	Sub Task Name  : Sales Person Registration 
*	Author		   : Rama Krishna.Y
*	Date Started   : 28-06-2005
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
  public final static String   FILE_NAME = "QMSSalesPersonRegistrationAdd.jsp";%>

<%
    logger  = Logger.getLogger(FILE_NAME);
    String       operation      =  request.getParameter("Operation");
    try 			
   {

	 if(loginbean.getTerminalId() == null) //this is to verify whether we can get "terminalid" through the login bean object.
	 {											 		
%>
		<jsp:forward page="../ESupplyLogin.jsp" />
<%
	 }
		else
		{
			//System.out.println("*************"+"2A".compareTo("4A") );

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>Salesperson</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="EAValidate.jsp" %>
</head>
<script language="javascript">

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
}
function showLov(lovName)
{
	 var URL  = "";
	if(lovName.name=="repOffCodeLOV")
	{
		if(document.forms[0].salesPersonCode.value.length<=0)
		{
			alert(" Enter SalesPerson Code ");
			return false;
		}
		if(document.forms[0].level.value.length<=0)
		{
			alert(" Please Enter Level ");
			return false;
		}
		URL="QMSRepOfficersIdsLOV.jsp?repOffCode="+document.forms[0].repOffCode.value+"&level="+document.forms[0].level.value+"&opeartion="+document.forms[0].Operation.value;
	}
	if(lovName.name=="designationLOV")
	{
		
		URL="QMSDesignationLOV.jsp?searchString="+document.forms[0].designation.value;
	}
	if(lovName.name=="salesPersonLOV")
	{  
		if(document.forms[0].terminalId.value.length<=0)
		{
			alert(" Enter terminal Id");
			return false;
		}
	   URL="QMSSalesPersonCodeLOV.jsp?salesPersonCode="+document.forms[0].salesPersonCode.value+"&terminalId="+document.forms[0].terminalId.value+"&opeartion="+document.forms[0].Operation.value+"&terminalId="+document.forms[0].terminalId.value;
	}
	if(lovName.name=="locationLOV")
	{
		URL="etrans/ETCLOVLocationIds.jsp?searchString="+document.forms[0].locationId.value+"&wheretoset="+document.forms[0].locationId.name;
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
	
	if(lovName.name=="countryLOV")
	{
		URL="etrans/ETCLOVCountryIds.jsp?searchString="+document.forms[0].countryId.value;
	}
	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;	
	var Win=open(URL,'Doc',Features);
}

function mandatory()
{	 
		if(document.forms[0].locationId.value.length<=0)
		{
			alert(" Enter location Id");
			document.forms[0].locationId.focus();
			return false;
		}
		if(document.forms[0].terminalId.value.length<=0)
		{
			alert(" Enter TerminalId Id");
			document.forms[0].terminalId.focus();
			return false;
		}
		if(document.forms[0].salesPersonCode.value.length<=0)
		{
			alert(" Enter SalesPerson Code ");
			document.forms[0].salesPersonCode.focus();
			return false;
		}
		if(document.forms[0].designation.value.length<=0)
		{
			alert(" Enter Designation Id");
			document.forms[0].designation.focus();
			return false;
		}
		if(document.forms[0].dateOfJoining.value.length<=0)
		{
			alert(" Enter DateOfJoining ");
			document.forms[0].dateOfJoining.focus();
			return false;
		}
		if(document.forms[0].addressLine1.value.length<=0)
		{
			alert(" Enter Address ");
			document.forms[0].addressLine1.focus();
			return false;
		}
		if(document.forms[0].countryId.value.length<=0)
		{
			alert(" Enter Country Id");
			document.forms[0].countryId.focus();
			return false;
		}
		if(document.forms[0].repOffCode.value.length<=0)
		{
			alert(" Enter Reporting Officer's Code");
			document.forms[0].repOffCode.focus();
			return false;
		}
		if(document.forms[0].time.value.length<=0)
		{
			alert(" Enter Alloted Time");
			document.forms[0].time.focus();
			return false;
		}	
		if(document.forms[0].city.value.length<=0)
		{
			alert(" Please Enter CITY");
			document.forms[0].time.focus();
			return false;
		}
		return true;
}

function toUpperCase(object)
{
	object.value=object.value.toUpperCase();
}

function checkEmail()
{
	var str = document.forms[0].emailid.value;
	var i=0,j=-1;
	var str1;
	var flag=false;
	while(1)
	{
		
		{
			j=str.indexOf(";",j);
			if(j==-1)
			{
				break;
			}
			str1=str.substring(i,j);			
			if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(str1)))
			{
				alert(str1 + " is an Invalid E-mail Address!");	
				document.forms[0].emailid.focus();
				flag=true;
				break;
			}
			
			i=j+1;
			j=j+1;
			continue;
		}
	}
	str1=str.substring(i);	
	if(str1!=''&& !flag)
	if (!(/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(str1)))
	{
		alert(str1 + " is an Invalid E-mail Address!");
		document.forms[0].emailid.focus();
		return false;
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

function checkSpecialKeyCode()
{

	if(event.keyCode == 96 || event.keyCode == 59 || event.keyCode == 39 || event.keyCode == 34)
	{
		return false;
	}
}

</script>
<body>

<form method="post" action="QMSSalesPersonRegistrationProcess.jsp"  onSubmit="return mandatory()" >

  <table border="0" cellPadding="0" cellSpacing="0" width="100%">
    <tbody>
      <tr>
        <td  vAlign="top">
          <table  border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
            <tbody>
              <tr class="formlabel" vAlign="top">
                <td  width="475">
                  <b>Sales Person
                  - Add </b></font>
                </td>
                <td  colSpan="2" width="460">
                  <p align="right"><b>QS1010411</b></font>
                </td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="3" width="921">&nbsp;</td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="2" width="295">Location
                  Id:<sup><font color="red">*</font></sup><br>
                  <input class="text" maxLength="3" name="locationId" onblur="toUpperCase(this)" size="3">
                  <input class="input" name="locationLOV" onclick="showLov(locationLOV)" onKeyPress='checkSpecialKeyCode()' type="button" value="..." null></font></td>
                <td  width="533">Terminal
                  Id:<sup><font color="red">*</font></sup></font><br>
                 <input class="text" maxLength="8" name="terminalId" onKeyPress='checkSpecialKeyCode()' onblur="toUpperCase(this)" size="8">&nbsp;<input class="input" name="terminalLOV" onclick="showLov(terminalLOV)"  type="button" value="..." null></font></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="2" width="295">Salesperson
                  Code:<font color="red">*</font><br>
                  <input class="text" maxLength="30" onKeyPress='checkSpecialKeyCode()' name="salesPersonCode" onblur="toUpperCase(this)"  size="20" value="">
                  <input class="input" name="salesPersonLOV" onclick="showLov(salesPersonLOV)"  type="button" value="..." null></font></td>
                <td  width="533">Salesperson
                  Name:<font color="red">*</font><br>
                  <input class="text" maxLength="15" name="salesPersonName"  onblur="toUpperCase(this)"  size="15" readOnly >
                  &nbsp;</font></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  width="200">Designation:<font color="red">*</font><br>
                  <input class="text" maxLength="16" name="designation" onKeyPress='checkSpecialKeyCode()' onblur="toUpperCase(this)"  size="20" value=""><input class="input" name="designationLOV" onclick="showLov(designationLOV)"  type="button" value="..." ></td>

                <td  width="95"><font face="Verdana" size="2">Level:<br>
                 <input class="text" maxLength="3" name="level" onblur="toUpperCase(this)"  size="3"  value="" readOnly></font></td>

                <td  width="533">Date of Joining (DD/MM/YY):<font color="red">*</font><br>
                  <input class="text" maxLength="10" name="dateOfJoining" onblur="dtCheck(this)" size="10" null>
                  <input class="input" name="dateOfJoiningLOV" onclick="newWindow('dateOfJoining')"  type="button" value="..." null></font></td>
              </tr>
            </tbody>
          </table>
          <table  border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
            <tbody>
              <tr class="formdata" vAlign="top">
                <td  colSpan="3" width="400" rowspan="2">Address:<font color="red">*</font><br>
                  <input class="text" maxLength="75" name="addressLine1" onblur="toUpperCase(this)" onKeyPress='checkSpecialKeyCode()' size="50"><br>
                  <input class="text" maxLength="75" onKeyPress='checkSpecialKeyCode()' name="addressLine2" onblur="toUpperCase(this)"  size="50"></td>
                <td  colSpan="3" width="406">Landline
                  No:<br>
                 <input class="text" maxLength="20" name="phoneNo" onblur="toUpperCase(this);"  onKeyPress='checkSpecialKeyCode()' size="20"></font></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="3" width="406">Mobile
                  No:<br>
                  <input class="text" maxLength="25" onKeyPress='checkSpecialKeyCode()' name="mobilePhoneNo" onblur="toUpperCase(this)"  size="25"></font></td>
              </tr>
              <tr class="formdata">
                <td  colSpan="3" width="400">City:<font color="red">*</font><br>
                  <input class="text" maxLength="30" name="city" onblur="toUpperCase(this)"  onKeyPress='checkSpecialKeyCode()' size="50"></font></td>
                <td  colSpan="3" width="406">Fax
                  No:<br>
                  <input class="text" maxLength="20" onKeyPress='checkSpecialKeyCode()' name="fax" onblur="toUpperCase(this)"  size="20"></font></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  width="148">Zip
                  or Postal Code:<sup><font color="red">*</font></sup><br>
                 <input class="text" maxLength="10" name="zipCode" onblur="toUpperCase(this)" onKeyPress='checkSpecialKeyCode()' size="25"></font></td>
                <td  width="156">State
                  or Province:<br>
                  <input class="text" maxLength="30" onKeyPress='checkSpecialKeyCode()' name="state" onblur="toUpperCase(this)"  size="25"></font></td>
                <td  width="92">Country
                  Id:<font color="red">*</font><br>
                  <input class="text" maxLength="2" name="countryId" onblur="toUpperCase(this)" onKeyPress='checkSpecialKeyCode()' size="4">
                  &nbsp; <input class="input" name="countryLOV" onclick="showLov(countryLOV)"  type="button" value="..."></font></td>
                <td  colSpan="3" width="406">Email
                  ID:<br>
                  <input class="text" maxLength="250" name="emailid" onblur="checkEmail()"  size="25"  value=""></font></td>
              </tr>
			  </table>
			  <table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
              <tr class="formlabel" vAlign="top">
                <td  colSpan="6" width="796">Escalation
                  Details</td>
              </tr>
              <tr class="formdata" >
                <td width="174" >Reporting&nbsp;Officer's<br> Code:<font color="red">*</font>
                  <input class="text" maxLength="16" onKeyPress='checkSpecialKeyCode()' name="repOffCode"  size="16" value="">
                  <input class="input" name="repOffCodeLOV"  type="button" value="..." onClick="showLov(repOffCodeLOV)"></font></td>
                <td width="168" >Reporting
                  Officer's <br>Name<br>
                  <input class="text" maxLength="20" name="repOffName" onblur="toUpperCase(this)" size="20"  readOnly></font></td>
				<td width="110" >Escalation<br>
                  Level<br>
                  <input class="text" maxLength="3" name="superLevel" onblur="toUpperCase(this)" onkeypress=" " size="3"  value="" readOnly></font></td>
                <td width="129" >Designation<br>
                  ID<br><input class="text" maxLength="15" name="superDesignationId" onblur="toUpperCase(this)" onkeypress="" size="15"  value="" readOnly></font></td>
                <td width="140" >Allotted<br>
                  Time (HH:MM)<font color="red">*</font><br>
                  <input class="text" maxLength="5" name="time" onblur="" onKeyPress='checkTime(this)' size="5" value=""></font></td>
                <td  width="141" ><font face="Verdana" size="2">Remarks<br><br>
                  <input class="text" maxLength="50" name="remarks" onblur="toUpperCase(this)" onKeyPress='checkSpecialKeyCode()' size="20"></font></td>
              </tr>
              <tr class="text" vAlign="top">
                <td  colSpan="4" width="519"><font color="red">*</font>Denotes
                  Mandatory
				  <input type="hidden" name="Operation"  value="<%=operation%>">
				  </td>

                <td align="right"  colSpan="2" width="266">&nbsp;&nbsp;
                  <input class="input" name="submit" ; BORDER-BOTTOM-STYLE: solid; BORDER-LEFT-STYLE: solid; BORDER-RIGHT-STYLE: solid; BORDER-TOP-STYLE: solid" type="submit" value="Submit"></font></td>
				  <input type='hidden' name='invalidate' value="">
				  <input type='hidden' name='addressId' value="">
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
			}
	}
	catch(Exception e)
	{
		e.printStackTrace();
		//Logger.error(FILE_NAME,"Error in QMSSalesPersonRegistrationAdd.jsp"+e.toString());
    logger.error(FILE_NAME+"Error in QMSSalesPersonRegistrationAdd.jsp"+e.toString());
		ArrayList       keyValueList   =  new ArrayList();
		ErrorMessage    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSSalesPersonRegistrationAdd.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
    <jsp:forward page="QMSESupplyErrorPage.jsp" />
<%
	}
%>