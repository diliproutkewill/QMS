
<%
/*	Programme Name : QMSCanadaZonesDownloadIndex.jsp
*	Module Name    : Terminal Setup
*	Task Name      : 
*	Sub Task Name  : Download.
*	Author		   : Yuvraj Waghray
*	Date Started   : 23-Oct-2006
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>


<%@ page import = "java.util.ArrayList,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
				    com.foursoft.esupply.common.java.KeyValue"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME="QMSCanadaZonesDownloadIndex.jsp";%>

<%
	logger  = Logger.getLogger(FILE_NAME);
	ArrayList          keyValueList           =    null;
	ErrorMessage       errorMessageObject     =    null;
	String             operation              =    null;

  try 			
  {
		operation    =   request.getParameter("Operation");	
%>

<html>
<head>
<title>Density Group Code</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<script language="JavaScript">

function placeFocus()
{
	document.forms[0].locationId.focus();
}

function showLov(lovName)
{
	var URL  = "";
	var searchString	= document.forms[0].locationId.value;
	

	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+document.forms[0].locationId.name+'&searchString='+searchString+'&shipmentMode=All&from=Canada';
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}

function showLocationValues(obj,where)
{
	var data="";
	for( i=0;i<obj.length;i++)
	{
		firstTemp	=obj[i].value;
		firstIndex	=	firstTemp.indexOf(0);
		lastIndex	=	firstTemp.indexOf('[');
		firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		temp		=   firstTemp.toString();
		secondTemp	=	obj[i].value;
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
		if(data!="")
			data=data+","+temp;
		else	
			data=temp;
		
	}
		
	document.getElementById(where).value=data;
		
	
}
function selectLocationId(listIds)
{
	for(var index=0;index<listIds.length;index++)
	{
		window.document.forms[0].locationId.options[index]	=	new Option(listIds[index],listIds[index]);
	}	
}
function mandatory()
{   
	if(document.forms[0].locationId.value.length<=0)
	{
		alert("Please enter Origin Location ");
		document.forms[0].locationId.focus();
		return false;
	}
	
	
	return true;
}
function toUpperCase(object)
{
	object.value=object.value.toUpperCase();
}
function changeConsoleVisibility()
{
	var console	 = document.getElementById('console');
	
	if(document.forms[0].shipmentMode.value=='2')
		console.style.display="block";
	else
		console.style.display="none";

}
</script>
<body onLoad='placeFocus()'>
 <form method="post" onSubmit="return mandatory()" action="QMSCanadaZonesController">

	<table width="100%" cellpadding="4" cellspacing="0" bgcolor='#FFFFFF'>
		<tr class="formlabel">
			<td >Canada Zone Code Master - Download</td><td align='right'>QS1040521</td>
		</tr>
	</table>
		  <table border="0" cellpadding="4" cellspacing="0" width="100%"  bgcolor='#FFFFFF'>
		  <tr class="formdata">
		  <td>Shipment Mode:
				<select name='shipmentMode' class='select' onchange='changeConsoleVisibility()'>
						<option value='1'>Air</option>
						<option value='2'>Sea</option>
				</select>
			</td>
			<td>
					 <DIV id="console" style="DISPLAY:none">
						Console Type:
						<select name="consoleType" class='select'>
							<option value="LCL">LCL</option>
							<option value="FCL">FCL</option>
						</select>
					</DIV>
			</td>
			<td>Location(s):<sup><font color="red">*</font></sup>
				<input type='text' name='locationId' size='15' onBlur='toUpperCase(this)' class='text'>
				<input class="input" name="locationLOV" onclick="showLov(locationLOV)"  type="button" value="..." >
			</td>
		  </tr>		  
		  
		</tbody>
	</table>	
	<table border="0" cellpadding="4" cellspacing="0" width="100%" bgcolor='#FFFFFF'>
       <tbody>
         <tr >
            <td colspan="2" class='denotes'><font color="#FF0000">*</font> Denotes Mandatory</td>
			  <td align="right" colspan="2">
			  <input class="input" name="Submit" type="submit" value="Submit">
              <input class="input" name="Reset" type="reset" value="Reset"> 
			  <input  name="Operation" type="hidden" value='<%=operation%>'></td>
			  <input  name="subOperation" type="hidden" value='process'></td>
         </tr>
       </tbody>
     </table>
  </form>
 </body>
</html>

<%
  }
  catch(Exception e)
  {
	  //Logger.error(FILE_NAME,"Error in QMSDensityGroupCodeEnterId.jsp file"+e.toString());
    logger.error(FILE_NAME+"Error in QMSDensityGroupCodeEnterId.jsp file"+e.toString());
	  errorMessageObject   =   new ErrorMessage("Unable to "+operation+"the record","QMSDensityGroupCodeAdd.jsp");
	  keyValueList         =   new ArrayList();
	  keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
	  keyValueList.add(new KeyValue("Operation",request.getParameter("Operation"))); 
	  errorMessageObject.setKeyValueList(keyValueList);
	  request.setAttribute("ErrorMessage",errorMessageObject); 
%>
			<jsp:forward page="QMSESupplyErrorPage.jsp"/>
<%

  }
%>