
<%@ page import="java.util.ArrayList,
				org.apache.log4j.Logger,
				com.qms.operations.charges.java.BuychargesHDRDOB,
				com.qms.operations.charges.java.BuychargesDtlDOB,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSSellChargesAccept.jsp";
%>
<%
   logger  = Logger.getLogger(FILE_NAME);	
	ArrayList terminalidList			=	null;
	ArrayList chargeIdList				=	null;
	ArrayList chargeDescList		    =	null;
	ArrayList sellchargeidList          =   null; 
	String operation					=	request.getParameter("Operation");
	String subOperation					=	request.getParameter("subOperation");
	ArrayList acceptanceList            =   (ArrayList)request.getAttribute("acceptanceList");
	if(acceptanceList!=null&&acceptanceList.size()>0)
	{
			sellchargeidList            = (ArrayList)acceptanceList.get(0);
			terminalidList              = (ArrayList)acceptanceList.get(1);
			chargeIdList                = (ArrayList)acceptanceList.get(2);
			chargeDescList              = (ArrayList)acceptanceList.get(3);
	}
%>
<html>
<head>
<title>SellCharges Accept</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<META HTTP-EQUIV="Expires" CONTENT="0">
<script src ="html/eSupply.js"></script>
<script>
function showDetails(obj,obj1,obj2,obj3)
{
	obj.disabled='disabled';
	var operation		=	'<%=operation%>';
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=950,height=300,resizable=yes';
	var Features=Bars+' '+Options;
	var Url='QMSSellChargesController?Operation='+operation+'&subOperation=Modify&terminalId='+obj1+'&chargeId='+obj2+'&chargeDescriptionId='+obj3;
	var Win=open(Url,'Doc',Features);

}
function selectAll(obj)
{
	if(obj.name=="select1" && document.forms[0].select1.checked)
	{
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=="checkbox")
			{
				document.forms[0].elements[i].checked=true;
			} 
		} 
	}
	else 
	{
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=="checkbox")
			{
				document.forms[0].elements[i].checked=false;
				
			}
		}
	}
}
function validation()
{
	  var count = 0;
	
	  <%if(terminalidList!=null)
		 {
			 for(int i = 0;i<terminalidList.size();i++)
			 {%>
				if(document.getElementById("acceptBox"+i).checked==true)
				{	
					count++;
				}
		<%   }
		}%>
		
		if(count==0)
		 {
			alert("Please select any sellcharge");
			return false;
		 }
		 return true;
} 
</script>
</head>
<body>
<form method="post" name='sellChargeform' action='QMSSellChargesController?Operation=Accept&subOperation=Insert' onSubmit='return validation()'>
<table width="100%" border="0" cellspacing="1" cellpadding="4">
	<tr bgcolor="#FFFFFF">
		<td >
			<table width="100%" border="0" cellspacing="0" cellpadding="4">
				<tr class='formlabel' > 
				<td><b>Sell Charges - <%=operation%></b></td><td align="right">QS1050611</td></tr>
			</table>
			<table id="chargesTable" width="100%" border="0" cellspacing="1" cellpadding="4" bgcolor="#FFFFFF">
			  <tr class='formheader'>
			  <td></td>
				<td><b>Accept:<font color="#FF0000">*</font></b><br><input type='checkbox' name='select1' id='select1' onClick="selectAll(this)"></td>
				<td><b>Modify<font color="#FF0000">*</font></b></td>
				<td><b>Terminal Id<font color="#FF0000">*</font></b></td>
				<td><b>Charge Id:*<font color="#FF0000">*</font></b></td>
				<td><b>ChargeDescription Id:*<font color="#FF0000">*</font></b></td>
			  </tr>
			 
			   <%if(terminalidList!=null)
			  {
			   for(int i = 0;i<terminalidList.size();i++)
				  {%>
				 <tr class='formdata'>		
			  <td>&nbsp;</td>
			  <td><input type='checkbox' name='acceptBox' id='acceptBox'></td>
			  <td><input type='button' class ='input' name='modify' value ='Modify' id='modify'  onClick ='showDetails(this,"<%=terminalidList.get(i)%>","<%=chargeIdList.get(i)%>","<%=chargeDescList.get(i)%>")'></td>
			 
 <td><%=terminalidList.get(i)%><input type='hidden' name='terminalId' id='terminalId' value='<%=terminalidList.get(i)%>'>
 <input type='hidden' name='sellchargeId' id='sellchargeId' value='<%=sellchargeidList.get(i)%>'></td>
 <td><%=chargeIdList.get(i)%><input type='hidden' name='chargeId' id='chargeId' value='<%=chargeIdList.get(i)%>'></td>
<td><%=chargeDescList.get(i)%><input type='hidden' name='chargeDescriptionId' id='chargeDescriptionId' value='<%=chargeDescList.get(i)%>'></td>
  </tr>
            
			 <% 
					 }%>
					  <tr class='formdata'>	
			  <td colspan='6' align='right'><input type='submit' class='input' name='submit' id='submit' value='Submit'></td>
			  </tr>
			  <%}	  
			  else
			  {%>
			  <tr class='formdata'>
			  <td>&nbsp;</td>
			   <td>&nbsp;</td>
			    <td>&nbsp;</td>
			  <td colspan='4'><b>No Data Found</b></td>
			  </tr>
			  <%}%>
			 

</form>
</body>
</html>