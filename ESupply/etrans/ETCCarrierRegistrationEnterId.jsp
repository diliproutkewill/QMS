<%--
	Program Name    : ETCCarrierRegistrationAddProcess.jsp
    Module Name     : ETrans
	Task            : CarrierRegistration
	Sub task        : Modify / View / Delete
	Author Name     : Giridhar Manda
	Date Started    : September 12,2001
    Date Completed  : September 12,2001
	Date Modified   : 
	Description     : This File main purpose is to add the CarrierRegistration Data to the database
					  Method's Summery:
--%>
<%@ page import	=	"org.apache.log4j.Logger"%>

<jsp:useBean id = "loginbean" class = "com.foursoft.esupply.common.bean.ESupplyGlobalParameters" scope = "session"/>
<%!
  private static Logger logger = null;
	private static final String FILE_NAME	=	"ETCCarrierRegistrationEnterId.jsp ";
%>
<%  
    logger  = Logger.getLogger(FILE_NAME);
    try
    {
 	  if(loginbean.getTerminalId()==null)
	   {
%>
          <jsp:forward page="../ESupplyLogin.jsp" />
<%			 
	    }
    }
	
    catch(Exception e)
    {
		//Logger.error(FILE_NAME,"ETCCarrierRegistrationEnterId.jsp : ",e.toString());
    logger.error(FILE_NAME+"ETCCarrierRegistrationEnterId.jsp : "+e.toString());
    }


%>
<html>
<head>
<title>CarrierRegistration EnterId</title>       <%@ include file="../ESEventHandler.jsp" %>
<link rel="stylesheet" href="../ESFoursoft_css.jsp">
<script>
//This function is used to place the focus to a field.
    function placeFocus()
    {
	 document.forms[0].carrierId.focus();
    }
//This function calls 'ETCLOVCarrierIds.jsp' to get CarrierIds.
    function showCarrierIds()
    {
	    var URL 		= 'ETransLOVCarrierIds.jsp?searchString='+document.forms[0].carrierId.value.toUpperCase()+'&shipmentMode=All';	
		var Bars 		= 'directories = no, location = no, menubar = no, status = no, titlebar = no';
		var Options 	= 'scrollbars = yes,width = 360,height = 360,resizable = yes';
		var Features 	= Bars +' '+ Options;
		var Win 		= open(URL,'Doc',Features);
    }
 
    function Mandatory()
    {
    CarrierId=document.forms[0].carrierId.value;
    if(CarrierId.length==0)
    {
	 alert("Please enter Carrier Id");
	 document.forms[0].carrierId.focus();
	 return false;
	}
	else
	   {
			s =CarrierId;
			var	i;
			var returningString = "";
			for(i=0;i<s.length;i++)
			{
				var c = s.charAt(i);
				returningString += c.toUpperCase();
			}
			document.forms[0].carrierId.value = returningString;
	   }
	   document.forms[0].enter.disabled='true';
	   return true;
   }
    function getAlphaNumeric()
    {
    if(event.keyCode!=13)
	{	
     if((event.keyCode < 48 ||event.keyCode > 57) && (event.keyCode < 65 || event.keyCode > 90) && (event.keyCode < 97 || event.keyCode > 122) )
 	   return false;	
	}
	return true;		
    }	

    function upper(input)
    {
    input.value = input.value.toUpperCase(); 
    }
//-->
</script>
</head>
<%
	String param = request.getParameter("Operation");
%>
<body  onLoad="placeFocus()" >
<form method="GET" action="ETCCarrierRegistrationView.jsp" name="carrirreg" onSubmit="return Mandatory()" >
  <table width="800" border="0" cellspacing="0" cellpadding="0">
    <tr bgcolor="#FFFFFF" valign="top"> 
      <td>
        <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class="formlabel"> 
            <td ><table width="790" border="0" ><tr class='formlabel'><td>Carrier - <%= param %></td>
         <td align=right><%=loginbean.generateUniqueId("ETCCarrierRegistrationEnterId.jsp",param)%></td></tr></table></td>
		 </tr>
		 </table>
		  <table border="0" width="800" cellpadding="4" cellspacing="1">
		  <tr valign="top" class='formdata'> 
            <td colspan="2" >&nbsp;</td>
          </tr>
          <tr valign="top" class='formdata'> 
            <td colspan="2" width="772"><b>Enter Carrier Id  to <%=param%> Carrier Registration information :</font></b></td>
          </tr>
          <tr valign="top" class="formdata" colspan=2> 
           <td colspan=2>Carrier Id:<font color="#FF0000" face="Verdana" size="2">*</font><br>
              <input type='text' class='text' name="carrierId" maxlength ="5" size="6" onKeyPress="return getAlphaNumeric()" onBlur ="upper(this)">
	   		  <input type="hidden" name= "Operation" value=<%= param %>>
			 <input type="button" value="..." name="lov_CustomerId" onClick="showCarrierIds()" class="input">
           </td>
          </tr>
		  </table>
		  <table border="0" width="800" cellpadding="4" cellspacing="1">
          <tr valign="top" class='denotes'> 
            <td><font	color="#FF0000">*</font>Denotes
			  Mandatory	</td>
            <td align="right"> 
             
                    <input type="submit" value="Next>>" name="enter"  class="input">
					<input type="reset" value="Reset" name="reset" class="input">
                    					
               
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</form>
</body>
</html>
