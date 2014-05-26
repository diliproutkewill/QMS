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
 % File			: ESACSessionExpired.jsp
 % sub-module	: AccessControl
 % module		: esupply
 %
 % This is used to inform the SessionExpiration
 % 
 % author		: Madhu. P
 % date			: 5-09-2001
--%>
<html>

<head>
<title></title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
</head>

<body >

<form method="POST" action="" target="_top">

  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
	  <table width="760" border="0" cellspacing="1" cellpadding="4" bgcolor="#FFFFFF">
		<tr valign="top"> 
			<td class="formlabel">
			  Session Expired </td>
		  </tr>
		  <tr class="formdata" valign="top" > 
			<td > 
			  Your session Expired!<br>

			</td>
			</tr>
			<tr class="formdata" valign="top" > <td>
			Possible Reasons:<br>
			1. The ideal time duration of your session might have Exceeded. (or)<br>
			2. Some other user might have logged in with same User credentials.
			</td></tr>
		  <tr valign="top" align="right"> 
			<td width="50%"> 
				&nbsp;
			</td>
		  </tr>
	  </table>
	  </td>
	</tr>
  </table>
  
</form>
</body>
</html>
