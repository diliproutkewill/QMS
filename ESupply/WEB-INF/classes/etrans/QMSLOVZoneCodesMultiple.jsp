<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSLOVZoneCodes.jsp
Product Name		: QMS
Module Name			: Quote
Task				: 
Date started		: 12th Aug 2005 	
Date modified		:  
Author    			: Yuvraj
Related Document	: CR_DHLQMS_1007

--%>
<%@ page import="java.util.ArrayList,
				org.apache.log4j.Logger,
				com.qms.setup.ejb.sls.SetUpSession,
                com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.etrans.setup.country.bean.CountryMaster,
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig,
				com.foursoft.esupply.common.java.LookUpBean "%>
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />

<%@ taglib  uri="/WEB-INF/lib/PageIterator.jar"  prefix="pageIterator" %>       
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSLOVZoneCodesMultiple.jsp";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList ZoneCodesList			=	null; 
	int len							=	0;	  
	LOVListHandler listHandler		=	null;
	ArrayList requiredAttributes	=	null;
	String	searchString1			=	request.getParameter("searchString1");
	String	searchString2			=	request.getParameter("searchString2");
	String	searchString3			=	request.getParameter("searchString3");//Added by RajKumari on 11/10/2008 for WPBN 143511...
	String	shipmentMode			=	request.getParameter("shipmentMode");
	String	consoleType				=	request.getParameter("consoleType");
	String	whereToset				=	request.getParameter("wheretoset");
	String  listTypes[]				=	request.getParameterValues("listTypes");
	String	fromWhere				=	request.getParameter("fromWhere");
	String	rownum	 = null;
	//Logger.info(FILE_NAME,"aaaaaaaaaaaaaaaaaaaaaa");
	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("ZoneCodesList");
			 //Added by Rakesh on 28-01-2011
			 rownum			  = (String)session.getAttribute("rownum");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}
	try
	{
		//Logger.info(FILE_NAME,"bbbbbbbbbbbbbbbbbbbbbbbb");
		if(listHandler == null)
		{
			requiredAttributes  = new ArrayList();
			requiredAttributes.add(whereToset);
			requiredAttributes.add(searchString1);
			
			
			SetUpSessionHome	home	=	(SetUpSessionHome )loginbean.getEjbHome("SetUpSessionBean" );//looking up SetUpSessionBean	
			SetUpSession	    remote	=	(SetUpSession)home.create();
			//Modified by RajKumari on 11/10/2008 for WPBN 143511...
			if("Quote".equals(fromWhere))
				ZoneCodesList 		=	remote.getZoneCodes(searchString1,searchString2,searchString3,shipmentMode,consoleType);
			else
				ZoneCodesList 		=	remote.getZoneCodes(searchString1,shipmentMode,consoleType);

			if(ZoneCodesList != null)
			{
				len	= ZoneCodesList.size();
			}
			if(ZoneCodesList!=null)
			{
				listHandler             = new LOVListHandler(ZoneCodesList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
				session.setAttribute("ZoneCodesList", listHandler);
				listHandler = (LOVListHandler)session.getAttribute("ZoneCodesList");
			}
		}
	} 
	catch(Exception ee)
	{
		ee.printStackTrace();
		//Logger.error(FILE_NAME," error in the jsp : "+ee.toString());
    logger.error(FILE_NAME+" error in the jsp : "+ee.toString());
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
	String 		fileName	= "QMSLOVZoneCodesMultiple.jsp";
	
	try
    {
        //System.out.println("Hai Size is " + currentPageList.size());
        if(requiredAttributes!=null)
        {
      
            whereToset        = (String)requiredAttributes.get(0);
            searchString1     = (String)requiredAttributes.get(1);

            //System.out.println("Hai334555 " + whereToset);
        }
    }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"QMSLOVZoneCodesMultiple.jsp : " +ex);
      logger.error(FILE_NAME+"QMSLOVZoneCodesMultiple.jsp : " +ex);
	  ex.printStackTrace();
    }
	
%>
<html>
<head>
<title>ZoneCode LOV</title>

<script>
var isAttributeRemoved  = 'false';
function  populateZoneCodes()
{
  
<%
	String zoneCode = "";
	for( int i=0;i<currentPageList.size();i++ )
	{
		zoneCode = currentPageList.get(i).toString();
%>		
		 
         window.document.forms[0].zoneCodes.options[ <%= i %> ] = new Option('<%= zoneCode %>','<%= zoneCode %>','<%= zoneCode %>');
<%
	}
	if(currentPageList.size() > 0)
	{
%>
		
			window.document.forms[0].zoneCodes.options[0].selected = true;	
			window.document.forms[0].zoneCodes.focus();
<%
	}else{
%>
		window.document.forms[0].B2.focus();
<%
	}	
%>
}

function populateExistingZoneCodes()
{
<%
	if(listTypes!=null)
	{
		for(int i=0;i<listTypes.length;i++ )
		{// for loop begin
%>
		  document.forms[0].listTypes.options[ <%= i %> ] = new Option('<%= listTypes[i] %>','<%= listTypes[i] %>');
<%
		}// for loop end
	}
	
%>
}

function setZoneCodes()
{
	selectallValues();
	if (document.forms[0].listTypes.selectedIndex == -1)
	{
		alert("Please select a Zone Code")
	}
	else
	{	//Modifed by Rakesh
		window.opener.setZoneCodeValues(document.forms[0].listTypes,'<%=whereToset%>','<%=rownum%>');
		resetValues();

	}
}


function onEnterKey()
{
	if(event.keyCode == 13)
	{
			if (document.forms[0].zoneCodes.selectedIndex == -1)
			{
				alert("Please select a Zone Code")
			}
			else
			{
				window.opener.setZoneCodeValues(document.forms[0].listTypes,'<%=whereToset%>');
				resetValues();
			}
	}
	if(event.keyCode == 27 ){
		resetValues();
	}

}
function onEscKey(){
	if(event.keyCode == 27 ){
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=ZoneCodesList";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=ZoneCodesList";
    document.forms[0].submit();   
}
function selectallValues()
 {
<%
	if(currentPageList.size() > 0)
	{
%>	
		obj = document.forms[0].listTypes;
		
		for (var i=0; i<obj.options.length; i++) 
		{
			obj.options[i].selected = true;
		}
<%
	}
%>

 }
function hasOptions(obj) {
	if (obj!=null && obj.options!=null) { return true; }
	return false;
	}

function removeSelectedRecords(list)
{
	if (!hasOptions(list)) { return; }

	for (var i=0; i<list.options.length; i++) 
	{
		var o = list.options[i];
		
		if (o.selected) 
		{
			list.options[i]=null;
		}
	}
}

function moveDestSelectedRecords(objSourceElement, objTargetElement)    
{    
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


function removeSelectedRecords(list)
{
	if (!hasOptions(list)) { return; }

	for (var i=0; i<list.options.length; i++) 
	{
		var o = list.options[i];
		
		if (o.selected) 
		{
			list.options[i]=null;
		}
	}
}

</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body onLoad="populateZoneCodes();populateExistingZoneCodes()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata' onbeforeunload='selectallValues()'><font face="Verdana" size="2">
<center><b>Zone Codes[Zip Codes]</b></center>
</font>
<form method='post'>
<%
	if(currentPageList.size() > 0)
	{
%>
	<center>
<TABLE cellSpacing=0 width="100%" align='center'>
  <tr  class="formdata"> 
	<td width="49%" align='center'>
	<select size="10" name="zoneCodes" selected=0 class="select" MULTIPLE onDblClick='moveDestSelectedRecords(document.forms[0].zoneCodes,document.forms[0].listTypes)' onKeyPress='onEnterKey()' style="width:300px;margin:0px 0 5px 0;">
	</select></td>
	<td width="2%" align='center' valign="middle">
	<input type="button" value='>>' name="right" class='input' onClick='moveDestSelectedRecords(zoneCodes,listTypes)'><br>
	<input type="button" value='<<' name="left" class='input' onClick='moveDestSelectedRecords(listTypes,zoneCodes)'>
	</td>
	<td width="49%" align='center'>
	<select size="10" name="listTypes" MULTIPLE onDblClick='setZoneCodes()' onKeyPress='onEnterKey()' class="select" style="width:300px;margin:0px 0 5px 0;">
	</select>
	</td>
 </tr>
</table>
	</center>
	<br>
	<center>
	<input type="button" value=" Ok " name="OK" onClick="setZoneCodes()" class="input">
	<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
	</center>
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
		<center><textarea rows=6 name="ta" class='select' cols="30" readOnly>No Zone Codes are available</textarea></center>
		<br>
		<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}
%>
</form>
</body>
</html>
