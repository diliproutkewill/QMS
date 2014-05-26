<!--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%

	Program Name	:ETCLOVReportFormatNames.jsp
	Module Name		:ETrans
	Task			    :ReportFormat	
	Sub Task		  :ReportFormatLOV	
	Author Name		:Supraja CKM
	Date Started	:January 15,2003
	Date Completed:January 15,2003
	Date Modified	:
	Description		:This file is invoked when clicked on the CommodityLOV in the CommodityEnterId Screen. 
					Commodity Ids are displayed in the List Box . 
					This file will interacts with SetUpSession Bean and then calls the method getCommodityIds which inturn 
					retrive the all the Commodity Ids .
		
-->

<%@ page import="javax.naming.InitialContext,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,		
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.common.util.java.OperationsImpl" %>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVReportFormatNames.jsp ";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList	repFormatNames	=	null; //Vector to store ReportFormatIds and ReportFormatNames
	int			  len				      =	0;	 // variable to store commodityIds size
  int       sMode           = 0;
	String		shipmentMode	  = "";	
	String		docType	        = "";	
  String[]  str             = {"", ""};
  String    frmtId          = null;
  ArrayList codeIds         = new ArrayList();    
  String    codeStr         = "";
  String    fromWhere       = "";
  String    operation       = "";
  String    terminalId      = "";

   String	sString			= ""; //new
  
  try
	{
    fromWhere         = request.getParameter("fromWhere");
    operation         = request.getParameter("Operation");
  	shipmentMode	    = request.getParameter("shipmentMode");  
    terminalId		    = request.getParameter("TerminalId");  
	sString	= request.getParameter("searchString");  //new
//Logger.info(FILE_NAME,"fromWhere: "+fromWhere);       
    if(shipmentMode != null)
    sMode             = Integer.parseInt(shipmentMode);
    if(fromWhere!=null && fromWhere.equals("ReportFormat"))
    docType         	= request.getParameter("docType").toUpperCase();
	
	InitialContext initial			  = 	new InitialContext(); // variable to get initial context for JNDI
			


    if(fromWhere.equals("ReportFormat") )
	{

		  SetUpSessionHome home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");	// looking up ETransUtitlitiesSessionBean
		  SetUpSession remote		=	(SetUpSession)home.create();
		  repFormatNames					      =	remote.getReportFormatNames(shipmentMode,docType);		
    
    	  if(repFormatNames != null);
			len	=	repFormatNames.size();	//To find the length of printformatIds		
    }
   else if(fromWhere.equals("CodeCust") &&repFormatNames==null)
   {
 			SetUpSessionHome hoHome	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");	// looking up ETransUtitlitiesSessionBean
			SetUpSession hoRemote		=	(SetUpSession)hoHome.create();

	        codeIds  =  hoRemote.getCodeCustomizationCodeType(operation,sMode,terminalId,sString);//new

            if(codeIds != null);
          		len	=	codeIds.size();
		} 
	}
	catch(Exception ee)
	{
	//Logger.error(FILE_NAME,"Error in ETCLOVReprtFormatNames.jsp file: Exception3 : ", ee.toString());
  logger.error(FILE_NAME+"Error in ETCLOVReprtFormatNames.jsp file: Exception3 : "+ ee.toString());
	}
%>
<html>
<head>
<title>Select</title>
<script>
var frmtIds  = new Array(<%=len%>);
var fromWhere = '<%=fromWhere%>';
var Operation = '<%=operation%>';
function  populateFormatNames()
{	
 
<%			
		for( int i=0;i<len;i++ )
		{// for loop begin , this for loop is to get the commodityids and there description
      if(fromWhere!=null&&fromWhere.equals("ReportFormat"))
			str	= (String[])repFormatNames.get(i); // variable to store CommodityIds
      if(fromWhere!=null&&fromWhere.equals("CodeCust"))
      codeStr=codeIds.get(i).toString();
%>
    if(fromWhere=='ReportFormat')
    {
			val	= '<%=str[0]%>';
			document.forms[0].formatNames.options[ <%= i %> ] =  new Option('<%= str[0] %>','<%= str[1] %>');
      frmtIds<%= i %>  = '<%=str[0]%>';
    }
    if(fromWhere=='CodeCust')
    {

 			val	= '<%=codeStr%>';
			document.forms[0].formatNames.options[ <%= i %> ] =  new Option('<%= codeStr %>','<%= codeStr %>');
      frmtIds<%= i %>  = '<%=codeStr%>';
    }
<%
		}//for loop end
		if(len > 0)
		{
%>
			document.forms[0].formatNames.options[0].selected = true;	
			document.forms[0].formatNames.focus();
<%
		}else{
%>
		document.forms[0].B2.focus();
<%
		}
%>
}

function setFormatIds()
	{
  
		if(document.forms[0].formatNames.selectedIndex == -1)
		{
			if(fromWhere=='ReportFormat')
			 alert("Please select a Report Format Id");
		    else if(fromWhere=='CodeCust')
             alert("Please select a Code Customisation Id")
		    else
		     alert("Please select a Report Format Id");
		}
		else
		{
			var index =document.forms[0].formatNames.selectedIndex;
			temp = document.forms[0].formatNames.options[index].text
      if(fromWhere=='CodeCust')
      {
           window.opener.document.forms[0].codeId.value=temp;
           //window.opener.document.forms[0].codeIdName.value="ETS"+temp+"ID";//commented by sanjay to remove ETS and Id from the string
		   window.opener.document.forms[0].codeIdName.value=temp;
           if(Operation=='Add')
           {
              window.opener.setOption(temp);
              window.opener.showCustFlag(temp);
           }   
      }
      else
      {
          window.opener.document.forms[0].frmtId.value=temp;
          window.opener.document.forms[0].frmtIdHide.value=document.forms[0].formatNames.options[index].value;
      }          
  		window.close();
		}
	}
function onEnterKey()
{

	if(event.keyCode == 13)
	{
		if(document.forms[0].formatNames.selectedIndex == -1)
		{
			if(fromWhere=='ReportFormat')
			 alert("Please select a Report Format Id");
		    else if(fromWhere=='CodeCust')
             alert("Please select a Code Customisation Id")
		    else
		     alert("Please select a Report Format Id");
		}
		else
		{
			
			firstTemp		=	document.forms[0].formatNames.options[document.forms[0].formatNames.selectedIndex].value
			temp			=	firstTemp.toString();
			if(fromWhere=='ReportFormat')
				window.opener.document.forms[0].frmtId.value=temp;
			else if(fromWhere=='CodeCust')
			  {
				   window.opener.document.forms[0].codeId.value=temp;
				   //window.opener.document.forms[0].codeIdName.value="ETS"+temp+"ID";//commented by sanjay to remove ETS and Id from the string
				   window.opener.document.forms[0].codeIdName.value=temp;
				   if(Operation=='Add')
				   {
					  window.opener.setOption(temp);
					  window.opener.showCustFlag(temp);
				   }   
			  }
			window.close();
		}
	}
	if(event.keyCode == 27){
		window.close();
	}
		
}

function onEscKey(){
	if(event.keyCode == 27){
		window.close();
	}
}

</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">

</head>
 <body  onLoad="populateFormatNames()" class='formdata' onKeyPress='onEscKey()'>
  <form> 
   <center><b>Ids</b></center>
<%
	if(len > 0)
	{
%>
			<br>
			<center>
			<select size="10" name="formatNames" onDblClick='setFormatIds()' onKeyPress='setFormatIds()' class="select">
			</select>
			<br>
			<br>
			<input type="button" value=" Ok " name="OK" onClick="setFormatIds()" class="input">
			<input type="button" value="Cancel" name="B2" onClick="window.close()" class="input">
			</center>
<%
	}
	else
	{
%>
			<br>
			<center>
			<% if(fromWhere!=null && fromWhere.equals("ReportFormat")){%>
			<textarea cols=30 class='select' rows = 6 readOnly >No Report Format Names available
			<%}else{%>
			<textarea cols=30 class='select' rows = 6 readOnly >No Ids are available for Customisation
			<%}%>
			</textarea>
			<br><br>

			<input type="button" value="Close" name="B2" onClick="window.close()" class="input">
			</center>
<%
	}
%>
</form>
</body>
</html>