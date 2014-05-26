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
	Program Name	: CommodityAdd.jsp
	Module Name		: ETrans
	Task			: Commodity	
	Sub Task		: Add	
	Author Name		: Sivarama Krishna .V
	Date Started	: September 9,2001
	Date Completed	: September 9,2001
	Date Modified	: 
	Description		:
         This file is invoked when user selects Add of Commodity from Menu bar of  main Tree structure .This file is 
         used to add new Commodity Details.
         On entering all the details and clicking the submit button
         CommodityAddProcess.jsp is called.
         This file will interacts with CommodityMasterSessionBean and then calls the method setCommodityDetails( CommodityMasterJspBean ,ESupplyGlobalParameters)
		 These details are then set to the respective varaibles through Object CommodityMasterJspBean.
		  
		
*/
%>

<%@ page import = "org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				   com.foursoft.esupply.common.java.KeyValue,
				   java.util.ArrayList " %>	
 	

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>

<%
   String FILE_NAME = "ETCCommodityAdd.jsp";	
  logger  = Logger.getLogger(FILE_NAME);
   
   ErrorMessage errorMessageObject = null;
   ArrayList	keyValueList	   = null; 	
   
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
<%@ include file="../ESEventHandler.jsp" %>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script language="JavaScript">
<!--
	function placeFocus() 
	{
	   document.forms[0].commodityId.focus();
	}
  	
function Mandatory()
{
    for(i=0;i<document.forms[0].elements.length;i++)
    {
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		    document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }
	commodityId    =  document.forms[0].commodityId.value;
    commodityDesc  =  document.forms[0].commodityDescription.value;
    type           =  document.forms[0].commodityType.selectedIndex;
    HandleInfo     =  document.forms[0].commodityHandlingInfo.value;
	commoditysubClass	   =  document.forms[0].subClass.value;
	commodityunNumber	   =  document.forms[0].unNumber.value;
	
    if(commodityId.length ==0)
	{
	    alert("Please enter Commodity Id");
		document.forms[0].commodityId.focus();
		return false;
	}	
     if(commodityDesc.length == 0)
	{
		alert("Please enter Commodity Description");
		document.forms[0].commodityDescription.focus();
		return false;
	}
	 if(type==0)
	{
		alert("Please select Commodity Type ")
		document.forms[0].commodityType.focus();
		return false;
	}
  if(HandleInfo.length > 2000)
   {
	alert("Handling Info should be less than 2000 characters");
	document.forms[0].commodityHandlingInfo.focus();
	return false;
   }
   
	 if(document.forms[0].hazardIndicator.checked)
	{
		 if(commoditysubClass=='')
		{
			 alert("please Enter Sub-Class value");
			 document.forms[0].subClass.focus();
			 return false;
		}
		if(commodityunNumber=='')
		{
			alert("Please Enter UnNumber Value");
			document.forms[0].unNumber.focus();
			return false;
		}

	}
  	document.forms[0].submit.disabled='true';
  	return true;	
}
function changeToUpper(field)
{
	field.value = field.value.toUpperCase();
}
		
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
 
 
function getSpecialCode()
 {
  if(event.keyCode!==13)
    {
     if(event.keyCode==34 || event.keyCode==39 ||event.keyCode==59||event.keyCode==96||event.keyCode==126)
	 event.returnValue =false;
    }
  return true;
 } 
 function star()
{
	 if(document.forms[0].hazardIndicator.checked)
	{
		document.getElementById("Label").innerHTML="*";
		document.getElementById("Label1").innerHTML="*";
	}
	else
	{
		document.getElementById("Label").innerHTML="&nbsp;";
		document.getElementById("Label1").innerHTML="&nbsp;";
	}
}
 
//-->
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body  onLoad="placeFocus()">
<form method="POST"  onSubmit="return Mandatory()" action="ETCCommodityProcess.jsp" name="commodity" >
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top"> 
    <td bgcolor="#ffffff">
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td ><table width="790" border="0" ><tr class='formlabel'><td>Commodity - Add </td><td align=right><%=loginbean.generateUniqueId("ETCCommodityAdd.jsp","Add")%></td></tr></table></td>
        </tr>
        </table>
            
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="4">&nbsp;</td></tr>
                <tr class='formdata'> 
                 <td   width="290">CommodityId:
				  <font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="commodityId" size="8" maxlength="5" onBlur="changeToUpper(this)" onkeyPress="return getKeyCode(commodityId)">
                    </td>
					
                  <td  width="320" >Description:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="commodityDescription" size="40" maxlength="50" onBlur="changeToUpper(this)" onkeyPress="return getSpecialCode(commodityDescription)">
                    </td>
					 <td  width="340" colspan="2" > 
                    
					Handling 
                      Info:<br>
                      <textarea rows="3" class='select' name="commodityHandlingInfo" cols="34" maxlength="2000" onBlur="changeToUpper(this)" onkeyPress="return getSpecialCode(commodityHandlingInfo)"></textarea>
                  </td>
					
               </tr>
				
                <tr class='formdata'> 
				<td   width="200" >Hazard Indicator:<br>
                    <input type="checkbox" name="hazardIndicator" value="Y" onClick="star()" >
                    </td>
					<td width=200>
					Sub-Class:<font color="#FF0000"><span id="Label">&nbsp;</span></font><br>
					<input type="text" class='text' name="subClass" size="10" maxlength="12"  onBlur="changeToUpper(this)" onKeyPress="return getSpecialCode(subClass)">
					</td>
					<td width=200>
					Un&nbsp;Number:<font color="#FF0000"><span id="Label1">&nbsp;</span></font><br>
					<input type="text" class='text' name="unNumber" size="10" maxlength="12"  onBlur="changeToUpper(this)" onKeyPress="return getSpecialCode(unNumber)">
					</td>
					<td width=200>
					Class&nbsp;Type:
					<input type="text" class='text' name="classType" size="10" maxlength="12"  onBlur="changeToUpper(this)" onKeyPress="return getSpecialCode(classType)">
					</td>
					<tr>
                <tr class='formdata'> 
                  <td  colspan="4">Commodity 
                    Type:<font color="#FF0000">*</font><br>
                    <select size="1" name="commodityType" class='select'>
                      <option selected>(Select)</option>
                      <option>Edible,Animal & Vegetable Products</option>
                      <option>AVI,Inedible Animal & Vegetable Products</option>
                      <option>Textiles,Fibres & Mfrs.</option>
                      <option>Metals,Mfrs Excl M/C , Vehicles & Electrical Equipment</option>
		              <option>Machinery,Vehicles & Electrical Equipment</option>
                      <option>Non-Metallic Minerals & Mfrs.</option>
                      <option>Chemicals & Related Products</option>
                      <option>Paper,Reed,Rubber & Wood Mfrs.</option>
                      <option>Scientific,Professional & Precision Instruments</option>
                      <option> Miscellaneous</option>
                    </select>
                    </td>
                </tr>
                <tr class='denotes'> 
                  <td colspan="3" valign="top">
		  <font color=navy>Commodity Ids to conform to Master Item Numbering and Description System of IATA</font></td></tr>
		  <tr class='denotes'><td  valign="top">
		  <font color="#FF0000">*</font>Denotes 
                    Mandatory </td>
                  <td  colspan="2" valign="top" align="right"> 
					<input type="hidden" name="Operation" value="Add">
                      <input type="submit" value="Submit" name="submit" class='input'>
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
	 //Logger.error(FILE_NAME,"Error in CommodityAdd.jsp file : "+e.toString());
   logger.error(FILE_NAME+"Error in CommodityAdd.jsp file : "+e.toString());
	
	 errorMessageObject = new ErrorMessage("Error while accessing the page","ETCCommodityAdd.jsp"); 
	 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
	 errorMessageObject.setKeyValueList(keyValueList);
     request.setAttribute("ErrorMessage",errorMessageObject);
		
	 /**
	 session.setAttribute("ErrorCode","ERR");
	 session.setAttribute("ErrorMessage","Error while accessing the page" );
     session.setAttribute("NextNavigation","ETCCommodityAdd.jsp");  */
  
%>
<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
 }	   
%>  
