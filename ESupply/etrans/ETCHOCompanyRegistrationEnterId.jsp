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
/*
*	Programme Name	:ETCHOCompanyRegistrationEnterId.jsp.
*	Module    Name  :Etrans.
*	Task			:HOCompanyRegistration.
*	Sub Task		:To provide the user options of  Modify,View and Delete.
*	Author Name		:Raghavender.G
*   Date Started    :
*   Date Completed  :
*   Date Modified   : Sept 12,2001.By Ratan K.M.
*	Description     :
*   Method's Summary:
*
* This file is invoked when user selects Modify,View and Delete module in HO Registration. 
* Here user has to enter the companyId or he can select the companyId from CompanyLOV by clicking the companyLOV button. 
* @version 	1.00 19 01 2001
* @author 	Raghavender.G
*/
%>
<%@ page import	=	"org.apache.log4j.Logger"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCHOCompanyRegistrationEnterId.jsp ";
%>

<%
 logger  = Logger.getLogger(FILE_NAME);	
 try
 {
	if(loginbean.getTerminalId()==null)
	{
%>		 
   <jsp:forward page="../ESupplyLogin.jsp"/>
<%   
	}
   else
   {
%>		
<html>
<head>
<title>HOCompanyRegistrationEnterId</title>
<script language="JavaScript">
<!--
function showCompanyLOV()
{
	var Url='ETCHOCompanyRegistrationCompanyLOV.jsp?searchString='+document.forms[0].id.value.toUpperCase()+"&Operation="+document.forms[0].Operation.value;	//added by rk
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=450,height=360,resizable=No';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
	function placeFocus() 
	{
	   document.forms[0].id.focus();
	}
 function getKeyCode()
 {
  if(event.keyCode!==13)
    {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
    }
  return true;
 }
  function upperCase(input)
 	{
 	 input.value=input.value.toUpperCase();
	}
 function Mandatory()
  {
     for(i=0;i<document.forms[0].elements.length;i++)
    {
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		    document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }				
	 Id = document.forms[0].id.value;
	  
	 if(Id.length ==0)
	  {
	   alert("Please enter Company Id");
	   document.forms[0].id.focus();
	   return false;
	  }
	  else if(Id.length < 3)
	  {
		alert("Please enter three characters for Company Id");
		document.forms[0].id.focus();
		return false;
	  }
	  document.forms[0].enter.disabled='true';
	 return true;
  }
-->	 
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<%
	String param = request.getParameter("Operation");
%>
<body  OnLoad="placeFocus()" ><form method="GET" onSubmit ="return Mandatory();upperCase()"  action="ETCHOCompanyRegistrationView.jsp" name="country">
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" bgcolor="ffffff"> 
    <td  colspan="2" valign="top"> 
      <table width="800" border="0" cellspacing="1" cellpadding="4" >
        <tr class='formlabel'> 
          <td ><table width="790" border="0" ><tr class='formlabel'><td>Company - <%= param %>&nbsp;</td><td align=right><%=loginbean.generateUniqueId("ETCHOCompanyRegistrationEnterId.jsp",param)%></td></tr></table></td>
        </tr></table>
       
             
              
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="4">&nbsp;</td></tr>
			  <tr class='formheader'><td colspan="4">Enter Company Id to <%=param%> Company Information</td></tr>
                <tr class='formdata'> 
                  <td colspan="4" >Company 
                    Id:<font color="#FF0000">*</font><br>
                    <input type="text" class="text" name="id" maxlength ="3" size="8" onBlur="upperCase(this)"  onkeyPress="return getKeyCode(id)">
                    <input type="hidden" name=Operation value="<%= param %>">
                    <input type="button" class='input' value="..." name="countryIdLOV" onClick ="showCompanyLOV();">
                    </td>
                </tr>
                <tr class='denotes'> 
                  <td colspan="2" valign="top" ><font color="#FF0000">*</font>Denotes 
                    Mandatory </td>
                  <td  valign="top" align="right"> 
                      <input type="submit" value="Next>>" name="enter" class='input'>
					  <input type="reset" name="Reset" value="Reset" onClick="placeFocus()" class='input'>
                    
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
 }
 catch(Exception e)
 {
	//Logger.error(FILE_NAME,"Exception in HOCompanyRegistrationEntrId.jsp : ", e.toString());
  logger.error(FILE_NAME+"Exception in HOCompanyRegistrationEntrId.jsp : "+ e.toString());
 }
%>