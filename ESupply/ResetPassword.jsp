<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import= "com.foursoft.esupply.common.util.BundleFile,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
	<%
					String  language = "";

%>

<fmt:setLocale value="en"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<html>
<head>
<title><fmt:message key="100308" bundle="${lang}"/></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="ESFoursoft_css.jsp">


<script language="JavaScript">

	function showUserIdsLOV()
	{
		var locId		=	document.f1.locationId.value;
		var	width		=	260;
		var	height		=	260;
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;
		var filter		=	document.f1.userId.value;

		var Url			= "ESACUserIdsLOV.jsp?accessType=password&locationId="+locId+"&filterString="+filter;
		var Bars		= 'directories=no, location=no,menubar=no,status=no,titlebar=no';		
		var Options		= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		if(locId.length == 0)
		{
			alert('<fmt:message key="100549" bundle="${lang}"/>');
			document.f1.locationId.focus();
			return false;
		}
		
		var Features	= Bars+' '+Options;
		var Win=open(Url,'Doc1',Features);
		if (!Win.opener) 
			this.Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;

	}

	function showLocationIdsLOV()
	{
		var	width		=	260;
		var	height		=	260;
		var	top			=	(screen.availHeight - height) / 2;
		var	left		=	(screen.availWidth  - width)  / 2;
		var filter		=	document.f1.locationId.value;
 
		var Url			= "ESACLocationIdsLOV.jsp?accessType=password&locationIdFilter="+filter;
		var Bars		= 'directories=no, location=no,menubar=no,status=no,titlebar=no';		
		var Options		= 'scrollbars=yes, width='+width+', height='+height+', top='+top+', left='+left+', resizable=no';
		
		var Features	= Bars+' '+Options;
		var Win=open(Url,'Doc1',Features);
		
		if (!Win.opener) 
			this.Win.opener = self;
		if (Win.focus != null) 
			Win.focus();
		return false;

	}

	function checkForm()
	{
		if(document.f1.userId.value == "")
		{
			alert('<fmt:message key="100551" bundle="${lang}"/>');
			document.f1.userId.focus();
			return false;
		}

    if(document.f1.locationId.value == "")
		{
			alert('<fmt:message key="100550" bundle="${lang}"/>');
			document.f1.locationId.focus();
			return false;
		}
		
		if(document.f1.locationId.value != "")
		{
			stringFilter(document.f1.locationId);
		}

		
	}

	function stringFilter (input) 
	{
		s = input.value;
		filteredValues = "'";     // Characters stripped out
		var i;
		var returnString = "";
		for (i = 0; i < s.length; i++) 
		{  
		// Search through string and append to unfiltered values to returnString.
			var c = s.charAt(i);
			if (filteredValues.indexOf(c) == -1) returnString += c.toUpperCase();
		}
		input.value = returnString;
	}

</script>
</head>

<body bgcolor="#FFFFFF">
<form name="f1" method="post" action="PasswordProcess.jsp" onSubmit="return checkForm();">
  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
        <table width="760" border="0" cellspacing="1" cellpadding="4">
          <tr class='formlabel'> 
            <td colspan="2"><fmt:message key="100082" bundle="${lang}"/></td>
          </tr>
          <tr class='formdata'> 
            <td colspan="2">&nbsp;</td>
          </tr>
          <tr class='formdata'>             
			<td><fmt:message key="100276" bundle="${lang}"/><font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
              <input type="text" name="userId" size="16" onBlur = "stringFilter(this)" >
<!--              <input type="button" name="userIdButton" value="..." onClick="return showUserIdsLOV()" class='input'>-->
            </td>
            <td><fmt:message key="100268" bundle="${lang}"/><font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font><br>
              <input type="text" name="locationId" size="16" onBlur = "stringFilter(this)" >
<!--              <input type="button" name="locationIdButton" value="..." onClick="return showLocationIdsLOV()" class='input'>-->
            </td>
          </tr>
          <tr> 
		   <td align="left">
          	<font face="verdana" size=1 color="#FF0000"><fmt:message key="9999" bundle="${lang}"/></font>
			<font face="verdana" size=1><fmt:message key="100233" bundle="${lang}"/></font> 
		  </td>
            <td colspan="2" align="right"> 
              <input type="submit" name="submit" value='<fmt:message key="6666" bundle="${lang}"/>' class='input'>
			  <input type="button" name="cancel" value='<fmt:message key="2222" bundle="${lang}"/>' onClick='javascript:window.close()' class='input'>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
</body>
</html>