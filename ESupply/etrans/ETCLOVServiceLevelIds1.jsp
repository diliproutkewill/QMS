<!--
	Program Name	:ETransLOVServiceLevelIds.jsp
	Module Name		:ETrans
	Task			:ServiceLevel	
	Sub Task		:ServiceLevelLOV	
	Author Name		:Ushasree.Petluri
	Date Started	:September 19,2001
	Date Completed	:September 19,2001
	Date Modified	:
	Description		:This method maim function is to pop up the list of values depending on the ServiceLevel Ids.	
-->
<%@ page import="javax.naming.InitialContext,
				java.util.ArrayList,
				org.apache.log4j.Logger,
				com.qms.setup.ejb.sls.SetUpSession,
                com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.etrans.setup.country.bean.CountryMaster,
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig" %>
<%@ taglib  uri="/WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>       
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVServiceLevelIds1.jsp";
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/><!-- ADDED BY RK -->

<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList serviceLevelIds	=	null; //varibale to store ServiceLevelIds
	int len						=	0;	  //varibale to store the size of the serviceLevel Ids vector
	//String whereClause		=	null;
		LOVListHandler listHandler	=	null;
			ArrayList requiredAttributes = null;
	String	searchString		=	request.getParameter("searchString");
	String	shipmentMode		=	request.getParameter("shipmentMode");
	String  listTypes1[]		=   request.getParameterValues("listTypes1");
	String operation            =   request.getParameter("Operation");//added by rk
	String rows=null;
	InitialContext initial				=	null; //object that represent initial context
	//System.out.println("listTypes1listTypes1listTypes1  "+listTypes1);
	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("serviceLevelIds");
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
	System.out.println("Handler is not null");
	requiredAttributes  = new ArrayList();
	initial									=		new InitialContext();	// looking up JNDI
	SetUpSessionHome	home			=		(SetUpSessionHome )initial.lookup("SetUpSessionBean" );	// looking up SetUpSessionBean	
	SetUpSession	    remote			=		(SetUpSession)home.create();
	serviceLevelIds 						=		remote.getServiceLevelIds1(searchString,shipmentMode,loginbean.getTerminalId(),operation);	

	if(serviceLevelIds != null)
    {
			len	= serviceLevelIds.size();
    }
    if(serviceLevelIds!=null)
    {
      listHandler                     = new LOVListHandler(serviceLevelIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
      session.setAttribute("serviceLevelIds", listHandler);
      listHandler = (LOVListHandler)session.getAttribute("serviceLevelIds");
    }
		if(request.getParameter("row")!=null)
		rows=request.getParameter("row");
	else
		rows = "";
   }
   } 
	catch(Exception ee)
	{
		//Logger.error(FILE_NAME," ServicelevelLov.jsp : ", ee.toString());
    logger.error(FILE_NAME+" ServicelevelLov.jsp : "+ ee.toString());
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
	String 		fileName	= "ETCLOVServiceLevelIds1.jsp";  
	
%>
<html>
<head>
<title>Service Level LOV</title>
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function submitForm(var1)
{
	selectallValues()
	setVar();

	document.forms[0].action="ETCLOVServiceLevelIds1.jsp?pageNo="+var1;
	document.forms[0].submit();
}

 function selectallValues()
 {
	
	 obj = document.forms[0].listTypes1;
	for (var i=0; i<obj.options.length; i++) 
		{
			obj.options[i].selected = true;
		}

 }
function hasOptions(obj) {
	if (obj!=null && obj.options!=null) { return true; }
	return false;
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
	function  populateCountry1Names()
{
<%
	if(listTypes1!=null)
	{
	for(int i=0;i<listTypes1.length;i++ )
	{// for loop begin
		//System.out.println("listTypes1 "+ listTypes1[i]);

%>
		document.forms[0].listTypes1.options[ <%= i %> ] = new Option('<%= listTypes1[i] %>','<%= listTypes1[i] %>');
<%
	}// for loop end
	}
	
%>
}
function  populateServiceLevelIds()
{
<%	boolean flag = true;
    int     count = 0;
	for( int i=0;i<currentPageList.size();i++ )
	{// begin of for loop
		String serviceId = currentPageList.get(i).toString();
		if(listTypes1!=null)
		for(int j=0;j<listTypes1.length;j++ )
	    {
          if(serviceId.equalsIgnoreCase(listTypes1[j])){
			  flag=false;
			  break;
		  }
		}
		if(flag){
%>
		window.document.forms[0].serviceIdNames.options[ <%= count %> ] = new Option('<%= serviceId %>','<%= serviceId %>','<%= serviceId %>');
<%		count++;
		}flag=true;
	}// end of for loop
	if(count>0 && currentPageList.size() > 0)
	{
%>
		
			window.document.forms[0].serviceIdNames.options[0].selected = true;	
			window.document.forms[0].serviceIdNames.focus();
<%
	}else{
%>
	window.document.forms[0].B2.focus();
<%
	}	
%>
}

function setServiceIdNames()
{
	selectallValues();
	if (document.forms[0].listTypes1.selectedIndex == -1)
	{
		alert("Please select a ServiceLevel Id")
	}
	else
	{
		window.opener.showLocationValues(document.forms[0].listTypes1,'serviceLevelId');
		/*var index	=	document.forms[0].serviceIdNames.selectedIndex;
		firstTemp	=	document.forms[0].serviceIdNames.options[index].value;
		firstIndex	=	firstTemp.indexOf(0);
		lastIndex	=	firstTemp.indexOf('[');	
		firstTemp	=	firstTemp.substring(-1,lastIndex);
		temp		=	firstTemp.toString();
	
		var index=0;
		var strTemp="";
		for(index;index<temp.length;index++)
		{
			if(temp.charAt(index)!=' ')
				strTemp=strTemp+temp.charAt(index);
			else
			break;
		}		
		opener.parent.text.document.forms[0].serviceLevelId.value=strTemp;*/
		resetValues();

	}

}


function onEnterKey()
{

	if(event.keyCode == 13)
	{
			if (document.forms[0].serviceIdNames.selectedIndex == -1)
			{
				alert("Please select a ServiceLevel Id")
			}
			else
			{
			var index	=	document.forms[0].serviceIdNames.selectedIndex;
			firstTemp	=	document.forms[0].serviceIdNames.options[index].value;
			firstIndex	=	firstTemp.indexOf(0);
			lastIndex	=	firstTemp.indexOf('[');	
			firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
			temp		=	firstTemp.toString();
		
			var index=0;
			var strTemp="";
			for(index;index<temp.length;index++)
			{
				if(temp.charAt(index)!=' ')
					strTemp=strTemp+temp.charAt(index);
				else
				break;
			}		
			opener.parent.text.document.forms[0].serviceLevelId.value=strTemp;
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=serviceLevelIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=serviceLevelIds";
    document.forms[0].submit();   
}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body onLoad="populateCountry1Names();populateServiceLevelIds()" onUnLoad='toKillSession()' onbeforeunload='selectallValues();' onKeyPress='onEscKey()' class='formdata'>
<font face="Verdana" size="2"><br><br>
<center><b>Service Level Nos </b></center>
</font>

<form method="post" action="">
<br>
<input type=hidden name='pageNo' value='<%=pageNo%>'>
<%
	if(currentPageList.size() > 0)
	{// begin of if loop
%>
<TABLE cellSpacing=0 width="100%" align='center'>
 <tr  class="formdata"> 
  <td width="49%" align='center'>
	<select size="10" name="serviceIdNames"  class="select" MULTIPLE onKeyPress='onEnterKey()' style="width:300px;margin:0px 0 5px 0;"></select>
  </td>
  <td width="2%" align='center' valign="middle">
	<input type="button" value='>>' name="right" class='input' onClick='moveDestSelectedRecords(document.forms[0].serviceIdNames,document.forms[0].listTypes1)'><br>
	<input type="button" value='<<' name="left" class='input' onClick='moveDestSelectedRecords(document.forms[0].listTypes1,document.forms[0].serviceIdNames)'>
  </td>
  <td width="49%" align='center'>
  <!--@@Modified by kameswari-->
    <select size="10" name="listTypes1" MULTIPLE onDblClick='setServiceIdNames()' onKeyPress='onEnterKey()' class="select" style="width:300px;margin:0px 0 5px 0;"></select>
	 <!--@@kameswari-->
  </td>
 </tr>
</table>
<TABLE cellSpacing=0 width="100%" align='center'>
	<tr><td align='center' colspan='3'>
	<input type="button" value=" Ok " name="OK" onClick="setServiceIdNames()" class="input">
	<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
	</td></tr>
	</table>
	<TABLE cellSpacing=0 width=100%>
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
	<center><textarea rows=6 name="ta" class='select' cols="30" readOnly>No Servicelevel Nos available</textarea></center>
	<br>
	<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}
%>
</form>
</body>
</html>
