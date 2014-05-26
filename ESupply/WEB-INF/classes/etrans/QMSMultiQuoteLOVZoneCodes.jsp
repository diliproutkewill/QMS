<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name		: QMSMultiLOVZoneCodes.jsp
Product Name		: QMS
Module Name			: Quote
Task				: 
Date started		: 12th Aug 2005 	
Date modified		:  
Author    			: S Anil Kumar
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
	private static final String FILE_NAME	=	"QMSMultiLOVZoneCodes.jsp";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList			ZoneCodesList		=	null; 
	int					len					=	0;	  
	LOVListHandler		listHandler			=	null;
	ArrayList			requiredAttributes	=	null;
	String				searchString1		=	request.getParameter("searchString1");
	String				searchString2		=	request.getParameter("searchString2");
	String				searchString3		=	""; //Added by RajKumari on 11/10/2008 for WPBN 143511...
	String				shipmentMode		=	request.getParameter("shipmentMode");
	String				consoleType			=	request.getParameter("consoleType");
	String				whereToset			=	request.getParameter("wheretoset");
	String				fromWhere			=	request.getParameter("fromWhere");
	String              rownum              =   request.getParameter("rownum");
	String              multiquote          =   request.getParameter("MultiQuote") ;
	String              MultishpMode        = null;
	SetUpSessionHome	home				=	null;
	SetUpSession	    remote				=	null;
  logger.info("hi...");
	if(request.getParameter("pageNo")!=null)
	{
		try
		{
			listHandler           = (LOVListHandler)session.getAttribute("ZoneCodesList");
			requiredAttributes    = listHandler.requiredAttributes; 
		}
		catch (Exception e)
		{
			listHandler = null;
		}
	}
	try
	{
		
          if(shipmentMode.equalsIgnoreCase("AIR")){
                  MultishpMode ="1";
		  }
		  else if(shipmentMode.equalsIgnoreCase("SEA")){
                       MultishpMode ="2";
		  }
                    

		logger.info("searchString1"+searchString1);
		if(listHandler == null)
		{
			requiredAttributes	=	new ArrayList();
			requiredAttributes.add(whereToset);
			logger.info("requiredAttributes"+requiredAttributes);
			home				=	(SetUpSessionHome )loginbean.getEjbHome("SetUpSessionBean" );//looking up SetUpSessionBean	
			remote				=	(SetUpSession)home.create();
            //Modified by RajKumari on 11/10/2008 for WPBN 143511...    
			//@@Added by Yuvraj for CR_DHLQMS_1005
			if("Cartage".equals(fromWhere)){
				
				ZoneCodesList 		=	remote.getZoneCodes(searchString1,MultishpMode,consoleType);
						
			}
			else//@@Yuvraj
			{
				
			ZoneCodesList 		=	remote.getZoneCodes(searchString1,searchString2,searchString3,MultishpMode,consoleType);	
			
			}

			if(ZoneCodesList != null)
			{
				len	= ZoneCodesList.size();
				logger.info("len"+len);
			}
			if(ZoneCodesList!=null)
			{
				listHandler             = new LOVListHandler(ZoneCodesList,FoursoftConfig.NOOFRECORDSPERPAGE,requiredAttributes);
				session.setAttribute("ZoneCodesList", listHandler);
				listHandler = (LOVListHandler)session.getAttribute("ZoneCodesList");
				logger.info("ZoneCodesList"+ZoneCodesList);
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
		logger.info("listHandler.currentPageIndex"+listHandler.currentPageIndex);
	}
	ArrayList	currentPageList	= listHandler.getCurrentPageData();
	logger.info("currentPageList"+currentPageList);
	String 		fileName	= "QMSLOVZoneCodes.jsp";
	//@@Added by Yuvraj for CR_DHLQMS_1005
	try
    {
        if(requiredAttributes!=null)
        {
			whereToset        = (String)requiredAttributes.get(0);
            //System.out.println("whereToset::" + whereToset);
        }
    }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"QMSLOVZoneCodes.jsp : " +ex);
      logger.error(FILE_NAME+"QMSLOVZoneCodes.jsp : " +ex);
	  ex.printStackTrace();
    }
	//@@Yuvraj
	
%>
<html>
<head>
<title>Zone Code LOV</title>

<script>
var isAttributeRemoved  = 'false';
function  populateZoneCodes()
{
<%
	for( int i=0;i<currentPageList.size();i++ )
	{
		String zoneCode = currentPageList.get(i).toString();
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

function setZoneCodes()
{
	if (document.forms[0].zoneCodes.selectedIndex == -1)
	{
		alert("Please select a Zone Code")
	}
	else
	{
		var index	=	document.forms[0].zoneCodes.selectedIndex;
		firstTemp	=	document.forms[0].zoneCodes.options[index].value;

		var str	=	firstTemp.split("[");
		var str1=   firstTemp.split("-");
	   <%  int count = 0; 
	   if(searchString2!=null)
		{
	   if(searchString2.indexOf("-")== -1)
		{

	
		   for (int j = 0;j < searchString2.length();j++) 
          {
	         	if (!(Character.isDigit(searchString2.charAt(j))))
            {
                  count++;
                  break;
            }
          }
		 }
		 
		}
		 else
			{
		    count++;
		 }
		 %>
		
       //alert('<%=count%>')
		 
		 	
			if('<%=whereToset%>'=='shipperZipCode')
		{
		
			opener.parent.text.document.forms[0].<%="shipperZone"+rownum%>.value=str[1].substring(0,str[1].indexOf("]"));
	
		
		<% if(count>0)
         {%>
			
      opener.parent.text.document.forms[0].<%="shipperZipCode"+rownum%>.value=str1[0];
				<%}%>	 
		window.opener.populateCAP('<%=rownum%>');//added by rk
		
		}
		else if('<%=whereToset%>'=='consigneeZipCode')
		{
		
		 opener.parent.text.document.forms[0].<%="consigneeZone"+rownum%>.value=str[1].substring(0,str[1].indexOf("]"));
			<%
				if(count>0)
			{	%>				
               opener.parent.text.document.forms[0].<%="consigneeZipCode"+rownum%>.value=str1[0];
			<%}%>
			  	window.opener.populateCAP('<%=rownum%>');
			
		}
		else
           			opener.parent.text.document.forms[0].<%=whereToset+rownum%>.value=firstTemp;
	
		
		resetValues();

	}

}


function onEnterKey()
{
	if(event.keyCode == 13)
	{
				setZoneCodes();
				resetValues();
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
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body onLoad="populateZoneCodes()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata'><font face="Verdana" size="2">
<center><b>Zip Codes[Zone Codes]</b></center>
</font>
<form>
<%
	if(currentPageList.size() > 0)
	{
%>
	<center>
	<select size="10" name="zoneCodes" selected=0 class="select" onDblClick='setZoneCodes()' onKeyPress='onEnterKey()'>
	</select></center>
	<br>
	<center>
	<input type="button" value=" Ok " name="OK" onClick="setZoneCodes()" class="input">
	<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
	</center>
	<TABLE cellSpacing=0 width=95% >
		<tr  class="formdata"> 
	
		<td width=100% align='center'>Pages : &nbsp;
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=FoursoftConfig.NOOFSEGMENTS%>" fileName="<%= fileName %>"/>
			
		</td>
		</tr>	
		</table>
  
<%
	}
	else
	{
%>
		<center><textarea rows=6 name="ta" class='select' cols="30" readOnly>No Zone(s) are available</textarea></center>
		<br>
		<center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}
%>
</form>
</body>
</html>
