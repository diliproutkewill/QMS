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
/**
	Program Name	: CommodityEnterId.jsp
	Module Name		: ETrans
	Task			: Commodity	
	Sub Task		: Modify/View/Delete	
	Author Name		: Sivarama Krishna .V
	Date Started	: September 9,2001
	Date Completed	: September 9,2001
	Date Modified	: 
	Description		:
		 This file is invoked when user selects Modify/View/Delete of Commodity from Menu bar of  main Tree structure .This file is 
		 used to select CommodityId to which the details are to be modified/viewd or deleted . CommodityId
		 will be selected on clicking the LOV,which inturn calls the CommodityMasterCommodityLOV.jsp 
		 and on clicking the Submit button it calls the CommodityView.jsp screen in which we can see all the details of Commodity
		 for that particular CommodityId .This file will interacts with CommodityMasterSessionBean and then calls the method 
		 getCommodityDetails. These details are then set to the respective varaibles through Object CommodityMasterJspBean.
		
*/
%>
<%@ page import = "org.apache.log4j.Logger "%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCommodityEnterId.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);
   try
   {
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
<title>Commodity Master</title>       
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<%@ include file="../ESEventHandler.jsp" %>
<script language="JavaScript">
<!--

function getKeyCode()
 {
  if(event.keyCode!==13)
    {
     if ((event.keyCode > 31 && event.keyCode < 48)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127||(event.keyCode > 57 && event.keyCode < 65)))
	 event.returnValue =false;
    }
  return true;
 } 
 
 function showCommodityLOV()
{
 	var Url='ETCLOVCommodityIds.jsp?searchString='+document.forms[0].commodityId.value.toUpperCase()+"&shipmentMode=All&Operation="+document.forms[0].Operation.value;//added by rk
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=400,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
function placeFocus() 
{
  document.forms[0].commodityId.focus();
 }
function Mandatory()
{
     document.forms[0].commodityId.value = document.forms[0].commodityId.value.toUpperCase();
     commodityId    =  document.forms[0].commodityId.value;
    if(commodityId.length ==0)
	{
	    alert("Please enter Commodity Id");
		document.forms[0].commodityId.focus();
		return false;
	}
	document.forms[0].enter.disabled='true';
   	return true;	
}
function changeToUpper(field)
{
	field.value = field.value.toUpperCase();
}		
//-->
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<%
  String param = request.getParameter("Operation"); //variable to get the Operation type i.e View/Modify/Delete
%>
<body  onLoad="placeFocus()">
<form method="GET" action="ETCCommodityView.jsp" name="commodityenterid" onSubmit="return Mandatory()">
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top"> 
    <td bgcolor="ffffff">
        <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td ><table width="790" border="0" ><tr class='formlabel'><td>
	   
	  Commodity - <%=param%></td><td align=right><%=loginbean.generateUniqueId("ETCCommodityEnterId.jsp",param)%></td></tr></table></td>
        </tr>
		</table>
       
           
            
              
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="4">&nbsp;</td></tr>
			  <tr class='formdata'><td colspan="4"><b>Enter Commodity Id to <%=param%> Commodity 
              Information:</b></td></tr>
                <tr class='formdata'> 
                  <td colspan="4">Commodity Id:
				  <font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="commodityId" maxlength ="5" size="8" onBlur="changeToUpper(this)" onKeyPress="return getKeyCode(commodityId)">
                    <input type="hidden" name=Operation value="<%= param %>">
                    <input type="button" class='input' value="..." name="bt_commodityID" onClick="showCommodityLOV()">
                    </td>
                </tr>
                <tr class='denotes'> 
                  <td colspan="2" ><font color="#FF0000">*</font>Denotes 
                    Mandatory </font></td>
                  <td colspan="2"  align="right">
					 <input type="submit" value="Next>>" name="enter" class='input'>
					 <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'>
                     
                      
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
	//Logger.error(FILE_NAME,"Error in CommodityEnterId.jsp file : ",e.toString());
  logger.error(FILE_NAME+"Error in CommodityEnterId.jsp file : "+e.toString());
	} 
%>	