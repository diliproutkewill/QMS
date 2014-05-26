<%--
     * @file : ETIATARatePreInfo.jsp
	 * @author : Srivegi
	 * @date : 23-03-1005
	 * @version : 1.8 
--%>

<%@page import="java.util.ArrayList,
                com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel,
				com.foursoft.esupply.common.util.ESupplyDateUtility,
				java.sql.Timestamp,
				org.apache.log4j.Logger"%>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<%!
  private static Logger logger = null;
%>

<%
   String FILE_NAME ="ETIATARatePreInfo.jsp";
   logger  = Logger.getLogger(FILE_NAME);	
   		 	
   String operation	   =  ""; 
   operation  		 =  request.getParameter("Operation");
   String		dateFormat				=	loginbean.getUserPreferences().getDateFormat();
  // Logger.info(FILE_NAME,"=.....................................dateFormat is " + dateFormat);
   ESupplyDateUtility	eSupplyDateUtility	=	new ESupplyDateUtility();
   eSupplyDateUtility.setPattern(dateFormat);
   IATADtlModel iataDtls = null;
  
   String validDateFrom ="";
   String flag="";
   String date = null;
   String readOnly = "";
   String disabled  =""; 
   String readOnlyV = "";
   String disabledV  =""; 
   String pFlag= "";
   String errorCodes="";
   if(session.getAttribute("errorCodes")!=null)
   {
	   errorCodes = (String)session.getAttribute("errorCodes");
	   //Logger.info(FILE_NAME,"....errorCodes............"+errorCodes);
   }
   if(session.getAttribute("displayDtls")!=null)
	{
	    iataDtls = (IATADtlModel)session.getAttribute("displayDtls");
          //Logger.info(FILE_NAME,"=....................................iataDtls " + iataDtls.getRateType());
		  pFlag = iataDtls.getRateType();

  	}
   //Logger.info(FILE_NAME,"=....................................operation " + operation);
   if( operation.equalsIgnoreCase("ModifyDtls") || operation.equalsIgnoreCase("ViewDtls") || operation.equalsIgnoreCase("DeleteDtls"))
   {    
	   if(errorCodes.equals(""))
	     {
		   readOnly = "readOnly" ;
		   disabled = "disabled" ;
		 }
       else
	    {
		       disabled ="";
	   		   readOnly = "" ;
	    }
	   
   }
  if( operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("ViewDtls") || operation.equalsIgnoreCase("DeleteDtls") || operation.equalsIgnoreCase("Delete"))
  {
	   readOnlyV = "readOnly" ;
	   disabledV = "disabled" ;
  }
%>
<html>
<head>
<title>IATA Rate Master</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="ESFoursoft_css.jsp" type="text/css">
<jsp:include page="ETDateValidation.jsp" >
	<jsp:param name="format" value="<%=dateFormat%>"/>
</jsp:include>
<script>

function showTerminalLOV(obj)
{ 
 	var whereClause = "";
	var modeOfShipment = "";
 	shipmentMode=1;
	
   <%if (operation.equalsIgnoreCase("ModifyDtls") || operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("Delete") || operation.equalsIgnoreCase("ViewDtls") || operation.equalsIgnoreCase("DeleteDtls")){%>
       var Url='etrans/ETCLOVTerminalIds.jsp?wheretoset='+obj.name+'&searchString='+obj.value+'&shipmentMode=7&module=IATAModify&dest='+document.forms[0].destinationGatewayId.value+'&serviceLevel='+document.forms[0].serviceLevelId.value+'&origin='+document.forms[0].originGatewayId.value;
	
<%}else{%>
 	var Url='etrans/ETCLOVTerminalIds.jsp?wheretoset='+obj.name+'&searchString='+obj.value+'&shipmentMode=7&module=IATAAdd';
<%}%>
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
function showServiceLevelLOV()
{   
	shipmentMode=1;
     <%if (operation.equalsIgnoreCase("ModifyDtls") || operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("Delete") || operation.equalsIgnoreCase("ViewDtls") || operation.equalsIgnoreCase("DeleteDtls")){%>
		 showTerminalLOV(document.forms[0].serviceLevelId);
		 <%}else{%>

     
 	var Url='etrans/ETCLOVServiceLevelIds.jsp?searchString='+document.forms[0].serviceLevelId.value+'&shipmentMode='+shipmentMode;
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
	<%}%>
}
function showCurrencyIdLOV()
{
 	var Url='etrans/ETCLOVCurrencyIds.jsp?searchString='+document.forms[0].currencyId.value;
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var Win=open(Url,'Doc',Features);
}
function validate()
{ 
	checkForUpperCase();//@@ This Line Added By Ravi Kumar to Resolve the Issue SPETI-6571 on 29-04-2005
	<% 
	if(operation.equalsIgnoreCase("Delete") || operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View"))
	{ %>

		
    return (checkZeros(document.forms[0].originGatewayId,"Origin Id") &&
           checkZeros(document.forms[0].destinationGatewayId,"Destination Id") &&
		   checkSame(document.forms[0].originGatewayId,document.forms[0].destinationGatewayId) &&
		   checkZeros(document.forms[0].serviceLevelId,"Service Level Id"));
	
<%}else{ %>
			 if(document.forms[0].validFrom.value!="" && document.forms[0].validUpto.value!="")
			 {	if(!chkFromToDate(document.forms[0].validFrom.value, document.forms[0].validUpto.value))
				{
					alert("Please Enter Upto date  Greater than or equal to From Date.");
					return false;
				}
			 }
            return (checkZeros(document.forms[0].originGatewayId,"Origin Id") &&
           checkZeros(document.forms[0].destinationGatewayId,"Destination Id") &&
		   checkSame(document.forms[0].originGatewayId,document.forms[0].destinationGatewayId) &&
		   checkZeros(document.forms[0].serviceLevelId,"Service Level Id") &&
		   checkZeros(document.forms[0].currencyId,"Currency Id") &&
	       checkZeros(document.forms[0].validFrom,"validFrom Date")  &&
    	   checkZeros(document.forms[0].validUpto,"validUpto Date")  );
    <%}%>
}
//@@ Added By Ravi Kumar to Resolve the Issue SPETI-6571 on 29-04-2005
function checkForUpperCase()
{
	var all =   document.all;
	for(var i=0;i<all.length;i++)
	{
		if(all[i].type=='text')
		{
			all[i].value = all[i].value.toUpperCase();
		}
	}
}
//@@ 29-04-2005
function upper(object)
{
  object.value = object.value.toUpperCase();
}
function checkZeros(obj,elementLabel)
{ 
   if(obj.value.length == 0)
   {	  
	  alert("Please enter " + elementLabel ); 
      obj.focus();
      return false;
   }
  return true;
}
function checkSame(input1,input2)
{
	if(input1.value==input2.value)
	{
			alert("Origin and Destination Id cannot be same");
			input2.focus();
			return false;
	}
	return true;
 }
 function specialCharFilter(input) 
	{
		s = input.value;
		filteredValues = "''~!@#$%^&*()_-+=|\:;<>,.?"+'"';		
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++) 
		{  
			var c = s.charAt(i);
			if ( filteredValues.indexOf(c) == -1 ) 
					returnString += c.toUpperCase();
			else
			{
				alert("Please do not enter special characters for "+label);
				input.focus();
				return false;
			}
		}
		return true;
	}
  function display(input)
  {
	var data = "";
	
	if(input.value=="SLAB" || input=="SLAB")
	{
     	data+= "		<td width='242' >Weight Class:<br>";
		data+= "		<select size='1' name='wgtClass' class='select' <%=disabledV%> >";
		data+= "		<option <%if(iataDtls!=null && iataDtls.getWeightClass().equals("G")){%> selected <%}else{%> hai <%}%>";
		data+=         " value='G'>General</option>";
		data+= "		<option <%if(iataDtls!=null && iataDtls.getWeightClass().equals("W")){%> selected <%}else{%>hello<%}%>";
		data+=         " value='W'>Weight Scale</option>";
		data+= "		</select>";
		data+= "		</td>";
//@@ Modified By Ravi KUmar to resolve the issue SPETI-6568 on 03-05-05
<%
		  if((operation.equalsIgnoreCase("Add") || errorCodes.equals("") ) && !operation.equalsIgnoreCase("Modify") && !operation.equalsIgnoreCase("View") && !operation.equalsIgnoreCase("Delete"))
		  { 
%>
		types.innerHTML = data;
<%
		  }
%>
//@@ 03-05-05
    }
	else
	  {
	 	data+= "		<td width='242' >Weight Class:<br>";
		data+= "		<select size='1' name='wgtClass' class='select' <%=disabledV%> >";
		data+= "		<option value='G'>General</option>";
		data+= "		</select>";
		data+= "		</td>";
//@@ Modified By Ravi KUmar to resolve the issue SPETI-6568 on 03-05-05
<%
		  if((operation.equalsIgnoreCase("Add") || errorCodes.equals("") ) && !operation.equalsIgnoreCase("Modify") && !operation.equalsIgnoreCase("View") && !operation.equalsIgnoreCase("Delete"))
		  { 
%>
		types.innerHTML = data;
<%
		  }
%>
//@@ 03-05-05		
	  }
	
  }
</script>

</head>
<body bgcolor="#FFFFFF" text="#000000"  onload="display('<%=pFlag%>');">
<form  method="post" action="ETIATARatesController" onSubmit='return validate()'>
  <table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td bgcolor="#FFFFFF" valign="top" width="800"> 
        <table border="0" width="800" class='formlabel'>
          <tr > 
            <td width="100%"  colspan="3"><b>IATA Rate Master</b><b> - <%=operation%> </b></td>
         <td align=right>SP1020712</td></tr></table>
		 
	     
     
         <%		if(!errorCodes.equals(""))          
		{
%>        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr class='formdata'><td colspan="4"><font color="#ff0000"><%=errorCodes%></font></td></tr>
<%		}
      
%>        
           <table border="0" width="800" cellspacing="1" cellpadding="4">
          <tr class='formdata'> 
            <td width="242" >Origin Location: <font color="#FF0000">*</font><br>
              <input type='text' class='text' name="originGatewayId" size="16" value='<%=iataDtls!=null?iataDtls.getOriginLocation():""%>' <%=readOnly%> onBlur='upper(this)' onKeyPress='return specialCharFilter(this)'   >
              <input type="button" value="..." name="OriginLOV"  class='input' <%=disabled%>  onClick='showTerminalLOV(document.forms[0].originGatewayId)' >
			   
            </td>
            <td width="258" >Destination Location: <font color="#FF0000">*</font><br>
              <input type='text' class='text' name="destinationGatewayId" size="16" value='<%=iataDtls!=null?iataDtls.getDestLocation():""%>' onBlur='upper(this)' <%=readOnly%> onKeyPress='return specialCharFilter(this)' >
              <input type="button" value="..." name="DestinationLOV" class='input' <%=disabled%>  onClick='showTerminalLOV(document.forms[0].destinationGatewayId)'  >
            </td>
            <td width="259" >Service Level: <font color="#FF0000">*</font><br>
              <input type='text' class='text' name="serviceLevelId" size="18" maxlength="5" value='<%=iataDtls!=null?iataDtls.getServiceLevel():""%>' <%=readOnly%> onBlur='upper(this)' onKeyPress='return specialCharFilter(this)' >
              <input type="button" value="..." name="ServiceLevelLOV" class='input'  <%=disabled%>  onClick='showServiceLevelLOV()' >
            </td>
          </tr>
		  <% 
		  //Logger.info(FILE_NAME,"=...................wwdssssssssswwwwwww..................Operation is " + operation);
		  if((operation.equalsIgnoreCase("Add") || errorCodes.equals("") ) && !operation.equalsIgnoreCase("Modify") && !operation.equalsIgnoreCase("View") && !operation.equalsIgnoreCase("Delete"))
		  
		  { %>
            
          <tr class='formdata'> 
            <td width="258" >Valid From: (<%=dateFormat%>)<font color="#FF0000">*</font><br>
			  <%
		          if (iataDtls!=null && iataDtls.getValidFromDate()!=null)
				  {  
			           //Logger.info(FILE_NAME,"=...................wwdssssssssswwwwwww..................iataDtls.getValidFromDate() is " + iataDtls.getValidFromDate());
					   Timestamp dateUpto= iataDtls.getValidFromDate();
        			   validDateFrom = eSupplyDateUtility.getDisplayString(dateUpto);
					   //Logger.info(FILE_NAME,"=...................wwdssssssssswwwwwww..................validDateFrom.length is " + validDateFrom);
                       date = validDateFrom; 
				  }
		  %>
              <input type="text" class='text' name="validFrom" size="12" <%=readOnlyV%> maxlength=10  value='<%=date!=null?date:""%>' onBlur="dtCheck(this);">
              <input type="button"  value="..." onClick="newWindow('validFrom','0','0','')" name="button2" <%=disabledV%> class="input">
            </td>

			<td width="258" >Valid Upto: (<%=dateFormat%>)<font color="#FF0000">*</font><br>
				  <%
		          if (iataDtls!=null && iataDtls.getValidUptoDate()!=null)
				  {  
				       Timestamp dateUpto= iataDtls.getValidUptoDate();
        			   validDateFrom = eSupplyDateUtility.getDisplayString(dateUpto);
					   date = validDateFrom; 
				  }
		  %>
              <input type="text" class='text' name="validUpto" size="12" <%=readOnlyV%> maxlength=10  value='<%=date!=null?date:""%>' onBlur="dtCheck(this);">
              <input type="button"  value="..." onClick="newWindow('validUpto','0','0','')" name="button2" <%=disabledV%> class="input">
            </td>

			<td width="258" > Currency:<font color="#FF0000">*</font><br>
              <input type='text' class='text' name="currencyId" size="4"  maxlength="3" value='<%=iataDtls!=null?iataDtls.getCurrencyId():""%>' onBlur='upper(this)' <%=readOnlyV%> onKeyPress='return specialCharFilter(this)' >                                             
              <input type="button" value="..." name="CurrencyLOV" class='input' <%=disabledV%> onClick='showCurrencyIdLOV()' >
            </td>
			<tr class='formdata'>
			<td width="258" >Rate Type: <font color="#FF0000">*</font><br>
              <select size="1" name="processFlag" class='select' <%=disabled%> onChange="display(this)">
				  <%//Logger.info(FILE_NAME,"////....======================="+pFlag);
			  %>
                <option <%if(pFlag.equals("FLAT")){%> selected <%}else{}%> value="FLAT">Flat</option>
                <option <%if( pFlag.equals("PIVOT")){%> selected <%}else{}%> value="PIVOT">Pivot</option>
                <option <%if( pFlag.equals("SLAB")){%> selected <%}else{}%> value="SLAB">Slab</option>
              </select>
            </td>
            <td>
				 <span id=types style='position:relative;'></span>
            </td>
			<td width="259" >UOM: <br>
                <select size="1" name="uom" class='select' <%=disabledV%> >
                <option <%if(iataDtls!=null && iataDtls.getUOM()=="KG"){%> selected <%}else{}%> value="KG">KG</option>
				<option <%if(iataDtls!=null && iataDtls.getUOM()=="LB"){%> selected <%}else{}%> value="LB">LB</option>
                </select>
            </td>
          </tr>
	  <%}%>
		  <table border="0" width="800" cellspacing="1" cellpadding="4">
          <tr>
				 
		  <td><font color="#FF0000">* </font>Denotes Mandatory </td>
          <td align="right"> 
              <input type="submit" value="Next&gt;&gt;" name="B5" class='input' >
			  <input type="reset" class='input' value="Reset" name="Reset">
			  
   			  <%
			  if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("ModifyDtls"))
			  {
				  //System.out.println("........errorCodes........"+errorCodes+"....operation......"+operation);
				  if(!errorCodes.equals("")  ) {%>
               		  <input type="hidden" name="Operation" value="ModifyDtls" >    
					  <input type="hidden" name="processFlag" value="<%=pFlag!=null?pFlag:""%>" >

               <%}else if (operation.equalsIgnoreCase("ModifyDtls")){%>
					   <input type="hidden" name="Operation" value="Modify" >
                       <input type="hidden" name="subOperation" value="Next" >
				       <input type="hidden" name="IATAMasterId" value="<%=iataDtls.getIATAMasterId()%>" >
					   <input type="hidden" name="processFlag" value="<%=pFlag!=null?pFlag:""%>" >
				       <input type="hidden" name="dateFormat" value="<%=dateFormat%>" >
					 
              <%}else if(operation.equalsIgnoreCase("Modify")){%>
                      <input type="hidden" name="Operation" value="ModifyDtls" >
				      <input type="hidden" name="subOperation" value="Next" >
				      <input type="hidden" name="processFlag" value="<%=pFlag!=null?pFlag:""%>" >
				      <input type="hidden" name="dateFormat" value="<%=dateFormat%>" >

				  <%}
			  }
			  if(operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("ViewDtls"))
			  {
				  // for view
				  if(!errorCodes.equals("")  ) {%>
               		  <input type="hidden" name="Operation" value="ViewDtls" >    
					  <input type="hidden" name="processFlag" value="<%=pFlag!=null?pFlag:""%>" >   

               <%}else if (operation.equalsIgnoreCase("ViewDtls")){%>
					   <input type="hidden" name="Operation" value="View" >
                       <input type="hidden" name="subOperation" value="Next" >
				       <input type="hidden" name="IATAMasterId" value="<%=iataDtls.getIATAMasterId()%>" >
					   <input type="hidden" name="processFlag" value="<%=pFlag!=null?pFlag:""%>" >
				       <input type="hidden" name="dateFormat" value="<%=dateFormat%>" >
					 
               <%}else if(flag.equalsIgnoreCase("") && operation.equalsIgnoreCase("View")){%>
                       <input type="hidden" name="Operation" value="ViewDtls" >
		          	   <input type="hidden" name="subOperation" value="Next" >
				       <input type="hidden" name="processFlag" value="<%=pFlag!=null?pFlag:""%>" >
				       <input type="hidden" name="dateFormat" value="<%=dateFormat%>" >

				  <%}
			  }
				  // for delete
			  else if(operation.equalsIgnoreCase("Delete") || operation.equalsIgnoreCase("DeleteDtls"))
			  {
				  if(!errorCodes.equals("")  ) {%>
               		  <input type="hidden" name="Operation" value="DeleteDtls" >    
					  <input type="hidden" name="processFlag" value="<%=pFlag!=null?pFlag:""%>" >

               <%}else if (operation.equalsIgnoreCase("DeleteDtls")){%>
					   <input type="hidden" name="Operation" value="Delete" >
                       <input type="hidden" name="subOperation" value="Next" >
				       <input type="hidden" name="IATAMasterId" value="<%=iataDtls.getIATAMasterId()%>" >
					   <input type="hidden" name="processFlag" value="<%=pFlag!=null?pFlag:""%>" >
				       <input type="hidden" name="dateFormat" value="<%=dateFormat%>" >
					 
             <%}else if(flag.equalsIgnoreCase("") && operation.equalsIgnoreCase("Delete")){%>
                       <input type="hidden" name="Operation" value="DeleteDtls" >
				       <input type="hidden" name="subOperation" value="Next" >
				       <input type="hidden" name="processFlag" value="<%=pFlag!=null?pFlag:""%>" >
				       <input type="hidden" name="dateFormat" value="<%=dateFormat%>" >

				  <%}
				  
			  }	   
			  else{%>

				 <input type="hidden" name="Operation" value="<%=operation%>" >
				 <input type="hidden" name="processFlag" value="<%=operation%>" >
				 <input type="hidden" name="dateFormat" value="<%=dateFormat%>" >
				 <input type="hidden" name="subOperation" value="Next" >

			    <%}%>
			</td>
			</tr>
			</table>
		  
</table>
</form>
</body>
</html>
