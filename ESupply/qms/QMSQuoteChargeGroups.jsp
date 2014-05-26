
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSQuoteChargeGroups.jsp
Product Name	: QMS
Module Name		: 
Date started	: 17 November 2005 
Date Completed	: 
Date modified	:  
Author    		: 

--%>
<%@page import ="javax.naming.InitialContext,
				 java.util.HashMap,
				 java.util.ArrayList,
				 java.util.Set,
				 java.util.Iterator,
				 com.foursoft.esupply.common.util.Logger,
				 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 com.foursoft.esupply.common.util.ESupplyDateUtility" %>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
	private static final String FILE_NAME="QMSQuoteChargeGroups.jsp";
%>

<%
		 ErrorMessage errorMessageObject   = null;
		 ArrayList	  keyValueList	       = new ArrayList();
		 ESupplyDateUtility  dateutil      = new ESupplyDateUtility();
		 String		  operation		       = request.getParameter("Operation");
		 String       label                = "";
         String       quoteIds             = null;
		 String       chargedesc           = null;
		 HashMap      quoteGroupsDtl       = new HashMap(); 
		 Set          quoteGroups          = null;
         Iterator     chargeGroups         = null;
		 String       appendQuotes         = "";
		 String[]     quoteIdDtl           = null;
		try
		{
       	  quoteGroupsDtl=(HashMap)session.getAttribute("quoteGroups");
		  quoteIdDtl=(String[])session.getAttribute("quoteId");
		  
		  if(quoteIdDtl!=null)
          session.setAttribute("quoteIdDtl",quoteIdDtl);

		  if(quoteGroupsDtl.size()>0)
		  {
            quoteGroups=quoteGroupsDtl.keySet();
            chargeGroups=quoteGroups.iterator();
%>
<html>
  <head>
	<title>QuoteGroup</title>
	<link rel="stylesheet" href="ESFoursoft_css.jsp">
</head>
<script>
</script>
  <body>
	<form method='POST'  action="QMSQuoteController" onSubmit=''>
	  <table width="800" border="0" cellspacing="0" cellpadding="0">
	    <tr valign="top" bgcolor="#FFFFFF">
		  <td>
			<table width="800" cellpadding="4" cellspacing="1">
			  <tr valign="top" class='formlabel'>
			    <td colspan="3"><table width="710" border="0" ><tr class='formlabel'>
			      <td>Quote Grouping</td>
			      <td align=right>
						</td></tr></table></td><td align=right>QS1012511</td>
			  
			  </tr>
		    </table>
			<table width="100%" cellpadding="4" cellspacing="1">
			<tr class="formheader"> 
				<td>QuoteId</td>
				<td colspan="4">Charge Group</td>
			 </tr>
	       <%
              while(chargeGroups.hasNext())
		      {  
		         chargedesc=(String)chargeGroups.next();
		         quoteIds=(String)quoteGroupsDtl.get(chargedesc);
				 if("null".equalsIgnoreCase(chargedesc) || chargedesc==null)
					chargedesc	=	"-";
		         appendQuotes=quoteIds.substring(0,quoteIds.length()-1)+","+appendQuotes;
		   %>
		   <tr class="formdata"> 
				<td><%=quoteIds.substring(0,quoteIds.length()-1)%></td>
				<td colspan="4"><%=chargedesc%></td><input type="hidden" name="quoteGroupIds" value='<%=appendQuotes%>'> 
			 </tr>
		   <%
			  } 
		   %>	  
	  <tr class='denotes'>             
				<td colspan='4'> 
					<font color='red'> * 
					</font>Denotes Mandatory Field
				</td>
				<td colspan='1' align='right'> 
	            <input type="submit" class='input' name="next" value="Next&gt;&gt;">
	            <input type="reset" class='input' name="next" value="Reset">
				</td>
			</tr>
			<input type="hidden" name='Operation' value='<%=request.getParameter("Operation")%>'>
	        <input type="hidden" name='subOperation' value='QuoteGroupInform'>
	</table>
	
	</table>
	</form>
  </body>
</html>
<%
		}
		else
		{
			errorMessageObject = new ErrorMessage(  "No records found","QMSQuoteController?Operation=Grouping"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
			%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
			<%
		 }
		 }
		catch(Exception e)
		{
			e.printStackTrace();
			Logger.error(FILE_NAME,"Error in QuoteGrouping "+e);
			errorMessageObject = new ErrorMessage(  "Error while retreiving the details","QMSReportController?Operation=buyratesExpiryReport"); 
			keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
			keyValueList.add(new KeyValue("Operation","Add")); 	
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
		}
%>
