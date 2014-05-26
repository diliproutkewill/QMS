<%--
% 
% Copyright (c) 1999-2001 by FourSoft, Pvt Ltd. All Rights Reserved.
% This software is the proprietary information of FourSoft, Pvt Ltd.
% Use is subject to license terms.
%
% esupply - v 1.x 
%
--%>
<% //@InvokerPoint: eSupply>>eTrans>>Administration>>ModuleI	Commodity	Modify  chhanged for SPETI-4633 %>

<%      
/**
	Program Name	: CommodityView.jsp
	Module Name		: ETrans
	Task			: Commodity	
	Sub Task		: CommodityView	
	Author Name		: Sivarama Krishna .V
	Date Started	: September 9,2001
	Date Completed	: September 9,2001
	Date Modified	: 
	Description		:
		 This file is invoked when user selects Update/View/Delete of Commodity from Menu bar of  main Tree structure .This file is 
		 used to modify/view/delete Commodity Details .On modifying any of the details and clicking
		 the submit button CommodityAddProcess.jsp is called.This file will interacts with CommodityMasterSessionBean
		 and then calls the method updateCommodityMasterDetails or deleteCommodityMasterDetails depending on the request.
		 These details are then set to the respective varaibles through Object CommodityMasterJSPBean.
		
*/
%> 
<%@ page import="javax.naming.InitialContext,
				 org.apache.log4j.Logger,
				 com.foursoft.etrans.setup.commodity.bean.CommodityJspBean,
				 com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
				 com.foursoft.esupply.common.java.ErrorMessage,
				 com.foursoft.esupply.common.java.KeyValue,
				 java.util.ArrayList"%>

<link rel="stylesheet" href="../ESFoursoft_css.jsp">

<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<jsp:useBean id="CommodityMasterObj" class="com.foursoft.etrans.setup.commodity.bean.CommodityJspBean" scope="session"/>

<%!
  private static Logger logger = null;
%>

<%
    
   	String FILE_NAME			 = "ETCCommodityView.jsp";
    logger  = Logger.getLogger(FILE_NAME);
   // try1 begin
   
   try
   {
    if(loginbean.getTerminalId() == null )
    {
%>
   <jsp:forward page="../ESupplyLogin.jsp" />
<% 
   }
   else
   {
    String commodityId           = null;  // variable to store CommodityId from getcommodity()
	String commodityDescription  = null;  // variable to store CommodityDescription
	String commodityHandlingInfo = null;  // variable to store Commodity Handling information
	String commodityType         = null;  // variable to store Commodity Type
	String hIndicator			 = null;
	String operation             = null;  // variable to store Operation type 
	String value                 = null;  
    String commodityIdl          = null;
	String readOnly              = null;
	String submitValue           = null;  // variable to store
	String actionValue           = null;  // variable to store the jsp name for processing in the Action tag 
    String temp_commodityID      = null;  // variable to store temparary value of commodityID
	String disable               = null;   
	String subClass				 = null;
	String unNumber				 = null;
	String classType			 = null;
	String checked				 ="";

%>
  <%
  // try 2 begin
  try
  {
	   operation = request.getParameter("Operation");
       String commodityId1 = request.getParameter("commodityId");
       temp_commodityID=commodityId1;
       InitialContext context = new InitialContext();
 		 SetUpSessionHome 	ETransHOSuperUserHome	=	(SetUpSessionHome)context.lookup("SetUpSessionBean");
		 SetUpSession 		ETransHOSuperUserRemote	=	(SetUpSession)ETransHOSuperUserHome.create();
	   CommodityJspBean commodityMasterObj = ETransHOSuperUserRemote.getCommodityDetails(commodityId1);
	   //outer if begin
	   //System.out.println("commodityMasterObj:"+ETransHOSuperUserRemote);
  	   if( commodityMasterObj != null)
    	{
	      commodityId = commodityMasterObj.getCommodityId();
	      commodityDescription  =  commodityMasterObj.getCommodityDescription();
	      commodityHandlingInfo = commodityMasterObj.getCommodityHandlingInfo();
		 // inner if1 begin
		 if(commodityHandlingInfo == null)
  		  {
  			commodityHandlingInfo = "";
  		 } //inner if1 end
		
        commodityType  = commodityMasterObj.getCommodityType().trim();  
		hIndicator	   = commodityMasterObj.getHazardIndicator();	
		subClass		= commodityMasterObj.getSubClass();
		if(subClass==null)
		{
			subClass="";
		}
		unNumber		= commodityMasterObj.getUnNumber();
		if(unNumber==null)
		{
			unNumber="";
		}

		classType		= commodityMasterObj.getClassType();
		if(classType==null)
		{
			classType="";
		}
		if(hIndicator.equalsIgnoreCase("Y"))
		{
			checked="checked";
		}
    // inner if2 begin  
   if ((operation.equals("View")) || (operation.equals("Delete"))  )
   {
      readOnly="readonly" ;
	  disable="disabled";
	  
   } // if2 end	   
   //inner if2 else begin
   else 
   {
     readOnly="" ;
	 disable="";
   }	 //inner if2 else end
  
	if (operation.equals("View") )
		actionValue="ETCCommodityEnterId.jsp?Operation="+operation;
	else
		actionValue="ETCCommodityProcess.jsp";
	if(operation.equals("Modify") )
	{
		value ="Modify";
	}
	 else if(operation.equals("Delete") )
	{
		value ="Delete";
	}
	else if(operation.equals("View") )
	{
		value =" Ok ";
	}
	
     
%>

<html>
<head>
<title>Comodity Master </title>       
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<%@ include file="/ESEventHandler.jsp" %>
<script language="JavaScript">
<!--
	
  
function Mandatory()
{
   
   for(i=0;i<document.forms[0].elements.length;i++)
    {
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		    document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }
	 var fields     = document.forms[0];
   
   Description    = fields.commodityDescription.value.length; 
   CommodityType  = fields.commodityType.selectedIndex;
   HandleInfo     =  document.forms[0].commodityHandlingInfo.value;
   if(Description==0)
    {
	 alert("Please enter Commodity Description");
	 fields.commodityDescription.focus();
	 return false;
	}
   if(CommodityType==0)
    {
	 alert("Please select CommodityType");
	 fields.commodityType.focus();
	 return false;
	}
   if(HandleInfo.length > 2000)
   {
	alert("Handling Info should be less than 2000 characters");
	document.forms[0].commodityHandlingInfo.focus();
	return false;
   }
   if(document.forms[0].hazardIndicator.checked)
	{
		commoditysubClass	   =  document.forms[0].subClass.value;
		commodityunNumber	   =  document.forms[0].unNumber.value;
		if(commoditysubClass=='')
		{
			 alert("Please Enter Sub-Class value");
			 document.forms[0].subClass.focus();
			 return false;
		}
		if(commodityunNumber=='')
		{
			alert("Please Enter UN Number Value");
			document.forms[0].unNumber.focus();
			return false;
		}

	}
	document.forms[0].submit.disabled='true';
   	return true;	
}
function changeToUpper(field)
{
	field.value =field.value.toUpperCase();
}
function placeFocus()
{
	
	var operation='<%= operation %>';
   if(operation==('View')|| operation==('Delete') )
	{
		document.forms[0].submit.focus();
		 
	}
	else
	{
		
  	document.forms[0].commodityDescription.focus();
   }
   assigining();
	
}		
function getKeyCode()
 {
  if(event.keyCode!==13)
    {
     if ((event.keyCode > 31 && event.keyCode < 47)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127||(event.keyCode > 58 && event.keyCode < 65)))
	 event.returnValue =false;
    }
  return true;
 } 
 
function getSpecialCode()
 {
  if(event.keyCode!==13)
    {
     if(event.keyCode==34 || event.keyCode==39 ||event.keyCode==59||event.keyCode==96||event.keyCode==126)
	 event.returnValue =false;
    }
  return true;
 } 
  function assigining()
 {
	<%
	 if(hIndicator.equals("Y"))
	 {
	%>	

		document.forms[0].hazardIndicator.checked=true;
	 <%
	 }
	 else
	 {
	%>		
		document.forms[0].hazardIndicator.checked=false;
		
	<%
	 }
	 %>
}	
function change()
{
	star();
	if(document.forms[0].hazardIndicator.checked)
	{
		document.forms[0].subClass.value="<%=subClass%>";
		document.forms[0].unNumber.value="<%=unNumber%>";
		document.forms[0].classType.value="<%=classType%>";
	}
	else
	{
		document.forms[0].subClass.value="";
		document.forms[0].unNumber.value="";
		document.forms[0].classType.value="";
	}
}
function star()
{
	 if(document.forms[0].hazardIndicator.checked)
	{
		document.getElementById("Label").innerHTML="*";
		document.getElementById("Label1").innerHTML="*";
	}
	else
	{
		document.getElementById("Label").innerHTML="&nbsp;";
		document.getElementById("Label1").innerHTML="&nbsp;";
	}
}
//-->
</script>
</head>
<body onLoad="placeFocus();star()">
<form method="POST"   name="commodity" onSubmit="return Mandatory()" action="<%=actionValue%>">
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top"> 
    <td  bgcolor="ffffff">
     
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td ><table width="790" border="0" ><tr class='formlabel'><td>Commodity - <%=operation%></td><td align=right  ><%=loginbean.generateUniqueId("ETCCommodityView.jsp",operation)%></td></tr></table></td>
        </tr>
        </table>
		<table border="0" width="800" cellpadding="4" cellspacing="1">
		<tr class='formdata'><td colspan="4">&nbsp;</td></tr>
                <tr class='formdata'> 
                   <td  width="290">Commodity Id:
				  <font color="#FF0000">*</font><br>
                   
                    <input type='text' class='text' name="commodityId" value = <%=commodityId%> readOnly  size="20" onkeyPress="return getKeyCode(commodityId)">
                   
                    </td>
						<% //  added by senthil prabhu %>
					
                  <td  width="320" >Description:<font color="#FF0000">*</font><br>
                    
                    <input type='text' class='text' name="commodityDescription" value = "<%=commodityDescription%>" <%=readOnly%> maxlength="50" size="40" onBlur="changeToUpper(this)" onkeyPress="return getSpecialCode(commodityDescription)">
                   </td>
				   <td width="340" colspan="2"  > 
                    Handling 
                      Info:<br>
                <textarea rows="3" class='select' name="commodityHandlingInfo" cols="34" maxlength="2000" <%=readOnly%>  onBlur="changeToUpper(this)" onkeyPress="return getSpecialCode(commodityHandlingInfo)"><%=commodityHandlingInfo%></textarea>
                      
                  </td>
               </tr>
				
                <tr class='formdata'> 
				<td width="200"  >Hazard Indicator:<br>

					<input type="checkbox" name="hazardIndicator" value="Y" <%=checked%>  <%=disable%> onClick="change()">
                    </td>
					<td width=200>
					Sub-Class:<font color="#FF0000"><span id="Label">&nbsp;</span></font><br>
					<input type="text" class='text' name="subClass" size="10" maxlength="12"  onBlur="changeToUpper(this)" onKeyPress="return getSpecialCode(subClass)" value="<%=subClass%>" <%=readOnly%> >
					</td>
					<td width=200>
					Un&nbsp;Number:<font color="#FF0000"><span id="Label1">&nbsp;</span></font><br>
					<input type="text" class='text' name="unNumber" size="10" maxlength="12"  onBlur="changeToUpper(this)" onKeyPress="return getSpecialCode(unNumber)" value="<%=unNumber%>" <%=readOnly%>  >
					</td>
					<td width=200>
					Class&nbsp;Type:
					<input type="text" class='text' name="classType" size="10" maxlength="12"  onBlur="changeToUpper(this)" onKeyPress="return getSpecialCode(classType)" value="<%=classType%>" <%=readOnly%> >
					</td>
					

					</tr>
                  <tr class='formdata'>
				  <td colspan="4" width="341">Commodity 
                    Type:<font color="#FF0000">*</font><br>
					<%
				  if(operation.equals("Modify"))
				  {	
				  %>	
                    <select size="1" name="commodityType" class='select'>
					 <option>(Select)</option>
					 <option selected  value="<%=commodityType%>" > <%=commodityType%> </option>
                      <option>Edible,Animal & Vegetable Products</option>
                      <option>AVI,Inedible Animal & Vegetable Products</option>
                      <option>Textiles,Fibres & Mfrs.</option>
                      <option>Metals,Mfrs Excl M/C , Vehicles & Electrical Equipment</option>
		              <option>Machinery,Vehicles & Electrical Equipment</option>
                      <option>Non-Metalic Minerals & Mfrs.</option>
                      <option>Chemicals & Related Products</option>
                      <option>Paper,Reed,Rubber & Wood Mfrs.</option>
                      <option>Scientific,Professional & Precision Instruments</option>
                      <option>Miscellaneous</option>
                    </select>
                   
				   <%
				  }
				  else
				  {
				  %>
				  	<input type='text' class='text' name="commodityType"  maxlength="50"  size="40" value="<%=commodityType%>"  <%=readOnly%> >
                  <%
				  }
				  %>
                     </td>
                </tr>
                <tr class='denotes'> 
                  <td colspan="3" valign="top">
		  
		  <font color=navy>
		  Commodity Ids to conform to Master Item Numbering and Description System of IATA</font></td></tr>
		  <tr class='denotes'> 
                  <td valign="top" colspan="2">
		  <font color="#FF0000">*</font>Denotes 
                    Mandatory </td>
                  <td  valign="top" align="right"> 
                      
		      <%
			if (operation.equals("View") )
			 submitValue="Continue" ;
			else if (operation.equals("Delete") )
			 submitValue="Delete";
			 else
			 submitValue="Submit";
			
			 %>
		      	      
		   <input type="hidden" name="Operation" value="<%=operation%>">
		<%
		if(operation.equals("Modify") )
		{%>
		   <input type="submit" name="submit" value="<%=submitValue%>" class='input'>
		  <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'>
        <%}else{%> 
			   <input type="submit" name="submit" value="<%=submitValue%>" class='input'>
		 <%}%>
                 <!--     <input type="reset" value="Clear" name="reset"> -->
                      
                  </td>
                </tr>
              </table>
            
          </td>
        </tr>
      </table></form>
      <%
	
%>
      <%
	}
		else
	{
%><font color="#FF0000" face="Verdana" size="3"><b>
 <%          
			 ArrayList keyValueList = new ArrayList();
			 
			 ErrorMessage errorMessageObject = new ErrorMessage("Record does not exist with CommodityId : "+temp_commodityID,"ETCCommodityEnterId.jsp"); 
			 keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
			 keyValueList.add(new KeyValue("Operation",operation)); 	
			 errorMessageObject.setKeyValueList(keyValueList);
			 request.setAttribute("ErrorMessage",errorMessageObject);
			
             /**
			 session.setAttribute("Operation",operation); 
			 String errorMessage = "Record does not exist with CommodityId : "+temp_commodityID;
			 session.setAttribute("ErrorCode","RNF");
			 session.setAttribute("ErrorMessage",errorMessage);
			 session.setAttribute("NextNavigation","ETCCommodityEnterId.jsp?Operation="+operation);  */
			
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
     
      </b>
	  </font>
	  </td>
      </tr>
       </table>
        </body>
         </html>
 <%		
	} // outer if end
  } // try 2 end
  catch(Exception nme )
  {
	  nme.printStackTrace();
       //Logger.error(FILE_NAME,"Error in ComodityView.jsp file : "+nme.toString());
       logger.error(FILE_NAME+"Error in ComodityView.jsp file : "+nme.toString());
       session.setAttribute("Operation",operation); 
	   String errorMessage = "Error while retrieving data : "+temp_commodityID;
	   session.setAttribute("ErrorCode","ERR");
	   session.setAttribute("ErrorMessage",errorMessage);
	   session.setAttribute("NextNavigation","ETCCommodityEnterId.jsp?Operation="+operation);
			
%>
			<jsp:forward page="../ESupplyErrorPage.jsp" />
<%
  }
   } //try 1 end
 }catch(Exception e)
  {
	//Logger.error(FILE_NAME,"Error in CommodityView.jsp file : "+e.toString());
  logger.error(FILE_NAME+"Error in CommodityView.jsp file : "+e.toString());
  }  			
%> 
