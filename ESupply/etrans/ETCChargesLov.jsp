
<%--
%
% Copyright(c) 1999-2001 by FourSoft,Pvt Ltd.All Rights Reserved.
% This Software is the proprietary information of FourSoft,Pvt Ltd.
% Use is subject to license terms
%
% esupply-V 1.x
%
--%>

<%--
 %		File					:	ETCChargesLov.jsp 
 %		Sub-module name			:	
 %		Module name				:	ETrans
 
 %		Purpose of the class	:	This file is invoked when clicked on the Charge LOV in the ChargeEnterId Screen. In this all the Charge details particular to that
 %		ChargeId are displayed in the List Box and Text Area. Once Selected any one of the Charge ID ,Details related to that ChargeId
 %		are displayed in the respective Text Fields. If no charges are available for this ChargeId then a Text Area with a message
 % 		No Charge Details are available for this ChargeId'.
 %		This file will interacts with ChargeMasterSession Bean and then calls the method getChargeIds which inturn 
 %		retrive the details.These details are then set to the respective varaibles in the Object ChargeMasterJSPBean

 %		Author					:	Raghavendra K	
 %		Date					:	
 %		Modified history		:
--%>


<%@ page import = "java.util.Vector,com.foursoft.esupply.common.util.Logger,					
					com.qms.setup.ejb.sls.SetUpSessionHome,
					com.qms.setup.ejb.sls.SetUpSession" %>
<jsp:useBean id="loginbean"  class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%
	int 	shipmentMode 	=	7; //Integer.parseInt(request.getParameter("shipmentMode"));
    String	moduleIndicator	=	loginbean.getModuleIndicator(); 
	String 	chargetype 		= 	"I" ;//request.getParameter("chargetype").toUpperCase();
	int 	query 			= 	1;//Integer.parseInt(request.getParameter("query"));
	String 	selectionType 	= 	(query==2) ? "multiple" :"";	
    String  searchId		=   request.getParameter("searchId");
	System.out.println("the Serach id is---------------->"+searchId);
   // System.out.println("account status :" + loginbean.getAccountsCredentials().getAcctIdStatus().toLowerCase());
   System.out.println("loginbean.getAccountsCredentials() = "+loginbean.getAccountsCredentials());
	System.out.println("THIS IS AT ....1");
	String 	isAcctId 		= 	"yes" ;//loginbean.getAccountsCredentials().getAcctIdStatus().toLowerCase();
	String	companyId		=	null ;
	String  operation		= 	request.getParameter("operation");
	System.out.println("THIS IS AT ....2");
    
	if(moduleIndicator.equalsIgnoreCase("ELOG"))
		companyId = loginbean.getCompanyId();
    System.out.println("THIS IS AT ....3");

//System.out.println("Shipment Mode:" + shipmentMode+", chargetype: "+chargetype+" query: "+query);
    SetUpSessionHome home = null;  // declaring home
   	SetUpSession remote   = null;  // declaring remote
	
	
	java.util.Vector chargeIds		= null;
	java.util.Vector abbrDesc		= null;			
	java.util.Vector  vecDesc      	= null;
	java.util.Hashtable ht 			= null;
	int len                        	= 0;
	int len1                       	= 0;
 try
 {	
	if(chargeIds == null)
    {
		System.out.println("EAChargeMasterChargeLov.jsp the shipmentMode -->"+shipmentMode);
		System.out.println("the chargeType -->"+chargetype+"Query-->"+query+":moduleIndicator->"+moduleIndicator+":companyId->"+companyId+":operation->"+operation);
		 
			 home 		=	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
			 remote 		= 	home.create();
  		     java.util.Vector 		   chDetails[]    = remote.getChargeIds_Taxes(searchId);
		
		
		if(chDetails.length>0)
		{
			chargeIds = chDetails[0];
			abbrDesc = chDetails[1];
			vecDesc = chDetails[2];
		}
		if(chargeIds != null)
       		 len = chargeIds.size();
		if(vecDesc != null)
        	 len1 = vecDesc.size();
     } 
  }
	
  catch(Exception ee)
  {
	System.out.println("Error in EAChargeMasterLov.jsp file : "+ee.toString());

  }
%>

<html>
<title>Select</title>
<style>
.input {
background-color: #cacaff; border-style: solid; border-width: 1
}
.select	
{font-family:tahoma,san-serif;font-size:12px;color:#000066;background-color:#f2f2f9;
}
</style>
<head>
<script>
 var ledgerArray = new Array();
function  populateChargeNames()
{	
 
<%	
   if(len == len1)
   {		
		for( int i=0;i<len;i++ )
	  	{	
			String arrValue = "";		
			String str = chargeIds.elementAt(i).toString();
			if(query == 3)
			{
				arrValue = ht.get(new String(str)).toString();
			}
			String abbrChargeDesc = "";
			if(	abbrDesc.elementAt(i) != null)
			{
				abbrChargeDesc  = abbrDesc.elementAt(i).toString();
			}
			String chargeDesc  = vecDesc.elementAt(i).toString();
			StringBuffer sb1 = new StringBuffer(" ");
			StringBuffer sb2 = new StringBuffer(" ");
			for(int k=0; k<(6-str.length()); k++)
			{
				sb1.append(" ");
			}
			for(int j=0; j<(14-abbrChargeDesc.length()); j++)
			{
				sb2.append(" ");
			}
			String str1 = str+sb1.toString()+abbrChargeDesc+sb2.toString()+chargeDesc;
%>				
		document.forms[0].chargeNames.options[ <%= i %> ] = new Option('<%= str1 %>','<%= str %>');
		ledgerArray[<%= i %>] = '<%=arrValue%>';
<%	
	 }		
   }	
%>
}
function getChargeNames()
{
	var type = '<%=selectionType%>';
	type = type.toUpperCase();
	if(type == 'MULTIPLE')
	{
		setMultipleCharges();
	}
	else
	{
		setChargeNames();
	}
}
function setChargeNames()
{
	if(document.forms[0].chargeNames.selectedIndex == -1)
	{
		alert("Please select atleast one ChargeId")
	}
	else
	{
		var qry = <%=query%>;
		var actstatus = '<%=isAcctId%>';
    	firstTemp = document.forms[0].chargeNames.options[document.forms[0].chargeNames.selectedIndex].value
		temp = firstTemp.toString();		
		window.opener.document.forms[0].ChargeId.value=temp;
		if(qry == '3')
		{
			var actledger = ledgerArray[document.forms[0].chargeNames.selectedIndex].split("$");
			if(actstatus == 'yes')
			{
				var ids = actledger[0].split("-");
				
				window.opener.document.forms[0].acctid.value=ids[0]+ids[1];
			}
			window.opener.document.forms[0].acctname.value=actledger[1];
		}
		window.close();
	}
}
function setMultipleCharges()
{
	var len = document.forms[0].chargeNames.length;
	var str="";
	if(document.forms[0].chargeNames.selectedIndex == -1)
	{
		alert("Please select atleast one Charge")
	}	
	else
	{
		for(i=0; i <len; i++)
		{
			if(document.forms[0].chargeNames.options[i].selected)
			{
			  str = str + "," + document.forms[0].chargeNames.options[i].value ;
			}
		}
		opener.getChargeCodes(str);
		window.close();
	}
}
</script>
</head>
<body bgcolor="#f2f2f9" onLoad="populateChargeNames()">
<form> 
<center>
    <b><font face="Verdana" size="2">Charge Ids</font></b> 
  </center>
 <%
   if(len > 0)
   {
 %>	
  <p align="center">
  <FONT face="Verdana" size="1" ><b>Note:-</b> Displayed are ChargeId, Abbreviation and Description.</FONT>
  <select size="15" class='select' name="chargeNames" selected=0 style="FONT-FAMILY: monospace" <%=selectionType%> >    
  </select>
  </p>
  <p align="center">
  <input type="button" value=" Ok " name="OK" onClick="getChargeNames()" class='input'>
  <input type="button" value="Cancel" name="B2" onClick="window.close()" class='input'></p>
 <%
   }
   else
   { 
%>
     <br><br>
	 <p align="center">
	     <textarea rows="6" name="notes" cols="30" readOnly class='select'>No Charge Ids avialable.
		 </textarea>  
		  <br>
		  <input type="button" value="Close" name="B2" onClick="window.close()" class='input'>
	</p>
<%
	}		  	
%>	
</form>
</body>
</html>
