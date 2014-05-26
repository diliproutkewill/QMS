
<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSIndustryRegistration.jsp
Product Name	: QMS
Module Name		: Industry Registration
Task		    : Adding/View/Modify/Delete/Invalidate Industry
Date started	: 16-06-2005 	
Date Completed	: 22-06-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete/Invalidate" Industry information
Actor           :
Related Document: CR_DHLQMS_1002
--%>
<%@page import = "com.qms.setup.java.IndustryRegDOB,
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
	private static final String FILE_NAME="QMSIndustryRegistration.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String operation				=	request.getParameter("Operation");
	String industry					=	request.getParameter("industryId");
	ArrayList	industryList		=	null;
	IndustryRegDOB industryRegDOB	=	null;
	String[] industryId				=	null;
	String[] industryDesc			=	null;
	boolean		success				=	true;
	String	msg						=	"";
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	try{
			if(operation!=null && (operation.equals("View") || operation.equals("Modify") || operation.equals("Delete")  ||   operation.equals("Invalidate")))
			{
				InitialContext initial		= new InitialContext();
				SetUpSessionHome	home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
				SetUpSession		remote	=	(SetUpSession)home.create();
				if(!operation.equals("Invalidate"))
				{
					industryRegDOB				=	remote.getIndustryDetails(industry,loginbean);
				}else
				{
					industryList				=	remote.getAllIndustryDetails("",operation,loginbean);
					session.setAttribute("industryList",industryList);
					/*if(industryList!=null && industryList.size()>0)
					{
						industryId		=	new String[industryList.size()];
						industryDesc	=	new String[industryList.size()];
						for(int i=0;i<industryId.length;i++)
						{
							industryRegDOB	=	(IndustryRegDOB)industryList.get(i);
							industryId[i]	=	industryRegDOB.getIndustry();
							industryDesc[i]	=	industryRegDOB.getDescription();
						}

					}*/
				}
			}

%>
<html>
<head>
<title>Industry Registration <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="../html/dynamicContent.js"></script>
<script src ="../html/eSupply.js"></script>
<script >

//************Dynamic row methods starts******************//
	function initialize()
	{
		importXML('../xml/industryreg.xml');
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
		var tableObj = document.getElementById("industryregistration");
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
	
		var tableObj = document.getElementById("industryregistration");
		var idcount =tableObj.getAttribute("idcounter");
		idcount=idcount-1;
		tableObj.setAttribute("idcounter",idcount);

	}
//*************end of dynamic row methods********//
	function mandatory()
	{

		<%if("Add".equalsIgnoreCase(operation)||"Modify".equalsIgnoreCase(operation)){%>
		var industryid	=	document.getElementsByName("industry");
		var description =	document.getElementsByName("description");
		var index		=	industryid.length;
		for(i=0;i<index;i++)
		{
			if(industryid[i].value.length==0)
			{
				alert("Please Select the Industry Id");
				industryid[i].focus();
				return false;
			}
		}
		for(i=0;i<index;i++)
		{
			if(description[i].value.length==0)
			{
				alert("Please Enter the description");
				description[i].focus();
				return false;
			}
		}
		<%}%>
		if(industryform.b1!=null)
		{
			industryform.b1.disabled	=true;
		}
		<%
			if(operation!=null && (operation.equals("Add") || operation.equals("Modify") || operation.equals("Invalidate")))
			{
		%>
		if(industryform.reset!=null)
		{
			industryform.reset.disabled	=true;
		}
		<%	}
		%>
		return true;
	}
	function setFocus()
	{
		industryform.industry.focus();
	}
<%
	String onLoad	=	null;
	if(operation!=null && operation.equals("Add"))
	{
		onLoad	=	"initialize();";
	}
%>
function submitContinue()
{
	window.document.forms[0].action = 'QMSIndustryRegistrationEnterId.jsp?Operation=<%=operation%>';
	window.document.forms[0].submit();
}
</script>
</head>
<body Onload="<%=onLoad%>">
<form name="industryform" method="post" action="QMSIndustryRegistrationProcess.jsp"  onSubmit="return mandatory();">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="100%" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
				<table border="0" width="100%" >
					<tr valign="top" class="formlabel">
						<td> Industry Registration - <%=operation%> </td>
						<td align=right>QS1010511</td></tr></table>
			</td>
		  </tr>
		</table>
		<table border='0' width="100%" cellpadding="0" cellspacing="0">
			<tr class='formdata'>
				<td></td>
				<td align='center'><b>Industry :<font color="#FF0000">*</font></b></td>
				<td align='center'><b>Description :<font color="#FF0000">*</font></b></td>
<%
		if(operation!=null && operation.equals("Invalidate"))
		{
%>
				<td width='30%' align ='center'><b>Invalidate</b></td>
<%
		}
%>
				<td></td>
			</tr>
		</table>
<%		if(operation!=null && operation.equals("Add"))
		{
%>
		<table border='0' width="100%"   id="industryregistration"  idcounter="1" 
         defaultElement="industry" xmlTagName="industryreg" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" maxRows=20>
		 </table>
<%		}else if(operation!=null && (operation.equals("View") || operation.equals("Delete"))) 
		{
%>		
		<table border='0' width="800" cellpadding="0" cellspacing="1">
			<tr class='formdata'>
				<td></td>
				<td width='50%' align ='center'><%=industryRegDOB.getIndustry()!=null?industryRegDOB.getIndustry():""%>
				<input type	='hidden' name='industry' value ='<%=industryRegDOB.getIndustry()!=null?industryRegDOB.getIndustry():""%>'></td>
				<td width='50%' align ='center'><%=industryRegDOB.getDescription()!=null?industryRegDOB.getDescription():""%>
				<input type	='hidden' name='description' value ='<%=industryRegDOB.getDescription()!=null?industryRegDOB.getDescription():""%>'></td>
				<td></td>
			</tr>
		 </table>
<%
		}else if(operation!=null && operation.equals("Modify"))
		{
%>
		<table border='0' width="800" cellpadding="0" cellspacing="1">
			<tr class='formdata'>
				<td></td>
				<td width='50%' align ='center'>
				<input type	='text' name='industry' readOnly class ='text' value ='<%=industryRegDOB.getIndustry()!=null?industryRegDOB.getIndustry():""%>'></td>
				<td width='50%' align ='center'>
				<input type	='text' name='description' class ='text' value ='<%=industryRegDOB.getDescription()!=null?industryRegDOB.getDescription():""%>' onkeyPress='specialCharFilter(this,"Description")'  onBlur="" ></td>
				<td></td>
			</tr>
		 </table>
<%
		}else if(operation!=null && operation.equals("Invalidate"))
		{
			if(industryList!=null && industryList.size()>0)
			{
%>
		<table border='0' width="800" cellpadding="0" cellspacing="1">
<%
				for(int i=0;i<industryList.size();i++)
				{
					industryRegDOB	=	(IndustryRegDOB)industryList.get(i);
%>
			<tr class='formdata'>
				<td></td>
				<td width='30%' align ='center'><%=industryRegDOB.getIndustry()!=null?industryRegDOB.getIndustry():""%>
				<input type	='hidden' name='industry' value ='<%=industryRegDOB.getIndustry()!=null?industryRegDOB.getIndustry():""%>'></td>
				<td width='40%' align ='center'><%=industryRegDOB.getDescription()!=null?industryRegDOB.getDescription():""%>
				<input type	='hidden' name='description' value ='<%=industryRegDOB.getDescription()!=null?industryRegDOB.getDescription():""%>'></td>
				<td width='30%' align ='center' border=0><input type	='checkbox' name =	'invalidate<%=i%>'  value = '<%=industryRegDOB.getIndustry()%>'  <%=((industryRegDOB.getInvalidate()!=null && industryRegDOB.getInvalidate().equals("T"))?"checked":"")%> ></td>
				<td></td> 
			</tr>
<%
				}
%>
		</table>
<%
			}
		}
%>
		 <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes Mandatory
            </td>
            <td valign="top" align="right">
<%
		if(operation!=null && (operation.equals("Add") || operation.equals("Modify") || operation.equals("Invalidate")))
		{
%>
                <input type="submit" value="Submit" name="b1" class="input">
				<input type="reset" value="Reset" name="reset" class="input">
<%		}else if(operation!=null && operation.equals("Delete"))
		{
%>
				<input type="submit" value="Delete" name="b1" class="input">
<%		}else if(operation!=null && operation.equals("View"))
		{
%>
				<input type="button" value="Continue" name="b1" class="input" onClick='submitContinue()'>
<%		}
%>
				<input type='hidden' name='Operation' value='<%=operation%>'>
		     </td>
          </tr>
        </table>
	  </td>	
    </tr>
</table>
</form>
</body>
</html>
<%
	}catch(ObjectNotFoundException e)
	{
		success=false;
		msg	=	"Record doesnt Exist with Industry Id :";
		//Logger.error(FILE_NAME,msg+e);
    logger.error(FILE_NAME+msg+e);
	}catch(Exception e)
	{
		success	=	false;
		msg	=	"Exception while reading the data";
		//Logger.error(FILE_NAME,"Exception while retrieving the data"+e);
    logger.error(FILE_NAME+"Exception while retrieving the data"+e);
	}
	if(!success)
	{
				if(operation!=null && operation.equals("Modify"))
				{
					errorMessage			=  msg+industry;
					url						=	"QMSIndustryRegistrationEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && (operation.equals("Delete")))
				{
					errorMessage			=	msg+industry;
					url						=	"QMSIndustryRegistrationEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation!=null && (operation.equals("View")))
				{
					errorMessage			=	msg+industry;
					url						=	"QMSIndustryRegistrationEnterId.jsp?Operation=View";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","View")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation!=null && (operation.equals("Invalidate")))
				{
					errorMessage			=	msg;
					url						=	"../ESMenuRightPanel.jsp?link=es-et-Administration-ModuleI";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Invalidate")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}

	%>
					<jsp:forward page="../QMSESupplyErrorPage.jsp" />
	<%
	}
%>