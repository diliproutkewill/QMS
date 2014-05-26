<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSIndustryIdMultipleLOV.jsp
Product Name		: QMS
Module Name			: SetUp
Task				: IndustryIdLOV
Date started		: 	
Date modified		:  
Author    			: Kameswari
Related Document	: Issue-61289

--%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%@page import = "com.qms.setup.java.IndustryRegDOB,
				  com.qms.setup.ejb.sls.SetUpSessionHome,
				  com.qms.setup.ejb.sls.SetUpSession,
		          javax.naming.InitialContext,
				  javax.naming.Context,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  java.util.ArrayList, com.foursoft.esupply.common.java.LOVListHandler,
				  java.util.Iterator"
				  %>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>				 

<%!
  private static Logger logger = null;
  private static final String FILE_NAME="QMSIndustryIdMultipleLOV.jsp";
%>
<%
    logger  = Logger.getLogger(FILE_NAME);	
	ArrayList	industryList		=	null;
	ArrayList	industryPageList	=	new ArrayList();
	IndustryRegDOB industryRegDOB	=	null;
	//String[] industryId				=	null;
	String[] industryDesc			=	null;
	boolean		success				=	true;

    LOVListHandler listHandler		=	null;
    ArrayList requiredAttributes	=	null;
	ArrayList currentPageList		=	null;
	String	searchStr				=	request.getParameter("searchString");
	String  operation				=	request.getParameter("Operation");
	String	multiple				=	request.getParameter("multiple");
	String	listTypes1[]			=   request.getParameterValues("listTypes1");
	String	fileName				=	"QMSIndustryIdMultipleLOV.jsp";
	int size		=10;
	try
	{  
		if(request.getParameter("pageNo")!= null)
		{
			listHandler           = (LOVListHandler)session.getAttribute("industryList");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
	}
	catch (Exception e)
	{
		listHandler = null;
	} 

	try{

			if(listHandler==null)
			{
				InitialContext initial		=   new InitialContext();
				SetUpSessionHome	home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
				SetUpSession		remote	=	(SetUpSession)home.create();
				industryList				=	remote.getAllIndustryDetails(searchStr,operation,loginbean);
				
				if(industryList!=null && industryList.size()>0)
				{
					/*industryId		=	new String[industryList.size()];
					industryDesc	=	new String[industryList.size()];*/
					for(int i=0;i<industryList.size();i++)
					{
						industryRegDOB	=	(IndustryRegDOB)industryList.get(i);
						industryPageList.add(industryRegDOB);
						//industryPageList.add(industryRegDOB.getDescription());
					}

				}
				if(industryPageList!=null )
				{
					
					if(loginbean.getUserPreferences().getLovSize()!=null && !(loginbean.getUserPreferences().getLovSize().equals("")))
						{	size	=	Integer.parseInt(loginbean.getUserPreferences().getLovSize());}        
					listHandler  = new LOVListHandler(industryPageList,size,requiredAttributes);
					session.setAttribute("industryList", listHandler);		
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

	}catch(Exception e)
	{
		success=false;
		//Logger.error(FILE_NAME,"Exception while retrieving the data"+e);
    logger.error(FILE_NAME+"Exception while retrieving the data"+e);

	}

%>
<html>
<head>
<title>IndustryRegistration Add</title>      
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script language=	"javascript">
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
function selectallValues()
{

obj = document.forms[0].listTypes1;
	for (var i=0; i<obj.options.length; i++) 
	{
		obj.options[i].selected = true;
	}		
}
function setIndustryId()
{
	selectallValues();
		if( document.form1.listTypes1.selectedIndex==-1)
	{
		alert("Please select a IndustryId");
	}
	else
	{
  	window.opener.setIndustryIdValues(document.forms[0].listTypes1);
	resetValues();
	}
}

	function  populateIndustryNames()
{
<%
	if(listTypes1!=null)
	{
	for(int i=0;i<listTypes1.length;i++ )
	{// for loop begin
		

%>
	   
		document.forms[0].listTypes1.options[ <%= i %> ] = new Option('<%= listTypes1[i] %>','<%= listTypes1[i] %>');
<%
	}// for loop end
	}
	
%>
}
function showIndustryIds()
{
<%	boolean flag = true;
    int     count = 0;
	for( int i=0; i<currentPageList.size(); i++)
	{//for loop begin
	   industryRegDOB	=	(IndustryRegDOB)currentPageList.get(i);
		String industryId = industryRegDOB.getIndustry()+'['+industryRegDOB.getDescription()+']';
		if(listTypes1!=null)
		for(int j=0;j<listTypes1.length;j++ )
	    {
          if(industryId.equalsIgnoreCase(listTypes1[j])){
			  flag=false;
			  break;
		  }
		}
		if(flag){
%>
		val				= '<%= industryId %>';
		firstIndex		= val.indexOf('['); 	
		optionValue		= val.substring(0,firstIndex);	
       // optionValue		= val.substring(0,firstIndex-1);	
		window.document.form1.ids.options[<%= count %>] = new Option(val,optionValue);
<%	     count++;
		}flag=true;		
	}// end of for loop
	if(count>0 && currentPageList.size() > 0)
	{
%>
			window.document.form1.ids.options[0].selected = true;	
			window.document.form1.ids.focus();

<%
	}else{
%>	
		window.document.form1.B2.focus();
<%
		}	
%>
}
function onEnterKey()
{

	if(event.keyCode == 13)
	{
		setCarrierId();
	}
	if(event.keyCode == 27){
		 resetValues();	
	}
}
///// Added for LOV Paging /////

function onEscKey()
{
	if(event.keyCode == 27)
	{
	resetValues();
	}
}

var isAttributeRemoved  = 'false';
var isRegistered  = 'false';

function onCancel()
{
	//removeLOVValues();
	resetValues();
	window.close();
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
	window.location.href="../ESupplyRemoveAttribute.jsp?valueList=industryList";
	}
}

function resetValues()
{
	isAttributeRemoved  = 'true';
	document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=industryList";
	document.forms[0].submit();   
}
///// Added for LOV Paging /////
</script>
</head>
<body class='formdata' onLoad='populateIndustryNames();showIndustryIds();' onKeyPress='onEscKey()' onUnLoad='toKillSession()' onbeforeunload='selectallValues()'>
<form name=form1 method='post'>
<%	if(currentPageList.size() > 0)	
	{ 
%>
<center>
<table cellSpacing=0 width="100%" align='center'>
<tr  class="formdata"> 
<td width="49%" align='center'>
<select size=10 name="ids" MULTIPLE onKeyPress='onEnterKey()'  class="select" style="width:300px;margin:0px 0 5px 0;"> 
</select></td>
<td width="2%" align='center' valign="middle">
<input type="button" value='>>' name="right" class='input' onClick='moveDestSelectedRecords(document.forms[0].ids,document.forms[0].listTypes1)'><br>
<input type="button" value='<<' name="left" class='input' onClick='moveDestSelectedRecords(document.forms[0].listTypes1,document.forms[0].ids)'></td>
<td width="49%" align='center'>
<select size="10" name="listTypes1" MULTIPLE onDblClick='setIndustryId()' onKeyPress='onEnterKey()' class="select" style="width:300px;margin:0px 0 5px 0;">
</select></td>
</tr>
</table>
</center>
<center>
<br>
<center> 
<input type="button" value=" Ok " name="addButton" onClick="setIndustryId()" class="input">
<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
</center>
<TABLE cellSpacing=0 width=95%>
<tr  class="formdata"> 
<td width=100% align='center'>Pages : &nbsp;

<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
</td>
</tr>	
</table>
<%	}
else 
{
%>     

<p align="center">
<br>
<br>
<SELECT SIZE="10" NAME="locationId" class="select" >

<option > No Data Found</option>
</select>


<p align='center'><INPUT TYPE=BUTTON VALUE="Close" onClick='javascript:window.close()'  class='input'>

<%    
}
%>
</table>
</form>
</body>
</html>