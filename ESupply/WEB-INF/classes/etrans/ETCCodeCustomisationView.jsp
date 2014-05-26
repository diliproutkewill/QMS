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
	Program Name	: ETCCodeCustomisationView.jsp
	Module Name		: ETrans
	Task			    : Code_Customization
	Sub Task		  : View
	Author Name		: Sivarama Krishna .V
	Date Started	: September 11,2001
	Date Completed	: September 11,2001
	Date Modified	 :
	Description		 :
		This file is used to Modify or View the details of the CodeType entered. This jsp takes all the data entered in the fields and passes to
		ETCCodeCustomisationProcess.jsp if the module selected from the eSupply tree is either 'Modify' else if the
		module selected is 'View', this jsp shows the details of the CodeType corresponding to the selected CodeType in the
		ETCCodeCustomisationEnterId.jsp.
		This file interacts with CodeCustomisationSessionBean and then calls the method 'getCodeCustomisationDetails( String codeType)' which inturn
		retrieves all the details of the CodeType passed.
	Method Summary  :
		upper(input)  //  This function is to convert the entered text into the Upper Case
		maximumLength()  // This function is used to check the maximum length of the entered StartingSerial No and length fields.
		mandatoryFields()  // This function is used to check for the mandatory fields to be non-empty
		getSpecialCode() 	// This function filters the Special chars like  " and ' and ` and  ;
		getNumberCode()  // This function is to allow only Numbers while feeding data.
		valueofthis()  //This function is used to allow the maximum length of the value field to be equal to the value of the length filed/.
		checkGroup(obj)  // This function allows only 3 groups
		visibility(input)  // This function is used to disable or enable the valueLOV and make the value field readonly.
		populateList(num)  // This function is used to populate the list on indicator fileds
		createGroup(num)  // This function is used to create the group based on the number passed.
		showIds(input)  // This function calls 'ETCCodeCustomisationValueLOV.jsp'
*/
%>
<%@ page import	=	"javax.naming.InitialContext,
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.setup.codecust.bean.CodeCustomisationDOB,
					com.foursoft.esupply.common.java.ErrorMessage,
				    com.foursoft.esupply.common.java.KeyValue,
				    java.util.ArrayList" %>

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session" />
<jsp:useBean id ="CodeCustomisationObj" class= "com.foursoft.etrans.setup.codecust.bean.CodeCustomiseJSPBean" scope ="request"/>

<%!
  private static Logger logger = null;
%>

<%
	String  FILE_NAME 		=   "ETCCodeCustomisationView.jsp";
  logger  = Logger.getLogger(FILE_NAME);
	ArrayList  codeName		=	new ArrayList();
	String  operation		=	null;       //String variable that represents the operation type
	String 	codeType   		=	null;       //String variable that represents the Code Type
	String	disable			=	"";	        //String variable that represents wethere the field is diabled or not
	String	read			=	"";
	String	readOnly		=	"";
	String	actionValue		=	"";           //String variable that represents the url for naviation
	int 	noOfGrps 		=	0;            //Integer variable that represents the no of groups
	long 	slNo 			=	0;            //Integer variable that represents the serial number
	int  	temp 			=	0;            //Variable that repesents temparary value
	String 	   val[] 		=	new String[3];
	String     grpValue[]	=	new String[3];
	String 	   desc[] 		=	new String[3];
	String 	   ind[] 		=	new String[3];
	int 	   len[] 		=	new int[3];
	String	   indValue[]	=	new String[3];
	ArrayList  codeValues   =	new ArrayList();
	String[]   codeNameValues = new String[3];
	String[]   indValues    =   new String[3];
	String     codeId       =   null;
	String	   custFlag		=	"";
	CodeCustomisationDOB  codeCustDOB = null;
%>
<%


	try
	{
		operation 	=  request.getParameter("Operation");
		codeId 	  =  request.getParameter("codeId");
		//new on 5-7-04
		if(request.getParameter("codeIdName")==null)
			codeType 	  =  request.getParameter("codeId");
		else
			codeType	=	codeId;
		//codeType	=	"ETS"+codeId+"ID";

 		
		//System.out.println(" operation.........."+operation);
		
		//System.out.println(" codeId.........."+codeId);

		//System.out.println("  codeType........."+codeType);

		codeCustDOB =	 new CodeCustomisationDOB(); //Object of CodeCustomisationDOB
		InitialContext context = new InitialContext();  //Initial context object for JNDI lookup process
		SetUpSessionHome 	ETransHOSuperUserHome	=	(SetUpSessionHome)context.lookup("SetUpSessionBean");
		SetUpSession 		ETransHOSuperUserRemote	=	(SetUpSession)ETransHOSuperUserHome.create();
		CodeCustomisationObj = ETransHOSuperUserRemote.getCodeCustomisationDetails(codeType,loginbean.getTerminalId());
		codeCustDOB          = ETransHOSuperUserRemote.getCodeCustDetails(loginbean);

		if( CodeCustomisationObj != null)
		{
			//Logger.info(FILE_NAME,"CustFlag:	"+ CodeCustomisationObj.getCustFlag());
			noOfGrps = CodeCustomisationObj.getNoOfGrps();
			slNo     = CodeCustomisationObj.getStartingSlNo();
			len[0]   = CodeCustomisationObj.getValLen1();
			val[0]   = CodeCustomisationObj.getValGrp1();
			desc[0]  = CodeCustomisationObj.getValDesc1();
			ind[0]   = CodeCustomisationObj.getValInd1();
			len[1]   = CodeCustomisationObj.getValLen2();
			val[1]   = CodeCustomisationObj.getValGrp2();
			desc[1] = CodeCustomisationObj.getValDesc2();
			ind[1] = CodeCustomisationObj.getValInd2();
	  		len[2] = CodeCustomisationObj.getValLen3();
			val[2] = CodeCustomisationObj.getValGrp3();
	     	desc[2] = CodeCustomisationObj.getValDesc3();
			ind[2] = CodeCustomisationObj.getValInd3();
			custFlag = 	CodeCustomisationObj.getCustFlag();
			  indValues[0] = ind[0];
			  indValues[1] = ind[1];
			  indValues[2] = ind[2];
		}
		else
		{

			ErrorMessage errorMessageObject = new ErrorMessage("Record does not Exist With CodeId Name  '" +codeType +"'","ETCCodeCustomisationEnterId.jsp");
			ArrayList keyValueList = new ArrayList();

			keyValueList.add(new KeyValue("ErrorCode","RNF"));
			keyValueList.add(new KeyValue("Operation",operation));
			errorMessageObject.setKeyValueList(keyValueList);
			request.setAttribute("ErrorMessage",errorMessageObject);



%>
		<jsp:forward page="../ESupplyErrorPage.jsp"/>
<%
		}
	}catch(Exception e)
	{

		//Logger.error(FILE_NAME,"Exception in 'Code Customisation View' : "+e.toString());
    logger.error(FILE_NAME+"Exception in 'Code Customisation View' : "+e.toString());

	}
%>
<%


	if(	operation.equalsIgnoreCase("Modify"))
	{
		readOnly	=	"";
		actionValue	=	"ETCCodeCustomisationProcess.jsp";
	}
	else if( operation.equalsIgnoreCase("View"))
	{
		readOnly	=	"readonly";
		actionValue	=	"ETCCodeCustomisationEnterId.jsp";
	}
%>
<html>
<head>
<title>Code Customization <%=operation%></title>
<%@ include file="/ESEventHandler.jsp" %>
<script language = "javascript">
var      data1       = "";
var		 number		 = 0;
valGrp 		= new Array("","","");
valInd		= new Array("","","");
valLen		= new Array("","","");
valDesc	    = new Array("","","");
custFlag	= "<%=custFlag%>";
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
                codeNames[<%=i%>]     = '<%=codeNameValues[0]%>';
                codeValues[<%=i%>]   = '<%=codeNameValues[1]%>';
<%
              }
%>

	var data1  = "";
	var data2  = "";
	var data3  = "";
	var data4  = "";
	var data5  = "";
	var data6  = "";

function spanCall(code)
{

  if('<%=operation%>'=="Modify" )
  {
	var dataspan = "";
	var strVal  = "";
    setValues1( <%=noOfGrps%>,'<%=ind[0]%>' );

 
  for(var j=0;j<codeNames.length;j++)
	{
		if(code	== codeNames[j])
		{
			strVal  = codeValues[j].split(',');
			break;
		}
	}
<%
			for( int i=0;i< noOfGrps;  i++)
			{
					if(indValues[i]!= null && indValues[i].equals("S"))
					{	
				  
	%>
								dataspan	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
								dataspan	+= "<TR class='formdata' valign='top'> "
								dataspan	+="<input type='text' class='text' name='valGrp' size='12' maxlength='16' value='<%=val[i]%>'></td>"
								
	<%
					  }
           if(indValues[i]!= null && indValues[i].equals("D"))
          {
%>
						 dataspan	+="<select class='select' name='valGrp'>"

                for(var t=0; t<strVal.length;t++ )
                {
                    if('<%=val[i]%>'==strVal[t])                
                    {
        							dataspan	+="<option value="+strVal[t]+" selected>"+strVal[t]+"</option><br>"
                    }
                    else 
                    {
         							dataspan	+="<option value="+strVal[t]+" >"+strVal[t]+"</option><br>"
                     
                    }
                }
<%
           
				 }
%>
			dataspan = "" + dataspan +"</table>";


		if (document.layers)
		{
			document.layers.cust.document.write(dataspan);
			document.layers.cust.document.close();
		}
		else
		{
			if (document.all)
			{
				if(document.frm.noOfGrps.value > 1)
				{
					if(custFlag == "C")
					cust[<%=i%>].innerHTML = dataspan;
				}
				else
				cust.innerHTML = dataspan;
			}
		}
		dataspan = "";
<%
			}
%> 
  }
}


	data1	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data1	+= "<TR class='formdata' valign='top'> "
				data1	+= "<td width='150'><select class='select' name='valGrp'><option value=></option><option value=>"
				data1	+= "</option><option value=></option><option value=></option><option value=></option></select>"
				data1	+= "</TD>"
				data1	+= "</TR>"
				data1	+= "</TABLE>"

				data2	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data2	+= "<TR class='formdata' valign='top'> "
				data2	+= "<td width='150'><select class='select' name='valGrp'><option value=></option><option value=>"
				data2	+= "</option><option value=></option><option value=></option><option value=></option></select>"
				data2	+= "</TD>"
				data2	+= "</TR>"
				data2	+= "</TABLE>"


				data3	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data3	+= "<TR class='formdata' valign='top'> "
				data3	+= "<td width='150'><select class='select' name='valGrp'><option value=></option><option value=>"
				data3	+= "</option><option value=></option><option value=></option><option value=></option></select>"
				data3	+= "</TD>"
				data3	+= "</TR>"
				data3	+= "</TABLE>"


	data4	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data4	+= "<TR class='formdata' valign='top'> "
				data4	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'  value='' onBlur='upper(this);specialCharFilter(this)'" 
				data4	+= "onKeyPress = 'return getSpecialCode()' ></TD>"
				data4	+= "</TR>"
				data4	+= "</TABLE>"

				data5	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data5	+= "<TR class='formdata' valign='top'> "
				data5	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'  value='' onBlur='upper(this);specialCharFilter(this)'" 
				data5	+= "onKeyPress = 'return getSpecialCode()' ></TD>"
				data5	+= "</TR>"
				data5	+= "</TABLE>"

				data6	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data6	+= "<TR class='formdata' valign='top'> "
				data6	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'  value='' onBlur='upper(this);specialCharFilter(this)'" 
				data6	+= "onKeyPress = 'return getSpecialCode()' ></TD>"
				data6	+= "</TR>"
				data6	+= "</TABLE>"             
        
		dataNames[0]=data1;
		dataNames[1]=data2;            
		dataNames[2]=data3;
		dataNames[3]=data4;
		dataNames[4]=data5;
		dataNames[5]=data6;


function showValues(input,num)
{
	number	=	num-1;
 	var ind	= input.value;
	var name = input.name;
	len = document.forms[0].valInd.length;
	numb = document.frm.noOfGrps.value;
	sel = input.value
		for(var i=0;i<len;i++)
		{
			var name = document.forms[0].valInd[i].value;
			if (numb==1)
			{
				if(name=='D')
				{
					cust.innerHTML = dataNames[0];
				}
				else
				{
					cust.innerHTML = dataNames[4];
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
			
			valGrp[ik2] = document.forms[0].elements[j].value;
		//	valGrp[ik2] = '';
			ik2++;
		}
	}

		if(valInd[0] == 'D' || valInd[1] == 'D' || valInd[2] == 'D')
		{
				data1	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data1	+= "<TR class='formdata' valign='top'> "
				data1	+= "<td width='150'><select class='select' name='valGrp'><option value=></option><option value=>"
				data1	+= "</option><option value = '" +valGrp[0]+"'></option><option value=></option><option value=></option></select>"
				data1	+= "</TD>"
				data1	+= "</TR>"
				data1	+= "</TABLE>"

				data2	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data2	+= "<TR class='formdata' valign='top'> "
				data2	+= "<td width='150'><select class='select' name='valGrp'><option value=></option><option value=>"
				data2	+= "</option><option value = '" +valGrp[1]+"'></option><option value=></option><option value=></option></select>"
				data2	+= "</TD>"
				data2	+= "</TR>"
				data2	+= "</TABLE>"


				data3	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data3	+= "<TR class='formdata' valign='top'> "
				data3	+= "<td width='150'><select class='select' name='valGrp'><option value=></option><option value=>"
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

				data4	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data4	+= "<TR class='formdata' valign='top'> "
				data4   += "<td width='130'>Value:</td>"
				data4	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'   onBlur='upper(this)'" 
				data4	+= "onKeyPress = 'return getSpecialCode()' value = '" +valGrp[0]+"'></TD>"
				data4	+= "</TR>"
				data4	+= "</TABLE>"

				data5	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data5	+= "<TR class='formdata' valign='top'> "
				data5   += "<td width='130'>Value:</td>"
				data5	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'  onBlur='upper(this)'" 
				data5	+= "onKeyPress = 'return getSpecialCode()' value = '" +valGrp[1]+"'></TD>"
				data5	+= "</TR>"
				data5	+= "</TABLE>"

				data6	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data6	+= "<TR class='formdata' valign='top'> "
				data6   += "<td width='130'>Value:</td>"
				data6	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'  onBlur='upper(this)'" 
				data6	+= "onKeyPress = 'return getSpecialCode()' value = '" +valGrp[2]+"'></TD>"
				data6	+= "</TR>"
				data6	+= "</TABLE>" 
				
				  dataNames[3]=data4;
			    dataNames[4]=data5;
			   dataNames[5]=data6;
        }
			   
                
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
        var indval =document.forms[0].valInd[i].value;

			if(indval=='D')
			{
				if( document.layers)
				{
					
					if(noGrps==1 )
					{
						document.layers.cust.document.write(dataNames[0]);// here 'cust' is the nameof span(see below for name span)
						document.layers.cust.document.close();
					}
					else{

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
						document.layers.cust.document.write(data4);// here 'cust' is the nameof span(see below for name span)
						document.layers.cust.document.close();
					}
					else{
					
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
	var	ik1	   =	0;
	var i = 0;

	for(j=0; j < document.forms[0].elements.length; j++)
	{

      if(document.forms[0].elements[j].name == "valInd"){
		  valInd[ik1]=document.forms[0].elements[j].value;
		   ik1++;
      }
      else if(document.forms[0].elements[j].name == "valGrp"){
		  valGrp[ik2] = document.forms[0].elements[j].value;
		  ik2++;
			i++;
		}
	}

	 if (valInd[0] == 'D'){

				data1	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data1	+= "<TR class='formdata' valign='top'> "
				data1	+= "<td width='150'><select class='select' name='valGrp'><option value=></option><option value=>"
				data1	+= "</option><option value = '" +valGrp[0]+"'></option><option value=></option><option value=></option></select>"
				data1	+= "</TD>"
				data1	+= "</TR>"
				data1	+= "</TABLE>"


	 }
	 if (valInd[1] == 'D'){

				data2	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data2	+= "<TR class='formdata' valign='top'> "
				data2	+= "<td width='150'><select class='select' name='valGrp'><option value=></option><option value=>"
				data2	+= "</option><option value = '" +valGrp[1]+"'></option><option value=></option><option value=></option></select>"
				data2	+= "</TD>"
				data2	+= "</TR>"
				data2	+= "</TABLE>"


	 }
	 if (valInd[2] == 'D'){

				data3	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data3	+= "<TR class='formdata' valign='top'> "
				data3	+= "<td width='150'><select class='select' name='valGrp'><option value=></option><option value=>"
				data3	+= "</option><option value = '" +valGrp[2]+"'></option><option value=></option><option value=></option></select>"
				data3	+= "</TD>"
				data3	+= "</TR>"
				data3	+= "</TABLE>"


	 }

	 if(valInd[0] == 'S' ){
		 if (size==0)
         valGrp[0]='';

				data4	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data4	+= "<TR class='formdata' valign='top'> "
				data4	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'   onBlur='upper(this);specialCharFilter(this)'" 
				data4	+= "onKeyPress = 'return getSpecialCode()' value = '" +valGrp[0]+"'></TD>"
				data4	+= "</TR>"
				data4	+= "</TABLE>"
				dataNames[3]=data4;
	 }

    if(valInd[1] == 'S' ){
		if (size==1)
         valGrp[1]='';

				data5	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data5	+= "<TR class='formdata' valign='top'> "
				data5	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'   onBlur='upper(this);specialCharFilter(this)'" 
				data5	+= "onKeyPress = 'return getSpecialCode()' value = '" +valGrp[1]+"'></TD>"
				data5	+= "</TR>"
				data5	+= "</TABLE>"
				dataNames[4]=data5;
	 }
	if(valInd[2] == 'S' ){
		if (size==2)
         valGrp[2]='';

				data6	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data6	+= "<TR class='formdata' valign='top'> "
				data6	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'   onBlur='upper(this);specialCharFilter(this)'" 
				data6	+= "onKeyPress = 'return getSpecialCode()' value = '" +valGrp[2]+"'></TD>"
				data6	+= "</TR>"
				data6	+= "</TABLE>"

				   dataNames[5]=data6;
	 }

	if (ind =='fromGroup')
		{

				data4	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data4	+= "<TR class='formdata' valign='top'> "
				data4   += "<td width='130'>Value:</td>"
				data4	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'   onBlur='upper(this);specialCharFilter(this)'" 
				data4	+= "onKeyPress = 'return getSpecialCode()' value = '" +valGrp[0]+"'></TD>"
				data4	+= "</TR>"
				data4	+= "</TABLE>"

				data5	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data5	+= "<TR class='formdata' valign='top'> "
				data5   += "<td width='130'>Value:</td>"
				data5	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'  onBlur='upper(this);specialCharFilter(this)'" 
				data5	+= "onKeyPress = 'return getSpecialCode()' value = '" +valGrp[1]+"'></TD>"
				data5	+= "</TR>"
				data5	+= "</TABLE>"

				data6	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				data6	+= "<TR class='formdata' valign='top'> "
				data6   += "<td width='130'>Value:</td>"
				data6	+= "<td width='150'><input type='text' class='text' name='valGrp' size='16' maxlength='16'  onBlur='upper(this);specialCharFilter(this)'" 
				data6	+= "onKeyPress = 'return getSpecialCode()' value = '" +valGrp[2]+"'></TD>"
				data6	+= "</TR>"
				data6	+= "</TABLE>" 
				
				dataNames[3]=data4;
			    dataNames[4]=data5;
			    dataNames[5]=data6;
        }
			   
                
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
				alert("grpVal:	"+grpVal)
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
  function setOption(grpId)
  {
    var name	= document.forms[0].codeId.value
     for(var m=0;m<codeNames.length;m++)
     {
         if(codeNames[m]==grpId)
         {
             var strVal  = codeValues[m].split(',');
			 if(document.forms[0].noOfGrps.value>1)
			 {
				 for(var j=0;j<document.forms[0].valInd.length;j++)
				 {
					 for(var k=0;k<strVal.length;k++)
					 {
						 if(document.forms[0].valInd[j].value=='D')
						 {
							if(name != 'HOUSEDOCUMENT' &&  name != 'HBL' && name !='CONSIGNEMENT' )
							{
									

									  if(document.forms[0].valInd[j].value=='D')
									  {
										document.forms[0].valGrp[j].options[k].value=strVal[k];
										document.forms[0].valGrp[j].options[k].text=strVal[k];
										document.forms[0].valGrp[j].value = valGrp[j];
									  }  
								 
							}
							if(name == 'HOUSEDOCUMENT' || name == 'HBL'|| name =='CONSIGNEMENT' )
							{
									 if(document.forms[0].custFlag[0].checked == false)
									 {	

										  if(document.forms[0].valInd[j].value=='D')
										  {
											document.forms[0].valGrp[j].options[k].value=strVal[k];
											document.forms[0].valGrp[j].options[k].text=strVal[k];
											document.forms[0].valGrp[j].value = valGrp[j];
										  }  
									 }
							}
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
								document.forms[0].valGrp.value = valGrp[j];
							  }  
						 
						 }
						 if(name == 'HOUSEDOCUMENT' || name == 'HBL'|| name =='CONSIGNEMENT' )
						 {
							 if(document.forms[0].custFlag[0].checked == false)
							 {	

								  if(document.forms[0].valInd.value=='D')
								  {
									document.forms[0].valGrp.options[k].value=strVal[k];
									document.forms[0].valGrp.options[k].text=strVal[k];
									document.forms[0].valGrp.value = valGrp[j];
								  }  
							 }
						}
					 }	
			 }
         }
      }  
     }    
	//  This function is to convert the entered text into the Upper Case
	function upper(input)
	{
		input.value = input.value.toUpperCase();
	}
	// This function is used to check the maximum length of the entered StartingSerial No and length fields.
	function maximumLength()
	{
		groups	=	document.frm.noOfGrps.value;
		flag	=	true;
		len		=	0; 
		totalLen =  0;
		
		len = parseInt(document.frm.valLen[0].value) + parseInt(document.frm.valLen[1].value) + parseInt(document.frm.valLen[2].value);
			
		if(len > 12)
		{
			alert("Sum of  Length fields should not exceed '12'");
			flag = false;
		}
		return flag;
	}
  
	// This function is used to check for the mandatory fields to be non-empty
function mandatoryFields()
{
		groups	 =	document.frm.noOfGrps.value;
		slNo	 =	document.frm.startingSlNo.value;
		code	 =	document.frm.codeIdName.value;
		grpId    =  document.frm.codeId.value;


 		//setOption(grpId);
    
    
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
           {	if(document.forms[0].valGrp.value=="")
				{
		        alert("Value can not be empty");
		        document.frm.valGrp.focus();
	            document.forms[0].valGrp.value.toUpperCase();
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
		return checkLength();		
	  }
     } 
     else if(code != 'ETSHOUSEDOCUMENTID' ||  code != 'ETSHBLID' ||  code !='ETSCONSIGNEMENTID')
    {
      
         
         return checkLength();		
          
     } 
	}
	document.forms[0].b1.disabled='true';
    return true;
}	
	// @@ Modified by Sailaja on 2005 04 29 for SPETI-5631
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
	// @@ 2005 04 29 for SPETI-5631
function showCustFlag(grpId)					
  {

			var dataCust = "";
			if(grpId=='HOUSEDOCUMENT' || grpId=='CONSIGNEMENT' || grpId=='HBL')	
			{
				dataCust	+= "<TABLE border=0 cellPadding=4 cellSpacing=1 bgcolor='#FFFFFF'>"
				dataCust	+= "<TR class='formdata' valign='top'> "
				dataCust    += "<td width='300'>Customization Type:<br>"
				dataCust	+= "<input type='radio' onClick='modifyGroup(document.forms[0].noOfGrps.value)' id='Default' name='custFlag' value='D' >Default" 
				dataCust	+= "<input type='radio'	id='customized'	name='custFlag'  onClick='modifyGroup(document.forms[0].noOfGrps.value)' >Customized"
				dataCust	+= "</td></TR>"
				dataCust	+= "</TABLE>"

//				alert(dataCust);

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
    // This function is to allow only Numbers while feeding data.
	function getNumberCode()
	{
		if(event.keyCode!=13)
		{
			if((event.keyCode < 48 || event.keyCode > 57) )
			return false;
		}
		return true;
	}
    //This function is used to allow the maximum length of the value field to be equal to the value of the length filed/.
	
	opr = '<%=operation%>';
    // This function allows only 3 groups
 /*   
  function setValues2(size,ind)
  {

	var data1  = "";
	var data2  = "";
	var data3  = "";
	var data4  = "";
	var data5  = "";
	var data6  = "";
	var	ik2	   =  0;
	var	ik3	   =  0;

//	alert("size -> "+size);

	var i = 0;
	for(j=0; j < document.forms[0].elements.length; j++)
	{
		if(document.forms[0].elements[j].name == "valGrp")
		{
			
			if(size == 0)
			{
				if(size!=i)	
				{
					valGrp[ik2] = document.forms[0].elements[j].value;
					//alert("valGrp[ik2] -> "+valGrp[ik2]);
					ik2++;
				}
			}
			else if(size != 0)
			{
				if(size == i)	
				{
					valGrp[ik3] = document.forms[0].elements[j].value;
					//alert("valGrp[ik3] -> "+valGrp[ik3]);
					ik3++;
				}
			}
			i++;
		}
	}
  }

*/
function showCustType(grpId)
{
   if(grpId=='HOUSEDOCUMENT' || grpId=='CONSIGNEMENT' || grpId=='HBL')	
   {
	
	  var custFlag  = "<%=custFlag%>";
		if(custFlag == "D")
			document.forms[0].custFlag[0].checked=true;
		else	
			document.forms[0].custFlag[1].checked=true; //new on 5-7-04
   }	
}

	function checkGroup(obj)
	{
		if( opr == "Modify" )
 		{
  			num = obj.value;
  			var temp;

		   if(num<4 )
  				modifyGroup(num);
  			else
  			{
  				alert("Only it allows 3 groups at Maximum");
  	   			obj.select();
  			}
 		}
	}
    // This function is used to disable or enable the valueLOV and make the value field readonly.
	function visibility(input)
	{
		na = input.name
		sel = input.value
		if(na == "valInd1")
		{
			/*if(sel == "S")
			{
				document.forms[0].lov_ValueId1.disabled = true
				document.forms[0].valGrp1.readOnly = false
			}
			if(sel == "D")
			{
				document.forms[0].lov_ValueId1.disabled = true
				alert("This Feature To be Implemented Next");
				input.options[0].selected=true;
				//document.forms[0].lov_ValueId1.disabled = false
				//document.forms[0].valGrp1.readOnly =true
			}*/
		}
		if(na == "valInd2")
		{
		
		}
		if(na == "valInd3")
		{
			
		}
	}

	len          = new Array("","","");
	val          = new Array("","","");
	desc         = new Array("","","");
  ind          = new Array("","","");
  ind1         = new Array("","","");
  indValue     = new Array("","","");
  indValue1    = new Array("","","");

<%
						for( int i=0;i< noOfGrps;  i++)
						{
%>            

          val[<%=i%>]		          =	'<%=val[i]%>';
          desc[<%=i%>]			      =	'<%=desc[i]%>';
          len[<%=i%>]			        =	'<%=len[i]%>';
          ind[<%=i%>]			        =	'<%=ind[i]%>';
          if(ind[<%=i%>] == 'D')
          {
              indValue[<%=i%>]  = 'Dynamic';         
              indValue1[<%=i%>]  = 'Static';         
              ind1[<%=i%>]       = 'S'; 
          }    
          if(ind[<%=i%>] == 'S')
          {
              indValue[<%=i%>]  = 'Static';         
              indValue1[<%=i%>]  = 'Dynamic';                       
              ind1[<%=i%>]       = 'D'; 
          }
          if(desc[<%=i%>]	== 'null')
              desc[<%=i%>]	=	'';

             
<%          
							if(ind[i].equals("Dynamic") )
							{
								read	=	"readOnly";
								disable =	"";
%>                
								indValue[i] = "D";
<%                
							}
							if( ind[i].equals("Static") )
							{
								disable	=	"disabled";
								read	=	"";
%>                
								indValue[i] = "S";
							
<%              
              }
            }
%>
	// This function is used to create the group based on the number passed.
	function modifyGroup(num)
	{
		opr       = '<%=operation%>';
    noOfGrps  = <%=noOfGrps%>;
		data = "";
    var	name		= document.forms[0].codeId.value;
    var numb  =	<%=noOfGrps%>;
<%
		for( int i=0;i< noOfGrps;  i++)
		{
			if( ind[i].equals("D") )
			{
				ind[i]	=	"Dynamic";

			}
			else if( ind[i].equals("S") )
			{
				ind[i]	=	"Static";
			}
			if( desc[i] == null )
				desc[i]	=	"";
		}
%>

  		{
<%
			if(!operation.equals("Modify"))
			{
%>
				data +="<table width=800  cellpadding=4 cellspacing=1 align='center' bgcolor='#FFFFFF'>";
        
<%
				for( int i=0;i< noOfGrps;  i++)
				{
%>
         

	 		 		data = "" +data
							+"<tr class='formdata'>"
							+"<td colspan='6' width='676' align='center'><b>Value Group"+(<%=i%>+1)+"</b></td></tr>"
	       					+"<tr class='formdata'>"
							+"<td width='120'>Length:</td>"
        					+"<td width='78'><input type='text' class='text' name='valLen' size='2' value='<%=len[i]%>' readonly ></td>"
       						+"<td width='47'>Indicator:</td>"
	       					+"<td width='150'><input type='text' class='text' name='valInd' size='10' value='<%=ind[i]%>' readonly ></td>"
       						+"<td width='130'>Value:</font></td>"
       						+"<td width='150'><input type='text' class='text' name='valGrp' size='16' value='<%=val[i]%>' readonly ></td>"
         					+"</tr><tr class='formdata'>"
                  +"<td  width='120'>Description:</td>"
            			+"<td colspan='5'  width='556'> <input type='text' class='text' name='ValDesc"+(<%=i%>+1)+"' size='28' value='<%=desc[i]%>' readonly ></td></tr>";
							data = "" + data + "</td></tr>";
              
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

<%
				}
			}
			else if(operation.equals("Modify"))
			{
%>
				data +="<table width=800 bordercolor=000080 cellpadding=4 cellspacing=1 align='center' bgcolor='#FFFFFF'>";
   		setValues1( num,ind[0] );
        if(num<4 && num>0)
        {
			

          for(i=0;i<numb;i++)
          {
				 if(ind[i] == '')    
				{
					ind[i] = 'S';
					indValue[i]  = 'Static'; 
					ind1[i] = 'D';
					indValue1[i]  = 'Dynamic';
				} 
            
							data = "" +data
              
							+"<tr class='formdata'>"
							+"<td colspan='6' width='676' align='center'><b>Value Group"+(i+1)+"</b></td></tr>"
							+"<tr class='formdata'>"
							+"<td width='120'>Length:</td>"
							+"<td width='78'> <input type='text' class='text' name='valLen' size='2' maxlength='1' value='"+len[i]+"' onKeyPress='return getNumberCode()' ></td>"
							+"<td width='47'>Indicator:</td>"
							+"<td width='152'><select name='valInd' onChange='setValues2("+i+",this.value);visibility(this);showValues(this,i);setOption(document.forms[0].codeId.value)' class='select'>"
 							+"<option value='"+ind[i]+"'>"+indValue[i]+"</option><option value='"+ind1[i]+"'>"+indValue1[i]+"</option>"
							+"</select></td>"
							+"<td width='130'>Value:</td>"
							+"<td width='150'><span id='cust'></span>"
							+"</td></tr><tr class='formdata'>"
							+"<td  width='120'>Description:</td>"
							+"<td colspan='5'  width='556'><input type='text' class='text' name='valDesc"+(i+1)+"' size='25' maxlength='25' value='"+desc[i]+"' onBlur='upper(this)' onKeyPress = 'return getSpecialCode()' ></td></tr>";
							data = "" + data + "</td></tr>";
          }
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
	//}
	if(numb >1 )
		{
			for(var p=0;p<num;p++) 
			{
			  disp(document.forms[0].valInd[p].value);
			}
		}
   if(numb  == 1 )
		{
		  disp(document.forms[0].valInd.value);
		}
	if(name=='HOUSEDOCUMENT' ||  name == 'CONSIGNEMENT' || name=='HBL')	
	{
		  if(document.forms[0].custFlag[0].checked==true)
		  {
        if(document.forms[0].custFlag[0].checked==true)
      {
        name = name.toLowerCase();
        data = "<td  width='120' class='formdata'><font color='black' size=2 face=verdana ><font color='red'>*</font>"+name+" Id will be same as Prq </font></td>";
        cust1.innerHTML = data;
        document.forms[0].noOfGrps.readOnly=true;
        document.forms[0].startingSlNo.readOnly=true;
        
      }
		  }
	}	
<%		
		}
%>		
}	
}
</script>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
</head>
<body onLoad='showCustFlag(document.forms[0].codeId.value);showCustType(document.forms[0].codeId.value);modifyGroup(1);spanCall(document.forms[0].codeId.value)'>
<form method='post' action='<%=actionValue%>' name = "frm" onSubmit = "return mandatoryFields()">
<table width="800" border="0" cellspacing="0" cellpadding="0"  bgcolor='#FFFFFF'>
<tr><td bgcolor="#fffffff">
  <table width="800" border="0" cellspacing="1" cellpadding="4" bgcolor='#FFFFFF'>
    <tr class='formlabel'>
      <td colspan="5" width="585" ><table width="800" border="0" ><tr class='formlabel'><td>Code Customization - <%=operation%></td><td align=right><%=loginbean.generateUniqueId("ETCCodeCustomisationView.jsp",operation)%></td></tr></table></td>
    </tr></table>
	<table width="800" border="0" cellspacing="1" cellpadding="4" bgcolor='#FFFFFF'>
	<tr class='formdata'><td colspan="7">&nbsp;</td></tr>
   <tr class='formdata'>
      <td width="120">Code&nbsp;Type:</td>
      <td width="170">
       <input type='text' class='text' name="codeIdName" size="24" maxlength = "30" value='<%=codeType%>' readonly >
       <input type="hidden" name="codeId" size="16" maxlength = "30" value='<%=codeId%>' readonly >
      </td>
<td colspan="5"  width="585">
         <span id=cust10 style='position:relative;'></span>
      </td>
      
    </tr>
    <tr class='formdata'>
      <td width="125">No&nbsp;Of&nbsp;Groups:</td>
      <td width="200" >
 			<input type='text' class='text'   name="noOfGrps" value='<%=noOfGrps%>'  size="2" maxlength="1" readonly >
      <td >Starting&nbsp;SL&nbsp;No:</td>
      <td >
        <input type='text' class='text'   name="startingSlNo" size="12" maxlength="12" value='<%=slNo%>' readonly >
      </td>     
      </tr>     
    <tr>
      <td colspan="5"  width="585">
         <span id=cust1 style='position:relative;'></span>
      </td>
    </tr>
    <tr valign="top" class='denotes'>
	    <td  width="145"></td>
		<td  width="250"></td>
		<td  width="210"></td>
    <input type="hidden" name= "terminalId" value="<%=loginbean.getTerminalId()%>" >
        <td  width="40012/26/200312/26/200312/26/2003" colspan="2" align="right">
	        	<input type="hidden" name= "Operation" value = '<%=operation%>' >
                <% if(	operation.equalsIgnoreCase("Modify"))
					 {
				%>
                <input type="submit" value="Submit" name="b1" class='input'>
             <%}else{%>
			 <input type="submit" value="Continue>>" name="b1" class='input'>
			 <%}%>
        </td>
    </tr>
	</table>
</form>
</body>
</html>

