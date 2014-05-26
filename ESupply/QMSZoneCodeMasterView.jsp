
<%
/*	Programme Name : QMSZoneCodeMasterView.jsp.
*	Module Name    : QMSSetup.
*	Task Name      : ZoneCodeMaster
*	Sub Task Name  : ZoneCodeDetails view for Modify/view/delete
*	Author		   : Rama Krishna.Y
*	Date Started   : 28-06-2005
*	Date Ended     : 
*	Modified Date  : 
*	Description    :
*	Methods		   :
*/
%>
<%@ page import="org.apache.log4j.Logger,
				 com.qms.setup.java.ZoneCodeChildDOB,
                 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 java.util.ArrayList,
				 com.qms.setup.java.ZoneCodeMasterDOB,
				 com.qms.setup.ejb.sls.QMSSetUpSession,
				 com.qms.setup.ejb.sls.QMSSetUpSessionHome,
				 javax.naming.InitialContext"%>


<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
  public final static String   FILE_NAME = "QMSZoneCodeMasterView.jsp";%>

<%
    logger  = Logger.getLogger(FILE_NAME);
		String						operation			=	request.getParameter("Operation");
		QMSSetUpSession				remote				=	null;
		QMSSetUpSessionHome			home				=	null;
		InitialContext              ictxt				=   null;
		ZoneCodeMasterDOB           dob					=   null;
		String                      location			=	request.getParameter("locationId");
		String                      shipmentMode		=	request.getParameter("shipmentMode");
		String                      consoleType			=	request.getParameter("consoleType");
		String                      zipCodeType    		=	request.getParameter("zipCodeType");
		String                      terminalId  		=	request.getParameter("terminalId");	
	    String[]					fromZipCode         =    null;
	    String[]					toZipCode           =    null;
	    String[]					zone                =    null;
	    String[]					estimationTime      =    null;
	    String[]					estimatedDistance   =    null;
	    String[]					alphaNumaric        =    null;
	    String[]					zoneCode            =    null;
		String[]					rowNo               =    null;
		ZoneCodeChildDOB            chileDOB            =    null;
		ArrayList                   zoneCodeList        =    null;
		String                      action              =    null;
		String                      readOnly            =    null;
	
    try 			
    {

		 ictxt						=		new InitialContext();
		 home						=		(QMSSetUpSessionHome)ictxt.lookup("QMSSetUpSessionBean");
		 remote						=		(QMSSetUpSession)home.create();
		 dob						=		(ZoneCodeMasterDOB)remote.selectZoneCodeDetails(location,zipCodeType,shipmentMode,consoleType,operation);	

		 zoneCodeList               =       dob.getZoneCodeList();
	
		//Logger.info(FILE_NAME,"zoneCodeList"+zoneCodeList);
		 if(zoneCodeList!=null && zoneCodeList.size()>0)
		 {           
			 if(!"View".equals(operation))
				 action   =  "QMSZoneCodeMasterProcess.jsp";
			 else
				 action   =  "QMSZoneCodeMasterEnterId.jsp?Operation=View";
			 if("View".equals(operation) || "Delete".equals(operation))
				 readOnly   =  "readOnly";
			 

%>
<html>
<head>
<title>ZoneCodeMaster</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script language="javascript" src="html/dynamicContent.js"></script>
<script language="javascript">
function showLov(lovName)
{
	 var URL  = "";
	
	if(lovName.name=="locationLOV")
	{
		URL="etrans/ETCLOVLocationIds.jsp?searchString="+document.forms[0].locationId.value+"&wheretoset="+document.forms[0].locationId.name;
	}
	/*if(lovName.name=="controlStationLOV")
	{
		if(document.forms[0].locationId.value.length<=0)
		{
			alert(" enter location Id");
			return false;
		}
		URL="etrans/ETCLOVLocationIds.jsp?searchString="+document.forms[0].controlStationId.value+"&wheretoset="+document.forms[0].controlStationId.name;
	}*/
	
	
	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;	
	var Win=open(URL,'Doc',Features);
}
function mandatory()
{	 		
		var checkBox=document.getElementsByName("check");
		var checkBoxValue=document.getElementsByName("checkBoxValue");
		<%if("ALPHANUMERIC".equals(dob.getZipCode())){%>
		var alphaNumeric = document.getElementsByName("alphaNumeric");
		<%}%>
		var fromZipCode = document.getElementsByName("fromZipCode");
		var toZipCode = document.getElementsByName("toZipCode");
		var zone = document.getElementsByName("zone");
		var estimatedTime = document.getElementsByName("estimatedTime");
		var estimatedDistance = document.getElementsByName("estimatedDistance");
		var count=0;
		
	   for(m=0;m<checkBox.length;m++)
	   {
		 if(checkBox[m].checked){
			checkBoxValue[m].value="true";    	 
			count++;
			<%if("ALPHANUMERIC".equals(dob.getZipCode())){%>
			if(alphaNumeric[m].value.length<=0){
				alert("Alpha Numeric Can't be empty")
				alphaNumeric[m].focus();
				return false;
			}
			<%}%>
			if(fromZipCode[m].value.length<=0){
				alert("From Zip Code Can't be empty")
				fromZipCode[m].focus();
				return false;
			}
			if(toZipCode[m].value.length<=0){
				alert("From Zip Code Can't be empty")
				toZipCode[m].focus();
				return false;
			}
			if(zone[m].value.length<=0){
				alert("Zone Can't be empty")
				zone[m].focus();
				return false;
			}
			if(estimatedTime[m].value.length<=0){
				alert("Estimated Time Can't be empty")
				estimatedTime[m].focus();
				return false;
			}
			if(estimatedDistance[m].value.length<=0){
				alert("Estimated Distance Can't be empty")
				estimatedDistance[m].focus();
				return false;
			}
			
		 }
		 
	   }
	   <%if(!"View".equals(operation)&& !"Invalidate".equals(operation)){%>
	   if(count==0){
			 alert("Please select atleast one CheckBox")
				 return false;
		 }
		 <%}%>
	   document.forms[0].count.value=count;
 <%if("Modify".equals(operation)){%>		
		if(!validateFromZipToZip())
			return false;
		else
			return true;
	<%}
%>
	//else
}
function numericOnly(input) 
{
		s = input.value;
		filteredValues = "''~!@#$%^&*()_-+=|\:;<>,/?";
		filteredValues1 = "1234567890.";
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++) 
		{  
			var c = s.charAt(i);
			if ( filteredValues.indexOf(c) == -1 && filteredValues1.indexOf(c)!=-1 ) 
				returnString += c;
			else
				flag = 1;
		}
		if( flag==1 )
		{
			alert("Characters not allowed");
			var field = document.forms[0];
			for(i = 0; i < field.length; i++) 
			{
			   if( field.elements[i] == input )
			   {
					document.forms[0].elements[i].focus();
					break;
		       }
		   }		
		}

		input.value = returnString;
}
function editable()
{<%if(!"Delete".equals(operation)){%>
	var checkBox=document.getElementsByName("check");
    var fromZipCode=document.getElementsByName("fromZipCode");
	var toZipCode=document.getElementsByName("toZipCode");
	var zone=document.getElementsByName("zone");
	var estimatedTime=document.getElementsByName("estimatedTime");
	var estimatedDistance=document.getElementsByName("estimatedDistance");
	document.forms[0].locationId.readonly=true;
	for(m=0;m<checkBox.length;m++)
    {
     if(checkBox[m].checked){
		
		fromZipCode[m].readOnly=false;
		toZipCode[m].readOnly=false;
		zone[m].readOnly=false;
		estimatedTime[m].readOnly=false;
		estimatedDistance[m].readOnly=false;		
	 }
	 else
		{
		 fromZipCode[m].readOnly=true;
		toZipCode[m].readOnly=true;
		zone[m].readOnly=true;
		estimatedTime[m].readOnly=true;
		estimatedDistance[m].readOnly=true;		
		}
    }
	<%}%>
}
function toUpperCase(object)
{
	object.value=object.value.toUpperCase();
}
function checkTime(field)
{
	timeStr=field.value
	if(timeStr.length!=0)
	{
		var	timePat	= /^(\d{1,2}):(\d{2})(:(\d{2}))?(\s?(AM|am|PM|pm))?$/;
		if(timeStr.length==4 && timeStr.indexOf(':')==-1)
		{
			timeStr = timeStr.substring(0,2)+':'+timeStr.substring(2,4);
			field.value=timeStr;


		}

		var	matchArray = timeStr.match(timePat);
		if (matchArray == null)
		{
			alert("Please enter in HH:MM format");
			field.focus();
		}
		else
		{
			hour = matchArray[1];
			minute = matchArray[2];
			second = matchArray[4];
			ampm = matchArray[6];
			if (second=="")	{ second = null; }
			if (ampm=="") {	ampm = null	}
			if (hour < 0  || hour >	99)	{
			alert("Please enter correct Hours");
			field.focus();
			}
			else if	(minute<0 || minute	> 59) {
				alert ("Please enter correct Minutes.");
				field.focus();
			}
		}
	}
}

//Included by shyam for Validation
function validateFromZipToZip()
{
	var fromZipCodes	=	document.getElementsByName("fromZipCode");
	var toZipCodes		=	document.getElementsByName("toZipCode");
	var alphaNumeric	=	document.getElementsByName("alphaNumeric");
	var checkBox		=	document.getElementsByName("check");

	for(i=0;i<fromZipCodes.length;i++)
	{
		if(fromZipCodes[i].value.length==0 || toZipCodes[i].value.length==0)
		{
			if(fromZipCodes[i].value.length==0)
			{
				alert("Please Enter the From Zip Code at row "+(i+1));
				fromZipCodes[i].focus();
				return false;
			}
			if(toZipCodes[i].value.length==0)
			{
				alert("Please Enter the To Zip Code at row "+(i+1));
				toZipCodes[i].focus();
				return false;
			}
		}
		else
		{
//			alert("ToZip Value---"+toZipCodes[i].value+" FromZipValue----"+fromZipCodes[i].value);
			if(checkBox[i].checked)
			{
				if(Number(toZipCodes[i].value) < Number(fromZipCodes[i].value))
				{
					alert("To ZipCode cannot be less than From ZipCode At Row No "+(i+1));
					return false;
				}
			}
			for(j=(i+1);j<fromZipCodes.length;j++)
			{
				//alert(alphaNumeric[i]);
				if(alphaNumeric[i]!=null && alphaNumeric[j]!=null)
				{
					if((alphaNumeric[i].value==alphaNumeric[j].value) && Number(fromZipCodes[i].value)<=Number(fromZipCodes[j].value) && Number(fromZipCodes[j].value)<=Number(toZipCodes[i].value))
					{
						alert("The From Zip Code "+fromZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
					if((alphaNumeric[i].value==alphaNumeric[j].value) && Number(fromZipCodes[i].value)<=Number(toZipCodes[j].value) && Number(toZipCodes[j].value)<=Number(toZipCodes[i].value))
					{
						alert("The To Zip Code "+toZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
				}
				else
				{
					if(Number(fromZipCodes[i].value)<=Number(fromZipCodes[j].value) && Number(fromZipCodes[j].value)<=Number(toZipCodes[i].value))
					{
						alert("The From Zip Code "+fromZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
					if(Number(fromZipCodes[i].value)<=Number(toZipCodes[j].value) && Number(toZipCodes[j].value)<=Number(toZipCodes[i].value))
					{
						alert("The To Zip Code "+toZipCodes[j].value+" given in row "+(j+1)+" falls in the range of the Zip Codes given in row "+(i+1));
						return false;
					}
				}
			}
		}
	}

	return true;
}

function selectAll()
{
	var checkBoxes = document.getElementsByName("check");
	if(document.forms[0].check1.checked)
	{
		for(var i=0;i<checkBoxes.length;i++)
		{ 
			checkBoxes[i].checked=true;
		}
	}
	else
	{
		for(var i=0;i<checkBoxes.length;i++)
		{
			checkBoxes[i].checked=false;
		}
	}
	editable();
}
</script>
<body >

<form method="post" action='<%=action%>'  onSubmit="return mandatory()" >

  <table width="100%" cellpadding="4" cellspacing="0" bgcolor='#FFFFFF'>

		<tr class="formlabel">
			<td >Zone Code Master - <%=operation%></td><td align='right'>QS1040422</td>
		</tr>
		</table>
			<table border="0" cellpadding="4" cellspacing="1" width="100%" bgcolor='#FFFFFF'>
              <tr class="formdata" vAlign="top">
                <td>Shipment Mode</td>
                <td>
					<select name='shipmentMode' class='select'>
						<option value='<%=shipmentMode!=null?shipmentMode:""%>'><%="1".equalsIgnoreCase(shipmentMode)?"Air":"Sea"%></option>
					</select>
				</td>
<%
				if("1".equalsIgnoreCase(shipmentMode))
				{
%>                
					<td colspan='2'></td>
<%
				}
				else
				{
%>					<td>Console Type</td>
					<td>
						<input class="text" maxLength="3" name="consoleType" size="10" value='<%=consoleType!=null?consoleType:""%>' readOnly>
					 </td>
<%
				}
%>
              </tr>
			  <tr class="formdata" vAlign="top">
                <td>Location</td>
                <td><input class="text" maxLength="10" name="locationId"  size="10" value='<%=dob.getOriginLocation()%>' readonly></td>
                
				<td>Terminal Id</td>
                 
				 <td>
					<input class="text" maxLength="16" name="terminalId" onBlur='toUpperCase(this)' size="10" value='<%=dob.getTerminalId()%>' readOnly>
				 </td>
              </tr>
              <tr class="formdata" vAlign="top">
                <td>City</td>
                <td><input class="text" maxLength="20" name="city"  size="20" value='<%=dob.getCity()%>' readonly></td>
                <td>State</td>
                <td><input class="text" maxLength="20" name="state"  size="20" value='<%=dob.getState()%>' readonly></td>
              </tr>
              <tr class="formdata" vAlign="top">
               <td>Zip Code Type</td>
 			   <td><input type="text" name="zipCode" value='<%=dob.getZipCode().toUpperCase()%>' readOnly class='text'></td>
			   <td colspan='2'></td>
              </tr>
          </table>
		  <table width="100%" border="0" cellPadding="4" cellSpacing="0" bgcolor='#FFFFFF' >
			<tr class='formheader' align="center"> 
				
				<td>From Zipcode:<font color="red">*</font></td>
				<td>To Zipcode:<font color="red">*</font></td>
				<td>Zone:<font color="red">*</font></td>
				<td>Estimation Time:<font color="red">*</font></td>
				<td>Estimated Distance:<font color="red">*</font></td>				
				<td><%=operation.equals("View")?"":operation%><br><%=(operation.equals("Delete") || "Modify".equalsIgnoreCase(operation))?"<input type='checkBox' name='check1'  onClick='selectAll()' >":""%></td>
			</tr> 

<%			   for(int i=0;i<zoneCodeList.size();i++)
			  {
					chileDOB     =       (ZoneCodeChildDOB) zoneCodeList.get(i);
%>
				<tr class='formdata' align="center">
<%				if("ALPHANUMERIC".equals(dob.getZipCode()))
				{
%>
					<td class=formdata ><span>
					<input type='text' name='alphaNumeric'  onBlur='toUpperCase(this)' value='<%=chileDOB.getAlphaNumaric()==null?"":chileDOB.getAlphaNumaric()%>' size="5" readonly nowrap class='text'>
					<input type='text' name='fromZipCode' value='<%=chileDOB.getFromZipCode()%>' onblur='numericOnly(this)' size="8" readonly class='text'>
					</span>
					</td>
<%				}
				else
				{
%>
					<td class=formdata ><span>
					<input type='text' name='fromZipCode' value='<%=chileDOB.getFromZipCode()%>' onblur='numericOnly(this)' size="8" readonly class='text'>
					</span>
					</td>
<%				}
%>
				<td class=formdata><input type='text' name='toZipCode' value='<%=chileDOB.getToZipCode()%>' onblur='numericOnly(this)' readonly class='text'></td>
				<td class=formdata ><input type='text' name='zone' onBlur='toUpperCase(this)' value='<%=chileDOB.getZone()%>' readonly class='text'></td>
				<td class=formdata ><input type='text' name='estimatedTime' onBlur='checkTime(this)' value='<%=chileDOB.getEstimationTime()%>' readonly class='text' size='5'></td>
				<td class=formdata ><input type='text' name='estimatedDistance' onBlur='numericOnly(this)' value='<%=chileDOB.getEstimatedDistance()%>' readonly class='text' size='5'></td>	
				<td class=formdata>

<%				if(!"View".equals(operation))
				{
%>
					<input type='checkBox' name='check'  onClick='editable()'  <%=chileDOB.getInvalidate()!=null?("T".equals(chileDOB.getInvalidate())?"checked":""):""%>>
<%				}
%>
				<input type='hidden' name='zoneCode' value='<%=chileDOB.getZoneCode()%>'  >
				<input type='hidden' name='checkBoxValue'   >
				<input type='hidden' name='rowNo' value='<%=chileDOB.getRowNo()%>'  >
				</td>
			</tr>
<%			}
%>
		</table>
		  
          <table  border="0" cellPadding="4" cellSpacing="0" width="100%">
              <tr class="text" vAlign="top">
			  <input type="hidden" name="count"  >
<%				if(!"View".equals(operation))
				{
%>
					<td  colSpan="4"><font color="red">*</font>Denotes
					  Mandatory
					  <input type="hidden" name="Operation"  value="<%=operation%>">
					  
					  </td>

					<td align="right"  colSpan="2">&nbsp;&nbsp;
					  <input class="input" name="submit" ; BORDER-BOTTOM-STYLE: solid; BORDER-LEFT-STYLE: solid; BORDER-RIGHT-STYLE: solid; BORDER-TOP-STYLE: solid" type="submit" value="Submit"></font></td>
<%				}
				else
				{
%>
					<td colSpan="4"></td>
					<td align="right"  colSpan="2">&nbsp;&nbsp;
					<input class="input" name="Continue" ; BORDER-BOTTOM-STYLE: solid; BORDER-LEFT-STYLE: solid; BORDER-RIGHT-STYLE: solid; BORDER-TOP-STYLE: solid" type="submit" value="Continue"></font></td>
<%				}
%>
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
				else
				{
					 ArrayList       keyValueList   =  new ArrayList();
					ErrorMessage    errorMessage   =  new ErrorMessage("Record Not Found","QMSZoneCodeMasterEnterId.jsp?Operation="+operation);
					keyValueList.add(new KeyValue("ErrorCode","RNF"));
					errorMessage.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessage);
%>
					<jsp:forward page="QMSESupplyErrorPage.jsp" />
<%				}
			//}
	 
	}
	catch(Exception e)
	{
		e.printStackTrace();
		//Logger.error(FILE_NAME,"Error in QMSZoneCodeMasterView.jsp"+e.toString());
		logger.error(FILE_NAME+"Error in QMSZoneCodeMasterView.jsp"+e.toString());
		ArrayList       keyValueList   =  new ArrayList();
		ErrorMessage    errorMessage   =  new ErrorMessage("Error in Accessing this page","QMSZoneCodeMasterEnterId.jsp");
		keyValueList.add(new KeyValue("ErrorCode","ERR"));
		errorMessage.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessage);
%>
		<jsp:forward page="QMSESupplyErrorPage.jsp" />
<%
	}
%>