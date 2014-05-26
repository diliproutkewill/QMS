<%
/*
	Program Name	:QMSDensityGroupCodePerKGLOV.jsp
	Module Name		:QMSSetup
	Task			:DensityGroupCode Master
	Sub Task		:LOV
	Author Name		:RamaKrishna Y
	Date Started	:June 17,2001
	Date Completed	:
	Date Modified	:
	Description		:
*/
%>

<%@ page import="javax.naming.InitialContext,
					java.util.ArrayList,					
					java.util.Locale,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.QMSSetUpSession,
					com.qms.setup.ejb.sls.QMSSetUpSessionHome,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig" %>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%@ taglib  uri="WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<%!
  private static Logger logger = null;
%>

<%
     String 		          fileName	            =     "QMSDensityGroupCodePerKGLOV.jsp"; 
     logger  = Logger.getLogger(fileName);
	 ArrayList			      requiredAttributes	=	  null;
	 LOVListHandler		      listHandler			=	  null;	 
	 QMSSetUpSession      remote				=     null;
	 QMSSetUpSessionHome  home					=     null;
	 String					  pageIndex			    =	  null;	 
	 String					  rows					=	  null;
	 String                   dgcCode				=     request.getParameter("dgcCode");
	 String                   operation 			=     request.getParameter("Operation");
	 String                   perKG					=     request.getParameter("perKG");
	 InitialContext			  initial	            =     null;
	 ArrayList                perKgList             =     new ArrayList();

	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("perKgIds");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
			e.printStackTrace();
			//Logger.error(fileName,"Exception while getting ListHandler"+e.toString());
      logger.error(fileName+"Exception while getting ListHandler"+e.toString());
		}
	}
	if(listHandler == null)
	{
		requiredAttributes  =	new ArrayList();	
		rows				=	request.getParameter("row");

		if(request.getParameter("row")!=null)
			rows	=	request.getParameter("row");
		else
			rows	=	"";

		requiredAttributes.add(rows);					
			
		try
		{ 
			initial					=	  new InitialContext();	
			home                    =     (QMSSetUpSessionHome)initial.lookup("QMSSetUpSessionBean");
			remote                  =     (QMSSetUpSession)home.create();
			perKgList               =     remote.getPerKGIds(Integer.parseInt(dgcCode),operation,perKG,loginbean.getTerminalId());

			if(perKgList!=null)
			{
			   listHandler                     =	new LOVListHandler(perKgList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
			   session.setAttribute("perKgIds", listHandler);
			   listHandler = (LOVListHandler)session.getAttribute("perKgIds");
			}
		}
		catch(Exception	e)
		{
			e.printStackTrace();
			//Logger.error(fileName,"Exception while calling remote method", e.toString());
      logger.error(fileName+"Exception while calling remote method"+ e.toString());
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
	

  try
    {        
        if(requiredAttributes!=null)
        {          
            rows         = (String)requiredAttributes.get(0);
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
<title>Select</title>
<link rel="stylesheet" href="ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>

<script language="javascript">
var isAttributeRemoved  = 'false';
function  populatePerKgIds()
{
<%

	for(int i=0;i<currentPageList.size();i++ )
	{// for loop begin
		pageIndex=currentPageList.get(i).toString();

%>
		document.forms[0].perkgrates.options[ <%= i %> ] = new Option('<%= pageIndex %>','<%= pageIndex %>');
<%
	}// for loop end
	if(currentPageList.size() > 0)
	{
%>
			document.forms[0].perkgrates.options[0].selected = true;	
			document.forms[0].perkgrates.focus();
<%
	}else{
%>
			document.forms[0].B2.focus();
<%
	}
%>
}
function setPerKGRates()
{
		if(document.forms[0].perkgrates.selectedIndex == -1)
		{
			alert("Please select a Per KG Rate")
		}
		else
		{
			var i=parseInt(window.opener.document.forms[0].perKG.value);			
			window.opener.document.forms[0].perKG.value=document.forms[0].perkgrates.options[document.forms[0].perkgrates.options.selectedIndex].text;       
			window.close();
		}
}

function onEnterKey()
{
	if(event.keyCode == 13)
	{
		setPerKGRates();
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
      window.location.href	=	"ESupplyRemoveAttribute.jsp?valueList=perkgrates";
   }
}

function resetValues()
{
    isAttributeRemoved			=	'true';
    document.forms[0].action	=	"ESupplyRemoveAttribute.jsp?valueList=perkgrates";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populatePerKgIds()" onKeyPress='onEscKey()'>
<form method ="post" action="">
		<center>
			<b><font face="Verdana" size="2">Per Kg Id's</font></b>
		</center>
<%
	if(currentPageList.size() >0)
	{//begin of if loop
%>
				<br>
				<center>
					<select size="10" name="perkgrates" onDblClick='setPerKGRates()' onKeyPress='onEnterKey()' class="select">
					</select>
				</center>
				<center>
				<br>
					<input type="button" value='Ok' name="OK" onClick=setPerKGRates() class="input">
					<input type="button" value='Cancel' name="B2" onClick="resetValues()" class="input">
				</center>
				<TABLE cellSpacing=0 width=95%>   
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
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
			 <center><textarea rows=6 name="ta" cols="30" class='select' readOnly >No PerKg Id's Found</textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}// end of else loop
 %>

</form>
</body>
</html>
