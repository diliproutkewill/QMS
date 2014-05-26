<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<!--
 Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
 This software is the proprietary information of FourSoft, Pvt Ltd.
 Use is subject to license terms.
	Program Name	: ETTVendorMasterEnterId.jsp
	Module Name		: ETrans
	Task			: VendorMaster
	Sub Task		: EnterId
	Author Name		: Shailendra Chak
	Date Started	: September 22,2001
	Date Completed	: october 4,2001
	Date Modified	:
	Description		: The purpose is to capture VendorIds and to get relavent data from the database and perform relevant action.	
	Methods' Summary	: getAllVendorIds() // calling this method to get all Vendor Ids for validating VendorId entered by user.
-->
<jsp:useBean id="loginbean" scope="session" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters"/>
<%@ page import="
				
				com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.etrans.truck.setup.bean.ETransTruckingVendor"%>
<%
//	InitialContext	ictx 		= null;	  	// variable for storing JNDI Initial Context 	
	java.util.ArrayList aL      = null;
	SetUpSessionHome home = null;  //Vendor home reference
	SetUpSession remote   = null;  //Vendor remote reference
	String operation =request.getParameter("Operation"); // for storing operation
			
%>
<%
	try
	{ // try begins
	//	ictx 		= 	new InitialContext();		// variable for storing JNDI Initial Context  
		home 		= 	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");					
		remote 		= 	(SetUpSession)home.create();   			
				
		aL = remote.getAllVendorIds();				
%>
<html>
<head>
<title>Vendor Master</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
</head>
<script language="JavaScript">
function placeFocus() 
{
   document.forms[0].vendorId.focus();
}
function showLOV()
{
	var Url 	= 'ETransLOVTruckingVendorIds.jsp?whereClause='+document.forms[0].vendorId.value+'&searchString='+document.forms[0].vendorId.value+'&moduleName=VendorReg';
	var Bars 	= 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options	='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
    var Win		=open(Url,'Doc',Features);
}
function upperCase(input)
{    	
    input.value = input.value.toUpperCase();
}

function Mandatory()
{
	document.forms[0].vendorId.value=document.forms[0].vendorId.value.toUpperCase();
	vendorId = document.forms[0].vendorId.value.length;
	if(vendorId ==0)
	{
		alert("Please enter VendorId");
		document.forms[0].vendorId.focus();
		return false;
	}		
	return true;
}	
function getKeyCode()
   {
     
    if(event.keyCode == 96 || event.keyCode == 59 || event.keyCode == 39 || event.keyCode == 34 || event.keyCode ==126 )
	 {
	    return false;
     }
	     return   true  	 
   }
function checkVendorId()
       {
         document.forms[0].vendorId.value=document.forms[0].vendorId.value.toUpperCase();
		 var count = 0;
		 var aLSize = '<%= aL.size()%>';
         var formVendorId =  document.forms[0].vendorId.value.toUpperCase();
	     <%
		   for(int i=0;i<aL.size();i++)
		   {
		     
		 %>
		   var vendorId = '<%= aL.get(i)%>';
           if(vendorId != formVendorId)
		     count++;
        <%
		 }
		%>           
		 if(count == aLSize)
		 {
		    alert("Enter a valid VendorId");
			document.forms[0].vendorId.focus();
			document.forms[0].vendorId.value="";
			return false;
		 }
		
      }
</script>
<body bgcolor="#FFFFFF" onLoad="placeFocus()">
<form method="post" onSubmit="return checkVendorId()" action="ETTVendorMasterAdd.jsp" name="frm">
  <table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
        <table width="800" border="0" cellspacing="1" cellpadding="4">
          <tr class="formlabel"> 
            <td colspan="2">Vendor - <%=operation%></td>
          </tr>
		  <tr class='formdata'><td colspan="2">&nbsp;</td></tr>
          <tr class='formdata'> 
            <td colspan="2"><b>Enter Vendor Id to <%=operation%>
              Vendor Information</b></td>
          </tr>
          <tr class="formdata" valign="top"> 
            <td colspan="2">Vendor Id :<font color="ff0000">*</font><br>
              <input type="text" class="text" name="vendorId" size="16"  maxlength=16 onBlur="upperCase(vendorId)" onKeyPress="return getKeyCode()" >
              <input type="button" name="vendorIdLOV" value="..." class="input" onClick="showLOV()">
			   <input type="hidden" name=Operation value="<%= operation %>">	  
            </td>
          </tr>
          <tr  valign="top" class='denotes'> 
            <td><font color="ff0000">*</font>Denotes Mandatory</td>
            <td align="right"> 
              <input type="reset" name="Reset" value="Reset" class="input">
              <input type="submit" name="Submit" value="Submit"  class="input">
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
</body>
</html>
<%
		
	} // ends try
	 catch(Exception e)
	{
		System.out.println("TaxMasterEnterId.jsp: "+e.toString());
	}
%>
