<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSChargesMaster.jsp
Product Name	: QMS
Module Name		: Charges master
Task		    : Adding/View/Modify/Delete ChargesMaster
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete" ChargesMaster information
Actor           :
Related Document: CR_DHLQMS_1005
--%>
<%@page import = "com.qms.setup.java.ChargesMasterDOB,
				  com.qms.setup.ejb.sls.SetUpSessionHome,
				  com.qms.setup.ejb.sls.SetUpSession,
		          javax.naming.InitialContext,
				  javax.naming.Context,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList,
				  javax.ejb.ObjectNotFoundException"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSChargesMaster.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String operation						=	request.getParameter("Operation");
	String chargeId							=	request.getParameter("chargeId");
	String costincurred						=	request.getParameter("costincurred");
	ChargesMasterDOB	chargesMasterDOB	=	null;
	String	airChecked						=	"";
	String  seaChecked						=	"";
	String	truckChecked					=	"";
	int		shipMode						=	0;
	boolean	success							=	true;
	String	msg						=	"";
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	ArrayList   chargesList         =   null;
		try{
			//System.out.println("operation"+operation+"klasdfj::"+loginbean.getAccessType());
			if(operation!=null && (operation.equals("View") || operation.equals("Modify") || operation.equals("Delete")|| operation.equals("Invalidate")|| operation.equals("ViewAll")))
			{ 
				InitialContext initial		= new InitialContext();
				SetUpSessionHome	home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
				SetUpSession		remote	=	(SetUpSession)home.create();
				if(!("Invalidate".equalsIgnoreCase(operation)|| operation.equals("ViewAll"))){
				if(chargeId!=null )
					{	chargesMasterDOB			=	remote.getChargesMasterDtl(chargeId,loginbean,operation);}
				if(chargesMasterDOB.getShipmentMode()>0)
				{
					shipMode	=	chargesMasterDOB.getShipmentMode();
					if(shipMode==1 || shipMode==3  || shipMode==5 || shipMode==7)
					{
						airChecked	=	"Checked";
					}
					if(shipMode==2 || shipMode==3  || shipMode==6 || shipMode==7)
					{
						seaChecked	=	"Checked";
					}
					if(shipMode==4  || shipMode==5 || shipMode==6 || shipMode==7)
					{
						truckChecked=	"Checked";
					}
				}
				}
				else{
					chargesList			=	remote.getChargeMasterDetails();
					session.setAttribute("chargeList",chargesList);
					//System.out.println("chargeList  2324 "+chargesList);
				}
			}
		
%>
<html>
<head>
<title>Charges -Add</title>    <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="../html/dynamicContent.js"></script>
<script src ='../html/eSupply.js'></script>
<script>
<%
	String onLoad	="";
	if(operation!=null && operation.equals("Add"))
	{
		onLoad	="initialize()";
	%>
//************Dynamic row methods starts******************//
		function initialize()
		{
			importXML('../xml/chargesmaster.xml');
		}
		function validateBeforeDeletion()
		{
			return true;
		}
		function validateBeforeCreation()
		{
		
			return true;
		}
		function initializeDynamicContentRows()
		{
			setValues();
		}
		function setValues()
		{
			var tableObj = document.getElementById("chargemaster");
			if(false)
			{}
			else
			{
				if(tableObj.getAttribute("idcounter")==1)
							createDynamicContentRow(tableObj.getAttribute("id"));
			}

		}
		function defaultFunction(currentRow)
		{
			var aircheckname	=	document.getElementById("aircheckbox"+currentRow);
			var seacheckname	=	document.getElementById("seacheckbox"+currentRow);
			var truckcheckname	=	document.getElementById("truckcheckbox"+currentRow);
			aircheckname.name	=	aircheckname.name+currentRow;
			seacheckname.name	=	seacheckname.name+currentRow;
			truckcheckname.name	=	truckcheckname.name+currentRow;

		}
		function defaultDeleteFunction()
		{
		


		}
	//*************end of dynamic row methods********//
<%
	}
%>
	function isShipModeChecked()
	{
<%
	if(operation!=null && operation.equals("Add"))
	{
%>
		var airCheck	=	document.forms[0].aircheckbox;
		var seaCheck	=	document.forms[0].seacheckbox;
		var truckCheck	=	document.forms[0].truckcheckbox;
		if(parseInt(airCheck.length)>1)
		{
			for(var i=0;i<airCheck.length;i++)
			{
				if(!airCheck[i].checked && !seaCheck[i].checked && !truckCheck[i].checked)
				{
					alert("Please,select atleast one shipmentMode at LaneNO :"+(i+1));
					airCheck[i].focus();
					return false;
				}
			}
		}
		else
		{
				if(!airCheck.checked && !seaCheck.checked && !truckCheck.checked)
				{
					alert("Please,select atleast one shipmentMode");
					airCheck.focus();
					return false;
				}
		}
<%
	}else if(operation!=null && operation.equals("Modify"))
		{
%>
				var airCheck	=	document.forms[0].aircheckbox1;
				var seaCheck	=	document.forms[0].seacheckbox1;
				var truckCheck	=	document.forms[0].truckcheckbox1;
				
				if(!airCheck.checked && !seaCheck.checked && !truckCheck.checked)
				{
					alert("Please,select atleast one shipmentMode");
					airCheck.focus();
					return false;
				}
<%
		}
%>
		return true;
	}

	function isChargeEntered()
	{
		var chargeId	=	document.forms[0].chargeid;
		if(parseInt(chargeId.length)>1)
		{
			for(var i=0;i<chargeId.length;i++)
			{

				if(chargeId[i].value=='')
				{
					
					alert("Please,Enter the chargeId at LaneNo :"+(i+1));
					chargeId[i].focus();
					return false;
				}else
				{
					if(chargeId[i+1]!=null)
					{
						for(j=i+1;j<chargeId.length;j++)
						{     
							 if(chargeId[j].value==chargeId[i].value)
							 {
								alert("Please,the chargeId is duplicated at LaneNo :"+(i+1));
								chargeId[j].focus();
								return false;
							 }
						}
					}
				}
			}			
		}else
		{
				if(chargeId.value=='')
				{
					alert("Please,Enter the chargeId");
					chargeId.focus();
					return false;
				}
		}

		return true;
	}

	function mandatory()
	{
<%
	if(operation!=null && operation.equals("Add"))
	{
%>
		if(!isChargeEntered())
			return false;
<%
	}
%>
		if(!isShipModeChecked())
			return false;

		if(document.forms[0].submit!=null)
		{
			document.forms[0].submit.disabled	=true;
		}
		if(document.forms[0].resetB!=null)
		{
			document.forms[0].resetB.disabled	=true;
		}

		return true;
	}
function viewContinue()//added by rk
{
	document.forms[0].action='QMSChargesMasterEnterId.jsp?Operation=View';
	document.forms[0].submit();
	return true;
}
</script>
</head>
<body onLoad=<%=onLoad%>>
<form method=post name=chargesadd action="QMSChargesMasterProcess.jsp"  onSubmit ="return mandatory()">
  <table border=0 cellpadding=0 cellspacing=0 width=800>
    <tr valign=top> 
      <td width=800> 
        <table border=0 cellpadding=4 cellspacing=1 width=800>
          <tr class='formlabel' valign=top> 
            <td  colspan=5  width=800>
              <table width="790" border="0" >
                <tr class='formlabel'>
                  <td>Charges -<%=operation%> </td>
                  <td align=right>QS1050111</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
<%
	if(operation!=null && operation.equals("Add"))
	{
	%>        
        <table border=0 cellpadding=4 cellspacing=1 width=800 id="chargemaster"  idcounter="1" 
         defaultElement="chargeid" xmlTagName="chargesmaster" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" maxRows=50 bgcolor='#FFFFFF'>
		  <tr valign=top class='formheader'>
            <td ></td>
			<td >Charge Id:<font color="#FF0000">*</font></td>
            <td >Description:</td>
            <td >Shipment Mode:<font color="#FF0000">*</font></td>
            <td >Cost Incurred At:</td>
			<td ></td>
          </tr>
		</table>
<%
	}else if(operation!=null && (operation.equals("View") || operation.equals("Delete")))
	{
%>
        <table border=0 cellpadding=4 cellspacing=1 width=800 bgcolor='#FFFFFF'>
		  <tr valign=top class='formheader'>
            <td ></td>
			<td >Charge Id:<font color="#FF0000">*</font></td>
            <td >Description:</td>
            <td >Shipment Mode:<font color="#FF0000">*</font></td>
            <td >Cost Incurred At:</td>
			<td ></td>
          </tr>
<%	
		if(chargesMasterDOB!=null )
		{
%>
			<tr valign=top class='formdata'>
			<td></td>
			<td><%=(chargesMasterDOB.getChargeId()!=null)?chargesMasterDOB.getChargeId():""%>
			<input type='hidden' name ='chargeid' value='<%=(chargesMasterDOB.getChargeId()!=null)?chargesMasterDOB.getChargeId():""%>'></td>
			<td><%=(chargesMasterDOB.getChargeDesc()!=null)?chargesMasterDOB.getChargeDesc():""%>
			<input type='hidden' name ='chargedesc' value='<%=(chargesMasterDOB.getChargeDesc()!=null)?chargesMasterDOB.getChargeDesc():""%>'></td>
			<td><input type='checkbox' name ='aircheckbox1' <%=airChecked%> disabled>Air
				<input type='checkbox' name ='seacheckbox1' <%=seaChecked%> disabled>Sea
				<input type='checkbox' name ='truckcheckbox1' <%=truckChecked%> disabled>Truck
			</td>
			<td><%=(chargesMasterDOB.getCostIncurr()!=null)?chargesMasterDOB.getCostIncurr():""%>
			<input type='hidden' name ='costincurr' value='<%=(chargesMasterDOB.getCostIncurr()!=null)?chargesMasterDOB.getCostIncurr():""%>'></td>
			<td></td>
			</tr>
<%
		}
%>
		</table>
<%
	}else if(operation!=null && (operation.equals("Modify")))
	{
%>
        <table border=0 cellpadding=4 cellspacing=1 width=800 bgcolor='#FFFFFF'>
		  <tr valign=top class='formheader'>
            <td ></td>
			<td >Charge Id:<font color="#FF0000">*</font></td>
            <td >Description:</td>
            <td >Shipment Mode:<font color="#FF0000">*</font></td>
            <td >Cost Incurred At:</td>
			<td ></td>
          </tr>
<%	
		if(chargesMasterDOB!=null)
		{
%>
			<tr valign=top class='formdata'>
			<td></td>
			<td><%=(chargesMasterDOB.getChargeId()!=null)?chargesMasterDOB.getChargeId():""%>
			<input type='hidden' name ='chargeid' value='<%=(chargesMasterDOB.getChargeId()!=null)?chargesMasterDOB.getChargeId():""%>'></td>
			<td><input type='text' name ='chargedesc' class='text' value='<%=(chargesMasterDOB.getChargeDesc()!=null)?chargesMasterDOB.getChargeDesc():""%>' maxlength='50'></td>
			<td><input type='checkbox' name ='aircheckbox1' <%=airChecked%> >Air
				<input type='checkbox' name ='seacheckbox1' <%=seaChecked%> >Sea
				<input type='checkbox' name ='truckcheckbox1' <%=truckChecked%> >Truck
			</td>
			<td>
				<select name='costincurr' class='select'>
					<option value = 'Origin' <%=(chargesMasterDOB.getCostIncurr()!=null && chargesMasterDOB.getCostIncurr().equals("Origin"))?"Selected":""%>>Origin</option>
					<option value = 'Destination' <%=(chargesMasterDOB.getCostIncurr()!=null && chargesMasterDOB.getCostIncurr().equals("Destination"))?"Selected":""%>>Destination</option>
				</select></td>
			<td></td>
			</tr>
<%
		}
%>
		</table>
<%
	}else{%>
	      <table border=0 cellpadding=4 cellspacing=1 width=800 bgcolor='#FFFFFF'>
		    <tr valign=top class='formheader'>
            <td ></td>
			<td >Charge Id:<font color="#FF0000">*</font></td>
            <td >Description:</td>
            <td >Shipment Mode:<font color="#FF0000">*</font></td>
            <td >Cost Incurred At:</td>
			<%if( operation.equals("Invalidate")){%>
			<td >Invalidate</td>
			<%}%>
			<td ></td>
            </tr>
	<%
				for(int i=0;i<chargesList.size();i++){
					chargesMasterDOB  = (ChargesMasterDOB)chargesList.get(i);
					airChecked ="";//added by rk 13112
					seaChecked = "";
					truckChecked = "";
					if(chargesMasterDOB.getShipmentMode()>0)
					{
						shipMode	=	chargesMasterDOB.getShipmentMode();
						if(shipMode==1 || shipMode==3  || shipMode==5 || shipMode==7)
						{
							airChecked	=	"Checked";
						}
						if(shipMode==2 || shipMode==3  || shipMode==6 || shipMode==7)
						{
							seaChecked	=	"Checked";
						}
						if(shipMode==4  || shipMode==5 || shipMode==6 || shipMode==7)
						{
							truckChecked=	"Checked";
						}
					}//System.out.println(chargesMasterDOB.getInvalidate()+"  77777777777");
%>
			<tr valign=top class='formdata'>
			<td></td>
			<td><%=(chargesMasterDOB.getChargeId()!=null)?chargesMasterDOB.getChargeId():""%>
			<input type='hidden' name ='chargeid' value='<%=(chargesMasterDOB.getChargeId()!=null)?chargesMasterDOB.getChargeId():""%>'></td>
			<td><%=(chargesMasterDOB.getChargeDesc()!=null)?chargesMasterDOB.getChargeDesc():""%>
			<input type='hidden' name ='chargedesc' value='<%=(chargesMasterDOB.getChargeDesc()!=null)?chargesMasterDOB.getChargeDesc():""%>'></td>
			<td><input type='checkbox' name ='aircheckbox1' <%=airChecked%> disabled>Air
				<input type='checkbox' name ='seacheckbox1' <%=seaChecked%> disabled>Sea
				<input type='checkbox' name ='truckcheckbox1' <%=truckChecked%> disabled>Truck
			</td>
			<td><%=(chargesMasterDOB.getCostIncurr()!=null)?chargesMasterDOB.getCostIncurr():""%>
			<input type='hidden' name ='costincurr' value='<%=(chargesMasterDOB.getCostIncurr()!=null)?chargesMasterDOB.getCostIncurr():""%>'></td>
			<%if(operation.equals("Invalidate")){%>
			<td><input type='checkBox' name ="invalidate<%=i%>" <%="T".equalsIgnoreCase(chargesMasterDOB.getInvalidate())?"checked":""%>></td>
			<%}%>
			<td></td>
			</tr>
<%}%></table><%}%>
		<table width="800" border="0" cellspacing="0" cellpadding="0" bgcolor='#FFFFFF'>
			<tr valign="top" bgcolor="#FFFFFF">
			  <td>
				<table border="0" width="800" cellpadding="4" cellspacing="1">
				  <tr> 
					<td colspan=3 valign=top> 
					  <div align=left> <font face=Verdana size=2>All Fields are Mandatory 
						</font> </div>
					</td>
					<td colspan=4 align=right> 
<%
		if(operation!=null && (operation.equals("Add") || operation.equals("Modify")))
		{
%>
					 <input type="submit" value="Submit" name="submit" class="input" >
					 <input type="reset" value="Reset" name="resetB" class="input">
<%		}else if(operation!=null && operation.equals("Delete")||operation.equalsIgnoreCase("Invalidate"))
		{
%>
					  <input type="submit" value="Submit" name="submit" class="input">
<%		}else{
%>
					<input type="button" value="Continue" name="Continue" class="input" onClick='return viewContinue()'>
<%}%>
					  <input type ='hidden' name ='Operation' value='<%=operation%>'>
					</td>
				  </tr>
				</table>
			</td>
		  </tr>
		</table>
      </td>
    </tr>
  </table>
</form>
</body>
</html>
<%	}catch(ObjectNotFoundException e)
	{e.printStackTrace();
		success=false;
		msg	=	"Record doesn't exist for "+operation+" with Charge Id :";
		//Logger.error(FILE_NAME,msg+e);
    logger.error(FILE_NAME+msg+e);
	}catch(Exception e)
	{e.printStackTrace();
		success	=	false;
		msg	=	"Exception while reading the data :";
		//Logger.error(FILE_NAME,"Exception while retrieving the data"+e);
    logger.error(FILE_NAME+"Exception while retrieving the data"+e);
	}
	if(!success)
	{
				if(operation!=null && operation.equals("Modify"))
				{
					errorMessage			=  msg+chargeId;
					url						=	"QMSChargesMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && (operation.equals("Delete")))
				{
					errorMessage			=	msg+chargeId;
					url						=	"QMSChargesMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation!=null && (operation.equals("View")||operation.equals("Invalidate")))
				{
					errorMessage			=	msg+chargeId;
					url						=	"QMSChargesMasterEnterId.jsp?Operation=View";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","View")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
	%>
					<jsp:forward page="../QMSESupplyErrorPage.jsp" />
	<%
	}
%>