
<!--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%

	Program Name	:ETCLOVLocationIds.jsp
	Module Name		:ETrans
	Task			:Location
	Sub Task		:LocationLOV
	Author Name		:
	Date Started	:September 20,2001
	Date Completed	:September 20,2001
	Date Modified	:
	Description		:This file is invoked when clicked on the Location LOV in the LocationEnterId Screen. In this all the Location details particular to that
					LocationId are displayed in the List Box. Once Selected any one of the Location ID ,Details related to that LocationIdETCLOVLocati
					are displayed in the respective Text Fields. If no Locations are available for this LocationId then a Text Area with a message
					'No Location Details are available for this LocationId'.
					This file will interacts with ETransUtiltitesSessionBean the method getLocationIds which inturn
					retrive the details.		

-->

<%@ page import="javax.naming.InitialContext,org.apache.log4j.Logger,
				java.util.ArrayList,
				com.qms.setup.ejb.sls.SetUpSession,
                com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.etrans.setup.country.bean.CountryMaster,
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig" %>
        
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVLocationIds.jsp ";
%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/><!-- Added by rk -->

<%
  logger  = Logger.getLogger(FILE_NAME);	
  String operation            =   request.getParameter("Operation");//ADDED BY RK
	ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;
	ArrayList locationIds	=null;	// ArrayList to store locationIds
	int len					=0;		//len to store length of locationIds
	String whereClause		=null;
	String wheretoset		="";
	String ArrayOrNot		= null;
	String fromWhat			= request.getParameter("fromWhat");
	String from				= request.getParameter("from");
	//System.out.println("fromfromfromfromfromfromfromfromfromfromfromfromfromfrom ::: "+from);
	String countryId		= request.getParameter("countryId");
	String shipmentMode     = request.getParameter("shipmentMode");//added by rk

	
	//String from				=null;
	String module			= null;
     String		searchString	="";
	 String		searchString2	="";
	 String		accessLevel		="";
	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("locationIds");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}

  
    if(listHandler == null)
    {
        System.out.println("Handler is not null");
        requiredAttributes  = new ArrayList();
        if(fromWhat == null)
			fromWhat="0";
		else
			fromWhat = fromWhat;
		searchString	= request.getParameter("searchString");
		searchString2	= request.getParameter("searchString2");
		
		if(searchString == null)
			searchString="";
		else
			searchString = searchString.trim();

		if(searchString2==null)
			searchString2="";
		else
			searchString2=searchString2.trim();

		module = request.getParameter("Module");
		
		if(request.getParameter("wheretoset")!=null)
			wheretoset=request.getParameter("wheretoset");	
		else
			wheretoset = "locationId";	
		
		if(request.getParameter("ArrayOrNot")!=null)
			ArrayOrNot=request.getParameter("ArrayOrNot");
		else
			ArrayOrNot="NoArray";
		
		if(request.getParameter("whereClause")!=null)
			whereClause=request.getParameter("whereClause");
		else
			whereClause="";
		requiredAttributes.add(whereClause);
		requiredAttributes.add(wheretoset);
		requiredAttributes.add(ArrayOrNot);
		requiredAttributes.add(searchString2);
		requiredAttributes.add(from);

		//Logger.info(FILE_NAME,"fromWhat : " +fromWhat+" whereClause---> "+whereClause+" ArrayOrNot---> "+ArrayOrNot+"  wheretoset--->"+wheretoset);
		//if(locationIds==null)
		//{
//			String where_condition="";
//			where_condition=" WHERE LOCATIONID LIKE '"+searchString+"%' " ;
			
			if("HO_TERMINAL".equals(loginbean.getAccessType()))
				accessLevel = "H";
			else if("ADMN_TERMINAL".equals(loginbean.getAccessType()))
				accessLevel = "A";
			else
				accessLevel = "O";

			try
			{
			InitialContext initial			=	new InitialContext(); // variable to get initial context for JNDI
			SetUpSessionHome home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");	// looking up the Bean
			SetUpSession remote		=	(SetUpSession)home.create();

			
//			locationIds						=	remote.getLocationIds(where_condition);
		//	Logger.info("inelssss",from+"shipmentMode"+shipmentMode+""+(shipmentMode!=null && !"buyrates".equals(from)));
			if("cartage".equalsIgnoreCase(from))//@@Added by Yuvraj for CR_DHLQMS_1006
			{
				locationIds	=	remote.getLocationsUnderTerminal(loginbean.getTerminalId(),accessLevel,searchString,shipmentMode);//@@Yuvraj*/
			}
			else if("location".equalsIgnoreCase(from))
			{
			   locationIds	=	remote.getLoctIds(loginbean,searchString);
			}
			else if("CSR".equalsIgnoreCase(from))
			{
				locationIds	=	remote.getLocationIdsForCountry(searchString,searchString2,loginbean.getTerminalId(),shipmentMode);
			}

			else
			{
				if(searchString2.equals("")){
					locationIds						=	remote.getLocationIds(searchString,loginbean.getTerminalId(),operation,shipmentMode);//added by rk
					//Logger.info(FILE_NAME,"searchString2 : " +searchString2);
				}
				else if(searchString2.trim().length()>0){
					locationIds						=	remote.getLocIds(searchString,searchString2,loginbean.getTerminalId(),operation,shipmentMode);//ADDED BY RK
					//Logger.info(FILE_NAME,"searchString2 :33 " +searchString2);
				}
				else if(shipmentMode!=null && !"buyrates".equals(from))//added by rk
						locationIds				=	remote.getLocationIdsAndPorts(loginbean.getTerminalId(),operation,searchString,shipmentMode);
				else 
					locationIds				=	remote.getLocationIds(searchString,operation,loginbean.getTerminalId(),shipmentMode);//ADDED
			}

			/*
				Here where condition should be passed within quotes when required in of passsing in query.
				If no condition is to passed then keep the String empty with quotes.
			*/
			if(locationIds != null)
			{
				len	=	locationIds.size();
				//System.out.println("the value of lenth is "+len);
			}
			
	
	if(locationIds!=null)
	{
		listHandler   = new LOVListHandler(locationIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
		session.setAttribute("locationIds", listHandler);
		listHandler = (LOVListHandler)session.getAttribute("locationIds");
	}
	}
	
	catch(Exception e)
	{
		//Logger.error(FILE_NAME,"ETCLOVLocationIds.jsp : " , e.toString());
    logger.error(FILE_NAME+"ETCLOVLocationIds.jsp : " + e.toString());
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
	String 		fileName	= "ETCLOVLocationIds.jsp";  

	 try
    {
       // System.out.println("Hai Size is " + currentPageList.size());
        if(requiredAttributes!=null)
        {
            whereClause			= (String)requiredAttributes.get(0);
            wheretoset			= (String)requiredAttributes.get(1);
            ArrayOrNot			= (String)requiredAttributes.get(2);
			searchString2		= (String)requiredAttributes.get(3);
			from				= (String)requiredAttributes.get(4);
            //System.out.println("Hai334555 " + wheretoset);
        }
    }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"ETCLOVTrackingIds.jsp : " +ex);
      logger.error(FILE_NAME+"ETCLOVTrackingIds.jsp : " +ex);
    }

%>
<html>
<head>
<title>Select </title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populatelocationNames()
{

<%

	for( int i=0;i<currentPageList.size();i++ )
	{// begin of for loop
		String str	=	currentPageList.get(i).toString();
%>
		document.forms[0].locationNames.options[ <%= i %> ] = new Option('<%= str %>','<%=str%>','<%= str %>');
<%
	}//end of for loop
	if(currentPageList.size() > 0)
	{
%>
			document.forms[0].locationNames.options[0].selected = true;	
			document.forms[0].locationNames.focus();

<%
	}else{
%>
		document.forms[0].B2.focus();
<%
		}
%>
}
function setlocationNames()
{
	if(document.forms[0].locationNames.selectedIndex == -1)
	{
		alert("Please select atleast one LocationId")
	}
	else
	{
		
<%
	if(module == null)
	{
%>
		var len		=<%=fromWhat%>;
		
		var cond 	=	'<%= ArrayOrNot%>' ;
		var index	=	document.forms[0].locationNames.selectedIndex;
		firstTemp	=	document.forms[0].locationNames.options[index].value;
		thirdTemp	=	document.forms[0].locationNames.options[index].value;
		secondTemp	=	document.forms[0].locationNames.options[index].value;
		firstIndex	=	0;
		lastIndex	=	firstTemp.indexOf('[');	
		firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		temp		=   firstTemp.toString();
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);

		var whereClause = "<%=whereClause%>";
	
	
		if(whereClause=="" && cond=='NoArray')
		{
			  if('<%=from%>'!=null && '<%=from%>'=='GateWay')
			{		   
			   window.opener.document.forms[0].<%=wheretoset%>.value=temp;
			   window.opener.document.forms[0].gatewayId.value=temp+'G';
			   window.opener.document.forms[0].gatewayName.value=temp1;
		       window.opener.document.forms[0].<%=wheretoset%>.focus();
			}
			else if('<%=from%>'=='Quote')
			{
				window.opener.document.forms[0].<%=wheretoset%>.value=temp;
				//window.opener.document.forms[0].locationName.value=temp1;
				if('<%=wheretoset%>'=='originLoc')
				   window.opener.document.forms[0].originLocationName.value=temp1;
				else
				   window.opener.document.forms[0].destLocationName.value=temp1;
				window.opener.document.forms[0].<%=wheretoset%>.focus();
				window.opener.populateCAP();
			}
      else if('<%=from%>'=='location')
			{
				  window.opener.document.forms[0].<%=wheretoset%>.value=thirdTemp;
						window.opener.document.forms[0].<%=wheretoset%>.focus(); 
			}
      else
			 {
				window.opener.document.forms[0].<%=wheretoset%>.value=temp;
				window.opener.document.forms[0].<%=wheretoset%>.focus();
			 }

		}
		else if((parseInt(len) > 0) && (cond == 'YesArray'))
		{
			if(document.forms[0].locationNames.selectedIndex==-1)
			{
				alert("Please select a Id");
			}
			else
			{
				window.opener.document.forms[0].<%=wheretoset%>[len].value=temp//.substring(index+2,index2);	
				window.opener.document.forms[0].<%=wheretoset%>[len].focus();
				resetValues();
			}
		}
	//	else
	//		window.opener.document.forms[0].withLocationId.value=temp;
			resetValues();
	}
<%
  }
  else
  {
 %>
 		var len		=	<%= fromWhat %>;
		var cond 	=	'<%= ArrayOrNot%>' ;
		var index	=	document.forms[0].locationNames.selectedIndex;
		firstTemp	=	document.forms[0].locationNames.options[index].value;
		firstIndex	=	0
		lastIndex	=	firstTemp.indexOf('[');	
		firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		window.opener.document.forms[0].locationId.value=firstTemp;
		window.opener.document.forms[0].locationId.focus();
		window.opener.document.forms[0].LOVlocationId.focus();
		window.opener.document.forms[0].locationId.focus();
   }	
<%
  }
%>  			
}


function onEnterKey()
{

	if(event.keyCode == 13)
	{
			if(document.forms[0].locationNames.selectedIndex == -1)
			{
				<%if(shipmentMode!=null && ("2".equals(shipmentMode)||"Sea".equalsIgnoreCase(shipmentMode))){%>
				alert("Please select atleast one LocationId")
				<%}else{%>
					alert("Please select atleast one PortId")
				<%}%>
			}
			else
			{
<%
				if(module == null)
				{
%>
							var len		=<%= fromWhat %>;
							var cond 	='<%= ArrayOrNot%>' ;
							var index	=document.forms[0].locationNames.selectedIndex;
							firstTemp	=document.forms[0].locationNames.options[index].value;
              thirdTemp	=	document.forms[0].locationNames.options[index].value;
							secondTemp	=document.forms[0].locationNames.options[index].value;
							firstIndex	=	0;
							lastIndex	=	firstTemp.indexOf('[');	
							firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
							temp		=	firstTemp.toString();
							lastIndex1	=	secondTemp.lastIndexOf('[')+1;
							lastIndex2	=	secondTemp.lastIndexOf(']');
							temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
							var whereClause = "<%=whereClause%>";
	
	
							if(whereClause=="" && cond=='NoArray')
							{
								if('<%=from%>'=='Quote')
								{
									window.opener.document.forms[0].<%=wheretoset%>.value=temp;
									window.opener.document.forms[0].locationName.value=temp1;
									window.opener.document.forms[0].<%=wheretoset%>.focus();
									window.opener.populateCAP();
								}else if('<%=from%>'=='location')
                {
                      window.opener.document.forms[0].<%=wheretoset%>.value=thirdTemp;
                      window.opener.document.forms[0].<%=wheretoset%>.focus(); 
                }
                else
                 {
                  window.opener.document.forms[0].<%=wheretoset%>.value=temp;
                  window.opener.document.forms[0].<%=wheretoset%>.focus();
                 }
								
							}
							else if((parseInt(len) > 0) && (cond == 'YesArray'))
							{
								if(document.forms[0].locationNames.selectedIndex==-1)
								{
									alert("Please select a Id");
								}
								else
								{
									window.opener.document.forms[0].<%=wheretoset%>[len].value=temp//.substring(index+1,index1);	
									window.opener.document.forms[0].<%=wheretoset%>[len].focus();
									resetValues();
								}
							}
	
							resetValues();
					}//end of else 
<%
				}
				else
				{
%>
						var len		=<%= fromWhat %>;
						var cond 	='<%= ArrayOrNot%>' ;
						var index	=document.forms[0].locationNames.selectedIndex;
						firstTemp	=document.forms[0].locationNames.options[index].value;
						firstIndex	=	firstTemp.indexOf(0);
						lastIndex	=	firstTemp.indexOf('[');	
						firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
						temp		=	firstTemp.toString(); 	
						window.opener.document.forms[0].locationId.value=temp;
						window.opener.document.forms[0].locationId.focus();
						window.opener.document.forms[0].LOVlocationId.focus();
						window.opener.document.forms[0].locationId.focus();
			}	
<%
				}//end of else
%>  			
		}
		if(event.keyCode == 27){
			resetValues();
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=locationIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=locationIds";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populatelocationNames()" onKeyPress='onEscKey()'>
<form method="post" action="">
<center>
<%if(shipmentMode!=null && ("2".equals(shipmentMode)||"Sea".equalsIgnoreCase(shipmentMode))){%>
<b><font face="Verdana">Port Ids</font></b>
<%}else{%>
<b><font face="Verdana">Location Ids</font></b>
<%}%>
</center>
<br>
<%
	if(currentPageList.size() > 0)
	{// begin of if
%>

<center>
<select size="10" name="locationNames" onDblClick='setlocationNames()' onKeyPress='onEnterKey()' class="select">
</select>
</center>
<br>
<center>
<input type="button" value=" Ok " name="OK" onClick="setlocationNames()" class="input">
<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
<TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>
<%
	}// end if
	else
	{// begin of else
%>
<br><br>
<center>
<textarea cols=30 class='select' rows=6 readOnly > <%if("buyrates".equals(from) && "2".equals(shipmentMode)) 
														{%>No Ports Ids available.<%}
														else
														{%>No Location Ids available.<%}%>
</textarea>
<br><br>
<input type="button" value="Close" name="B2" onClick="window.close()" class="input">
<%
	}// end of else
%>
</center>
</form>
</body>
</html>
