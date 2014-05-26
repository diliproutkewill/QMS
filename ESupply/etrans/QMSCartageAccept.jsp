<%--

	Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
	This software is the proprietary information of FourSoft, Pvt Ltd.
	Use is subject to license terms.
	esupply - v 1.x.
	Program	Name	: QMSCartageAccept.jsp
	Product Name	: QMS
	Module Name		: Cartage
	Task		    : Cartage Acceptence
	Date started	: 11-10-2005 	
	Date Completed	: 
	Date modified	:  
	Author    		: Rama Krishna .Y
	Description		: The application "Acceptence" Of Cartage sell rate.
	Actor           :
	Related Document: CR_DHLQMS_1005

--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%@page import =	"	com.foursoft.esupply.common.java.FoursoftConfig,
						java.util.ArrayList,
						java.util.HashMap,
						java.util.Set,
						java.util.Iterator,            
						org.apache.log4j.Logger,
						com.foursoft.esupply.common.java.ErrorMessage,
						com.foursoft.esupply.common.java.KeyValue,
						com.qms.operations.sellrates.java.QMSSellRatesDOB,
						com.qms.operations.sellrates.java.QMSBoundryDOB,
            com.qms.operations.charges.java.QMSCartageMasterDOB,
						com.foursoft.esupply.common.util.ESupplyDateUtility "%>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSCartageAccept.jsp";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);	
    ErrorMessage		errorMessageObject		= null;
	ArrayList			keyValueList			= new ArrayList();
   try
   {
          QMSCartageMasterDOB         dob   =  null;
          ArrayList cartageBuyChargesList   =  (ArrayList)session.getAttribute("CartageBuyCharges");

if(cartageBuyChargesList!=null && cartageBuyChargesList.size()>0)
{
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<title>Cartage Accept</title>
<link rel="stylesheet" >
</head>
<script language='javascript'>
function showModify(zoneCodes,cartageId,chargeType,weightBreak,index,uom,chargeBasis)
{  
	var rateType    = document.forms[0].rateType.value;
	var chargeBasis = document.forms[0].chargeBasis.value;
	var cheObh		=	document.getElementsByName("chkBox");
	var butObj		=	document.getElementsByName("modify");
	butObj[index].disabled=true;
	cheObh[index].checked=true;
	setValue(index);
	var URL         = "QMSCartageController?zoneCode="+zoneCodes+"&weightBreak="+weightBreak+"&Operation=cartageAccept&subOperation=acceptModify&cartageId="+cartageId+"&chargeType="+chargeType+"&index="+index+"&uom="+uom+"&chargeBasis="+chargeBasis;
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 900,height = 500,resizable = yes';
	var Features 	= Bars +' '+ Options;
	var Win 		= open(URL,'Doc',Features);
}
function selectAll()
{
	var checkBoxes = document.getElementsByName("chkBox");
	
	if(document.forms[0].select.checked)
	{
		for(var i=0;i<checkBoxes.length;i++)
		{		
			checkBoxes[i].checked=true;
			setValue(i);
		}
	}
	else
	{
		for(var i=0;i<checkBoxes.length;i++)
		{
			checkBoxes[i].checked=false;
			setValue(i);
		}
	}
}
function setValue(index)
{
	if(document.getElementsByName("chkBox")[index].checked)
		document.getElementsByName("checkBoxValue")[index].value="YES";
	else
		document.getElementsByName("checkBoxValue")[index].value='';

}
</script>
<body>

<form action="QMSCartageController" method="post"  >
 
  <table border="0" cellPadding="0" cellSpacing="0" width = '100%'>
    <tbody>
      <tr  vAlign="top">
        
          <table border="0" cellPadding="4" cellSpacing="0" width = '100%'>
            <tbody>
              <tr class="formlabel" vAlign="top">
                <td colspan="6" height="20" align="center" >
                  <p align="left">UPDATED CARTAGE SELL CHARGES
                </td><td colspan="6" height="20" align="center" >
                  <p align="right">QS1050811
                </td>
              </tr></tbody>
			  </table>
			  <table border="0" cellPadding="4" cellSpacing="1" height="77" width = '100%'>
            <tbody>
              <tr class="text" vAlign="top">
                <td colspan="6" /td height="15">
              </tr>
              <tr class="formheader" vAlign="top">
                <td height="1" align="center" >
                  Accept<br>
                  <input type="checkbox" name="select" value="ON" onClick='selectAll()'></td>
                <td height="1" align="center" >MODIFY</td>
                <td height="1" align="center" >
                  Location ID
                </td>
                <td height="1" align="center" >
                  Terminal ID
                </td>
                <td align="center" height="1" >Zone Code</td>
                <td align="center" height="1" >Charge Type<br>
                  (Pickup/Delivery/Both)</td>
              </tr>
<%
			     for(int i=0;i<cartageBuyChargesList.size();i++)
				{
				    dob    =    (QMSCartageMasterDOB)cartageBuyChargesList.get(i);
%>
              <tr class="formdata" vAlign="top">
                <td height="1" valign="middle" align="center" ><input type="checkbox" name="chkBox" value="ON" onClick='setValue(<%=i%>)'></td>
                <td align="center" >
					<input type="button" value="Modify" name='modify' class="input" onClick = "showModify('<%=dob.getZoneCode()%>','<%=dob.getCartageId()%>','<%=dob.getChargeType()%>','<%=dob.getWeightBreak()%>','<%=i%>','<%=dob.getUom()%>','<%=dob.getChargeBasis()%>')">
				</td>
                <td height="1" valign="middle" align="center" ><input type='hidden' name='locationId' value='<%=dob.getLocationId()%>'><%=dob.getLocationId()%></td>
                <td align="center" height="1" valign="middle" ><input type='hidden' name='rateType' value='<%=dob.getRateType()%>'><%=dob.getTerminalId()%></td>
                <td align="center" height="1" valign="middle" ><input type='hidden' name='zoneCode' value='<%=dob.getZoneCode()%>'><%=dob.getZoneCode()%></td>
                <td align="center" height="1" valign="middle" ><input type='hidden' name='chargeType' value='<%=dob.getChargeType()%>'><%=dob.getChargeType()%></td>
				<input type='hidden' name='weightBreak' value='<%=dob.getWeightBreak()%>'>
				<input type='hidden' name='chargeBasis' value='<%=dob.getChargeBasis()%>'>
				<input type='hidden' name='cartageId' value='<%=dob.getCartageId()%>'>
				<input type='hidden' name='zoneCodes' value='<%=dob.getZoneCode()%>'>
				<input type='hidden' name='index' value='<%=i%>'>
				<input type='hidden' name='minCharge' value=''>
				<input type='hidden' name='maxCharge' value=''>
				<input type='hidden' name='checkBoxValue'>
				<input type='hidden' name='subOperation' value='submit'>
				<input type='hidden' name='Operation' value='cartageAccept'>
              </tr>
              <%}%>
              <tr class="formdata" vAlign="top">
                <td height="27" valign="middle" colspan="6" ></td>
              </tr>
              <tr class="text">
                <td height="27" valign="middle" colspan="6" >
                  <p align="right"><input type="submit" value="Submit" class="input">
                </td>
              </tr>
            </tbody>
          </table>
       
      </tr>
    </tbody>
  </table>  
</form>
</body>
</html>
<%}else{
	        errorMessageObject = new ErrorMessage("No Records found.","QMSCartageController"); 
			keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
			keyValueList.add(new KeyValue("Operation","cartageAccept")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);

%>		<jsp:forward page="../ESupplyErrorPage.jsp"/>

<%
	  }%>
<%}
	  catch(Exception e)
	  {
		  e.printStackTrace();
		  //Logger.error(FILE_NAME,"Error while Accessing the page "+e.toString());		  
      logger.error(FILE_NAME+"Error while Accessing the page "+e.toString());		  
			errorMessageObject = new ErrorMessage("Unable to get the Details! Please Try Again.","QMSCartageController"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","cartageAccept")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);

%>		<jsp:forward page="../ESupplyErrorPage.jsp"/>

<%
	  }

%>