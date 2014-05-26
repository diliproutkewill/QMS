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
<title> <fmt:message key="100316" bundle="${lang}"/></title>
<% 
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
%>
<%@ include file="/ESEventHandler.jsp" %>
</head>
<frameset frameborder="1" framespacing="0" border="1"rows="40,*">
  <frame name="banner" scrolling="no" border=0 noresize target="contents" src="ESMenuETCHeading.jsp">
  <frameset cols="160,*">
    <frame name="contents" target="main" src="ESMenuETCTreeGenerator.jsp">
    <frame name="main" scrolling="yes" src="html/ETCHome.html">
	
  </frameset>
  <noframes>
  <body>
  <p> <fmt:message key="100033" bundle="${lang}"/></p>
  </body>
  </noframes>
</frameset>

</html>
