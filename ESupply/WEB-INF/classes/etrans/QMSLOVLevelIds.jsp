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
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"QMSLOVLevelIds.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList levelIds			=	null; //varibale to store levelIds
	int len						=	0;	  //varibale to store the size of the level Ids vector
	//String whereClause		=	null;
		LOVListHandler listHandler	=	null;
			ArrayList requiredAttributes = null;
    ArrayList	currentPageList	=	new ArrayList();
	String  operation			=	request.getParameter("Operation");
	String  name				=	(request.getParameter("name")!=null)?request.getParameter("name"):"levelId";
	String	searchString		=	request.getParameter("searchString");
	String	shipmentMode		=	request.getParameter("shipmentMode");
	String  selection			=	(request.getParameter("selection")!=null)?request.getParameter("selection"):"single";
	String  slevelIds			=	(request.getParameter("slevelId")!=null)?request.getParameter("slevelId"):"";
	String  fromWhere			=	(request.getParameter("fromwhere")!=null)?request.getParameter("fromwhere"):"";
	String  consoleType			=	(request.getParameter("consoleType")!=null)?request.getParameter("consoleType"):"";
	String  chargeType          =    request.getParameter("chargeType");
	String  multiple			=	"";
  int size              = 10;
	if(selection!=null && selection.equals(""))
	{
		selection	=	"single";
	}
	if(selection!=null && selection.equals("multiple"))
	{
		multiple	=	"multiple";
	}
	InitialContext initial				=	null; //object that represent initial context
	
	if(request.getParameter("pageNo")!=null && selection.equals("single"))
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("levelIds");
			requiredAttributes    = listHandler.requiredAttributes; 
			name				  = (String)requiredAttributes.get(0);
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
	requiredAttributes  = new ArrayList();
	requiredAttributes.add(name);
	initial									=		new InitialContext();	// looking up JNDI
	SetUpSessionHome	home			=		(SetUpSessionHome )initial.lookup("SetUpSessionBean" );	// looking up SetUpSessionBean	
	SetUpSession	    remote			=		(SetUpSession)home.create();
	if(request.getParameter("flag")!=null)
	 {
		//@@ Commented and added by subrahmanyam for the pbn id: 203354  on 23-APR-10
			    //levelIds = remote.getMarginLimitLevelIds(searchString,shipmentMode,consoleType,chargeType.toUpperCase());
				levelIds = remote.getMarginLimitLevelIds(searchString,shipmentMode,consoleType,chargeType.toUpperCase(),loginbean,operation);
	 }			
	 else
	 {
	            if(!fromWhere.equals("") && fromWhere.equals("marginlimitsView"))
				{
					levelIds 						=		remote.getMarginLimitsLevelIds(operation,searchString,shipmentMode,consoleType,slevelIds,loginbean);
				}else{
					levelIds 						=		remote.getLevelIds(searchString,loginbean,operation);//added by rk
				}
     }
	if(levelIds != null)
    {
			len	= levelIds.size();
    }
    if(levelIds!=null && selection.equals("single"))
    {
      listHandler                     = new LOVListHandler(levelIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
      session.setAttribute("levelIds", listHandler);
      listHandler = (LOVListHandler)session.getAttribute("levelIds");
    }
   }
   } 
	catch(Exception ee)
	{
		//Logger.error(FILE_NAME," ------ : ", ee.toString());
    logger.error(FILE_NAME+" ------ : "+ ee.toString());
	}

	String pageNo			= request.getParameter("pageNo");
	String 		fileName	= "QMSLOVLevelIds.jsp";
	if(pageNo == null && multiple.equals(""))
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
		currentPageList = levelIds;
	}
	
%>
<html>
<head>
<title>ServiceLevel LOV</title>
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateLevelIds()
{
<%
		for( int i=0;i<currentPageList.size();i++ )
	{// begin of for loop
		String levelId = currentPageList.get(i).toString();
%>
		window.document.forms[0].levelIdNames.options[ <%= i %> ] = new Option('<%= levelId %>','<%= levelId %>','<%= levelId %>');
<%
	}// end of for loop
	if(currentPageList.size() > 0)
	{
%>
		
			window.document.forms[0].levelIdNames.options[0].selected = true;	
			window.document.forms[0].levelIdNames.focus();
<%
	}else{
%>
	window.document.forms[0].B2.focus();
<%
	}	
%>
}

function setlevelIdNames()
{
	var count =0;
	if (document.forms[0].levelIdNames.selectedIndex == -1)
	{
		alert("Please select a levelId Id")
	}
	else
	{
		for(var i=0;i<document.forms[0].levelIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].levelIdNames.options[i].selected)
			{
				count++;
			}
		}
		var levelTemp = new Array(count);
		var sIndex=0;

		for(var i=0;i<document.forms[0].levelIdNames.options.length;i++)
		{
			//var index	=	document.forms[0].serviceIdNames.selectedIndex;
			if(document.forms[0].levelIdNames.options[i].selected)
			{
				strTemp		=	document.forms[0].levelIdNames.options[i].value;
				levelTemp[sIndex]	=	strTemp;
				sIndex++;
			}
		}
<%
		if(multiple!=null && multiple.equals("multiple"))
		{	
%>
			window.opener.setLevelIdValues(levelTemp);
<%
		}else{
%>
			opener.parent.text.document.forms[0].<%=name%>.value=levelTemp[0];
<%
		}
%>
		resetValues();

	}

}


function onEnterKey()
{
	var count		=	0;
	if(event.keyCode == 13)
	{
			if (document.forms[0].levelIdNames.selectedIndex == -1)
			{
				alert("Please select a Level Id")
			}
			else
			{
				for(var i=0;i<document.forms[0].levelIdNames.options.length;i++)
				{
					if(document.forms[0].levelIdNames.options[i].selected)
					{
						count++;
					}
				}
				var levelTemp = new Array(count);
				var sIndex=0;
				for(var i=0;i<document.forms[0].levelIdNames.options.length;i++)
				{
					if(document.forms[0].levelIdNames.options[i].selected)
					{
						strTemp		=	document.forms[0].levelIdNames.options[i].value;
						levelTemp[sIndex]	=	strTemp;
						sIndex++;
					}
				}
<%
				if(multiple!=null && multiple.equals("multiple"))
				{	
%>
					window.opener.setLevelIdValues(levelTemp);
<%
				}else{
%>
					opener.parent.text.document.forms[0].<%=name%>.value=levelTemp[0];
<%
				}
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=levelIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=levelIds";
    document.forms[0].submit();   
}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body onLoad="populateLevelIds()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata'><font face="Verdana" size="2">
<center><b>Level Nos </b></center>
</font>
<form>
<%
	if(currentPageList.size() > 0)
	{// begin of if loop
%>
	<center>
	<select size="10" name="levelIdNames" selected=0 class="select" onDblClick='setlevelIdNames()' onKeyPress='onEnterKey()' <%=multiple%>></select></center>
	<br>
	<center>
	<input type="button" value=" Ok " name="OK" onClick="setlevelIdNames()" class="input">
	<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
	</center>
<%
		if(multiple.equals(""))
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
	<center><textarea rows=6 name="ta" class='select' cols="30" readOnly>No level Nos available</textarea></center>
	<br>
	<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}
%>
</form>
</body>
</html>
