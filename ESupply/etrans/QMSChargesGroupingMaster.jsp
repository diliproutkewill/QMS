
<%--
<%--

Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
This software is the proprietary information of FourSoft, Pvt Ltd.
Use is subject to license terms.
esupply - v 1.x.
Program	Name	: QMSChargesGroupingMaster.jsp
Product Name	: QMS
Module Name		: Charges Grouping Master
Task		    : Adding/View/Modify/Delete
Date started	: 21-09-2005 	
Date Completed	: 21-09-2005 with all the validations
Date modified	:  
Author    		: I.V.Sekhar Merrinti
Description		: The application "Adding/View/Modify/Delete" Charges Grouping Master
Actor           :
Related Document: CR_DHLQMS_1005
--%>
<%@page import = "com.qms.setup.java.ChargeGroupingDOB,
				  com.qms.setup.ejb.sls.SetUpSessionHome,
				  com.qms.setup.ejb.sls.SetUpSession,
		          javax.naming.InitialContext,
				  javax.naming.Context,
				  org.apache.log4j.Logger,
				  com.foursoft.esupply.common.java.ErrorMessage,
				  com.foursoft.esupply.common.java.KeyValue,
				  com.qms.operations.charges.java.BuySellChargesEnterIdDOB,
				  java.util.ArrayList,
				  java.util.StringTokenizer,
				  javax.ejb.ObjectNotFoundException"
%>
<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME="QMSChargesGroupingMaster.jsp";
%>
<%
  logger  = Logger.getLogger(FILE_NAME);	
	String operation						=	request.getParameter("Operation");
	String chargeGroupId					=	(request.getParameter("chargeGroupId")!=null?request.getParameter("chargeGroupId"):"");
	String terminalId						=   request.getParameter("terminalId");
	ChargeGroupingDOB	chargeGroupingDOB	=	null;
	ChargeGroupingDOB	chargeGroupingDOB2	=	null;//Added by Anil.k for Issue 236357 on 23Feb2011
	ArrayList			dataList			=	null;
	ArrayList			buyChargeList		=	null;//Added by Anil.k for Issue 236357 on 23Feb2011
	ArrayList			sellChargeList		=	null;
	ArrayList			dataList2			=	null;
	ArrayList			chargeDescList   	=	null;
	int					originCharges		=	0;
	int					destCharges			=	0;//Ended by Anil.k for Issue 236357 on 23Feb2011
	String	airChecked						=	"";
	String  seaChecked						=	"";
	String	truckChecked					=	"";
	int		shipMode						=	0;
	String	chargeIds					=	null;
	boolean	success							=	true;
	String	msg						=	"";
	ErrorMessage errorMessageObject =   null;
	ArrayList	 keyValueList	    =	new ArrayList();
	String		errorMessage		=	null;
	String		url					=	null;
	String		disabled			=	"";
	String		readOnly			=	"";
	String airDisabled  ="" ;
	String seaDisabled  ="" ;
	String truckDisabled  ="" ;
	int dataListSize				=	0;
	BuySellChargesEnterIdDOB buySellChargesEnterIdDOB = null;
	String	fromWhat				=	request.getParameter("fromWhere");
	String  originValues			= "";//Added by Anil.k for Enhancement 231214 on 24Jan2011
	String  destValues  			= "";//Added by Anil.k for Enhancement 231214 on 24Jan2011
	try{
			if(terminalId==null || terminalId.equals(""))
			{	terminalId = loginbean.getTerminalId();}

			if(operation!=null && (operation.equals("View") || operation.equals("Delete")))
			{	
				disabled	=	"disabled";
				readOnly	=	"readOnly";
			}
			if(operation!=null && (operation.equals("View") || operation.equals("Modify") || operation.equals("Delete")|| operation.equals("Invalidate")|| operation.equals("ViewAll")))
			{ 
				InitialContext initial		= new InitialContext();
				SetUpSessionHome	home	=	(SetUpSessionHome)initial.lookup("SetUpSessionBean");
				SetUpSession		remote	=	(SetUpSession)home.create();
				
				if(chargeGroupId!=null)
					{
						buySellChargesEnterIdDOB    = new BuySellChargesEnterIdDOB();
						buySellChargesEnterIdDOB.setTerminalId(terminalId);
						buySellChargesEnterIdDOB.setOperation(operation);
						buySellChargesEnterIdDOB.setChargeGroupId(chargeGroupId);
            buySellChargesEnterIdDOB.setFromWhere(fromWhat);
						//dataList			=	remote.getChargeGroupDtl(chargeGroupId,loginbean);
						if(!("Invalidate".equalsIgnoreCase(operation)|| operation.equals("ViewAll") || operation.equals("View"))){
						  dataList			=	remote.getChargeGroupDtl(buySellChargesEnterIdDOB,loginbean);
                         chargeDescList    = remote.getChargeDescList();
						}
						else if(operation.equals("View"))
						{
							dataList2			=	remote.getChargeGroupDtl(operation,buySellChargesEnterIdDOB,loginbean);
                         chargeDescList    = remote.getChargeDescList();
						}
						else{
						dataList			=	remote.getChargeGroupMasterDetails(operation,loginbean.getTerminalId(),loginbean.getAccessType());
						session.setAttribute("chargeList",dataList);
						}
					}
					if(!("Invalidate".equalsIgnoreCase(operation)|| operation.equals("ViewAll") || operation.equals("View"))){
						if(dataList!=null && dataList.size()>0)
						{
							chargeGroupingDOB  = (ChargeGroupingDOB)dataList.get(0);
							if(chargeGroupingDOB!=null && chargeGroupingDOB.getShipmentMode()>0)
							{
								shipMode	=	chargeGroupingDOB.getShipmentMode();
								if(shipMode==1 || shipMode==3  || shipMode==5 || shipMode==7)
								{
									airChecked	=	"Checked";
									airDisabled =    "disabled";
								}
								if(shipMode==2 || shipMode==3  || shipMode==6 || shipMode==7)
								{
									seaChecked	=	"Checked";
									seaDisabled =    "disabled";
								}
								if(shipMode==4  || shipMode==5 || shipMode==6 || shipMode==7)
								{
									truckChecked=	"Checked";
									truckDisabled =    "disabled";
								}

								if(!operation.equals("Modify"))
								{ airDisabled =    "disabled";seaDisabled =    "disabled";truckDisabled =    "disabled";}
							}					
						}else
						{
							throw new Exception("Invalid data");
						}
				}
				else if(operation.equals("View")){//Added by Anil.k for Issue 236357 on 22Feb2011
					if(dataList2!=null && dataList2.size()>0)
						{
							dataList		=	(ArrayList)dataList2.get(0);
							buyChargeList	=	(ArrayList)dataList2.get(1);
							for(int i=0;i<buyChargeList.size();i++){
							chargeGroupingDOB2 = (ChargeGroupingDOB)buyChargeList.get(i);
							if(chargeGroupingDOB2!=null && "Origin".equals(chargeGroupingDOB2.getCostIncurredIn()))
								originCharges++;
							else if(chargeGroupingDOB2!=null && "Destination".equals(chargeGroupingDOB2.getCostIncurredIn()))
								destCharges++;
							}
							sellChargeList	=	(ArrayList)dataList2.get(2);
							System.out.println(buyChargeList+"!!!!");
							System.out.println(sellChargeList+"22222222");
							chargeGroupingDOB  = (ChargeGroupingDOB)dataList.get(0);
							if(chargeGroupingDOB!=null && chargeGroupingDOB.getShipmentMode()>0)
							{
								shipMode	=	chargeGroupingDOB.getShipmentMode();
								if(shipMode==1 || shipMode==3  || shipMode==5 || shipMode==7)
								{
									airChecked	=	"Checked";
									airDisabled =    "disabled";
								}
								if(shipMode==2 || shipMode==3  || shipMode==6 || shipMode==7)
								{
									seaChecked	=	"Checked";
									seaDisabled =    "disabled";
								}
								if(shipMode==4  || shipMode==5 || shipMode==6 || shipMode==7)
								{
									truckChecked=	"Checked";
									truckDisabled =    "disabled";
								}

								if(!operation.equals("Modify"))
								{ airDisabled =    "disabled";seaDisabled =    "disabled";truckDisabled =    "disabled";}
							}					
						}else
						{
							throw new Exception("Invalid data");
						}
				}//Ended by Anil.k for Issue 236357 on 22Feb2011
			}

%>
<html>
<head>
<title>Charge Grouping <%=operation%></title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<script src ="../html/dynamicContent.js"></script>
<script src ='../html/eSupply.js'></script>
<script>
<%
	String onLoad	="";
	if(operation!=null && (operation.equals("Add") || operation.equals("Modify")))
	{
		onLoad	="initialize()";
	%>
//************Dynamic row methods starts******************//
		var rowNo	=	0;
		function initialize()
		{
			importXML('../xml/chargegroupmaster.xml');
		}
		function validateBeforeDeletion()
		{
			return true;
		}
		function validateBeforeCreation()
		{
			return true;
		}
		function initializeDynamicContentRows()
		{
			setValues();
		}
		function setValues()
		{
			var tableObj = document.getElementById("chargegroupmaster");
<%
			if(dataList!=null && dataList.size()>0)	
			{
				dataListSize = dataList.size();
				for(int i=0;i<dataListSize;i++)
				{
					chargeGroupingDOB  = (ChargeGroupingDOB)dataList.get(i);
%>
					idcnt	=	tableObj.getAttribute("idcounter");
					createDynamicContentRow(tableObj.getAttribute("id"));
					document.getElementById("chargeid"+idcnt).value='<%=chargeGroupingDOB.getChargeIds()%>';
					document.getElementById("chargedescid"+idcnt).value='<%=chargeGroupingDOB.getChargeDescId()%>';
<%
				}
			}
			else
			{
%>
				if(tableObj.getAttribute("idcounter")==1)
							createDynamicContentRow(tableObj.getAttribute("id"));
<%			}
			//Added by Anil.k for Enhancement 231214 on 24Jan2011
			originValues = chargeGroupingDOB!=null?chargeGroupingDOB.getOriginCountry():"";
			destValues = chargeGroupingDOB!=null?chargeGroupingDOB.getDestinationCountry():"";
			//System.out.println("originValues:"+originValues+"	 	destValues:"+destValues);
			
%>		
                       document.forms[0].originCountry.value ='<%=originValues%>';
			document.forms[0].destination.value = '<%=destValues%>';	
                        var ov = '<%=originValues%>';			
			var ovr = ov.split(",");
			var dv = '<%=destValues%>';
			var dvr = dv.split(",");
                        // Commented by Silpa 
			/*for(i=0;i<ovr.length;i++)
			{
				document.getElementById("originCountry").options[i]=new Option(ovr[i],ovr[i]);
				document.getElementById("originCountry").options[i].selected=true;
			}
			for(i=0;i<dvr.length;i++)
			{
				document.forms[0].destination.options[i]=new Option(dvr[i],dvr[i]);
				document.forms[0].destination.options[i].selected=true;
			}			
			if(document.forms[0].destination.value=="null")
				document.forms[0].destination.options[0]=new Option("","");
			if(document.forms[0].originCountry.value=="null")
				document.forms[0].originCountry.options[0]=new Option("","");
			Ended by Anil.k */
		}
		function defaultFunction(currentRow)
		{

		}
		function defaultDeleteFunction()
		{

		}
	//*************end of dynamic row methods********//
<%
	}
%>
		var Win		=	null;
		function openChargeIdsLOV(input)
		{
			var id			=	input.id;
			var Bname		=	input.name;
			var index		=	id.substring(Bname.length);
			var searchStr	=	document.getElementById("chargeid"+index).value;
			var name		=	"chargeid"+index;
			var fromWhere	=	"chargegroup";
			var aircheck	=	'';
			var seacheck	=	'';
			var truckcheck	=	'';
			if(shipModeChecked(index))
			{
				if(document.getElementById("aircheckbox").checked)
					aircheck	=	'checked';
				if(document.getElementById("seacheckbox").checked)
					seacheck	=	'checked';
				if(document.getElementById("truckcheckbox").checked)
					truckcheck	=	'checked';
				var Url			= "QMSLOVChargeIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&selection=single&fromWhere="+fromWhere+"&aircheck="+aircheck+"&seacheck="+seacheck+"&truckcheck="+truckcheck+"&terminalId=<%=terminalId%>";
				
				showLOV(Url);
			}
		}
		function openChargeDescIdsLOV(input)
		{
			var id			=	input.id;
			var Bname		=	input.name;
			var index		=	id.substring(Bname.length);
			var searchStr	=	document.getElementById("chargedescid"+index).value;//Modified by RajKumari on 11/10/2008 for WPBN 143519...  
			var name		=	"chargedescid"+index;
			var chargeId	=	document.getElementById("chargeid"+index).value;
			var fromWhere	=	"chargegroup";
			var aircheck	=	'';
			var seacheck	=	'';
			var truckcheck	=	'';
			if(shipModeChecked(index))
			{
				if(chargeId=='')
				{ alert("Please Select chargeId");return false;}
				if(document.getElementById("aircheckbox").checked)
					aircheck	=	'checked';
				if(document.getElementById("seacheckbox").checked)
					seacheck	=	'checked';
				if(document.getElementById("truckcheckbox").checked)
					truckcheck	=	'checked';
				var Url			= "QMSLOVDescriptionIds.jsp?Operation=<%=operation%>&searchString="+searchStr+"&name="+name+"&selection=single&fromWhere="+fromWhere+"&aircheck="+aircheck+"&seacheck="+seacheck+"&truckcheck="+truckcheck+"&chargeId="+chargeId+"&terminalId=<%=terminalId%>";
				showLOV(Url);
			}
		}
		function shipModeChecked(index)
		{
			var aircheckname	=	document.getElementById("aircheckbox");
			var seacheckname	=	document.getElementById("seacheckbox");
			var truckcheckname	=	document.getElementById("truckcheckbox");
			if(!aircheckname.checked && !seacheckname.checked && !truckcheckname.checked)
			{
				alert("Please Select a Shipment Mode");
				return false;
			}

			return true;
		}
<%
	if(operation!=null && operation.equals("Add"))
	{
%>
		function isChargeGroupEntered()
		{
			var chargegroupid	=	document.forms[0].chargegroupid;
					if(chargegroupid.value=='')
					{
						alert("Please Enter the Charge Group Id");
						chargegroupid.focus();
						return false;
					}
			return true;
		}
<%
	}
%>
	function isShipModeChecked()
	{
				var airCheck	=	document.forms[0].aircheckbox;
				var seaCheck	=	document.forms[0].seacheckbox;
				var truckCheck	=	document.forms[0].truckcheckbox;
				
				if(!airCheck.checked && !seaCheck.checked && !truckCheck.checked)
				{
					alert("Please Select At Least one Shipment Mode");
					airCheck.focus();
					return false;
				}
				return true
	}
	
	function ischargesSelected()
	{
		var chargeid	 = document.forms[0].chargeid;
		var chargedescid = document.forms[0].chargedescid;
		if(chargeid.length>1)
		{
			for(i=0;i<chargeid.length;i++)
			{
				if(chargeid[i].value=='')
				{
					alert("Please Enter the Charge Id at Line no :"+(i+1));
					chargeid[i].focus();
					return false;
				}
				if(chargedescid[i].value=='')
				{
					alert("Please enter the Charge Description Id at Line no :"+(i+1));
					chargedescid[i].focus();
					return false;
				}
				
				
				for(j=i+1;j<chargeid.length;j++)
				{

					if(chargeid[i].value==chargeid[j].value && chargedescid[i].value==chargedescid[j].value)
					{
							alert("Duplicate records are found at line no :"+(j+1));
							chargeid[j].focus();
							return false;
					}
				}
			}
		}else
		{
				if(chargeid.value=='')
				{
					alert("Please Enter the Charge Id at Line no :1");
					chargeid.focus();
					return false;
				}
				if(chargedescid.value=='')
				{
					alert("Please Enter the Charge Description Id at Line no :1");
					chargedescid.focus();
					return false;
				}			
		}
		//return validateChargeDesc(chargedescid[i].value);
		return true;
	}

	function mandatory()
	{
		var count;
		var orgtemp;//Added by Anil.k for Enhancement 231214 on 24Jan2011
		var desttemp;//Added by Anil.k for Enhancement 231214 on 24Jan2011
<% if(!(operation!=null && operation.equals("Invalidate"))){
	if(operation!=null && operation.equals("Add"))
	{
%>
		if(!isChargeGroupEntered())
		{	return false;}
<%
	}
%>
		if(!isShipModeChecked())
		{	return false;}

		if(!ischargesSelected())
		{	return false;}
			if(!validateChargeDesc())
		{	
			return false;
			}	
		
			//Added by Anil.k for Enhancement 231214 on 24Jan2011
			/*for(i=0;i<document.forms[0].originCountry.length;i++)
			{
				if(i==0)
					orgtemp = document.forms[0].originCountry.value;
				else
					orgtemp = orgtemp+","+document.forms[0].originCountry.value;
			}


			for(i=0;i<document.forms[0].destination.length;i++)
			{
				if(i==0)
					desttemp = document.forms[0].destination.options[i].value;
				else
					desttemp = desttemp+","+document.forms[0].destination.options[i].value;
			}
			*/
			orgtemp = document.forms[0].originCountry.value;
			desttemp = document.forms[0].destination.value;
			//alert("kishore: "+orgtemp +"			"+desttemp);
			
			document.forms[0].originValue.value=orgtemp;
			
			document.forms[0].destValue.value=desttemp;
			//Ended by Anil.k for Enhancement 231214 on 24Jan2011					
		if(document.forms[0].submit!=null)
		{
			document.forms[0].submit.disabled	=true;
		}
		if(document.forms[0].resetB!=null)
		{
			document.forms[0].resetB.disabled	=true;
		}<%}%>
		
		return true;
		
	}
	
	function onCheckShipmentMode(input)
	{
		var name	=	input.name;
		var chargeid	 = document.forms[0].chargeid;
		var chargedescid = document.forms[0].chargedescid;
		if(input.checked==false)
		{
			if(chargeid.length>1)
			{
				for(i=0;i<chargeid.length;i++)
				{
					chargeid[i].value		= '';
					chargedescid[i].value   = '';
				}
			}else
			{
					chargeid.value		 = '';
					chargedescid.value   = '';
			}
		}
	}
function viewContinue()//added by rk
{
<%	if("Quote".equalsIgnoreCase(fromWhat))
	{
%>		window.close();
<%	}
	else
	{
%>
		document.forms[0].action='QMSChargesGroupingMasterEnterId.jsp?Operation=View';
		document.forms[0].submit();
		return true;
<%	}
%>
}

function resetData()
		{

				if(document.forms[0].aircheckbox.disabled==false)
				    document.forms[0].aircheckbox.checked = false;

				if(document.forms[0].seacheckbox.disabled==false)
				    document.forms[0].seacheckbox.checked = false;

				if(document.forms[0].truckcheckbox.disabled==false)
				    document.forms[0].truckcheckbox.checked = false;				
				
				removeDynamicContentTableRows("chargegroupmaster");
				setValues();
		}
		function validateChargeDesc()
		{
			var chargedescid = document.forms[0].chargedescid;
		   var c ;
		   for(var k=0;k<chargedescid.length;k++)
			{
			  c = 0;
		 
		   <% 
			   if (chargeDescList!=null)
			{
			   
			   for(int i=0;i<chargeDescList.size();i++)
				{%>
                   
				  
				   if(chargedescid[k].value=='<%=chargeDescList.get(i)%>')
					{
					   c++;
					 
					}
					
			      <%}%>
						
		     if(c==0)
				{
					alert(" Please enter valid ChargeDescription at lane "+k);
					chargedescid[k].focus();
					return false;
					}
				       
					<%}
			%>
							
			}
					
			return true;			
		}
 //Added by Anil.k for the Enhancement 231214 on 24Jan2011
function showCountryIds(obj)
{	
	/*var countryValue = "";
	var country = "";
	if(obj.name == "originBtn")
	{
		countryValue = document.forms[0].originCountry.value.toUpperCase();
		country = "originCountry";
	}
	else if(obj.name == "destinationBtn")
	{
		countryValue = document.forms[0].destination.value.toUpperCase();
		country = "destination";
	}
	var Url      = 'ETCLOVCountryIds.jsp?searchString='+countryValue+'&whereClause=ChargesGroupingCountryMaster&country='+country;
	var Bars     = 'directories=no,location=no,menubar=no,status=no,titlebar=no';
	var Options  = 'scrollbars=yes,width=360,height=360,resizable=no';
	var Features =  Bars+''+Options;
	var Win      =  open(Url,'Doc',Features);*/
	var countryValue = "";
	var country = "";
	var shipmentMode = "";
	if(obj.name == "originBtn")
	{
		countryValue = document.forms[0].originCountry.value.toUpperCase();
		country = "originCountry";
	}
	else if(obj.name == "destinationBtn")
	{
		countryValue = document.forms[0].destination.value.toUpperCase();
		country = "destination";
	}	
	if(document.forms[0].aircheckbox.checked == true && document.forms[0].seacheckbox.checked == true &&  document.forms[0].truckcheckbox.checked == true)
		shipmentMode = "7";
	if(document.forms[0].aircheckbox.checked == true && document.forms[0].seacheckbox.checked == true &&  document.forms[0].truckcheckbox.checked == false)
		shipmentMode = "3";
	if(document.forms[0].aircheckbox.checked == true && document.forms[0].seacheckbox.checked == false &&  document.forms[0].truckcheckbox.checked == false)
		shipmentMode = "1";
	if(document.forms[0].aircheckbox.checked == false && document.forms[0].seacheckbox.checked == true &&  document.forms[0].truckcheckbox.checked == false)
		shipmentMode = "2";
	if(document.forms[0].aircheckbox.checked == false && document.forms[0].seacheckbox.checked == true &&  document.forms[0].truckcheckbox.checked == true)
		shipmentMode = "6";
	if(document.forms[0].aircheckbox.checked == false && document.forms[0].seacheckbox.checked == false &&  document.forms[0].truckcheckbox.checked == true)
		shipmentMode = "4";
	if(document.forms[0].aircheckbox.checked == true && document.forms[0].seacheckbox.checked == false &&  document.forms[0].truckcheckbox.checked == true)
		shipmentMode = "5";
	/*var locationId;
	
	if(toSet=='fromCountry')
	{
		locationId = document.forms[0].fromLocation.value;
	}
	else if(toSet=='toCountry')
	{
		locationId = document.forms[0].toLocation.value;
    }*/
		
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=300,resizable=no';
	var Features=Bars+' '+Options;
	
	/*var searchString = (toSet=='fromCountry')?(document.forms[0].fromCountry.value):(document.forms[0].toCountry.value);*/

	var Url="ETCLOVCountryIds1.jsp?searchString=&whereClause=ChargesGroupingCountryMaster&wheretoset="+country+"&locationId=&shipmentMode="+shipmentMode;

	var Win      =  open(Url,'Doc',Features);
}
function countryValue(obj)
{	
	if(obj.value!="")
	{
	if(obj.name == "originCountry")
	{		
		if(document.forms[0].destination.value.toUpperCase() == obj.value)
		{
			alert("Origin And Destination values should not be Same");
			obj.value="";
			obj.focus();
		}
	}
	else if( obj.name == "destination")
	{
		if(document.forms[0].originCountry.value.toUpperCase() == obj.value)
		{
			alert("Origin And Destination values should not be Same");
			obj.value="";
			obj.focus();
		}
	}
	}
}
function showLocationValues(obj,where)
{
	
	
	var data="";
        // Commented by Silpa
	//document.getElementById(where).options.length=0;
	for( i=0;i<obj.length;i++)
	{
		if(where=='QuoteId')
    		   temp=obj[i].value;
		else
        { 
			firstTemp	=obj[i].value;
		firstIndex	=	firstTemp.indexOf(0);
		if(where=="serviceLevelId")
			lastIndex	=	firstTemp.indexOf(' [');	
		else
			lastIndex	=	firstTemp.indexOf('[');
 //@@ Commented & Added by subrahmanyam for the pbn id: 212190 on 22-Jul-10				
		//firstTemp	=	firstTemp.substring(firstIndex,lastIndex);
		firstTemp	=	firstTemp.substring(0,lastIndex);
    //@@ Ended by subrahmanyam for the pbn id: 212190 on 22-Jul-10			
		temp		=   firstTemp.toString();
		secondTemp	=obj[i].value;
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
			if(where=="originCountry" || where=="destination")
			{
				if(data!="")
				data=data+","+temp1;
				else
				data=temp1;
			}
			else
			{
				if(data!="")
					data=data+","+temp;
				else	
					data=temp;
			}   
			
	    }
                // Commented by Silpa
		/*if(where=="originCountry" || where=="destination")
		{
			document.getElementById(where).options[i]=new Option(temp1,temp1);  
		}else
		{
			document.getElementById(where).options[i]=new Option(temp,temp);
		}  
	    document.getElementById(where).options[i].selected=true; */ 
	 }
	 
}
//Ended by Anil.k for the Enhancement 231214 on 24Jan2011
//@ added by silpa on 27-04-2011
function showCountryLOV(toSet)
{
	var shipmentMode = "";
	//var locationId;
	
		
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=700,height=360,resizable=no';
	var Features=Bars+' '+Options;
	
	var searchString = (toSet=='originCountry')?(document.forms[0].originCountry.value):(document.forms[0].destination.value);
	var Url='ETCLOVCountryIds1.jsp?searchString='+searchString+"&wheretoset="+toSet+"&shipmentMode="+shipmentMode+"&searchString3=&locationId=";//+document.forms[0].shipmentMode.value;


	

	var Win=open(Url,'Doc',Features);
}//ended
//@ added by silpa on 27-04-2011
function showLocationValues(obj,where)
{
	var data="";
	for( i=0;i<obj.length;i++)
		{
		firstTemp	=obj[i].value;
		firstIndex	=	firstTemp.indexOf(0);
		if(where=="serviceLevelId")
			lastIndex	=	firstTemp.indexOf(' [');	
		else
			lastIndex	=	firstTemp.indexOf('[');
		//firstTemp	=	firstTemp.substring(firstIndex,lastIndex);//@@ Commented by Govind for the pbd id: 199753
		firstTemp	=	firstTemp.substring(0,lastIndex);//@@ Added by Govind for the pbd id: 199753
		temp		=   firstTemp.toString();
		secondTemp	=obj[i].value;
		lastIndex1	=	secondTemp.lastIndexOf('[')+1;
		lastIndex2	=	secondTemp.lastIndexOf(']');	
		temp1		=	secondTemp.substring(lastIndex1,lastIndex2);
		if(where=="originCountry"||where=="destination")
		{
			if(data!="")
			data=data+","+temp1;
		else
			data=temp1;
		}
		else
		{
			if(data!="")
				data=data+","+temp;
			else	
				data=temp;
		}
		
		}
		
	document.getElementById(where).value=data;
		
	
}//ended
//added by silpa on 27-04-2011
function trimAll(obj) 
{
	var sString = obj.value;
	while (sString.substring(0,1) == ' ')
	{
		sString = sString.substring(1, sString.length);
	}
	while (sString.substring(sString.length-1, sString.length) == ' ')
	{
		sString = sString.substring(0,sString.length-1);
	}
	obj.value = sString;
}//ended

</script>
</head>
<body onLoad=<%=onLoad%>>
<form name="form1" method="post" action='QMSChargesGroupMasterProcess.jsp' onSubmit	='return mandatory(); '>
  <table width="800" border="0" cellspacing="0" cellpadding="0" bgcolor='#FFFFFF'>
    <tr> 
      <td valign="top" > 
        <table width="800" border="0" cellspacing="1" cellpadding="4">
          <tr class='formlabel'> 
            <td colspan="3">
              <table width="790" border="0" >
                <tr class='formlabel'>
                  <td>Charge Grouping - <%=operation%></td>
                  <td align=right>QS1050311</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
		<table width="800" border="0" cellspacing="1" cellpadding="4" bgcolor='#FFFFFF'>
		  <tr class='formdata'>
		  </tr>
		</table>
		<%if(!("Invalidate".equalsIgnoreCase(operation)|| operation.equals("ViewAll"))){%>
		<table width="800" border="0" cellspacing="1" cellpadding="4">
		  <tr class='formheader'>
            <td ></td>
			<td align=center>Charge Group:<font color="#FF0000">*</font></td> 
			<td align=center>Shipment Mode:<font color="#FF0000">*</font></td>
			<td ></td>
		  </tr>
		  <tr class='formdata'>
            <td ></td>
			<td align=center><input type='text' class='text' name='chargegroupid' size=20 maxLength='40' onblur='specialCharFilter1(this.value,"chargegroupid");toUpper(this);' value='<%=chargeGroupId%>' <%=("Add".equals(operation)?"":"readOnly")%>></td> 
			<td align=center>
				<input type='checkbox' name='aircheckbox' onClick='onCheckShipmentMode(this)' <%=airChecked%> <%=airDisabled%>>Air
				     <input type='hidden' name='airChecked' value='<%=airChecked%>' >
				<input type='checkbox' name='seacheckbox' onClick='onCheckShipmentMode(this)' <%=seaChecked%> <%=seaDisabled%>>Sea
				     <input type='hidden' name='seaChecked' value='<%=seaChecked%>' >
				<input type='checkbox' name='truckcheckbox' onClick='onCheckShipmentMode(this)' <%=truckChecked%> <%=truckDisabled%>>Truck
				     <input type='hidden' name='truckChecked' value='<%=truckChecked%>' ></td>
			<td ></td>
		  </tr>
		</table>
		<!-- Added by Anil.k for Enhancement 231214 on 24/01/2011 -->
		<table width="800" border="0" cellspacing="1" cellpadding="4">
		<tr class='formheader'> 
            <td >&nbsp&nbsp&nbsp</td>
			<td align=center>Origin Country:</td>
			<td align=center>Destination Country:</td>			
          </tr>
		  <tr class='formdata'>
            <td ></td>
		  <% if("Delete".equals(operation) || "View".equals(operation))
		  { %>
		  <td align=center> <%=chargeGroupingDOB.getOriginCountry()!=null?chargeGroupingDOB.getOriginCountry():""%>
		  <input type='hidden' class='text' name='originCountry' value='<%=chargeGroupingDOB.getOriginCountry()!=null?chargeGroupingDOB.getOriginCountry():""%>' readOnly></td> 
		  <td align=center> <%=chargeGroupingDOB.getDestinationCountry()!=null?chargeGroupingDOB.getDestinationCountry():""%>
		  <input type='hidden' class='text' name='destination' value='<%=chargeGroupingDOB.getDestinationCountry()!=null?chargeGroupingDOB.getDestinationCountry():""%>' readOnly></td>
		  <% } else { %>
		   <td align=center>
                   <!-- Modified by Silpa -->
		   <input name='originCountry' class='text'  size='8'   onblur='trimAll(this);this.value=this.value.toUpperCase()'>
           <input class="input" name="originBtn" onclick='showCountryLOV("originCountry")' type="button" value="...">
		   <input type="hidden" name="originValue">
		  <!--  <input type='text' class='text' name='originCountry' size=20 maxLength='40' onBlur='countryValue(this);specialCharFilter1(this.value,"originCountry");toUpper(this);' value='<%=chargeGroupingDOB!=null?chargeGroupingDOB.getOriginCountry():""%>'>&nbsp;
		  <input type="button" class='input' value="..." name="originBtn" onClick=showCountryIds(this) > --></td> 
		  <td align=center>
		  <input name='destination'  class='text'  size='8'  onblur='trimAll(this);this.value=this.value.toUpperCase()'>
          <input class="input" name="destinationBtn"  onclick='showCountryLOV("destination")' type="button" value="...">
		  <input type="hidden" name="destValue">
		  
		  <!-- <input type='text' class='text' name='destination' size=20 maxLength='40' onBlur='countryValue(this);specialCharFilter1(this.value,"destination");toUpper(this);' value='<%=chargeGroupingDOB!=null?chargeGroupingDOB.getDestinationCountry():""%>'>&nbsp;<input type="button" class='input' value="..." name="destinationBtn" onClick=showCountryIds(this) > --></td>
		  <% } %>
		  </tr>
		</table>
		<!-- Ended by Anil.k for Enhancement 231214 on 24/01/2011 -->
        <!--<table width="800" border="0" cellspacing="1" cellpadding="4" id="chargegroupmaster"  idcounter="1" 
         defaultElement="chargegroup" xmlTagName="chargegroupmaster" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" maxRows=20 bgcolor='#FFFFFF'>-->
          <!--@@Modified by Kameswari for Chargegroup enhancement-->
          <table width="800" border="0" cellspacing="1" cellpadding="4" id="chargegroupmaster"  idcounter="1" 
         defaultElement="chargegroup" xmlTagName="chargegroupmaster" functionName="defaultFunction" deleteFunctionName="defaultDeleteFunction" onBeforeDelete="validateBeforeDeletion" onBeforeCreate="validateBeforeCreation" maxRows=40 bgcolor='#FFFFFF'>
		<% if(!"View".equals(operation)){ %>	
          <tr class='formheader'> 
            <td >&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</td>
			<td align=center>Charge Id:<font color="#FF0000">*</font></td>
			<td align=center>Charge Description Id:<font color="#FF0000">*</font></td>
			<td ></td>
          </tr>
		  <%}%>
<%
		//if("Delete".equals(operation) || "View".equals(operation))
		if("Delete".equals(operation))//Modified by Anil.k for Issue 236357 on 23Feb2011
		{
			if(dataList!=null && dataList.size()>0)	
			{
				dataListSize = dataList.size();
				for(int i=0;i<dataListSize;i++)
				{
					chargeGroupingDOB  = (ChargeGroupingDOB)dataList.get(i);
%>
				  <tr class='formdata'> 
					<td >&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp</td>
					<td align=center><%=(chargeGroupingDOB.getChargeIds())%><input type='hidden' class='text' name='chargeid' value='<%=(chargeGroupingDOB.getChargeIds())%>' readOnly></td>
					<td align=center><%=chargeGroupingDOB.getChargeDescId()%><input type='hidden' class='text' name='chargeid' value='<%=chargeGroupingDOB.getChargeDescId()%>' readOnly></td>
					<td ></td>
				  </tr>	
<%
				}
			}
		}
%>
		</table>		
		<!-- Added by Anil.k for issue 236357 on 22Feb2011  -->
		<% if("View".equals(operation)){ %>		
		<table width="800" border="0" cellspacing="1" cellpadding="4" id="chargesDetails">
		<%if(originCharges>0){%>
		<tr class='formheader'>
		<th>Charge Id</th>
		<th>Origin Charges</th>
		<th>Weight Break Slab</th>
		<th>Currency</th>
		<th>Buy Rate</th>
		<th>Sell Rate</th>
		<th>Basis</th>
		<th>Density Ratio</th>
		</tr>
		<%if(buyChargeList!=null && buyChargeList.size()>0)
			{
				String chargeDescIdValue = "";
				int buyChargeListSize = buyChargeList.size();				
				for(int i=0;i<buyChargeListSize;i++)
				{
					chargeGroupingDOB  = (ChargeGroupingDOB)buyChargeList.get(i);
					if(chargeGroupingDOB!=null && "Origin".equals(chargeGroupingDOB.getCostIncurredIn())){%>
					<tr class='formdata'>
					<%if(!chargeDescIdValue.equals(chargeGroupingDOB.getChargeDescId())){%>
					<td align="center"><%=chargeGroupingDOB.getChargeIds()%></td>
					<td align="center"><%=chargeGroupingDOB.getChargeDescId()%></td>
					<%}else{%>
					<td></td>
					<td></td>
					<%}
					chargeDescIdValue = chargeGroupingDOB.getChargeDescId();%>
					<td align="center"><%=chargeGroupingDOB.getChargeSlab()!=null?chargeGroupingDOB.getChargeSlab():""%></td>
					<td align="center"><%=chargeGroupingDOB.getCurrency()!=null?chargeGroupingDOB.getCurrency():""%></td>
					<td align="center"><%=chargeGroupingDOB.getBuyChargeRate()%></td>
					<td align="center"><%=chargeGroupingDOB.getSellChargeRate()%></td>
					<td align="center"><%=chargeGroupingDOB.getChargeBasis()!=null?chargeGroupingDOB.getChargeBasis():""%></td>
					<td align="center"><%=chargeGroupingDOB.getDensityCode()!=null?(1+":"+chargeGroupingDOB.getDensityCode()):""%></td>
					</tr>
			<%
				}}
			}}
		if(destCharges>0){%>
		<tr class='formdata'>
		<th>Charge Id</th>
		<th>Destination Charges</th>
		<th>Weight Break Slab</th>
		<th>Currency</th>
		<th>Buy Rate</th>
		 <th>Sell Rate</th>
		<th>Basis</th>
		<th>Density Ratio</th>
		</tr>
		<%if(buyChargeList!=null && buyChargeList.size()>0)
			{
				int buyChargeListSize = buyChargeList.size();				
					String chargeDescIdValue = "";
				for(int i=0;i<buyChargeListSize;i++)
				{
					chargeGroupingDOB  = (ChargeGroupingDOB)buyChargeList.get(i);
					if(chargeGroupingDOB!=null && "Destination".equals(chargeGroupingDOB.getCostIncurredIn())){%>
					<tr class='formdata'>					
					<%if(!chargeDescIdValue.equals(chargeGroupingDOB.getChargeDescId())){%>
					<td align="center"><%=chargeGroupingDOB.getChargeIds()%></td>
					<td align="center"><%=chargeGroupingDOB.getChargeDescId()%></td>
					<%}else{%>
                   	<td></td>
					<td></td>
					<%}
					   chargeDescIdValue =chargeGroupingDOB.getChargeDescId(); %>
					<td align="center"><%=chargeGroupingDOB.getChargeSlab()%></td>
					<td align="center"><%=chargeGroupingDOB.getCurrency()%></td>
					<td align="center"><%=chargeGroupingDOB.getBuyChargeRate()%></td>
					<td align="center"><%=chargeGroupingDOB.getSellChargeRate()%></td>
					<td align="center"><%=chargeGroupingDOB.getChargeBasis()%></td>
					<td align="center"><%=chargeGroupingDOB.getDensityCode()!= null?chargeGroupingDOB.getDensityCode():""%></td>
					</tr>
			<%
				}}
			} }%>
		</table>
		<% } %>
		<!-- Ended by Anil.k for issue 236357 on 22Feb2011  -->
		<%}else {%>
		<table width="800" border="0" cellspacing="1" cellpadding="4" bgcolor='#FFFFFF'>
		  <tr class='formheader'>
            <td ></td>
			<td align=center>Charge Group:</td> 
			<td align=center width='200'>Shipment Mode:</td>
            <td align=center>Terminal Id</td>
			<td align=center>Charge Id:</td>
			<td align=center>ChargeDescrption Id:</td>
			<%if("Invalidate".equalsIgnoreCase(operation)){%>
			<td align=center>Invalidate:</td>
			<%}%>
		  </tr>
		  <%
	        ArrayList chargeIdList       = null;
	        ArrayList descriptionIdList  = null;
	      for(int i=0;i<dataList.size();i++){
				chargeGroupingDOB  =  (ChargeGroupingDOB)dataList.get(i);
	             shipMode	=	chargeGroupingDOB.getShipmentMode();

				        airChecked = "";
						
						seaChecked = "";
						
                        truckChecked="";
                        

						if(shipMode==1 || shipMode==3  || shipMode==5 || shipMode==7)
						{
							airChecked	=	"Checked";
							
						}
						if(shipMode==2 || shipMode==3  || shipMode==6 || shipMode==7)
						{
							seaChecked	=	"Checked";
							
						}
						if(shipMode==4  || shipMode==5 || shipMode==6 || shipMode==7)
						{
							truckChecked=	"Checked";
							
						}
				chargeIdList       =  chargeGroupingDOB.getChargeIdList();
				descriptionIdList  =  chargeGroupingDOB.getDescriptionIdList();

	      %>
		  
            
			<%for(int k=0;k<chargeIdList.size();k++){
			  if(k==0){%>
			  <tr class='formdata'>
			<td ></td>
			<td align=center><%=chargeGroupingDOB.getChargeGroup()%></td> 
			<td align=center>
			<input type='checkbox' name='aircheckbox' onClick='onCheckShipmentMode(this)' <%=airChecked%> disabled>Air
			<input type='checkbox' name='seacheckbox' onClick='onCheckShipmentMode(this)' <%=seaChecked%> disabled>Sea
			<input type='checkbox' name='truckcheckbox' onClick='onCheckShipmentMode(this)' <%=truckChecked%> disabled>Truck</td>
			<td align=center><%=chargeGroupingDOB.getTerminalId()!=null?chargeGroupingDOB.getTerminalId():""%></td>
			<td align=center><%=chargeIdList.get(k)%></td>
			<td align=center><%=descriptionIdList.get(k)%></td>
			
			<%if(operation.equals("Invalidate")){%>
			<td><input type='checkBox' name ="invalidate<%=i%>" <%="T".equalsIgnoreCase(chargeGroupingDOB.getInvalidate())?"checked":""%>></td>
			<%}%>
			</tr>
			<%}else{%>
			<tr class='formdata'>
             <td></td>
			 <td></td>
			 <td></td>
			<td align=center><%=chargeGroupingDOB.getTerminalId()!=null?chargeGroupingDOB.getTerminalId():""%></td>
			 <td align=center><%=chargeIdList.get(k)%></td>
			 <td align=center><%=descriptionIdList.get(k)%></td>
			 <%if(operation.equals("Invalidate")){%>
				<td></td>
			<%}%>
			 </tr>
			<%}%>
			

		<%}}}%>
        <table width="800" border="0" cellspacing="0" cellpadding="4" bgcolor='#FFFFFF'>
          <tr class='text'> 
            <td colspan='4' ><FONT COLOR="red">*</FONT>Denotes Mandatory</td>
            <td colspan="4" align="right" width="397"> 
<%
		if(operation!=null && (operation.equals("Add") || operation.equals("Modify")))
		{
%>
          
					 <input type="submit" value="Submit" name="submit" class="input">
					 <input type="button" value="Reset" name="resetB" class="input" onClick='resetData()'>
<%		}else if(operation!=null && operation.equals("Delete") ||  operation.equals("Invalidate"))
		{
%>
					  <input type="submit" value="Submit" name="submit" class="input">
<%		}else{
%>
					<input type="button" value="Continue" name="Continue" class="input" onClick='return viewContinue()'>
<%}%>

			  <input type='hidden' name='Operation' value='<%=operation%>'>
			  <input type='hidden' name='terminalId' value='<%=terminalId%>'>
			  
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
</body>
</html>
<%	}catch(ObjectNotFoundException e)
	{e.printStackTrace();
		success=false;
		msg	=	"Record doesnt Exist for "+operation+" with ChargeGroup Id :";
		//Logger.error(FILE_NAME,msg+e);
    logger.error(FILE_NAME+msg+e);
	}catch(Exception e)
	{e.printStackTrace();
		success	=	false;
		msg	=	"Exception while reading the data :";
		//Logger.error(FILE_NAME,"Exception while retrieving the data"+e);
    logger.error(FILE_NAME+"Exception while retrieving the data"+e);
	}
	if(!success)
	{
				if(operation!=null && operation.equals("Modify"))
				{
					errorMessage			=  msg+chargeGroupId;
					url						=	"QMSChargesGroupingMasterEnterId.jsp?Operation=Modify";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Modify")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}else if(operation!=null && (operation.equals("Delete")))
				{
					errorMessage			=	msg+chargeGroupId;
					url						=	"QMSChargesGroupingMasterEnterId.jsp?Operation=Delete";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Delete")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation!=null && (operation.equals("View")))
				{
					errorMessage			=	msg+chargeGroupId;
					url						=	"QMSChargesGroupingMasterEnterId.jsp?Operation=View";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","View")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
				else if(operation!=null && (operation.equals("Invalidate")))
				{
					errorMessage			=	"Error while getting Details";
					url						=	"QMSChargesGroupingMasterEnterId.jsp?Operation=View";
					errorMessageObject      =  new ErrorMessage(errorMessage,url); 
					keyValueList.add(new KeyValue("ErrorCode","ERR")); 	
					keyValueList.add(new KeyValue("Operation","Invalidate")); 	
					errorMessageObject.setKeyValueList(keyValueList);
					request.setAttribute("ErrorMessage",errorMessageObject);
				}
	%>
					<jsp:forward page="../QMSESupplyErrorPage.jsp" />
	<%
	}
%>