<%--

	Program Name	:ETransLOVVendorIds.jsp
	Module Name		:ETrans
	Task					:VendorRegistration
	Sub Task			:VendorIds Lov
	Author Name		:Nageswara Rao.D
	Date Started		:21-01-2003[dd/mm/yyyy]
	Date Completed	:21-01-2003[dd/mm/yyyy]
	Date Modified	:
	Description		:This file is invoked when clicked on the VendorId LOV. In this, all the VendorIds particular to that
					Terminal are displayed in the List Box. Once Selected any one of the VendorId, that VendorId
					is displayed in the respective Text Field. 
					This file interacts with SetUpSession SessionBean to call the method getVendorIds() which inturn 
					retrieves the VednorIds. 

--%>	
<%@ page import="javax.naming.InitialContext,
				java.util.ArrayList,
				org.apache.log4j.Logger,				
				com.qms.setup.ejb.sls.SetUpSession,
                com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig" %>
<jsp:useBean id="loginbean" class ="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope ="session"/>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>


<%!
  private static Logger logger = null;
%>

<%	
	ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;
	String					FILE_NAME			=		"ETransLOVVendorIds.jsp ";
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList				vendorIds			=		null;
	int						len					=		0;
	String					str					=		null;
	String					value				=		"No Vendor Ids are available";
	String					searchString		=		"";	
	String					terminalId			=		"";
	String					indicator			=		"";
	String					careerId			=		"";
	String					shipmentMode		=		"";
    String                  masterid            =       null;
	String					from				=		null;
	SetUpSessionHome home				=		null;
	SetUpSession		remote				=		null;
	
		searchString	= request.getParameter("searchString");
		if(searchString == null)
			searchString="";
		else
		  searchString = searchString.trim();
		terminalId		=	request.getParameter("terminalId");
		indicator		=	request.getParameter("indicator");
		careerId		= request.getParameter("careerId");
		indicator		=	request.getParameter("indicator");
		shipmentMode	=	request.getParameter("shipmentMode");
		masterid		=	request.getParameter("masterid");
		from				=	request.getParameter("from");
		if (masterid== null)
		masterid="";
	
if(request.getParameter("pageNo")!=null)
{
	try
	{
  
		listHandler           = (LOVListHandler)session.getAttribute("vendorIds");
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
	requiredAttributes.add(terminalId);
	requiredAttributes.add(indicator);
	requiredAttributes.add(careerId);
	requiredAttributes.add(indicator);
	requiredAttributes.add(shipmentMode);
	requiredAttributes.add(masterid);
	requiredAttributes.add(searchString);
	requiredAttributes.add(from);

		//Logger.info(FILE_NAME,"searchString "+searchString);
    logger.info(FILE_NAME+"searchString "+searchString);
	try
	{
		InitialContext	initial				=		new InitialContext();	// looking up JNDI context
		home								=		(SetUpSessionHome)initial.lookup("SetUpSessionBean");	// looking up ETransUtitlities Session Bean	
		remote								=		(SetUpSession)home.create();
		if(indicator != null)
			// @@ Suneetha Added a New Parameter (operation) to the folowing method call on 20050430
			vendorIds					=		remote.getVendorIds(terminalId,searchString,indicator,masterid,request.getParameter("operation"));	
			// @@ 20050430
		else
			vendorIds					=		remote.getVendorIdsForAll(terminalId,searchString,careerId,shipmentMode,masterid);
		
		if(vendorIds != null)
		{
			len	= vendorIds.size();
		}
    if(vendorIds!=null)
        {
            listHandler                     = new LOVListHandler(vendorIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
            session.setAttribute("vendorIds", listHandler);
            listHandler = (LOVListHandler)session.getAttribute("vendorIds");
        }
    
	}
	catch(Exception	e)
	{
		//Logger.error(FILE_NAME,"ETCLOVVendorIds.jsp: Exception5", e.toString());
    logger.error(FILE_NAME+"ETCLOVVendorIds.jsp: Exception5"+ e.toString());
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
	String 		fileName	= "ETransLOVVendorIds.jsp";  

	try
    {
        System.out.println("Hai Size is " + currentPageList.size());
        if(requiredAttributes!=null)
        {
            terminalId        = (String)requiredAttributes.get(0);
            indicator        = (String)requiredAttributes.get(1);
            careerId         = (String)requiredAttributes.get(2);
            indicator     = (String)requiredAttributes.get(3);
            shipmentMode = (String)requiredAttributes.get(4);
			masterid = (String)requiredAttributes.get(5);
			searchString = (String)requiredAttributes.get(6);
			from = (String)requiredAttributes.get(7);
              
            System.out.println("Hai222");
            System.out.println("Hai334555 " + careerId);
        }
     }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"ETSLOVTerminalIds.jsp : " +ex);
      logger.error(FILE_NAME+"ETSLOVTerminalIds.jsp : " +ex);
    }
%>
<html>
<head>
<title>Select</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<%@ include file="/ESEventHandler.jsp" %>
<script>
var isAttributeRemoved  = 'false';
function  populateVendorNames()
{
<%
	
	for(int i=0;i<currentPageList.size();i++ )
	{// for loop begin
		str=currentPageList.get(i).toString();

%>
		document.forms[0].vendorIds.options[ <%= i %> ] = new Option('<%= str %>','<%= str %>');
<%
	}// for loop end
	if(currentPageList.size() > 0)
	{
%>
			document.forms[0].vendorIds.options[0].selected = true;	
			document.forms[0].vendorIds.focus();

<%
	}else{

%>
			document.forms[0].B2.focus();
<%
	}
%>
}
function setVendorNames()
{
	if(document.forms[0].vendorIds.selectedIndex == -1)
	{
		alert("Please select atleast one Vendor Id")
	}
	else
	{
		var	index	=document.forms[0].vendorIds.selectedIndex;
		firstTemp	=document.forms[0].vendorIds.options[index].value;
		firstIndex	=firstTemp.indexOf('[');
	//	lastIndex	=firstTemp.indexOf(']');
		firstTemp	=firstTemp.substring(0,firstIndex-1);
		temp		=firstTemp.toString();
       	
  <%if ((from!=null)&&(from.equals("Second"))){%>
          window.opener.document.forms[0].destvendorId.value=temp;
   <%}else{%>       
			window.opener.document.forms[0].vendorId.value=temp;
    <%}%>  			
		resetValues();
	}
}

function onEnterKey()
{

	if(event.keyCode == 13)
	{
		if(document.forms[0].vendorIds.selectedIndex == -1)
		{
			alert("Please select atleast one Vendor Id")
		}
		else
		{
			var	index	=document.forms[0].vendorIds.selectedIndex;
			firstTemp	=document.forms[0].vendorIds.options[index].value;
			firstIndex	=firstTemp.indexOf('[');
			lastIndex	=firstTemp.indexOf(']');
			firstTemp	=firstTemp.substring(0,firstIndex-1);
			temp		=firstTemp.toString();
      <%if ((from!=null)&&(from.equals("Second"))){%>
          window.opener.document.forms[0].destvendorId.value=temp;
   <%}else{%>       
			window.opener.document.forms[0].vendorId.value=temp;
    <%}%>  
			resetValues();
		}
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=vendorIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=vendorIds";
    document.forms[0].submit();   
}


</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populateVendorNames()" onKeyPress='onEscKey()'>
<form>
		<center>
			<b><font face="Verdana" size="2">Vendor Ids</font></b>
		</center>
		

<%
	if(currentPageList.size() >0)
	{//begin of if loop
%>
				<br>
				<center>
					<select size="10" name="vendorIds" onDblClick='setVendorNames()' onKeyPress='onEnterKey()' class="select">
					</select>
				</center>
				<center>
				<br>
					<input type="button" value=" Ok " name="OK" onClick=setVendorNames() class="input">
					<input type="button" value="Cancel" name="B2" onClick="resetValues()" class="input">
				</center>
        <TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>
<%
	}// end of if loop
	else
	{// begin  of else loop
%>
			<br><br>
			 <center><textarea rows=6 name="ta" class='select' cols="30" readOnly ><%= value %></textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}// end of else loop
 %>

</form>
</body>
</html>
