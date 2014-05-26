<%
/*
	Program Name	:QMSDensityRatioLOV.jsp
	Module Name		:QMSSetup
	Task			:Content Description Master
	Sub Task		:LOV
	Author Name		:RamaKrishna Y
	Date Started	:Sep 21,2005
	Date Completed	:
	Date Modified	:
	Description		:
*/
%>

<%@ page import="javax.naming.InitialContext,
					java.util.ArrayList,					
					java.util.Locale,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
					com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig" %>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<%!
  private static Logger logger = null;
%>

<%
     String 		          fileName	            =     "QMSDensityRatioLOV.jsp"; 
     logger  = Logger.getLogger(fileName);	
	 ArrayList			      requiredAttributes	=	  null;
	 LOVListHandler		      listHandler			=	  null;	 
	 SetUpSession             remote				=     null;
	 SetUpSessionHome         home					=     null;
	 String					  pageIndex			    =	  null;	 
	 String					  rows					=	  null;
	 String                   dgcCode         	    =     request.getParameter("shipmentMode");
	 String                   searchString          =     request.getParameter("searchString");
	 InitialContext			  initial	            =     null;
	// ArrayList                ratioIds              =     new ArrayList();
  ArrayList                ratioIds              =     null;//At line no 44, ArrayList ratioIds is unnecessary initialized
     String                   operation             =     request.getParameter("opeartion");
	 String                   terminalId            =     request.getParameter("terminalId");
	 String					   name					=	  request.getParameter("name");/* naresh*/

	 String					  fromWhere				=	 request.getParameter("fromWhere");
	 String					  uom				    =    request.getParameter("uom");

	 String                   descriptionId         =     "";
	 String					  densityValue			=   "";
	logger.info("searchString   "+searchString);
	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("descriptionIdIds");
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
    requiredAttributes.add(name);			
		try
		{ 
			initial					=	  new InitialContext();	
			home                    =     (SetUpSessionHome)initial.lookup("SetUpSessionBean");
			remote                  =     (SetUpSession)home.create();			
			//System.out.println("dgcCode    "+dgcCode);
			if("Air".equalsIgnoreCase(dgcCode))
				dgcCode = "1";
			else if("Sea".equalsIgnoreCase(dgcCode))
				dgcCode = "2";
			if("Truck".equalsIgnoreCase(dgcCode))
				dgcCode = "4";
			
			if("BC".equals(fromWhere))
			{
				//ratioIds     =     remote.getDensityRatioIds(dgcCode,uom,loginbean);
				ratioIds     =     remote.getDensityRatioIds(dgcCode,uom,loginbean,searchString);
			}else if("BR".equals(fromWhere))
			{
				//ratioIds     =     remote.getDensityRatioIdsForRates(dgcCode,uom,loginbean);
				ratioIds     =     remote.getDensityRatioIdsForRates(dgcCode,uom,loginbean,searchString);
			}
			else
			{
				//ratioIds     =     remote.getDensityRatioIds(dgcCode,loginbean);
				ratioIds     =     remote.getDensityRatioIds(dgcCode,loginbean,searchString);
			}

			if(ratioIds!=null)
			{
			   listHandler                     =	new LOVListHandler(ratioIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
			   session.setAttribute("descriptionIdIds", listHandler);
			   //listHandler = (LOVListHandler)session.getAttribute("descriptionIdIds");At line no 114,Unnecessary Retrieval of session 
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
            name		 =	(String)requiredAttributes.get(1);
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
function  populateDescIds()
{
<%

	for(int i=0;i<currentPageList.size();i++ )
	{// for loop begin
		pageIndex=currentPageList.get(i).toString();
		if(pageIndex.indexOf(" ")>0)
			densityValue = pageIndex.substring(0,pageIndex.indexOf(" "));
	    else
           densityValue = pageIndex;

%>
		document.forms[0].descriptionId.options[ <%= i %> ] = new Option('<%= pageIndex %>','<%= densityValue %>');
<%
	}// for loop end
	if(currentPageList.size() > 0)
	{
%>          
			document.forms[0].descriptionId.options[0].selected = true;	
			document.forms[0].descriptionId.focus();
<%
	}else{
%>
			document.forms[0].B2.focus();
<%
	}
%>
}
function setdescriptionId()
{
		if(document.forms[0].descriptionId.selectedIndex == -1)
		{
			alert("Please select a Density Ratio ")
		}
		else
		{
			
			var index	=   document.forms[0].descriptionId.selectedIndex;
			
			window.opener.document.getElementById("<%=name%>").value=document.forms[0].descriptionId.options[index].value;	/*naresh*/			
			
			resetValues();		
			
			window.close();
		}
}

function onEnterKey()
{
	if(event.keyCode == 13)
	{
		setdescriptionId();
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
      window.location.href	=	"<%=request.getContextPath()%>/ESupplyRemoveAttribute.jsp?valueList=descriptionId";
	  listHandler=null;
   }
}

function resetValues()
{
    isAttributeRemoved			=	'true';
    document.forms[0].action	=	"<%=request.getContextPath()%>/ESupplyRemoveAttribute.jsp?valueList=descriptionId";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populateDescIds()" onKeyPress='onEscKey()'>
<form method ="post" action="">
		<center>
			<b><font face="Verdana" size="2">Density Ratio </font></b>
		</center>
<%
	if(currentPageList.size() >0)
	{//begin of if loop
%>
				<br>
				<center>
					<select size="10" name="descriptionId" onDblClick='setdescriptionId()' onKeyPress='onEnterKey()' class="select">
					</select>
				</center>
				<center>
				<br>
					<input type="button" value='Ok' name="OK" onClick=setdescriptionId() class="input">
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
			 <center><textarea rows=6 name="ta" cols="30" class='select' readOnly >No Density Ratio Ids Found</textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}// end of else loop
 %>

</form>
</body>
</html>
