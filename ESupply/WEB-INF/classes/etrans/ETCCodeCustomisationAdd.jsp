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
	Program Name	: ETCCodeCustomisationAdd.jsp
	Module Name		: ETrans
	Task			    : Code_Customization
	Sub Task		  : Add
	Author Name		: Supraja .CKM
	Date Started	: January 22,2003
	Date Completed	: January 22,2003
	Date Modified	:
	Description		:
		This file is used to add a new CodeId Name. This jsp takes all the data entered in the fields and passes to
	  ETCCodeCustAddProcess.jsp

    This file interacts with CodeCustomisationSessionBean and then calls the method getCodeType()' which inturn
    retrieves all the CodeId Names.

	Method Summary  :
		maximumLength()  //This function is used to check the maximum length of the entered StartingSerial No and length fields.
      mandatoryFields()  // This function is used to check for the mandatory fields to be non-empty
		upper(input)  //  This function is to convert the entered text into the Upper Case
		getSpecialCode()  // This function filters the Special chars like  " and ' and ` and
		checkGroup(obj)  // This function allows only 3 groups
		getNumberCode()  // This function is to allow only Numbers while feeding data.
		valueofthis()  //This function is used to allow the maximum length of the value field to be equal to the value of the length filed.
		visibility(input)  // This function is used to disable or enable the valueLOV and make the Value field readonly.
		placeFocus()  // This function is used to place the focus.
		createGroup( num, focusing )  // This function is used to create the group based on the number passed.
		showIds(input)  // This function calls 'ETCCodeCustomisationValueLOV.jsp'
*/
%>
<%@ page import	=	"java.util.ArrayList,javax.naming.InitialContext,
					org.apache.log4j.Logger,
					com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean,
                    com.foursoft.etrans.setup.codecust.bean.CodeCustomisationDOB,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome"%>
          
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />
<%!
  private static Logger logger = null;
%>
<%
  String    FILE_NAME   = "ETCCodeCustomisationAdd.jsp";
  logger  = Logger.getLogger(FILE_NAME);
	ArrayList  codeName	  =	new ArrayList();  //Variable that gets all the codetypes existing in the database
  ArrayList  codeValues = new ArrayList();   
  ArrayList  codeDisplay = new ArrayList(); 
  CodeCustomisationDOB  codeCustDOB = null;
	int		length		=	0;
  String[] codeNameValues = new String[3];
	String temp	=""; //new
	try
	{
		InitialContext 					        initial 		=   new InitialContext();  //Variable that represents the initialcontext for JNDI look up proce
		CodeCustomiseJSPBean			      codeCust		=	new CodeCustomiseJSPBean(); //Object of CodeCustomiseJspBean
    codeCustDOB =	new CodeCustomisationDOB(); //Object of CodeCustomisationDOB    
		SetUpSessionHome 	ETransHOSuperUserHome	= 	( SetUpSessionHome )initial.lookup("SetUpSessionBean");  //Variable that represents CideCystinusatuibSession bean Home interface
		SetUpSession 		  ETransHOSuperUserRemote	= 	( SetUpSession )ETransHOSuperUserHome.create(); //Variable that represents the CodeCustomicationSessionBean Remote Interface
		codeName = ETransHOSuperUserRemote.getCodeCustomizationCodeType("Add",1,loginbean.getTerminalId(),temp);	//new added temp
		// To get the all the CodeTypes Existing in the database.
    codeCustDOB = ETransHOSuperUserRemote.getCodeCustDetails(loginbean);   
         
	}catch(Exception e)
	{
		//Logger.error(FILE_NAME,"Exception in 'Add' " + e);
    logger.error(FILE_NAME+"Exception in 'Add' " + e);
	}
%>
<title>Code Customization Add</title>
<%@ include file="/ESEventHandler.jsp" %>
<script language = "javascript">


valGrp 		= new Array("","","");
valInd		= new Array("","","");
valLen	  = new Array("","","");
valDesc	    = new Array("","","");

var number = 0;

<%
      codeValues = codeCustDOB.getIdsList();  
             
%>
    codeNames   = new Array(<%=codeValues.size()%>);
    codeValues  = new Array(<%=codeValues.size()%>);
    dataNames   = new Array(6);

<%                   
             for(int  i=0;i<codeValues.size();i++)
             {
                codeNameValues  = (String[])codeValues.get(i);
%>             
                codeNames[<%=i%>]    = '<%=codeNameValues[0]%>';
                codeValues[<%=i%>]   = '<%=codeNameValues[1]%>';
<%                
/*Logger.info(FILE_NAME,"codeNameValues[1]: "+codeNameValues[1]);
Logger.info(FILE_NAME,"codeNameValues[1]: "+codeNameValues[2]);       */            
              } 
%>  

	var data1  = "";
	var data2  = "";
	var data3  = "";
	var data4  = "";
	var data5  = "";
	var data6  = "";

dataVal1	= new Array(3);
dataVal2	= new Array(3);
dataVal3	= new Array(3);
  
	data1	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data1	+= "<TR class='formdata' valign='top'> "
				data1	+= "<td ><select name='valGrp'><option value=></option><option value=>"
				data1	+= "</option><option value=></option><option value=></option><option value=></option></select>"
				data1	+= "</TD>"
				data1	+= "</TR>"
				data1	+= "</TABLE>"

				data2	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data2	+= "<TR class='formdata' valign='top'> "
				data2	+= "<td ><select name='valGrp'><option value=></option><option value=>"
				data2	+= "</option><option value=></option><option value=></option><option value=></option></select>"
				data2	+= "</TD>"
				data2	+= "</TR>"
				data2	+= "</TABLE>"


				data3	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data3	+= "<TR class='formdata' valign='top'> "
				data3	+= "<td ><select name='valGrp'><option value=></option><option value=>"
				data3	+= "</option><option value=></option><option value=></option><option value=></option></select>"
				data3	+= "</TD>"
				data3	+= "</TR>"
				data3	+= "</TABLE>"

	// cccc
	data4	+= "<TABLE >"
				data4	+= "<TR class='formdata' valign='top'> "
				data4   += "<td >Value:</td>"
				data4	+= "<td ><input type='text' class='text' name='valGrp' size='7' maxlength='16'  value='' onBlur='upper(this);specialCharFilter(this)'" 
				data4	+= " ></TD>"
				data4	+= "</TR>"
				data4	+= "</TABLE>"

				data5	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data5	+= "<TR class='formdata' valign='top'> "
				data5   += "<td >Value:</td>"
				data5	+= "<td ><input type='text' class='text' name='valGrp' size='16' maxlength='16'  value='' onBlur='upper(this);specialCharFilter(this)'" 
				data5	+= "></TD>"
				data5	+= "</TR>"
				data5	+= "</TABLE>"

				data6	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data6	+= "<TR class='formdata' valign='top'> "
				data6   += "<td >Value:</td>"
				data6	+= "<td ><input type='text' class='text' name='valGrp' size='16' maxlength='16'  value='' onBlur='upper(this);specialCharFilter(this)'" 
				data6	+= "></TD>"
				data6	+= "</TR>"
				data6	+= "</TABLE>"             
        
       dataNames[0]=data1;
       dataNames[1]=data2;            
       dataNames[2]=data3;
       dataNames[3]=data4;
       dataNames[4]=data5;
	     dataNames[5]=data6;
                
	function maximumLength()
	{
		groups	=	document.frm.noOfGrps.value;
		flag	=	true;
		len		=	0; 
		totalLen =  0;
		code	 =	document.frm.codeIdName.value;
		
		if(code == 'ETSHOUSEDOCUMENTID' ||  code == 'ETSHBLID' ||  code == 'ETSCONSIGNEMENTID')
		{
          if(document.forms[0].custFlag[1].checked==true)
		  {
				if(groups == 1)
					len = parseInt(document.frm.valLen.value);
				else if(groups == 2)
					len = parseInt(document.frm.valLen[0].value) + parseInt(document.frm.valLen[1].value);
				else if(groups == 3)
					len = parseInt(document.frm.valLen[0].value) + parseInt(document.frm.valLen[1].value) + parseInt(document.frm.valLen[2].value);
		  }
       }
	   else if(code != 'ETSHOUSEDOCUMENTID' ||  code != 'ETSHBLID' ||  code != 'ETSCONSIGNEMENTID')
	   {
				if(groups == 1)
					len = parseInt(document.frm.valLen.value);
				else if(groups == 2)
					len = parseInt(document.frm.valLen[0].value) + parseInt(document.frm.valLen[1].value);
				else if(groups == 3)
					len = parseInt(document.frm.valLen[0].value) + parseInt(document.frm.valLen[1].value) + parseInt(document.frm.valLen[2].value);
       }

		if(len > 12)
		{
			alert("Sum of  Length fields should not exceed '12'");
			flag = false;
		}
		return flag;
	}

  function setValues1(size,ind)
  {

	var data1  = "";
	var data2  = "";
	var data3  = "";
	var data4  = "";
	var data5  = "";
	var data6  = "";
	var	ik2	   =	0;

	for(j=0; j < document.forms[0].elements.length; j++)
	{
		if(document.forms[0].elements[j].name == "valGrp")
		{
			valGrp[ik2] = "";
			ik2++;
		}
	}

		if(valInd[0] == 'D' || valInd[1] == 'D' || valInd[2] == 'D')
		{
				data1	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data1	+= "<TR class='formdata' valign='top'> "
				data1	+= "<td ><select name='valGrp'><option value=></option><option value=>"
				data1	+= "</option><option value = '" +valGrp[0]+"'></option><option value=></option><option value=></option></select>"
				data1	+= "</TD>"
				data1	+= "</TR>"
				data1	+= "</TABLE>"

				data2	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data2	+= "<TR class='formdata' valign='top'> "
				data2	+= "<td ><select name='valGrp'><option value=></option><option value=>"
				data2	+= "</option><option value = '" +valGrp[1]+"'></option><option value=></option><option value=></option></select>"
				data2	+= "</TD>"
				data2	+= "</TR>"
				data2	+= "</TABLE>"


				data3	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data3	+= "<TR class='formdata' valign='top'> "
				data3	+= "<td ><select name='valGrp'><option value=></option><option value=>"
				data3	+= "</option><option value = '" +valGrp[2]+"'></option><option value=></option><option value=></option></select>"
				data3	+= "</TD>"
				data3	+= "</TR>"
				data3	+= "</TABLE>"

				dataNames[0]=data1;
			   dataNames[1]=data2;            
			   dataNames[2]=data3;
		}	
		else if(valInd[0] == 'S' || valInd[1] == 'S' || valInd[2] == 'S' || ind =='fromGroup')
		{

				data4	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data4	+= "<TR class='formdata' valign='top'> "
				data4   += "<td >Value:</td>"
				data4	+= "<td ><input type='text' class='text' name='valGrp' size='16' maxlength='16'   onBlur='upper(this);specialCharFilter(this)'" 
				data4	+= " value = '" +valGrp[0]+"'></TD>"
				data4	+= "</TR>"
				data4	+= "</TABLE>"

				data5	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data5	+= "<TR class='formdata' valign='top'> "
				data5   += "<td >Value:</td>"
				data5	+= "<td ><input type='text' class='text' name='valGrp' size='16' maxlength='16'  onBlur='upper(this);specialCharFilter(this)'" 
				data5	+= "value = '" +valGrp[1]+"'></TD>"
				data5	+= "</TR>"
				data5	+= "</TABLE>"

				data6	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data6	+= "<TR class='formdata' valign='top'> "
				data6   += "<td >Value:</td>"
				data6	+= "<td ><input type='text' class='text' name='valGrp' size='16' maxlength='16'  onBlur='upper(this);specialCharFilter(this)'" 
				data6	+= " value = '" +valGrp[2]+"'></TD>"
				data6	+= "</TR>"
				data6	+= "</TABLE>" 
				
				dataNames[3]=data4;
			   dataNames[4]=data5;
			   dataNames[5]=data6;
        }
			   
                
}
  
function showValues(input,num)
{
	number	=	num-1;
 	var ind	= input.value;
	var name = input.name;
	len = document.forms[0].valInd.length;
		sel = input.value
		for(var i=0;i<len;i++)
		{
			var name = document.forms[0].valInd[i].value;

			if (num==1)
			{
				if(name=='D')
				{
					cust.innerHTML = dataNames[0];
				}
				else
				{
					cust.innerHTML = dataNames[0];
				}
            
           }
		   else
		   {
		   	if(name=='D')
				{
					cust[i].innerHTML = dataNames[i];
				}
				else
				{
					cust[i].innerHTML = dataNames[(i+3)];
				}
           }
		}
  }
function specialCharFilter(input) 
	{
		s = trim(input.value);
		filteredValues = "''~!@#$%^&*()_-+=|\:;<>,./?"+'"';		
		var i;
		var returnString = "";
		var flag = 0;
		for (i = 0; i < s.length; i++) 
		{  
			var c = s.charAt(i);
			if ( filteredValues.indexOf(c) == -1 ) 
					returnString += c.toUpperCase();
			else
			{
				alert("Please do not enter special characters for value");
				input.focus();
				return false;
			}
		}
		return true;
	}
function disp(input)
  {
	var ind	= input.value;
	var name = input.name;

	if(ind=='undefined')
	{
		ind = input;
	}
	
	var noGrps	=	eval(document.forms[0].noOfGrps.value);

	for(var i=0;i< noGrps;i++)
	{
        var indval   = document.forms[0].valInd[i].value;

			if(indval=='D')
			{
				if( document.layers)
				{
					
					if(noGrps==1 )
					{
						document.layers.cust.document.write(dataNames[0]);// here 'cust' is the nameof span(see below for name span)
						document.layers.cust.document.close();
					}
					else
					{

						document.layers.cust[i].document.write(dataNames[i]);// here 'cust' is the nameof span(see below for name span)
					    document.layers.cust[i].document.close();
					}
				}
				else
				{
					if(document.all)
					{	
						if(noGrps==1)
						{
							setValues1(noGrps,indval);
							cust.innerHTML = dataNames[0];
						}
					    else
						{
							setValues1(noGrps,indval);
							cust[i].innerHTML = dataNames[i];
						}
					}
				}
			}
			else  
			{
				
				if( document.layers)
				{
					if(noGrps==1 )
					{
						setValues1(noGrps,indval);
						document.layers.cust.document.write(data4);// here 'cust' is the nameof span(see below for name span)
					 	document.layers.cust.document.close();
					}
					else
					{
						setValues1(noGrps,indval);
						document.layers.cust[i].document.write(dataNames[(3+i)]);// here 'cust' is the nameof span(see below for name span)
					    document.layers.cust[i].document.close();
					}
				}
				else
				{
					
					if(document.all)
					{	
						if(noGrps==1)
						{
							setValues1(noGrps,indval);
							cust.innerHTML = data4;
						}
					    else
						{	
							setValues1(noGrps,indval);
						    cust[i].innerHTML = dataNames[i+3];
						}
					}
				}
			}
	}
}

  function setValues2(size,ind)
  {

	var data1  = "";
	var data2  = "";
	var data3  = "";
	var data4  = "";
	var data5  = "";
	var data6  = "";
	var	ik2	   =	0;


	var i = 0;
if(document.forms[0].codeId.value.length>0)
{
	for(j=0; j < document.forms[0].elements.length; j++)
	{
		if(document.forms[0].elements[j].name == "valGrp")
		{
			if(size!=i)	
				valGrp[ik2] = document.forms[0].elements[j].value;
			else
				valGrp[ik2] = '';

			ik2++;
			i++;
		}
	}

		if(valInd[0] == 'D' || valInd[1] == 'D' || valInd[2] == 'D')
		{
				

				data1	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data1	+= "<TR class='formdata' valign='top'> "
				data1	+= "<td ><select name='valGrp'><option value=></option><option value=>"
				data1	+= "</option><option value = '" +valGrp[0]+"'></option><option value=></option><option value=></option></select>"
				data1	+= "</TD>"
				data1	+= "</TR>"
				data1	+= "</TABLE>"

				data2	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data2	+= "<TR class='formdata' valign='top'> "
				data2	+= "<td ><select name='valGrp'><option value=></option><option value=>"
				data2	+= "</option><option value = '" +valGrp[1]+"'></option><option value=></option><option value=></option></select>"
				data2	+= "</TD>"
				data2	+= "</TR>"
				data2	+= "</TABLE>"


				data3	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data3	+= "<TR class='formdata' valign='top'> "
				data3	+= "<td ><select name='valGrp'><option value=></option><option value=>"
				data3	+= "</option><option value = '" +valGrp[2]+"'></option><option value=></option><option value=></option></select>"
				data3	+= "</TD>"
				data3	+= "</TR>"
				data3	+= "</TABLE>"


				dataNames[0]=data1;
			   dataNames[1]=data2;            
			   dataNames[2]=data3;
		}	
		else if(valInd[0] == 'S' || valInd[1] == 'S' || valInd[2] == 'S' || ind =='fromGroup')
		{

				data4	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data4	+= "<TR class='formdata' valign='top'> "
				data4   += "<td >Value:</td>"
				data4	+= "<td ><input type='text' class='text' name='valGrp' size='16' maxlength='16'   onBlur='upper(this);specialCharFilter(this)'" 
				data4	+= " value = '" +valGrp[0]+"'></TD>"
				data4	+= "</TR>"
				data4	+= "</TABLE>"

				data5	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data5	+= "<TR class='formdata' valign='top'> "

				data5   += "<td >Value:</td>"
				data5	+= "<td ><input type='text' class='text' name='valGrp' size='16' maxlength='16'  onBlur='upper(this);specialCharFilter(this)'" 
				data5	+= "value = '" +valGrp[1]+"'></TD>"
				data5	+= "</TR>"
				data5	+= "</TABLE>"

				data6	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				data6	+= "<TR class='formdata' valign='top'> "
				data6   += "<td >Value:</td>"
				data6	+= "<td ><input type='text' class='text' name='valGrp' size='16' maxlength='16'  onBlur='upper(this);specialCharFilter(this)'" 
				data6	+= "onKeyPress = 'return getSpecialCode()' value = '" +valGrp[2]+"'></TD>"
				data6	+= "</TR>"
				data6	+= "</TABLE>" 
				
				dataNames[3]=data4;
			   dataNames[4]=data5;
			   dataNames[5]=data6;
        }
			   
}
else
{
	alert("Please select/enter Code Type");
	document.forms[0].codeId.focus();
}
}


function mandatoryFields()
{
	groups	 =	document.frm.noOfGrps.value;
	slNo	 =	document.frm.startingSlNo.value;
	code	 =	document.frm.codeIdName.value;
	grpId    =  document.frm.codeId.value;
   // @@ Suneetha Added on 20050525 PBN-1732
   if(document.frm.codeId.value.length == 0){
	   alert("Enter Code Type ");
	   return false;
   }
   // @@ 20050525 PBN-1732
   if(!maximumLength())
   {
		if(groups > 1)
		{
			for(var  i=0;i<groups;i++)
			{
				document.frm.valLen[i].select();
			}
		}
		else
		{
			document.frm.valLen.select();
		}
		return false;
	} 
	
   if(code == 'ETSHOUSEDOCUMENTID' ||  code == 'ETSHBLID' ||  code =='ETSCONSIGNEMENTID')
   { 
      if(document.forms[0].custFlag[1].checked==true)
      {
        if( slNo.length  == 0)
        {
          alert("Starting Serial No field can not be empty");
          document.frm.startingSlNo.focus();
          return false
        }
        if( slNo.length  > 13)
        {
          alert("Starting Serial No field can not be greater than 13");
          document.frm.startingSlNo.focus();
          return false
        }
      }
   }
   else if(code != 'ETSHOUSEDOCUMENTID' ||  code != 'ETSHBLID' ||  code !='ETSCONSIGNEMENTID')
    { 
        if( slNo.length  == 0)
        {
          alert("Starting Serial No field can not be empty");
          document.frm.startingSlNo.focus();
          return false
        }
        if( slNo.length  > 13)
        {
          alert("Starting Serial No field can not be greater than 13");
          document.frm.startingSlNo.focus();
          return false
        }
	 }
	for(var k=0;k<groups;k++)
	{
     if(code == 'ETSHOUSEDOCUMENTID' ||  code == 'ETSHBLID' ||  code == 'ETSCONSIGNEMENTID')
    {
      if(document.forms[0].custFlag[1].checked==true)
      {
           if(groups > 1)
           {
			 if(document.forms[0].valGrp[k].value=="")
			 {
		        alert("Value can not be empty");
	            document.frm.valGrp[k].focus();
		        document.forms[0].valGrp[k].value.toUpperCase();
				return false;
			 }
           }
           else
           {
			 if(document.forms[0].valGrp.value=="")
			 {	
   		        alert("Value can not be empty");
				document.frm.valGrp.focus();
				document.forms[0].valGrp.value.toUpperCase();
				return false;
			 }	
           }
            
          }
       }   
      
	//}
	else if(code != 'ETSHOUSEDOCUMENTID' ||  code != 'ETSHBLID' ||  code !='ETSCONSIGNEMENTID')
	{
			if(groups == 1)
			{
   				if(document.forms[0].valGrp.value=="")
    			{
    				alert("Value can not be empty");
      				document.frm.valGrp.focus();
 				    document.forms[0].valGrp.value.toUpperCase();
        			return false;
				}
			}
			else
			{	
			  if(document.forms[0].valGrp[k].value=="")
			  {
					alert("Value can not be empty");
					document.frm.valGrp[k].focus();
					document.forms[0].valGrp[k].value.toUpperCase();
					return false;
	          }
		    }
	}  
}
  for(var k=0;k<groups;k++)
  {
    if(code == 'ETSHOUSEDOCUMENTID' ||  code == 'ETSHBLID' ||  code =='ETSCONSIGNEMENTID')
    {  

		if(document.forms[0].custFlag[1].checked == true)
		{
		   if(groups == 1)
		   {
				if(document.forms[0].valLen.value=="")
				{
					alert("Length can not be empty");
					document.frm.valGrp.focus();
					return false;
				}
		   }
		   else if(groups > 1)
		   {	
			  if(document.forms[0].valLen[k].value=="")
			  {
				alert("Length can not be empty");
				document.frm.valLen[k].focus();
				return false;
			  }
		   }
		}
     }   
    else if(code != 'ETSHOUSEDOCUMENTID' ||  code != 'ETSHBLID' ||  code !='ETSCONSIGNEMENTID')
    {  
       if(groups == 1)
       {
      		if(document.forms[0].valLen.value=="")
        	{
    			alert("Length can not be empty");
      			document.frm.valLen.focus();
        		return false;
			}
       }
       else
       {	
          if(document.forms[0].valLen[k].value=="")
          {
            alert("Length can not be empty");
            document.frm.valLen[k].focus();
            return false;
          }
       }
     }  
	 if(code == 'ETSHOUSEDOCUMENTID' ||  code == 'ETSHBLID' ||  code =='ETSCONSIGNEMENTID')
    {
      if(document.forms[0].custFlag[1].checked==true)
      {
         if(document.forms[0].valGrp[k].value=="")
         {
           if(groups > 1)
           {
		        alert("Value can not be empty");
	            document.frm.valGrp[k].focus();
		        document.forms[0].valGrp[k].value.toUpperCase();
           }
           else
           {
		        alert("Value can not be empty");
		        document.frm.valGrp.focus();
	            document.forms[0].valGrp.value.toUpperCase();
           }
            return false;
          }
       }   
     } 
     else if(code != 'ETSHOUSEDOCUMENTID' ||  code != 'ETSHBLID' ||  code !='ETSCONSIGNEMENTID')
    {
      
          if(groups > 1)
           {
		      if(document.forms[0].valGrp[k].value=="")
			  {
				alert("Value can not be empty");
	            document.frm.valGrp[k].focus();
		        document.forms[0].valGrp[k].value.toUpperCase();
	            return false;
			 }	
           }
           else if(groups == 1)
           {
 		      if(document.forms[0].valGrp.value=="")
			  {

		        alert("Value can not be empty");
		        document.frm.valGrp.focus();
	            document.forms[0].valGrp.value.toUpperCase();
	            return false;
			  }
           }
          
     } 
	}
if(code == 'ETSHOUSEDOCUMENTID' ||  code == 'ETSHBLID' ||  code =='ETSCONSIGNEMENTID')
    {
      if(document.forms[0].custFlag[1].checked==true)
      {
		return checkLength();		
	  }
	}	 
	else if(code != 'ETSHOUSEDOCUMENTID' ||  code != 'ETSHBLID' ||  code !='ETSCONSIGNEMENTID')
    {
		return checkLength();		
	}
    return true;

}		
	function showCustFlag(grpId)					
		{
			var dataCust = "";
			if(grpId=='HOUSEDOCUMENT' || grpId=='CONSIGNEMENT' || grpId=='HBL')	
			{
				dataCust	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 >"
				dataCust	+= "<TR class='formdata' valign='top'> "
				dataCust    += "<td >Customisation Type:<br>"
				dataCust	+= "<input type='radio' onClick='createGroup(document.forms[0].noOfGrps.value,2)' id='Default' name='custFlag' value='D' >Default" 
				dataCust	+= "<input type='radio'	id='customized'	 name='custFlag' checked value='C' onClick='createGroup(document.forms[0].noOfGrps.value,1)' >Customized"
				dataCust	+= "</td></TR>"
				dataCust	+= "</TABLE>"


				if( document.layers)
				{
					document.layers.cust10.document.write(dataCust);// here 'cust' is the nameof span(see below for name span)
					document.layers.cust10.document.close();
				}
				else
				{
					if(document.all)
					{
						cust10.innerHTML = dataCust;
					}
				}
			}
  }
  
	function upper(input)
	{
		input.value = input.value.toUpperCase();
	}
	 // @@ Modified by Sailaja on 2005 04 29 for SPETI-5628 
	function getSpecialCode()
	{
		/*if(event.keyCode == 96 || event.keyCode == 59 || event.keyCode == 39 || event.keyCode == 34)
		{
			return false;
		}
		return true;
		*/
	var keycode	= (window.Event) ? window.event.which : window.event.keyCode;

		if(keycode < 65 || keycode > 122 || (keycode > 90 && keycode < 97))	
			{ 		
				return false;
	}
			return true;
	}
	// @@ 2005 04 29 for SPETI- 5628
	function checkGroup(obj)
	{
		if(obj.value.length >0)
  			num = obj.value;
		else
	    	num = 0;

		if(num == 0)
  		{
  			alert("It should have atleast one Group");
  			obj.value="1";
  			obj.focus();
  		}
		else if(num > 3 )
  		{
  			alert("Only it allows 3 Groups at Maximum");
  	    	obj.select();
  		}
  		else if(num > 0 && num<4 )
  		{
  			createGroup( num, 'fromGroup' );
  		}
	}
	function getNumberCode()
	{
		if(event.keyCode!=13)
		{
			if((event.keyCode < 48 || event.keyCode > 57) )
			return false;
		}
		return true;
	}
  
function trim(input)
{
    var indVal  = document.forms[0].valInd.value;
	var groups	 =	document.forms[0].noOfGrps.value;
	if(groups == 1)
	{
		    var indVal  = document.forms[0].valInd.value;
			if(indVal == "S")
			{
			while (input.substring(0,1) == ' ')
				input = input.substring(1, input.length);

			while (input.substring(input.length-1,input.length) == ' ')
				input = input.substring(0, input.length-1);
			if ( input.indexOf(' ') >= 1 )
				 input = input.substring(input.indexOf(' '),input.length);
			}
	}
	else
	{
		for(var o=0;o<groups;o++)
		{
		    var indVal  = document.forms[0].valInd[o].value;

			if(indVal == "S")
			{
			while (input.substring(0,1) == ' ')
				input = input.substring(1, input.length);

			while (input.substring(input.length-1,input.length) == ' ')
				input = input.substring(0, input.length-1);
			if ( input.indexOf(' ') >= 1 )
				 input = input.substring(input.indexOf(' '),input.length);
			}
		}	
	}	
   return input;
}

function checkLength()
	{
		groups	 =	document.forms[0].noOfGrps.value;
		if(groups == 1)
		{
		    indVal  = document.forms[0].valInd.value;
			if(indVal == 'D')
			{
				grpVal = document.forms[0].valGrp.options[document.forms[0].valGrp.selectedIndex].text;
				if(grpVal=='LOCATION' || grpVal=='COMPANY' || grpVal=='ORIGIN' || grpVal=='COMPANYID' || grpVal=='DESTINATION'  || grpVal=='DESTLOCATION' ||  grpVal=='PORTOFLOADING' ||  grpVal=='PORTOFDISCHARGE' ||  grpVal=='ORIGINLOCATION' )
				{
					if(document.forms[0].valLen.value > 3 )
					{
						alert("Selected Value Can't be Greater than 3");
						document.forms[0].valLen.focus();
						return false;
					}
				}
				if(grpVal=='DAY' || grpVal=='MONTH' || grpVal=='CARRIER')
				{
					if(document.forms[0].valLen.value > 2 )
					{
						alert("Selected Value Can't be Greater than 2");
						document.forms[0].valLen.focus();
						return false;
					}
				}
			}
			else if(indVal == 'S')
			{
	
				grpVal = trim(document.forms[0].valGrp.value).length;
				if(grpVal > document.forms[0].valLen.value)
				{
						alert("Entered Value Can't be Greater than entered length");
						document.forms[0].valGrp.focus();
						return false;
				}
				if(grpVal < document.forms[0].valLen.value)
				{
						alert("Entered Value Can't be less than entered length");
						document.forms[0].valGrp.focus();
						return false;
				}
			}
		}	
		else if(groups > 1)
		{
			for(var i=0;i<groups;i++)
			{

		        indVal  = document.forms[0].valInd[i].value;
				
				if(indVal == 'D')
				{
					grpVal = document.forms[0].valGrp[i].options[document.forms[0].valGrp[i].selectedIndex].text;

					if(grpVal=='LOCATION' || grpVal=='COMPANY' || grpVal=='ORIGIN' || grpVal=='COMPANYID' || grpVal=='DESTINATION'  || grpVal== 'DESTLOCATION' ||  grpVal=='PORTOFLOADING' ||  grpVal== 'PORTOFDISCHARGE' ||  grpVal=='ORIGINLOCATION')
					{
						if(trim(document.forms[0].valLen[i].value) > 3 )
						{
							alert("Selected Can't be Greater than 3");
							document.forms[0].valLen[i].focus();
							return false;
						}
					}
					if(grpVal=='DAY' || grpVal=='MONTH' || grpVal=='CARRIER')
					{
						if(trim(document.forms[0].valLen[i].value.length) > 2 )
						{
							alert("Selected Value Can't be Greater than 2");
							document.forms[0].valLen[i].focus();
							return false;
						}
					}
				}
				else if(indVal == 'S')
				{
					
					grpVal = trim(document.forms[0].valGrp[i].value).length;
					if(grpVal > document.forms[0].valLen[i].value)
					{
							alert("Entered Value Can't be Greater than entered length");
							document.forms[0].valGrp[i].focus();
							return false;
					}
					if(grpVal < document.forms[0].valLen[i].value)
					{
						alert("Entered Value Can't be less than entered length");
						document.forms[0].valGrp[i].focus();
						return false;
					}
				}
			}
		}
		return true;
	}		
	
  function setOption(grpId)
  {
    var name	= document.forms[0].codeId.value

     for(var m=0;m<codeNames.length;m++)
     {
         if(codeNames[m]==grpId)
         {
			 var strVal  = codeValues[m].split(',');
			 if(document.getElementsByName("noOfGrps")[0].value>1)
			 {
				 for(var j=0;j<document.forms[0].valInd.length;j++)
				 {
					 for(var k=0;k<strVal.length;k++)
					 {
						 if(document.forms[0].valInd[j].value=='D')
						 {
							document.forms[0].valGrp[j].options[k].value=strVal[k];
							document.forms[0].valGrp[j].options[k].text =strVal[k];
							document.forms[0].valGrp[j].value = valGrp[j];
							
						 }	
					 }	
				 }

			 }
			 else
			 {
					 for(var k=0;k<strVal.length;k++)
					 {
						
						 if(name != 'HOUSEDOCUMENT' &&  name != 'HBL' && name !='CONSIGNEMENT' )
						 {
							

							  if(document.forms[0].valInd.value=='D')
							  {
								document.forms[0].valGrp.options[k].value=strVal[k];
								document.forms[0].valGrp.options[k].text=strVal[k];
								document.forms[0].valGrp.value = valGrp[0];
							  }  
						 
						 }
						 if(name == 'HOUSEDOCUMENT' &&  name == 'HBL' && name =='CONSIGNEMENT' )
						 {

							 if(document.forms[0].custFlag[0].selected == false)
							 {	

								  if(document.forms[0].valInd.value=='D')
								  {
									document.forms[0].valGrp.options[k].value=strVal[k];
									document.forms[0].valGrp.options[k].text=strVal[k];
									document.forms[0].valGrp.value = valGrp[0];

								  }  
							 }
						}
					 }	
			 }
         }
      }  
     }    
	function visibility(input)
	{
		sel = input.value
    
		for(var j=0;j<document.forms[0].valInd.length;j++)
		{
			if(sel == "S")
			{
				document.forms[0].lov_ValueId1.disabled = true
				document.forms[0].valGrp[j].readOnly = false
			}
			if(sel == "D")
			{
				document.forms[0].lov_ValueId1.disabled = true
				input.options[1].selected=true;
			}
		}
	}
	function placeFocus()
	{
   		document.forms[0].codeId_Lov.focus();
    	createGroup(1,'');
	}
	
	function createGroup( num, focusing )
	{
		data = "";
		var indName = "";
		var	name		= document.forms[0].codeId.value;
		var number = 0;
		setValues1( num, focusing );
		var indi		= focusing;


		var actarr		=0;

		for(i=number;i<num;i++)
		 {
			if(indi != "")
			{
	
					dataVal1[i]	= "";
					dataVal2[i]	= "";
					dataVal3[i]	= "";
				
			}
			else
			{
					dataVal1[i]	= "";
					dataVal2[i]	= "";
					dataVal3[i]	= "";
		
			}
		}

		var k1=0;
		var k2=0;
		var k3=0;
		var k4=0;

	
		if(num<4 && num>0)
  		{
			data +="<table width='800' cellspacing='1'>";
			for(i=number;i<num;i++)
			{
				
				if(valInd[i] == "")
				   valInd[i] = "S";
		
				if(valInd[i] == "S")
				    optionsValue = "<option value='S' selected>Static</option>"
								 + "<option value='D'>Dynamic</option>"
				else
				    optionsValue = "<option value='D' selected>Dynamic</option>" 
								 + "<option value='S' >Static</option>"
						
				
				data = "" +data
					+"<tr class='formdata' width=100% >"
						+"<td ><b>ValueGroup"+(i+1)+"</b></td></tr>"
					+"<tr class='formdata'>"
					// cccc 

					+"<td >Length:</td>"
				    +"<td ><input type='text' class='text' name='valLen' size='2' maxlength='1' value= '" +valLen[i]+"' onKeyPress='return getNumberCode()'></td>"
				    +"<td >Indicator:</td>"
				    +"<td ><select name='valInd' onChange='setValues2("+i+",this.value);visibility(this);showValues(this,i);setOption(document.forms[0].codeId.value);' value= '" +valInd[i]+"' class='select' >"
					+optionsValue+"</select></td>" 
				    +"<td ><span id='cust'></span>"
					+"</td><td><input type ='button' disabled name='lov_ValueId"+(i+1)+ "' value='...' onClick='showIds(this)' class='input'>"
					+"</td></tr><tr class='formdata'>"

					+"<td  >Description:</td>"
					+"<td > <input type='text' class='text' name='valDesc' size='25' maxlength='25' onKeyPress = 'return getSpecialCode()' onBlur='upper(this)' value='"+valDesc[i]+"'></td></tr>";
					data = "" + data + "</td></tr>";
			
	   			
			}
			data = "" + data +"</table>";
		

				     
			if (document.layers)
			{
				document.layers.cust1.document.write(data);
				document.layers.cust1.document.close();
			}
			else
			{
				if (document.all)
				{
					cust1.innerHTML = data;
				}
			}
		}

    
      if(num > 1)
      {
           for(var p=0;p<num;p++) 
           {
              disp(document.forms[0].valInd[p].value);
           }
      }

	  if(num==1)
      {
		disp(document.forms[0].valInd.value);
      }

			    k1 = 0;
				k2 = 0;
				k3 = 0;
				k4 = 0;

	if(name=='HOUSEDOCUMENT' ||  name == 'CONSIGNEMENT' || name=='HBL')	
	{
      if(document.forms[0].custFlag[0].checked==true)
      {
        name = name.toLowerCase();
        data = "<td  class='formdata'><font color='black' size=2 face=verdana ><font color='red'>*</font>"+name+" Id will be same as Prq </font></td>";
        cust1.innerHTML = data;
        document.forms[0].noOfGrps.readOnly=true;
        document.forms[0].startingSlNo.readOnly=true;
        
      }
	  }	
	}
	function getCodeIdNames()
	{
		var	name		= document.forms[0].codeIdName.value;
		//var URL 		= 'ETCLOVReportFormatNames.jsp?Operation=Add&fromWhere=CodeCust&TerminalId=<%=loginbean.getTerminalId()%>&shipmentMode='+document.forms[0].shipmentMode.value;
    var URL 		= 'ETCLOVReportFormatNames.jsp?Operation=Add&fromWhere=CodeCust&TerminalId=<%=loginbean.getTerminalId()%>';    
		var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
		var Options		= 'scrollbars = yes,width = 300,height = 350,resizable = yes';
		var Features 	= Bars +' '+ Options;
		var Win 		= open(URL,'Doc',Features);
	}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body   onLoad='placeFocus();disp("S")'>
<form method='post' action="ETCCodeCustomisationProcess.jsp" name = "frm" onSubmit = "return mandatoryFields()">
<table width="100%" cellspacing="1" cellpadding="4"  bgcolor="#FFFFFF" >
  <tr>
	<td>
		<table width="100%" cellspacing="1">
			<tr class='formlabel'>
				<td>
					<table width="100%" cellspacing="1" cellpadding="4" >
						<tr class='formlabel'>
							<td width=50%>Code Customization - Add</td>
							<td align=right><%=loginbean.generateUniqueId("ETCCodeCustomisationAdd.jsp","Add")%></td>
							<%//2005 04 26 for SPETI-5625%>
						</tr>
					</table>
				</td>
			</tr>
		</table>
 			<table width="100%" cellspacing="1">    
				<tr class='formdata'>
					<td width=50%>Code&nbsp;Type:<font color="red">*</font></td>
					<td>
						<input type="text" name="codeId" value="" size="18" maxlength="30" class='text'>
						<input type="hidden" name="codeIdName" value="">
						<input type="button" name="codeId_Lov" value="..." onClick=getCodeIdNames() class='input'>
					</td>
					<td >
						<span id=cust10 style='position:relative;'></span>
					</td>
				</tr>
			</table>
			<table width="100%" cellspacing="1">    
				<tr class='formdata'>
					<td >No&nbsp;Of&nbsp;Groups:</td>
					<td >
 						<input type="text"   name="noOfGrps" value='1' maxlength='1' onKeyPress='return getNumberCode()' onBlur = "checkGroup(this)" size="2">
					</td>
					<td >Starting&nbsp;SL&nbsp;No:</td>
					<td >
						<input type="text" name="startingSlNo" size="12" maxlength="12" onKeyPress='return getNumberCode()' class='text'>
					</td>     
				</tr>
			</table>	
			<table width="100%" cellspacing="1">    
				<tr class='formdata' colspan = '6'>
				    <td >&nbsp;</td>
				 </tr>
			    <tr class='formdata' >
					<td>
						<span id=cust1 style='position:relative;'></span>
					</td>
				</tr>
			    <tr valign="top" class="denotes"> 
					<td align="left">
						<font face="Verdana" size="1" color="blue">*</font> By changing the No of Groups whole value groups will be refreshed 
					</td>
				</tr>                
				<tr>
					<td align="right">  
						<input type="hidden" name= "terminalId" value="<%=loginbean.getTerminalId()%>" >
						<input type="hidden" name= "Operation" value="Add" >
						<input type="submit" name="submit" value="Submit" class='input'>
						<input type="reset" name="reset" value="Reset" class='input'>
				    </td>
			   </tr>
			</table>
      </td>
    </tr>
    </table>
 </form>
</body>