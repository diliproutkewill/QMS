<%@ page import ="com.foursoft.esupply.delegate.UserRoleDelegate,com.foursoft.esupply.common.exception.FoursoftException"  contentType="text/html;charset=windows-1252"%>
<%@ page import= "com.foursoft.esupply.common.util.BundleFile,java.util.ResourceBundle,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<!--<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>-->
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
	<%!					
          UserRoleDelegate userDelegate =  null;
%>
<%
	    String language = "en";

		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>
<%
String locationId = request.getParameter("locationId");
String userId = request.getParameter("userId");
String status = "";
try
{
    userDelegate = new UserRoleDelegate();    
	status = userDelegate.upDateUserWithPassword(userId,locationId,request.getRemoteAddr());
	status = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100309")+status;
}
catch(FoursoftException exp)
{
	status = ((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("100310");
}
%>
<fmt:message key="100263" bundle="${lang}"/>
</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">

</head>
<body class='formdata' >
<form name="form" method="post">
<br><br>
<center><%=status%></center>
<p align='center'>
<input type=button value='<fmt:message key="8888" bundle="${lang}"/>' onClick='javascript:window.close()'  class='input'>
</form>
</body>
</html>
 