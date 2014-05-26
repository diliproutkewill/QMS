<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%
/*	Program Name	: CarrierContractsDestinationGatewayLOV.jsp
	Module name		: HOSetup
	Task			: Carrier Contracts
	Sub task		: to display all Gateways
	Author Name		: A.Hemanth Kumar
	Date Started	: September 16, 2001
	Date completed	: September 17, 2001

	Description		:
	   This file is invoked when clicked on the DestinationGateway LOV. It displays the all Gateways in the list box.
       Once user selects the gateway id, it assign the appropriate field.
       This file will interacts with CarrierContractSessionBean 
*/
%>
<%@ page import =	"javax.naming.InitialContext,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig"%>
<jsp:useBean id="loginbean" class ="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope ="session"/>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVGatewayIds.jsp ";
%>

<%	
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;
	ArrayList	gatewayIds 		=	null;
	String		gatewayOrHub	=	"Gateway";
	int			len 			=	0;
	String		shipmentMode	=	request.getParameter("ShipmentMode");
//	String		terminalType	=	(String)session.getAttribute("TerminalType");
	String		terminalType	=	"";
	String		whereClause		=	null;
	String		searchString	=	"";
	String		where_condition	=	"";		
    String		type			=	request.getParameter("type");
	if(shipmentMode != null)
	{
		if(shipmentMode.equals("Truck"))
		{
			gatewayOrHub	=	"Hub";
		}
	}

	if(request.getParameter("pageNo")!=null)
	{
		try
		{
	  
			listHandler           = (LOVListHandler)session.getAttribute("gatewayIds");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}
 

	if(listHandler == null)
	{
	//System.out.println("Handler is not null");
        requiredAttributes  = new ArrayList();
	 if(request.getParameter("terminalType")!=null)
		 terminalType	=	request.getParameter("terminalType");
	 else
		 terminalType	=	"";
	 
	searchString	= request.getParameter("searchString");
	//Logger.info(FILE_NAME,"Skip ......... >>> "+ terminalType);
	if(searchString == null)
		searchString="";
	else
		searchString = searchString.trim();
	
	whereClause = 	request.getParameter("whereClause");
	if(request.getParameter("ShipmentMode")!=null)
		request.getParameter("ShipmentMode");
	
		requiredAttributes.add(shipmentMode);
		requiredAttributes.add(terminalType);
		requiredAttributes.add(type);
		requiredAttributes.add(searchString);
		requiredAttributes.add(whereClause);
	try
	{
	if(gatewayIds == null)
	{
		InitialContext initial			= new InitialContext();
         	SetUpSessionHome home 	= ( SetUpSessionHome )loginbean.getEjbHome( "SetUpSessionBean" );
	        SetUpSession remote		= (SetUpSession)home.create();
			 //Logger.info(FILE_NAME,"where_condition:	"+where_condition);
               gatewayIds 					= remote.getGatewayIds(terminalType,searchString,shipmentMode,type);
		
		if(gatewayIds != null)
		{ 
			len = gatewayIds.size();
	       }
     if(gatewayIds!=null)
        {
            listHandler                     = new LOVListHandler(gatewayIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
            session.setAttribute("gatewayIds", listHandler);
            listHandler = (LOVListHandler)session.getAttribute("gatewayIds");
        }  
  }
 }
  catch(Exception ee)
  {
 	//Logger.error(FILE_NAME,"ETCLOVGatewayIds.jsp : Exception : ", ee.toString());
  logger.error(FILE_NAME+"ETCLOVGatewayIds.jsp : Exception : "+ ee.toString());
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
	String 		fileName	= "ETCLOVGatewayIds.jsp"; 
	try
	{
		//System.out.println("Hai Size is " + currentPageList.size());
		if(requiredAttributes!=null)
		{
		    shipmentMode        = (String)requiredAttributes.get(0);
		    terminalType        = (String)requiredAttributes.get(1);
		    type         = (String)requiredAttributes.get(2);
		    searchString     = (String)requiredAttributes.get(3);
		    whereClause = (String)requiredAttributes.get(4);
		      
		    System.out.println("Hai222");
		    //System.out.println("Hai334555 " + shipmentMode);
		}
     }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"ETCLOVGatewayIds.jsp : " +ex);
      logger.error(FILE_NAME+"ETCLOVGatewayIds.jsp : " +ex);
    }
%>
<html>
<title>Select</title>
<head>
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateGatewayIds()
{	
 
<%	
   
	for( int i=0;i<currentPageList.size();i++ )
	{
		String str = currentPageList.get(i).toString();	
%>		
		document.forms[0].gatewayIds.options[ <%= i %> ] = new Option('<%= str %>','<%= str %>');
<%	
	}

	if(currentPageList.size() > 0)
	{
%>
		document.forms[0].gatewayIds.options[0].selected = true;	
		document.forms[0].gatewayIds.focus();
<%
	}else{	
%>
		document.forms[0].B2.focus();
<%
		}
%>
}
function setGatewayId()
{	
	
	var shipmentMode='<%=shipmentMode%>';
	if(document.forms[0].gatewayIds.selectedIndex == -1)
	{
		alert("Please select the <%=gatewayOrHub%>Id");	
	}
	else
	{	
		firstTemp = document.forms[0].gatewayIds.options[document.forms[0].gatewayIds.selectedIndex].value
		temp = firstTemp.toString();
		if(shipmentMode=='Air' || shipmentMode=='1' || shipmentMode=='2' || shipmentMode=='4')
		{
			
			sel = window.opener.document.forms[0].terminal.value;
			if( sel=="origin" )
			{
				window.opener.document.forms[0].originGatewayId.value = temp;
			}
			else
			{
				window.opener.document.forms[0].destinationGatewayId.value = temp;
			}
		}
		else
		{   
			window.opener.document.forms[0].gatewayId.value = temp;
			window.opener.document.forms[0].gatewayId.focus();
		}
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
			var shipmentMode='<%=shipmentMode%>';
			if(document.forms[0].gatewayIds.selectedIndex == -1)
			{
				alert("Please select the <%=gatewayOrHub%>Id");	
			}
			else
			{	
				firstTemp = document.forms[0].gatewayIds.options[document.forms[0].gatewayIds.selectedIndex].value
				temp = firstTemp.toString();
				if(shipmentMode=='Air')
				{
					
					sel = window.opener.document.forms[0].terminal.value;
					if( sel=="origin" )
					{
						window.opener.document.forms[0].originGatewayId.value = temp;
					}
					else
					{
						window.opener.document.forms[0].destinationGatewayId.value = temp;
					}
				}
				else
				{   
					window.opener.document.forms[0].gatewayId.value = temp;
					window.opener.document.forms[0].gatewayId.focus();
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=gatewayIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=gatewayIds";
    document.forms[0].submit();   
}

</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
				<body onLoad="populateGatewayIds()" onUnLoad='toKillSession()' class='formdata' onKeyPress='onEscKey()'>
				<form> 
				<center><b><%=gatewayOrHub%>Ids</b> </center>   <br>
<%
if(currentPageList.size() > 0)
 {
%>	

				<center>
				<select size="10" name="gatewayIds" class='select'  onDblClick='setGatewayId()' onKeyPress='onEnterKey()'>    
				</select>
				</center>
				<br>
				<center>
				<input type="button" value=" Ok " name="OK" class='input' onClick="setGatewayId()" >
				<input type="button" value="Cancel" name="B2" onClick="resetValues()" class='input'>
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
			<center>
			<textarea cols=30 class='select' rows=6 readOnly >No Gateway Ids available
			</textarea>
			<br>
			<br>
			<input type="button" value="Close" name="B2" onClick="window.close()" class='input'>
<%
  }
%>  		  
  
   </center>
</form>
</body>
</html>


