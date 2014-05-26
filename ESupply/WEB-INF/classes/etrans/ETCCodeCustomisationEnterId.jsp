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
	Program Name	: ETCCodeCustomisationEnterId.jsp
	Module Name		: ETrans
	Task			: Code_Customization	
	Sub Task		: View/Modify/Delete	
	Author Name		: Sivarama Krishna .V
	Date Started	: September 11,2001
	Date Completed	: September 11,2001
	Date Modified	: 
	Description		:
						This file is used to choose a CodeId Name to Modify or View.	
	Method Summary  :
						placeFocus()  // This function is used to place the focus    	
*/
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />
<%
	String 	Operation 		= request.getParameter("Operation");
  String  actionPage    = "";

 /* if(Operation.equals("Modify"))
    actionPage = "ETCCodeCustomisationAdd.jsp";
  else  
    actionPage = "ETCCodeCustomisationView.jsp";*/
  
%>
<html>
<head>
<title>Code Customization</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<script>
	// @@ Added by  Sailaja on 2005 04 29 for SPETI-5632 & SPETI-5630
	function checkSpecialCharacters()
		{
			var keycode	= (window.Event) ? window.event.which : window.event.keyCode;

			if(keycode < 48 || keycode > 122 || (keycode > 90 && keycode < 97) || (keycode > 57 && keycode < 65))	
			{ 
				return false;
			} 
			return true;	
		}
	// @@ 2005 04 29 forSPETI-5632 & SPETI-5630
	function upper(obj) //new
	{ 
		obj.value = obj.value.toUpperCase();
	}	
	// This function is used to place the focus.
	function placeFocus()
	{
		document.forms[0].codeId.focus();	
	}	
  function getCodeIdNames()
	{
    	var	name	=	document.forms[0].codeIdName.value;
		var cd		=	document.forms[0].codeId.value;//new
		var searchString=cd; //new
		var URL 		= 'ETCLOVReportFormatNames.jsp?TerminalId=<%=loginbean.getTerminalId()%>&Operation='+document.forms[0].Operation.value+'&fromWhere=CodeCust&searchString='+cd; //new
  		var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
        var Options = 'scrollbars = yes,width = 300,height = 350,resizable = yes';
        var Features 	= Bars +' '+ Options;
        var Win 		= open(URL,'Doc',Features);
	}
</script>
<body onLoad="placeFocus()" >
<form method="GET" action="ETCCodeCustomisationView.jsp" name="frm" >
  <table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td bgcolor="#FFFFFF">
        <table width="800" border="0" cellspacing="1" cellpadding="4" >
          <tr class='formlabel'> 
            <td colspan="2"><table width="790" border="0" ><tr class='formlabel'><td>Code Customization - <%=Operation%></td><td align=right><%=loginbean.generateUniqueId("ETCCodeCustomisationEnterId.jsp",Operation)%></td></tr></table>
              </td>
          </tr></table>
		  <table width="800" border="0" cellspacing="1" cellpadding="4" >
		  <tr class="formdata"><td  colspan="2">&nbsp;</td></tr>
 			<tr valign="top" class="formdata"> 
            <td colspan="2" >Code Type:<font color="red">*</font></td>
          </tr>
          <tr valign="top" class='formdata'> 
            <td colspan="2" >
			  <% // @@ Sailaja Added onKeyPress='return checkSpecialCharacters()' on 2005 04 29 for SPETI-5632 %>
              <input name=codeId class='text' onBlur='upper(this); ' onKeyPress='return checkSpecialCharacters()' value="" maxlength = "16"> &nbsp;&nbsp;<input type="button" value="..." name="prjId"  class='input' onClick="getCodeIdNames()">
			 <% // @@ 2005 04 29 for SPETI-5632 %>
              </td>
          </tr>
           <input type="hidden" name="codeIdName" value="">
    
                </td>
          </tr>

              <input type="hidden" name="Operation" value= '<%=Operation%>' >
              </td>
          </tr></table>
		  <table width="800" border="0" cellspacing="1" cellpadding="4" >
          <tr valign="top" class="denotes"> 
            <td><font color="#FF0000">*</font>Denotes Mandatory </td>
            <td align="right">
                <input type="submit" value="Next>>" name="Enter" class='input'>
				<input type="reset" name="Reset" value="Reset" class='input'>
                
            </td>
          </tr>
		  </table>
        
      </td>
    </tr>
  </table>
</form>
</body>
</html>
