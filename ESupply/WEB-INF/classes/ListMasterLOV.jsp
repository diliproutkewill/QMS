<%--
%
% Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
% This Software is the proprietary information of FourSoft,Pvt Ltd.
% Use is subject to license terms
%
% esupply-V 1.x
%
 %		File					:	ListMasterLOV.jsp
 %		Sub-module name			:	
 %		Module name				:	DHL-QMS
 %    	Autor					:	K.NareshKumarReddy
 %		Date					:	6/22/2005	
 %		Modified history		:
--%>





<%@ page import="javax.naming.InitialContext,
					java.util.ArrayList,
					java.util.ResourceBundle,
					java.util.Locale,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.common.util.java.OperationsImpl,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ListMasterLOV.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);
	String operation            =   request.getParameter("Operation");//added by rk
    //@@ Srivegi Added on 20050503
	 response.setContentType("text/html; charset=UTF-8");
	 String language = loginbean.getUserPreferences().getLanguage();
	 ResourceBundle rb = ResourceBundle.getBundle("Lang_"+language);
	//@@ 20050503	
	ArrayList requiredAttributes	=	null;
	LOVListHandler listHandler	=	null;
	ArrayList listTypeIds		=	null;
	int len				=	0;
	String str			=	null;
	String value			=	rb.getString("No_Designation_Ids_are_available");
	SetUpSessionHome home	=	null;
	SetUpSession remote	=	null;
	String shipmentMode=request.getParameter("shipmentMode");
	String whereClause		=	null;
	String wheretoset		=	null;
	String		searchString	=	"";	
	String rows = null;

	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("listTypes");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}

	if(listHandler == null)
	{
		//System.out.println("Handler is  null");
		requiredAttributes  = new ArrayList();
	searchString	= request.getParameter("searchString");
	rows	= request.getParameter("row");

	if(searchString == null)
		searchString="";
	else
		searchString = searchString.trim();

	if(request.getParameter("row")!=null)
		rows=request.getParameter("row");
	else
		rows = "";

		//Logger.info(FILE_NAME,"searchString "+searchString);

		if(request.getParameter("whereClause")!=null)
			
			 whereClause=request.getParameter("whereClause");
		
		else
			 whereClause="";
  
		String new_whereClause="";

		if(request.getParameter("wheretoset")!=null)
			wheretoset=request.getParameter("wheretoset");	
		else
			wheretoset = "listTypes";	
		
		requiredAttributes.add(whereClause);
		requiredAttributes.add(wheretoset);
		requiredAttributes.add(searchString);					
		requiredAttributes.add(rows);					
			
		try
		{ 
		InitialContext	initial			=	new InitialContext();	// looking up JNDI context
		home					=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");	// looking up ETransUtitlities Session Bean	
		remote					=	(SetUpSession)home.create();
		//System.out.println("remote:"+remote);
		//System.out.println("searchString:"+searchString);
		//System.out.println("shipmentMode:"+shipmentMode);
		    listTypeIds				=	remote.getListTypeIds(searchString,shipmentMode,operation,loginbean.getTerminalId());	//added by rk
			//System.out.println("listTypeIds:"+listTypeIds);
		    if(listTypeIds != null)
        {
          len	= listTypeIds.size();
        }
        if(listTypeIds!=null)
        {
           listHandler  = new LOVListHandler(listTypeIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
		  // System.out.println("listHandler:"+listHandler);
           session.setAttribute("listTypes", listHandler);
           listHandler = (LOVListHandler)session.getAttribute("listTypes");
        }
	}
	catch(Exception	e)
	{
		//Logger.error(FILE_NAME,"ListMasterLOV.jsp: Exception5s", e.toString());
    logger.error(FILE_NAME+"ListMasterLOV.jsp: Exception5"+ e.toString());
	}

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
	String 		fileName	= "ListMasterLOV.jsp"; 

  try
    {
        if(requiredAttributes!=null)
        {
            whereClause        = (String)requiredAttributes.get(0);
            wheretoset        = (String)requiredAttributes.get(1);
            searchString         = (String)requiredAttributes.get(2);
            rows         = (String)requiredAttributes.get(3);

        }
    }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"ListMasterLOV.jsp : " +ex);
      logger.error(FILE_NAME+"ListMasterLOV.jsp : " +ex);
    }
%>
<html>
<head>
<title>Select</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateListTypes()
{
<%

	for(int i=0;i<currentPageList.size();i++ )
	{// for loop begin
		str=currentPageList.get(i).toString();

%>
		document.forms[0].listTypes.options[ <%= i %> ] = new Option('<%= str %>','<%= str %>');
<%
	}// for loop end
	if(currentPageList.size() > 0)
	{
%>
			document.forms[0].listTypes.options[0].selected = true;	
			document.forms[0].listTypes.focus();

<%
	}else{

%>
			document.forms[0].B2.focus();
<%
	}
%>
}
function setListTypes()
{
	if(document.forms[0].listTypes.selectedIndex == -1)
	{
		alert('Please Enter the List id')
	}
	else
	{
		var	index	=document.forms[0].listTypes.selectedIndex;
		window.opener.document.forms[0].listType.value=document.forms[0].listTypes.options[index].value;
		

	}
	resetValues();	
	
}

function onEnterKey()
{

	if(event.keyCode == 13)
	{
		if(document.forms[0].listTypes.selectedIndex == -1)
		{
			alert('Please Enter the List id')
		}
		else
		{
			var	index	=document.forms[0].listTypes.selectedIndex;
			window.opener.document.forms[0].listType.value=document.forms[0].listTypes.options[index].value;
			resetValues();
		}
	}
	if(event.keyCode == 27){
		
		resetValues();
	}
	
	
}
		
function onEscKey(){
	if(event.keyCode == 27){
		resetValues();
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
      window.location.href="ESupplyRemoveAttribute.jsp?valueList=designationIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="ESupplyRemoveAttribute.jsp?valueList=designationIds";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populateListTypes()" onKeyPress='onEscKey()'>
<form method ="post" action="">
		<center>
			<b><font face="Verdana" size="2">ListMaster Ids</font></b>
		</center>
		

<%
	if(currentPageList.size() >0)
	{//begin of if loop
%>
				<br>
				<center>
					<select size="10" name="listTypes" onDblClick='setListTypes()' onKeyPress='onEnterKey()' class="select">
					</select>
				</center>
				<center>
				<br>
					<input type="button" value='<%=rb.getString("Ok")%>' name="OK" onClick="setListTypes()" class="input">
					<input type="button" value='<%=rb.getString("Cancel")%>' name="B2" onClick="resetValues()" class="input">
				</center>
				<TABLE cellSpacing=0 width=95%>   
		<tr  class="formdata"> 
		<td width=100% align='center'><%=rb.getString("Pages")%> : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%= Integer.parseInt(loginbean.getUserPreferences().getSegmentSize()) %>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>
<%
	}// end of if loop
	else
	{// begin of else loop
%>
			<br><br>
			 <center><textarea rows=6 name="ta" cols="30" class='select' readOnly >No ListMaster Ids found</textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}// end of else loop
 %>

</form>
</body>
</html>
