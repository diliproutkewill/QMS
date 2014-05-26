<!---
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%


*	Programme Name	:HOCompanyRegistrationCountryLOV
*	Module    Name  :Etrans	
*	Task			:Company Registration.
*	Sub Task		:Displaying the all CountryIds and country name in the list box.
*	Author Name		:Raghavender.G
*   Date Started    :
*   Date Completed  :
*   Date Modified   : Sept 12,2001.By Ratan K.M.
*	Description     :
*   Method's Summary:

-->
<%@ page import =	"javax.naming.InitialContext,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
					com.qms.setup.ejb.sls.SetUpSessionHome"%>

<!--
* This file is invoked when clicked on the CountryId LOV. It displays the all CountryIds and country name in the list box,
* from that user can select desired countryId.Once user selects the id, displays in the appropriate field.
* This file will interacts with ETAUtilitiesSessionBean 
* @version 	1.00 19 01 2001
* @author 	Raghavender.G

-->
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVCountryAndCurrencyIds.jsp ";
%>

<%	
  logger  = Logger.getLogger(FILE_NAME);	
	String		value		=	"No Country Ids available";
	ArrayList	countryIds	=	null;
    ArrayList	currencyIds	=	null;
	int			len			=	0;
%>

<!--	

Creating InitialContext Object,remote and home references and looking up the "ETAUtilitiesSessionBean".After calling the
loadCountryId() and loadCurrencyId() methods, ContryIds and currenyIds become vector objects.
-->

<%
 String		searchString	="";	
    try
	{
		searchString	= request.getParameter("searchString");
		if(searchString == null)
			searchString="";
		else
		  searchString = searchString.trim();
		//Logger.info(FILE_NAME,"searchString "+searchString);
		String new_whereClause="";
/*		if(searchString != null && !searchString.equals(""))
		{
			new_whereClause = " WHERE COUNTRYID LIKE '"+searchString+"%' ";
		}
		else
			new_whereClause= "";
*/
	if(countryIds == null)
    {
      InitialContext initial					= new InitialContext();
      SetUpSessionHome home	= (SetUpSessionHome )initial.lookup( "SetUpSessionBean" );
 	  SetUpSession remote		= (SetUpSession)home.create();
 //   countryIds						= remote.getCountryIds(new_whereClause);
//    currencyIds						= remote.getCurrencyIds(new_whereClause); 
	  countryIds						= remote.getCountryIds(searchString,"","");
	  currencyIds						= remote.getCurrencyIds(searchString); 
      if(countryIds != null)
		  len = countryIds.size();
     }
 }//end of try block.
  catch(Exception ee)
  {
 	//Logger.error(FILE_NAME,"Exception in ETransLOVCountryAndCurrencyIds.jsp : ", ee.toString());
  logger.error(FILE_NAME+"Exception in ETransLOVCountryAndCurrencyIds.jsp : "+ ee.toString());
  }//end of catch block.
%>
<html>
<head>
<title>Select </title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<script>
function  populateCountryNames()
{	
<%
	for( int i=0;i<len;i++ )
	{
		String country = countryIds.get(i).toString();//converting countryIds and currencyIds vector elements into strings
	    String currency= currencyIds.get(i).toString();//and displaying them in LOV.
%>		
       document.forms[0].countryNames.options[ <%= i %> ] = new Option('<%= country %>','<%= currency %>','','');
<%	
	}
	if(len > 0)
	{
%>
			document.forms[0].countryNames.options[0].selected = true;	
			document.forms[0].countryNames.focus();

<%
	}
%>
}
function setCountryNames()
{
	if(document.forms[0].countryNames.selectedIndex == -1)
		alert("Please select atleast one Country Id")
	else
	{
		firstTemp	= document.forms[0].countryNames.options[document.forms[0].countryNames.selectedIndex].value;
		secondTemp	= document.forms[0].countryNames.options[document.forms[0].countryNames.selectedIndex].text;
		len			= secondTemp.indexOf('[');
		len1		= secondTemp.indexOf(']');
		firstTemp	= firstTemp.substring(len+2,len1+2);
		var str		= secondTemp.substring(len+1,len1);
		window.opener.document.forms[0].currencyId.value=firstTemp;
		window.opener.document.forms[0].countryId.value=str;
		window.close();
	}
}


function onEnterKey()
{

	if(event.keyCode == 13)
	{
		setCountryNames();
	}
}

</script>
</head>
<body  class='formdata' onLoad="populateCountryNames()">
<center><b>Country Ids</b></center><br>
<form>
<%
 if(len > 0)
 {
%>	 		
	
			<center><select size="10" name="countryNames"  class='select' onDblClick='setCountryNames()' onKeyPress='onEnterKey()'>    
			</select>
			<center>
			<br>
			<input type="button" value=" Ok " name="OK" onClick="setCountryNames()" class='input'>
			<input type="button" value="Cancel" name="B2" onClick="window.close()" class='input'>
			</center>
<%
 }
	else
	{
%>
			 <center><textarea rows=6 name="ta" class='select' cols="30" readOnly ><%= value %></textarea></center><br>
			<center><input type="button" value="Close" name="B2" onClick="window.close()" class='input'></center>
<%
	 }
%>    
		</form>
		</body>
		</html>
