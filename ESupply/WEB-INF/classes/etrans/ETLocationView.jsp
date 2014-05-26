<%--
%
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x
%
--%>
<%
/* Program Name		: ETLocationView.jsp
   Module name		: HO Setup
   Task		        : Location
   Sub task			: View,Modify,Delete processes
   Author Name		: A.Hemanth Kumar
   Date Started     : September 08, 2001
   Date completed   :
   Description      :
     This file is invoked when user selects Update/View/Delete of Locations from Menu bar of  main Tree structure .This file is
     used to modify/view/delete Locations Details .
     On modifying any of the details and clicking the submit button LocationsAddProcess.jsp is called.
     This file will interacts with LocationsMasterSessionBean and then calls the method updateLocationMasterDetails or deleteLocationMaster
     depending on the request. These details are then set to the respective varaibles through Object LocationMasterJspBean.
*/
%>
<%@ page import = "javax.naming.InitialContext,
				javax.naming.NamingException,
				java.util.ArrayList,
				org.apache.log4j.Logger,
				com.foursoft.etrans.setup.location.bean.LocationMasterJspBean,
				com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				java.util.Vector,
				com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.java.KeyValue"%>
<jsp:useBean id ="LocationMasterObj" class= "com.foursoft.etrans.setup.location.bean.LocationMasterJspBean" scope ="request"/>
<jsp:setProperty name="LocationMasterObj" property="*"/>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
%>

<%
	String FILE_NAME			= "ETLocationView.jsp";	
  logger  = Logger.getLogger(FILE_NAME);	
   try
   {
    // checking for TerminalId
    if(loginbean.getTerminalId() == null )
    {
%>
   		<jsp:forward page="../ESupplyLogin.jsp" />
<%
		return;
   }
 	
	String locationId    		= null;		// String to store locationId
	String locationName   		= null;  	// String to store locationName
	String countryId      		= null;    	// String to store countryId
	String city		    		= null;		// String to store city
	String zipCode		   		= null;  	// String to store zipCode
	String operation      		= null;		// String to store the type of opertion
	String actionValue    		= null;   	// String to store actionValue i.e a link
	String submitValue   		= null;   	// String to store the value of submit button
	String readOnly             = null;   	// String to store read only value
	String shipmentMode    		= "";    	// String to store shipmentMode
	String dsbl                 = null;   	// to store disable value
	ArrayList	countryIds		= null;   	// Vector to store countryIds obtained from 'ETransHOSuperSession' bean
	int    len 					= 0;		// integer to store length of the Vector
	int    shipMode				= 0;

 		operation 						= request.getParameter("Operation");
		String locationMasterId 		= request.getParameter("locationId");
	    InitialContext context			= new InitialContext();
		SetUpSessionHome clh 	= ( SetUpSessionHome )context.lookup("SetUpSessionBean");   //Home interface variable
		SetUpSession cl 					=  (SetUpSession)clh.create();    // Session Bean instance
		SetUpSessionHome clh1 	= ( SetUpSessionHome )context.lookup("SetUpSessionBean");   //Home interface variable
		SetUpSession cl1 		=  (SetUpSession)clh1.create();    // Session Bean instance
		LocationMasterObj						= cl1.getLocationMasterDetails(locationMasterId);   // instance of LocationMasterJspBean

		countryIds		= cl.getCountryIds("",loginbean.getTerminalId(),operation);//ADDED BY RK
		//Logger.info(FILE_NAME,"LocationMasterObj:	"+LocationMasterObj);
		if(LocationMasterObj != null){
		shipmentMode	= LocationMasterObj.getShipmentMode();
		//Logger.info(FILE_NAME,"Ship 0000:	"+shipmentMode);
		
		if(shipmentMode != null){
			shipMode	= Integer.parseInt(shipmentMode.trim());


		if(shipMode == 1)
			shipmentMode = "(1,3,5,7)";
		if(shipMode == 2)
			shipmentMode = "(2,3,6,7)";
		if(shipMode == 4)
			shipmentMode = "(4,5,6,7)";
		}
		}
		 if(countryIds != null)
             len = countryIds.size();
		if(operation.equalsIgnoreCase("View"))
   		{
		   dsbl = "disabled";
		   actionValue="ETLocationEnterId.jsp?Operation="+operation;
		   submitValue="Continue";
		   readOnly="readonly";
		}
		else if(operation.equalsIgnoreCase("Delete"))
		{
      		dsbl 		= "disabled";
      		actionValue	="ETLocationProcess.jsp";
			submitValue	="Delete";
			readOnly	="readonly";
		}
		else
   		{
      		dsbl = "";
		    actionValue="ETLocationProcess.jsp?";
			submitValue="Submit";
			readOnly="";
		}


	// checking the 'LoationMasterJspBean' instance
	if( LocationMasterObj != null)
	{
	   locationId    = LocationMasterObj.getLocationId();
       locationName  = LocationMasterObj.getLocationName();
       countryId     = LocationMasterObj.getCountryId();
	   city			 = LocationMasterObj.getCity();
	   zipCode		 = LocationMasterObj.getZipCode();
	   shipmentMode	 = LocationMasterObj.getShipmentMode();
	   //Logger.info(FILE_NAME,"shipmentMode:	"+shipmentMode);
		if(shipmentMode == null )
		{
			shipmentMode = "0";
			//Logger.info(FILE_NAME,"Ship 2222:	"+shipmentMode);
		}
	   if(zipCode == null)
		 zipCode = "";
	  else if(zipCode.equalsIgnoreCase("null"))
		zipCode = "";
%>
<html>
<head>
<title>LocationView</title>
<%@ include file="../ESEventHandler.jsp" %>
<script language="JavaScript">
ope='<%=operation%>'
function showCountryLOV()
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
    var Url='ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
	var Win=open(Url,'Doc',Features);
}
function setShipmentMode(obj)
	{
	  var objName = obj.name;
	  var objValue = obj.value;
	  var shipmentValue = document.forms[0].shipmentMode.value;
	  if(obj.checked)
		document.forms[0].shipmentMode.value = parseInt(shipmentValue) + parseInt(obj.value);
	  else
	  	document.forms[0].shipmentMode.value = parseInt(shipmentValue) - parseInt(obj.value);
	}
function placeFocus()
{
	var operation='<%= operation %>';
   if(operation==('View')|| operation==('Delete') )
	{
		document.forms[0].jbt_Test.focus();
	}
	else
	{
  	document.forms[0].locationName.focus();
   }
   shipmentMode = <%= shipmentMode%>
	  document.forms[0].shipmentMode.value = shipmentMode;
	  if(shipmentMode == '1' || shipmentMode == '3' ||shipmentMode == '5' ||shipmentMode == '7'){
		document.forms[0].Air.checked = true;
		document.forms[0].Air.disabled = true;
	  }
	  if(shipmentMode == '2' || shipmentMode == '3' ||shipmentMode == '6' ||shipmentMode == '7'){
		document.forms[0].Sea.checked = true;
		document.forms[0].Sea.disabled = true;
	  }
	  if(shipmentMode == '4' || shipmentMode == '5' ||shipmentMode == '6' ||shipmentMode == '7'){
		document.forms[0].Truck.checked = true;
		document.forms[0].Truck.disabled = true;
	  }
}
function Mandatory()
{
	ShipmentMode  =  document.forms[0].shipmentMode.value ;
	for(i=0;i<document.forms[0].elements.length;i++)
    {
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		    document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }
   LocationName = document.forms[0].locationName.value;
   CountryId    = document.forms[0].countryId.value;
   city			= document.forms[0].city.value;

	var operation='<%= operation %>';
	if(operation != "View")
	{
		 if(LocationName.length == 0)
		{
			alert("Please enter Location Name");
			document.forms[0].locationName.focus();
			return false;
		}
		if(operation!="Delete")
		{
			if(city.length == 0)
			{
				alert("Please enter City ");
				document.forms[0].city.focus();
				return false;
			}
		}
		if(CountryId.length== 0)
		{
			alert("Please enter Country Id");
			document.forms[0].countryId.focus();
			return false;
		}
	}
	if(ShipmentMode==0)
		{
			 alert("Please select atleast one Shipment Mode");
			 return false;
		}
   else if(CountryId.length <2)
	{
	   alert("Please enter two characters for Country Id");
		document.forms[0].countryId.focus();
		return false;
	}
	else
	{
<%
		if(operation.equals("Modify"))
	{
%>
	 var country_arr  = new Array(<%=len%>);
<%
     for (int i=0; i < len; i++)
	{
%>
	country_arr[<%=i%>]   = "<%=(String)countryIds.get(i)%>";
<%
	}
%>
	
//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5653 on 20050412.
	if(document.forms[0].countryId.value.length > 0)
	{
	 var count=0;
	 for( count =0 ; count < <%=len%> ; count++)
	 {
		var sub = country_arr[count].substring(country_arr[count].indexOf("[")+1,country_arr[count].indexOf("]"))
	  	if ( document.forms[0].countryId.value==sub )
			return true;
     }
	alert("Please enter correct Country Id ");
	document.forms[0].countryId.value='';
	document.forms[0].countryId.focus();
	return false;
	}


 <%
 }
%>
}
	document.forms[0].jbt_Test.disabled='true';
  	return true;
}
function changeToUpper(field)
{
	field.value = field.value.toUpperCase();
}
function getKeyCode()
 {
  if(event.keyCode!==13)
    {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
    }
  return true;
 }
function getSpecialCode()
 {
  if(event.keyCode!==13)
    {
     if(event.keyCode==34 || event.keyCode==39 ||event.keyCode==59||event.keyCode==96||event.keyCode==126)
	 event.returnValue =false;
    }
  return true;
 }
 
 function placeModes()
 {
      
	  document.forms[0].locationId.value='<%=locationId%>';
      document.forms[0].locationName.value='<%=locationName%>';
	  document.forms[0].city.value='<%=city%>';
      document.forms[0].zipCode.value='<%=zipCode==null?"":zipCode%>';
	  document.forms[0].countryId.value='<%=countryId%>';
	
	  shipmentMode = '<%=shipmentMode%>';
	  if(shipmentMode == '1' || shipmentMode == '3' ||shipmentMode == '5' ||shipmentMode == '7'){
		document.forms[0].Air.checked = true;
		document.forms[0].Air.disabled = true;
	  }
	  if(shipmentMode == '2' || shipmentMode == '3' ||shipmentMode == '6' ||shipmentMode == '7'){
		document.forms[0].Sea.checked = true;
		document.forms[0].Sea.disabled = true;
	  }
	  if(shipmentMode == '4' || shipmentMode == '5' ||shipmentMode == '6' ||shipmentMode == '7'){
		document.forms[0].Truck.checked = true;
		document.forms[0].Truck.disabled = true;
	  }
 }


</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body   onLoad="placeFocus()">
<form method="post" name="location" onSubmit="return Mandatory()" action="<%=actionValue%>" >
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" bgcolor="ffffff">
    <td colspan="2">
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'>
          <td colspan="4"><table width="790" border="0" ><tr class='formlabel'><td>&nbsp;Location&nbsp;
            - <%=operation%></td><td align=right><%=loginbean.generateUniqueId("ETLocationView.jsp",operation)%></td></tr></table></td>
        </tr>
        <table>
            
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
			  <tr class='formdata'><td colspan="3">&nbsp;</td></tr>
                <tr valign="top" class='formdata'>
                  <td   width="25%">Location
                    Id:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' maxlength="3" name="locationId" size="5" readOnly value='<%=locationId%>' >
                    </td>
                  <td width="40%">Location
                    Name:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="locationName" size="40"  value='<%=locationName%>'   <%=readOnly%> maxlength="50" onBlur="changeToUpper(this)" onKeypress="return getSpecialCode(this)">
                    </td>
                  <td  width="35%">City :<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="city" size="30" maxlength="30"  value='<%= (city != null) ? city : "" %>'  onKeypress="return getSpecialCode(this)"  onBlur="changeToUpper(this)" <%=readOnly%>>
                    </td>
                </tr>
				<td class='formdata' >Zip Code
                   :<br>
                    <input type='text' class='text' maxlength="15" name="zipCode" size="20"  value='<%=zipCode%>'onKeypress="return getSpecialCode(this)"   onBlur="changeToUpper(this)"  <%=readOnly%>>
                    </td>
                  <td  width="262" class='formdata'>Country
                    Id:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="countryId" size="5" maxlength="2" onBlur="changeToUpper(this)" onKeypress="return getSpecialCode(this)" value='<%=countryId%>'  <%=readOnly%>>
<%
	 if(operation.equals("Modify"))
	{
%>
					<input type="button" class='input' value="..." name="CountryLOV" onClick="showCountryLOV()">
<%
	}
%>
                    </td>
                  <td  class=formdata width="330">Shipment Mode:</font><br>
			    			   <input type="checkbox" name="Air" value="1" onClick="setShipmentMode(this)" <%=dsbl%> >Air
			   &nbsp;&nbsp;
			   <input type="checkbox" name="Sea" value="2" onClick="setShipmentMode(this)" <%=dsbl%>>Sea
			   &nbsp;&nbsp;
			   <input type="checkbox" name="Truck" value="4" onClick="setShipmentMode(this)" <%=dsbl%>>Truck
			   </td>
                </tr>
              </table>
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
                <tr class='denotes'>
                  <td valign="top"   >
                   <font color="#FF0000">*</font>Denotes
                      Mandatory<br>
                    Note : Location Ids to conform to IATA Rule No 1.2.3
                  </td>
                  
                  <td valign="top" align="right">
				  <input type="submit" value="<%=submitValue%>" name="jbt_Test" class='input'>  
                  <!--Modified By Vijay-->
				  <% if(operation.equalsIgnoreCase("Modify")) {%>
				  <input type="button" value="Reset" name="Reset" class='input' onClick="placeModes()"> 
					 <% } %>
                    <input type="hidden" name=Operation value="<%= operation %>">
                     <input type="hidden" name="shipmentMode" value=<%=shipmentMode%>>
                  </td>
                </tr>
              </table>
              
          </td>
        </tr>
      </table></form>
      
</body>
</html>
<%
	}
	else
	{
			 java.util.ArrayList keyValueList = new ArrayList();
			 ErrorMessage errorMessageObject = new ErrorMessage("Record does not exist with LocationId : "+locationMasterId,"ETLocationEnterId.jsp");
			 keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
			 keyValueList.add(new KeyValue("Operation",operation)); 	
			 errorMessageObject.setKeyValueList(keyValueList);
             request.setAttribute("ErrorMessage",errorMessageObject);
			 
			/**		
			
			session.setAttribute("Operation",operation);
			String errorMessage = "Record does not exist with LocationId : "+locationMasterId;
			session.setAttribute("ErrorCode","RNF");
			session.setAttribute("ErrorMessage",errorMessage);
			session.setAttribute("NextNavigation","ETLocationEnterId.jsp");  */
%><jsp:forward page="../ESupplyErrorPage.jsp" />
<%
	  }
   }
   catch(Exception e)
  {
	//Logger.error(FILE_NAME,"Error in LocationView.jsp file : "+e.toString());
  logger.error(FILE_NAME+"Error in LocationView.jsp file : "+e.toString());
  }
%>
