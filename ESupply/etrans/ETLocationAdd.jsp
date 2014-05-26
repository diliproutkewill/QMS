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
/* Program Name		: ETLocationAdd.jsp
   Module name		: HO Setup
   Task		        : Location
   Sub task			: Add Location
   Author Name		: A.Hemanth Kumar
   Date Started     : September 08, 2001
   Date completed   : 
   
   Description      :
    This file is invoked when user selects Add of Locations from Menu bar of  main Tree structure .This file is 
    used to add new Location Details .
    On entering all the details and clicking the submit button LocationAddProcess.jsp is called.
    This file  interacts with ETransHOSuperSessionBean and then calls the method setETransHOSuper 
    These details are then set to the respective varaibles through Object LocationMasterJSPBean.
*/
%>
<%@ page import = "java.util.ArrayList,
				javax.naming.InitialContext,
				com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				org.apache.log4j.Logger"%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
%>

<%
	String FILE_NAME ="ETLocationAdd.jsp";
  logger  = Logger.getLogger(FILE_NAME);	
   try
   {
    if(loginbean.getTerminalId() == null )
    {
%>
   <jsp:forward page="../ESupplyLogin.jsp" />
<%
   }
   else
   {
 
	String		terminalId;   			 // String to store terminal Id
	ArrayList	countryIds = null;    // Vector to store countryIds obtained from 'ETransHOSuperSession' bean
	int 		len = 0;    			// integer to store the size of vector 'countryIds'
    String param = "Add";//request.getParameter("Operation");
try
 {
        String operation  = request.getParameter("Operation");//added by rk
	    terminalId 						= loginbean.getTerminalId();
        InitialContext initial                	= new InitialContext();    // Context variable to store InitialContext
        SetUpSessionHome home =(SetUpSessionHome )initial.lookup( "SetUpSessionBean" );    //Home interface variable
	    SetUpSession remote   =(SetUpSession)home.create();   // Session Bean instance
        countryIds = remote.getCountryIds("",terminalId,operation);//added by rk
		// if Vector contains elements then it's size is assigned to 'len'
		if(countryIds != null)
         len = countryIds.size();
  }
  catch(Exception ee)
  {
    //Logger.info(FILE_NAME,"Error in LocationAdd.jsp file  : "+ee.toString());
    logger.info(FILE_NAME+"Error in LocationAdd.jsp file  : "+ee.toString());
  }
%>

<html>
<head>
<title>Location Master </title>
<%@ include file="../ESEventHandler.jsp" %>
<script language="JavaScript">

function placeFocus() 
{
	  document.forms[0].locationId.focus();
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
function showCountryLOV()
{
 	
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
 	var Url='ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
	var Win=open(Url,'Doc',Features);
}
function Mandatory()
{
	ShipmentMode  =  document.forms[0].shipmentMode.value;
	for(i=0;i<document.forms[0].elements.length;i++)
    {
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		    document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }				
    LocationId   = document.forms[0].locationId.value;
	LocationName = document.forms[0].locationName.value;
	CountryId    = document.forms[0].countryId.value;
	city		 = document.forms[0].city.value; 			
	
    if(LocationId.length ==0)
	{
	    alert("Please enter Location Id");
		document.forms[0].locationId.focus();
		return false;
	}	
   else if(LocationId.length < 3)
   {
	  alert("Please enter three characters for Location Id");
	  document.forms[0].locationId.focus();
	  return false;
   }	
    substr='';
   for(c=0;c<LocationId.length;c++)
     {
      substr=LocationId.substring(c,c+1);
	  if((substr==' ')||(substr=='1')||(substr=='2')||(substr=='3')||(substr=='4')||(substr=='5')||(substr=='6')||(substr=='7')||(substr=='8')||(substr=='9')||(substr=='0'))
	     {
		 alert('Please Don\'t enter Spaces and Numbers');
		 document.forms[0].locationId.value='';
		 return false;
		 }
	 }
   if(LocationName.length == 0)
	{
		alert("Please enter Location Name");
		document.forms[0].locationName.focus();
		return false;
	}
	if(city.length == 0)
	{
		alert("Please enter City ");
		document.forms[0].city.focus();
		return false;
	}
	
	if(CountryId.length == 0)
	{
		alert("Please enter Country Id ");
		document.forms[0].countryId.focus();
		return false;
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
		var country_arr  = new Array();   
 
<%
	for (int i=0; i < len; i++)
	{
%>
	country_arr[<%=i%>]   = "<%=(String)countryIds.get(i)%>";
<%
	}
%>
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
	
}
	document.forms[0].jbt_Test.disabled='true';
  	return true;	
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
     if((event.keyCode > 32 && event.keyCode < 40) || event.keyCode == 64   ||event.keyCode==96 || event.keyCode==126 )
	 event.returnValue =false;
    }
  return true;
 } 
function changeToUpper(field)
{
	field.value = field.value.toUpperCase();
}		
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body   onLoad="placeFocus()">
<form method="post" name="location" onSubmit="return Mandatory()" action="ETLocationProcess.jsp" >
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" bgcolor="ffffff"> 
    <td  colspan="2"> 
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td colspan="4" ><table width="790" border="0" ><tr class='formlabel'><td>&nbsp;Location&nbsp;- Add </td><td align=right><%=loginbean.generateUniqueId("ETLocationAdd.jsp","Add")%></td></tr></table></td>
        </tr>
       </table>
            
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="3">&nbsp;</td></tr>
                <tr valign="top" class='formdata'> 
                  <td  width="25%">Location 
                    Id:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' maxlength="3" name="locationId" size="5"  onBlur="changeToUpper(this)" onkeyPress="return getKeyCode(this)">
                   
                    </td>
                  <td  width="40%">Location 
                    Name:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="locationName" size="40" maxlength="50" onBlur="changeToUpper(this)" onKeypress="return getSpecialCode(this)">
                    </td>
                  <td  width="35%">City :<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="city" size="30" maxlength="30" onBlur="changeToUpper(this)" onkeyPress="return getSpecialCode(this)">
                    
                   </td>
                </tr>
<tr class='formdata'>
				<td >Zip Code 
                   :<br>
                    <input type='text' class='text' maxlength="15" name="zipCode" size="20"  onBlur="changeToUpper(this)" onkeyPress="return getSpecialCode(this)">
                   
                    </td>
                  <td  width="262" >Country 
                    Id:<font color="#FF0000">*</font><br>
                    <input type='text' class='text' name="countryId" size="5" maxlength="2" onBlur="changeToUpper(this)" onKeypress="return getSpecialCode(this)"> <input type="button" value="..." name="CountryLOV" onClick="showCountryLOV()" class='input'>
                    </td>
                  <td width=50%>Shipment
              Mode:<font color="#FF0000">*</font><br>
			  &nbsp;&nbsp;
               <input type="hidden" name="shipmentMode" value=0 class="select">
			   Air
			   <input type="checkbox" name="Air" value="1" onClick="setShipmentMode(this)">
			   &nbsp;&nbsp;Sea
			   <input type="checkbox" name="Sea" value="2" onClick="setShipmentMode(this)">
			   &nbsp;&nbsp;Truck
			   <input type="checkbox" name="Truck" value="4" onClick="setShipmentMode(this)">
              
            </td>
                </tr>
              </table>
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
                <tr class='denotes'> 
                  <td valign="top"  > 
                   <font color="#FF0000">*</font>Denotes 
                      Mandatory <br>
                    Note : Location Ids to conform to IATA Rule No 1.2.3
                  </td>
                  
                  <td valign="top"  align="right">
                     <input type="submit" value="Submit" name="jbt_Test" class='input'>
					 <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'>
                      <input type="hidden" name=Operation value="<%=param%>">
                     
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
 }
 catch(Exception e)
  {
	//Logger.info(FILE_NAME,"Error in ETLocationAdd.jsp file : "+e.toString());
  logger.info(FILE_NAME+"Error in ETLocationAdd.jsp file : "+e.toString());
  } 
%>	