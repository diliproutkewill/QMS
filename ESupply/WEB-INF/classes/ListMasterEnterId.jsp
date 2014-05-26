
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
/*	Programme Name : ListMasterEnterId.jsp.
*	Module Name    : DHL-QMS.
*	Task Name      : List Master
*	Author		   : K.NareshKumarReddy
*	Date Started   :21 July 2005 
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>
<%@ page import =	"javax.naming.InitialContext,
					javax.sql.DataSource,
					java.util.ArrayList,
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
					com.qms.setup.ejb.sls.SetUpSessionHome"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>

<%
		     String    id		  = null;
			 ArrayList designationIds = null;
			 String	   FILE_NAME  = "ListMasterEnterId.jsp";		
       logger  = Logger.getLogger(FILE_NAME);
    try
    {
	 
	 if(loginbean.getTerminalId() ==null)//this is to verify whether we can get "terminalid" through the 
	 {											  //login bean object.		
%>
	
         <jsp:forward page="../ESupplyLogin.jsp" />
<%
	}
	else
	{
		try{
		     id = request.getParameter("id");
   			 InitialContext initial = new InitialContext();
 			 SetUpSessionHome 	ETransUtilitiesHome	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
			 SetUpSession 		ETransUtilitiesRemote	=	(SetUpSession)ETransUtilitiesHome.create();
		  }
	 	  catch(Exception exp)
		  {
			//Logger.error(FILE_NAME,"Error in QMSDesignation EnterId "+exp.toString());
      logger.error(FILE_NAME+"Error in QMSDesignation EnterId "+exp.toString());
		  }
%>
<html>
<head>
<title>Designation EnterId</title>
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
function verifyDesignationId()
{
<%
	if(id == null)
	{
%>
		document.forms[0].action = "ListMasterEnterId.jsp";
		document.forms[0].submit();
<%
	}
	else
	{
		if(designationIds.contains(id))
		{
%>
			Record does not exist.
<%
		}
		else
		{
%>
			document.forms[0].action = "ListMasterProcess.jsp";
			document.forms[0].submit();
<%
		}
	}
%>	
}
function showListTypeLOV()
{
    var selectedIndex=document.forms[0].shipmentMode.selectedIndex;
 	var Url='ListMasterLOV.jsp?searchString='+document.forms[0].listType.value.toUpperCase()+'&shipmentMode='+document.forms[0].shipmentMode.options[selectedIndex].value+"&Operation="+document.forms[0].Operation.value;//added by rk
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
function getKeyCode()
 {
  if(event.keyCode!==13)
    {
     if ((event.keyCode > 31 && event.keyCode < 48)||(event.keyCode>61 && event.keyCode<65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
    }
  return true;
 }
 
 function upperCase(input)
 {
  input.value=input.value.toUpperCase();
 }

function placeFocus() 
{
  document.listmaster.listType.focus();
}
function changeLabel()
{
  var selectedIndex=document.forms[0].shipmentMode.selectedIndex;
	if(document.forms[0].shipmentMode.options[selectedIndex].value=="Sea")
	{
		document.getElementById("RefDocLabel").innerHTML="Container&nbsp;Type:&nbsp;";
		
	}
	else if(document.forms[0].shipmentMode.options[selectedIndex].value=="Air")
	{
		document.getElementById("RefDocLabel").innerHTML="ULD Type:";
		

	}
	else if(document.forms[0].shipmentMode.options[selectedIndex].value=="Truck")
	{
		document.getElementById("RefDocLabel").innerHTML="Container&nbsp;Type:&nbsp;";
		
	}
		
}

function Mandatory()
{
   document.forms[0].listType.value = document.forms[0].listType.value.toUpperCase();
   lisType    =  document.forms[0].listType.value;
   if(lisType.length == 0)
	{
	    alert(" Please enter List Id");
		document.forms[0].listType.focus();
		return false;
	}	
   	document.forms[0].enter.disabled='true';
   	return true;	
}

</script>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<% String param = request.getParameter("Operation"); %>
<body  OnLoad="placeFocus()">
<form method="GET"  onSubmit="return Mandatory()" action="ListMasterView.jsp" name="listmaster">
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top"> 
    <td colspan="2" bgcolor="ffffff">
      
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td ><table width="790" border="0" ><tr class='formlabel'><td>List Master - <%=param%>&nbsp;</td><td align=right></td></tr></table></td>
        </tr>
		<tr class='formdata'><td>&nbsp;</td></tr>
        </table>
            
              
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  
                <tr class='formdata'> 
				<td width="400" height="1">
              Shipment Mode:<font color="#FF0000">*</font><br>
              <select size="1" name="shipmentMode" class="select" onChange="changeLabel()">
                <option value="Air" selected>Air</option>
                <option value="Sea">Sea</option>
                <option value="Truck">Truck</option>
            </select>
            </td>
                  <td colspan="2" ><span id=RefDocLabel>ULD Type:</span><font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="listType" maxlength = "15" size="20" onBlur="upperCase(this)" onkeyPress="return getKeyCode(id)" >
                    <input type="hidden" name="Operation"   value="<%= param %>" >
                    
                    <input type="button" class='input' value="..." name="listTypeLOV" onClick="showListTypeLOV()">
                    </td>
                </tr>
                <tr class='denotes' valign="top"> 
                  <td><font color="#FF0000">*</font>Denotes Mandatory </td>
                  <td align="right">
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
	 	//Logger.error(FILE_NAME,"Exception in QMSDesignationEnterId.jsp"+e.toString());
    logger.error(FILE_NAME+"Exception in QMSDesignationEnterId.jsp"+e.toString());
  	}
 %>