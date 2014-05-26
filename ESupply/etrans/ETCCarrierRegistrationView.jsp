<%--
	 Program Name    : ETCCarrierRegistrationView.jsp
     Module Name     : ETrans
	 Task            : CarrierRegistration
	 Sub task        : View 
	 Author Name     : Giridhar Manda
	 Date Started    : September 12,2001
	 Date Completed  : September 12,2001
	 Date Modified   : 
	 Description     :
	     This file is used to Modify, View or Delete a Carrier. This jsp takes all the data entered in the fields and passes to 
	ETCCarrierRegistrationProcess.jsp if the module selected from the eSupply tree is either 'Modify' or 'Delete' else if the
  module selected is 'View', this jsp shows the details of the Carrier corresponding to the entered CarrierId in the
  ETCCarrierRegistrationEnterId.jsp.
  This file interacts with CustomerRegSession Bean and then calls the method 'getCountryIds()' which inturn 
  retrieves all the CountryIds corresponding to the Country Names.
  This file also calls the methods getCarrierDetail( String carrierId ) and getAddressDetail( String addressId ) of SetUpSession Bean to get the details of the
  Carrier particular to the CarrierId.
	
 
--%>
<%@ page import = "java.sql.Connection,
            	java.sql.Statement,
				java.sql.ResultSet,
				javax.sql.DataSource,
				javax.naming.InitialContext,
				java.util.Vector,
				org.apache.log4j.Logger,
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.qms.setup.ejb.sls.SetUpSession,
                com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.etrans.setup.carrier.bean.CarrierDetail,
				com.foursoft.etrans.common.bean.Address,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue,
				java.util.ArrayList" %>
				
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCarrierRegistrationView.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);
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
		//Logger.error(FILE_NAME,"Exception in Login of ETCarrierRegistrationView.jsp : ",e.toString());
    logger.error(FILE_NAME+"Exception in Login of ETCarrierRegistrationView.jsp : "+e.toString());
    }

	String 	operation 		= null;
	String 	value			= null;
	String 	errorMessage	= null;
	String countryIds[]		= null;
	String carrierId		= null;
	String carrierName 		= null;
	String carrierNumber	= null;
  	String numericCode 		= null;
  	String shipmentMode		= null;
    String  addressLine1    = null;
    String  addressLine2    = null;
    String  city            = null;
    String  state           = null;
    String  zipCode         = null;
    String  countryId       = null;
    String  phoneNo         = null;
    String  emailId         = null;
    String  fax             = null;
    String  actionValue		= null;
    String  submitValue		= null; 
    String  readOnly		= null;
    String  characterFilterFunc	= null;
    String  disabled 		= "";
	String	check			= "";
   	boolean exists ;
	
    InitialContext 					initial			=	null;
	SetUpSessionHome	home 			=	null;
	SetUpSession 		remote 			=	null;
	SetUpSessionHome		utilHome		=	null;
	SetUpSession			utilRemote		=	null;
	OperationsImpl					operationsImpl	=	null; 
	
	ErrorMessage errorMessageObject = null;
	ArrayList	 keyValueList	    = new ArrayList();
	
	try
	{
			operationsImpl = new OperationsImpl();
			operationsImpl.createDataSource();
			initial 		=	new InitialContext();
			home			=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
		    remote			=	(SetUpSession)home.create();
			utilHome		=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
			utilRemote		=	(SetUpSession)utilHome.create();
			countryIds 		=	operationsImpl.getCountryIds();
			session.setAttribute("CountryIds",countryIds);
		    operation		= request.getParameter("Operation");	
		    carrierId 		= request.getParameter("carrierId");
			shipmentMode	= request.getParameter("shipmentMode");
			//carrierNumber	= request.getParameter("carrierNumber");

		if(remote.checkCarrier(carrierId,shipmentMode))
		{
		  CarrierDetail CarrierRegObj	= remote.getCarrierDetail(carrierId,shipmentMode);
          if(CarrierRegObj != null)
		  {
			carrierName 		= CarrierRegObj.getCarrierName();
			numericCode  		= CarrierRegObj.getNumericCode()!=null?CarrierRegObj.getNumericCode():"";
			shipmentMode 		= CarrierRegObj.getShipmentMode();
			carrierNumber		= CarrierRegObj.getCarrierNumber();
			
			//Address AddressObj	= utilRemote.getAddressDetails(new Integer(CarrierRegObj.getAddressId()).toString());
			Address AddressObj	= utilRemote.getAddressDetails(CarrierRegObj.getAddressId());			
            addressLine1  		= AddressObj.getAddressLine1();
			addressLine2  		= AddressObj.getAddressLine2();
			city  				= AddressObj.getCity();
			state  				= AddressObj.getState();
			zipCode  			= AddressObj.getZipCode();
			countryId  			= AddressObj.getCountryId();
			phoneNo  			= AddressObj.getPhoneNo();
			emailId  			= AddressObj.getEmailId();
			fax  				= AddressObj.getFax();
		    if(addressLine2 == null)
				addressLine2 = "";
			if(state == null)
				state = "";
			if(zipCode == null)
				zipCode = "";
			if(phoneNo == null)
				phoneNo = "";
			if(emailId == null)
				emailId = "";
			if(fax == null)
				fax = "";
			
			if(carrierNumber.equals("Y"))
			  check="Checked";
		  }	
		 else
		 { 
			 
			 errorMessageObject = new ErrorMessage("Record Not Found  With carrier Id : "+carrierId,"ETCCarrierRegistrationEnterId.jsp"); 
		 	
			 keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
			 keyValueList.add(new KeyValue("Operation",operation)); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
			 
			 
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		 }
	 }else
	  {
		 errorMessageObject = new ErrorMessage("Record Not Found  With carrier Id : "+carrierId,"ETCCarrierRegistrationEnterId.jsp"); 
		 	
		 keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
		 keyValueList.add(new KeyValue("Operation",operation)); 	
		 errorMessageObject.setKeyValueList(keyValueList);
		 request.setAttribute("ErrorMessage",errorMessageObject);
		
		
		
		
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%	}
	 if(operation.equalsIgnoreCase("Modify"))
		value	=	"Modify";
	else if(operation.equalsIgnoreCase("Delete"))
		value	=	"Delete";
	else if(operation.equalsIgnoreCase("View"))
		value	=	"Continue";
%>
<html>
<head>
<title>CarrierRegistration <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<script>
arrayCountryIds  = new Array();
	// This function is used to load the CountryIds
	function loadValues()
	{
<%		
	if(!(operation.equals("View") || operation.equals("Delete")))
	{
		 if( countryIds!=null )
		{
			int len2 = countryIds.length;
			for( int i=0; i<len2; i++ )
			{
				int idx = countryIds[i].indexOf('(');		// square brace changed to angular
				int idy	= countryIds[i].indexOf(')');	
				String cId = countryIds[i].substring(idx+2,idy-1);
%>
				arrayCountryIds[<%=i%>] = '<%=cId%>';
<%		
			}
		}
	}	
%>
	}
	
  //This function is used to populate the list of ShipmentMode.
  function populateList()
  {
	  shipmentMode = '<%= shipmentMode%>'
      
	  document.forms[0].shipmentMode.value = shipmentMode;

	  if(shipmentMode == '1' || shipmentMode == '3' ||shipmentMode == '5' ||shipmentMode == '7')
	  {
		document.forms[0].Air.checked = true;
		document.forms[0].Air.disabled = true;
	  }

	  if(shipmentMode == '2' || shipmentMode == '3' ||shipmentMode == '6' ||shipmentMode == '7')
	  {
		document.forms[0].Sea.checked = true;
		document.forms[0].Sea.disabled = true;
		//document.forms[0].carrierNumber.checked = false;
		//document.forms[0].carrierNumber.disabled = true;
	  }

	  if(shipmentMode == '4' || shipmentMode == '5' ||shipmentMode == '6' ||shipmentMode == '7')
	  {
		document.forms[0].Truck.checked = true;
		document.forms[0].Truck.disabled = true;
	  }
		document.forms[0].carrierName.focus();
 
	/*if(document.forms[0].length > 0)
	{
		var field = document.forms[0];
		for( i=0; i<field.length; i++)
		{
			document.forms[0].numericCode.focus();
			break;
		}
	} */
//	loadValues();
}
	function populate()
	{
		//carrierNumber='<%= carrierNumber%>'

		//document.forms[0].carrierNumber.value = carrierNumber;
		
	}
	//This function calls 'ETCLOVCountryIds.jsp' to get the CountryIds.	
	function showCountryIds()
	{
		var URL 		= 'ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
		var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
		var Options 	= 'scrollbars = yes,width = 360,height = 360,resizable = yes';
		var Features 	= Bars +' '+ Options;
		var Win 		= open(URL,'Doc',Features);
	}
	
Operation='<%=operation%>'   
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
			if(ShipmentMode==1)
			{
				if(NumericCode==0)
				{
					alert("Please enter Numeric Code");
					fields.numericCode.focus();
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
 <%
	if(operation.equals("Modify"))
	{
%>	
		loadValues();
		var flag	=	0;
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
		if(document.forms.length > 0)
		 {	
		   for( i=0;i<document.forms[0].elements.length;i++)
		   {
			 if((document.forms[0].elements[i].type=="text") )
			 {
				if(document.forms[0].elements[i].name != "emailId")
				document.forms[0].elements[i].value=document.forms[0].elements[i].value.toUpperCase();
			 }
			}	
		 }
<%

	}
%>
	// @@ Murali Added On 20050513 Regarding SPETI - 5585
	<%
	if(operation.equals("Delete"))
	{
	%>
			if(confirm("Do You Really want to delete it?"+ '\n' + "Click Ok to Delete Or Click Cancel To Cancel"))
			{
				return true;
			}
			else
			{
				return false;
			}
	<%
	}
	%>
	// @@ Murali SPETI - 5585
 	 	
	
	document.forms[0].submit.disabled='true'; 
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
function placeFocus() 
{
	<%
   	if((operation.equals("View") || operation.equals("Delete")))
	{ 
	%>
   	document.forms[0].submit.focus();
	<%
	}	
	%>
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

</script>
</head>
<body onLoad = "placeFocus();populateList();populate();" >
<% 
if(operation.equalsIgnoreCase("View"))
{  
	submitValue="Continue";
    actionValue="ETCCarrierRegistrationEnterId.jsp?Operation="+operation; 
}
else if(operation.equalsIgnoreCase("Modify"))
{
    submitValue="Submit";
	actionValue="ETCCarrierRegistrationProcess.jsp"; 
}
else if(operation.equalsIgnoreCase("Delete"))
{
    submitValue="Delete";
	actionValue="ETCCarrierRegistrationProcess.jsp"; 
}
if( (operation.equalsIgnoreCase("View")) || (operation.equalsIgnoreCase("Delete")) )
{ 
      readOnly="readonly"; 
      disabled = "disabled";
}
else
{ 
	readOnly=""; 
	   
}
%>
<form method="POST" action="<%=actionValue%>" name="carrier" onSubmit="return Mandatory()">
  
  <table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
	  <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel"> 
            <td >
        <table border="0" width="790">
          <tr valign="top"  class="formlabel"> 
            <td >Carrier - <%=operation%></td>
			 <td align=right><%=loginbean.generateUniqueId("ETCCarrierRegistrationView.jsp",operation)%></td>
          </tr>
			</table>
			</td></tr></table>
	   <table border="0" width="800" cellpadding="4" cellspacing="1" >
	   <tr valign="top" class='formdata'> 
            <td colspan="3" >&nbsp;</td>
          </tr>
          <tr valign="top" class="formdata"> 
            <td width="30%">Carrier 
              Id: <font color="#FF0000">*</font>
              <br>
              
              <input type='text' class='text' name="carrierId" value = "<%=carrierId%>" readonly size="6" maxlength ="5" onBlur="upper(this)" onKeyPress="return getAlphaNumeric()" >
               </td>
            <td width="30%">Numeric 
              Code: <!-- <font face="Verdana" size="2"><font color="#FF0000">*</font></font> --><br>
              <input type='text' class='text' name="numericCode" value = "<%=numericCode%>" size="3" maxlength="3"  onKeyPress="return getNumberCode()" readOnly  >
            </td>
			<td>&nbsp;&nbsp;</td>
          </tr>
          <tr valign="top" class="formdata"> 
            <td width="30%">Carrier 
              Name:<font face="Verdana" size="2"><font color="#FF0000">*</font></font><br>
              
              <input type='text' class='text' name="carrierName" value = "<%=carrierName%>" size="50" maxlength ="50" onBlur="upper(this)" onKeyPress="return getSpecialCode()" <%=readOnly%> >
              </td>
            <td width="30%">Shipment 
              Mode:<font face="Verdana" size="2"><font color="#FF0000">*</font></font><br>
				<input type="hidden" name="shipmentMode" value="<%=shipmentMode%>" class="select">
			  
			   <input type="checkbox" name="Air" value="1" onClick="setShipmentMode(this)" <%=disabled%>>Air
			   &nbsp;&nbsp;
			   <input type="checkbox" name="Sea" value="2" onClick="setShipmentMode(this)" <%=disabled%>>Sea
			   <!--Modified by kameswari-->
				<input type="checkbox" name="Truck" value="4" onClick="setShipmentMode(this)" <%=disabled%>>Truck
			   </td>
			   <td width="25%"><!-- Auto Numbering Of MAWB
				<input type="checkbox"  name="carrierNumber"  <%=check%> onClick="setCarrierGenerationType(this)" <%=disabled%>> -->
				</td>
       </tr>
        </table>

        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formdata"> 
            <td width="50%" rowspan="2">Address:<font color="#FF0000">*</font><br>
              
              <input type='text' class='text' name="addressLine1" value = "<%=addressLine1%>" size="50" maxlength="65"  onBlur="upper(this)" onKeyPress="return getSpecialCode()" <%=readOnly%>>
              
              <input type='text' class='text' name="addressLine2" value = "<%=addressLine2%>" size="50" maxlength="65" onBlur="upper(this)" onKeyPress="return getSpecialCode()" <%=readOnly%>  >
              </td>
            <td width="50%">Zip 
              or Postal Code:<br>
             <input type='text' class='text' name="zipCode" value = "<%=zipCode%>" size="15" maxlength="10"  onBlur="upper(this)" onKeyPress="return getSpecialCode()" <%=readOnly%> >
             </td>
          </tr>
          <tr valign="top" class="formdata"> 
            <td width="50%">Contact 
              No:<br>
              <input type='text' class='text' name="phoneNo" value = "<%=phoneNo%>" size="20" maxlength="15" onBlur="upper(this)" onKeyPress="return getSpecialCode()" <%=readOnly%>  >
               </td>
          </tr>
          <tr  valign="top" class="formdata"> 
            <td width="50%">City:<font color="#FF0000">*</font><br>
             
      <input type='text' class='text' name="city" value = "<%=city%>" size="30" maxlength="30" onBlur="upper(this)" onKeyPress="return getSpecialCode()" <%=readOnly%>  >
              </td>
            <td colspan="2" width="339">Fax 
              No:<br>
              <input type='text' class='text' name="fax" value = "<%=fax%>" size="20" maxlength="15" onBlur="upper(this)" onKeyPress="return getSpecialCode()" <%=readOnly%>>
             </td>
          </tr>
          <tr valign="top"  class="formdata"> 
            <td width="50%">State 
              or Province:<br>
              
              <input type='text' class='text' name="state" value = "<%=state%>" size="30" maxlength="30"  onBlur="upper(this)" onKeyPress="return getSpecialCode()" <%=readOnly%> >
             </td>
            <td width="50%">Email 
              Id: <br>
              <input type='text' class='text' name="emailId" value = "<%=emailId%>" size="50"  maxlength = "50"  onKeyPress="return getSpecialCode()"<%=readOnly%> >
             </td>
          </tr>
          <tr valign="top" class="formdata"> 
            <td width="50%" >Country Id:<font color="#FF0000">*</font><br>
              
              <input type='text' class='text' name="countryId" value = "<%=countryId%>" size="3" maxlength="2"  onBlur = "upper(this)" onKeyPress="return getKeyCode()" <%=readOnly%> >
<%
	if(!(operation.equals("View") || operation.equals("Delete")))
	{
%>		
              <input type="button" value="..." name="lov_CountryId" onClick="showCountryIds()" <%=disabled%> class="input">
	<%
	}
	%>		  
              </td>
            <td width="50%">&nbsp;</td>
          </tr>
        </table>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='denotes'> 
            <td valign="top"  width="399"> 
              <font color="#FF0000">*</font>Denotes 
                Mandatory 
            </td>
            <td valign="top"   align="right"> 
               <input type="hidden" value= "<%=operation%>"  name="Operation">
			   <!-- <input type="hidden" name="carrierNumber" value="<%=carrierNumber%>"> -->
			   <input type="hidden" name="chkCarrierNumber" >
	
                <input type="submit" value="<%=submitValue%>" name="submit" class="input">
				<% // @@ added by Suneetha for IssueID - SPETI-3844 on 7th Feb 05 %>
				<%//Commented by Sreelakshmi KVA - 20050411 SPETI-5566  //%>
				<%//Added by Sreelakshmi KVA - 20050423 SPETI-5566  //%>
			<%
			if(operation.equalsIgnoreCase("Modify"))
			{
			%>		
				<input type='reset' name='reset' value='Reset'  class='input'>
			<%
	        }
	        %>	
				<% // @@ %>
              
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
<% } catch( Exception e )
	{
		//Logger.error(FILE_NAME,"Exception in ETCarrierRegistrationView.jsp ",e.toString());
    logger.error(FILE_NAME+"Exception in ETCarrierRegistrationView.jsp "+e.toString());
		
		errorMessageObject = new ErrorMessage("Problem while retrieving the data","ETCCarrierRegistrationEnterId.jsp"); 
		 	
		keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
		keyValueList.add(new KeyValue("Operation",operation)); 	
		errorMessageObject.setKeyValueList(keyValueList);
		request.setAttribute("ErrorMessage",errorMessageObject); 
		
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	}
%>
</form>
</body>
</html> 