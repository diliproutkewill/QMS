
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
<script src ="../html/eSupply.js"></script>
</head>

<script language="JavaScript">

function placeFocus()
{
	document.forms[0].chargeId.focus();
}

function showLOV(obj)
{
	var url="";
	var terminalId = document.forms[0].terminalId.value;
	if(terminalId=='')
	{	terminalId = '<%=loginbean.getTerminalId()%>'; }
	if(obj.name=='chargeIdLOV')
 	  url="QMSLOVChargeIds.jsp?Operation=<%=operation%>&searchString="+document.forms[0].chargeId.value+"&name="+document.forms[0].chargeId.name+"&fromWhere=chargeDescriptionEnterId&terminalId="+terminalId;
	else{
		if(document.forms[0].chargeId.value.length<=0)
		{
			alert("Please enter ChargeID value");
			document.forms[0].chargeId.focus();
			return false;
		}
	    else
		{						url="QMSLOVDescriptionIds.jsp?Operation=<%=operation%>&searchString="+document.forms[0].descId.value+"&chargeId="+document.forms[0].chargeId.value+"&terminalId="+terminalId+"&fromWhere=chargeDescriptionEnterId";
		}
	}

	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(url,'Doc',Features);
}
function mandatory()
{
	if(document.forms[0].chargeId.value.length<=0)
	{
		alert("Please Enter the Charge ID.");
		document.forms[0].chargeId.focus();
		return false;
	}
	if(document.forms[0].descId.value.length<=0)
	{
		alert("Please Enter the Charge Description Id.");
		document.forms[0].descId.focus();
		return false;
	}
	return true;
}
function removeSpaces(obj)
{
    if(obj!=null)
	{  var temp  = obj.value.trim;
	   var temp  = obj.value.trim;
        for(var i=0;i<obj.value.trim.length();i++)
		{
			
		}

	}
}
function showterminalIdLOV()
{
	var name		=	"terminalId";
	var searchStr	=	document.getElementById(name).value;
 	//var fromWhere	=	"sellchargesenterid";
	var fromWhere	=	"terminal";

		var Url			= "../QMSLOVAllLevelTerminalIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&selection=single&fromWhere="+fromWhere;
		var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
		var Options='scrollbar=yes,width=400,height=360,resizable=no';
		var Features=Bars+' '+Options;
		var Win=open(Url,'Doc',Features);
}
</script>
<body onLoad='placeFocus()'>
 <form method="get"  onSubmit="return mandatory()" action="QMSChargeDescriptionView.jsp"  >

    <table border="0" cellpadding="4" cellspacing="1" width="800" bgcolor='#FFFFFF'>
        <tbody>
          <tr class="formlabel">
            <td colspan="2" width="789" height="1"><font size='3'><b>Charge Description - <%=operation%></td>
			<td width="500" height="16" align="right">QS1050221</b></font></td>
          </tr>	    
		  <tr class="formdata">
			<td width="20%"><b>Terminal Id:<font color="#FF0000">**</font></b>
				<input type	='text' name ="terminalId" id='terminalId' size='10' maxLength='10' class ='text' value ='' onkeyPress='specialCharFilter(this,"terminalId")'  onBlur="toUpperCase()">
				<input type	='button' name='terminalIdB' class ='input' value ="..." Onclick ="showterminalIdLOV()">
			</td>
			<td width="168" height="16">Charge Id:<font color="#FF0000">*</font> &nbsp;
			<input type="text" name="chargeId" size='12'  class ='text' onBlur="toUpperCase()">
			<input class="input" type="button" name="chargeIdLOV" value="..." onClick="showLOV(this)"></td>
			<td width="500" height="16">Description Id:<font color="#FF0000">*</font> <br>
			<input type="text" name="descId" size='12'  class ='text' onBlur="toUpperCase()">
			<input class="input" type="button" name="descIdLOV" value="..." onClick="showLOV(this)"></td>
		  </tr> 
		</tbody>
	</table>	
	<table border="0" cellpadding="4" cellspacing="1" width="800" bgcolor='#FFFFFF'>
       <tbody>
         <tr >
            <td colspan="2" width="452" class='denotes'>Notes :<font color="#FF0000">*</font>Denotes Mandatory <br>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font color="#FF0000">**</font>Default Terminal ID is Login Terminal ID</td>
			  <td align="right" colspan="2" width="325">
			  <input class="input" name="Submit" type="submit" value="Submit" "border-style: solid">
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