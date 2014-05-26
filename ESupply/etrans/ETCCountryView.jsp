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
/*	Programme Name : CountryViewJSP.
*	Module Name    : ETrans.
*	Task Name      : Country Master
*	Sub Task Name  : Taking inputs from the CountryAddJsp  and pass these values to "CountryMasterSessionBean" .
*	Author		   : 
*	Date Started   :
*	Date Ended     : Sept 06, 2001.
*	Modified Date  : Sept 06, 2001(By Ratan K.M.).
*	Description    :
*	Methods		   :
*/
%>
<%@ page import	=	"javax.naming.InitialContext,
					org.apache.log4j.Logger,
					com.foursoft.etrans.setup.country.bean.CountryMaster,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.esupply.common.java.ErrorMessage,
				    com.foursoft.esupply.common.java.KeyValue,
				    java.util.ArrayList"%>

<jsp:useBean id="CountryMasterObj" class="com.foursoft.etrans.setup.country.bean.CountryMaster" scope="session"/>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCountryView.jsp ";
%>

<% 
  logger  = Logger.getLogger(FILE_NAME);	
	String[] countryId   		= null;
	String[] countryName  		= null;
	String[] currencyId  		= null;
	String[] region     		= null;
	String operation   			= null;
	String value       			= null;
	String actionValue 			= null;
	String countryMasterId 		= null;
	String readOnly 	="";
	String submitValue  ="Submit";

	String selected1 = null;
	String selected2 = null;
	String selected3 = null;
	String dsbl 	 = null;
	String area		 = null;
%>
<%
 try
 {

	if(loginbean.getTerminalId() == null)
	{
%>
   <jsp:forward page="../ESupplyLogin.jsp" />
 <%
	}
	else
	{  
  	operation = request.getParameter("Operation");
	if(operation.equals("Add") )
	{
		actionValue = "ETCCountryAddProcess.jsp";
	}
	
	else
	   {
	   try
         {
	      countryMasterId = request.getParameter("countryId").toUpperCase();
		 	 if(operation.equals("View")|| operation.equals("Delete") )
			 {
			   readOnly = "readonly";
			    dsbl 		= "disabled";
			 }
		    else
		      {
			    readOnly = "";
				 dsbl 		= "";
		      }
		     if(operation.equals("View") )
		      {
			    actionValue = "ETCCountryEnterId.jsp";
		      }
		     else
		      {
			    actionValue = "ETCCountryProcess.jsp";
		      }
	
	             InitialContext context = new InitialContext();
 				 SetUpSessionHome 	ETransHOSuperUserHome	=	(SetUpSessionHome)context.lookup("SetUpSessionBean");
		 	     SetUpSession 		ETransHOSuperUserRemote	=	(SetUpSession)ETransHOSuperUserHome.create();
	             CountryMasterObj = ETransHOSuperUserRemote.getCountryMasterDetails(countryMasterId);
         }
		  catch(Exception exsp)
		 	 {
			   //Logger.error(FILE_NAME,"Exception in CountryView ",exsp.toString());
         logger.error(FILE_NAME+"Exception in CountryView "+exsp.toString());
			 }
    	 }
	    if( CountryMasterObj == null)
		{
			 
			  ArrayList  keyValueList = new ArrayList();
		      ErrorMessage errorMessageObject = new ErrorMessage("Record does not exist with Country Id : "+countryMasterId,"ETCCountryEnterId.jsp");
		      keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
		      keyValueList.add(new KeyValue("Operation",operation)); 	
			  errorMessageObject.setKeyValueList(keyValueList);
              request.setAttribute("ErrorMessage",errorMessageObject); 
			 
			 /** 
			 String errorMessage = "Record does not exist with Country Id : "+countryMasterId;
			 session.setAttribute("ErrorCode","RNF");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("Operation",operation);
			 session.setAttribute("NextNavigation","ETCCountryEnterId.jsp");  */
			
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		}
		else
		 {
		    countryId = CountryMasterObj.getCountryId();
			countryName  =  CountryMasterObj.getCountryName();
			currencyId = CountryMasterObj.getCurrencyId();
	        region     = CountryMasterObj.getRegion();
			area = CountryMasterObj.getArea();
			
			//System.out.println(" <><><><><> "+area);

			if(area!=null && area.equalsIgnoreCase("Area1"))
			{
				selected1 = "selected";
			}
			else if(area!=null && area.equalsIgnoreCase("Area2"))
			{
				selected2 = "selected";
			}
			else if(area!=null && area.equalsIgnoreCase("Area3"))
			{
				selected3 = "selected";
			}
		 }
%>
<html>
<head>
<title>Country View</title>
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">

function upperCase(input)
 {
  input.value=input.value.toUpperCase();
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
		
  	document.forms[0].countryName.focus();
   }
	
 }
function showValues()
{
	  val = '<%= region[0] %>';
      for(i = 0 ;i < document.forms[0].region.length;i++)
      {
         if(document.forms[0].region.options[i].value == val)
         {
            document.forms[0].region.options[i].selected = true;
            break;
         }
      }        
}
function getKeyCode()
 {
  if(event.keyCode!=13)
    {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	 event.returnValue =false;
    }
  return true;
 }
 //@@ Modifed By G.Srinivas on 20050523 for QA-Issue 1771-1774
function getSpecialCode()
 {
  if(event.keyCode!=13)
    {
      if((event.keyCode > 32 && event.keyCode < 40) || event.keyCode == 64   ||event.keyCode==96 || event.keyCode==126 || event.keyCode==45 )
	 event.returnValue =false;
    }
  return true;
 }
 
function specialCharFilter(input,label) 
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
				alert("Please do not enter special characters "+label);
				input.value = "";
				input.focus();
				return false;
			}
		}
		return true;
	}
//@@ Modifed By G.Srinivas on 20050523 for QA-Issue 1771-1774 ends

function Mandatory()
{
	
	for(i=0;i<document.forms[0].elements.length;i++)
    {
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		    document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }	
    
    countryName  =  document.forms[0].countryName.value;
    currencyId   =  document.forms[0].currencyId.value; 
    region       =  document.forms[0].region.text;
    
    if(countryName.length == 0)
	{
		alert(" Please enter Country Name");
		document.forms[0].countryName.focus();
		return false;
	}
	if(currencyId.length== 0)
	{
		alert("Please enter Currency Id");
		document.forms[0].currencyId.focus();
		return false;
	}
	else if(currencyId.length <3)
	{ 
		alert("Please enter three characters for Currency Id");
		document.forms[0].currencyId.focus();
		return false;
	}
	if(region == 0)
	{
		alert("Please select a Region");
		document.forms[0].region.focus();
		return false;
	}
	
	
	document.forms[0].jbt_Test.disabled='true';
  	return true;	
}	 
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body  onLoad="showValues();placeFocus()" >
<form method="POST" onSubmit="return Mandatory()" action="<%=actionValue%>" name="country" >
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top"> 
    <td bgcolor="#ffffff">
     
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td ><table width="790" border="0" ><tr class='formlabel'><td>Country -&nbsp;<%=operation%>&nbsp;</td><td align=right><%=loginbean.generateUniqueId("ETCCountryView.jsp",operation)%></td></tr></table></td>
        </tr>
        </table>
            
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
			  <% // @@ Murali Modified On 20050421 Regarding SPETI-5642 ;  %>
			  <!-- <tr class='formdata'><td colspan='6'>&nbsp;</td></tr> -->
			  <tr class='formdata'><td colspan='8'>&nbsp;</td></tr>
			  <% // @@ Murali Modified On 20050421 Regarding SPETI-5642 ;  %>
                <tr valign="top" class='formdata'> 
                  <td colspan="2" width="115">CountryId:<font color="#FF0000">*</font><br>
                    
                    <input type='text' class='text' name="countryId" readonly size="3" maxlength="2" value="<%=countryId[0]%>"  >
                    </td>
                  <td width="271" >Country 
                    Name:<font color="#FF0000">*<br>
                    </font>
					<!-- //@@ Modifed By G.Srinivas on 20050523 for QA-Issue 1771-1774 -->
                    <input type='text' class='text' name="countryName" size="33" maxlength="30" value="<%=countryName[0]%>" <%=readOnly%> onBlur='upperCase(countryName);return specialCharFilter(this,"")' onkeyPress="return getSpecialCode(countryName)">
                    </td>
                  <td width="102">Currency 
                    Id:<font color="#FF0000">*<br>
                    </font> 
                    <input type='text' class='text' name="currencyId" size="5" maxlength="3" value="<%=currencyId[0]%>" <%=readOnly%> onBlur="upperCase(currencyId)" onkeyPress="return getKeyCode(currencyId)">
                    </td>
                  <td colspan="2" width="322">Region:<font color="#FF0000">*<br>
                    </font> 
<%	
		if(operation.equals("View")|| operation.equals("Delete") )
		{
%>
                    <input type='text' class='text' name="region" size="15" maxlength="3" value="<%=region[0]%>" <%=readOnly%> >
<%
		}
		else
		{
%>         
	           <select size="1" name="region" class='select'>
                      <option value = " "> Select </option>
                      <option value = ASPA>ASPA</option>
                      <option value = AMNO>AMNO </option>
                      <option value = AMLA>AMLA </option>
                      <option value = EURE>EURE</option>
                      <option value = EMA>EMA</option>
			<!--@@ Added by subrahmanyam for the adding of regions as requested by KIM on 18-jan-10 -->                      
                     <!-- <option value = NAP>NAP</option>--> <!-- North Asia Pacific -->
                      <!--<option value = SAP>SAP</option>--> <!-- South Asia Pacific -->
			<!--@@ Ended by subrahmanyam for the adding of regions as requested by KIM on 18-jan-10 -->                      
					  <option value = OTHER>OTHER</option>
                      <!-- <option value = ANTARCTICA>ANTARCTICA</option> -->
                    </select>
                    </td>
<%
		}
%>
				<td  class='formdata' colspan="1">Area:<br>
                    <select size="1" name="area" class='select' <%=dsbl%>>
                     <% // @@ Murali Modified On 20050421 Regarding SPETI-5641 ;  %>
					  <!-- <option >&nbsp;</option> -->
					  <option >Select</option>
					  <% // @@ Murali Modified On 20050421 Regarding SPETI-5641 ;  %>
                      <option value='Area1' <%=selected1%>>Area 1</option>
                      <option value='Area2' <%=selected2%>>Area 2</option>
                      <option value='Area3' <%=selected3%>>Area 3</option>
                    </select>
                    </td>
                </tr>
              </table>
              <table border="0" width="800" cellpadding="4" cellspacing="1">
                <tr class='denotes'> 
                  <td valign="top" colspan="3"> 
                  <font color="#FF0000">*</font><font face="Verdana" size="1">Denotes Mandatory</font>
                  </td>
				    
                </tr>
                <tr class='denotes'> 
                  <td valign="top" width="50"><font face="Verdana" size="1"> 
                    Note :
					</font>
                  </td>
                  <td valign="top" > <font face="Verdana" size="1"> 
                    
					 1) Country Ids to conform to IATA Rule No 1.3.1<br>
					 2) Currency Ids to conform to IATA Rule No 5.7.1
					</font>
                  </td>
					
                
				<td valign="top"  align="right"> 
					  <input type="hidden" value="<%=operation%>" name="Operation">
<%
		if(operation.equals("View") )
		{
			submitValue = "Continue";
			
		}
		else if(operation.equals("Delete") )
		{
			submitValue = "Delete";
			
		}
	    else
		{
			submitValue = "Submit";
		}
%>
        
		 <%
		if(operation.equals("Modify") )
		{%>
		  <input type="submit" value="<%=submitValue%>" name="jbt_Test" class='input'>
		  <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'>
         <%}else{%> 
		  <input type="submit" value="<%=submitValue%>" name="jbt_Test" class='input'>
		  <%}%>
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
	//Logger.error(FILE_NAME,"Exception in Country Master.jsp",e.toString());
  logger.error(FILE_NAME+"Exception in Country Master.jsp"+e.toString());
 }
 %>
