<%
/*
	Program Name	:QMSLOVDescriptionIds.jsp
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
					com.qms.operations.charges.ejb.sls.ChargeMasterSession,
					com.qms.operations.charges.ejb.sls.ChargeMasterSessionHome,
					com.foursoft.esupply.common.java.LOVListHandler,
					com.qms.operations.charges.java.BuySellChargesEnterIdDOB,
					com.foursoft.esupply.common.java.FoursoftConfig" %>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>

<%@ taglib  uri="../WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<%!
  private static Logger logger = null;
%>

<%
    String 		          fileName	            =     "QMSLOVDescriptionIds.jsp"; 
    logger  = Logger.getLogger(fileName);	
	 ArrayList			      requiredAttributes	=	  null;
	 LOVListHandler		      listHandler			=	  null;	 
	 ChargeMasterSession          remote				=     null;
	 ChargeMasterSessionHome      home					=     null;
	 String					  pageIndex			    =	  null;	 
	 String					  rows					=	  null;
	 String                   chargeId            	=     (request.getParameter("chargeId")!=null)?request.getParameter("chargeId"):"";
//@@Added by subrahmanyam for pbn id: 195270 on 20-Jan-10
	 String                   chargeGroupId        =     (request.getParameter("chargeGroupId")!=null)?request.getParameter("chargeGroupId"):"";
	// String						name				=	(request.getParameter("name")!=null)?request.getParameter("name"):"descId";
	String						name				=	(request.getParameter("name")!=null)?request.getParameter("name"):"";
	 String                   searchString          =     request.getParameter("searchString");
	 InitialContext			  initial	            =     null;
	// ArrayList                descriptionIdList   =     new ArrayList();Unnecessary instantiation of ArrayList object descriptionIdList. Only reference is enough.
  ArrayList                descriptionIdList   = null;
     String                   operation             =     request.getParameter("Operation");
	 String              terminalId            =     (request.getParameter("terminalId")!=null?request.getParameter("terminalId"):"");
	 String				 fromWhere			   =	 (request.getParameter("fromWhere")!=null?request.getParameter("fromWhere"):"");
	String              descriptionId         =     "";
	String  aircheck			=	(request.getParameter("aircheck")!=null)?request.getParameter("aircheck"):"";
	String  seacheck			=	(request.getParameter("seacheck")!=null)?request.getParameter("seacheck"):"";
	String  truckcheck			=	(request.getParameter("truckcheck")!=null)?request.getParameter("truckcheck"):"";
	int	    shipMode			=	0;
	String	shipModeStr			=	"";
	BuySellChargesEnterIdDOB buySellChargesEnterIdDOB =null;

	
	if(fromWhere!=null && fromWhere.length()>0)
		session.setAttribute("fromWhere",fromWhere);
	if(name!=null && name.length()>0)
		session.setAttribute("name",name);
	

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
			
		try
		{ 
			if(terminalId==null || "".equals(terminalId))
			{	terminalId = loginbean.getTerminalId();}

					if(aircheck.equals("checked"))
					{
						if(seacheck.equals("checked"))
						{
							if(truckcheck.equals("checked"))
							{
								shipMode	=	7;
								//shipModeStr	=	"1,2,3,4,5,6,7";
								shipModeStr	=	"7";
							}else
							{
								shipMode	=	3;
								//shipModeStr	=	"1,2,3,5,6,7";
								shipModeStr	=	"3,7";
							}
						}else if(truckcheck.equals("checked"))
						{
							shipMode	=	5;
							//shipModeStr	=	"1,3,4,5,6,7";
							shipModeStr	=	"5,7";
						}else
						{
							shipMode	=	1;
							shipModeStr	=	"1,3,5,7";
						}
					}else if(seacheck.equals("checked"))
					{
						if(truckcheck.equals("checked"))
						{
							shipMode	=	6;
							//shipModeStr	=	"2,3,4,5,6,7";
							shipModeStr	=	"6,7";
						}else
						{
							shipMode	=	2;
							shipModeStr	=	"2,3,6,7";
						}
					}else if(truckcheck.equals("checked"))
					{
						shipMode	=	4;
						shipModeStr	=	"4,5,6,7";
					}

			initial					=	  new InitialContext();	
			home                    =     (ChargeMasterSessionHome)initial.lookup("ChargeMasterSession");
			remote                  =     (ChargeMasterSession)home.create();			
			//System.out.println("descriptionId"+descriptionId);
			if("chargegroup".equals(fromWhere) || "sellcharge".equals(fromWhere) || "buycharge".equals(fromWhere))
			{	//@@Commented by subrahmanyam for pbn id: 195270 on 20-Jan-10
				//descriptionIdList     =     remote.getDesriptionIds(chargeId,searchString,shipModeStr,terminalId);
				//@@Added by subrahmanyam for pbn id: 195270 on 20-Jan-10
				descriptionIdList     =     remote.getDesriptionIds(chargeId,searchString,shipModeStr,terminalId,chargeGroupId);
			}
			else if("buychargesenterid".equals(fromWhere) || "sellchargesenterid".equals(fromWhere))
			{	
				//descriptionIdList     =     remote.getBuyChargeDescIds(searchString,fromWhere,chargeId,terminalId,loginbean);
				buySellChargesEnterIdDOB	=	new BuySellChargesEnterIdDOB();
				buySellChargesEnterIdDOB.setChargeId(chargeId);
				buySellChargesEnterIdDOB.setChargeDescId(searchString);
				buySellChargesEnterIdDOB.setTerminalId((terminalId==null || terminalId.equals(""))?loginbean.getTerminalId():terminalId);
				buySellChargesEnterIdDOB.setFromWhere(fromWhere);
				buySellChargesEnterIdDOB.setChargeGroupId(chargeGroupId);//@@Added by subrahmanyam for pbn id: 195270 on 20-Jan-10
				descriptionIdList     =     remote.getBuySellChargeDescIds(buySellChargesEnterIdDOB,loginbean);

			}
			else //if("chargeDescription".equals(fromWhere))
			{
				buySellChargesEnterIdDOB	=	new BuySellChargesEnterIdDOB();
				buySellChargesEnterIdDOB.setChargeId(chargeId);
				buySellChargesEnterIdDOB.setChargeDescId(searchString);
				buySellChargesEnterIdDOB.setOperation(operation);
				buySellChargesEnterIdDOB.setTerminalId((terminalId==null || terminalId.equals(""))?loginbean.getTerminalId():terminalId);
				descriptionIdList = remote.getDesriptionIds(buySellChargesEnterIdDOB);
			}
			/*else
			{	
				descriptionIdList     =     remote.getDesriptionIds(chargeId,searchString,terminalId);
			}*/
			if(descriptionIdList!=null)
			{
			   listHandler                     =	new LOVListHandler(descriptionIdList,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
			   session.setAttribute("descriptionIdIds", listHandler);
			   //listHandler = (LOVListHandler)session.getAttribute("descriptionIdIds");
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
<%
		fromWhere	= (String)session.getAttribute("fromWhere");
		name		= (String)session.getAttribute("name");
%>

var isAttributeRemoved  = 'false';

function  populateDescIds()
{
	<%

	for(int i=0;i<currentPageList.size();i++ )
	{// for loop begin
		pageIndex=currentPageList.get(i).toString();

%>
		document.forms[0].descriptionId.options[ <%= i %> ] = new Option('<%= pageIndex %>','<%= pageIndex %>');
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
			alert("Please select a descriptionId ")
		}
		else
		{
				
			var index	=   document.forms[0].descriptionId.selectedIndex;
<%

			if("chargegroup".equals(fromWhere) || "buycharge".equals(fromWhere) || "buychargesenterid".equals(fromWhere) || "sellchargesenterid".equals(fromWhere))
			{
%>			
				window.opener.document.getElementById("<%=name%>").value=document.forms[0].descriptionId.options[index].value;			
<%
			}else if("sellcharge".equals(fromWhere))
			{
%>
				window.opener.document.getElementById("<%=name%>").value=document.forms[0].descriptionId.options[index].value;
				window.opener.document.forms[0].<%=name%>.onchange();
<%
			}
			else
			{
%>
				window.opener.document.forms[0].descId.value=document.forms[0].descriptionId.options[index].value;				
<%
			}
%>			
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
   // modified by VLAKSHMI for issue 174399 ON 20090624
      window.location.href	=	"<%=request.getContextPath()%>/ESupplyRemoveAttribute.jsp?valueList=descriptionIdIds";
   }
}

function resetValues()
{
    isAttributeRemoved			=	'true';
    // modified by VLAKSHMI for issue 174399  ON 20090624
    document.forms[0].action	=	"<%=request.getContextPath()%>/ESupplyRemoveAttribute.jsp?valueList=descriptionIdIds";
    document.forms[0].submit();   
}

</script>
</head>
<body class='formdata' onUnLoad='toKillSession()' onLoad="populateDescIds()" onKeyPress='onEscKey()'>
<form method ="post" action="">
		<center>
			<b><font face="Verdana" size="2">Charge description Ids</font></b>
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
					<input type="button" value='Ok' name="OK" onClick='setdescriptionId()' class="input">
					<input type="button" value='Cancel' name="B2" onClick="resetValues();window.close()" class="input">
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
			 <center><textarea rows=6 name="ta" cols="30" class='select' readOnly >No Description Id's Found</textarea></center><br>
			 <center><input type="button" value="Close" name="B2" onClick="window.close()" class="input"></center>
<%
	}// end of else loop
 %>

</form>
</body>
</html>
