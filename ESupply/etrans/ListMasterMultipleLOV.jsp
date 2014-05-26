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
					com.foursoft.esupply.common.java.FoursoftConfig,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%@ taglib  uri="/WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt_rt"%>
<%!
  private static Logger logger = null;
  private static final String FILE_NAME	=	"ListMasterLOV.jsp ";
	
	%>
<%
   logger  = Logger.getLogger(FILE_NAME);	
   String  language = "";
  LOVListHandler listHandler   = null;
  ArrayList requiredAttributes = null;
	ArrayList currentPageList = null;	
    language = loginbean.getUserPreferences().getLanguage();
	//System.out.println("loginbean.getUserPreferences().getLanguage()"+loginbean.getUserPreferences().getLanguage());
%>
<fmt_rt:setLocale value="<%=language%>"/>
	<fmt:setBundle basename="Lang" var="lang" scope="application"/>


<%
   response.setContentType("text/html; charset=UTF-8");
	 //String language = loginbean.getUserPreferences().getLanguage();
	 //ResourceBundle rb = ResourceBundle.getBundle("Lang",new Locale(language));
		String shipmentMode	=	request.getParameter("shipmentMode");
		String legNo		=	request.getParameter("legNo");
		String  temp  = request.getParameter("listValue");
		/*edited by Govind
		  for issue id 195170*/
		
		if(shipmentMode != null || legNo != null || temp != null)
		{
         session.setAttribute("shipmentMode",shipmentMode);
		session.setAttribute("legNo",legNo);
		session.setAttribute("temp",temp);
		}
		//ArrayList requiredAttributes	=	null;
		//LOVListHandler listHandler	=	null;
		ArrayList listTypeIds		=	null;
		int len				=	0;
		String str			=	null;
		String value		=	null;
		String labelName	=	null;
		String labelName1	=	null;
		
		/*edited by Govind
		  for issue id 195170*/
		if(shipmentMode == null || legNo == null || temp == null)
		{
			shipmentMode = (String)session.getAttribute("shipmentMode");
      		legNo = (String)session.getAttribute("legNo");
			temp  = (String)session.getAttribute("temp");
		}
		
		
		if("Air".equals(shipmentMode))
		{
			value			=	"No ULD Type's are available";
			labelName		=	"ULD Types";
			labelName1		=	"ULD Type";

		}
		else if("Sea".equals(shipmentMode))
		{
			value			=	"No ContainerId's are available";
			labelName		=	"Container Ids";

			labelName1		=	"Container Id";
		}
		else if("Truck".equals(shipmentMode))
		{
			value			=	"No ContainerId's are available";
			labelName		=	"Container Ids";

			labelName1		=	"Container Id";
		}
		
		//((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("No_Designation_Ids_are_available");
		SetUpSessionHome home	=	null;
		SetUpSession remote	=	null;
		String whereClause		=	null;
		String wheretoset		=	null;
		String		searchString	=	"";	
		String rows = null;
		String  listTypes1[]	= request.getParameterValues("listTypes1");
			if(listTypes1==null && !"".equals(temp)){
    		listTypes1	= temp.split(",");
		}
	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("listTypes");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{e.printStackTrace();
			listHandler = null;
		}
	}

	if(listHandler == null)
	{
		System.out.println("Handler is  null");
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
//		shipmentMode="1";

			if("Air".equalsIgnoreCase(shipmentMode))
			{
				shipmentMode="1";
			}else if("Sea".equalsIgnoreCase(shipmentMode))
			{
				shipmentMode="2";
			}else if("Truck".equalsIgnoreCase(shipmentMode))
			{
				shipmentMode="4";
			}
		    listTypeIds				=	remote.getListTypeIds(searchString,shipmentMode,request.getParameter("Operation"),loginbean.getTerminalId());	
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
	{e.printStackTrace();
		//Logger.error(FILE_NAME,"ListMasterLOV.jsp: Exception5", e.toString());
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
		currentPageList	= listHandler.getCurrentPageData();
	String 		fileName	= "ListMasterMultipleLOV.jsp"; 

  try
    {
        if(requiredAttributes!=null)
        {
            whereClause        = (String)requiredAttributes.get(0);
            wheretoset         = (String)requiredAttributes.get(1);
            searchString       = (String)requiredAttributes.get(2);
            rows			   = (String)requiredAttributes.get(3);

        }
    }
    catch(Exception ex)
    {ex.printStackTrace();
      //Logger.error(FILE_NAME,"ListMasterMultipleLOV.jsp : " +ex);
      logger.error(FILE_NAME+"ListMasterMultipleLOV.jsp : " +ex);
    }
%>
<html>
<head>
<title>Select</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';

function submitForm(var1)
{
	selectallValues()
	setVar();

	document.forms[0].action="ListMasterMultipleLOV.jsp?pageNo="+var1;
	document.forms[0].submit();
}

 function selectallValues()
 {
	
	
	 obj = document.forms[0].listTypes1;
	if(obj!=null)
	{
		for (var i=0; i<obj.options.length; i++) 
		{
		
			obj.options[i].selected = true;
		}
	}

 }

function hasOptions(obj) {
	
	if (obj!=null && obj.options!=null) { return true; }
	return false;
	}

function removeSelectedOptions(from) { 
	if (!hasOptions(from)) { return; }
	if (from.type=="select-one") {
		from.options[from.selectedIndex] = null;
		}
	else {
		alert(from.options.length);
		for (var i=(from.options.length); i>0; i--) { 
			var o=from.options[i]; 
			if (o.selected) { 
				from.options[i] = null; 
				} 
			}
		}
	from.selectedIndex = -1; 
	} 

function moveDestSelectedRecords(objSourceElement, objTargetElement)    {    
	var aryTempSourceOptions = new Array();        var x = 0;                //looping through source element to find selected options        
			for (var i = 0; i < objSourceElement.length; i++) {   
					if (objSourceElement.options[i].selected) {               
							//need to move this option to target element                
							var intTargetLen = objTargetElement.length++; 
							objTargetElement.options[intTargetLen].text = objSourceElement.options[i].text;                
							objTargetElement.options[intTargetLen].value = objSourceElement.options[i].value;           

			} 
			else 
			{ 
						//storing options that stay to recreate select element          
						var objTempValues = new Object(); 
						objTempValues.text = objSourceElement.options[i].text;
						objTempValues.value = objSourceElement.options[i].value;  
						aryTempSourceOptions[x] = objTempValues;  
						x++;    
			}








				}
			//resetting length of source 




			objSourceElement.length = aryTempSourceOptions.length; 
			//looping through temp array to recreate source select element 
			for (var i = 0; i < aryTempSourceOptions.length; i++) {      
				objSourceElement.options[i].text = aryTempSourceOptions[i].text;  
				objSourceElement.options[i].value = aryTempSourceOptions[i].value; 
				objSourceElement.options[i].selected = false;  
			}
			}



	



function  populateList1Types()
{
<%
	if(listTypes1!=null)
	{
	
		for(int i=0;i<listTypes1.length;i++ )
		{
				
%>
			document.forms[0].listTypes1.options[ <%= i %> ] = new Option('<%= listTypes1[i] %>','<%= listTypes1[i] %>');
<%
		}
	}
%>
}

function  populateListTypes()
{
<%
    boolean flag = true;
    int count=0;
	for(int i=0;i<currentPageList.size();i++ )
	{
		str=currentPageList.get(i).toString();
		if(listTypes1!=null)
		for(int j=0;j<listTypes1.length;j++){
			if(str.equals(listTypes1[j]))
				flag=false;
		}
		if(flag){
%>
		document.forms[0].listTypes.options[ <%= count %> ] = new Option('<%= str %>','<%= str %>');
<%       count++;
		}flag = true;
	}
	if(currentPageList.size() > 0 && listTypes1==null)
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
	var val	='<%=labelName1%>';
	selectallValues();
	if(document.forms[0].listTypes1.selectedIndex == -1)
	{
		//alert('<fmt:message key="Please_enter_Designation_Id" bundle="${lang}"/>')
    alert("Please select atleast one "+val);
		return false;
	}
	else
	{
		var	index	=document.forms[0].listTypes.selectedIndex;
		if('<%=legNo%>'!= 'null')
			window.opener.showListValues(document.forms[0].listTypes1,<%=legNo%>);
		else
			window.opener.showListValues(document.forms[0].listTypes1);
	}
	resetValues();	
	
}

function onEnterKey()
{

	if(event.keyCode == 13)
	{
		if(document.forms[0].listTypes.selectedIndex == -1)
		{
			alert('<fmt:message key="Please_enter_Designation_Id" bundle="${lang}"/>')
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

	document.forms[0].action="ListMasterMultipleLOV.jsp?pageNo="+<%=pageNo%>;
	document.forms[0].submit();
}

function toKillSession()
{
   if(closeWindow == 'true' && isAttributeRemoved=='false')
   {
      window.location.href=
		  "../ESupplyRemoveAttribute.jsp?valueList=designationIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=designationIds";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onbeforeunload='selectallValues();'  onLoad="populateList1Types();populateListTypes()" onKeyPress='onEscKey()' onunload="">
<form method ="post" onsubmit="selectallValues();setVar();">
<input type=hidden name='pageNo' value='<%=pageNo%>'>
		<center>
			<!--<b><font face="Verdana" size="2"><fmt:message key="Designation_Ids" bundle="${lang}"/></font></b>-->
      			<b><font face="Verdana" size="2"><%=labelName%></font></b>

		</center>
		

<%
	if(currentPageList.size() >0)
	{//begin of if loop
%>
				<br>
				<center>
					<select size="10" name="listTypes" MULTIPLE  onKeyPress='onEnterKey()' class="select">
					</select>
					<!-- <input type="button" value='<<' name="left" class='input' 
					onclick='removeSelectedOptions(document.forms[0].listTypes1)'> -->

					<input type="button" value='<<' name="left" class='input' 
					onclick='moveDestSelectedRecords(document.forms[0].listTypes1,document.forms[0].listTypes)'>
					<input type="button" value='>>' name="right" class='input' onClick='moveDestSelectedRecords(document.forms[0].listTypes,document.forms[0].listTypes1)'>


					<select size="10" name="listTypes1" MULTIPLE onDblClick='setListTypes()' onKeyPress='onEnterKey()' class="select">
					</select>					

				</center>
				<center>
				<br>
					<input type="button" value='<fmt:message key="Ok" bundle="${lang}"/>' name="OK" onClick="setListTypes()" class="input">
					<input type="button" value='<fmt:message key="Cancel" bundle="${lang}"/>' name="B2" onClick="resetValues()" class="input">
				</center>
				<TABLE cellSpacing=0 width=95%>   
		<tr  class="formdata"> 
		<td width=100% align='center'><fmt:message key="Pages" bundle="${lang}"/>: &nbsp;
        
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
