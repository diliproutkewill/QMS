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
				 com.foursoft.esupply.common.java.KeyValue,
					java.util.Iterator,
				 org.apache.log4j.Logger,
				 java.util.ArrayList" %>  
<%!
  private static Logger logger = null;
%>

<%
			 String FILE_NAME ="QMSESupplyErrorPage.jsp";
       logger  = Logger.getLogger(FILE_NAME);
			 String primaryErrorCode = "none";
			 String secondaryErrorCode = "none";
			 String errorCode = "none";
			 String errorMessage = "none";
			  String message = null;			 
			 String reportUrl = "";
			 String next = null;		
			  String operation	   = null; 
			  String close  =  (String)request.getAttribute("close");
			  //Logger.info("","IN ERROR PAGE");

			 if(session.getAttribute("PrimaryErrorCode")!=null)
			 {
				primaryErrorCode = (String) session.getAttribute("PrimaryErrorCode");		// String to store error code obtained from session
			 }
			 if(session.getAttribute("SecondaryErrorCode")!=null)
			 {
				secondaryErrorCode = (String) session.getAttribute("SecondaryErrorCode");
			 }
			 
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
				   
				   if(keyValueObject.getName().equals("ErrorCode"))
                      errorCode =  keyValueObject.getValue();	
			   
			      }
			    }
			}else if((session.getAttribute("ErrorMessageObj"))!=null)   
			{
				ErrorMessage messageObject  = (ErrorMessage)session.getAttribute("ErrorMessageObj");
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
				   
				   if(keyValueObject.getName().equals("ErrorCode"))
                      errorCode =  keyValueObject.getValue();	
			   
			      }
			    }
				session.removeAttribute("ErrorMessageObj");
			}
			else
			{
			   errorCode = (String) session.getAttribute("ErrorCode");		// String to store error code obtained from session
			   errorMessage = (String) session.getAttribute("ErrorMessage");		// String to store error message obtained from session
			   next = (String) session.getAttribute("NextNavigation");		// String to store next navigation link
			   operation = (String) session.getAttribute("Operation");		// String to store the kind of operation performed
			}
			 
			
			 	

			 ArrayList errorCodeList = new ArrayList(); // list of codes which already exist

			 session.removeAttribute("reportUrl");
			 reportUrl	=	reportUrl != null ? reportUrl : "";
		
			// error codes are verified and message variable is assigned a valid message
		if(primaryErrorCode!=null)
		{
			if(primaryErrorCode.equalsIgnoreCase("RSI") && secondaryErrorCode.equalsIgnoreCase("RAE"))
			{
				errorCodeList = (ArrayList) session.getAttribute("ErrorCodeList");

				Iterator iterator = errorCodeList.iterator();

				int startIdx = -1;
				int endIdx = -1;
					for(int cnt=0; cnt<errorCodeList.size(); cnt++)
					{
						if(errorCodeList.get(cnt).toString().equalsIgnoreCase("Duplicate"))
						{
							startIdx = cnt;
							continue;
						}
						if(errorCodeList.get(cnt).toString().equalsIgnoreCase("Inserted"))
						{
						endIdx = cnt;
						}          
					}

					if(startIdx>-1)
					{
						errorMessage = "Following records already exist:\n";
						for(int cnt=startIdx+1; cnt<endIdx; cnt++)
						{
							errorMessage = errorMessage+errorCodeList.get(cnt).toString()+"\n";
						}
						errorMessage = errorMessage+"\n"+"Following records have been inserted: \n";
						for(int cnt1=endIdx+1; cnt1<errorCodeList.size(); cnt1++)
						{
							errorMessage = errorMessage+errorCodeList.get(cnt1).toString()+"\n";
						}
					}// end of  if(startIdx>=0)
					else
					{
						  errorMessage = "Following records have been inserted:\n";
						  Iterator itr = errorCodeList.iterator();
						  itr.next();
						  while(itr.hasNext())
						  {
							  errorMessage = errorMessage+itr.next().toString()+"\n";
						  }
					}
				}
			}
			  if( errorCode.equals("RSI") )
			 {
				message = " Record Successfully Inserted";	
			 }
			 else if( errorCode.equals("RSM") )
			 {
				message = "Record Successfully Updated";	
			 }
			 else if( errorCode.equals("RSD") )
			 {
				message = "Record Successfully Deleted ";	
			 }
			 else if( errorCode.equals("RNF") )
			 {
				message = "Record Not Found ";	
			 }
			 else if( errorCode.equals("RAE") )
			 {
				message = "Record Already Exists ";	
			 }
			 else if(errorCode.equalsIgnoreCase("RCE"))
			   {
				  message = "Requested category has no records";
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
			 else if( errorCode.equals("MSG") || errorCode.equals("INF") )
			 {
				 message = "Information";	

			 }else if(errorCode.equals("QMS"))
			 {
				message = "QuoteShop Message Page";	
			 }
		//}
		

%>			

<html>

<head>
<title>QuoteShopMessagePage</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
<SCRIPT LANGUAGE="JavaScript">
function openInNewWindow()
{
	//alert("new windowOpen");
	Url='<%=reportUrl%>';
	var Bars = 'directories=yes, location=no,menubar=yes,status=no,titlebar=no,toolbar=yes';
	var Options='scrollbars=yes,width=960,height=560,top=50,left=50,resizable=yes';
	var Features=Bars+' '+Options;
	
	if(Url.length > 0)
		var Win=open(Url,'ReportDoc',Features);
	
}
</SCRIPT>
</head>

<body onLoad="openInNewWindow()">
<form method="POST" action="<%=next%>">
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
<!--	<input type="hidden" name="ConfirmToAccounts" value="<%=request.getParameter("ConfirmToAccounts")%>" >-->
	<input type="hidden" name=Operation value = "<%=operation%>" >
	<input type="hidden" name=UserAccess value ="Add" >
<%if(!("Accept".equalsIgnoreCase(operation)&& errorCode.equals("RSM")))
{%>
	<input type="submit" value="Continue" name="b1" class='input'>
<%}%>
	            
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  </form>
</body>

</html>


