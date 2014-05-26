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
/*	Programme Name : ListMasterView.jsp.
*	Module Name    : QMS.
*	Task Name      : DHL-QMS
*	Author		   : K.NareshKumarReddy.
*	Date Started   :21 July 2005 
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "javax.naming.InitialContext,
      			   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.qms.setup.ejb.sls.SetUpSession,
				   com.qms.setup.ejb.sls.SetUpSessionHome,
				   com.qms.setup.java.ListMasterDOB,
				   javax.ejb.ObjectNotFoundException,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList "%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
%>

<%
		 String FILE_NAME	=	"ListMasterView.jsp";
     logger  = Logger.getLogger(FILE_NAME);
  try 			
  {

	 if(loginbean.getTerminalId() == null) 
	 {											 
%>
   <jsp:forward page="ESupplyLogin.jsp" />
<%
	 }
	 else
	 {
		 String operation = request.getParameter("Operation");
		 String shipmentMode=request.getParameter("shipmentMode");
		 String listId=request.getParameter("listType");
		 String read="";
		 String action=null;
		 String disable="";
		 String desigSubmit="";
		 String sel="",sel1="",sel2="";
		 ListMasterDOB listDOB=new ListMasterDOB();
		try
		{
			  if(operation.equalsIgnoreCase("modify"))
			 {
				 
				 action="ListMasterProcess.jsp?Operation=modify";
				 desigSubmit="Modify";
			    

			 }
			 else if(operation.equalsIgnoreCase("view"))
			{
				read="readonly";	
				disable="disabled";
				action="ListMasterEnterId.jsp?Operation=view";
				desigSubmit="Continue";
			}
			else if(operation.equalsIgnoreCase("delete"))
			{
				read="readonly";
				disable="disabled";
				action="ListMasterProcess.jsp?Operation=delete";
				desigSubmit="Delete";
			}
			 InitialContext context = new InitialContext();
			 SetUpSessionHome 	SetUpSessionHome	=	(SetUpSessionHome)context.lookup("SetUpSessionBean");
			 SetUpSession 		SetUpSession	=	(SetUpSession)SetUpSessionHome.create();
				 
			 listDOB=SetUpSession.getListMasterDetails(shipmentMode,listId);	

			
	
		}catch(ObjectNotFoundException exsp)
		{ 
		 ArrayList  keyValueList = new ArrayList();
		 ErrorMessage errorMessageObject = new ErrorMessage("List Type not found ","ListMasterEnterId.jsp");
		 keyValueList.add(new KeyValue("Operation",operation));
		 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 errorMessageObject.setKeyValueList(keyValueList);
         request.setAttribute("ErrorMessage",errorMessageObject); 
%>
		 <jsp:forward page="ESupplyErrorPage.jsp" /> 
<%
		}
		catch(Exception ex)
		{
			    //Logger.error(FILE_NAME,"Exception in DesignationView ",ex.toString());
          logger.error(FILE_NAME+"Exception in DesignationView "+ex.toString());
		}


 %>
<html>
<head>
<title>Designation Add </title>
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
<!--

function upperCase(input)
 {
  input.value=input.value.toUpperCase();
 }
function getKeyCode()
 {
  if(event.keyCode!=13)
    {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
    }
  return true;
 }
function getSpecialCode()
 {
	
  if(event.keyCode!=13)
    {
      if((event.keyCode > 32 && event.keyCode < 40) || event.keyCode == 64   ||event.keyCode==96 || event.keyCode==126 || event.keyCode==45)
	 event.returnValue =false;
    }
  return true;
 }


function specialCharFilter(input,label) 
	{
		
		s = input.value;
		
		filteredValues = "''~!@#$%^&*()_-+=|\:;<>,.?"+'"';		
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++) 
		{  
			var c = s.charAt(i);
			if ( filteredValues.indexOf(c) == -1 ) 
					returnString += c.toUpperCase();
			else
			{
				alert("Please do not enter special characters "+label);
				input.value = "";
				input.focus();
				return false;
			}
		}
		return true;
	}

function placeFocus() 
{
  document.forms[0].listType.focus(); 
 }

function Mandatory()
{
	
	   
 
	document.forms[0].submit.disabled='true';				
  	return true;	
}	 
</script>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<body OnLoad="placeFocus()">
<form method="POST"  onSubmit="return Mandatory()" action="<%=action%>" name="listView" >
<table width="800"  border="0" cellspacing="0" cellpadding="0" >
  <tr valign="top"> 
    <td  colspan="2" bgcolor="ffffff">
      <table width="800" border="0" cellspacing="1" cellpadding="4" >
        <tr class='formlabel'> 
			 <td ><table width="800" border="0" ><tr class='formlabel'><td colspan="4">&nbsp;List Master-<%=operation%> </td><td align="right">QS1020222</td></tr></table></td>
        </tr>
        </table>
		
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
                <tr Class=formdata> 
		<td width="400" colspan="2" height="1">

		<%
			 if(operation.equalsIgnoreCase("view")|| operation.equalsIgnoreCase("delete"))
		 {
%>
			<input type='hidden' name='shipmentMode' value="<%=shipmentMode%>">
			<input type='hidden' name='uov' value="<%=listDOB.getUov()%>">
			<input type='hidden' name='uow' value="<%=listDOB.getUom()%>">
<%
		 }
			if(shipmentMode.equalsIgnoreCase("Air"))
				  sel="selected";
			  else if(shipmentMode.equalsIgnoreCase("Sea"))
				  sel1="selected";
			  else if(shipmentMode.equalsIgnoreCase("Truck"))
				  sel2="selected";
			  %>
              Shipment Mode:<font color="#FF0000">*</font><br>
              <select size="1" name="shipmentMode" class="select" <%=disable%> >
                <option  <%=sel%> value="Air">Air</option>
                <option <%=sel1%> value="Sea" >Sea</option>
				<option   <%=sel2%> value="Truck">Truck</option>
            </select>
            </td>
			<%if(listDOB.getUov().equals("CBM"))
				  sel="selected";
			  else if(listDOB.getUov().equals("CFT"))
				  sel1="selected";
			  %>
		<td width="124" colspan="2" height="1">
              UOV:&nbsp;<br>
             <select size="1" name="uov" class="select" <%=disable%>>
             <option <%=sel%> >CBM</option>
              <option <%=sel1%> >CFT</option>
            </select>
            </td>
			<%
		
				  if(listDOB.getUom().equals("KG"))
				  sel="selected";
				  else if(listDOB.getUom().equals("LB"))
				  sel1="selected";
		
			

			%>
		<td width="372"  colspan="1" height="1"  >
            UOW:&nbsp;<br>
              <select size="1" name="uow" class="select" <%=disable%> >
                <option <%=sel%> >KG</option>
                <option <%=sel1%> >LB</option>
              </select>
            </td>
		</tr>
		<tr Class=formheader> 
		<td height="1"><font color="#FFFFFF">ULD Type:*</font></td>
		<td  height="1">ULD&nbsp;Description:</td>
		<td  height="1" align="center">Volume:</td>
		<td  height="1" align="center">Pivot Weight:*</td>
		<td  height="1" align="center">Over&nbsp;Pivot&nbsp;Weight:*</td>
		
	</tr>
			<tr class='formdata'>	
            <td width="200" >
                    <input type='text' class='text' name="listType" size="20"  maxlength="15" onBlur="" value="<%=listId%>" readonly onkeyPress='return getSpecialCode(listType);specialCharFilter(this,"") '>
                    </td>
                  <td width="150">
                    <input type='text' class='text' name="description" size="20" maxlength="25" onkeyPress="return getSpecialCode(description)" <%=read%> value= "<%=listDOB.getDescription()==null?"":listDOB.getDescription()%>">
                    </td>
                  <td width="102"<br>
                    <input type="text" class='text' name="volume" size="10" maxlength="15" <%=read%> value="<%=listDOB.getVolume()%>">
                    </td>
					<td width="250"<br>
                    <input type="text" class='text' name="pivoteWeight" size="10" maxlength="15"  <%=read%> value="<%=listDOB.getPivoteUladenWeight()%>">
                    </td>
					<td width="150"<br>
                    <input type="text" class='text' name="overpivoteWeight" size="10" maxlength="15" <%=read%> value="<%=listDOB.getOverPivoteTareWeight()%>">
                    </td>
                  </tr>
              </table>
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
                <tr class='denotes'> 
                  <td valign="top" colspan="4"> 
					  <font color="#FF0000">*</font><font face="Verdana" size="1">Denotes Mandatory</font>
                </tr>
                <tr class='denotes' > 
                	<td  align="right">
					 <input type="submit" value="<%=desigSubmit%>" name=submit name class='input'>
<%
						if(operation.equalsIgnoreCase("modify"))
						{
%>
					 <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'>
<%
						}
%>
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
		 //Logger.error(FILE_NAME,"Error in ListMasteView.jsp file : "+e.toString());
     logger.error(FILE_NAME+"Error in ListMasteView.jsp file : "+e.toString());
		 
		 ArrayList  keyValueList = new ArrayList();
		 ErrorMessage errorMessageObject = new ErrorMessage("Error while accessing the page","ListMasterAdd.jsp");
		 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 errorMessageObject.setKeyValueList(keyValueList);
         request.setAttribute("ErrorMessage",errorMessageObject); 
	
%>
		 <jsp:forward page="ESupplyErrorPage.jsp" /> 
<%
 	}	   
%>  

