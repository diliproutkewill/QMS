<%--
     * @file : ETIATARateLaneInfo.jsp
	 * @author : Srivegi
	 * @date : 23-03-1005
	 * @version : 1.8 
--%>

<%@page import="com.foursoft.etrans.setup.IATARateMaster.java.IATADtlModel,
                com.foursoft.etrans.setup.IATARateMaster.java.IATAChargeDtlsModel,
				java.util.ArrayList,
				org.apache.log4j.Logger"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>


<%!
  private static Logger logger = null;
%>

<%
	String FILE_NAME ="ETIATARateLaneInfo.jsp";
  
  logger  = Logger.getLogger(FILE_NAME);	
  
	String originId 		= null;
	String destinationId	= null;
	String serviceLevel		= null;
	String contractType		= null;
	String currencyId		= null;
	String uom				= null;
	String operation		= null;
	String weightClass		= null;

	String[] rateValues = new String[6];
	String[] slabValues1 = new String[11];
	String[] rateValues1 = new String[11];

	//Logger.info(FILE_NAME,"===================ETIATARateLaneInfo=========================");
	
    IATADtlModel  rateDtls    =  null;         
	IATAChargeDtlsModel  chargeDtlsModel    =  null; 

	operation	  = request.getParameter("Operation");	
	originId  	  = request.getParameter("originGatewayId");
	destinationId = request.getParameter("destinationGatewayId");
	serviceLevel  = request.getParameter("serviceLevelId");
	 ArrayList rateDtlsModel  = new ArrayList();
	 String readOnlyV = "";
   String disabledV  =""; 
	
	if(request.getParameter("processFlag")!=null)
	   contractType = request.getParameter("processFlag");
    else
	   contractType ="FLAT";
	   //Logger.info(FILE_NAME,"===================operation========================="+operation);
	
	
  try
  {	
	
	
  if(session.getAttribute("displayChargeData")!=null)
	  {
		  rateDtlsModel =  (ArrayList)session.getAttribute("displayChargeData");
	  }

  if( operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("ViewDtls") || operation.equalsIgnoreCase("DeleteDtls") || operation.equalsIgnoreCase("Delete") || operation.equalsIgnoreCase("MAWBView"))
     {
	   readOnlyV = "readOnly" ;
	   disabledV = "disabled" ;
     }
    

%>

<html>
<head>
<title>Flat - Rate Info</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="ESFoursoft_css.jsp" type="text/css">
<script>
var operation    = '<%=operation%>';
var weightClass  = '<%=weightClass!=null?weightClass:""%>' 
var contractType = '<%=contractType%>';
function submitForm()
{
  
 
 <% if( contractType.equalsIgnoreCase("flat")) { %>
	  if(!validateFlatRates())
	      return false;
   <%}%>   


  <% if( contractType.equalsIgnoreCase("Slab")) { %>
        if(!validateRates())
           return false;
	 <%}%>   

 
  <% if( contractType.equalsIgnoreCase("Pivot")) { %>
      if(!validatePivotRates())
	  return false;
  <%}%>   
  document.forms[0].submit();
}
function  valueGreaterThanZero(objVal,mesg1,mesg2)
{  
    if(objVal.length == 0 || isNaN(objVal)==true)
	{
	     alert(mesg1);
	     return false;
    }
	if(parseInt(objVal) < 0)
	{
	     alert(mesg2);
	     return false;
	}
	return true;
}
function validateRates()
{    
  		 for(var i=0;i<10;i++)
         {
				
					if(i == 0)
					{
						if(document.forms[0].rateValues[i].value == 0 || document.forms[0].rateValues[i].value == "")
						{
							alert('Please Enter MIN Rate Value ');
							document.forms[0].rateValues[i].focus();
							return false;
						}
					}
					else if(i == 1)
					{
						if(document.forms[0].slabValues[i-1].value == 0 || document.forms[0].slabValues[i-1].value == "")
						{
							alert('Please Enter Negative Value For Slab at Column 2 ');
							document.forms[0].slabValues[i-1].focus();
							return false;
						}
						else
						{
							if(isNaN(document.forms[0].slabValues[i-1].value) || parseInt(document.forms[0].slabValues[i-1].value) >= 0)
							{
								alert('Please Enter Negative Value For Slab at Column 2 ');
								document.forms[0].slabValues[i-1].focus();
								return false;
							}
							if(document.forms[0].rateValues[i].value == 0 || document.forms[0].rateValues[i].value == "")
							{
								alert('Please Enter Rate Value at column 2 ');
								document.forms[0].rateValues[i].focus();
								return false;
							}
						}
					}
					else if(i == 2)
					{
						if(document.forms[0].slabValues[i-1].value == 0 || document.forms[0].slabValues[i-1].value == "")
						{
							alert('Please Enter Positive Value For Slab at Column 2 ');
							document.forms[0].slabValues[i-1].focus();
							return false;
						}
						else
						{
							if(isNaN(document.forms[0].slabValues[i-1].value) || parseInt(document.forms[0].slabValues[i-1].value) <= 0)
							{
								alert('Please Enter Positive Value For Slab at Column 2 ');
								document.forms[0].slabValues[i-1].focus();
								return false;
							}
							if(Math.abs(document.forms[0].slabValues[0].value)  != Math.abs(document.forms[0].slabValues[i-1].value))
							{
								 alert("Please Enter Equal Possitive Amount for Slab at Column No. "+(i+1));
								 document.forms[0].slabValues[i-1].focus();
								 return false;				 
							}
							if(document.forms[0].rateValues[i].value == 0 || document.forms[0].rateValues[i].value == "")
							{
								alert('Please Enter Rate Value at column 3 ');
								document.forms[0].rateValues[i].focus();
								return false;
							}
						}
					}
					else
					{
						if(document.forms[0].slabValues[i-1].value != 0 || document.forms[0].slabValues[i-1].value != "")
						{
							if(isNaN(document.forms[0].slabValues[i-1].value) || parseInt(document.forms[0].slabValues[i-1].value) <= 0)
							{
								alert('Please Enter Positive Value For Slab at Column '+(i+1));
								document.forms[0].slabValues[i-1].focus();
								return false;
							}
							if(Math.abs(document.forms[0].slabValues[i-1].value)  <= Math.abs(document.forms[0].slabValues[i-2].value))
							{
								 alert("Please Enter Slab at Column No. "+(i+1)+" is should be greate than Slab at Column No. "+i);
								 document.forms[0].slabValues[i-1].focus();
								 return false;				 
							}
							if(document.forms[0].rateValues[i].value == 0 || document.forms[0].rateValues[i].value == "")
							{
								alert('Please Enter Rate Value at column '+(i+1));
								document.forms[0].rateValues[i].focus();
								return false;
							}
						}
					}
				}
			
            
        return true;  
}
function validateFlatRates()
{ 

   if(document.forms[0].minRate.value=="")
   {
	 alert("Please enter IATA Min Rate Value ");
	 document.forms[0].minRate.focus();
	 return false;
   }
   else if(document.forms[0].flatRate.value=="")
   {
     alert("Please enter IATA Flat Rate Value ");
	 document.forms[0].flatRate.focus();
	 return false;
   }
    
    return true;
}
function validatePivotRates()
{
  if(document.forms[0].pivotWeight.value.length ==0 || document.forms[0].pivotWeight.value.length =="")
  {
    alert("Please Enter IATA Pivot  Weight Value");
    document.forms[0].pivotWeight.focus();
	return false;
  }
  else if(document.forms[0].pivotWeightRate.value.length ==0 || document.forms[0].pivotWeightRate.value.length =="")
  {
     alert("Please Enter IATA  Pivot Weight Rate  Value");
     document.forms[0].pivotWeightRate.focus();
	 return false;
  }
  else if(document.forms[0].overPivotWeight.value.length==0 || document.forms[0].overPivotWeight.value.length=="")
  {
     alert("Please Enter IATA  Over Pivot Weight Value");
     document.forms[0].overPivotWeight.focus();
	 return false;
  }
  else if(document.forms[0].overPivotRate.value.length==0 || document.forms[0].overPivotRate.value.length=="")
  {
     alert("Please Enter  IATA  Over Pivot Rate Value");
     document.forms[0].overPivotRate.focus();
	 return false;
  }
  else if( (parseInt(document.forms[0].pivotWeight.value) > parseInt(document.forms[0].overPivotWeight.value)) || parseInt(document.forms[0].pivotWeight.value) == parseInt(document.forms[0].overPivotWeight.value))
  {
    
     alert("Please Enter Over IATA  Pivot Weight Value More than IATA  Pivot Weight");
     document.forms[0].overPivotWeight.focus();
	 return false;
	 
  }
  else if(document.forms[0].maximumCharge.value.length==0 || document.forms[0].maximumCharge.value.length=="")
  {
     alert("Please Enter IATA  Maximum Charge");
     document.forms[0].maximumCharge.focus();
	 return false;
  }
  else if(document.forms[0].overFlowRate.value.length==0 || document.forms[0].overFlowRate.value.length=="")
  {
     
     document.forms[0].overFlowRate.value=0;
	 
  }
  
  return true;
}

function getNumberCode(input)    
{
	if(input=="Y")
    { 
		if((event.keyCode==45) )
		 return true;
		if((event.keyCode < 46 || event.keyCode==47 || event.keyCode > 57) )
			return false;
		
    }
	else if(event.keyCode!=13)
	{
		
		if((event.keyCode < 46 || event.keyCode==47 || event.keyCode > 57) )
			return false;
	}
	return true;
}
</script>

</head>
<!-- @@ Modified By Ravi Kumar to Resolve the Issue SPETI-6568 on 03-05-05-->
<body> 
<form name="form1" method="post" action="ETIATARatesController">
<!-- @@ 03-05-05-->
 <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td bgcolor="#FFFFFF" valign="top">
	  <table border="0" width="100%"  cellspacing="1" cellpadding="4">
	<tr class='formlabel'><td>
      <table border="0" width="100%" >
			<tr class='formlabel'> 
			<td colspan="6"><b>IATA Rate Master - <%=operation%></b></td>
			<td align=right>SP1020711</td></tr></table></td></tr>
	   </table>
	   <table border="0" width="100%"  cellspacing="1" cellpadding="4">
	   <tr class='formdata'> 
       <td colspan="6">&nbsp;</td>
       </tr>
       </table>
     
<%  
		   if(contractType.equalsIgnoreCase("Flat"))
            {  if(session.getAttribute("displayChargeData")!=null)
			  {
			 // Logger.info(FILE_NAME,"=======slabbbbbbb==============");
			  for (int i=0;i<rateDtlsModel.size();i++)
			   {  rateDtls =(IATADtlModel)rateDtlsModel.get(i);	
                  chargeDtlsModel = rateDtls.getRateDtls();
				  rateValues1[i]= chargeDtlsModel.getRateValue();
                  slabValues1[i]= chargeDtlsModel.getSlabValue(); 
				  //Logger.info(FILE_NAME,"====rateValues1[i]================="+slabValues1[i]);
               }
		    }
%>             
         <table border="0" width="100%" cellspacing="1" cellpadding="4">
         <tr class='formdata'> 
         <td colspan="6"><b>IATA -Flat Rate Information</b></td>
         </tr>
         <tr class='formheader'> 
         <td width="33%" align="center"><b>Slab</b></td>
         <td width="33%" align="center"><b>MIN</b> <font color="ff0000">*</font></td>
         <td width="34%" align="center"><b>FLAT</b> <font color="ff0000">*</font></td>
         </tr>
 
              <tr class='formdata'> 
              <td width="33%" align="center"><b>Rates</b></td>
              <td width="33%" align="center"> 
              <input type='text' class='text' name="minRate" size="20" maxlength="8" onkeyPress='return getNumberCode();' <%=readOnlyV%> value='<%=slabValues1[0]!=null?slabValues1[0]:""%>'   >
              </td>
              <td width="34%" align="center"> 
              <input type='text' class='text' name="flatRate" size="20" onkeyPress='return getNumberCode();' maxlength="8" <%=readOnlyV%> value='<%=rateValues1[0]!=null?rateValues1[0]:""%>'  >
              </td>
              </tr>
	  </table>
              
<%		  }
          else if(contractType.equalsIgnoreCase("Slab"))
          { 
			  if(session.getAttribute("displayChargeData")!=null)
			  {
			 // Logger.info(FILE_NAME,"=======slabbbbbbb==============");
			  for (int i=0;i<rateDtlsModel.size();i++)
			    { rateDtls =(IATADtlModel)rateDtlsModel.get(i);	
                  chargeDtlsModel = rateDtls.getRateDtls();
				  rateValues1[i]= chargeDtlsModel.getRateValue();
                  slabValues1[i]= chargeDtlsModel.getSlabValue(); 
				  //Logger.info(FILE_NAME,"====================="+chargeDtlsModel.getRateValue());
                 }
			 }
%>      
	       
              <table border="0" width="100%" >
              <tr class='formdata'> 
              <td colspan="6"><b>IATA - Slab Rate Information</b></td>
              </tr>
              <tr class='formdata'> 
              <td width="10%" align="center"><b>Slab</b></td>
              <td width="10%" align="center"><b>MIN</b></td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="slabValues" size="7" id='1' <%=readOnlyV%> onkeyPress='return getNumberCode("Y");'  value='<%=slabValues1[0]!=null?slabValues1[0]:""%>' >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="slabValues" size="7"  id='2' <%=readOnlyV%> onkeyPress='return getNumberCode();'  value='<%=slabValues1[1]!=null?slabValues1[1]:""%>' >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="slabValues" size="7" id='3' <%=readOnlyV%> onkeyPress='return getNumberCode();'  value='<%=slabValues1[2]!=null?slabValues1[2]:""%>'  >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="slabValues" size="7"  id='4' <%=readOnlyV%> onkeyPress='return getNumberCode();'  value='<%=slabValues1[3]!=null?slabValues1[3]:""%>' >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="slabValues" size="7"  id='5' <%=readOnlyV%> onkeyPress='return getNumberCode();'  value='<%=slabValues1[4]!=null?slabValues1[4]:""%>' >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="slabValues" size="7"  id='6'  <%=readOnlyV%> onkeyPress='return getNumberCode();'  value='<%=slabValues1[5]!=null?slabValues1[5]:""%>' >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="slabValues" size="7"  id='7'  <%=readOnlyV%> onkeyPress='return getNumberCode();'  value='<%=slabValues1[6]!=null?slabValues1[6]:""%>' >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="slabValues" size="7"  id='8' <%=readOnlyV%> onkeyPress='return getNumberCode();'  value='<%=slabValues1[7]!=null?slabValues1[7]:""%>' >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="slabValues" size="7"  id='9' <%=readOnlyV%> onkeyPress='return getNumberCode();'  value='<%=slabValues1[8]!=null?slabValues1[8]:""%>' >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="slabValues" size="7" id='10' <%=readOnlyV%> onkeyPress='return getNumberCode();' value='<%=slabValues1[9]!=null?slabValues1[9]:""%>' >
              </td>
              </tr>
              <tr class='formdata'> 
              <td width="10%" align="center"><b>Rates</b></td>
              
              <td width="8%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" <%=readOnlyV%> value='<%=rateValues1[0]!=null?rateValues1[0]:""%>'  >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" value='<%=rateValues1[1]!=null?rateValues1[1]:""%>'  <%=readOnlyV%> onkeyPress='return getNumberCode();' >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" value='<%=rateValues1[2]!=null?rateValues1[2]:""%>'  <%=readOnlyV%> onkeyPress='return getNumberCode();'>
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" value='<%=rateValues1[3]!=null?rateValues1[3]:""%>' <%=readOnlyV%> onkeyPress='return getNumberCode();'>
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" value='<%=rateValues1[4]!=null?rateValues1[4]:""%>'  <%=readOnlyV%> onkeyPress='return getNumberCode();'>
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" value='<%=rateValues1[5]!=null?rateValues1[5]:""%>'  <%=readOnlyV%> onkeyPress='return getNumberCode();' >
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" value='<%=rateValues1[6]!=null?rateValues1[6]:""%>' <%=readOnlyV%> onkeyPress='return getNumberCode();'>
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" value='<%=rateValues1[7]!=null?rateValues1[7]:""%>' <%=readOnlyV%> onkeyPress='return getNumberCode();'>
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" value='<%=rateValues1[8]!=null?rateValues1[8]:""%>' <%=readOnlyV%> onkeyPress='return getNumberCode();'>
              </td>
              <td width="8%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" value='<%=rateValues1[9]!=null?rateValues1[9]:""%>' <%=readOnlyV%> onkeyPress='return getNumberCode();'>
              </td>
			  <td width="10%" align="center"> 
              <input type='text' class='text' name="rateValues" size="7" <%=readOnlyV%> value='<%=rateValues1[10]!=null?rateValues1[10]:""%>'  onkeyPress='return getNumberCode();'>
              </td>
              </tr>
			  
			  <tr class='formdata'> 
              <td colspan="12" > &nbsp;
              </td>
              </tr>
			  </table>
			  
              
<%		  }
         else if(contractType.equalsIgnoreCase("Pivot"))	        
		 {        
%>        
            <table border="0" width="100%" cellspacing="1" cellpadding="4">
            <tr class='formdata'> 
            <td colspan="3"><b></b></td>
            </tr>
            <tr class='formheader' align="center"> 
            <td width="15%">&nbsp;</td>
            <td width="15%">Pivot Weight</td>
            <td width="15%" >Pivot Weight Rate</td>
            <td width="15%">Over Pivot Wt</td>
			<td width="15%" >Over Pivot Rate</td>
            <td width="15%">Over Flow Rate</td>
			<td width="15%">Maximum Charge</td>
            </tr>
            <tr class='formdata' align="center">
			<td width="15%">Agreed Rates</td> 
            <td width="15%"> 
			<%
	        for (int i=0;i<rateDtlsModel.size();i++)
			 {    rateDtls =(IATADtlModel)rateDtlsModel.get(i);	
                  chargeDtlsModel = rateDtls.getRateDtls();
				  rateValues[i]= chargeDtlsModel.getRateValue();
			     // Logger.info(FILE_NAME,"====================="+chargeDtlsModel.getRateValue());
             }
               
              
%>
            <input type='text' class='text' name="pivotWeight" size="15" maxlength="8" value='<%=rateValues[0]!=null?rateValues[0]:""%>' <%=readOnlyV%> onKeyPress='return getNumberCode()' onkeyPress='return getNumberCode();' >
            </td>
            <td width="15%"> 
            <input type='text' class='text' name="pivotWeightRate" size="15" maxlength="8" <%=readOnlyV%> value='<%=rateValues[1]!=null?rateValues[1]:""%>'  onkeyPress='return getNumberCode();'>
            </td>
            <td width="15%"> 
            <input type='text' class='text' name="overPivotWeight" size="15" maxlength="8" <%=readOnlyV%> value='<%=rateValues[2]!=null?rateValues[2]:""%>'  onkeyPress='return getNumberCode();'>
            </td>
			 <td width="15%"> 
            <input type='text' class='text' name="overPivotRate" size="15" maxlength="8" <%=readOnlyV%> value='<%=rateValues[3]!=null?rateValues[3]:""%>' onkeyPress='return getNumberCode();'>
            </td>
			 <td width="15%"> 
            <input type='text' class='text' name="overFlowRate" size="15" maxlength="8" value='<%=rateValues[4]!=null?rateValues[4]:""%>' <%=readOnlyV%> onkeyPress='return getNumberCode();'>
            </td>
			 <td width="15%"> 
            <input type='text' class='text' name="maximumCharge" size="15" maxlength="8" <%=readOnlyV%> value='<%=rateValues[5]!=null?rateValues[5]:""%>'  onkeyPress='return getNumberCode();'>
            </td> 
            </tr>
			 
			
            </table>
<%		}
%>      
      <table border="0" width="100%" cellspacing="1" cellpadding="4">
          <tr class='formdata'> 
            <td width="100%" align="right">
<%   
	if(operation.equalsIgnoreCase("MAWBView"))	 
	   {
		 if(contractType.equals("null"))
		 {  
%>         <tr class='formdata'>  
           <td align="CENTER" <b><font color='red' size=2><label >NO RATES DEFINED</label></b></font></td></tr>
	   <%}
%>      <tr>
	    <td width="100%" align="right">
        <input type="button" value="Continue" name="button" onClick="window.close()" class='input'>
	    <input type="hidden" name="processFlag" value='<%=contractType%>' > </td></tr>
<%	   }
    else if(operation.equalsIgnoreCase("View"))	              
	  {
%>        <input type="button" value="Continue" name="button" onClick="javascript:history.go(-2)" class='input'>
	      <input type="hidden" name="processFlag" value='<%=contractType%>' >
<%	  }
    else if(operation.equalsIgnoreCase("Delete")){	              
%>        <input type="submit" value="Delete" name="button" onClick='return submitForm()' class='input'>
	      <input type="hidden" name="Operation"   value='<%=operation%>' >
	      <input type="hidden" name="IATAMasterId"   value='<%=request.getParameter("masterId")%>' >
	      <input type="hidden" name="processFlag" value='<%=contractType%>' >
	      <input type="hidden" name="subOperation" value='Delete'>
<%}else{%>
        </a> 
		      <input type="submit" value="Submit" name="button" onClick='return submitForm()' class='input'>
              <input type="hidden" name="processFlag" value='<%=contractType%>' >
              <input type="hidden" name="Operation"   value='<%=operation%>' >
              <input type="hidden" name="contractType" value='<%=contractType%>' >
              <input type="hidden" name="subOperation" value='Submit'>
			  <input type="reset" class='input' value="Reset" name="Reset">
	<%}%>
            </td>
          </tr>
        </table>
      

</form>
</body>
</html>
<%
   }catch(Exception ex)
   {
      String nextNavigation = "ETCarrierContractsEnterId.jsp";
      if(operation.equals("Add"))
          nextNavigation   = "ETCarrierContractPreInfo.jsp"; 
      
      //Logger.error(FILE_NAME,"Exception in ETCarrierContractRateInfo.jsp " + ex.toString());
      logger.error(FILE_NAME+"Exception in ETCarrierContractRateInfo.jsp " + ex.toString());
	  session.setAttribute("ErrorCode","ERR");
	  session.setAttribute("ErrorMessage","Exception While getting IATA Rate Dtls"); 
	  session.setAttribute("Operation",operation);
	  session.setAttribute("NextNavigation",nextNavigation);
%>		 
	  <jsp:forward page="../ESupplyErrorPage.jsp" />
<%	 
	 }
%>
   
   	