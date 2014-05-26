<%
/* Program Name		: LocationAdd.jsp
   Module name		: HO Setup
   Task		        : Location
   Sub task			: Add Location
   Author Name		: A.Hemanth Kumar
   Date Started     : September 08, 2001
   Date completed   : 
   
   Description      :
    This file is invoked when user selects Add of Locations from Menu bar of  main Tree structure .This file is 
    used to add new Location Details .
    On entering all the details and clicking the submit button LocationAddProcess.jsp is called.
    This file  interacts with ETransHOSuperSessionBean and then calls the method setETransHOSuper 
    These details are then set to the respective varaibles through Object LocationMasterJSPBean.
*/
%>
 <%@ page import = "java.util.ArrayList,
					javax.ejb.CreateException,
					org.apache.log4j.Logger,
					com.qms.setup.ejb.sls.SetUpSession,
                    com.qms.setup.ejb.sls.SetUpSessionHome,
					com.foursoft.etrans.sea.setup.port.bean.PortMasterJSPBean, 
					com.foursoft.esupply.common.java.ErrorMessage,
				    com.foursoft.esupply.common.java.KeyValue" %>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
%>
<%
	String FILE_NAME = "ETSPortMasterAdd.jsp";
  logger  = Logger.getLogger(FILE_NAME);
   try
   {
    if(loginbean.getTerminalId() == null )
    {
%>
   <jsp:forward page="ESupplyLogin.jsp" />
<%
   }
   else
   {
 
	String		        terminalId    = null;   			 // String to store terminal Id
	ArrayList	        countryIds    = null;    // ArryList to store countryIds obtained from 'ETransHOSuperSession' bean
	PortMasterJSPBean   portMasterObj = null;
	String              action		  = null;	
	String 				readOnly	  = null;	
	String 				portIdReadOnly= null;	
	int 		        len = 0;    			// integer to store the size of ArrayList 'countryIds'
    String operation   = request.getParameter("Operation");
    String portId      = null;
	String portName    = null;
	String countryId   = null;
	String scheduleD   = "";
	String scheduleK	= "";
	String description = "";
try
 {
	    
	    terminalId 						= loginbean.getTerminalId();
//        SetUpSessionHome home =(SetUpSessionHome )initial.lookup( "SetUpSessionBean" );    //Home interface variable
		 SetUpSessionHome home =(SetUpSessionHome )loginbean.getEjbHome( "SetUpSessionBean" );    //Home interface variable
	    SetUpSession remote   =(SetUpSession)home.create();   // Session Bean instance
        countryIds = remote.getCountryIds("",terminalId,operation);//added by rk
		// if ArrayList contains elements then it's size is assigned to 'len'
		if(countryIds != null)
         len = countryIds.size();
  
  
        if(operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("View") || operation.equalsIgnoreCase("Delete") )
        {
          portId       = request.getParameter("portId");
         // SetUpSessionHome homeObject    = (SetUpSessionHome)initial.lookup("SetUpSessionBean");
		  SetUpSessionHome homeObject    = (SetUpSessionHome)loginbean.getEjbHome("SetUpSessionBean");
          SetUpSession     remoteObject  = (SetUpSession)homeObject.create();
          portMasterObj=(PortMasterJSPBean)remoteObject.getPortMasterDetails(portId);
          if(portMasterObj!=null)
          {
            portId      =  portMasterObj.getPortId();
		    portName     =  portMasterObj.getPortName();
		    countryId   =  portMasterObj.getCountryId();

		    if(portMasterObj.getDescription()!=null)
			  description=portMasterObj.getDescription().trim();
			else  
			  description="";
			if(portMasterObj.getScheduleD()!=null)
			scheduleD	=	portMasterObj.getScheduleD();
			else
			scheduleD	="";
			if(portMasterObj.getScheduleK()!=null)
			scheduleK	=	portMasterObj.getScheduleK();
			else
			scheduleK	="";
			 
			       
          }
          else
          {
            
			ErrorMessage errorMessageObject = new ErrorMessage("Record does not exist  with portId : "+portId,"ETSPortMasterEnterId.jsp");
			ArrayList    keyValueList		= new ArrayList();

			keyValueList.add(new KeyValue("ErrorCode","RNF")); 	
			keyValueList.add(new KeyValue("Operation",operation)); 	
			errorMessageObject.setKeyValueList(keyValueList);
			
			request.setAttribute("ErrorMessage",errorMessageObject);
			/**
			String errorMessage = "Record does not exist  with portId : "+portId;		// String to store error message
		    session.setAttribute("ErrorCode","RNF");
			session.setAttribute("ErrorMessage",errorMessage);
			session.setAttribute("Operation",operation);
			session.setAttribute("NextNavigation","ETSPortMasterEnterId.jsp");  */
%>
				<jsp:forward page="../../ESupplyErrorPage.jsp" />
<%
          }
        
        }
        else
        {
            portId      =  "";
		    portName     =  "";
		    countryId   =  "";
		    description =  "";
        
        
        }
  
  
  }
  catch(Exception ee)
  {
    //Logger.error(FILE_NAME,"Error in ETSPortMasterAdd.jsp file  : "+ee.toString());
    logger.error(FILE_NAME+"Error in ETSPortMasterAdd.jsp file  : "+ee.toString());
  }
   
  
   
   if(operation.equalsIgnoreCase("Add") || operation.equalsIgnoreCase("Modify") || operation.equalsIgnoreCase("Delete") )
   {
      action = " ETSPortMasterProcess.jsp";
      if(operation.equalsIgnoreCase("Delete"))
      {  
		 portIdReadOnly = "readOnly";
		 readOnly = "readOnly";
	  }
	  else if(operation.equalsIgnoreCase("Add"))
	  {
	    portIdReadOnly = "";
	    readOnly       = "";	 
	  }
      else
      {
       portIdReadOnly = "readOnly";
       readOnly       = "";
      }
   
   
   }
   else
   {
      action         = "ETSPortMasterEnterId.jsp";
	  readOnly	     = "readOnly";   
      portIdReadOnly = "readOnly";
   }
    
%>
<html>
<head>
<title>Port Master </title>       <%@ include file="../../ESEventHandler.jsp" %>
<script language="JavaScript">
var ope = '<%=operation%>';
function placeFocus() 
{
   if(ope=='Add')
	{
		document.forms[0].portId.focus();
	  
	}
  else if(ope=='Modify')
	{	
		document.forms[0].portName.focus();
		showValues();
	}
  else if(ope=='View'||ope=='Delete')
  {
		document.forms[0].jbt_Test.focus();
		showValues();
  }	
  else 
  {
	showValues();
  }	
}
function showValues()
{	
	if(ope=='Modify')
	{
		document.forms[0].jbt_Test.value  = 'Submit';
	}
	else if(ope=='Delete')
	{
		document.forms[0].jbt_Test.value  = 'Delete';
	}
	else if(ope=='View')
	{
		document.forms[0].jbt_Test.value  = 'Continue';
	}
}
function showCountryLOV()
{
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var URL="../ETCLOVCountryIds.jsp?searchString="+document.forms[0].countryId.value;
	var Win=open(URL,'Doc',Features);
}
function Mandatory()
{
	for(i=0;i<document.forms[0].elements.length;i++)
    {
		if(document.forms[0].elements[i].type=="text" || document.forms[0].elements[i].type=="textarea")
		    document.forms[0].elements[i].value= document.forms[0].elements[i].value.toUpperCase();
    }				
    portId   = document.forms[0].portId.value;
	portName = document.forms[0].portName.value;
	CountryId    = document.forms[0].countryId.value;
	scheduleK	=	document.forms[0].scheduleK.value;
	scheduleD	=	document.forms[0].scheduleD.value;
	 			
	
    if(portId.length ==0)
	{
	    alert("Please enter Port Id");
		document.forms[0].portId.focus();
		return false;
	}	
   else if(portId.length < 3)
   {
	  alert("Please enter three characters for Port Id");
	  document.forms[0].portId.focus();
	  return false;
   }	
   if(portName.length == 0)
	{
		alert("Please enter Port Name");
		document.forms[0].portName.focus();
		return false;
	}
	if(document.forms[0].description.value.length>50)
  	{
  	 alert("Description should be less then 50 characters");
	 document.forms[0].description.focus();
	 return false;  
  	
  	}
	if(CountryId.length == 0)
	{
		alert("Please enter Country Id ");
		document.forms[0].countryId.focus();
		return false;
	}
	else if(CountryId.length <2)
	{		
	    alert("Please enter two characters for Country Id");
		document.forms[0].countryId.focus();
		return false;
	}
	
	else
	{
		var country_arr  = new Array();   
 
<%
	if("Add".equals(operation) || "Modify".equals(operation))
	{
	for (int i=0; i < len; i++)
	{
%>
	country_arr[<%=i%>]   = "<%=(String)countryIds.get(i)%>";
<%
	}
%>
	if(document.forms[0].countryId.value.length > 0)
	{	 
	 var count=0;
	 for( count =0 ; count < <%=len%> ; count++)
	 {
		var sub = country_arr[count].substring(country_arr[count].indexOf("[")+1,country_arr[count].indexOf("]"))
	  	if ( document.forms[0].countryId.value==sub )
			return true;
     }
 	 alert("Please enter correct Country Id ");
     document.forms[0].countryId.value='';
     document.forms[0].countryId.focus();
	 return false;

	} 
<%
	}
%>
	
}
	document.forms[0].jbt_Test.disabled='true';
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
  
function getSpecialCode()
 {
  if(event.keyCode!==13)
    {
     if(event.keyCode==34 || event.keyCode==39 ||event.keyCode==59||event.keyCode==96||event.keyCode==126)
	 event.returnValue =false;
    }
  return true;
 } 
function changeToUpper(field)
{
	field.value = field.value.toUpperCase();
}		
</script>
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
</head>
<body   onLoad="placeFocus()">
<form method="post" name="location" onSubmit="return Mandatory()" action='<%=action%>' >
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" > 
    <td bgcolor="ffffff"> 
      <table width="800" border="0" cellspacing="1" cellpadding="4">
        <tr class='formlabel'> 
          <td colspan="4" ><table width="790" border="0" ><tr class='formlabel'><td>&nbsp;Port Master&nbsp;- <%=operation%></td><td align=right><%=loginbean.generateUniqueId("ETSPortMasterAdd.jsp",operation)%></td></tr></table></td>
        </tr>
        </table>
            
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="3">&nbsp;</td></tr>
                <tr valign="top" class='formdata'> 
                  <td  width="25%">Port 
                    Id:<font color="#FF0000">*</font><br>
                    <input type="text" class="text"  maxlength="6" name="portId" size="7"  value='<%=portId%>' onBlur="changeToUpper(this)" onkeyPress="return getSpecialCode()" <%=portIdReadOnly%>>
                  </td>
                  <td  width="40%">Port 
                    Name:<font color="#FF0000">*</font><br>
                    <input type="text" class="text"   name="portName" size="40" maxlength="50" value='<%=portName%>' onBlur="changeToUpper(this)" onKeypress="return getSpecialCode()" <%=readOnly%> >
                    </td>
                    <td  width="40%">Country 
                    Id:<font color="#FF0000">*</font><br>
                    <input type="text" class="text"  name="countryId" size="5" maxlength="2" value='<%=countryId%>' onBlur="changeToUpper(this)" onKeypress="return getSpecialCode()" <%=readOnly%> > 
					<%if(operation.equalsIgnoreCase("Add") || operation.equalsIgnoreCase("Modify"))
					{	
					%>
					<input type="button" value="..." name="CountryLOV" onClick="showCountryLOV()" class='input'>
                    <%
					}
					%>
                    </td></tr>
                  
                  <tr class='formdata'>
                  <td >Description : <br>
					<textarea class="select" name="description" cols="40" rows="5" maxlength="50" onBlur="changeToUpper(this)" onKeypress="return getSpecialCode()" <%=readOnly%> ><%=description%></textarea>
				  </td>
				  <td>Schedule D:
				  <input type="text" class="text" name="scheduleD" size="20" maxlength="15" value='<%=scheduleD%>' onBlur="changeToUpper(this)" onKeypress="return getSpecialCode()" <%=readOnly%> >
				  </td>
				  <td>Schedule K:
				  <input type="text" class="text" name="scheduleK" size="20" maxlength="15" value='<%=scheduleK%>' onBlur="changeToUpper(this)" onKeypress="return getSpecialCode()" <%=readOnly%> >
				  </td>
					</tr>
              </table>
              <table border="0" width="800" cellpadding="4" cellspacing="1" >
                <tr class='denotes'> 
                  <td width="598" > 
                   <font color="#FF0000">*</font>Denotes Mandatory </td>
                  
                  <td align="right">
				  <input type="submit" value="Submit" name="jbt_Test" class='input'>
                <% 
				  if(operation.equalsIgnoreCase("Add") || operation.equalsIgnoreCase("Modify"))  
				  {
                %>  
                  <input type="reset" value="Reset" name="reset" onClick="placeFocus()" class='input'>
                <%
				  }
				%>     
                  <input type="hidden" name=Operation value="<%=operation%>">
                     
                  </td>
                </tr>
              </table>
              
          </td>
        </tr>
      </table></form>
      
</body>
</html>
<%
    }
 }
 catch(Exception e)
  {
	//Logger.error(FILE_NAME,"Error in LocationAdd.jsp file : "+e.toString());
  logger.error(FILE_NAME+"Error in LocationAdd.jsp file : "+e.toString());
  } 
%>	