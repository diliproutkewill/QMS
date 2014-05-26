<%--
%
% Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
% This Software is the proprietary information of FourSoft,Pvt Ltd.
% Use is subject to license terms
%
% esupply-V 1.x
%
 %		File					:	QMSDesignationLOV.jsp
 %		Sub-module name			:	
 %		Module name				:	DHL-QMS
 %		Purpose of the class	:	This File brings all designationLOVs when the button in enterId screen	was	pressed
 %    	Autor					:	K.NareshKumarReddy
 %		Date					:	6/22/2005	
 %		Modified history		:
--%>





<%@ page import="javax.naming.InitialContext,
					java.util.ArrayList,
					java.util.ResourceBundle,
					java.util.Locale,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.QMSSetUpSession,
                    com.qms.setup.ejb.sls.QMSSetUpSessionHome,
					com.foursoft.etrans.common.util.java.OperationsImpl,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSDesignationLOV.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);
	String operation            =   request.getParameter("Operation");//added by rk
    //@@ Srivegi Added on 20050503
	 response.setContentType("text/html; charset=UTF-8");
	 String language = loginbean.getUserPreferences().getLanguage();
	// ResourceBundle rb = ResourceBundle.getBundle("Lang",new Locale(language));
	//@@ 20050503	
	ArrayList requiredAttributes	=	null;
	LOVListHandler listHandler	=	null;
	ArrayList designationIds		=	null;
	int len				=	0;
	String str			=	null;
	String value			=	"No designation Ids found";//rb.getString("No_Designation_Ids_are_available");
	QMSSetUpSessionHome home	=	null;
	QMSSetUpSession remote	=	null;
	String whereClause		=	null;
	String wheretoset		=	null;
	String		searchString	=	"";	
	String rows = null;
	String from = "";

	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("designationIds");
			requiredAttributes    = listHandler.requiredAttributes; 
			from	= request.getParameter("from");

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
		from	= request.getParameter("from");
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
			wheretoset = "designationId";	
		
		requiredAttributes.add(whereClause);
		requiredAttributes.add(wheretoset);
		requiredAttributes.add(searchString);					
		requiredAttributes.add(rows);
		requiredAttributes.add(from);
			
		try
		{ 
		InitialContext	initial			=	new InitialContext();	// looking up JNDI context
		home					=	(QMSSetUpSessionHome)initial.lookup("QMSSetUpSessionBean");	// looking up ETransUtitlities Session Bean	
		remote					=	(QMSSetUpSession)home.create();
		    designationIds				=	remote.getDesignationIds(searchString,loginbean.getTerminalId(),operation);	
			//Logger.info(FILE_NAME,"designationIds "+designationIds);
		    if(designationIds != null)
        {
          len	= designationIds.size();
        }
        if(designationIds!=null)
        {
           listHandler                     = new LOVListHandler(designationIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
           session.setAttribute("designationIds", listHandler);
           listHandler = (LOVListHandler)session.getAttribute("designationIds");
		   
        }
			
	}
	catch(Exception	e)
	{
		e.printStackTrace();
		//Logger.error(FILE_NAME,"QMSDesignationLOV.jsp: Exception5", e.toString());
    logger.error(FILE_NAME+"QMSDesignationLOV.jsp: Exception5"+ e.toString());
	}

	}

	String pageNo			= request.getParameter("pageNo");
	from	= request.getParameter("from");
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
	String 		fileName	= "QMSDesignationLOV.jsp"; 

  try
    {
        if(requiredAttributes!=null)
        {
            whereClause        = (String)requiredAttributes.get(0);
            wheretoset        = (String)requiredAttributes.get(1);
            searchString         = (String)requiredAttributes.get(2);
            rows         = (String)requiredAttributes.get(3);
			      from		=(String)requiredAttributes.get(4);

        }
    }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"ETCLOVTrackingIds.jsp : " +ex);
      logger.error(FILE_NAME+"ETCLOVTrackingIds.jsp : " +ex);
    }
%>
<html>
<head>
<title>Select</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateDesignationIds()
{
<%

	for(int i=0;i<currentPageList.size();i++ )
	{// for loop begin
		str=currentPageList.get(i).toString();

%>
		document.forms[0].designationIds.options[ <%= i %> ] = new Option('<%= str %>','<%= str %>');
<%
	}// for loop end
	if(currentPageList.size() > 0)
	{
%>
			document.forms[0].designationIds.options[0].selected = true;	
			document.forms[0].designationIds.focus();

<%
	}else{

%>
			document.forms[0].B2.focus();
<%
	}
%>
}
function setdesignationIds()
{
	if(document.forms[0].designationIds.selectedIndex == -1)
	{
		alert("Please_enter_Designation_Id")
	}
	else
	{
		var	index	=document.forms[0].designationIds.selectedIndex;
		//window.opener.document.forms[0].designation.value=document.forms[0].designationIds.options[index].value;
		var index	=   document.forms[0].designationIds.selectedIndex;
			firstTemp	=   document.forms[0].designationIds.options[index].value;
			secondTemp	=   document.forms[0].designationIds.options[index].value;
			//firstIndex	=	firstTemp.indexOf(0);			
			lastIndex	=	firstTemp.indexOf('[');				
			firstTemp	=	firstTemp.substring(0,lastIndex);			
			temp		=   firstTemp.toString();
			lastIndex1	=	secondTemp.lastIndexOf('[')+1;
			lastIndex2	=	secondTemp.lastIndexOf(']');	
			temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
			tempp2		=	temp1.substring(temp1.indexOf(',')+1,temp1.length);
		   		
								

<%				if(!"View".equalsIgnoreCase(from))
				{
%>
				window.opener.document.forms[0].level.value=tempp2;
				window.opener.document.forms[0].designation.value=temp;	
<%				}else{
%>				
	window.opener.document.forms[0].designationId.value=temp;
<%}
%>

			window.close();
	}
	resetValues();	
	
}

function onEnterKey()
{

	if(event.keyCode == 13)
	{
		if(document.forms[0].designationIds.selectedIndex == -1)
		{
			alert("Please_enter_Designation_Id")
		}
		else
		{
			var	index	=document.forms[0].designationIds.selectedIndex;
			//window.opener.document.forms[0].designationId.value=document.forms[0].designationIds.options[index].value;
			setdesignationIds();
			
			//resetValues();
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
<body class='formdata' onUnLoad='toKillSession()' onLoad="populateDesignationIds()" onKeyPress='onEscKey()'>
<form method ="post" action="">
		<center>
			<b><font face="Verdana" size="2">Designation<br> Id[Description,Level No]</font></b>
		</center>
				<input type="hidden" name="from" value="<%=from%>">

		

<%
	if(currentPageList.size() >0)
	{//begin of if loop
%>
				<br>
				<center>
					<select size="10" name="designationIds" onDblClick='setdesignationIds()' onKeyPress='onEnterKey()' class="select">
					</select>
				</center>
				<center>
				<br>
					<input type="button" value='Ok<%//=rb.getString("Ok")%>' name="OK" onClick=setdesignationIds() class="input">
					<input type="button" value='Cancel<%//=rb.getString("Cancel")%>' name="B2" onClick="resetValues()" class="input">
				</center>
				<TABLE cellSpacing=0 width=95%>   
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages<%//=rb.getString("Pages")%> : &nbsp;
        
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
			 <center><textarea rows=6 name="ta" cols="30" class='select' readOnly ><%= value %></textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}// end of else loop
 %>

</form>
</body>
</html>
