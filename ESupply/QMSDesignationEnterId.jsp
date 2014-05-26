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
/*	Programme Name : QMSDesignationEnterId.
*	Module Name    : DHL-QMS.
*	Task Name      : Designation Master
*	Sub Task Name  : Taking inputs from the DesignationAdd  and pass these values to "QMSSetuSessionBean" .
*	Author		   : K.NareshKumarReddy
*	Date Started   :6/22/2005
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
					com.qms.setup.ejb.sls.QMSSetUpSession,
          com.qms.setup.ejb.sls.QMSSetUpSessionHome"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>

<%
		     String    id		  = null;
			 ArrayList designationIds = null;
			 String	   FILE_NAME  = "QMSDesigntionEnterId.jsp";		
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
 			 QMSSetUpSessionHome 	ETransUtilitiesHome	=	(QMSSetUpSessionHome)initial.lookup("QMSSetUpSessionBean");
			 QMSSetUpSession 		ETransUtilitiesRemote	=	(QMSSetUpSession)ETransUtilitiesHome.create();
             designationIds = ETransUtilitiesRemote.getDesignationIs("");
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
		document.forms[0].action = "QMSDesignationEnterId.jsp";
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
			document.forms[0].action = "QMSDesignationProcess.jsp";
			document.forms[0].submit();
<%
		}
	}
%>	
}
function showDesignationLOV()
{
 	var Url='QMSDesignationLOV.jsp?searchString='+document.forms[0].designationId.value.toUpperCase()+"&from=View&Operation="+document.forms[0].Operation.value;//added by rk
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
  document.designation.designationId.focus();
}

function Mandatory()
{
   document.forms[0].designationId.value = document.forms[0].designationId.value.toUpperCase();
   var designation    =  document.forms[0].designationId.value;
   if(designation.length == 0)
	{
	    alert(" Please enter designation Id");
		document.forms[0].designationId.focus();
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
<form method="GET"  onSubmit="return Mandatory()" action="QMSDesignationView.jsp" name="designation">
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top"> 
    <td colspan="2" bgcolor="ffffff">
      
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td ><table width="790" border="0" ><tr class='formlabel'><td>Designation - <%=param%>&nbsp;</td><td align=right>QS1020121</td></tr></table></td>
        </tr>
		<tr class='formdata'><td>&nbsp;</td></tr>
        </table>
            
              
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="2">Designation Id to  <%=param%> Designation Information:</td></tr>
                <tr class='formdata'> 
                  <td colspan="2" >Designation 
                    ID:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="designationId" maxlength = "15" size="20" onBlur="upperCase(this)" onkeyPress="return getKeyCode(id)" >
                    <input type="hidden" name="Operation"   value="<%= param %>" >
                    
                    <input type="button" class='input' value="..." name="designationIdLOV" onClick="showDesignationLOV()">
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