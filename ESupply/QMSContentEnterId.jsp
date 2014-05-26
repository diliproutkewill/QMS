
<%
/*	Programme Name : QMSContentEnterId.jsp.
Module Name		:QMSSetup
	Task			:Content Description Master
	Sub Task		:LOV
	Author Name		:RamaKrishna Y
	Date Started	:Sep 21,2005
	Date Completed	:
	Date Modified	:
	Description		:
*/
%>

<%@ page import = "java.util.ArrayList,
				   org.apache.log4j.Logger,
				   com.foursoft.esupply.common.java.ErrorMessage,
    			   com.foursoft.esupply.common.java.KeyValue"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
  public static final String FILE_NAME="QMSContentEnterId.jsp";%>

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
					 
%>

<html>
<head>
<title>Content Master</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>

<script language="JavaScript">

function placeFocus()
{
	
}

function showLOV(obj)
{
	var url="";
	
	
		if(!document.forms[0].airCheck.checked && !document.forms[0].seaCheck.checked && !document.forms[0].truckCheck.checked)
		{
			alert("Please Select Shipment Mode value");			
			return false;
		}
	    else{
			var shipMode ;
			var shipModeStr ;
			if(document.forms[0].airCheck.checked )
					{
						if(document.forms[0].seaCheck.checked)
						{
							if(document.forms[0].truckCheck.checked)
							{
								shipMode	=	7;
								shipModeStr	=	"1,2,3,4,5,6,7";
							}else
							{
								shipMode	=	3;
								shipModeStr	=	"1,2,3,5,6,7";
							}
						}else if(document.forms[0].truckCheck.checked)
						{
							shipMode	=	5;
							shipModeStr	=	"1,3,4,5,6,7";
						}else
						{
							shipMode	=	1;
							shipModeStr	=	"1,3,5,7";
						}
					}else if(document.forms[0].seaCheck.checked)
					{
						if(document.forms[0].truckCheck.checked)
						{
							shipMode	=	6;
							shipModeStr	=	"2,3,4,5,6,7";
						}else
						{
							shipMode	=	2;
							shipModeStr	=	"2,3,6,7";
						}
					}else if(document.forms[0].truckCheck.checked)
					{
						shipMode	=	4;
						shipModeStr	=	"4,5,6,7";
					}
				
			//modified by vlakshmi for wpbn 174301 on 20090624
			url="QMSLOVContentIds.jsp?operation=<%=operation%>&searchString="+document.forms[0].descId.value+"&shipmentMode="+shipMode+"&shipModeStr="+shipModeStr;
		}
	

	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(url,'Doc',Features);
}

 function upperCase(input)
 {
  input.value=input.value.toUpperCase();
 }

function mandatory()
{
	if(!document.forms[0].airCheck.checked && !document.forms[0].seaCheck.checked && !document.forms[0].truckCheck.checked)
	{
		alert("Please Select Shipment Mode value");			
		return false;
	}
	if(document.forms[0].descId.value.length<=0)
	{
		alert("Please enter Content ID value");
		document.forms[0].descId.focus();
		return false;
	}
	return true;
}


</script>
<body onLoad='placeFocus()'>
 <form method="get"  onSubmit="return mandatory()" action="QMSSetupController"  >

    <table border="0" cellpadding="4" cellspacing="0" width="100%" bgcolor='#FFFFFF'>
        <tbody>
          <tr class="formlabel">
            <td height="1"><b>Content Master-<%=operation%></td>
			<td align='right'>QS1020421</b></td>
          </tr>	
		 </tbody>
	</table>
	<table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF' >
	 <tbody>
		  <tr class="formdata">
			<td width="100" >ShipmentMode:<font color="#FF0000">*</font> &nbsp;</td>
			<td width="450"><input type="checkbox" name="airCheck" size='12' >Air
			<input type="checkbox" name="seaCheck" size='12' >Sea<input type="checkbox" name="truckCheck" size='12' >Truck</td>
			<td width="110" >Content ID:<font color="#FF0000">*</font> &nbsp;</td>
			<td width="300"><input type="text" name="descId" size='20' maxLength='25' class='text' onBlur="upperCase(this);">
			<input class="input" type="button" name="descIdLOV" value="..." onClick="showLOV(this)"></td>
		  </tr> 
		</tbody>
	</table>	
	<table width="100%"  border="0" cellspacing="1" cellpadding="4"  bgcolor='#FFFFFF' >
       <tbody>
         <tr >
            <td colspan="2" width="452" class='denotes'><font color="#FF0000">*</font> Denotes Mandatory</td>
			  <td align="right" colspan="2" width="325">
			  <input class="input" name="Submit" type="submit" value="Submit">
              <input class="input" name="Reset" type="reset" value="Reset"> 
			  <input  name="Operation" type="hidden" value='<%=operation%>'> </td>
			  <input name="subOperation" type="hidden" value="view" class='input'>
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