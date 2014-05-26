<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: ConfirmBox.jsp
Product Name		: QMS
Module Name			: SetUp
Task				: Modify 
Date started		: 	
Date modified		:  
Author    			: Kameswari
Related Document	: Issue-61235

--%>
<%@page import ="org.apache.log4j.Logger" %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="ConfirmBox.jsp";
%>

<%
    logger  = Logger.getLogger(FILE_NAME);	
	String   Operation      =  null;
	 String   uniqueId      =  null;
    String   sellBuyFlag    =  null;
	String   changeDesc     =  null; 
	String   quoteId       =  null;
	String   sortedBy       =  null;
	String   sortedOrder    =  null;
	String   pageNo         =  null;
	String   fromWhat       =  null;
	String   isMultiQuote   =  null;//Added by Anil.k for CR 231104 on 31Jan2011
	try
	{
		Operation     =  request.getParameter("Operation");
		uniqueId     =  request.getParameter("quoteId");
		sellBuyFlag   =  request.getParameter("sellBuyFlag");
		changeDesc    =  request.getParameter("changeDesc");
		quoteId      =  request.getParameter("masterId");
		sortedBy      =  request.getParameter("sortedBy");
		sortedOrder   =  request.getParameter("sortedOrder");
		pageNo        =  request.getParameter("pageNo");
		fromWhat      =  request.getParameter("fromWhat");
		isMultiQuote  =  request.getParameter("isMultiQuote");//Added by Anil.k for CR 231104 on 31Jan2011
	%>

<html>
<head>
<link rel="stylesheet" href="./ESFoursoft_css.jsp">
<script>

var flag;
function confirmBox(input)
{
   flag = confirm("Select O.K to open modified Quote and Cancel to open previous Quote");
    nextPage(input);
}
function nextPage(object)
{
   var subOperation = null;
   var isMultiQuote = '<%=isMultiQuote%>';//Added by Anil.k for CR 231104 on 31Jan2011
  if(object.value=="View/Print"||(object.value=="Modify"&&flag==true))
	{
     quoteType  =  'modifiedQuote';
	}
  else
	{
     quoteType  =  'previousQuote';
	}
	if(isMultiQuote == "N")
  	document.forms[0].action='QMSQuoteController?Operation=Update&quoteId=<%=uniqueId%>&sellBuyFlag=<%=sellBuyFlag%>&changeDesc=<%=changeDesc%>&masterId=<%=quoteId%>&sortedBy=<%=sortedBy%>&sortedOrder=<%=sortedOrder%>&pageNo=<%=pageNo%>&fromWhat=<%=fromWhat%>&quoteType='+quoteType+'&quoteOption='+object.value; 
	else if(isMultiQuote == "Y")
		document.forms[0].action='QMSMultiQuoteController?Operation=Update&quoteId=<%=uniqueId%>&sellBuyFlag=<%=sellBuyFlag%>&changeDesc=<%=changeDesc%>&masterId=<%=quoteId%>&sortedBy=<%=sortedBy%>&sortedOrder=<%=sortedOrder%>&pageNo=<%=pageNo%>&fromWhat=<%=fromWhat%>&quoteType='+quoteType+'&quoteOption='+object.value+'&originLoc='+""+'&destLoc='+""; 
   document.forms[0].submit();
  
}
</script>
</head>
<body>
<form method="POST">
<table border='0' cellspacing='0' cellpadding='0' width='102%' height='120%'>
<tr class="formdata"  height='50%'>
<td align="center">
Select View to view the updated quote,Modify to Modify the updated/previous Quote,Do not Modify for not to modify the previous quote</td>
</tr>
<tr class="formdata" height='50%'>
<td align="center">
<input type ="button" name="view" class="input" onClick="nextPage(this)" value="View/Print" >
<input type ="button" name="modify" class="input" onClick="confirmBox(this)" value="Modify">
<input type ="button" name="notmodify" class="input" onClick="nextPage(this)" value="Do not Modify">
</tr>
<tr class="formdata" height='10%'><td></td></tr>
</table>
</form>
</body>
</html>
<%			
		}
		catch(Exception	e)
		{
			e.printStackTrace();
			
		}
%>
