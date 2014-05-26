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
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>       
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVServiceLevelIds.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList serviceLevelIds	=	null; //varibale to store ServiceLevelIds
	int len						=	0;	  //varibale to store the size of the serviceLevel Ids vector
	//String whereClause		=	null;
		LOVListHandler listHandler	=	null;
			ArrayList requiredAttributes = null;
    ArrayList	currentPageList	=	new ArrayList();
	String  operation			=	request.getParameter("Operation");
	String  name				=	(request.getParameter("name")!=null)?request.getParameter("name"):"serviceLevelId";
	String	searchString		=	request.getParameter("searchString");
	String	shipmentMode		=	request.getParameter("shipmentMode");
	String	wheretoset			=	request.getParameter("wheretoset");
	String  selection			=	(request.getParameter("selection")!=null)?request.getParameter("selection"):"single";
	String  levelIds			=	(request.getParameter("levelId")!=null)?request.getParameter("levelId"):"";
	String  fromWhere			=	(request.getParameter("fromwhere")!=null)?request.getParameter("fromwhere"):"";
	String  consoleType			=	(request.getParameter("consoleType")!=null)?request.getParameter("consoleType"):"";
	String  multiple			=	"";

	if(selection!=null && selection.equals(""))
	{
		selection	=	"single";
	}
	if(selection.equals("multiple"))
	{
		multiple	=	"multiple";
	}

	InitialContext initial				=	null; //object that represent initial context
	if(request.getParameter("pageNo")!=null && selection.equals("single") )
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
	requiredAttributes.add(wheretoset);
	initial									=		new InitialContext();	// looking up JNDI
	SetUpSessionHome	home			=		(SetUpSessionHome )initial.lookup("SetUpSessionBean" );	// looking up SetUpSessionBean	
	SetUpSession	    remote			=		(SetUpSession)home.create();

	//Added by Anil on 8/26/2005 for cr-1007
	if(shipmentMode==null)
		{	shipmentMode = "1";}
	if(shipmentMode.equalsIgnoreCase("Truck")){
		shipmentMode	=	"4";
	}else 	if(shipmentMode.equalsIgnoreCase("Air")){
		shipmentMode	=	"1";
	}else if(shipmentMode.equalsIgnoreCase("Sea")){
		shipmentMode	=	"2";
	}
	//Anil
	
	if(!fromWhere.equals("") && fromWhere.equals("marginlimitsView"))
	{
		serviceLevelIds							=		remote.getServiceLevelIds(operation,searchString,shipmentMode,consoleType,levelIds,loginbean);
		//serviceLevelIds 						=		remote.getServiceLevelIds(searchString,shipmentMode);
	}else if(!"".equals(fromWhere) && fromWhere.equals("buyrates"))
	{
			serviceLevelIds 						=		remote.getServiceLevelIdsHirarchy(searchString,shipmentMode,loginbean.getTerminalId(),operation);		
	}
	else{

		serviceLevelIds 						=		remote.getServiceLevelIds(searchString,shipmentMode,loginbean.getTerminalId(),operation);	
	}
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
   }
   } 
	catch(Exception ee)
	{
		//Logger.error(FILE_NAME," ServicelevelLov.jsp : ", ee.toString());
    logger.error(FILE_NAME+" ServicelevelLov.jsp : "+ ee.toString());
	}

	String pageNo			= request.getParameter("pageNo");
	if(pageNo == null && selection.equals("single"))
	{
		pageNo = "1";
		listHandler.currentPageIndex = 1;
	}
	else if(selection.equals("single"))
	{
		listHandler.currentPageIndex = Integer.parseInt(pageNo);
	}
	if(selection.equals("single"))
	{
		currentPageList	= listHandler.getCurrentPageData();
	}else if(selection.equals("multiple"))
	{
		currentPageList	=	serviceLevelIds;
	}
	String 		fileName	= "ETCLOVServiceLevelIds.jsp";  
	try
    {        
        if(requiredAttributes!=null)
        {          
            wheretoset   = (String)requiredAttributes.get(0);
        }
    }
    catch(Exception ex)
    {
      //Logger.error(fileName,"Error while getting rows : " +ex);
      logger.error(fileName+"Error while getting rows : " +ex);
    }
%>
<html>
<head>
<title>ServiceLevel LOV</title>
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateServiceLevelIds()
{
<%
		for( int i=0;i<currentPageList.size();i++ )
	{// begin of for loop
		String serviceId = currentPageList.get(i).toString();
%>
		window.document.forms[0].serviceIdNames.options[ <%= i %> ] = new Option('<%= serviceId %>','<%= serviceId %>','<%= serviceId %>');
<%
	}// end of for loop
	if(currentPageList.size() > 0)
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
	var count = 0;
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
		for(var i=0;i<document.forms[0].serviceIdNames.options.length;i++)
		{
			if(document.forms[0].serviceIdNames.options[i].selected)
			{
				count++;
			}
		}
		var sLevelTemp = new Array(count);
		var sIndex=0;

		for(var i=0;i<document.forms[0].serviceIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].serviceIdNames.options[i].selected)
			{
				firstTemp	=	document.forms[0].serviceIdNames.options[i].value;
				firstIndex	=	firstTemp.indexOf(0);
				lastIndex	=	firstTemp.indexOf('[');	
				firstTemp	=	firstTemp.substring(-1,lastIndex);
				temp		=	firstTemp.toString();
				//strTemp[index]	=	temp.trim();
				//index++;
				var index=0;
				var strTemp="";
				for(index;index<temp.length;index++)
				{
					if(temp.charAt(index)!=' ')
						strTemp=strTemp+temp.charAt(index);
					else
					break;
				}
				sLevelTemp[sIndex]	=	strTemp;
				sIndex++;
			}
		}		
<%
		if(multiple!=null && multiple.equals("multiple"))
		{	
%>
			window.opener.setServiceIdValues(sLevelTemp);

<%		}else if(wheretoset!=null)
		{
%>
			opener.parent.text.document.forms[0].<%=wheretoset%>.value=strTemp;
<%
		}else{
%>
		opener.parent.text.document.forms[0].<%=name%>.value=strTemp;
<%		}
%>
		resetValues();

	}

}


function onEnterKey()
{
	var count =0;
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
			for(var i=0;i<document.forms[0].serviceIdNames.options.length;i++)
			{
				if(document.forms[0].serviceIdNames.options[i].selected)
				{
					count++;
				}
			}
			var sLevelTemp = new Array(count);
			var sIndex=0;

			for(var i=0;i<document.forms[0].serviceIdNames.options.length;i++)
			{
				//var index	=	document.forms[0].serviceIdNames.selectedIndex;
				if(document.forms[0].serviceIdNames.options[i].selected)
				{
					firstTemp	=	document.forms[0].serviceIdNames.options[i].value;
					firstIndex	=	firstTemp.indexOf(0);
					lastIndex	=	firstTemp.indexOf('[');	
					firstTemp	=	firstTemp.substring(-1,lastIndex);
					temp		=	firstTemp.toString();
					//strTemp[index]	=	temp.trim();
					//index++;
					var index=0;
					var strTemp="";
					for(index;index<temp.length;index++)
					{
						if(temp.charAt(index)!=' ')
							strTemp=strTemp+temp.charAt(index);
						else
						break;
					}
					sLevelTemp[sIndex]	=	strTemp;
					sIndex++;
				}
			}
<%
		if(multiple!=null && multiple.equals("multiple"))
		{	
%>
			window.opener.setValues(sLevelTemp);

<%		}else if(wheretoset!=null)
		{
%>
			opener.parent.text.document.forms[0].<%=wheretoset%>.value=strTemp;
<%
		}else{
%>
		opener.parent.text.document.forms[0].<%=name%>.value=strTemp;
<%		}
%>
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
<body onLoad="populateServiceLevelIds()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata'><font face="Verdana" size="2">
<center><b>ServiceLevel Nos </b></center>
</font>
<form>
<%
	if(currentPageList.size() > 0)
	{// begin of if loop
%>
	<center>
	<select size="10" name="serviceIdNames" selected=0 class="select" <%=multiple%> onDblClick='setServiceIdNames()' onKeyPress='onEnterKey()'></select></center>
	<br>
	<center>
	<input type="button" value=" Ok " name="OK" onClick="setServiceIdNames()" class="input">
	<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
	</center>
<%
		if(selection.equals("single"))
		{
%>
		<TABLE cellSpacing=0 width=95%>
			<tr  class="formdata"> 
			<td width=100% align='center'>Pages : &nbsp;
			
			<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
			</td>
			</tr>	
			</table>
<%		
		}
%>
  
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
