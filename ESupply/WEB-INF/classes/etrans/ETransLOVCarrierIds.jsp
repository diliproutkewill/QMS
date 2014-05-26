<%--

	Program Name	:ETransLOVCarrierIds.jsp
	Module Name		:ETrans
	Task			:Location
	Sub Task		:LocationLOV
	Author Name		:UshaSree.Petluri
	Date Started	:September 20,2001
	Date Completed	:September 20,2001
	Date Modified	:
	Description		:This file is invoked when clicked on the CarrierId LOV. In this, all the CarrierIds particular to that
					Terminal are displayed in the List Box. Once Selected any one of the CarrierId, that CarrierId
					is displayed in the respective Text Field. 
					This file interacts with CarrierRegistration SessionBean to call the method getCarrierIds() which inturn 
					retrieves the CarrierIds. 
*	Modified Date	Modified By		Reason
*	03-11-2001		Prasad RLV		Changed in javascript function showCarrierIds(). "substring(0,firstIndex) to substring(0,firstIndex-1)"
	06-11-01        Shravan 		Changes made to handle the multiple carrier-ID LOV buttons in Dynamic arrays (in CarrierSchedulesAdd.jsp)

--%>	
<%@ page import="javax.naming.InitialContext,
				java.util.ArrayList,
				org.apache.log4j.Logger,				
				com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.etrans.setup.country.bean.CountryMaster,
				com.foursoft.etrans.common.util.java.OperationsImpl,
				com.foursoft.esupply.common.java.LOVListHandler,
				com.foursoft.esupply.common.java.FoursoftConfig" %>
<jsp:useBean id="loginbean" class ="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope ="session"/>
<%@ taglib  uri="/WEB-INF/lib/PageIterator.jar"  prefix="pageIterator"  %>

<%!
  private static Logger logger = null;
%>

<%	
	 ArrayList requiredAttributes = null;
	LOVListHandler listHandler   = null;
	String FILE_NAME	=	"ETransLOVCarrierIds.jsp ";
  logger  = Logger.getLogger(FILE_NAME);	
	ArrayList carrierIds				=	null;	// ArrayList for store carrierIds
	int			len						=	0;		// len to store the length of all carrierIds
	String		terminalId				=	loginbean.getTerminalId();
	String		whereClause				=	"";
	String		ArrayOrNot				=	"";
	String		wheretoset				=	"carrierId";
	String		index					=	"";
	String		searchString			=	"";
	String		where_condition			=	"";
	String		shipmentMode			=   "";
	int			shipMode				=   0;
	String		address					=	request.getParameter("address");
	//@@Added by Yuvraj for CR_DHLQMS_1006
	String		multiple				=	request.getParameter("multiple");
		String operation            =   request.getParameter("Operation");//added by rk
	if(multiple==null)
		multiple = "";
	//@@Yuvraj

	if(request.getParameter("pageNo")!=null)  
	{
		 try
		{
	  
			listHandler           = (LOVListHandler)session.getAttribute("carrierIds");
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
        System.out.println("Handler is not null");
        requiredAttributes  = new ArrayList();

		if(request.getParameter("shipmentMode") != null)
			shipmentMode = request.getParameter("shipmentMode");
	
		
		if("1".equals(shipmentMode) || "Air".equalsIgnoreCase(shipmentMode))
			shipmentMode = "(1,3,5,7)";
		else if("2".equals(shipmentMode) || "Sea".equalsIgnoreCase(shipmentMode))
			shipmentMode = "(2,3,6,7)";
		else if("4".equals(shipmentMode) || "Truck".equalsIgnoreCase(shipmentMode))
			shipmentMode = "(4,5,6,7)";
		else
			shipmentMode = "(1,2,3,4,5,6,7)";		

		searchString	= request.getParameter("searchString");
		if(searchString == null)
			searchString="";
		else
			searchString = searchString.trim();
		if(request.getParameter("ArrayOrNot")!=null)
			ArrayOrNot=request.getParameter("ArrayOrNot");
/*		if(request.getParameter("whereClause")!=null)
		 {
			 whereClause=request.getParameter("whereClause");
			 where_condition=whereClause+" AND A.CARRIERID LIKE '"+searchString+"%' AND A.SHIPMENTMODE IN "+shipmentMode+" " ;
		 }
		 else
		 {
			 whereClause="";
			 where_condition=" WHERE A.CARRIERID LIKE '"+searchString+"%' AND A.SHIPMENTMODE IN "+shipmentMode+" " ;
		 }
*/
		if(request.getParameter("wheretoset")!=null)
		  wheretoset=request.getParameter("wheretoset");
		if(request.getParameter("index")!=null)
		   index=request.getParameter("index");
		//Logger.info(FILE_NAME," 2 "+wheretoset+" "+index+"  " +ArrayOrNot);
		//@@ Srivegi Added on 20050520 for UAT
		requiredAttributes.add(index);
        requiredAttributes.add(searchString);
        requiredAttributes.add(ArrayOrNot);
        requiredAttributes.add(wheretoset);
		//@@ 20050520 for UAT
      
		
		InitialContext initial						=	new InitialContext(); // variable to get initial context for JNDI
		SetUpSessionHome home		=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");	// looking up the Bean
		SetUpSession remote		=	(SetUpSession)home.create();
		
		
		
		//added by Nageswara Rao.D for DetailTracking 25/03/02	
	/*	if(request.getParameter("carrierId")!=null)
		{
			String			boundIndex		= request.getParameter("boundIndex");
						shipmentMode	= request.getParameter("ShipmentMode");
			searchString					= searchString.toUpperCase();
			carrierIds						=  remote.getMasterCarrierIds(terminalId,boundIndex,shipmentMode,searchString);
		}
		else*/


	//Logger.info(FILE_NAME,"where_Condition...."+where_condition);
	//		carrierIds							=	remote.getCarrierIds(where_condition);
	if(request.getParameter("type")!=null)
	 {
		//Logger.info(FILE_NAME,"The is vendor if @@@@@@@@@@@@@@@@ ");
		carrierIds							=	remote.getSpcificCarrierIds(shipmentMode,searchString);
	 }else
		{
			//Logger.info(FILE_NAME,"in else"+shipmentMode);
			carrierIds							=	remote.getCarrierIds(shipmentMode,searchString,address,operation,loginbean.getTerminalId());
		}


//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5550-5554 on 200505011.

/*	else if (request.getParameter("shipmentMode").equals("All"))
	 {
		Logger.info(FILE_NAME,"The is Carrier Modify,view,delete else if  @@@@@@@@@@@@@@");
		carrierIds							=	remote.getCarrierIds("All",searchString,address,operation,loginbean.getTerminalId());//added by rk
	 }
//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5550-5554 on 200505011 ends. 
	else {
		Logger.info(FILE_NAME,"The is vendor else @@@@@@@@@@@@@@@@@@@@");
		carrierIds							=	remote.getCarrierIds("Air",searchString,address,operation,loginbean.getTerminalId());//added by rk
	 }
*/
		/*
			Here where condition should be passed within quotes when required in of passsing in query.
			If no condition is to passed then keep the String empty with quotes.
		*/
	if(carrierIds != null)
	{
		len	=	carrierIds.size();
	}
      if(carrierIds!=null)
        {
            listHandler                     = new LOVListHandler(carrierIds,Integer.parseInt(loginbean.getUserPreferences().getLovSize()),requiredAttributes);
            session.setAttribute("carrierIds", listHandler);
            listHandler = (LOVListHandler)session.getAttribute("carrierIds");
        }
      }
	}
	catch( Exception e )
	{
		//Logger.error(FILE_NAME,"ETransLOVCarrierIds.jsp : Exception :  " , e.toString()); //RS#NAMECHANGE#699# 
    logger.error(FILE_NAME+"ETransLOVCarrierIds.jsp : Exception :  " + e.toString()); //RS#NAMECHANGE#699# 
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
	String 		fileName	= "ETransLOVCarrierIds.jsp";  
	//@@ Srivegi Added on 20050520 for UAT
	try
    {
        if(requiredAttributes!=null)
        {
            index        = (String)requiredAttributes.get(0);
            searchString        = (String)requiredAttributes.get(1);
            ArrayOrNot         = (String)requiredAttributes.get(2);
            wheretoset     = (String)requiredAttributes.get(3);
            
        }
	
    }
    catch(Exception ex)
    {
      //Logger.error(FILE_NAME,"ETransLOVPRQIds.jsp : " +ex);
      logger.error(FILE_NAME+"ETransLOVPRQIds.jsp : " +ex);
    }
	//@@ 20050520 for UAT
%>

<html>
<head>
<title>Select</title>
<script>
var isAttributeRemoved  = 'false';
function showCarrierIds()
{
<%
	for( int i=0; i<currentPageList.size(); i++)
	{//for loop begin
		String carrierId = currentPageList.get(i).toString();
%>
		val				= '<%= carrierId %>';
		firstIndex		= val.indexOf('['); 	
		optionValue		= val.substring(0,firstIndex-1);	
		window.document.form1.ids.options[<%= i %>] = new Option(val,optionValue);
<%			
	}// end of for loop
	if(currentPageList.size() > 0)
	{
%>
			window.document.form1.ids.options[0].selected = true;	
			window.document.form1.ids.focus();

<%
	}else{
%>	
		window.document.form1.B2.focus();
<%
		}	
%>
}
function setCarrierId()
{
	var whereClause="<%=whereClause%>";	
	if( document.form1.ids.selectedIndex==-1)
		alert("Please select a CarrierId");
	else
	{
		var count		=	0;
		var index	=	document.form1.ids.selectedIndex;
		temp		=	document.form1.ids.options[index].value;
		var cond	=	'<%=ArrayOrNot%>';
		var len		=	'<%=index%>';
		//@@Added by Yuvraj for CR_DHLQMS_1006
		if('<%=multiple%>'=='multiple')
		{
			for(var i=0;i<document.forms[0].ids.options.length;i++)
			{
				if(document.forms[0].ids.options[i].selected)
				{
					count++;
				}
			}
			var levelTemp = new Array(count);
			var sIndex=0;

			for(var i=0;i<document.forms[0].ids.options.length;i++)
			{
				if(document.forms[0].ids.options[i].selected)
				{
					strTemp		=	document.forms[0].ids.options[i].value;
					levelTemp[sIndex]	=	strTemp;
					sIndex++;
				}
			}
			window.opener.setCarrierIdValues(levelTemp);
		}//@@Yuvraj
		else
		{
	//		if(whereClause=="" || cond=='NoArray')
			if(cond == '' || cond=='NoArray')
				window.opener.document.forms[0].<%=wheretoset%>.value=temp;
			else if(cond == 'YesArray')
			{
				//if(parseInt(len) > 0)
				//{
					if(document.forms[0].ids.selectedIndex==-1)
						alert("Please select a Id");
					else
						window.opener.document.forms[0].<%=wheretoset%>[len].value=temp;
			/*	}
				else
				{
					if(document.forms[0].ids.selectedIndex==-1)
						alert("Please select a Id");
					else
						window.opener.document.forms[0].<%=wheretoset%>.value=temp;
				}*/
			}
		}

			
		 resetValues();	
	}
}	
function onEnterKey()
{

	if(event.keyCode == 13)
	{
		setCarrierId();
	}
	if(event.keyCode == 27){
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
      window.location.href="../ESupplyRemoveAttribute.jsp?valueList=carrierIds";
   }
}

function resetValues()
{
    isAttributeRemoved  = 'true';
    document.forms[0].action="../ESupplyRemoveAttribute.jsp?valueList=carrierIds";
    document.forms[0].submit();   
}
		

</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">

</head>


<body onLoad=showCarrierIds() onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata' >
<form name=form1 method="post" action="">
<center><b>Carrier Ids</b><br>
</center>
<%
	if(currentPageList.size()>0)
	{// begin if loop
%>
			<center>
				<select size=10 name="ids" onDblClick='setCarrierId()' onKeyPress='onEnterKey()'  class="select" <%=multiple%>> 
				</select>
			</center>
			<br>
			<center> 
				<input type="button" value=" Ok " name="addButton" onClick="setCarrierId()" class="input">
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
	}// end if loop
	else
	{// begin of else loop
%>
			<center>
					<textarea rows=6 name="ta" class='select' readOnly cols="30" > No Carriers Ids are available </textarea>
			</center><br>
			<center>
					<input type="button" value="Close" name="B2" onClick="window.close()"  class="input">
			</center>
<%
	}// end of else loop
%>
</form>
</body>
</html>