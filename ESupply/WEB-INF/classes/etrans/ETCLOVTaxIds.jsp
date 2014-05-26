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
/*
	Program Name	:TaxLevelLov.jsp
	Module Name		:ETrans
	Task			:TaxMaster
	Sub Task		:Lov
	Author Name		:Ushasree.Petluri
	Date Started	:September 11,2001
	Date Completed	:September 12,2001
	Date Modified	:September 11,2001 by Ushasree.P
	Description		:This file main purpose is to get taxIds using Lov from the database.
*/
%>
<%@ page import = "javax.naming.InitialContext,
					java.util.Vector,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.setup.taxes.bean.TaxMaster,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.foursoft.esupply.common.java.FoursoftConfig" %>
<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<jsp:useBean id="loginbean" scope="session" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" />
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCLOVTaxIds.jsp ";
%>

<%
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;
	ArrayList taxMaster  =   null; 
	int len 				= 	0;										// for storing the Size of Vector  
	 String value			=	" No Tax Ids available";            // Storing the value 
	 String ForsurChargeId	=   "";
	 ForsurChargeId			=   request.getParameter("ForsurChargeId");
	 //System.out.println("the value of ForsurChargeId-->"+ForsurChargeId);
	 if(ForsurChargeId == null)
		ForsurChargeId="";
	 
	 InitialContext ictx   	= 	null; 								// declaring initialContext

	 //String operation		=	request.getParameter("oper"); 
	 // @@ Anup modified for SPETI-3718 on 20050124 
	 String operation		=	request.getParameter("Operation"); 
	 // @@ 
	 String tax_Id= request.getParameter("tax_Id");
	 //System.out.println("ETCLovTaxIds:tax_Id-->"+tax_Id+"Operation-->"+operation);
	 
	 com.qms.setup.ejb.sls.SetUpSessionHome home = null;  // declaring home
   	 com.qms.setup.ejb.sls.SetUpSession remote   = null;  // declaring remote
	 String		searchString	="";
	 if(request.getParameter("pageNo")!=null)  
	 {
		try
		{
		listHandler           = (LOVListHandler)session.getAttribute("taxMaster");
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
   
    requiredAttributes  = new ArrayList();
	 requiredAttributes.add(request.getParameter("ForsurChargeId"));
		searchString	= request.getParameter("searchString");
		if(searchString == null)
			searchString="";
		else
			searchString = searchString.trim();
	   
    	  		ictx 		= 	new InitialContext();
	 		home 		=	(com.qms.setup.ejb.sls.SetUpSessionHome)ictx.lookup("SetUpSessionBean");
			remote 		= 	home.create();
  			
			if(ForsurChargeId != null && ForsurChargeId.equals("yes"))
			{
				//System.out.println("surChargeId==>"+request.getParameter("surChargeId"));
				if(request.getParameter("surChargeId") == null)
				searchString="";
				else
				searchString = request.getParameter("surChargeId")	;
			}	
		taxMaster	= 	remote.getTaxMasterTaxIds(searchString,loginbean,ForsurChargeId,tax_Id,operation); 
    	if(taxMaster != null)
  		{
    		len =	taxMaster.size();
      }
      if(taxMaster!=null)
      {
        listHandler                     = new LOVListHandler(taxMaster,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
        session.setAttribute("taxMaster", listHandler);
        listHandler = (LOVListHandler)session.getAttribute("taxMaster");
      }
  }    
}
catch(Exception ee)
{
    //Logger.error(FILE_NAME,"Exception in TaxLevel Lov : s", ee.toString()); 
    logger.error(FILE_NAME+"Exception in TaxLevel Lov : "+ ee.toString()); 
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
    String 		fileName	= "ETCLOVTaxIds.jsp";  

	   try
		{
			//System.out.println("Hai Size is " + currentPageList.size());
			if(requiredAttributes!=null){
				ForsurChargeId        = (String)requiredAttributes.get(0);
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
<title>Select</title>
<script language ="Javascript">
var isAttributeRemoved  = 'false';
function  populateTaxLevelIds()
{	
<%
	for( int i=0;i<currentPageList.size();i++ )
	{// for begin
  	 String str = currentPageList.get(i).toString();
	   //t1 = taxMaster[i];	 
		 	
%>		
	document.forms[0].taxIdNames.options[ <%= i %> ] = new Option('<%= str %>','<%= str %>');
	document.forms[0].taxIdNames.options[ <%= i %> ].id = "<%=str%>" 
<%	
	}// for end 

	 if(currentPageList.size() > 0)
		{
  %>
	      document.forms[0].taxIdNames.options[0].selected = true;
		  document.forms[0].taxIdNames.focus();
  <% 
	    }else{
  %>
	  document.forms[0].B2.focus();
<%
		}	  
%>

}
function setTaxIdNames()
{
	
	if ( window.opener.document.forms[0].elements[1].name=="taxIdNames" || window.opener.document.forms[0].elements[1].name=="selTerminalId" )
	{  
		 if (document.forms[0].taxIdNames.selectedIndex == -1)
		 {
		
			alert("Please select atleast one Tax Id")
		 }				
		 else
		 {
    	
    		var index 	= document.forms[0].taxIdNames.selectedIndex;	
			firstTemp 	= document.forms[0].taxIdNames.options[index].value;
	  		
	  		firstIndex 	= firstTemp.indexOf(''); 
	        lastIndex  	= firstTemp.indexOf('');
			firstTemp  	= firstTemp.substring(firstIndex,lastIndex+3);
	        temp    	= firstTemp.toString();
<%
			//System.out.println("the ForsurChargeId  raghu raghu raghu--- -- -- -- >"+ForsurChargeId);
			if(ForsurChargeId != null && ForsurChargeId.equals("yes"))
			{
				
%>			
    			                     
    			window.opener.document.forms[0].surChargeId.value=temp;
<%
			}
			else
			{
				System.out.println("this is inside the else contion");
%>	
					  		  
				window.opener.document.forms[0].taxId.value=temp;
<%
			}
%>			
			
			window.opener.document.forms[0].termId.value=document.forms[0].taxIdNames.options[index].id;
	              resetValues();
		 }
 	}
    else
            resetValues();
}


function onEnterKey()
{

	if(event.keyCode == 13)
	{

		if ( window.opener.document.forms[0].elements[1].name=="taxIdNames" )
		{  
			 if (document.forms[0].taxIdNames.selectedIndex == -1)
			 {
			
				alert("Please select atleast one Tax Id")
			 }				
			 else
			 {
			
				var index 	= document.forms[0].taxIdNames.selectedIndex;	
				firstTemp 	= document.forms[0].taxIdNames.options[index].value;
				firstIndex 	= firstTemp.indexOf(''); 
				lastIndex  	= firstTemp.indexOf('');
				firstTemp  	= firstTemp.substring(firstIndex,lastIndex+3);
				temp    	= firstTemp.toString();
<%
			//System.out.println("the ForsurChargeId  raghu raghu raghu--- -- -- -- >"+ForsurChargeId);
			if(ForsurChargeId != null && ForsurChargeId.equals("yes"))
			{
				
%>				
				window.opener.document.forms[0].surChargeId.value=temp;

<%
			}
			else
			{
%>
					window.opener.document.forms[0].taxId.value=temp;
<%
			}
 %>

				       resetValues();
			 }
		}
		else
		        resetValues();
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=taxMaster";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=taxMaster";
    document.forms[0].submit();   
}
</script>
<%@ include file="/ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body  onLoad="populateTaxLevelIds()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata' >
<center><b>Tax Ids </b></center>
<form method="post" action=""> 
<%
	  if (currentPageList.size() > 0) 
  	  {//if begin
%> 
  <p align="center"><select size="10" name="taxIdNames" selected=0 class='select' onDblClick='setTaxIdNames()' onKeyPress='onEnterKey()'>    
  </select></p>
  <p align="center">
  <input type="button" value=" Ok " name="OK" onClick="setTaxIdNames()" class='input'>
  <input type="button" value="Cancel" name="Cancel" onClick="resetValues()" class='input'>
  </p>
   <TABLE cellSpacing=0 width=95%>
		<tr  class="formdata"> 
		<td width=100% align='center'>Pages : &nbsp;
        
		<pageIterator:PageIterator currentPageNo="<%= listHandler.currentPageIndex %>" noOfPages="<%= listHandler.getNoOfPages() %>" noOfSegments="<%=Integer.parseInt(loginbean.getUserPreferences().getSegmentSize())%>" fileName="<%= fileName %>"/>
		</td>
		</tr>	
		</table>

<%
     }//end if
 	 else
  	 {// else begin
 %>	
<br>
 <center><textarea rows=6 name="ta" cols="30" readOnly class='select'><%= value %></textarea></center><br>
 <center><input type="button" value="Close" name="B2" onClick="window.close()" class='input'></center>
 <%
     }//else end 
 %>  
</form>
</body>
</html>