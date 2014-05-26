
<%
/*	Programme Name : QMSZoneCodeMasterAdd.jsp.
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
<%@ page import="org.apache.log4j.Logger,
                 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 java.util.ArrayList"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%! 
  private static Logger logger = null;
public final static String   FILE_NAME = "QMSZoneCodeMasterAdd.jsp";%>

<%
    logger  = Logger.getLogger(FILE_NAME);  
    String       operation      =  request.getParameter("Operation");
	
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

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
<meta name="GENERATOR" content="Microsoft FrontPage 4.0">
<meta name="ProgId" content="FrontPage.Editor.Document">
<title>ZoneCodeMaster</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script language="javascript" src="<%=request.getContextPath()%>/html/dynamicContent.js"></script>
<script src ='html/eSupply.js'></script>
<script language="javascript">
	var rowNo	=	0;
function defaultFunction(currentRow)
{
	rowNo=rowNo+1;
	var aircheckname	=	document.getElementById("aircheckbox"+rowNo);
	var seacheckname	=	document.getElementById("seacheckbox"+rowNo);
	var truckcheckname	=	document.getElementById("truckcheckbox"+rowNo);
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
		var tableZipCode	= document.getElementById("QuoteContent");		
		createDynamicContentRow(tableZipCode.getAttribute("id")); 		
 }

var Win   =  null;

function shipModeChecked(index)
{
	var aircheckname	=	document.getElementById("aircheckbox"+index);
	var seacheckname	=	document.getElementById("seacheckbox"+index);
	var truckcheckname	=	document.getElementById("truckcheckbox"+index);
	
	if(!aircheckname.checked && !seacheckname.checked && !truckcheckname.checked)
	{
		alert("Pls,select the shipmentMode before going for Content Ids select at LaneNo:"+index);
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
				alert("Please Select AtLeast one Shipment Mode at Lane :"+(i+1));
				airCheck[i].focus();
				return false;
			}
		}
	}
	else
	{
			if(!airCheck.checked && !seaCheck.checked && !truckCheck.checked)
			{
				alert("Please Select atleast one Shipment Mode");
				airCheck.focus();
				return false;
			}
	}
return true;
}
function isContentSelected()
{
		var contentId	=	document.getElementsByName("contentId");
		
		if(contentId.length>1)
		{
			for(var i=0;i<contentId.length;i++)
			{
				
				if(contentId[i].value.length==0)
				{
					alert("Please Select Content Id at Row"+(i+1));
					return false;
				}
			}
		}else
		{
				contentId	=	document.getElementById("contentId");
				if(contentId.value.length==0)
				{
					alert("Please Select Content Id");
					return false;
				}
		}

		return true;
}
function isDescSelected()
{

var contentDescription	=	document.getElementsByName("contentDescription");
		
		if(contentDescription.length>1)
		{
			for(var i=0;i<contentDescription.length;i++)
			{
				
				if(contentDescription[i].value.length<=1)
				{
					alert("Please Select Content Description at row "+(i+1));
					return false;
				}
			}
		}else
		{
				contentDescription	=	document.getElementById("contentDescription");
				if(contentDescription.value.length<=1)
				{
					alert("Please Select Content Description at row 1");
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
function mandatory()
{
	if(!isShipModeChecked())
	 	return false;
    if(!isContentSelected())
	  return false;
	if(!isDescSelected())
	  return false;

	if(document.forms[0].B1!=null)
	{
		document.forms[0].B1.disabled	=true;
	}

	return true;
}
function check_Length(maxLength,input)
{
	val = input.value;
	if(val.length > maxLength)
	alert("Character Limit reached("+maxLength+")");
	val = val.substring(0,maxLength);
	
	input.value = val;
}

	</script>
<body onLoad='initialize()'>
<form  action = 'QMSSetupController' onSubmit='return mandatory()' method='post'>
  <table border=0 cellpadding=0 cellspacing=0 width='100%'>
    <tr valign=top> 
      <td> 
        <table border=0 cellpadding=4 cellspacing=1 width="100%">
          <tr class='formlabel' valign=top> 
            <td  colspan=5>
              <table width="100%" border="0" >
                <tr class='formlabel'>
                  <td>Content Master  -Add </td>
                  <td align=right>QS1020411</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
        
        <table width="100%" border='0' cellspacing='1' cellpadding='2' nowrap id='QuoteContent' idcounter="1" defaultElement="defaultElement" xmlTagName="QuoteContent" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" maxRows=20 bgcolor='#FFFFFF'>
		  <tr valign=top class='formheader'>
			<td>&nbsp;</td>
			<td align='center'>Shipment Mode:</td>
			<td>Header/Footer:</td>
            <td align='center'>Content ID:</td>
			<td align='center'>Content Description:</td>
			<td align='center'>Default:</td>
			<td>&nbsp;</td>
          </tr>
         </table> 
		 <table width="100%" border='0' cellspacing='1' cellpadding='2' bgcolor='#FFFFFF'>
          <tr> 
            <td colspan=3 valign=top class='denotes'>All Fields are Mandatory 
               </font>
            </td>
            <td colspan=2 valign=top align='right'> 
                <input name=B1 type=submit value=Submit class='input'>
				<input name="Operation" type="hidden" value="<%=operation%>" class='input'>
				<input name="subOperation" type="hidden" value="submit" class='input'>
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
			}
	}
	catch(Exception e)
	{
		e.printStackTrace();
		//Logger.error(FILE_NAME,"Error in QMSZoneCodeMasterAdd.jsp"+e.toString());
    logger.error(FILE_NAME+"Error in QMSZoneCodeMasterAdd.jsp"+e.toString());
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