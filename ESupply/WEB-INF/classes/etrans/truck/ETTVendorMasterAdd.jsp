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
/**
	Program Name		: ETTVendorMasterAdd.jsp
	Module Name			: ETrans
	Task				: VendorMaster
	Sub Task			: Add
	Author Name			: Shailendra Chak
	Date Started		: September 22,2001
	Date Completed		: october 4,2001
	Date Modified		:
	Description			: The purpose is to Add the Vendor Informations to the database or to perform Modify or View or Delete operation.
	Methods' Summary	: getVendorDetails(vendorId) // calling this method to get Vendor Details.
						  getAllCountryIds() // Getting all Country Ids to validate the value of countryId entered by user.		
*/%>
<%@ page import="
				com.foursoft.etrans.truck.setup.bean.ETransTruckingVendor,
				com.qms.setup.ejb.sls.SetUpSession,
                com.qms.setup.ejb.sls.SetUpSessionHome,
				com.foursoft.esupply.common.java.ErrorMessage,java.util.ArrayList,
				com.foursoft.esupply.common.java.KeyValue"%>
<jsp:useBean id="loginbean" scope="session" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters"/>								
<%     
	String vendorId	    		= "";    
	String vendorName		  	= "";
	String vendorAdd			= "";
	String addLine1				= "";
	String addLine2				= "";
	String city					= "";
	String state				= "";
	String contactNo			= "";
	String fax					= "";
	String zipCode				= "";
	String email				= "";
	String countryId			= "";
	String helpLine				= "";    
	String errorMessage 		= null;		// for storing error message	 
	String readOrNot    		= "";     // temporary variable
	String readOrNot1 			= "";    	// temporary variable
	//InitialContext	ictx 		= null;	  	// variable for storing JNDI Initial Context 	
	String form_name			= null;
	String errorCode			= null;    	// for storing the error code
	String operation			= null;
	ETransTruckingVendor vendor	= null;     //for storing vendor object
	java.util.ArrayList aL      = null;
	operation 					= request.getParameter("Operation");				
	SetUpSessionHome home = null;  //Vendor home reference
	SetUpSession remote   = null;  //Vendor remote reference
	//	ictx 		= 	new InitialContext();		// variable for storing JNDI Initial Context  
		home 		= 	(SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");		
		remote 		= 	(SetUpSession)home.create();   					                 	
%>
<%
	ErrorMessage	errorMessageObject   =  null;
	ArrayList		keyValueList		 =  new ArrayList();
	
	try
	{		
			if(operation == null)
				operation = (String)session.getAttribute("Operation");				
			if(operation.equals("Modify") ||operation.equals("Delete") ||operation.equals("Add"))
				form_name		= "ETTVendorMasterProcess.jsp";					  				
			else if (operation.equals("View"))
				form_name	= "ETTVendorMasterEnterId.jsp";									
			if(operation.equals("Add")|| operation.equals("Modify") )
				readOrNot1 	= "readonly";				   				
			if(operation.equals("View") || operation.equals("Delete"))
				readOrNot 	= "readonly";				   				
			if(operation.equals("Add")|| operation.equals("Modify") || operation.equals("View") || operation.equals("Delete"))
				aL = remote.getAllCountryIds();	
			if( operation.equals("Modify") || operation.equals("Delete") || operation.equals("View"))
			{
				
				vendorId 	= 	request.getParameter("vendorId");					 					                 	
				java.util.ArrayList aList1 	= 	new java.util.ArrayList();
				java.util.ArrayList aList2 	= 	new java.util.ArrayList();					
				aList1  = remote.getVendorDetails(vendorId);										
				try
 				{					    
					if( aList1!=null )
					{																				
						vendorName		= (String)aList1.get(0);							
						vendorAdd		= (String)aList1.get(1);															
					}	
					else
					{ 
						errorMessage 	=	"Record does not exist with Vendor Id : "+vendorId;
 						errorCode 		= 	"RNF";
  						
						errorMessageObject = new ErrorMessage(errorMessage,"ETTVendorMasterEnterId.jsp");
						keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
						keyValueList.add(new KeyValue("Operation",operation)); 	
						errorMessageObject.setKeyValueList(keyValueList);
						request.setAttribute("ErrorMessage",errorMessageObject); 	
								
%>	 
		   				<jsp:forward page="../../ESupplyErrorPage.jsp" />  
<%			
				 	} 	 
 			  	}			 
			  	catch(Exception ne)
			  	{	
					 errorMessage 	=	"Record does not exist with Vendor Id: "+vendorId;
 					 errorCode 		= 	"RNF";
					 
					 errorMessageObject = new ErrorMessage(errorMessage,"ETTVendorMasterEnterId.jsp");
					 keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
					 keyValueList.add(new KeyValue("Operation",operation)); 	
					 errorMessageObject.setKeyValueList(keyValueList);
					 request.setAttribute("ErrorMessage",errorMessageObject); 	
					 
					 
					
%>	 
		   			<jsp:forward page="../../ESupplyErrorPage.jsp" />  
<%				 
				}	
				aList2  = remote.getVendorAddressDetails(vendorAdd);	
				try {				    
			    		if( aList2!=null )
						{
							addLine1		= (String)aList2.get(0);   							
							addLine2		= (String)aList2.get(1);  							
							city			= (String)aList2.get(2);
							state			= (String)aList2.get(3);
							contactNo		= (String)aList2.get(4);
							fax				= (String)aList2.get(5);
							zipCode			= (String)aList2.get(6);							
							email			= (String)aList2.get(7);
							countryId			= (String)aList2.get(8);
							helpLine		= (String)aList2.get(9);
															
							if (addLine2==null)
							{
								addLine2="";
							}														
							if (state==null)
							{
								state="";
							}
							if (contactNo==null)
							{
								contactNo="";
							}
							if (fax==null)
							{
								fax="";
							}
							if (zipCode==null)
							{
								zipCode="";
							}
							if (email==null)
							{
								email="";
							}						
							if (helpLine==null)
							{
								helpLine="";
							}																		
						}	
							else
						{ 
				    		errorMessage 	=	"Record does not exist with Vendor Id : "+vendorId;
 				    		errorCode 		= 	"RNF";
  				    		
							 errorMessageObject = new ErrorMessage(errorMessage,"ETTVendorMasterEnterId.jsp");
							 keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
							 keyValueList.add(new KeyValue("Operation",operation)); 	
							 errorMessageObject.setKeyValueList(keyValueList);
							 request.setAttribute("ErrorMessage",errorMessageObject); 	
							 
%>	 
		   				<jsp:forward page="../../ESupplyErrorPage.jsp" />  
<%			
				 		} 	 
 			  		  }			 
			  		  catch(Exception ne)
			  		  	{	
                    	 errorMessage 	=	"Record does not exist with Vendor Id: "+vendorId;
 				    	 errorCode 		= 	"RNF";
						 
						 errorMessageObject = new ErrorMessage(errorMessage,"ETTVendorMasterEnterId.jsp");
						 keyValueList.add(new KeyValue("ErrorCode",errorCode)); 	
						 keyValueList.add(new KeyValue("Operation",operation)); 	
						 errorMessageObject.setKeyValueList(keyValueList);
						 request.setAttribute("ErrorMessage",errorMessageObject); 	
						 
%>	 
		   			<jsp:forward page="../../ESupplyErrorPage.jsp" />  
<%				 
			  			}				 
		  			  // main try
		  			 
		  		 }  
%>
<html>
<head>
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
<title>Vendor Master</title>
</head>
<script language="JavaScript">
function showLOV()
{
	var myUrl = '../ETCLOVCountryIds.jsp?searchString='+document.forms[0].countryId.value.toUpperCase();
	var myBars = 'directories=no, location=no, menubar=no, status=no, titlebar=no, toolbar=no'; 
	var myOptions = 'scrollbars=yes width=360 height=360 resizable=no';
	var myFeatures = myBars+','+myOptions;
	var newWin = window.open(myUrl,'myDoc',myFeatures);
	
	if (!newWin.opener) 
		newWin.opener = self;
	if (newWin.focus != null)
		newWin.focus();
}

function initializeform()
{
	document.frm.vendorName.focus();
	document.frm.reset();
}
function goToFirst()
{
	document.frm.vendorName.focus();
} 
function checkNumbers(input,label)
{
	alert(label);
	if(input.value.length>0)
	{
		if(isNaN(input.value))
		{
			alert("Please do not enter characters for "+label);
			input.focus();
			return false;
		}
	}
	return true
}
function Mandatory()
{
/*	if (document.forms[0].tyreMntcid.value!=0)
	{
		return checkNumbers(document.forms[0].tyreMntcid,'Tyre Maintanence Id');
	}	*/
 	for(i=0;i<document.forms[0].elements.length;i++)
 	{
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		  document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }		
 	vendorName = document.forms[0].vendorName.value.length;
 	
  	if(vendorName==0)
   	{ 
	  alert("Please enter VendorName");
      document.forms[0].vendorName.focus();
	  return false;
	} 	    
		 
	addressLine1 = document.forms[0].addLine1.value.length;
 	
  	if(addressLine1==0)
   	{ 
	  alert("Please enter address Line1");
      document.forms[0].addLine1.focus();
	  return false;
	}
	
	city1 = document.forms[0].city.value.length;
 	
  	if(city1==0)
   	{ 
	  alert("Please enter City Name");
      document.forms[0].city.focus();
	  return false;
	}
	
	document.forms[0].countryId.value=document.forms[0].countryId.value.toUpperCase();
		 var count = 0;
		 var aLSize = '<%= aL.size()%>';
         var formCountryId =  document.forms[0].countryId.value.toUpperCase();
	     <%
		   for(int i=0;i<aL.size();i++)
		   {
		     
		 %>
		   var countryId = '<%= aL.get(i)%>';
           if(countryId != formCountryId)
		     count++;
        <%
		 }
		%>           
		 if(count == aLSize)
		 {
		    alert("Enter Valid Country Id");
			document.forms[0].countryId.focus();
			document.forms[0].countryId.value="";
			return false;
		 }
	
 	    return true; 	 
		 
}
function getKeyCode()
   {
     
    if(event.keyCode == 96 || event.keyCode == 59 || event.keyCode == 39 || event.keyCode == 34 || event.keyCode == 126 )
	 {
	    return false;
     }
	     return   true  	 
   }
function checkStringKeyCode()
   {
	if(event.keyCode != 13)
	 {
	 if((event.keyCode < 65 || event.keyCode > 90) && (event.keyCode < 97 || event.keyCode > 122) && (event.keyCode < 48 || event.keyCode > 57) ) 
	   {
	     {
	   		if (event.keyCode == 32) 
				return true;			
	     }	
		return false;
      }
     }
	
	return true; 
   } 

function upperCase(input)
{
  input.value=input.value.toUpperCase();
}
</script>
<body onLoad="initializeform()">
<form method="POST" name = "frm" onSubmit="return Mandatory()"  action =<%=form_name%> >
  <table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
        <table border="0" width="800" cellspacing="1" cellpadding="4">
          <tr class="formlabel"> 
            <td>Vendor - <%=operation%> </td>
          </tr>
		  <tr class='formdata'><td>&nbsp;</td></tr>
        </table>
        <table border="0" width="800" cellspacing="1" cellpadding="4">
          <tr class="formdata" valign="top"> 
            <td colspan="4" width="746"><b>Vendor Details :</b></td>
          </tr>
          <tr class="formdata" valign="top"> 
            <td width="177">Vendor Name : <font color="ff0000">*</font><br>
              <input type="text" class="text" class="text" name="vendorName" value="<%=vendorName%>" size="20" maxlength="16" onBlur="upperCase(vendorName)" onKeyPress="return checkStringKeyCode()" <%=readOrNot%>>
            </td>
            <td width="180">Address Line1 : <font color="ff0000">*</font><br>
              <input type="text" class="text" name="addLine1" value="<%=addLine1%>" size="20" maxlength=75 onBlur="upperCase(addLine1)" onKeyPress="return getKeyCode()" <%=readOrNot%>>
            </td>
            <td width="176">Address Line2 : <br>
              <input type="text" class="text" name="addLine2" value="<%=addLine2%>" size="20" maxlength=75 onBlur="upperCase(addLine2)" onKeyPress="return getKeyCode()" <%=readOrNot%> >
            </td>
            <td width="177">Contact No : <br>
              <input type="text" class="text" name="contactNo" value="<%=contactNo%>" size="20" maxlength=15 onBlur="upperCase(contactNo)" onKeyPress="return getKeyCode()" <%=readOrNot%>>
            </td>
          </tr>
          <tr class="formdata" valign="top"> 
            <td width="177">Fax : <br>
              <input type="text" class="text" name="fax" value="<%=fax%>" size="20" maxlength=15 onBlur="upperCase(fax)" onKeyPress="return getKeyCode()" <%=readOrNot%>>
            </td>
            <td width="180">e Mail : <br>
              <input type="text" class="text" name="email" value="<%=email%>" size="20" maxlength=50 onBlur="upperCase(email)" onKeyPress="return getKeyCode()" <%=readOrNot%>>
            </td>
            <td width="176">City : <font color="ff0000">*</font><br>
              <input type="text" class="text" name="city" value="<%=city%>" size="20" maxlength=30 onBlur="upperCase(city)"  onKeyPress="return getKeyCode()" <%=readOrNot%>>
            </td>
            <td width="177">State : <br>
              <input type="text" class="text" name="state" value="<%=state%>" size="20" maxlength=30 onBlur="upperCase(state)" onKeyPress="return getKeyCode()" <%=readOrNot%> >
            </td>
          </tr>
          <tr class="formdata" valign="top"> 
            <td width="177">Zip Code : <br>
              <input type="text" class="text" name="zipCode" value="<%=zipCode%>" size="20"  onBlur="upperCase(zipCode)" maxlength=10 onKeyPress="return getKeyCode()" <%=readOrNot%>>
            </td>
            <td width="180"> Country : <font color="ff0000">*</font><br>
              <input type="text" class="text" name="countryId" value="<%=countryId%>" maxlength=2 size="16" onBlur="upperCase(countryId)" onKeyPress="return getKeyCode()" <%=readOrNot%> >
<%
			if (operation.equals("Add") || operation.equals("Modify"))
			{
%>
			  <input type="button" name="countryIdLOV" value="..." class="input" onClick="showLOV()"  <%=readOrNot%> > 
<%
			}	
%>
			
            </td>
            <td width="183">
            Help line No:<input type="text" class="text" name="helpLine" value="<%=helpLine%>" maxlength=15 size="20" onBlur="upperCase(helpLine)" onKeyPress="return getKeyCode()" <%=readOrNot%>>
            </td>
            <td width="182">
            </td>
          </tr>
			   
          <tr  valign="top" class='denotes'> 
		  <td><font color="ff0000">*</font>Denotes Mandatory</td>   
            <td colspan="3"  align="right" width="746">                  	
<%			
	if( operation.equals("Add") )
		{
%>
			 <input type="hidden" name="Operation" value="<%= operation %>">			 
			  <input type="reset" name="Reset" value="Reset" class="input">
			 <input type="submit" name="submit" value="Submit" class="input">
<%
		} 		
	else if( operation.equals("Modify") || operation.equals("Delete") || operation.equals("View"))
		{	
%>
			<input type="hidden" name="vendorId" value="<%= vendorId %>">
			<input type="hidden" name="vendorAdd" value="<%= vendorAdd %>">
		  	<input type="hidden" name="Operation" value="<%= operation %>">
			<input type="submit" name="submit" value="Submit" class="input">
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
		catch(Exception e)
		{
		System.out.println("ETTVendorMasterAdd.jsp: "+e.toString());		
 		}	
%>

