<!--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%

*	Programme Name	:ETCHOCompanyRegistrationCompanyLOV.jsp
*	Module    Name  :ETrans.
*	Task			:Company Registration.
*	Sub Task		:To get the Company Registration LOV.
*	Author Name		:Raghavender.G
*   Date Started    :
*   Date Completed  :
*   Date Modified   : Sept 11,2001.By Ratan K.M.
*	Description     :
*   Method's Summary:
*/
-->

<%@ page import =	"javax.naming.InitialContext,
					java.util.Vector,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig" %>
<!--

* This file is invoked when clicked on the CompanyId LOV in ETCUtilitiesEnterId.jsp. It displays the all registered companies.
* This file will interacts with SetUpSessionBean 
*
* @version 	1.00 19 01 2001
* @author 	Raghavender.G
-->
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/><!-- added by rk -->
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCHOCompanyRegistrationCompanyLOV.jsp ";
%>
<%@ taglib  uri="/WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;

	ArrayList	companyIds		=	null;
	String		value			=	"No Company Ids available";
	int			len				=	0;
	String		searchString	=	"";	
	searchString				=	request.getParameter("searchString");
		String operation            =   request.getParameter("Operation");//added by rk
	if(searchString == null)
		searchString="";
	else
		searchString = searchString.trim();

	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("companyIds");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}
  
 try
 {
  if(listHandler == null)
    {
        //System.out.println("Handler is not null");
        requiredAttributes  = new ArrayList();
	
        InitialContext initial		 =    new InitialContext();
        SetUpSessionHome home =	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
 	    SetUpSession remote	 = (SetUpSession)home.create();
        companyIds					 = remote.getCompanyIds("HO",searchString,loginbean.getTerminalId(),operation);
		if(companyIds != null)
	{
	        len = companyIds.size();
	}
	if(companyIds!=null)
        {
            listHandler                     = new LOVListHandler(companyIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
            session.setAttribute("companyIds", listHandler);
            listHandler = (LOVListHandler)session.getAttribute("companyIds");
        }
	}
	}
 catch(Exception ee)
  {
	//Logger.error(FILE_NAME,"Exception in HOCompanyRegistrationCompanyLOV", ee.toString());  
  logger.error(FILE_NAME+"Exception in HOCompanyRegistrationCompanyLOV"+ ee.toString());  
  }
   String pageNo			= request.getParameter("pageNo");

	if(pageNo == null)
	{
		pageNo = "1";
		listHandler.currentPageIndex = 1;
	}
	else
	{
		listHandler.currentPageIndex = Integer.parseInt(pageNo);
	}

	ArrayList	currentPageList	= listHandler.getCurrentPageData();
	String 		fileName	= "ETCHOCompanyRegistrationCompanyLOV.jsp";  
%>
<html>
<head>
<title>Select </title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateCompanyNames()
{	
<%
	for( int i=0;i<currentPageList.size();i++ )
	{
		String companyId = currentPageList.get(i).toString();	
%>		
       val		="<%=companyId%>";
       first	= val.indexOf("[");
       last		= val.indexOf("]");
       cid		= val.substring(first+1,last);
       cname	= val.substring(0,first); 
       document.forms[0].companyNames.options[ <%= i %> ] = new Option(cid+" \[ "+cname+" \]",cid);
<%	
	}
	if(currentPageList.size() > 0)
	{
%>
			document.forms[0].companyNames.options[0].selected = true;	
			document.forms[0].companyNames.focus();

<%
	}
%>
}
function setCompanyName()
{
	if(document.forms[0].companyNames.selectedIndex == -1)
	{
		alert("Please select atleast one Company Id")
	}
	else
	{
	    var index =	document.forms[0].companyNames.selectedIndex;
    	firstTemp = document.forms[0].companyNames.options[index].value;
    	window.opener.document.forms[0].id.value=firstTemp;
    	resetValues();
	}
}

function onEnterKey()
{

	if(event.keyCode == 13)
	{
		setCompanyName();
	}
}
var closeWindow = 'true';

function setVar()
{
  closeWindow = 'false';
}

function toKillSession()
{
   if(closeWindow == 'true' && isAttributeRemoved=='false')
   {
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=companyIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=companyIds";
    document.forms[0].submit();   
}
</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populateCompanyNames()">
<form method="post" action="">
		<center>
			<b><font face='verdana' size='2'>Company Ids</font></b>
		</center><br>
<%
		if (currentPageList.size() > 0) 
	{
%> 
				 <center>
					<select size="10" name="companyNames" class='select'  onDblClick='setCompanyName()' onKeyPress='onEnterKey()'>    
					</select>
				</center>
				 <br>
				   <center>
						  <input type="button" value=" Ok " name="OK" onClick="setCompanyName()" class='input'>
						  <input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">

 <TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>
<%
    }
	else
	{
%>	
			 <br>
			 <center><textarea rows=6 name="ta" class='select' cols="30" ><%= value %></textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class='input'></center>
 <%
	}
 %>
</form>
</body>
</html>
