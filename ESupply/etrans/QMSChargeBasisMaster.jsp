<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSChargeBasisMaster.jsp
Product Name	: QMS
Module Name		: Charge Basis Master
Task		    : Adding/View/Modify/Delete chargebasis
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete" chargebasis information
Actor           :
Related Document: CR_DHLQMS_1005
--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%@page import = "com.qms.setup.java.ChargeBasisMasterDOB,
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
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSChargeBasisMaster.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String operation		=	request.getParameter("Operation");
	String chargeGroupId	=	request.getParameter("chargeBasisId");
	boolean	success					=	true;
	String	msg						=	"";
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	ChargeBasisMasterDOB	chargeBasisMasterDOB = null;
	ArrayList   chargesList         =   null;
	String calculation	=	"";
		try{
			if(operation!=null && (operation.equals("View") || operation.equals("Modify") || operation.equals("Delete")|| operation.equals("Invalidate")|| operation.equals("ViewAll")))
			{
				InitialContext initial		= new InitialContext();
				SetUpSessionHome	home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
				SetUpSession		remote	=	(SetUpSession)home.create();
				if(chargeGroupId!=null && !("Invalidate".equalsIgnoreCase(operation)||"ViewAll".equalsIgnoreCase(operation)))
					{	chargeBasisMasterDOB	=	remote.getChargeBasisDtl(chargeGroupId,operation,loginbean);}
				else{
					chargesList			=	remote.getChargeBasisDetails();
					session.setAttribute("chargeList",chargesList);
					//System.out.println("chargesList  "+chargesList);
				}
			}
		
%>

<html>
<head>
<title>Charge Basis</title>	<%@ include file="../ESEventHandler.jsp" %>
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
			importXML('../xml/chargebasismaster.xml');
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
			var tableObj = document.getElementById("chargebasismaster");
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
		}
		function defaultDeleteFunction()
		{
		


		}
	//*************end of dynamic row methods********//
<%
	}
%>

<%
	if(operation!=null && operation.equals("Add"))
	{
%>
		function isChargeBasisEntered()
		{
			var chargebasisid	=	document.forms[0].chargebasisid;
			if(parseInt(chargebasisid.length)>1)
			{
				for(var i=0;i<chargebasisid.length;i++)
				{
					if(chargebasisid[i].value=='')
					{
						alert("Please,Enter the chargeBasisId at LaneNo :"+(i+1));
						chargebasisid[i].focus();
						return false;
					}
				}			
			}else
			{
					if(chargebasisid.value=='')
					{
						alert("Please,Enter the chargeBasisId");
						chargebasisid.focus();
						return false;
					}
			}

			return true;
		}
		function isChargeBasisDuplicate()
		{
			var chargebasisid	=	document.forms[0].chargebasisid;
			if(parseInt(chargebasisid.length)>1)
			{
				for(var i=0;i<chargebasisid.length;i++)
				{
					for(var j=i+1;j<chargebasisid.length;j++)
					{
						if(chargebasisid[i].value==chargebasisid[j].value)
						{
							alert("Duplicate Charge BasisIds are found at LaneNO :"+j);
							chargebasisid[j].value='';
							chargebasisid[j].focus();
							return false;
						}
					}
				}
			}
			return true;
		}
<%
	}
%>
<%
	if(operation!=null && (operation.equals("Add") || operation.equals("Modify")))
	{
%>
		function setReadOnly(input)
		{
			var id		=	input.id;
			var index	=	id.substring(input.name.length);
			var blockId	=	document.getElementById("block"+index);
			if(input.value=='Auto')
			{	
				blockId.value	=	'';
				blockId.readOnly=	false;
			}else if(input.value=='Manual')
			{
				blockId.value	=	'';
				blockId.readOnly=	true;
			}
		}

		function isPrinaryNCalSelected()
		{
			var primaryBasis	=	document.forms[0].primary;
			//var calculation		=	document.forms[0].calculation;
			var chargebasisdesc	=	document.forms[0].chargebasisdesc;
			if(parseInt(chargebasisdesc.length)>1)
			{
				for(var i=0;i<chargebasisdesc.length;i++)
				{
					if(primaryBasis[i].value=='')
					{
						alert("Please Select primaryBasis at LaneNo :"+(i+1));
						primaryBasis[i].focus();
						return false;
					}
					if(chargebasisdesc[i].value=='')
					{
						alert("Please Select chargeBasisDescription at LaneNo :"+(i+1));
						chargebasisdesc[i].focus();
						return false;
					}
					/*if(calculation[i].value=='')
					{
						alert("Please Select Calculation at LaneNo :"+(i+1));
						calculation[i].focus();
						return false;
					}*/
				}
			}else
			{
					if(primaryBasis.value=='')
					{
						alert("Please Select primaryBasis");
						primaryBasis.focus();
						return false;
					}
					if(chargebasisdesc.value=='')
					{
						alert("Please Select chargeBasisDescription at LaneNo :"+(i+1));
						chargebasisdesc.focus();
						return false;
					}
					/*if(calculation.value=='')
					{
						alert("Please Select Calculation");
						calculation.focus();
						return false;
					}*/
			}

			return true;
		}
<%
	}
%>

	function mandatory()
	{
<%
	if(operation!=null && operation.equals("Add"))
	{
%>
		if(!isChargeBasisEntered() || !isPrinaryNCalSelected())
			return false;
		if(!isChargeBasisDuplicate())
			return false;
<%
	}
%>
<%
	if(operation!=null && operation.equals("Modify"))
	{
%>
		if(!isPrinaryNCalSelected())
			return false;
<%
	}
%>
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
	document.forms[0].action='QMSChargeBasisMasterEnterId.jsp?Operation=View';
	document.forms[0].submit();
	return true;
}
</script>
</head>

<body onLoad=<%=onLoad%>>
<form name="form1" method="post" action ='QMSChargeBasisMasterProcess.jsp' onSubmit='return mandatory()'>
  <table width="800" border="0" cellspacing="0" cellpadding="0" bgcolor='#FFFFFF'>
    <tr> 
      <td valign="top" > 
        <table width="800" border="0" cellspacing="1" cellpadding="4">
          <tr class='formlabel'> 
            <td colspan="3">
              <table width="790" border="0" >
                <tr class='formlabel'>
                  <td>Charge Basis Master - <%=operation%></td>
                  <td align=right>QS1050411</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
<%
	if(operation!=null && operation.equals("Add"))
	{
%>  
        <table width="800" border="0" cellspacing="1" cellpadding="4" id="chargebasismaster"  idcounter="1" 
         defaultElement="chargebasis" xmlTagName="chargebasismaster" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" maxRows=20 bgcolor='#FFFFFF'>
          <tr class='formheader'> 
            <td ></td>
			<td align=center>Charge Basis:<font color="#FF0000">*</font></td>
			<td align=center>Description:<font color="#FF0000">*</font></td>
			<td align=center>Block:</td>
			<td align=center>Primary Basis:<font color="#FF0000">*</font></td>
			<td align=center>Secondary Basis:</td>
			<td align=center>Tertiary Basis:</td>
<!-- 			<td align=center>Calculation*</td> -->
			<td ></td>
          </tr>
		</table>
 <%
	}else if(operation!=null && (operation.equals("View") || operation.equals("Delete")))
	{
%>
		<table width="800" border="0" cellspacing="1" cellpadding="4" bgcolor='#FFFFFF'>
          <tr class='formheader'> 
            <td ></td>
			<td align=center>Charge Basis:<font color="#FF0000">*</font></td>
			<td align=center>Description:<font color="#FF0000">*</font></td>
			<td align=center>Block:</td>
			<td align=center>Primary Basis:<font color="#FF0000">*</font></td>
			<td align=center>Secondary Basis:</td>
			<td align=center>Tertiary Basis:</td>
			<!-- <td align=center>Calculation*</td> -->
			<td ></td>
          </tr>
<%
		if(chargeBasisMasterDOB!=null)
		{
			String chargeBasis	=	(chargeBasisMasterDOB.getChargeBasis()!=null)?chargeBasisMasterDOB.getChargeBasis():"";
			String chargeDesc	=	(chargeBasisMasterDOB.getChargeDesc()!=null)?chargeBasisMasterDOB.getChargeDesc():"";	
			String block		=	(chargeBasisMasterDOB.getBlock()!=null)?chargeBasisMasterDOB.getBlock():"";	
			String primary		=	(chargeBasisMasterDOB.getPrimaryBasis()!=null)?chargeBasisMasterDOB.getPrimaryBasis():"";	
			String secondary	=	(chargeBasisMasterDOB.getSecondaryBasis()!=null)?chargeBasisMasterDOB.getSecondaryBasis():"";	
			String tertiary		=	(chargeBasisMasterDOB.getTertiaryBasis()!=null)?chargeBasisMasterDOB.getTertiaryBasis():"";	
			//String calculation	=	(chargeBasisMasterDOB.getCalculation()!=null)?chargeBasisMasterDOB.getCalculation():"";	
%>
		  <tr class='formdata'>
			<td></td>
			<td><%=chargeBasis%>
			<input type='hidden' name ='chargebasisid' value='<%=chargeBasis%>'></td>
			<td><input type='text' maxlength="50" name ='chargebasisdesc' class ='text' value='<%=chargeDesc%>' readonly ></td>
			<td><input type='text' maxlength="5" name ='block' class ='text' value='<%=block%>' readonly ></td>
			<td> 
				<select name='primary' class='select' >
				  <option value='<%=primary%>'><%=primary%></option>
				</select>
			</td>
			<td> 
				<select name='secondary' class='select' >
				  <option value='<%=secondary%>'><%=secondary%></option>
				</select>
			</td>
			<td> 
				<select name='tertiary' class='select' >
				  <option value='<%=tertiary%>'><%=tertiary%></option>
				</select>
			</td>
<!-- 			<td> 
				<select name='calculation' class='select' >
				  <option value='<%=calculation%>'><%=calculation%></option>
				</select>
			</td> -->
			<td></td>
		  </tr>
<%
		}else
		{
			throw new Exception("No data found");
		}
%>
		</table>
<%
	}else if(operation!=null && (operation.equals("Modify")))
	{
%>
		<table width="800" border="0" cellspacing="1" cellpadding="4" bgcolor='#FFFFFF'>
          <tr class='formheader'> 
            <td ></td>
			<td align=center>Charge Basis:<font color="#FF0000">*</font></td>
			<td align=center>Description:</td>
			<td align=center>Block:</td>
			<td align=center>Primary Basis:<font color="#FF0000">*</font></td>
			<td align=center>Secondary Basis:</td>
			<td align=center>Tertiary Basis:</td>
			<!-- <td align=center>Calculation*</td> -->
			<td ></td>
          </tr>
<%
		if(chargeBasisMasterDOB!=null)
		{
			String chargeBasis	=	(chargeBasisMasterDOB.getChargeBasis()!=null)?chargeBasisMasterDOB.getChargeBasis():"";
			String chargeDesc	=	(chargeBasisMasterDOB.getChargeDesc()!=null)?chargeBasisMasterDOB.getChargeDesc():"";	
			String block		=	(chargeBasisMasterDOB.getBlock()!=null)?chargeBasisMasterDOB.getBlock():"";	
			String primary		=	(chargeBasisMasterDOB.getPrimaryBasis()!=null)?chargeBasisMasterDOB.getPrimaryBasis():"";	
			String secondary	=	(chargeBasisMasterDOB.getSecondaryBasis()!=null)?chargeBasisMasterDOB.getSecondaryBasis():"";	
			String tertiary		=	(chargeBasisMasterDOB.getTertiaryBasis()!=null)?chargeBasisMasterDOB.getTertiaryBasis():"";	
			//String calculation	=	(chargeBasisMasterDOB.getCalculation()!=null)?chargeBasisMasterDOB.getCalculation():"";	
%>
		  <tr class='formdata'>
			<td></td>
			<td><%=chargeBasis%>
			<input type='hidden' name ='chargebasisid' value='<%=chargeBasis%>'></td>
			<td><input type='text' maxlength="50" name ='chargebasisdesc' class ='text' value='<%=chargeDesc%>' onBlur="specialCharFilter1(this.value,'chargeBasis');" ></td><!-- toUpper(this);@@Commented by Kameswari for the WPBN issue-29901 -->
			<td><input type='text' maxlength="5" name ='block' id='block1' class ='text' value='<%=block%>' onBlur='numberFilter(this,"Block");' ></td>
			<td> 
				<select name='primary' class='select' >
				  <option value=''></option>
                  <option value="KG" <%=(primary!=null && primary.equals("KG"))?"Selected":""%>>Per KG</option>
                  <option value="LB" <%=(primary!=null && primary.equals("LB"))?"Selected":""%>>Per LB</option>
                  <option value="CBM" <%=(primary!=null && primary.equals("CBM"))?"Selected":""%>>Per CBM</option>
                  <option value="Shipment" <%=(primary!=null && primary.equals("Shipment"))?"Selected":""%>>Per Shipment</option>
                  <option value="Document" <%=(primary!=null && primary.equals("Document"))?"Selected":""%>>Per Document</option>
                  <option value="Piece" <%=(primary!=null && primary.equals("Piece"))?"Selected":""%>>Per Piece</option>
                  <option value="Container" <%=(primary!=null && primary.equals("Container"))?"Selected":""%>>Per Container</option>
                  <option value="Pallet" <%=(primary!=null && primary.equals("Pallet"))?"Selected":""%>>Per Pallet</option>
                  <option value="Value" <%=(primary!=null && primary.equals("Value"))?"Selected":""%>>Value</option>
                  <option value="KM" <%=(primary!=null && primary.equals("KM"))?"Selected":""%>>Per KM</option>
                  <option value="Line" <%=(primary!=null && primary.equals("Line"))?"Selected":""%>>Per Line</option>
                  <option value="Person" <%=(primary!=null && primary.equals("Person"))?"Selected":""%>>Per Person</option>
				</select>
			</td>
			<td> 
				<select name='secondary' class='select' >
				  <option value=''></option>
                  <option value="Day" <%=(secondary!=null && secondary.equals("Day"))?"Selected":""%>>Per Day</option>
                  <option value="Hour" <%=(secondary!=null && secondary.equals("Hour"))?"Selected":""%>>Per Hour</option>
                  <option value="Invoice" <%=(secondary!=null && secondary.equals("Invoice"))?"Selected":""%>>Invoice</option>
                  <option value="Charge" <%=(secondary!=null && secondary.equals("Charge"))?"Selected":""%>>Charge</option>
                  <option value="Freight" <%=(secondary!=null && secondary.equals("Freight"))?"Selected":""%>>Freight</option>
                  <option value="List" <%=(secondary!=null && secondary.equals("List"))?"Selected":""%>>List</option>
				</select>
			</td>
			<td> 
				<select name='tertiary' class='select' >
                  <option value="Actual" <%=(tertiary!=null && tertiary.equals("Actual"))?"Selected":""%>>Actual</option>
                  <option value="Chargeable" <%=(tertiary!=null && tertiary.equals("Chargeable"))?"Selected":""%>>Chargeable</option>
				</select>
      </td>
<!-- 			<td> 
				<select name='calculation' id='calculation1' class='select' onChange='setReadOnly(this)'>
				  <option value=''></option>
                  <option value="Auto" <%=(calculation!=null && calculation.equals("Auto"))?"Selected":""%>>Auto</option>
                  <option value="Manual" <%=(calculation!=null && calculation.equals("Manual"))?"Selected":""%>>Manual</option>
				</select>
			</td> -->
			<td></td>
		  </tr>
<%
		}else
		{
			throw new Exception("No data found");
		}
%>
		</table>
<%
	}else {%>
	      <table border=0 cellpadding=4 cellspacing=1 width=800 bgcolor='#FFFFFF'>
		    <tr valign=top class='formheader'>
            <td ></td>
			<td align=center>Charge Basis:<font color="#FF0000">*</font></td>
			<td align=center>Description:</td>
			<td align=center>Block:</td>
			<td align=center>Primary Basis:<font color="#FF0000">*</font></td>
			<td align=center>Secondary Basis:</td>
			<td align=center>Tertiary Basis:</td>
			<!-- <td align=center>Calculation*</td> -->
			<%if("Invalidate".equalsIgnoreCase(operation)){%>
			<td align=center>Invalidate</td>
			<%}%>
			<td ></td>
            </tr>
	<%
				for(int i=0;i<chargesList.size();i++){
					chargeBasisMasterDOB  = (ChargeBasisMasterDOB)chargesList.get(i);
					String chargeBasis	=	(chargeBasisMasterDOB.getChargeBasis()!=null)?chargeBasisMasterDOB.getChargeBasis():"";
					String chargeDesc	=	(chargeBasisMasterDOB.getChargeDesc()!=null)?chargeBasisMasterDOB.getChargeDesc():"";	
					String block		=	(chargeBasisMasterDOB.getBlock()!=null)?chargeBasisMasterDOB.getBlock():"";	
					String primary		=	(chargeBasisMasterDOB.getPrimaryBasis()!=null)?chargeBasisMasterDOB.getPrimaryBasis():"";	
					String secondary	=	(chargeBasisMasterDOB.getSecondaryBasis()!=null)?chargeBasisMasterDOB.getSecondaryBasis():"";	
					String tertiary		=	(chargeBasisMasterDOB.getTertiaryBasis()!=null)?chargeBasisMasterDOB.getTertiaryBasis():"";	
					//String calculation	=	(chargeBasisMasterDOB.getCalculation()!=null)?chargeBasisMasterDOB.getCalculation():"";	
%>
			<tr valign=top class='formdata'>
			<td></td>
			<td><%=chargeBasis%>
			<input type='hidden' name ='chargebasisid' value='<%=chargeBasis%>'></td>
			<td><input type='text' maxlength="50" name ='chargebasisdesc' class ='text' value='<%=chargeDesc%>' readonly ></td>
			<td><input type='text'  maxlength="5" name ='block' class ='text' value='<%=block%>' readonly ></td>
		
			<!--@@Modified by Kameswari for the WPBN issue-38130-->
            <td><%=primary%></td>
			<td><%=secondary%></td>
			<td><%=tertiary%></td>
			
			<!---	<select name='primary' class='select' disabled=true  >
				  <option value='<%//=primary%>' ><%//=primary%> </option>
				</select>
			</td>
			<td> 
				<select name='secondary' class='select' disabled=true  >
				  <option value='<%//=secondary%>' ><%//=secondary%></option>
				</select>
			</td>
			<td> 
				<select name='tertiary' class='select' disabled=true  >
				  <option value='<%//=tertiary%>' ><%//=tertiary%></option>
				</select>
			
			</td>
            	<td> 
				<select name='calculation' class='select' >
				  <option value='<%//=calculation%>'><%//=calculation%></option>
				</select>
			</td>	 -->		
			<%if("Invalidate".equalsIgnoreCase(operation)){%>
			<td><input type='checkBox' name ="invalidate<%=i%>" <%="T".equalsIgnoreCase(chargeBasisMasterDOB.getInvalidate())?"checked":""%>></td>			
			<%}%>
			<td></td>
			</tr>
<%}%></table><%}%>
        <table width="800" border="0" cellspacing="0" cellpadding="4" bgcolor='#FFFFFF'>
          <tr class='text'> 
            <td colspan='4' >*Denotes Mandatory</td>
            <td colspan="4" align="right" width="397"> 
<%
		if(operation!=null && (operation.equals("Add") || operation.equals("Modify")))
		{
%>
					 <input type="submit" value="Submit" name="submit" class="input">
					 <input type="reset" value="Reset" name="resetB" class="input">
<%		}else if(operation!=null && operation.equals("Delete")||operation.equals("Invalidate"))
		{
%>
					  <input type="submit" value="Submit" name="submit" class="input">
<%		}else{
%>
					<input type="button" value="Continue" name="Continue" class="input" onClick='return viewContinue()'>
<%}%>
              <input type="hidden" name="Operation" value='<%=operation%>' >
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
		msg	=	"Record doesnt Exist for "+operation+" with Charge Id :";
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
					errorMessage			=  msg+chargeGroupId;
					url						=	"QMSChargeBasisMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && (operation.equals("Delete")))
				{
					errorMessage			=	msg+chargeGroupId;
					url						=	"QMSChargeBasisMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation!=null && (operation.equals("View")))
				{
					errorMessage			=	msg+chargeGroupId;
					url						=	"QMSChargeBasisMasterEnterId.jsp?Operation=View";
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