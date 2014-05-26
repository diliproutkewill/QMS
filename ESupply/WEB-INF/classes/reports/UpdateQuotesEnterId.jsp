
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	:UpdateQuotesEnterId.jsp
Product Name	: QMS
Module Name		: UpdateQuotesEnterId
Date started	: 19 September 2005 
Date Completed	: 
Date modified	:  
Author    		: K.NareshKumarReddy

--%>
<%@page import ="java.util.ArrayList,
				 com.qms.reports.java.UpdatedQuotesDOB,
				 com.qms.reports.java.UpdatedQuotesFinalDOB,
				 org.apache.log4j.Logger,
				 com.foursoft.esupply.common.util.ESupplyDateUtility"%>


<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME="UpdateQuotesEnterId.jsp";
%>

<%
      logger  = Logger.getLogger(FILE_NAME);	
			ESupplyDateUtility  eSupplyDateUtility		=	new ESupplyDateUtility();
      		ArrayList			dataList				=	null;
      		String              dateFormat				=	loginbean.getUserPreferences().getDateFormat();
      		String				currentDate				=	null;
			UpdatedQuotesFinalDOB finalDOB				=	null;
      		UpdatedQuotesDOB	updatedDOB				=	null;
			int					selectedIndex			=	-1;
			String				checked					=	"";
			String				fromWhat				=	"";
      		String              fromSummary             =  request.getParameter("fromSummary");//added by subrahmanyam for 146463
			
			try
      		{
      			eSupplyDateUtility.setPatternWithTime(dateFormat);
      		}
      		catch(Exception exp)
      		{
      			//Logger.error(FILE_NAME," Error in JSP UserPreferences DateFormat---> "+exp.toString());
            logger.error(FILE_NAME+" Error in JSP UserPreferences DateFormat---> "+exp.toString());
      		}
      		
      		try
      		{ 
      			finalDOB				=	(UpdatedQuotesFinalDOB)session.getAttribute("reportFinalDOB");

				if("activitySummary".equalsIgnoreCase(request.getParameter("subOperation")))
					fromWhat			=	"summaryReport";
				else
					fromWhat			=	request.getParameter("fromWhat");
				
				if(finalDOB!=null)
				{
					dataList			=	finalDOB.getChangeDescList();
					selectedIndex		=	finalDOB.getChangeDescSelectedIndex();
				}
      			
      			if(dataList==null)
      				dataList			=	new ArrayList();
      			
      			currentDate				=	eSupplyDateUtility.getCurrentDateString(dateFormat);
		

%>



<html>
  <head>
	<title>Updated Quotes </title>
	<link rel="stylesheet" href="ESFoursoft_css.jsp">
<script>
function setSelectedIndex(index)
{
	document.forms[0].selectedIndex.value=index
}
//Added by Anil.k for Excel Page
function setSelectedIndexChk(index)
{
	if(document.forms[0].selectedIndex.value!="")
		document.forms[0].selectedIndex.value=document.forms[0].selectedIndex.value+","+index;
	else
		document.forms[0].selectedIndex.value=index
}//End
function validate()
{
<%	if(dataList!=null && dataList.size() > 0)	
	{
%>		if(document.forms[0].htxl.value == "HTML")//Added by Anil.k for Excel Page
		{
		var obj	=	document.getElementsByName("radiobutton");
		for(var i=0;i<obj.length;i++)
		{
			if(obj[i].checked)
				setSelectedIndex(i);
		}
		if(document.forms[0].selectedIndex.value.length==0)
		{
			alert('Please Select a Change Description');
			return false;
		}
		else
			return true;
		}//Added by Anil.k for Excel Page
		else if(document.forms[0].htxl.value == "EXCEL")
		{
		var obj	=	document.getElementsByName("checkBoxButton");
		for(var i=0;i<obj.length;i++)
		{
			if(obj[i].checked)
				setSelectedIndexChk(i);
		}		
		if(document.forms[0].selectedIndex.value.length==0)
		{
			alert('Please Select a Change Description');
			return false;
		}
		else
			return true;
		}//Ended by Anil.k for Excel Page
<%
	}
	else
	{
%>
		return true;
<%
	}
%>
}
//Added by Anil.k for Excel Page
function format1()
{
	if(document.forms[0].format.selectedIndex == 0)
	{
		/*document.getElementById("radioUpdate").style.display='block';
		document.getElementById("checkboxUpdate").style.display='none';*/
		for(var i=0;i<document.getElementsByName("checkBoxButton").length;i++)
		{
			document.getElementById("checkBoxButton"+i).disabled=true;
			document.getElementById("radiobutton"+i).disabled=false;
			document.getElementById("checkBoxSelect").disabled=true;
			document.getElementById("checkBoxButton"+i).checked=false;
			document.forms[0].checkBoxSelect.checked=false;
			document.forms[0].htxl.value="HTML";
		}
	}
	else if(document.forms[0].format.selectedIndex == 1)	
	{
		//document.getElementById("radioUpdate").style.display='none';
		//document.getElementById("checkboxUpdate").style.display='block';
		for(var i=0;i<document.getElementsByName("radiobutton").length;i++)
		{
			document.getElementById("radiobutton"+i).disabled=true;	
			document.getElementById("radiobutton"+i).checked=false;
			document.getElementById("checkBoxButton"+i).disabled=false;
			document.forms[0].checkBoxSelect.disabled=false;
			document.forms[0].htxl.value="EXCEL";
		}
	}
}
function selectAll()
{
	for(var i=0;i<document.getElementsByName("checkBoxButton").length;i++)
	{
		document.getElementById("checkBoxButton"+i).checked=true;		
	}
}//Ended by Anil.k
</script>
  </head>

  <body onLoad=format1()>
	<form method='POST' action='QMSReportController' onSubmit='return validate()'>
		<table width="100%" cellpadding="0" cellspacing="0">
			<tr valign="top" class="formlabel">
				<td colspan="2"><table cellpadding="4" cellspacing="1" width="100%"><tr class='formlabel'><td>Updated Quotes</td>
				<td align='right'><!-- Screen Id goes here --></td></tr></table></td>
					</tr></table>
		<table width="100%" cellpadding="4" cellspacing="1" bgcolor="#FFFFFF">
			<tr valign="top" class='formdata'>
				<td>Terminal Id:<%=loginbean.getTerminalId()%></td>				
				<td >User:<%=loginbean.getUserId()%></td>			
				<td> Date:<%=currentDate%></td>
		  </tr>
		</table>
<%
		if(dataList.size()==0)
      	{	
%>
			<table width="100%"   border="0" cellpadding="4" cellspacing="1" bgcolor="#FFFFFF">
				<tr>
					<td colspan="13" align="center">
						<font face="Verdana" size="2" color='red'>
							<br><b>No Updated Quotes Found.</b></font>
					</td>
				</tr>
				<tr valign="top" class='denotes'>
					<td colspan="4">&nbsp;</td>
					<td  align="right"><input type='submit' class='input'  name='b1' value='Continue'></td>
					<INPUT TYPE="hidden" name='Operation' value='updatedQuotes'>
					<INPUT TYPE="hidden" name='subOperation' value='Continue'>
				</tr>
			</table>
<%
		}
		else
      	{
%>
				<table width="100%" cellpadding="4" cellspacing="1" bgcolor="#FFFFFF">
					<tr valign="top" class='formheader'>
						<td>Select<br>
						<input type="checkbox" name="checkBoxSelect" id="checkBoxSelect"  onclick='selectAll()'></td>
						<td>Change Description</td>
						<td>Total Quotes Updated</td>
						<td>Quotes Confirmed</td>
						<td>Quotes not Confirmed</td>
					</tr>

<%				for(int i=0;i<dataList.size();i++)
      			{
      					updatedDOB	= (UpdatedQuotesDOB)dataList.get(i);
						if(i==selectedIndex)
							checked	=	"checked";
						else
							checked	=	"";
%>						
					 <tr valign="top" class='formdata'>
					<!--  Added by Anil.k for Excel Page  --><td>
					 <!-- <span id="checkboxUpdate" style='DISPLAY:none' class="formdata"> -->
						<input type="checkbox" name="checkBoxButton" id="checkBoxButton<%=i%>"  <%=checked%>>
					 <!-- </span>
					 <span id="radioUpdate" style='DISPLAY:block' class="formdata"> --><!--  Ended by Anil.k for Excel Page  -->
						<input type="radio" name="radiobutton" id="radiobutton<%=i%>"  onclick='setSelectedIndex(<%=i%>)' <%=checked%>>
                     <!-- </span> --><!--  Added by Anil.k for Excel Page  --></td>

						<td><%="Change in "+updatedDOB.getChangeDesc()%></td>
						<td><%=updatedDOB.getUpdatedQuotes()%></td>
						<td><%=updatedDOB.getConfirmedQuotes()%></td>
						<td><%=updatedDOB.getUnconfirmedQuotes()%></td>
					 </tr>
<%
				}
%>				
			 <tr class='formdata'>
				<td>&nbsp;</td>
				<td colspan='4'>
					<select name='format' class='select' onChange='format1()'><!-- Modified by Anil.k for Excel Page -->
						<option value='html' selected>HTML</option>
						<option value='excel'>EXCEL</option>
					</select>
				</td>
			</tr>
			<tr valign="top" class='denotes'>
				<td colspan="4"></td>
				<td  align="right"><input type='submit' class='input'  name='b1' value='Next>>'></td>
				<INPUT TYPE="hidden" name='Operation' value='<%=request.getParameter("Operation")%>'>
				<INPUT TYPE="hidden" name='subOperation' value='Report'>
				<INPUT TYPE="hidden" name='fromWhat' value='<%=fromWhat!=null?fromWhat:""%>'>
				<INPUT TYPE="hidden" name='SortOrder' value="ASC">
				<INPUT TYPE="hidden" name='SortBy' value="QuoteId">
				<INPUT TYPE="hidden" name='PageNo' value="1">
				<INPUT TYPE="hidden" name='selectedIndex'>
				<INPUT TYPE="hidden" name='htxl'><!--Added by Anil.k for Excel Page -->
				<!-- added by subrahmanyam for 146463 -->
				<INPUT TYPE="hidden" class='input' name='fromSummary' value='<%=fromSummary%>'>
			</tr>
		</table>
<%
		}
%>
			  
	  </form>
	</body>
</html>
<%		}
		catch(Exception	e)
		{
			e.printStackTrace();
			//Logger.error(FILE_NAME,"Error in JSP", e.toString());
      logger.error(FILE_NAME+"Error in JSP"+ e.toString());
		}
%>