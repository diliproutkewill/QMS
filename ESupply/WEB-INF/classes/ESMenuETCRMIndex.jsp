<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%
	  String  language = loginbean.getUserPreferences().getLanguage();
		%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<html>
<head>
<title><fmt:message key="100320" bundle="${lang}"/></title>
<% 
   //com.foursoft.esupply.common.util.BundleFile bundle = (com.foursoft.esupply.common.util.BundleFile) session.getAttribute("bundle");
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
%>
<%@ include file="/ESEventHandler.jsp" %>
</head>

<frameset rows="156,501*" frameborder="NO" border="0" framespacing="0" cols="*"> 
  <frame name="topFrame" scrolling="NO" noresize src="ESMenuETCRMTop.jsp" target="mainFrame">
  <frame name="mainFrame" src="ESMenuETCRMBottom.jsp">
</frameset>
<noframes><body bgcolor="#FFFFFF">

</body></noframes>
</html>
