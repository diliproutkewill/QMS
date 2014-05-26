<jsp:useBean id ="label" class="com.foursoft.esupply.common.util.BundleFile" scope ="session" />
<html>
<%
    com.foursoft.esupply.common.util.BundleFile bundle = (com.foursoft.esupply.common.util.BundleFile) session.getAttribute("bundle");
%>
<head>
<title><%=bundle.getBundle().getString("100302")%></title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<body leftmargin="5" topmargin="0" marginwidth="0" marginheight="0">
<br>
<table width="760" border="0" cellspacing="0" cellpadding="0">
	<tr valign="top" > 
		<td> 
		<table width="760" cellpadding="4" cellspacing="1">
			<tr valign="top" class="formlabel"> 
				<td width="50%" height="17">
				<%=bundle.getBundle().getString("100303")%>
				</td>
			</tr>
			<tr valign="top" class="formdata"> 
				<td width="50%" height="17">
				<%=bundle.getBundle().getString("100304")%> <b><%=bundle.getBundle().getString("100305")%></b>.<%=bundle.getBundle().getString("100306")%>
				<br>
				<font color="#FF0000"><%=bundle.getBundle().getString("100307")%></font>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</body>