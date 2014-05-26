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
	String FILE_NAME	=	"ETransLOVCarrierIds1.jsp";
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
	String		listTypes1[]			=   request.getParameterValues("listTypes1");
	String		operation				=   request.getParameter("Operation");//added by rk
	String		originLocation			=	request.getParameter("originLoc");
	String		destLocation			=	request.getParameter("destLoc");
	
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
		else
			shipmentMode="All";
	/*	if(shipmentMode.equals("All"))
			shipmentMode = "(1,2,3,4,5,6,7)";		
		else
			shipmentMode = "(1,3,5,7)";
*/
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
	//	Logger.info(FILE_NAME," 2 "+wheretoset+" "+index+"  " +ArrayOrNot);
		//@@ Srivegi Added on 20050520 for UAT
		requiredAttributes.add(index);
        requiredAttributes.add(searchString);
        requiredAttributes.add(ArrayOrNot);
        requiredAttributes.add(wheretoset);
		requiredAttributes.add(multiple);
		//@@ 20050520 for UAT
      		if("1".equals(shipmentMode) || "Air".equalsIgnoreCase(shipmentMode))
			shipmentMode = "(1,3,5,7)";
		else if("2".equals(shipmentMode) || "Sea".equalsIgnoreCase(shipmentMode))
			shipmentMode = "(2,3,6,7)";
		else if("4".equals(shipmentMode) || "Truck".equalsIgnoreCase(shipmentMode))
			shipmentMode = "(4,5,6,7)";
		else
			shipmentMode = "(1,2,3,4,5,6,7)";		
		
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
			//Logger.info(FILE_NAME,"in else "+loginbean.getTerminalId());
			if("CSR".equalsIgnoreCase(operation))
				carrierIds							=	remote.getCarriersForCSR(shipmentMode,originLocation,destLocation,loginbean.getTerminalId());
			else
				carrierIds							=	remote.getCarrierIds1(shipmentMode,searchString,address,operation,loginbean.getTerminalId());
		}
    //System.out.println("carrierIds  "+carrierIds);
//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5550-5554 on 200505011.

/*	else if (request.getParameter("shipmentMode").equals("All"))
	 {
		carrierIds							=	remote.getCarrierIds("All",searchString,address,loginbean.getTerminalId(),operation);
	 }//added by rk
//Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5550-5554 on 200505011 ends. 
	else if (request.getParameter("shipmentMode").equalsIgnoreCase("Air") ||request.getParameter("shipmentMode").equals("1") ) {
		carrierIds							=	remote.getCarrierIds("Air",searchString,address,loginbean.getTerminalId(),operation);
	 }
	 else if (request.getParameter("shipmentMode").equalsIgnoreCase("Sea") ||request.getParameter("shipmentMode").equals("2") ) {
		carrierIds							=	remote.getCarrierIds("Sea",searchString,address,loginbean.getTerminalId(),operation);
	 }
	 else if (request.getParameter("shipmentMode").equalsIgnoreCase("Truck") ||request.getParameter("shipmentMode").equals("4") ) {
		carrierIds							=	remote.getCarrierIds("Truck",searchString,address,loginbean.getTerminalId(),operation);
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
	String 		fileName	= "ETransLOVCarrierIds1.jsp";  
	//@@ Srivegi Added on 20050520 for UAT
	try
    {
        if(requiredAttributes!=null)
        {
            index        = (String)requiredAttributes.get(0);
            searchString        = (String)requiredAttributes.get(1);
            ArrayOrNot         = (String)requiredAttributes.get(2);
            wheretoset     = (String)requiredAttributes.get(3);
			multiple	   = (String)requiredAttributes.get(4);
            
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

function submitForm(var1)
{
	selectallValues()
	setVar();

	document.forms[0].action="ETransLOVCarrierIds1.jsp?pageNo="+var1;
	document.forms[0].submit();
}

 function selectallValues()
 {
	
	 obj = document.forms[0].listTypes1;
	for (var i=0; i<obj.options.length; i++) 
		{
			obj.options[i].selected = true;
		}

 }
function hasOptions(obj) {
	if (obj!=null && obj.options!=null) { return true; }
	return false;
	}

function moveDestSelectedRecords(objSourceElement, objTargetElement)    {    
	var aryTempSourceOptions = new Array();        var x = 0;                //looping through source element to find selected options        
			for (var i = 0; i < objSourceElement.length; i++) {   
					if (objSourceElement.options[i].selected) {               
							//need to move this option to target element                
							var intTargetLen = objTargetElement.length++; 
							objTargetElement.options[intTargetLen].text = objSourceElement.options[i].text;                
							objTargetElement.options[intTargetLen].value = objSourceElement.options[i].value;           
					}
					else
					{               
						//storing options that stay to recreate select element          
						var objTempValues = new Object(); 
						objTempValues.text = objSourceElement.options[i].text;
						objTempValues.value = objSourceElement.options[i].value;  
						aryTempSourceOptions[x] = objTempValues;  
						x++;    
					}
			}
			//resetting length of source 

			objSourceElement.length = aryTempSourceOptions.length; 
			//looping through temp array to recreate source select element 
			for (var i = 0; i < aryTempSourceOptions.length; i++) {      
				objSourceElement.options[i].text = aryTempSourceOptions[i].text;  
				objSourceElement.options[i].value = aryTempSourceOptions[i].value; 
				objSourceElement.options[i].selected = false;  
			}
}
	function  populateCarrier1Names()
{
<%
	if(listTypes1!=null)
	{
	for(int i=0;i<listTypes1.length;i++ )
	{// for loop begin
		

%>
		document.forms[0].listTypes1.options[ <%= i %> ] = new Option('<%= listTypes1[i] %>','<%= listTypes1[i] %>');
<%
	}// for loop end
	}
	
%>
}
function showCarrierIds()
{
<%	boolean flag = true;
    int     count = 0;
	for( int i=0; i<currentPageList.size(); i++)
	{//for loop begin
		String carrierId = currentPageList.get(i).toString();
		if(listTypes1!=null)
		for(int j=0;j<listTypes1.length;j++ )
	    {
          if(carrierId.equalsIgnoreCase(listTypes1[j])){
			  flag=false;
			  break;
		  }
		}
		if(flag){
%>
		val				= '<%= carrierId %>';
		firstIndex		= val.indexOf('['); 	
		optionValue		= val.substring(0,firstIndex-1);	
		window.document.form1.ids.options[<%= count %>] = new Option(val,optionValue);
<%	     count++;
		}flag=true;		
	}// end of for loop
	if(count>0 && currentPageList.size() > 0)
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
	selectallValues();
	if( document.form1.listTypes1.selectedIndex==-1)
		alert("Please select a CarrierId");
	else
	{
		window.opener.setCarrierIdValues(document.forms[0].listTypes1);
		/*var count		=	0;
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
			window.opener.setCarrierIdValues(levelTemp);*/
		}//@@Yuvraj
		/*else
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
				}
			}
		}*/

			
		 resetValues();	
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


<body onLoad="populateCarrier1Names();showCarrierIds()" onUnLoad='toKillSession()' onKeyPress='onEscKey()' class='formdata' onbeforeunload='selectallValues()'>
<form name=form1 method="post" action=""><br><br>
<center><b>Carrier Ids</b><br>
</center><br>
<%
	if(currentPageList.size()>0)
	{// begin if loop
%>
		<center>
		<TABLE cellSpacing=0 width="100%" align='center'>
		 <tr  class="formdata"> 
			<td width="49%" align='center'>
			 <select size=10 name="ids" MULTIPLE onKeyPress='onEnterKey()'  class="select" style="width:300px;margin:0px 0 5px 0;"> 
				</select></td>
	        <td width="2%" align='center' valign="middle">
				<input type="button" value='>>' name="right" class='input' onClick='moveDestSelectedRecords(document.forms[0].ids,document.forms[0].listTypes1)'><br>
				<input type="button" value='<<' name="left" class='input' onClick='moveDestSelectedRecords(document.forms[0].listTypes1,document.forms[0].ids)'></td>
	        <td width="49%" align='center'>
				<select size="10" name="listTypes1" MULTIPLE onDblClick='setCarrierId()' onKeyPress='onEnterKey()' class="select" style="width:300px;margin:0px 0 5px 0;">
				</select></td>
          </tr>
         </table>
		</center>
			<center>
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