


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
				 java.util.ArrayList" buffer="8kb" autoFlush="true"%>
<%
			
			 
			 String message		   = null;
			 String next           = null;
			 String errorMessage   = null; 
			 String errorCode      = null;
             String operation	   = null; 
			 String type		   = null;
			 //@@Added by Yuvraj for WPBN-10261
			 String quoteId		   = null;
			 String shipmentMode   = null;
			 String validTo		   = null;
		String customerName =null;//added by silpa.p on 3-06-11
			ArrayList  bufferList  = null;
			int bufferlistsize = 0;
			try{
			
			  bufferList  =  (ArrayList)session.getAttribute("filepdf");
			   bufferlistsize  =	bufferList!= null? bufferList.size():0;
			 //@@Yuvraj
			
			 //@@ Srivegi Added on 20050503
			 response.setContentType("text/html; charset=UTF-8");
			 response.setHeader("Cache-Control","no-cache"); // HTTP 1.1
			 response.setHeader("Pragma","no-cache"); // HTTP 1.0
			 response.setDateHeader ("Expires", -1); // Prevents caching at the proxy server
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
				   
				   if(keyValueObject.getName().equals("ErrorCode"))
                      errorCode =  keyValueObject.getValue();
				   if(keyValueObject.getName().equals("Type"))
                      type		=  keyValueObject.getValue();
					if(keyValueObject.getName().equals("quoteId"))
					  quoteId	=	keyValueObject.getValue();
					if(keyValueObject.getName().equals("validTo"))
					   validTo	=	keyValueObject.getValue();
					if(keyValueObject.getName().equals("shipmentMode"))
					   shipmentMode	= keyValueObject.getValue();
					if(keyValueObject.getName().equals("customerName"))//added by silpa.p on 3-06-11
					   customerName	= keyValueObject.getValue();

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
<title>ESupplyMessagePage</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/ESFoursoft_css.jsp">
</head>
<script>
	//@@ Added by kiran.v on 19/09/2011 for Wpbn Issue 	  271485
	function openAbbrivationPDF()
{
	var Bars = '';
	var Features = '';
	var Options	= '';
	var Win=null;
<%
	if(session.getAttribute("AbbrivationOuptutStream")!=null)
	{
%>
		Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=no';
		Options='width=1000,height=800,resizable=yes';

		Features=Bars+','+Options;			
		Win=open('QMSAbbrivationPDF.jsp','',Features);
<%} %>
	
}
// Ended by kiran.v
function openQuotePDF()
{
	var Bars = '';
	var Features = '';
	var Options	= '';
	var Win=null;
//@@ Added by subrahmanyamk for the enhancement 146460 on 29/01/09
<%
	//if("on".equalsIgnoreCase(request.getParameter("print")) && session.getAttribute("QuoteOuptutStream")!=null)
	if(("View".equalsIgnoreCase(request.getParameter("Operation"))|| "on".equalsIgnoreCase(request.getParameter("print")))&&session.getAttribute("QuoteOuptutStream")!=null)
	{
%>
		Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=no';
		Options='width=1000,height=800,resizable=yes';

		Features=Bars+','+Options;			
	//@@ Commented by subrahmanyam for the Enhancement 146460 on 29/01/09
		//Win=open('QMSQuotePDF.jsp','QuotePDF',Features);
	//@@ Added by subrahmanyam for the Enhancement 146460 on 29/01/09
		Win=open('QMSQuotePDF.jsp','',Features);
	
<%} %>
	
}
//@@Added by Kameswari for the WPBN issue-61289
function openFilePDF()
{
	
	var Bars = '';
	var Features = '';
	var Options	= '';
//@@ Added by subrahmanyamk for the enhancement on 29/01/09
<%
	
	if(("View".equalsIgnoreCase(request.getParameter("Operation"))|| "on".equalsIgnoreCase(request.getParameter("print"))) && session.getAttribute("filepdf")!=null)
	{
%>
		Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=no';
		Options='width=1000,height=800,resizable=yes';

		Features=Bars+','+Options;			
//@@ Commented by subrahmanyam for the Enhancement 146460 on 29/01/09
		//Win=open('QuoteAttachmentPDF.jsp','QuoteAttachmentPDF',Features);	
//@@ Added by subrahmanyam for the Enhancement 146460 on 29/01/09
<%		for(int l=0;l<bufferlistsize;l++){%>
		Win=open('QuoteAttachmentPDF.jsp?bufferno='+<%=l%>,'',Features);	
<%}%>

<%}
%>
}
//@@WPBN issue-61289
function openCartagePDF()
{
	var Bars = '';
	var Features = '';
	var Options	= '';
//@@ Added By Subrahmanyamk for the enhancement 146460 on 29/01/09
    <%
	if(("View".equalsIgnoreCase(request.getParameter("Operation")) ||"on".equalsIgnoreCase(request.getParameter("print"))) && session.getAttribute("CartageOuptutStream")!=null)
	{
%>
		Bars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,scrollbars=no';
		Options='width=1000,height=800,resizable=yes';

		Features=Bars+','+Options;
//@@ Commented by subrahmanyam for the Enhancement 146460 on 29/01/09
		//Win=open('QMSCartagePDF.jsp','CartagePDF',Features);
//@@ Added by subrahmanyam for the Enhancement 146460 on 29/01/09
		Win=open('QMSCartagePDF.jsp','',Features);
	
  <%}%>
}
function setAction()
{
	var quoteId		   = '<%=quoteId!=null?quoteId:""%>';
	var shipmentMode   = '<%=shipmentMode!=null?shipmentMode:""%>';
	var validTo		   = '<%=validTo!=null?validTo:""%>';

//@@ commented by subrahmanya for pbn id: 188022 on 29/10/09
	//document.forms[0].action = 'QMSCostingController?Operation=Add&subOperation=enter&quoteid='+quoteId+'&validtill='+validTo+'&shipmentMode='+shipmentMode+'&advantage=Customer';
	//@@ Added by subrahmanya for pbn id: 188022 on 29/10/09
	document.forms[0].action = 'QMSCostingController?Operation=Add&subOperation=enter&quoteid='+quoteId+'&validtill='+validTo+'&shipmentMode='+shipmentMode+'&fromPage=MultiQuote';//modified by silpa.p on 3-06-11
	document.forms[0].submit();
}

</script>
<body onLoad="openQuotePDF();openCartagePDF();openFilePDF();openAbbrivationPDF();">
<form method="POST" action="<%=next%>">
  <table width="660" border="0" cellspacing="0" cellpadding="0">
    <tr valign="top" bgcolor="#FFFFFF"> 
      <td>
        <table width="660" cellpadding="4" cellspacing="1">
          <tr valign="top" class='formlabel'> 
            <td><img border="0" src="<%=request.getContextPath()%>/images/<%=errorCode%>.gif" width="19" height="23" align="left">
              <%=message%> </td>
          </tr>
          <tr valign="top" class='formdata'> 
            <td> 
              <textarea rows="6" name="Message" cols="100" readonly  class='text'><%=errorMessage%></textarea>
            </td>
          </tr>
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
	<input type="hidden" name=UserAccess value ="Add" >
	<input type="hidden" value="N" name="terminalType" >
	<input type="hidden" value="<%=type!=null?type:""%>" name="Type" >

		<input type="hidden" value= "<%=customerName!=null?customerName:""%>" name="customer_Id" ><!--ADDED BBY SILPA.P ON 3-06-11-->
	<!-- Added by subrahmanyam for Enhancement 146460 on 10/04/09 -->
	<%if("pdf".equalsIgnoreCase(request.getParameter("pdf"))){%>
		<input type="hidden" name='from' value = "summary" >	
	<%}%>

<tr valign="top"> 
	<td align='right'> 
		<input type="submit" value="Continue" name="b1" class='input'>
<%
		if(quoteId!=null && quoteId.trim().length()!=0)
		{
%>
			<input type="button" value="Generate Costing" name="costing" class='input' onclick='setAction()'>
<%
		}
			}catch(Exception e){
				e.printStackTrace();
			}finally{

			}
%>
	</td>
</tr>
</table>
      </td>
    </tr>
  </table>
  </form>
</body>

</html>
