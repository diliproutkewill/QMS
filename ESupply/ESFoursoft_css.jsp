<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%
	if(loginbean.getCSSId().equals("1"))
	{
%>
body {background-color: 000000; margin-left: 0px; margin-top: 0px;  margin-width: 0px; margin-height:   0px;}
.formlabel{  font-family: Verdana; font-size: 12pt; font-weight: bold; color: #ffffff;background-color: #cc0000;  vertical-align: top}
.formheader{font-family: Verdana; font-size: 10pt; font-weight: bold; color: #ffffff; background-color: #cc0000;  vertical-align: top}
.formdata {font-family: Verdana; font-size: 10pt; background-color: #ffcc00; vertical-align: top}
.denotes { font-family: Verdana; font-size: 8pt; vertical-align: top }
.input {background-color: #ffff99; border-style: solid; border-width: 1}
.select	{font-family:tahoma,san-serif;font-size:12px;color:#000066;background-color:#ffff99;}
.text {background-color:#ffffff; font-family: verdana; font-size:8pt; color=#000000; border: 1px solid #003366} 
.textHighlight {background-color:#FFFFCC; font-family: verdana; font-size:8pt; border: 1px solid #003366}
<%
	}
	else if(loginbean.getCSSId().equals("2"))
	{
%>
body {background-color: 000000; margin-left: 0px; margin-top: 0px;  margin-width: 0px; margin-height:   0px;}
.formlabel{  font-family: Verdana; font-size: 12pt; font-weight: bold; color: #ffffff;background-color: #cc0000;  vertical-align: top}
.formheader{font-family: Verdana; font-size: 10pt; font-weight: bold; color: #ffffff; background-color: #cc0000;  vertical-align: top}
.formdata {font-family: Verdana; font-size: 10pt; background-color: #ffcc00; vertical-align: top}
.denotes { font-family: Verdana; font-size: 8pt; vertical-align: top }
.input {background-color: #ffff99; border-style: solid; border-width: 1}
.select	{font-family:tahoma,san-serif;font-size:12px;color:#000066;background-color:#ffff99;}
.text {background-color:#ffffff; font-family: verdana; font-size:8pt; color=#000000; border: 1px solid #003366} 
.textHighlight {background-color:#FFFFCC; font-family: verdana; font-size:8pt; border: 1px solid #003366}
<%
	}
	else if(loginbean.getCSSId().equals("3"))
	{
%>
body {background-color: 000000; margin-left: 0px; margin-top: 0px;  margin-width: 0px; margin-height:   0px;}
.formlabel{  font-family: Verdana; font-size: 12pt; font-weight: bold; color: #ffffff;background-color: #cc0000;  vertical-align: top}
.formheader{font-family: Verdana; font-size: 10pt; font-weight: bold; color: #ffffff; background-color: #cc0000;  vertical-align: top}
.formdata {font-family: Verdana; font-size: 10pt; background-color: #ffcc00; vertical-align: top}
.denotes { font-family: Verdana; font-size: 8pt; vertical-align: top }
.input {background-color: #ffff99; border-style: solid; border-width: 1}
.select	{font-family:tahoma,san-serif;font-size:12px;color:#000066;background-color:#ffff99;}
.text {background-color:#ffffff; font-family: verdana; font-size:8pt; color=#000000; border: 1px solid #003366} 
.textHighlight {background-color:#FFFFCC; font-family: verdana; font-size:8pt; border: 1px solid #003366}
<%
	}
	else if(loginbean.getCSSId().equals("4"))
	{
%>
body {background-color: 000000; margin-left: 0px; margin-top: 0px;  margin-width: 0px; margin-height:   0px;}
.formlabel{  font-family: Verdana; font-size: 12pt; font-weight: bold; color: #ffffff;background-color: #cc0000;  vertical-align: top}
.formheader{font-family: Verdana; font-size: 10pt; font-weight: bold; color: #ffffff; background-color: #cc0000;  vertical-align: top}
.formdata {font-family: Verdana; font-size: 10pt; background-color: #ffcc00; vertical-align: top}
.denotes { font-family: Verdana; font-size: 8pt; vertical-align: top }
.input {background-color: #ffff99; border-style: solid; border-width: 1}
.select	{font-family:tahoma,san-serif;font-size:12px;color:#000066;background-color:#ffff99;}
.text {background-color:#ffffff; font-family: verdana; font-size:8pt; color=#000000; border: 1px solid #003366} 
.textHighlight {background-color:#FFFFCC; font-family: verdana; font-size:8pt; border: 1px solid #003366}
<%
	}
	else if(loginbean.getCSSId().equals("5"))
	{
%>
body {background-color: 000000; margin-left: 0px; margin-top: 0px;  margin-width: 0px; margin-height:   0px;}
.formlabel{  font-family: Verdana; font-size: 12pt; font-weight: bold; color: #ffffff;background-color: #cc0000;  vertical-align: top}
.formheader{font-family: Verdana; font-size: 10pt; font-weight: bold; color: #ffffff; background-color: #cc0000;  vertical-align: top}
.formdata {font-family: Verdana; font-size: 10pt; background-color: #ffcc00; vertical-align: top}
.denotes { font-family: Verdana; font-size: 8pt; vertical-align: top }
.input {background-color: #ffff99; border-style: solid; border-width: 1}
.select	{font-family:tahoma,san-serif;font-size:12px;color:#000066;background-color:#ffff99;}
.text {background-color:#ffffff; font-family: verdana; font-size:8pt; color=#000000; border: 1px solid #003366} 
.textHighlight {background-color:#FFFFCC; font-family: verdana; font-size:8pt; border: 1px solid #003366}
<%
	}
	else
	{
%>
body {background-color: #000000; margin-left: 0px; margin-top: 0px;  margin-width: 0px; margin-height:   0px;}
.formlabel{  font-family: Verdana; font-size: 12pt; font-weight: bold; color: #ffffff;background-color: #d00000;  vertical-align: top}
.formheader{font-family: Verdana; font-size: 10pt; font-weight: bold; color: #ffffff; background-color: #ffa206;  vertical-align: top}
.formdata {font-family: Verdana; font-size: 10pt; background-color: #efefef; vertical-align: top}
.denotes { font-family: Verdana; font-size: 8pt; vertical-align: top }
.input {background-color: #8b888b; border-style: solid; border-width: 1;  height: 17px; font-family: Verdana; font-size: 10pt;  line-height: 13px; }
.select	{font-family:tahoma,san-serif;font-size:12px;color:#000066;background-color:#d8e8fb;}
.text {background-color:#ffffff; font-family: verdana; font-size:8pt; color=#000000; border: 1px solid #003366} 
.textHighlight {background-color:#FFFFCC; font-family: verdana; font-size:8pt; border: 1px solid #003366}
<%
	}
%>