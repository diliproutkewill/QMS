<%
/* Program Name		: LocationEnterId.jsp
   Module name		: HO Setup
   Task		        : Location
   Sub task			: View ,Modify,Delete processes
   Author Name		: A.Hemanth Kumar
   Date Started     : September 08, 2001
   Date completed   : 
   
   Description      :
     This file is invoked when user selects Modify/View/Delete of Locations from Menu bar of  main Tree structure .This file is 
     used to select LocationId to which Details are to be modified/viewed or deleted. LocationId will be selected on clicking the LOV
     which inturn calls the LocationLOV.jsp and on clicking the Submit button it calls the LocationView.jsp screen in which 
     we can see all the details for that particular given LocationId .
     This file interacts with ETransHOSuperSessionBean and then calls the method getETransHOSuperDetails
     These details are then set to the respective varaibles through Object ETransHOSuperJSPBean.
  
*/ 
%>
<%@ page import="org.apache.log4j.Logger"%>
<jsp:useBean id="loginbean" class="com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope="session"/>
<%!
  private static Logger logger = null;
%>

<%
	String FILE_NAME = "ETSPortMasterEnterId.jsp";
  logger  = Logger.getLogger(FILE_NAME);
   try
   {
    // checking for TerminalId
    if(loginbean.getTerminalId() == null )
    {
%>
   <jsp:forward page="ESupplyLogin.jsp" />
<%
   }
   else
   {
              
   String operation = request.getParameter("Operation");
         
%>    	
<html>
<head>
<title>Location Master </title>       <%@ include file="../../ESEventHandler.jsp" %>
<script language="JavaScript">
<!--     
	function placeFocus() 
	{
	   document.forms[0].portId.focus();
	}
function showPortLOV()
{
 	var  searchStr	  = document.forms[0].portId.value.toUpperCase();
	var Bars = 'directories=no, location=no,menubar=no,status=no,titlebar=no';
	var Options='scrollbar=yes,width=360,height=360,resizable=no';
	var Features=Bars+' '+Options;
	var URL='ETSLOVPortIds.jsp?searchStr='+searchStr+"&Operation="+document.forms[0].Operation.value;//added by rk
	var Win=open(URL,'Doc',Features);
}
function openPortLov()
{
	var tabArray = '';
	var formArray = '';
	var lovWhere	=	"";

	
	tabArray = 'PORT_ID';
	formArray = 'portId';
	Url		="<%=request.getContextPath()%>/qms/ListOfValues.jsp?lovid=PORT_MASTER&tabArray="+tabArray+"&formArray="+formArray+"&lovWhere="+lovWhere+"&shipmentMode=&operation=PORTS&search= where PORT_ID LIKE '"+document.forms[0].portId.value.toUpperCase()+"~'";	Options	='width=600,height=750,resizable=yes';	
		
	
	Bars	='directories=0,location=0,menubar=no,status=yes,titlebar=0,scrollbars=0';
	Features=Bars+','+Options;
	Win=open(Url,'Doc',Features);
}
function Mandatory()
{
   document.forms[0].portId.value     = document.forms[0].portId.value.toUpperCase();
   portId = document.forms[0].portId.value;
   
     if(portId.length==0)
	{
	    alert("Please enter Port Id");
		document.forms[0].portId.focus();
		return false;
	}	
    else if(portId.length <3)
   {
	  alert("Please enter three characters for Location Id");
	  document.forms[0].portId.focus();
	  return false;
   }
    document.forms[0].enter.disabled='true';
  	return true;	
}	 
  function getKeyCode()
  {
	if(event.keyCode!==13)
    {
     if ((event.keyCode > 31 && event.keyCode < 48)||(event.keyCode > 90 && event.keyCode < 97)||
	 (event.keyCode > 122 && event.keyCode <127||(event.keyCode > 57 && event.keyCode < 65)))
	 event.returnValue =false;
    }
    return true;
  } 
 function getNumberCode()
{
		if(event.keyCode!=13)
		{
			if(event.keyCode < 48	|| event.keyCode > 57 ) 
				return false;
		}
		return true;
}

//return getKeyCode(portId)
function changeToUpper(field)
{
	field.value = field.value.toUpperCase();
}		
//-->
</script>
<link rel="stylesheet" href="../../ESFoursoft_css.jsp">
</head>
<body  OnLoad="placeFocus()" >
<form method="POST" name="port" onSubmit="return Mandatory()" action="ETSPortMasterAdd.jsp" >
<table width="800" border="0" cellspacing="0" cellpadding="0">
  <tr valign="top" bgcolor="ffffff"> 
    <td  colspan="2"> 
      <table width="800" border="0" cellspacing="1" cellpadding="4" >
        <tr class='formlabel'> 
          <td ><table width="790" border="0" ><tr class='formlabel'><td>Port - <%=operation%>&nbsp;</td><td align=right><%=loginbean.generateUniqueId("ETSPortMasterEnterId.jsp",operation)%></td></tr></table></td>
        </tr>
        </table>
             
              
              <table border="0" width="800" cellpadding="4" cellspacing="1">
			  <tr class='formdata'><td colspan="2">&nbsp;</td></tr>
			  <tr class='formdata'><td colspan="2"><b>Enter Port Id to <%=operation%> Port Information:</b></td></tr>
                <tr class='formdata'> 
                  <td colspan="2">Port 
                    Id:<font color="#FF0000">*</font><br>
					<!-- showPortLOV() -->
                    <input type="text" class="text" name="portId" maxlength ="6" size="7" onBlur="changeToUpper(this)" onkeyPress="return getKeyCode(portId)">
					 <input type="button" class='input' value="..." name="LocationLOV" onClick="openPortLov()">
                    <input type="hidden" name=Operation value="<%= operation %>">
                   </td>
                </tr>
                <tr class='denotes'> 
                  <td  ><font color="#FF0000">*</font>Denotes 
                    Mandatory </td>
                  <td    align="right">
                      <input type="submit" value="Next>>" name="enter" class='input'>
					  <input type="reset" name="Reset" value="Reset" onClick="placeFocus()" class='input'>
                      
                  
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
}catch(Exception e)
  {
	//Logger.error(FILE_NAME,"Error in ETSPortMasterEnterId.jsp file : " +e.toString());
  logger.error(FILE_NAME+"Error in ETSPortMasterEnterId.jsp file : " +e.toString());
	}
%>	 