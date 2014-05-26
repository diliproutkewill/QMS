


<%--
%
% Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
% This Software is the proprietary information of FourSoft,Pvt Ltd.
% Use is subject to license terms
%
% esupply-V 1.x
%                   
--%>

<%--
 %		File					:	ESupplyErrorPage.jsp
 %		Sub-module name			:	common
 %		Module name				:	ESupply
 %		Purpose of the class	:	This jsp file's main purpose is to check error codes and  help redirect accordingly.
 %    	Autor					:	
 %		Date					:	
 %		Modified history		:
--%>
			      
<%@ page import="com.foursoft.esupply.common.java.ErrorMessage,
				com.foursoft.esupply.common.util.ESupplyDateUtility,java.util.HashMap,
				 com.foursoft.esupply.common.java.KeyValue,
				 java.util.ArrayList" %>  
<%
			
			 
			 String message		   = null;
			 String next           = null;
			 String errorMessage   = null; 
			 String errorCode      = null;
             String operation	   = null;
             String subOperation   = null;
			 String type      = null;
			 //@@ Srivegi Added on 20050503
			 response.setContentType("text/html; charset=UTF-8");
			 //@@ 20050503			 		 
			 
			 
			 
			 if(request.getAttribute("ErrorMessage")!=null)
			 {
				ErrorMessage messageObject  = (ErrorMessage)request.getAttribute("ErrorMessage");
				ArrayList	  keyValuesList  = null;
				KeyValue	  keyValueObject = null;
			 
			    
			    next         = messageObject.getNextNavigation();
			    errorMessage = messageObject.getMessage(); 
			    			 
			    keyValuesList	  = (ArrayList)messageObject.getKeyValueList();
			    
			    if(keyValuesList!=null && !keyValuesList.isEmpty())
			    {
			      for(int i=0;i<keyValuesList.size();i++)
			      {
			        keyValueObject =(KeyValue)keyValuesList.get(i);
				   
				   if(keyValueObject.getName().equals("Operation"))
                      operation =  keyValueObject.getValue();
					
					if(keyValueObject.getName().equals("subOperation"))
                      subOperation =  keyValueObject.getValue();
				   
				   if(keyValueObject.getName().equals("ErrorCode"))
                      errorCode =  keyValueObject.getValue();
				   if(keyValueObject.getName().equals("Type"))
                      type =  keyValueObject.getValue();
			   
			      }
			    }
			}   
			else
			{
			   errorCode = (String) session.getAttribute("ErrorCode");		// String to store error code obtained from session
			   errorMessage = (String) session.getAttribute("ErrorMessage");		// String to store error message obtained from session
			   next = (String) session.getAttribute("NextNavigation");		// String to store next navigation link
			   operation = (String) session.getAttribute("Operation");		// String to store the kind of operation performed
			}
		  
			// error codes are verified and message variable is assigned a valid message
			 if( errorCode.equals("RSI") )
			 {
				message = " Record Successfully Inserted";	
			 }
             else if(errorCode.equals("RSV"))
             {
                message="Record Successfully Validated";
             }
             else if(errorCode.equals("RSU"))
             {
                message="Record Not Validated";
             }
			 else if( errorCode.equals("RSM") )
			 {
				message = "Record Successfully Updated";	
			 }
			 else if( errorCode.equals("RSC") )
			 {
				message = "Record Successfully Closed";	
			 }
			 else if( errorCode.equals("RSD") )
			 {
				message = "Record Successfully Deleted ";	
			 }
			 else if( errorCode.equals("RPA") )
			 {
				message = "Record Successfully Posted ";	
			 }
			 else if( errorCode.equals("RNF") )
			 {
				message = "Record Not Found ";	
			 }
			 else if( errorCode.equals("RAE") )
			 {
				message = "Record Already Exists ";	
			 }
			 else if( errorCode.equals("ERR") )
			 {
				message = "Error";	
			 }
			 else if( errorCode.equals("ANI") ) 
			 {
				message = "Record Successfully Inserted";	
			
			 }
			 else if( errorCode.equals("RPS") )
			 {
			
				System.out.println("message "+ message +" Operation in ErrorPage : "+operation );
			 }
			 else if( errorCode.equals("MSG") )
			 {
				 message = "Information";	

			 }
			 if(errorCode.equals("MSG")||errorCode.equals("RSC")||errorCode.equals("RPA"))
			 {
				errorCode="INF";
			 }
%>			

<html>

<head>
<script>
function disableSubOperation()
{
	if(document.forms[0].subOperation.value.length==0)
		document.forms[0].subOperation.disabled=true;
}
</script>
<title>QuoteShop Message Page</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
</head>

<body>
<form method="POST" action="<%=next%>" onsubmit='disableSubOperation()'>
  <table width="760" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
        <table width="760" cellpadding="4" cellspacing="1">
          <tr valign="top" class='formlabel'> 
            <td colspan="2"><img border="0" src="<%=request.getContextPath()%>/images/<%=errorCode%>.gif" width="19" height="23" align="left">
              <%=message%> </td>
          </tr>
          <tr valign="top" class='formdata'> 
            <td colspan="2"> 
              <textarea rows="6" name="Message" cols="80" readonly  class='text'><%=errorMessage%></textarea>
            </td>
          </tr>
          <tr valign="top"> 
            <td width="50%"  align="right"> 
<%
	if( errorCode.equals("RAE")  || errorCode.equals("RNF"))
	{
%>
	        	<input type="hidden" name=Operation value = "<%=operation%>" >
<%
	}
%>
	<input type="hidden" name="ConfirmToAccounts" value="<%=request.getParameter("ConfirmToAccounts")!=null?request.getParameter("ConfirmToAccounts"):""%>" >
	<input type="hidden" name=Operation value = "<%=operation!=null?operation:""%>" >
	<input type="hidden" name="subOperation" value = "<%=subOperation!=null?subOperation:""%>" >
	<input type="hidden" name=UserAccess value ="Add" >
	<input type="hidden" value="N" name="terminalType" >
	<input type="hidden" value="<%=type!=null?type:""%>" name="Type" >
	<input type="submit" value="Continue" name="b1" class='input'>
	            
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  </form>
</body>

</html>


