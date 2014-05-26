<%--
 % 
 % Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
 % This software is the proprietary information of FourSoft, Pvt Ltd.
 % Use is subject to license terms.
 %
 % esupply - v 1.x 
 %
 --%>
 <%--
 % File			: ESACUserRegistrationAdd.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is the User Registration UI and it also display the possible assignable to Roles 
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<%@ page import="com.foursoft.esupply.common.bean.ESupplyGlobalParameters, com.foursoft.esupply.accesscontrol.util.UserAccessUtility, com.foursoft.esupply.accesscontrol.java.UserModel, com.foursoft.esupply.accesscontrol.java.RoleModel, com.foursoft.esupply.accesscontrol.java.UserAccessConfig, com.foursoft.esupply.accesscontrol.java.UserAccessConfigFactory, com.foursoft.esupply.common.java.UserPreferenceMaster, com.foursoft.esupply.delegate.UserRoleDelegate,

org.apache.log4j.Logger, com.foursoft.esupply.common.exception.FoursoftException, com.foursoft.esupply.common.java.ErrorMessage, java.util.ArrayList, java.util.HashMap,java.util.Set,java.util.Iterator, java.util.Hashtable,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext"%>

<%--com.foursoft.esupply.common.util.Logger, com.foursoft.esupply.common.exception.FoursoftException, com.foursoft.esupply.common.java.ErrorMessage, java.util.ArrayList, java.util.HashMap,java.util.Set,java.util.Iterator, java.util.Hashtable,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext"--%>

<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>		   
<jsp:useBean id ="supportLanguage" class= "java.util.HashMap" scope ="application" />

<%!
  private static Logger logger = null;
	String fileName			= "ESACUserRegistrationAdd.jsp";
  String  langu = null;
	
%>
<%
    logger  = Logger.getLogger(fileName);
	  langu = loginbean.getUserPreferences().getLanguage();
%>
<fmt_rt:setLocale value="<%=langu%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<%
    
	String		actionPage		= "ESACUserRegistrationController.jsp";
	
	String		action 			= "";
	String		actionLabel		= "";
	String[]	modules;
	
	String		roleModule			= "";
	String		roleDescription		= "";

	String		disable	="";

	action 			= request.getParameter("action");

	UserModel	userModel		= (UserModel) session.getAttribute("userModel");
	RoleModel	roleModel		= (RoleModel) session.getAttribute("roleModel");

	UserRoleDelegate userDelegate = null;
    try
    {
        userDelegate = new UserRoleDelegate();
    }
    catch(FoursoftException exp)
    {
        String errorMessage = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100240")+exp.getErrorCode();
        ErrorMessage errMsg = null;
        if (action.equalsIgnoreCase("add"))
            errMsg = new ErrorMessage(errorMessage, "ESACRoleRegistration.jsp",exp.getErrorCode(),exp.getComponentDetails());
        else if (action.equalsIgnoreCase("ViewAll"))
            errMsg = new ErrorMessage(errorMessage,"ESACRoleRegistrationController.jsp?action="+action,exp.getErrorCode(),exp.getComponentDetails());
        else
            errMsg = new ErrorMessage(errorMessage, "ESACUserRoleEnterId.jsp?action="+action+"&UIName=Role",exp.getErrorCode(),exp.getComponentDetails());
        request.setAttribute("errorMessage", errMsg); 
%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
<%
    }

	ArrayList settings =null;

	try
    {
        settings=userDelegate.getSecuritySettings();

    }
    catch(FoursoftException exp)
    {
        String errorMessage = "Error in getSecuritySettings "+exp.getErrorCode();
        ErrorMessage errMsg = new ErrorMessage(errorMessage, "SecuritySettings.jsp",exp.getErrorCode(),exp.getComponentDetails());
        request.setAttribute("errorMessage", errMsg); 
%>
	<jsp:forward page="ESupplyMessagePage.jsp" />
<%
    }

	Hashtable	userPreferences	= userModel.getUserPreferences();
	
	if(userPreferences == null) {
		userPreferences = new Hashtable();
	}

	if(roleModel!=null) {
		roleModule		=	roleModel.getRoleModule();
		roleDescription	=	roleModel.getDescription();
	}
	
	if(roleModule!=null && roleModule.length() > 0) {
		roleModule = UserAccessUtility.convertIntoModuleString( Integer.parseInt(roleModule) );
	} else {
		roleModule = "";
	}

	session.removeAttribute("roleModel");

	String readOnly		= "";
	String readOnly1	= "";
	
	if(action!=null && !action.equalsIgnoreCase("add") )
	{
		readOnly = "readonly";
		
		if(action.equalsIgnoreCase("modify")) {
			actionLabel	=	"Modify";
			readOnly1	=	"readonly";
		}
		if(action.equalsIgnoreCase("view")) {
			actionLabel	=	"View";
			disable="disabled";
		}
		if(action.equalsIgnoreCase("delete")) {
			actionLabel	=	"Delete";
			disable="disabled";
		}
	} else {
		actionLabel	=	"Add";
	}

	String		locationIdLeble		= UserAccessUtility.getLocationLable(userModel.getUserLevel());

	//Logger.info(fileName, "Module the user is going to belongs to is asdfsdfsdfsaddfas: "+roleModule);

	String	whLabel 	=	"";
	String	whesLabel 	=	"";
	String	accessType	=	userModel.getUserLevel();
	String	parentId	=	"";
    String  readFlag    =   "";
    String  disFlag     =   ""; 
	//@@ Added by Subrahmanyam for the Enhancement 154384 on 23/01/2009
	ArrayList salesPersons =new ArrayList();
	String locationId      =request.getParameter("locationId");
	String delUserId      =request.getParameter("userId");
	String accessTerminal      =request.getParameter("accessType");
	String delSalesPerson   = userDelegate.getDelSalesPerson(delUserId);//to get the SalesPerson for the deleting EmpId
	salesPersons=userDelegate.getSalesPersons(locationId,delSalesPerson,accessTerminal);
	
//@@ Ended by Subrahmanyam for the Enhancement 154384 on 23/01/2009
	
    if(action.equalsIgnoreCase("View") || action.equalsIgnoreCase("Delete"))
    {
	 readFlag = "readOnly";
     disFlag  =  "disabled";
	} 

/*
	if(FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.EP) && accessType.equals("COMPANY")) {
		whLabel		= "Facility";
		whesLabel	= "Facilities";
		parentId	= userModel.getLocationId();
	}
	if(FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.ELOG) && accessType.equals("WAREHOUSE")) {
		whLabel		= "Warehouse Customer";
		whesLabel	= "Warehouse Customers";
		parentId	= userModel.getLocationId();
		
	} //Below code added to test
	if(FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.SP) && accessType.equals("WAREHOUSE")) {
		whLabel		= "Warehouse Customer";
		whesLabel	= "Warehouse Customers";
		parentId	= userModel.getLocationId();
		
	}
	*/
%>
<html>
<head>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<title><fmt:message key="100100" bundle="${lang}"/></title>
<%@ include file="/ESEventHandler.jsp" %>
<SCRIPT language="JavaScript" src="sha1.js"></SCRIPT>
<script language="JavaScript">
//////////////////////////////// trim(strText) ////////////////////////////

	function trim(strText) { 
		/*  removes empty spaces from wither side of a javascript string */
 
		while (strText.substring(0,1) == ' ') {
			strText = strText.substring(1, strText.length);
		}

		while (strText.substring(strText.length-1,strText.length) == ' ') {
			strText = strText.substring(0, strText.length-1);
		}

		return strText;
	} 
			
	function stringFilter (input) 
	{
		s = input.value;
		filteredValues = "'";     // Characters stripped out
		var i;
		var returnString = "";
		for (i = 0; i < s.length; i++) 
		{  // Search through string and append to unfiltered values to returnString.
			var c = s.charAt(i);
			if (filteredValues.indexOf(c) == -1) returnString += c.toUpperCase();
		}
		input.value = returnString;
	}
	// added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
	function setSelectedMode(i,mode)
	{
		var air=0;
		var sea=0;
		var truck=0;
		var checkingAirvalue=0;
		var checkingSeavalue=0;
		var checkingTruckvalue=0;
			for(var j=0;j<3;j++){
			if(mode=='Air')
			{
				for(var k=0;k<3;k++)
				{
					if(j==0 && document.getElementById(mode+k).checked)
					{

                     checkingAirvalue=1;
					}
				}
		if(document.getElementById(mode+j).checked && checkingAirvalue==1)
				{
			document.getElementById(mode+j).disabled = false;
				}
				else if(checkingAirvalue==1) {
					document.getElementById(mode+j).disabled = true;
				}
				else if(checkingAirvalue==0){
					document.getElementById(mode+j).disabled = false;
				}
     
			}
			if(mode=='Sea')
			{
				for(var k=0;k<3;k++)
				{
					if(j==0 && document.getElementById(mode+k).checked)
					{

                     checkingSeavalue=1;
					}
				}
		if(document.getElementById(mode+j).checked && checkingSeavalue==1)
				{
			document.getElementById(mode+j).disabled = false;
				}
		  else if(checkingSeavalue==1) {
			document.getElementById(mode+j).disabled = true;
		  }
		 	else if(checkingSeavalue==0){
					document.getElementById(mode+j).disabled = false;
				}
			}
			if(mode=='Truck')
			{
				for(var k=0;k<3;k++)
				{
					if(j==0 && document.getElementById(mode+k).checked)
					{

                     checkingTruckvalue=1;
					}
				}
		if(document.getElementById(mode+j).checked && checkingTruckvalue==1)
				{
			document.getElementById(mode+j).disabled = false;
				}
			else  if(checkingTruckvalue==1) {
			document.getElementById(mode+j).disabled = true;
			}
			else if(checkingTruckvalue==0){
					document.getElementById(mode+j).disabled = false;
				}
			}
		}

           if(document.getElementById('Air'+i).checked)
           air=document.getElementById('Air'+i).value
			if(document.getElementById('Sea'+i).checked)
            sea=document.getElementById('Sea'+i).value
			if(document.getElementById('Truck'+i).checked)
			truck=document.getElementById('Truck'+i).value
  document.getElementById('checkboxName'+i).value=Number(air)+Number(sea)+Number(truck);
	}

function validateCombination(strng){

	var sizechar =strng.value.length;			
	var upassID=strng;
	
	var upass_string = upassID.value;
		
    var valid="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ~!%?@#$^*()_+-[]\{}|:;,./><";

    for (var i=0; i<sizechar; i++) {
        if (valid.indexOf(upass_string.charAt(i)) < 0) {
            alert('<fmt:message key="100086" bundle="${lang}"/>');
			upassID.focus();
            return false;
        }
    }

	var num_valid="1234567890";
	var upper_valid="abcdefghijklmnopqrstuvwxyz";
	var lower_valid="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	var spchars_valid="~!%?@#$^*()_-+[]\{}|:;,./><";
	
	var upper =false;
	var lower =false;
	var spchars =false;
	var num =false;
	
    for (var i=0; i<sizechar; i++) 
	{
		if (num_valid.indexOf(upass_string.charAt(i)) >= 0) 
		{
		num=true;
		
		}
		else if (upper_valid.indexOf(upass_string.charAt(i)) >= 0) 
		{
            lower=true;
			
        }
		else if (lower_valid.indexOf(upass_string.charAt(i)) >= 0) 
		{
            upper=true;
			
        }
		else if (spchars_valid.indexOf(upass_string.charAt(i)) >= 0) 
		{
            spchars=true;
			
        }

    }
	

	if((upper==true)&&(lower==true)&&(num==true))
	{
	return true;
	}
	else if ((upper==true)&&(lower==true)&&(spchars==true)){
	return true;
	}
	else if ((upper==true)&&(spchars==true)&&(num==true)){
	return true;
	}
	else if ((spchars==true)&&(lower==true)&&(num==true)){
	return true;
	}
	else {
	alert('<fmt:message key="100085" bundle="${lang}"/>');
	upassID.focus();
	return false;
	}
 }

	function intValues(input) 
	{
		s = parseInt(input.value);
		
		if (isNaN(s))
		{
			alert('<fmt:message key="100614" bundle="${lang}"/>');
			input.focus();
			return false;
		}
		return true;
	}
	 // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
function checkbox()
{
	var value = 0;
	var air=0;
	var sea=0;
	var truck=0;
	var checkingValue=0;
	var modifyValue=0;
	<%for(int i=0;i<userModel.getShipmentModeCode2().size();i++)
	{%>
		value='<%=userModel.getShipmentModeCode2().get(i)%>';
      if(value=='1')
		{
	document.getElementById('Air'+<%=i%>).checked=true;
	document.getElementById('Air'+<%=i%>).disabled = false;
	air=value;
		}
		  if(value=='2')
		{
	document.getElementById('Sea'+<%=i%>).checked=true;
	document.getElementById('Sea'+<%=i%>).disabled = false;
	sea=value;
		}
		  if(value=='4')
		{
truck=value;
	document.getElementById('Truck'+<%=i%>).checked=true;
	document.getElementById('Truck'+<%=i%>).disabled = false;
		}
		  if(value=='3')
		{
	document.getElementById('Air'+<%=i%>).checked=true;
	document.getElementById('Sea'+<%=i%>).checked=true;
	document.getElementById('Air'+<%=i%>).disabled = false;
	document.getElementById('Sea'+<%=i%>).disabled = false;
		}
		  if(value=='6')
		{
	 document.getElementById('Sea'+<%=i%>).checked=true;
	document.getElementById('Truck'+<%=i%>).checked=true;
	document.getElementById('Truck'+<%=i%>).disabled = false;
	document.getElementById('Sea'+<%=i%>).disabled = false;
	
		}
		  if(value=='5')
		{
	document.getElementById('Air'+<%=i%>).checked=true;
	document.getElementById('Truck'+<%=i%>).checked=true;
	document.getElementById('Truck'+<%=i%>).disabled = false;
	document.getElementById('Air'+<%=i%>).disabled = false;
	
		}
		 if(value=='7')
		{
	document.getElementById('Air'+<%=i%>).checked=true;
	document.getElementById('Sea'+<%=i%>).checked=true;
	document.getElementById('Truck'+<%=i%>).checked=true;
	document.getElementById('Sea'+<%=i%>).disabled = false;
	document.getElementById('Truck'+<%=i%>).disabled = false;
	document.getElementById('Air'+<%=i%>).disabled = false;
	
		}
		checkingValue=Number(checkingValue)+Number(value)
		modifyValue=1;
	<%}%>
		for(var i=0;i<3;i++)
	{
		if(checkingValue==1)
		{
	document.getElementById('Sea'+i).disabled = false;
	document.getElementById('Truck'+i).disabled = false;
		}
		if(checkingValue==2)
		{
	document.getElementById('Air'+i).disabled = false;
	document.getElementById('Truck'+i).disabled = false;
		}
		if(checkingValue==4)
		{
	document.getElementById('Sea'+i).disabled = false;
	document.getElementById('Air'+i).disabled = false;
		}
		if(checkingValue==3)
		{
		document.getElementById('Truck'+i).disabled = false;
		}
		if(checkingValue==5)
		{
		document.getElementById('Sea'+i).disabled = false;
	
		}
		if(checkingValue==6)
		{
		document.getElementById('Air'+i).disabled = false;
	
		}
		if(modifyValue==0)
		{
			document.getElementById('Air'+i).disabled = false;
			document.getElementById('Sea'+i).disabled = false;
			document.getElementById('Truck'+i).disabled = false;
		}
		
	}
}
	function checkForm()
	{
		var checkingValue=0;
//@@ Added by subrahmanyam for the Enhancement 154384 on 30/01/09 (for the Script error)				
		<%		if(action!=null && (action.equalsIgnoreCase("add")|| action.equalsIgnoreCase("modify")))
		{
		%>
//@@ Ended by subrahmanyam for the Enhancement 154384 on 30/01/09
// added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
		if(document.getElementById('repOffCode'+0).value=="" && document.getElementById('repOffCode'+1).value=="" && document.getElementById('repOffCode'+2).value=="")
			{
			alert("Please enter Reporting Officer's Code");
			document.getElementById('repOffCode'+0).focus();
			return false;
			}
		for(var z=0;z<3;z++)
			{
			if(document.getElementById('repOffCode'+z).value!="")
				{
               if(document.getElementById('allotedTime'+z).value=="")
					{
				   alert("Please enter Alloted Time");
			      document.getElementById('allotedTime'+z).focus();
			      return false;
					}
				   if(document.getElementById('Air'+z).checked!=true &&	document.getElementById('Sea'+z).checked!=true && document.getElementById('Truck'+z).checked!=true)
					{
		     alert("Please Check corresponding  ShipmentMode of "+ document.getElementById('repOffCode'+z).value);
			 return false;
					}

				} //if(document.getElementById('checkboxName'+z).value !="")
				//{
					if(document.getElementById('repOffCode'+z).value=="")
					{
						if(document.getElementById('Air'+z).checked==true ||	document.getElementById('Sea'+z).checked==true || document.getElementById('Truck'+z).checked==true)
						{
						alert("Please enter Reporting Officer's Code in Row " + Number(z+1) );
						 return false;
						}
					}
				//}
				
				checkingValue=Number(checkingValue)+Number(document.getElementById('checkboxName'+z).value);
				
			}
			//alert("after outside   "+checkingValue);
			if(checkingValue!=7)
			{
				alert(" Please Check All  ShipmentModes ");
				return false;
			}
			
		// end // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009
		document.forms[0].eMailId.value=document.forms[0].eMailId.value.toLowerCase();
		var roleid = trim( document.forms[0].roleId.value );
		document.forms[0].roleId.value = roleid;
		if(roleid == "")
		{
			alert('<fmt:message key="100566" bundle="${lang}"/>');
			return false;
		}
		if(document.forms[0].designation.value == "")
		{
			alert("Please enter Designation Id");
			document.forms[0].designation.focus();
			return false;
		}
		if(document.forms[0].empId.value == "")
		{
			alert("Please enter Employee Id");
			document.forms[0].empId.focus();
			return false;
		}if(document.forms[0].repOffCode.value == "")
		{
			alert("Please enter Reporting Officer's Code");
			document.forms[0].repOffCode.focus();
			return false;
		}if(document.forms[0].allotedTime.value == "")
		{
			alert("Please enter Alloted Time");
			document.forms[0].allotedTime.focus();
			return false;
		}
		if(document.forms[0].userId.value == "")
		{
			alert('<fmt:message key="100551" bundle="${lang}"/>');
			document.forms[0].userId.focus();
			return false;
		}
//@@ Commented by subrahmanyam for the Enhancement 154384 on 28/01/09
//<%		if(action!=null && (action.equalsIgnoreCase("add")|| action.equalsIgnoreCase("modify")))
		//{
//%>
			if(document.forms[0].eMailId.value == "")
			{
				alert('<fmt:message key="100567" bundle="${lang}"/>');
				document.forms[0].eMailId.focus();
				return false;
			}
			
			if(document.forms[0].eMailId.value != "")
			{	
			if(!emailCheck(document.forms[0].eMailId.value)){
			document.forms[0].eMailId.focus();
			return false;
			}
			}

<%
		}
%>
		if(document.forms[0].userName.value == "")
		{
			alert('<fmt:message key="100568" bundle="${lang}"/>');
			document.forms[0].userName.focus();
			return false;
		}

//ELG-2851
		if(document.forms[0].password != null) {
			var	passWord1	=	document.forms[0].password.value;
			var	passWord2	=	document.forms[0].confirmPassword.value;
		
			if(passWord1 != passWord2) {
				alert(checkEscape('<fmt:message key="100569" bundle="${lang}"/>'));
				document.forms[0].password.value="";
				document.forms[0].confirmPassword.value="";
				document.forms[0].password.focus();
				return false;
			}
		}
////ELG-2851
<%  if(action.equalsIgnoreCase("Add"))
	{
%>
		  
		if(document.forms[0].password.value.length !=0) {
			var	passWord1	=	document.forms[0].password.value;
			var	passWord2	=	document.forms[0].confirmPassword.value;
			var user		=	toLower(document.forms[0].userId);
			//alert(user);
			if((passWord1.length > <%=settings.get(2)%>)||(passWord1.length < <%=settings.get(1)%>)){
			alert(checkEscape('<fmt:message key="100084" bundle="${lang}"/> <%=settings.get(1)%> <fmt:message key="100081" bundle="${lang}"/> <%=settings.get(2)%> <fmt:message key="100096" bundle="${lang}"/>'));
			document.forms[0].password.focus();
			return false;
			}
			
			if (user.length != 0 && (user.indexOf(passWord1) >= 0 || passWord1.indexOf(user) >= 0)){
			alert('<fmt:message key="100107" bundle="${lang}"/>');
			document.forms[0].password.focus();
			return false;
			}
			
			if(passWord1 != passWord2) {
				alert(checkEscape('<fmt:message key="100569" bundle="${lang}"/>'));
				document.forms[0].password.value="";
				document.forms[0].confirmPassword.value="";
				document.forms[0].password.focus();
				return false;
			}else{
				
				<%
				if(settings.get(3).equals("1")){
				%>
				var check=validateCombination(document.forms[0].password);
				<%}else{%>
				var check=true;
				<%}%>

			if(check==true)
			{
				document.forms[0].eMailNotification.value=mailword(document.forms[0].password.value);
				document.forms[0].password.value=hex_sha1(document.forms[0].password.value);
				document.forms[0].confirmPassword.value=hex_sha1(document.forms[0].confirmPassword.value);
			}
			else
				return false;

			}
		}else{
			//document.forms[0].eMailNotification.value="tLxLOf";
			document.forms[0].password.value="A1MyGHTY";
		}


<%
	}
%>
	
		if(document.forms[0].lovPagingSize.value == "")
		{
			alert('<fmt:message key="100572" bundle="${lang}"/>');
			document.forms[0].lovPagingSize.focus();
			return false;
		}
		
		if(document.forms[0].segmentSize.value == "")
		{
			alert('<fmt:message key="100573" bundle="${lang}"/>');
			document.forms[0].segmentSize.focus();
			return false;
		}
		
	<%
		if (action.equalsIgnoreCase("Delete") )
		{
	%>
		var temp= confirm("Confirm Deletion?");
			
			if(temp==true)
				return true;
			else
				return false;
	<%
		}	
	%>
	//@@Added by kameswari for the WPBN issue-61303  
	if(document.forms[0].phoneNo.value=="")
		{
			alert('Please enter phoneNo');
			document.forms[0].phoneNo.focus();
			return false;
		}
		if(document.forms[0].faxNo.value=="")
		{
			alert('Please enter faxNo');
			document.forms[0].faxNo.focus();
			return false;
		}
		if(document.forms[0].mobileNo.value=="")
		{
			alert('Please enter mobileNo');
			document.forms[0].mobileNo.focus();
			return false;
		}
		
		return true;
		//@@WPBN issue-61303   
	}
	
	function toLower (input) 
	{
		s = input.value;
		filteredValues = "'";     // Characters stripped out
		var i;
		var returnString = "";
		for (i = 0; i < s.length; i++) 
		{  // Search through string and append to unfiltered values to returnString.
			var c = s.charAt(i);
			if (filteredValues.indexOf(c) == -1) returnString += c.toLowerCase();
		}
		
		return returnString;
		
	}

	function selectAllWareHouseAndRoles() {
		for(var i=0; i < document.forms[0].selectedwarehouses.length; i++) {
			var warehouse	= document.forms[0].selectedwarehouses[i].value;
			var role		= document.forms[0].selectedroles[i].value;

			var whOption		=	document.createElement("OPTION");
			whOption.value		=	whOption.text  = warehouse;
			whOption.selected	=	true;

			var roleOption		=	document.createElement("OPTION");
			roleOption.value	=	roleOption.text  = role;
			roleOption.selected	=	true;
      
			document.forms[0].selWarehouses.options.add( whOption );
			document.forms[0].selRoles.options.add( roleOption );
		}
	}


	function showLOV(url)
	{
		var	width	=	260;
		var	height	=	260;
		var	top		=	(screen.availHeight - height) / 2;
		var	left	=	(screen.availWidth  - width)  / 2;
		
		var Bars	= 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options	= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		var Features	= Bars+' '+Options;
		var Win=open(url,'Doc',Features);
		if (!Win.opener) 
			Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;
	}

	var Win = null;

	function showLOV1(url)
	{
		var	width	=	300;
		var	height	=	300;
		var	top		=	(screen.availHeight - height) / 2;
		var	left	=	(screen.availWidth  - width)  / 2;
		
		var Bars	= 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options	= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		var Features	= Bars+' '+Options;

		if(Win !=null)
		{
			Win.close();
			Win = open(url,'Doc',Features);
		}		
		else
			Win = window.open(url,'Doc',Features);

		if (!Win.opener) 
			Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;
	}

	function openWin()
	{
		var rolId = document.forms[0].roleId.value;
		var roleLocationId = document.forms[0].roleLocationId.value;
		
		if(rolId=="") {
			alert('<fmt:message key="100574" bundle="${lang}"/>');
			return false;
		}
		
		var	width	=	800;
		var	height	=	600;
		var	top		=	(screen.availHeight - height) / 2;
		var	left	=	(screen.availWidth  - width)  / 2;
		
		var myUrl	= "ESACRoleRegistrationController.jsp?action=VIEW&screen_name=user_role_entry&roleId="+rolId+"&locationId="+roleLocationId+"&isPopUp=yes&accessType=<%= userModel.getUserLevel() %>";
		var Bars	= 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options	= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=yes';
		
		var Features	= Bars+', '+Options;

		if(Win !=null)
		{
			Win.close();
			Win = open(myUrl,'MyDoc',Features);
		}		
		else
			Win = window.open(myUrl,'MyDoc',Features);
		
		if (!Win.opener) 
			Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;
	}

	function assignNewRole()
	{
		var roleModule = 0;
		
		for(var i=0; i < document.forms[0].elements.length; i++) {
			var element		= document.forms[0].elements[i];
			
			if(element.name=="module" && element.type=="checkbox" && element.checked==true) {
				var num	=	parseInt( element.value, 10 );
				if( !isNaN(num)) {
					roleModule += num;
				}
			}
		
		}
		if(roleModule==0) {
			alert('<fmt:message key="100562" bundle="${lang}"/>');
			return false;
		}
		//alert("roleModule = "+roleModule);
		
		var locId = document.forms[0].locationId.value;
		if(locId.length == 0)
		{
			alert('<fmt:message key="100561" bundle="${lang}"/>');
			document.forms[0].locationId.focus();
		}
		else
		{
			var rolId = document.forms[0].roleId.value;
			var myUrl = "ESACRoleIdLocationIdLOV.jsp?roleId="+rolId+"&roleLocationId=<%= userModel.getRoleLocationId() %>&accessType=<%= userModel.getUserLevel() %>&locationId="+locId+"&roleModule="+roleModule;
			showLOV1(myUrl);
		}
	}



	function addRoleCombination() {

		var	whIndex		=	document.forms[0].warehouses.selectedIndex;
		var	roleIndex	=	document.forms[0].roles.selectedIndex;

		// Check if both a warehouse and a role are selected
		if(whIndex==-1 || roleIndex==-1) {
			alert('<fmt:message key="100532" bundle="${lang}"/> '+'<%=whLabel%> '+' <fmt:message key="100533" bundle="${lang}"/> '+' <%=whLabel%> '+' <fmt:message key="100534" bundle="${lang}"/>');
			return false;
		}
		
		var	warehouse	=	document.forms[0].warehouses[whIndex].value;
		var	role		=	document.forms[0].roles[roleIndex].value;

		// Check if the combination is already defined

		for(var i=0; i < document.forms[0].selectedwarehouses.length; i++) {
			var selWh	= document.forms[0].selectedwarehouses[i].value;
			var selRole	= document.forms[0].selectedroles[i].value;

			if(warehouse==selWh && role==selRole) {
				alert('<fmt:message key="100535" bundle="${lang}"/>');
				return false;
			}
		}

		// Check if the role warehouse is already defined

		for(var i=0; i < document.forms[0].selectedwarehouses.length; i++) {
			var selWh	= document.forms[0].selectedwarehouses[i].value;
			var selRole	= document.forms[0].selectedroles[i].value;

			if(warehouse==selWh && role!=selRole) 
			{
				var goahead = false;
				var s1 = '<fmt:message key="100536" bundle="${lang}"/>';
				var s2 = '<fmt:message key="100537" bundle="${lang}"/>';
				var s3 = '<fmt:message key="100538" bundle="${lang}"/>';
				goahead = confirm(	s1+" '"+selRole+"' "+s2+" <%=whLabel%> '"+warehouse+"'."+"\n"+s3+" '"+role+"'");

				if(goahead==false) {
					// If no change required cancel operation
					return false;
				} else {
					// If User chooses to change the role, do it

					document.forms[0].selectedroles[i].value = role;
					document.forms[0].selectedroles[i].text = role;

					return true;
				}
			}
		}

		
		
		// If its defined ask user if he wanst to mdofy the old role added

		var whOption		=	document.createElement("OPTION");
		whOption.value		=	whOption.text  = warehouse;

		var roleOption		=	document.createElement("OPTION");
		roleOption.value	=	roleOption.text  = role;
		
		document.forms[0].selectedwarehouses.options.add( whOption );
		document.forms[0].selectedroles.options.add( roleOption );

		colorWareHouseName( "RED", warehouse)
	}

	function removeRoleCombination() {

		// Check if both a warehouse and a role are selected
		if(document.forms[0].selectedwarehouses.length==0) 
		{
			var s4='<fmt:message key="100540" bundle="${lang}"/>'
			var s5='<fmt:message key="100539" bundle="${lang}"/>'
			alert(s4+" <%=whLabel%> "+s5);
			return false;
		}

		var	whIndex		=	document.forms[0].selectedwarehouses.selectedIndex;
		var	roleIndex	=	document.forms[0].selectedroles.selectedIndex;

		// Check if both a warehouse and a role are selected
		if(whIndex==-1 || roleIndex==-1) 
		{
			var s6='<fmt:message key="100532" bundle="${lang}"/>'
			var s7='<fmt:message key="100533" bundle="${lang}"/>'
			var s8='<fmt:message key="100541" bundle="${lang}"/>'
			alert(s6+" <%=whLabel%> "+s7+" <%=whLabel%> "+s8);
			return false;
		}

		var	warehouse	=	document.forms[0].selectedwarehouses[whIndex].value;
		var	role		=	document.forms[0].selectedroles[roleIndex].value;

		for(var i=0; i < document.forms[0].selectedwarehouses.length; i++) {
			var selWh	= document.forms[0].selectedwarehouses[i].value;
			var selRole	= document.forms[0].selectedroles[i].value;

			if(warehouse==selWh && role==selRole) {
				document.forms[0].selectedwarehouses.options.remove(i);
				document.forms[0].selectedroles.options.remove(i);
				colorWareHouseName( "BLACK", selWh)

				if(i != -1) {
					if(i < document.forms[0].selectedwarehouses.length) {
						document.forms[0].selectedwarehouses.selectedIndex = i;
						document.forms[0].selectedroles.selectedIndex = i;
					}
					if(i == document.forms[0].selectedwarehouses.length) {
						document.forms[0].selectedwarehouses.selectedIndex = i-1;
						document.forms[0].selectedroles.selectedIndex = i-1;
					}
					
				}

				return true;
			}
		}
		
	}

	function sync( selectObj ) {

		var index = selectObj.selectedIndex;
		var name  = selectObj.name;

		if(index > -1) {
			if(name=="selectedwarehouses") {
				document.forms[0].selectedroles.selectedIndex = index
			}
			if(name=="selectedroles") {
				document.forms[0].selectedwarehouses.selectedIndex = index
			}
		}

	}

	function colorWareHouseName( color, warehouse) {
		
		for(var i=0; i < document.forms[0].warehouses.length; i++) {
			var selWh	= document.forms[0].warehouses[i].value;
			if(warehouse==selWh) {
				if(color=="RED") {
					document.forms[0].warehouses[i].style.color = "RED";
				} else {
					document.forms[0].warehouses[i].style.color = "BLACK";
				}
				return true;
			}
		}

	}

	function putRoles( selectObj ) {

		clearRoles();

		var	index		=	selectObj.selectedIndex;
		var warehouse	=	selectObj[ index ].value;

		if(index > -1) {
	
			for(var i=0; i < genRolesArray.length; i++) {
				var genRole = genRolesArray[i];

				var roleOption		=	document.createElement("OPTION");
				roleOption.value	=	roleOption.text  = genRole;
				
				document.forms[0].roles.options.add( roleOption );
			}

			var specRolesArray	=	whRolesArray[ warehouse ];

			for(var i=0; i < specRolesArray.length; i++)
				{
				var specRole = specRolesArray[i];

				var roleOption		=	document.createElement("OPTION");
				roleOption.value	=	roleOption.text  = specRole;

				//roleOption.style.color = "BLUE"
				
				document.forms[0].roles.options.add( roleOption );
			}

		}

	}

	function clearRoles() {

		while(document.forms[0].roles.length > 0) {
			document.forms[0].roles.options.remove(0);
		}

	}

	// Modified For the New Access Control
	function resetCheckBoxes() {
		for(var i=0; i < document.forms[0].elements.length; i++) 
        {
			if(document.forms[0].elements[i].name == "mode") 
            {
				document.forms[0].elements[i].checked = false;
				document.forms[0].elements[i].disabled = true;
			}
		}
	}
function showPopLov(lovName)
{
	 var URL  = "";
	if(lovName.name=="repOffCodeLOV")
	{		
		URL="ESACUserIdsLOV.jsp?accessType=repOfficer";
	}
	if(lovName.name=="desiIdLOV")
	{		
		URL="QMSDesignationLOV.jsp?searchString="+document.forms[0].designation.value;
	}
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;	
	var Win=open(URL,'Doc',Features);
}

function IsValidTime(field)
	{
		timeStr=field.value
		if(timeStr.length!=0)
		{
			var	timePat	= /^(\d{1,2}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;
			if(timeStr.length==4 && timeStr.indexOf(':')==-1)
			{
				timeStr = timeStr.substring(0,2)+':'+timeStr.substring(2,4);
				field.value=timeStr;


			}

			var	matchArray = timeStr.match(timePat);
			if (matchArray == null)
			{
				alert("Please enter in HH:MM format");
				field.focus();
			}
			else
			{
				hour = matchArray[1];
				minute = matchArray[2];
				second = matchArray[4];
				ampm = matchArray[6];
				if (second=="")	{ second = null; }
				if (ampm=="") {	ampm = null	}
				if (hour < 0  || hour >	99)	{
				alert("Please enter correct Hours");
				field.focus();
				}
				else if	(minute<0 || minute	> 59) {
					alert ("Please enter correct Minutes.");
					field.focus();
				}
			}
		}
	}
//email Validation
function emailCheck (emailStr) {

/* The following variable tells the rest of the function whether or not
to verify that the address ends in a two-letter country or well-known
TLD.  1 means check it, 0 means don't. */

var checkTLD=1;

/* The following is the list of known TLDs that an e-mail address must end with. */

var knownDomsPat=/^(com|net|org|edu|int|mil|gov|arpa|biz|aero|name|coop|info|pro|museum)$/;

/* The following pattern is used to check if the entered e-mail address
fits the user@domain format.  It also is used to separate the username
from the domain. */

var emailPat=/^(.+)@(.+)$/;

/* The following string represents the pattern for matching all special
characters.  We don't want to allow special characters in the address. 
These characters include ( ) < > @ , ; : \ " . [ ] */

var specialChars="\\(\\)><@,;:\\\\\\\"\\.\\[\\]";

/* The following string represents the range of characters allowed in a 
username or domainname.  It really states which chars aren't allowed.*/

var validChars="\[^\\s" + specialChars + "\]";

/* The following pattern applies if the "user" is a quoted string (in
which case, there are no rules about which characters are allowed
and which aren't; anything goes).  E.g. "jiminy cricket"@disney.com
is a legal e-mail address. */

var quotedUser="(\"[^\"]*\")";

/* The following pattern applies for domains that are IP addresses,
rather than symbolic names.  E.g. joe@[123.124.233.4] is a legal
e-mail address. NOTE: The square brackets are required. */

var ipDomainPat=/^\[(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})\]$/;

/* The following string represents an atom (basically a series of non-special characters.) */

var atom=validChars + '+';

/* The following string represents one word in the typical username.
For example, in john.doe@somewhere.com, john and doe are words.
Basically, a word is either an atom or quoted string. */

var word="(" + atom + "|" + quotedUser + ")";

// The following pattern describes the structure of the user

var userPat=new RegExp("^" + word + "(\\." + word + ")*$");

/* The following pattern describes the structure of a normal symbolic
domain, as opposed to ipDomainPat, shown above. */

var domainPat=new RegExp("^" + atom + "(\\." + atom +")*$");

/* Finally, let's start trying to figure out if the supplied address is valid. */

/* Begin with the coarse pattern to simply break up user@domain into
different pieces that are easy to analyze. */

var matchArray=emailStr.match(emailPat);

if (matchArray==null) {

/* Too many/few @'s or something; basically, this address doesn't
even fit the general mould of a valid e-mail address. */

alert('<fmt:message key="100615" bundle="${lang}"/>');
return false;
}
var user=matchArray[1];
var domain=matchArray[2];

// Start by checking that only basic ASCII characters are in the strings (0-127).

for (i=0; i<user.length; i++) {
if (user.charCodeAt(i)>127) {
alert('<fmt:message key="100616" bundle="${lang}"/>');
return false;
   }
}
for (i=0; i<domain.length; i++) {
if (domain.charCodeAt(i)>127) {
	alert('<fmt:message key="100617" bundle="${lang}"/>');
return false;
   }
}

// See if "user" is valid 

if (user.match(userPat)==null) {

// user is not valid
alert('<fmt:message key="100618" bundle="${lang}"/>');
return false;
}

/* if the e-mail address is at an IP address (as opposed to a symbolic
host name) make sure the IP address is valid. */

var IPArray=domain.match(ipDomainPat);
if (IPArray!=null) {

// this is an IP address

for (var i=1;i<=4;i++) {
if (IPArray[i]>255) {
	alert('<fmt:message key="100619" bundle="${lang}"/>');

return false;
   }
}
return true;
}

// Domain is symbolic name.  Check if it's valid.
 
var atomPat=new RegExp("^" + atom + "$");
var domArr=domain.split(".");
var len=domArr.length;
for (i=0;i<len;i++) {
if (domArr[i].search(atomPat)==-1) {
	alert('<fmt:message key="100620" bundle="${lang}"/>');
return false;
   }
}

/* domain name seems valid, but now make sure that it ends in a
known top-level domain (like com, edu, gov) or a two-letter word,
representing country (uk, nl), and that there's a hostname preceding 
the domain or country. */

if (checkTLD && domArr[domArr.length-1].length!=2 && 
domArr[domArr.length-1].search(knownDomsPat)==-1) {
alert('<fmt:message key="100621" bundle="${lang}"/>');
return false;
}

// Make sure there's a host name preceding the domain.

if (len<2) {
alert('<fmt:message key="100622" bundle="${lang}"/>');
return false;
}

// If we've gotten this far, everything's valid!
return true;
}
function openReportingOfficerLov(obj,i)
{
	//alert(obj.value);
	//alert(i);
	if(document.forms[0].designation.value.length==0)
	{
		alert("Please Enter/Select Designation Id");
		document.forms[0].designation.focus();
		return false;
	}
	
	
	var tabArray = 'EMPID,USERNAME,DESIGNATION_ID';
	var formArray = "repOffCode"+i+",repOffName"+i+",superDesignationId"+i+"";


	//var lovWhere  = " where ORG_ID='<%//=sessOrgid%>' and STATUS='ACTIVE'";
	var lovWhere	=	"";
	//alert('<%=request.getContextPath()%>');
  // added localTerminal,localAcceslevel by VLAKSHMI on 22/05/2009
	Url		="qms/ListOfValues.jsp?lovid=REPORTING_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation=REPORTINGOFF&designationID="+document.forms[0].designation.value+"&localTerminal="+document.forms[0].locationId.value+"&localAcceslevel="+document.forms[0].accessType.value+"&search= where EMPID LIKE '"+obj.value+"~'";
	Bars	='directories=0,location=0,menubar=no,status=0,titlebar=0,scrollbars=0';
	Options	='width=800,height=750,resizable=0';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
 }

/*function numericOnly(input) 
{
		/*s = input.value;
		filteredValues = "''~!@#$%^&*()_-+=|\:;<>,/?.";
		filteredValues1 = "1234567890";
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
	
}*/
</script>
<%
if (!FoursoftWebConfig.MODULE.equals(FoursoftWebConfig.ETRANS))
{

   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
}
%>

</head>
<body onLoad="checkbox();">
<!-- modified For New Access Control-->
<form method="POST" action="<%= actionPage %>" name="userreg" onSubmit="return checkForm();"onReset="resetCheckBoxes();">

<table border="0" cellPadding="4" cellSpacing="1" width="100%" bgcolor='#FFFFFF'>
<tr>
 <td>
	<table width="100%" cellpadding="0" cellspacing="0">
		  <tr valign="top" class="formlabel">
			<td	colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td><fmt:message key="100271" bundle="${lang}"/> - <%= actionLabel %></td><td align=right><%=loginbean.generateUniqueId(fileName,actionLabel)%></td></tr></table></td>
	</tr></table>
	
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		  
		 <input type=hidden name="eMailNotification">

		<tr valign="top" class="formdata"> 
		<%		//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	  
          <td  width="50%" colspan=2><%= locationIdLeble %> :<font color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
              <input type="text" class='text' name="locationId" size="16" readonly  maxlength="16"  value="<%= userModel.getLocationId().toUpperCase() %>">
<%    }
			  else 
			  {
%>				 
               <td  width="50%" colspan=2><%= locationIdLeble %> :<font color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
              <input type="text" class='text'  name="locationId" size="16" readonly  maxlength="16"  value="<%= userModel.getLocationId() %>">
<%
			  }
%>			  

          </td>
<%		//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	  
          <td  width="50%"> <fmt:message key="100200" bundle="${lang}"/><br>
              <input type="text" class='text'  name="accessType"  value="<%= userModel.getUserLevel() %>" size="16" readonly  maxlength="16" >
		  </td>
<%    }
        else 
		{
%>            <td  width="50%"> <fmt:message key="100200" bundle="${lang}"/><br>
              <input type="text" class='text'  name="accessType"  value="<%= userModel.getUserLevel() %>" size="16" readonly  maxlength="16" >
		  </td>
<%     }
%><td>		  </td>
<!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
<td>		  </td>

		</tr>
		
		<tr valign="top" class="formdata"> 
<% //if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>		  
		  <td  width="50%" colspan=2><fmt:message key="100201" bundle="${lang}"/><br>
			<input type="text" class='text'  name="roleModule" size="20" value="<%= roleModule  %>" readonly>
		  </td>
		
<%		}else
		{
%>	
           <td  width="50%" colspan=2><fmt:message key="100201" bundle="${lang}"/><br>
			<input type="text" class='text'  name="roleModule" size="20" value="<%= roleModule  %>" readonly>
		  </td>
<%  }
%>
<%		//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>		   <td  width="50%"><fmt:message key="100202" bundle="${lang}"/><br>
			<input type="text" class='text'  name="companyId" size="20" maxlength="16" value="<%= userModel.getCompanyId() == null ? "" : userModel.getCompanyId() %>" onBlur="stringFilter(this)" <%= readOnly %> >
		  </td>
		  <td>		  </td>
            <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
		  <td>		  </td>
				</tr>
		
<%		}else
		{
%>		
           <td  width="50%"><fmt:message key="100202" bundle="${lang}"/><br>
			<input type="text" class='text' name="companyId" size="20" maxlength="16" value="<%= userModel.getCompanyId() == null ? "" : userModel.getCompanyId() %>" onBlur="stringFilter(this)" readonly >
		  </td>
		  <td>		  </td>
            <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
		  <td>		  </td>

		</tr>
<%
		}
%>
				   
		
		<tr valign="top" class="formdata"> 
          <td  width="33%"><fmt:message key="100203" bundle="${lang}"/><font color="#FF0000"><fmt:message key="100204" bundle="${lang}"/></font><br>
<%		//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	
			<input type="text" class='text'  name="userId" size="20" maxlength="12" value="<%= userModel.getUserId() == null ? "" : userModel.getUserId() %>" onBlur="stringFilter(this)" <%= readOnly %> >
			  </td>
<%  }
	  else 
	  {
		   if (action!=null &&(action.equalsIgnoreCase("modify")))
		  {

       %> <input type="text"  class='text'  name="userId" size="20" maxlength="12" value="<%=userModel.getUserId() == null ? "" : userModel.getUserId() %>" onBlur="stringFilter(this)" <%= readOnly %> >
         </td>
	<%
		  }
		else {

%>     <input type="text" class='text'  name="userId" size="20" maxlength="12" value="<%=userModel.getUserId() == null ? "" : userModel.getUserId() %>" onBlur="stringFilter(this)" <%= readOnly %> >
         </td>
<%

            } 
			
 }
%>
		
<%		//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	  <td  width="33%"><fmt:message key="100205" bundle="${lang}"/><font 		color="#FF0000"><fmt:message key="9999" bundle="${lang}"/> </font><br>
			<input type="text" class='text' name="eMailId" size="30" maxlength="45" value="<%= userModel.getEMailId() == null ? "" : userModel.getEMailId() %>"<%= readOnly %> >
		  </td>
		
<%		}else
		{
%>		
      <td  width="33%"><fmt:message key="100205" bundle="${lang}"/><font color="#FF0000"><fmt:message key="9999" bundle="${lang}"/> </font><br>
			<input type="text" class='text' name="eMailId" size="30" maxlength="45" value="<%= userModel.getEMailId() == null ? "" : userModel.getEMailId() %>" >
		  </td>    
<%
		}
%>
<%		//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	 
	 <td  width="34%"><fmt:message key="100206" bundle="${lang}"/><font color="#FF0000"><fmt:message key="100207" bundle="${lang}"/></font><br>
			<input type="text" class='text' name="userName" size="30" maxlength="30" value="<%= userModel.getUserName() == null ? "" : userModel.getUserName() %>" onBlur="stringFilter(this)" <%= readOnly %>>
		  </td>
		  <td>		  </td>
            <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
		  <td>		  </td>

		</tr>	
<%		}else
		{
%>		

      <td  width="34%"><fmt:message key="100206" bundle="${lang}"/><font color="#FF0000"><fmt:message key="100207" bundle="${lang}"/></font><br>
			<input type="text" class='text' name="userName" size="30" maxlength="30" value="<%= userModel.getUserName() == null ? "" : userModel.getUserName() %>" onBlur="stringFilter(this)" >
		  </td>
		  <td>		  </td>
            <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
		  <td>		  </td>

		</tr>
<%
		}
%>
	  
        
<%		 
	if(action.equalsIgnoreCase("add") )
	{
%>				  
		<tr valign="top" class="formdata"> 
		    <!-- // colspan=3 added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
		  <td  width="50%" colspan=3 ><fmt:message key="100211" bundle="${lang}"/><font color="#FF0000"></font><br>

			<input type="password" name="password" maxlength="20" size="20" class='text'>
		  </td>
		  <td  width="50%"><fmt:message key="100212" bundle="${lang}"/><font color="#FF0000"></font><br>
              <input type="password" name="confirmPassword"  maxlength="20" size="20" class='text'>
		  </td>
		  <td></td>
            <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
		  <td></td>

		</tr>
		<!-- @@ Added by Subrahmanyam for 167668  on 27/04/09 -->
			<tr valign="top" class="formdata"> 
		<td>Address Line1<br>
			<input type="text" name="custaddr1" maxlength="100" size="30" class='text' onBlur="stringFilter(this)">
		</td>
		 <td>Address Line2<br>
			 <input type="text" name="custaddr2" maxlength="100" size="30" class='text' onBlur="stringFilter(this)">
		</td>
		 <td>Address Line3<br>
			 <input type="text" name="custaddr3" maxlength="100" size="30" class='text' onBlur="stringFilter(this)">
		 </td>
		 <td></td>
     <td></td>
	   </tr>
		<!-- @@ Ended by subrahmanyam for 167668  on 27/04/09 -->
		  <!--@@Added by Kameswari for WPBN issue-61303-->  
       <tr valign="top" class="formdata"> 
		<td>Phone<font color='red'>*</font><br>
			<input type="text" name="phoneNo" maxlength="20" size="20" class='text' >
		</td>
		 <td>Fax<font color='red'>*</font><br>
			 <input type="text" name="faxNo" maxlength="20" size="20" class='text' >
		</td>
		 <td>Mobile<font color='red'>*</font><br>
			<input type="text" name="mobileNo" maxlength="20" size="20" class='text'>
		 </td>
		 <td></td>
          <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
		 <td></td>
    <td></td>
	   </tr>
	     <!--@@WPBN issue-61303-->  
		<tr valign="top" class="formdata"> 


<%	//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	 
	 <td  width="33%"><fmt:message key="100208" bundle="${lang}"/><font color='red'>*</font><br>
            <input type="text" class='text' name="empId" size="20" maxlength="25"  value="<%= userModel.getEmpId() == null ? "" : userModel.getEmpId() %>" onBlur="stringFilter(this)" <%= readOnly %>>
		  </td>
	
<%		}else
		{
%>		

     <td  width="33%"><fmt:message key="100208" bundle="${lang}"/><font color='red'>*</font><br>
            <input type="text" class='text' name="empId" size="20" maxlength="16"  value="<%= userModel.getEmpId() == null ? "" : userModel.getEmpId() %>" onBlur="stringFilter(this)" <%=readOnly1%>>
		  </td> 
<%
		}
%>	

<%	//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	 <td  width="34%"><fmt:message key="100210" bundle="${lang}"/><br>
            <input type="text" class='text' name="department" size="30" maxlength="45" value="<%= userModel.getDepartment() == null ? "" : userModel.getDepartment() %>" onBlur="stringFilter(this)" <%= readOnly %>>
		  </td>
		
	 
<%		}else
		{
%>		

    <td  width="34%"><fmt:message key="100210" bundle="${lang}"/><br>
            <input type="text" class='text' name="department" size="30" maxlength="45" value="<%= userModel.getDepartment() == null ? "" : userModel.getDepartment() %>" onBlur="stringFilter(this)" >
		  </td>
		
<%
		}
%>
          <td width="33%" >Designation Id:<font color='red'>*</font><br><input type='text'  class='text' name='designation' >
		  <input type='button' class='input' value='...' name='desiIdLOV' onClick='showPopLov(desiIdLOV)'  ></td>
		  <td>Level No:<br><input type='text' class='text' name='level' size='5' readOnly ></td>
            <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
		  <td ></td>

		  </tr>

			  <TR class="formdata">
<%
				UserAccessConfig accessConfig	= UserAccessConfigFactory.getUserAccessConfig();
				modules = accessConfig.getModules();
				int		moduleIndexes[] = accessConfig.getModuleIndexes();
%>
				<TD align=left valign="middle" rowspan='<%=modules.length%>'>
				<fmt:message key="100214" bundle="${lang}"/><br>
<%
				for(int i=0; i < modules.length; i++)	{ 
					String operationModule = UserAccessConfig.getModuleLabel( modules[i] );
					if(operationModule.equalsIgnoreCase("ETRANS"))
						operationModule = "QMS";
					%>		
					<input type="checkbox" name="module" value="<%= moduleIndexes[i] %>"><%= operationModule %><br>
<%				}
%>
				</TD>
				<TD align="center" valign="middle">
					<input type="button" class="input" value='<fmt:message key="8893" bundle="${lang}"/>' name="roleBtn" onClick = 'assignNewRole()'>
				</TD>
				<TD rowspan='<%=modules.length%>'>
					<fmt:message key="100213" bundle="${lang}"/>
					<BR>
					<input type="text" class='text' name="roleId" size="20"  value = "<%= userModel.getRoleId() == null ? "" : userModel.getRoleId() %>"  readonly >
				</TD>
				<TD rowspan='<%=modules.length%>'>
				  <font face="Verdana" size="2"><fmt:message key="100215" bundle="${lang}"/> <br>			
					<input type="text" class='text' name="roleDescription" value = "<%= roleDescription %>"  readonly >
				</font>
					<input type="hidden" name="roleLocationId" value = "" readOnly>
					&nbsp;
				</TD>
        <TD rowspan='<%=modules.length%>'></TD>
				</tr>
		  <tr class='formdata'>
		  
		  <td align='center' valign='middle'>
			<input type="button"  value='<fmt:message key="8892" bundle="${lang}"/>' name="roleBtn" onClick = 'openWin()' class="input">
		  </td>
	      </tr>
		

<%
	}
	else
	{
%>

         <!--@@Added by Kameswari for WPBN issue-61303-->  
	 <tr valign="top" class="formdata"> 
	 <%if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
	{%>
<!-- @@ Added by Subrahmanyam for 167668  on 27/04/09 -->
			
		<td>Address Line1<br>
			<input type="text" name="custaddr1" maxlength="100" size="30" class='text' value="<%= (userModel.getCustAddr1()!=null?userModel.getCustAddr1():"" )%>" readonly >
		</td>
		 <td>Address Line2<br>
			 <input type="text" name="custaddr2" maxlength="100" size="30" class='text' value="<%= (userModel.getCustAddr2()!=null?userModel.getCustAddr2():"") %>" readonly>
		</td>
		 <td>Address Line3<br>
			 <input type="text" name="custaddr3" maxlength="100" size="30" class='text' value="<%= (userModel.getCustAddr3()!=null?userModel.getCustAddr3():"") %>" readonly>
		 </td>
		 <td></td>
		 <td></td>
	   </tr>
	   <tr valign="top" class="formdata"> 
<!-- @@ Ended by subrahmanyam for 167668  on 27/04/09 -->
		<td>Phone<font color='red'>*</font><br>
			<input type="text" name="phoneNo" maxlength="20" size="20" class='text' value="<%= (userModel.getPhoneNo()!=null)?userModel.getPhoneNo():"" %>" readOnly>
		</td>
		 <td>Fax<font color='red'>*</font><br>
			 <input type="text" name="faxNo" maxlength="20" size="20" class='text' value="<%= (userModel.getFaxNo()!=null)?userModel.getFaxNo():""%>"  readOnly>
		</td>
		 <td>Mobile<font color='red'>*</font><br>
			<input type="text" name="mobileNo" maxlength="20" size="20" class='text' value="<%= (userModel.getMobileNo()!=null)? userModel.getMobileNo():""%>"  readOnly>
		 </td>
          <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
		 <td></td>

		 <td></td>
		 
	 <%}
     else
	 {%>
	 <!-- @@ Added by Subrahmanyam for 167668  on 27/04/09 -->
			
		<td>Address Line1<br>
			<input type="text" name="custaddr1" maxlength="100" size="30" class='text' value="<%= (userModel.getCustAddr1()!=null?userModel.getCustAddr1():"" )%>" onBlur="stringFilter(this)">
		</td>
		 <td>Address Line2<br>
			 <input type="text" name="custaddr2" maxlength="100" size="30" class='text' value="<%= (userModel.getCustAddr2()!=null?userModel.getCustAddr2():"") %>" onBlur="stringFilter(this)">
		</td>
		 <td>Address Line3<br>
			 <input type="text" name="custaddr3" maxlength="100" size="30" class='text' value="<%= (userModel.getCustAddr3()!=null?userModel.getCustAddr3():"") %>" onBlur="stringFilter(this)">
		 </td>
		 <td></td>
		 <td></td>
	   </tr>
	   <tr valign="top" class="formdata"> 
<!-- @@ Ended by subrahmanyam for 167668  on 27/04/09 -->
	 <td>Phone<font color='red'>*</font><br>
			<input type="text" name="phoneNo" maxlength="20" size="20" class='text' value="<%= (userModel.getPhoneNo()!=null)?userModel.getPhoneNo():"" %>" >
		</td>
		 <td>Fax<font color='red'>*</font><br>
			 <input type="text" name="faxNo" maxlength="20" size="20" class='text' value="<%= (userModel.getFaxNo()!=null)?userModel.getFaxNo():""%>">
		</td>
		 <td>Mobile<font color='red'>*</font><br>
			<input type="text" name="mobileNo" maxlength="20" size="20" class='text' value="<%=(userModel.getMobileNo()!=null)? userModel.getMobileNo():""%>">
		 </td>
		 <td></td>
          <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
		 <td></td>

	<%}%>
	</tr>
	<!--@@WPBN issue-61303-->  
		  <tr valign="top" class="formdata"> 


<%	//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	 
	 <td  width="33%"><fmt:message key="100208" bundle="${lang}"/><font color='red'>*</font><br>
            <input type="text" class='text' name="empId" size="20" maxlength="16"  value="<%= userModel.getEmpId() == null ? "" : userModel.getEmpId() %>" onBlur="stringFilter(this)" <%= readOnly %>>
		  </td>
	
<%		}else
		{
%>		

     <td  width="33%"><fmt:message key="100208" bundle="${lang}"/><font color='red'>*</font><br>
            <input type="text" class='text' name="empId" size="20" maxlength="16"  value="<%= userModel.getEmpId() == null ? "" : userModel.getEmpId() %>" onBlur="stringFilter(this)" <%=readOnly1%>>
		  </td> 
<%
		}
%>	

<%	//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	 <td  width="34%"><fmt:message key="100210" bundle="${lang}"/><br>
            <input type="text" class='text' name="department" size="30" maxlength="45" value="<%= userModel.getDepartment() == null ? "" : userModel.getDepartment() %>" onBlur="stringFilter(this)" <%= readOnly %>>
		  </td>
		
	 
<%		}else
		{
%>		

    <td  width="34%"><fmt:message key="100210" bundle="${lang}"/><br>
            <input type="text" class='text' name="department" size="30" maxlength="45" value="<%= userModel.getDepartment() == null ? "" : userModel.getDepartment() %>" onBlur="stringFilter(this)" >
		  </td>
		
<%
		}
%>
          <td width="33%" >Designation Id:<font color='red'>*</font><br><input type='text' class='text' name='designation' value='<%= userModel.getDesignationId() == null ? "" : userModel.getDesignationId() %>' <%=readFlag%> >
		<input type='button' class='input' value='...' name='desiIdLOV' onClick='showPopLov(desiIdLOV)'  <%=disable%>></td>
		  <td>Level No:<br><input type='text' size='5' name='level' class='text' value="<%= userModel.getDesignationLevel() == null ? "" : userModel.getDesignationLevel() %>" readOnly></td>
       <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
      <td></td>
		  </tr>
<%
			if(action.equalsIgnoreCase("Modify") )
			{
%>
			  <TR class="formdata">
<%
				UserAccessConfig accessConfig	= UserAccessConfigFactory.getUserAccessConfig();
				modules = accessConfig.getModules();
				int		moduleIndexes[] = accessConfig.getModuleIndexes();
%>
				<TD align=left valign="middle" rowspan='<%=modules.length%>'>
				<fmt:message key="100214" bundle="${lang}"/><br>
<%
				for(int i=0; i < modules.length; i++)	{ 
					String operationModule = UserAccessConfig.getModuleLabel( modules[i] );
					String checked="";
					if(operationModule.equalsIgnoreCase("ETRANS"))
						operationModule = "QMS";
					 if(i==0)
	                 { 
					    if(roleModule.equalsIgnoreCase("AD") || roleModule.equalsIgnoreCase("AD-ET")) 
					     checked ="checked";
				     }
					 else
                     { 
						if(roleModule.equalsIgnoreCase("ET") || roleModule.equalsIgnoreCase("AD-ET")) 
						  checked ="checked"; 
					 }
					%>		
					<input type="checkbox" name="module" value="<%= moduleIndexes[i] %>" <%=checked%> ><%= operationModule %><br>
<%				}
%>
				</TD>
				<TD align="center" valign="middle">
					<input type="button" class="input" value='<fmt:message key="8894" bundle="${lang}"/>'" name="roleBtn" onClick = 'assignNewRole()'>
					<!--<input type="button"  value="View Role Permissions" name="roleBtn" onClick = 'openWin()' class="input">-->
				</TD>
				<TD rowspan='<%=modules.length%>'>
					<fmt:message key="100213" bundle="${lang}"/>
					<BR>
					<input type="text" class='text' name="roleId" size="20"  value = "<%= userModel.getRoleId() == null ? "" : userModel.getRoleId() %>"  readonly >
				</TD>
				<TD rowspan='<%=modules.length%>'>
				  <font face="Verdana" size="2"><fmt:message key="100215" bundle="${lang}"/> <br>			
					<input type="text" class='text' name="roleDescription" value = "<%= roleDescription %>"  readonly >
				</font>
					<input type="hidden" name="roleLocationId" value = "<%= userModel.getRoleLocationId() == null ? "" : userModel.getRoleLocationId() %>" >
					&nbsp;
				</TD>
        <TD></TD>
        <TD></TD>
			  </TR>
			  <tr class='formdata'>
		  
		  <td align='center' valign='middle'>
			<input type="button"  value='<fmt:message key="8892" bundle="${lang}"/>' name="roleBtn" onClick = 'openWin()' class="input">
		  </td>
		  <td></td>
	      </tr>
<%
			}
		   else if("View".equalsIgnoreCase(action))
		   {
%>			
			<tr class='formdata'>
		  
				<TD>
					<fmt:message key="100213" bundle="${lang}"/>
					<BR>
					<input type="text" class='text' name="roleId" size="20"  value = "<%= userModel.getRoleId() == null ? "" : userModel.getRoleId() %>"  readonly >
				</TD>
				<td align='center' valign='middle'>
					<input type="button"  value='<fmt:message key="8892" bundle="${lang}"/>' name="roleBtn" onClick = 'openWin()' class="input">
				</td>
				<TD >
				  <font face="Verdana" size="2"><fmt:message key="100215" bundle="${lang}"/> <br>			
					<input type="text" class='text' name="roleDescription" value = "<%= roleDescription %>"  readonly size='30'>
				</font>
					<input type="hidden" name="roleLocationId" value = "<%= userModel.getRoleLocationId() == null ? "" : userModel.getRoleLocationId() %>" >
					&nbsp;
				</TD>
				   <!-- // added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
				<TD></TD>
				<TD></TD>
			</tr>
<%		   }
%>		
<%
	}

%>		  </tr>	
              <tr class="formlabel" vAlign="top">
                              <!-- colSpan="8" added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
                <td  colSpan="8">Escalation Details</td>

              </tr>
              <!--  added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
			   <% if(action!=null && (action.equalsIgnoreCase("Add")))
			   { for(int i=0;i<3;i++)
				{%>
              <tr class="formdata" >
                <td nowrap >Reporting Officer's Code:<font color="red">*</font><br>
                  <input class="text" maxLength="20" onKeyPress='' name="repOffCode" id='repOffCode<%=i%>' size="15" value="<%= userModel.getRepOfficersCode() == null ? "" : userModel.getRepOfficersCode() %>" <%=readFlag%> >
                  <input class="input" name="repOffCodeLOV"  type="button" value="..."  onClick="openReportingOfficerLov(document.forms[0].repOffCode<%=i%>,<%=i%>)" <%=disFlag%> ></font></td>
				  <!-- onClick="showPopLov(repOffCodeLOV)" -->
                <td  >Reporting Officer's Name:<br>
                  <input class="text" maxLength="20" name="repOffName" id='repOffName<%=i%>'  size="20" value="<%= userModel.getRepOffiecersName() == null ? "" : userModel.getRepOffiecersName() %>" readOnly></font></td>
				
                <td  >Designation ID:<br><input class="text" maxLength="15" name="superDesignationId"  id='superDesignationId<%=i%>'  onkeypress="" size="15"  value="<%= userModel.getRepOffDesignation() == null ? "" : userModel.getRepOffDesignation() %>" readOnly></font></td>
                <td nowrap >Allotted Time (HH:MM):<font color="red">*</font><br>
                  <input class="text" maxLength="5" name="allotedTime" id='allotedTime<%=i%>'   onBlur='IsValidTime(this)' size="5" value="<%= userModel.getAllotedTime() == null ? "" : userModel.getAllotedTime() %>"  <%=readFlag%> ></font></td>
                <td nowrap>Shipment Mode <font color="red">*</font><br>
				<input type='checkbox' name='Air' id='Air<%=i%>' onClick="setSelectedMode('<%=i%>','Air')"  value="1">AIR 
				<input type='checkbox' name='Sea' id='Sea<%=i%>' onClick="setSelectedMode('<%=i%>','Sea')"  value="2">SEA 
				<input type='checkbox' name='Truck'  id='Truck<%=i%>' onClick="setSelectedMode('<%=i%>','Truck')"  value="4">TRUCK 
				<input type="hidden"    name="checkboxName" id='checkboxName<%=i%>' value="">
				</td>
              </tr>
<%}} else {for(int i=0;i<userModel.getRepOfficersCode2().size();i++)
				{ logger.info("userModel.getRepOfficersCode2().size()"+userModel.getRepOfficersCode2().size());%>
              <tr class="formdata" >
                <td nowrap >Reporting Officer's Code:<font color="red">*</font><br>
                  <input class="text" maxLength="20" onKeyPress='' name="repOffCode" id='repOffCode<%=i%>' size="15" value="<%= userModel.getRepOfficersCode2().get(i) == null ? "" : userModel.getRepOfficersCode2().get(i) %>" <%=readFlag%> >
                  <input class="input" name="repOffCodeLOV"  type="button" value="..."  onClick="openReportingOfficerLov(document.forms[0].repOffCode<%=i%>,<%=i%>)" <%=disFlag%> ></font></td>
				  <!-- onClick="showPopLov(repOffCodeLOV)" -->
                <td  >Reporting Officer's Name:<br>
                  <input class="text" maxLength="20" name="repOffName" id='repOffName<%=i%>'  size="20" value="<%= userModel.getRepOffiecersName2().get(i) == null ? "" : userModel.getRepOffiecersName2().get(i) %>" readOnly></font></td>
				
                <td  >Designation ID:<br><input class="text" maxLength="15" name="superDesignationId"  id='superDesignationId<%=i%>'  onkeypress="" size="15"  value="<%= userModel.getRepOffDesignation2().get(i) == null ? "" : userModel.getRepOffDesignation2().get(i) %>" readOnly></font></td>
                <td nowrap >Allotted Time (HH:MM):<font color="red">*</font><br>
                  <input class="text" maxLength="5" name="allotedTime" id='allotedTime<%=i%>'   onBlur='IsValidTime(this)' size="5" value="<%= userModel.getAllotedTime2().get(i) == null ? "" : userModel.getAllotedTime2().get(i) %>"  <%=readFlag%> ></font></td>
                <td nowrap>Shipment Mode <font color="red">*</font><br>
				<input type='checkbox' name='Air' id='Air<%=i%>'  disabled="true" onClick="setSelectedMode('<%=i%>','Air')"  value="1">AIR 
				<input type='checkbox' name='Sea' id='Sea<%=i%>' disabled="true" onClick="setSelectedMode('<%=i%>','Sea')"  value="2">SEA 
				<input type='checkbox' name='Truck'  id='Truck<%=i%>' disabled="true" onClick="setSelectedMode('<%=i%>','Truck')"  value="4">TRUCK 
				<input type="hidden"    name="checkboxName" id='checkboxName<%=i%>' value="<%= userModel.getShipmentModeCode2().get(i) == null ? "" : userModel.getShipmentModeCode2().get(i) %>">
				</td>
              </tr>
			  <%}for(int i=userModel.getRepOfficersCode2().size();i<3;i++)
				{userModel.setRepOfficersCode(null);userModel.setRepOffiecersName(null);userModel.setRepOffDesignation(null);userModel.setAllotedTime(null);%>
              <tr class="formdata" >
                <td nowrap >Reporting Officer's Code:<font color="red">*</font><br>
                  <input class="text" maxLength="20" onKeyPress='' name="repOffCode" id='repOffCode<%=i%>' size="15" value="<%= userModel.getRepOfficersCode() == null ? "" : userModel.getRepOfficersCode() %>" <%=readFlag%> >
                  <input class="input" name="repOffCodeLOV"  type="button" value="..."  onClick="openReportingOfficerLov(document.forms[0].repOffCode<%=i%>,<%=i%>)" <%=disFlag%> ></font></td>
				  <!-- onClick="showPopLov(repOffCodeLOV)" -->
                <td  >Reporting Officer's Name:<br>
                  <input class="text" maxLength="20" name="repOffName" id='repOffName<%=i%>'  size="20" value="<%= userModel.getRepOffiecersName() == null ? "" : userModel.getRepOffiecersName() %>" readOnly></font></td>
				
                <td  >Designation ID:<br><input class="text" maxLength="15" name="superDesignationId"  id='superDesignationId<%=i%>'  onkeypress="" size="15"  value="<%= userModel.getRepOffDesignation() == null ? "" : userModel.getRepOffDesignation() %>" readOnly></font></td>
                <td nowrap >Allotted Time (HH:MM):<font color="red">*</font><br>
                  <input class="text" maxLength="5" name="allotedTime" id='allotedTime<%=i%>'   onBlur='IsValidTime(this)' size="5" value="<%= userModel.getAllotedTime() == null ? "" : userModel.getAllotedTime() %>"  <%=readFlag%> ></font></td>
                <td nowrap>Shipment Mode <font color="red">*</font><br>
				<input type='checkbox' name='Air' id='Air<%=i%>' disabled="true" onClick="setSelectedMode('<%=i%>','Air')"  value="1">AIR 
				<input type='checkbox' name='Sea' id='Sea<%=i%>' disabled="true" onClick="setSelectedMode('<%=i%>','Sea')"  value="2">SEA 
				<input type='checkbox' name='Truck'  id='Truck<%=i%>' disabled="true" onClick="setSelectedMode('<%=i%>','Truck')"  value="4">TRUCK 
				<input type="hidden"    name="checkboxName" id='checkboxName<%=i%>' value="">
				</td>
              </tr>
			  <%}}%>
    <!--     added by VLAKSHMI for WPBN issue 167659 (CR) on 22/04/2009 -->
	</table>




	
	<table width="100%" cellpadding="4" cellspacing="1" border="0">
		<tr valign="top" class="formlabel"> 
		  <td  width="100%" colspan="2" ><fmt:message key="100224" bundle="${lang}"/></td>
		</tr>
	</table>
	
	<table width="100%" cellpadding="1" cellspacing="1" border="0">
		<tr valign="top" class="formdata"> 
<!-- @@ Commented by subrahmanyam for the Enhancement 154384 on 30/01/09 -->
<!-- 		<td  width="50%" ><fmt:message key="100225" bundle="${lang}"/><br> -->
<!-- @@ Added by subrahmanyam for the Enhancement 154384 on 30/01/09 -->
		  <td  width="33%" ><fmt:message key="100225" bundle="${lang}"/><br>
			<select size="1" name="dateFormat" class="select" >

<!-- Modified For New access control 2852-->
	<%
	String dateFormatLocal = (String)userPreferences.get("date_format");
	
	if((dateFormatLocal != null)&& (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
	{
	%>
				<option value="<%= dateFormatLocal %>" selected><%= dateFormatLocal %> </option>
	<%
	}
	else if((dateFormatLocal != null)&& (action.equalsIgnoreCase("modify")))
	{
		String[] dateFormat = UserPreferenceMaster.getDateFormat();

		for(int i=0; i < dateFormat.length; i++)	
		{	
			if(!dateFormatLocal.equalsIgnoreCase(dateFormat[i]) ) 
			{	
	%>		
				<option value="<%= dateFormat[i]  %>" ><%= dateFormat[i] %></option>
	<%		
			}	
			else 
			{
	%>    
				<option value="<%= dateFormat[i]  %>" selected><%= dateFormat[i] %></option>
	<%
			}	
		}
	}
	else 
	{
		String[] dateFormat = UserPreferenceMaster.getDateFormat();

		for(int i=0; i < dateFormat.length; i++)	
		{	
		%>		
				<option value="<%= dateFormat[i]  %>" ><%= dateFormat[i] %></option>
		<%		
		}	

	}
	%>
         <!-- Modified For New access control-2852-->
			  </select>
		  </td>
  <!--@@ Commented by subrahmanyam for the Enhancement 154384 on 30/01/09 -->
	<!--  <td height="25" width="50%" align="right">  -->
<!--@@ Added by subrahmanyam for the Enhancement 154384 on 30/01/09 -->
		  <td height="25" width="33%" align="right"> 
            <p align="left"><fmt:message key="100226" bundle="${lang}"/> <br>
	
			<select size="1" name="dimension" class="select"  >

<%
	String dimensionLocal = (String)userPreferences.get("dimension");
	if ((dimensionLocal != null) && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
	{
	%>
				<option value="<%= dimensionLocal %>" selected><%= dimensionLocal %></option>
	<%  
	}
	else if((dimensionLocal != null)&& (action.equalsIgnoreCase("modify")))
	{
       //dimensionLocal	= "";
	
		String[] dimension = UserPreferenceMaster.getDimension();
		for(int i=0; i < dimension.length; i++)	
		{	
			if(!dimensionLocal.equalsIgnoreCase(dimension[i]) ) 
			{	
	%>		
				<option value="<%= dimension[i]  %>" ><%= dimension[i] %></option>
	<%		
			}
			else 
			{
   %>
			   <option value="<%= dimensionLocal %>" selected><%= dimensionLocal %></option>
   <%		}
   

		}
	
	}
	else
	{
			String[] dimension = UserPreferenceMaster.getDimension();
				for(int i=0; i < dimension.length; i++)	
				{	
				%>	
						<option value="<%= dimension[i]  %>" ><%= dimension[i] %></option>
				<%			   

				}
	}
	%>

			</select>
            </p>
		  </td>   
<!-- @@Added by subrahmanyam for the Enhancement 154384 on 23/01/2009-->
<%if(action.equalsIgnoreCase("Delete") ){%>
		<td height="25" width="34%" align="left"> 
		NewSalesPerson:<br>	
			<select size="1" name="NewSalesPerson" class="select"  >
			<option value="" selected></option>
			<%for(int i=0;i<salesPersons.size();i++)
			{%>
			<option value="<%=salesPersons.get(i)%>" ><%=salesPersons.get(i)%></option>
			<%}%>
			</select>
			</td>
<%}
%>
<td>		</td>
<!-- @@Ended by subrahmanyam for the Enhancement 154384 on 23/01/2009-->

		</tr>
		
   
		  <tr valign="top" class="formdata"> 
<!-- @@ Commented by subrahmanyam for the Enhancement 154384 on 30/01/09  -->
<!-- 		  <td  width="50%" ><fmt:message key="100229" bundle="${lang}"/><br> -->
<!-- @@ Added by subrahmanyam for the Enhancement 154384 on 30/01/09  -->
		  <td  width="33%" ><fmt:message key="100229" bundle="${lang}"/><br>
<%
	String lovInStr = (String) userPreferences.get("lovPageSize");
	int lovSize = 50;
	if (lovInStr != null)
		lovSize = Integer.parseInt(lovInStr);
%>

  <!-- Modified For new access control -->

  <%		//if(action!=null && action.equalsIgnoreCase("view")and ("Delete" )
		if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	  <input type="text" class='text' name="lovPagingSize" size="6" maxlength="16"  value="<%=lovSize%>" <%= readOnly %> onKeypress="if (event.keyCode < 45 || event.keyCode > 57) event.returnValue = false;" onBlur="return intValues(this);" >
		  </td>
 <!--@@ Commened by subrahmanyam for the Enhancement 154384 on 30/01/09  -->
	<!-- <td  width="50%" ><fmt:message key="100231" bundle="${lang}"/><br> -->
 <!--@@ Added by subrahmanyam for the Enhancement 154384 on 30/01/09  -->
		  <td  width="33%" ><fmt:message key="100231" bundle="${lang}"/><br>
          
<%    }
			  else 
			  {
%>				 <input type="text" class='text' name="lovPagingSize" size="6" maxlength="16"  value="<%=lovSize%>"  onKeypress="if (event.keyCode < 45 || event.keyCode > 57) event.returnValue = false;" onBlur="return intValues(this);">
		  </td>
  <!--@@ Commened by subrahmanyam for the Enhancement 154384 on 30/01/09  -->
		<!-- <td  width="50%" ><fmt:message key="100231" bundle="${lang}"/><br> -->
 <!--@@ Added by subrahmanyam for the Enhancement 154384 on 30/01/09  -->
		  <td  width="33%" ><fmt:message key="100231" bundle="${lang}"/><br>
               
<%
			  }
%>			  
		
<%
	String segmentSize = (String) userPreferences.get("segmentSize");
	int segSize = 10;
	if (segmentSize != null)
		segSize = Integer.parseInt(segmentSize);
%>

<!-- Modified For new Access control -->
<%
if(action!=null && (action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
		{
%>	  <input type="text" class='text' name="segmentSize" size="6" maxlength="16"  value="<%=segSize%>" <%= readOnly %> onKeypress="if (event.keyCode < 45 || event.keyCode > 57) event.returnValue = false;" onBlur="return intValues(this);">
		  </td>
		  <td ></td><!-- Added by subrahmanyam for the Enhancement 154384 on 27/01/09 -->
		  
		</tr>
		<tr valign="top" class="formdata"> 
<!--@@ Commened by subrahmanyam for the Enhancement 154384 on 30/01/09  -->
		<!-- <td  width="50%" ><fmt:message key="100232" bundle="${lang}"/><font color="#FF0000"><fmt:message key="9999" bundle="${lang}"/> </font><br> -->
		  <td  width="34%" ><fmt:message key="100232" bundle="${lang}"/><font color="#FF0000"><fmt:message key="9999" bundle="${lang}"/> </font><br>
<!--@@ Added by subrahmanyam for the Enhancement 154384 on 30/01/09  -->
          
<%    }
			  else 
			  {
%>				<input type="text" class='text' name="segmentSize" size="6" maxlength="16"  value="<%=segSize%>" onKeypress="if (event.keyCode < 45 || event.keyCode > 57) event.returnValue = false;" onBlur="return intValues(this);">
		  </td>
		<td></td>  <!--@@ Added by subrahmanyam for the Enhancement 154384 on 30/01/09  -->	  
		</tr>
		<tr valign="top" class="formdata"> 
		  <td  width="50%" ><fmt:message key="100232" bundle="${lang}"/><font color="#FF0000"><fmt:message key="9999" bundle="${lang}"/> </font><br>
               
<%
			  }
%>			  
		
<!-- Modified For new acces Control -->

	 <select size="1" name="language" class="select">
<%
	String language = (String)userPreferences.get("language");
	Set      names     = supportLanguage.keySet();
	Iterator itrNames  = names.iterator();
	String   langName  = null;
	String   langValue = null;

	if ((language != null)&&(action.equalsIgnoreCase("view")|| action.equalsIgnoreCase("delete")))
	{
		while(itrNames.hasNext())
		 {
		  langName  = (String)itrNames.next();
		  langValue = (String)supportLanguage.get(langName);
		  if (langValue.equals(language))
			{   %>
			   <option value="<%=langValue%>" ><%=langName%></option>
<%	
			}		 
		 }
	}
	else
	{
		while(itrNames.hasNext())
		 {
		  langName  = (String)itrNames.next();
		  langValue = (String)supportLanguage.get(langName);
		  if (!langValue.equals(language))
			{ 
			  if(action.equalsIgnoreCase("Add")){
			  %>
			   <option value="<%=langValue%>" <%="en".equalsIgnoreCase(langValue)?"SELECTED":""%>><%=langName%></option>
<%	         }
		  else
		   {%>
               <option value="<%=langValue%>"><%=langName%></option>
		<% }}
		  else{	%>
		       <option value="<%=langValue%>" selected ><%=langName%></option>		 
<%
		   }
		 }	
    }
%>
		  </td>
		  <td></td>
		  <td>		</td><!-- Added by subrahmanyam for the Enhancement 154384 on 27/01/09 -->
		</tr>
	</table>
	
	<table width="100%" cellpadding="4" cellspacing="1" border="0">

		<tr valign="top"> 
          <td width="60%" >
			  <font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font>
              <font face="Verdana" size="1"><fmt:message key="100233" bundle="${lang}"/></font><br>
              <font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font>
              <font face="Verdana" size="1"><fmt:message key="100234" bundle="${lang}"/></font>
		  </td>
		  <td width="40%" align="right">
 			<input type="hidden" name="screen_name"  value="user_registration">
			<input type="hidden" name="action" value="<%= action %>">
			<%
			if(action.equalsIgnoreCase("View") )
			{ 
			%>
				<input type="button" class="input" value='<fmt:message key="7777" bundle="${lang}"/>' name="continue" onClick = "javascript:history.go(-1)"> 
			<%
			}
			//modified for new access control
			else if (action.equalsIgnoreCase("Delete") )
			{
			%>
			<input type="submit" class="input" value='<fmt:message key="100261" bundle="${lang}"/>' name="submit">
				
			<%
			}
			  else 
			  {
			%>
			<input type="submit" class="input" value='<fmt:message key="6666" bundle="${lang}"/>' name="submit">
			<input type="reset" class="input" value='<fmt:message key="8890" bundle="${lang}"/>' name="submit">
			<%
			}
			%>

				
		  </td>   
		</tr>
<%
	if(userModel.getVersion() > 0) {
%>
		<tr valign="top">
          <td width="60%" >
              <font face="Verdana" size="1"> <%= userModel.getModificationInfoMessage("HTML") %>
			  </font>
		  </td>
		</tr>
<%	
	}

%>
	</table>
	
  </td>
 </tr>
</table>

<SELECT size=1 name="selWarehouses" style="VISIBILITY:hidden" multiple></select>
<SELECT size=1 name="selRoles"  	style="VISIBILITY:hidden" multiple></select>

<input type="hidden" readOnly name="version" value="<%= userModel.getVersion() %>">

</form>
</body>
</html>