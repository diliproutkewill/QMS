
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
	
	if(lovName.name=="locationLOV")
	{
		URL="etrans/ETCLOVLocationIds.jsp?searchString="+document.forms[0].locationId.value+"&wheretoset="+document.forms[0].locationId.name;
	}
	if(lovName.name=="controlStationLOV")
	{
		controlStationFlag  =  true;
		if(document.forms[0].locationId.value.length<=0)
		{
			alert(" enter location Id");
			return false;
		}
		URL="etrans/ETCLOVLocationIds.jsp?searchString="+document.forms[0].controlStationId.value+"&wheretoset="+document.forms[0].controlStationId.name;
	}
	/*if(lovName.name=="portLOV")
	{
		portFlag = true;		
		URL="etrans/sea/ETSLOVPortIds.jsp?searchStr="+document.forms[0].port.value+"&whereToSet="+document.forms[0].port.name;
	}
	*/
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(URL,'Doc',Features);
}
function openLocationLov()
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";
	
	var indx = document.forms[0].shipmentMode.selectedIndex;

	formArray = 'locationId';		
	
	tabArray = 'LOCATIONID';
	Url		="qms/ListOfValues.jsp?lovid=LOC_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode="+document.forms[0].shipmentMode.options[indx].text+"&operation=LOCATIONS&search= where LOCATIONID LIKE '"+document.forms[0].locationId.value+"~'";


	Options	='width=750,height=750,resizable=yes';
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Features=Bars+','+Options;

	Win=open(Url,'Doc',Features);
}
function mandatory()
{
	if(document.forms[0].locationId.value.length<=0)
	{
		alert("Please enter Origin Location ");
		document.forms[0].locationId.focus();
		return false;
	}
	/*if(document.forms[0].controlStationId.value.length<=0)
	{
		alert("Please enter Control Station ");
		document.forms[0].controlStationId.focus();
		return false;
	}
	if(document.forms[0].port.value.length<=0)
	{
		alert("Please enter port ");
		document.forms[0].port.focus();
		return false;
	}*/
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
 <form method="get"  onSubmit="return mandatory()" action="QMSZoneCodeMasterView.jsp">

<table width="100%" cellpadding="4" cellspacing="0" bgcolor='#FFFFFF'>

		<tr class="formlabel">
			<td >Zone Code Master - <%=operation%></td><td align='right'>QS1040421</td>
		</tr>
		</table>
	
	<table border="0" cellpadding="4" cellspacing="0" width="100%" bgcolor='#FFFFFF'>
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
			<td>Location:<sup><font color="red">*</font></td>
			
			<td>
				<input class="text" onBlur='toUpperCase(this)' maxLength="16" name="locationId"  size="10">  <input class="input" name="locationLOV" onclick="openLocationLov()"  type="button" value="..." ></font>
			</td>
			
			 <td>Zip Code Type:<font color="#FF0000">*</font> 
			   
			   <select name="zipCodeType"  class='select'>
				  <option value="NUMERIC">Numeric</option>
				  <option value="ALPHANUMERIC">Alpha-Numeric</option>
				</select>
			</td>
		</tr>
	</table>	
	<table border="0" cellpadding="4" cellspacing="1" width="100%" bgcolor='#FFFFFF'>
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
	 //}
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