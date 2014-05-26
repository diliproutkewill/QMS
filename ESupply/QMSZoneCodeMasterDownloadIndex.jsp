
<%
/*	Programme Name : QMSZoneCodeMasterEnterId.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : DensityGroupCode
*	Sub Task Name  : Modify/View/Delete.
*	Author		   : Rama Krishna.Y
*	Date Started   : 16-06-2005
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
  public static final String FILE_NAME="QMSZoneCodeMasterEnterId.jsp";%>

<%
  logger  = Logger.getLogger(FILE_NAME);
	ArrayList          keyValueList           =    null;
	ErrorMessage       errorMessageObject     =    null;
	String             operation              =    null;

  try 			
  {

	 if(loginbean.getTerminalId() == null) //this is to verify whether we can get "terminalid" through the login bean object.
	 {											 		
%>
		<jsp:forward page="../ESupplyLogin.jsp" />
<%
	 }
	 else
	 { 
		             operation    =   request.getParameter("Operation");	
					// System.out.println(operation);
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
	
	/*if(lovName.name=="locationLOV")
	{
	   if(document.forms[0].controlStationId.value.length<=0)
		{
			alert(" enter ControlStation Id");
			return false;
		}
	
		URL="QMSLOVLocationIds.jsp?Operation="+document.forms[0].Operation.value+"&controlStationId="+document.forms[0].controlStationId.value;
	}*/
	/*if(lovName.name=="controlStationLOV")
	{
		controlStationFlag  =  true;
		
		URL="etrans/ETCLOVLocationIds.jsp?searchString="+document.forms[0].controlStationId.value+"&wheretoset="+document.forms[0].controlStationId.name;
	}*/	
	
	var searchString	= document.forms[0].locationId.value;
	

	var Url='etrans/ETCLOVLocationIds1.jsp?wheretoset='+document.forms[0].locationId.name+'&searchString='+searchString+'&shipmentMode=All';
	//var Win=open(Url,'Doc',Features);
	
	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	//var Features=Bars+' '+Options;
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
		if(where=="originCountry"||where=="destinationCountry")
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
function selectLocationId(listIds)
{
	for(var index=0;index<listIds.length;index++)
	{
		window.document.forms[0].locationId.options[index]	=	new Option(listIds[index],listIds[index]);
	}	
}
function mandatory()
{   
	for(var index=0;index<document.forms[0].locationId.length;index++)
	{
		document.forms[0].locationId[index].selected=true;
		
	}
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
 <form method="get"  onSubmit="return mandatory()" action="QMSZoneCodeMasterDownloadProcess.jsp" name="DensityGroupCode"  >

    <table border="0" cellpadding="4" cellspacing="0" width="100%"  bgcolor='#FFFFFF'>
        <tbody>
          <tr class="formlabel">
            <td colspan="4" width="789"><font size='3'><b>Zone Code Master - <%=operation%></td><td>QS1000141</b></font></td>			
          </tr>
		  </tbody>
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
			<td>Location
                  :<sup><font color="red">*</font>
                  </td>
			<td><!-- <input type='textArea' onBlur='toUpperCase(this)' maxLength="16" name="locationId"  size="10" width ='200' height='100'>  <input class="input" name="locationLOV" onclick="showLov(locationLOV)"  type="button" value="..." ></font> -->
			<input type='text' name='locationId' size='15' onBlur='toUpperCase(this)' class='text'>
			<input class="input" name="locationLOV" onclick="showLov(locationLOV)"  type="button" value="..." >
			</td>			
		  </tr>		  
		  
		</tbody>
	</table>	
	<table border="0" cellpadding="4" cellspacing="0" width="100%" bgcolor='#FFFFFF'>
       <tbody>
         <tr >
            <td colspan="2" width="452" class='denotes'><font color="#FF0000">*</font> Denotes Mandatory</td>
			  <td align="right" colspan="2" width="325">
			  <input class="input" name="Submit" type="submit" value="Submit">
              <input class="input" name="Reset" type="reset" value="Reset"> 
			  <input  name="Operation" type="hidden" value='<%=operation%>'> </td>
         </tr>
       </tbody>
     </table>
  </form>
 </body>
</html>

<%
	 }
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