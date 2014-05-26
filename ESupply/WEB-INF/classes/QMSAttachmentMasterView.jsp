<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSAttachmentMasterView.jsp
Product Name		: QMS
Module Name			: SetUp
Task				: View/Modify/Delete
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
    public final static String   FILE_NAME = "QMSAttachmentMasterView.jsp";%>

<%  
                               logger                = Logger.getLogger(FILE_NAME);  
  String			               operation               =  "";
	String			               accessType	             =  "";
	String			               accesstype	             =  "";
	String			               terminalId	             =  "";
	String			               attachmentId	           =  "";
	String			               attachment           =  "";
  String			               shipmentMode            =  "";
	String			               consoleType	           =  "";
	String			               quoteType	             =  "";
	String			               defaultFlag	           =  "";
	String			               fromCountry	           =  "";
  String			               fromLocation            =  "";
	String			               toCountry	             =  "";
	String			               toLocation	             =  "";
	String			               carrierId	             =  "";
	String			               serviceLevelId	         =  "";
	String			               industryId	             =  "";
	ArrayList		               quoteAttachmentIdList   = null;
  ArrayList			             fileName	               =  new ArrayList();
  ArrayList			             fieldsList	             =  new ArrayList();
	ArrayList			             filesList	             =  new ArrayList();
  QMSAttachmentDOB           dob                     =  null;
  QMSAttachmentDetailDOB     detailDOB               =  null;
  QMSAttachmentFileDOB       fileDOB                 =  null;
	String                     mode                    =  null; 
	String                     defaultFlagValue        =  null;
  int count = 0;
    try 			
   {
		
		
		operation		        = request.getParameter("Operation");
		quoteAttachmentIdList   =(ArrayList)request.getAttribute("quoteAttachmentIdList");
		dob			    = (QMSAttachmentDOB)request.getAttribute("QMSAttachmentDOB");
		accessType		= dob.getAccessType();	
	if("H".equalsIgnoreCase(accessType))
			accessType	=	"HO" ;
		else	if("A".equalsIgnoreCase(accessType))
			accessType	=	"ADMIN";
		else	if("O".equalsIgnoreCase(accessType))
			accessType	=	"OPER";
		terminalId		=	dob.getTerminalId();
		attachmentId	=	dob.getAttachmentId();
		 attachment =attachmentId.replace('&',',');
      for(int j=0;j<quoteAttachmentIdList.size();j++)
	  {
	    if(attachmentId.equalsIgnoreCase((String)quoteAttachmentIdList.get(j))) 
		{
			count++;
		}
	  }
	   fieldsList       = (ArrayList)dob.getQmsAttachmentDetailDOBList();
	   for(int i = 0;i<fieldsList.size();i++)
       {
	      detailDOB	   = (QMSAttachmentDetailDOB)fieldsList.get(i);
		  if(i==0)
		  {
			shipmentMode	=	shipmentMode+((detailDOB.getShipmentMode()!=null)?detailDOB.getShipmentMode():"");
		    consoleType		=	consoleType+((detailDOB.getConsoleType()!=null)?detailDOB.getConsoleType():"");
		    quoteType		=	quoteType+((detailDOB.getQuoteType()!=null)?detailDOB.getQuoteType():"");
			defaultFlag		=	defaultFlag+detailDOB.getDefaultFlag();
			fromCountry		=	fromCountry+((detailDOB.getFromCountry()!=null)?detailDOB.getFromCountry():"");
			fromLocation	=	fromLocation+((detailDOB.getFromLocation()!=null)?detailDOB.getFromLocation():"");
			toCountry		=	toCountry+((detailDOB.getToCountry()!=null)?detailDOB.getToCountry():"");
			toLocation		=	toLocation+((detailDOB.getToLocation()!=null)?detailDOB.getToLocation():"");
			carrierId		=	carrierId+((detailDOB.getCarrierId()!=null)?detailDOB.getCarrierId():"");
			serviceLevelId	=	serviceLevelId+((detailDOB.getServiceLevelId()!=null)?detailDOB.getServiceLevelId():"");
			industryId		=	industryId+((detailDOB.getIndustry()!=null)?detailDOB.getIndustry():"");
		  }
          else
		  {
			fromCountry		=	fromCountry+((detailDOB.getFromCountry()!=null)?','+detailDOB.getFromCountry():"");
			fromLocation	=	fromLocation+((detailDOB.getFromLocation()!=null)?','+detailDOB.getFromLocation():"");
			toCountry		=	toCountry+((detailDOB.getToCountry()!=null)?','+detailDOB.getToCountry():"");
			toLocation		=	toLocation+((detailDOB.getToLocation()!=null)?','+detailDOB.getToLocation():"");
			carrierId		=	carrierId+((detailDOB.getCarrierId()!=null)?','+detailDOB.getCarrierId():"");
			serviceLevelId	=	serviceLevelId+((detailDOB.getServiceLevelId()!=null)?','+detailDOB.getServiceLevelId():"");
			industryId		=	industryId+((detailDOB.getIndustry()!=null)?','+detailDOB.getIndustry():"");
		  }    
	   }
	  
	   filesList	=  (ArrayList)dob.getQmsAttachmentFileDOBList();
	   if("".equalsIgnoreCase(shipmentMode))
		   mode  = "";
	   else if("1".equalsIgnoreCase(shipmentMode))
		    mode  = "Air";
	  else if("2".equalsIgnoreCase(shipmentMode))
		    mode  = "Sea";
      else if("4".equalsIgnoreCase(shipmentMode))
		    mode  = "Truck";

          if("Y".equalsIgnoreCase(defaultFlag))
			   defaultFlagValue = "Yes";
		  else
			  defaultFlagValue = "No";
	   

 %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<title>Quote Attachment Master - <%=operation%></title> 
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script>
	
function showShipmentMode()
{
  if(document.forms[0].shipmentMode.value=="2")
	{	  
    document.getElementById("console").innerHTML="Console Type:<br><select size='1' class='select' name='console'><option value='LCL'>LCL</option><option value='FCL'>FCL</option></select>";
    }
  else if(document.forms[0].shipmentMode.value=="4")
	{	  
    document.getElementById("console").innerHTML="Console Type:<br><select size='1' class='select' name='console'><option value='LTL'>LTL</option><option value='FTL'>FTL</option></select>";
    }
	else
	{
	 document.getElementById("console").innerHTML="";
	}
}
function showCountryLOV(toSet)
{
	var locationId ="";
	var fromCountry='<%=fromCountry%>';
	var toCountry='<%=toCountry%>';
	if(toSet=='fromCountry'&&fromCountry=="")
	{
		locationId = document.forms[0].fromLocation.value;
	}
	else if(toSet=='toCountry'&&toCountry=="")
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
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
function confirmDelete()
{
	var r	=	confirm("Are you sure to delete it");
	if(r==true)
	{
		document.forms[0].action  =	'QMSAttachmentController?Operation=Delete&subOperation=Submit';
		document.forms[0].submit();
	}
	else
	{	
		document.forms[0].action  =	'QMSAttachmentEnterId.jsp?Operation=Delete&subOperation=Submit';
		document.forms[0].submit();
	}
}
function previousPage()
{
	document.forms[0].action  =	'QMSAttachmentEnterId.jsp?Operation=<%=operation%>';
	document.forms[0].submit();

}
function manageAttachments()
{
	
	var attachmentId ="<%=attachment%>";

	var Url	= 'QMSAttachmentController?Operation=manageAttachment&subOperation=viewFile&attachmentId='+attachmentId;
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}

 </script>
</head>
<body>
<form   action='QMSAttachmentController?Operation=<%=operation%>&subOperation=Submit' method='POST' ENCTYPE="multipart/form-data" >
<table border=0 cellpadding=4 cellspacing=0 width="100%">
	<tr class='formlabel' valign=top> 
		<td  colspan='4'>Quote Attachment Master- <%=operation%> </td>
		<td align=right><%=loginbean.generateUniqueId("QMSAttachmentMasterView.jsp",operation)%></td>
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

	<%if("Modify".equalsIgnoreCase(operation)&&("No".equalsIgnoreCase(defaultFlag))&&(count==0))
{ %>	 
	
	<tr class="formdata">
		<td valign="middle">Mode:<br>
			<select size="1" name="shipmentMode" class="select" onChange="showShipmentMode()">
				<option value=""<%="".equalsIgnoreCase(shipmentMode)?"selected":""%>></option>
				<option value="1"<%="1".equalsIgnoreCase(shipmentMode)?"selected":""%>>Air</option>
				<option value="2"<%="2".equalsIgnoreCase(shipmentMode)?"selected":""%>>Sea</option>
				<option value="4"<%="4".equalsIgnoreCase(shipmentMode)?"selected":""%>>Truck</option>
			</select>
		</td>
		<td>
			 <span name ='console' id='console' style='position:relative;'></span> 
		</td>
		<td valign="middle">Quote Type:<br>
			<select size="1" name="quoteType" class="select">
				<option value=""<%="".equalsIgnoreCase(quoteType)?"selected":""%>></option>
				<option value="N"<%="N".equalsIgnoreCase(quoteType)?"selected":""%>>New</option>
				<option value="U"<%="U".equalsIgnoreCase(quoteType)?"selected":""%>>Updated</option>
			</select>
		</td>
		<td valign="middle">Default:<br>
			<select size="1" name="defaultFlag" class="select">
				<option value="Y"<%="Yes".equalsIgnoreCase(defaultFlagValue)?"selected":""%>>Yes</option>
				<option value="N"<%="No".equalsIgnoreCase(defaultFlagValue)?"selected":""%>>No</option>
			</select>
		</td>
	</tr>
	<tr class="formdata" valign="middle">
		<td>From Country:<br>
			<input type="text" class="text" name="fromCountry" id="fromCountry" value="<%=(fromCountry!=null)?fromCountry:""%>" onblur='this.value=this.value.toUpperCase()'>
			<input class="input" name="button4" onclick='showCountryLOV("fromCountry")' type="button" value="...">
		</td>
		<td>From Location:<br>
			<input type="text" class="text" name="fromLocation" id="fromLocation" value="<%=(fromLocation!=null)?fromLocation:""%>" onblur='this.value=this.value.toUpperCase()'>
			<input class="input" name="button5" onclick='showLocationLOV("fromLocation")' type="button" value="...">
		</td>
		<td>To Country:<br>
			<input type="text" class="text" name="toCountry" id="toCountry" value="<%=(toCountry!=null)?toCountry:""%>" onblur='this.value=this.value.toUpperCase()'>
			<input class="input" name="button6" onclick='showCountryLOV("toCountry")' type="button" value="...">
		</td>
		<td>To Location:<br>
			<input type="text" class="text" name="toLocation" id="toLocation" value="<%=(toLocation!=null)?toLocation:""%>" onblur='this.value=this.value.toUpperCase()'>
			<input class="input" name="button7" onclick='showLocationLOV("toLocation")' type="button" value="...">
		</td>
	 </tr>
	<tr class="formdata" valign="middle">
		<td>Carrier Id:<br>
			<input type="text"  name="carriers"  class='text' size="10" onblur='this.value=this.value.toUpperCase()' value="<%=(carrierId!=null)?carrierId:""%>" onchange='disableSubmit();'>&nbsp;<input type="button" value="..." name="gatebut"  class='input' onclick='showCarrierIds()'>
		</td>
		<td >Service Level ID:<br>
			<input type="text" class="text" name="serviceLevelId" size="13" onblur='this.value=this.value.toUpperCase()' value="<%=(serviceLevelId!=null)?serviceLevelId:""%>"> 
			<input class="input" name="button8" onclick='showServiceLevelLOV()' type="button" value="...">
		</td>
		<td>Industry:<br>
			<input type	='text' name ="industryId" id="industryId" size='15' maxLength='25' class ='text' value="<%=(industryId!=null)?industryId:""%>" onkeyPress='specialCharFilter(this,"industryId")'  onBlur="toUpperCase()">
			<input type	='button' name='b1' class ='input' value ="..." onClick ="openIndustryIdLOV()"">
		</td>
		<td></td>
	</tr>
	
<%}
else
	   {%>
 
	<tr class="formdata">
		<td valign="middle">Mode:<br>
			<select size="1" name="shipmentMode" class="select" onChange="showShipmentMode()">
				<option value="<%=shipmentMode%>"><%=mode%></option>
			</select>
		</td>
		<td>
			 <span name ='console' id='console' style='position:relative;'></span> 
		</td>
		<td valign="middle">Quote Type:<br>
			<select size="1" name="quoteType" class="select" >
					<option value="<%=quoteType%>"><%="N".equalsIgnoreCase(quoteType)?"New":"Updated"%></option>
			</select>
		</td>
		<td valign="middle">Default:<br>
			<select size="1" name="defaultFlag" class="select">
				<option value="<%=(defaultFlag)%>"><%=defaultFlagValue%></option>
						</select>
		</td>
	</tr>

	<tr class="formdata" valign="middle">
		<td>From Country:<br>
			<input type="text" class="text" name="fromCountry" id="fromCountry" value="<%=fromCountry%>" readOnly>
			<input class="input" name="button4" type="button" value="..." disabled="true">
		</td>
		<td>From Location:<br>
			<input type="text" class="text" name="fromLocation" id="fromLocation" value="<%=fromLocation%>" readOnly>
			<input class="input" name="button5"  type="button" value="..." disabled="true">
		</td>
		<td>To Country:<br>
			<input type="text" class="text" name="toCountry" id="toCountry" value="<%=toCountry%>" readOnly>
			<input class="input" name="button6" type="button" value="..." disabled="true">
		</td>
		<td>To Location:<br>
			<input type="text" class="text" name="toLocation" id="toLocation" value="<%=toLocation%>" readOnly>
			<input class="input" name="button7" type="button" value="..." disabled="true">
		</td>
	 </tr>
	<tr class="formdata" valign="middle">
		<td>Carrier Id:<br>
			<input type="text"  name="carriers"  class='text' size="10"  value="<%=carrierId%>" readOnly>
			<input type="button" value="..." name="gatebut"  class='input'  disabled="true">
		</td>
		<td >Service Level ID:<br>
			<input type="text" class="text" name="serviceLevelId" size="13" value="<%=serviceLevelId%>"  readOnly> 
			<input class="input" name="button8"  type="button" value="..." disabled="true">
		</td>
		<td>Industry:<br>
			<input type	='text' name ="industryId" id="industryId" size='15' maxLength='25' class ='text' value ="<%=industryId%>" readOnly>
			<input type	='button' name='b1' class ='input' value ="..." disabled="true">
		</td>
		<td></td>
	</tr>
<%}
			if("Modify".equalsIgnoreCase(operation)) 
			{%>	
       <tr class="formdata" align="center">
		<td colspan="4"><input type="button" class="input" name="manageattachments" value="Manage Attachments" onClick="manageAttachments()"></td>
	</tr>
	<%}
	   for(int j=0;j<filesList.size();j++)
	   {
	     fileDOB	= (QMSAttachmentFileDOB)filesList.get(j);
	%>
	   <tr class="formdata" align="center">
	   <td colspan='4'><a href="QMSAttachmentController?Operation=viewFile&uniqueId=<%=fileDOB.getId()%>" target="_blank"><%=(String)fileDOB.getFileName()%></a></td>
	   </tr>
	   <%}%>
</table>

<table width="100%" cellspacing='0' cellpadding='0' border='0' bgcolor='#FFFFFF'>
	<!--<tr>
		<td  valign="top" class='denotes'><font color=#ff0000>*</font>Denotes Mandatory</td>
	</tr>	-->
	<tr>
		<td align="right">
			 <%if("View".equalsIgnoreCase(operation))
			  {
			%>
			   <input name=B1 type="button" value=Continue class='input' onClick=previousPage()>
			<%
			  }
			  else	if("Delete".equalsIgnoreCase(operation))
			  {
			%>
				<input type="button" class="input" name="<<Back" value="<<Back" onClick=previousPage()>
				<input name="Delete" type=button value="Delete" class='input' onClick=confirmDelete()>
				
			<%
			}
			  else  if("Modify".equalsIgnoreCase(operation))
			 {
			%>
				<input type="button" class="input" name="<<Back" value="<<Back" onClick=previousPage()>
				<input name=B1 type=submit value="Submit" class='input'>
				
			<%
			 }
			%>
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
	    logger.error(FILE_NAME+"Error in QMSAttachmentMasterView.jsp"+e.toString());
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
