<%--
%
% Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
% This Software is the proprietary information of FourSoft,Pvt Ltd.
% Use is subject to license terms
%
% esupply-V 1.x
%
--%>

<%--	File								:	ETVendorRegistrationEnterId
		Sub-module name			:	Vendor Registration
		Module name					:	ETrans
		Purpose of the class		:	It presents The GUI to add,Modify,View and Delete the Vendor Ids
		Author							:	Nageswara Rao.D
		Date								:	22/Jan/2003
		Modified history		:
--%>
<%@ page import = "org.apache.log4j.Logger"%>
<jsp:useBean id="loginbean" scope="session" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" />

<%!
  private static Logger logger = null;
  private static final String FILE_NAME = "ETAPRQEnterId.jsp"; %>
<%
    logger  = Logger.getLogger(FILE_NAME);	
		String	operation			=	request.getParameter("Operation");
		String	subOperation		=	request.getParameter("SubOperation");
		//Logger.info(FILE_NAME,"Sub Operation issssssssss----"+subOperation);
    logger.info(FILE_NAME+"Sub Operation issssssssss----"+subOperation);
		String	type						=	 request.getParameter("Type");
//@@ Added By Ravi Kumar to Resolve the Issue SPETI-5895 on 26-04-2005
		String vendAcctOperation    =	"";
		if(request.getParameter("vendAcctOperation")==null)
			vendAcctOperation    =	request.getParameter("SubOperation");
		else
			vendAcctOperation    =	request.getParameter("vendAcctOperation");
//@@ 26-04-2005
%>

<html>
<head>
<title>Vendor <%=operation%>  EnterId</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>

function showVendorLOV()
{
	var indicator	=	'Y';
	// @@ Suneetha Added on 20050430
	var oper='<%=operation%>';
	// @@ 20050430
<%	
	if(subOperation.equals("Accounts") || vendAcctOperation.equals("Accounts")){ //Modified By Ravi KUmar 26-04-2005
%>	
		indicator	=	'N';
<%
	}
%>
	var terminalId    ='<%=loginbean.getTerminalId()%>';
// @@ Suneetha Added a new parameter 'operation' to the URL on 20050430
	var Url='ETransLOVVendorIds.jsp?terminalId='+terminalId+'&searchString='+document.forms[0].vendorId.value.toUpperCase()+'&indicator='+indicator+'&operation='+oper;
// @@ 20050430
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features); 
}
function upper(input)
{
   input.value= input.value.toUpperCase();
}
function placeFocus()
{
	document.forms[0].vendorId.focus();
}
function Mandatory()
{
	if(document.forms[0].vendorId.value.length==0)
	{
		alert("Please enter VendorId ");
		document.forms[0].vendorId.focus();
		return false;
	}
	document.forms[0].Submit.disabled='true';
}
</script>
</head>
<body bgcolor="#FFFFFF" onLoad=placeFocus()>
<form method="post"  action="../ETVendorRegistrationController"  onSubmit="return Mandatory()">
  <table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
        <table width="800" border="0" cellspacing="1" cellpadding="4">
          <tr class="formlabel"> 
            <td colspan="2"><table width="790" border="0" ><tr class='formlabel'><td>Vendor Registration - <%=operation%></td><td align=right><%=loginbean.generateUniqueId("ETVendorRegistrationEnterId.jsp",operation,subOperation.equals("Accounts")?"Accounts":"")%></td></tr></table></td>
          </tr></table>
		  <table width="800" border="0" cellspacing="1" cellpadding="4">
		  <tr class='formdata'> 
            <td colspan="2">&nbsp;</td>

          </tr>
          <tr class='formdata'> 
            <td colspan="2"><b>Please enter Vendor Id 
              to <%=request.getParameter("Operation")%> Vendor Details</b></td>

          </tr>
          <tr class="formdata" valign="top"> 
            <td colspan="2">Vendor Id :<font color="ff0000">*</font><br>
             <!--@@Code changed(incresed maxlength : 25) added by Ujwala as per jira SPETI-5856 onDate :25/05/2005-->
              <input type="text" class="text" name="vendorId" size="16" maxlength=25 value="" class=text>
			<!--End -ujwala -->
              <input type="button" name="Button" value="..." onClick=showVendorLOV() class="input">
            </td>
          </tr>
          <tr  valign="top" class='denotes'> 
            <td><font color="#ff0000">*</font> 
              Denotes Mandatory</td>
            <td align="right"> 
			<input type=hidden name=Operation value='<%=operation%>' >
			<input type=hidden name=SubOperation value='<%=subOperation%>' >
<!-- Added By Ravi Kumar to Resolve the Issue SPETI-5895 on 26-04-2005 -->
			<input type=hidden name=vendAcctOperation value='<%=vendAcctOperation%>'>
<!-- 26-04-2005 -->
			<input type=hidden name="NextProcess" value="EnterId" >
			<input type=hidden name=Type value='<%=type%>'>
			   <input type="hidden" name="terminalId" value="<%=loginbean.getTerminalId()%>" >
			 <input type="submit" name="Submit" value="Next>>" class="input">
			 <input type="reset" name="reset" value="Reset" class="input">
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
</body>
</html>