<%--
	Program Name    :	ETCCarrierRegistrationAdd.jsp
	Module Name     :	ETrans
	Task            :	CarrierRegistration
	Sub task        :	Add / Modify /delete
	Author Name     :	Giridhar Manda
	Date Started    :	September 12,2001
	Date Completed  :	September 12,2001
	Date Modified   :
	Description		:	This File,s main purpose is to add the CarrierRegistration Data to the database
						Method's Summery:

--%>
<%@ page import = "java.sql.Connection,
	            java.sql.Statement,
				java.sql.ResultSet,
				javax.sql.DataSource,
				java.util.Vector,
				org.apache.log4j.Logger,
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue,
				java.util.ArrayList" %>
				
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCarrierRegistrationAdd.jsp ";
%>

<%
    logger  = Logger.getLogger(FILE_NAME);
   	String	terminalId  = null;
	String countryIds[] = null;
	int    len        = 0;
	
	ErrorMessage errorMessageObject = null;
	ArrayList	 keyValueList	    = null;
	
	try
   	{
 		if(loginbean.getTerminalId()==null)
	   	{
%>
			<jsp:forward page="../ESupplyLogin.jsp" />
<%
	    }
    }
    catch(Exception e)
    {
		//Logger.error(FILE_NAME," ETCCarrierRegistrationAdd.jsp : ",e.toString());
    logger.error(FILE_NAME+" ETCCarrierRegistrationAdd.jsp : "+e.toString());
    }
%>
<%
    terminalId 					            = loginbean.getTerminalId();
	OperationsImpl operationsImpl = new OperationsImpl();
	operationsImpl.createDataSource();
	try
	{
			
			countryIds 		= operationsImpl.getCountryIds();
			if( countryIds!=null )
	        len = countryIds.length;
			session.setAttribute("CountryIds",countryIds);
%>

<html>
<head>
<title>CarrierRegistration Add</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script>
    function showCountryIds()
    {
	    var URL 		= 'ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
		var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
		var Options 	= 'scrollbars = yes,width = 360,height = 360,resizable = yes';
		var Features 	= Bars +' '+ Options;
		var Win 		= open(URL,'Doc',Features);
    }
    var arrayCountryIds  = new Array();
    function loadValues()
    {
<%
		for( int i=0; i<len; i++ )
		{
			int idx = countryIds[i].indexOf('(');		// square braces changed to angular
			int idy	= countryIds[i].indexOf(')');
			String cId = countryIds[i].substring(idx+2,idy-1);
%>
			arrayCountryIds[<%=i%>] = '<%=cId%>';
<%
		}
%>
    }
//This function is used to check whether the entered CountryId is valid or not.

    function placeFocus()
    {
	document.forms[0].carrierId.focus();
    }
	function changeShipmentMode()
	{
	    var fields    =  document.carrier;
//		if ( fields.shipmentMode[2].selected == true ||  fields.shipmentMode[3].selected == true )
		if ( fields.shipmentMode[3].selected == true )
		{
		 alert("To be Implemented");
		 fields.shipmentMode[1].selected=true;
		 fields.shipmentMode.focus();
		 return false;
		}
	}
    function Mandatory()
    {
		var fields    =  document.carrier;
		CarrierId     =  fields.carrierId.value.length;
		NumericCode   =  fields.numericCode.value.length;
		CarrierName   =  fields.carrierName.value.length;
		ShipmentMode  =  document.forms[0].shipmentMode.value ;
		//carrierNumber =  document.forms[0].carrierNumber.value ;
		AddressLine1  =  fields.addressLine1.value.length;
		City          =  fields.city.value.length;
		CountryId     =  fields.countryId.value.length;
		if(CarrierId==0)
		{
			alert("Please enter Carrier Id");
			fields.carrierId.focus();
			return false;
		}
		if(document.forms[0].shipmentMode.value==1)
		{
			if(document.forms[0].numericCode.value.length==0)
			{
				alert("Please enter Numeric Code ");
				document.forms[0].numericCode.focus();
				return false;
			}
		}
		if(document.forms[0].shipmentMode.value==1)
		{
			if(document.forms[0].numericCode.value.length != 3)
			{
				alert("Length of Numeric Code should be 3 characters");
				document.forms[0].numericCode.focus();
				return false;
			}
		}
		
		if(CarrierName==0)
		{
		 alert("Please enter Carrier Name");
		 fields.carrierName.focus();
		 return false;
		}
		if(ShipmentMode==0)
		{
		 alert("Please select atleast one Shipment Mode");
		 return false;
		}

		if(AddressLine1==0)
		{
		 alert("Please enter Address");
		 fields.addressLine1.focus();
		 return false;
		}
		if(City==0)
		{
		 alert("Please enter City");
		 fields.city.focus();
		 return false;
		}
		if(CountryId==0)
		{
		 alert("Please enter Country Id");
		 fields.countryId.focus();
		 return false;
		}
		else
		{
			loadValues();
			var flag=0;
			document.forms[0].countryId.value=document.forms[0].countryId.value.toUpperCase();
			if(document.forms[0].countryId.value.length > 0 )
			{
				for( i=0; i<arrayCountryIds.length; i++ )
				{
					if( document.forms[0].countryId.value.toUpperCase()==arrayCountryIds[i])
					{
						flag = 1;
						break;
					}
				}
				if(flag==0)
				{
						alert("Invalid country Id");
						document.forms[0].countryId.value = "";
						document.forms[0].countryId.focus();
				}
		   }
			if(flag==0)
			return false;
		 }
		if(document.forms.length > 0)
		 {
		   for( i=0;i<document.forms[0].elements.length;i++)
		   {
			 if((document.forms[0].elements[i].type=="text") )
			 {
				document.forms[0].elements[i].value=document.forms[0].elements[i].value.toUpperCase();
			 }
			}
		 }
		document.forms[0].b1.disabled='true';
		return true;
    }
    function getKeyCode()
    {
	if(event.keyCode!=13)
	{
     if((event.keyCode < 65 || event.keyCode > 90) && (event.keyCode < 97 || event.keyCode > 122))
 	   return false;
	}
	return true;
    }
    function getAlphaNumeric()
    {
    if(event.keyCode!=13)
	{
     if((event.keyCode < 48 ||event.keyCode > 57) && (event.keyCode < 65 || event.keyCode > 90) && (event.keyCode < 97 || event.keyCode > 122) )
 	   return false;
	}
	return true;
    }
    function getNumberCode()
    {
	if(event.keyCode!=13)
	{
     if((event.keyCode < 48 || event.keyCode > 57) )
 	   return false;
	}
	return true;
    }
    function getSpecialCode()
    {
    if(event.keyCode!=13)
    {
	if((event.keyCode==34 || event.keyCode==39 || event.keyCode==59 || event.keyCode==96 || event.keyCode==126))
		return false;
    }
    return true;
    }
    function upper(input)
    {
    input.value = input.value.toUpperCase();
    }
	/*function changeCarrierGen()
	{
		var shipmentValue = document.forms[0].shipmentMode.value;
		
		if(shipmentValue==2 ||shipmentValue==3 ||shipmentValue==5 ||shipmentValue==6 ||shipmentValue==7)
		{
			document.forms[0].carrierNumber.disabled=true;
			document.forms[0].carrierNumber.checked=false;
		}
		else
		{
			document.forms[0].carrierNumber.disabled=false;
			document.forms[0].carrierNumber.checked=true;
		}
	}*/
	function setShipmentMode(obj)
	{
	  
	  var objName = obj.name;
	  var objValue = obj.value;
	  var shipmentValue = document.forms[0].shipmentMode.value;
	  
	  if(obj.checked)
		{
			document.forms[0].shipmentMode.value = parseInt(shipmentValue) + parseInt(obj.value);
		}
	  else
	    {
	  		document.forms[0].shipmentMode.value = parseInt(shipmentValue) - parseInt(obj.value);
		}
	  //changeCarrierGen();

	}
	/*function setCarrierGenerationType(obj)
	{
	  var objName = obj.name;
	  var objValue = obj.value;
  
      
      document.forms[0].chkCarrierNumber.value = "Y";

	if(!obj.checked)
	{
	  document.forms[0].chkCarrierNumber.value = "N";
	}
    
	}*/
	function changeAuto()
	{
		//document.forms[0].carrierNumber.disabled=true;
		//document.forms[0].CarrierGenerationType.checked=true;
		
	}

</script>
</head>
<body onLoad ="placeFocus();changeAuto()">
<form name="carrier" method="post" action="ETCCarrierRegistrationProcess.jsp" onSubmit="return Mandatory()">
  <table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF">
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel">
            <td >
			<table border="0" width="790" >
          <tr valign="top" class="formlabel">
            <td> Carrier - Add </td>
			<td align=right><%=loginbean.generateUniqueId("ETCCarrierRegistrationAdd.jsp","Add")%>
		</td></tr>
		</table>
		</td></tr></table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
		<tr valign="top" class='formdata'><td colspan="3">&nbsp;</td></tr>
          <tr valign="top" class="formdata">
            <td width=30%>Carrier
              Id:<font color="#FF0000">*</font>
				<br>
              <input type='text' class='text' name="carrierId" size="6" maxlength = "5" onKeyPress="return getAlphaNumeric()" onBlur= "upper(this)" >
              </td>
            <td width=30%>Numeric
              Code:<!-- <font color="#FF0000">*</font> --><br>
              <input type='text' class='text' name="numericCode" size="6" maxlength="3" onKeyPress="return getSpecialCode()" onBlur= "upper(this)" >
            </td>
			<td>&nbsp;</td>
          </tr>
          <tr valign="top" class="formdata">
            <td width=30% >Carrier
              Name: <font color="#FF0000">*</font>

              <input type='text' class='text' name="carrierName" size="50" maxlength="50" onKeyPress="return getSpecialCode()" onBlur= "upper(this)" >
              </td>
            <td width=30%>Shipment
              Mode:<font color="#FF0000">*</font><br><input type="hidden" name="shipmentMode" value=0 class="select">

			   <input type="checkbox" name="Air" value="1" onClick="setShipmentMode(this)">Air
			   &nbsp;&nbsp;
			   <input type="checkbox" name="Sea" value="2" onClick="setShipmentMode(this)">Sea
				<input type="checkbox" name="Truck" value="4" onClick="setShipmentMode(this)">Truck
			   </td><td width="25%"><!--  Auto Numbering Of MAWB
					<input type="checkbox"  name="carrierNumber" value="Y"  onClick="setCarrierGenerationType(this)" > -->
					</td>
			  <!--  <input type="checkbox" name="Truck" value="4" onClick="setShipmentMode(this)">Truck  -->
              
            
          </tr>
        </table>
          <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formdata">
            <td width=50% rowspan="2">Address:<font color="#FF0000">*</font><br>
             
              <input type='text' class='text' name="addressLine1" size="50" maxlength="75" onKeyPress="return getSpecialCode()" onBlur= "upper(this)" >
              
              <input type='text' class='text' name="addressLine2" size="50" maxlength="75" onKeyPress="return getSpecialCode()" onBlur= "upper(this)" >
              </td>
            <td width=50%>Zip
              or Postal Code:<br>
              <input type='text' class='text' name="zipCode" size="15" maxlength="10" onKeyPress="return getSpecialCode()" onBlur= "upper(this)" >
              </td>
          </tr>
          <tr class="formdata" valign="top">
            <td width=50%>Contact
              No:<br>
              <input type='text' class='text' name="phoneNo" size="20" maxlength="15" onKeyPress="return getSpecialCode()" onBlur= "upper(this)" >
              </td>
          </tr>
          <tr valign="top" class="formdata">
            <td width=50%>City:<font color="#FF0000">*</font><br>
              <input type='text' class='text' name="city" size="30" maxlength="30"onKeyPress="return getSpecialCode()" onBlur= "upper(this)"  >
              </td>
            <td width=50%>Fax
              No:<br>
              <input type='text' class='text' name="fax" size="20" maxlength="15" onKeyPress="return getSpecialCode()">
             </td>
          </tr>
          <tr valign="top" class="formdata">
            <td width=50%>State
              or Province:<br>
              <input type='text' class='text' name="state" size="30" maxlength="30" onKeyPress="return getSpecialCode()" onBlur= "upper(this)" >
              </td>
            <td  width=50%>EMail
              Id: <br>
              <input type='text' class='text' name="emailId" size="50" maxlength="50" onKeyPress="return getSpecialCode()" >
              </td>
          </tr>
          <tr valign="top" class="formdata">
            <td width=50%>Country Id:<font color="#FF0000">*</font><br>
              
              <input type='text' class='text' name="countryId" size="3" maxlength="2" onBlur= "upper(this)" onKeyPress="return getKeyCode()">
             
              <input type="button" value="..." name="lov_CountryId" onClick="showCountryIds()" class="input">
              </td>
			  <input type="hidden" name="Operation" value="Add">
			  <input type="hidden" name="chkCarrierNumber">
            <td width=50%>&nbsp;</td>
          </tr>
        </table>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'>
            <td valign="top" >
              <font color="#FF0000">*</font>Denotes
                Mandatory <br>
              Note : Carrier Ids &amp; Numeric Code Should Confirm to IATA rule 1.4.1
            </td>
            
            <td valign="top" align="right">
              
                <input type="submit" value="Submit" name="b1" class="input">
				<input type="reset" value="Reset" name="reset" class="input">
				
                
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  
<%
    }
	catch( Exception e )
	{
		 
		 errorMessageObject = new ErrorMessage("Problem while Opening Page","ETCCarrierRegistrationAdd.jsp"); 
		 	
		 keyValueList = new ArrayList();
		 keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		 keyValueList.add(new KeyValue("Operation","Add")); 	
		 errorMessageObject.setKeyValueList(keyValueList);
		 request.setAttribute("ErrorMessage",errorMessageObject);
%>		 
		 <jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
	}
%>
</form>
</body>
</html>
