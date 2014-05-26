<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<%--

	Program Name	:AttentionToLOV.jsp
	Module Name		:DHLQMS
	Task			:AttentionTO
	Sub Task		:AttentiontoLOV	
	Author Name		:P.UmaPhaniSekhar
	Date Started	:26 May 2009
	Date Completed	:26 May 2009
	Date Modified	:
		

--%>

<%@ page import="javax.naming.InitialContext,
					javax.naming.Context,
					java.util.ArrayList,
				 	org.apache.log4j.Logger,					
					com.qms.setup.ejb.sls.SetUpSession,
					com.foursoft.esupply.common.util.StringUtility,
                    com.qms.setup.ejb.sls.SetUpSessionHome" %>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"AttentionToLOV.jsp";
%>
<jsp:useBean id="loginbean" class ="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope ="session"/>
<%
	logger  = Logger.getLogger(FILE_NAME);
	ArrayList ids			=	null;
	int len					=	0;	 
	String whereClause		= 	null;	
	String customerId		=	null;    
    String address          =   null;
	String flag             =   null;
	String quoteId			=	null;
	ArrayList dispList = null;
	String dataArray[]=null;
	String dispAttentionCustomerId[]= null;
	String dispAttentionSlno[] = null;
	String dispAttentionEmailId[]= null;
	String dispAttentionFaxNo[]=null;
	String dispAttentionContactNo[] = null;


	try
	{
		
		flag  =  request.getParameter("flag");
		
		
		if(request.getParameter("custId")!=null)
			customerId	=	request.getParameter("custId");
		else
			customerId	=	"";
       
		if(request.getParameter("quoteId")!=null && request.getParameter("quoteId").trim().length()>0)
			     quoteId		=	request.getParameter("quoteId");
	
		if(request.getParameter("attentionCustomerId")!=null && request.getParameter("attentionCustomerId").trim().length()>0 ) 
		{
				dispAttentionCustomerId=request.getParameter("attentionCustomerId").split(",");
		}
		if(request.getParameter("attentionSlno")!=null && request.getParameter("attentionSlno").trim().length()>0 ) 
		{
			dispAttentionSlno=request.getParameter("attentionSlno").split(",");
		}
		if(request.getParameter("attentionEmailId")!=null && request.getParameter("attentionEmailId").trim().length()>0 ) 
		{
			dispAttentionEmailId=request.getParameter("attentionEmailId").split(",");
		}
		
		if(request.getParameter("attentionFaxNo")!=null && request.getParameter("attentionFaxNo").trim().length()>0 ) 
		{
			dispAttentionFaxNo=request.getParameter("attentionFaxNo").split(",");
		}
		
		if(request.getParameter("attentionContactNo")!=null && request.getParameter("attentionContactNo").trim().length()>0 ) 
		{
			dispAttentionContactNo=request.getParameter("attentionContactNo").split(",");
			
		}

		if(dispAttentionContactNo==null)
		{
			try{
				
			int sizeOfSLno = dispAttentionSlno!= null?dispAttentionSlno.length:1;
			dispAttentionContactNo=new String[sizeOfSLno];
			}catch(Exception e){e.printStackTrace();}
		}
		
		if(dispAttentionFaxNo==null)
		{
			try{
			int sizeOfSLno = dispAttentionSlno != null ?dispAttentionSlno.length:1;
			dispAttentionFaxNo=new String[sizeOfSLno];
			}catch(Exception e){e.printStackTrace();}
		}
		
		if(dispAttentionEmailId==null)
		{
			try{
			int sizeOfSLno = dispAttentionSlno != null ?dispAttentionSlno.length:1;
			dispAttentionEmailId=new String[sizeOfSLno];
			}catch(Exception e){e.printStackTrace();}
		}
		
					
		if(ids==null)
		{
			Context initial						= 	new InitialContext();
			SetUpSessionHome home		=	(SetUpSessionHome) loginbean.getEjbHome("SetUpSessionBean");	
			SetUpSession remote			=	(SetUpSession)home.create();
			
			if(flag!=null)
            {
				
				  if("Costing".equalsIgnoreCase(flag))
				  {
					  
						dispList = remote.getContactNamesforAttentionLOV(customerId,address,quoteId,flag);
						
				  }
				  else
				  {

					  if(request.getParameter("address")!=null)
						address =request.getParameter("address");
					  else
						address ="";

					 
					  dispList = remote.getContactNamesforAttentionLOV(customerId,address,"",flag);	
				  }
				  
			}
			
			

			if(dispList != null);
				len	=	dispList.size();
			
		}
	}
	catch(Exception ee)
	{
	
  logger.error(FILE_NAME+"Error in AttentionToLOV.jsp : "+ ee.toString());
	}
%>
<html>
<head>
<title>Select</title>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<script>
 function checknSetData()
 {
	
	 var tempslNoArray = new Array();
	 var tempEmailArray = new Array();
	 var tempFaxArray = new Array();
	 var tempContactArray = new Array();
	 var tempCustomerArray = new Array();

	 <% if(dispAttentionSlno!=null) 
	 { 
		 for(int i=0;i<dispAttentionSlno.length;i++)
		 {
		 %>

			
			var val = '<%=dispAttentionSlno[i]%>';	
			 tempslNoArray['<%=i%>']=val;
	<%} }%>

 <% if(dispAttentionCustomerId!=null) 
	 { 
		 for(int i=0;i<dispAttentionCustomerId.length;i++)
		 {
		 %>

			
			var val = '<%=dispAttentionCustomerId[i]%>';	
			 tempCustomerArray['<%=i%>']=val;
	<%} }%>

 <% if(dispAttentionFaxNo!=null) 
	 { 
		 for(int i=0;i<dispAttentionFaxNo.length;i++)
		 {
		 %>

			
			var val = '<%=StringUtility.noNull(dispAttentionFaxNo[i])%>';	//modifed by phani sekhar for wpbn 174840 on 6/29/2009
			 tempFaxArray['<%=i%>']=val;
	<%} }%>


 <% if(dispAttentionEmailId!=null) 
	 { 
		 for(int i=0;i<dispAttentionEmailId.length;i++)
		 {
		 %>

			
			var val = '<%=StringUtility.noNull(dispAttentionEmailId[i])%>';	//modifed by phani sekhar for wpbn 174840 on 6/29/2009
			 tempEmailArray['<%=i%>']=val;
	<%} }%>


 <% if(dispAttentionContactNo!=null) 
	 { 
		 for(int i=0;i<dispAttentionContactNo.length;i++)
		 {
		 %>

			
			var val = '<%=StringUtility.noNull(dispAttentionContactNo[i])%>';	
			 tempContactArray['<%=i%>']=val;
	<%} }%>
	var t1;
	var cst;
		for( var k=0;k<tempslNoArray.length;k++)
	 {
			t1=tempslNoArray[k];
				
			cst=tempCustomerArray[k];
	  		
		if(cst==document.getElementById('customer'+t1).value)
		 {
			if(k<tempFaxArray.length)
		 {
				if(document.getElementById('fax'+t1)!=null)
					document.getElementById('fax'+t1).value=tempFaxArray[k];
		 }

			if(k<tempEmailArray.length)
		 {
				if(document.getElementById('mailId'+t1)!=null)
					document.getElementById('mailId'+t1).value=tempEmailArray[k];
		 }
			if(k<tempContactArray.length)
		 {
				if(document.getElementById('contact'+t1)!=null )					
					document.getElementById('contact'+t1).value=tempContactArray[k];

			 
		 }
		
		 if(document.getElementById('sId'+t1)!=null)
				document.getElementById('sId'+t1).checked=true;
		 }
	 }
			
			
 }
function setcheckedIds()
{
	 var contactnames="";
	 var emaildetails="";
	 var faxNos  = "";
	 var slNo="";
	 var customer = "";
   var contactNo="";
	 var tempContactName;
	 var tempEmailId,tempFaxNo,tempSlno,tempCustomer,tempContactNo;
	 var x=document.getElementsByName("sId");
	 var count		=	0;

			for(var j=0;j<x.length;j++)
			{
				if(document.getElementById('sId'+j).checked)
				{
					count++;
				}
			}
			if(count ==0)
	{
				alert("Please select At Least one Contact Person");
	}
	else
	{
		var levelTemp = new Array(count);
		var sIndex=0;

		for( var i=0;i<x.length;i++)
		{		
		if(document.getElementById('sId'+i).checked)
		{
			tempContactName =document.getElementById('contactPerson'+i).value;
			tempEmailId=document.getElementById('mailId'+i).value;
			tempFaxNo=document.getElementById('fax'+i).value;
			tempSlno = document.getElementById('slno'+i).value;
			tempCustomer= document.getElementById('customer'+i).value;
      tempContactNo= document.getElementById('contact'+i).value;
			if(contactnames.length!=0)
				contactnames=contactnames+","+tempContactName;
			else
        	contactnames	=	tempContactName;

			if(emaildetails.length!=0)
				emaildetails=emaildetails+","+tempEmailId;
			else
        	emaildetails	=	tempEmailId;

			if(faxNos.length!=0)
				faxNos=faxNos+","+tempFaxNo;
			else
        	faxNos	=	tempFaxNo;

			
			if(slNo.length!=0)
				slNo=slNo+","+tempSlno;
			else
        	slNo	=	tempSlno;

			
			if(customer.length!=0)
				customer=customer+","+tempCustomer;
			else
        	customer	=	tempCustomer;
    if(contactNo.length!=0)
				contactNo=contactNo+","+tempContactNo;
			else
        	contactNo	=	tempContactNo;
    
			levelTemp[sIndex]	=	tempContactName;
					sIndex++;

		}
				
	}

		window.opener.document.forms[0].contactIds.value=slNo;	
		window.opener.setValues(levelTemp);
		window.opener.document.forms[0].attentionCustomerId.value=customer;	
		window.opener.document.forms[0].attentionSlno.value=slNo;	
		window.opener.document.forms[0].attentionEmailId.value=emaildetails;	
		window.opener.document.forms[0].attentionFaxNo.value=faxNos;	
		window.opener.document.forms[0].attentionContactNo.value=contactNo;	
		window.close();
   }
}


function onEnterKey()
{
	
	if(event.keyCode == 13)
	{
		setcheckedIds();
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

function checkemail(id){	
		var testresults;
		var str=document.getElementById(id).value;
		var filter=/^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i
		if (filter.test(str))
		{
		testresults=true;
		}
		else{
		alert("Please input a valid email address!");
		testresults=false;
		document.getElementById(id).focus();
		}
		return (testresults)
}
//@@ added by silpa on 22-03-11
function selectAll(){
	//alert("check");


var check = document.getElementsByName("sId");
if(document.forms[0].select.checked)
	{


for(var i=0;i<check.length;i++)
		{
			check[i].checked=true;
			//setValue(i,"origin");
		}



	}

else
	{
		for(var i=0;i<check .length;i++)
		{
			check [i].checked=false;
			//setValue(i,"origin");
		}
	}


}
//@@ ended
//@added by silpa for invalidate function

/*function deselect(obj,Sid,contactperson,contact,fax,mail,slno,customer)
{


	if(obj.checked)
	{
		getAttentionByAjax1(document.getElementById(slno).value,document.getElementById(customer).value);	
//alert("contactperson---------"+document.getElementById(contactperson).value+"---fax---------"+document.getElementById(fax).value);

document.getElementById(contactperson).disabled=true;
document.getElementById(fax).disabled=true;
document.getElementById(mail).disabled=true;
document.getElementById(contact).disabled=true;
document.getElementById(Sid).checked=true;//modified by silpa.p on 1-05-11
document.getElementById(Sid).disabled=true;
obj.disabled = true;
		
			
		


	}

}
function getAttentionByAjax1(slno,customer) {
   //alert("ATTENTION");
       //var len = document.forms[0].contactPersons.options.length;			
		// for (i=len-1; i>=0; i--) {
				//document.forms[0].contactPersons.remove(i);  }
	   // var custid = obj.value;
	   // var custid = obj.value;
		//if(custid != '' )
		//{
		   //alert("ATTENTION1");
	   var url='<%=request.getContextPath()%>/QMSQuoteAjaxController?type=AJAX&SearchOption=Update&slno='+slno+'&customer='+customer;
		 if (window.XMLHttpRequest) 
		{
            req1 = new XMLHttpRequest();
        } 
		else if (window.ActiveXObject) 
		{
            req1 = new ActiveXObject("Microsoft.XMLHTTP");
        } 
        req1.open("GET",url,true);
       	 //alert("ATTENTION2");
		//req1.onreadystatechange = callback1;
		  
		req1.send(null);
		//}else{
       //   var len = document.forms[0].contactPersons.options.length;			
		// for (i=len-1; i>=0; i--) {
		//		document.forms[0].contactPersons.remove(i);  }
		}*/
	



//@ ended



</script>
</head>
 <body class='formdata' onLoad="checknSetData()" onKeyPress='onEscKey()'>
  <form> 
  
   <table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
   <tr class="formlabel">
   <td align="left"> Contact Person Details </td>
   </tr>
   </table>
   <table width="100%">
   <tr>
   
<%
	if(len > 0)
	{
%>
		
		<table width="100%">
		<tr class="formheader">
		<td>Select<BR>
		 <input type="checkbox" name='select' onclick='selectAll()'></td>
		<td>Contact Person</td>
    <td>Contact</td>
    <td>Fax</td>
		<td>E-Mail</td>
		<!--<td>Invalidate</td>-->
		
		</tr>
		
		
<% 
	for(int i=0;i<dispList.size();i++)
		{
	dataArray=dispList.get(i).toString().split(",");
%>
<tr class="formdata">
<td>	<input type="checkBox" value="" name ="sId" id='sId<%=i%>'></td>
<!--<td>	<input type="checkBox" value="" name ="sId" id='sId<%=dataArray[5]%>'></td>-->
	<td><input type="text" value="<%=dataArray[0]%>" name="contactPerson" id='contactPerson<%=i%>' readonly  class="text"></td>
	<td><input type="text" value='<%=dataArray[2]%>' name = "contact" id="contact<%=i%>" class="text"></td>
  <td><input type="text" value='<%=dataArray[3]%>' name = "fax" id="fax<%=i%>" class="text"></td>
  <td><input type="text" value="<%=dataArray[1]%>" name ="mailId" id="mailId<%=i%>" onblur="checkemail(id)" class="text"></td>  
  <!--<td>	<input type="checkBox" value="" name ="Did" id='Did<%=i%>' onClick="deselect(this,'sId<%=i%>','contactPerson<%=i%>','contact<%=i%>','fax<%=i%>','mailId<%=i%>','slno<%=i%>','customer<%=i%>')"></td>-->
	<td><input type="hidden" value='<%=dataArray[5]%>' name = "slno" id="slno<%=i%>" class="text"></td>
	<td><input type="hidden" value='<%=dataArray[4]%>' name = "customer" id="customer<%=i%>" class="text"></td>
	<!--<td><input type="hidden" value='<%=dataArray[4]%>' name = "customer" id="customer<%=dataArray[5]%>" class="text"></td>-->
	</tr>
<%}%>
</table>

		
<table width="101%"" cellpadding="4" cellspacing="1" bgcolor='#FFFFFF'>
<tr class="text">
<td  align="center">
		<input type="button" value=" Ok " name="OK" onClick="setcheckedIds()" class="input">		
		<input type="button" value="Cancel" name="B2" onClick="window.close()" class="input">
 </td>
 </tr>
</table>		
<%
	}
	else
	{
%>
		<br>
		<center>
		<textarea cols=30 class='select' rows = 6 readOnly >No ContactNames  available
		</textarea>
		<br><br>
		<input type="button" value="Close" name="B2" onClick="window.close()" class="input">
		</center>
<%
	}
%>
</form>
</body>
</html>
