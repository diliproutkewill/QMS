<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
Program Name	: ETCLOVTerminalIds.jsp
	Module name		: ETrans
	Task			: LOV
	Sub task		: 
	Author Name		: Ushasree
	Date Started	: September 19, 2001
	Date completed	: September 19, 2001
	Description		:  This file displays all the terminal Ids for selection.

--%>

<%@ page import	=	"javax.naming.InitialContext,
					java.util.ArrayList,
					java.util.ResourceBundle,
					java.util.Locale,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.setup.country.bean.CountryMaster,
					com.foursoft.etrans.common.util.java.OperationsImpl,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig,javax.servlet.jsp.jstl.fmt.LocalizationContext" %>
					
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>

<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVTerminalIds.jsp ";  	
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
  LOVListHandler listHandler   = null;
	ArrayList requiredAttributes = null;
	ArrayList currentPageList = null; 

    //@@ Srivegi Added on 20050503
		 response.setContentType("text/html; charset=UTF-8");
		 //String language = loginbean.getUserPreferences().getLanguage();
		 //ResourceBundle rb = ResourceBundle.getBundle("../Lang_"+language);
		//@@ 20050503	
		java.util.ArrayList terminalId	=	 null;    // a Vector to store all terminal Ids
		int len							=	 0;    // integer to store size of Vector
		//ArrayList requiredAttributes	= null;
		//LOVListHandler listHandler		= null;
		String whereClause				=	 null;
		String fromWhat					=	 null;
		String ArrayOrNot				=	 null;
		String wheretoset				=    "terminalId";
		String		searchString		="";
		String shipmentMode				="1";  //Air   "1,3,5,7";
		String	terminalType			= request.getParameter("terminalType");
		String	from					= request.getParameter("from");
		//Srivegi Added on 20050323 (IATA-PR) 
	    String module					= null;
		String origin					= null;
		String dest						= null;
		String serviceLevel				= null;
		String  operationModule         = request.getParameter("operationModule") ;
	  	// 20050323 (IATA-PR) 
	
			searchString	= request.getParameter("searchString");
			if(searchString == null)
				searchString="";
			else
				searchString = searchString.trim();
			fromWhat	=	request.getParameter("fromWhat");
		//The following lines are added for Weblogic 
			if(fromWhat == null)
				fromWhat="0";
			else
				fromWhat = fromWhat;
    //Srivegi Added on 20050323 (IATA-PR) 
	module = 	request.getParameter("module");
	origin = 	request.getParameter("origin");
	dest    = 	request.getParameter("dest");
	serviceLevel     = 	request.getParameter("serviceLevel");
   //20050323 (IATA-PR) 
		// for Weblogic  ends here
			if( request.getParameter("shipmentMode") != null)
				shipmentMode	= request.getParameter("shipmentMode");

			//Added by Anil on 8/26/2005 for cr-1007
			if(shipmentMode.equalsIgnoreCase("Truck")){
				shipmentMode	=	"4";
			}else 	if(shipmentMode.equalsIgnoreCase("Air")){
				shipmentMode	=	"1";
			}else if(shipmentMode.equalsIgnoreCase("Sea")){
				shipmentMode	=	"2";
			}
			//Anil

			
			if( request.getParameter("wheretoset") != null)
				wheretoset	= request.getParameter("wheretoset");

			if(request.getParameter("ArrayOrNot")!=null)
				ArrayOrNot=request.getParameter("ArrayOrNot");
			else
				ArrayOrNot="NoArray";
			if(terminalType!=null && !(terminalType.equals(""))){
				terminalType = terminalType;
			}else{
				terminalType = "O";
			}

			
		/*	if(request.getParameter("whereClause")!=null)
			{		
				whereClause=request.getParameter("whereClause");
			    whereClause = whereClause.replace('^' ,'%');			
		
			// whereClause=whereClause +" AND TERMINALID LIKE '"+searchString+"%' AND OPER_ADMIN_FLAG = 'O'  AND SHIPMENTMODE LIKE '%A%' ";
			}
			else
			{
				Logger.info(FILE_NAME,"else whereClause : "+whereClause);
				whereClause=" WHERE TERMINALID LIKE '"+searchString+"%' AND OPER_ADMIN_FLAG = 'O'  AND SHIPMENTMODE IN ("+shipmentMode+") ";
			} */
		if(request.getParameter("pageNo")!=null)  
		{
			try
			{
		  
				listHandler           = (LOVListHandler)session.getAttribute("terminalId");
				requiredAttributes    = listHandler.requiredAttributes; 
			}
			catch (Exception e)
			{
				listHandler = null;
			}
		}
		
	if(listHandler == null)
	{
      requiredAttributes  = new ArrayList();

      requiredAttributes.add(terminalType);
      requiredAttributes.add(searchString);
      requiredAttributes.add(fromWhat);
      requiredAttributes.add(shipmentMode);
      requiredAttributes.add(wheretoset);
      requiredAttributes.add(ArrayOrNot);
      requiredAttributes.add(from);
    try
		{	
			InitialContext ic				=new InitialContext();			// getting the instance of InitialContext
			SetUpSessionHome th		=(SetUpSessionHome)ic.lookup("SetUpSessionBean");	// getting Home instance
			SetUpSession   	tr		=(SetUpSession)th.create();		// getting the instance of SessionBean
	//		terminalId						=tr.getTerminalIds(whereClause);		// Vector terminalId is populated		
			if(from != null && from.equals("ThirdParty"))
			{
				
				String originTerminal		= request.getParameter("originTrml");
				String destinationTerminal	= request.getParameter("destTrml");
				if(originTerminal == null || originTerminal.equals("null") || originTerminal.equals(""))
					originTerminal = " ";
				if(destinationTerminal == null || destinationTerminal.equals("null") || destinationTerminal.equals(""))
					destinationTerminal = " ";
				/*
				if(originTerminal.equals(""))
					terminals = destinationTerminal;
				else if(destinationTerminal.equals(""))
					terminals = originTerminal;
				else if(originTerminal.equals("") && destinationTerminal.equals(""))
					terminals = "";
				else 
					terminals = originTerminal+" , "+destinationTerminal;
				*/	
				terminalId			=	tr.getTerminalIdsforThirdStation("","",
																		shipmentMode,null,
																		searchString,originTerminal,
																		destinationTerminal);
			}
			//Srivegi Added on 20050323 (IATA-PR) 
			// Null condition checked by Santhosam to solve known issue
			else if(module !=null && module.equals("IATAAdd"))
		     { 
			   terminalId 					= tr.getLocations(searchString,wheretoset,module,origin,dest,serviceLevel);
			 }
			 // Null condition checked by Santhosam to solve known issue
			 else if(module !=null && module.equals("IATAModify"))
		     {  
			   int mode=7;
			   terminalId 					= tr.getLocations(searchString,wheretoset,module,origin,dest,serviceLevel);
			   } 
			else if(request.getParameter("locationId")!=null)
		      { 
				terminalId			=	tr.getTerminalIdsforThirdStation(request.getParameter("locationId"),searchString);
				// operationModule is added by VLAKSHMI FOR ISSUE 173655 on 20090629
			  }else if(operationModule !=null && operationModule.equalsIgnoreCase(terminalType)) {
				terminalId			=	tr.getTerminalIds(loginbean.getTerminalId(),"",shipmentMode,terminalType,searchString,loginbean.getAccessType());
			               }   else if(operationModule !=null && ( operationModule.equalsIgnoreCase("A") && terminalType.equalsIgnoreCase("O"))) {
				terminalId			=	tr.getTerminalIds(loginbean.getTerminalId(),"",shipmentMode,terminalType,searchString,loginbean.getAccessType());
		               }
                   // operationModule is added by VLAKSHMI FOR ISSUE 173655 on 20090629
			   else{
	                 terminalId			=	tr.getTerminalIds("","",shipmentMode,terminalType,searchString,loginbean.getAccessType());
                    }
			 //20050323 (IATA-PR) 
			
			 //    
			/*
				Here where condition should be passed within quotes when required in of passsing in query.
				If no condition is to passed then keep the String empty with quotes.
			*/
			if(terminalId != null)
				len	=	terminalId.size();
		if(terminalId!=null)
        {
            listHandler                     = new LOVListHandler(terminalId,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
            session.setAttribute("terminalId", listHandler);
            listHandler = (LOVListHandler)session.getAttribute("terminalId");
        }
   } 
		catch(Exception e)
		{
			//Logger.error(FILE_NAME,"Error in TerminalInformation TerminalLov  : ", e.toString());
      logger.error(FILE_NAME+"Error in TerminalInformation TerminalLov  : "+ e.toString());
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

	String 		fileName	    = "ETCLOVTerminalIds.jsp";

   try
    {
        if(requiredAttributes!=null)
        {
            terminalType        = (String)requiredAttributes.get(0);
            searchString        = (String)requiredAttributes.get(1);
            fromWhat         = (String)requiredAttributes.get(2);
            shipmentMode     = (String)requiredAttributes.get(3);
            wheretoset = (String)requiredAttributes.get(4);
            ArrayOrNot     = (String)requiredAttributes.get(5);  
			from			= (String)requiredAttributes.get(6);  
         }
    }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"ETCLOVTerminalIds.jsp : " +ex);
      logger.error(FILE_NAME+"ETCLOVTerminalIds.jsp : " +ex);
    }
  %>

<html>
 <head>
 <title>Select </title>
 <%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function populateList()
{
<%
	if(currentPageList.size() > 0)
	{		
%>
		var len = window.document.forms[0].TerminalId.length
<%
		for(int i = 0; i < currentPageList.size(); i++)
		{
%>
			window.document.forms[0].TerminalId.options[len] = new Option('<%=currentPageList.get(i)%>','<%=currentPageList.get(i)%>')
			len++;
<%
		}
%>
			document.forms[0].TerminalId.options[0].selected = true;	
			document.forms[0].TerminalId.focus();
<%
	}else{
%>
		document.forms[0].B2.focus();
<%
		}
%>
}
function setTerminalId()
{
	if(document.forms[0].TerminalId.selectedIndex == -1)
	{
		alert('<fmt:message key="Please_select_a_TerminalId" bundle="${lang}"/>')
	}
	else
	{
		var len		='<%= fromWhat %>';
		var cond 	='<%= ArrayOrNot%>' ;
		temp 		= document.forms[0].TerminalId.options[document.forms[0].TerminalId.selectedIndex].value
		if(cond=='NoArray')
			window.opener.document.forms[0].<%=wheretoset%>.value=temp;
		else if((parseInt(len) > 0) && (cond == 'YesArray'))
		{
			if(document.forms[0].TerminalId.selectedIndex==-1)
			{
				alert('<fmt:message key="Please_select_a_Id" bundle="${lang}"/>');
			}
			else
			{
				window.opener.document.forms[0].<%=wheretoset%>[len].value=temp;	
			}
		}
	//	else
	//		window.opener.document.forms[0].terminalId.value=temp;
		resetValues();
	}
}

function onEnterKey()
{

	if(event.keyCode == 27){
		resetValues();
	}
	if(event.keyCode == 13)
	{
		if(document.forms[0].TerminalId.selectedIndex == -1)
		{
			alert("Please select a TerminalId")
		}
		else
		{
			var len		='<%= fromWhat %>';
			var cond 	='<%= ArrayOrNot%>' ;
			temp 		= document.forms[0].TerminalId.options[document.forms[0].TerminalId.selectedIndex].value
			if(cond=='NoArray')
				window.opener.document.forms[0].<%=wheretoset%>.value=temp;
			else if((parseInt(len) > 0) && (cond == 'YesArray'))
			{
				if(document.forms[0].TerminalId.selectedIndex==-1)
				{
					alert("Please select a Id");
				}
				else
				{
					window.opener.document.forms[0].<%=wheretoset%>[len].value=temp;	
				}
			}
		
			resetValues();
		}
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=terminalId";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=terminalId";
    document.forms[0].submit();   
}

</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body onLoad=populateList() onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata'>
<form method="post" action="">
<b> <center>
<%
//@@ Srivegi Modified on 20050420 
if(request.getParameter("module")!=null){%>Location Ids <%}else{%> <%=((LocalizationContext)application.getAttribute("lang")).getResourceBundle().getString("Terminal_Ids")%> <%}//@@ 20050420%> </center></b>
<%
// if the Vector has got elements
	if(currentPageList.size() > 0)
	{// begin of if loop
%>
<center>
<select size=10 name="TerminalId" onDblClick='setTerminalId()' onKeyPress='onEnterKey()' class=select>
</select>
</center>
<center>
<br>
<input type="button" value='<fmt:message key="Ok" bundle="${lang}"/>' name="B1" onClick="setTerminalId()" class="input">
<input type="button" value='<fmt:message key="Cancel" bundle="${lang}"/>' name="B2" onClick="resetValues()" class="input"></center>
<TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'><fmt:message key="Pages" bundle="${lang}"/> : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table><%
	}// end of if loop
	else
	{// begin of else loop
%>
<center>
<textarea rows="6" name="notes" cols="30" class=select><fmt:message key="No_Data_Available" bundle="${lang}"/>.
</textarea>
<br><br>
<input type="button" value='<fmt:message key="Close" bundle="${lang}"/>' name="B2" onClick="window.close()" class="input">
<%
	}// end of else loop
%>

</center>
</form>
</body>
</html>

