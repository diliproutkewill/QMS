<%
/*
	Program Name	:QMSRepOfficersIdsLOV.jsp
	Module Name		:QMSSetup
	Task			:SalesPersonRegistration Master
	Sub Task		:LOV
	Author Name		:RamaKrishna Y
	Date Started	:June 28,2005
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
     String 		          fileName	            =     "QMSRepOfficersIdsLOV.jsp"; 
     logger  = Logger.getLogger(fileName);
	 ArrayList			      requiredAttributes	=	  null;
	 LOVListHandler		      listHandler			=	  null;	 
	 QMSSetUpSession          remote				=     null;
	 QMSSetUpSessionHome      home					=     null;
	 String					  pageIndex			    =	  null;	 
	 String[]				  values			    =	  null;	 
	 String					  rows					=	  null;
	 String                   repOffCode   	        =     request.getParameter("repOffCode");
	 InitialContext			  initial	            =     null;
	 ArrayList                salesPersonCodeList   =     new ArrayList();
     String                   operation             =     request.getParameter("opeartion");
	 String                   level                 =     request.getParameter("level");
	 String                   optionValue           =      null;
	// System.out.println("operation        "+operation);
	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("repOffCode");
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
			repOffCode              =      repOffCode!=null?repOffCode:"";
			
			    salesPersonCodeList     =     remote.getRepOffIds(repOffCode,level);
				
			

			if(salesPersonCodeList!=null)
			{
			   listHandler                     =	new LOVListHandler(salesPersonCodeList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
			   session.setAttribute("repOffCode", listHandler);
			   listHandler = (LOVListHandler)session.getAttribute("repOffCode");
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
	    values     =(String[])currentPageList.get(i);
		pageIndex=values[0]+"["+values[2]+"]";
        optionValue  =  values[0]+"#$%"+values[1]+"#$%"+values[2]+"#$%"+values[3];
	//	System.out.println("pageIndex  "+currentPageList.get(i));
%>
		document.forms[0].salesPersonCode.options[ <%= i %> ] = new Option('<%= pageIndex %>','<%= optionValue %>');
<%
	}// for loop end
	if(currentPageList.size() > 0)
	{
%>          
			document.forms[0].salesPersonCode.options[0].selected = true;	
			document.forms[0].salesPersonCode.focus();
			
<%
	}else{
%>
			document.forms[0].B2.focus();
<%
	}
%>
}
function setSalesPersonCode()
{
		if(document.forms[0].salesPersonCode.selectedIndex == -1)
		{
			alert("Please select a Sales Person Code ")
		}
		else
		{
			
			var index	=   document.forms[0].salesPersonCode.selectedIndex;
			firstTemp	=   document.forms[0].salesPersonCode.options[index].value;
			secondTemp	=   firstTemp.split("#$%");
			//firstIndex	=	firstTemp.indexOf(0);			
			//lastIndex	=	firstTemp.indexOf('[');				
			//firstTemp	=	firstTemp.substring(0,lastIndex);			
			//temp		=   firstTemp.toString();
			//lastIndex1	=	secondTemp.lastIndexOf('[')+1;
			//lastIndex2	=	secondTemp.lastIndexOf(']');	
			//temp1		=	secondTemp.substring(lastIndex1,lastIndex2);	   		
		    window.opener.document.forms[0].repOffCode.value=secondTemp[0];				
		    window.opener.document.forms[0].superLevel.value=secondTemp[2];		
			window.opener.document.forms[0].repOffName.value=secondTemp[1];				
		    window.opener.document.forms[0].superDesignationId.value=secondTemp[3];		
			
			resetValues();		
			
			window.close();
		}
}

function onEnterKey()
{
	if(event.keyCode == 13)
	{
		setSalesPersonCode();
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
      window.location.href	=	"ESupplyRemoveAttribute.jsp?valueList=salesPersonCode";
   }
}

function resetValues()
{
    isAttributeRemoved			=	'true';
    document.forms[0].action	=	"ESupplyRemoveAttribute.jsp?valueList=salesPersonCode";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populatePerKgIds()" onKeyPress='onEscKey()'>
<form method ="post" action="">
		<center>
			<b><font face="Verdana" size="2">Sales Person Id's</font></b>
		</center>
<%
	if(currentPageList.size() >0)
	{//begin of if loop
%>
				<br>
				<center>
					<select size="10" name="salesPersonCode" onDblClick='setSalesPersonCode()' onKeyPress='onEnterKey()' class="select">
					</select>
				</center>
				<center>
				<br>
					<input type="button" value='Ok' name="OK" onClick=setSalesPersonCode() class="input">
					<input type="button" value='Cancel' name="B2" onClick="resetValues()" class="input">
				</center>
				<TABLE cellSpacing=0 width=95%>   
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%= FoursoftConfig.NOOFSEGMENTS %>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>
<%
	}// end of if loop
	else
	{// begin of else loop
%>
			<br><br>
			 <center><textarea rows=6 name="ta" cols="30" class='select' readOnly >No SalesPerson Id's Found</textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}// end of else loop
 %>

</form>
</body>
</html>
