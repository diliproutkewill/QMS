
<%
/*	Programme Name : QMSZoneCodeMasterAdd.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : ZoneCodeMaster
*	Sub Task Name  : ZoneCodeMaster Add
*	Author		   : Rama Krishna.Y
*	Date Started   : 16-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>
<%@ page import="org.apache.log4j.Logger,
                 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 java.util.ArrayList"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
    private static Logger logger = null;
    public final static String   FILE_NAME = "QMSChargeDescriptionAdd.jsp";%>

<%
    logger  = Logger.getLogger(FILE_NAME);	
    String       operation      =  request.getParameter("Operation");
	
    try 			
   {
	 //Commented By Yuvraj on 20060504
	 /*if(loginbean.getTerminalId() == null) //this is to verify whether we can get "terminalid" through the login bean object.
	 {*/											 		
%>
		<%//=<!--jsp:forward page="../ESupplyLogin.jsp" /-->%>
<%
	/* }
	 else
	 {*/
	 //@@Yuvraj

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>ZoneCodeMaster</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
<script src ='../html/eSupply.js'></script>
<script language="javascript">
	var rowNo	=	0;
function defaultFunction(currentRow)
{
	rowNo=rowNo+1;
	//@@Commented By Yuvraj on 20060504
	/*var aircheckname	=	document.getElementById("aircheckbox"+rowNo);
	var seacheckname	=	document.getElementById("seacheckbox"+rowNo);
	var truckcheckname	=	document.getElementById("truckcheckbox"+rowNo);*/
	//@@Yuvraj
	var aircheckname	=	document.getElementsByName("aircheckbox")[rowNo-1];
	var seacheckname	=	document.getElementsByName("seacheckbox")[rowNo-1];
	var truckcheckname	=	document.getElementsByName("truckcheckbox")[rowNo-1];
	aircheckname.name	=	aircheckname.name+rowNo;
	seacheckname.name	=	seacheckname.name+rowNo;
	truckcheckname.name	=	truckcheckname.name+rowNo;
	
}
function defaultDeleteFunction()
{
	rowNo=rowNo-1;
}
function initialize()
{

	importXML('<%=request.getContextPath()%>/html/DynamicContent.xml');	
}
function initializeDynamicContentRows() 
{       
		var tableZipCode	= document.getElementById("chargeTable");
		createDynamicContentRow(tableZipCode.getAttribute("id")); 		
 }

var Win   =  null;
function showchaLOV(input)
{
	var id			=	input.id;
	var Bname		=	input.name;
	var index		=	id.substring(Bname.length);
	var name		=	"chargeId"+index;
	var searchStr	=	document.getElementById(name).value;
	var fromWhere	=	"chargeDescription";
	var aircheck	=	'';
	var seacheck	=	'';
	var truckcheck	=	'';
	if(shipModeChecked(index))
	{
		
		if(document.getElementById("aircheckbox"+index).checked)
			aircheck	=	'checked';
		if(document.getElementById("seacheckbox"+index).checked)
			seacheck	=	'checked';
		if(document.getElementById("truckcheckbox"+index).checked)
			truckcheck	=	'checked';
		var Url			= "QMSLOVChargeIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&selection=single&fromWhere="+fromWhere+"&aircheck="+aircheck+"&seacheck="+seacheck+"&truckcheck="+truckcheck;
		
		showLOV(Url);
	}
	return false;
}
function shipModeChecked(index)
{
	var aircheckname	=	document.getElementById("aircheckbox"+index);
	var seacheckname	=	document.getElementById("seacheckbox"+index);
	var truckcheckname	=	document.getElementById("truckcheckbox"+index);
	if(!aircheckname.checked && !seacheckname.checked && !truckcheckname.checked)
	{
		alert("Please Select a Shipment Mode at Lane No:"+index);
		return false;
	}

	return true;
}
function validateBeforeDeletion()
{
	return true;
}
function validateBeforeCreation()
{
	return true;
}
function isShipModeChecked()
{

	var airCheck	=	document.forms[0].aircheckbox;
	var seaCheck	=	document.forms[0].seacheckbox;
	var truckCheck	=	document.forms[0].truckcheckbox;
	if(parseInt(airCheck.length)>1)
	{
		for(var i=0;i<airCheck.length;i++)
		{
			if(!airCheck[i].checked && !seaCheck[i].checked && !truckCheck[i].checked)
			{
				alert("Please Select At Least One Shipment Mode at Lane No :"+(i+1));
				airCheck[i].focus();
				return false;
			}
		}
	}
	else
	{
			if(!airCheck.checked && !seaCheck.checked && !truckCheck.checked)
			{
				alert("Please Select at Least One Shipment Mode");
				airCheck.focus();
				return false;
			}
	}
return true;
}
function ischargesSelected()
{
		var chargeGroupId	=	document.getElementsByName("chargeId");
		
		if(chargeGroupId.length>1)
		{
			for(var i=0;i<chargeGroupId.length;i++)
			{
				
				if(chargeGroupId[i].value.length==0)
				{
					alert("Please Select Charge Id at Row No "+(i+1));
					chargeGroupId[i].focus();
					return false;
				}
			}
		}else
		{
				chargeIdsSelect	=	document.getElementById("chargeId");
				if(chargeIdsSelect.value.length==0)
				{
					alert("Please Select Charge Id");
					chargeIdsSelect.focus();
					return false;
				}
		}

		return true;
}
function isDescSelected()
{

var chargeDescId	=	document.getElementsByName("chargeDescId");
		
		if(chargeDescId.length>1)
		{
			//alert("hi   "+chargeDescId.length);
			for(var i=0;i<chargeDescId.length;i++)
			{
				
				if(chargeDescId[i].value.length==0)
				{
					alert("Please Select Charge Description Id ");
					chargeDescId[i].focus();
					return false;
				}else
				{
					if(chargeDescId[i+1]!=null)
					{
						//alert("hi "+chargeDescId[i+1])
						for(j=i+1;j<chargeDescId.length;j++)
						{
							//alert("chargeDescId[j].value="+chargeDescId[j].value+"chargeDescId[i].value="+chargeDescId[i].value)
							if(chargeDescId[j].value==chargeDescId[i].value)
							{
								alert("Duplicate Charge Description Id found at Line No:"+j);
								chargeDescId[j].focus();
								return false;
							}
						}
					}
				}
			}
		}else
		{
				chargeIdsSelect	=	document.getElementById("chargeDescId");
				if(chargeIdsSelect.value.length==0)
				{
					alert("Please Select Charge Description Id");
					chargeIdsSelect.focus();
					return false;
				}
		}

		return true;
}

function isRemarksSelected()
{

	var chargeGroupId	=	document.getElementsByName("remarks");
	var externalName	=	document.getElementsByName("externalChargeName");
	
	if(chargeGroupId.length>1)
	{
		for(var i=0;i<chargeGroupId.length;i++)
		{
			if(chargeGroupId[i].value.length==0)
			{
				alert("Please Enter the Internal Charge Name at Row Number "+(i+1));
				chargeGroupId[i].focus();
				return false;
			}
			if(i==(chargeGroupId.length-1))
				trimAll(externalName[i]);
			if(externalName[i].value.length==0)
			{
				alert("Please Enter the External Charge Name at Row Number "+(i+1));
				externalName[i].focus();
				return false;
			}
		}
	}
	else
	{
			trimAll(externalName[0]);
			chargeIdsSelect	=	document.getElementById("remarks");
			if(chargeIdsSelect.value.length==0)
			{
				alert("Please Enter the Internal Charge Name at Row Number 1");
				chargeIdsSelect.focus();
				return false;
			}
			if(externalName[0].value.length==0)
			{
				alert("Please Enter the External Charge Name at Row Number 1");
				externalName[0].focus();
				return false;
			}

	}
	return true;
}
function spacetest(obj)
{
	var input = obj.value;

	var objRegExp = /\s+/g;
	input= input.replace(objRegExp,' ');
	obj.value  = input;   
	return input;
}
function trimAll(obj) 
{
	var sString = obj.value;
	while (sString.substring(0,1) == ' ')
	{
		sString = sString.substring(1, sString.length);
	}
	while (sString.substring(sString.length-1, sString.length) == ' ')
	{
		sString = sString.substring(0,sString.length-1);
	}
	obj.value = sString;
}
function mandatory()
{
	if(!isShipModeChecked())
	 	return false;
    if(!ischargesSelected())
	  return false;
	if(!isDescSelected())
	  return false;
	if(!isRemarksSelected())
	  return false;

	if(document.forms[0].B1!=null)
	{
		document.forms[0].B1.disabled	=true;
	}

	return true;
}

	</script>
<body onLoad='initialize()'>
<form  action = 'QMSChargeDescriptionProcess.jsp?<%=operation%>' onSubmit='return mandatory()' method='post'>
  <table border=0 cellpadding=0 cellspacing=0 width='100%' bgcolor='#FFFFFF'>
    <tr valign=top> 
      <td> 
        <table border=0 cellpadding=4 cellspacing=1 width='100%'>
          <tr class='formlabel' valign=top> 
            <td  colspan=5>
              <table width="100%" border="0">
                <tr class='formlabel'>
                  <td>Charge Description  -Add </td>
                  <td align=right>QS1050211</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
        
        <table width='100%' border='0' cellspacing='1' cellpadding='2' nowrap id='chargeTable' idcounter="1" defaultElement="defaultElement" xmlTagName="ChargeDescription" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" maxRows=20 bgcolor='#FFFFFF'>
		  <tr valign=top align='center' class='formheader'> 
            <td></td>
			<td width='20%'>Shipment Mode:<font color="#FF0000">*</font></td>
			<td>Charge Id:<font color="#FF0000">*</font></td>
            <td>Charge Description ID:<font color="#FF0000">*</font></td>
			<td>Internal Charge Name:<font color="#FF0000">*</font></td>
			<td>External Charge Name:<font color="#FF0000">*</font></td>
			<td></td>
          </tr>
         </table> 
		 <table width='100%' border='0' bgcolor='#FFFFFF'>
          <tr> 
            <td colspan=3 valign=top height="27" class='denotes'> 
              <div align=left> <font face=Verdana size=2>All Fields are Mandatory 
                </font> </div>
            </td>
            <td colspan=2 valign=top height="27"> 
              <div align=right> 
                <p align="right"> 
                <input name=B1 type=submit value=Submit class='input'>
				<input name="Operation" type="hidden" value="<%=operation%>" class='input'>
                <input name=Reset onClick=placeFocus() type=reset value=Reset class='input'>
              </div>
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
			//}
	}
	catch(Exception e)
	{
		e.printStackTrace();
		//Logger.error(FILE_NAME,"Error in QMSChargeDescriptionAdd.jsp"+e.toString());
    logger.error(FILE_NAME+"Error in QMSChargeDescriptionAdd.jsp"+e.toString());
		ArrayList       keyValueList   =  new ArrayList();
		ErrorMessage    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSSalesPersonRegistrationAdd.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
    <jsp:forward page="QMSESupplyErrorPage.jsp" />
<%
	}
%>