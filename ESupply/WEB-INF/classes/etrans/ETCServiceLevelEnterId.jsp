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
/**
	Program Name	: ETCServiceLevelEnterId.jsp
	Module Name		: ETrans
	Task			: ServiceLevel	
	Sub Task		: EnterId	
	Author Name		: Sivarama Krishna .V
	Date Started	: September 12,2001
	Date Completed	: September 12,2001
	Date Modified	: 
	Description		:
		This file is used to choose a Service Level ids to Modify or View.
	Method Summary	:
		placeFocus()  			//This method will focus the cursor in the serviceLevelId 
		showLOV()				//This method will popup the list of ServiceLevelIds
		stringFilter(input)		//This method will change lower case letter to upper case 
		Mandatory()				//This method will prompt the user about the Mandatory fields
		getKeyCode()			//This method will return the keycode.
*/
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ page import = "org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList" %>
<%!
  private static Logger logger = null;
%>
	
<%	session.removeAttribute("Service");
	String param=null; //String variable that represents param value
	String FILE_NAME = "ETCServiceLevelEnterId.jsp";
  logger  = Logger.getLogger(FILE_NAME);	
  
	ErrorMessage errorMessageObject  = null;
    ArrayList	 keyValueList		 = null;
	
	try
    {
	if(loginbean.getTerminalId() == null)
	{
%>
	<jsp:forward page="../ESupplyLogin.jsp" />
<%
	}
    else
	{
%>
<html>
<head>
<title>Service Level EnterId</title>
<script language="JavaScript">
<!--

	function placeFocus() 
	{
	   document.forms[0].serviceLevelId.focus();
	}
	function showLOV()
	{   
					
		if(document.forms[0].serviceLevelId.value==0)
			searchString='';
		else
		  searchString=document.forms[0].serviceLevelId.value.toUpperCase();	
	//	input=" WHERE SERVICELEVELID LIKE '"+searchString+"'";
		var Url		=	"ETCLOVServiceLevelIds.jsp?searchString="+searchString+"&shipmentMode=All&Operation="+document.forms[0].Operation.value;	//added by rk
		
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=360,height=360,resizable=no';
		var Features=Bars+' '+Options;
    	var Win=open(Url,'Doc',Features);
	}
	function stringFilter(input) 
	{
		s = input.value;
		input.value = s.toUpperCase();
	}
	function Mandatory()
	{
		document.forms[0].serviceLevelId.value=document.forms[0].serviceLevelId.value.toUpperCase();
		if(document.forms[0].serviceLevelId.value.length==0)
		{
	   		alert("Please enter ServiceLevel  Id");
	   		document.forms[0].serviceLevelId.focus();
	   		return false;
		}
		document.forms[0].enter.disabled='true';
	  	return true;	
	}	
	function getKeyCode()
 	{
  		if(event.keyCode!==13)
    	{
     		if ((event.keyCode > 31 && event.keyCode < 48)||(event.keyCode > 90 && event.keyCode < 97)||
	 			(event.keyCode > 122 && event.keyCode <127||(event.keyCode >57 && event.keyCode <65)))
	 		event.returnValue =false;
    	}
 		return true;
 	}
	//-->
</script> 
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body  onLoad="placeFocus()">
<form method="GET" onSubmit="return Mandatory()" action="ETCServiceLevelView.jsp" name="country">
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top"> 
    <td colspan="2" bgcolor="ffffff"> 
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td><table width="790" border="0" ><tr class='formlabel'><td> 
            
<%
	param = request.getParameter("Operation");
%>
        Service Level - <%=param%></td><td align=right><%=loginbean.generateUniqueId("ETCServiceLevelEnterId.jsp",param)%></td></tr></table></td></tr></table>
                  
              
			
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="2">&nbsp;</td></tr>
			  <tr class='formdata'><td colspan="2">Enter Service Level Id to <%=param%> Service Level Information:</td></tr>
                <tr class='formdata'>
				<td colspan="2">
		  Service Level Id :<font color="#FF0000">*</font><br>
		    <input type="text" class="text" name="serviceLevelId" maxlength="5" size="8"  onkeyPress="return getKeyCode(serviceLevelId)" onBlur="stringFilter(serviceLevelId)">
		    <input type="button" class='input' value="..." name="serviceIdNames" onClick="showLOV()"  >
		    <input type="hidden" name="Operation" value=<%= param %>>
                  
		    </td>
                </tr>
                <tr class='denotes'> 
                  <td ><font color="#FF0000">*</font>Denotes 
                    Mandatory </td>
                  <td align="right">
                     <input type="submit" value="Next>>" name="enter" class='input'>
					 <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'>
                     
                      
                  </td>
                </tr>
              </table>
            
          </td>
        </tr>
      </table></form>
    
</body>
</html>
<%
	}
	}catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Exception while accessing Loginbean in ServiceLevelEnterId JSP");
    logger.error(FILE_NAME+"Exception while accessing Loginbean in ServiceLevelEnterId JSP");
	    	
		errorMessageObject = new ErrorMessage("Error occured while accessing the page ","ETCServiceLevelEnterId.jsp"); 
	    keyValueList = new ArrayList();
		
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
	    keyValueList.add(new KeyValue("Operation",param)); 	
	    errorMessageObject.setKeyValueList(keyValueList);
	    request.setAttribute("ErrorMessage",errorMessageObject);
		
		/**
		session.setAttribute("ErrorCode","ERR");
		session.setAttribute("ErrorMessage","Error occured while accessing the page ");
		session.setAttribute("NextNavigation","ETCServiceLevelEnterId.jsp");
		session.setAttribute("Operation","Add");  */
%>
	<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	}
%>