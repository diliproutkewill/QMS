<!-- Copyright ©.-->
<!-- created by Ramakumar using Pramati Studio 2.5 -->

<html>
<head>
<%
    com.foursoft.esupply.common.util.BundleFile bundle = (com.foursoft.esupply.common.util.BundleFile) session.getAttribute("bundle");
%>
<title><%=bundle.getBundle().getString("100320")%></title>
<meta http-equiv="Content-Type" content="text/html" charset="iso-8859-1">
<% 
   response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
   response.setHeader("Pragma","no-cache"); // HTTP 1.0
   response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
%>
<%@ include file="/ESEventHandler.jsp" %>
</head>
<frameset rows="156,501*" frameborder="NO" border="0" framespacing="0" cols="*"> 
  <frame name="topFrame" scrolling="NO" noresize src="html/CrmTop.html" target="mainFrame">
  <frame name="mainFrame" src="html/CrmBottom.html">
</frameset>
<noframes>
</noframes>
</html>

