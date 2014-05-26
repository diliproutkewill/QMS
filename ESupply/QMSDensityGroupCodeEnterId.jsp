
<%
/*	Programme Name : QMSDensityGroupCodeEnterId.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : DensityGroupCode
*	Sub Task Name  : Modify/View/Delete.
*	Author		   : Rama Krishna.Y
*	Date Started   : 16-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>


<%@ page import = "java.util.ArrayList,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				    com.foursoft.esupply.common.java.KeyValue"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME="QMSDensityGroupCodeEnterId.jsp";%>

<%
  logger  = Logger.getLogger(FILE_NAME);
	ArrayList          keyValueList           =    null;
	ErrorMessage       errorMessageObject     =    null;
	String             operation              =    null;

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
		             operation    =   request.getParameter("Operation");		
%>

<html>
<head>
<title>Density Group Code</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<script language="JavaScript">

function placeFocus()
{
	document.forms[0].perKG.focus();
}

function showPerKgLOV()
{
	var dgcCode=document.forms[0].dgcCode.value;
	//var uom=document.forms[0].uom.value;
 	var url="QMSDensityGroupCodePerKGLOV.jsp?perKG="+document.forms[0].perKG.value+"&Operation="+document.forms[0].Operation.value+"&dgcCode="+dgcCode;//added by rk
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(url,'Doc',Features);
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
	if(document.forms[0].perKG.value.length<=0)
	{
		alert("Please enter KG per m3 value");
		document.forms[0].perKG.focus();
		return false;
	}
	return true;
}

</script>
<body onLoad='placeFocus()'>
 <form method="get"  onSubmit="return mandatory()" action="QMSDensityGroupCodeView.jsp" name="DensityGroupCode"  >

    <table border="0" cellpadding="4" cellspacing="1" width="803" height="67" bgcolor='#FFFFFF'>
        <tbody>
          <tr class="formdata">
            <td  width="789" height="1" class='formlabel'><font size='3'><b>Density Group Code - <%=operation%> </td><td class='formlabel' align="right">QS1010321</td>
          </tr>
		  <tr class="formdata" >
			<td width="168" height="4" >Shipment Mode<font color="#FF0000">*</font>&nbsp;</td>
			<td width="511" height="4"><select name='dgcCode' class='select'>
			<option value='1'>Air</option>
			<option value='2'>Sea</option>
			<option value='4'>Truck</option></select></td>
		  </tr>
		  <!-- <tr class="formdata">
			<td width="168" height="16">UOM<font color="#FF0000">*</font> &nbsp;</td>
			<td width="511" height="16"><select name='uom' class='select'><option value='CM'>CM</option><option value='INC'>INC</option><option value='KG'>KG</option><option value='CBM'>CBM</option></select></td>
		  </tr>  --> 
		  <tr class="formdata">
			<td width="168" height="16">KG per m3 :<font color="#FF0000">*</font> &nbsp;</td>
			<td width="511" height="16"><input type="text" name="perKG" size='12' maxLength='12' onKeyPress='numaricOnly(perKG)' class='text'>&nbsp;<input class="input" type="button" name="perKGLOV" value="..." onClick="showPerKgLOV()"></td>
		  </tr> 
		</tbody>
	</table>	
	<table border="0" cellpadding="4" cellspacing="1" width="803" bgcolor='#FFFFFF'>
       <tbody>
         <tr >
            <td colspan="2" width="452" class='denotes'><font color="#FF0000" class='denotes'>*</font> Denotes Mandatory</td>
			  <td align="right" colspan="2" width="325">
			  <input class="input" name="Submit" type="submit" value="Submit">
              <input class="input" name="Reset" type="reset" value="Reset"> 
			  <input  name="Operation" type="hidden" value='<%=operation%>'> </td>
         </tr>
       </tbody>
     </table>
  </form>
 </body>
</html>

<%
	 }
  }
  catch(Exception e)
  {
	  //Logger.error(FILE_NAME,"Error in QMSDensityGroupCodeEnterId.jsp file"+e.toString());
    logger.error(FILE_NAME+"Error in QMSDensityGroupCodeEnterId.jsp file"+e.toString());
	  errorMessageObject   =   new ErrorMessage("Unable to "+operation+"the record","QMSDensityGroupCodeAdd.jsp");
	  keyValueList         =   new ArrayList();
	  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
	  keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
	  errorMessageObject.setKeyValueList(keyValueList);
	  request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%

  }
%>