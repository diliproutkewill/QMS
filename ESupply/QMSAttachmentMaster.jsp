<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSAttachmentMaster.jsp
Product Name		: QMS
Module Name			: SetUp
Task				: Add
Date started		: 	
Date modified		:  
Author    			: Kameswari
Related Document	: Issue-61289

--%>
<%@ page import="org.apache.log4j.Logger,
                 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 com.qms.setup.java.QMSAttachmentDOB,
				 com.qms.setup.java.QMSAttachmentDetailDOB,
				 com.qms.setup.java.QMSAttachmentFileDOB,
				  java.util.ArrayList"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%! private static Logger logger = null;
    public final static String   FILE_NAME = "QMSAttachmentMaster.jsp";%>

<%
    logger  = Logger.getLogger(FILE_NAME);  
    String			    operation        =  "";
	String			    accessType	     =  "";
	String			    terminalId	     =  "";
	String			    attachmentId	 =  "";
    QMSAttachmentDOB    attachmentDOB    =  null;
  
    try 			
   {
		operation		=	request.getParameter("Operation");
	    attachmentDOB	= (QMSAttachmentDOB)session.getAttribute("attachmentDOB");
		accessType		=  attachmentDOB.getAccessType();
		if("H".equalsIgnoreCase(accessType))
			accessType	=	"HO" ;
		else	if("A".equalsIgnoreCase(accessType))
			accessType	=	"ADMIN";
		else	if("O".equalsIgnoreCase(accessType))
			accessType	=	"OPER";
		terminalId		=	attachmentDOB.getTerminalId();
		attachmentId	=	attachmentDOB.getAttachmentId();
 
 %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Quote Attachment Master - <%=operation%></title> 
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
<script>
 var i = 0;
function initialize()
{
  	importXML('<%=request.getContextPath()%>/html/DynamicContent.xml');
}
function initializeDynamicContentRows()
{
	var tableObj = document.getElementById("QMSAttachment");
	createDynamicContentRow(tableObj.getAttribute("id"));
	i++;

}
function mandatory()
{
   var k=0;
	while(document.forms[0].fileName[k]!=null)
	{
		if (document.forms[0].fileName[k].value == '')
		{
			alert('Please enter the File name to be uploaded');
			document.forms[0].fileName[k].focus();
			return false;
		}
		var fName=document.forms[0].fileName[k].value;		
		var c=".pdf";   
		
	   if(fName.length!=0 && fName.indexOf(c)==-1)
		{
			alert("This is Not a Valid File Type");
			document.forms[0].fileName[k].focus();
			return false;
		}
		k++;
	}
	return true;
}
function showShipmentMode()
{
  if(document.forms[0].shipmentMode.value=="2")
	{	  
    document.getElementById("consoleType").innerHTML="Console Type:<br><select size='1' class='select' name='consoleType'><option value='LCL'>LCL</option><option value='FCL'>FCL</option></select>";
    }
  else if(document.forms[0].shipmentMode.value=="4")
	{	  
    document.getElementById("consoleType").innerHTML="Console Type:<br><select size='1' class='select' name='consoleType'><option value='LTL'>LTL</option><option value='FTL'>FTL</option></select>";
    }
	else
	{
	 document.getElementById("consoleType").innerHTML="";
	}
}
function showCountryLOV(toSet)
{
	var locationId;
	
	if(toSet=='fromCountry')
	{
		locationId = document.forms[0].fromLocation.value;
	}
	else if(toSet=='toCountry')
	{
		locationId = document.forms[0].toLocation.value;
    }
		
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = (toSet=='fromCountry')?(document.forms[0].fromCountry.value):(document.forms[0].toCountry.value);

	var Url="etrans/ETCLOVCountryIds1.jsp?searchString="+searchString+"&whereClause=CSR&wheretoset="+toSet+"&locationId="+locationId+"&shipmentMode="+document.forms[0].shipmentMode.value;
	
  	var Win=open(Url,'Doc',Features);
}
function showLocationLOV(toSet)
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	
	//var searchString = (toSet=='origin')?(document.forms[0].origin.value):(document.forms[0].destination.value);
	var searchString1= (toSet=='fromLocation')?(document.forms[0].fromCountry.value):(document.forms[0].toCountry.value);
	var searchString2=(toSet=='fromLocation')?(document.forms[0].fromLocation.value):(document.forms[0].toLocation.value);
	var searchString=searchString2.toString();
  	var searchString2=searchString1.toString();
	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+toSet+'&searchString='+searchString+'&searchString2='+searchString2+'&shipmentMode='+document.forms[0].shipmentMode.value;
	var Win=open(Url,'Doc',Features);
}

function showLocationValues(obj,where)
{
	
	//document.getElementById(where).value="";
	var data="";
	//document.getElementById(where).options.length=0;
	for( i=0;i<obj.length;i++)
	{
		/*if(where=='QuoteId')
    		   temp=obj[i].value;
		else
        { */
			firstTemp	=obj[i].value;
		firstIndex	=	firstTemp.indexOf(0);
		if(where=="serviceLevelId")
			lastIndex	=	firstTemp.indexOf(' [');	
		else
			lastIndex	=	firstTemp.indexOf('[');
		firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		temp		=   firstTemp.toString();
		secondTemp	=obj[i].value;
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
			if(where=="fromCountry" || where=="toCountry")
			{
				if(data!="")
				data=data+","+temp1;
				else
				data=temp1;
			}
			else
			{
				if(data!="")
					data=data+","+temp;
				else	
					data=temp;
			}   
			
	}
	     document.getElementById(where).value=data;
}

function showCarrierIds()
{
	var URL 		= 'etrans/ETransLOVCarrierIds1.jsp?searchString='+document.forms[0].carriers.value.toUpperCase()+'&shipmentMode='+document.forms[0].shipmentMode.value+'&multiple=multiple';	
	var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
	var Options 	= 'scrollbars = yes,width = 700,height = 360,resizable = yes';
	var Features 	= Bars +' '+ Options;
	var Win 		= open(URL,'Doc',Features);
}
function setCarrierIdValues(obj)
{
	var data="";
	for( i=0;i<obj.length;i++)
		{
		
		
		if(data!="")
			data=data+","+obj[i].value;
		else
			data=obj[i].value;
		}

		document.getElementById("carriers").value=data;
	
}
function setIndustryIdValues(obj)
{
	var data="";
	for( i=0;i<obj.length;i++)
		{
		
		
		if(data!="")
			data=data+","+obj[i].value;
		else
			data=obj[i].value;
		}

		document.getElementById("industryId").value=data;
	
}
function showServiceLevelLOV()
{   
				
	var searchString=document.forms[0].serviceLevelId.value.toUpperCase();
	var Url		=	"etrans/ETCLOVServiceLevelIds1.jsp?searchString="+searchString+"&shipmentMode="+document.forms[0].shipmentMode.options.value;	
	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
function openIndustryIdLOV()
{
	var searchStr	=	document.forms[0].industryId.value;
	var Url			= "etrans/QMSIndustryIdMultipleLOV.jsp?Operation=<%=operation%>&searchString="+searchStr+"&multiple=multiple";
	var Bars        = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options     ='scrollbar=yes,width=700,height=300,resizable=no';
	var Features    = Bars+' '+Options;
	var Win         = open(Url,'Doc',Features);
}

function previousPage()
{
	document.forms[0].action  =	'QMSAttachmentEnterId.jsp?Operation=<%=operation%>';
	document.forms[0].submit();

}
 </script>
</head>
<body onLoad='initialize();'>
<form   action='QMSAttachmentController?Operation=<%=operation%>&subOperation=Submit' onSubmit ='return mandatory()' method='post'  ENCTYPE="multipart/form-data">
<table border=0 cellpadding=4 cellspacing=0 width="100%">
	<tr class='formlabel' valign=top> 
		<td colspan='4'>Quote Attachment Master- <%=operation%> </td>
		<td align=right><%=loginbean.generateUniqueId("QMSAttachmentMaster.jsp",operation)%></td>
	</tr>
</table>
<table width="100%" border='0' cellspacing='1' cellpadding='4' bgcolor="#FFFFFF">
	<tr  class="formdata">
		<td valign="middle">Access Type:<br><b><%=accessType%>&nbsp;TERMINAL</b></td>
		<td valign="middle">Created At terminal:<br><b><%=terminalId%></b></td>
		<td valign="middle" colspan='2'>Attachment ID:<br><b><%=attachmentId%></b></td>
	</tr>
	<tr class="formheader">
		<td valign="middle" colspan='4'>Attachment Criteria</td>
	</tr>
	<tr class="formdata">
		<td valign="middle">Mode:<br>
			<select size="1" name="shipmentMode" class="select" onChange="showShipmentMode()">
				<option value=""></option>
				<option value="1">Air</option>
				<option value="2">Sea</option>
				<option value="4">Truck</option>
			</select>
		</td>
		<td>
			 <span name ='consoleType' id='consoleType' style='position:relative;'></span> 
		</td>
		<td valign="middle">Quote Type:<br>
			<select size="1" name="quoteType" class="select" value="">
				<option value=""></option>
				<option value="N">New</option>
				<option value="U">Updated</option>
			</select>
		</td>
		<td valign="middle">Default:<br>
			<select size="1" name="defaultFlag" class="select">
				<option value="Y">Yes</option>
				<option value="N">No</option>
			</select>
		</td>
	</tr>
	<tr class="formdata" valign="middle">
		<td>From Country:<br>
			<input type="text" class="text" name="fromCountry" id="fromCountry" value="" onblur='this.value=this.value.toUpperCase()' >
			<input class="input" name="button4" onclick='showCountryLOV("fromCountry")' type="button" value="...">
		</td>
		<td>From Location:<br>
			<input type="text" class="text" name="fromLocation" id="fromLocation" value="" onblur='this.value=this.value.toUpperCase()' >
			<input class="input" name="button5" onclick='showLocationLOV("fromLocation")' type="button" value="...">
		</td>
		<td>To Country:<br>
			<input type="text" class="text" name="toCountry" id="toCountry" value="" onblur='this.value=this.value.toUpperCase()' >
			<input class="input" name="button6" onclick='showCountryLOV("toCountry")' type="button" value="...">
		</td>
		<td>To Location:<br>
			<input type="text" class="text" name="toLocation" id="toLocation" value="" onblur='this.value=this.value.toUpperCase()' >
			<input class="input" name="button7" onclick='showLocationLOV("toLocation")' type="button" value="...">
		</td>
	 </tr>
	<tr class="formdata" valign="middle">
		<td>Carrier Id:<br>
			<input type="text"  name="carriers"  class='text' size="10" onblur='this.value=this.value.toUpperCase()' value="" onchange='disableSubmit();'>&nbsp;<input type="button" value="..." name="gatebut"  class='input' onclick='showCarrierIds()'>
		</td>
		<td >Service Level ID:<br>
			<input type="text" class="text" name="serviceLevelId" size="13" onblur='this.value=this.value.toUpperCase()' value=""> 
			<input class="input" name="button8" onclick='showServiceLevelLOV()' type="button" value="...">
		</td>
		<td>Industry:<br>
			<input type	='text' name ="industryId" id="industryId" size='15' maxLength='25' class ='text' value="" onkeyPress='specialCharFilter(this,"industryId")'  onBlur="toUpperCase()">
			<input type	='button' name='b1' class ='input' value ="..." onClick ="openIndustryIdLOV()"">
		</td>
		<td></td>
	</tr>
	<tr class="formheader" valign="middle">
		<td colspan="4">ATTACHMENTS</td>
	</tr>
</table>
<table width="100%" cellpadding="4" cellspacing="2" nowrap id="QMSAttachment" idcounter="1"   defaultElement="fileName"  xmlTagName="QMSAttachment"  bgcolor='#FFFFFF'>
	<tr class='formdata'>
		<td width="25%">&nbsp;</td>
		<td width="50%" align="center"> <b>Select PDF file to Upload</b><font color=#ff0000>*</font></td>
		<td width="25%">&nbsp;</td>
	</tr>
</table>
<table width="100%" cellspacing='0' cellpadding='0' border='0' bgcolor='#FFFFFF'>
	<tr>
		<td  valign="top" class='denotes'><font color=#ff0000>*</font>Denotes Mandatory</td>
	</tr>	
	<tr>
	<td align="right">
	<input type="button" class="input" name="<<Back" value="<<Back" onClick=previousPage()>
	<input name=B1 type=submit value="Submit" class='input'>
	</td>
	</tr>
</table>
</form>
</body>
</html>
<%
          
}
	catch(Exception e)
	{
		e.printStackTrace();
	    logger.error(FILE_NAME+"Error in QMSAttachmentViewAll.jsp"+e.toString());
		ArrayList       keyValueList   =  new ArrayList();
		ErrorMessage    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSAttachmentEnterId.jsp");
		
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
    <jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%
	}
%>
