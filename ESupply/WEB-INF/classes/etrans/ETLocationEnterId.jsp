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
	Program Name		: ETLocationEnterId.jsp
	Module name		    : HO Setup
	Task		        : Location
	Sub task			: View ,Modify,Delete processes
	Author Name		    : A.Hemanth Kumar
	Date Started        : September 08, 2001
	Date completed      :
    Description         :
     This file is invoked when user selects Modify/View/Delete of Locations from Menu bar of  main Tree structure .This file is
     used to select LocationId to which Details are to be modified/viewed or deleted. LocationId will be selected on clicking the LOV
     which inturn calls the LocationLOV.jsp and on clicking the Submit button it calls the LocationView.jsp screen in which
     we can see all the details for that particular given LocationId .
     This file interacts with ETransHOSuperSessionBean and then calls the method getETransHOSuperDetails
     These details are then set to the respective varaibles through Object ETransHOSuperJSPBean.

--%>
<%@ page import="org.apache.log4j.Logger"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>

<%
   String FILE_NAME ="ETLocationEnterId.jsp";
   logger  = Logger.getLogger(FILE_NAME);	
   try
   {
    // checking for TerminalId
    if(loginbean.getTerminalId() == null )
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
<title>Location Master </title>
<%@ include file="../ESEventHandler.jsp" %>
<script language="JavaScript">
<!--
	function placeFocus()
	{
	   document.forms[0].locationId.focus();
	}
function showLocationLOV()
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=450,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Url='ETCLOVLocationIds.jsp?wheretoset=locationId&searchString='+document.forms[0].locationId.value.toUpperCase()+"&shipmentMode=All&Operation="+document.forms[0].Operation.value+"&from=location";//ADDED BY RK
	var Win=open(Url,'Doc',Features);
}
function Mandatory()
{
   document.forms[0].locationId.value     = document.forms[0].locationId.value.toUpperCase();
   LocationId = document.forms[0].locationId.value;
     if(LocationId.length==0)
	{
	    alert("Please enter Location Id");
		document.forms[0].locationId.focus();
		return false;
	}
    else if(LocationId.length <3)
   {
	  alert("Please enter three characters for Location Id");
	  document.forms[0].locationId.focus();
	  return false;
   }
    document.forms[0].enter.disabled='true';
  	return true;
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
function changeToUpper(field)
{
	field.value = field.value.toUpperCase();
}

function openLocationIdLov()
{
	var tabArray = 'LOCATIONID';
	var formArray = 'locationId';
	var lovWhere	=	"";
	var operation	=	document.forms[0].Operation.value;

	if(operation.toUpperCase()=='MODIFY' || operation.toUpperCase()=='DELETE')
	{
		operation	=	"LOCSETUPMODIFY";
	}
	else if(operation.toUpperCase()=='VIEW')
	{
		operation	=	"LOCSETUPVIEW";
	}
	
	Url		="<%=request.getContextPath()%>/qms/ListOfValues.jsp?lovid=LOC_MASTER_SETUP&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&operation="+operation+"&search= where LOCATIONID LIKE '"+document.forms[0].locationId.value+"~'&&shipmentMode=All";

	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Options	='width=800,height=750,resizable=yes';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
	
}
//-->
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<%           String param = request.getParameter("Operation");
%>
<body  OnLoad="placeFocus()" >
<form method="POST" name="location" onSubmit="return Mandatory()" action="ETLocationView.jsp" >
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" bgcolor="ffffff">
    <td  colspan="2">
      <table width="800" border="0" cellspacing="1" cellpadding="4" >
        <tr class='formlabel'>
          <td ><table width="790" border="0" ><tr class='formlabel'><td>Location - <%=param%>&nbsp;</td><td align=right><%=loginbean.generateUniqueId("ETLocationEnterId.jsp",param)%></td></tr></table></td>
        </tr>
        </table>
             
              
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="2">&nbsp;</td></tr>
			  <tr class='formdata'><td colspan="2"><b>Enter Location Id to <%=param%> Location Information:</b></td></tr>
                <tr class='formdata'>
                  <td colspan="2">Location
                    Id:<font color="#FF0000">*</font><br>
                    <input type="text" class="text" name="locationId" maxlength ="3" size="8" onBlur="changeToUpper(this)" onkeyPress="return getKeyCode(locationId)">
					 <!-- <input type="button" class='input' value="..." name="LocationLOV" onClick="showLocationLOV()"> -->
					 <input type="button" class='input' value="..." name="LocationLOV" onClick="openLocationIdLov()">
                    <input type="hidden" name=Operation value="<%= param %>">
                   </td>
                </tr>
                <tr class='denotes'>
                  <td  valign="top" ><font color="#FF0000">*</font>Denotes
                    Mandatory </td>
                  <td   width="340" valign="top" align="right">
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
}catch(Exception e)
  {
	//Logger.error(FILE_NAME,"Error in LocationEnterId.jsp file : "+e.toString());
  logger.error(FILE_NAME+"Error in LocationEnterId.jsp file : "+e.toString());
	}
%>