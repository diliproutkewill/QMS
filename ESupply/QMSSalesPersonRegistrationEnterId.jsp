
<%
/*	Programme Name : QMSSalesPersonRegistrationEnterId.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : SalesPersonRegistration
*	Sub Task Name  : The SalesPersonRegistration is to registring the SalesPerson
*	Author		   : Rama Krishna.Y
*	Date Started   : 28-06-2005
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
  public static final String FILE_NAME="QMSSalesPersonRegistrationEnterId.jsp";%>

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
<title>Sales Person Registration</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<script language="JavaScript">

function placeFocus()
{
	document.forms[0].salesPersonCode.focus();
}

function showSalesCodeLOV()
{
 	var url="QMSSalesPersonCodeLOV.jsp?salesPersonCode="+document.forms[0].salesPersonCode.value+"&opeartion="+document.forms[0].Operation.value;
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(url,'Doc',Features);
}
function mandatory()
{
	if(document.forms[0].salesPersonCode.value.length<=0)
	{
		alert("Please enter Sales Person Code value");
		document.forms[0].salesPersonCode.focus();
		return false;
	}
	return true;
}

</script>
<body onLoad='placeFocus()'>
 <form method="get"  onSubmit="return mandatory()" action="QMSSalesPersonRegistrationView.jsp" name="SalesPersonRegistration"  >

    <table border="0" cellpadding="4" cellspacing="1" width="803" height="67">
        <tbody>
          <tr class="formlabel">
            <td colspan="1" width="789" height="1" class='formlabel'><font size='3'><b>Sales Person Registration - <%=operation%></td><td align="right">QS1010421</td>
          </tr>	    
		  <tr class="formdata">
			<td width="168" height="16">Sales Person Code<font color="#FF0000">*</font> &nbsp;</td>
			<td width="511" height="16" ><input type="text" name="salesPersonCode" size='12' ><input class="input" type="button" name="salesCodeLOV" value="..." onClick="showSalesCodeLOV()"></td>
		  </tr> 
		</tbody>
	</table>	
	<table border="0" cellpadding="4" cellspacing="1" width="803" class="formdata">
       <tbody>
         <tr >
            <td colspan="2" width="452"><font color="#FF0000">*</font> Denotes Mandatory</td>
			  <td align="right" colspan="2" width="325">
			  <input class="input" name="Submit" type="submit" value="Submit" border-style: solid">
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