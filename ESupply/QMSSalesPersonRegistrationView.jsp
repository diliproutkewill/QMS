
<%
/*	Programme Name : QMSSalesPersonRegistrationView.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : SalesPersonRegistration
*	Sub Task Name  : 
*	Author		   : Rama Krishna.Y
*	Date Started   : 16-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>
<%@ page buffer='20kb' autoFlush = "true"  import="org.apache.log4j.Logger,
                 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 java.util.ArrayList,
				 com.qms.setup.ejb.sls.QMSSetUpSession,
				 com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				 com.qms.setup.java.SalesPersonRegistrationDOB,
				 javax.naming.InitialContext"%>


<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>



<%! 
  private static Logger logger = null;
  public final static String   FILE_NAME = "QMSSalesPersonRegistrationAdd.jsp";%>

<%
    logger  = Logger.getLogger(FILE_NAME);
    String       operation      =  request.getParameter("Operation");
	QMSSetUpSessionHome             home			=           null;
    QMSSetUpSession                 remote		    =           null;
    InitialContext                  iCtxt		    =           null;
	String                          salesPersonCode =           request.getParameter("salesPersonCode");
	SalesPersonRegistrationDOB      dob             =           null;
	ArrayList                       keyValueList    =           null;
	ErrorMessage                    errorMessage    =           null;
	String                          readOnly        =            "";
	String                          action          =            "";

    try 			
   {

	 if(loginbean.getTerminalId() == null) //this is to verify whether we can get "terminalid" through the login bean object.
	 {											 		
%>
		<jsp:forward page="../ESupplyLogin.jsp" />
<%
	 }
		{
			
			   iCtxt                 =         new InitialContext();
			   home                  =         (QMSSetUpSessionHome) iCtxt.lookup("QMSSetUpSessionBean");
			   remote                =         (QMSSetUpSession)     home.create(); 
			   dob                   =          remote.getSalePersonDetails(salesPersonCode);			  
			   
			   if("View".equals(operation))
				   action="QMSSalesPersonRegistrationEnterId.jsp?Opeartion=View";
			   else
				   action="QMSSalesPersonRegistrationProcess.jsp?Opeartion="+operation;
			   

			   if("View".equals(operation) || "Delete".equals(operation))
				   readOnly   =  "readOnly";
			   
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
			alert(" enter SalesPerson Code ");
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
			alert(" enter terminal Id");
			return false;
		}
	   URL="QMSSalesPersonCodeLOV.jsp?searchString="+document.forms[0].salesPersonCode.value+"&terminalId="+document.forms[0].terminalId.value+"&opeartion="+document.forms[0].Operation.value+"&terminalId="+document.forms[0].terminalId.value;
	}
	if(lovName.name=="locationLOV")
	{
		URL="etrans/ETCLOVLocationIds.jsp?searchString="+document.forms[0].locationId.value+"&wheretoset="+document.forms[0].locationId.name;
	}
	if(lovName.name=="terminalLOV")
	{
		if(document.forms[0].locationId.value.length<=0)
		{
			alert(" enter location Id");
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
			alert(" enter location Id");
			document.forms[0].locationId.focus();
			return false;
		}
		if(document.forms[0].terminalId.value.length<=0)
		{
			alert(" enter TerminalId Id");
			document.forms[0].terminalId.focus();
			return false;
		}
		if(document.forms[0].salesPersonCode.value.length<=0)
		{
			alert(" enter SalesPerson Code ");
			document.forms[0].salesPersonCode.focus();
			return false;
		}
		if(document.forms[0].designation.value.length<=0)
		{
			alert(" enter Designation Id");
			document.forms[0].designation.focus();
			return false;
		}
		if(document.forms[0].dateOfJoining.value.length<=0)
		{
			alert(" enter DateOfJoining ");
			document.forms[0].dateOfJoining.focus();
			return false;
		}
		if(document.forms[0].addressLine1.value.length<=0)
		{
			alert(" enter Address ");
			document.forms[0].addressLine1.focus();
			return false;
		}
		if(document.forms[0].countryId.value.length<=0)
		{
			alert(" enter Country Id");
			document.forms[0].countryId.focus();
			return false;
		}
		if(document.forms[0].repOffCode.value.length<=0)
		{
			alert(" enter Reporting Officer's Code");
			document.forms[0].repOffCode.focus();
			return false;
		}
		if(document.forms[0].time.value.length<=0)
		{
			alert(" enter Alloted Time");
			document.forms[0].time.focus();
			return false;
		}
		if(document.forms[0].city.value.length<=0)
		{
			alert(" enter City");
			document.forms[0].city.focus();
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
			//alert(str1);
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

<form action="<%=action%>" method="post" onSubmit="return mandatory()" >

  <table border="0" cellPadding="0" cellSpacing="0" width="100%">
    <tbody>
      <tr>
        <td  vAlign="top">
          <table  border="0" cellPadding="4" cellSpacing="1" width="100%">
            <tbody>
              <tr class="formlabel" vAlign="top">
                <td  width="475">
                  <b>Sales Person Registration
                  - <%=operation%></b></font>
                </td>
                <td  colSpan="2" width="460">
                  <p align="right"><b>QS1010422</b></font>
                </td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="3" width="921">&nbsp;</td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="2" width="295">Location
                  Id:<sup><font color="red">*</font></sup><br>
                  <input class="text" maxLength="3" name="locationId" onKeyPress='checkSpecialKeyCode()' onblur='toUpperCase(this)' value='<%=dob.getLocationId()%>' size="3" <%=readOnly%>>
                  <input class="input" name="locationLOV" onclick="showLov(locationLOV)"  type="button" value="..." <%=readOnly%>></font></td>
                <td  width="533">Terminal
                  Id:<sup><font color="red">*</font></sup></font><br>
                 <input class="text" maxLength="8" name="terminalId" onblur='toUpperCase(this)' onKeyPress='checkSpecialKeyCode()' value='<%=dob.getTerminalId()%>' size="8" <%=readOnly%>><input class="input" name="terminalLOV" onclick="showLov(terminalLOV)"  type="button" value="..." <%=readOnly%>></font></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="2" width="295">Salesperson
                  Code:<font color="red">*</font><br>
                  <input class="text" maxLength="16" onKeyPress='checkSpecialKeyCode()' name="salesPersonCode" onblur='toUpperCase(this)' value='<%=dob.getSalesPersonCode()%>' size="16" <%=readOnly%>>
                  <input class="input" name="salesPersonLOV" onclick="showLov(salesPersonLOV)"  type="button" value="..." <%=readOnly%>></font></td>
                <td  width="533">Salesperson
                  Name:<br>
                  <input class="text" maxLength="30" name="salesPersonName" value='<%=dob.getSalesPersonName()%>' size="30"  <%=readOnly%>>
                  &nbsp;</font></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  width="215">Designation:<font color="red">*</font><br>
                  <input class="text" maxLength="15" name="designation" onKeyPress='checkSpecialKeyCode()' value='<%=dob.getDesignation()%>' onblur="toUpperCase(this)" onkeypress="return checkSpecialKeyCode()" size="15" <%=readOnly%>><input class="input" name="designationLOV" onclick="showLov(designationLOV)"  type="button" value="..." <%=readOnly%>></td>

                <td  width="94"><font face="Verdana" size="2">Level:<br>
                 <input class="text" maxLength="3" onKeyPress='checkSpecialKeyCode()' name="level" value='<%=dob.getLevel()%>' onblur="toUpperCase(this);" onkeypress="return checkSpecialKeyCode()" size="3"  readOnly></font></td>

                <td  width="533">Date
                  of Joining (DD/MM/YY):<font color="red">*</font><br>
                  <input class="text" maxLength="10" name="dateOfJoining"  value='<%=dob.getDateOfJoining()%>' onblur="dtCheck(this)" size="10" null>
                  <input class="input" name="dateOfJoiningLOV" onclick="newWindow('dateOfJoining')"  type="button" value="..." <%=readOnly%>></font></td>
              </tr>
            </tbody>
          </table>
          <table  border="0" cellPadding="4" cellSpacing="1" width="100%">
            <tbody>
              <tr class="formdata" vAlign="top">
                <td  colSpan="3" width="400" rowspan="2">Address:<font color="red">*</font><br>
                  <input class="text" maxLength="75" name="addressLine1" onblur="toUpperCase(this)" onKeyPress='checkSpecialKeyCode()' value='<%=dob.getAddressLine1()%>' size="50" <%=readOnly%>><br>
                  <input class="text" maxLength="75" onKeyPress='checkSpecialKeyCode()' value='<%=dob.getAddressLine2()%>' name="addressLine2" onblur="toUpperCase(this)"  size="50" <%=readOnly%>></td>
                <td  colSpan="3" width="406">Landline
                  No:<br>
                 <input class="text" maxLength="20" name="phoneNo" onblur="toUpperCase(this);"  onKeyPress='checkSpecialKeyCode()' value='<%=dob.getPhoneNo()%>' size="20" <%=readOnly%>></font></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  colSpan="3" width="406">Mobile
                  No:<br>
                  <input class="text" maxLength="25" onKeyPress='checkSpecialKeyCode()' value='<%=dob.getMobilePhoneNo()%>' name="mobilePhoneNo" onblur="toUpperCase(this)"  size="25" <%=readOnly%>></font></td>
              </tr>
              <tr class="formdata">
                <td  colSpan="3" width="400">City:<font color="red">*</font><br>
                  <input class="text" maxLength="30" name="city" value='<%=dob.getCity()%>' onblur="toUpperCase(this)"  onKeyPress='checkSpecialKeyCode()' size="50" <%=readOnly%>></font></td>
                <td  colSpan="3" width="406">Fax
                  No:<br>
                  <input class="text" maxLength="20" onKeyPress='checkSpecialKeyCode()' value='<%=dob.getFax()%>' name="fax" onblur="toUpperCase(this)"  size="20" <%=readOnly%>></font></td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td  width="148">Zip
                  or Postal Code:<sup><font color="red">*</font></sup><br>
                 <input class="text" maxLength="10" name="zipCode" onblur="toUpperCase(this)" onKeyPress='checkSpecialKeyCode()' size="20" value='<%=dob.getZipCode()%>' <%=readOnly%>></font></td>
                <td  width="156">State
                  or Province:<br>
                  <input class="text" maxLength="30" onKeyPress='checkSpecialKeyCode()' value='<%=dob.getState()%>' name="state" onblur="toUpperCase(this)"  size="18" <%=readOnly%>></font></td>
                <td  width="92">Country
                  Id:<font color="red">*</font><br>
                  <input class="text" maxLength="2" name="countryId" onblur="toUpperCase(this)" onKeyPress='checkSpecialKeyCode()' size="4"  value='<%=dob.getCountryId()%>' <%=readOnly%>>
                  &nbsp; <input class="input" name="countryLOV" onclick="showLov(countryLOV)"  type="button" value="..."></font></td>
                <td  colSpan="3" width="406">Email
                  ID:<br>
                  <input class="text" maxLength="250" name="emailid" onblur="checkEmail()" value='<%=dob.getEmailid()%>' size="25"  <%=readOnly%>></font></td>
              </tr>
			  </table>
			  <table border="0" cellPadding="4" cellSpacing="1" width="100%">
              <tr class="formlabel" vAlign="top">
                <td  colSpan="6" width="796">Escalation
                  Details</td>
              </tr>
              <tr class="formdata" >
                <td width="174" >Reporting&nbsp;Officer's<br> Code:<font color="red">*</font>
                  <input class="text" maxLength="16" onKeyPress='checkSpecialKeyCode()' value='<%=dob.getRepOffCode()%>'  name="repOffCode"  size="16" <%=readOnly%>>
                  <input class="input" name="repOffCodeLOV"  type="button" value="..." onClick="showLov(repOffCodeLOV)"></font></td>
                <td width="168" >Reporting
                  Officer's <br>Name<br>
                  <input class="text" maxLength="20" name="repOffName" value='<%=dob.getRepOffName()%>' onblur="toUpperCase(this)" size="20"  readOnly></font></td>
				<td width="110" >Escalation<br>
                  Level<br>
                  <input class="text" maxLength="3" name="superLevel" value='<%=dob.getSuperLevel()%>' onblur="toUpperCase(this)" onkeypress=" " size="3"  readOnly></font></td>
                <td width="129" >Designation<br>
                  ID<br><input class="text" maxLength="15" name="superDesignationId" value='<%=dob.getSuperDesignationId()%>' onblur="toUpperCase(this)" onkeypress="" size="15"  readOnly></font></td>
                <td width="140" >Allotted<br>
                  Time (HH:MM)<font color="red">*</font><br>
                  <input class="text" maxLength="5" name="time" value='<%=dob.getTime()%>' onblur="" onKeyPress='checkTime(this)' <%=readOnly%> size="5"></font></td>
                <td  width="141" ><font face="Verdana" size="2">Remarks<br><br>
                  <input class="text" maxLength="50" name="remarks" onblur="toUpperCase(this)" onKeyPress='checkSpecialKeyCode()' value='<%=dob.getRemarks()%>' size="20" <%=readOnly%>></font></td>
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
		//Logger.error(FILE_NAME,"Error in QMSSalesPersonRegistrationAView.jsp"+e.toString());
    logger.error(FILE_NAME+"Error in QMSSalesPersonRegistrationAView.jsp"+e.toString());
		keyValueList   =  new ArrayList();
		errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSSalesPersonRegistrationAdd.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
    <jsp:forward page="QMSESupplyErrorPage.jsp" />
<%
	}
%>