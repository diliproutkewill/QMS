<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/*	Programme Name	:ETCustAddressLOV.jsp
*	Module Name		:ETrans
*	Task Name		:PRQ
*	Sub	Task Name	:Customer Address LOV.
*	Author Name		:Raghu Ram.
*	Date Started	:Aug 05th 2003.
*	Date Completed	:Aug 05th 2003.
*	Description		: This file brings all addresses of the selected Customer into LOV depending on    	     conditions it will popup the Addresses of respective Customer

*/
%>

<%@ page import =  "java.util.ArrayList,
					java.util.ResourceBundle,
					java.util.Locale,
					com.qms.setup.ejb.sls.QMSSetUpSession,
					com.qms.setup.ejb.sls.QMSSetUpSessionHome,com.foursoft.esupply.common.java.LOVListHandler,
					org.apache.log4j.Logger,javax.servlet.jsp.jstl.fmt.LocalizationContext"
%> 
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVTerminalIds.jsp ";
	
	%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
  String  language = "";
  LOVListHandler listHandler   = null;
  ArrayList requiredAttributes = null;
	ArrayList currentPageList = null;
  language = loginbean.getUserPreferences().getLanguage();
	//System.out.println("loginbean.getUserPreferences().getLanguage()"+loginbean.getUserPreferences().getLanguage());
%>

<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>

<%
	//String 	FILE_NAME		=	"ETCustAddressLOV.jsp";
	String	operation		=	request.getParameter("Operation");
	String	addrType		=	request.getParameter("addrType");
	String	searchStr		=	request.getParameter("searchString");
	String	bodyOnLoad		=	"";
	
    String checkFlag        =   request.getParameter("flag");

	if(checkFlag!=null)
	session.setAttribute("checkFlag",checkFlag);
	
	if (searchStr == null || searchStr.equals("null"))
		searchStr = "";
	
	QMSSetUpSessionHome 	home   			= 	null;
	QMSSetUpSession			remote 			= 	null;
	String 					terminalId 		=	loginbean.getTerminalId();
	ArrayList				custAddress		=	null;
	//@@ Srivegi Added on 20050503
	 response.setContentType("text/html; charset=UTF-8");
	// String language = loginbean.getUserPreferences().getLanguage();
	 //ResourceBundle rb = ResourceBundle.getBundle("../Lang_"+language);
	//@@ 20050503
	
	String checkValue=(String)session.getAttribute("checkFlag");

	
	
	try
	{			
			home   		  = (QMSSetUpSessionHome)loginbean.getEjbHome("QMSSetUpSessionBean");
			remote 		  = (QMSSetUpSession)home.create();
			if(searchStr != "")
			{
				custAddress = remote.getCustAddresses(searchStr,addrType,operation);
				bodyOnLoad = " onLoad='displayValues()'";
			}
			else
				bodyOnLoad = "";			
%>
<html>
<head>
<title>Select</title>
<script language="JavaScript" content="text/html; charset=UTF-8">
 
 	function setMasterDocNo()
 	{
		
		if(event.keyCode == 27)
		{
			window.close();
		}
		var field = document.getElementsByName("ids");
		var len	=	field.length;
		var index=0;
		if(len >1)
		{
			for(i=0;i<len;i++)
			{
				if(field[i].checked==true)
				{
					index=i;
					break;
				}
			}
		
			if(index == 0)
			{
				alert("Please Select an Id");
				return false;
			}
			else 
			{			
				var str	 =""+document.forms[0].id1[index].value+" ";
				str		+=document.forms[0].id2[index].value+" ";
				str		+=document.forms[0].id3[index].value+" ";
				str		+=document.forms[0].id4[index].value+" ";
				str		+=document.forms[0].id5[index].value+" ";
				str		+=document.forms[0].id6[index].value+" ";
				str		+=document.forms[0].id7[index].value+" ";
				//str		+=document.forms[0].id8[index].value;
				
				opener.parent.text.document.forms[0].address.value = str;
				opener.parent.text.document.forms[0].addressId.value = document.forms[0].ids[index].value;

				window.close();
			}
		}
		else
		{
			if(!document.forms[0].ids.checked)
			{
				alert("Please Select an Id");
				return false;
			}
			else
			{
				var str	 =""+document.forms[0].id1.value+" ";
				str		+=document.forms[0].id2.value+" ";
				str		+=document.forms[0].id3.value+" ";
				str		+=document.forms[0].id4.value+" ";
				str		+=document.forms[0].id5.value+" ";
				str		+=document.forms[0].id6.value+" ";
				str		+=document.forms[0].id7.value+" ";
				//str		+=document.forms[0].id8.value;
				
			   <%  if(checkValue!=null && "Quote".equalsIgnoreCase(checkValue)) { %> window.opener.document.forms[0].checkFlag.value=document.forms[0].addrType.options[document.forms[0].addrType.selectedIndex].value;
				
				window.opener.document.forms[0].contactPersons.options.length=0;
				<% } %>

				opener.parent.text.document.forms[0].address.value = str;
				opener.parent.text.document.forms[0].addressId.value = document.forms[0].ids.value;

				window.close();
			}
			
		}
  	}
	  
	function displayValues()
	{
<%
		if(searchStr != null && custAddress.size() >0) 
		{
%>		
		var field = document.getElementsByName("ids");
		var len	=	field.length;
		var index=0;
		if(len >1)
		{
			for(i=0;i<len;i++)
			{
				if(field[i].checked==true)
				{
					index=i;
					break;
				}
			}
			var str	 ="Address Line1 : "+document.forms[0].id1[index].value+"\n";
			str		+="Address Line2 : "+document.forms[0].id2[index].value+"\n";
			str		+="Address Line3 : "+document.forms[0].id3[index].value+"\n";
			str		+="City          : "+document.forms[0].id4[index].value+"\n";
			str		+="State         : "+document.forms[0].id5[index].value+"\n";
			str		+="Country       : "+document.forms[0].id6[index].value+"\n";
			str		+="Zip Code      : "+document.forms[0].id7[index].value+"\n";
			//str		+="EmailId		 : "+document.forms[0].id8[index].value+"\n";
			
			document.forms[0].list.value = str;
		}
		else
		{
			var str	 ="Address Line1 : "+document.forms[0].id1.value+"\n";
			str		+="Address Line2 : "+document.forms[0].id2.value+"\n";
			str		+="Address Line3 : "+document.forms[0].id3.value+"\n";
			str		+="City          : "+document.forms[0].id4.value+"\n";
			str		+="State         : "+document.forms[0].id5.value+"\n";
			str		+="Country       : "+document.forms[0].id6.value+"\n";
			str		+="Zip Code      : "+document.forms[0].id7.value+"\n";
			//str		+="EmailId		 : "+document.forms[0].id8.value+"\n";
			
			document.forms[0].list.value = str;
		}
<%
		}
%>
	}

	function onEnterKey()
	{
		if(event.keyCode == 13)
		{
			setMasterDocNo();
		}
		if(event.keyCode == 27){
			window.close();
		}
	}	

	function onEscKey()
	{
		if(event.keyCOde == 27)
		{
			window.close();
		}
	}

	function reSubmit()
	{
		//alert();
		document.forms[0].action	='ETCustAddressLOV.jsp?searchString='+document.forms[0].searchString.value;
		//alert(document.forms[0].addrType.options[document.forms[0].addrType.selectedIndex].value);
		//alert(document.forms[0].searchString.value);
		document.forms[0].submit();
	}

</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
			<body class='formdata' <%=bodyOnLoad%> onKeyPress='onEscKey()'>
			<form method='post'>
			<p align="center">
			<br>
			<b><%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("Addresses")%></b>
			<br>

			<center><select size=1 name="addrType" class='select' onChange="reSubmit()" onDblClick='setMasterDocNo' onKeyPress='onEnterKey()' onchange='displayValues()'> 
				<option value="" <%="".equalsIgnoreCase(addrType)?"selected":""%>></option>
				<option value="P" <%="P".equalsIgnoreCase(addrType)?"selected":""%>>Pick Up</option>
				<option value="D" <%="D".equalsIgnoreCase(addrType)?"selected":""%>>Delivery</option>
				<option value="B" <%="B".equalsIgnoreCase(addrType)?"selected":""%>>Billing</option>
			</select></center>
			<center>Address Ids</center>
			<center>
			<input type="hidden" name="searchString"  value='<%=searchStr%>'>
<%
				String sel="";
		//if(custAddress!=null)
		//{
    		if(custAddress!=null && searchStr != null && custAddress.size() >0) 
			{
			
					for(int j=0; j<custAddress.size(); j=j+9)
					{
						String addressId		= (String)custAddress.get(j);
						String addressline1		= (String)custAddress.get(j+1);
						String addressline2		= (String)custAddress.get(j+2);
						String addressline3		= (String)custAddress.get(j+3);
						String city				= (String)custAddress.get(j+4);
						String state			= (String)custAddress.get(j+5);
						String country			= (String)custAddress.get(j+6);
						String zipcode			= (String)custAddress.get(j+7);
						String op_mail			= (String)custAddress.get(j+8);
						
						if(j==0)
						{
							sel="checked";
						}
						else
						{
							sel="";
						}

%>
						
   						<input type="radio" <%=sel%>  value='<%=addressId%>'  name="ids" onclick="displayValues()"> <%=addressline1%><br>
						<!--@@modified by Kameswari-08/08/08
                        <!--<input name="id1" type="hidden" value='<%=addressline1%>'>
						<!--<input name="id1" type="hidden" value='<%=addressline2%>'>-->
						<!--<input name="id1" type="hidden" value='<%=addressline3%>'>-->
						<input name="id1" type="hidden" value="<%=addressline1%>">
						<input name="id2" type="hidden" value="<%=addressline2%>">
						<input name="id3" type="hidden" value="<%=addressline3%>">
						<input name="id4" type="hidden" value='<%=city%>'>
						<input name="id5" type="hidden" value='<%=state%>'>
						<input name="id6" type="hidden" value='<%=country%>'>
						<input name="id7" type="hidden" value='<%=zipcode%>'>
						<input name="id8" type="hidden" value='<%=op_mail%>'>
<%
				 	}
%>
				
				</center>
				<br>
				<table align=center>
				<tr>
		  		<td valign="Top" align="center" rowspan="1" colspan="9">
				<textarea name="list" rows="9" readOnly value="" cols="40"></textarea>
		  		</td>
				</tr>
				</table>
				<br>
				<center>
				<input type=button name="addButton" class='input' value='<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("Ok")%>' onClick="setMasterDocNo()">
				<input type="button" value='<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("Cancel")%>' name="B2" onClick="window.close()" class='input'>
				</center>
<%
			//}
		}
		else 
		{
			
%>
			<br>
			<center><textarea rows=6 name="ta" class='select' readOnly cols="30"><%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("No_Addresses_available")%>:</textarea></center><br>
			<center><input class='input' type="button" value='<%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("Close")%>' name="B2" onClick="window.close()"></center>
<%
		}
	}
 	catch(Exception ex)
  	{	
			ex.printStackTrace();
			//Logger.error(FILE_NAME,"Exception While get data..."+ex.toString());
      logger.error(FILE_NAME+"Exception While get data..."+ex.toString());
	
  	}
  	finally
   	{
 			home	=	null;
			remote	=	null;
	}	
%>
</form>
</body>
</html>
