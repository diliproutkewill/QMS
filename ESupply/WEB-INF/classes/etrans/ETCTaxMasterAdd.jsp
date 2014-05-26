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
	Program Name	:ETCTaxMasterAdd.jsp
	Module Name		:ETrans
	Task			:TaxMaster
	Sub Task		:Add
	Author Name		:Ushasree.Petluri
	Date Started	:September 11,2001
	Date Completed	:September 12,2001
	Date Modified	:September 11,2001 by Ushasree.P
	Description	:This file main purpose is to Add the Tax Data to the database and view Add,Modify,View,Delete Screens.
*/
%>
<%@ page import	=	"javax.naming.InitialContext,java.util.ArrayList,
				 	org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.setup.taxes.bean.TaxMaster"%>
					
<jsp:useBean id="loginbean" scope="session" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" />
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCTaxMasterAdd.jsp ";
%>

<% 
  logger  = Logger.getLogger(FILE_NAME);	
	String taxId	    		= null;    
	String desc		  			= null;
	String remarks	    		= null;     // temporary variables for immediate values
    String errorMessage 		= null;		// for storing error message	 
	String readOrNot    		= "";     // temporary variables
	String readOrNot1 			= null;    	// temporary variables
	InitialContext	ictx 		= null;	  	// variable for storing JNDI Initial Context 
	String form_name			= null;
    String errorCode			= null;    	// for storing the error code
	String operation			= null;
	String accessLevel			= "OT";
	String taxType				= "";
	String chargeId				= "";
	String surCharge			= "";
	double    taxPer				= 0;
	String terminalId			= "";//terminalId
	String disabledBut			= "";
	ArrayList cList				= null;
    TaxMaster taxMaster			= null;     //for storing taxMaster object
    operation 					= request.getParameter("Operation");			
	com.qms.setup.ejb.sls.SetUpSessionHome home = null;  //ETransHOSuper home reference
	com.qms.setup.ejb.sls.SetUpSession remote   = null;  // TaxMaster remote reference
    //System.out.println("accesstype->"+loginbean.getAccessType()+":user type-->"+loginbean.getUserType());
    String termId				="";
    termId=(String)request.getParameter("termId");
	if(termId==null ||  (termId !=null && termId.equals("")))
	termId =loginbean.getTerminalId();
	
	//System.out.println("ETCTaxMasterAdd.jsp file...the termid------>"+termId);
	//System.out.println("THE VALUE OF OPERATIONIS -->"+operation);
	//url: var Url='../eaccounts/EAChargeMasterChargeLov.jsp?shipmentMode='+shipmentMode+'&chargetype=i&selectionType=single&query=1'; 	
%>
<%
	try
	{
		if(loginbean.getTerminalId()== null)
		{
%>		
   		<jsp:forward page="../ESupplyLogin.jsp" />
 <%
		}
  		else
  		{	
	   		if(operation == null)
	   		{
				operation = (String)session.getAttribute("Operation");
	   		}
       		if(operation.equals("Add"))
       		{
				remarks	="";
				desc	="";
				taxId	="";
       		}		
	   		if(operation.equals("Modify") ||operation.equals("Delete") ||operation.equals("Add"))
	   		{	
		  		form_name		= "ETCTaxMasterProcess.jsp";	
			}
	   		else if(operation.equals("View"))
	   		{
				form_name	= "ETCTaxMasterEnterId.jsp";	
	   		}
 	   		if(operation.equals("Modify"))
 	   		{	
		   		readOrNot1 	= "readonly";
 	   		} 	
	   		if(operation.equals("Delete") || operation.equals("View"))
	   		{
		   		readOrNot 	= "readonly";
				disabledBut = "disabled";   
	   		}    	
  	   		if( operation.equals("Modify") || operation.equals("Delete") || operation.equals("View"))
	   		{
				try	
		    	{
				 	taxMaster  	= 	new TaxMaster();
				 	taxId 		= 	request.getParameter("taxId");
 				 	ictx 		= 	new InitialContext();		// variable for storing JNDI Initial Context  
				 	home 		= 	(com.qms.setup.ejb.sls.SetUpSessionHome)ictx.lookup("SetUpSessionBean");
				 	remote 		= 	home.create();
                 	//taxMaster 	=  	remote.getTaxDetails(taxId,loginbean,loginbean.getTerminalId());			
					 // @@ Anup modified for SPETI-3716  on 20050124
                 	taxMaster 	=  	remote.getTaxDetails(taxId,loginbean,loginbean.getTerminalId(),operation);
					// @@ 20050124
 			  	 	try
 			  	 	{				    
			    		if( taxMaster!=null )
						{
			    			taxId	= taxMaster.getTaxId();	
							desc	= taxMaster.getDesc();	
				  			remarks	= taxMaster.getRemarks();
							if(remarks==null)
							{
   					   			taxMaster.setRemarks("");   
					   			remarks	= taxMaster.getRemarks();
							}
							taxType				= taxMaster.getTaxType();
							chargeId			= taxMaster.getChargeId();
							surCharge			= taxMaster.getSurchargeId();
							taxPer				= taxMaster.getTaxPer();
							terminalId			= taxMaster.getTermId();
							//System.out.println("THE TAX THE PERCENTAGE IS-->"+taxPer);
							if(loginbean.getAccessType()!= null && loginbean.getAccessType().equals("HO_TERMINAL"))
   							{
							 	cList= new ArrayList();
								cList= taxMaster.getCountryList(); 	
   							}	
						//	System.out.println("this is inside the ETCTAXMASTER ADD IN MVD COND taxType==>"+taxType);
							//System.out.println("ETCTaxMasterAdd:the country list is (cList)-->"+cList);							
						}	
						else
						{ 
				    		errorMessage 	=	"Record does not exist with Tax Id :"+taxId;
 				    		errorCode 		= 	"RNF";
  				    		session.setAttribute("ErrorCode",errorCode);
							session.setAttribute("Operation",operation);
							session.setAttribute("ErrorMessage",errorMessage);
							session.setAttribute("NextNavigation","ETCTaxMasterEnterId.jsp"); 
				
%>	 
		   				<jsp:forward page="../ESupplyErrorPage.jsp" />  
<%			
				 		} 	 
 			  		  }			 
			  		  catch(NullPointerException ne)
			  		  {	
                    	 errorMessage 	=	"Record does not exist with Tax Id: "+taxId;
 				    	 errorCode 		= 	"RNF";
						 session.setAttribute("ErrorCode",errorCode);
						 session.setAttribute("Operation",operation);
						 session.setAttribute("ErrorMessage",errorMessage);
						 session.setAttribute("NextNavigation","ETCTaxMasterEnterId.jsp"); 
				
%>	 
		   			<jsp:forward page="../ESupplyErrorPage.jsp" />  
<%				 
			  			}	
			 
		  			 } // main try
		  			 catch( Exception exp ){
							//Logger.error(FILE_NAME,"Exception in ETransTaxMasterEnterAddProcess : ", exp.toString() );
              logger.error(FILE_NAME+"Exception in ETransTaxMasterEnterAddProcess : "+ exp.toString() );
		  			 }
		  		 }  
%>
<html>
<head>
<title>Tax <%=operation%></title>
<script>
var ope = '<%=operation%>';
function placeFocus() 
{
  if(ope=='Add')
  {
	document.forms[0].taxId.focus();
    
  }
  else if(ope=='Modify')
  {	
	document.forms[0].desc.focus();
	showValues();
  }
  else if(ope=='View'||ope=='Delete')
  {
	document.forms[0].submit.focus();
	showValues();
  }	
  else 
  {
	showValues();
  }	
}
function getSpecialCode()
{

  if(document.forms[0].remarks.value.length+1 > document.forms[0].remarks.maxlength)
	  return false;
  
  if(event.keyCode!==13)
  {
    if(event.keyCode==34 || event.keyCode==39 ||event.keyCode==59||event.keyCode==96||event.keyCode==126)
	 event.returnValue =false;
  }
  return true;
}
function getKeyCode()
{
  if(event.keyCode!==13)
  {
     if ((event.keyCode > 31 && event.keyCode < 65)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127))
	  event.returnValue =false;
  }
  return true;
}

function upperCase(input)
{
  input.value=input.value.toUpperCase();
}

function Mandatory()
{
 	for(i=0;i<document.forms[0].elements.length;i++)
 	{
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		  document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }		
 	taxId = document.forms[0].taxId.value.length;
 	desc  = document.forms[0].desc.value.length;
	remarks=document.forms[0].remarks.value.length;

//@@ Added by Ravi Kumar 25-04-2005
	var invalid = " "; // Invalid character is a space   
	if (document.forms[0].taxId.value.indexOf(invalid) > -1) 
	{
		alert("Spaces are not Allowed!. Please Enter Tax Id");
	    document.forms[0].taxId.value="";
	    document.forms[0].taxId.focus();
		return false;
	}
//@@ 25-04-2005
  	if(taxId==0)
   	{ 
	  alert("Please enter Tax Id");
      document.forms[0].taxId.focus();
	  return false;
	}
 	else if(taxId < 3)
 	{
		alert("Please enter three characters for Tax Id");
		document.forms[0].taxId.focus();
	  	return false;
 	}
  	if(desc==0)
    {
	   alert("Please enter Tax Description");
	   document.forms[0].desc.focus();
       return false;
	 }
	 if(remarks>1999)
    {
	   alert("Please enter Tax Remarks less Than 2000 char");
	   document.forms[0].remarks.focus();
       return false;
	 }
<%
   if(loginbean.getAccessType()!= null && loginbean.getAccessType().equals("HO_TERMINAL"))
   {	
%>    	
  		len	=	document.forms[0].countryCodes.options.length;
		
		if(len ==0)
		{
		  alert("please select Contries for which tax is added.")	 ;
		  return false;
		}
		
		for(i=0;i<len;i++)
		document.forms[0].countryCodes.options[i].selected=true
		
<%
   }
%>
     if(!checkOtherMandatoryFields() )
	 return false;
  	document.forms[0].submit.disabled='true';		 
    return true;  
}

function showValues()
{	
	if(ope=='Modify')
	{
		document.forms[0].submit.value  = 'Submit';
	}
	else if(ope=='Delete')
	{
		document.forms[0].submit.value  = 'Delete';
	}
	else if(ope=='View')
	{
		document.forms[0].submit.value  = 'Continue';
	}
}
//@@ Srivegi Added on 20050524 for issue:1782
function resetValues()
{
  document.forms[0].taxType.value='FREIGHT AMOUNT' ;
  disp();
  document.Tax_Addition.taxId.focus();
}
//@@ 20050524 for issue:1782
function disp()
{
	var data = "";
	var data1 = "";
	index = document.forms[0].taxType.selectedIndex;
	//chargeId
	if(index==3 )
	{
		data1+= " <TABLE border=0 cellPadding=4 cellSpacing=1 width=800>"
		data1 += "	<TR class='formdata' valign='top'> "
		data1+= "		<TD width='80'>Charge Id :</td>"
		data1+= "		<td width='300'> "
		data1+="			<input type='text' class='text' name='ChargeId' value='<%=chargeId != null?chargeId:""%>' size='14'  onBlur='upperCase(ChargeId)'  maxlength='16' <%=readOrNot%> >  "
		data1+= "			<INPUT name=Button5  class='input'  type='button' value=... onClick='showChargeLOV()' <%=disabledBut%> > "
		data1+= "		</TD> "
		data1 += "		<TD width='80'>Tax % :<FONT color=red>*</FONT></td>"
		data1 += "		<td width='300'>  "
		data1+= "			<input type='text' class='text' name='taxPer' value='<%=taxPer%>' size='14'   maxlength=5   onkeyPress='return getDotNumberCode(this)' onBlur='removeDecimal(this)'  <%=readOrNot%>	>"
		data1+= "		</TD>"
		data1+= "	</TR>"
		data1 += "</TABLE>"
		
		types.innerHTML = data1;
	}else  
	{
		if(index==0 || index==1 || index==2)
		{
		data+= " <TABLE border=0 cellPadding=4 cellSpacing=1 width=800>"
		data += "<TR class='formdata' valign='top'> "
		data+= "<TD width='80'> Tax % :<font color=red>*</font></td>"
		data+= "<td width='680'> "
		data+="  <input type='text' class='text' name='taxPer'   value='<%=taxPer%>' size='14' maxlength=5    onkeyPress='return getDotNumberCode(this)' onBlur='removeDecimal(this)'  <%=readOrNot%>  >"
		data+= "<input type='hidden' name='te'>"
		data+= "		</TD>"
		data+= "	</TR>"
		data += "</TABLE>"
		
		}else if(index == 4)
		{
		data+= " <TABLE border=0 cellPadding=4 cellSpacing=1 width=800>"
		data += "<TR class='formdata' valign='top'> "
		data+= "<TD width='220'>Attach to Id for Surcharge :"
		data+= "<td width='540'> "
		data+=" <input type='text' class='text'   name='surChargeId'  maxlength='16' onBlur='upperCase(surChargeId)'   size='14' value='<%=surCharge!=null?surCharge:""%>'	  <%=readOrNot%>> "
		data+= "<input type='button'  name='consBtn' value='...' onClick = 'showSurchargeTaxIdsLOV()' class='input' <%=disabledBut%>>"
		data+= "<input type='hidden' name='routeId' value=''>"
		data+= "		</TD>"
		data+= "<TD width='80'>Tax % :<FONT color=red>*</FONT></td>"
		data+= "<td width='220'>  "
		// Modified By G.Srinivas to resolve the QA-Issue No:SPETI-5689 on 20050430.-->
		data+= "<input type='text' class='text' name='taxPer' value='<%=taxPer%>' <%=readOrNot%> size='14' maxlength=5  onkeyPress='return getDotNumberCode(this)' onBlur='removeDecimal(this)'  null	>"
		data+=  "<input type='hidden' name='routeId' value=''>"
		data+= "</TD>"
		data+= "	</TR>"
		data += "</TABLE>"
		
		}
		if( document.layers)
		{
			document.layers.types.document.write(data);// here 'cust' is the nameof span(see below for name span)
			document.layers.types.document.close();
		}
		else
		{
			if(document.all)
			{
				types.innerHTML = data;
			}
		}
		
	}
	
}

 function assignTaxType()
 {
<%

 	if(operation.equals("Modify")|| operation.equals("View") || operation.equals("Delete"))
    {
%>				
	
		var taxT ='<%=taxType%>' ;
		
		if(taxT =='FRT_AMT')
		document.forms[0].taxType.value='FREIGHT AMOUNT' ;	
		if(taxT =='PER_CHARGE')
		document.forms[0].taxType.value='PER CHARGE' ;	
		if(taxT =='SURCHARGE')
		document.forms[0].taxType.value='SURCHARGE' ;	
		if(taxT =='MNUL')
		document.forms[0].taxType.value='MANUAL' ;	
		if(taxT =='INV_VAL')
		document.forms[0].taxType.value='INVOICE VALUE' ;	
		if(taxT =='EXCPT_FRT')
		document.forms[0].taxType.value='EXCEPT FREIGHT' ;	
		disp();
		assingCountryList();
<%
    }
	if(operation.equals("View") || operation.equals("Delete"))
	{	
%>	
       document.forms[0].taxType.disabled=true;
<%
	}
%>

 }
 
 
 function showCurrencies()
{
		
		myUrl= 'ETCCountriesSelection.jsp';
		var myBars = 'directories=no,location=no,menubar=no,status=no,titlebar=no,toolbar=no';
		var myOptions ='scrollbars=yes,width=450,height=360,resizable=no';
		var myFeatures = myBars+','+myOptions;
		newWin = open(myUrl,'myDoc',myFeatures);
		if (newWin.opener == null) 
		newWin.opener = self;
		return true;
}

function assignLocations()
{
	var len1= window.document.forms[0].countryCodes.options.length;
	var index=0;
	for(var i=0;i<len1;i++)
	{
		window.document.forms[0].countryCodes.options.remove(index);
	}
	str = hf;	
	
	entries = str.split("-");
	
	for(i=0;i<entries.length;i++)
	{
		if(entries[i] != "-" && entries[i]!="")
		{
			window.document.forms[0].countryCodes.options[index] = new Option(entries[i] ,entries[i] )	
			index++;
		}
	}
	
    <% //@@ Srivegi Added on 20050425 (SPETI-5694)
	    
        if(cList !=null)
		for(int i=0;i<cList.size();i++)
		{
		  String country_id = (String)cList.get(i);	
		 %>	         
			    val = new Option('<%=country_id%>')
			 	document.forms[0].countryCodes.options.add(val,<%=i%>)
				document.forms[0].countryCodes.options[<%=i%>].value	=	'<%=country_id%>';	 
	<%
		}//20050425 (SPETI-5694)
%>
	len = document.forms[0].countryCodes.options.length;
	currencyList = new Array();
	for(i=0;i<len;i++)
	{
	  
		currencyList[i] = document.forms[0].countryCodes.options[i].length;
	}
	if(document.forms[0].countryCodes.options.length > 0)
	{
		
		var terId = document.forms[0].countryCodes.options[0].value
		document.forms[0].countryIdHide.value = currencyList.toString();
		
	}
	else
	{
		document.forms[0].countryIdHide.value = '';
	}
}

  function assingCountryList()
  {
	var j=0;
<%
      System.out.println("this is inside assingCountryList...");
	  if(loginbean.getAccessType()!= null && loginbean.getAccessType().equals("HO_TERMINAL"))
   	  {	
			//System.out.println("this is inside assingCountryList...1");
			String cValue ="";
			if(cList != null)
			{
				//System.out.println("this is inside assingCountryList...2"+cList.size());
				for(int m=0;m<cList.size();m++)
				{	
					//System.out.println("this is inside assingCountryList...3");
					cValue=(String)cList.get(m);	
					//System.out.println("the cValue-->"+cValue);	
%>	

					//document.forms[0].countryCodes[<%=m%>].value = '<%=cValue%>'
					//document.forms[0].countryCodes[m].value='<%=cValue%>'	
					j=j+1 ;
<%
				}
			}
   	  }	
%>
  }		
 	


 function showChargeLOV()
{
  var searchId   =document.forms[0].ChargeId.value;
  var shipmentMode=7;  
  var Url='ETCChargesLov.jsp?selectionType=single&query=1&searchId='+searchId;
  var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no'; 
  var Options='scrollbar=yes,width=550,height=450,resizable=no';
  var Features=Bars+' '+Options;
  var Win=open(Url,'Doc',Features);
   
}

function showSurchargeTaxIdsLOV()
{
	var tax_Id	= document.forms[0].taxId.value;	
	var oper	= '<%=operation%>' ;	
	var Url 	= 'ETCLOVTaxIds.jsp?ForsurChargeId=yes&oper='+oper+'&tax_Id='+tax_Id+'&surChargeId='+document.forms[0].surChargeId.value.toUpperCase();	
	var Bars 	= 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options	='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
    var Win		=open(Url,'Doc',Features);
}

function checkNumberCode()
{
	if(event.keyCode!=13)
	{
		if(event.keyCode <48 || event.keyCode >57)
		return false;
	}
	return true;
}

 function checkOtherMandatoryFields()
  {
     var  taxType =document.forms[0].taxType.value ;
	 
	 if((taxType =='FREIGHT AMOUNT' || taxType=='INVOICE VALUE' || taxType=='EXCEPT FREIGHT'))
	 {
	   if(document.forms[0].taxPer.value==0)
	   {alert("please enter tax %")
	    return false;
	   }
	   else
		 {
		   var rValue=validUnitPrice(document.forms[0].taxPer,2);
		   if(rValue==false)return false;
		 }
	   return true;
	 }
	 if(taxType =='PER CHARGE')
	 {
	    var name1='';
	   if(document.forms[0].taxPer.value==0)
	    name1=' Tax Id' ;
	   if(document.forms[0].ChargeId.value==0)
	     name1=' Charge Id' ;

	   if(document.forms[0].ChargeId.value==0 &&  document.forms[0].taxPer.value==0)
	     name1=" Tax % and ChargeId ";
	   if(document.forms[0].taxPer.value==0 || document.forms[0].ChargeId.value==0) 
	   {
		alert("Please Enter"+name1)
		return false
	   }
	   else 
	   return true
	 }
	 if(taxType =='SURCHARGE')
	 {
		var name1='';
		if(document.forms[0].taxPer.value==0)
	    name1=' Tax Id' ;
	    if(document.forms[0].surChargeId.value==0)
	    name1=' SurchageId' ;
	    if(document.forms[0].surChargeId.value==0 &&  document.forms[0].taxPer.value==0)
	    name1=" Tax % and ChargeId ";
	    if(document.forms[0].taxPer.value==0 || document.forms[0].surChargeId.value==0) 
	    {
		  alert("Please Enter"+name1)
	  	  return false
	    }
	    else 
	    return true ;

	 }
	 else if(taxType =='MANUAL')
	 return true;

  }



 function getDotNumberCode(input)    // Numbers + Dot
{
	if(event.keyCode!=13)
	{
		if(event.keyCode == 46 )
		{
			if(input.value.indexOf(".") == -1)
				return true;
			else
				return false;
		}

		if((event.keyCode < 46 || event.keyCode==47 || event.keyCode > 57) )
			return false;
	}
	return true;
}


function removeDecimalWt(input)
{
  var newValue='';

  var first = input.value.substring(0,input.value.indexOf("."));
  var last = input.value.substring(input.value.indexOf(".")+1);
  var lastTwo = input.value.substring(input.value.indexOf(".")+1);
  var find  = input.value.indexOf(".");

   if(lastTwo.length >1)
      lastTwo = lastTwo.substring(0,1);

   if(find!=-1 )
   {
        newValue = first+"."+lastTwo;
		input.value=newValue;
	 }
 }

 function removeDecimal(input)
{
  var newValue='';

  var first = input.value.substring(0,input.value.indexOf("."));
  var last = input.value.substring(input.value.indexOf(".")+1);
  var lastTwo = input.value.substring(input.value.indexOf(".")+1);
  var find  = input.value.indexOf(".");

   if(lastTwo.length >2)
      lastTwo = lastTwo.substring(0,2);

   if(find!=-1 )
   {
        newValue = first+"."+lastTwo;
		input.value=newValue;
	 }
 }

 function roundWeight(obj,X)
{
	var number = obj.value;
	X = (!X ? 2 : X);
	obj.value=Math.round(number*Math.pow(10,X))/Math.pow(10,X);
}
function validUnitPrice(input1,maxLength)
{
	var amtValue=input1.value;
	
	var indexOfDot=amtValue.indexOf('.');

	if((indexOfDot==-1)&&(amtValue.length > maxLength))
	{
		alert("You can enter maxium of "+maxLength+" digits without '.' ");
		input1.focus();
		return false;
	}
	if((indexOfDot!=-1)&&(indexOfDot >2))
	{
		alert("You can enter maxium of 2 digits before '.' ");
		input1.focus();
		return false;
	}
}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body onLoad="placeFocus();disp();assignTaxType()">
	<% //@@ Srivegi Modified on 20050524 ,onReset function added for issue : 1782 %>
<form method="POST" name = "Tax_Addition" onSubmit="return Mandatory()"  onReset= "resetValues()" action =<%=form_name%> >
	<%//@@ 20050524%>
<table width="800" cellpadding="0" cellspacing="0" border="0">								
<tr bgcolor="ffffff"> 
<td>
<table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td >
<table width="790" border="0" >
  <tr class='formlabel'> 
  <td >Tax - <%=operation%><td align=right><%=loginbean.generateUniqueId("ETCTaxMasterAdd.jsp",operation,loginbean.getAccessType())%></td></td>
  </tr>
</table>
</td></tr></table>

<table width="800" cellspacing="1" cellpadding="4">
<tr class='formdata'><td colspan="3">&nbsp;</td></tr>
 <tr class='formdata'> 
   <td width="30%">Tax Id:<font color="#FF0000">*</font><br>
   <input type='text' class='text' name="taxId" size="5" value="<%=taxId%>" maxlength="3" onBlur="upperCase(taxId)" onkeyPress="return getKeyCode(taxId)" <%=readOrNot1%> <%=readOrNot%>>
   <input type="hidden" name="selTerminalId" value="<%=termId%>">
   </td>
   <td width="30%">Description:<font color="#FF0000">*</font><br>
   <input type='text' class='text' name="desc" maxlength="50" value="<%=desc%>" onBlur="upperCase(desc)" onkeyPress="return getSpecialCode(desc)" <%=readOrNot%> >
    <input type="hidden" name="termId" >
   </td>
   
  <td width="40%">Tax Type:<font color="#FF0000">*</font><br>
    <select size="1" name="taxType"  class='select' onChange="disp()">
    <option value="FREIGHT AMOUNT">Freight Amount</option>
    <option value="INVOICE VALUE">Invoice Value</option>
    <option value="EXCEPT FREIGHT">Except Freight</option>
    <option value="PER CHARGE">Per Charge</option>
    <option value="SURCHARGE">Surcharge Value</option>
    <option value="MANUAL">Manual</option>
    </select> 
  </td>
  </tr>
  </table>

<span id=types style='position:relative;'></span>


  <table width="800" cellspacing="1" cellpadding="4">
<%
   if(loginbean.getAccessType()!= null && loginbean.getAccessType().equals("HO_TERMINAL"))
   {	
%>  
	 
	  
	  <tr class='formdata'>
	  <td colspan="3">Country Code:<font color="#FF0000">*</font><br>
	  <select size="8" name="countryCodes" multiple class='select' >
	  </select>
<script>	  
<%
        
		if(cList !=null)
		for(int i=0;i<cList.size();i++)
		{
			//System.out.println("this is inside the clist");
			
		  String country_id = (String)cList.get(i);	
		  //System.out.println("the country_id==>"+country_id);
%>	  
			   val = new Option('<%=country_id%>')
				document.forms[0].countryCodes.options.add(val,<%=i%>)
				document.forms[0].countryCodes.options[<%=i%>].value	=	'<%=country_id%>';	 
<%
		}
%>	 
</script>	  
	  <INPUT name=""  class='input'  type='button' value=... onClick="return showCurrencies();" <%=disabledBut%>> 
	   <input type="hidden" name="countryIdHide" >
	  </td>
	  </tr>
<%
   }	
%>   
 <tr valign="top" class='formdata'>
  <td colspan="3">Remarks:<br> 
  <textarea  rows="5" class='select' name="remarks" cols="60" maxlength ="2000"  onBlur="upperCase(remarks)" onkeyPress="return getSpecialCode(remarks)" <%=readOrNot%> ><%=remarks%></textarea> 
  </td>
 </tr>
 
 <tr valign="top" class='denotes'>
  <td ><font color="#FF0000">*</font>Denotes Mandatory</td>
  <td colspan="2" align="right">
  <input type="hidden" name="ids">
<%
   if(operation.equals("Add")||operation.equals("Modify"))
   {
 %>
     
   <input type="hidden" name=Operation value="<%= operation %>">
   <input type="submit" name="submit" value="Submit" class='input'>
   <input type="reset" name="reset" value="Reset"  class='input'>
   
<%
   }
   else
   {
%>
     <input type="hidden" name=Operation value="<%= operation %>">
     <input type="submit" name="submit" value="Submit" class='input'>
<%
   }
%>   	 
  
  
  </td>
 </tr>
</table>
</td>
</tr>
</table>
</form>
</body>
</html>
<%
   	}
 }	 
 catch(Exception e)
 {
    //Logger.error(FILE_NAME,"Exception in ETCTaxMasterAdd.jsp:", e.toString());
    logger.error(FILE_NAME+"Exception in ETCTaxMasterAdd.jsp:"+ e.toString());
 }	
%>