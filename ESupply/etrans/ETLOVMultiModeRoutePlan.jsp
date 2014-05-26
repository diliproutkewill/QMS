<%@ page import="javax.naming.InitialContext,
					java.sql.Timestamp,
					org.apache.log4j.Logger,java.util.ArrayList,
					com.foursoft.esupply.common.util.ESupplyDateUtility,
					com.foursoft.etrans.common.routeplan.ejb.sls.ETMultiModeRoutePlanSessionHome,
					com.foursoft.etrans.common.routeplan.ejb.sls.ETMultiModeRoutePlanSession,
					com.foursoft.etrans.common.routeplan.java.MasterDocumentHeader,  
					com.foursoft.etrans.common.routeplan.java.MasterDocument,
					com.foursoft.etrans.common.routeplan.java.MasterDocumentFlightDetails" %> 

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/> 

<%!
   private static Logger logger = null;
%>

<%
	String FILE_NAME = "ETLOVMultiModeRoutePlan.jsp";

  logger  = Logger.getLogger(FILE_NAME);	

	String header = ""; 
	String alertMessage = ""; 

	InitialContext ictx = null;
	ETMultiModeRoutePlanSessionHome home	= null;
	ETMultiModeRoutePlanSession		remote	= null;
	
	String[] idInfo = null;
	String terminalId = null;
	String row = "";
	String forWhat = "";
	String onLoad = "";
	Timestamp[]	timestampEtd	=	null;
	Timestamp[]	timestampEta	=	null;
	String[]  etaDate			=	new String[2];
	String[]  etdDate			=	new String[2]; 

	terminalId		= loginbean.getTerminalId();
	String searchStr = request.getParameter("searchStr");
	forWhat	 = request.getParameter("forWhat");
	String documentType = request.getParameter("documentType");
	String shipmentMode = request.getParameter("shipmentMode");

	boolean flag = false;

	//////////////////////
	// for Descriptive Air & Truck

	MasterDocument etsRouteDOB = null;
	MasterDocumentHeader masterHeader=null;
	MasterDocumentFlightDetails flightdetails	= null;
	MasterDocument[] masterDocIds = null;

	// for Descriptive Air & Truck
	// for Descriptive Document Lov  Added by Nageswara Rao.D 14/08/02
	MasterDocumentHeader documentNos[] = null; 
	////////////
	ArrayList consoleIds = null;
	//////////////////////
		String		dateFormat				=	loginbean.getUserPreferences().getDateFormat();
		ESupplyDateUtility	eSupplyDateUtility  = new ESupplyDateUtility();

	try
	{
	
		
		try
		{
			eSupplyDateUtility.setPatternWithTime(dateFormat);
		}
		catch(Exception ex)
		{
			//Logger.error(FILE_NAME,"The Date format is not setting in ETLOVMultiModeRoutePlan.jsp" +ex.toString());
      logger.error(FILE_NAME+"The Date format is not setting in ETLOVMultiModeRoutePlan.jsp" +ex.toString());
			ex.printStackTrace();
		}
		ictx = new InitialContext();
		home	= (ETMultiModeRoutePlanSessionHome)ictx.lookup("ETMultiModeRoutePlanSessionBean");
		remote	= home.create();

		if(forWhat.equals("documentNos"))
		{
			// code changed by Nageswara Rao.D for Descriptive Document Lov 14/08/02

			//documentNos	= remote.getDocumentNos(forWhat, documentType, searchStr, terminalId, shipmentMode);
	     	
			if(documentType.equals("PRQ"))
				// @@ Modified by Sailaja on 2005 04 27 for SPETI-6440
	     		//header = "Prq Ids";
				header = "PRQ Ids";
				// @@ 2005 04 27 for SPETI-6440				
			if(documentType.equals("BOOKING"))
	     		header = "Booking Ids";
		    if(documentType.equals("HAWB"))
	     		header = "HAWB Ids";
			if(documentType.equals("HBL"))
	     		header = "HBL Ids";
			if(documentType.equals("CONSIGNMENTNOTE"))
	     		header = "Consignment Note Ids";

			alertMessage = "Please Select the Document No.";
	
			if(documentNos == null || documentNos.length == 0)
				flag = true;
			// ends here
		}
		else if(forWhat.equals("gateIds"))
		{
			header = "Gateway And Terminal Ids";
			alertMessage = "Please Select the Terminal Or Gateway Id.";
			String originTerminal = request.getParameter("originTerminalId");
			String destinationTerminal = request.getParameter("destinationTerminalId");
			searchStr = request.getParameter("searchStr");
			row = request.getParameter("row");

			idInfo = remote.getGatewayIds(originTerminal, destinationTerminal, searchStr);

			if(idInfo == null || idInfo.length == 0)
				flag = true;

		}
		else if(forWhat.equals("masDoc") || forWhat.equals("ogmasDoc") || forWhat.equals("olmasDoc") || forWhat.equals("otmasDoc") || forWhat.equals("dtmasDoc") || forWhat.equals("dgmasDoc") || forWhat.equals("dlmasDoc"))
		{
			header = "Master Document Nos";
			alertMessage = "Please Select the Master Document No.";

			searchStr = request.getParameter("searchStr");
			String originTerminalId = request.getParameter("originTerminalId");
			String destinationTerminalId = request.getParameter("destinationTerminalId");
			row = request.getParameter("row");
			//idInfo	= remote.getMasterDocumentNos(shipmentMode, searchStr, originTerminalId, destinationTerminalId);
			if(shipmentMode.equals("Air") || shipmentMode.equals("Truck"))
			{
				//masterDocIds = remote.getDescriptiveMasterDocumentNos(shipmentMode, searchStr, originTerminalId, destinationTerminalId);
				if(masterDocIds != null )
					onLoad = " onLoad='displayValues();selectOne1() '";
				else
					onLoad = "";			
				if(masterDocIds == null || (masterDocIds!=null && masterDocIds.length <= 0))
					flag = true;
			}
			else
			{
				consoleIds = remote.getDescriptiveConsoleIds(shipmentMode, searchStr, originTerminalId, destinationTerminalId);

				if(consoleIds == null || (consoleIds!=null && consoleIds.size() <= 0))
					flag = true;

				if(consoleIds != null  && (consoleIds.size() >0) )
					onLoad = " onLoad='displayValues();selectOne1() '";
				else
					onLoad = "";			
			}
		}  
       // for Descriptive Document LOv
		if("documentNos".equals(forWhat))
			onLoad = " onLoad='displayValues();selectOne() '";
		if(idInfo != null && idInfo.length != 0)
			onLoad = "onLoad='selectOne()'";
			
	}
	catch(Exception ex)
	{
		//Logger.error(FILE_NAME, " -> "+ex.toString()); 
    logger.error(FILE_NAME+ " -> "+ex.toString()); 
	}
%>
<html>
  <head>
	<title>Select</title>
	<link rel="stylesheet" href="../ESFoursoft_css.jsp">
	<%@ include file="/ESEventHandler.jsp" %>
	<script language='JavaScript'>
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

 // ***************** For Descriptive Document Lov ************************
<%
		if("documentNos".equals(forWhat))
		{
				
%>			
			function displayValues()
			{
				//var index = document.forms[0].ids.selectedIndex;
				var index = -1;
				if(document.forms[0].ids != null)
					index = document.forms[0].ids.selectedIndex;
			
				if(index > -1 )   
				{
						var mode;
						var sMode;
						var str="Shipper Id           : "+document.forms[0].ids.options[index].getAttribute("id1")+"\n";
							str+="Consignee Id         : "+document.forms[0].ids.options[index].getAttribute("id2")+"\n";
							str+="Origin Terminal      : "+document.forms[0].ids.options[index].getAttribute("id3")+"\n";
							str+="Destination Terminal : "+document.forms[0].ids.options[index].getAttribute("id4")+"\n";
							mode=document.forms[0].ids.options[index].getAttribute("id5");
						
						if(mode==1)
							sMode="Air";
						else if(mode==2)
							sMode="Sea";
						else if(mode==4)
							sMode="Truck";
							
						str+="Shipment Mode        : "+sMode+"\n"
						document.forms[0].list.value = str;
			  }
			  
			}   // ends here
<%			
		} 
	if(forWhat.equals("masDoc") || forWhat.equals("ogmasDoc") || forWhat.equals("olmasDoc") || forWhat.equals("otmasDoc") || forWhat.equals("dtmasDoc") || forWhat.equals("dgmasDoc") || forWhat.equals("dlmasDoc"))
	{
%>
 
		function setMasterDocNo()
		{
		
		
			if(document.forms[0].ids.selectedIndex == -1)
				alert("Please Select Id");
			else if(document.forms[0].ids.selectedIndex >=0) 
			{	
<%
				if(forWhat.equals("masDoc"))
				{
%>
					var lent = window.opener.document.forms[0].masterDocNo.length;

					if(lent > 1)
					{
						window.opener.document.forms[0].masterDocNo[<%=row%>].value = document.forms[0].ids.options[document.forms[0].ids.selectedIndex].value;
						window.close();
					}
					else
					{
						window.opener.document.forms[0].masterDocNo.value = document.forms[0].ids.options[document.forms[0].ids.selectedIndex].value;
						window.close();
					}
<%
				}
				else if(forWhat.equals("otmasDoc"))
				{
%>
						window.opener.document.forms[0].otmasterDocNo.value = document.forms[0].ids.options[document.forms[0].ids.selectedIndex].value;
						window.close();
<%
				}
				else if(forWhat.equals("ogmasDoc"))
				{
%>
						window.opener.document.forms[0].ogmasterDocNo.value = document.forms[0].ids.options[document.forms[0].ids.selectedIndex].value;
						window.close();
<%
				}
				else if(forWhat.equals("dgmasDoc"))
				{
%>
						window.opener.document.forms[0].dgmasterDocNo.value = document.forms[0].ids.options[document.forms[0].ids.selectedIndex].value;
						window.close();
<%
				}
				else if(forWhat.equals("dtmasDoc"))
				{
%>
						window.opener.document.forms[0].dtmasterDocNo.value = document.forms[0].ids.options[document.forms[0].ids.selectedIndex].value;
						window.close();
<%
				}
				else if(forWhat.equals("dlmasDoc"))
				{
%>
						window.opener.document.forms[0].dlmasterDocNo.value = document.forms[0].ids.options[document.forms[0].ids.selectedIndex].value;
						window.close();
<%
				}

%>
			}		   
		}
	
		function onEnterKeyAT()
		{
			if(event.keyCode == 13)
			{
				setMasterDocNo();
			}
			if(event.keyCode == 27){
				window.close();
			}
		}		  
		
		function displayValues()
		{
			var index = document.forms[0].ids.selectedIndex;
			
			if(index != -1)
			{
<%
				if(shipmentMode.equals("Air") || shipmentMode.equals("Truck"))
				{
%>
					var str="Origin  Gateway Id    : "+document.forms[0].ids.options[index].getAttribute("id1")+"\n";
					str+="Destination GatewayId : "+document.forms[0].ids.options[index].getAttribute("id2")+"\n";
					str+="Carrier Id            : "+document.forms[0].ids.options[index].getAttribute("id3")+"\n";
					str+="Chargebale Weight     : "+document.forms[0].ids.options[index].getAttribute("id4")+"\n";
					str+="Blocked Space         : "+document.forms[0].ids.options[index].getAttribute("id5")+"\n";
					str+="From Location	      : "+document.forms[0].ids.options[index].getAttribute("id6")+"\n";
					str+="To Location	      : "+document.forms[0].ids.options[index].getAttribute("id7")+"\n";
					str+="ETD                   : "+document.forms[0].ids.options[index].getAttribute("id8")+"\n";
					str+="ETA                   : "+document.forms[0].ids.options[index].getAttribute("id9")+"";

<%
				}
				else
				{
%>
					var	str="Origin Gateway	    : "+document.forms[0].ids.options[index].getAttribute("id1")+"\n";
						str+="Destination Gateway : "+document.forms[0].ids.options[index].getAttribute("id2")+"\n";
						str+="Port Of Loading	    : "+document.forms[0].ids.options[index].getAttribute("id3")+"\n";
						str+="Port Of Discharge   : "+document.forms[0].ids.options[index].getAttribute("id4")+"\n";
						str+="Cut Off Date	    : "+document.forms[0].ids.options[index].getAttribute("id5")+"\n";
						str+="ETA		    : "+document.forms[0].ids.options[index].getAttribute("id6")+"\n";
						str+="ETD		    : "+document.forms[0].ids.options[index].getAttribute("id7")+"\n";
						str+="RouteId		    : "+document.forms[0].ids.options[index].getAttribute("id8")+"\n";
<%
				}
%>
				document.forms[0].list.value = str;
			}
		}

		function selectOne1()   
		{
		  <%
		   if((masterDocIds != null && masterDocIds.length >0) ) 
			{ 
		   %>   
			   document.forms[0].ids.options[0].selected=true;
			   document.forms[0].ids.focus();
<% 
			}else{
%>
				document.forms[0].B2.focus();
<%
			}			   
%>
		}
<%
	}
%>
//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	function setId()
	{
		
<%
		if(forWhat.equals("documentNos"))
		{
%>
			if(document.forms[0].ids.selectedIndex == -1)
			{
				alert('<%=alertMessage%>');
			}
			else
			{
				setValue = document.forms[0].ids.options[document.forms[0].ids.options.selectedIndex].value;
				window.opener.document.forms[0].hawbNo.value = setValue;
				window.close();
			}
<%
		}
		else if(forWhat.equals("gateIds"))
		{
%>
			if(document.forms[0].lovOption.selectedIndex == -1)
			{
				alert('<%=alertMessage%>');
			}
			else
			{
				setValue = document.forms[0].lovOption.options[document.forms[0].lovOption.options.selectedIndex].value;
				if(setValue == '')
				{
					alert('Please Select Gateway or Terminal Id.');
				}
					else if(setValue != '')
					{
						var lent = window.opener.document.forms[0].originId.length;
						if(lent > 1)
						{
							window.opener.document.forms[0].originId[<%=row%>].value = setValue;
							window.opener.document.forms[0].originIdTemp[<%=row%>].value = setValue;
							window.close();
						}
						else
						{
							window.opener.document.forms[0].originId.value = setValue;
							window.opener.document.forms[0].originIdTemp.value = setValue;
							window.close();
						}
					}
			}					
<%
		}
%>
		
	} 

function selectOne()
{
  <%
	   if(idInfo != null && idInfo.length > 0 )
	   {
  %>	   
            document.forms[0].lovOption.options[0].selected=true;
            document.forms[0].lovOption.focus();
  <%	   
	   }
  %>
  <%
	   if(documentNos !=null && documentNos .length >0)
	   {
		  //System.out.println("the is 888888888888888888888");
  %>	   
           
            document.forms[0].ids.options[0].selected=true;
            document.forms[0].ids.focus();
  <%	   
	   }else{
  %>
			document.forms[0].B2.focus();
<%
			}	  
  %>
}

function onEnterKey()
{
	if(event.keyCode == 13)
	{
		setId();
	}
	if(event.keyCode == 27){
		window.close();
	}

}	
function onEscKey(){
	if(event.keyCode == 27){
		window.close();
	}
}

	</script>
  </head>
  <body class='formdata' <%=onLoad%>  onKeyPress='onEscKey()'>
    <form>
	  <font face="Verdana" size="2"><center><b><%=header%></b></center></font><br>
	  <center>
<%

	if(flag)
	{
%>
		<textarea rows=6 name="ta"  readOnly cols="30" class='select'>No Ids are Available.
		</textarea><br><input type="button" value="Ok" name="B2" onClick="window.close()" class='input'>
<%
	}
	else 
	{
		if(forWhat.equals("gateIds"))
		{
%>
		<select name='lovOption' size=10 onKeyPress='onEnterKey()' class='select'>
<%
			for(int i=0; i<idInfo.length; i++)
			{
%>
				<option value='<%=idInfo[i]%>' style="FONT-WEIGHT: bold; COLOR: blue; FONT-FAMILY: Arial"><%=idInfo[i]%></option>
<%
			}
%>
		</select><br><br>
		 <input type="button" name="B2" value="Ok" onClick= "setId()" class='input'>
		 <input type="button" name="Cancel" value="Cancel" onClick = "window.close()" class='input'>
<%
		}
		else if( forWhat.equals("documentNos"))  // added by Nageswara Rao.D for Descriptive Document Lov 14/08/02
		{
			if(documentNos!=null)
			{
%>
				<center><select size=1 name="ids" class='select' selected onChange="displayValues()" onKeyPress='onEnterKey()' >
				
<%
				for(int i=0; i<documentNos.length; i++)
				{

%>
					<option style="FONT-WEIGHT: bold; COLOR: blue; FONT-FAMILY: Arial" value='<%= documentNos[i].getDocId()%>'  id1='<%=documentNos[i].getShipperId()%>' id2='<%=documentNos[i].getConsigneeId()%>'  id3='<%=documentNos[i].getOriginTerminal()%>'   id4='<%=documentNos[i].getDestTerminal()%>'  id5='<%=documentNos[i].getPrimaryMode()%>'><%= documentNos[i].getDocId()%></option>
<%
				}
%>
				</select>
				<br><br>
				<textarea name="list" rows="7" value="" cols="40"></textarea><br><br>
				 <input type="button" name="B2" value="Ok" onClick="setId()" class='input'>
				 <input type="button" name="Cancel" value="Cancel" onClick = "window.close()" class='input'><center>				
<%			}
			else
			{
%>				 <textarea name="listdd" rows="7" value="" class='select' cols="40">No <%=documentType%>'s Available</textarea><br><br>
				 <input type="button" name="Cancel" value="Close" onClick = "window.close()" class='input'><center>
<%			}
		}  //ends here
		else if(forWhat.equals("masDoc") || forWhat.equals("ogmasDoc") || forWhat.equals("olmasDoc") || forWhat.equals("otmasDoc") || forWhat.equals("dtmasDoc") || forWhat.equals("dgmasDoc") || forWhat.equals("dlmasDoc"))
		{
%>
			
			<center><select size=1 name="ids" class='select' onChange="displayValues()" onKeyPress='onEnterKeyAT()' > 
<%
			if(shipmentMode.equals("Air") || shipmentMode.equals("Truck"))
			{
				for(int j=0;j<masterDocIds.length;j++)
				{
					etsRouteDOB=masterDocIds[j];
					masterHeader=etsRouteDOB.getMasterDocHeader();
					flightdetails=etsRouteDOB.getMasterDocFlightDetails();

					String masterId				= "";
					String originGateway		= "";
					String destinationGateway	= "";
					String carrierID			= "";
					double chargeweight			= 0.0;
					double block				= 0.0;
					String locaionfrom			= "";
					String locationto			= "";
					String etd					= "";
					String eta					= "";
					String locaionfrom1[]		= flightdetails.getFlightFrom();
					String locationto1[]		= flightdetails.getFlightTo();
					timestampEtd				= flightdetails.getEtd();
					timestampEta				= flightdetails.getEta();	
					etdDate						= new String[timestampEtd.length];
					etaDate						= new String[timestampEta.length];
					int k =0 ;
					for(k =0;k<timestampEtd.length;k++)
					{
						etdDate[k]				= eSupplyDateUtility.getDisplayString(timestampEtd[k]);
					}
					for(k =0;k<timestampEta.length;k++)
					{
						etaDate[k]				= eSupplyDateUtility.getDisplayString(timestampEta[k]);
					}
					
					//String etd1[]				= flightdetails.getEtd();
					//String eta1[]				= flightdetails.getEta();
				
					if(masterHeader.getMasterDocId() != null)
						masterId=masterHeader.getMasterDocId() ;		

					if(masterHeader.getOriginGatewayId() != null)
						originGateway=masterHeader.getOriginGatewayId()!=null?masterHeader.getOriginGatewayId():"" ;
					
					if(masterHeader.getDestinationGatewayId() != null)
						destinationGateway=masterHeader.getDestinationGatewayId()!=null?masterHeader.getDestinationGatewayId():"" ;	
					
					if(masterHeader.getCarrierId() != null)
						carrierID=masterHeader.getCarrierId()!=null?masterHeader.getCarrierId():"" ;
						
					//if(masterHeader.getChargeableWeight() != null)
						chargeweight=masterHeader.getChargeableWeight() ;	
						
					//if(masterHeader.getBlockedSpace() != null)
						block=masterHeader.getBlockedSpace() ;	
					
					if(locaionfrom1 != null)
						locaionfrom=locaionfrom1[0]!=null?locaionfrom1[0]:"" ;	
					
					if(locationto1 != null)
						locationto=locationto1[0]!=null?locationto1[0]:"" ;
						
					if(etdDate != null )
						etd=etdDate[0] ;	
					else
						etd="";
					if(etaDate != null )
						eta=etaDate[0] ;
					else
						eta="" ;
%>
				<option style="FONT-WEIGHT: bold; COLOR: blue; FONT-FAMILY: Arial" value='<%= masterHeader.getMasterDocId()%>'  id1='<%=originGateway%>' id2='<%=destinationGateway%>'  id3='<%=carrierID%>'   id4='<%=chargeweight%>'  id5='<%=block%>'  id6='<%=locaionfrom%>'  id7='<%=locationto%>'  id8='<%=etd%>'  id9='<%=eta%>' name="<%= masterHeader.getOriginGatewayId()%>"><%= masterHeader.getMasterDocId()%></option>
<%
			
				}
			}
			else
			{
			    String alldates="";
				for(int j=0;j<consoleIds.size();j++)
				{
					String[] val = (String[])consoleIds.get(j);
					for(int loop=0;loop<val.length;loop++)
					{
						if(val[loop] == null)
							val[loop] = "";
                         if (loop>=5 && loop <=7){
							alldates=val[loop];
						  	if(alldates != null && ! alldates.equals("") ){
								val[loop] = alldates.substring(0,11); 
							}
						}
					}
                   
%>
				<option style="FONT-WEIGHT: bold; COLOR: blue; FONT-FAMILY: Arial" value='<%=val[0]%>'  id1='<%=val[1]%>' id2='<%=val[2]%>'  id3='<%=val[3]%>'   id4='<%=val[4]%>'  id5='<%=val[5]%>'  id6='<%=val[6]%>'  id7='<%=val[7]%>'  id8='<%=val[8]%>' name="<%=val[1]%>"><%=val[0]%></option>
<%
			
				}
			}// else
%>
				</select>
				<br><br>
				<textarea name="list" rows="9" value="" cols="45"></textarea><br><br>
				 <input type="button" name="B2" value="Ok" onClick= "setMasterDocNo()" class='input'>
				 <input type="button" name="Cancel" value="Cancel" onClick = "window.close()" class='input'><center>
<%
		}
	}
%>
	  </center>
	</form>
  </body>
</html>