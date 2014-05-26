
<%
/*	Programme Name : QMSDensityGroupCodeAdd.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : DensityGroupCode
*	Sub Task Name  : The density group is used for the calculation of the volumetric weight.The DGC codes are 1 for                           Air,2 for Sea and 4 for Truck shipments.
*	Author		   : Rama Krishna.Y
*	Date Started   : 16-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>

<%@ page import = "org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList
				   "%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>

<% 
	         String       FILE_NAME     =       "QMSDensityGroupCodeAdd.jsp";
           logger  = Logger.getLogger(FILE_NAME);
  try 			
  {

	 if(loginbean.getTerminalId() == null) //this is to verify whether we can get "terminalid" through the login bean object.
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
<title>Density Group Code</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
function placeFocus() 
{
  document.forms[0].perKG.focus(); 
}

function numaricOnly(input) 
{
		s = input.value;
		filteredValues = "''~!@#$%^&*()_-+=|\:;<>,/?";
		filteredValues1 = "1234567890.";
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++) 
		{  
			var c = s.charAt(i);
			if ( filteredValues.indexOf(c) == -1 && filteredValues1.indexOf(c)!=-1 ) 
				returnString += c;
			else
				flag = 1;
		}
		if( flag==1 )
		{
			alert("Characters not allowed");
			var field = document.forms[0];
			for(i = 0; i < field.length; i++) 
			{
			   if( field.elements[i] == input )
			   {
					document.forms[0].elements[i].focus();
					break;
		       }
		   }		
		}

		input.value = returnString;
}

function mandatory()
{
	
	if(document.forms[0].perKG.value.length<=0){
		alert("Please Enter KG per m3");
		document.forms[0].perKG.focus(); 
		return false;
	}
	else if(document.forms[0].perKG.value<=0)
	{
		alert("KG per m3 should be Grater than 0");
		document.forms[0].perKG.focus(); 
		return false;
	}
	if(document.forms[0].perLB.value.length<=0){
		alert("Please Enter LB per f3");
		document.forms[0].perLB.focus(); 
		return false;
	}
	else if(document.forms[0].perLB.value<=0)
	{
		alert("LB per f3 should be Grater than 0");
		document.forms[0].perLB.focus(); 
		return false;
	}
  return true;
}

 </script>
<body OnLoad="placeFocus()">
<form method="post"  onSubmit="return mandatory()" action="QMSDensityGroupCodeProcess.jsp" name="DensityGroupCode"  >
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr >
    <td>
      <table border="0" cellpadding="4" cellspacing="0" width="100%">
        <tbody>
          <tr class="formlabel">
            <td colspan="3" >  
			Density Group Code - Add 
            </td>
			<td align="right"> QS1010311</td>
			
          </tr>
        </tbody>
      </table>
      <table border="0" cellpadding="4" cellspacing="1" width="100%" height="67" bgcolor='#FFFFFF'>
        <tbody>
          <tr class="formdata">
            <td colspan="2" width="789" height="1">&nbsp;</td>
          </tr>
          <tr class="formdata">
            <td width="168" height="4">Shipment Mode :<font color="#FF0000">*</font>&nbsp;</td>
            <td width="511" height="4"><select name='dgcCode' class='select'><option value='1'>Air</option><option value='2'>Sea</option><option value='4'>Truck</option></select></td>
          </tr>
          <tr class="formdata">
            <td width="168" height="16">KG per m3 :<font color="#FF0000">*</font> &nbsp;</td>
            <td width="511" height="16"><input type="text" name="perKG" size="11" onblur='numaricOnly(perKG)' class='text'>
			<input type="hidden" name="invalidate" size="11" value="F">
			</td>
          </tr>
          <tr class="formdata">
            <td width="168" height="15">LB per f3 :<font color="#FF0000">*</font> </td>
            <td width="511" height="15"><input type="text" name="perLB" size="11" class='text' onblur='numaricOnly(perKG)'></td>
          </tr>
		 <!--  <tr class="formdata">
            <td width="168" height="16">UOM<font color="#FF0000">*</font> &nbsp;</td>
            <td width="511" height="16"><select name='uom' class='select'><option>CM</option><option>INC</option><option>KG</option><option>CBM</option></select></td>
          </tr> -->
        </tbody>
      </table>      
    </td>
  </tr>
</table>
<table border="0" cellpadding="4" cellspacing="1" width="100%" bgcolor='#FFFFFF'>
 <tr >
  <td  class='denotes' colspan="3"><font color="#FF0000">*</font> Denotes Mandatory</td>
  <td align="right" >
	  <input class="input" name="Submit" type="submit" value="Submit">
       <input class="input" name="Reset" type="reset" value="Reset"> 
	 </td>
	  <input  name="Operation" type="hidden" value="Add">
          </tr>
      
      </table>
</form>      
</body>
</html>

<%
	}
  }
  catch(Exception e)
  {
		 //Logger.error(FILE_NAME,"Error in QMSDensityGroupCode.jsp file : "+e.toString());
     logger.error(FILE_NAME+"Error in QMSDensityGroupCode.jsp file : "+e.toString());
		 
		 ArrayList  keyValueList = new ArrayList();
		 ErrorMessage errorMessageObject = new ErrorMessage("Error while accessing the page","QMSDensityGroupCodeAdd.jsp");
		 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 errorMessageObject.setKeyValueList(keyValueList);
         request.setAttribute("ErrorMessage",errorMessageObject); 
	
%>
		 <jsp:forward page="ESupplyErrorPage.jsp" /> 
<%
 	}	   
%>  